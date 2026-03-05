/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum UserPermissionType {
    ALLOW(1),
    BLOCK(2);

    private byte value;
    private static final HashMap<Byte, UserPermissionType> LOOKUP;

    private UserPermissionType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static UserPermissionType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static UserPermissionType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (UserPermissionType userPermissionType : UserPermissionType.values()) {
            LOOKUP.put(userPermissionType.value, userPermissionType);
        }
    }
}

