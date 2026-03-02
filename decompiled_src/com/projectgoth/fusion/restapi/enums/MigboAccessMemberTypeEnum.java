/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.restapi.enums;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum MigboAccessMemberTypeEnum {
    WHITELIST(1),
    BLACKLIST(2),
    LEVEL_GATE_THRESHOLD(3),
    MIN_VERSION(4);

    private int value;

    private MigboAccessMemberTypeEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}

