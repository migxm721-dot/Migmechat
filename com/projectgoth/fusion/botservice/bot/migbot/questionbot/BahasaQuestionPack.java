/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.questionbot;

import com.projectgoth.fusion.botservice.bot.migbot.questionbot.QuestionPack;
import java.util.Locale;

public class BahasaQuestionPack
extends QuestionPack {
    public BahasaQuestionPack(String bundleName, Locale locale) {
        this.questions = BahasaQuestionPack.loadQuestions(bundleName, locale);
    }
}

