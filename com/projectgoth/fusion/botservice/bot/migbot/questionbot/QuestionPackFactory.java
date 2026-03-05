/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.questionbot;

import com.projectgoth.fusion.botservice.bot.migbot.questionbot.BahasaQuestionPack;
import com.projectgoth.fusion.botservice.bot.migbot.questionbot.DefaultQuestionPack;
import com.projectgoth.fusion.botservice.bot.migbot.questionbot.QuestionPack;
import com.projectgoth.fusion.common.StringUtil;
import java.util.Locale;

public class QuestionPackFactory {
    public static QuestionPack getQuestionPack(String name, String bundleName, Locale locale) {
        if (StringUtil.isBlank(name) || name.equals("questions")) {
            return new DefaultQuestionPack(bundleName, locale);
        }
        if (name.equals("taring")) {
            return new BahasaQuestionPack(bundleName, locale);
        }
        return null;
    }
}

