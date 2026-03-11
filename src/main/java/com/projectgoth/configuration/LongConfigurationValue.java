package com.projectgoth.configuration;

public class LongConfigurationValue {

    private final String namespace;
    private final String key;
    private final long defaultValue;

    public LongConfigurationValue(String namespace, String key, long defaultValue) {
        this.namespace = namespace;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public long get() {
        return defaultValue;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }
}
