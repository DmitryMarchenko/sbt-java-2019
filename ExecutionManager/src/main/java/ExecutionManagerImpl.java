package main.java;

public class ExecutionManagerImpl implements ExecutionManager {

    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        Context context = new ContextImpl(tasks);
        new Thread(() -> {
            while (!context.isFinished()) {
                synchronized (context) {
                    try {
                        context.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            callback.run();
        }).start();

        return context;
    }
}
