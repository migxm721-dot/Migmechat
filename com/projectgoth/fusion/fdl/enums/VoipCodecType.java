/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum VoipCodecType {
    ULAW(1),
    ALAW(2),
    GSM(3),
    G729(4);

    private byte value;
    private static final HashMap<Byte, VoipCodecType> LOOKUP;

    private VoipCodecType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static VoipCodecType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static VoipCodecType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (VoipCodecType voipCodecType : VoipCodecType.values()) {
            LOOKUP.put(voipCodecType.value, voipCodecType);
        }
    }
}

