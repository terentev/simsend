package com.evg.simsend.other;

import java.util.HashSet;
import java.util.Set;

public class SingletonChecker {
    private final static Set<Class> set = new HashSet<Class>();

    public SingletonChecker() {
        Class<? extends SingletonChecker> clazz = getClass();
        synchronized (set) {
            if (set.contains(clazz))
                throw new IllegalStateException("Singleton double create: " + clazz);
            set.add(clazz);
        }
    }
}
