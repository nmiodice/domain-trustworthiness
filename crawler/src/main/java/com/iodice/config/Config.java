package com.iodice.config;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Config {

    private static final PropertiesConfiguration[] configs;

    static {
        try {
            configs = new PropertiesConfiguration[] {
                new Configurations().properties(new File("config.crawler")),
                new Configurations().properties(new File("config.db")),
                new Configurations().properties(new File("config.sqs")) };
        } catch (ConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static String getString(String key) {
        for (PropertiesConfiguration config : configs) {
            if (config.containsKey(key)) {
                return config.getString(key);
            }
        }

        return null;
    }

    public static List<String> getStringList(String key) {
        String value = getString(key);
        if (value == null) {
            return null;
        }

        return Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList());
    }
}
