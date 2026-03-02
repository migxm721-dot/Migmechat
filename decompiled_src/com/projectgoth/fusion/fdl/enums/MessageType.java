/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum MessageType {
    FUSION(1),
    SMS(2),
    EMAIL(3),
    MSN(4),
    AIM(5),
    YAHOO(6),
    FACEBOOK(7),
    GTALK(8),
    OFFLINE_MESSAGE(9),
    SERVER_INFO(98);

    private byte value;
    private static final HashMap<Byte, MessageType> LOOKUP;

    private MessageType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static MessageType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static MessageType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (MessageType messageType : MessageType.values()) {
            LOOKUP.put(messageType.value, messageType);
        }
    }
}

