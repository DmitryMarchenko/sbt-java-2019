package main.java;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ContextImpl implements Context {

    private AtomicInteger completedTaskCount = new AtomicInteger(0);
    private AtomicInteger failedTaskCount = new AtomicInteger(0);
    private AtomicInteger interruptedTaskCount = new AtomicInteger(0);
    private AtomicBoolean interrupted = new AtomicBoolean(false);

    private int allTaskCount;

    public ContextImpl(Runnable... tasks) {
        allTaskCount = tasks.length;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (Runnable task: tasks) {
            executorService.submit(() -> {
                if (interrupted.get()) {
                    interruptedTaskCount.incrementAndGet();
                    return;
                }

                try {
                    task.run();
                    synchronized (this) {
                        this.notify();
                    }
                } catch (Exception e) {
                    failedTaskCount.incrementAndGet();
                    e.printStackTrace();
                    return;
                }

                completedTaskCount.incrementAndGet();
            });
        }
    }

    @Override
    public int getCompletedTaskCount() {
        return completedTaskCount.get();
    }

    @Override
    public int getFailedTaskCount() {
        return failedTaskCount.get();
    }

    @Override
    public int getInterruptedTaskCount() {
        return interruptedTaskCount.get();
    }

    @Override
    public void interrupt() {
        interrupted.set(true);
    }

    @Override
    public boolean isFinished() {
        return getCompletedTaskCount() + getFailedTaskCount() + getInterruptedTaskCount() == allTaskCount;
    }
}
