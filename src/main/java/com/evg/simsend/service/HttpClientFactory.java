package com.evg.simsend.service;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.concurrent.TimeUnit;

public class HttpClientFactory {

    private HttpClientConnectionManager connectionManager;

    public void setConnectionManager(HttpClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public CloseableHttpClient create() {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setRedirectStrategy(NoRedirectStrategy.instance)
                .setConnectionTimeToLive(30, TimeUnit.MINUTES)
                .build();
        return httpClient;
    }
}