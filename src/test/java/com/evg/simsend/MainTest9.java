package com.evg.simsend;


import com.evg.simsend.simsenddata.SimEvent;
import com.evg.simsend.simsenddata.SimEvent0;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainTest9 {

    public static void main(String... args) throws IOException, InterruptedException, TimeoutException {
        System.setProperty("configuration.file.path", "/home/evg/project/temp/simsend-app/simsend.json");

        SimEvent se = new SimEvent("+CUSD:", s -> c(s));
        SimEvent0 se0 = new SimEvent0("+CUSD:", s -> c(s));

        //  String s = "\r\r+CUSD: 0,+\rCUSD \"04110430043B0430043D0441003A003100320034002C003300320440\", 72\r\r\r";
        String s = "++CU+++CUSD:bbb+CUSD:ba\r";

        byte[] bytes = s.getBytes();
        for (byte b : bytes) {
            se.add(b);
            se0.add(b);
        }
    }

    private static void c(String s) {
        p("[" + s + "]");
    }


    private static void p(Object o) {
        System.out.println(o);
    }
}