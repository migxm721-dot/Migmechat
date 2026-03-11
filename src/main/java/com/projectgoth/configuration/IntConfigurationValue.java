package com.projectgoth.configuration;

public class IntConfigurationValue {

    private final String namespace;
    private final String key;
    private final int defaultValue;

    public IntConfigurationValue(String namespace, String key, int defaultValue) {
        this.namespace = namespace;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public int get() {
        return defaultValue;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }
}
