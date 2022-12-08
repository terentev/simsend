package com.evg.simsend.util;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class LazyObject<T> {
    private final Supplier<T> supplier;
    private T value;

    private LazyObject(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> LazyObject<T> obj(Supplier<T> supplier) {
        return new LazyObject<>(supplier);
    }

    @Nonnull
    public T obj() {
        if (value == null)
            value = supplier.get();
        return value;
    }

    /**return value and set null*/
    @Nonnull
    public T nullify() {
        if (value == null)
            value = supplier.get();
        T r = value;
        value = null;
        return r;
    }
}