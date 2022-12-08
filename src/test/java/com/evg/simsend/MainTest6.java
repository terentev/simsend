package com.evg.simsend;


import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTest6 {

    public static void main(String... args) throws IOException, InterruptedException, TimeoutException {
        System.setProperty("configuration.file.path", "/home/evg/project/temp/simsend-app/simsend.json");

        //ATCUSD1("AT+CUSD=1,\"*${p}#\"\r", "AT\\+CUSD=1,\"\\*${p}#\"\r\r\n(.*)OK\r\n", "ERROR", true),
        String s0 = "AT+CUSD=1,\"${p}\"\r";
        String s1 = "AT\\+CUSD=1,\"${p}\"(.*)OKAT\\+CUSD=1,\"${p}\"(.*)OK";

        // p(s0.replaceAll(Pattern.quote("${p}"), "*100#"));
        // p(s1.replaceAll(Pattern.quote("${p}"), quoteRegex("*100#")));


        p(replaceAll(s1, "${p}", quoteRegex("*10+0#")));
    }

    private static String replaceAll(String str, String pattern, String replace) {
        StringBuilder r = new StringBuilder();
        Pattern p = Pattern.compile(Pattern.quote(pattern));
        Matcher m = p.matcher(str);
        int start = 0;
        while (m.find()) {
            r.append(str, start, m.start());
            r.append(replace);
            start = m.end();
        }
        r.append(str, start, str.length());
        return r.toString();
    }

    private static String quoteRegex(String str) {
        if (str.indexOf('*') == -1 && str.indexOf('+') == -1)
            return str;
        StringBuilder r = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            switch (c) {
                case '*':
                case '+':
                    r.append('\\').append(c);
                    break;
                default:
                    r.append(c);
            }
        }
        return r.toString();
    }

    private static void p(Object o) {
        System.out.println(o);
    }
}