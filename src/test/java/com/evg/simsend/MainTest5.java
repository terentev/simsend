package com.evg.simsend;


import com.evg.simsend.util.Utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTest5 {

    public static void main(String... args) throws IOException, InterruptedException, TimeoutException {
        System.setProperty("configuration.file.path", "/home/evg/project/temp/simsend-app/simsend.json");

        String s = "AT+CMGL=\"ALL\"\n" +
                "+CMGL: 1,\"REC READ\",\"Jusan Bank\",\"\",\"22/12/06,04:43:55+24\"\r\n" +
                "0020041D0438043A043E043C04430020043D043500200441043E043E0431044904300439044204350020043A043E04340020043F043E04340442043204350440043604340435043D0438044F003A0020003400330036003100200020004C006600490032005400420072006E007600610038\r\n" +
                "\r\n" +
                "+CMGL: 2,\"REC UNREAD\",\"Jusan Bank\",\"\",\"22/12/06,04:52:35+24\"\r\n" +
                "0020041D0438043A043E043C04430020043D043500200441043E043E0431044904300439044204350020043A043E04340020043F043E04340442043204350440043604340435043D0438044F003A0020003000390035003300200020004C006600490032005400420072006E007600610038\r\n" +
                "\r\n" +
                "OK\n";

        Pattern p = Pattern.compile("\r\n([0-9A-F]+)\r\n\r\n");
        //  Pattern p = Pattern.compile("([0-9A-F]+)");

        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "\r\n" + Utils.ucs2(m.group(1)) + "\r\n\r\n");
            p(m.group(1));
        }
        m.appendTail(sb);
        p(sb.toString());

        String s0 = "AT+CMGS=\"+79991111111\"\r\n>";

        Pattern p0 = Pattern.compile("AT\\+CMGS=\"\\+79991111111\"\r\n>");
        Matcher matcher = p0.matcher(s0);
        p(matcher.find());

        String s1 = "+79991111111";
        String s2 = s1.replaceAll(Pattern.quote("+"), "\\\\+");
        p(s2);

        p((int) 'A');
        p((int) 'z');

        //String s3 = "+CUSD: 0, \"04110430043B0430043D0441003A00310039002C003800340440\", 72";
      //  String s3 = "+CUSD: 0, \"Balance:19,84r\", 15";
      //  String s4 = Utils.cusdFromUcs2(s3);
      //  p(s4);

        String s5= "Баланс:19,84р";
        p(Utils.parseBalance(s5));
    }



    private static void p(Object o) {
        System.out.println(o);
    }
}