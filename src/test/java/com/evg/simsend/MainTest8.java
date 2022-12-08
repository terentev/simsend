package com.evg.simsend;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.google.api.client.util.Preconditions.checkState;

public class MainTest8 {

    public static void main(String... args) throws IOException, InterruptedException, TimeoutException {
        System.setProperty("configuration.file.path", "/home/evg/project/temp/simsend-app/simsend.json");

        byte [] b = new byte[3];
        String s = "abc";
        checkState(s.indexOf((char) 0) == -1);
        String s0 = new String(b);
        p(s0);
    }


    private static void p(Object o) {
        System.out.println(o);
    }
}