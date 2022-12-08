package com.evg.simsend.simsenddata;

public class SimData {
    public volatile double balance = 0;
    public SimCard sim;

    public SimData(SimCard sim) {
        this.sim = sim;
    }

    public static SimData init(SimCard sim) {
        return new SimData(sim);
    }
}