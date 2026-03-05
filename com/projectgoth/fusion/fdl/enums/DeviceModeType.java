/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum DeviceModeType {
    AWAKE(0),
    SLEEP(1);

    private int value;
    private static final HashMap<Integer, DeviceModeType> LOOKUP;

    private DeviceModeType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static DeviceModeType fromValue(Integer value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (DeviceModeType deviceModeType : DeviceModeType.values()) {
            LOOKUP.put(deviceModeType.value, deviceModeType);
        }
    }
}

