/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.userevent.domain;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum VirtualGoodType {
    EMOTICON_PACK(1),
    WALLPAPER(2),
    RINGTONE(3),
    GAME(4),
    VIDEO(5),
    PREMIUM_EMOTICON_PACK(6);

    private final byte value;

    private VirtualGoodType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static VirtualGoodType fromValue(byte value) {
        for (VirtualGoodType e : VirtualGoodType.values()) {
            if (e.value() != value) continue;
            return e;
        }
        return null;
    }
}

