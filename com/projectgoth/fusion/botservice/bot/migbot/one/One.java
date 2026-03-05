/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.botservice.bot.migbot.one;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.bot.Pot;
import com.projectgoth.fusion.botservice.bot.migbot.one.Card;
import com.projectgoth.fusion.botservice.bot.migbot.one.Player;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ExceptionHelper;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class One
extends Bot {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(One.class));
    public static final String TIMER_JOIN_GAME = "timerJoinGame";
    public static final String TIMER_IDLE = "timerIdle";
    public static final String MIN_POT_ENTRY = "minPotEntry";
    public static final long IDLE_TIME_VALUE = 5L;
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;
    long timeToJoinGame = 90L;
    long timeAllowedToIdle = 30L;
    Date lastActivityTime;
    public static final int BLUE = 1;
    public static final int GREEN = 2;
    public static final int RED = 3;
    public static final int YELLOW = 4;
    public static final String EMOTICON_MARKER = "|";
    public static final String EMOTICON_HOTKEY_BLUE = "(uno_blue)";
    public static final String EMOTICON_HOTKEY_GREEN = "(uno_green)";
    public static final String EMOTICON_HOTKEY_RED = "(uno_red)";
    public static final String EMOTICON_HOTKEY_YELLOW = "(uno_yellow)";
    public static final int WILD = 10;
    public static final int DRAW_2 = 20;
    public static final int REVERSE = 21;
    public static final int SKIP = 22;
    public static final int WILD_DRAW_4 = 23;
    public static final int ANY = 99;
    public static final String STR_ANY = "*";
    public static final String STR_WILD = "w";
    public static final String STR_DRAW_2 = "d2";
    public static final String STR_REVERSE = "r";
    public static final String STR_SKIP = "s";
    public static final String STR_WILD_DRAW_4 = "wd4";
    public String CMD_START = "!start";
    public String CMD_JOIN = "!j";
    public String CMD_DEAL = "!deal";
    public String CMD_PLAY_CARD = "!p";
    public String CMD_DRAW = "!d";
    public String CMD_PASS = "!s";
    public String CMD_HAND = "!h";
    public String CMD_COUNT = "!c";
    public String CMD_HELP = "!help";
    private String CMD_RESET = "!reset";
    public Map<String, Boolean> playersNames;
    private BotData.BotStateEnum gameState = BotData.BotStateEnum.NO_GAME;
    public boolean inProgress = false;
    public boolean drawn = false;
    public String dealer = "";
    public Player nextPlayer = null;
    public List<Player> players = null;
    public List<Card> cards = null;
    public Card cardInPlay = null;
    public int wildColour = 10;
    public List<Card> discardedCards = null;
    private double minPotEntry = 0.03;
    private double costToJoinGame;
    DecimalFormat df = new DecimalFormat("0.00");
    private ScheduledFuture waitForPlayersTimer;

    public One(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDAO) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDAO);
        this.loadGameConfig();
        this.players = new ArrayList<Player>();
        this.playersNames = new HashMap<String, Boolean>();
        log.info((Object)("OneBot [" + this.instanceID + "] added to channel [" + this.channel + "]"));
        this.sendChannelMessage(this.createMessage("BOT_ADDED"));
        String message = this.createMessage("GAME_STATE_DEFAULT_AMOUNT");
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
    public synchronized void onMessage(String username, String messageText, long receivedTimestamp) {
        if (messageText.startsWith(this.CMD_START)) {
            if (this.getGameState() == BotData.BotStateEnum.NO_GAME) {
                try {
                    this.startGame(username, messageText);
                }
                catch (Exception e1) {
                    log.error((Object)"Error starting game", (Throwable)e1);
                }
            } else {
                this.sendGameCannotBeStartedMessage(username);
            }
        } else if (messageText.equalsIgnoreCase(this.CMD_JOIN)) {
            if (this.players.size() < 4) {
                if (!this.playersNames.containsKey(username)) {
                    if (this.gameState == BotData.BotStateEnum.GAME_JOINING) {
                        this.addPlayer(username);
                    } else {
                        this.sendMessage(this.createMessage("JOIN_ENDED", username), username);
                    }
                } else {
                    this.sendMessage(this.createMessage("ALREADY_IN_GAME", username), username);
                }
            } else {
                this.sendMessage(this.createMessage("MAX_REACHED", username), username);
            }
        } else if (messageText.equalsIgnoreCase(this.CMD_DEAL)) {
            this.dealGame(username);
        } else if (messageText.equalsIgnoreCase(this.CMD_RESET) && username.equals(this.gameStarter)) {
            this.reset(username);
        } else if (messageText.toLowerCase().startsWith(this.CMD_PLAY_CARD) && this.inProgress()) {
            if (this.isPlayersTurn(username)) {
                this.playCard(username, messageText);
            } else {
                this.sendMessage(this.createMessage("NOT_YOUR_TURN", username), username);
            }
        } else if (messageText.equalsIgnoreCase(this.CMD_HAND) && this.inProgress()) {
            this.sendHand(username);
        } else if (messageText.equalsIgnoreCase(this.CMD_COUNT) && this.inProgress()) {
            this.count();
        } else if (messageText.toLowerCase().startsWith(this.CMD_DRAW) && this.inProgress() && this.isPlayersTurn(username)) {
            this.draw(username);
        } else if (messageText.toLowerCase().startsWith(this.CMD_PASS) && this.inProgress() && this.isPlayersTurn(username)) {
            this.pass(username);
        }
    }

    public void updateScores(String nick, int newScore) {
        this.updateScores(nick, newScore, 0, 0);
    }

    public void updateScores(String nick, int newScore, int wins, int lose) {
    }

    @Override
    public void stopBot() {
        if (this.waitForPlayersTimer != null) {
            this.waitForPlayersTimer.cancel(true);
            this.waitForPlayersTimer = null;
        }
        if (this.pot != null) {
            try {
                this.pot.cancel();
            }
            catch (Exception e) {
                log.error((Object)("Unable to cancel pot " + this.pot.getPotID()), (Throwable)e);
            }
            this.pot = null;
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
            log.debug((Object)("Bot has been idle for " + minutes + (minutes == 1L ? " minute" : " minutes") + ". Marking as idle..."));
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
        this.timeAllowedToIdle = this.getLongParameter(TIMER_IDLE, 5L);
        this.minPotEntry = this.getDoubleParameter(MIN_POT_ENTRY, this.minPotEntry);
    }

    public void startGame(String username, String messageText) throws Exception {
        this.updateLastActivityTime();
        if (this.gameState.equals((Object)BotData.BotStateEnum.NO_GAME)) {
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
            this.setGameState(BotData.BotStateEnum.GAME_STARTING);
            this.gameStarter = username;
            this.executor.execute(new StartGame(this));
        } else {
            this.sendGameCannotBeStartedMessage(username);
        }
    }

    private void sendGameCannotBeStartedMessage(String username) {
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void onUserJoinChannel(String username) {
        String message = null;
        BotData.BotStateEnum botStateEnum = this.gameState;
        synchronized (botStateEnum) {
            switch (this.gameState.value()) {
                case 2: 
                case 5: {
                    message = this.createMessage("GAME_STATE_STARTED", username);
                    break;
                }
                default: {
                    message = this.createMessage("GAME_STATE_DEFAULT_AMOUNT");
                }
            }
        }
        this.sendMessage(message, username);
    }

    @Override
    public synchronized void onUserLeaveChannel(String username) {
        this.removePlayer(username);
    }

    public double endGame(boolean cancelPot, String winner) {
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
                    payout = this.pot.payout(winner, true);
                    payout = accountEJB.convertCurrency(payout, "AUD", "USD");
                }
                catch (Exception e) {
                    log.error((Object)("Unable to payout pot " + this.pot.getPotID()), (Throwable)e);
                    payout = -1.0;
                }
            }
            this.pot = null;
        }
        this.resetGame();
        this.updateLastActivityTime();
        this.sendChannelMessage(this.createMessage("GAME_STATE_DEFAULT_AMOUNT"));
        return payout;
    }

    private void resetGame() {
        if (this.pot != null) {
            try {
                this.pot.cancel();
            }
            catch (Exception e) {
                log.error((Object)("Unable to cancel pot " + this.pot.getPotID()), (Throwable)e);
            }
            this.pot = null;
        }
        this.gameStarter = null;
        this.costToJoinGame = 0.0;
        this.initGame(true);
        this.setGameState(BotData.BotStateEnum.NO_GAME);
    }

    protected String createMessage(String messageKey) {
        return this.createMessage(messageKey, null, null, null);
    }

    String createMessage(String messageKey, String player) {
        return this.createMessage(messageKey, player, null, null);
    }

    private String createMessage(String messageKey, String player, String player2) {
        return this.createMessage(messageKey, player, player2, null, null, null);
    }

    String createMessage(String messageKey, String player, Score currentPlayerScore, String scoreboard) {
        return this.createMessage(messageKey, player, null, currentPlayerScore, scoreboard, null);
    }

    private String createMessage(String messageKey, String player, String player2, Score currentPlayerScore, String scoreboard, String errorInput) {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Looking for messageKey: " + messageKey));
            }
            String messageToSend = (String)this.messages.get(messageKey);
            messageToSend = messageToSend.replaceAll("BOTNAME", this.botData.getDisplayName());
            messageToSend = messageToSend.replaceAll("MINPLAYERS", "2");
            if (scoreboard != null) {
                messageToSend = messageToSend.replaceAll("SCORES", scoreboard);
            }
            if (player != null) {
                messageToSend = messageToSend.replaceAll("PLAYER", player);
                if (currentPlayerScore != null) {
                    messageToSend = messageToSend.replaceAll("POINTS", currentPlayerScore.points + (currentPlayerScore.points == 1 ? " point" : " points"));
                }
            }
            if (player2 != null) {
                messageToSend = messageToSend.replaceAll("PLAYR2", player2);
            }
            messageToSend = messageToSend.replaceAll("TIMER_JOIN", this.timeToJoinGame + "");
            messageToSend = messageToSend.replaceAll("CURRENCY", "USD");
            messageToSend = messageToSend.replaceAll("DENOMINATION", "c");
            messageToSend = messageToSend.replaceAll("AMOUNT_START", "0");
            messageToSend = messageToSend.replaceAll("AMOUNT_JOIN", "0");
            messageToSend = messageToSend.replaceAll("CMD_START", "!start");
            messageToSend = messageToSend.replaceAll("CMD_NO", "!n");
            messageToSend = messageToSend.replaceAll("CMD_JOIN", "!j");
            messageToSend = messageToSend.replaceAll("CMD_DEAL", this.CMD_DEAL);
            messageToSend = messageToSend.replaceAll("CMD_DRAW", this.CMD_DRAW);
            messageToSend = messageToSend.replaceAll("CMD_PASS", this.CMD_PASS);
            messageToSend = messageToSend.replaceAll("CMD_HAND", this.CMD_HAND);
            messageToSend = messageToSend.replaceAll("CMD_COUNT", this.CMD_COUNT);
            messageToSend = messageToSend.replaceAll("CMD_PLAY", this.CMD_PLAY_CARD);
            messageToSend = messageToSend.replace("COST_TO_JOIN", this.df.format(this.costToJoinGame));
            if (StringUtils.hasLength((String)errorInput)) {
                messageToSend = messageToSend.replaceAll("ERROR_INPUT", errorInput);
            }
            return messageToSend;
        }
        catch (NullPointerException e) {
            log.error((Object)("Outgoing message could not be created, key = " + messageKey), (Throwable)e);
            return "";
        }
    }

    public void initGame(boolean initializePlayers) {
        int x;
        if (initializePlayers) {
            this.initializePlayerLists();
        }
        this.cards = new ArrayList<Card>();
        this.cardInPlay = null;
        this.discardedCards = new ArrayList<Card>();
        this.inProgress = false;
        this.nextPlayer = null;
        this.wildColour = 10;
        this.cards.add(new Card(1, 0));
        this.cards.add(new Card(2, 0));
        this.cards.add(new Card(3, 0));
        this.cards.add(new Card(4, 0));
        for (int y = 0; y <= 1; ++y) {
            for (int x2 = 1; x2 <= 9; ++x2) {
                this.cards.add(new Card(1, x2));
                this.cards.add(new Card(2, x2));
                this.cards.add(new Card(3, x2));
                this.cards.add(new Card(4, x2));
            }
        }
        for (x = 1; x <= 2; ++x) {
            this.cards.add(new Card(1, 20));
            this.cards.add(new Card(2, 20));
            this.cards.add(new Card(3, 20));
            this.cards.add(new Card(4, 20));
            this.cards.add(new Card(1, 21));
            this.cards.add(new Card(2, 21));
            this.cards.add(new Card(3, 21));
            this.cards.add(new Card(4, 21));
            this.cards.add(new Card(1, 22));
            this.cards.add(new Card(2, 22));
            this.cards.add(new Card(3, 22));
            this.cards.add(new Card(4, 22));
        }
        for (x = 1; x <= 4; ++x) {
            this.cards.add(new Card(10, 10));
            this.cards.add(new Card(10, 23));
        }
    }

    private void initializePlayerLists() {
        this.playersNames = new HashMap<String, Boolean>();
        this.players = new ArrayList<Player>();
        this.dealer = "";
    }

    public void redeal() {
        int x;
        this.wildColour = 10;
        this.cards.add(new Card(1, 0));
        this.cards.add(new Card(2, 0));
        this.cards.add(new Card(3, 0));
        this.cards.add(new Card(4, 0));
        for (int y = 0; y <= 1; ++y) {
            for (int x2 = 1; x2 <= 9; ++x2) {
                this.cards.add(new Card(1, x2));
                this.cards.add(new Card(2, x2));
                this.cards.add(new Card(3, x2));
                this.cards.add(new Card(4, x2));
            }
        }
        for (x = 1; x <= 2; ++x) {
            this.cards.add(new Card(1, 20));
            this.cards.add(new Card(2, 20));
            this.cards.add(new Card(3, 20));
            this.cards.add(new Card(4, 20));
            this.cards.add(new Card(1, 21));
            this.cards.add(new Card(2, 21));
            this.cards.add(new Card(3, 21));
            this.cards.add(new Card(4, 21));
            this.cards.add(new Card(1, 22));
            this.cards.add(new Card(2, 22));
            this.cards.add(new Card(3, 22));
            this.cards.add(new Card(4, 22));
        }
        for (x = 1; x <= 4; ++x) {
            this.cards.add(new Card(10, 10));
            this.cards.add(new Card(10, 23));
        }
        for (int i = 0; i < this.players.size(); ++i) {
            for (int ii = 0; ii < this.players.get(i).getCards().size(); ++ii) {
                this.cards.remove((Card)this.players.get(i).getCards().get(ii));
            }
        }
    }

    public boolean inProgress() {
        return this.inProgress;
    }

    public void noCardsLeft(String channel) {
        this.sendChannelMessage(this.createMessage("SHUFFLING_CARDS"));
        this.redeal();
    }

    public void countPlayersCards() {
        StringBuffer res = new StringBuffer(this.createMessage("CARD_COUNTS"));
        for (int i = 0; i < this.players.size(); ++i) {
            res.append(this.players.get(i).getName() + ": (" + this.players.get(i).cardCount() + ") ");
        }
        this.sendChannelMessage(res.toString());
    }

    public void count() {
        this.countPlayersCards();
    }

    public void pass(String sender) {
        if (this.drawn) {
            this.sendChannelMessage(this.createMessage("CARD_COUNTS", sender));
            this.nextPlayer(1);
            this.showTopCard(this.channel);
            this.drawn = false;
        } else {
            this.sendMessage(this.createMessage("DRAW_THEN_PASS", sender), sender);
        }
    }

    public void draw(String sender) {
        if (this.drawCard(sender, 1)) {
            this.sendChannelMessage(this.createMessage("TOOK_CARD", sender));
        } else {
            this.noCardsLeft(this.channel);
            this.drawCard(sender, 1);
            this.sendChannelMessage(this.createMessage("TOOK_CARD", sender));
        }
        this.drawn = true;
    }

    public void sendHand(String player) {
        if (this.getPlayer(player) != null) {
            this.sendMessage(this.getPlayer(player).toString(), player);
        }
    }

    public boolean hasPlayer(String player) {
        return this.playersNames.containsKey(player);
    }

    public void reset(String sender) {
        if (sender.equalsIgnoreCase(this.dealer)) {
            this.endGame(true, null);
            this.sendChannelMessage(this.createMessage("GAME_RESET", sender));
        }
    }

    public void dealGame(String sender) {
        if (!sender.equalsIgnoreCase(this.dealer)) {
            return;
        }
        if (!this.inProgress && this.players.size() >= 2) {
            this.inProgress = true;
            this.setNextPlayer(this.players.get(1));
            this.deal();
            this.cardInPlay = this.drawCard();
            this.discardedCards.add(this.cardInPlay);
            if (this.cardInPlay.getValue() == 10 || this.cardInPlay.getValue() == 23) {
                do {
                    this.cardInPlay = this.drawCard();
                    this.discardedCards.add(this.cardInPlay);
                } while (this.cardInPlay.getValue() == 10 || this.cardInPlay.getValue() == 23);
            }
            if (this.cardInPlay.getValue() == 21) {
                if (this.players.size() > 2) {
                    this.nextPlayer(-1);
                }
                this.reversePlayerOrder();
            } else if (this.cardInPlay.getValue() == 22) {
                this.sendChannelMessage(this.createMessage("PLAYER_SKIPPED", this.nextPlayer.getName()));
                this.nextPlayer(1);
            } else if (this.cardInPlay.getValue() == 20) {
                if (this.drawCard(this.nextPlayer.getName(), 1)) {
                    if (!this.drawCard(this.nextPlayer.getName(), 1)) {
                        this.noCardsLeft(this.channel);
                        this.drawCard(this.nextPlayer.getName(), 1);
                    }
                } else {
                    this.noCardsLeft(this.channel);
                    this.drawCard(this.nextPlayer.getName(), 2);
                }
                this.sendChannelMessage(this.nextPlayer.getName() + this.createMessage("PLAYER_2CARDS_SKIPPED"));
                this.nextPlayer(1);
            }
            this.showTopCard(this.channel);
        } else if (!this.inProgress) {
            this.sendChannelMessage(this.createMessage("ONE_MORE_PLAYER", sender));
        }
    }

    public boolean playCard(String sender, String message) {
        boolean valid = this.playCard(this.channel, sender, message);
        if (valid) {
            if (this.getPlayer(sender).hasWon()) {
                int totalScore = 0;
                for (Player p : this.players) {
                    if (p.getName().equalsIgnoreCase(sender)) continue;
                    totalScore += p.getPoints();
                    this.sendChannelMessage(p.toString());
                }
                this.updateScores(sender, totalScore);
                this.sendChannelMessage(this.createMessage("GAME_OVER_POINTS", sender).replace("SCORE", Integer.toString(totalScore)));
                double payout = this.endGame(false, sender);
                if (payout < 0.0) {
                    this.sendChannelMessageAndPopUp(SystemProperty.get(SystemPropertyEntities.BOT.PAYOUT_FAILURE_MESSAGE));
                } else if (payout > 0.0) {
                    this.sendChannelMessageAndPopUp(this.createMessage("WON_GAME_POT", sender).replace("POT_PAYOUT", this.df.format(payout)));
                } else {
                    this.sendChannelMessageAndPopUp(this.createMessage("WON_GAME", sender));
                }
                return true;
            }
            if (this.getPlayer(sender).hasUno()) {
                this.sendChannelMessage(this.createMessage("ONE", sender));
            }
            this.showTopCard(this.channel);
            this.drawn = false;
        }
        return false;
    }

    public void removePlayer(String name) {
        Player player = this.getPlayer(name);
        if (player == null) {
            return;
        }
        this.playersNames.remove(name);
        if (this.nextPlayer != null && this.nextPlayer.getName().equalsIgnoreCase(player.getName())) {
            this.nextPlayer(1);
        }
        if (this.pot != null) {
            try {
                this.pot.removePlayer(name);
            }
            catch (Exception e) {
                log.error((Object)("Unable to remove user " + name + " from pot " + this.pot.getPotID()), (Throwable)e);
            }
        }
        this.discardedCards.addAll(player.getCards());
        this.players.remove(player);
        if (this.nextPlayer != null) {
            this.sendChannelMessage(this.createMessage("REMOVED_AND_NEXT", name, this.nextPlayer.getName()));
        }
        if (this.players.size() == 1) {
            double payout = this.endGame(false, this.players.get(0).getName());
            if (payout > 0.0) {
                this.sendChannelMessage(this.createMessage("WON_BY_DEFAULT_POT", this.players.get(0).getName()).replace("POT_PAYOUT", this.df.format(payout)));
            } else {
                this.sendChannelMessage(this.createMessage("WON_BY_DEFAULT", this.players.get(0).getName()));
            }
        } else if (this.players.size() == 0) {
            this.sendChannelMessage(this.createMessage("NO_WINNER"));
            this.endGame(true, null);
        }
    }

    public boolean isPlayersTurn(String sender) {
        Player player = this.getPlayer(sender);
        return null != player && player.getName().equalsIgnoreCase(this.nextPlayer.getName());
    }

    public void showPlayerHand(Player player) {
        this.sendMessage("Your cards: " + player.getHand(), player.getName());
    }

    public void showTopCard(String channel) {
        StringBuffer additionalInfo = new StringBuffer("");
        switch (this.wildColour) {
            case 1: 
            case 2: 
            case 3: 
            case 4: {
                additionalInfo.append(this.createMessage("COLOR_IS") + Card.colorEmoticonMappings.get(this.wildColour) + STR_ANY);
                break;
            }
            default: {
                additionalInfo.append("");
            }
        }
        this.sendChannelMessage(this.createMessage("PLAYER_TURN", this.nextPlayer.getName()) + this.cardInPlay + additionalInfo.toString());
        this.showPlayerHand(this.nextPlayer);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean playCard(String channel, String sender, String message) {
        Player player = this.getPlayer(sender);
        if (player == null) return true;
        String cardToPlay = message.toLowerCase().substring(this.CMD_PLAY_CARD.length()).trim();
        int cardValue = -1;
        int cardColour = -1;
        String cardValueStr = "";
        String cardColourStr = "";
        try {
            cardValueStr = cardToPlay.charAt(2) + "";
            cardColourStr = cardToPlay.charAt(0) + "";
            if (cardToPlay.indexOf(STR_WILD_DRAW_4) != -1 && cardToPlay.length() == 5) {
                cardValue = 23;
                cardColourStr = cardToPlay.charAt(4) + "";
            } else if (cardToPlay.indexOf(STR_DRAW_2) != -1 && cardToPlay.length() == 4) {
                cardValue = 20;
                cardColourStr = cardToPlay.charAt(0) + "";
            } else if (cardToPlay.charAt(2) == 'r' && cardToPlay.indexOf(STR_WILD) == -1 && cardToPlay.length() == 3) {
                cardValue = 21;
                cardColourStr = cardToPlay.charAt(0) + "";
            } else if (cardToPlay.indexOf(STR_SKIP) != -1 && cardToPlay.length() == 3) {
                cardValue = 22;
                cardColourStr = cardToPlay.charAt(0) + "";
            } else if (cardToPlay.indexOf(STR_WILD) != -1 && cardToPlay.length() == 3) {
                cardValue = 10;
                cardColourStr = cardToPlay.charAt(2) + "";
            } else {
                cardValue = new Integer(cardValueStr);
            }
            if (!(cardColourStr.equalsIgnoreCase(STR_REVERSE) || cardColourStr.equalsIgnoreCase("y") || cardColourStr.equalsIgnoreCase("b") || cardColourStr.equalsIgnoreCase("g"))) {
                this.sendMessage(this.createMessage("INVALID_COLOR", sender), sender);
                return false;
            }
            cardColour = this.getColour(cardColourStr);
        }
        catch (Exception ex) {
            this.sendMessage(this.createMessage("INVALID_CARD", sender), sender);
            return false;
        }
        if (cardColour == this.cardInPlay.getColour() || cardColour == this.wildColour || cardValue == this.cardInPlay.getValue() || cardValue == 10 || cardValue == 23) {
            Card card = player.getCard(cardValue, cardColour);
            if (card == null) {
                this.sendMessage(this.createMessage("INVALID_CARD", sender), sender);
                return false;
            }
            this.discardedCards.add(card);
            this.cardInPlay = card;
            player.removeCard(card);
            String additionalInfo = ".";
            if (cardValue == 23) {
                this.wildColour = this.getColour(cardColourStr);
                this.nextPlayer(1);
                if (!this.drawCard(this.nextPlayer.getName(), 4)) {
                    this.noCardsLeft(channel);
                    return true;
                }
                Card newCard = new Card(this.getColour(cardColourStr), 99);
                additionalInfo = (String)this.messages.get("CHANGES_COLOR") + newCard + " " + this.nextPlayer.getName() + (String)this.messages.get("PLAYER_4CARDS_SKIPPED");
                this.nextPlayer(1);
            } else if (cardValue == 20) {
                this.nextPlayer(1);
                if (!this.drawCard(this.nextPlayer.getName(), 2)) {
                    this.noCardsLeft(channel);
                    return true;
                }
                additionalInfo = " " + this.nextPlayer.getName() + (String)this.messages.get("PLAYER_2CARDS_SKIPPED");
                this.nextPlayer(1);
            } else if (cardValue == 21) {
                if (this.players.size() > 2) {
                    this.nextPlayer(-1);
                }
                additionalInfo = (String)this.messages.get("TURN_IS_BACK_TO") + this.nextPlayer.getName();
                this.reversePlayerOrder();
            } else if (cardValue == 22) {
                this.nextPlayer(1);
                additionalInfo = ", " + this.nextPlayer.getName() + (String)this.messages.get("SKIPPED");
                this.nextPlayer(1);
            } else if (cardValue == 10) {
                this.wildColour = this.getColour(cardColourStr);
                Card newCard = new Card(this.getColour(cardColourStr), 99);
                additionalInfo = (String)this.messages.get("CHANGES_COLOR") + newCard;
                this.nextPlayer(1);
            } else {
                this.nextPlayer(1);
            }
            this.sendChannelMessage(sender + (String)this.messages.get("PLAYS") + this.cardInPlay + additionalInfo);
            if (cardValue == 10) return true;
            if (cardValue == 23) return true;
            this.wildColour = 10;
            return true;
        }
        if (!player.hasCardWithValue(cardValue) && !player.hasCardWithColour(cardColour)) {
            this.sendMessage(this.createMessage("INVALID_CARD", sender), sender);
            return false;
        }
        this.sendMessage(this.createMessage("FOLLOW_LAST_CARD", sender), sender);
        return false;
    }

    public void reversePlayerOrder() {
        ArrayList<Player> tempPlayers = new ArrayList<Player>();
        for (int x = this.players.size() - 1; x >= 0; --x) {
            tempPlayers.add(this.players.get(x));
        }
        this.players = tempPlayers;
    }

    public int getColour(String cardColourStr) {
        if (cardColourStr.equals("b")) {
            return 1;
        }
        if (cardColourStr.equals("g")) {
            return 2;
        }
        if (cardColourStr.equals(STR_REVERSE)) {
            return 3;
        }
        return 4;
    }

    public void nextPlayer(int increment) {
        int playerTurn = this.players.indexOf(this.nextPlayer);
        if ((playerTurn += increment) > this.players.size() - 1) {
            this.setNextPlayer(this.players.get(0));
        } else if (playerTurn == -1) {
            this.setNextPlayer(this.players.get(this.players.size() - 1));
        } else {
            this.setNextPlayer(this.players.get(playerTurn));
        }
    }

    public void setNextPlayer(Player player) {
        this.nextPlayer = player;
    }

    public Player getPlayer(String name) {
        for (Player player : this.players) {
            if (!player.getName().equalsIgnoreCase(name)) continue;
            return player;
        }
        return null;
    }

    public void deal() {
        Random random = new Random();
        int rnd = 0;
        for (Player player : this.players) {
            if (null == player) continue;
            for (int x = 1; x <= 7; ++x) {
                rnd = random.nextInt(this.cards.size());
                player.addCard(this.cards.get(rnd));
                this.cards.remove(rnd);
            }
            this.sendMessage(player.toString(), player.getName());
        }
    }

    public Card drawCard() {
        if (this.cards.size() > 0) {
            Random random = new Random();
            int rnd = random.nextInt(this.cards.size());
            Card card = this.cards.get(rnd);
            this.cards.remove(rnd);
            return card;
        }
        return null;
    }

    public boolean drawCard(String sender, int numCards) {
        Player player = this.getPlayer(sender);
        String sendPlayerMsg = "You Drew: ";
        if (player != null && null != this.cards && null != this.discardedCards) {
            if (this.cards.size() == 0 && this.discardedCards.size() >= numCards) {
                this.cards = this.discardedCards;
            } else if (this.cards.size() == 0 && this.discardedCards.size() == 0) {
                return false;
            }
            Card tmpCard = new Card(99, 99);
            for (int x = 1; x <= numCards; ++x) {
                tmpCard = this.drawCard();
                if (tmpCard == null) {
                    return false;
                }
                player.addCard(tmpCard);
                sendPlayerMsg = sendPlayerMsg + tmpCard.toString() + " ";
            }
            this.sendMessage(sendPlayerMsg, player.getName());
        }
        return true;
    }

    public void addPlayer(String username) {
        if (this.getGameState() == BotData.BotStateEnum.GAME_STARTED || this.getGameState() == BotData.BotStateEnum.GAME_JOINING) {
            if (this.costToJoinGame > 0.0 && this.pot != null && !username.equalsIgnoreCase(this.gameStarter)) {
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
            this.players.add(new Player(username));
            if (this.dealer.equalsIgnoreCase("")) {
                this.dealer = username;
                this.playersNames.put(username, true);
            } else {
                this.playersNames.put(username, false);
            }
            StringBuilder message = new StringBuilder();
            message.append(this.createMessage("ADDED_TO_GAME", username));
            log.info((Object)(username + " joined the game"));
            this.sendMessage(message.toString(), username);
            if (!username.equals(this.gameStarter)) {
                this.sendChannelMessage(this.createMessage("JOIN", username));
            }
        }
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class Score
    implements Comparable<Score> {
        String player;
        int points;

        public Score(String player, int points) {
            this.player = player;
            this.points = points;
        }

        public String getPlayer() {
            return this.player;
        }

        public int getScore() {
            return this.points;
        }

        public void incrementScore(int points) {
            this.points += points;
        }

        public boolean equals(Object scoreObj) {
            if (scoreObj == null || !(scoreObj instanceof Score)) {
                return false;
            }
            Score score = (Score)scoreObj;
            return this.player.equals(score.player) && this.points == score.points;
        }

        @Override
        public int compareTo(Score o) {
            Score score = o;
            if (this.points > score.points) {
                return -1;
            }
            return this.points > score.points ? 0 : 1;
        }
    }

    class StartPlay
    implements Runnable {
        One bot;

        StartPlay(One bot) {
            this.bot = bot;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            One.this.waitForPlayersTimer = null;
            try {
                One one = this.bot;
                synchronized (one) {
                    BotData.BotStateEnum gameState;
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"OneBot: starting play in StartPlay()");
                    }
                    if ((gameState = One.this.getGameState()) == BotData.BotStateEnum.GAME_JOINING) {
                        One.this.setGameState(BotData.BotStateEnum.GAME_JOIN_ENDED);
                        if (One.this.getPlayers().size() < 2) {
                            One.this.sendChannelMessage(One.this.createMessage("JOIN_NO_MIN"));
                            One.this.resetGame();
                            if (log.isDebugEnabled()) {
                                log.debug((Object)("botInstanceID[" + One.this.getInstanceID() + "]: Join ended. Not enough players."));
                            }
                        } else {
                            log.info((Object)("New game started in " + One.this.channel));
                            String message = One.this.createMessage("GAME_STARTED_NOTE", One.this.gameStarter);
                            One.this.sendChannelMessage(message);
                            One.this.setGameState(BotData.BotStateEnum.PLAYING);
                            One.this.initGame(false);
                        }
                    }
                }
            }
            catch (Exception e) {
                log.error((Object)"Unexpected exception caught in StartPlay.run()", (Throwable)e);
                One.this.resetGame();
            }
        }
    }

    class StartGame
    implements Runnable {
        One bot;

        StartGame(One bot) {
            this.bot = bot;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            One one = this.bot;
            synchronized (one) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("botInstanceID[" + One.this.getInstanceID() + "]: in StartGame() "));
                }
                BotData.BotStateEnum gameState = null;
                gameState = One.this.getGameState();
                if (gameState == BotData.BotStateEnum.GAME_STARTING) {
                    One.this.setGameState(BotData.BotStateEnum.GAME_STARTED);
                    One.this.addPlayer(One.this.gameStarter);
                    One.this.sendChannelMessage(One.this.createMessage("GAME_JOIN", One.this.gameStarter));
                    if (One.this.timeToJoinGame > 0L) {
                        One.this.setGameState(BotData.BotStateEnum.GAME_JOINING);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)"OneBot: starting timer for StartPlay()");
                        }
                        log.info((Object)("One game started by " + this.bot.gameStarter));
                        One.this.waitForPlayersTimer = One.this.executor.schedule(new StartPlay(this.bot), One.this.timeToJoinGame, TimeUnit.SECONDS);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("botInstanceID[" + One.this.getInstanceID() + "]: scheduled to start play. Awaiting join.. "));
                        }
                    }
                }
            }
        }
    }
}

