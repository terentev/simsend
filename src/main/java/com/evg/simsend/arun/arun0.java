package com.evg.simsend.arun;


import com.evg.simsend.service.SimService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class arun0 {

    private static String path = "/home/evg/project/temp/simsend-app/";
    private static volatile boolean exit = false;

    //java -Xmx100m -Dconfiguration.file.path="/home/evg/project/temp/simsend-app/simsend.json"
    // -jar /home/evg/project/temp/simsend/target/simsend-1.0-SNAPSHOT.jar
    public static void main(String[] args) throws Exception {
        if (System.getProperty("configuration.file.path") == null)
            System.setProperty("configuration.file.path", path("simsend.json"));

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "/com/evg/simsend/spring/applicationContext-all.xml");
        SimService simService = (SimService) context.getBean("simService");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            context.close();
            p("exit0");
            exit = true;
        }));
        while (true) {
            if (exit)
                break;
            Thread.sleep(100);
        }

        p("exit1");
    }

    public static void p(Object o) {
        System.out.println(o);
    }

    private static String path(String file) {
        return path + file;
    }
}