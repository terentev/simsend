package com.evg.simsend;


import com.evg.simsend.simsenddata.Command;
import com.evg.simsend.simsenddata.SimSerial;
import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainTest3 {

    public static void main(String... args) throws IOException, InterruptedException, TimeoutException {
        System.setProperty("configuration.file.path", "/home/evg/project/temp/simsend-app/simsend.json");
        Runnable r = () -> p("sms");

        SimSerial ss = new SimSerial(SerialPort.getCommPorts()[0]);

       // while (true) {
            p("");
            p("=======================0");
            String rr = ss.command(Command.ATCCID, null, 50000);
            p(rr);
            p("=======================1");

        SerialPort commPorts = SerialPort.getCommPorts()[0];
       p(commPorts.isOpen());
        boolean b = commPorts.openPort();
        p(b);

        ss.closeNoThrow();
       //     Thread.sleep(3000);
            // break;
      //  }
    }

    private static void p(Object o) {
        System.out.println(o);
    }
}