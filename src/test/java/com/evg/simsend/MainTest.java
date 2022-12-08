package com.evg.simsend;


import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_SEMI_BLOCKING;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_WRITE_BLOCKING;


public class MainTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello world");

        SerialPort[] ports = SerialPort.getCommPorts();

        for (SerialPort port : ports) {
            System.out.println(port.getSystemPortName());
        }

        //comPort.setBaudRate(115200);
        SerialPort comPort = SerialPort.getCommPorts()[0];
        comPort.setComPortTimeouts(TIMEOUT_READ_SEMI_BLOCKING | TIMEOUT_WRITE_BLOCKING, 1000, 0);
        comPort.setBaudRate(115200);
        boolean op = comPort.openPort();

        if (op == false)
            throw new RuntimeException("");

        //  p(comPort.getDeviceWriteBufferSize());
        Random rnd = new Random();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    List<String> list = new ArrayList<>();
                    list.add("AT+CMGL=\"ALL\"\r");
                    list.add("AT+COPS\r");
                    list.add("AT+GMM\r");
                    // list.add("AT+CFUN=1\r");
                    list.add("AT+CSQ\r");
                    //  list.add("AT+COPS=?\r");
                    list.add("AT+COPS?\r");
                    list.add("AT+CSCS?\r");
                    list.add("AT+CREG?\r");
                    list.add("AT+CSCA?\r");
                    list.add("ATI\r");
                    // list.add("\r");

                    // comPort.getDeviceWriteBufferSize()
                    //  String s = "ATI\r";
                    //  comPort.writeBytes(s.getBytes(), s.getBytes().length);
                    // p("XXX");
                    // String  s = "AT+CMGF=1\r";
                    // comPort.writeBytes(s.getBytes(), s.getBytes().length);
                    String s = list.get(rnd.nextInt(list.size()));
                    //  String s = "A";
                    //   comPort.writeBytes(s.getBytes(), s.getBytes().length);
                    p("WRITE END");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
        thread.start();

        try {
            while (true) {
                byte[] readBuffer = new byte[1];
                int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                if (numRead == 0)
                    continue;
                // p("numRead=" + numRead);
                if (numRead == -1)
                    throw new RuntimeException();
                byte b = readBuffer[0];
                if (b == 10 || b == 13) {
                    System.out.print("[" + b + "]");
                } else {
                    System.out.print(new String(readBuffer));
                }

                Thread.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        comPort.closePort();


    }

    private static void data(List<Byte> data) {
        // System.out.println(data.size());
        byte[] b = new byte[data.size()];
        for (int i = 0; i < b.length; i++) {
            b[i] = data.get(i);
        }
        System.out.println(new String(b));
    }

    private static void p(Object o) {
        System.out.println(o);
    }
}
