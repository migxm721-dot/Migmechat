/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot.migbot.icarus;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class Icarus
extends Bot {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Icarus.class));
    public static final String EMOTICON_HOTKEY_DICE_PREFIX = "d";
    public static final String AMOUNT_JOIN_POT = "amountJoinPot";
    public static final String MIN_PLAYERS = "minPlayers";
    public static final String MAX_PLAYERS = "maxPlayers";
    public static final String MIN_RANGE = "minRange";
    public static final String MAX_RANGE = "maxRange";
    public static final String FINAL_ROUND = "finalRound";
    public static final String TIMER_JOIN_GAME = "timeToJoinGame";
    public static final String TIMER_CANCEL_GAME = "timeToCancel";
    public static final String TIMER_DECISION_INTERVAL = "decisionInterval";
    double minAmountJoinPot = 5.0;
    double amountJoinPot = 5.0;
    long timeToJoinGame = 60000L;
    long timeToCancel = 20000L;
    long decisionInterval = 20000L;
    public int minPlayers = 2;
    public int maxPlayers = 5;
    public int minRange = 1;
    public int maxRange = 6;
    public int finalRound = 3;
    public int numRollsPerRound = 3;
    private int round = 1;
    private BotData.BotStateEnum gameState = BotData.BotStateEnum.NO_GAME;
    private long idleInterval = 1800000L;
    private long timeLastGameFinished = System.currentTimeMillis();
    private Map<String, Integer> playerScores;
    private Map<String, Integer> playerScoresForCurRound;
    private Map<String, Integer> playerNumRollsPerRound;
    private List<String> players;
    private Integer curPlayerIndex = 0;
    private String startPlayer = "";
    private String currentPlayer;
    private ScheduledFuture decisionTimer;
    private ScheduledFuture waitingPlayersTimer;
    private ScheduledFuture startingTimer;
    private boolean waitRound = false;
    private static final String COMMAND_ROLL = "!r";
    private static final String COMMAND_KEEP = "!k";
    private static final String COMMAND_CANCEL = "!n";

    public Icarus(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDao) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDao);
        this.loadGameConfig();
        this.playerScores = new LinkedHashMap<String, Integer>(this.maxPlayers);
        this.playerScoresForCurRound = new HashMap<String, Integer>(this.maxPlayers);
        this.playerNumRollsPerRound = new HashMap<String, Integer>(this.maxPlayers);
        this.players = new ArrayList<String>(this.maxPlayers);
        log.info((Object)("DangerBot [" + this.instanceID + "] added to channel [" + this.channel + "]"));
        this.sendChannelMessage(this.createMessage("BOT_ADDED", null));
        this.sendChannelMessage(this.createMessage("GAME_STATE_DEFAULT_AMOUNT", null));
    }

    private void loadGameConfig() {
        this.minAmountJoinPot = this.getDoubleParameter(AMOUNT_JOIN_POT, this.minAmountJoinPot);
        this.minPlayers = this.getIntParameter(MIN_PLAYERS, this.minPlayers);
        this.maxPlayers = this.getIntParameter(MAX_PLAYERS, this.maxPlayers);
        this.minRange = this.getIntParameter(MIN_RANGE, this.minRange);
        this.maxRange = this.getIntParameter(MAX_RANGE, this.maxRange);
        this.finalRound = this.getIntParameter(FINAL_ROUND, this.finalRound);
        this.timeToJoinGame = this.getLongParameter(TIMER_JOIN_GAME, this.timeToJoinGame);
        this.timeToCancel = this.getLongParameter(TIMER_CANCEL_GAME, this.timeToCancel);
        this.decisionInterval = this.getLongParameter(TIMER_DECISION_INTERVAL, this.decisionInterval);
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
        Icarus icarus = this;
        synchronized (icarus) {
            this.endGame(true);
            this.gameState = BotData.BotStateEnum.NO_GAME;
        }
    }

    public void onUserJoinChannel(String username) {
        switch (this.gameState) {
            case NO_GAME: {
                this.sendMessage("Play Danger. Enter !start to start a game.", username);
                break;
            }
            case GAME_STARTING: {
                this.sendMessage(this.createMessage("PLAYER: Danger Game is starting soon.", username), username);
                break;
            }
            case GAME_JOINING: {
                this.sendMessage(this.createMessage("Play Danger. Enter !j to join the game. Cost CURRENCY " + this.amountJoinPot / 100.0 + ".", username), username);
                break;
            }
            case PLAYING: {
                this.sendMessage("Danger is on going now. Get ready for the next game.", username);
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onUserLeaveChannel(String username) {
        Icarus icarus = this;
        synchronized (icarus) {
            switch (this.gameState) {
                case GAME_JOINING: {
                    this.removePlayer(username);
                    break;
                }
                case PLAYING: {
                    if (this.currentPlayer == null || !this.currentPlayer.equals(username)) break;
                    if (this.decisionTimer != null) {
                        this.decisionTimer.cancel(true);
                    }
                    if (this.playerNumRollsPerRound.get(username) == 0) {
                        this.botAutoRoll(username);
                    }
                    this.goAroundTheTable();
                    break;
                }
            }
        }
    }

    public synchronized void onMessage(String username, String messageText, long receivedTimestamp) {
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
        } else if (COMMAND_ROLL.equals(messageText)) {
            this.playerAction(username, Decision.ROLL);
        } else if (COMMAND_KEEP.equals(messageText)) {
            this.playerAction(username, Decision.KEEP);
        } else {
            this.sendMessage(messageText + " is not a valid command.", username);
        }
    }

    public void startNewGame(final String username, String messageText) {
        switch (this.gameState) {
            case NO_GAME: {
                this.amountJoinPot = this.minAmountJoinPot;
                if (messageText.length() > "!start".length()) {
                    try {
                        Double amount = Double.parseDouble(messageText.substring("!start".length() + 1));
                        if (amount < this.minAmountJoinPot) {
                            this.sendMessage(this.createMessage("PLAYER: Invalid amount. Custom amount has to be CURRENCY 0.05 or more (e.g. !start 5) ", username), username);
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
                        Icarus.this.initGame();
                        Icarus.this.addPlayer(username);
                        Icarus.this.sendMessage(Icarus.this.createMessage("PLAYER: added to game. Charges apply. CURRENCY " + Icarus.this.amountJoinPot / 100.0 + ".", username), username);
                        Icarus.this.sendChannelMessage(Icarus.this.createMessage("Danger Game started. !j to join. Cost CURRENCY " + Icarus.this.amountJoinPot / 100.0 + ". " + Icarus.this.timeToJoinGame / 1000L + " seconds", username));
                        Icarus.this.waitForMorePlayers();
                    }
                }, this.timeToCancel, TimeUnit.MILLISECONDS);
                break;
            }
            case GAME_STARTING: {
                this.sendMessage(this.createMessage("PLAYER: Danger Game is starting soon.", username), username);
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
        this.pot = null;
        this.playerScoresForCurRound.clear();
        this.playerScores.clear();
        this.playerNumRollsPerRound.clear();
        this.players.clear();
        this.curPlayerIndex = 0;
    }

    private Integer addPlayer(String username) {
        Integer oldScore = this.playerScores.put(username, 0);
        if (oldScore == null) {
            this.playerScoresForCurRound.put(username, 0);
            this.playerNumRollsPerRound.put(username, 0);
            this.players.add(username);
        }
        return oldScore;
    }

    private boolean removePlayer(String username) {
        if (this.playerScores.remove(username) != null) {
            this.playerScoresForCurRound.remove(username);
            this.playerNumRollsPerRound.remove(username);
            this.players.remove(username);
            return true;
        }
        return false;
    }

    private synchronized void joinGame(String username) {
        switch (this.gameState) {
            case NO_GAME: {
                this.sendMessage("Enter !start to start a game", username);
                break;
            }
            case GAME_STARTING: {
                this.sendMessage(this.createMessage("PLAYER: Danger Game is starting soon.", username), username);
                break;
            }
            case GAME_JOINING: {
                if (this.playerScores.containsKey(username)) {
                    this.sendMessage("You have already joined the game. Please wait for the game to start", username);
                    break;
                }
                if (this.playerScores.size() + 1 > this.maxPlayers) {
                    this.sendMessage("Too many players joined the game. Max " + this.maxPlayers + " players. Please wait for the next game.", username);
                    break;
                }
                if (!this.userCanAffordToEnterPot(username, this.amountJoinPot / 100.0, true) || this.addPlayer(username) != null) break;
                log.info((Object)(username + " joined the game"));
                this.sendMessage(this.createMessage("PLAYER: added to game. Charges apply. CURRENCY " + this.amountJoinPot / 100.0 + ".", username), username);
                this.sendChannelMessage(username + " joined the game");
                if (this.playerScores.size() != this.maxPlayers || this.waitingPlayersTimer == null) break;
                this.waitingPlayersTimer.cancel(true);
                this.chargeAndCountPlayers();
                break;
            }
            case PLAYING: {
                this.sendMessage("A game is currently in progress. Please wait for next game", username);
                break;
            }
        }
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

    public synchronized double endGame(boolean cancelPot) {
        if (this.gameState == BotData.BotStateEnum.NO_GAME) {
            log.warn((Object)"endGame() called but game has already ended");
            return 0.0;
        }
        double payout = 0.0;
        if (cancelPot) {
            this.revertLimitInCache(this.playerScores.keySet());
        }
        if (this.decisionTimer != null) {
            this.decisionTimer.cancel(true);
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
        this.waitingPlayersTimer = this.executor.schedule(new Runnable(){

            public void run() {
                Icarus.this.chargeAndCountPlayers();
            }
        }, this.timeToJoinGame, TimeUnit.MILLISECONDS);
    }

    private synchronized void chargeAndCountPlayers() {
        if (this.gameState != BotData.BotStateEnum.GAME_JOINING) {
            return;
        }
        try {
            this.pot = new Pot(this);
            LinkedList<String> notAdded = new LinkedList<String>();
            for (String player : this.playerScores.keySet()) {
                try {
                    this.pot.enterPlayer(player, this.amountJoinPot / 100.0, "USD");
                }
                catch (Exception e) {
                    this.sendMessage("Unable to join you to the game " + ExceptionHelper.getRawRootMessage(e), player);
                    notAdded.add(player);
                }
            }
            for (String notAddedPlayer : notAdded) {
                this.removePlayer(notAddedPlayer);
            }
            if (this.playerScores.size() < this.minPlayers) {
                this.endGame(true);
                this.sendChannelMessage("Joining ends. Not enough players. Need " + this.minPlayers + ". Enter !start to start a new game.");
            } else if (this.playerScores.size() > this.maxPlayers) {
                this.endGame(true);
                this.sendChannelMessage("Joining ends. Too many players. Max " + this.maxPlayers + ". Enter !start to start a new game.");
            } else {
                this.logGamesPlayed(this.playerScores.size(), this.playerScores.keySet(), this.amountJoinPot);
                this.incrementGamesPlayed(Leaderboard.Type.DANGER_GAMES_PLAYED, this.playerScores.keySet());
                this.gameState = BotData.BotStateEnum.PLAYING;
                this.sendChannelMessage("Game begins - Keep rolling for maximum points but watch out for the Skull!");
                this.goAroundTheTable();
            }
        }
        catch (Exception e) {
            log.error((Object)"Unexpected exception occured in chargeAndCountPlayers()", (Throwable)e);
            this.endGame(true);
            this.sendChannelMessage("Unable to start the game. " + ExceptionHelper.getRawRootMessage(e));
        }
    }

    private void goAroundTheTable() {
        if (this.curPlayerIndex == 0 && !this.waitRound) {
            ++this.round;
            if (this.round > 1) {
                this.waitRound = true;
                this.sendChannelMessage("Round #" + this.round + " is starting in 5 seconds.");
                this.executor.schedule(new Runnable(){

                    public void run() {
                        Icarus.this.goAroundTheTable();
                    }
                }, 5000L, TimeUnit.MILLISECONDS);
                return;
            }
        }
        if (this.curPlayerIndex < this.players.size()) {
            Integer n = this.curPlayerIndex;
            Integer n2 = this.curPlayerIndex = Integer.valueOf(this.curPlayerIndex + 1);
            this.currentPlayer = this.players.get(n);
            this.waitRound = false;
            for (int i = 0; i < this.players.size(); ++i) {
                String player = this.players.get(i);
                if (this.currentPlayer.equalsIgnoreCase(player)) {
                    this.sendMessage("Round #" + this.round + ": " + this.currentPlayer + "'s turn. !r to roll. " + this.decisionInterval / 1000L + " seconds.", player);
                    continue;
                }
                this.sendMessage("Round #" + this.round + ": " + this.currentPlayer + "'s turn. " + this.decisionInterval / 1000L + " seconds.", player);
            }
            this.decisionTimer = this.executor.schedule(new Runnable(){

                public void run() {
                    Icarus.this.decisionTimeUp(Icarus.this.currentPlayer);
                }
            }, this.decisionInterval, TimeUnit.MILLISECONDS);
            return;
        }
        if (this.curPlayerIndex >= this.players.size()) {
            this.currentPlayer = null;
            this.curPlayerIndex = 0;
            this.waitRound = false;
            this.roundEnded();
        }
    }

    private void botAutoRoll(String username) {
        SecureRandom random = new SecureRandom();
        Integer dicePoint = random.nextInt(this.maxRange - this.minRange + 1) + this.minRange;
        this.playerNumRollsPerRound.put(username, 1);
        if (dicePoint == 1) {
            this.sendChannelMessage(this.createMessage("Bot rolls - PLAYER: (skull)", username));
        } else {
            Integer curPoint = this.playerScoresForCurRound.get(username);
            this.playerScoresForCurRound.put(username, curPoint + dicePoint);
            this.sendChannelMessage(this.createMessage("Bot rolls - PLAYER: (d" + dicePoint + ")", username));
        }
    }

    private synchronized void decisionTimeUp(String username) {
        if (this.playerNumRollsPerRound.get(username) == 0) {
            this.botAutoRoll(username);
        }
        Integer totalPoints = this.playerScoresForCurRound.get(username);
        this.sendMessage(this.createMessage("BOT KEEPS. " + totalPoints + " points received.", username), username);
        this.goAroundTheTable();
    }

    private void playerAction(String player, Decision decision) {
        switch (this.gameState) {
            case NO_GAME: {
                this.sendMessage("Enter !start to start a game", player);
                break;
            }
            case GAME_JOINING: {
                this.sendMessage("Game stating soon! Please wait.", player);
                break;
            }
            case PLAYING: {
                if (this.waitRound) {
                    this.sendMessage("Round #" + this.round + " starting. Please wait.", player);
                    return;
                }
                if (!player.equals(this.currentPlayer)) {
                    this.sendMessage("It's not your turn", player);
                    return;
                }
                if (decision == Decision.ROLL) {
                    if (this.decisionTimer != null) {
                        this.decisionTimer.cancel(true);
                    }
                    this.roll(player);
                    break;
                }
                if (decision == Decision.KEEP) {
                    if (this.decisionTimer != null) {
                        this.decisionTimer.cancel(true);
                    }
                    this.keep(player);
                    break;
                }
                this.sendMessage("Unexpected decision " + (Object)((Object)decision), player);
                break;
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void roll(String player) {
        int numRolls = this.playerNumRollsPerRound.get(player);
        if (numRolls >= this.numRollsPerRound) {
            this.sendMessage(this.createMessage("PLAYER: You have reached the maximum number of rolls for this round.", player), player);
            return;
        }
        SecureRandom random = new SecureRandom();
        Integer dicePoint = random.nextInt(this.maxRange - this.minRange + 1) + this.minRange;
        if (dicePoint == 1) {
            this.playerScoresForCurRound.put(player, 0);
            this.sendChannelMessage(this.createMessage("PLAYER: You rolled a (skull). TURN ENDS.", player));
            this.goAroundTheTable();
            return;
        }
        Integer curPoint = this.playerScoresForCurRound.get(player);
        this.playerScoresForCurRound.put(player, curPoint + dicePoint);
        this.playerNumRollsPerRound.put(player, ++numRolls);
        if (numRolls >= this.numRollsPerRound) {
            this.sendChannelMessage(this.createMessage("PLAYER: (d" + dicePoint + ") points rolled. Maximum rolls per round reached. Round ends.", player));
            this.goAroundTheTable();
            return;
        }
        this.sendChannelMessage(this.createMessage("PLAYER: (d" + dicePoint + ") points rolled.", player));
        this.sendMessage(this.createMessage("PLAYER, KEEP these or ROLL for more? !r to roll. !k to keep. " + this.decisionInterval / 1000L + " seconds left.", player), player);
        this.decisionTimer = this.executor.schedule(new Runnable(){

            public void run() {
                Icarus.this.decisionTimeUp(Icarus.this.currentPlayer);
            }
        }, this.decisionInterval, TimeUnit.MILLISECONDS);
    }

    private void keep(String player) {
        Integer totalPoints = this.playerScoresForCurRound.get(player);
        this.sendMessage(this.createMessage("PLAYER KEEPS. " + totalPoints + " points received.", player), player);
        this.goAroundTheTable();
    }

    private void roundEnded() {
        if (this.decisionTimer != null) {
            this.decisionTimer.cancel(true);
        }
        this.sendChannelMessage("Round over!");
        this.sendChannelMessage("Results:");
        this.playerScores = this.sortByValue(this.playerScores);
        Map<String, Integer> tallyMessages = new HashMap();
        for (String player : this.players) {
            Integer totalScore;
            if (null == player) continue;
            Integer roundScore = this.playerScoresForCurRound.get(player);
            if (null == roundScore) {
                roundScore = 0;
            }
            if (null == (totalScore = this.playerScores.get(player))) {
                totalScore = 0;
            }
            totalScore = totalScore + roundScore;
            this.playerScores.put(player, totalScore);
            String message = this.createMessage("PLAYER: +" + roundScore + "(" + totalScore + ")", player);
            tallyMessages.put(message, totalScore);
        }
        tallyMessages = this.sortByValue(tallyMessages);
        for (String message : tallyMessages.keySet()) {
            this.sendChannelMessage(message);
        }
        if (this.round < this.finalRound) {
            this.goAroundTheTable();
        } else {
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
                this.removePlayer(p);
                try {
                    this.pot.removePlayer(p);
                    continue;
                }
                catch (Exception e) {
                    log.error((Object)"Unexpected exception occured in removing bottom player from the pot", (Throwable)e);
                }
            }
            if (this.playerScores.size() > 1) {
                this.sendChannelMessage("There is a tie. " + this.playerScores.size() + " left in the game [" + StringUtil.join(this.playerScores.keySet(), ", ") + "]");
                this.goAroundTheTable();
            } else if (this.playerScores.size() == 1) {
                double payout = this.endGame(false);
                if (payout < 0.0) {
                    this.sendChannelMessageAndPopUp(SystemProperty.get(SystemPropertyEntities.BOT.PAYOUT_FAILURE_MESSAGE));
                } else {
                    String winner = this.playerScores.keySet().iterator().next();
                    this.sendChannelMessageAndPopUp(this.createMessage("Danger Game over! PLAYER WINS CURRENCY " + new DecimalFormat("0.00").format(payout) + "! CONGRATS!", winner));
                    this.logMostWins(winner, payout);
                    this.incrementMostWins(Leaderboard.Type.DANGER_MOST_WINS, winner);
                }
                this.sendChannelMessage("Enter !start to start a game");
                this.executor.schedule(new Runnable(){

                    public void run() {
                        Icarus.this.sendChannelMessage(Icarus.this.createMessage("GAME_STATE_DEFAULT_AMOUNT", null));
                    }
                }, 5000L, TimeUnit.MILLISECONDS);
            } else if (this.playerScores.size() == 0) {
                this.sendChannelMessage("No more players left in the game. Enter !start to start a new game");
                this.endGame(false);
                this.executor.schedule(new Runnable(){

                    public void run() {
                        Icarus.this.sendChannelMessage(Icarus.this.createMessage("GAME_STATE_DEFAULT_AMOUNT", null));
                    }
                }, 5000L, TimeUnit.MILLISECONDS);
            }
        }
        for (String player : this.playerScores.keySet()) {
            this.playerScoresForCurRound.put(player, 0);
            this.playerNumRollsPerRound.put(player, 0);
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum Decision {
        ROLL,
        KEEP;

    }
}

