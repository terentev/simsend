package com.evg.simsend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RLNT implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(RLNT.class);

    private final Runnable run;
    private final Object lock;

    public RLNT(Runnable run, Object lock) {
        this.run = run;
        this.lock = lock;
    }

    @Override
    public void run() {
        synchronized (lock) {
            try {
                run.run();
            } catch (Throwable t) {
                log.error("", t);
            }
        }
    }

    public static Runnable g(Runnable run, Object lock) {
        return new RLNT(run, lock);
    }
}
