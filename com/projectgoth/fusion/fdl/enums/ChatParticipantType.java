/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ChatParticipantType {
    NORMAL(0),
    ADMINISTRATOR(1),
    MUTED(2);

    private byte value;
    private static final HashMap<Byte, ChatParticipantType> LOOKUP;

    private ChatParticipantType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static ChatParticipantType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static ChatParticipantType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (ChatParticipantType chatParticipantType : ChatParticipantType.values()) {
            LOOKUP.put(chatParticipantType.value, chatParticipantType);
        }
    }
}

