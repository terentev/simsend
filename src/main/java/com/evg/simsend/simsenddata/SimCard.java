package com.evg.simsend.simsenddata;

public class SimCard {
    public String number;
    public Operator operator;
    public String ccid;
    public boolean sendCusdBalance = true;
    public boolean sendSmsForNoBlock = true;

    public SimCard(String number, Operator operator, String ccid) {
        this.number = number;
        this.operator = operator;
        this.ccid = ccid;
    }

    public static SimCard init(String number, Operator operator, String ccid) {
        return new SimCard(number, operator, ccid);
    }
}