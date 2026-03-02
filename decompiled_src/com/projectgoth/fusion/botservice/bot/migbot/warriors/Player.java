/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot.migbot.warriors;

import com.projectgoth.fusion.botservice.bot.migbot.warriors.Attack;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.leaderboard.Leaderboard;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class Player
implements Comparable<Player> {
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Player.class));
    public static final int MAX_CONSECUTIVE_GAME_FOR_MODE_CHANGE = 3;
    public static final int MAX_CONSECUTIVE_GAME_IN_MODE_CHANGE = 3;
    boolean isChallenger = false;
    boolean hasAcceptedChallenge = false;
    ModeEnum mode = ModeEnum.NORMAL;
    String username;
    UserData userData;
    int migLevel;
    int HP;
    int maxHP;
    double potEntry;
    int gamesPlayed = -1;
    MilestoneEnum lastMilestone;
    int sequence;
    boolean isInChannel;
    private List<Attack> normalAttacks;
    private List<Attack> berserkAttacks;

    Player(String username, List<Attack> normalAttacks, List<Attack> berserkAttacks) {
        this.username = username;
        this.normalAttacks = normalAttacks;
        this.berserkAttacks = berserkAttacks;
        this.lastMilestone = MilestoneEnum.NONE;
        this.userData = null;
        this.migLevel = 0;
        this.HP = 0;
        this.maxHP = 0;
        this.potEntry = 0.0;
        this.sequence = 0;
        this.isInChannel = true;
    }

    public synchronized void setInChannel(boolean flag) {
        this.isInChannel = flag;
    }

    public synchronized boolean isInChannel() {
        return this.isInChannel;
    }

    public boolean isFever() {
        return this.mode == ModeEnum.FEVER;
    }

    public boolean isBerserk() {
        return this.mode == ModeEnum.BERSERK;
    }

    public void changeMode(ModeEnum newMode) {
        this.mode = newMode;
    }

    public String getPlayerEmote() {
        switch (this.mode) {
            case NORMAL: {
                return String.format("(warriors-num-n-%d)", this.sequence);
            }
            case FEVER: {
                return String.format("(warriors-num-d-%d)", this.sequence);
            }
            case BERSERK: {
                return String.format("(warriors-num-a-%d)", this.sequence);
            }
        }
        return " ";
    }

    public Attack getAttack() {
        List<Attack> attackList = this.isBerserk() ? this.berserkAttacks : this.normalAttacks;
        double random = Math.random();
        double probability_sum = 0.0;
        for (Attack att : attackList) {
            if (!(random <= (probability_sum += att.probability))) continue;
            return att;
        }
        return attackList.get(0);
    }

    @Override
    public int compareTo(Player player2) {
        if (this.HP == player2.HP) {
            if (this.userData.broadcastList.size() == player2.userData.broadcastList.size()) {
                if (this.gamesPlayed == -1 || player2.gamesPlayed == -1) {
                    ArrayList<String> usernames = new ArrayList<String>(2);
                    ArrayList<Integer> userids = new ArrayList<Integer>(2);
                    if (this.gamesPlayed == -1) {
                        usernames.add(this.username);
                        userids.add(this.userData.userID);
                    }
                    if (player2.gamesPlayed == -1) {
                        usernames.add(player2.username);
                        userids.add(player2.userData.userID);
                    }
                    try {
                        List<Integer> gamesPlayedStats = Leaderboard.getGamesMetric(Leaderboard.Type.WARRIORS_GAMES_PLAYED, Leaderboard.Period.WEEKLY, usernames, userids);
                        int index = 0;
                        if (this.gamesPlayed == -1) {
                            this.gamesPlayed = gamesPlayedStats.get(index);
                            ++index;
                        }
                        if (player2.gamesPlayed == -1) {
                            player2.gamesPlayed = gamesPlayedStats.get(index);
                        }
                    }
                    catch (Exception e) {
                        log.warn((Object)String.format("Failed to get weekly games played for Warriors to compare players [%s] [%s]", this.username, player2.username));
                        return Math.random() > 0.5 ? -1 : 1;
                    }
                }
                if (this.gamesPlayed == player2.gamesPlayed) {
                    return Math.random() > 0.5 ? -1 : 1;
                }
                return this.gamesPlayed - player2.gamesPlayed;
            }
            return this.userData.broadcastList.size() - player2.userData.broadcastList.size();
        }
        return this.HP - player2.HP;
    }

    public String getMilestoneMessageKey() {
        String messageKey = null;
        if (this.HP < 10) {
            if (this.lastMilestone != MilestoneEnum.D10) {
                messageKey = "MILESTONE_D10";
                this.lastMilestone = MilestoneEnum.D10;
            }
        } else if ((double)this.HP <= 0.25 * (double)this.maxHP) {
            if (this.lastMilestone != MilestoneEnum.P25) {
                messageKey = "MILESTONE_P25";
                this.lastMilestone = MilestoneEnum.P25;
            }
        } else if ((double)this.HP <= 0.5 * (double)this.maxHP) {
            if (this.lastMilestone != MilestoneEnum.P50) {
                messageKey = "MILESTONE_P50";
                this.lastMilestone = MilestoneEnum.P50;
            }
        } else if ((double)this.HP <= 0.75 * (double)this.maxHP && this.lastMilestone != MilestoneEnum.P75) {
            messageKey = "MILESTONE_P75";
            this.lastMilestone = MilestoneEnum.P75;
        }
        return messageKey;
    }

    public static boolean isUserInMode(String username, ModeEnum modeToCheck) {
        MemCachedKeySpaces.CommonKeySpace keySpace = modeToCheck == ModeEnum.FEVER ? MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_FEVER : MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_BERSERK;
        long count = MemCachedClientWrapper.getCounter(keySpace, username);
        return count >= 3L && count < 6L;
    }

    public static ModeEnum getMode(String username) {
        if (Player.isUserInMode(username, ModeEnum.BERSERK)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("username '%s' in mode '%s'", username, "BERSERK"));
            }
            return ModeEnum.BERSERK;
        }
        if (Player.isUserInMode(username, ModeEnum.FEVER)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("username '%s' in mode '%s'", username, "FEVER"));
            }
            return ModeEnum.FEVER;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("username '%s' in mode '%s'", username, "NORMAL"));
        }
        return ModeEnum.NORMAL;
    }

    public String getModeDisplay() {
        switch (this.mode) {
            case BERSERK: {
                return "(Berserk)";
            }
            case FEVER: {
                return "(Fever)";
            }
        }
        return "";
    }

    private ModeEnum checkAndUpdateMode(ModeEnum modeToCheck) {
        MemCachedKeySpaces.CommonKeySpace keySpace = modeToCheck == ModeEnum.FEVER ? MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_FEVER : MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_BERSERK;
        long count = MemCachedClientWrapper.getCounter(keySpace, this.username);
        if (count >= 3L) {
            if (count == 3L) {
                MemCachedClientWrapper.set(keySpace, this.username, 4L);
            } else {
                if (count >= 6L) {
                    MemCachedClientWrapper.delete(keySpace, this.username);
                    return ModeEnum.NORMAL;
                }
                MemCachedClientWrapper.incr(keySpace, this.username);
            }
            return modeToCheck;
        }
        if (count > 0L) {
            return ModeEnum.NORMAL;
        }
        return null;
    }

    public void enterGameUpdateMode() {
        ModeEnum m = this.checkAndUpdateMode(ModeEnum.BERSERK);
        if (m == null) {
            m = this.checkAndUpdateMode(ModeEnum.FEVER);
        }
        if (m == null) {
            m = ModeEnum.NORMAL;
        }
        this.changeMode(m);
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("enter game mode stat '%s' FEVER %d, BERSERK %d", this.username, MemCachedClientWrapper.getCounter(MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_FEVER, this.username), MemCachedClientWrapper.getCounter(MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_BERSERK, this.username)));
        }
    }

    public long endGameUpdateModeStat(boolean winner) {
        MemCachedKeySpaces.CommonKeySpace keySpace = winner ? MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_FEVER : MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_BERSERK;
        long count = MemCachedClientWrapper.getCounter(keySpace, this.username);
        if (this.mode == ModeEnum.NORMAL) {
            if (count > 0L) {
                count = MemCachedClientWrapper.incr(keySpace, this.username);
            } else {
                MemCachedClientWrapper.set(keySpace, this.username, 1L);
                MemCachedKeySpaces.CommonKeySpace otherKeySpace = !winner ? MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_FEVER : MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_BERSERK;
                MemCachedClientWrapper.delete(otherKeySpace, this.username);
                count = 1L;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("end game mode stat '%s' FEVER %d, BERSERK %d", this.username, MemCachedClientWrapper.getCounter(MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_FEVER, this.username), MemCachedClientWrapper.getCounter(MemCachedKeySpaces.CommonKeySpace.BOT_WARRIORS_BERSERK, this.username)));
        }
        return count;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum MilestoneEnum {
        NONE,
        D10,
        P25,
        P50,
        P75;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum ModeEnum {
        NORMAL,
        FEVER,
        BERSERK;

    }
}

