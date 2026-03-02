/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.trivia.enums;

import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum TriviaQuestionCategoryEnum {
    ENGLISH(1),
    BAHASA(2);

    private int value;
    public static final int ID_ENGLISH = 1;
    public static final int ID_BAHASA = 2;

    private TriviaQuestionCategoryEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static TriviaQuestionCategoryEnum fromValue(int value) {
        for (TriviaQuestionCategoryEnum e : TriviaQuestionCategoryEnum.values()) {
            if (e.value() != value) continue;
            return e;
        }
        return null;
    }

    public static List<String> getCategoryList() {
        ArrayList<String> categoryList = new ArrayList<String>();
        for (TriviaQuestionCategoryEnum e : TriviaQuestionCategoryEnum.values()) {
            categoryList.add(e.name() + " - " + e.value());
        }
        return categoryList;
    }
}

