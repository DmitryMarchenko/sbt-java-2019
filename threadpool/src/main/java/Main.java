package main.java;

public class Main {

    public static void main(String[] args) {
        ThreadPool threadPool = new ScalableThreadPool(3, 6);
        threadPool.start();
        for (int i = 0; i < 50; i++) {
            int curr = i;
            threadPool.execute(() -> {
                    System.out.println("Task id: " + curr + " Thread id: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
