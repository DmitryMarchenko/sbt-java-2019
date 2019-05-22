package main.java;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        ExecutionManager executionManager = new ExecutionManagerImpl();
        Runnable callback = () -> {
            System.out.println("callback");
        };

        Runnable[] tasks = {
                () -> {
                    System.out.println("task 1");
                },
                () -> {
                    System.out.println("task 2");
                },
                () -> {
                    System.out.println("task 3");
                    throw new RuntimeException("task 3");
                },
                () -> {
                    System.out.println("task 4");
                },
                () -> {
                    System.out.println("task 5");
                },
                () -> {
                    System.out.println("task 6");
                }
        };
        Context execute = executionManager.execute(callback, tasks);

        execute.interrupt();
        Thread.sleep(1000);
        System.out.println(execute.isFinished());
        System.out.println(execute.getFailedTaskCount());
        System.out.println(execute.getCompletedTaskCount());
        System.out.println(execute.getInterruptedTaskCount());
    }
}

