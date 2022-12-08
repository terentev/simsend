package com.evg.simsend.simsenddata;

import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.google.api.client.util.Preconditions.checkState;

//+CUSD: 0, "04110430043B0430043D0441003A00310039002C003800340440", 72
//+CMTI: "SM",1
public class SimEvent0 {
    private boolean start;
    private int index = 0;
    private final byte[] cmp;
    private final byte[] buf;
    private Consumer<String> run;
    private TByteList data = new TByteArrayList();
    private boolean fill = false;

    public SimEvent0(String start, Consumer<String> run) {
        checkState(start.indexOf((char) 0) == -1);
        this.cmp = start.getBytes();
        this.buf = new byte[cmp.length];
        this.run = run;
    }

    public void add(byte b) {
        if (fill) {
            fill(b);
        } else {
            for (int i = 1; i < buf.length; i++)
                buf[i - 1] = buf[i];
            buf[buf.length - 1] = b;

            if (Arrays.compare(buf, cmp) == 0) {
                Arrays.fill(buf, (byte) 0);
                fill = true;
                data.addAll(cmp);
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
        if (data.size() > 2048) {
            data.clear();
            fill = false;
            throw new RuntimeException("buffer to long");
        }
        data.add(b);
    }
}