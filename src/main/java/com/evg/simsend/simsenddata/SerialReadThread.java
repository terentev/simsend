package com.evg.simsend.simsenddata;

import com.evg.simsend.simsenddata.exception.BufferToLongException;
import com.evg.simsend.simsenddata.exception.PortReadException;
import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkState;

public class SerialReadThread {
    private final static Logger log = LoggerFactory.getLogger(SerialReadThread.class);
    private volatile boolean close;
    private SerialPort port;
    private Thread thread;
    private SimEvent smsEvent;
    private SimEvent cusdEvent;
    private Buffer buffer;
    private volatile Throwable error;

    public SerialReadThread(SerialPort port0, Buffer buffer0, Consumer<String> smsEvent0, Consumer<String> cusdEvent0) {
        checkState(port0.isOpen());
        buffer = buffer0;
        port = port0;
        smsEvent = new SimEvent("+CMTI:", smsEvent0);
        cusdEvent = new SimEvent("+CUSD:", cusdEvent0);
        Runnable run = () -> {
            log.info("Open thread: " + port.getSystemPortName());
            while (true) {
                if (close)
                    break;
                try {
                    byte[] buf = new byte[1];
                    int count = port.readBytes(buf, buf.length);
                    if (count == -1)
                        throw new PortReadException();
                    if (count == 0)
                        continue;

                    checkState(count == 1);
                    byte b = buf[0];
                    System.out.print((b != 13 && b != 10 ? (char) b : "[" + b + "]"));
                    smsEvent.add(b);
                    cusdEvent.add(b);
                    buffer.add(b);
                } catch (PortReadException | BufferToLongException e) {
                    error = e;
                    log.error("", e);
                    break;
                } catch (Throwable t) {
                    log.error("Unknown error", t);
                }
            }
            log.info("Close thread: " + port.getSystemPortName());
        };
        thread = new Thread(run, "Thread-SerialReadThread " + port.getSystemPortName());
    }

    public void init() {
        thread.start();
    }

    public Throwable error() {
        return error;
    }

    public void closeNoThrow() {
        close = true;
        try {
            thread.join();
        } catch (Throwable t) {
            log.error("", t);
        }
    }
}