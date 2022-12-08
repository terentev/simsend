package com.evg.simsend;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTest1 {


    public static void main(String... args) {
        String s = "13][10]Hi6[13][10][13][10]+CMGL: 9,\"REC READ\",\"+79991111111\",\"\",\"22/12/02,10:21:07+12\"[13][10]Hi7[13][10][13][10]+CMGL: 10,\"REC READ\",\"+79991111111\",\"\",\"22/12/02,10:22:48+12\"[13][10]Hi8[13][10][13][10]OK[13][10]WRITE END\n" +
                "ATI\r\r\nSIM800 R14.18\r\n\r\nOK\r\n[13][10]OVER-VOLTAGE WARNNING[13][10]WRITE END\n" +
                "AT+CREG?[13][13][10]+CREG: 0,1[13][10][13][10]OK[13][10]WRITE END\n" +
                "AT+COPS=?[13]WRITE END\n" +
                "WRITE END\n" +
                "WRITE END\n" +
                "WRITE END";

        Pattern p = Pattern.compile("ATI\r\r\n(.*)\r\n\r\nOK\r\n");

        Matcher m = p.matcher(s);
        m.find();
        p(m.group(1));

    }

    private static void p(Object o) {
        System.out.println(o);
    }
}