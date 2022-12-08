package com.evg.simsend.configuration;

import com.evg.simsend.other.SingletonChecker;
import com.google.gson.Gson;

import java.util.Properties;

public class ConfigurationPropertiesToPropertiesConverter extends SingletonChecker {
    private Gson gson;
    private String prefix;

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Properties convert(ConfigurationProperties value) {
        return ObjectToPropertiesConverter.convert(gson, prefix, value);
    }
}
