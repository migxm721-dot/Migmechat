/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import com.projectgoth.fusion.packet.ByteValueEnum;
import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum PresenceType implements ByteValueEnum
{
    AVAILABLE(1),
    ROAMING(2),
    BUSY(3),
    AWAY(4),
    OFFLINE(99);

    private byte value;
    private static final HashMap<Byte, PresenceType> LOOKUP;

    private PresenceType(byte value) {
        this.value = value;
    }

    @Override
    public byte value() {
        return this.value;
    }

    public static PresenceType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static PresenceType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    public static PresenceType[] fromByteArrayValues(byte[] values) {
        if (values == null) {
            return null;
        }
        PresenceType[] result = new PresenceType[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = PresenceType.fromValue((int)values[i]);
        }
        return result;
    }

    public boolean isOnline() {
        switch (this) {
            case AVAILABLE: 
            case ROAMING: 
            case BUSY: 
            case AWAY: {
                return true;
            }
        }
        return false;
    }

    static {
        LOOKUP = new HashMap();
        for (PresenceType presenceType : PresenceType.values()) {
            LOOKUP.put(presenceType.value, presenceType);
        }
    }
}

