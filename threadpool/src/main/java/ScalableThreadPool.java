package main.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ScalableThreadPool implements ThreadPool {

    private int minThreadsCnt;
    private int maxThreadsCnt;
    private AtomicInteger workingThreadsCnt = new AtomicInteger(0);

    private final Queue<Runnable> tasks = new LinkedBlockingQueue<>();
    private List<AdvThread> threads;

    private class AdvThread extends Thread {

        @Override
        public void run() {
            while (true) {
                Runnable currTask = null;
                synchronized (tasks) {
                    if (!tasks.isEmpty()) {
                        currTask = tasks.poll();
                    } else {
                        try {
                            tasks.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (currTask != null) {
                    workingThreadsCnt.incrementAndGet();
                    currTask.run();
                    workingThreadsCnt.decrementAndGet();
                }


                synchronized (tasks) {
                    if (tasks.isEmpty() && threads.size() > minThreadsCnt) {
                        threads.remove(this);
                        return;
                    }
                }
            }
        }
    }

    public ScalableThreadPool(int minThreadsCnt, int maxThreadsCnt) {
        this.minThreadsCnt = minThreadsCnt;
        this.maxThreadsCnt = maxThreadsCnt;
        threads = new ArrayList<>();
        for (int i = 0; i < minThreadsCnt; i++) {
            threads.add(new AdvThread());
        }
    }

    @Override
    public void start() {
        threads.forEach(AdvThread::start);
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (tasks) {
            if (workingThreadsCnt.get() == threads.size() && threads.size() < maxThreadsCnt) {
                threads.add(new AdvThread());
                threads.get(threads.size() - 1).start();
            }
            tasks.add(runnable);
            tasks.notify();
        }
    }
}
