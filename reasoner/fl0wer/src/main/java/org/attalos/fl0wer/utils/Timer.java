package org.attalos.fl0wer.utils;

public class Timer {
    private long total;
    private long start;
    private boolean running;

    public Timer() {
        this.total = 0;
        this.start = 0;
        this.running = false;
    }

    public void start() {
        if (this.running) {
            throw new RuntimeException("tried to start timer, which is already running");
        }

        this.running = true;
        this.start = System.currentTimeMillis();
    }

    public void stop() {
        if (!this.running) {
            throw new RuntimeException("tried to stop timer, which isn't running");
        }

        this.running = false;
        this.total += System.currentTimeMillis() - start;
    }

    public long get_total_time() {
        if (this.running) {
            throw new RuntimeException("tried to read timer result of still running timer");
        }
        return this.total;
    }
}


