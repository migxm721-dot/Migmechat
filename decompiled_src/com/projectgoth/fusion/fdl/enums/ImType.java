/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import com.projectgoth.fusion.packet.ByteValueEnum;
import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ImType implements ByteValueEnum
{
    FUSION(1),
    MSN(2),
    AIM(3),
    YAHOO(4),
    ICQ(5),
    GTALK(6),
    FACEBOOK(7);

    private byte value;
    private static final HashMap<Byte, ImType> LOOKUP;

    private ImType(byte value) {
        this.value = value;
    }

    @Override
    public byte value() {
        return this.value;
    }

    public static ImType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static ImType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    public static ImType[] fromByteArrayValues(byte[] values) {
        if (values == null) {
            return null;
        }
        ImType[] result = new ImType[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = ImType.fromValue((int)values[i]);
        }
        return result;
    }

    static {
        LOOKUP = new HashMap();
        for (ImType imType : ImType.values()) {
            LOOKUP.put(imType.value, imType);
        }
    }
}

