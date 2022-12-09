package com.evg.simsend.configuration;

import com.evg.simsend.other.SingletonChecker;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class ConfigurationService extends SingletonChecker {
    private final static Logger log = LoggerFactory.getLogger(ConfigurationService.class);
    private final static String CONFIGURATION_FILE_PATH_PROPERTY = "configuration.file.path";
    private final static Gson gson = new Gson();

    private final File file;
    private final SimpleFileReloadStrategy reload;

    private volatile ConfigurationProperties configurationProperties;

    public ConfigurationService() throws IOException {
        file = Help.getConfigurationFileFromProperties();
        load();
        reload = new SimpleFileReloadStrategy(5);
    }

    public ConfigurationProperties getConfiguration() {
        return configurationProperties;
    }

    private void load() throws IOException {
        configurationProperties = Help.parseFile(file);
    }

    public void close() {
        reload.close();
    }

    public static ConfigurationProperties getConfigurationProperties() {
        return Help.parseFile(Help.getConfigurationFileFromProperties());
    }

    public static class Help {

        public static File getConfigurationFileFromProperties() {
            String configurationFilePath = System.getProperty(CONFIGURATION_FILE_PATH_PROPERTY);
            if (configurationFilePath == null)
                throw new IllegalStateException("System property not set: " + CONFIGURATION_FILE_PATH_PROPERTY);
            return new File(configurationFilePath);
        }

        public static ConfigurationProperties parseFile(File file) {
            return gson.fromJson(mergeEnvAndProperties(file), ConfigurationProperties.class);
        }

        private static String mergeEnvAndProperties(File file) {
            try {
                String str = FileUtils.readFileToString(file, "UTF-8");
                Map<String, String> env = new TreeMap<>(System.getenv());
                Map<String, String> prop = new TreeMap<>((Map<String, String>) (Map) System.getProperties());
                env.putAll(prop);
                for (Map.Entry<String, String> x : env.entrySet()) {
                    String key = "${" + x.getKey() + "}";
                    if (str.contains(key))
                        str = str.replaceAll(Pattern.quote(key), x.getValue());
                }
                return str;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class SimpleFileReloadStrategy {
        private volatile boolean close;
        private Thread thread;

        private SimpleFileReloadStrategy(final int second) {
            Runnable run = () -> {
                long last = file.lastModified() - 1;
                while (true) {
                    if (close)
                        break;
                    try {
                        Thread.sleep(second * 1000);
                        long current = file.lastModified();
                        if (current == 0)
                            throw new IllegalStateException("lastModified is 0: " + file.getAbsolutePath());
                        if (last == current)
                            continue;
                        load();
                        last = file.lastModified();
                        log.info("Configuration reloaded: " + file.getAbsolutePath());
                    } catch (InterruptedException e) {
                        log.info("ConfigurationService interrupted");
                        break;
                    } catch (Throwable t) {
                        log.error("Read config error", t);
                    }
                }
            };
            thread = new Thread(run, "SimpleFileReloadStrategy");
            thread.setDaemon(true);
            thread.start();
        }

        public void close() {
            close = true;
            thread.interrupt();
        }
    }
}