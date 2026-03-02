/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot.migbot.questionbot;

import com.projectgoth.fusion.botservice.bot.migbot.questionbot.Question;
import com.projectgoth.fusion.botservice.bot.migbot.questionbot.QuestionBot;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MessageBundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class QuestionPack {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(QuestionBot.class));
    protected List<Question> questions = new ArrayList<Question>();
    private int nextQuestionIndex = 0;
    private boolean shuffled = false;

    public Question getNextQuestion() {
        if (!this.shuffled) {
            Collections.shuffle(this.questions);
            this.shuffled = true;
        }
        Question nextQuestion = this.questions.get(this.nextQuestionIndex);
        ++this.nextQuestionIndex;
        if (this.nextQuestionIndex >= this.questions.size()) {
            Collections.shuffle(this.questions);
            this.nextQuestionIndex = 0;
        }
        return nextQuestion;
    }

    protected static List<Question> loadQuestions(String bundleName, Locale locale) throws MissingResourceException {
        ArrayList<Question> questions = new ArrayList<Question>();
        ResourceBundle questionsRb = MessageBundle.getBundle(bundleName, locale);
        if (questionsRb != null) {
            Enumeration<String> keys = questionsRb.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = questionsRb.getString(key);
                try {
                    int questionNumber = Integer.parseInt(key);
                    Question question = QuestionPack.createQuestion(questionNumber, value);
                    if (question == null) continue;
                    questions.add(question);
                }
                catch (NumberFormatException nfe) {
                    log.warn((Object)("Unable to parse the question number: " + key));
                }
            }
        } else {
            log.warn((Object)"Couldn't retrieve resource bundle for question pack");
        }
        return questions;
    }

    private static Question createQuestion(int questionNumber, String question) {
        StringTokenizer tokenizer = new StringTokenizer(question, "\t");
        String questionTypeStr = tokenizer.nextToken();
        int type = -1;
        try {
            type = Integer.parseInt(questionTypeStr);
        }
        catch (Exception e) {
            log.warn((Object)("Error parsing question string: " + question));
            return null;
        }
        String questionStr = "";
        String answer = "";
        try {
            questionStr = tokenizer.nextToken();
            answer = tokenizer.nextToken();
        }
        catch (NoSuchElementException e) {
            log.error((Object)("Error retrieving next token. Question was: " + question));
            return null;
        }
        return new Question(type, questionStr, answer);
    }
}

