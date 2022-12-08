package com.evg.simsend.simsenddata.exception;

public class ErrorCommandException extends RuntimeException {
    public ErrorCommandException(String pattern) {
        super(pattern);
    }
}
