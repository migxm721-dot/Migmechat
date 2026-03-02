/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot.migbot.warriors;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.bot.Pot;
import com.projectgoth.fusion.botservice.bot.migbot.warriors.Attack;
import com.projectgoth.fusion.botservice.bot.migbot.warriors.Player;
import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.dao.BotDAO;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.leaderboard.Leaderboard;
import com.projectgoth.fusion.slice.BotChannelPrx;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class WarriorsBot
extends Bot {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(WarriorsBot.class));
    DecimalFormat df = new DecimalFormat("0.00");
    private static final SecureRandom RANDOM_GENERATOR = new SecureRandom();
    public static final String MIN_POT_ENTRY = "minPotEntry";
    public static final String DEFAULT_POT_ENTRY = "defaultPotEntry";
    public static final String MAX_HIT_POINTS_BY_MIGLEVEL = "maxHitPointsByMigLevel";
    public static final String MAX_HIT_POINTS_BY_FRIENDS = "maxHitPointsByFriends";
    public static final String MAX_HIT_POINTS_BY_ANTE = "maxHitPointsByAnte";
    public static final String MAX_HIT_POINTS_BY_MIGLEVEL_DIFFERENCE = "maxHitPointsByMigLevelDiff";
    public static final String MAX_ANTE_FOR_HIT_POINTS = "maxAnteForHitPoints";
    public static final String MAX_HIT_POINTS_BY_MIGDNA = "maxHitPointsByMigDNA";
    public static final String MAX_HIT_POINTS_BY_FEVER = "maxHitPointsByFever";
    public static final String TIME_TO_RESPOND = "timeToRespond";
    public static final String TIME_TO_DISPLAY_HP = "timeToDisplayHP";
    public static final String TIME_TO_CANCEL = "timeToCancel";
    public static final String IDLE_INTERVAL = "idleInterval";
    public static final String WINS_BEFORE_FEVER = "winsBeforeFever";
    public static final String LOSSES_BEFORE_BERSERK = "lossesBeforeBererk";
    public static final String FEVERS_GIVEN_FOR_CONSECUTIVE_WINS = "feversGivenForConsecuiveWins";
    public static final String BERSERKS_GIVEN_FOR_CONSECUTIVE_LOSSES = "berserksGivenForConsecLosses";
    public static final String MIN_PLAYERS = "minPlayers";
    public static final String MAX_PLAYERS = "maxPlayers";
    public static final String DEFAULT_POT_START = "defaultPotStart";
    public static final String MAX_ROUND = "maxRound";
    public static final String MAX_ATTACK_PER_ROUND = "maxAttackPerRound";
    public static final String TIME_TO_DELAY_ROUND_START = "timeToDelayRoundStart";
    public static final String TIME_TO_END_ROUND = "timeToEndRound";
    public static final String TIME_TO_END_GAME_NO_PLAYER = "timeToEndGameNoPlayer";
    private double minPotEntry = 0.05;
    private double defaultPotEntry = 0.05;
    private double defaultPotStart = 0.05;
    private static int maxHitPointsByMigLevel = 800;
    private static int maxHitPointsByFriends = 800;
    private static int maxHitPointsByAnte = 100;
    private static int maxHitPointsByMigLevelDiff = 500;
    private static int maxAnteForHitPoints = 100;
    private static int maxHitPointsByMigDNA = 400;
    private static int maxHitPointsByFever = 200;
    private long idleInterval = 1800000L;
    private int minPlayers = 2;
    private int maxPlayers = 5;
    private long timeToCancel = 20000L;
    private long timeToRespond = 60000L;
    private long timeToDisplayHP = 3000L;
    private int winsBeforeFever = 3;
    private int lossesBeforeBerserk = 3;
    private int feversGivenForConsecutiveWins = 3;
    private int berserksGivenForConsecLosses = 3;
    private int maxRound = 6;
    private int maxAttackPerRound = 1;
    private long timeToDelayRoundStart = 10000L;
    private long timeToEndRound = 20000L;
    private long timeToEndGameNoPlayer = 60000L;
    private BotData.BotStateEnum gameState = BotData.BotStateEnum.NO_GAME;
    private long timeLastGameFinished = System.currentTimeMillis();
    private long timeGameStarted = System.currentTimeMillis();
    private long timeGameJoining = 0L;
    private static final String COMMAND_START = "!start";
    private static final String COMMAND_CANCEL = "!n";
    private static final String COMMAND_JOIN = "!j";
    private static final String COMMAND_ATTACK = "!a";
    private static final String COMMAND_HP_CHECK = "!p";
    private static final String EMOTE_WINNER_CUP = "(warriors-trophy)";
    private static final String EMOTE_LOSER = "(warriors-death)";
    private static final String EMOTE_WEAK_HIT = "(warriors-atk-1)";
    private static final String EMOTE_AVERAGE_HIT = "(warriors-atk-2)";
    private static final String EMOTE_STRONG_HIT = "(warriors-atk-3)";
    private static final String EMOTE_BERSERK_HIT = "(warriors-atk-4)";
    public static final String EMOTE_PLAYERNUM_NORMAL = "(warriors-num-n-%d)";
    public static final String EMOTE_PLAYERNUM_BERSERK = "(warriors-num-a-%d)";
    public static final String EMOTE_PLAYERNUM_FEVER = "(warriors-num-d-%d)";
    private List<Attack> normalAttacks = new ArrayList<Attack>();
    private List<Attack> berserkAttacks = new ArrayList<Attack>();
    private Player challengerPlayer = null;
    private Map<String, Player> playerMap = null;
    private Map<String, Player> deadPlayerMap = null;
    private int currentRound = 0;
    private boolean isInRound = false;
    private Map<String, Integer> attacksInRound = new HashMap<String, Integer>();
    private ScheduledFuture<?> cancellationTimer;
    private ScheduledFuture<?> challengeResponseTimer;
    private ScheduledFuture<?> hpDisplayTimer;
    private ScheduledFuture<?> roundStartDelayTimer;
    private ScheduledFuture<?> roundEndTimer;
    private ScheduledFuture<?> noPlayerTimer;

    public WarriorsBot(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDao) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDao);
        this.loadGameConfig();
        log.info((Object)(botData.getDisplayName() + " [" + this.instanceID + "] added to channel [" + this.channel + "]"));
        this.playerMap = Collections.synchronizedMap(new LinkedHashMap());
        this.normalAttacks.add(new Attack("Weak Hit", "Weak hit!", 12, 36, 0.1, EMOTE_WEAK_HIT));
        this.normalAttacks.add(new Attack("Average Hit", "", 48, 108, 0.73, EMOTE_AVERAGE_HIT));
        this.normalAttacks.add(new Attack("Strong Hit", "STRONG HIT!", 120, 180, 0.17, EMOTE_STRONG_HIT));
        this.berserkAttacks.add(new Attack("Weak Hit", "Weak hit!", 12, 36, 0.05, EMOTE_WEAK_HIT));
        this.berserkAttacks.add(new Attack("Average Hit", "", 48, 108, 0.45, EMOTE_AVERAGE_HIT));
        this.berserkAttacks.add(new Attack("Strong Hit", "STRONG HIT!", 120, 180, 0.25, EMOTE_STRONG_HIT));
        this.berserkAttacks.add(new Attack("Berserk Hit", "BERSERK HIT!", 168, 204, 0.25, EMOTE_BERSERK_HIT));
        this.deadPlayerMap = new HashMap<String, Player>();
        this.sendChannelMessage(this.createMessage("BOT_ADDED", null));
        this.sendChannelMessage(this.createMessage("PLAY_NOW", null));
    }

    private void loadGameConfig() {
        this.minPotEntry = this.getDoubleParameter(MIN_POT_ENTRY, this.minPotEntry);
        this.defaultPotEntry = this.getDoubleParameter(DEFAULT_POT_ENTRY, this.defaultPotEntry);
        this.defaultPotStart = this.getDoubleParameter(DEFAULT_POT_START, this.defaultPotStart);
        maxHitPointsByMigLevel = this.getIntParameter(MAX_HIT_POINTS_BY_MIGLEVEL, maxHitPointsByMigLevel);
        maxHitPointsByFriends = this.getIntParameter(MAX_HIT_POINTS_BY_FRIENDS, maxHitPointsByFriends);
        maxHitPointsByAnte = this.getIntParameter(MAX_HIT_POINTS_BY_ANTE, maxHitPointsByAnte);
        maxHitPointsByMigLevelDiff = this.getIntParameter(MAX_HIT_POINTS_BY_MIGLEVEL_DIFFERENCE, maxHitPointsByMigLevelDiff);
        maxAnteForHitPoints = this.getIntParameter(MAX_ANTE_FOR_HIT_POINTS, maxAnteForHitPoints);
        maxHitPointsByMigDNA = this.getIntParameter(MAX_HIT_POINTS_BY_MIGDNA, maxHitPointsByMigDNA);
        maxHitPointsByFever = this.getIntParameter(MAX_HIT_POINTS_BY_FEVER, maxHitPointsByFever);
        this.timeToCancel = this.getLongParameter(TIME_TO_CANCEL, this.timeToCancel);
        this.timeToRespond = this.getLongParameter(TIME_TO_RESPOND, this.timeToRespond);
        this.idleInterval = this.getLongParameter(IDLE_INTERVAL, this.idleInterval);
        this.timeToDisplayHP = this.getLongParameter(TIME_TO_DISPLAY_HP, this.timeToDisplayHP);
        this.winsBeforeFever = this.getIntParameter(WINS_BEFORE_FEVER, this.winsBeforeFever);
        this.lossesBeforeBerserk = this.getIntParameter(LOSSES_BEFORE_BERSERK, this.lossesBeforeBerserk);
        this.feversGivenForConsecutiveWins = this.getIntParameter(FEVERS_GIVEN_FOR_CONSECUTIVE_WINS, this.feversGivenForConsecutiveWins);
        this.berserksGivenForConsecLosses = this.getIntParameter(BERSERKS_GIVEN_FOR_CONSECUTIVE_LOSSES, this.berserksGivenForConsecLosses);
        this.minPlayers = this.getIntParameter(MIN_PLAYERS, this.minPlayers);
        this.maxPlayers = this.getIntParameter(MAX_PLAYERS, this.maxPlayers);
        this.maxRound = this.getIntParameter(MAX_ROUND, this.maxRound);
        this.maxAttackPerRound = this.getIntParameter(MAX_ATTACK_PER_ROUND, this.maxAttackPerRound);
        this.timeToDelayRoundStart = this.getLongParameter(TIME_TO_DELAY_ROUND_START, this.timeToDelayRoundStart);
        this.timeToEndRound = this.getLongParameter(TIME_TO_END_ROUND, this.timeToEndRound);
        this.timeToEndGameNoPlayer = this.getLongParameter(TIME_TO_END_GAME_NO_PLAYER, this.timeToEndGameNoPlayer);
    }

    private String createMessage(String messageKey, String player) {
        return this.createMessage(messageKey, player, new String[0][]);
    }

    private String createMessage(String messageKey, String player, String[][] variables) {
        try {
            String messageToSend = (String)this.messages.get(messageKey);
            if (messageToSend == null) {
                log.warn((Object)this.getLogMessage(String.format("Unable to find message for key '%s', using the key as message", messageKey)));
                messageToSend = messageKey;
            }
            messageToSend = messageToSend.replace("BOT_NAME", this.botData.getDisplayName());
            messageToSend = messageToSend.replace("GAME", this.botData.getGame());
            messageToSend = messageToSend.replace("DEFAULT_POT_START", this.df.format(this.defaultPotStart));
            messageToSend = messageToSend.replace("DEFAULT_POT_ENTRY", this.df.format(this.defaultPotEntry));
            messageToSend = messageToSend.replace("CURRENCY", "USD");
            if (player != null) {
                messageToSend = messageToSend.replace("PLAYER", player);
            }
            messageToSend = messageToSend.replace("COMMAND_START", COMMAND_START);
            messageToSend = messageToSend.replace("COMMAND_CANCEL", COMMAND_CANCEL);
            messageToSend = messageToSend.replace("COMMAND_JOIN", COMMAND_JOIN);
            messageToSend = messageToSend.replace("COMMAND_ATTACK", COMMAND_ATTACK);
            messageToSend = messageToSend.replace("COMMAND_HP_CHECK", COMMAND_HP_CHECK);
            messageToSend = messageToSend.replace("WINNER_CUP_EMOTE", EMOTE_WINNER_CUP);
            messageToSend = messageToSend.replace("LOSER_EMOTE", EMOTE_LOSER);
            messageToSend = messageToSend.replace("ROUND_NUM", Integer.toString(this.currentRound));
            if (variables != null) {
                for (Object[] objectArray : variables) {
                    if (objectArray != null && objectArray.length == 2) {
                        if (!messageToSend.contains(objectArray[0])) continue;
                        messageToSend = messageToSend.replace((CharSequence)objectArray[0], (CharSequence)objectArray[1]);
                        continue;
                    }
                    log.warn((Object)String.format("Incorrect variable passed to createMessage: %s", StringUtil.join(objectArray, ",")));
                }
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
        return this.gameState == BotData.BotStateEnum.NO_GAME;
    }

    public synchronized void stopBot() {
        this.endGame();
    }

    public void onUserJoinChannel(String username) {
        switch (this.gameState) {
            case NO_GAME: {
                this.sendMessage(this.createMessage("PLAY_NOW", null), username);
                break;
            }
            case GAME_STARTING: {
                this.sendMessage(this.createMessage("GAME_STARTING_USER_JOINED", null), username);
                break;
            }
            case GAME_JOINING: 
            case GAME_STARTED: 
            case PLAYING: {
                this.userJoinChannelWhilePlaying(username);
                break;
            }
            default: {
                this.sendMessage(this.createMessage("BYSTANDER_PLEASE_WAIT", null), username);
            }
        }
    }

    private synchronized void userJoinChannelWhilePlaying(String username) {
        if (this.playerMap.containsKey(username)) {
            if (this.noPlayerTimer != null) {
                this.noPlayerTimer.cancel(true);
                this.noPlayerTimer = null;
            }
            Player p = this.playerMap.get(username);
            p.setInChannel(true);
            String message = this.createMessage("PLAYER_REJOIN", username, new String[][]{{"SEQUENCE_EMOTE", p.getPlayerEmote()}});
            this.sendChannelMessage(message);
            if (this.gameState == BotData.BotStateEnum.GAME_STARTED) {
                this.sendMessage(this.createMessage("GAME_STARTED_PLAYER_REJOIN", username), username);
            } else if (this.gameState == BotData.BotStateEnum.GAME_JOINING) {
                this.sendMessage(this.createMessage("GAME_JOINING_PLAYER_REJOIN", username), username);
            } else if (this.isInRound) {
                this.sendMessage(this.createMessage("ROUND_STARTED_PLAYER_REJOIN", username), username);
            } else {
                this.sendMessage(this.createMessage("ROUND_STARTING_PLAYER_REJOIN", username), username);
            }
        } else if (this.gameState == BotData.BotStateEnum.GAME_JOINING) {
            long timeRemaining = this.timeToRespond - (System.currentTimeMillis() - this.timeGameJoining);
            if (timeRemaining < 1000L) {
                timeRemaining = 1000L;
            }
            this.sendMessage(this.createMessage("JOIN_NOW", this.challengerPlayer.username, new String[][]{{"SEQUENCE_EMOTE", this.challengerPlayer.getPlayerEmote()}, {"TIME", Long.toString(timeRemaining / 1000L)}, {"AMOUNT", this.df.format(this.challengerPlayer.potEntry)}}), username);
            block4: for (int i = 2; i <= this.playerMap.size(); ++i) {
                for (Player p : this.playerMap.values()) {
                    if (p.sequence != i) continue;
                    this.sendMessage(this.createMessage("PLAYER_JOINED", p.username, new String[][]{{"SEQUENCE_EMOTE", p.getPlayerEmote()}, {"AMOUNT", this.df.format(p.potEntry)}, {"NUM", Integer.toString(this.playerMap.size())}}), username);
                    continue block4;
                }
            }
            switch (Player.getMode(username)) {
                case FEVER: {
                    this.sendMessage(this.createMessage("JOIN_REMINDER_FEVER", null), username);
                    break;
                }
                case BERSERK: {
                    this.sendMessage(this.createMessage("JOIN_REMINDER_BERSERK", null), username);
                }
            }
        } else {
            this.sendMessage(this.createMessage("BYSTANDER_PLEASE_WAIT", null), username);
        }
    }

    public void onUserLeaveChannel(String username) {
        switch (this.gameState) {
            case NO_GAME: {
                break;
            }
            case GAME_STARTING: {
                if (!username.equals(this.challengerPlayer.username)) break;
                this.endGame();
                break;
            }
            case GAME_JOINING: 
            case GAME_STARTED: 
            case PLAYING: {
                this.userLeaveChannelWhilePlaying(username);
                break;
            }
            default: {
                log.warn((Object)this.getLogMessage("Unknown game state while processing onUserLeaveChannel() state[" + (Object)((Object)this.gameState) + "] user[" + username + "]"));
            }
        }
    }

    private boolean isNoPlayerInChannel() {
        int players_in_channel = 0;
        for (Map.Entry<String, Player> entry : this.playerMap.entrySet()) {
            if (!entry.getValue().isInChannel()) continue;
            ++players_in_channel;
            break;
        }
        return players_in_channel == 0;
    }

    private synchronized void userLeaveChannelWhilePlaying(String username) {
        if (this.playerMap.containsKey(username)) {
            Player p = this.playerMap.get(username);
            p.setInChannel(false);
            String message = this.createMessage("PLAYER_LEFT", username, new String[][]{{"SEQUENCE_EMOTE", p.getPlayerEmote()}});
            this.sendChannelMessage(message);
            if (this.isNoPlayerInChannel()) {
                this.noPlayerTimer = this.executor.schedule(new Runnable(){

                    public void run() {
                        WarriorsBot.this.endGameNoPlayer();
                    }
                }, this.timeToEndGameNoPlayer, TimeUnit.MILLISECONDS);
            }
        }
    }

    private synchronized void endGameNoPlayer() {
        this.noPlayerTimer = null;
        if (this.isNoPlayerInChannel()) {
            this.sendChannelMessage(this.createMessage("END_GAME_NO_PLAYER", null));
            for (Player p : this.playerMap.values()) {
                try {
                    this.pot.removePlayer(p.username);
                }
                catch (Exception e) {
                    log.error((Object)this.getLogMessage(String.format("Unexpected exception occured in removing player [%s] from the pot after all players left the game", p.username)), (Throwable)e);
                }
            }
            try {
                this.pot.payout(true);
            }
            catch (Exception e) {
                log.error((Object)this.getLogMessage(String.format("Unable to pay out pot [%d] to clear the pot after all players left the game", this.pot.getPotID())), (Throwable)e);
                this.sendChannelMessageAndPopUp(SystemProperty.get(SystemPropertyEntities.BOT.PAYOUT_FAILURE_MESSAGE));
            }
            this.pot = null;
            this.endGame();
            this.sendChannelMessage(this.createMessage("PLAY_NOW", null));
        }
    }

    public synchronized void onMessage(String username, String messageText, long receivedTimestamp) {
        try {
            messageText = messageText.toLowerCase().trim();
            if (messageText.startsWith(COMMAND_START) && this.gameState == BotData.BotStateEnum.NO_GAME) {
                this.startNewGame(username, messageText);
            } else if (messageText.startsWith(COMMAND_CANCEL) && this.gameState == BotData.BotStateEnum.GAME_STARTING && username.equals(this.challengerPlayer.username)) {
                this.cancelGame(username);
            } else if ((messageText.equals(COMMAND_JOIN) || messageText.startsWith("!j ")) && this.gameState == BotData.BotStateEnum.GAME_JOINING && !this.playerMap.containsKey(username)) {
                this.playerJoinsGame(username, messageText);
            } else if (messageText.startsWith(COMMAND_ATTACK) && this.gameState == BotData.BotStateEnum.PLAYING && this.playerMap.containsKey(username)) {
                this.playerAttacks(username, messageText, receivedTimestamp);
            } else if (messageText.startsWith(COMMAND_HP_CHECK) && this.gameState == BotData.BotStateEnum.PLAYING) {
                this.displayHPAllPlayers(username);
            } else if (this.gameState == BotData.BotStateEnum.GAME_STARTING && !username.equals(this.challengerPlayer.username)) {
                this.sendMessage(this.createMessage("GAME_STARTING_NON_PLAYER", null), username);
            } else if (this.gameState == BotData.BotStateEnum.NO_GAME) {
                this.sendMessage(this.createMessage("PLAY_NOW", null), username);
            } else if (this.hasGameStarted() && this.deadPlayerMap.containsKey(username)) {
                this.sendMessage(this.createMessage("GAME_PLAYING_DEAD_PLAYER", null), username);
            } else if (this.hasGameStarted() && !this.playerMap.containsKey(username)) {
                this.sendMessage(this.createMessage("GAME_STARTED_NON_PLAYER", null), username);
            } else if (this.gameState == BotData.BotStateEnum.GAME_STARTED && this.playerMap.containsKey(username)) {
                this.sendMessage(this.createMessage("GAME_STARTED_PLAYER", null), username);
            } else if (this.gameState == BotData.BotStateEnum.PLAYING && this.playerMap.containsKey(username)) {
                if (this.isInRound) {
                    this.sendMessage(this.createMessage("ROUND_STARTED_PLAYER", null), username);
                } else {
                    this.sendMessage(this.createMessage("ROUND_STARTING_PLAYER", null), username);
                }
            } else if (this.gameState == BotData.BotStateEnum.GAME_JOINING && this.playerMap.containsKey(username)) {
                this.sendMessage(this.createMessage("GAME_JOINING_PLAYER", null), username);
            } else if (this.gameState == BotData.BotStateEnum.GAME_JOINING && !this.playerMap.containsKey(username)) {
                this.sendMessage(this.createMessage("GAME_JOINING_NON_PLAYER", null), username);
            } else {
                this.sendMessage(messageText + " is not a valid command.", username);
            }
        }
        catch (Exception e) {
            this.sendMessage("Error while processing command. Please try again.", username);
            log.error((Object)("Unknown Exception: " + e.getMessage()), (Throwable)e);
            return;
        }
    }

    private synchronized boolean hasGameStarted() {
        return this.gameState == BotData.BotStateEnum.GAME_STARTED || this.gameState == BotData.BotStateEnum.PLAYING;
    }

    private void enterPlayerIntoGame(Player player) throws Exception {
        this.pot.enterPlayer(player.username, player.potEntry, "USD");
        User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        player.userData = userEJB.loadUser(player.username, true, false);
        player.migLevel = MemCacheOrEJB.getUserReputationLevel(player.username);
        this.playerMap.put(player.username, player);
        player.sequence = this.playerMap.size();
        try {
            player.enterGameUpdateMode();
        }
        catch (Exception e) {
            log.error((Object)this.getLogMessage(String.format("failed to update mode for player [%s], mode set to %s", player.username, player.mode.name())));
        }
        log.debug((Object)this.getLogMessage(String.format("enter player into game [%s] [seq:%d] [migLevel:%d] [mode:%s]", player.username, player.sequence, player.migLevel, player.mode.name())));
    }

    private CustomAmountData getCustomAmountFromCommand(String messageText, String username, String commandName) {
        String[] tokens = messageText.split("\\s+", 2);
        String errMsg = null;
        if (tokens.length == 2 && !StringUtil.isBlank(tokens[1])) {
            try {
                double amount = Double.parseDouble(tokens[1].trim()) / 100.0;
                if (!(amount < this.minPotEntry)) {
                    return new CustomAmountData(null, amount);
                }
                errMsg = this.createMessage("INVALID_AMOUNT_LESS", username, new String[][]{{"AMOUNT", this.df.format(this.minPotEntry)}});
            }
            catch (Exception e) {
                errMsg = this.createMessage("INVALID_AMOUNT", username, new String[][]{{"AMOUNT", tokens[1]}, {"COMMAND", commandName}});
            }
        } else {
            return new CustomAmountData(null, null);
        }
        return new CustomAmountData(errMsg, null);
    }

    private synchronized void startNewGame(String username, String messageText) {
        if (this.gameState != BotData.BotStateEnum.NO_GAME) {
            this.sendMessage(this.createMessage("NEW_GAME_GOING_ON", username), username);
            return;
        }
        this.gameState = BotData.BotStateEnum.GAME_STARTING;
        try {
            if (this.playerMap.size() != 0) {
                this.playerMap.clear();
            }
            this.deadPlayerMap.clear();
            Player player1 = new Player(username, this.normalAttacks, this.berserkAttacks);
            player1.isChallenger = true;
            this.challengerPlayer = player1;
            boolean startShortcut = false;
            if (messageText.endsWith(" !y")) {
                messageText = messageText.substring(0, messageText.length() - 3);
                startShortcut = true;
            }
            CustomAmountData cad = this.getCustomAmountFromCommand(messageText, username, COMMAND_START);
            if (cad.errMsg != null) {
                this.sendMessage(cad.errMsg, username);
                this.endGame();
                return;
            }
            player1.potEntry = cad.getAmount(this.defaultPotEntry);
            if (!this.userCanAffordToEnterPot(username, player1.potEntry, true)) {
                this.endGame();
                return;
            }
            try {
                this.pot = new Pot(this);
            }
            catch (Exception e) {
                this.sendMessage("Unable to start the game. Please try again later.", username);
                log.error((Object)this.getLogMessage(String.format("Unable to create pot for user [%s], amount=%f, error=%s", username, player1.potEntry, e.getMessage())), (Throwable)e);
                this.endGame();
                return;
            }
            this.enterPlayerIntoGame(player1);
            if (startShortcut) {
                String message = this.createMessage("GAME_STARTING_IMMEDIATE", username, new String[][]{{"TIME", Long.toString(this.timeToCancel / 1000L)}, {"AMOUNT", this.df.format(player1.potEntry)}});
                this.sendMessage(message, username);
                this.startJoiningPhase();
            } else {
                String message = this.createMessage("GAME_STARTING_STARTER", username, new String[][]{{"TIME", Long.toString(this.timeToCancel / 1000L)}, {"AMOUNT", this.df.format(player1.potEntry)}});
                this.sendMessage(message, username);
                this.cancellationTimer = this.executor.schedule(new Runnable(){

                    public void run() {
                        WarriorsBot.this.startJoiningPhase();
                    }
                }, this.timeToCancel - 100L, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception e) {
            this.sendMessage("Unable to start the game. Please try again later.", username);
            log.error((Object)("Unable to start game for [" + username + "] :" + e.getMessage()), (Throwable)e);
            this.endGame();
            return;
        }
    }

    private synchronized void cancelGame(String username) {
        if (this.gameState != BotData.BotStateEnum.GAME_STARTING) {
            return;
        }
        if (this.cancellationTimer != null) {
            this.cancellationTimer.cancel(true);
            this.cancellationTimer = null;
        }
        try {
            if (username != null) {
                this.sendMessage(this.createMessage("CANCEL_GAME", username), username);
            }
            this.endGame();
        }
        catch (Exception e) {
            log.error((Object)("Unexpected exception: " + e.getMessage()), (Throwable)e);
        }
    }

    private synchronized void startJoiningPhase() {
        String[] allParticipants;
        if (this.gameState != BotData.BotStateEnum.GAME_STARTING) {
            return;
        }
        this.gameState = BotData.BotStateEnum.GAME_JOINING;
        this.timeGameJoining = System.currentTimeMillis();
        this.cancellationTimer = null;
        this.sendChannelMessage(this.createMessage("JOIN_NOW", this.challengerPlayer.username, new String[][]{{"SEQUENCE_EMOTE", this.challengerPlayer.getPlayerEmote()}, {"TIME", Long.toString(this.timeToRespond / 1000L)}, {"AMOUNT", this.df.format(this.challengerPlayer.potEntry)}}));
        String message2 = this.createMessage("WAITING_FOR_PLAYERS", null);
        this.sendChannelMessage(message2);
        block4: for (String participant : allParticipants = this.getChannelProxy().getParticipants(null)) {
            if (this.playerMap.containsKey(participant)) continue;
            switch (Player.getMode(participant)) {
                case FEVER: {
                    this.sendMessage(this.createMessage("JOIN_REMINDER_FEVER", null), participant);
                    continue block4;
                }
                case BERSERK: {
                    this.sendMessage(this.createMessage("JOIN_REMINDER_BERSERK", null), participant);
                }
            }
        }
        this.challengeResponseTimer = this.executor.schedule(new Runnable(){

            public void run() {
                WarriorsBot.this.challengeTimesOut();
            }
        }, this.timeToRespond - (System.currentTimeMillis() - this.timeGameJoining), TimeUnit.MILLISECONDS);
    }

    private synchronized void challengeTimesOut() {
        if (this.gameState != BotData.BotStateEnum.GAME_JOINING) {
            return;
        }
        if (this.challengeResponseTimer != null) {
            this.challengeResponseTimer = null;
        }
        if (this.playerMap.size() >= this.minPlayers) {
            this.prepareForGames();
        } else {
            this.sendChannelMessage(this.createMessage("JOINING_END", null, new String[][]{{"NUM", Integer.toString(this.minPlayers)}}));
            this.endGame();
        }
    }

    private synchronized void prepareForGames() {
        if (this.gameState != BotData.BotStateEnum.GAME_JOINING) {
            return;
        }
        try {
            this.timeGameStarted = System.currentTimeMillis();
            this.gameState = BotData.BotStateEnum.GAME_STARTED;
            this.sendChannelMessage(this.createMessage("PREPARING_TO_FIGHT_1", null));
            this.sendChannelMessage(this.createMessage("PREPARING_TO_FIGHT_2", null));
            this.sendChannelMessage(this.createMessage("PREPARING_TO_FIGHT_3", null));
            this.sendChannelMessage(this.createMessage("PREPARING_TO_FIGHT_4", null));
            int opponentMigLevel = this.challengerPlayer.migLevel;
            for (Player player : this.playerMap.values()) {
                if (player.migLevel <= opponentMigLevel) continue;
                opponentMigLevel = player.migLevel;
            }
            for (Map.Entry entry : this.playerMap.entrySet()) {
                Player p = (Player)entry.getValue();
                p.maxHP = p.HP = this.calculateHitPoints(p, opponentMigLevel);
                log.debug((Object)this.getLogMessage(String.format("start game [%s] [seq:%d] [HP:%d] [migLevel:%d] [mode:%s]", p.username, p.sequence, p.HP, p.migLevel, p.mode.name())));
            }
            this.hpDisplayTimer = this.executor.schedule(new Runnable(){

                public void run() {
                    WarriorsBot.this.displayHPAndStartGame();
                }
            }, Math.max(0L, this.timeToDisplayHP - (System.currentTimeMillis() - this.timeGameStarted)), TimeUnit.MILLISECONDS);
            ArrayList<String> playerUsernames = new ArrayList<String>(this.playerMap.size());
            ArrayList<Integer> arrayList = new ArrayList<Integer>(this.playerMap.size());
            for (Player p : this.playerMap.values()) {
                playerUsernames.add(p.username);
                arrayList.add(p.userData.userID);
            }
            this.incrementGamesPlayed(Leaderboard.Type.WARRIORS_GAMES_PLAYED, playerUsernames, arrayList);
            this.logGamesPlayed(this.playerMap.size(), this.playerMap.keySet(), this.pot.getTotalAmountInBaseCurrency());
        }
        catch (Exception e) {
            this.sendChannelMessage("Unxpected error while starting game. Please try again.");
            log.error((Object)("Unxpected exeception: " + e.getMessage()), (Throwable)e);
            this.endGame();
        }
    }

    private synchronized void displayHPAllPlayers(String targetUsername) {
        int numPlayers = this.playerMap.size() + this.deadPlayerMap.size();
        for (int i = 1; i <= numPlayers; ++i) {
            Player player = null;
            boolean dead = false;
            for (Player p : this.playerMap.values()) {
                if (p.sequence != i) continue;
                player = p;
                break;
            }
            if (player == null) {
                dead = true;
                for (Player p : this.deadPlayerMap.values()) {
                    if (p.sequence != i) continue;
                    player = p;
                    break;
                }
            }
            if (null == player) continue;
            String message = null;
            message = dead ? this.createMessage("PLAYER_HITPOINT_DEAD", player.username, new String[][]{{"SEQUENCE_EMOTE", player.getPlayerEmote()}, {"MODE_MSG", player.getModeDisplay()}}) : this.createMessage("PLAYER_HITPOINT", player.username, new String[][]{{"SEQUENCE_EMOTE", player.getPlayerEmote()}, {"HITPOINT", Integer.toString(player.HP)}, {"MODE_MSG", player.getModeDisplay()}});
            if (targetUsername == null) {
                this.sendChannelMessage(message);
                continue;
            }
            this.sendMessage(message, targetUsername);
        }
    }

    private synchronized void displayHPAndStartGame() {
        this.displayHPAllPlayers(null);
        this.sendChannelMessage(this.createMessage("BATTLE_BEGIN", null));
        this.sendChannelMessage(this.createMessage("BATTLE_BEGIN_HELP", null));
        this.gameState = BotData.BotStateEnum.PLAYING;
        this.startNewRound();
    }

    private synchronized void startNewRound() {
        if (this.gameState != BotData.BotStateEnum.PLAYING) {
            return;
        }
        ++this.currentRound;
        this.isInRound = false;
        this.sendChannelMessage(this.createMessage("ROUND_STARTING", null, new String[][]{{"TIME", Long.toString(this.timeToDelayRoundStart / 1000L)}}));
        this.roundStartDelayTimer = this.executor.schedule(new Runnable(){

            public void run() {
                WarriorsBot.this.roundStarted();
            }
        }, this.timeToDelayRoundStart, TimeUnit.MILLISECONDS);
    }

    private synchronized void roundStarted() {
        if (this.gameState != BotData.BotStateEnum.PLAYING) {
            return;
        }
        this.roundStartDelayTimer = null;
        this.sendChannelMessage(this.createMessage("ROUND_STARTED", null, new String[][]{{"TIME", Long.toString(this.timeToEndRound / 1000L)}}));
        this.isInRound = true;
        this.attacksInRound.clear();
        this.roundEndTimer = this.executor.schedule(new Runnable(){

            public void run() {
                WarriorsBot.this.roundEnded();
            }
        }, this.timeToEndRound, TimeUnit.MILLISECONDS);
    }

    private synchronized void roundEnded() {
        if (this.gameState != BotData.BotStateEnum.PLAYING) {
            return;
        }
        if (!this.isInRound) {
            return;
        }
        this.roundEndTimer = null;
        this.isInRound = false;
        ArrayList<String> nonAttackers = new ArrayList<String>(this.playerMap.size());
        for (Player p : this.playerMap.values()) {
            if (this.attacksInRound.containsKey(p.username)) continue;
            nonAttackers.add(String.format("%s %s", p.getPlayerEmote(), p.username));
        }
        if (nonAttackers.size() > 0) {
            this.sendChannelMessage(this.createMessage("ROUND_IDLE", null, new String[][]{{"PLAYES", StringUtil.join(nonAttackers, ", ")}}));
        }
        this.sendChannelMessage(this.createMessage("ROUND_ENDED", null));
        this.displayHPAllPlayers(null);
        if (this.currentRound >= this.maxRound) {
            this.gameTimeOut();
        } else {
            this.startNewRound();
        }
    }

    private synchronized void gameTimeOut() {
        if (this.gameState != BotData.BotStateEnum.PLAYING) {
            return;
        }
        ArrayList<Player> survivors = new ArrayList<Player>();
        for (Map.Entry<String, Player> entry : this.playerMap.entrySet()) {
            Player p = entry.getValue();
            if (p.HP <= 0) continue;
            survivors.add(p);
        }
        Collections.sort(survivors);
        Player winner = (Player)survivors.get(survivors.size() - 1);
        for (int i = 0; i < survivors.size() - 1; ++i) {
            this.removePlayerFromPot((Player)survivors.get(i), winner, false);
        }
        this.makeWinner(winner.username);
    }

    private synchronized void playerJoinsGame(String playerName, String messageText) {
        if (this.gameState != BotData.BotStateEnum.GAME_JOINING) {
            return;
        }
        if (this.challengerPlayer == null) {
            this.sendMessage("PLAYER: Unable to accept the challenge, please try again", playerName);
            log.error((Object)this.getLogMessage("Unable to join challenge [" + playerName + "]"));
            return;
        }
        Player player2 = new Player(playerName, this.normalAttacks, this.berserkAttacks);
        CustomAmountData cad = this.getCustomAmountFromCommand(messageText, playerName, COMMAND_JOIN);
        if (cad.errMsg != null) {
            this.sendMessage(cad.errMsg, playerName);
            return;
        }
        player2.potEntry = cad.getAmount(this.defaultPotEntry);
        if (!this.userCanAffordToEnterPot(player2.username, player2.potEntry, true)) {
            return;
        }
        try {
            this.enterPlayerIntoGame(player2);
        }
        catch (Exception e) {
            this.sendMessage("PLAYER: Unable to accept the challenge, please try again", player2.username);
            log.error((Object)this.getLogMessage("Unable to accept challenge:" + e.getMessage()), (Throwable)e);
            return;
        }
        this.sendChannelMessage(this.createMessage("PLAYER_JOINED", player2.username, new String[][]{{"SEQUENCE_EMOTE", player2.getPlayerEmote()}, {"AMOUNT", this.df.format(player2.potEntry)}, {"NUM", Integer.toString(this.playerMap.size())}}));
        if (this.playerMap.size() >= this.maxPlayers) {
            if (this.challengeResponseTimer != null) {
                this.challengeResponseTimer.cancel(true);
                this.challengeResponseTimer = null;
            }
            this.sendChannelMessage(this.createMessage("MAX_PLAYER_JOINED", null, new String[][]{{"NUM", Integer.toString(this.playerMap.size())}}));
            this.prepareForGames();
        }
    }

    private synchronized void playerAttacks(String username, String messageText, long receivedTimestamp) {
        int curNumAttack;
        if (this.gameState != BotData.BotStateEnum.PLAYING) {
            return;
        }
        if (!this.isInRound) {
            this.sendMessage(this.createMessage("ROUND_STARTING_PLAYER", null), username);
            return;
        }
        Player attacker = this.playerMap.get(username);
        int n = curNumAttack = this.attacksInRound.containsKey(username) ? this.attacksInRound.get(username) : 0;
        if (curNumAttack >= this.maxAttackPerRound) {
            this.sendMessage(this.createMessage("ROUND_ATTACK_EXCEEDED", username, new String[][]{{"MAX_ATTACK_PER_ROUND", Integer.toString(this.maxAttackPerRound)}}), username);
            return;
        }
        Player defender = null;
        int target = 0;
        if ((messageText = messageText.trim()).equals(COMMAND_ATTACK)) {
            target = RANDOM_GENERATOR.nextInt(this.playerMap.size() - 1);
            int curIndex = 0;
            for (Player player : this.playerMap.values()) {
                if (player.sequence == attacker.sequence) continue;
                if (target == curIndex) {
                    defender = player;
                    break;
                }
                ++curIndex;
            }
            log.debug((Object)("random attack target " + target + " of " + (this.playerMap.size() - 1) + " for [" + username + "]"));
        } else {
            String playerNumStr = messageText.substring(COMMAND_ATTACK.length()).trim();
            try {
                target = Integer.parseInt(playerNumStr);
            }
            catch (NumberFormatException nfe) {
                this.sendMessage("Invalid target [" + playerNumStr + "] specified for attack command.", username);
                return;
            }
            if (target == attacker.sequence) {
                this.sendMessage(this.createMessage("ATTACK_SELF", null, new String[][]{{"SEQUENCE_EMOTE", attacker.getPlayerEmote()}}), username);
                return;
            }
            log.debug((Object)("attack target [" + target + "] for [" + username + "]"));
            for (Map.Entry entry : this.playerMap.entrySet()) {
                Player p = (Player)entry.getValue();
                if (p.sequence != target) continue;
                defender = p;
                break;
            }
        }
        if (defender == null) {
            for (Player p : this.deadPlayerMap.values()) {
                if (p.sequence != target) continue;
                defender = p;
                break;
            }
            if (defender == null) {
                log.error((Object)this.getLogMessage(String.format("Can't find defender sequence [%d] for [%s] to attack", target, username)));
                this.sendMessage("Invalid target [" + target + "] specified for attack command.", username);
            } else {
                log.error((Object)this.getLogMessage(String.format("Can't find defender sequence [%d] for [%s] to attack", target, username)));
                this.sendMessage(this.createMessage("ATTACK_DEAD_PLAYER", defender.username, new String[][]{{"SEQUENCE_EMOTE", defender.getPlayerEmote()}}), username);
            }
            return;
        }
        log.debug((Object)("Found target [" + target + "] for [" + username + "]"));
        Attack attack = attacker.getAttack();
        int damage = attack.getRandomizedAttackDamage();
        defender.HP -= damage;
        if (defender.HP < 0) {
            defender.HP = 0;
        }
        String string = this.createMessage("ATTACK_MESSAGE", attacker.username, new String[][]{{"PLAYE2", defender.username}, {"ATTACK_EMOTE", attack.getAttackEmote()}, {"SEQUENCE_EMOTE", attacker.getPlayerEmote()}, {"SEQUENCE2_EMOTE", defender.getPlayerEmote()}, {"DAMAGE_POINT", Integer.toString(damage)}, {"HITPOINT", Integer.toString(defender.HP)}});
        this.sendChannelMessage(string);
        if (defender.HP == 0) {
            this.removePlayerFromPot(defender, attacker, true);
            this.incrementMostWins(Leaderboard.Type.WARRIORS_NUM_KILLS, username, attacker.userData.userID);
            if (this.playerMap.size() == 1) {
                this.makeWinner(attacker.username);
                return;
            }
        } else {
            String messageKey = defender.getMilestoneMessageKey();
            if (messageKey != null) {
                String string2 = this.createMessage(messageKey, defender.username, new String[][]{{"SEQUENCE_EMOTE", defender.getPlayerEmote()}});
                this.sendChannelMessage(string2);
            }
        }
        this.attacksInRound.put(username, curNumAttack + 1);
        if (this.attacksInRound.size() == this.playerMap.size()) {
            boolean allAttacked = true;
            for (Player p : this.playerMap.values()) {
                Integer numOfAttacks = this.attacksInRound.get(p.username);
                if (numOfAttacks == null || numOfAttacks >= this.maxAttackPerRound) continue;
                allAttacked = false;
                break;
            }
            if (allAttacked) {
                if (this.roundEndTimer != null) {
                    this.roundEndTimer.cancel(true);
                    this.roundEndTimer = null;
                }
                this.roundEnded();
            }
        }
    }

    private synchronized void makeWinner(String username) {
        if (this.gameState != BotData.BotStateEnum.PLAYING) {
            return;
        }
        String winner = username;
        Player winning_player = this.playerMap.get(winner);
        double payout = 0.0;
        try {
            double payoutInBaseCurrency = this.pot.payout(true);
            Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
            payout = accountEJB.convertCurrency(payoutInBaseCurrency, "AUD", "USD");
            this.logMostWins(username, payoutInBaseCurrency);
            this.incrementMostWins(Leaderboard.Type.WARRIORS_MOST_WINS, username, winning_player.userData.userID);
        }
        catch (Exception e) {
            log.error((Object)this.getLogMessage(String.format("Unable to pay out pot [%d] amount %s %f to [%s]", this.pot.getPotID(), "USD", payout, username)), (Throwable)e);
        }
        if (payout < 0.0) {
            this.sendChannelMessageAndPopUp(SystemProperty.get(SystemPropertyEntities.BOT.PAYOUT_FAILURE_MESSAGE));
        } else {
            try {
                String messageKey;
                long count = winning_player.endGameUpdateModeStat(true);
                if (count >= 3L && (messageKey = this.getMessageKeyForModeEndGame(winning_player, count, true)) != null) {
                    this.sendMessage(this.createMessage(messageKey, winning_player.username), winning_player.username);
                }
            }
            catch (Exception e) {
                log.error((Object)this.getLogMessage(String.format("failed to update mode stat after game ended for winner [%s]", winner)));
            }
            this.sendChannelMessage(this.createMessage("CHAMPION_MESSAGE", username, new String[][]{{"SEQUENCE_EMOTE", winning_player.getPlayerEmote()}}));
            this.sendChannelMessageAndPopUp(this.createMessage("WINNER_MESSAGE", username).replace("AMOUNT", this.df.format(payout)));
            this.sendChannelMessage(this.createMessage("PLAY_NOW", null));
        }
        this.pot = null;
        this.endGame();
    }

    private synchronized void endGame() {
        if (this.gameState == BotData.BotStateEnum.NO_GAME) {
            return;
        }
        log.info((Object)this.getLogMessage("Ending game, cur game state: " + this.gameState.name()));
        if (this.pot != null) {
            try {
                this.pot.cancel();
            }
            catch (Exception e) {
                log.error((Object)this.getLogMessage("Unable to endGame() with pot ID " + this.pot.getPotID() + ": " + e.getMessage()), (Throwable)e);
            }
            this.pot = null;
        }
        if (this.cancellationTimer != null) {
            this.cancellationTimer.cancel(true);
            this.cancellationTimer = null;
        }
        if (this.challengeResponseTimer != null) {
            this.challengeResponseTimer.cancel(true);
            this.challengeResponseTimer = null;
        }
        if (this.hpDisplayTimer != null) {
            this.hpDisplayTimer.cancel(true);
            this.hpDisplayTimer = null;
        }
        if (this.roundStartDelayTimer != null) {
            this.roundStartDelayTimer.cancel(true);
            this.roundStartDelayTimer = null;
        }
        if (this.roundEndTimer != null) {
            this.roundEndTimer.cancel(true);
            this.roundEndTimer = null;
        }
        if (this.noPlayerTimer != null) {
            this.noPlayerTimer.cancel(true);
            this.noPlayerTimer = null;
        }
        this.playerMap.clear();
        this.deadPlayerMap.clear();
        this.currentRound = 0;
        this.challengerPlayer = null;
        this.timeLastGameFinished = System.currentTimeMillis();
        this.gameState = BotData.BotStateEnum.NO_GAME;
    }

    private String getMessageKeyForModeEndGame(Player player, long count, boolean won) {
        if (count >= 3L) {
            if (count == 3L) {
                if (won) {
                    return "END_GAME_FEVER";
                }
                return "END_GAME_BERSERK";
            }
            String modeStr = "";
            if (player.isFever()) {
                modeStr = "FEVER";
            } else if (player.isBerserk()) {
                modeStr = "BERSERK";
            } else {
                return null;
            }
            return String.format("END_GAME_%s_%d_MORE", modeStr, 6L - count);
        }
        return null;
    }

    private void removePlayerFromPot(Player loser, Player attacker, boolean broadcaseLostMessage) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Player lost: " + loser.username + ". Removing from pot."));
        }
        if (this.pot != null) {
            try {
                this.pot.removePlayer(loser.username);
                try {
                    String messageKey;
                    long count = loser.endGameUpdateModeStat(false);
                    if (count >= 3L && (messageKey = this.getMessageKeyForModeEndGame(loser, count, false)) != null) {
                        this.sendMessage(this.createMessage(messageKey, loser.username), loser.username);
                    }
                }
                catch (Exception e) {
                    log.error((Object)this.getLogMessage(String.format("failed to update mode stat for loser [%s]", loser.username)), (Throwable)e);
                }
                this.playerMap.remove(loser.username);
                this.deadPlayerMap.put(loser.username, loser);
                if (broadcaseLostMessage) {
                    String message = this.createMessage("LOST_MESSAGE", attacker.username, new String[][]{{"PLAYE2", loser.username}, {"SEQUENCE_EMOTE", attacker.getPlayerEmote()}, {"SEQUENCE2_EMOTE", loser.getPlayerEmote()}, {"NUM", Integer.toString(this.playerMap.size())}});
                    this.sendChannelMessage(message);
                }
            }
            catch (Exception e) {
                log.error((Object)this.getLogMessage(String.format("Unexpected exception occured in removing bottom player [%s] from the pot", loser.username)), (Throwable)e);
            }
        }
    }

    private int calculateHitPoints(Player player, int opponentMigLevel) {
        String username = player.username;
        int migLevel = player.migLevel;
        int numFriends = player.userData.broadcastList.size();
        int migLevelDiff = Math.max(0, opponentMigLevel - player.migLevel);
        double ante = player.potEntry;
        boolean isFever = player.isFever();
        double migDNA = (double)username.length() / (double)SystemProperty.getInt("MaxUsernameLength", 128);
        int maxMigLevel = SystemProperty.getInt("MaxMigLevel", 100);
        int minHp = SystemProperty.getInt("MinHp", 400);
        log.debug((Object)("username [" + username + "] migLevel [" + migLevel + "] numFriends [" + numFriends + "] migLevelDifference [" + migLevelDiff + "] ante[" + ante + "] migDNA[" + migDNA + "]"));
        int hitPoints = (int)(Math.ceil(1.0 * (double)migLevel / (double)maxMigLevel * (double)maxHitPointsByMigLevel + (double)minHp + 1.0 * (double)numFriends / (double)SystemProperty.getInt("MaxFusionContacts", 2000) * (double)maxHitPointsByFriends) + (Math.pow(migLevelDiff, 2.0) / Math.pow(maxMigLevel, 2.0) * (double)maxHitPointsByMigLevelDiff + 100.0 * ante / (double)maxAnteForHitPoints * (double)maxHitPointsByAnte + migDNA * (double)maxHitPointsByMigDNA + (double)(isFever ? maxHitPointsByFever : 0)));
        return hitPoints;
    }

    private class CustomAmountData {
        String errMsg = null;
        Double amount;

        CustomAmountData(String errMsg, Double amount) {
            this.errMsg = errMsg;
            this.amount = amount;
        }

        double getAmount(double defaultAmount) {
            return this.amount == null ? defaultAmount : this.amount;
        }
    }
}

