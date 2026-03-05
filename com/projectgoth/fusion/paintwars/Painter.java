/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.Tuple
 */
package com.projectgoth.fusion.paintwars;

import com.projectgoth.fusion.botservice.bot.migbot.common.LimitTracker;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.leaderboard.Leaderboard;
import com.projectgoth.fusion.paintwars.ItemData;
import com.projectgoth.fusion.paintwars.PainterStats;
import com.projectgoth.fusion.slice.FusionException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Painter {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Painter.class));
    private static final int DAY_IN_SECONDS = 86400;
    private static final int RECORD_EXPIRY_TIME = 2592000;
    private static final int INVENTORY_LIMIT = 30;
    private static final int DEFAULT_REQUIRED_LEVEL = 1;
    private static int freePaintsPerDay = 3;
    private static int freeCleansPerDay = 2;
    private static final String CURRENCY = "USD";
    private static double priceOfPaintCredit = 0.01;
    private static double priceOfCleanCredit = 0.02;
    private static double priceOfIdenticon = 0.01;
    private static int paintsSentEligibility = 35;
    private static int cleansSentEligibility = 21;
    private static final String PRICE_OF_PAINT_CREDIT = "PriceOfPaintCredit";
    private static final String PRICE_OF_CLEAN_CREDIT = "PriceOfCleanCredit";
    private static final String PRICE_OF_IDENTICON = "PriceOfIdenticon";
    private static final String FREE_PAINTS_PER_DAY = "FreePaintsPerDay";
    private static final String FREE_CLEANS_PER_DAY = "FreeCleansPerDay";
    private static final String PAINTS_SENT_ELIGIBILITY = "PaintsSentEligibility";
    private static final String CLEANS_SENT_ELIGIBILITY = "CleansSentEligibility";

    public static int paint(String user1, String user2) throws FusionException {
        int userId = Painter.getUserId(user1);
        int targetUserId = Painter.getUserId(user2);
        long currentTime = Painter.getCurrentTimeInSeconds();
        String key = "";
        Jedis masterInstance = null;
        try {
            masterInstance = Redis.getMasterInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        key = KeySpace.USER.toString() + userId;
        int points = Painter.calculatePoints(user1, user2, true, Painter.isClean(user1));
        if (Painter.hasDualPaint(user1)) {
            points *= 2;
        }
        masterInstance.hincrBy(key, "TotalPaintWarsPoints", (long)points);
        masterInstance.hincrBy(key, "TotalPaintsSent", 1L);
        key = KeySpace.U.toString() + userId + ":" + KeySpace.PAINTS_SENT.toString();
        masterInstance.zadd(key, (double)currentTime, targetUserId + ":" + user2 + ":" + currentTime);
        Redis.disconnect(masterInstance, log);
        Leaderboard.recordGamesMetric(Leaderboard.Type.PAINT_WARS_PAINT_POINTS, user1, userId, points);
        Painter.checkRaffleEligibility(user1, userId, key, currentTime, paintsSentEligibility);
        try {
            masterInstance = Redis.getMasterInstanceForUserID(targetUserId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        key = KeySpace.USER.toString() + targetUserId;
        masterInstance.hincrBy(key, "TotalPaintsReceived", 1L);
        key = KeySpace.U.toString() + targetUserId + ":" + KeySpace.PAINTS_RECEIVED.toString();
        masterInstance.zadd(key, (double)currentTime, userId + ":" + user1 + ":" + currentTime);
        Redis.disconnect(masterInstance, log);
        log.info((Object)(user1 + " painted " + user2 + " and received " + points + " points"));
        return points;
    }

    public static int clean(String user1, String user2) throws FusionException {
        int userId = Painter.getUserId(user1);
        int targetUserId = Painter.getUserId(user2);
        long currentTime = Painter.getCurrentTimeInSeconds();
        String key = "";
        Jedis masterInstance = null;
        try {
            masterInstance = Redis.getMasterInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        key = KeySpace.USER.toString() + userId;
        int points = Painter.calculatePoints(user1, user2, false, Painter.isClean(user1));
        masterInstance.hincrBy(key, "TotalPaintWarsPoints", (long)points);
        masterInstance.hincrBy(key, "TotalCleansSent", 1L);
        key = KeySpace.U.toString() + userId + ":" + KeySpace.CLEANS_SENT.toString();
        masterInstance.zadd(key, (double)currentTime, targetUserId + ":" + user2 + ":" + currentTime);
        Redis.disconnect(masterInstance, log);
        Leaderboard.recordGamesMetric(Leaderboard.Type.PAINT_WARS_PAINT_POINTS, user1, userId, points);
        Painter.checkRaffleEligibility(user1, userId, key, currentTime, cleansSentEligibility);
        try {
            masterInstance = Redis.getMasterInstanceForUserID(targetUserId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        key = KeySpace.USER.toString() + targetUserId;
        masterInstance.hincrBy(key, "TotalCleansReceived", 1L);
        key = KeySpace.U.toString() + targetUserId + ":" + KeySpace.CLEANS_RECEIVED.toString();
        masterInstance.zadd(key, (double)currentTime, userId + ":" + user1 + ":" + currentTime);
        Redis.disconnect(masterInstance, log);
        log.info((Object)(user1 + " cleaned " + user2 + " and received " + points + " points"));
        return points;
    }

    public static boolean isClean(String user) throws FusionException {
        return Painter.isClean(user, null);
    }

    public static boolean isClean(String user, Integer userId) throws FusionException {
        long previousTime;
        if (userId == null) {
            userId = Painter.getUserId(user);
        }
        double latestPaintTimestamp = 0.0;
        double latestCleanTimestamp = 0.0;
        String key = "";
        key = KeySpace.U.toString() + userId + ":" + KeySpace.PAINTS_RECEIVED.toString();
        Set<Tuple> paintsReceived = Painter.getSortedSet(userId, key, 0, 0);
        Iterator<Tuple> itr = paintsReceived.iterator();
        if (!itr.hasNext()) {
            return true;
        }
        latestPaintTimestamp = itr.next().getScore();
        key = KeySpace.U.toString() + userId + ":" + KeySpace.CLEANS_RECEIVED.toString();
        Set<Tuple> cleansReceived = Painter.getSortedSet(userId, key, 0, 0);
        itr = cleansReceived.iterator();
        if (!itr.hasNext()) {
            return false;
        }
        latestCleanTimestamp = itr.next().getScore();
        return !(latestPaintTimestamp > latestCleanTimestamp) || !(latestPaintTimestamp > (double)(previousTime = Painter.getCurrentTimeInSeconds() - 259200L));
    }

    public static boolean hadInteraction(String user1, String user2) throws FusionException {
        int userId = Painter.getUserId(user1);
        int targetUserId = Painter.getUserId(user2);
        String key = "";
        key = KeySpace.U.toString() + userId + ":" + KeySpace.PAINTS_SENT.toString();
        Set<Tuple> paintsSent = Painter.getSortedSet(userId, key, 0, -1);
        if (Painter.containsWithinPeriod(paintsSent, targetUserId + ":" + user2, 86400)) {
            return true;
        }
        key = KeySpace.U.toString() + userId + ":" + KeySpace.CLEANS_SENT.toString();
        Set<Tuple> cleansSent = Painter.getSortedSet(userId, key, 0, -1);
        return Painter.containsWithinPeriod(cleansSent, targetUserId + ":" + user2, 86400);
    }

    public static ArrayList<String> getAllTaggedUsers(String[] users) throws FusionException {
        ArrayList<String> taggedUsers = new ArrayList<String>();
        for (int i = 0; i < users.length; ++i) {
            if (Painter.isClean(users[i])) continue;
            taggedUsers.add(users[i]);
        }
        return taggedUsers;
    }

    public static PainterStats getStats(String user) throws FusionException {
        int userId = Painter.getUserId(user);
        String key = KeySpace.USER.toString() + userId;
        Jedis slaveInstance = null;
        try {
            slaveInstance = Redis.getSlaveInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        if (slaveInstance == null) {
            return new PainterStats();
        }
        String points = slaveInstance.hget(key, "TotalPaintWarsPoints");
        String paintsSent = slaveInstance.hget(key, "TotalPaintsSent");
        String paintsReceived = slaveInstance.hget(key, "TotalPaintsReceived");
        int paintsRemaining = Painter.getRemainingFreePaints(user);
        String cleansSent = slaveInstance.hget(key, "TotalCleansSent");
        String cleansReceived = slaveInstance.hget(key, "TotalCleansReceived");
        int cleansRemaining = Painter.getRemainingFreeCleans(user);
        Redis.disconnect(slaveInstance, log);
        PainterStats stats = new PainterStats();
        try {
            stats.setTotalPaintWarsPoints(StringUtil.isBlank(points) ? 0 : Integer.parseInt(points));
            stats.setTotalPaintsSent(StringUtil.isBlank(paintsSent) ? 0 : Integer.parseInt(paintsSent));
            stats.setTotalPaintsReceived(StringUtil.isBlank(paintsReceived) ? 0 : Integer.parseInt(paintsReceived));
            stats.setPaintsRemaining(paintsRemaining);
            stats.setTotalCleansSent(StringUtil.isBlank(cleansSent) ? 0 : Integer.parseInt(cleansSent));
            stats.setTotalCleansReceived(StringUtil.isBlank(cleansReceived) ? 0 : Integer.parseInt(cleansReceived));
            stats.setCleansRemaining(cleansRemaining);
        }
        catch (NumberFormatException nfe) {
            throw new FusionException("Error parsing stats for user: " + user);
        }
        return stats;
    }

    public static Vector<List<String>> getStatsDetails(String username, int type, int offset, int numberOfEntries) throws FusionException {
        Vector<List<String>> statsDetails = null;
        int userId = Painter.getUserId(username);
        String field = "";
        if (type == 1) {
            field = KeySpace.PAINTS_SENT.toString();
        } else if (type == 2) {
            field = KeySpace.PAINTS_RECEIVED.toString();
        } else if (type == 3) {
            field = KeySpace.CLEANS_SENT.toString();
        } else if (type == 4) {
            field = KeySpace.CLEANS_RECEIVED.toString();
        } else {
            throw new FusionException("Unknown getStatsDetails type");
        }
        String key = KeySpace.U.toString() + userId + ":" + field;
        Painter.purgeExpiredRecords(userId, key);
        Set<Tuple> users = Painter.getSortedSet(userId, key, offset, offset + (numberOfEntries - 1));
        statsDetails = Painter.convertUserSetToVector(users);
        return statsDetails;
    }

    public static void purgeExpiredRecords(int userId, String key) throws FusionException {
        Tuple member;
        double score;
        Jedis masterInstance = null;
        try {
            masterInstance = Redis.getMasterInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        Set sortedSet = masterInstance.zrangeWithScores(key, 0L, -1L);
        Iterator itr = sortedSet.iterator();
        while (itr.hasNext() && (score = (member = (Tuple)itr.next()).getScore()) < (double)(Painter.getCurrentTimeInSeconds() - 2592000L)) {
            masterInstance.zrem(key, new String[]{member.getElement()});
        }
        Redis.disconnect(masterInstance, log);
    }

    public static String getUserIdenticonIndex(String username) throws FusionException {
        int userId = Painter.getUserId(username);
        Jedis slaveInstance = null;
        try {
            slaveInstance = Redis.getSlaveInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        if (slaveInstance == null) {
            return "0";
        }
        String key = KeySpace.USER.toString() + userId;
        String index = slaveInstance.hget(key, "IdenticonIndex");
        index = StringUtil.isBlank(index) ? "0" : index;
        Redis.disconnect(slaveInstance, log);
        return index;
    }

    public static Vector<List<String>> getUserPaint(String username) throws FusionException {
        String key;
        Set<Tuple> paintsReceived;
        Iterator<Tuple> itr;
        int userId = Painter.getUserId(username);
        ArrayList<String> userDetails = new ArrayList<String>();
        Vector<List<String>> user = new Vector<List<String>>();
        if (!Painter.isClean(username) && (itr = (paintsReceived = Painter.getSortedSet(userId, key = KeySpace.U.toString() + userId + ":" + KeySpace.PAINTS_RECEIVED.toString(), 0, 0)).iterator()).hasNext()) {
            userDetails.add(itr.next().getElement());
            userDetails.add(Painter.getUserIdenticonIndex(username));
        }
        user.add(userDetails);
        return user;
    }

    public static String getPriceOfPaint() {
        return "USD " + priceOfPaintCredit;
    }

    public static String getPriceOfClean() {
        return "USD " + priceOfCleanCredit;
    }

    public static String getPriceOfIdenticon() {
        return "USD " + priceOfIdenticon;
    }

    public static void buyIdenticon(String username) throws FusionException {
        Painter.chargeUser(username, CURRENCY, priceOfIdenticon, "Identicon purchase");
        int userId = Painter.getUserId(username);
        Jedis masterInstance = null;
        try {
            masterInstance = Redis.getMasterInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        String key = KeySpace.USER.toString() + userId;
        masterInstance.hincrBy(key, "IdenticonIndex", 1L);
        Redis.disconnect(masterInstance, log);
    }

    public static void buyPaintCredit(String username) throws FusionException {
        Painter.chargeUser(username, CURRENCY, priceOfPaintCredit, "Paint Wars paint credit purchase");
    }

    public static void buyCleanCredit(String username) throws FusionException {
        Painter.chargeUser(username, CURRENCY, priceOfCleanCredit, "Paint Wars clean credit purchase");
    }

    public static void buySpecialItem(String username, int typeId) throws FusionException {
        if (Painter.isInventoryFull(username)) {
            throw new FusionException("The inventory is full");
        }
        double price = 0.0;
        String currency = "";
        boolean itemFound = false;
        Vector<ItemData> specialItems = Painter.getSpecialItems();
        Iterator<ItemData> itr = specialItems.iterator();
        try {
            while (itr.hasNext()) {
                ItemData item = itr.next();
                if (item.getId() != typeId) continue;
                currency = item.getCurrency();
                price = item.getPrice();
                itemFound = true;
            }
        }
        catch (NumberFormatException e) {
            log.error((Object)("Unable to parse item price: " + e.getMessage()));
        }
        catch (IndexOutOfBoundsException e) {
            log.error((Object)("Unable to parse item details: " + e.getMessage()));
        }
        if (!itemFound) {
            throw new FusionException("Item not found");
        }
        if (typeId == 1) {
            Painter.chargeUser(username, currency, price, "Paint Wars paint proof item purchase");
        } else if (typeId == 2) {
            Painter.chargeUser(username, currency, price, "Paint Wars dual paint item purchase");
        } else if (typeId == 3) {
            Painter.chargeUser(username, currency, price, "Paint Wars stealth paint item purchase");
        } else {
            log.error((Object)"Unknown item type");
            throw new FusionException("Unknown item type");
        }
        Painter.addItemToInventory(username, typeId);
    }

    public static boolean isInventoryFull(String username) throws FusionException {
        int userId = Painter.getUserId(username);
        Jedis slaveInstance = null;
        try {
            slaveInstance = Redis.getSlaveInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        String key = KeySpace.U.toString() + userId + ":" + KeySpace.INVENTORY.toString();
        int inventorySize = 0;
        if (slaveInstance != null) {
            Long resultLong = slaveInstance.zcard(key);
            if (resultLong == null) {
                throw new FusionException("isInventoryFull-zcard returns null");
            }
            inventorySize = resultLong.intValue();
            Redis.disconnect(slaveInstance, log);
        }
        return inventorySize >= 30;
    }

    public static void addItemToInventory(String username, int itemId) throws FusionException {
        int userId = Painter.getUserId(username);
        Jedis masterInstance = null;
        try {
            masterInstance = Redis.getMasterInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        String key = KeySpace.U.toString() + userId + ":" + KeySpace.INVENTORY.toString();
        masterInstance.zadd(key, (double)Painter.getCurrentTimeInSeconds(), Integer.toString(itemId) + ":" + Painter.getCurrentTimeInSeconds());
        Redis.disconnect(masterInstance, log);
    }

    public static Vector<ItemData> getSpecialItems() throws FusionException {
        try {
            Content contentBean = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
            return contentBean.getPaintWarsSpecialItems();
        }
        catch (CreateException e) {
            log.error((Object)e.getMessage());
            throw new FusionException(e.getMessage());
        }
        catch (RemoteException e) {
            log.error((Object)e.getMessage());
            throw new FusionException(e.getMessage());
        }
    }

    public static String getSpecialItemName(int itemId) throws FusionException {
        Vector<ItemData> specialItems = Painter.getSpecialItems();
        for (ItemData item : specialItems) {
            if (item.getId() != itemId) continue;
            return item.getName();
        }
        return null;
    }

    public static Vector<List<String>> getUserInventory(String username) throws FusionException {
        int userId = Painter.getUserId(username);
        String key = KeySpace.U.toString() + userId + ":" + KeySpace.INVENTORY.toString();
        Set<Tuple> itemSet = Painter.getSortedSet(userId, key, 0, -1);
        return Painter.convertSetToVector(itemSet);
    }

    public static void useSpecialItem(String username, int itemId) throws FusionException {
        int userId = Painter.getUserId(username);
        Jedis masterInstance = null;
        try {
            masterInstance = Redis.getMasterInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        String key = "";
        key = KeySpace.U.toString() + userId + ":" + KeySpace.INVENTORY.toString();
        Set<Tuple> itemSet = Painter.getSortedSet(userId, key, 0, -1);
        Iterator<Tuple> itr = itemSet.iterator();
        boolean itemFound = false;
        while (itr.hasNext()) {
            Tuple member = itr.next();
            String itemName = member.getElement();
            if (!itemName.startsWith(Integer.toString(itemId))) continue;
            masterInstance.zrem(key, new String[]{member.getElement()});
            itemFound = true;
            break;
        }
        if (!itemFound) {
            log.error((Object)"Item not found");
            throw new FusionException("Item not found");
        }
        key = KeySpace.U.toString() + userId + ":" + KeySpace.ITEM_IN_USE.toString() + Integer.toString(itemId);
        masterInstance.zadd(key, (double)Painter.getCurrentTimeInSeconds(), "TRUE");
        masterInstance.expire(key, 86400);
        Redis.disconnect(masterInstance, log);
    }

    public static boolean isPaintProof(String username) throws FusionException {
        return Painter.checkIfSpecialItemUsed(username, 1);
    }

    public static boolean hasDualPaint(String username) throws FusionException {
        return Painter.checkIfSpecialItemUsed(username, 2);
    }

    public static boolean hasStealthPaint(String username) throws FusionException {
        return Painter.checkIfSpecialItemUsed(username, 3);
    }

    private static boolean checkIfSpecialItemUsed(String username, int itemType) throws FusionException {
        int userId = Painter.getUserId(username);
        Jedis slaveInstance = null;
        try {
            slaveInstance = Redis.getSlaveInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        if (slaveInstance == null) {
            return false;
        }
        String key = KeySpace.U.toString() + userId + ":" + KeySpace.ITEM_IN_USE.toString() + Integer.toString(itemType);
        boolean itemUsed = false;
        if (slaveInstance.exists(key).booleanValue()) {
            itemUsed = true;
        }
        Redis.disconnect(slaveInstance, log);
        return itemUsed;
    }

    private static void chargeUser(String username, String currency, double price, String description) throws FusionException {
        try {
            Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
            if (!accountEJB.userCanAffordCost(username, price, currency, null)) {
                throw new FusionException("Insufficient credits. Please recharge credits.");
            }
            Painter.checkMerchantLimits(username, price);
            accountEJB.chargeUserForGameItem(username, "Paint Wars", description, price, currency, new AccountEntrySourceData(Painter.class));
        }
        catch (FusionException e) {
            throw e;
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
    }

    public static boolean hasFreePaintCredits(String username) throws FusionException {
        int remainingFreeCredits = Painter.getRemainingFreePaints(username);
        return remainingFreeCredits > 0;
    }

    public static boolean hasFreeCleanCredits(String username) throws FusionException {
        int remainingFreeCredits = Painter.getRemainingFreeCleans(username);
        return remainingFreeCredits > 0;
    }

    public static int getRemainingFreePaints(String username) throws FusionException {
        int userId = Painter.getUserId(username);
        String key = KeySpace.U.toString() + userId + ":" + KeySpace.PAINTS_SENT.toString();
        return Painter.getRemainingFreeCreditCount(userId, key, freePaintsPerDay);
    }

    public static int getRemainingFreeCleans(String username) throws FusionException {
        int userId = Painter.getUserId(username);
        String key = KeySpace.U.toString() + userId + ":" + KeySpace.CLEANS_SENT.toString();
        return Painter.getRemainingFreeCreditCount(userId, key, freeCleansPerDay);
    }

    private static int getRemainingFreeCreditCount(int userId, String key, int freeCreditsPerDay) throws FusionException {
        double actionTimeInSeconds;
        if (freeCreditsPerDay <= 0) {
            return 0;
        }
        long previousDayTime = Painter.getCurrentTimeInSeconds() - 86400L;
        Set<Tuple> sentSet = Painter.getSortedSet(userId, key, 0, freeCreditsPerDay - 1);
        int remaining = freeCreditsPerDay;
        Iterator<Tuple> itr = sentSet.iterator();
        while (itr.hasNext() && !((actionTimeInSeconds = itr.next().getScore()) < (double)previousDayTime) && --remaining > 0) {
        }
        return remaining;
    }

    public static int getRequiredLevel() {
        int requiredLevel = 1;
        Jedis slaveInstance = null;
        try {
            slaveInstance = Redis.getGamesSlaveInstance();
        }
        catch (Exception e) {
            log.warn((Object)"Unable to load game configuration");
        }
        if (slaveInstance != null) {
            String requiredLevelStr = slaveInstance.hget("PaintWars", "ReqdMigLevel");
            if (requiredLevelStr != null) {
                try {
                    requiredLevel = Integer.parseInt(requiredLevelStr);
                }
                catch (NumberFormatException e) {
                    log.error((Object)"Unable to parse required mig level for paint wars");
                }
            }
            Redis.disconnect(slaveInstance, log);
        }
        return requiredLevel;
    }

    private static int getUserId(String username) throws FusionException {
        int userId = -1;
        try {
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            userId = userBean.getUserID(username, null);
        }
        catch (CreateException e) {
            log.error((Object)("Unable to create userbean: " + e.getMessage()));
            throw new FusionException(e.getMessage());
        }
        catch (RemoteException e) {
            log.error((Object)("Unable to get user id: " + e.getMessage()));
            throw new FusionException(e.getMessage());
        }
        return userId;
    }

    private static Set<Tuple> getSortedSet(int userId, String key, int min, int max) throws FusionException {
        Jedis slaveInstance = null;
        try {
            slaveInstance = Redis.getSlaveInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException("Unable to get slave instance: " + e.getMessage());
        }
        if (slaveInstance == null) {
            Set<Tuple> set = Collections.emptySet();
            return set;
        }
        Set sortedSet = slaveInstance.zrevrangeWithScores(key, (long)min, (long)max);
        Redis.disconnect(slaveInstance, log);
        return sortedSet;
    }

    private static String getHashValue(int userId, String field) throws FusionException {
        Jedis slaveInstance = null;
        try {
            slaveInstance = Redis.getSlaveInstanceForUserID(userId);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        if (slaveInstance == null) {
            return "";
        }
        String key = KeySpace.USER.toString() + userId;
        String value = slaveInstance.hget(key, field);
        Redis.disconnect(slaveInstance, log);
        return value;
    }

    private static boolean containsWithinPeriod(Set<Tuple> sortedSet, String targetUser, int period) {
        Tuple member;
        double scoreInSeconds;
        long previousDayTime = Painter.getCurrentTimeInSeconds() - (long)period;
        Iterator<Tuple> itr = sortedSet.iterator();
        while (itr.hasNext() && !((scoreInSeconds = (member = itr.next()).getScore()) < (double)previousDayTime)) {
            if (!member.getElement().startsWith(targetUser)) continue;
            return true;
        }
        return false;
    }

    private static Vector<List<String>> convertUserSetToVector(Set<Tuple> set) throws FusionException {
        Vector<List<String>> vector = new Vector<List<String>>();
        for (Tuple member : set) {
            String element = member.getElement();
            int index = element.lastIndexOf(58);
            if (index == -1) {
                log.warn((Object)("Error converting set to vector. Element: " + element));
                continue;
            }
            String userInfo = element.substring(0, index);
            if ((index = userInfo.indexOf(58)) == -1) {
                log.warn((Object)("Error converting set to vector. Element: " + element));
                continue;
            }
            int userId = Integer.parseInt(userInfo.substring(0, index));
            String score = Painter.getHashValue(userId, "TotalPaintWarsPoints");
            String timestamp = Painter.convertDoubleToStr(member.getScore());
            ArrayList<String> userDetails = new ArrayList<String>();
            userDetails.add(userInfo);
            userDetails.add(score);
            userDetails.add(timestamp);
            vector.add(userDetails);
        }
        return vector;
    }

    private static Vector<List<String>> convertSetToVector(Set<Tuple> set) {
        Vector<List<String>> vector = new Vector<List<String>>();
        for (Tuple member : set) {
            ArrayList<String> list = new ArrayList<String>();
            String element = member.getElement();
            String[] splitElement = element.split(":");
            for (int i = 0; i < splitElement.length; ++i) {
                list.add(splitElement[i]);
            }
            vector.add(list);
        }
        return vector;
    }

    private static int calculatePoints(String user1, String user2, boolean isPainting, boolean isClean) throws FusionException {
        if (isClean) {
            if (isPainting) {
                if (Painter.areFriends(user1, user2)) {
                    return 10;
                }
                return 15;
            }
            if (Painter.areFriends(user1, user2)) {
                return 25;
            }
            return 30;
        }
        if (isPainting) {
            if (Painter.areFriends(user1, user2)) {
                return 5;
            }
            return 10;
        }
        if (user1.equals(user2)) {
            return 0;
        }
        if (Painter.areFriends(user1, user2)) {
            return 10;
        }
        return 15;
    }

    private static boolean areFriends(String user1, String user2) throws FusionException {
        try {
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            Set broadcastList = userBean.loadBroadcastList(user1, null);
            return broadcastList.contains(user2);
        }
        catch (CreateException e) {
            log.error((Object)e.getMessage());
            throw new FusionException(e.getMessage());
        }
        catch (RemoteException e) {
            log.error((Object)e.getMessage());
            throw new FusionException(e.getMessage());
        }
        catch (SQLException e) {
            log.error((Object)e.getMessage());
            throw new FusionException(e.getMessage());
        }
    }

    private static long getCurrentTimeInSeconds() {
        return Calendar.getInstance().getTimeInMillis() / 1000L;
    }

    private static String convertDoubleToStr(double value) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);
        return numberFormat.format(value);
    }

    private static void checkRaffleEligibility(String username, int userId, String key, double currentTime, int eligibilityAmount) throws FusionException {
        Set<Tuple> paintsSent;
        Iterator<Tuple> itr;
        int eligibilityIndex = 0;
        if (eligibilityAmount > 0) {
            eligibilityIndex = eligibilityAmount - 1;
        }
        if ((itr = (paintsSent = Painter.getSortedSet(userId, key, eligibilityIndex, eligibilityIndex)).iterator()).hasNext()) {
            Jedis masterInstance = null;
            try {
                masterInstance = Redis.getGamesMasterInstance();
            }
            catch (Exception e) {
                log.warn((Object)"Unable to load game configuration");
            }
            if (masterInstance != null) {
                double paintSentTimestamp;
                double lastRaffleTime = 0.0;
                String lastRaffleTimeStr = masterInstance.hget("PaintWars", "LastRaffleTime");
                if (lastRaffleTimeStr != null) {
                    try {
                        lastRaffleTime = Double.parseDouble(lastRaffleTimeStr);
                    }
                    catch (NumberFormatException e) {
                        log.warn((Object)"Unable to parse last raffle time");
                    }
                }
                if ((paintSentTimestamp = itr.next().getScore()) > lastRaffleTime) {
                    masterInstance.zadd("PaintWars:RaffleCandidates", currentTime, username);
                }
                Redis.disconnect(masterInstance, log);
            }
        }
    }

    private static void checkMerchantLimits(String username, double cost) throws FusionException {
        if (cost == 0.0) {
            return;
        }
        try {
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            UserData userData = userBean.loadUser(username, false, false);
            if (userData.type.value() != UserData.TypeEnum.MIG33_TOP_MERCHANT.value()) {
                return;
            }
        }
        catch (CreateException e) {
            throw new FusionException(e.getMessage());
        }
        catch (RemoteException e) {
            throw new FusionException(e.getMessage());
        }
        double amountLimitPerTimeSlot = SystemProperty.getDouble("MerchantLimitPerTimeSlot", 20.0);
        long timeSlot = SystemProperty.getLong("MerchantLimitTimeSlot", 24L) * 60L * 60L * 1000L;
        LimitTracker limit = (LimitTracker)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.MERCHANT_GAME_LIMIT, username);
        String instanceId = "PaintWars";
        if (cost > amountLimitPerTimeSlot) {
            log.debug((Object)("Top merchant: " + username + " cannot make this purchase without going over the limit. Cost: " + cost));
            throw new FusionException("You cannot make this purchase without exceeding your limit for games.");
        }
        if (limit == null) {
            log.debug((Object)("Creating new game merchant limit for: " + username));
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.MERCHANT_GAME_LIMIT, username, new LimitTracker(instanceId, Calendar.getInstance().getTimeInMillis() + timeSlot, cost));
        } else if (limit.hasExpired(Calendar.getInstance().getTimeInMillis())) {
            log.debug((Object)("Recreating new game merchant limit for: " + username));
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.MERCHANT_GAME_LIMIT, username, new LimitTracker(instanceId, Calendar.getInstance().getTimeInMillis() + timeSlot, cost));
        } else {
            if (limit.getTotalAmountSpent() + cost > amountLimitPerTimeSlot) {
                log.debug((Object)("Top merchant: " + username + " has exceeded the merchant limits. Merchant would have spent: " + (limit.getTotalAmountSpent() + cost)));
                throw new FusionException("You have exceeded your limit for games. Please try again later");
            }
            log.debug((Object)("Updating merchant limit balance for " + username + ". Balance is: " + (limit.getTotalAmountSpent() + cost)));
            limit.add(instanceId, cost);
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.MERCHANT_GAME_LIMIT, username, limit);
        }
    }

    static {
        Jedis slaveInstance = null;
        try {
            slaveInstance = Redis.getGamesSlaveInstance();
        }
        catch (Exception e) {
            log.warn((Object)"Unable to load game configuration");
        }
        String key = "PaintWars";
        String value = null;
        if (slaveInstance != null) {
            value = slaveInstance.hget(key, PRICE_OF_PAINT_CREDIT);
            if (value != null) {
                priceOfPaintCredit = Double.parseDouble(value);
            }
            if ((value = slaveInstance.hget(key, PRICE_OF_CLEAN_CREDIT)) != null) {
                priceOfCleanCredit = Double.parseDouble(value);
            }
            if ((value = slaveInstance.hget(key, PRICE_OF_IDENTICON)) != null) {
                priceOfIdenticon = Double.parseDouble(value);
            }
            if ((value = slaveInstance.hget(key, FREE_PAINTS_PER_DAY)) != null) {
                freePaintsPerDay = Integer.parseInt(value);
            }
            if ((value = slaveInstance.hget(key, FREE_CLEANS_PER_DAY)) != null) {
                freeCleansPerDay = Integer.parseInt(value);
            }
            if ((value = slaveInstance.hget(key, PAINTS_SENT_ELIGIBILITY)) != null) {
                paintsSentEligibility = Integer.parseInt(value);
            }
            if ((value = slaveInstance.hget(key, CLEANS_SENT_ELIGIBILITY)) != null) {
                cleansSentEligibility = Integer.parseInt(value);
            }
            Redis.disconnect(slaveInstance, log);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum KeySpace {
        USER("User:"),
        U("U:"),
        PAINTS_SENT("PaintsSent"),
        PAINTS_RECEIVED("PaintsReceived"),
        CLEANS_SENT("CleansSent"),
        CLEANS_RECEIVED("CleansReceived"),
        INVENTORY("PaintWars:Inventory"),
        ITEM_IN_USE("PaintWars:ItemInUse:"),
        RAFFLE_CANDIDATES("PaintWars:RaffleCandidates");

        private String value;

        private KeySpace(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }
}

