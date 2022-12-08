package com.evg.simsend.configuration;

import ch.qos.logback.core.PropertyDefinerBase;
import com.google.gson.Gson;

import java.util.Properties;

public class LogbackPropertiesConfigurer extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        ConfigurationProperties configuration = ConfigurationService.getConfigurationProperties();
        Properties properties = ObjectToPropertiesConverter.convert(new Gson(), "configuration.log", configuration.log);
        for (String name : properties.stringPropertyNames()) {
            System.setProperty(name, properties.getProperty(name));
        }
        return "";
    }
}