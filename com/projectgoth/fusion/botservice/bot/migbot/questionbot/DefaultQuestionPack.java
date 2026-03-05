/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.questionbot;

import com.projectgoth.fusion.botservice.bot.migbot.questionbot.QuestionPack;
import java.util.Locale;

public class DefaultQuestionPack
extends QuestionPack {
    public DefaultQuestionPack(String bundleName, Locale locale) {
        this.questions = DefaultQuestionPack.loadQuestions(bundleName, locale);
    }
}

