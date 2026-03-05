/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.botservice.bot.migbot.lowcard;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.bot.Pot;
import com.projectgoth.fusion.botservice.bot.migbot.common.Card;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LowCard
extends Bot {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(LowCard.class));
    public static final String TIMER_JOIN_GAME = "timerJoinGame";
    public static final String TIMER_CHARGE_CONF = "timerChargeConfirm";
    public static final String TIMER_DRAW = "timerDraw";
    public static final String TIMER_NEW_ROUND_INTERVAL = "timerNewRound";
    public static final String TIMER_IDLE = "timerIdle";
    public static final String AMOUNT_JOIN_POT = "amountJoinPot";
    public static final long TIMER_DRAW_VALUE = 10L;
    public static final long TIMER_NEW_ROUND_VALUE = 3L;
    public static final double AMOUNT_JOIN_POT_VALUE = 5.0;
    public static final long IDLE_TIME_VALUE = 3L;
    long timeToJoinGame = 90L;
    long timeToConfirmCharge = 20L;
    long timeToDraw = 10L;
    long timeToNewRound = 10L;
    double amountJoinPot = 5.0;
    double winnings = 0.0;
    public int minPlayers = 2;
    long timeAllowedToIdle = 30L;
    double amountOriginalJoinPot = 5.0;
    public static final String COMMAND_DRAW = "!d";
    Date lastActivityTime;
    private Map<String, Hand> playerHands = new HashMap<String, Hand>();
    private Map<String, Hand> tiebreakerHands = new HashMap<String, Hand>();
    private BotData.BotStateEnum gameState = BotData.BotStateEnum.NO_GAME;
    private boolean isRoundStarted = false;
    private boolean isTiebreaker = false;
    Deck deck = new Deck();
    Hand lowestHandAlreadyLeft = null;
    int currentRoundNumber = 0;
    ScheduledFuture nextDrawTimerTask = null;

    public LowCard(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDao) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDao);
        this.loadGameConfig();
        this.loadCardEmoticons();
        log.info((Object)("LowCardBot [" + this.instanceID + "] added to channel [" + this.channel + "]"));
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

    private void loadCardEmoticons() {
        int i;
        String[] cardEmoticons = new String[Card.EMOTICONS.length + (this.emoticonHotKeys != null ? this.emoticonHotKeys.length : 0)];
        for (i = 0; i < Card.EMOTICONS.length; ++i) {
            cardEmoticons[i] = Card.EMOTICONS[i];
        }
        if (this.emoticonHotKeys != null) {
            i = 0;
            int j = Card.EMOTICONS.length;
            while (i < this.emoticonHotKeys.length) {
                cardEmoticons[j] = this.emoticonHotKeys[i];
                ++i;
                ++j;
            }
        }
        this.emoticonHotKeys = cardEmoticons;
    }

    @Override
    public void stopBot() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Stopping bot instanceID[" + this.instanceID + "]"));
        }
        if (this.nextDrawTimerTask != null && !this.nextDrawTimerTask.isDone() && !this.nextDrawTimerTask.isCancelled()) {
            this.nextDrawTimerTask.cancel(true);
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
        this.timeToDraw = this.getLongParameter(TIMER_DRAW, 10L);
        this.timeToNewRound = this.getLongParameter(TIMER_NEW_ROUND_INTERVAL, 3L);
        this.amountOriginalJoinPot = this.amountJoinPot = this.getDoubleParameter(AMOUNT_JOIN_POT, 5.0);
        this.timeAllowedToIdle = this.getLongParameter(TIMER_IDLE, 3L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void onMessage(String username, String messageText, long receivedTimestamp) {
        LowCard lowCard = this;
        synchronized (lowCard) {
            if (messageText.equalsIgnoreCase("!n")) {
                this.processNoMessage(username);
            } else if (messageText.toLowerCase().startsWith("!start")) {
                this.start(username, messageText);
            } else if (messageText.equalsIgnoreCase("!j")) {
                this.join(username);
            } else if (messageText.toLowerCase().startsWith(COMMAND_DRAW)) {
                if (this.gameState == BotData.BotStateEnum.PLAYING && this.isRoundStarted) {
                    if (!this.playerHands.containsKey(username)) {
                        this.sendMessage(this.createMessage("NOT_IN_GAME", username), username);
                    } else {
                        if (this.isTiebreaker && !this.tiebreakerHands.containsKey(username)) {
                            this.sendMessage(this.createMessage("ONLY_TIED_PLAYERS", username), username);
                            return;
                        }
                        this.draw(username, "", false);
                    }
                } else {
                    this.sendMessage(this.createMessage("INVALID_COMMAND", username), username);
                }
            }
        }
    }

    private void join(String username) {
        if (!this.playerHands.containsKey(username)) {
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

    public Map<String, Hand> getPlayers() {
        return this.playerHands;
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
            Map<String, Hand> hasFunds22 = this.playerHands;
            synchronized (hasFunds22) {
                if (!this.playerHands.containsKey(username)) {
                    this.playerHands.put(username, new Hand(username));
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
                        log.debug((Object)"LowCardBot: starting timer for StartGame()");
                    }
                    this.executor.schedule(new StartGame(this), this.timeToConfirmCharge, TimeUnit.SECONDS);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"LowCardBot: started timer for StartGame()");
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
                    log.debug((Object)"LowCardBot: starting timer for StartGame()");
                }
                this.executor.execute(new StartGame(this));
                if (log.isDebugEnabled()) {
                    log.debug((Object)"LowCardBot: started timer for StartGame()");
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
        LowCard lowCard = this;
        synchronized (lowCard) {
            this.removePlayerFromPot(username);
            if (this.playerHands != null) {
                Map<String, Hand> currentHands = this.isTiebreaker ? this.tiebreakerHands : this.playerHands;
                Hand hand = currentHands.get(username);
                if (hand != null && hand.getCard() != null && (this.lowestHandAlreadyLeft == null || hand.compareTo(this.lowestHandAlreadyLeft) < 0)) {
                    this.lowestHandAlreadyLeft = hand;
                }
                if (this.playerHands.containsKey(username)) {
                    this.playerHands.remove(username);
                    this.sendChannelMessage(this.createMessage("PLAYER_LEFT", username));
                }
                if (this.tiebreakerHands.containsKey(username)) {
                    this.tiebreakerHands.remove(username);
                }
                if (this.getGameState() == BotData.BotStateEnum.PLAYING && this.playerHands.size() < this.minPlayers) {
                    if (this.nextDrawTimerTask != null && !this.nextDrawTimerTask.isDone() && !this.nextDrawTimerTask.isCancelled()) {
                        log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: Pending timer task to cancel in endGame() "));
                        this.nextDrawTimerTask.cancel(true);
                    }
                    this.pickWinner();
                }
            }
        }
    }

    private void removePlayerFromPot(String username) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Player lost: " + username + ". Removing from pot."));
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
    private void endGame(String winner) {
        block12: {
            block11: {
                try {
                    try {
                        Pot localPot;
                        if (this.getGameState() != BotData.BotStateEnum.PLAYING) {
                            Object var5_2 = null;
                            this.resetGame(false);
                            this.updateLastActivityTime();
                            break block11;
                        }
                        if (this.nextDrawTimerTask != null && !this.nextDrawTimerTask.isDone() && !this.nextDrawTimerTask.isCancelled()) {
                            log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: Pending timer task to cancel in endGame() "));
                            this.nextDrawTimerTask.cancel(true);
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
                        if (winner != null) {
                            this.sendChannelMessageAndPopUp(this.amountJoinPot > 0.0 ? this.createMessage("GAME_OVER_PAID", winner) : this.createMessage("GAME_OVER_FREE", winner));
                            this.logMostWins(winner, this.winnings);
                            this.incrementMostWins(Leaderboard.Type.LOW_CARD_MOST_WINS, winner);
                        }
                        break block12;
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
            this.revertLimitInCache(this.playerHands.keySet());
        }
        this.playerHands.clear();
        this.tiebreakerHands.clear();
        this.isTiebreaker = false;
        this.lowestHandAlreadyLeft = null;
        this.nextDrawTimerTask = null;
        this.currentRoundNumber = 0;
        this.isRoundStarted = false;
        this.gameStarter = null;
        this.pot = null;
        this.amountJoinPot = this.amountOriginalJoinPot;
        this.setGameState(BotData.BotStateEnum.NO_GAME);
    }

    protected String createMessage(String messageKey) {
        return this.createMessage(messageKey, null, null, null);
    }

    String createMessage(String messageKey, String username) {
        return this.createMessage(messageKey, username, null, null);
    }

    private String createMessage(String messageKey, String username, Card card) {
        return this.createMessage(messageKey, username, card, null);
    }

    private String createMessage(String messageKey, String player, Card card, String errorInput) {
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
            messageToSend = messageToSend.replaceAll("TIMER_DRAW", "" + this.timeToDraw);
            messageToSend = messageToSend.replaceAll("TIMER_ROUND", "" + this.timeToNewRound);
            messageToSend = messageToSend.replaceAll("CMD_DRAW", COMMAND_DRAW);
            messageToSend = messageToSend.replaceAll("CMD_NO", "!n");
            messageToSend = messageToSend.replaceAll("CMD_JOIN", "!j");
            messageToSend = messageToSend.replaceAll("CMD_START", "!start");
            messageToSend = messageToSend.replaceAll("MINPLAYERS", this.minPlayers + "");
            if (player != null) {
                messageToSend = messageToSend.replaceAll("PLAYER", player);
                messageToSend = messageToSend.replaceAll("LEADER", player);
            }
            if (card != null) {
                messageToSend = messageToSend.replaceAll("CARD", card.toEmoticonHotkey());
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

    private String stringifyPlayerList(Map<String, Hand> players) {
        StringBuilder playerList = new StringBuilder();
        Iterator<String> iterator = players.keySet().iterator();
        while (iterator.hasNext()) {
            playerList.append((Object)iterator.next()).append(", ");
        }
        String playerListString = playerList.toString();
        return playerListString.endsWith(", ") ? playerListString.substring(0, playerListString.length() - 2) : playerListString;
    }

    private synchronized void draw(String username, String cardToDraw, boolean auto) {
        Map<String, Hand> currentRoundHands = this.isTiebreaker ? this.tiebreakerHands : this.playerHands;
        Hand hand = currentRoundHands.get(username);
        if (hand != null) {
            if (hand.card == null) {
                Card card = this.deck.dealCard(cardToDraw);
                hand.setCard(card);
                this.sendChannelMessage(this.createMessage(auto ? "AUTO_DRAW" : "PLAYER_DRAWS", username, card));
                if (!auto) {
                    for (Hand currentHand : currentRoundHands.values()) {
                        if (currentHand.getCard() != null) continue;
                        return;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"Looks like everyone has drawn. Let's tally!");
                    }
                    if (this.nextDrawTimerTask != null && !this.nextDrawTimerTask.isDone() && !this.nextDrawTimerTask.isCancelled()) {
                        this.nextDrawTimerTask.cancel(true);
                    }
                    this.tallyDraws();
                }
            } else if (!auto) {
                this.sendMessage(this.createMessage("ALREADY_DRAWN", username), username);
            } else {
                log.warn((Object)("Auto draw requested for player: " + username + ". But they already seem to have drawn a card!"));
            }
        }
    }

    private synchronized void newRound() {
        if (this.playerHands.size() > 1) {
            this.isRoundStarted = true;
            log.debug((Object)("Time is " + new Date()));
            ++this.currentRoundNumber;
            this.deck = new Deck();
            this.resetHands(this.tiebreakerHands);
            this.resetHands(this.playerHands);
            this.lowestHandAlreadyLeft = null;
            this.sendChannelMessage(this.createMessage("PLAYERS_TURN"));
            this.executor.schedule(new TimedPickWinnerTask(this, this.currentRoundNumber), this.timeToDraw, TimeUnit.SECONDS);
        }
    }

    private void tallyDraws() {
        Map<String, Hand> currentHands = this.isTiebreaker ? this.tiebreakerHands : this.playerHands;
        ArrayList<Hand> lowestHands = new ArrayList<Hand>();
        if (currentHands.size() > 1) {
            for (Hand hand : currentHands.values()) {
                if (hand.getCard() == null) {
                    this.draw(hand.getPlayer(), "", true);
                }
                if (this.lowestHandAlreadyLeft != null && hand.compareTo(this.lowestHandAlreadyLeft) >= 0) continue;
                if (lowestHands.size() == 0) {
                    lowestHands.add(hand);
                    continue;
                }
                if (hand.compareTo(lowestHands.get(0)) < 0) {
                    lowestHands.clear();
                    lowestHands.add(hand);
                    continue;
                }
                if (hand.compareTo(lowestHands.get(0)) != 0) continue;
                lowestHands.add(hand);
            }
        }
        if (lowestHands.size() == 0) {
            this.tiebreakerHands.clear();
        } else if (lowestHands.size() == 1) {
            Hand lowestHand = (Hand)lowestHands.get(0);
            this.playerHands.remove(lowestHand.player);
            this.removePlayerFromPot(lowestHand.player);
            this.sendChannelMessage(this.createMessage(this.isTiebreaker ? "PLAYER_TIEBREAK_LOWCARD" : "PLAYER_LOWCARD", lowestHand.player, lowestHand.card));
            this.tiebreakerHands.clear();
        } else {
            this.tiebreakerHands.clear();
            for (Hand hand : lowestHands) {
                this.tiebreakerHands.put(hand.player, hand);
            }
        }
        if (this.playerHands.size() < this.minPlayers) {
            this.pickWinner();
        } else {
            this.isRoundStarted = false;
            boolean bl = this.isTiebreaker = this.tiebreakerHands.size() > 0;
            if (this.isTiebreaker) {
                this.sendChannelMessage(this.createMessage("TIED_PLAYERS_LEFT") + "(" + this.tiebreakerHands.size() + "): " + this.stringifyPlayerList(this.tiebreakerHands));
                this.sendChannelMessage(this.createMessage("TIEBREAKER_ROUND"));
            } else {
                this.sendChannelMessage(this.createMessage("ALL_PLAYERS_LEFT") + "(" + this.playerHands.size() + "): " + this.stringifyPlayerList(this.playerHands));
                this.sendChannelMessage(this.createMessage("NEXT_ROUND"));
            }
            this.nextDrawTimerTask = this.executor.schedule(new TimedNewRoundTask(this, this.currentRoundNumber), this.timeToNewRound, TimeUnit.SECONDS);
        }
    }

    private void resetHands(Map<String, Hand> currentRoundHands) {
        for (String player : currentRoundHands.keySet()) {
            Hand hand = currentRoundHands.get(player);
            hand.setCard(null);
        }
    }

    private void pickWinner() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: " + "Picking winner: "));
        }
        Iterator<String> iterator = this.playerHands.keySet().iterator();
        String winner = null;
        if (iterator.hasNext()) {
            winner = iterator.next();
        }
        this.endGame(winner);
    }

    class Deck {
        private List<Card> deck = Card.newShuffledDeck();

        public Card dealCard() {
            return this.dealCard("");
        }

        public Card dealCard(String cardToDeal) {
            if (this.deck.isEmpty()) {
                this.deck = Card.newShuffledDeck();
                log.warn((Object)"Should not be happening! Deck ran out in the middle of a round. Resetting deck...");
            }
            if ((cardToDeal = cardToDeal.trim()).length() == 2) {
                Card.Rank rank = Card.Rank.fromChar(cardToDeal.charAt(0));
                Card.Suit suit = Card.Suit.fromChar(cardToDeal.toUpperCase().charAt(1));
                if (rank != null && suit != null) {
                    Iterator<Card> i = this.deck.iterator();
                    while (i.hasNext()) {
                        Card card = i.next();
                        if (card.rank() != rank || card.suit() != suit) continue;
                        i.remove();
                        return card;
                    }
                }
            }
            return this.deck.remove(0);
        }
    }

    class Hand
    implements Comparable {
        private String player;
        private Card card;

        public Hand(String player) {
            this.player = player;
        }

        public String getPlayer() {
            return this.player;
        }

        public Card getCard() {
            return this.card;
        }

        public void setCard(Card card) {
            this.card = card;
        }

        public int compareTo(Object obj) {
            Hand compareHand = (Hand)obj;
            return this.getCard().compareTo(compareHand.getCard());
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Hand)) {
                return false;
            }
            Hand compareHand = (Hand)obj;
            return this.player.equals(compareHand.player) && this.card.equals(compareHand.card);
        }
    }

    class TimedPickWinnerTask
    implements Runnable {
        LowCard bot;
        int roundNumber;

        TimedPickWinnerTask(LowCard bot, int roundNumber) {
            this.bot = bot;
            this.roundNumber = roundNumber;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            LowCard lowCard = this.bot;
            synchronized (lowCard) {
                if (this.bot.getGameState() == BotData.BotStateEnum.PLAYING && this.bot.currentRoundNumber == this.roundNumber) {
                    this.bot.sendChannelMessage(this.bot.createMessage("TIME_UP"));
                    this.bot.tallyDraws();
                }
            }
        }
    }

    class TimedNewRoundTask
    implements Runnable {
        LowCard bot;
        int roundNumber;

        TimedNewRoundTask(LowCard bot, int roundNumber) {
            this.bot = bot;
            this.roundNumber = roundNumber;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            LowCard lowCard = this.bot;
            synchronized (lowCard) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("TimedNewRoundTask: currentRoundNumber = " + this.bot.currentRoundNumber + ", task roundNumber = " + this.roundNumber));
                }
                if (this.bot.getGameState() == BotData.BotStateEnum.PLAYING && this.bot.currentRoundNumber == this.roundNumber) {
                    LowCard.this.newRound();
                }
            }
        }
    }

    class StartPlay
    implements Runnable {
        LowCard bot;

        StartPlay(LowCard bot) {
            this.bot = bot;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            LowCard lowCard = this.bot;
            synchronized (lowCard) {
                try {
                    BotData.BotStateEnum gameState;
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"LowCardBot: starting play in StartPlay()");
                    }
                    if ((gameState = LowCard.this.getGameState()) == BotData.BotStateEnum.GAME_JOINING) {
                        LowCard.this.setGameState(BotData.BotStateEnum.GAME_JOIN_ENDED);
                        if (LowCard.this.getPlayers().size() < LowCard.this.minPlayers) {
                            LowCard.this.sendChannelMessage(LowCard.this.createMessage("JOIN_NO_MIN"));
                            if (log.isDebugEnabled()) {
                                log.debug((Object)("botInstanceID[" + LowCard.this.getInstanceID() + "]: Join ended. Not enough players."));
                            }
                            LowCard.this.resetGame(true);
                        } else {
                            HashSet copyOfPlayers = new HashSet();
                            copyOfPlayers.addAll(LowCard.this.playerHands.keySet());
                            if (gameState != BotData.BotStateEnum.NO_GAME) {
                                gameState = BotData.BotStateEnum.PLAYING;
                                try {
                                    if (LowCard.this.amountJoinPot > 0.0) {
                                        this.bot.pot = new Pot(this.bot);
                                        log.debug((Object)("Pot id[" + LowCard.this.pot.getPotID() + "] created for bot instanceID[" + LowCard.this.getInstanceID() + "]"));
                                        for (String player : copyOfPlayers) {
                                            try {
                                                LowCard.this.pot.enterPlayer(player, LowCard.this.amountJoinPot / 100.0, "USD");
                                                if (!log.isDebugEnabled()) continue;
                                                log.debug((Object)("botInstanceID[" + LowCard.this.getInstanceID() + "]: Entered into pot " + player + " = " + "USD" + " " + LowCard.this.amountJoinPot / 100.0));
                                            }
                                            catch (Exception e) {
                                                LowCard.this.playerHands.remove(player);
                                                log.warn((Object)("botInstanceID[" + LowCard.this.getInstanceID() + "]: Error charging player[" + player + "]"), (Throwable)e);
                                                LowCard.this.sendMessage(LowCard.this.createMessage("INSUFFICIENT_FUNDS_POT", player), player);
                                            }
                                        }
                                        if (LowCard.this.playerHands.size() < LowCard.this.minPlayers) {
                                            if (log.isDebugEnabled()) {
                                                log.debug((Object)("botInstanceID[" + LowCard.this.getInstanceID() + "]: Not enough valid players."));
                                            }
                                            this.cancelPot();
                                            LowCard.this.resetGame(true);
                                            return;
                                        }
                                    }
                                    LowCard.this.incrementGamesPlayed(Leaderboard.Type.LOW_CARD_GAMES_PLAYED, LowCard.this.playerHands.keySet());
                                    LowCard.this.logGamesPlayed(LowCard.this.playerHands.size(), LowCard.this.playerHands.keySet(), LowCard.this.amountJoinPot);
                                    LowCard.this.sendChannelMessage(LowCard.this.createMessage("GAME_STARTED_NOTE"));
                                    LowCard.this.setGameState(BotData.BotStateEnum.PLAYING);
                                    LowCard.this.newRound();
                                }
                                catch (Exception e) {
                                    log.error((Object)("Error creating pot for botInstanceID[" + LowCard.this.getInstanceID() + "]."), (Throwable)e);
                                    LowCard.this.setGameState(BotData.BotStateEnum.NO_GAME);
                                    LowCard.this.sendChannelMessage(LowCard.this.createMessage("GAME_CANCELED"));
                                }
                            } else {
                                this.cancelPot();
                                LowCard.this.resetGame(true);
                                if (log.isDebugEnabled()) {
                                    log.debug((Object)("botInstanceID[" + LowCard.this.getInstanceID() + "]: Billing error. Game canceled. No charges."));
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    log.error((Object)"Unexpected exception caught in startPlay.run()", (Throwable)e);
                    this.cancelPot();
                    LowCard.this.resetGame(true);
                }
            }
        }

        private void cancelPot() {
            try {
                if (LowCard.this.pot != null) {
                    LowCard.this.pot.cancel();
                }
            }
            catch (Exception e) {
                log.error((Object)("Error canceling pot for botInstanceID[" + LowCard.this.getInstanceID() + "]."), (Throwable)e);
            }
            LowCard.this.sendChannelMessage(LowCard.this.createMessage("GAME_CANCELED"));
        }
    }

    class StartGame
    implements Runnable {
        LowCard bot;

        StartGame(LowCard bot) {
            this.bot = bot;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            LowCard lowCard = this.bot;
            synchronized (lowCard) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("botInstanceID[" + LowCard.this.getInstanceID() + "]: in StartGame() "));
                }
                BotData.BotStateEnum gameState = null;
                gameState = LowCard.this.getGameState();
                if (gameState == BotData.BotStateEnum.GAME_STARTING) {
                    LowCard.this.setGameState(BotData.BotStateEnum.GAME_STARTED);
                    LowCard.this.addPlayer(LowCard.this.gameStarter);
                    if (LowCard.this.timeToJoinGame > 0L) {
                        LowCard.this.setGameState(BotData.BotStateEnum.GAME_JOINING);
                        LowCard.this.sendChannelMessage(LowCard.this.amountJoinPot > 0.0 ? LowCard.this.createMessage("GAME_JOIN_PAID") : LowCard.this.createMessage("GAME_JOIN_FREE"));
                        if (log.isDebugEnabled()) {
                            log.debug((Object)"LowCardBot: starting timer for StartPlay()");
                        }
                        LowCard.this.executor.schedule(new StartPlay(this.bot), LowCard.this.timeToJoinGame, TimeUnit.SECONDS);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("botInstanceID[" + LowCard.this.getInstanceID() + "]: scheduled to start play. Awaiting join.. "));
                        }
                    }
                }
            }
        }
    }
}

