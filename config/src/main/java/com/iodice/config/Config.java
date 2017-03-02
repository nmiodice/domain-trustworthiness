package com.iodice.config;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Config {

    private static final List<PropertiesConfiguration> configs = new ArrayList<>();

    public static void init(String... configFiles) {
        try {
            for (String configFile : configFiles) {
                configs.add(new Configurations().properties(new File(configFile)));
            }
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

    public static int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public static List<String> getStringList(String key) {
        String value = getString(key);
        if (value == null) {
            return null;
        }

        return Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList());
    }
}
