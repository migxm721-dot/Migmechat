/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ImDetailType {
    UNREGISTERED(0),
    DISCONNECTED(1),
    CONNECTED(2);

    private byte value;
    private static final HashMap<Byte, ImDetailType> LOOKUP;

    private ImDetailType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static ImDetailType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static ImDetailType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (ImDetailType imDetailType : ImDetailType.values()) {
            LOOKUP.put(imDetailType.value, imDetailType);
        }
    }
}

