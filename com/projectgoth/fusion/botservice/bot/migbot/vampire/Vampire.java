/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.botservice.bot.migbot.vampire;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.bot.migbot.vampire.Vote;
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
public class Vampire
extends Bot {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Vampire.class));
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
    Vector vampires;
    Vector vampireVictim;
    final int JOINTIME = 90;
    final int MINPLAYERS = 5;
    final int MAXPLAYERS = 12;
    final int TWOVAMPIRES = 8;
    long dayTime = 90L;
    long nightTime = 60L;
    long voteTime = 30L;
    int slayer;
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
    boolean[] vampire;
    boolean[] dead;
    boolean[] voted;
    String role;
    String oneVampire;
    String manyVampires;
    String winnerString;

    public Vampire(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDao) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDao);
        this.loadGameConfig();
        log.info((Object)("VampireBot [" + this.instanceID + "] added to channel [" + this.channel + "]"));
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
        this.oneVampire = (String)this.messages.get("1-VAMPIRE");
        this.manyVampires = (String)this.messages.get("MANY-VAMPIRES");
        this.timeAllowedToIdle = this.getLongParameter(TIMER_IDLE, 5L);
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
                messageToSend = messageToSend.replaceAll("ISAVAMPIRE?", this.role);
                messageToSend = messageToSend.replaceAll("ROLE", this.role);
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
                                    boolean isVampire = false;
                                    int vampireIndex = -1;
                                    for (int i = 0; i < this.vampires.size(); ++i) {
                                        if (!this.vampires.get(i).equals(username)) continue;
                                        isVampire = true;
                                        vampireIndex = i;
                                        break;
                                    }
                                    if (isVampire) {
                                        int otherVampire = -1;
                                        if (this.vampires.size() > 1) {
                                            otherVampire = vampireIndex == 0 ? 1 : 0;
                                        }
                                        try {
                                            String victim = messageText.substring(messageText.indexOf(" ") + 1, messageText.length());
                                            victim = victim.trim();
                                            if (this.players.contains(victim)) {
                                                int i;
                                                if (otherVampire != -1 && victim.equals(this.vampires.get(otherVampire))) {
                                                    if (log.isDebugEnabled()) {
                                                        log.debug((Object)("Invalid: " + username + " tried to kill the other vampire " + victim));
                                                    }
                                                    this.sendMessage(this.createMessage("CANT_KILL_OTHER_VAMPIRE", username, victim), username);
                                                    return;
                                                }
                                                boolean isDead = false;
                                                for (i = 0; i < this.players.size(); ++i) {
                                                    if (this.players.get(i) == null || !this.players.get(i).equalsIgnoreCase(victim) || !this.dead[i]) continue;
                                                    isDead = true;
                                                }
                                                if (!isDead) {
                                                    if (!victim.equalsIgnoreCase(username)) {
                                                        while (!this.vampireVictim.add(victim)) {
                                                        }
                                                        if (this.vampires.size() == 1) {
                                                            this.sendMessage(this.createMessage("VAMPIRE-CHOICE", username, victim), username);
                                                        } else {
                                                            this.sendMessage(this.createMessage("VAMPIRES-CHOICE", username, victim), username);
                                                            for (i = 0; i < this.vampires.size(); ++i) {
                                                                if (((String)this.vampires.get(i)).equals(username)) continue;
                                                                this.sendMessage(this.createMessage("VAMPIRES-CHOICE-OTHER" + (i + 1), username, victim), (String)this.vampires.get(i));
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
                                        this.sendMessage(this.createMessage("NOT-VAMPIRE", username), username);
                                    }
                                } else {
                                    this.sendMessage(this.createMessage("NOT_PLAYING", username), username);
                                }
                            }
                        }
                        if (messageText.toLowerCase().startsWith("!s ")) {
                            try {
                                if (this.players.contains(username)) {
                                    if (this.players.get(this.slayer) != null && this.players.get(this.slayer).equals(username)) {
                                        if (this.dead != null && this.dead.length > 0 && !this.dead[this.slayer]) {
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
                                            this.sendMessage(this.createMessage("SLAYER-DEAD", username), username);
                                        }
                                    } else {
                                        this.sendMessage(this.createMessage("NOT-SLAYER", username), username);
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
                        if (this.vampire[i]) {
                            if (this.vampires.size() == 1) {
                                this.sendMessage(this.createMessage("V-ROLE", username), username);
                                continue;
                            }
                            for (int j = 0; j < this.vampires.size(); ++j) {
                                if (username.equals(this.vampires.get(j))) continue;
                                this.sendMessage(this.createMessage("VS-ROLE", username, (String)this.vampires.get(j)), username);
                            }
                            continue;
                        }
                        if (i == this.slayer) {
                            this.sendMessage(this.createMessage("S-ROLE", username), username);
                            continue;
                        }
                        this.sendMessage(this.createMessage("E-ROLE", username), username);
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

    public List<String> getPlayers() {
        return this.players;
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
            message.append(this.createMessage("ADDED_TO_GAME", username));
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
                log.debug((Object)"VampireBot: executing StartGame()");
            }
            this.executor.execute(new StartGame(this));
            if (log.isDebugEnabled()) {
                log.debug((Object)"VampireBot: executed for StartGame()");
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
                message = this.createMessage("STATUS-JOINING", username);
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
                    if (this.vampire[i]) {
                        for (j = 0; j < this.vampires.size(); ++j) {
                            if (!((String)this.vampires.get(j)).equals(username)) continue;
                            this.vampires.remove(j);
                        }
                        this.sendChannelMessage(this.createMessage("FLEE-VAMPIRE", username));
                    } else {
                        this.sendChannelMessage(this.createMessage("FLEE-EXPLORER", username));
                    }
                    this.dead[i] = true;
                    if (this.vampireVictim != null) {
                        for (j = 0; j < this.vampireVictim.size(); ++j) {
                            if (!username.equals(this.vampireVictim.get(j))) continue;
                            this.vampireVictim.set(j, null);
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
            if (guilty == this.slayer) {
                this.sendChannelMessage(this.createMessage("SLAYER-LYNCH", guiltyStr));
                this.role = (String)this.messages.get("ROLE-SLAYER");
            } else if (this.vampire[guilty]) {
                if (this.vampires.size() != 1) {
                    for (int i3 = 0; i3 < this.vampires.size(); ++i3) {
                        if (!guiltyStr.equals((String)this.vampires.get(i3))) continue;
                        this.vampires.remove(i3);
                    }
                }
                this.sendChannelMessage(this.createMessage("VAMPIRE-LYNCH", guiltyStr));
                this.role = (String)this.messages.get("ROLE-VAMPIRE");
            } else {
                this.sendChannelMessage(this.createMessage("EXPLORER-LYNCH", guiltyStr));
                this.role = (String)this.messages.get("ROLE-EXPLORER");
            }
            this.sendChannelMessage(this.createMessage("IS-LYNCHED", guiltyStr));
            if (guilty != this.slayer && guilty > -1 && !this.vampire[guilty]) {
                this.sendMessage(this.createMessage("DYING-BREATH", guiltyStr), guiltyStr);
            }
        } else {
            this.sendChannelMessage(this.createMessage("NO-VOTES"));
        }
    }

    protected void vampireKill() {
        String victim = "";
        if (this.vampireVictim.isEmpty()) {
            this.sendChannelMessage(this.createMessage("NO-KILL"));
            return;
        }
        if (this.vampireVictim.size() == 1) {
            victim = (String)this.vampireVictim.get(0);
        } else if (this.vampireVictim.get(0).equals(this.vampireVictim.get(1))) {
            victim = (String)this.vampireVictim.get(0);
        } else {
            int randChoice = (int)(Math.random() * (double)this.vampireVictim.size());
            victim = (String)this.vampireVictim.get(randChoice);
        }
        for (int i = 0; i < this.players.size(); ++i) {
            if (this.players.get(i) == null || !this.players.get(i).equalsIgnoreCase(victim)) continue;
            if (this.players.get(i) != null) {
                String deadName = this.players.get(i);
                this.dead[i] = true;
                if (i == this.slayer) {
                    this.sendChannelMessage(this.createMessage("SLAYER-KILL", deadName));
                    this.role = (String)this.messages.get("ROLE-SLAYER");
                } else {
                    this.sendChannelMessage(this.createMessage("EXPLORER-KILL", deadName));
                    this.role = (String)this.messages.get("ROLE-EXPLORER");
                }
                this.sendChannelMessage(this.createMessage("IS-KILLED", deadName));
                continue;
            }
            for (int j = 0; j < this.vampires.size(); ++j) {
                this.sendMessage(this.createMessage("VAMPIRE_SELECTION_LEFT", (String)this.vampires.get(j)), (String)this.vampires.get(j));
            }
        }
    }

    protected void setRoles() {
        if (this.players.size() < 5) {
            this.sendChannelMessage(this.createMessage("NOT-ENOUGH"));
            this.playing = false;
            return;
        }
        int randVampire = (int)(Math.random() * (double)this.players.size());
        this.vampires.add(this.players.get(randVampire));
        this.vampire[randVampire] = true;
        if (this.players.size() < 8) {
            this.sendMessage(this.createMessage("VAMPIRE-ROLE", this.players.get(randVampire)), this.players.get(randVampire));
        } else {
            boolean isVampire = true;
            while (isVampire) {
                randVampire = (int)(Math.random() * (double)this.players.size());
                if (this.vampire[randVampire]) continue;
                isVampire = false;
            }
            this.vampires.add(this.players.get(randVampire));
            this.vampire[randVampire] = true;
            for (int i = 0; i < this.vampires.size(); ++i) {
                this.sendMessage(this.createMessage("VAMPIRES-ROLE", (String)this.vampires.get(i), (String)(i == 0 ? this.vampires.get(1) : this.vampires.get(0))), (String)this.vampires.get(i));
            }
            this.sendChannelMessage(this.createMessage("TWOVAMPIRES"));
        }
        boolean isVampire = true;
        while (isVampire) {
            this.slayer = (int)(Math.random() * (double)this.players.size());
            if (this.vampire[this.slayer]) continue;
            isVampire = false;
        }
        this.sendMessage(this.createMessage("SLAYER-ROLE", this.players.get(this.slayer)), this.players.get(this.slayer));
        for (int i = 0; i < this.players.size(); ++i) {
            try {
                if (i % 2 == 0) {
                    Thread.sleep(300L);
                }
            }
            catch (Exception x) {
                x.printStackTrace();
            }
            if (this.vampire[i] || i == this.slayer) continue;
            this.sendMessage(this.createMessage("EXPLORER-ROLE", this.players.get(i)), this.players.get(i));
        }
    }

    protected boolean checkWin() {
        int i;
        int humanCount = 0;
        int vampireCount = 0;
        for (i = 0; i < this.players.size(); ++i) {
            if (!this.vampire[i] && !this.dead[i] && this.players.get(i) != null) {
                ++humanCount;
                continue;
            }
            if (!this.vampire[i] || this.dead[i]) continue;
            ++vampireCount;
        }
        if (vampireCount == 0) {
            this.playing = false;
            this.sendChannelMessage(this.createMessage("EXPLORERS-WIN"));
            this.sendChannelMessage(this.createMessage("CONGR-VILL"));
            this.day = false;
            for (i = 0; i < this.players.size(); ++i) {
                this.dead[i] = false;
            }
            this.winnerString = "Explorers each win";
            this.endGame();
            return true;
        }
        if (vampireCount == humanCount) {
            int i2;
            this.playing = false;
            if (this.players.size() < 8) {
                String vampirePlayer = "";
                for (i2 = 0; i2 < this.players.size(); ++i2) {
                    if (!this.vampire[i2]) continue;
                    vampirePlayer = this.players.get(i2);
                }
                this.sendChannelMessage(this.createMessage("VAMPIRE-WIN", vampirePlayer));
                this.sendChannelMessage(this.createMessage("CONGR-VAMPIRE", vampirePlayer));
                this.winnerString = "Vampire wins";
            } else {
                String theVampires = (String)this.messages.get("VAMPIRES-WERE");
                for (i2 = 0; i2 < this.vampires.size(); ++i2) {
                    if (this.vampires.get(i2) == null) continue;
                    theVampires = theVampires + (String)this.vampires.get(i2) + " ";
                }
                this.sendChannelMessage(this.createMessage("VAMPIRES-WIN"));
                this.sendChannelMessage(this.createMessage("CONGR-VAMPIRES"));
                this.sendChannelMessage(theVampires);
            }
            for (int i3 = 0; i3 < this.players.size(); ++i3) {
                this.dead[i3] = false;
            }
            this.day = false;
            this.winnerString = vampireCount > 1 ? "Vampires each win" : "Vampire wins";
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
                    log.debug((Object)"VampireBot: starting timer for WereTask(): voting");
                }
                this.executor.schedule(new VampireTask(this), this.voteTime, TimeUnit.SECONDS);
                if (log.isDebugEnabled()) {
                    log.debug((Object)"VampireBot: started timer for WereTask(): voting");
                }
            } else if (this.day) {
                if (this.toSee != -1) {
                    if (!this.dead[this.slayer] && !this.dead[this.toSee]) {
                        this.role = this.vampire[this.toSee] ? (String)this.messages.get("ROLE-VAMPIRE") : (String)this.messages.get("ROLE-EXPLORER");
                        this.sendMessage(this.createMessage("SLAYER-SEE", this.players.get(this.slayer), this.players.get(this.toSee)), this.players.get(this.slayer));
                    } else if (this.dead[this.slayer]) {
                        this.sendMessage(this.createMessage("SLAYER-SEE-KILLED", this.players.get(this.slayer), this.players.get(this.toSee)), this.players.get(this.slayer));
                    } else {
                        this.sendMessage(this.createMessage("SLAYER-SEE-TARGET-KILLED", this.players.get(this.slayer), this.players.get(this.toSee)), this.players.get(this.slayer));
                    }
                }
                this.sendChannelMessage(this.createMessage("DAYTIME", null, null, (int)this.dayTime, null));
                if (log.isDebugEnabled()) {
                    log.debug((Object)"VampireBot: starting timer for WereTask(): daytime");
                }
                this.executor.schedule(new VampireTask(this), this.dayTime, TimeUnit.SECONDS);
                if (log.isDebugEnabled()) {
                    log.debug((Object)"VampireBot: started timer for WereTask(): daytime");
                }
            } else if (!this.day) {
                if (this.firstNight) {
                    this.firstNight = false;
                    this.sendChannelMessage(this.createMessage("FIRSTNIGHT"));
                } else {
                    this.sendChannelMessage(this.createMessage("NIGHTTIME"));
                }
                if (this.vampires.size() == 1) {
                    this.sendChannelMessage(this.createMessage("VAMPIRE-INSTRUCTIONS", null, null, (int)this.nightTime, null));
                } else {
                    this.sendChannelMessage(this.createMessage("VAMPIRES-INSTRUCTIONS", null, null, (int)this.nightTime, null));
                }
                if (!this.dead[this.slayer]) {
                    this.sendChannelMessage(this.createMessage("SLAYER-INSTRUCTIONS", null, null, (int)this.nightTime, null));
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)"VampireBot: starting timer for WereTask(): nighttime");
                }
                this.executor.schedule(new VampireTask(this), this.nightTime, TimeUnit.SECONDS);
                if (log.isDebugEnabled()) {
                    log.debug((Object)"VampireBot: started timer for WereTask(): nighttime");
                }
            }
        }
    }

    private class VampireTask
    implements Runnable {
        private Vampire bot;

        VampireTask(Vampire bot) {
            this.bot = bot;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            Vampire vampire = this.bot;
            synchronized (vampire) {
                if (Vampire.this.getGameState() == BotData.BotStateEnum.PLAYING) {
                    if (Vampire.this.day) {
                        Vampire.this.day = !Vampire.this.day;
                        Vampire.this.timeToVote = true;
                        Vampire.this.playGame();
                    } else if (Vampire.this.timeToVote) {
                        int i;
                        Vampire.this.timeToVote = false;
                        Vampire.this.tallyVotes();
                        Vampire.this.votes = new Vector(1, 1);
                        for (i = 0; i < Vampire.this.voted.length; ++i) {
                            Vampire.this.voted[i] = false;
                        }
                        for (i = 0; i < Vampire.this.wasVoted.length; ++i) {
                            Vampire.this.wasVoted[i] = 0;
                        }
                        Vampire.this.toSee = -1;
                        Vampire.this.checkWin();
                        Vampire.this.playGame();
                    } else if (!Vampire.this.day) {
                        Vampire.this.vampireKill();
                        Vampire.this.vampireVictim = new Vector(1, 1);
                        Vampire.this.day = !Vampire.this.day;
                        Vampire.this.checkWin();
                        Vampire.this.playGame();
                    }
                }
            }
        }
    }

    class StartPlay
    implements Runnable {
        Vampire bot;

        StartPlay(Vampire bot) {
            this.bot = bot;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            try {
                Vampire vampire = this.bot;
                synchronized (vampire) {
                    BotData.BotStateEnum gameState = this.bot.getGameState();
                    if (gameState == BotData.BotStateEnum.GAME_JOINING) {
                        this.bot.setGameState(BotData.BotStateEnum.GAME_JOIN_ENDED);
                        if (this.bot.getPlayers().size() < this.bot.minPlayers) {
                            this.bot.sendChannelMessage(Vampire.this.createMessage("JOIN_NO_MIN"));
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
                                    log.info((Object)("New game started in " + Vampire.this.channel));
                                    Vampire.this.setGameState(BotData.BotStateEnum.PLAYING);
                                    if (Vampire.this.gameStart) {
                                        this.initializePlay();
                                        Vampire.this.sendChannelMessage(Vampire.this.createMessage("JOIN_ENDED"));
                                        Vampire.this.setRoles();
                                        if (Vampire.this.players.size() >= 5) {
                                            Vampire.this.day = true;
                                        }
                                        Vampire.this.playGame();
                                    }
                                }
                                catch (Exception e) {
                                    log.error((Object)("Error creating pot for botInstanceID[" + Vampire.this.getInstanceID() + "]."), (Throwable)e);
                                    Vampire.this.setGameState(BotData.BotStateEnum.NO_GAME);
                                    Vampire.this.sendChannelMessage(Vampire.this.createMessage("GAME_CANCELED"));
                                }
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug((Object)("botInstanceID[" + this.bot.getInstanceID() + "]: Billing error. Game canceled. No charges."));
                                }
                                Vampire.this.resetGame();
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                log.error((Object)"Unexpected exception caught in StartPlay.run()", (Throwable)e);
                Vampire.this.resetGame();
            }
        }

        private void initializePlay() {
            Vampire.this.gameStart = false;
            Vampire.this.vampireVictim = new Vector(1, 1);
            Vampire.this.votes = new Vector(1, 1);
            Vampire.this.voted = new boolean[Vampire.this.players.size()];
            Vampire.this.vampire = new boolean[Vampire.this.players.size()];
            Vampire.this.dead = new boolean[Vampire.this.players.size()];
            Vampire.this.notVoted = new int[Vampire.this.players.size()];
            Vampire.this.wasVoted = new int[Vampire.this.players.size()];
            for (int i = 0; i < Vampire.this.players.size(); ++i) {
                Vampire.this.voted[i] = false;
                Vampire.this.vampire[i] = false;
                Vampire.this.dead[i] = false;
                Vampire.this.notVoted[i] = 0;
                Vampire.this.wasVoted[i] = 0;
            }
        }
    }

    class StartGame
    implements Runnable {
        Vampire bot;

        StartGame(Vampire bot) {
            this.bot = bot;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            Vampire vampire = this.bot;
            synchronized (vampire) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("botInstanceID[" + this.bot.getInstanceID() + "]: in StartGame() "));
                }
                BotData.BotStateEnum gameState = null;
                gameState = this.bot.getGameState();
                if (gameState == BotData.BotStateEnum.GAME_STARTING) {
                    this.bot.setGameState(BotData.BotStateEnum.GAME_STARTED);
                    Vampire.this.players = new ArrayList<String>(5);
                    this.bot.addPlayer(this.bot.gameStarter);
                    Vampire.this.priority = new Vector(1, 1);
                    Vampire.this.vampires = new Vector(1, 1);
                    Vampire.this.playing = true;
                    Vampire.this.day = false;
                    Vampire.this.timeToVote = false;
                    Vampire.this.gameStart = true;
                    Vampire.this.firstDay = true;
                    Vampire.this.firstNight = true;
                    Vampire.this.toSee = -1;
                    String messageKey = "STARTGAME_FREE";
                    Vampire.this.sendChannelMessage(Vampire.this.createMessage(messageKey, this.bot.gameStarter, null, (int)Vampire.this.timeToJoinGame, null));
                    if (Vampire.this.players.contains(Vampire.this.gameStarter)) {
                        Vampire.this.sendChannelMessage(Vampire.this.createMessage("JOIN", Vampire.this.gameStarter));
                    }
                    if (this.bot.timeToJoinGame > 0L) {
                        this.bot.setGameState(BotData.BotStateEnum.GAME_JOINING);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)"VampireBot: starting timer for StartPlay()");
                        }
                        Vampire.this.executor.schedule(new StartPlay(this.bot), this.bot.timeToJoinGame, TimeUnit.SECONDS);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("botInstanceID[" + this.bot.getInstanceID() + "]: scheduled to start play. Awaiting join.. "));
                        }
                    }
                }
            }
        }
    }
}

