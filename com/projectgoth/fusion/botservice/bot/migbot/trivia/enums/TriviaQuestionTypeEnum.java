/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.trivia.enums;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum TriviaQuestionTypeEnum {
    OPEN(0),
    MULTIPLE_CHOICE(1);

    private int value;
    public static final int ID_OPEN = 0;
    public static final int ID_MULTIPLE_CHOICE = 1;

    private TriviaQuestionTypeEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static TriviaQuestionTypeEnum fromValue(int value) {
        for (TriviaQuestionTypeEnum e : TriviaQuestionTypeEnum.values()) {
            if (e.value() != value) continue;
            return e;
        }
        return null;
    }
}

