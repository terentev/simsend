package com.evg.simsend.util;


import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static Pattern p = Pattern.compile("\r\n([0-9A-F]+)\r\n\r\n");
    private static Pattern cusd = Pattern.compile("(\\+CUSD: \\d, \")([0-9A-F]+)(\", \\d+)");
    private static Pattern cusd0 = Pattern.compile("\\+CUSD: \\d, \"(.+)\", \\d+");
    private static Pattern balance = Pattern.compile("\\d+,\\d\\d?");

    public static String replaceAll(String str, String search, String replace) {
        StringBuilder r = new StringBuilder();
        Pattern p = Pattern.compile(Pattern.quote(search));
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

    public static String quoteRegex(String str) {
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

    public static boolean isBalance(String data) {
        try {
            parseBalance(data);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static double parseBalance(String data) {
        if (!data.contains("Баланс"))
            throw new IllegalArgumentException();
        Matcher m = balance.matcher(data);
        if (!m.find())
            throw new IllegalArgumentException();
        return Double.parseDouble(m.group(0).replace(',', '.'));
    }

    public static String cusdParse(String data) {
        Matcher m = cusd0.matcher(data);
        if (!m.find())
            throw new IllegalArgumentException("not cusd: " + data);
        String a = m.group(1);
        return isHex(a) ? ucs2(a) : a;
    }

    public static boolean isCusdUcs2(String data) {
        try {
            Utils.cusdFromUcs2(data);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String cusdFromUcs2(String data) {
        Matcher m = cusd.matcher(data);
        if (!m.find())
            throw new IllegalArgumentException("not cusd ucs: " + data);
        StringBuilder r = new StringBuilder();
        r.append(m.group(1));
        r.append(Utils.ucs2(m.group(2)));
        r.append(m.group(3));
        return r.toString();
    }

    public static boolean isUcs2(String data) {
        try {
            Utils.fromUcs2(data);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String fromUcs2(String data) {
        Matcher m = p.matcher(data);
        StringBuffer sb = new StringBuffer();
        while (m.find())
            m.appendReplacement(sb, "\r\n" + Utils.ucs2(m.group(1)) + "\r\n\r\n");
        m.appendTail(sb);
        return sb.toString();
    }

    public static String ucs2(String data) {
        if (!(data.length() % 2 == 0))
            throw new IllegalArgumentException("not hex");
        byte[] bytes = DatatypeConverter.parseHexBinary(data);
        return decodeUCS2(bytes);
    }

    private static byte[] encodeUCS2(String s) {
        try {
            return s.getBytes("UTF-16BE");
        } catch (UnsupportedEncodingException e) {
            return new byte[]{};
        }
    }

    private static String decodeUCS2(byte[] e) {
        try {
            return new String(e, "UTF-16BE");
        } catch (UnsupportedEncodingException e1) {
            return null;
        }
    }

    private static boolean isHex(String data) {
        try {
            DatatypeConverter.parseHexBinary(data);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}