package com.evg.simsend;


import com.evg.simsend.simsenddata.Command;
import com.evg.simsend.simsenddata.SimSerial;
import com.fazecast.jSerialComm.SerialPort;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainTest7 {

    private static String path = "/home/evg/project/temp/simsend-app/";
    private static volatile boolean exit = false;

    public static void main(String[] args) throws Exception {
        if (System.getProperty("configuration.file.path") == null)
            System.setProperty("configuration.file.path", path("simsend.json"));

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "/com/evg/simsend/spring/applicationContext-all.xml");
        // SimService simService = (SimService) context.getBean("simService");

        SerialPort port = SerialPort.getCommPorts()[0];
        SimSerial ss = new SimSerial(port);
        String send = "" + (char) 26;
        String esc = "" + (char) 27;
        while (true) {
            p("=======================0");
            ss.write("" + (char) 27);
            Thread.sleep(1000);
            ss.command(Command.ATCMGS, "+79991111111", 5000);
            Thread.sleep(1000);
            ss.write("send sms to not block sim");
            Thread.sleep(1000);
            ss.write(send);
            p("=======================1");
            Thread.sleep(100000);
        }

    }

    public static void p(Object o) {
        System.out.println(o);
    }

    private static String path(String file) {
        return path + file;
    }
}