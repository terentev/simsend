package com.evg.simsend.service;

import com.evg.simsend.configuration.ConfigurationService;
import com.evg.simsend.simsenddata.*;
import com.evg.simsend.simsenddata.exception.PortOpenException;
import com.evg.simsend.util.RLNT;
import com.evg.simsend.util.RNT;
import com.evg.simsend.util.Utils;
import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class SimService {
    private final static Logger log = LoggerFactory.getLogger(SimService.class);

    @Autowired
    private GmailService gmail;
    @Autowired
    private ConfigurationService configuration;
    private ScheduledExecutorService se = Executors.newScheduledThreadPool(3);
    private final Object lock = new Object();
    private SimSerials simSerials = new SimSerials();
    private static String SEND = "" + (char) 26;
    private static String ESC = "" + (char) 27;
    private Map<String, SimData> map = new HashMap<>();

    @PostConstruct
    public void start() {
        for (SimCard a : configuration.getConfiguration().global.simCards)
            map.put(a.ccid, new SimData(a));
        se.scheduleWithFixedDelay(RNT.g(this::addNewPorts), 0, 1, TimeUnit.MINUTES);
        se.scheduleWithFixedDelay(RLNT.g(this::sendSmsToEmailPeriod, lock), 0, 1, TimeUnit.MINUTES);
        se.scheduleWithFixedDelay(RLNT.g(this::sendSmsForNoBlock, lock), 45, 45, TimeUnit.DAYS);
        se.scheduleWithFixedDelay(RLNT.g(this::sendCusdBalance, lock), 0, 1, TimeUnit.DAYS);
    }

    private void addNewPorts() {
        simSerials.clearError();
        for (SerialPort port : SerialPort.getCommPorts()) {
            if (!isFreeSim800(port))
                continue;
            SimSerial ss = null;
            boolean ok = false;
            try {
                ss = new SimSerial(port);
                final SimSerial ss0 = ss;
                String ccid = ss0.command(Command.ATCCID, null, 1000);
                ss0.setCcid(ccid);
                ss0.command(Command.ATCMGF1, null, 10000);
                ss0.addSms(data -> se.execute(RLNT.g(() -> eventSms(ss0, data), lock)));
                ss0.addCusd(data -> se.execute(RLNT.g(() -> eventCusd(ss0, data), lock)));
                simSerials.put(port.getSystemPortName(), ss0);
                ok = true;
            } catch (TimeoutException | RuntimeException e) {
                log.error("", e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (!ok) {
                    if (ss != null)
                        ss.closeNoThrow();
                }
            }
        }
    }

    private void sendSmsForNoBlock() {
        for (SimSerial ss : simSerials.get()) {
            if (!map.containsKey(ss.getCcid()))
                continue;
            SimData sd = map.get(ss.getCcid());
            if (!sd.sim.sendSmsForNoBlock)
                continue;
            try {
                try {
                    String phone = configuration.getConfiguration().global.phoneForSendSmsNoBlock;
                    ss.command(Command.ATCMGS, phone, 10000);
                    Thread.sleep(1000);
                    ss.write("send sms to not block sim " + sd.sim.number);
                    Thread.sleep(1000);
                } finally {
                    ss.write(ESC);
                }
                Help.p("SMS send");
                Thread.sleep(20000);
            } catch (Throwable t) {
                log.error("", t);
            }
        }
    }

    private void sendCusdBalance() {
        for (SimSerial ss : simSerials.get()) {
            if (!map.containsKey(ss.getCcid()))
                continue;
            SimData sd = map.get(ss.getCcid());
            if (!sd.sim.sendCusdBalance)
                continue;
            try {
                if (sd.sim.operator == Operator.MTS)
                    ss.command(Command.ATCUSD1, "*100#", 1000);
            } catch (Throwable t) {
                log.error("", t);
            }
        }
    }

    //cusd balance
    private void eventCusd(SimSerial ss, String data) {
        String data0 = Utils.cusdParse(data);
        String email = configuration.getConfiguration().global.email;
        if (Utils.isBalance(data0)) {
            SimData sd = map.get(ss.getCcid());
            if (sd == null)
                throw new IllegalStateException();
            double balance0 = Utils.parseBalance(data0);
            if (sd.balance != balance0) {
                gmail.sendEmail(email, "sim",
                        sd.sim.number + " old balance " + sd.balance + " new " + balance0);
                sd.balance = balance0;
            }
            return;
        }

        gmail.sendEmail(email, "sim", "CUSD: " + data0);
        log.info("send cusd: {}", data0);
    }

    private void eventSms(SimSerial ss, String data) {
        try {
            sendAllSmsFromSim(ss);
        } catch (InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendSmsToEmailPeriod() {
        for (SimSerial a : simSerials.get()) {
            try {
                sendAllSmsFromSim(a);
            } catch (Throwable e) {
                log.error("", e);
            }
        }
    }

    private void sendAllSmsFromSim(SimSerial ss) throws InterruptedException, TimeoutException {
        String r = ss.command(Command.ATCMGLALL, null, 10000);
        if (!r.isEmpty()) {
            String email = configuration.getConfiguration().global.email;
            gmail.sendEmail(email, "sim", Utils.isUcs2(r) ? Utils.fromUcs2(r) : r);
        }
        ss.command(Command.ATCMGDADELREAD, null, 10000);
    }

    private static boolean isFreeSim800(SerialPort port) {
        SimSerial ss = null;
        try {
            ss = new SimSerial(port);
            String c = ss.command(Command.ATI, null, 5000);
            return c.contains("SIM800");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (PortOpenException | TimeoutException e) {
            return false;
        } finally {
            if (ss != null)
                ss.closeNoThrow();
        }
    }

    @PreDestroy
    public void closeNoThrow() {
        try {
            se.shutdown();
        } catch (Throwable t) {
            log.error("", t);
        }
        try {
            se.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (Throwable t) {
            log.error("", t);
        }
        simSerials.clearNoThrow();
    }

    private static class SimSerials {
        private Map<String, SimSerial> map = new HashMap<>();
        private boolean closed = false;

        public synchronized List<SimSerial> get() {
            if (closed)
                throw new RuntimeException("Exception need to close SimSerial");
            return new ArrayList<>(map.values());
        }

        public synchronized void put(String name, SimSerial ss) {
            if (closed)
                throw new RuntimeException("Exception need to close SimSerial");
            if (map.containsKey(name)) {
                SimSerial a = map.get(name);
                a.closeNoThrow();
                map.remove(name);
            }
            map.put(name, ss);
        }

        public synchronized void clearError() {
            if (closed)
                throw new RuntimeException("Exception need to close SimSerial");
            List<String> remove = new ArrayList<>();
            for (String key : map.keySet()) {
                if (map.get(key).error() != null)
                    remove.add(key);
            }
            for (String a : remove)
                map.get(a).closeNoThrow();
            for (String a : remove)
                map.remove(a);
            remove.clear();
        }

        public synchronized void clearNoThrow() {
            closed = true;
            for (SimSerial a : map.values())
                a.closeNoThrow();
            map.clear();
        }
    }

    private static class Help {
        private static void p(Object o) {
            System.out.println(o);
        }
    }
}