package com.evg.simsend.util;

import java.util.concurrent.TimeUnit;

public class TimeDelay {

    private long last;
    private final long delay;

    public TimeDelay(long delayMillis) {
        this.delay = TimeUnit.MILLISECONDS.toNanos( delayMillis );
        this.last = System.nanoTime();
    }

    public synchronized boolean isTrue() {
        long time = System.nanoTime();
        if ( time - last > delay ) {
            last = time;
            return true;
        }
        return false;
    }
}