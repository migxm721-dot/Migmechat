/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum PhoneNumberType {
    MOBILE(1),
    HOME(2),
    OFFICE(3);

    private byte value;
    private static final HashMap<Byte, PhoneNumberType> LOOKUP;

    private PhoneNumberType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static PhoneNumberType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static PhoneNumberType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (PhoneNumberType phoneNumberType : PhoneNumberType.values()) {
            LOOKUP.put(phoneNumberType.value, phoneNumberType);
        }
    }
}

