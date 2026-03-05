/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum AlertContentType {
    TEXT(1),
    URL(2),
    URL_WITH_CONFIRMATION(3);

    private byte value;
    private static final HashMap<Byte, AlertContentType> LOOKUP;

    private AlertContentType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static AlertContentType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static AlertContentType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (AlertContentType alertContentType : AlertContentType.values()) {
            LOOKUP.put(alertContentType.value, alertContentType);
        }
    }
}

