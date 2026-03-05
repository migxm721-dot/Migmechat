/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.botservice.bot.migbot.headsortails;

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
import com.projectgoth.fusion.slice.BotChannelPrx;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

public class HeadsOrTails
extends Bot {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(HeadsOrTails.class));
    private int minPlayers = 2;
    private long waitForPlayerInterval = 30000L;
    private long placeBetInterval = 10000L;
    private long idleInterval = 1800000L;
    public static final String MIN_POT_ENTRY = "minPotEntry";
    private double minPotEntry = 0.03;
    private double costToJoinGame;
    DecimalFormat df = new DecimalFormat("0.00");
    private int maxTiedRounds = 5;
    private int numTiedRounds = 0;
    private Map<String, Bet> playerBets = new HashMap<String, Bet>();
    private BotData.BotStateEnum state = BotData.BotStateEnum.NO_GAME;
    private int round;
    private long timeLastGameFinished = System.currentTimeMillis();
    private ScheduledFuture waitForPlayersTimer;
    private ScheduledFuture tossCoinTimer;

    public HeadsOrTails(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDAO) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDAO);
        this.minPlayers = this.getIntParameter("MinPlayers", this.minPlayers);
        this.waitForPlayerInterval = this.getLongParameter("WaitForPlayerInterval", this.waitForPlayerInterval);
        this.placeBetInterval = this.getLongParameter("PlaceBetInterval", this.placeBetInterval);
        this.idleInterval = this.getLongParameter("IdleInterval", this.idleInterval);
        this.minPotEntry = this.getDoubleParameter(MIN_POT_ENTRY, this.minPotEntry);
        this.sendChannelMessage("Bot HeadsOrTails added to the room. !start to start a game of HeadsOrTails. Cost: USD 0.00. For custom entry, !start <entry_amount>");
    }

    public boolean isIdle() {
        return this.state == BotData.BotStateEnum.NO_GAME && System.currentTimeMillis() - this.timeLastGameFinished > this.idleInterval;
    }

    public boolean canBeStoppedNow() {
        return (this.state != BotData.BotStateEnum.PLAYING || this.pot == null) && this.state != BotData.BotStateEnum.GAME_JOINING && this.state != BotData.BotStateEnum.GAME_STARTING;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopBot() {
        HeadsOrTails headsOrTails = this;
        synchronized (headsOrTails) {
            if (this.waitForPlayersTimer != null) {
                this.waitForPlayersTimer.cancel(true);
                this.waitForPlayersTimer = null;
            }
            if (this.tossCoinTimer != null) {
                this.tossCoinTimer.cancel(true);
                this.tossCoinTimer = null;
            }
            this.endGame(true);
        }
    }

    public void onUserJoinChannel(String username) {
        switch (this.state) {
            case NO_GAME: {
                this.sendMessage("Play Heads or Tails. !start to start a game of HeadsOrTails. Cost: USD 0.00. For custom entry, !start <entry_amount>", username);
                break;
            }
            case GAME_JOINING: {
                this.sendMessage("Play Heads or Tails. Enter !j to join the game. Cost " + this.df.format(this.costToJoinGame) + " " + "USD", username);
                break;
            }
            case PLAYING: {
                this.sendMessage("Heads or Tails is on now. Get ready for next game", username);
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onUserLeaveChannel(String username) {
        HeadsOrTails headsOrTails = this;
        synchronized (headsOrTails) {
            if (this.playerBets.remove(username) != null && this.state != BotData.BotStateEnum.NO_GAME) {
                this.sendChannelMessage(username + " left the game");
                if (this.pot != null) {
                    try {
                        this.pot.removePlayer(username);
                    }
                    catch (Exception e) {
                        log.error((Object)("Unable to remove " + username + " from pot " + this.pot.getPotID()), (Throwable)e);
                    }
                }
            }
        }
    }

    public void onMessage(String username, String messageText, long receivedTimestamp) {
        if ((messageText = messageText.toLowerCase().trim()).startsWith("!start")) {
            this.startNewGame(username, messageText);
        } else if ("!j".equals(messageText)) {
            this.joinGame(username);
        } else if ("!h".equals(messageText)) {
            this.placeBet(username, Bet.HEAD);
        } else if ("!t".equals(messageText)) {
            this.placeBet(username, Bet.TAIL);
        } else {
            this.sendMessage(messageText + " is not a valid command", username);
        }
    }

    private synchronized void startNewGame(String username, String messageText) {
        switch (this.state) {
            case NO_GAME: {
                String[] params = messageText.split(" ");
                double customPotEntry = 0.0;
                if (params.length > 1) {
                    try {
                        customPotEntry = (double)Integer.parseInt(params[1]) / 100.0;
                        if (customPotEntry < this.minPotEntry && customPotEntry != 0.0) {
                            this.sendMessage("[PVT] " + username + ": Invalid amount. Minimum amount is " + this.df.format(this.minPotEntry) + " " + "USD", username);
                            return;
                        }
                    }
                    catch (NumberFormatException e) {
                        this.sendMessage("[PVT] " + username + ": Invalid amount", username);
                        return;
                    }
                }
                if (customPotEntry > 0.0) {
                    if (!this.userCanAffordToEnterPot(username, customPotEntry, true)) {
                        return;
                    }
                    try {
                        this.pot = new Pot(this);
                        this.pot.enterPlayer(username, customPotEntry, "USD");
                    }
                    catch (Exception e) {
                        this.sendMessage("Unable to start the game: " + ExceptionHelper.getRawRootMessage(e), username);
                        return;
                    }
                    this.costToJoinGame = customPotEntry;
                }
                this.round = 1;
                this.playerBets.clear();
                this.playerBets.put(username, Bet.UNKNOWN);
                this.sendChannelMessage(username + " started a new game");
                this.waitForMorePlayers();
                break;
            }
            case GAME_JOINING: {
                this.sendMessage("Enter !j to join the game. Cost " + this.df.format(this.costToJoinGame) + " " + "USD", username);
                break;
            }
            case PLAYING: {
                this.sendMessage("A game is currently in progress. Please wait for next game", username);
                break;
            }
        }
    }

    private synchronized void joinGame(String username) {
        switch (this.state) {
            case NO_GAME: {
                this.sendMessage("!start to start a game of HeadsOrTails. Cost: USD 0.00. For custom entry, !start <entry_amount>", username);
                break;
            }
            case GAME_JOINING: {
                if (this.playerBets.containsKey(username)) {
                    this.sendMessage("You have already joined the game. Please wait for the game to start", username);
                    return;
                }
                if (this.costToJoinGame > 0.0 && this.pot != null) {
                    if (!this.userCanAffordToEnterPot(username, this.costToJoinGame, true)) {
                        return;
                    }
                    try {
                        this.pot.enterPlayer(username, this.costToJoinGame, "USD");
                    }
                    catch (Exception e) {
                        this.sendMessage("You could not be added to the game: " + ExceptionHelper.getRawRootMessage(e), username);
                        log.error((Object)("Unable to add player to game (" + username + "): " + e.getMessage()));
                        return;
                    }
                }
                this.playerBets.put(username, Bet.UNKNOWN);
                log.info((Object)(username + " joined the game"));
                this.sendChannelMessage(username + " joined the game");
                break;
            }
            case PLAYING: {
                this.sendMessage("A game is currently in progress. Please wait for next game", username);
                break;
            }
        }
    }

    private synchronized void placeBet(String username, Bet bet) {
        switch (this.state) {
            case NO_GAME: {
                this.sendMessage("!start to start a game of HeadsOrTails. Cost: USD 0.00. For custom entry, !start <entry_amount>", username);
                break;
            }
            case GAME_JOINING: {
                this.sendMessage("Game starting soon! Please wait", username);
                break;
            }
            case PLAYING: {
                Bet existingBet = this.playerBets.get(username);
                if (existingBet == null) {
                    this.sendMessage("A game is currently in progress. Please wait for next game", username);
                    break;
                }
                if (existingBet == Bet.UNKNOWN) {
                    this.playerBets.put(username, bet);
                    this.sendMessage("You have chosen " + bet.getEmoticonKey(), username);
                    break;
                }
                this.sendMessage("You have already chosen " + existingBet.getEmoticonKey(), username);
                break;
            }
        }
    }

    private void waitForMorePlayers() {
        this.sendChannelMessage("Waiting for more players. Enter !j to join the game. Cost " + this.df.format(this.costToJoinGame) + " " + "USD");
        this.state = BotData.BotStateEnum.GAME_JOINING;
        this.waitForPlayersTimer = this.executor.schedule(new Runnable(){

            public void run() {
                HeadsOrTails.this.chargeAndCountPlayers();
            }
        }, this.waitForPlayerInterval, TimeUnit.MILLISECONDS);
    }

    private synchronized void chargeAndCountPlayers() {
        this.waitForPlayersTimer = null;
        try {
            if (this.playerBets.size() < this.minPlayers) {
                this.endGame(true);
                this.sendChannelMessage("Not enough players joined the game. Enter !start to start a new game");
            } else {
                log.info((Object)("New game started in " + this.channel));
                this.waitForPlacingBets();
            }
        }
        catch (Exception e) {
            log.error((Object)"Unexpected exception occured in chargeAndCountPlayers()", (Throwable)e);
            this.endGame(true);
            this.sendChannelMessage("Unable to start the game. " + ExceptionHelper.getRawRootMessage(e));
        }
    }

    private synchronized void waitForPlacingBets() {
        String waitingForBets = "Round " + this.round++ + ". Tossing coin in " + this.placeBetInterval / 1000L + " seconds.";
        waitingForBets = waitingForBets + "!h for " + Bet.HEAD.getEmoticonKey() + " (Heads) or !t for " + Bet.TAIL.getEmoticonKey() + " (Tails) ";
        this.sendChannelMessage(waitingForBets);
        this.state = BotData.BotStateEnum.PLAYING;
        this.tossCoinTimer = this.executor.schedule(new Runnable(){

            public void run() {
                HeadsOrTails.this.tossCoin();
            }
        }, this.placeBetInterval, TimeUnit.MILLISECONDS);
    }

    private synchronized void tossCoin() {
        LinkedList<String> losers;
        String round_status;
        this.tossCoinTimer = null;
        SecureRandom random = new SecureRandom();
        LinkedList<String> heads = new LinkedList<String>();
        LinkedList<String> tails = new LinkedList<String>();
        for (String player : this.playerBets.keySet()) {
            Bet bet = this.playerBets.get(player);
            if (bet == Bet.UNKNOWN) {
                bet = random.nextBoolean() ? Bet.HEAD : Bet.TAIL;
                this.sendMessage("You did not make a choice. Bot chose " + bet.getEmoticonKey() + " for you", player);
            }
            if (bet == Bet.HEAD) {
                heads.add(player);
            } else {
                tails.add(player);
            }
            this.playerBets.put(player, Bet.UNKNOWN);
        }
        if (!heads.isEmpty()) {
            round_status = (heads.size() == 1 ? (String)heads.get(0) : StringUtil.join(heads.subList(0, heads.size() - 1), ",")) + (heads.size() > 1 ? " and " + (String)heads.get(heads.size() - 1) : "");
            round_status = round_status + " picked " + Bet.HEAD.getEmoticonKey() + ".";
            this.sendChannelMessage(round_status);
        }
        if (!tails.isEmpty()) {
            round_status = (tails.size() == 1 ? (String)tails.get(0) : StringUtil.join(tails.subList(0, tails.size() - 1), ",")) + (tails.size() > 1 ? " and " + (String)tails.get(tails.size() - 1) : "");
            round_status = round_status + " picked " + Bet.TAIL.getEmoticonKey() + ".";
            this.sendChannelMessage(round_status);
        }
        if (random.nextBoolean()) {
            this.sendChannelMessage("Bot tossed " + Bet.HEAD.getEmoticonKey());
            losers = tails;
        } else {
            this.sendChannelMessage("Bot tossed " + Bet.TAIL.getEmoticonKey());
            losers = heads;
        }
        boolean tiedRound = false;
        if (losers.size() == 0 || losers.size() == this.playerBets.size()) {
            tiedRound = true;
        }
        if (!tiedRound) {
            for (String loser : losers) {
                this.playerBets.remove(loser);
                if (this.pot == null) continue;
                try {
                    this.pot.removePlayer(loser);
                }
                catch (Exception e) {
                    log.error((Object)("Unable to remove " + loser + " from pot " + this.pot.getPotID()), (Throwable)e);
                }
            }
        }
        if (this.playerBets.size() == 0) {
            this.endGame(false);
            this.sendChannelMessage("No more players left in the game. Enter !start to start a new game");
        } else if (this.playerBets.size() == 1) {
            double payout = this.endGame(false);
            if (payout < 0.0) {
                this.sendChannelMessageAndPopUp(SystemProperty.get(SystemPropertyEntities.BOT.PAYOUT_FAILURE_MESSAGE));
            } else if (payout > 0.0) {
                this.sendChannelMessageAndPopUp(this.playerBets.keySet().iterator().next() + " won " + this.df.format(payout) + " " + "USD" + "!");
            } else {
                this.sendChannelMessageAndPopUp(this.playerBets.keySet().iterator().next() + " won!");
            }
            this.sendChannelMessage("!start to start a game of HeadsOrTails. Cost: USD 0.00. For custom entry, !start <entry_amount>");
        } else {
            if (tiedRound) {
                ++this.numTiedRounds;
                if (this.numTiedRounds == this.maxTiedRounds) {
                    double payout = this.endGame(false);
                    String winners = StringUtils.collectionToDelimitedString(this.playerBets.keySet(), (String)", ");
                    this.sendChannelMessage("That was the final tie-breaker round. It's a draw! Remaining players split the prize");
                    if (payout > 0.0) {
                        this.sendChannelMessageAndPopUp(winners + " won " + this.df.format(payout) + " " + "USD" + "!");
                    } else {
                        this.sendChannelMessageAndPopUp(winners + " won!");
                    }
                    this.sendChannelMessage("!start to start a game of HeadsOrTails. Cost: USD 0.00. For custom entry, !start <entry_amount>");
                    return;
                }
            } else {
                this.numTiedRounds = 0;
            }
            this.sendChannelMessage(this.playerBets.size() + " players left in the game [" + StringUtil.join(this.playerBets.keySet(), ", ") + "]");
            this.waitForPlacingBets();
        }
    }

    private double endGame(boolean cancelPot) {
        double payout = 0.0;
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
            this.pot = null;
        }
        this.timeLastGameFinished = System.currentTimeMillis();
        this.state = BotData.BotStateEnum.NO_GAME;
        this.costToJoinGame = 0.0;
        this.numTiedRounds = 0;
        return payout;
    }

    public static void main(String[] args) {
        try {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
            BotData botData = new BotData();
            botData.setId(7L);
            HeadsOrTails hot = new HeadsOrTails(executor, null, botData, "EN", "dave", null);
            hot.waitForPlayerInterval = 1000L;
            hot.placeBetInterval = 1000L;
            hot.onMessage("maxpower", "!start", System.currentTimeMillis());
            hot.onMessage("dave", "!j", System.currentTimeMillis());
            Thread.sleep(20000L);
            executor.shutdown();
            executor.awaitTermination(60L, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum Bet {
        HEAD("(coin_head)"),
        TAIL("(coin_tail)"),
        UNKNOWN("(coin_unknown)");

        private String emoticonKey;

        private Bet(String emoticonKey) {
            this.emoticonKey = emoticonKey;
        }

        public String getEmoticonKey() {
            return this.emoticonKey;
        }
    }
}

