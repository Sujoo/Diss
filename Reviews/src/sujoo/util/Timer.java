package sujoo.util;

public class Timer {

    private long start;
    private long end;

    public Timer() {
        start = 0;
        end = 0;
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public void stop() {
        end = System.currentTimeMillis();
    }

    public void print() {
        System.out.printf("Duration: %.1f minutes", (double) ((end - start) / 1000) / 60);
    }
}
