/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot.migbot.esp;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.bot.Pot;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ExceptionHelper;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.dao.BotDAO;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.leaderboard.Leaderboard;
import com.projectgoth.fusion.slice.BotChannelPrx;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class Esp
extends Bot {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Esp.class));
    public static final String MIN_AMOUNT_JOIN_POT = "minAmountJoinPot";
    public static final String MIN_PLAYERS = "minPlayers";
    public static final String MAX_PLAYERS = "maxPlayers";
    public static final String MIN_RANGE = "minRange";
    public static final String MAX_RANGE = "maxRange";
    public static final String FINAL_ROUND = "finalRound";
    public static final String TIMER_JOIN_GAME = "timeToJoinGame";
    public static final String TIMER_BETWEEN_ROUNDS = "timeBetweenRounds";
    public static final String TIMER_GUESSES = "timeToGuess";
    public static final long IDLE_TIME_VALUE = 5L;
    double minAmountJoinPot = 5.0;
    long timeToJoinGame = 60000L;
    long timeBetweenRounds = 10000L;
    long timeToGuess = 20000L;
    long timeToCancel = 20000L;
    public int minPlayers = 2;
    public int maxPlayers = 5;
    public int minRange = 1;
    public int maxRange = 11;
    public int finalRound = 5;
    Date lastActivityTime;
    private double amountJoinPot = 5.0;
    private BotData.BotStateEnum gameState = BotData.BotStateEnum.NO_GAME;
    private Map<String, Integer> playerGuesses = new HashMap<String, Integer>();
    private Map<String, Integer> playerScores = new HashMap<String, Integer>();
    private int round = 0;
    private long idleInterval = 1800000L;
    private long timeLastGameFinished = System.currentTimeMillis();
    private String startPlayer = "";
    private boolean waitPeriod = false;
    private static final String COMMAND_CANCEL = "!n";
    private ScheduledFuture startingTimer;

    public Esp(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDao) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDao);
        this.loadGameConfig();
        log.info((Object)("GuessBot [" + this.instanceID + "] added to channel [" + this.channel + "]"));
        this.sendChannelMessage(this.createMessage("BOT_ADDED", null));
        this.sendChannelMessage(this.createMessage("GAME_STATE_DEFAULT_AMOUNT", null));
    }

    private void loadGameConfig() {
        this.minAmountJoinPot = this.getDoubleParameter(MIN_AMOUNT_JOIN_POT, this.minAmountJoinPot);
        this.minPlayers = this.getIntParameter(MIN_PLAYERS, this.minPlayers);
        this.maxPlayers = this.getIntParameter(MAX_PLAYERS, this.maxPlayers);
        this.minRange = this.getIntParameter(MIN_RANGE, this.minRange);
        this.maxRange = this.getIntParameter(MAX_RANGE, this.maxRange);
        this.finalRound = this.getIntParameter(FINAL_ROUND, this.finalRound);
        this.timeToJoinGame = this.getLongParameter(TIMER_JOIN_GAME, this.timeToJoinGame);
        this.timeBetweenRounds = this.getLongParameter(TIMER_BETWEEN_ROUNDS, this.timeBetweenRounds);
        this.timeToGuess = this.getLongParameter(TIMER_GUESSES, this.timeToGuess);
    }

    private String createMessage(String messageKey, String player) {
        try {
            String messageToSend;
            if (log.isDebugEnabled()) {
                log.debug((Object)("Looking for messageKey: " + messageKey));
            }
            if ((messageToSend = (String)this.messages.get(messageKey)) == null) {
                messageToSend = messageKey;
            }
            messageToSend = messageToSend.replaceAll("BOTNAME", this.botData.getDisplayName());
            messageToSend = messageToSend.replaceAll("TIMER_JOIN", "" + this.timeToJoinGame);
            messageToSend = messageToSend.replaceAll("CMD_NO", COMMAND_CANCEL);
            messageToSend = messageToSend.replaceAll("CMD_JOIN", "!j");
            messageToSend = messageToSend.replaceAll("CMD_START", "!start");
            messageToSend = messageToSend.replaceAll("MINPLAYERS", this.minPlayers + "");
            messageToSend = messageToSend.replaceAll("MAXPLAYERS", this.maxPlayers + "");
            if (player != null) {
                messageToSend = messageToSend.replaceAll("PLAYER", player);
            }
            messageToSend = messageToSend.replaceAll("CURRENCY", "USD");
            messageToSend = messageToSend.replaceAll("AMOUNT_POT", this.amountJoinPot / 100.0 + "");
            messageToSend = messageToSend.replaceAll("CUSTOM_MIN_AMOUNT", this.amountJoinPot + 1.0 + "");
            if (this.round != 0) {
                messageToSend = messageToSend.replaceAll("ROUND_NUMBER", this.round + "");
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Found message for key:" + messageToSend));
            }
            return messageToSend;
        }
        catch (NullPointerException e) {
            log.error((Object)("Outgoing message could not be created, key = " + messageKey), (Throwable)e);
            return "";
        }
    }

    public boolean isIdle() {
        return this.gameState == BotData.BotStateEnum.NO_GAME && System.currentTimeMillis() - this.timeLastGameFinished > this.idleInterval;
    }

    public boolean canBeStoppedNow() {
        return (this.gameState != BotData.BotStateEnum.PLAYING || this.pot == null) && this.gameState != BotData.BotStateEnum.GAME_JOINING && this.gameState != BotData.BotStateEnum.GAME_STARTING;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopBot() {
        Esp esp = this;
        synchronized (esp) {
            this.endGame(true);
            this.gameState = BotData.BotStateEnum.NO_GAME;
        }
    }

    public void onUserJoinChannel(String username) {
        switch (this.gameState) {
            case NO_GAME: {
                this.sendMessage("Play Guess. Enter !start to start a game.", username);
                break;
            }
            case GAME_STARTING: {
                this.sendMessage(this.createMessage("PLAYER: Guess Game is starting soon.", username), username);
                break;
            }
            case GAME_JOINING: {
                this.sendMessage("Play Guess. Enter !j to join the game.", username);
                break;
            }
            case PLAYING: {
                this.sendMessage("Guess is on going now. Get ready for the next game.", username);
                break;
            }
        }
    }

    public void onUserLeaveChannel(String username) {
    }

    public synchronized void onMessage(String username, String messageText, long receivedTimestamp) {
        block15: {
            if ("!start".equals(messageText = messageText.toLowerCase().trim()) || messageText.startsWith("!start") && messageText.split(" ").length == 2) {
                this.startNewGame(username, messageText);
            } else if (COMMAND_CANCEL.equals(messageText)) {
                if (this.startPlayer.equals(username)) {
                    this.cancelGame(username);
                } else {
                    this.sendMessage("Only " + this.startPlayer + " can cancel the pot", username);
                }
            } else if ("!j".equals(messageText)) {
                this.joinGame(username);
            } else if (messageText.startsWith("!")) {
                String input = messageText.substring("!".length());
                if (!this.playerGuesses.containsKey(username)) {
                    this.sendMessage(this.createMessage("PLAYER: You're not in the game.", username), username);
                    return;
                }
                try {
                    Integer guess = Integer.parseInt(input);
                    if (this.waitPeriod) {
                        this.sendMessage("Please wait till the round begins.", username);
                        break block15;
                    }
                    if (guess < this.minRange || guess > this.maxRange) {
                        this.sendMessage(this.createMessage("PLAYER: Guess a number from " + this.minRange + "-" + this.maxRange, username), username);
                        break block15;
                    }
                    this.guessNumber(username, guess);
                }
                catch (NumberFormatException e) {
                    this.sendMessage(this.createMessage("PLAYER: You can only guess numbers.", username), username);
                }
            } else {
                this.sendMessage(messageText + " is not a valid command.", username);
            }
        }
    }

    public void startNewGame(final String username, String messageText) {
        switch (this.gameState) {
            case NO_GAME: {
                this.amountJoinPot = this.minAmountJoinPot;
                if (messageText.length() > "!start".length()) {
                    try {
                        double amount = Double.parseDouble(messageText.substring("!start".length() + 1));
                        if (amount < this.minAmountJoinPot) {
                            this.sendMessage(this.createMessage("PLAYER: Invalid amount. Custom amount has to be CURRENCY " + this.amountJoinPot / 100.0 + " or more (e.g. !start 5) ", username), username);
                            return;
                        }
                        this.amountJoinPot = amount;
                    }
                    catch (NumberFormatException e) {
                        this.sendMessage(this.createMessage("PLAYER: Invalid amount. Custom amount has to be in integer (e.g. !start 5) ", username), username);
                        return;
                    }
                }
                if (!this.userCanAffordToEnterPot(username, this.amountJoinPot / 100.0, true)) {
                    return;
                }
                this.startPlayer = username;
                this.sendMessage(this.createMessage("PLAYER: added to game. Charges apply. CURRENCY " + this.amountJoinPot / 100.0 + ". Create/enter pot. !n to cancel. " + this.timeToCancel / 1000L + " seconds.", username), username);
                this.gameState = BotData.BotStateEnum.GAME_STARTING;
                this.startingTimer = this.executor.schedule(new Runnable(){

                    public void run() {
                        Esp.this.initGame();
                        Esp.this.playerGuesses.put(username, -1);
                        Esp.this.playerScores.put(username, 0);
                        Esp.this.waitForMorePlayers();
                        Esp.this.sendMessage(Esp.this.createMessage("PLAYER: added to game. Charges apply. CURRENCY " + Esp.this.amountJoinPot / 100.0 + ".", username), username);
                        Esp.this.sendChannelMessage(Esp.this.createMessage("Guess game started. !j to join. Cost CURRENCY " + Esp.this.amountJoinPot / 100.0 + ". 60 seconds.", username));
                    }
                }, this.timeToCancel, TimeUnit.MILLISECONDS);
                break;
            }
            case GAME_STARTING: {
                this.sendMessage(this.createMessage("PLAYER: Guess Game is starting soon.", username), username);
                break;
            }
            case GAME_JOINING: {
                this.sendMessage("Game has already started. Enter !j to join the game.", username);
                break;
            }
            case PLAYING: {
                this.sendMessage("A game is currently in progress. Please wait for next game.", username);
                break;
            }
        }
    }

    private void initGame() {
        this.round = 0;
        this.playerGuesses.clear();
        this.playerScores.clear();
        this.pot = null;
    }

    private synchronized void cancelGame(String username) {
        switch (this.gameState) {
            case GAME_STARTING: {
                if (this.startingTimer != null) {
                    this.startingTimer.cancel(true);
                }
                ArrayList<String> player = new ArrayList<String>();
                player.add(username);
                this.revertLimitInCache(player);
                this.gameState = BotData.BotStateEnum.NO_GAME;
                this.amountJoinPot = this.minAmountJoinPot;
                this.sendMessage(this.createMessage("PLAYER: You were not charged.", username), username);
                break;
            }
            default: {
                this.sendChannelMessage("Invalid command.");
            }
        }
    }

    private synchronized void joinGame(String username) {
        switch (this.gameState) {
            case NO_GAME: {
                this.sendMessage("Enter !start to start a game", username);
                break;
            }
            case GAME_STARTING: {
                this.sendMessage(this.createMessage("PLAYER: Guess Game is starting soon.", username), username);
                break;
            }
            case GAME_JOINING: {
                if (this.playerGuesses.containsKey(username)) {
                    this.sendMessage("You have already joined the game. Please wait for the game to start", username);
                    break;
                }
                if (this.playerGuesses.size() + 1 > this.maxPlayers) {
                    this.sendMessage("Too many players joined the game. Max " + this.maxPlayers + " players. Please wait for the next game.", username);
                    break;
                }
                if (!this.userCanAffordToEnterPot(username, this.amountJoinPot / 100.0, true) || this.playerGuesses.put(username, -1) != null) break;
                log.info((Object)(username + " joined the game"));
                this.playerScores.put(username, 0);
                this.sendMessage(this.createMessage("PLAYER: added to game. Charges apply. CURRENCY " + this.amountJoinPot / 100.0 + ".", username), username);
                this.sendChannelMessage(username + " joined the game");
                break;
            }
            case PLAYING: {
                this.sendMessage("A game is currently in progress. Please wait for next game", username);
                break;
            }
        }
    }

    public synchronized double endGame(boolean cancelPot) {
        if (this.gameState == BotData.BotStateEnum.NO_GAME) {
            log.warn((Object)"endGame() called but game has already ended");
            return 0.0;
        }
        double payout = 0.0;
        if (cancelPot) {
            this.revertLimitInCache(this.playerGuesses.keySet());
        }
        if (this.pot != null) {
            if (cancelPot) {
                try {
                    this.pot.cancel();
                }
                catch (Exception e) {
                    log.error((Object)("Unable to cancel pot " + this.pot.getPotID()), (Throwable)e);
                }
            } else {
                try {
                    Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
                    payout = this.pot.payout(true);
                    payout = accountEJB.convertCurrency(payout, "AUD", "USD");
                }
                catch (Exception e) {
                    log.error((Object)("Unable to payout pot " + this.pot.getPotID()), (Throwable)e);
                    payout = -1.0;
                }
            }
        }
        this.timeLastGameFinished = System.currentTimeMillis();
        this.gameState = BotData.BotStateEnum.NO_GAME;
        this.amountJoinPot = this.minAmountJoinPot;
        return payout;
    }

    private void waitForMorePlayers() {
        this.sendChannelMessage("Waiting for more players. Enter !j to join the game");
        this.gameState = BotData.BotStateEnum.GAME_JOINING;
        this.executor.schedule(new Runnable(){

            public void run() {
                Esp.this.chargeAndCountPlayers();
            }
        }, this.timeToJoinGame, TimeUnit.MILLISECONDS);
    }

    private synchronized void chargeAndCountPlayers() {
        try {
            this.pot = new Pot(this);
            LinkedList<String> notAdded = new LinkedList<String>();
            for (String player : this.playerGuesses.keySet()) {
                try {
                    this.pot.enterPlayer(player, this.amountJoinPot / 100.0, "USD");
                }
                catch (Exception e) {
                    this.sendMessage("Unable to join you to the game " + ExceptionHelper.getRawRootMessage(e), player);
                    notAdded.add(player);
                }
            }
            for (String notAddedPlayer : notAdded) {
                this.playerGuesses.remove(notAddedPlayer);
                this.playerScores.remove(notAddedPlayer);
            }
            this.sendChannelMessage("Number of players: " + this.playerGuesses.size());
            if (this.playerGuesses.size() < this.minPlayers) {
                this.endGame(true);
                this.sendChannelMessage("Joining ends. Not enough players. Need " + this.minPlayers + ". Enter !start to start a new game.");
            } else if (this.playerGuesses.size() > this.maxPlayers) {
                this.endGame(true);
                this.sendChannelMessage("Joining ends. Too many players. Max " + this.maxPlayers + ". Enter !start to start a new game.");
            } else {
                this.logGamesPlayed(this.playerGuesses.size(), this.playerGuesses.keySet(), this.amountJoinPot);
                this.incrementGamesPlayed(Leaderboard.Type.GUESS_GAMES_PLAYED, this.playerScores.keySet());
                this.sendChannelMessage("Game begins - Guess the secret number!");
                this.waitForNextRound();
            }
        }
        catch (Exception e) {
            log.error((Object)"Unexpected exception occured in chargeAndCountPlayers()", (Throwable)e);
            this.endGame(true);
            this.sendChannelMessage("Unable to start the game. " + ExceptionHelper.getRawRootMessage(e));
        }
    }

    private synchronized void waitForNextRound() {
        ++this.round;
        this.waitPeriod = true;
        this.sendChannelMessage("Round #" + this.round + " starting in " + this.timeBetweenRounds / 1000L + " seconds.");
        this.executor.schedule(new Runnable(){

            public void run() {
                Esp.this.waitForGuesses();
            }
        }, this.timeBetweenRounds, TimeUnit.MILLISECONDS);
    }

    private synchronized void waitForGuesses() {
        for (String player : this.playerGuesses.keySet()) {
            this.playerGuesses.put(player, -1);
        }
        this.sendChannelMessage("Round #" + this.round + ". Reveal number in " + this.timeToGuess / 1000L + " seconds. !<number> to make your guess.");
        this.gameState = BotData.BotStateEnum.PLAYING;
        this.waitPeriod = false;
        this.executor.schedule(new Runnable(){

            public void run() {
                Esp.this.revealNumber();
            }
        }, this.timeToGuess, TimeUnit.MILLISECONDS);
    }

    private void guessNumber(String username, Integer guess) {
        switch (this.gameState) {
            case NO_GAME: {
                this.sendMessage("Enter !start to start a game.", username);
                break;
            }
            case GAME_JOINING: {
                this.sendMessage("Please wait till the game starts.", username);
                break;
            }
            case PLAYING: {
                Integer number = this.playerGuesses.get(username);
                if (number == null) {
                    this.sendMessage(this.createMessage("PLAYER: You're not in the game. Please wait for next game.", username), username);
                    break;
                }
                if (number == -1) {
                    this.playerGuesses.put(username, guess);
                    this.sendChannelMessage(this.createMessage("PLAYER: Guessed " + guess + ".", username));
                    break;
                }
                this.sendMessage(this.createMessage("PLAYER: You already guessed.", username), username);
                break;
            }
        }
    }

    private void revealNumber() {
        SecureRandom random = new SecureRandom();
        if (this.round > this.finalRound) {
            for (String player : this.playerGuesses.keySet()) {
                Integer guess = this.playerGuesses.get(player);
                if (guess != -1) continue;
                guess = random.nextInt(this.maxRange - this.minRange + 1) + this.minRange;
                this.playerGuesses.put(player, guess);
                this.sendMessage(this.createMessage("Bot guess: " + guess, player), player);
            }
        }
        Integer magicNumber = random.nextInt(this.maxRange - this.minRange) + this.minRange;
        this.sendChannelMessage("TIME'S UP! The magic number was " + magicNumber + "!");
        this.sendChannelMessage("Results for Round #" + this.round + ":");
        Map<String, Integer> tallyMessages = new HashMap();
        for (String player : this.playerGuesses.keySet()) {
            Integer guess = this.playerGuesses.get(player);
            Integer score = 0;
            score = guess == -1 ? Integer.valueOf(0) : (guess.intValue() == magicNumber.intValue() ? Integer.valueOf(2) : Integer.valueOf(1));
            Integer total = this.playerScores.get(player) + score;
            this.playerScores.put(player, total);
            String msg = "";
            switch (score) {
                case 0: {
                    msg = this.createMessage("PLAYER: No guess +" + score + " (" + total + ")", player);
                    break;
                }
                case 1: {
                    msg = this.createMessage("PLAYER: Incorrect +" + score + " (" + total + ")", player);
                    break;
                }
                case 2: {
                    msg = this.createMessage("PLAYER: Correct! +" + score + " (" + total + ")", player);
                    break;
                }
            }
            tallyMessages.put(msg, total);
        }
        tallyMessages = this.sortByValue(tallyMessages);
        for (String message : tallyMessages.keySet()) {
            this.sendChannelMessage(message);
        }
        if (this.round < this.finalRound) {
            this.waitForNextRound();
        } else if (this.round >= this.finalRound) {
            this.playerScores = this.sortByValue(this.playerScores);
            Integer highestScore = -1;
            ArrayList<String> playerRemoved = new ArrayList<String>();
            for (String player : this.playerScores.keySet()) {
                Integer score = this.playerScores.get(player);
                if (score < highestScore) {
                    playerRemoved.add(player);
                    continue;
                }
                highestScore = score;
            }
            for (int i = 0; i < playerRemoved.size(); ++i) {
                String p = (String)playerRemoved.get(i);
                try {
                    this.pot.removePlayer(p);
                }
                catch (Exception e) {
                    log.error((Object)"Unexpected exception occured in removing bottom player from the pot", (Throwable)e);
                }
                this.playerGuesses.remove(p);
                this.playerScores.remove(p);
            }
            if (this.playerGuesses.size() > 1) {
                this.sendChannelMessage("There is a tie. " + this.playerGuesses.size() + " left in the game [" + StringUtil.join(this.playerGuesses.keySet(), ", ") + "]");
                this.waitForNextRound();
            } else if (this.playerGuesses.size() == 1) {
                double payout = this.endGame(false);
                if (payout < 0.0) {
                    this.sendChannelMessageAndPopUp(SystemProperty.get(SystemPropertyEntities.BOT.PAYOUT_FAILURE_MESSAGE));
                } else {
                    String winner = this.playerScores.keySet().iterator().next();
                    this.sendChannelMessageAndPopUp(this.createMessage("Guess Game over! PLAYER WINS CURRENCY " + new DecimalFormat("0.00").format(payout) + " CONGRATS!", winner));
                    this.logMostWins(winner, payout);
                    this.incrementMostWins(Leaderboard.Type.GUESS_MOST_WINS, winner);
                }
                this.executor.schedule(new Runnable(){

                    public void run() {
                        Esp.this.sendChannelMessage(Esp.this.createMessage("GAME_STATE_DEFAULT_AMOUNT", null));
                    }
                }, 3000L, TimeUnit.MILLISECONDS);
            } else if (this.playerGuesses.size() == 0) {
                this.endGame(false);
                this.sendChannelMessage("No more players left in the game.");
                this.executor.schedule(new Runnable(){

                    public void run() {
                        Esp.this.sendChannelMessage(Esp.this.createMessage("GAME_STATE_DEFAULT_AMOUNT", null));
                    }
                }, 3000L, TimeUnit.MILLISECONDS);
            }
        }
    }

    private Map sortByValue(Map map) {
        LinkedList list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator(){

            public int compare(Object o1, Object o2) {
                return ((Comparable)((Map.Entry)o1).getValue()).compareTo(((Map.Entry)o2).getValue());
            }
        });
        Collections.reverse(list);
        LinkedHashMap result = new LinkedHashMap();
        for (Map.Entry entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}

