/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.instrumentation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ProcessingResultEnum {
    FAILED(-2),
    DROPPED(-1),
    SUCCESSFUL(1);

    private int code;

    private ProcessingResultEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}

