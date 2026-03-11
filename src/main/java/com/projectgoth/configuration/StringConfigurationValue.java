package com.projectgoth.configuration;

public class StringConfigurationValue {

    private final String namespace;
    private final String key;
    private final String defaultValue;

    public StringConfigurationValue(String namespace, String key, String defaultValue) {
        this.namespace = namespace;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String get() {
        return defaultValue;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return defaultValue;
    }
}
