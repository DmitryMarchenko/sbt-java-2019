package main.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class FixedThreadPool implements ThreadPool {

    private final Queue<Runnable> tasks = new LinkedBlockingQueue<>();
    private List<AdvThread> threads;

    public class AdvThread extends Thread {
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
                    currTask.run();
                }
            }
        }
    }

    public FixedThreadPool(int threadsCnt) {
        threads = new ArrayList<>();
        for (int i = 0; i < threadsCnt; i++) {
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
            tasks.add(runnable);
            tasks.notify();
        }
    }

}
