/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot.migbot.rockpaperscissors;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.bot.Pot;
import com.projectgoth.fusion.botservice.bot.migbot.rockpaperscissors.Hand;
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
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class RockPaperScissors
extends Bot {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RockPaperScissors.class));
    private int minPlayers = 2;
    private int maxPlayers = 5;
    private long waitForPlayerInterval = 30000L;
    private long countDownInterval = 10000L;
    private long idleInterval = 1800000L;
    private double minCostToJoinGame = 0.05;
    private Map<String, Hand> playerHands = new HashMap<String, Hand>();
    private BotData.BotStateEnum state = BotData.BotStateEnum.NO_GAME;
    private int round;
    private double costToJoinGame;
    private long timeLastGameFinished = System.currentTimeMillis();

    public RockPaperScissors(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDAO) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDAO);
        this.minPlayers = this.getIntParameter("MinPlayers", this.minPlayers);
        this.maxPlayers = this.getIntParameter("MaxPlayers", this.maxPlayers);
        this.waitForPlayerInterval = this.getLongParameter("WaitForPlayerInterval", this.waitForPlayerInterval);
        this.countDownInterval = this.getLongParameter("CountDownInterval", this.countDownInterval);
        this.idleInterval = this.getLongParameter("IdleInterval", this.idleInterval);
        this.minCostToJoinGame = this.getDoubleParameter("MinCostToJoinGame", this.minCostToJoinGame);
        this.sendChannelMessage("Bot RockPaperScissors added to the room. Enter !start to start a game. " + this.minCostToJoinGame + " " + "USD" + ". For custom entry, !start <entry_amount>");
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
        RockPaperScissors rockPaperScissors = this;
        synchronized (rockPaperScissors) {
            this.endGame(true);
        }
    }

    public void onUserJoinChannel(String username) {
        switch (this.state) {
            case NO_GAME: {
                this.sendMessage("Play Rock, Paper, Scissors. Enter !start to start a game. " + this.minCostToJoinGame + " " + "USD" + ". For custom entry, !start <entry_amount>", username);
                break;
            }
            case GAME_JOINING: {
                this.sendMessage("Play Rock, Paper, Scissors. Enter !j to join the game. " + this.costToJoinGame + " " + "USD", username);
                break;
            }
            case PLAYING: {
                this.sendMessage("Rock, Paper, Scissors is on now. Get ready for next game", username);
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onUserLeaveChannel(String username) {
        RockPaperScissors rockPaperScissors = this;
        synchronized (rockPaperScissors) {
            if (this.playerHands.remove(username) != null && this.state != BotData.BotStateEnum.NO_GAME) {
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
        } else if ("!r".equals(messageText)) {
            this.pickHand(username, Hand.ROCK);
        } else if ("!p".equals(messageText)) {
            this.pickHand(username, Hand.PAPER);
        } else if ("!s".equals(messageText)) {
            this.pickHand(username, Hand.SCISSORS);
        } else {
            this.sendMessage(messageText + " is not a valid command", username);
        }
    }

    private synchronized void startNewGame(String username, String messageText) {
        switch (this.state) {
            case NO_GAME: {
                String[] params = messageText.split(" ");
                try {
                    double d = this.costToJoinGame = params.length > 1 ? Double.parseDouble(params[1]) / 100.0 : this.minCostToJoinGame;
                    if (this.costToJoinGame < this.minCostToJoinGame) {
                        this.sendMessage("Minimum amount to start a game is " + this.minCostToJoinGame + " " + "USD", username);
                        return;
                    }
                    if (!this.userCanAffordToEnterPot(username, this.costToJoinGame, true)) {
                        return;
                    }
                }
                catch (NumberFormatException e) {
                    this.sendMessage(params[1] + " is not a valid amount", username);
                    return;
                }
                this.round = 1;
                this.playerHands.clear();
                this.playerHands.put(username, Hand.CLOSED);
                this.pot = null;
                this.sendChannelMessage(username + " started a new game");
                this.waitForMorePlayers();
                break;
            }
            case GAME_JOINING: {
                this.sendMessage("Enter !j to join the game. " + this.costToJoinGame + " " + "USD", username);
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
                this.sendMessage("Enter !start to start a game. " + this.minCostToJoinGame + " " + "USD" + ". For custom entry, !start <entry_amount>", username);
                break;
            }
            case GAME_JOINING: {
                if (this.playerHands.containsKey(username)) {
                    this.sendMessage("You have already joined the game. Please wait for the game to start", username);
                }
                if (this.playerHands.size() >= this.maxPlayers) {
                    this.sendMessage("Game is currently full. Please wait for next game", username);
                    break;
                }
                if (!this.userCanAffordToEnterPot(username, this.costToJoinGame, true) || this.playerHands.put(username, Hand.CLOSED) != null) break;
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

    private synchronized void pickHand(String username, Hand hand) {
        switch (this.state) {
            case NO_GAME: {
                this.sendMessage("Enter !start to start a game", username);
                break;
            }
            case GAME_JOINING: {
                this.sendMessage("Game starting soon! Please wait", username);
                break;
            }
            case PLAYING: {
                Hand existingHand = this.playerHands.get(username);
                if (existingHand == null) {
                    this.sendMessage("A game is currently in progress. Please wait for next game", username);
                    break;
                }
                if (existingHand == Hand.CLOSED) {
                    this.playerHands.put(username, hand);
                    this.sendMessage("You have picked " + hand.getEmoticonKey(), username);
                    break;
                }
                this.sendMessage("You have already picked " + existingHand.getEmoticonKey(), username);
                break;
            }
        }
    }

    private void waitForMorePlayers() {
        this.sendChannelMessage("Waiting for more players. Enter !j to join the game. " + this.costToJoinGame + " " + "USD");
        this.state = BotData.BotStateEnum.GAME_JOINING;
        this.executor.schedule(new Runnable(){

            public void run() {
                RockPaperScissors.this.chargeAndCountPlayers();
            }
        }, this.waitForPlayerInterval, TimeUnit.MILLISECONDS);
    }

    private synchronized void chargeAndCountPlayers() {
        try {
            if (this.costToJoinGame > 0.0) {
                if (this.playerHands.size() < this.minPlayers) {
                    this.endGame(true);
                    this.sendChannelMessage("Not enough players joined the game. Enter !start to start a new game");
                    return;
                }
                this.pot = new Pot(this);
                Iterator<String> i = this.playerHands.keySet().iterator();
                while (i.hasNext()) {
                    String player = i.next();
                    try {
                        this.pot.enterPlayer(player, this.costToJoinGame, "USD");
                    }
                    catch (Exception e) {
                        i.remove();
                        this.sendMessage("Unable to join you to the game " + ExceptionHelper.getRawRootMessage(e), player);
                    }
                }
            }
            if (this.playerHands.size() < this.minPlayers) {
                this.endGame(true);
                this.sendChannelMessage("Not enough players joined the game. Enter !start to start a new game");
            } else {
                log.info((Object)("New game started in " + this.channel));
                this.countDown();
            }
        }
        catch (Exception e) {
            log.error((Object)"Unexpected exception occured in chargeAndCountPlayers()", (Throwable)e);
            this.endGame(true);
            this.sendChannelMessage("Unable to start the game. " + ExceptionHelper.getRawRootMessage(e));
        }
    }

    private synchronized void countDown() {
        this.sendChannelMessage("Round " + this.round++ + ". Counting down... Enter !r " + Hand.ROCK.getEmoticonKey() + " or !p " + Hand.PAPER.getEmoticonKey() + " or !s " + Hand.SCISSORS.getEmoticonKey() + ". " + this.countDownInterval / 1000L + " seconds");
        this.state = BotData.BotStateEnum.PLAYING;
        this.executor.schedule(new Runnable(){

            public void run() {
                RockPaperScissors.this.pickWinner();
            }
        }, this.countDownInterval, TimeUnit.MILLISECONDS);
    }

    private void pickWinner() {
        LinkedList<String> losers;
        LinkedList<String> rocks = new LinkedList<String>();
        LinkedList<String> papers = new LinkedList<String>();
        LinkedList<String> scissorss = new LinkedList<String>();
        for (String player : this.playerHands.keySet()) {
            Hand hand = this.playerHands.get(player);
            if (hand == Hand.CLOSED) {
                hand = Hand.random();
                this.sendMessage("You did not pick a hand. Bot picked " + hand.getEmoticonKey() + " for you", player);
            }
            switch (hand) {
                case ROCK: {
                    rocks.add(player);
                    break;
                }
                case PAPER: {
                    papers.add(player);
                    break;
                }
                case SCISSORS: {
                    scissorss.add(player);
                }
            }
            this.playerHands.put(player, Hand.CLOSED);
        }
        HashMap<Hand, LinkedList<String>> choiceMap = new HashMap<Hand, LinkedList<String>>();
        choiceMap.put(Hand.ROCK, rocks);
        choiceMap.put(Hand.PAPER, papers);
        choiceMap.put(Hand.SCISSORS, scissorss);
        for (Hand hand : choiceMap.keySet()) {
            List l = (List)choiceMap.get((Object)hand);
            if (l.isEmpty()) continue;
            String round_status = (l.size() == 1 ? (String)l.get(0) : StringUtil.join(l.subList(0, l.size() - 1), ",")) + (l.size() > 1 ? " and " + (String)l.get(l.size() - 1) : "");
            round_status = round_status + " picked " + hand.getEmoticonKey() + ".";
            this.sendChannelMessage(round_status);
        }
        if (rocks.size() > 0 && scissorss.size() > 0 && papers.size() == 0) {
            this.sendChannelMessage(Hand.ROCK.getEmoticonKey() + " wins");
            losers = scissorss;
        } else if (scissorss.size() > 0 && papers.size() > 0 && rocks.size() == 0) {
            this.sendChannelMessage(Hand.SCISSORS.getEmoticonKey() + " wins");
            losers = papers;
        } else if (papers.size() > 0 && rocks.size() > 0 && scissorss.size() == 0) {
            this.sendChannelMessage(Hand.PAPER.getEmoticonKey() + " wins");
            losers = rocks;
        } else {
            this.sendChannelMessage("Draw");
            losers = Collections.EMPTY_LIST;
        }
        for (String loser : losers) {
            this.playerHands.remove(loser);
            if (this.pot == null) continue;
            try {
                this.pot.removePlayer(loser);
            }
            catch (Exception e) {
                log.error((Object)("Unable to remove " + loser + " from pot " + this.pot.getPotID()), (Throwable)e);
            }
        }
        if (this.playerHands.size() == 0) {
            this.endGame(false);
            this.sendChannelMessage("No more players left in the game. Enter !start to start a game. " + this.minCostToJoinGame + " " + "USD" + ". For custom entry, !start <entry_amount>");
        } else if (this.playerHands.size() == 1) {
            double payout = this.endGame(false);
            if (payout < 0.0) {
                this.sendChannelMessageAndPopUp(SystemProperty.get(SystemPropertyEntities.BOT.PAYOUT_FAILURE_MESSAGE));
            } else {
                this.sendChannelMessageAndPopUp(this.playerHands.keySet().iterator().next() + " won " + new DecimalFormat("0.00").format(payout) + " " + "USD" + "!");
            }
            this.sendChannelMessage("Enter !start to start a game. " + this.minCostToJoinGame + " " + "USD" + ". For custom entry, !start <entry_amount>");
        } else {
            this.sendChannelMessage(this.playerHands.size() + " players left in the game [" + StringUtil.join(this.playerHands.keySet(), ", ") + "]");
            this.countDown();
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
        }
        this.timeLastGameFinished = System.currentTimeMillis();
        this.state = BotData.BotStateEnum.NO_GAME;
        return payout;
    }
}

