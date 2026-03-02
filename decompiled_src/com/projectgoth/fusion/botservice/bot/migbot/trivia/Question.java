/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 *  uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric
 *  uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein
 */
package com.projectgoth.fusion.botservice.bot.migbot.trivia;

import com.projectgoth.fusion.botservice.bot.migbot.trivia.enums.TriviaQuestionTypeEnum;
import org.springframework.util.StringUtils;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class Question {
    int questionID;
    TriviaQuestionTypeEnum type;
    String question;
    String answer;
    char answerChar;
    private AbstractStringMetric metric = new Levenshtein();
    private double similarityScoreThreshold = 0.7;

    public Question(int questionID, TriviaQuestionTypeEnum type, String question, String answer) {
        this.questionID = questionID;
        this.type = type;
        this.question = question;
        this.answer = answer;
    }

    public void setAnswerChar(char answerChar) {
        if (this.type == TriviaQuestionTypeEnum.MULTIPLE_CHOICE) {
            this.answerChar = answerChar;
        }
    }

    public boolean isCorrectAnswer(String a) {
        return StringUtils.hasLength((String)a) && (this.type.value() == 0 ? this.isSimilarAnswer(a) : a.length() == 1 && a.trim().toLowerCase().charAt(0) == this.answerChar);
    }

    public String toString() {
        return "Type: " + this.type.name() + ", Question: " + this.question + ", Answer: " + this.answer;
    }

    private boolean isSimilarAnswer(String guess) {
        if (this.answer.length() > 4) {
            float stringSimilarity = this.metric.getSimilarity(guess, this.answer);
            return (double)stringSimilarity >= this.similarityScoreThreshold;
        }
        return this.answer.equalsIgnoreCase(guess);
    }
}

