package com.evg.simsend.simsenddata;

import com.evg.simsend.simsenddata.exception.BufferToLongException;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;

public class Buffer {
    private TByteList list = new TByteArrayList();
    private boolean running;

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void start() {
        running = true;
    }

    public synchronized void stopClear() {
        running = false;
        list.clear();
    }

    public synchronized int size() {
        return list.size();
    }

    public synchronized void add(byte b) {
        if (!running)
            return;
        if (list.size() > 1024 * 1024)
            throw new BufferToLongException();
        list.add(b);
    }

    public synchronized byte[] get() {
        return list.toArray();
    }
}