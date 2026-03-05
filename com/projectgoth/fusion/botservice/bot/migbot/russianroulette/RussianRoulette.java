/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.botservice.bot.migbot.russianroulette;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.bot.Pot;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.dao.BotDAO;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.slice.BotChannelPrx;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RussianRoulette
extends Bot {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RussianRoulette.class));
    public static final String TIMER_JOIN_GAME = "timerJoinGame";
    public static final String TIMER_CHARGE_CONF = "timerChargeConfirm";
    public static final String TIMER_SPIN = "timerSpin";
    public static final String TIMER_IDLE = "timerIdle";
    public static final String AMOUNT_JOIN_POT = "amountJoinPot";
    public static final long TIMER_SPIN_VALUE = 10L;
    public static final double AMOUNT_JOIN_POT_VALUE = 5.0;
    public static final long IDLE_TIME_VALUE = 3L;
    long timeToJoinGame = 90L;
    long timeToConfirmCharge = 20L;
    long timeToSpin = 10L;
    double amountJoinPot = 5.0;
    double winnings = 0.0;
    public int minPlayers = 2;
    long timeAllowedToIdle = 30L;
    double amountOriginalJoinPot = 5.0;
    public static final String COMMAND_SPIN = "!s";
    Date lastActivityTime;
    private BotData.BotStateEnum gameState = BotData.BotStateEnum.NO_GAME;
    int currentRoundNumber = 0;
    String currentPlayer;
    public List<String> players;
    List<String> playersRemaining;
    ScheduledFuture nextSpinTimerTask = null;

    public RussianRoulette(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDao) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDao);
        this.loadGameConfig();
        log.info((Object)("RussianRouletteBot [" + this.instanceID + "] added to channel [" + this.channel + "]"));
        this.sendChannelMessage(this.createMessage("BOT_ADDED"));
        String message = this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT");
        this.sendChannelMessage(message);
        this.players = new ArrayList<String>();
        this.playersRemaining = new ArrayList<String>();
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
        if (this.nextSpinTimerTask != null && !this.nextSpinTimerTask.isDone() && !this.nextSpinTimerTask.isCancelled()) {
            this.nextSpinTimerTask.cancel(true);
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
            this.resetGame();
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
        this.timeToSpin = this.getLongParameter(TIMER_SPIN, 10L);
        this.amountOriginalJoinPot = this.amountJoinPot = this.getDoubleParameter(AMOUNT_JOIN_POT, 5.0);
        this.timeAllowedToIdle = this.getLongParameter(TIMER_IDLE, 3L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onMessage(String username, String messageText, long receivedTimestamp) {
        RussianRoulette russianRoulette = this;
        synchronized (russianRoulette) {
            if (messageText.equalsIgnoreCase("!n")) {
                this.processNoMessage(username);
            } else if (messageText.toLowerCase().startsWith("!start")) {
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
            } else if (messageText.equalsIgnoreCase("!j")) {
                if (!this.players.contains(username)) {
                    if (this.gameState == BotData.BotStateEnum.GAME_JOINING) {
                        this.addPlayer(username);
                    } else {
                        this.sendMessage(this.createMessage("JOIN_ENDED", username), username);
                    }
                } else {
                    this.sendMessage(this.createMessage("ALREADY_IN_GAME", username), username);
                }
            } else if (messageText.equalsIgnoreCase(COMMAND_SPIN) && this.gameState == BotData.BotStateEnum.PLAYING) {
                if (!username.equals(this.currentPlayer)) {
                    this.sendMessage(this.createMessage("NOT_YOUR_TURN", username), username);
                } else {
                    this.sendChannelMessage(this.createMessage("PLAYER_SPINS", username));
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("botInstanceID[" + this.instanceID + "]: " + username + " spins"));
                    }
                    if (this.nextSpinTimerTask != null && !this.nextSpinTimerTask.isCancelled()) {
                        this.nextSpinTimerTask.cancel(false);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("User " + username + " spins for themselves. Auto-spin canceled? " + this.nextSpinTimerTask.isCancelled() + ". Or was it done? " + this.nextSpinTimerTask.isDone()));
                        }
                    }
                    this.spin(username);
                }
            }
        }
    }

    private void sendGameCannotStartMessage(String username) {
        String message = null;
        switch (this.gameState.value()) {
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

    public List<String> getPlayers() {
        return this.players;
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
            List<String> hasFunds22 = this.players;
            synchronized (hasFunds22) {
                if (!this.players.contains(username)) {
                    this.players.add(username);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void spin(String player) {
        if (this.players.size() > 1) {
            Random ChamberValue = new Random(System.currentTimeMillis());
            int intChamber = ChamberValue.nextInt(6);
            if (intChamber == 5) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("botInstanceID[" + this.instanceID + "]: " + player + " got a BANG"));
                }
                this.sendChannelMessage(this.createMessage("BANG", player));
                List<String> list = this.players;
                synchronized (list) {
                    this.players.remove(player);
                }
                try {
                    this.pot.removePlayer(player);
                }
                catch (Exception e) {
                    log.error((Object)("botInstanceID[" + this.instanceID + "]: Problem removing player[" + player + "] from pot stake. "));
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("botInstanceID[" + this.instanceID + "]: " + player + " got a CLICK"));
                }
                this.sendChannelMessage(this.createMessage("CLICK", player));
            }
            if (this.players.size() > 1) {
                this.nextPlayer();
            } else if (this.players.size() == 1) {
                this.endGame();
            }
        }
    }

    void nextPlayer() {
        if (this.players.size() > 1) {
            if (this.playersRemaining.isEmpty()) {
                this.playersRemaining.addAll(this.players);
                if (++this.currentRoundNumber > 1) {
                    this.sendChannelMessage(this.createMessage("NEXT_ROUND"));
                }
                this.sendChannelMessage(this.createMessage("SPIN_ORDER"));
            }
            this.currentPlayer = this.playersRemaining.remove(0);
            if (this.currentPlayer == null) {
                log.warn((Object)"Unable to assign current player. playersRemaining.remove(0) return null");
                this.resetGame();
                return;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("botInstanceID[" + this.instanceID + "]: Next spin by " + this.currentPlayer));
            }
            this.sendChannelMessage(this.createMessage("PLAYER_TURN_TO_SPIN", this.currentPlayer));
            this.nextSpinTimerTask = this.executor.schedule(new TimedAutoSpinTask(this, this.currentPlayer), this.timeToSpin, TimeUnit.SECONDS);
        }
    }

    public void processNoMessage(String username) {
        String message = null;
        switch (this.getGameState().value()) {
            case 1: {
                if (username.equals(this.gameStarter) && this.amountJoinPot > 0.0) {
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
                String message = this.createMessage("INVALID_AMOUNT", username, parameter);
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
            String message = this.createMessage("INVALID_AMOUNT", username, parameter);
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
                        log.debug((Object)"RussianRouletteBot: starting timer for StartGame()");
                    }
                    this.executor.schedule(new StartGame(this), this.timeToConfirmCharge, TimeUnit.SECONDS);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"RussianRouletteBot: started timer for StartGame()");
                    }
                } else {
                    this.resetGame();
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: No charges. Game started by user[" + username + "]"));
                }
                this.setGameState(BotData.BotStateEnum.GAME_STARTING);
                this.gameStarter = username;
                if (log.isDebugEnabled()) {
                    log.debug((Object)"RussianRouletteBot: starting timer for StartGame()");
                }
                this.executor.execute(new StartGame(this));
                if (log.isDebugEnabled()) {
                    log.debug((Object)"RussianRouletteBot: started timer for StartGame()");
                }
            }
        } else {
            this.sendGameCannotStartMessage(username);
        }
    }

    @Override
    public void onUserJoinChannel(String username) {
        String message = null;
        switch (this.getGameState().value()) {
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
        RussianRoulette russianRoulette = this;
        synchronized (russianRoulette) {
            if (this.pot != null) {
                try {
                    this.pot.removePlayer(username);
                }
                catch (Exception e) {
                    log.error((Object)("BotInstanceID: " + this.instanceID + "]: Error making player " + username + "] ineligible for pot."), (Throwable)e);
                }
            }
            List<String> list = this.playersRemaining;
            synchronized (list) {
                if (this.playersRemaining.contains(username)) {
                    this.playersRemaining.remove(username);
                }
            }
            if (this.players != null) {
                list = this.players;
                synchronized (list) {
                    if (this.players.contains(username)) {
                        this.players.remove(username);
                        this.sendChannelMessage(this.createMessage("PLAYER_LEFT", username));
                    }
                }
                if (this.getGameState() == BotData.BotStateEnum.PLAYING) {
                    if (this.players.size() >= this.minPlayers && username.equals(this.currentPlayer)) {
                        if (this.nextSpinTimerTask != null && !this.nextSpinTimerTask.isDone() && !this.nextSpinTimerTask.isCancelled()) {
                            this.nextSpinTimerTask.cancel(true);
                        }
                        this.nextPlayer();
                    } else if (this.players.size() < this.minPlayers) {
                        this.endGame();
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void endGame() {
        block14: {
            block13: {
                try {
                    try {
                        if (this.getGameState() != BotData.BotStateEnum.PLAYING) {
                            Object var5_1 = null;
                            this.resetGame();
                            this.updateLastActivityTime();
                            break block13;
                        }
                        if (this.nextSpinTimerTask != null && !this.nextSpinTimerTask.isDone() && !this.nextSpinTimerTask.isCancelled()) {
                            log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: Pending timer task to cancel in endGame() "));
                            this.nextSpinTimerTask.cancel(true);
                        }
                        if (!this.players.isEmpty()) {
                            String winner = this.players.get(0);
                            Pot localPot = this.pot;
                            if (localPot != null) {
                                try {
                                    this.winnings = localPot.payout(true);
                                    Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
                                    this.winnings = accountEJB.convertCurrency(this.winnings, "AUD", "USD");
                                    log.debug((Object)("Game over. Pot [" + localPot.getPotID() + "] payout completed."));
                                }
                                catch (Exception e) {
                                    log.error((Object)("Game over. Error in pot [" + localPot.getPotID() + "] payout."), (Throwable)e);
                                }
                            }
                            if (this.winnings < 0.0) {
                                this.sendChannelMessageAndPopUp(SystemProperty.get(SystemPropertyEntities.BOT.PAYOUT_FAILURE_MESSAGE));
                                break block14;
                            } else {
                                this.sendChannelMessageAndPopUp(this.amountJoinPot > 0.0 ? this.createMessage("GAME_OVER_PAID", winner) : this.createMessage("GAME_OVER_FREE", winner));
                            }
                            break block14;
                        }
                        log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: endGame(): There are no players remaining. players empty."));
                        break block14;
                    }
                    catch (Exception e) {
                        log.error((Object)("botInstanceID[" + this.getInstanceID() + "]: Error getting game winner. "), (Throwable)e);
                        Object var5_3 = null;
                        this.resetGame();
                        this.updateLastActivityTime();
                        this.sendChannelMessage(this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT"));
                        return;
                    }
                }
                catch (Throwable throwable) {
                    Object var5_4 = null;
                    this.resetGame();
                    this.updateLastActivityTime();
                    this.sendChannelMessage(this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT"));
                    throw throwable;
                }
            }
            this.sendChannelMessage(this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT"));
            return;
        }
        Object var5_2 = null;
        this.resetGame();
        this.updateLastActivityTime();
        this.sendChannelMessage(this.amountJoinPot > 0.0 ? this.createMessage("GAME_STATE_DEFAULT_AMOUNT") : this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT"));
    }

    void resetGame() {
        this.nextSpinTimerTask = null;
        this.currentRoundNumber = 0;
        this.currentPlayer = null;
        this.gameStarter = null;
        this.players.clear();
        this.playersRemaining.clear();
        this.pot = null;
        this.amountJoinPot = this.amountOriginalJoinPot;
        this.setGameState(BotData.BotStateEnum.NO_GAME);
    }

    String createMessage(String messageKey, String username) {
        return this.createMessage(messageKey, username, null);
    }

    protected String createMessage(String messageKey) {
        return this.createMessage(messageKey, null, null);
    }

    private String createMessage(String messageKey, String player, String errorInput) {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Looking for messageKey: " + messageKey));
            }
            String messageToSend = (String)this.messages.get(messageKey);
            messageToSend = messageToSend.replaceAll("BOTNAME", this.botData.getDisplayName());
            messageToSend = messageToSend.replaceAll("CONF_TIMER", this.timeToConfirmCharge + "");
            messageToSend = messageToSend.replaceAll("TIMER_JOIN", "" + this.timeToJoinGame);
            messageToSend = messageToSend.replaceAll("TIMER_SPIN", "" + this.timeToSpin);
            messageToSend = messageToSend.replaceAll("SPIN_COMMAND", COMMAND_SPIN);
            messageToSend = messageToSend.replaceAll("CMD_NO", "!n");
            messageToSend = messageToSend.replaceAll("CMD_JOIN", "!j");
            messageToSend = messageToSend.replaceAll("CMD_START", "!start");
            messageToSend = messageToSend.replaceAll("MINPLAYERS", this.minPlayers + "");
            if (player != null) {
                messageToSend = messageToSend.replaceAll("PLAYER", player);
                messageToSend = messageToSend.replaceAll("LEADER", player);
            }
            messageToSend = messageToSend.replaceAll("CURRENCY", "USD");
            messageToSend = messageToSend.replaceAll("AMOUNT_POT", this.amountJoinPot / 100.0 + "");
            messageToSend = messageToSend.replaceAll("CUSTOM_MIN_AMOUNT", this.amountJoinPot + 1.0 + "");
            if (this.winnings > 0.0) {
                DecimalFormat df = new DecimalFormat("0.00");
                messageToSend = messageToSend.replaceAll("WINNINGS", df.format(this.winnings));
            }
            if (StringUtils.hasLength((String)errorInput)) {
                errorInput = errorInput.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$");
                messageToSend = messageToSend.replaceAll("ERROR_INPUT", errorInput);
            }
            if (this.currentRoundNumber != 0) {
                messageToSend = messageToSend.replaceAll("ROUND_NUMBER", this.currentRoundNumber + "");
            }
            if (this.playersRemaining != null && this.playersRemaining.size() > 1) {
                String playerListString = this.stringifyPlayerList();
                messageToSend = messageToSend.replaceAll("PLAYERS", playerListString);
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

    private String stringifyPlayerList() {
        StringBuilder playerList = new StringBuilder();
        for (int i = 0; i < this.playersRemaining.size(); ++i) {
            playerList.append(this.playersRemaining.get(i)).append(", ");
        }
        String playerListString = playerList.toString();
        return playerListString.endsWith(", ") ? playerListString.substring(0, playerListString.length() - 2) : playerListString;
    }

    class TimedAutoSpinTask
    implements Runnable {
        RussianRoulette bot;
        String player;

        TimedAutoSpinTask(RussianRoulette bot, String player) {
            this.bot = bot;
            this.player = player;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            RussianRoulette russianRoulette = this.bot;
            synchronized (russianRoulette) {
                if (this.bot.getGameState() == BotData.BotStateEnum.PLAYING && this.player.equals(RussianRoulette.this.currentPlayer)) {
                    this.bot.sendChannelMessage(this.bot.createMessage("TIME_UP_AUTO_SPIN", RussianRoulette.this.currentPlayer));
                    this.bot.spin(RussianRoulette.this.currentPlayer);
                }
            }
        }
    }

    class StartPlay
    implements Runnable {
        RussianRoulette bot;

        StartPlay(RussianRoulette bot) {
            this.bot = bot;
        }

        public void run() {
            try {
                BotData.BotStateEnum gameState;
                if (log.isDebugEnabled()) {
                    log.debug((Object)"RussianRouletteBot: starting play in StartPlay()");
                }
                if ((gameState = RussianRoulette.this.getGameState()) == BotData.BotStateEnum.GAME_JOINING) {
                    RussianRoulette.this.setGameState(BotData.BotStateEnum.GAME_JOIN_ENDED);
                    if (RussianRoulette.this.getPlayers().size() < RussianRoulette.this.minPlayers) {
                        RussianRoulette.this.sendChannelMessage(RussianRoulette.this.createMessage("JOIN_NO_MIN"));
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("botInstanceID[" + RussianRoulette.this.getInstanceID() + "]: Join ended. Not enough players."));
                        }
                        RussianRoulette.this.setGameState(BotData.BotStateEnum.NO_GAME);
                    } else {
                        boolean hasFunds = false;
                        if (RussianRoulette.this.amountJoinPot > 0.0 && !(hasFunds = RussianRoulette.this.userCanAffordToEnterPot(RussianRoulette.this.gameStarter, RussianRoulette.this.amountJoinPot / 100.0, false))) {
                            RussianRoulette.this.setGameState(BotData.BotStateEnum.NO_GAME);
                            if (log.isDebugEnabled()) {
                                log.debug((Object)("botInstanceID[" + RussianRoulette.this.getInstanceID() + "]: " + RussianRoulette.this.gameStarter + " has insufficient funds."));
                            }
                            return;
                        }
                        HashSet<String> copyOfPlayers = new HashSet<String>();
                        copyOfPlayers.addAll(RussianRoulette.this.players);
                        if (RussianRoulette.this.amountJoinPot > 0.0) {
                            for (String player : copyOfPlayers) {
                                if (player.equals(RussianRoulette.this.gameStarter) || (hasFunds = RussianRoulette.this.userCanAffordToEnterPot(player, RussianRoulette.this.amountJoinPot / 100.0, false))) continue;
                                RussianRoulette.this.players.remove(player);
                                if (log.isDebugEnabled()) {
                                    log.debug((Object)("botInstanceID[" + RussianRoulette.this.getInstanceID() + "]: " + player + " has insufficient funds."));
                                }
                                if (RussianRoulette.this.players.size() >= RussianRoulette.this.minPlayers && RussianRoulette.this.players.size() != 0) continue;
                                RussianRoulette.this.setGameState(BotData.BotStateEnum.NO_GAME);
                                if (!log.isDebugEnabled()) break;
                                log.debug((Object)("botInstanceID[" + RussianRoulette.this.getInstanceID() + "]: Not enough valid players."));
                                break;
                            }
                            if (gameState == BotData.BotStateEnum.NO_GAME) {
                                log.error((Object)("Game canceled: instanceID[" + RussianRoulette.this.getInstanceID() + "]"));
                                RussianRoulette.this.sendChannelMessage(RussianRoulette.this.createMessage("GAME_CANCELED"));
                                return;
                            }
                        }
                        if (gameState != BotData.BotStateEnum.NO_GAME) {
                            gameState = BotData.BotStateEnum.PLAYING;
                            try {
                                if (RussianRoulette.this.amountJoinPot > 0.0) {
                                    this.bot.pot = new Pot(this.bot);
                                    log.debug((Object)("Pot id[" + RussianRoulette.this.pot.getPotID() + "] created for bot instanceID[" + RussianRoulette.this.getInstanceID() + "]"));
                                    for (String player : copyOfPlayers) {
                                        try {
                                            RussianRoulette.this.pot.enterPlayer(player, RussianRoulette.this.amountJoinPot / 100.0, "USD");
                                            if (!log.isDebugEnabled()) continue;
                                            log.debug((Object)("botInstanceID[" + RussianRoulette.this.getInstanceID() + "]: Entered into pot " + player + " = " + "USD" + " " + RussianRoulette.this.amountJoinPot / 100.0));
                                        }
                                        catch (Exception e) {
                                            RussianRoulette.this.players.remove(player);
                                            log.error((Object)("botInstanceID[" + RussianRoulette.this.getInstanceID() + "]: Error charging player[" + player + "]"), (Throwable)e);
                                            RussianRoulette.this.sendMessage(RussianRoulette.this.createMessage("INSUFFICIENT_FUNDS_POT", player), player);
                                        }
                                    }
                                    if (RussianRoulette.this.players.size() < RussianRoulette.this.minPlayers || RussianRoulette.this.players.size() == 0) {
                                        if (log.isDebugEnabled()) {
                                            log.debug((Object)("botInstanceID[" + RussianRoulette.this.getInstanceID() + "]: Not enough valid players."));
                                        }
                                        this.cancelPot();
                                        RussianRoulette.this.resetGame();
                                        return;
                                    }
                                }
                                log.info((Object)("New game started in " + RussianRoulette.this.channel));
                                RussianRoulette.this.sendChannelMessage(RussianRoulette.this.createMessage("GAME_STARTED_NOTE"));
                                RussianRoulette.this.setGameState(BotData.BotStateEnum.PLAYING);
                                Collections.shuffle(RussianRoulette.this.players);
                                RussianRoulette.this.nextPlayer();
                            }
                            catch (Exception e) {
                                log.error((Object)("Error creating pot for botInstanceID[" + RussianRoulette.this.getInstanceID() + "]."), (Throwable)e);
                                RussianRoulette.this.setGameState(BotData.BotStateEnum.NO_GAME);
                                RussianRoulette.this.sendChannelMessage(RussianRoulette.this.createMessage("GAME_CANCELED"));
                            }
                        } else {
                            this.cancelPot();
                            RussianRoulette.this.resetGame();
                            if (log.isDebugEnabled()) {
                                log.debug((Object)("botInstanceID[" + RussianRoulette.this.getInstanceID() + "]: Billing error. Game canceled. No charges."));
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                log.error((Object)"Unexpected exception caught in startPlay.run()", (Throwable)e);
                this.cancelPot();
                RussianRoulette.this.resetGame();
            }
        }

        private void cancelPot() {
            try {
                if (RussianRoulette.this.pot != null) {
                    RussianRoulette.this.pot.cancel();
                }
            }
            catch (Exception e) {
                log.error((Object)("Error canceling pot for botInstanceID[" + RussianRoulette.this.getInstanceID() + "]."), (Throwable)e);
            }
            RussianRoulette.this.sendChannelMessage(RussianRoulette.this.createMessage("GAME_CANCELED"));
        }
    }

    class StartGame
    implements Runnable {
        RussianRoulette bot;

        StartGame(RussianRoulette bot) {
            this.bot = bot;
        }

        public void run() {
            if (log.isDebugEnabled()) {
                log.debug((Object)("botInstanceID[" + RussianRoulette.this.getInstanceID() + "]: in StartGame() "));
            }
            BotData.BotStateEnum gameState = null;
            gameState = RussianRoulette.this.getGameState();
            if (gameState == BotData.BotStateEnum.GAME_STARTING) {
                RussianRoulette.this.setGameState(BotData.BotStateEnum.GAME_STARTED);
                RussianRoulette.this.addPlayer(RussianRoulette.this.gameStarter);
                if (RussianRoulette.this.timeToJoinGame > 0L) {
                    RussianRoulette.this.setGameState(BotData.BotStateEnum.GAME_JOINING);
                    RussianRoulette.this.sendChannelMessage(RussianRoulette.this.amountJoinPot > 0.0 ? RussianRoulette.this.createMessage("GAME_JOIN_PAID") : RussianRoulette.this.createMessage("GAME_JOIN_FREE"));
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"RussianRouletteBot: starting timer for StartPlay()");
                    }
                    RussianRoulette.this.executor.schedule(new StartPlay(this.bot), RussianRoulette.this.timeToJoinGame, TimeUnit.SECONDS);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("botInstanceID[" + RussianRoulette.this.getInstanceID() + "]: scheduled to start play. Awaiting join.. "));
                    }
                }
            }
        }
    }
}

