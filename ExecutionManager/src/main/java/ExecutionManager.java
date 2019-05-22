package main.java;

public interface ExecutionManager {
    Context execute(Runnable callback, Runnable... tasks);
}
