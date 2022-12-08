package com.evg.simsend;


import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;


public class MainTest0 {

    private static final class MessageListener implements SerialPortMessageListener
    {
        @Override
        public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

        @Override
        public byte[] getMessageDelimiter() { return new byte[] { (byte)0x0D, (byte)0x0A }; }

        @Override
        public boolean delimiterIndicatesEndOfMessage() { return true; }

        @Override
        public void serialEvent(SerialPortEvent event)
        {
            byte[] delimitedMessage = event.getReceivedData();
            p("===============================START");
            p(new String(delimitedMessage));
            p("===============================END");
           // System.out.println("Received the following delimited message: " + delimitedMessage);
        }
    }

    public static void main(String[] args) throws Exception {

        SerialPort comPort = SerialPort.getCommPorts()[0];
        comPort.setBaudRate(115200);
        comPort.openPort();
        MessageListener listener = new MessageListener();
        comPort.addDataListener(listener);

        while (comPort != null){
            // comPort.getDeviceWriteBufferSize()
            //  String s = "ATI\r";
            //  comPort.writeBytes(s.getBytes(), s.getBytes().length);
            // p("XXX");
            // String  s = "AT+CMGF=1\r";
            // comPort.writeBytes(s.getBytes(), s.getBytes().length);
            String s = "AT+CMGL=\"ALL\"\r";
            //String s = "A";
            comPort.writeBytes(s.getBytes(), s.getBytes().length);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try { Thread.sleep(500000); } catch (Exception e) { e.printStackTrace(); }
        comPort.removeDataListener();
        comPort.closePort();

    }

    private static void p(Object o) {
        System.out.println(o);
    }
}
