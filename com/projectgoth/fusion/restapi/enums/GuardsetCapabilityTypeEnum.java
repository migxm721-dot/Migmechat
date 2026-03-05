/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.restapi.enums;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum GuardsetCapabilityTypeEnum {
    GUARD_BY_USER_ID(1),
    GUARD_BY_MIN_CLIENT_VERSION(2);

    private int value;

    private GuardsetCapabilityTypeEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}

