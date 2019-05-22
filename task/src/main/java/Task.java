package main.java;

import java.util.concurrent.Callable;

public class Task<T> {

    private volatile Boolean finished = false;
    private T result = null;
    private Callable<? extends T> callable;
    private CalculateException exception = null;

    public Task(Callable<? extends T> callable) {
        this.callable = callable;
    }

    public T get() {
        if (!finished) {
            if (exception != null) {
                throw exception;
            }
            synchronized (this) {
                if (!finished) {

                    if (exception != null) {
                        throw exception;
                    }
                    
                    try {
                        result = callable.call();
                    } catch (Exception e) {
                        exception = new CalculateException("Calculating fail!");
                        throw exception;
                    }
                    finished = true;
                    finished.notifyAll();
                }
            }
        }

        return result;
    }
}