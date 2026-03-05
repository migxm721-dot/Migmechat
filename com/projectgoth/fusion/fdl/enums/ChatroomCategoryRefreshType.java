/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.enums;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ChatroomCategoryRefreshType {
    REPLACE(1),
    APPEND(2);

    private byte value;
    private static final HashMap<Byte, ChatroomCategoryRefreshType> LOOKUP;

    private ChatroomCategoryRefreshType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static ChatroomCategoryRefreshType fromValue(int value) {
        return LOOKUP.get((byte)value);
    }

    public static ChatroomCategoryRefreshType fromValue(Byte value) {
        return LOOKUP.get(value);
    }

    static {
        LOOKUP = new HashMap();
        for (ChatroomCategoryRefreshType chatroomCategoryRefreshType : ChatroomCategoryRefreshType.values()) {
            LOOKUP.put(chatroomCategoryRefreshType.value, chatroomCategoryRefreshType);
        }
    }
}

