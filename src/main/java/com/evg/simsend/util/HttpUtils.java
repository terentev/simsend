package com.evg.simsend.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpUtils {

    public static String executeGet(String uri, Charset charset, HttpClient httpClient) {
        HttpGet request = new HttpGet(uri);
      //  request.setHeader("Cookie", "__ddg1=bsW6GOLkbnhCJeOjdA6F; accept=1; __ddg2=D1Ihzo8QTNoJE8x7");
        return HttpUtils.execute(httpClient, request, UTF_8);
    }

    public static String execute(HttpClient httpClient, HttpUriRequest request, Charset charset) {
        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null)
                throw new RuntimeException("httpEntity == null");
            return EntityUtils.toString(httpEntity, charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}