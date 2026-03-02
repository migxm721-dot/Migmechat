/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric
 *  uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein
 */
package com.projectgoth.fusion.botservice.bot.migbot.questionbot;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.bot.Pot;
import com.projectgoth.fusion.botservice.bot.migbot.questionbot.Question;
import com.projectgoth.fusion.botservice.bot.migbot.questionbot.QuestionPack;
import com.projectgoth.fusion.botservice.bot.migbot.questionbot.QuestionPackFactory;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ExceptionHelper;
import com.projectgoth.fusion.common.MessageBundle;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.dao.BotDAO;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.slice.BotChannelPrx;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class QuestionBot
extends Bot {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(QuestionBot.class));
    DecimalFormat df = new DecimalFormat("0.00");
    public static final String MIN_POT_ENTRY = "minPotEntry";
    public static final String DEFAULT_POT_ENTRY = "defaultPotEntry";
    public static final String TIME_TO_ANSWER = "timeToAnswer";
    public static final String TIME_BETWEEN_QUESTION_REPEATS = "timeBetweenQuestionRepeats";
    public static final String SIMILARITY_SCORE_THRESHOLD = "similarityScoreThreshold";
    public static final String TIME_BETWEEN_PLAY_NOW_MESSAGES = "timeBetweenPlayNowMessages";
    public static final String TIME_TO_ASK_AGAIN = "timeToAskAgain";
    private double minPotEntry = 0.03;
    private double defaultPotEntry = 0.03;
    private long timeToAnswer = 60000L;
    private long timeBetweenQuestionRepeats = 10000L;
    private long timeBetweenPlayNowMessages = 150000L;
    private long timeToAskAgain = 20000L;
    private BotData.BotStateEnum gameState = BotData.BotStateEnum.NO_GAME;
    private double currentPotEntry;
    private long idleInterval = 1800000L;
    private long timeLastGameFinished = System.currentTimeMillis();
    private String askingPlayer;
    private long timeQuestionAsked;
    private int numIncorrectGuesses;
    private QuestionPack questionPack;
    private Question currentQuestion;
    private Set<String> usersWhoPlayed = new HashSet<String>();
    private boolean lastQuestionAnsweredCorrectly = true;
    private ScheduledFuture questionTimer;
    private ScheduledFuture repeatQuestionTimer;
    private ScheduledFuture playNowTimer;
    private String commandAsk = "";
    private String commandAskAgain = "";
    private AbstractStringMetric metric = new Levenshtein();
    private double similarityScoreThreshold = 0.7;
    private static final String QUESTIONS_BUNDLE_NAME = "resource.QuestionBot_Questions";
    private static final String COMMANDS_BUNDLE_NAME = "resource.QuestionBot_Commands";
    private static final String MESSAGES_BUNDLE_NAME = "resource.QuestionBot_Messages";
    Locale locale = null;

    public QuestionBot(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDao) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDao);
        this.loadGameConfig();
        log.info((Object)("QuestionBot [" + this.instanceID + "] added to channel [" + this.channel + "]"));
        String commandName = botData.getCommandName();
        this.locale = commandName.equals("taring") ? new Locale("in", "ID") : Locale.ENGLISH;
        this.commandAsk = MessageBundle.getMessage(COMMANDS_BUNDLE_NAME, this.locale, "command.questionbot.ask");
        this.commandAskAgain = MessageBundle.getMessage(COMMANDS_BUNDLE_NAME, this.locale, "command.questionbot.again");
        this.sendChannelMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.botAdded", botData.getDisplayName()));
        this.playNowTimer = executor.scheduleWithFixedDelay(new Runnable(){

            public void run() {
                QuestionBot.this.sendChannelMessage(MessageBundle.getMessage(QuestionBot.MESSAGES_BUNDLE_NAME, QuestionBot.this.locale, "message.questionbot.playNow", QuestionBot.this.commandAsk, "USD", QuestionBot.this.df.format(QuestionBot.this.defaultPotEntry)));
            }
        }, 0L, this.timeBetweenPlayNowMessages, TimeUnit.MILLISECONDS);
    }

    private void loadGameConfig() {
        this.minPotEntry = this.getDoubleParameter(MIN_POT_ENTRY, this.minPotEntry);
        this.defaultPotEntry = this.getDoubleParameter(DEFAULT_POT_ENTRY, this.defaultPotEntry);
        this.timeToAnswer = this.getLongParameter(TIME_TO_ANSWER, this.timeToAnswer);
        this.timeBetweenQuestionRepeats = this.getLongParameter(TIME_BETWEEN_QUESTION_REPEATS, this.timeBetweenQuestionRepeats);
        this.similarityScoreThreshold = this.getDoubleParameter(SIMILARITY_SCORE_THRESHOLD, this.similarityScoreThreshold);
        this.timeBetweenPlayNowMessages = this.getLongParameter(TIME_BETWEEN_PLAY_NOW_MESSAGES, this.timeBetweenPlayNowMessages);
        this.timeToAskAgain = this.getLongParameter(TIME_TO_ASK_AGAIN, this.timeToAskAgain);
    }

    public boolean isIdle() {
        return this.gameState == BotData.BotStateEnum.NO_GAME && System.currentTimeMillis() - this.timeLastGameFinished > this.idleInterval;
    }

    public boolean canBeStoppedNow() {
        return this.gameState != BotData.BotStateEnum.PLAYING;
    }

    public synchronized void stopBot() {
        this.endGame();
        if (this.playNowTimer != null) {
            this.playNowTimer.cancel(true);
            this.playNowTimer = null;
        }
    }

    public void onUserJoinChannel(String username) {
        switch (this.gameState) {
            case NO_GAME: {
                this.sendMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.playNow", this.commandAsk, "USD", this.df.format(this.defaultPotEntry)), username);
                break;
            }
            case PLAYING: {
                this.sendMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.questionAsked", username, this.currentQuestion.getQuestion(), Long.toString((this.timeQuestionAsked + this.timeToAnswer - System.currentTimeMillis()) / 1000L), "USD", this.df.format(this.currentPotEntry)), username);
                break;
            }
        }
    }

    public void onUserLeaveChannel(String username) {
    }

    public synchronized void onMessage(String username, String messageText, long receivedTimestamp) {
        if (this.isGameCommand(messageText = messageText.toLowerCase().trim(), this.commandAskAgain)) {
            this.startNewGame(username, messageText, true);
        } else if (this.isGameCommand(messageText, this.commandAsk)) {
            this.startNewGame(username, messageText, false);
        } else if (this.gameState != BotData.BotStateEnum.PLAYING) {
            this.sendMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.invalidCommand", messageText), username);
        } else {
            this.playerGuess(username, messageText.substring(1));
        }
    }

    private void startNewGame(String username, String messageText, boolean askAgain) {
        switch (this.gameState) {
            case NO_GAME: {
                String commandUsed;
                this.currentPotEntry = this.defaultPotEntry;
                String string = commandUsed = askAgain ? this.commandAskAgain : this.commandAsk;
                if (askAgain && (this.lastQuestionAnsweredCorrectly || this.timeLastGameFinished + this.timeToAskAgain < System.currentTimeMillis())) {
                    this.sendMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.invalidCommand", this.commandAskAgain), username);
                    return;
                }
                if (messageText.length() > commandUsed.length()) {
                    try {
                        double amount = Double.parseDouble(messageText.substring(commandUsed.length() + 1)) / 100.0;
                        if (amount < this.minPotEntry) {
                            this.sendMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.invalidAmountCurrency", username, "USD", this.df.format(this.minPotEntry)), username);
                            return;
                        }
                        this.currentPotEntry = amount;
                    }
                    catch (NumberFormatException e) {
                        this.sendMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.invalidAmount", username), username);
                        return;
                    }
                }
                if (!this.userCanAffordToEnterPot(username, this.currentPotEntry, true)) {
                    return;
                }
                try {
                    this.pot = new Pot(this);
                    this.pot.enterPlayer(username, this.currentPotEntry, "USD");
                }
                catch (Exception e) {
                    this.sendMessage("Unable to start the game: " + ExceptionHelper.getRawRootMessage(e), username);
                    return;
                }
                if (this.questionPack == null) {
                    this.questionPack = QuestionPackFactory.getQuestionPack(this.botData.getCommandName(), QUESTIONS_BUNDLE_NAME, this.locale);
                }
                if (this.questionPack != null && !askAgain) {
                    this.currentQuestion = this.questionPack.getNextQuestion();
                }
                if (this.playNowTimer != null) {
                    this.playNowTimer.cancel(false);
                    this.playNowTimer = null;
                }
                this.timeQuestionAsked = System.currentTimeMillis();
                this.askingPlayer = username;
                this.usersWhoPlayed.add(username);
                this.lastQuestionAnsweredCorrectly = false;
                this.sendChannelMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.questionAsked", username, this.currentQuestion.getQuestion(), Long.toString((this.timeQuestionAsked + this.timeToAnswer - System.currentTimeMillis()) / 1000L), "USD", this.df.format(this.currentPotEntry)));
                this.questionTimer = this.executor.schedule(new Runnable(){

                    public void run() {
                        QuestionBot.this.questionTimedOut();
                    }
                }, this.timeToAnswer - 100L, TimeUnit.MILLISECONDS);
                this.repeatQuestionTimer = this.executor.scheduleWithFixedDelay(new Runnable(){

                    public void run() {
                        if (!QuestionBot.this.repeatQuestion()) {
                            throw new IllegalStateException("");
                        }
                    }
                }, this.timeBetweenQuestionRepeats - 100L, this.timeBetweenQuestionRepeats, TimeUnit.MILLISECONDS);
                this.gameState = BotData.BotStateEnum.PLAYING;
                break;
            }
            case PLAYING: {
                if (username.equals(this.askingPlayer)) {
                    this.sendMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.questionAlreadyAsked", username), username);
                    break;
                }
                this.sendMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.gameInProgress", "USD", this.df.format(this.currentPotEntry)), username);
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void questionTimedOut() {
        if (this.gameState != BotData.BotStateEnum.PLAYING) {
            return;
        }
        if (this.numIncorrectGuesses > 0) {
            double payout = 0.0;
            try {
                try {
                    double payoutInBaseCurrency = this.pot.payout(true);
                    Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
                    payout = accountEJB.convertCurrency(payoutInBaseCurrency, "AUD", "USD");
                    this.sendChannelMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.timeOutWithAttempts", this.askingPlayer, "USD", this.df.format(payout)));
                }
                catch (Exception e) {
                    log.error((Object)("Unable to pay out pot " + this.pot.getPotID()), (Throwable)e);
                    this.sendChannelMessageAndPopUp(SystemProperty.get(SystemPropertyEntities.BOT.PAYOUT_FAILURE_MESSAGE));
                    Object var7_7 = null;
                    this.pot = null;
                }
                Object var7_6 = null;
                this.pot = null;
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                this.pot = null;
                throw throwable;
            }
        } else {
            this.sendChannelMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.timeOutNoAttempts", this.botData.getDisplayName(), this.askingPlayer, "USD", this.df.format(this.currentPotEntry)));
            try {
                this.pot.cancel();
            }
            catch (Exception e) {
                log.error((Object)("Unable to cancel pot " + this.pot.getPotID()), (Throwable)e);
            }
            this.pot = null;
        }
        log.info((Object)this.getLogMessage("QUESTION TIMED OUT. ID: " + this.currentQuestion.getID() + "; Incorrect guesses: " + this.numIncorrectGuesses));
        this.sendChannelMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.askAgain", this.commandAskAgain, "USD", this.df.format(this.defaultPotEntry), Long.toString(this.timeToAskAgain / 1000L)));
        this.lastQuestionAnsweredCorrectly = false;
        this.endGame();
    }

    private synchronized boolean repeatQuestion() {
        if (this.gameState != BotData.BotStateEnum.PLAYING) {
            return false;
        }
        this.sendChannelMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.questionAsked", this.askingPlayer, this.currentQuestion.getQuestion(), Long.toString((this.timeQuestionAsked + this.timeToAnswer - System.currentTimeMillis()) / 1000L), "USD", this.df.format(this.currentPotEntry)));
        return true;
    }

    private void playerGuess(String username, String guess) {
        block16: {
            if (!this.userCanAffordToEnterPot(username, this.currentPotEntry, true)) {
                return;
            }
            try {
                this.pot.enterPlayer(username, this.currentPotEntry, "USD");
                boolean correctAnswer = false;
                double stringSimilarity = 0.0;
                if (!StringUtil.isBlank(guess)) {
                    guess = guess.trim().toLowerCase();
                    String answer = this.currentQuestion.getAnswer().toLowerCase();
                    if (answer.length() <= 4) {
                        correctAnswer = guess.equals(answer);
                    } else {
                        stringSimilarity = this.metric.getSimilarity(guess, answer);
                        if (stringSimilarity >= this.similarityScoreThreshold) {
                            correctAnswer = true;
                        }
                    }
                }
                this.usersWhoPlayed.add(username);
                if (correctAnswer) {
                    double payout;
                    if (!this.askingPlayer.equalsIgnoreCase(username)) {
                        this.pot.removePlayer(this.askingPlayer);
                    }
                    try {
                        double payoutInBaseCurrency = this.pot.payout(true);
                        Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
                        payout = accountEJB.convertCurrency(payoutInBaseCurrency, "AUD", "USD");
                    }
                    catch (Exception ex) {
                        payout = -1.0;
                    }
                    this.pot = null;
                    if (payout < 0.0) {
                        this.sendChannelMessageAndPopUp(SystemProperty.get(SystemPropertyEntities.BOT.PAYOUT_FAILURE_MESSAGE));
                    } else {
                        String message = MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.correctAnswerToPlayers", username, this.currentQuestion.getAnswer());
                        for (String user : this.usersWhoPlayed) {
                            this.sendMessage(message, user);
                        }
                        this.sendChannelMessageAndPopUp(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.correctAnswerToAll", username, "USD", this.df.format(payout)));
                    }
                    log.info((Object)this.getLogMessage("QUESTION ANSWERED. ID: " + this.currentQuestion.getID() + "; Time to answer: " + (System.currentTimeMillis() - this.timeQuestionAsked) + "; Incorrect guesses: " + this.numIncorrectGuesses + "; String similarity: " + this.df.format(stringSimilarity)));
                    this.lastQuestionAnsweredCorrectly = true;
                    this.endGame();
                    break block16;
                }
                if (!this.askingPlayer.equalsIgnoreCase(username)) {
                    this.pot.removePlayer(username);
                }
                ++this.numIncorrectGuesses;
                this.sendChannelMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.wrongAnswer", username, guess));
                double currentPayout = this.pot.getCurrentPayout("USD");
                this.sendMessage(MessageBundle.getMessage(MESSAGES_BUNDLE_NAME, this.locale, "message.questionbot.tryAgain", "USD", this.df.format(currentPayout), this.df.format(this.currentPotEntry)), username);
                if (log.isDebugEnabled()) {
                    log.debug((Object)this.getLogMessage("QUESTION ANSWERED INCORRECTLY. ID: " + this.currentQuestion.getID() + "; Answer: " + this.currentQuestion.getAnswer() + "; Guess: " + guess + "; String similarity: " + this.df.format(stringSimilarity)));
                }
            }
            catch (Exception e) {
                this.sendMessage("Your answer could not be processed: " + ExceptionHelper.getRawRootMessage(e), username);
                log.error((Object)("Unable to handle player guess (" + username + " guessing " + guess + ": " + e.getMessage()));
            }
        }
    }

    private synchronized void endGame() {
        if (this.gameState == BotData.BotStateEnum.NO_GAME) {
            return;
        }
        if (this.questionTimer != null) {
            this.questionTimer.cancel(false);
            this.questionTimer = null;
        }
        if (this.repeatQuestionTimer != null) {
            this.repeatQuestionTimer.cancel(true);
            this.repeatQuestionTimer = null;
        }
        if (this.pot != null) {
            try {
                this.pot.cancel();
            }
            catch (Exception e) {
                log.error((Object)("Unable to endGame() with pot ID " + this.pot.getPotID() + ": " + e.getMessage()));
            }
            this.pot = null;
        }
        this.timeLastGameFinished = System.currentTimeMillis();
        this.numIncorrectGuesses = 0;
        this.usersWhoPlayed.clear();
        this.gameState = BotData.BotStateEnum.NO_GAME;
        this.playNowTimer = this.executor.scheduleWithFixedDelay(new Runnable(){

            public void run() {
                QuestionBot.this.sendChannelMessage(MessageBundle.getMessage(QuestionBot.MESSAGES_BUNDLE_NAME, QuestionBot.this.locale, "message.questionbot.playNow", QuestionBot.this.commandAsk, "USD", QuestionBot.this.df.format(QuestionBot.this.defaultPotEntry)));
            }
        }, this.timeToAskAgain, this.timeBetweenPlayNowMessages, TimeUnit.MILLISECONDS);
    }

    private boolean isGameCommand(String messageText, String command) {
        String[] args;
        if (messageText.equals(command)) {
            return true;
        }
        return messageText.startsWith(command) && (args = messageText.split(" "))[0].equals(command) && args.length == 2;
    }
}

