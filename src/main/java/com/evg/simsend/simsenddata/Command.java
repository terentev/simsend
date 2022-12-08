package com.evg.simsend.simsenddata;

import com.evg.simsend.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Command {
    ATI("ATI\r", "ATI\r\r\n(.*)\r\n\r\nOK\r\n", "ERROR"),
    ATCSQ("AT+CSQ\r", "AT\\+CSQ\r\r\n(\\+CSQ:.*)\r\n\r\nOK\r\n", "ERROR"),
    ATCCID("AT+CCID\r", "AT\\+CCID\r\r\n(.*)\r\n\r\nOK\r\n", "AT\\+CCID\r\r\nERROR"),
    ATCFUN0("AT+CFUN=0\r", "AT\\+CFUN=0\r\r\n(.*)OK\r\n", "ERROR"),
    ATCFUN1("AT+CFUN=1\r", "AT\\+CFUN=1\r\r\n(.*)OK\r\n", "ERROR"),
    ATCOPSVALUE("AT+COPS?\r", "AT\\+COPS\\?\r\r\n(.*)OK\r\n", "ERROR"),
    ATCREGVALUE("AT+CREG?\r", "AT\\+CREG\\?\r\r\n(.*)OK\r\n", "ERROR"),
    ATCPINVALUE("AT+CPIN?\r", "AT\\+CPIN\\?\r\r\n(.*)OK\r\n", "ERROR"),
    ATCGATTVALUE("AT+CGATT?\r", "AT\\+CGATT\\?\r\r\n(.*)OK\r\n", "ERROR"),
    ATCMGF1("AT+CMGF=1\r", "AT\\+CMGF=1\r\r\n(.*)OK\r\n", "AT\\+CMGF=1\r\r\n(.*)ERROR\r\n"),
    ATCMGR("AT+CMGR=${p}\r", "AT\\+CMGR=${p}\r\r\n(.*)OK\r\n", "ERROR", true),
    ATCMGD("AT+CMGD=${p}\r", "AT\\+CMGD=${p}\r\r\n(.*)OK\r\n", "ERROR", true),
    ATCUSD1("AT+CUSD=1,\"${p}\"\r", "AT\\+CUSD=1,\"${p}\"\r\r\n(.*)OK\r\n", "ERROR", true),
    ATCMGS("AT+CMGS=\"${p}\"\r", "AT\\+CMGS=\"${p}\"\r\r\n(.*)>", "ERROR", true),
    ATCMGDADELREAD("AT+CMGDA=\"DEL READ\"\r", "AT\\+CMGDA=\"DEL READ\"\r\r\n(.*)OK\r\n", "ERROR"),
    ATCMGLALL("AT+CMGL=\"ALL\"\r", "AT\\+CMGL=\"ALL\"\r\r\n(.*)OK\r\n", "ERROR");

    final String data;
    final String result;
    final String error;
    final boolean isParam;

    Command(String data, String result, String error) {
        this.isParam = false;
        this.data = data;
        this.result = result;
        this.error = error;
    }

    Command(String data, String result, String error, boolean isParam) {
        this.isParam = isParam;
        this.data = data;
        this.result = result;
        this.error = error;
    }

    public byte[] data(String param) {
        if (isParam)
            return Utils.replaceAll(data, "${p}", param).getBytes();
        return data.getBytes();
    }

    public String find(String str, String param) {
        Pattern p = Pattern.compile(getPatternWithQuoteRegexParam(result, param), Pattern.DOTALL);
        Matcher m = p.matcher(str);
        if (m.find())
            return m.group(1);
        return null;
    }

    public boolean error(String str, String param) {
        Pattern p = Pattern.compile(getPatternWithQuoteRegexParam(error, param), Pattern.DOTALL);
        return p.matcher(str).find();
    }

    public String data0(String param0) {
        return new String(data(param0));
    }

    private String getPatternWithQuoteRegexParam(String str, String param) {
        if (isParam)
            return Utils.replaceAll(str, "${p}", Utils.quoteRegex(param));
        return str;
    }
}