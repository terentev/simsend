package com.evg.simsend.util;

import java.util.concurrent.TimeUnit;

public class Delay {
    private long start;
    private final long delay;

    public Delay(long delayMillis) {
        this.delay = TimeUnit.MILLISECONDS.toNanos(delayMillis);
        this.start = System.nanoTime();
    }

    public boolean passed() {
        long time = System.nanoTime();
        return time - start > delay;
    }
}