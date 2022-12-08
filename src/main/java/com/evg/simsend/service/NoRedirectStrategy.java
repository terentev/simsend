package com.evg.simsend.service;

import org.apache.http.impl.client.LaxRedirectStrategy;

public class NoRedirectStrategy extends LaxRedirectStrategy {

    public static NoRedirectStrategy instance = new NoRedirectStrategy();

    @Override
    protected boolean isRedirectable(String method) {
        return false;
    }
}