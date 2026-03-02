/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum MidletPropertyType {
    RMS(1),
    COOKIE(2);

    private byte value;
    private static final HashMap<Byte, MidletPropertyType> LOOKUP;

    private MidletPropertyType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static MidletPropertyType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static MidletPropertyType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (MidletPropertyType midletPropertyType : MidletPropertyType.values()) {
            LOOKUP.put(midletPropertyType.value, midletPropertyType);
        }
    }
}

