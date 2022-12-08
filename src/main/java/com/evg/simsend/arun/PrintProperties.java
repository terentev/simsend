package com.evg.simsend.arun;

import com.evg.simsend.configuration.ConfigurationProperties;
import com.evg.simsend.configuration.ConfigurationPropertiesToPropertiesConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;


public class PrintProperties {

    public static void main(String[] args) throws Throwable {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String s = gson.toJson(new ConfigurationProperties());
        System.out.println(s);
        ConfigurationPropertiesToPropertiesConverter converter = new ConfigurationPropertiesToPropertiesConverter();
        converter.setGson(new Gson());
        converter.setPrefix("configuration");
        Properties convert = converter.convert(new ConfigurationProperties());
        StringWriter sw = new StringWriter();
        convert.list(new PrintWriter(sw));
        sw.flush();
        System.out.println(sw.toString());
        FileUtils.writeStringToFile(new File("/home/evg/configuration.json"), s, "UTF-8");
    }
}
