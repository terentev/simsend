package com.evg.simsend.simsenddata;


import com.evg.simsend.simsenddata.exception.ErrorCommandException;
import com.evg.simsend.simsenddata.exception.PortOpenException;
import com.evg.simsend.util.Delay;
import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_SEMI_BLOCKING;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_WRITE_BLOCKING;
import static com.google.common.base.Preconditions.checkState;

public class SimSerial {
    private final static Logger log = LoggerFactory.getLogger(SimSerial.class);

    private String ccid;
    private final SerialReadThread rt;
    private final SerialPort port;
    private final Buffer buffer = new Buffer();
    private final CopyOnWriteArraySet<Consumer<String>> cusdSet = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<Consumer<String>> smsSet = new CopyOnWriteArraySet<>();

    public SimSerial(SerialPort port0) {
        port = port0;
        port.setComPortTimeouts(TIMEOUT_READ_SEMI_BLOCKING | TIMEOUT_WRITE_BLOCKING, 1, 0);
        port.setBaudRate(115200);
        boolean ok = false;
        try {
            boolean open = port.openPort();
            if (!open)
                throw new PortOpenException();
            rt = new SerialReadThread(port, buffer, this::smsEvent, this::cusdEvent);
            rt.init();
            ok = true;
        } finally {
            if (!ok)
                closeNoThrow();
        }
    }

    public synchronized void write(String s) {
        byte[] data = s.getBytes();
        int i = port.writeBytes(data, data.length);
        if (i == -1)
            throw new RuntimeException("Write error");
    }

    public synchronized String command(Command command, String param, int timeoutMillis)
            throws TimeoutException, InterruptedException {
        checkState(!buffer.isRunning());
        checkState(buffer.size() == 0);
        buffer.start();
        try {
            byte[] data = command.data(param);
            int i = port.writeBytes(data, data.length);
            if (i == -1)
                throw new RuntimeException("Write error");
            Delay delay = new Delay(timeoutMillis);
            String r;
            while (true) {
                Thread.sleep(50);
                if (rt.error() != null)
                    throw new RuntimeException("Read thread error", rt.error());
                if (delay.passed()) {
                    buffer.stopClear();
                    throw new TimeoutException();
                }
                r = isCommandEnd(command, param, buffer.get());
                if (r != null) {
                    buffer.stopClear();
                    break;
                }
            }
            return r;
        } finally {
            buffer.stopClear();
        }
    }

    @Nullable
    private static String isCommandEnd(Command command, String param, byte[] list) {
        String s = new String(list);
        p("=================00");
        p(s);
        p("=================11");
        if (command.error(s, param))
            throw new ErrorCommandException(command.data0(param));
        return command.find(s, param);
    }

    public void addCusd(Consumer<String> run) {
        checkState(cusdSet.size() < 1024);
        cusdSet.add(run);
    }

    public void removeCusd(Consumer<String> run) {
        cusdSet.remove(run);
    }

    public void addSms(Consumer<String> run) {
        checkState(smsSet.size() < 1024);
        smsSet.add(run);
    }

    public void removeSms(Consumer<String> run) {
        smsSet.remove(run);
    }

    private void cusdEvent(String str) {
        for (Consumer<String> a : cusdSet)
            a.accept(str);
    }

    private void smsEvent(String str) {
        for (Consumer<String> a : smsSet)
            a.accept(str);
    }

    public Throwable error() {
        return rt.error();
    }

    public void closeNoThrow() {
        if (rt != null)
            rt.closeNoThrow();
        try {
            if (port != null)
                port.closePort();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    private static void p(Object o) {
        System.out.println(o);
    }

    public void setCcid(String ccid) {
        this.ccid = ccid;
    }

    public String getCcid() {
        return ccid;
    }
}