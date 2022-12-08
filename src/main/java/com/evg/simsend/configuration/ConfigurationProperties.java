package com.evg.simsend.configuration;


import com.evg.simsend.configuration.configurationclasses.GlobalConfiguration;
import com.evg.simsend.configuration.configurationclasses.GmailConfiguration;
import com.evg.simsend.configuration.configurationclasses.LogConfiguration;
import com.evg.simsend.configuration.configurationclasses.OtherConfiguration;

public class ConfigurationProperties {
    public GlobalConfiguration global = new GlobalConfiguration();
    public LogConfiguration log = new LogConfiguration();
    public GmailConfiguration gmail = new GmailConfiguration();
    public OtherConfiguration other = new OtherConfiguration();

    public LogConfiguration getLog() {
        return log;
    }

    public OtherConfiguration getOther() {
        return other;
    }

    public void setOther(OtherConfiguration other) {
        this.other = other;
    }

    public GlobalConfiguration getGlobal() {
        return global;
    }
}