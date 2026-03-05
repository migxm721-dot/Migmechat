/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.botservice.bot.migbot.dice;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.bot.Pot;
import com.projectgoth.fusion.botservice.bot.migbot.dice.DiceRoll;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.dao.BotDAO;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.leaderboard.Leaderboard;
import com.projectgoth.fusion.slice.BotChannelPrx;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Dice
extends Bot {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Dice.class));
    public static final String EMOTICON_HOTKEY_DICE_PREFIX = "d";
    public static final String TIMER_JOIN_GAME = "timerJoinGame";
    public static final String TIMER_CHARGE_CONF = "timerChargeConfirm";
    public static final String TIMER_ROLL = "timerRoll";
    public static final String TIMER_NEW_ROUND_INTERVAL = "timerNewRound";
    public static final String TIMER_IDLE = "timerIdle";
    public static final String AMOUNT_JOIN_POT = "amountJoinPot";
    public static final long TIMER_ROLL_VALUE = 10L;
    public static final long TIMER_NEW_ROUND_VALUE = 3L;
    public static final double AMOUNT_JOIN_POT_VALUE = 5.0;
    public static final long IDLE_TIME_VALUE = 3L;
    long timeToJoinGame = 90L;
    long timeToConfirmCharge = 20L;
    long timeToRoll = 10L;
    long timeToNewRound = 3L;
    double amountJoinPot = 5.0;
    double winnings = 0.0;
    public int minPlayers = 2;
    long timeAllowedToIdle = 30L;
    double amountOriginalJoinPot = 5.0;
    public static final String COMMAND_ROLL = "!r";
    Date lastActivityTime;
    private Map<String, DiceRoll> playerDiceRolls = new HashMap<String, DiceRoll>();
    private Map<Integer, Set<String>> safePlayers = new HashMap<Integer, Set<String>>();
    private BotData.BotStateEnum gameState = BotData.BotStateEnum.NO_GAME;
    DiceRoll botDice = new DiceRoll();
    boolean hasWinner = false;
    int currentRoundNumber = 0;
    int numPlayed = 0;
    boolean isRoundStarted = false;
    ScheduledFuture nextRollTimerTask = null;

    public Dice(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDao) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDao);
        this.loadGameConfig();
        log.info((Object)("DiceBot [" + this.instanceID + "] added to channel [" + this.channel + "]"));
        this.sendChannelMessage(this.createMessage("BOT_ADDED"));
        String message = this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT");
        this.sendChannelMessage(message);
        this.updateLastActivityTime();
    }

    private synchronized BotData.BotStateEnum getGameState() {
        return this.gameState;
    }

    private synchronized void setGameState(BotData.BotStateEnum gameState) {
        this.gameState = gameState;
    }

    @Override
    public void stopBot() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Stopping bot instanceID[" + this.instanceID + "]"));
        }
        if (this.nextRollTimerTask != null && !this.nextRollTimerTask.isDone() && !this.nextRollTimerTask.isCancelled()) {
            this.nextRollTimerTask.cancel(true);
        }
        if (this.pot != null) {
            log.debug((Object)("Expiring pot [" + this.pot.getPotID() + "] for bot instanceID[" + this.instanceID + "]"));
            try {
                this.pot.cancel();
            }
            catch (Exception e) {
                log.error((Object)("Error canceling pot [" + this.pot.getPotID() + "], botInstanceID[" + this.instanceID + "]"));
            }
        }
        this.setGameState(BotData.BotStateEnum.NO_GAME);
        log.debug((Object)("Stopped bot instanceID[" + this.instanceID + "]"));
    }

    @Override
    public boolean isIdle() {
        long timeSince = new Date().getTime() - this.lastActivityTime.getTime();
        if (timeSince < 0L) {
            log.warn((Object)"Error calculating time since. Target date is in the future.");
            return false;
        }
        long minutes = timeSince / 60000L % 60L;
        if (minutes > this.timeAllowedToIdle) {
            log.warn((Object)("Bot has been idle for " + minutes + (minutes == 1L ? " minute" : " minutes") + ". Marking as idle, and resetting game, if any..."));
            this.resetGame(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeStoppedNow() {
        return (this.gameState != BotData.BotStateEnum.PLAYING || this.pot == null) && this.gameState != BotData.BotStateEnum.GAME_JOINING && this.gameState != BotData.BotStateEnum.GAME_STARTING;
    }

    private synchronized void updateLastActivityTime() {
        this.lastActivityTime = new Date();
    }

    private void loadGameConfig() {
        this.timeToJoinGame = this.getLongParameter(TIMER_JOIN_GAME, this.timeToJoinGame);
        this.timeToConfirmCharge = this.getLongParameter(TIMER_CHARGE_CONF, this.timeToConfirmCharge);
        this.timeToRoll = this.getLongParameter(TIMER_ROLL, 10L);
        this.timeToNewRound = this.getLongParameter(TIMER_NEW_ROUND_INTERVAL, 3L);
        this.amountOriginalJoinPot = this.amountJoinPot = this.getDoubleParameter(AMOUNT_JOIN_POT, 5.0);
        this.timeAllowedToIdle = this.getLongParameter(TIMER_IDLE, 3L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void onMessage(String username, String messageText, long receivedTimestamp) {
        Dice dice = this;
        synchronized (dice) {
            if (messageText.equalsIgnoreCase("!n")) {
                this.processNoMessage(username);
            } else if (messageText.toLowerCase().startsWith("!start")) {
                this.start(username, messageText);
            } else if (messageText.equalsIgnoreCase("!j")) {
                this.join(username);
            } else if (messageText.equalsIgnoreCase(COMMAND_ROLL)) {
                if (this.gameState == BotData.BotStateEnum.PLAYING && this.isRoundStarted) {
                    if (!this.playerDiceRolls.containsKey(username)) {
                        this.sendMessage(this.createMessage("NOT_IN_GAME", username), username);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("botInstanceID[" + this.instanceID + "]: " + username + " rolls"));
                        }
                        this.roll(username, false);
                    }
                } else {
                    this.sendMessage(this.createMessage("INVALID_COMMAND", username), username);
                }
            }
        }
    }

    private void join(String username) {
        if (!this.playerDiceRolls.containsKey(username)) {
            if (this.gameState == BotData.BotStateEnum.GAME_JOINING) {
                this.addPlayer(username);
            } else if (this.gameState == BotData.BotStateEnum.PLAYING) {
                this.sendMessage(this.createMessage("JOIN_ENDED", username), username);
            } else {
                this.sendMessage(this.createMessage("INVALID_COMMAND", username), username);
            }
        } else {
            this.sendMessage(this.createMessage("ALREADY_IN_GAME", username), username);
        }
    }

    private void start(String username, String messageText) {
        if (this.getGameState() == BotData.BotStateEnum.NO_GAME) {
            if (messageText.trim().length() > "!start".length()) {
                String parameter = messageText.trim().substring("!start".length() + 1);
                if (StringUtils.hasLength((String)parameter) && this.checkJoinPotParameter(parameter, username)) {
                    try {
                        this.startGame(username);
                    }
                    catch (Exception e) {
                        log.error((Object)("Error starting game with custom amount. Command was : '" + messageText + "'"), (Throwable)e);
                    }
                }
            } else {
                try {
                    this.startGame(username);
                }
                catch (Exception e) {
                    log.error((Object)"Error starting game with default amount: ", (Throwable)e);
                }
            }
        } else {
            this.sendGameCannotStartMessage(username);
        }
    }

    private void sendGameCannotStartMessage(String username) {
        String message = null;
        switch (this.gameState.value()) {
            case 1: 
            case 2: 
            case 5: {
                message = this.createMessage("STATUS-PLAYING", username);
                break;
            }
            case 3: {
                message = this.createMessage("STATUS-JOINING", username);
                break;
            }
            default: {
                message = this.createMessage("STATUS-CANNOT-START", username);
            }
        }
        this.sendMessage(message, username);
    }

    public Map<String, DiceRoll> getPlayers() {
        return this.playerDiceRolls;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPlayer(String username) {
        if (this.getGameState() == BotData.BotStateEnum.GAME_STARTED || this.getGameState() == BotData.BotStateEnum.GAME_JOINING) {
            boolean hasFunds22;
            if (this.amountJoinPot > 0.0 && !(hasFunds22 = this.userCanAffordToEnterPot(username, this.amountJoinPot / 100.0, !this.gameStarter.equals(username)))) {
                return;
            }
            Map<String, DiceRoll> hasFunds22 = this.playerDiceRolls;
            synchronized (hasFunds22) {
                if (!this.playerDiceRolls.containsKey(username)) {
                    this.playerDiceRolls.put(username, new DiceRoll());
                }
            }
            StringBuilder message = new StringBuilder();
            message.append(this.createMessage("ADDED_TO_GAME", username));
            if (this.amountJoinPot > 0.0) {
                message.append(this.createMessage("CHARGES_APPLY_POT"));
            }
            log.info((Object)(username + " joined the game"));
            this.sendMessage(message.toString(), username);
            if (!username.equals(this.gameStarter)) {
                this.sendChannelMessage(this.createMessage("JOIN", username));
            }
        }
    }

    public void processNoMessage(String username) {
        String message = null;
        switch (this.getGameState().value()) {
            case 1: {
                if (username.equals(this.gameStarter) && this.amountJoinPot > 0.0) {
                    this.revertLimitInCache(this.gameStarter);
                    this.setGameState(BotData.BotStateEnum.NO_GAME);
                    this.amountJoinPot = 5.0;
                    this.gameStarter = null;
                    message = this.createMessage("NOT_CHARGED", username);
                    break;
                }
                message = this.createMessage("INVALID_COMMAND", username);
                break;
            }
            default: {
                message = this.createMessage("INVALID_COMMAND", username);
            }
        }
        this.sendMessage(message, username);
    }

    private boolean checkJoinPotParameter(String parameter, String username) {
        boolean isAmountValid = false;
        try {
            double amount = Double.parseDouble(parameter);
            if (amount >= this.amountJoinPot) {
                this.amountJoinPot = amount;
                isAmountValid = true;
            } else {
                String message = this.createMessage("INVALID_AMOUNT", username, null, parameter);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Lower value specified for amountJoinPot: " + parameter));
                }
                this.sendMessage(message, username);
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Parameter defined : amountJoinPot=" + this.amountJoinPot));
            }
        }
        catch (Exception e) {
            String message = this.createMessage("INVALID_AMOUNT", username, null, parameter);
            this.sendMessage(message, username);
        }
        return isAmountValid;
    }

    public void startGame(String username) throws Exception {
        this.updateLastActivityTime();
        if (this.gameState.equals((Object)BotData.BotStateEnum.NO_GAME)) {
            if (this.amountJoinPot > 0.0) {
                boolean hasFunds = this.userCanAffordToEnterPot(username, this.amountJoinPot / 100.0, true);
                if (hasFunds) {
                    StringBuilder message = new StringBuilder(this.createMessage("CHARGE_NEW_POT", username));
                    message.append(this.createMessage("CHARGE_CONF_NO_MSG", username));
                    this.setGameState(BotData.BotStateEnum.GAME_STARTING);
                    this.sendMessage(message.toString(), username);
                    this.gameStarter = username;
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"DiceBot: starting timer for StartGame()");
                    }
                    this.executor.schedule(new StartGame(this), this.timeToConfirmCharge, TimeUnit.SECONDS);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"DiceBot: started timer for StartGame()");
                    }
                } else {
                    this.resetGame(false);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: No charges. Game started by user[" + username + "]"));
                }
                this.setGameState(BotData.BotStateEnum.GAME_STARTING);
                this.gameStarter = username;
                if (log.isDebugEnabled()) {
                    log.debug((Object)"DiceBot: starting timer for StartGame()");
                }
                this.executor.execute(new StartGame(this));
                if (log.isDebugEnabled()) {
                    log.debug((Object)"DiceBot: started timer for StartGame()");
                }
            }
        } else {
            this.sendGameCannotStartMessage(username);
        }
    }

    @Override
    public synchronized void onUserJoinChannel(String username) {
        String message = null;
        switch (this.getGameState().value()) {
            case 1: 
            case 2: 
            case 5: {
                message = this.createMessage("GAME_STATE_STARTED");
                break;
            }
            case 3: {
                message = this.amountJoinPot > 0.0 ? this.createMessage("GAME_JOIN_PAID") : this.createMessage("GAME_JOIN_FREE");
                break;
            }
            default: {
                message = this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT");
            }
        }
        this.sendMessage(message, username);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onUserLeaveChannel(String username) {
        Dice dice = this;
        synchronized (dice) {
            this.removePlayerFromPot(username);
            if (this.playerDiceRolls != null) {
                this.removeSafePlayer(username);
                if (this.playerDiceRolls.containsKey(username)) {
                    this.playerDiceRolls.remove(username);
                    this.sendChannelMessage(this.createMessage("PLAYER_LEFT", username));
                }
                if (this.getGameState() == BotData.BotStateEnum.PLAYING && this.playerDiceRolls.size() < this.minPlayers) {
                    if (this.nextRollTimerTask != null && !this.nextRollTimerTask.isDone() && !this.nextRollTimerTask.isCancelled()) {
                        this.nextRollTimerTask.cancel(true);
                    }
                    this.pickWinner();
                }
            }
        }
    }

    private void removePlayerFromPot(String username) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Player lost, and is not immune :" + username + ". Removing from pot."));
        }
        if (this.pot != null) {
            try {
                this.pot.removePlayer(username);
            }
            catch (Exception e) {
                log.error((Object)("BotInstanceID: " + this.instanceID + "]: Error removing player " + username + "] from pot."), (Throwable)e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private synchronized void endGame(String winner) {
        block11: {
            block10: {
                try {
                    try {
                        Pot localPot;
                        if (this.getGameState() != BotData.BotStateEnum.PLAYING) {
                            Object var5_2 = null;
                            this.resetGame(false);
                            this.updateLastActivityTime();
                            break block10;
                        }
                        if (this.nextRollTimerTask != null && !this.nextRollTimerTask.isDone() && !this.nextRollTimerTask.isCancelled()) {
                            log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: Pending timer task to cancel in endGame() "));
                            this.nextRollTimerTask.cancel(true);
                        }
                        if ((localPot = this.pot) != null) {
                            try {
                                this.winnings = localPot.payout(true);
                                Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
                                this.winnings = accountEJB.convertCurrency(this.winnings, "AUD", "USD");
                                log.debug((Object)("Game over. Pot [" + localPot.getPotID() + "] payout completed."));
                            }
                            catch (Exception e) {
                                log.error((Object)("Game over. Error in pot [" + localPot.getPotID() + "] payout."), (Throwable)e);
                                this.sendChannelMessageAndPopUp(SystemProperty.get(SystemPropertyEntities.BOT.PAYOUT_FAILURE_MESSAGE));
                                Object var5_3 = null;
                                this.resetGame(false);
                                this.updateLastActivityTime();
                                this.sendChannelMessage(this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT"));
                                return;
                            }
                        }
                        this.sendChannelMessageAndPopUp(this.amountJoinPot > 0.0 ? this.createMessage("GAME_OVER_PAID", winner) : this.createMessage("GAME_OVER_FREE", winner));
                        this.logMostWins(winner, this.winnings);
                        this.incrementMostWins(Leaderboard.Type.DICE_MOST_WINS, winner);
                        break block11;
                    }
                    catch (Exception e) {
                        log.error((Object)("botInstanceID[" + this.getInstanceID() + "]: Error getting game winner. "), (Throwable)e);
                        Object var5_5 = null;
                        this.resetGame(false);
                        this.updateLastActivityTime();
                        this.sendChannelMessage(this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT"));
                        return;
                    }
                }
                catch (Throwable throwable) {
                    Object var5_6 = null;
                    this.resetGame(false);
                    this.updateLastActivityTime();
                    this.sendChannelMessage(this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT"));
                    throw throwable;
                }
            }
            this.sendChannelMessage(this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT"));
            return;
        }
        Object var5_4 = null;
        this.resetGame(false);
        this.updateLastActivityTime();
        this.sendChannelMessage(this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT"));
    }

    void resetGame(boolean cancel) {
        if (cancel) {
            this.revertLimitInCache(this.playerDiceRolls.keySet());
        }
        this.playerDiceRolls.clear();
        this.safePlayers.clear();
        this.nextRollTimerTask = null;
        this.currentRoundNumber = 0;
        this.isRoundStarted = false;
        this.hasWinner = false;
        this.numPlayed = 0;
        this.gameStarter = null;
        this.botDice.reset();
        this.pot = null;
        this.amountJoinPot = this.amountOriginalJoinPot;
        this.setGameState(BotData.BotStateEnum.NO_GAME);
    }

    private void addSafePlayer(int roundNumber, String username) {
        Set<String> players = this.safePlayers.get(roundNumber);
        if (players == null) {
            players = new HashSet<String>();
            this.safePlayers.put(roundNumber, players);
        }
        players.add(username);
    }

    private void removeSafePlayer(String username) {
        for (Integer roundNumber : this.safePlayers.keySet()) {
            Set<String> playerList = this.safePlayers.get(roundNumber);
            playerList.remove(username);
        }
    }

    private boolean isSafePlayer(int roundNumber, String username) {
        boolean isSafe = false;
        Set<String> players = this.safePlayers.get(roundNumber);
        if (players != null) {
            isSafe = players.contains(username);
        }
        return isSafe;
    }

    private void removeSafeList(int roundNumber) {
        this.safePlayers.remove(roundNumber);
    }

    private void showSafePlayers(int roundNumber) {
        Set<String> players = this.safePlayers.get(roundNumber);
        if (players != null) {
            for (String player : players) {
                log.debug((Object)(player + " "));
            }
        }
    }

    protected String createMessage(String messageKey) {
        return this.createMessage(messageKey, null, null, null);
    }

    String createMessage(String messageKey, String username) {
        return this.createMessage(messageKey, username, null, null);
    }

    private String createMessage(String messageKey, String username, DiceRoll dice) {
        return this.createMessage(messageKey, username, dice, null);
    }

    private String createMessage(String messageKey, String player, DiceRoll dice, String errorInput) {
        try {
            String messageToSend;
            if (log.isDebugEnabled()) {
                log.debug((Object)("Looking for messageKey: " + messageKey));
            }
            if ((messageToSend = (String)this.messages.get(messageKey)) == null) {
                messageToSend = messageKey;
            }
            messageToSend = messageToSend.replaceAll("BOTNAME", this.botData.getDisplayName());
            messageToSend = messageToSend.replaceAll("CONF_TIMER", this.timeToConfirmCharge + "");
            messageToSend = messageToSend.replaceAll("TIMER_JOIN", "" + this.timeToJoinGame);
            messageToSend = messageToSend.replaceAll("TIMER_ROLL", "" + this.timeToRoll);
            messageToSend = messageToSend.replaceAll("TIMER_ROUND", "" + this.timeToNewRound);
            messageToSend = messageToSend.replaceAll("CMD_ROLL", COMMAND_ROLL);
            messageToSend = messageToSend.replaceAll("CMD_NO", "!n");
            messageToSend = messageToSend.replaceAll("CMD_JOIN", "!j");
            messageToSend = messageToSend.replaceAll("CMD_START", "!start");
            messageToSend = messageToSend.replaceAll("MINPLAYERS", this.minPlayers + "");
            if (player != null) {
                messageToSend = messageToSend.replaceAll("PLAYER", player);
                messageToSend = messageToSend.replaceAll("LEADER", player);
            }
            if (dice != null) {
                messageToSend = messageToSend.replaceAll("DICE_VALUES", dice.toString());
            }
            if (this.botDice != null && this.botDice.total() > 0) {
                messageToSend = messageToSend.replaceAll("DICE_TOTAL", this.botDice.total() + "");
            }
            messageToSend = messageToSend.replaceAll("CURRENCY", "USD");
            messageToSend = messageToSend.replaceAll("AMOUNT_POT", this.amountJoinPot / 100.0 + "");
            messageToSend = messageToSend.replaceAll("CUSTOM_MIN_AMOUNT", this.amountJoinPot + 1.0 + "");
            if (this.winnings > 0.0) {
                DecimalFormat df = new DecimalFormat("0.00");
                df.setMinimumFractionDigits(2);
                df.setMaximumFractionDigits(2);
                messageToSend = messageToSend.replaceAll("WINNINGS", df.format(this.winnings));
            }
            if (StringUtils.hasLength((String)errorInput)) {
                errorInput = errorInput.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$");
                messageToSend = messageToSend.replaceAll("ERROR_INPUT", errorInput);
            }
            if (this.currentRoundNumber != 0) {
                messageToSend = messageToSend.replaceAll("ROUND_NUMBER", this.currentRoundNumber + "");
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

    private synchronized void roll(String username, boolean auto) {
        DiceRoll dice = this.playerDiceRolls.get(username);
        if (dice.total() == 0) {
            dice.rollAndMatch(this.botDice.total());
            if (dice.total() == this.botDice.total()) {
                this.sendChannelMessage(this.createMessage(auto ? "AUTO_ROLL_MATCH" : "PLAYER_ROLLS_MATCH", username, dice));
                if (!this.hasWinner) {
                    this.hasWinner = true;
                }
            } else if (dice.total() > this.botDice.total()) {
                this.sendChannelMessage(this.createMessage(auto ? "AUTO_ROLL_HIGHER" : "PLAYER_ROLLS_HIGHER", username, dice));
                if (!this.hasWinner) {
                    this.hasWinner = true;
                }
                if (dice.total() == 12) {
                    this.addSafePlayer(this.currentRoundNumber + 1, username);
                    this.sendChannelMessage(this.createMessage("IMMUNITY", username, dice));
                }
            } else if (this.isSafePlayer(this.currentRoundNumber, username)) {
                this.sendChannelMessage(this.createMessage("SAFE_BY_IMMUNITY", username, dice));
            } else {
                this.sendChannelMessage(this.createMessage(auto ? "AUTO_ROLL_OUT" : "PLAYER_ROLLS_OUT", username, dice));
            }
            if (!auto && ++this.numPlayed >= this.playerDiceRolls.size()) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Looks like everyone has rolled. Let's tally!");
                }
                if (this.nextRollTimerTask != null && !this.nextRollTimerTask.isDone() && !this.nextRollTimerTask.isCancelled()) {
                    this.nextRollTimerTask.cancel(true);
                }
                this.tallyRolls();
            }
        } else if (!auto) {
            this.sendMessage(this.createMessage("ALREADY_ROLLED", username), username);
        } else {
            log.warn((Object)("Auto roll requested for player: " + username + ". But they already seem to have rolled!"));
        }
    }

    private void newRound() {
        this.isRoundStarted = true;
        ++this.currentRoundNumber;
        this.hasWinner = false;
        this.resetDice();
        this.numPlayed = 0;
        this.botDice.roll();
        this.sendChannelMessage(this.createMessage("BOT_ROLLED", null, this.botDice));
        this.sendChannelMessage(this.createMessage("PLAYERS_TURN"));
        this.executor.schedule(new TimedPickWinnerTask(this, this.currentRoundNumber), this.timeToRoll, TimeUnit.SECONDS);
    }

    private void resetDice() {
        this.botDice.reset();
        for (DiceRoll dice : this.playerDiceRolls.values()) {
            dice.reset();
        }
    }

    private void tallyRolls() {
        ArrayList<String> losers = new ArrayList<String>();
        for (String player : this.playerDiceRolls.keySet()) {
            DiceRoll dice = this.playerDiceRolls.get(player);
            if (dice.total() == 0) {
                this.roll(player, true);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Bot rolls for :" + player + ": " + dice.toString()));
                }
            }
            if (dice.isWinner() && !this.hasWinner) {
                this.hasWinner = true;
            }
            if (dice.isWinner()) continue;
            if (!this.isSafePlayer(this.currentRoundNumber, player)) {
                losers.add(player);
                continue;
            }
            if (!log.isDebugEnabled()) continue;
            log.debug((Object)("Skipping removal of player " + player + "because they have immunity"));
        }
        if (this.hasWinner) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Safe players: ");
                this.showSafePlayers(this.currentRoundNumber);
                log.debug((Object)"Next round safe players: ");
                this.showSafePlayers(this.currentRoundNumber + 1);
            }
            for (String player : losers) {
                this.playerDiceRolls.remove(player);
                this.removePlayerFromPot(player);
                this.sendMessage(this.createMessage("PLAYER_LOST", player), player);
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: " + "Removed user: " + player));
            }
        }
        this.removeSafeList(this.currentRoundNumber);
        if (log.isDebugEnabled()) {
            log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: " + "Players remaining: " + this.playerDiceRolls.size()));
        }
        if (this.playerDiceRolls.size() > 1) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: " + "Players size > 1"));
            }
            if (!this.hasWinner) {
                this.sendChannelMessage(this.createMessage("ALL_LOST_PLAY_AGAIN"));
            }
            this.isRoundStarted = false;
            this.sendChannelMessage(this.createMessage("NEXT_ROUND"));
            this.executor.schedule(new Runnable(){

                public void run() {
                    Dice.this.newRound();
                }
            }, this.timeToNewRound, TimeUnit.SECONDS);
            if (log.isDebugEnabled()) {
                log.debug((Object)" Started timer for a new round");
            }
        } else if (this.playerDiceRolls.size() == 1) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: " + "Players size = 1"));
            }
            this.pickWinner();
        }
    }

    private void pickWinner() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: " + "Picking winner: "));
        }
        Iterator<String> iterator = this.playerDiceRolls.keySet().iterator();
        String winner = iterator.next();
        this.endGame(winner);
    }

    public static void main(String[] args) {
        try {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
            BotData botData = new BotData();
            botData.setDisplayName("Dice");
            Dice dice = new Dice(executor, null, botData, "EN", "koko", null);
            dice.timeToRoll = 5L;
            dice.timeToNewRound = 3L;
            dice.timeToConfirmCharge = 1L;
            dice.timeToJoinGame = 3L;
            dice.amountJoinPot = 0.0;
            dice.messages.put("BOT_ADDED", "Bot BOTNAME added to room.");
            dice.messages.put("GAME_STATE_DEFAULT_AMOUNT", "Play now: CMD_START to enter. Cost: CURRENCY AMOUNT_POT. For custom entry, CMD_START <entry_amount>");
            dice.messages.put("ADDED_TO_GAME", "PLAYER: added to game.");
            dice.messages.put("GAME_JOIN_FREE", "Dice started. CMD_JOIN to join. TIMER_JOIN seconds");
            dice.messages.put("JOIN_NO_MIN", "Joining ends. Not enough players. Need MINPLAYERS.");
            dice.messages.put("JOIN", "PLAYER joined the game.");
            dice.messages.put("GAME_STARTED_NOTE", "Game begins! Bot rolls first - match or beat total to stay IN!");
            dice.messages.put("BOT_ROLLED", "ROUND #ROUND_NUMBER: Bot rolled DICE_VALUES Your TARGET: DICE_TOTAL!");
            dice.messages.put("PLAYERS_TURN", "Players: CMD_ROLL to roll. TIMER_ROLL seconds. ");
            dice.messages.put("AUTO_ROLL_OUT", "Bot rolls - PLAYER: DICE_VALUES OUT! ");
            dice.messages.put("AUTO_ROLL_HIGHER", "Bot rolls - PLAYER: DICE_VALUES IN!");
            dice.messages.put("AUTO_ROLL_MATCH", "Bot rolls - PLAYER: DICE_VALUES IN!");
            dice.messages.put("ALL_LOST_PLAY_AGAIN", "Nobody won, so we'll try again!");
            dice.messages.put("NEXT_ROUND", "Players, next round starts in TIMER_ROUND seconds!");
            dice.messages.put("GAME_OVER_FREE", "Game over! LEADER wins!! CONGRATS!");
            dice.messages.put("IMMUNITY", "PLAYER: DICE_VALUES = immunity for the next round!");
            dice.onMessage("koko", "!start", System.currentTimeMillis());
            Thread.sleep(1000L);
            dice.onMessage("kien", "!j", System.currentTimeMillis());
            Thread.sleep(3000L);
            dice.onUserLeaveChannel("koko");
            Thread.sleep(60000L);
            executor.shutdown();
            executor.awaitTermination(60L, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    class TimedPickWinnerTask
    implements Runnable {
        Dice bot;
        int roundNumber;

        TimedPickWinnerTask(Dice bot, int roundNumber) {
            this.bot = bot;
            this.roundNumber = roundNumber;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            Dice dice = this.bot;
            synchronized (dice) {
                if (this.bot.getGameState() == BotData.BotStateEnum.PLAYING && this.bot.currentRoundNumber == this.roundNumber) {
                    this.bot.sendChannelMessage(this.bot.createMessage("TIME_UP"));
                    this.bot.tallyRolls();
                }
            }
        }
    }

    class StartPlay
    implements Runnable {
        Dice bot;

        StartPlay(Dice bot) {
            this.bot = bot;
        }

        public void run() {
            try {
                BotData.BotStateEnum gameState;
                if (log.isDebugEnabled()) {
                    log.debug((Object)"DiceBot: starting play in StartPlay()");
                }
                if ((gameState = Dice.this.getGameState()) == BotData.BotStateEnum.GAME_JOINING) {
                    Dice.this.setGameState(BotData.BotStateEnum.GAME_JOIN_ENDED);
                    if (Dice.this.getPlayers().size() < Dice.this.minPlayers) {
                        Dice.this.sendChannelMessage(Dice.this.createMessage("JOIN_NO_MIN"));
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("botInstanceID[" + Dice.this.getInstanceID() + "]: Join ended. Not enough players."));
                        }
                        Dice.this.resetGame(true);
                    } else {
                        HashSet copyOfPlayers = new HashSet();
                        copyOfPlayers.addAll(Dice.this.playerDiceRolls.keySet());
                        if (gameState != BotData.BotStateEnum.NO_GAME) {
                            gameState = BotData.BotStateEnum.PLAYING;
                            try {
                                if (Dice.this.amountJoinPot > 0.0) {
                                    this.bot.pot = new Pot(this.bot);
                                    log.debug((Object)("Pot id[" + Dice.this.pot.getPotID() + "] created for bot instanceID[" + Dice.this.getInstanceID() + "]"));
                                    for (String player : copyOfPlayers) {
                                        try {
                                            Dice.this.pot.enterPlayer(player, Dice.this.amountJoinPot / 100.0, "USD");
                                            if (!log.isDebugEnabled()) continue;
                                            log.debug((Object)("botInstanceID[" + Dice.this.getInstanceID() + "]: Entered into pot " + player + " = " + "USD" + " " + Dice.this.amountJoinPot / 100.0));
                                        }
                                        catch (Exception e) {
                                            Dice.this.playerDiceRolls.remove(player);
                                            log.warn((Object)("botInstanceID[" + Dice.this.getInstanceID() + "]: Error charging player[" + player + "]"), (Throwable)e);
                                            Dice.this.sendMessage(Dice.this.createMessage("INSUFFICIENT_FUNDS_POT", player), player);
                                        }
                                    }
                                    if (Dice.this.playerDiceRolls.size() < Dice.this.minPlayers) {
                                        if (log.isDebugEnabled()) {
                                            log.debug((Object)("botInstanceID[" + Dice.this.getInstanceID() + "]: Not enough valid players."));
                                        }
                                        this.cancelPot();
                                        Dice.this.resetGame(true);
                                        return;
                                    }
                                }
                                Dice.this.incrementGamesPlayed(Leaderboard.Type.DICE_GAMES_PLAYED, Dice.this.playerDiceRolls.keySet());
                                Dice.this.logGamesPlayed(Dice.this.playerDiceRolls.size(), Dice.this.playerDiceRolls.keySet(), Dice.this.amountJoinPot);
                                Dice.this.sendChannelMessage(Dice.this.createMessage("GAME_STARTED_NOTE"));
                                Dice.this.setGameState(BotData.BotStateEnum.PLAYING);
                                Dice.this.newRound();
                            }
                            catch (Exception e) {
                                log.error((Object)("Error creating pot for botInstanceID[" + Dice.this.getInstanceID() + "]."), (Throwable)e);
                                Dice.this.setGameState(BotData.BotStateEnum.NO_GAME);
                                Dice.this.sendChannelMessage(Dice.this.createMessage("GAME_CANCELED"));
                            }
                        } else {
                            this.cancelPot();
                            Dice.this.resetGame(true);
                            if (log.isDebugEnabled()) {
                                log.debug((Object)("botInstanceID[" + Dice.this.getInstanceID() + "]: Billing error. Game canceled. No charges."));
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                log.error((Object)"Unexpected exception caught in startPlay.run()", (Throwable)e);
                this.cancelPot();
                Dice.this.resetGame(true);
            }
        }

        private void cancelPot() {
            try {
                if (Dice.this.pot != null) {
                    Dice.this.pot.cancel();
                }
            }
            catch (Exception e) {
                log.error((Object)("Error canceling pot for botInstanceID[" + Dice.this.getInstanceID() + "]."), (Throwable)e);
            }
            Dice.this.sendChannelMessage(Dice.this.createMessage("GAME_CANCELED"));
        }
    }

    class StartGame
    implements Runnable {
        Dice bot;

        StartGame(Dice bot) {
            this.bot = bot;
        }

        public void run() {
            if (log.isDebugEnabled()) {
                log.debug((Object)("botInstanceID[" + Dice.this.getInstanceID() + "]: in StartGame() "));
            }
            BotData.BotStateEnum gameState = null;
            gameState = Dice.this.getGameState();
            if (gameState == BotData.BotStateEnum.GAME_STARTING) {
                Dice.this.setGameState(BotData.BotStateEnum.GAME_STARTED);
                Dice.this.addPlayer(Dice.this.gameStarter);
                if (Dice.this.timeToJoinGame > 0L) {
                    Dice.this.setGameState(BotData.BotStateEnum.GAME_JOINING);
                    Dice.this.sendChannelMessage(Dice.this.amountJoinPot > 0.0 ? Dice.this.createMessage("GAME_JOIN_PAID") : Dice.this.createMessage("GAME_JOIN_FREE"));
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"DiceBot: starting timer for StartPlay()");
                    }
                    Dice.this.executor.schedule(new StartPlay(this.bot), Dice.this.timeToJoinGame, TimeUnit.SECONDS);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("botInstanceID[" + Dice.this.getInstanceID() + "]: scheduled to start play. Awaiting join.. "));
                    }
                }
            }
        }
    }
}

