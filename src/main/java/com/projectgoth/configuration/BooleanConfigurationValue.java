package com.projectgoth.configuration;

public class BooleanConfigurationValue {

    private final String namespace;
    private final String key;
    private final boolean defaultValue;

    public BooleanConfigurationValue(String namespace, String key, boolean defaultValue) {
        this.namespace = namespace;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public boolean get() {
        return defaultValue;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }
}
