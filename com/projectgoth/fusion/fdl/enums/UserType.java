/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum UserType {
    STANDARD(1),
    MERCHANT(2),
    TOP_MERCHANT(3),
    PREPAID_CARD(4);

    private byte value;
    private static final HashMap<Byte, UserType> LOOKUP;

    private UserType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static UserType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static UserType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (UserType userType : UserType.values()) {
            LOOKUP.put(userType.value, userType);
        }
    }
}

