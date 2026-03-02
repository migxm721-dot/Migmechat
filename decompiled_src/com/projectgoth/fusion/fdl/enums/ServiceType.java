/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ServiceType {
    X_TXT(1),
    X_TXT_ASIA(2);

    private byte value;
    private static final HashMap<Byte, ServiceType> LOOKUP;

    private ServiceType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static ServiceType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static ServiceType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (ServiceType serviceType : ServiceType.values()) {
            LOOKUP.put(serviceType.value, serviceType);
        }
    }
}

