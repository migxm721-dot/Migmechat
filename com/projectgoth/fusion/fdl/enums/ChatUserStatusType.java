/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ChatUserStatusType {
    JOINED(1),
    LEFT(2);

    private byte value;
    private static final HashMap<Byte, ChatUserStatusType> LOOKUP;

    private ChatUserStatusType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static ChatUserStatusType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static ChatUserStatusType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (ChatUserStatusType chatUserStatusType : ChatUserStatusType.values()) {
            LOOKUP.put(chatUserStatusType.value, chatUserStatusType);
        }
    }
}

