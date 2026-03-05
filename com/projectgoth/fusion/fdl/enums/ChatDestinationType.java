/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import com.projectgoth.fusion.packet.ByteValueEnum;
import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ChatDestinationType implements ByteValueEnum
{
    PRIVATE(1),
    GROUP_CHAT(2),
    CHATROOM(3),
    DISTRIBUTION_LIST(4);

    private byte value;
    private static final HashMap<Byte, ChatDestinationType> LOOKUP;

    private ChatDestinationType(byte value) {
        this.value = value;
    }

    @Override
    public byte value() {
        return this.value;
    }

    public static ChatDestinationType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static ChatDestinationType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    public static ChatDestinationType[] fromByteArrayValues(byte[] values) {
        if (values == null) {
            return null;
        }
        ChatDestinationType[] result = new ChatDestinationType[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = ChatDestinationType.fromValue((int)values[i]);
        }
        return result;
    }

    static {
        LOOKUP = new HashMap();
        for (ChatDestinationType chatDestinationType : ChatDestinationType.values()) {
            LOOKUP.put(chatDestinationType.value, chatDestinationType);
        }
    }
}

