/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot.migbot.trivia.data;

import com.projectgoth.fusion.botservice.bot.migbot.trivia.Question;
import com.projectgoth.fusion.botservice.bot.migbot.trivia.enums.TriviaQuestionCategoryEnum;
import com.projectgoth.fusion.botservice.bot.migbot.trivia.enums.TriviaQuestionTypeEnum;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MessageBundle;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TriviaQuestions {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(TriviaQuestions.class));
    private static final String BUNDLE_NAME = "resource.Trivia_Questions";
    public static List<Question> englishQuestions = new ArrayList<Question>();
    public static List<Question> bahasaQuestions = new ArrayList<Question>();

    public static List<Question> chooseRandomQuestions(int itemsToSelect) {
        return TriviaQuestions.chooseRandomQuestions(TriviaQuestionCategoryEnum.ENGLISH, itemsToSelect);
    }

    public static List<Question> chooseRandomQuestions(TriviaQuestionCategoryEnum category, int itemsToSelect) {
        Random random = new Random();
        List<Question> questions = TriviaQuestions.getQuestionSet(category);
        int sourceSize = questions.size();
        int[] selections = new int[itemsToSelect];
        ArrayList<Question> resultArray = new ArrayList<Question>();
        for (int count = 0; count < itemsToSelect; ++count) {
            int selection;
            selections[count] = selection = random.nextInt(sourceSize - count);
            for (int scanIdx = count - 1; scanIdx >= 0; --scanIdx) {
                if (selection < selections[scanIdx]) continue;
                ++selection;
            }
            resultArray.add(questions.get(selection));
        }
        return resultArray;
    }

    private static List<Question> getQuestionSet(TriviaQuestionCategoryEnum category) {
        switch (category.value()) {
            case 1: {
                if (englishQuestions.isEmpty()) {
                    englishQuestions = TriviaQuestions.loadQuestions(Locale.ENGLISH);
                }
                return englishQuestions;
            }
            case 2: {
                if (bahasaQuestions.isEmpty()) {
                    bahasaQuestions = TriviaQuestions.loadQuestions(new Locale("id_ID_", "id", "ID"));
                }
                return bahasaQuestions;
            }
        }
        return englishQuestions;
    }

    private static List<Question> loadQuestions(Locale locale) throws MissingResourceException {
        ArrayList<Question> questions = new ArrayList<Question>();
        ResourceBundle englishQuestions = MessageBundle.getBundle(BUNDLE_NAME, locale);
        if (englishQuestions != null) {
            Enumeration<String> keys = englishQuestions.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = englishQuestions.getString(key);
                try {
                    int questionNumber = Integer.parseInt(key);
                    questions.add(TriviaQuestions.createQuestion(questionNumber, value));
                }
                catch (NumberFormatException nfe) {
                    log.warn((Object)("Unable to parse the question number: " + key));
                }
            }
        }
        return questions;
    }

    private static Question createQuestion(int questionNumber, String question) {
        StringTokenizer tokenizer = new StringTokenizer(question, "\t");
        TriviaQuestionTypeEnum typeEnum = null;
        String questionTypeStr = tokenizer.nextToken();
        int type = -1;
        try {
            type = Integer.parseInt(questionTypeStr);
            typeEnum = TriviaQuestionTypeEnum.fromValue(type);
        }
        catch (Exception e) {
            log.warn((Object)("Error parsing question string: " + question));
            typeEnum = TriviaQuestionTypeEnum.OPEN;
        }
        String questionStr = tokenizer.nextToken();
        String answer = tokenizer.nextToken();
        Question questionObj = new Question(questionNumber, typeEnum, questionStr, answer.toLowerCase());
        if (typeEnum == TriviaQuestionTypeEnum.MULTIPLE_CHOICE) {
            if (tokenizer.hasMoreTokens()) {
                char answerChar = tokenizer.nextToken().toLowerCase().charAt(0);
                questionObj.setAnswerChar(answerChar);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Question.answerChar = " + answerChar));
                }
            } else {
                log.warn((Object)("Improper question format for multiple choice question: " + question));
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Question " + questionNumber + ": " + question));
        }
        return questionObj;
    }

    public static void main(String[] args) {
        List<Question> questionList = TriviaQuestions.chooseRandomQuestions(TriviaQuestionCategoryEnum.ENGLISH, 10);
        for (Question question : questionList) {
            System.out.println(question);
        }
    }
}

