/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.userevent.domain;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum UserEventType {
    SHORT_TEXT_STATUS(1),
    PHOTO_UPLOAD_WITH_TITLE(2),
    PHOTO_UPLOAD_WITHOUT_TITLE(3),
    CREATE_PUBLIC_CHATROOM(4),
    ADDING_FRIEND(5),
    UPDATING_PROFILE(6),
    PURCHASED_GOODS(7),
    VIRTUAL_GIFT(8),
    GROUP_DONATION(9),
    GROUP_JOINED(10),
    GROUP_ANNOUNCEMENT(11),
    GROUP_USER_POST(12),
    USER_WALL_POST(13),
    GENERIC_APP_EVENT(14),
    GIFT_SHOWER_EVENT(15);

    private final byte value;

    private UserEventType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static UserEventType fromValue(byte value) {
        for (UserEventType e : UserEventType.values()) {
            if (e.value() != value) continue;
            return e;
        }
        return null;
    }
}

