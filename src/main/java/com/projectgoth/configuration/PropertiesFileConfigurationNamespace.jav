package com.projectgoth.configuration;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesFileConfigurationNamespace {

    private final Properties props = new Properties();
    private final String identifier;

    public PropertiesFileConfigurationNamespace(String file) {
        this.identifier = file;
        try {
            props.load(new FileInputStream(file));
        } catch (Exception e) {
            throw new RuntimeException("Cannot load config: " + file, e);
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public String get(String key) {
        return props.getProperty(key);
    }
}
