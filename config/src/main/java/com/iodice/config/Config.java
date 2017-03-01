package com.iodice.config;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Config {

    private static final PropertiesConfiguration config;

    static {
        try {
            config = new Configurations().properties(new File("config.configs"));
        } catch (ConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static String getString(String key) {
        return config.getString(key);
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
