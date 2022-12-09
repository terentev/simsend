package com.evg.simsend.simsenddata;

import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static com.google.api.client.util.Preconditions.checkState;

//+CUSD: 0, "04110430043B0430043D0441003A00310039002C003800340440", 72
//+CMTI: "SM",1
public class SimEvent {
    private final static Logger log = LoggerFactory.getLogger(SerialReadThread.class);

    private final static int MAX_EVENT_BUF_SIZE = 2048;
    private final byte[] cmp;
    private final Consumer<String> run;
    private final TByteList data = new TByteArrayList();
    private boolean start;
    private int index = 0;
    private boolean fill = false;

    public SimEvent(String start, Consumer<String> run) {
        checkState(!start.substring(1).contains(start.substring(0, 1)));
        this.cmp = start.getBytes();
        this.run = run == null ? null : new ConsumerNoThrow(run);
    }

    public void add(byte b) {
        if (fill) {
            fill(b);
        } else {
            if (b == cmp[0]) {
                start = true;
                index = 0;
            }
            if (start) {
                if (b == cmp[index]) {
                    index++;
                    if (index == cmp.length) {
                        start = false;
                        fill = true;
                        data.addAll(cmp);
                    }
                } else {
                    start = false;
                }
            }
        }
    }

    private void fill(byte b) {
        if (fill && b == '\r') {
            fill = false;
            byte[] r = data.toArray();
            data.clear();
            if (run != null)
                run.accept(new String(r));
            return;
        }
        if (data.size() > MAX_EVENT_BUF_SIZE) {
            data.clear();
            fill = false;
            throw new RuntimeException("buffer to long");
        }
        data.add(b);
    }

    private static class ConsumerNoThrow implements Consumer<String> {
        private final Consumer<String> run;

        public ConsumerNoThrow(Consumer<String> run) {
            checkState(run != null);
            this.run = run;
        }

        @Override
        public void accept(String str) {
            try {
                run.accept(str);
            } catch (Throwable t) {
                log.error("", t);
            }
        }
    }
}