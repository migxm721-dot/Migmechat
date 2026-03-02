/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.botservice.bot.migbot.werewolf;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.bot.migbot.werewolf.Vote;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.dao.BotDAO;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.slice.BotChannelPrx;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Werewolf
extends Bot {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Werewolf.class));
    List<String> players;
    public static final String TIMER_JOIN_GAME = "timerJoinGame";
    public static final String MAX_PLAYERS = "maxPlayers";
    public static final String TIMER_DAYTIME = "dayTime";
    public static final String TIMER_NIGHTTIME = "nightTime";
    public static final String TIMER_VOTE = "voteTime";
    public static final String IS_TIE_GAME_ON = "tieGame";
    public static final String TIMER_IDLE = "timerIdle";
    public static final long TIMER_DAYTIME_VALUE = 45L;
    public static final long TIMER_NIGHTTIME_VALUE = 45L;
    public static final long TIMER_VOTE_VALUE = 60L;
    public static final long IDLE_TIME_VALUE = 5L;
    public static final String COMMAND_VOTE = "!v";
    public static final String COMMAND_KILL = "!k";
    public static final String COMMAND_SEE = "!s";
    public static final String COMMAND_ALIVE = "!a";
    public static final String COMMAND_ROLE = "!r";
    Date lastActivityTime;
    private BotData.BotStateEnum gameState = BotData.BotStateEnum.NO_GAME;
    long timeToJoinGame = 90L;
    public int minPlayers = 5;
    public int maxPlayers = 12;
    long timeAllowedToIdle = 30L;
    Vector priority;
    Vector votes;
    Vector wolves;
    Vector wolfVictim;
    final int JOINTIME = 90;
    final int MINPLAYERS = 5;
    final int MAXPLAYERS = 12;
    final int TWOWOLVES = 8;
    long dayTime = 90L;
    long nightTime = 60L;
    long voteTime = 30L;
    int seer;
    int toSee = -1;
    int[] notVoted;
    int[] wasVoted;
    boolean playing = false;
    boolean day = false;
    boolean gameStart = false;
    boolean firstDay;
    boolean firstNight;
    boolean tieGame = true;
    boolean timeToVote;
    boolean[] wolf;
    boolean[] dead;
    boolean[] voted;
    String role;
    String oneWolf;
    String manyWolves;
    String winnerString;

    public Werewolf(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDao) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDao);
        this.loadGameConfig();
        log.info((Object)("WerewolfBot [" + this.instanceID + "] added to channel [" + this.channel + "]"));
        this.sendChannelMessage(this.createMessage("BOT_ADDED"));
        String message = this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT");
        this.sendChannelMessage(message);
        this.players = new ArrayList<String>();
        this.priority = new Vector(1, 1);
        this.dead = new boolean[this.players.size()];
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
            log.debug((Object)("Bot has been idle for " + minutes + (minutes == 1L ? " minute" : " minutes") + ". Canceling pot and resetting game, if any"));
            this.resetGame();
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeStoppedNow() {
        return (this.gameState != BotData.BotStateEnum.PLAYING || this.pot == null) && this.gameState != BotData.BotStateEnum.GAME_JOINING && this.gameState != BotData.BotStateEnum.GAME_STARTING;
    }

    private void loadGameConfig() {
        this.timeToJoinGame = this.getLongParameter(TIMER_JOIN_GAME, this.timeToJoinGame);
        this.dayTime = this.getLongParameter(TIMER_DAYTIME, 45L);
        this.nightTime = this.getLongParameter(TIMER_NIGHTTIME, 45L);
        this.voteTime = this.getLongParameter(TIMER_VOTE, 60L);
        this.tieGame = this.getBooleanParameter(IS_TIE_GAME_ON, false);
        this.maxPlayers = this.getIntParameter(MAX_PLAYERS, 12);
        this.oneWolf = (String)this.messages.get("1-WOLF");
        this.manyWolves = (String)this.messages.get("MANY-WOLVES");
        this.timeAllowedToIdle = this.getLongParameter(TIMER_IDLE, 5L);
    }

    public List<String> getPlayers() {
        return this.players;
    }

    protected String createMessage(String message) {
        return this.createMessage(message, null, null, -1, null);
    }

    private String createMessage(String message, String username) {
        return this.createMessage(message, username, null, -1, null);
    }

    private String createMessage(String message, String username, String username2) {
        return this.createMessage(message, username, username2, -1, null);
    }

    private String createMessage(String messageKey, String player, String player2, int time, String errorInput) {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Looking for messageKey: " + messageKey));
            }
            String messageToSend = (String)this.messages.get(messageKey);
            messageToSend = messageToSend.replaceAll("BOTNAME", this.botData.getDisplayName());
            messageToSend = messageToSend.replaceAll("CMD_NO", "!n");
            messageToSend = messageToSend.replaceAll("CMD_JOIN", "!j");
            messageToSend = messageToSend.replaceAll("MINPLAYERS", this.minPlayers + "");
            if (player != null) {
                messageToSend = messageToSend.replaceAll("PLAYER", player);
            }
            if (StringUtils.hasLength((String)this.winnerString)) {
                messageToSend = messageToSend.replaceAll("WINNER_STRING", this.winnerString);
                String winningsText = (String)this.messages.get("WINNINGS_ZERO");
                messageToSend = messageToSend.replaceAll("WINNINGS_TEXT", winningsText);
            }
            messageToSend = messageToSend.replaceAll("CURRENCY", "USD");
            messageToSend = messageToSend.replaceAll("AMOUNT_POT", "0");
            messageToSend = messageToSend.replaceAll("DENOM", "c");
            if (player2 != null) {
                messageToSend = messageToSend.replaceAll("PLAYR2", player2);
            }
            messageToSend = messageToSend.replaceAll("TIME", "" + time);
            if (this.role != null) {
                messageToSend = messageToSend.replaceAll("ISAWOLF?", this.role);
                messageToSend = messageToSend.replaceAll("ROLE", this.role);
            }
            if (this.wolves != null && !this.wolves.isEmpty()) {
                messageToSend = messageToSend.replaceAll("WEREWOLF", this.wolves.size() == 1 ? this.oneWolf : this.manyWolves);
            }
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

    @Override
    public synchronized void onMessage(String username, String messageText, long receivedTimestamp) {
        if (messageText.equalsIgnoreCase("!n")) {
            this.processNoMessage(username);
        } else if (messageText.startsWith("!start")) {
            if (this.getGameState() == BotData.BotStateEnum.NO_GAME) {
                try {
                    this.startGame(username);
                }
                catch (Exception e) {
                    log.error((Object)"Error starting game with default amount: ", (Throwable)e);
                }
            } else {
                this.sendGameCannotStartMessage(username);
            }
        }
        if (this.playing) {
            if (messageText.toLowerCase().equalsIgnoreCase("!j")) {
                if (this.gameStart) {
                    if (!this.isNameAdded(username)) {
                        if (this.players.size() < 12) {
                            this.addPlayer(username);
                            if (!this.players.contains(username)) {
                                this.sendMessage(this.createMessage("COULD-NOT-ADD", username), username);
                            }
                        } else {
                            this.sendMessage(this.createMessage("MAX-REACHED", username), username);
                        }
                    } else {
                        this.sendMessage(this.createMessage("ALREADY_IN_GAME", username), username);
                    }
                } else {
                    this.sendMessage(this.createMessage("GAME-PLAYING", username), username);
                }
            } else if (this.players.contains(username)) {
                block77: {
                    if (this.timeToVote) {
                        if (messageText.toLowerCase().startsWith("!v ")) {
                            int index = this.players.lastIndexOf(username);
                            if (index != -1 && this.dead != null && this.dead.length > 0 && this.dead[index]) {
                                this.sendMessage(this.createMessage("DEAD_CANT_VOTE", username), username);
                                return;
                            }
                            if (!this.hasVoted(username)) {
                                try {
                                    String choice = messageText.substring(messageText.indexOf(" ") + 1, messageText.length());
                                    choice = choice.trim();
                                    if (this.players.contains(choice) && !choice.equalsIgnoreCase(username)) {
                                        int i;
                                        Vote vote = new Vote(username, choice);
                                        for (i = 0; i < this.players.size(); ++i) {
                                            if (this.players.get(i) == null) continue;
                                            if (this.players.get(i).equalsIgnoreCase(choice) && !this.dead[i]) {
                                                while (!this.votes.add(vote)) {
                                                }
                                                continue;
                                            }
                                            if (!this.players.get(i).equalsIgnoreCase(choice) || !this.dead[i]) continue;
                                            this.sendMessage(this.createMessage("ALREADY_DEAD", username), username);
                                            return;
                                        }
                                        for (i = 0; i < this.players.size(); ++i) {
                                            if (!username.equals(this.players.get(i))) continue;
                                            if (!this.dead[i]) {
                                                this.voted[i] = true;
                                                this.notVoted[i] = 0;
                                                this.sendChannelMessage(this.createMessage("HAS-VOTED", username, choice));
                                                continue;
                                            }
                                            this.sendMessage(this.createMessage("DEAD_CANT_VOTE", username), username);
                                        }
                                        break block77;
                                    }
                                    this.sendMessage(this.createMessage("INVALID_CHOICE", username), username);
                                }
                                catch (Exception x) {
                                    this.sendMessage(this.createMessage("INVALID_VOTE_COMMAND", username), username);
                                    x.printStackTrace();
                                }
                            } else {
                                this.sendMessage(this.createMessage("ALREADY_VOTED", username), username);
                            }
                        } else if (messageText.toLowerCase().startsWith("!k ") || messageText.toLowerCase().startsWith("!s ")) {
                            this.sendMessage(this.createMessage("VOTING_TIME_ONLY", username), username);
                        }
                    } else if (!this.day) {
                        block78: {
                            if (messageText.toLowerCase().startsWith("!k ")) {
                                if (this.players.contains(username)) {
                                    boolean isWolf = false;
                                    int wolfIndex = -1;
                                    for (int i = 0; i < this.wolves.size(); ++i) {
                                        if (!this.wolves.get(i).equals(username)) continue;
                                        isWolf = true;
                                        wolfIndex = i;
                                        break;
                                    }
                                    if (isWolf) {
                                        int otherWolf = -1;
                                        if (this.wolves.size() > 1) {
                                            otherWolf = wolfIndex == 0 ? 1 : 0;
                                        }
                                        try {
                                            String victim = messageText.substring(messageText.indexOf(" ") + 1, messageText.length());
                                            victim = victim.trim();
                                            if (this.players.contains(victim)) {
                                                int i;
                                                if (otherWolf != -1 && victim.equals(this.wolves.get(otherWolf))) {
                                                    if (log.isDebugEnabled()) {
                                                        log.debug((Object)("Invalid: " + username + " tried to kill the other wolf " + victim));
                                                    }
                                                    this.sendMessage(this.createMessage("CANT_KILL_OTHER_WOLF", username, victim), username);
                                                    return;
                                                }
                                                boolean isDead = false;
                                                for (i = 0; i < this.players.size(); ++i) {
                                                    if (this.players.get(i) == null || !this.players.get(i).equalsIgnoreCase(victim) || !this.dead[i]) continue;
                                                    isDead = true;
                                                }
                                                if (!isDead) {
                                                    if (!victim.equalsIgnoreCase(username)) {
                                                        while (!this.wolfVictim.add(victim)) {
                                                        }
                                                        if (this.wolves.size() == 1) {
                                                            this.sendMessage(this.createMessage("WOLF-CHOICE", username, victim), username);
                                                        } else {
                                                            this.sendMessage(this.createMessage("WOLVES-CHOICE", username, victim), username);
                                                            for (i = 0; i < this.wolves.size(); ++i) {
                                                                if (((String)this.wolves.get(i)).equals(username)) continue;
                                                                this.sendMessage(this.createMessage("WOLVES-CHOICE-OTHER" + (i + 1), username, victim), (String)this.wolves.get(i));
                                                            }
                                                        }
                                                    } else {
                                                        this.sendMessage(this.createMessage("CANT_EAT_SELF", username), username);
                                                    }
                                                } else {
                                                    this.sendMessage(this.createMessage("ALREADY_DEAD", username), username);
                                                }
                                                break block78;
                                            }
                                            this.sendMessage(this.createMessage("VALID_PLAYER", username), username);
                                        }
                                        catch (Exception x) {
                                            x.printStackTrace();
                                            this.sendMessage(this.createMessage("VALID_PLAYER", username), username);
                                        }
                                    } else {
                                        this.sendMessage(this.createMessage("NOT-WOLF", username), username);
                                    }
                                } else {
                                    this.sendMessage(this.createMessage("NOT_PLAYING", username), username);
                                }
                            }
                        }
                        if (messageText.toLowerCase().startsWith("!s ")) {
                            try {
                                if (this.players.contains(username)) {
                                    if (this.players.get(this.seer) != null && this.players.get(this.seer).equals(username)) {
                                        if (this.dead != null && this.dead.length > 0 && !this.dead[this.seer]) {
                                            String see = messageText.substring(messageText.indexOf(" ") + 1, messageText.length());
                                            if (this.players.contains(see = see.trim())) {
                                                if (!username.equals(see)) {
                                                    for (int i = 0; i < this.players.size(); ++i) {
                                                        if (this.players.get(i) == null || !this.players.get(i).equalsIgnoreCase(see)) continue;
                                                        this.toSee = i;
                                                    }
                                                    this.sendMessage(this.createMessage("WILL-SEE", username, this.players.get(this.toSee)), username);
                                                } else {
                                                    this.sendMessage(this.createMessage("ALREADY_KNOW_HUMAN", username), username);
                                                }
                                            }
                                        } else {
                                            this.sendMessage(this.createMessage("SEER-DEAD", username), username);
                                        }
                                    } else {
                                        this.sendMessage(this.createMessage("NOT-SEER", username), username);
                                    }
                                    break block77;
                                }
                                this.sendMessage(this.createMessage("INVALID_CHOICE", username), username);
                            }
                            catch (Exception x) {
                                this.sendMessage(this.createMessage("VALID_PLAYER", username), username);
                                x.printStackTrace();
                            }
                        } else if (messageText.toLowerCase().startsWith("!v ")) {
                            this.sendMessage(this.createMessage("NIGHT_TIME_ONLY", username), username);
                        }
                    }
                }
                if (messageText.toLowerCase().equalsIgnoreCase(COMMAND_ALIVE)) {
                    String names = this.getPlayersAlive();
                    this.sendMessage(this.createMessage("PLAYERS_ALIVE", names), username);
                }
                if (messageText.toLowerCase().equalsIgnoreCase(COMMAND_ROLE) && !this.gameStart) {
                    for (int i = 0; i < this.players.size(); ++i) {
                        if (!username.equals(this.players.get(i))) continue;
                        if (this.wolf[i]) {
                            if (this.wolves.size() == 1) {
                                this.sendMessage(this.createMessage("W-ROLE", username), username);
                                continue;
                            }
                            for (int j = 0; j < this.wolves.size(); ++j) {
                                if (username.equals(this.wolves.get(j))) continue;
                                this.sendMessage(this.createMessage("WS-ROLE", username, (String)this.wolves.get(j)), username);
                            }
                            continue;
                        }
                        if (i == this.seer) {
                            this.sendMessage(this.createMessage("S-ROLE", username), username);
                            continue;
                        }
                        this.sendMessage(this.createMessage("V-ROLE", username), username);
                    }
                }
            }
        }
    }

    private String getPlayersAlive() {
        String names = "";
        for (int i = 0; i < this.players.size(); ++i) {
            if (this.dead == null || this.dead.length <= 0 || this.dead[i] || this.players.get(i) == null) continue;
            names = names + this.players.get(i) + " ";
        }
        return names;
    }

    private boolean isNameAdded(String name) {
        for (int i = 0; i < this.players.size(); ++i) {
            if (!name.equals(this.players.get(i))) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPlayer(String username) {
        if (this.getGameState() == BotData.BotStateEnum.GAME_STARTED || this.getGameState() == BotData.BotStateEnum.GAME_JOINING) {
            List<String> list = this.players;
            synchronized (list) {
                if (!this.players.contains(username)) {
                    this.players.add(username);
                }
            }
            StringBuilder message = new StringBuilder();
            message.append(this.createMessage("ADDED", username));
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
                message = this.createMessage("INVALID_COMMAND", username);
                break;
            }
            default: {
                message = this.createMessage("INVALID_COMMAND", username);
            }
        }
        this.sendMessage(message, username);
    }

    public void startGame(String username) throws Exception {
        this.updateLastActivityTime();
        if (this.gameState.equals((Object)BotData.BotStateEnum.NO_GAME)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("botInstanceID[" + this.getInstanceID() + "]: No charges. Game started by user[" + username + "]"));
            }
            this.setGameState(BotData.BotStateEnum.GAME_STARTING);
            this.gameStarter = username;
            if (log.isDebugEnabled()) {
                log.debug((Object)"WerewolfBot: executing StartGame()");
            }
            this.executor.execute(new StartGame(this));
            if (log.isDebugEnabled()) {
                log.debug((Object)"WerewolfBot: executed for StartGame()");
            }
        } else {
            this.sendGameCannotStartMessage(username);
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

    @Override
    public synchronized void onUserJoinChannel(String username) {
        String message = null;
        switch (this.getGameState().value()) {
            case 2: {
                message = this.createMessage("GAME-STARTED");
                break;
            }
            case 5: {
                message = this.createMessage("STATUS-PLAYING");
                break;
            }
            case 3: {
                message = this.createMessage("GAME-STARTED");
                break;
            }
            default: {
                message = this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT");
            }
        }
        this.sendMessage(message, username);
    }

    @Override
    public synchronized void onUserLeaveChannel(String username) {
        block9: {
            block10: {
                int i;
                if (!this.playing) break block9;
                if (this.gameStart) break block10;
                for (i = 0; i < this.players.size(); ++i) {
                    int j;
                    if (!username.equals(this.players.get(i))) continue;
                    this.players.set(i, null);
                    if (this.dead == null || this.dead.length <= 0 || this.dead[i]) continue;
                    if (this.wolf[i]) {
                        for (j = 0; j < this.wolves.size(); ++j) {
                            if (!((String)this.wolves.get(j)).equals(username)) continue;
                            this.wolves.remove(j);
                        }
                        this.sendChannelMessage(this.createMessage("FLEE-WOLF", username));
                    } else {
                        this.sendChannelMessage(this.createMessage("FLEE-VILLAGER", username));
                    }
                    this.dead[i] = true;
                    if (this.wolfVictim != null) {
                        for (j = 0; j < this.wolfVictim.size(); ++j) {
                            if (!username.equals(this.wolfVictim.get(j))) continue;
                            this.wolfVictim.set(j, null);
                        }
                    }
                    this.checkWin();
                }
                if (!this.timeToVote) break block9;
                for (i = 0; i < this.votes.size(); ++i) {
                    if (!((Vote)this.votes.get(i)).getVote().equalsIgnoreCase(username)) continue;
                    this.votes.remove(i);
                }
                break block9;
            }
            if (this.gameStart) {
                for (int i = 0; i < this.players.size(); ++i) {
                    if (!username.equals(this.players.get(i))) continue;
                    this.players.remove(i);
                    this.sendChannelMessage(this.createMessage("FLEE", username));
                    break;
                }
            }
        }
    }

    public void endGame() {
        if (this.getGameState() != BotData.BotStateEnum.PLAYING) {
            return;
        }
        this.sendChannelMessageAndPopUp(this.createMessage("GAME_OVER_FREE"));
        this.resetGame();
        this.updateLastActivityTime();
        this.sendChannelMessage(this.createMessage("GAME_STATE_DEFAULT_NO_AMOUNT"));
    }

    private synchronized void updateLastActivityTime() {
        this.lastActivityTime = new Date();
    }

    void resetGame() {
        this.gameStarter = null;
        this.players.clear();
        this.priority.clear();
        this.setGameState(BotData.BotStateEnum.NO_GAME);
    }

    protected boolean hasVoted(String name) {
        for (int i = 0; i < this.players.size(); ++i) {
            if (!name.equals(this.players.get(i))) continue;
            return this.voted[i];
        }
        return false;
    }

    protected void tallyVotes() {
        int i;
        this.sendChannelMessage(this.createMessage("TALLY"));
        for (int i2 = 0; i2 < this.players.size(); ++i2) {
            if (this.players.get(i2) == null) continue;
            if (this.voted[i2]) {
                Vote thisVote = null;
                for (int k = 0; k < this.votes.size(); ++k) {
                    if (!this.players.get(i2).equals(((Vote)this.votes.get(k)).getName())) continue;
                    thisVote = (Vote)this.votes.get(k);
                    break;
                }
                for (int j = 0; j < this.players.size(); ++j) {
                    if (this.players.get(j) == null || thisVote.getVote() == null || !thisVote.getVote().equalsIgnoreCase(this.players.get(j))) continue;
                    int n = j;
                    this.wasVoted[n] = this.wasVoted[n] + 1;
                }
                continue;
            }
            if (this.dead[i2]) continue;
            int n = i2;
            this.notVoted[n] = this.notVoted[n] + 1;
        }
        int majority = 0;
        int guilty = -1;
        Vector<Integer> majIndexes = new Vector<Integer>(1, 1);
        for (i = 0; i < this.wasVoted.length; ++i) {
            if (this.wasVoted[i] <= majority) continue;
            majority = this.wasVoted[i];
        }
        for (i = 0; i < this.wasVoted.length; ++i) {
            if (this.wasVoted[i] != majority) continue;
            majIndexes.add(new Integer(i));
        }
        if (majIndexes.size() == 1) {
            guilty = Integer.parseInt(((Integer)majIndexes.get(0)).toString());
        } else if (this.tieGame && majIndexes != null && majIndexes.size() != 0) {
            int rand = (int)(Math.random() * (double)majIndexes.size());
            if (this.wasVoted[(Integer)majIndexes.get(rand)] == 0) {
                guilty = -1;
            } else {
                guilty = (Integer)majIndexes.get(rand);
                this.sendChannelMessage(this.createMessage("TIE"));
            }
        } else {
            guilty = -10;
        }
        if (guilty == -10) {
            this.sendChannelMessage(this.createMessage("NO-LYNCH"));
        } else if (guilty != -1) {
            String guiltyStr = this.players.get(guilty);
            this.dead[guilty] = true;
            if (guiltyStr == null) {
                this.sendChannelMessage(this.createMessage("LYNCH-LEFT"));
                return;
            }
            if (guilty == this.seer) {
                this.sendChannelMessage(this.createMessage("SEER-LYNCH", guiltyStr));
                this.role = (String)this.messages.get("ROLE-SEER");
            } else if (this.wolf[guilty]) {
                if (this.wolves.size() != 1) {
                    for (int i3 = 0; i3 < this.wolves.size(); ++i3) {
                        if (!guiltyStr.equals((String)this.wolves.get(i3))) continue;
                        this.wolves.remove(i3);
                    }
                }
                this.sendChannelMessage(this.createMessage("WOLF-LYNCH", guiltyStr));
                this.role = (String)this.messages.get("ROLE-WOLF");
            } else {
                this.sendChannelMessage(this.createMessage("VILLAGER-LYNCH", guiltyStr));
                this.role = (String)this.messages.get("ROLE-VILLAGER");
            }
            this.sendChannelMessage(this.createMessage("IS-LYNCHED", guiltyStr));
            if (guilty != this.seer && guilty > -1 && !this.wolf[guilty]) {
                this.sendMessage(this.createMessage("DYING-BREATH", guiltyStr), guiltyStr);
            }
        } else {
            this.sendChannelMessage(this.createMessage("NO-VOTES"));
        }
    }

    protected void wolfKill() {
        String victim = "";
        if (this.wolfVictim.isEmpty()) {
            this.sendChannelMessage(this.createMessage("NO-KILL"));
            return;
        }
        if (this.wolfVictim.size() == 1) {
            victim = (String)this.wolfVictim.get(0);
        } else if (this.wolfVictim.get(0).equals(this.wolfVictim.get(1))) {
            victim = (String)this.wolfVictim.get(0);
        } else {
            int randChoice = (int)(Math.random() * (double)this.wolfVictim.size());
            victim = (String)this.wolfVictim.get(randChoice);
        }
        for (int i = 0; i < this.players.size(); ++i) {
            if (this.players.get(i) == null || !this.players.get(i).equalsIgnoreCase(victim)) continue;
            if (this.players.get(i) != null) {
                String deadName = this.players.get(i);
                this.dead[i] = true;
                if (i == this.seer) {
                    this.sendChannelMessage(this.createMessage("SEER-KILL", deadName));
                    this.role = (String)this.messages.get("ROLE-SEER");
                } else {
                    this.sendChannelMessage(this.createMessage("VILLAGER-KILL", deadName));
                    this.role = (String)this.messages.get("ROLE-VILLAGER");
                }
                this.sendChannelMessage(this.createMessage("IS-KILLED", deadName));
                continue;
            }
            for (int j = 0; j < this.wolves.size(); ++j) {
                this.sendMessage(this.createMessage("WOLF_SELECTION_LEFT", (String)this.wolves.get(j)), (String)this.wolves.get(j));
            }
        }
    }

    protected void setRoles() {
        if (this.players.size() < 5) {
            this.sendChannelMessage(this.createMessage("NOT-ENOUGH"));
            this.playing = false;
            return;
        }
        int randWolf = (int)(Math.random() * (double)this.players.size());
        this.wolves.add(this.players.get(randWolf));
        this.wolf[randWolf] = true;
        if (this.players.size() < 8) {
            this.sendMessage(this.createMessage("WOLF-ROLE", this.players.get(randWolf)), this.players.get(randWolf));
        } else {
            boolean isWolf = true;
            while (isWolf) {
                randWolf = (int)(Math.random() * (double)this.players.size());
                if (this.wolf[randWolf]) continue;
                isWolf = false;
            }
            this.wolves.add(this.players.get(randWolf));
            this.wolf[randWolf] = true;
            for (int i = 0; i < this.wolves.size(); ++i) {
                this.sendMessage(this.createMessage("WOLVES-ROLE", (String)this.wolves.get(i), (String)(i == 0 ? this.wolves.get(1) : this.wolves.get(0))), (String)this.wolves.get(i));
            }
            this.sendChannelMessage(this.createMessage("TWOWOLVES"));
        }
        boolean isWolf = true;
        while (isWolf) {
            this.seer = (int)(Math.random() * (double)this.players.size());
            if (this.wolf[this.seer]) continue;
            isWolf = false;
        }
        this.sendMessage(this.createMessage("SEER-ROLE", this.players.get(this.seer)), this.players.get(this.seer));
        for (int i = 0; i < this.players.size(); ++i) {
            try {
                if (i % 2 == 0) {
                    Thread.sleep(300L);
                }
            }
            catch (Exception x) {
                x.printStackTrace();
            }
            if (this.wolf[i] || i == this.seer) continue;
            this.sendMessage(this.createMessage("VILLAGER-ROLE", this.players.get(i)), this.players.get(i));
        }
    }

    protected boolean checkWin() {
        int i;
        int humanCount = 0;
        int wolfCount = 0;
        for (i = 0; i < this.players.size(); ++i) {
            if (!this.wolf[i] && !this.dead[i] && this.players.get(i) != null) {
                ++humanCount;
                continue;
            }
            if (!this.wolf[i] || this.dead[i]) continue;
            ++wolfCount;
        }
        if (wolfCount == 0) {
            this.playing = false;
            this.sendChannelMessage(this.createMessage("VILLAGERS-WIN"));
            this.sendChannelMessage(this.createMessage("CONGR-VILL"));
            this.day = false;
            for (i = 0; i < this.players.size(); ++i) {
                this.dead[i] = false;
            }
            this.winnerString = "Villagers each win";
            this.endGame();
            return true;
        }
        if (wolfCount == humanCount) {
            int i2;
            this.playing = false;
            if (this.players.size() < 8) {
                String wolfPlayer = "";
                for (i2 = 0; i2 < this.players.size(); ++i2) {
                    if (!this.wolf[i2]) continue;
                    wolfPlayer = this.players.get(i2);
                }
                this.sendChannelMessage(this.createMessage("WOLF-WIN", wolfPlayer));
                this.sendChannelMessage(this.createMessage("CONGR-WOLF", wolfPlayer));
                this.winnerString = "Wolf wins";
            } else {
                String theWolves = (String)this.messages.get("WOLVES-WERE");
                for (i2 = 0; i2 < this.wolves.size(); ++i2) {
                    if (this.wolves.get(i2) == null) continue;
                    theWolves = theWolves + (String)this.wolves.get(i2) + " ";
                }
                this.sendChannelMessage(this.createMessage("WOLVES-WIN"));
                this.sendChannelMessage(this.createMessage("CONGR-WOLVES"));
                this.sendChannelMessage(theWolves);
            }
            for (int i3 = 0; i3 < this.players.size(); ++i3) {
                this.dead[i3] = false;
            }
            this.day = false;
            this.winnerString = wolfCount > 1 ? "Wolves each win" : "Wolf wins";
            this.endGame();
            return true;
        }
        return false;
    }

    protected void playGame() {
        if (this.playing) {
            if (this.timeToVote) {
                for (int i = 0; i < this.players.size(); ++i) {
                    if (this.dead[i] || this.notVoted[i] != 2) continue;
                    this.dead[i] = true;
                    this.sendChannelMessage(this.createMessage("NOT-VOTED", this.players.get(i)));
                    this.sendMessage(this.createMessage("NOT-VOTED-NOTICE", this.players.get(i)), this.players.get(i));
                }
                if (this.checkWin()) {
                    return;
                }
                this.sendChannelMessage(this.createMessage("VOTETIME", null, null, (int)this.voteTime, null));
                if (log.isDebugEnabled()) {
                    log.debug((Object)"WerewolfBot: starting timer for WereTask(): voting");
                }
                this.executor.schedule(new WereTask(this), this.voteTime, TimeUnit.SECONDS);
                if (log.isDebugEnabled()) {
                    log.debug((Object)"WerewolfBot: started timer for WereTask(): voting");
                }
            } else if (this.day) {
                if (this.toSee != -1) {
                    if (!this.dead[this.seer] && !this.dead[this.toSee]) {
                        this.role = this.wolf[this.toSee] ? (String)this.messages.get("ROLE-WOLF") : (String)this.messages.get("ROLE-VILLAGER");
                        this.sendMessage(this.createMessage("SEER-SEE", this.players.get(this.seer), this.players.get(this.toSee)), this.players.get(this.seer));
                    } else if (this.dead[this.seer]) {
                        this.sendMessage(this.createMessage("SEER-SEE-KILLED", this.players.get(this.seer), this.players.get(this.toSee)), this.players.get(this.seer));
                    } else {
                        this.sendMessage(this.createMessage("SEER-SEE-TARGET-KILLED", this.players.get(this.seer), this.players.get(this.toSee)), this.players.get(this.seer));
                    }
                }
                this.sendChannelMessage(this.createMessage("DAYTIME", null, null, (int)this.dayTime, null));
                if (log.isDebugEnabled()) {
                    log.debug((Object)"WerewolfBot: starting timer for WereTask(): daytime");
                }
                this.executor.schedule(new WereTask(this), this.dayTime, TimeUnit.SECONDS);
                if (log.isDebugEnabled()) {
                    log.debug((Object)"WerewolfBot: started timer for WereTask(): daytime");
                }
            } else if (!this.day) {
                if (this.firstNight) {
                    this.firstNight = false;
                    this.sendChannelMessage(this.createMessage("FIRSTNIGHT"));
                } else {
                    this.sendChannelMessage(this.createMessage("NIGHTTIME"));
                }
                if (this.wolves.size() == 1) {
                    this.sendChannelMessage(this.createMessage("WOLF-INSTRUCTIONS", null, null, (int)this.nightTime, null));
                } else {
                    this.sendChannelMessage(this.createMessage("WOLVES-INSTRUCTIONS", null, null, (int)this.nightTime, null));
                }
                if (!this.dead[this.seer]) {
                    this.sendChannelMessage(this.createMessage("SEER-INSTRUCTIONS", null, null, (int)this.nightTime, null));
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)"WerewolfBot: starting timer for WereTask(): nighttime");
                }
                this.executor.schedule(new WereTask(this), this.nightTime, TimeUnit.SECONDS);
                if (log.isDebugEnabled()) {
                    log.debug((Object)"WerewolfBot: started timer for WereTask(): nighttime");
                }
            }
        }
    }

    private class WereTask
    implements Runnable {
        private Werewolf bot;

        WereTask(Werewolf bot) {
            this.bot = bot;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            Werewolf werewolf = this.bot;
            synchronized (werewolf) {
                if (Werewolf.this.getGameState() == BotData.BotStateEnum.PLAYING) {
                    if (Werewolf.this.day) {
                        Werewolf.this.day = !Werewolf.this.day;
                        Werewolf.this.timeToVote = true;
                        Werewolf.this.playGame();
                    } else if (Werewolf.this.timeToVote) {
                        int i;
                        Werewolf.this.timeToVote = false;
                        Werewolf.this.tallyVotes();
                        Werewolf.this.votes = new Vector(1, 1);
                        for (i = 0; i < Werewolf.this.voted.length; ++i) {
                            Werewolf.this.voted[i] = false;
                        }
                        for (i = 0; i < Werewolf.this.wasVoted.length; ++i) {
                            Werewolf.this.wasVoted[i] = 0;
                        }
                        Werewolf.this.toSee = -1;
                        Werewolf.this.checkWin();
                        Werewolf.this.playGame();
                    } else if (!Werewolf.this.day) {
                        Werewolf.this.wolfKill();
                        Werewolf.this.wolfVictim = new Vector(1, 1);
                        Werewolf.this.day = !Werewolf.this.day;
                        Werewolf.this.checkWin();
                        Werewolf.this.playGame();
                    }
                }
            }
        }
    }

    class StartPlay
    implements Runnable {
        Werewolf bot;

        StartPlay(Werewolf bot) {
            this.bot = bot;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            try {
                Werewolf werewolf = this.bot;
                synchronized (werewolf) {
                    BotData.BotStateEnum gameState = this.bot.getGameState();
                    if (gameState == BotData.BotStateEnum.GAME_JOINING) {
                        this.bot.setGameState(BotData.BotStateEnum.GAME_JOIN_ENDED);
                        if (this.bot.getPlayers().size() < this.bot.minPlayers) {
                            this.bot.sendChannelMessage(Werewolf.this.createMessage("JOIN_NO_MIN"));
                            if (log.isDebugEnabled()) {
                                log.debug((Object)("botInstanceID[" + this.bot.getInstanceID() + "]: Join ended. Not enough players."));
                            }
                            this.bot.setGameState(BotData.BotStateEnum.NO_GAME);
                        } else {
                            HashSet<String> copyOfPlayers = new HashSet<String>();
                            copyOfPlayers.addAll(this.bot.players);
                            if (gameState != BotData.BotStateEnum.NO_GAME) {
                                gameState = BotData.BotStateEnum.PLAYING;
                                try {
                                    log.info((Object)("New game started in " + Werewolf.this.channel));
                                    Werewolf.this.setGameState(BotData.BotStateEnum.PLAYING);
                                    if (Werewolf.this.gameStart) {
                                        this.initializePlay();
                                        Werewolf.this.sendChannelMessage(Werewolf.this.createMessage("JOIN_ENDED"));
                                        Werewolf.this.setRoles();
                                        if (Werewolf.this.players.size() >= 5) {
                                            Werewolf.this.day = true;
                                        }
                                        Werewolf.this.playGame();
                                    }
                                }
                                catch (Exception e) {
                                    log.error((Object)("Error creating pot for botInstanceID[" + Werewolf.this.getInstanceID() + "]."), (Throwable)e);
                                    Werewolf.this.setGameState(BotData.BotStateEnum.NO_GAME);
                                    Werewolf.this.sendChannelMessage(Werewolf.this.createMessage("GAME_CANCELED"));
                                }
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug((Object)("botInstanceID[" + this.bot.getInstanceID() + "]: Billing error. Game canceled. No charges."));
                                }
                                Werewolf.this.resetGame();
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                log.error((Object)"Unexpected exception caught in StartPlay.run()", (Throwable)e);
                Werewolf.this.resetGame();
            }
        }

        private void initializePlay() {
            Werewolf.this.gameStart = false;
            Werewolf.this.wolfVictim = new Vector(1, 1);
            Werewolf.this.votes = new Vector(1, 1);
            Werewolf.this.voted = new boolean[Werewolf.this.players.size()];
            Werewolf.this.wolf = new boolean[Werewolf.this.players.size()];
            Werewolf.this.dead = new boolean[Werewolf.this.players.size()];
            Werewolf.this.notVoted = new int[Werewolf.this.players.size()];
            Werewolf.this.wasVoted = new int[Werewolf.this.players.size()];
            for (int i = 0; i < Werewolf.this.players.size(); ++i) {
                Werewolf.this.voted[i] = false;
                Werewolf.this.wolf[i] = false;
                Werewolf.this.dead[i] = false;
                Werewolf.this.notVoted[i] = 0;
                Werewolf.this.wasVoted[i] = 0;
            }
        }
    }

    class StartGame
    implements Runnable {
        Werewolf bot;

        StartGame(Werewolf bot) {
            this.bot = bot;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            Werewolf werewolf = this.bot;
            synchronized (werewolf) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("botInstanceID[" + this.bot.getInstanceID() + "]: in StartGame() "));
                }
                BotData.BotStateEnum gameState = null;
                gameState = this.bot.getGameState();
                if (gameState == BotData.BotStateEnum.GAME_STARTING) {
                    this.bot.setGameState(BotData.BotStateEnum.GAME_STARTED);
                    Werewolf.this.players = new ArrayList<String>(5);
                    this.bot.addPlayer(this.bot.gameStarter);
                    Werewolf.this.priority = new Vector(1, 1);
                    Werewolf.this.wolves = new Vector(1, 1);
                    Werewolf.this.playing = true;
                    Werewolf.this.day = false;
                    Werewolf.this.timeToVote = false;
                    Werewolf.this.gameStart = true;
                    Werewolf.this.firstDay = true;
                    Werewolf.this.firstNight = true;
                    Werewolf.this.toSee = -1;
                    String messageKey = "STARTGAME_FREE";
                    Werewolf.this.sendChannelMessage(Werewolf.this.createMessage(messageKey, this.bot.gameStarter, null, (int)Werewolf.this.timeToJoinGame, null));
                    if (Werewolf.this.players.contains(Werewolf.this.gameStarter)) {
                        Werewolf.this.sendChannelMessage(Werewolf.this.createMessage("JOIN", Werewolf.this.gameStarter));
                    }
                    if (this.bot.timeToJoinGame > 0L) {
                        this.bot.setGameState(BotData.BotStateEnum.GAME_JOINING);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)"WerewolfBot: starting timer for StartPlay()");
                        }
                        Werewolf.this.executor.schedule(new StartPlay(this.bot), this.bot.timeToJoinGame, TimeUnit.SECONDS);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("botInstanceID[" + this.bot.getInstanceID() + "]: scheduled to start play. Awaiting join.. "));
                        }
                    }
                }
            }
        }
    }
}

