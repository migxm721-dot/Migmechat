/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.PipelineBlock
 */
package com.projectgoth.fusion.leaderboard;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.PipelineBlock;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Leaderboard {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Leaderboard.class));

    public static void insert(Type leaderboardType, Period leaderboardPeriod, int userID, String username, double amount) throws Exception {
        String redisKey = leaderboardType.append(leaderboardPeriod.toString());
        String setMember = Integer.toString(userID) + ':' + username;
        Jedis masterInst = Redis.getLeaderboardsMasterInstance();
        masterInst.zadd(redisKey, amount, setMember);
        Redis.disconnect(masterInst, log);
    }

    public static void increment(Type leaderboardType, Period leaderboardPeriod, int userID, String username, double amount) throws Exception {
        String redisKey = leaderboardType.append(leaderboardPeriod.toString());
        String setMember = Integer.toString(userID) + ':' + username;
        Jedis masterInst = Redis.getLeaderboardsMasterInstance();
        masterInst.zincrby(redisKey, amount, setMember);
        Redis.disconnect(masterInst, log);
    }

    public static void setLastLoggedIn() throws Exception {
    }

    public static void freeLastLoggedIn() throws Exception {
        String lastLoggedInKey = "LastLoggedIn";
        Jedis masterInst = Redis.getLeaderboardsMasterInstance();
        masterInst.del(lastLoggedInKey);
        Redis.disconnect(masterInst, log);
    }

    public static void allTimeCleanUp(Type leaderboardType) throws Exception {
    }

    public static void reset(Type leaderboardType, Period leaderboardPeriod, Period leaderboardPreviousPeriod) throws Exception {
        String redisKey = leaderboardType.append(leaderboardPeriod.toString());
        String previousRedisKey = leaderboardType.append(leaderboardPreviousPeriod.toString());
        try {
            Jedis masterInst = Redis.getLeaderboardsMasterInstance();
            masterInst.rename(redisKey, previousRedisKey);
            Redis.disconnect(masterInst, log);
        }
        catch (Exception e) {
            log.info((Object)("[ERROR] Unable to rename " + previousRedisKey + " from " + redisKey));
        }
    }

    public static void recordGamesMetric(Type leaderboardType, List<String> usernames) throws Exception {
        User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        ArrayList<Integer> userids = new ArrayList<Integer>(usernames.size());
        for (int i = 0; i < usernames.size(); ++i) {
            String username = usernames.get(i);
            int userid = userBean.getUserID(username, null);
            userids.add(userid);
        }
        Leaderboard.recordGamesMetric(leaderboardType, usernames, userids);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void recordGamesMetric(Type leaderboardType, List<String> usernames, List<Integer> userids) throws Exception {
        if (usernames.size() != userids.size()) {
            log.warn((Object)String.format("Incorrect parameters to recordGamesMetric for key [%s], %d usernames but %d userids. Recalculate userids from UserBean.", leaderboardType.value, usernames.size(), userids.size()));
            Leaderboard.recordGamesMetric(leaderboardType, usernames);
            return;
        }
        final String redisKeyDaily = leaderboardType.append(Period.DAILY.toString());
        final String redisKeyWeekly = leaderboardType.append(Period.WEEKLY.toString());
        Jedis masterInst = null;
        final ArrayList<String> userKeys = new ArrayList<String>(usernames.size());
        for (int i = 0; i < usernames.size(); ++i) {
            String username = usernames.get(i);
            int userid = userids.get(i);
            userKeys.add(username + ":" + userid);
        }
        try {
            masterInst = Redis.getLeaderboardsMasterInstance();
            masterInst.pipelined(new PipelineBlock(){

                public void execute() {
                    for (int j = 0; j < userKeys.size(); ++j) {
                        String userKey = (String)userKeys.get(j);
                        this.zincrby(redisKeyDaily, 1.0, userKey);
                        this.zincrby(redisKeyWeekly, 1.0, userKey);
                    }
                }
            });
        }
        catch (Exception e) {
            log.info((Object)("[ERROR] Unable to recordGamesMetric for key [" + leaderboardType.value + "]"));
        }
        finally {
            Redis.disconnect(masterInst, log);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void recordGamesMetric(Type leaderboardType, String username, int userId, int amount) {
        String redisKeyDaily = leaderboardType.append(Period.DAILY.toString());
        String redisKeyWeekly = leaderboardType.append(Period.WEEKLY.toString());
        String userKey = username + ":" + userId;
        Jedis masterInstance = null;
        try {
            masterInstance = Redis.getLeaderboardsMasterInstance();
            masterInstance.zincrby(redisKeyDaily, (double)amount, userKey);
            masterInstance.zincrby(redisKeyWeekly, (double)amount, userKey);
        }
        catch (Exception e) {
            log.error((Object)"Error getting master instance of redis to update leaderboard");
        }
        finally {
            Redis.disconnect(masterInstance, log);
        }
    }

    public static List<Integer> getGamesMetric(Type leaderboardType, Period period, List<String> usernames) throws Exception {
        User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        ArrayList<Integer> userids = new ArrayList<Integer>(usernames.size());
        for (int i = 0; i < usernames.size(); ++i) {
            String username = usernames.get(i);
            int userid = userBean.getUserID(username, null);
            userids.add(userid);
        }
        return Leaderboard.getGamesMetric(leaderboardType, period, usernames, userids);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<Integer> getGamesMetric(Type leaderboardType, Period period, List<String> usernames, List<Integer> userids) throws Exception {
        if (usernames.size() != userids.size()) {
            log.warn((Object)String.format("Incorrect parameters to getGamesMetric for key [%s] period [%s], %d usernames but %d userids. Recalculate userids from UserBean.", leaderboardType.value, period.value, usernames.size(), userids.size()));
            return Leaderboard.getGamesMetric(leaderboardType, period, usernames);
        }
        final String redisKey = leaderboardType.append(period.toString());
        Jedis masterInst = null;
        final ArrayList<String> userKeys = new ArrayList<String>(usernames.size());
        for (int i = 0; i < usernames.size(); ++i) {
            String username = usernames.get(i);
            int userid = userids.get(i);
            userKeys.add(username + ":" + userid);
        }
        try {
            masterInst = Redis.getLeaderboardsMasterInstance();
            List results = masterInst.pipelined(new PipelineBlock(){

                public void execute() {
                    for (int j = 0; j < userKeys.size(); ++j) {
                        String userKey = (String)userKeys.get(j);
                        this.zscore(redisKey, userKey);
                    }
                }
            });
            ArrayList<Integer> intResults = new ArrayList<Integer>(results.size());
            for (Object o : results) {
                intResults.add(((Double)o).intValue());
            }
            ArrayList<Integer> arrayList = intResults;
            return arrayList;
        }
        catch (Exception e) {
            log.info((Object)("[ERROR] Unable to getGamesMetric for key [" + redisKey + "] " + e.getMessage()));
            List<Integer> list = null;
            return list;
        }
        finally {
            Redis.disconnect(masterInst, log);
        }
    }

    public static void updateMigLevel() throws Exception {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void sendVirtualGift(String senderUsername, String receiverUsername) throws Exception {
        if (!SystemProperty.getBool("EnableLeaderboards", false)) {
            return;
        }
        User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        int senderUserID = userBean.getUserID(senderUsername, null);
        int receiverUserID = userBean.getUserID(receiverUsername, null);
        final String senderUserKey = senderUsername + ":" + senderUserID;
        final String receiverUserKey = receiverUsername + ":" + receiverUserID;
        Type giftSentLBType = Type.GIFT_SENT;
        Type giftReceivedLBType = Type.GIFT_RECEIVED;
        final String giftSentDailyRedisKey = giftSentLBType.append(Period.DAILY.toString());
        final String giftSentWeeklyRedisKey = giftSentLBType.append(Period.WEEKLY.toString());
        final String giftReceivedDailyRedisKey = giftReceivedLBType.append(Period.DAILY.toString());
        final String giftReceivedWeeklyRedisKey = giftReceivedLBType.append(Period.WEEKLY.toString());
        Jedis masterInst = null;
        try {
            masterInst = Redis.getLeaderboardsMasterInstance();
            masterInst.pipelined(new PipelineBlock(){

                public void execute() {
                    this.zincrby(giftSentDailyRedisKey, 1.0, senderUserKey);
                    this.zincrby(giftSentWeeklyRedisKey, 1.0, senderUserKey);
                    this.zincrby(giftReceivedDailyRedisKey, 1.0, receiverUserKey);
                    this.zincrby(giftReceivedWeeklyRedisKey, 1.0, receiverUserKey);
                }
            });
        }
        catch (Exception e) {
            log.error((Object)("Pipeline execution error during updating of GiftSent/GiftReceived Daily/Weekly Leaderboards: " + e.getMessage()));
        }
        finally {
            Redis.disconnect(masterInst, log);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void updateReferrerLeaderboards(String referrerUsername, int referrerUserID) throws Exception {
        if (!SystemProperty.getBool("EnableLeaderboards", false)) {
            return;
        }
        final String referrerUserKey = referrerUsername + ":" + referrerUserID;
        Type referrerLBType = Type.REFERRER;
        final String referrerDailyRedisKey = referrerLBType.append(Period.DAILY.toString());
        final String referrerWeeklyRedisKey = referrerLBType.append(Period.WEEKLY.toString());
        Jedis masterInst = null;
        try {
            masterInst = Redis.getLeaderboardsMasterInstance();
            masterInst.pipelined(new PipelineBlock(){

                public void execute() {
                    this.zincrby(referrerDailyRedisKey, 1.0, referrerUserKey);
                    this.zincrby(referrerWeeklyRedisKey, 1.0, referrerUserKey);
                }
            });
        }
        catch (Exception e) {
            log.error((Object)("Pipeline execution error during updating of Referrer Daily/Weekly Leaderboards: " + e.getMessage()));
        }
        finally {
            Redis.disconnect(masterInst, log);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Period {
        DAILY("Daily"),
        WEEKLY("Weekly"),
        ALL_TIME("AllTime"),
        PREVIOUS_DAILY("PreviousDaily"),
        PREVIOUS_WEEKLY("PreviousWeekly");

        private String value;

        private Period(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Type {
        LOW_CARD_GAMES_PLAYED("LB:GamesPlayed:LowCard:"),
        DICE_GAMES_PLAYED("LB:GamesPlayed:Dice:"),
        DANGER_GAMES_PLAYED("LB:GamesPlayed:Danger:"),
        CRICKET_GAMES_PLAYED("LB:GamesPlayed:Cricket:"),
        FOOTBALL_GAMES_PLAYED("LB:GamesPlayed:Football:"),
        GUESS_GAMES_PLAYED("LB:GamesPlayed:Guess:"),
        WARRIORS_GAMES_PLAYED("LB:GamesPlayed:Warriors:"),
        LOW_CARD_MOST_WINS("LB:MostWins:LowCard:"),
        DICE_MOST_WINS("LB:MostWins:Dice:"),
        DANGER_MOST_WINS("LB:MostWins:Danger:"),
        CRICKET_MOST_WINS("LB:MostWins:Cricket:"),
        FOOTBALL_MOST_WINS("LB:MostWins:Football:"),
        GUESS_MOST_WINS("LB:MostWins:Guess:"),
        WARRIORS_MOST_WINS("LB:MostWins:Warriors:"),
        WARRIORS_NUM_KILLS("LB:NumKills:Warriors:"),
        USER_LIKES("LB:UserLikes:"),
        MIG_LEVEL("LB:MigLevel:"),
        REFERRER("LB:Referrer:"),
        GIFT_SENT("LB:GiftSent:"),
        GIFT_RECEIVED("LB:GiftReceived:"),
        AVATAR_VOTES("LB:AvatarVotes:"),
        PAINT_WARS_PAINT_POINTS("LB:PaintPoints:");

        public static final Type[] TOTAL_PLAYED_LEADERBOARDS;
        private String value;

        private Type(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }

        public String append(String s) {
            return this.value + s;
        }

        static {
            TOTAL_PLAYED_LEADERBOARDS = new Type[]{LOW_CARD_GAMES_PLAYED, DICE_GAMES_PLAYED, DANGER_GAMES_PLAYED, CRICKET_GAMES_PLAYED, FOOTBALL_GAMES_PLAYED, GUESS_GAMES_PLAYED, WARRIORS_GAMES_PLAYED};
        }
    }
}

