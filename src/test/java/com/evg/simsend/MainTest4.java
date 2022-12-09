package com.evg.simsend;


import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

public class MainTest4 {

    public static void main(String... args) throws IOException, InterruptedException, TimeoutException {
        System.setProperty("configuration.file.path", "/home/evg/project/temp/simsend-app/simsend.json");

        String s = "04110430043B0430043D0441003A00310039002C003800340440";

        byte[] bytes = DatatypeConverter.parseHexBinary(s);

        p(decodeUCS2(bytes));
    }

    public static byte[] encodeUCS2(String s) {
        try {
            return s.getBytes("UTF-16BE");
        } catch (UnsupportedEncodingException e) {
            return new byte[]{};
        }
    }

    public static String decodeUCS2(byte[] e) {
        try {
            return new String(e, "UTF-16BE");
        } catch (UnsupportedEncodingException e1) {
            return null;
        }
    }


    private static void p(Object o) {
        System.out.println(o);
    }
}