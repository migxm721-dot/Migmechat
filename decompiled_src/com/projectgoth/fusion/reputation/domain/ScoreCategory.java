/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.domain;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ScoreCategory {
    TIME_IN_PRODUCT(0),
    CREDITS_SPENT(1),
    HUMAN_LIKELY_BEHAVIOUR(2),
    BASIC_ACTIVITY(3);

    private final int value;

    private ScoreCategory(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static ScoreCategory fromValue(int value) {
        for (ScoreCategory e : ScoreCategory.values()) {
            if (e.value() != value) continue;
            return e;
        }
        return null;
    }
}

