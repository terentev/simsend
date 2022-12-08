package com.evg.simsend.configuration;


import com.evg.simsend.other.SingletonChecker;

import java.util.Properties;


public class LocalPropertiesFactory extends SingletonChecker {
    private ConfigurationService configurationService;
    private ConfigurationPropertiesToPropertiesConverter converter;

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setConverter(ConfigurationPropertiesToPropertiesConverter converter) {
        this.converter = converter;
    }

    public Properties create() {
        return converter.convert(configurationService.getConfiguration());
    }
}