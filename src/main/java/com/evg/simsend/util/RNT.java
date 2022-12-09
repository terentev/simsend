package com.evg.simsend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RNT implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(RNT.class);
    private final Runnable run;

    public RNT(Runnable run) {
        this.run = run;

    }

    @Override
    public void run() {
        try {
            run.run();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    public static Runnable g(Runnable run) {
        return new RNT(run);
    }
}
