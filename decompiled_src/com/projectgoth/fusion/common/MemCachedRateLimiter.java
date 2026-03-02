/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MemCachedRateLimiter {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MemCachedRateLimiter.class));
    private static final MemCachedKeySpaces.MemCachedKeySpaceInterface RATE_LIMIT_KEYSPACE = MemCachedKeySpaces.RateLimitKeySpace.RATE_LIMIT;
    private static final Pattern HIT_PER_DURATION_PATTERN = Pattern.compile("(([0-9]+)/([0-9]+)(D|H|M|S|MS)(;|$))");
    private static final String[] DURATION_TYPES = new String[]{"D", "H", "M", "S", "MS"};
    private static final String[] DURATION_NAMES = new String[]{"day", "hour", "minute", "second", "milli-second"};
    private static final long[] DURATION_VALUES = new long[]{86400000L, 3600000L, 60000L, 1000L, 1L};
    private static Map<String, String> durationType2Name = new HashMap<String, String>();
    private static Map<String, Long> durationType2Value = new HashMap<String, Long>();

    public static boolean reset(String namespace, String key) {
        return MemCachedClientWrapper.delete(RATE_LIMIT_KEYSPACE, MemCachedKeyUtils.getFullKeyFromStrings(namespace, key));
    }

    public static boolean checkWithoutHit(NameSpace namespace, String key, long maxHit, long duration) {
        if (namespace == null) {
            log.error((Object)String.format("null namespace for key='%s', maxHit=%d, duration=%d", key, maxHit, duration));
            return false;
        }
        return MemCachedRateLimiter.checkWithoutHit(namespace.toString(), key, maxHit, duration);
    }

    public static boolean checkWithoutHit(String namespace, String key, long maxHit, long duration) {
        String mcKey = MemCachedKeyUtils.getFullKeyFromStrings(namespace, key);
        return MemCachedClientWrapper.getCounter(RATE_LIMIT_KEYSPACE, mcKey) < maxHit;
    }

    public static boolean hit(NameSpace namespace, String key, long maxHit, long duration) {
        if (namespace == null) {
            log.error((Object)String.format("null namespace for key='%s', maxHit=%d, duration=%d", key, maxHit, duration));
            return false;
        }
        return MemCachedRateLimiter.hit(namespace.toString(), key, maxHit, duration);
    }

    public static boolean hit(String namespace, String key, long maxHit, long duration) {
        String mcKey = MemCachedKeyUtils.getFullKeyFromStrings(namespace, key);
        long currentHit = MemCachedClientWrapper.getCounter(RATE_LIMIT_KEYSPACE, mcKey);
        if (currentHit == -1L && MemCachedClientWrapper.add(RATE_LIMIT_KEYSPACE, mcKey, 1, duration)) {
            return true;
        }
        return currentHit < maxHit && MemCachedClientWrapper.incr(RATE_LIMIT_KEYSPACE, mcKey) <= maxHit;
    }

    public static void hit(NameSpace namespace, String key, String hitPerDuration) throws LimitExceeded, FormatError {
        if (namespace == null) {
            log.error((Object)String.format("null namespace for key='%s', hit='%s'", key, hitPerDuration));
            throw new FormatError("Empty namespace");
        }
        MemCachedRateLimiter.hit(namespace.toString(), key, hitPerDuration);
    }

    private static List<DurationEntry> parseDurationString(String hitPerDuration, String namespace, String key, boolean throwErrorOnAnyError) throws FormatError {
        LinkedList<DurationEntry> results = new LinkedList<DurationEntry>();
        Matcher m = HIT_PER_DURATION_PATTERN.matcher(hitPerDuration);
        if (m.find()) {
            m.reset();
            while (m.find()) {
                DurationEntry entry = new DurationEntry();
                entry.durationPattern = m.group(0);
                entry.maxHit = Long.parseLong(m.group(2));
                entry.duration = Long.parseLong(m.group(3));
                entry.durationMillis = -1L;
                entry.durationType = m.group(4);
                entry.durationTypeName = durationType2Name.get(entry.durationType);
                entry.durationTypeValue = durationType2Value.get(entry.durationType);
                if (entry.durationTypeValue != null) {
                    entry.durationMillis = entry.duration * entry.durationTypeValue;
                    results.add(entry);
                    continue;
                }
                log.warn((Object)String.format("Incorrect hitPerDuration string '%s', for ns='%s', key='%s': unrecognized duration type '%s'", hitPerDuration, namespace, key, entry.durationType));
            }
            return results;
        }
        log.error((Object)String.format("Incorrect hitPerDuration string '%s', for ns='%s', key='%s'", hitPerDuration, namespace, key));
        throw new FormatError("Incorrect hitPerDuration string " + hitPerDuration);
    }

    public static void hit(String namespace, String key, String hitPerDuration) throws LimitExceeded, FormatError {
        for (DurationEntry entry : MemCachedRateLimiter.parseDurationString(hitPerDuration, namespace, key, false)) {
            if (MemCachedRateLimiter.hit(namespace, MemCachedKeyUtils.getFullKeyFromStrings(key, entry.durationType), entry.maxHit, entry.durationMillis)) continue;
            throw new LimitExceeded("rate limit exceeded " + namespace + "/" + key + "/" + entry.durationType, entry.durationPattern);
        }
    }

    public static boolean checkWithoutHit(String namespace, String key, String hitPerDuration) throws LimitExceeded, FormatError {
        boolean result = true;
        for (DurationEntry entry : MemCachedRateLimiter.parseDurationString(hitPerDuration, namespace, key, false)) {
            result = result && MemCachedRateLimiter.checkWithoutHit(namespace, MemCachedKeyUtils.getFullKeyFromStrings(key, entry.durationType), entry.maxHit, entry.durationMillis);
        }
        return result;
    }

    public static boolean checkWithoutHit(NameSpace namespace, String key, String hitPerDuration) throws LimitExceeded, FormatError {
        return MemCachedRateLimiter.checkWithoutHit(namespace.toString(), key, hitPerDuration);
    }

    public static void reset(NameSpace namespace, String key, String hitPerDuration) {
        try {
            for (DurationEntry entry : MemCachedRateLimiter.parseDurationString(hitPerDuration, namespace.toString(), key, false)) {
                MemCachedRateLimiter.reset(namespace.toString(), MemCachedKeyUtils.getFullKeyFromStrings(key, entry.durationType));
            }
        }
        catch (FormatError formatError) {
            // empty catch block
        }
    }

    static String getPrettyHitPerDuration(String hitPerDuration) {
        try {
            List<DurationEntry> entries = MemCachedRateLimiter.parseDurationString(hitPerDuration, null, null, false);
            if (entries.size() > 0) {
                DurationEntry entry = entries.get(0);
                return String.format("%d %%s within %d %s%s", entry.maxHit, entry.duration, entry.durationTypeName, entry.duration > 1L ? "s" : "");
            }
            return "";
        }
        catch (FormatError e) {
            return "";
        }
    }

    public static boolean isValidHitPerDuration(String hitPerDuration) {
        try {
            return MemCachedRateLimiter.parseDurationString(hitPerDuration, null, null, true).size() > 0;
        }
        catch (FormatError e) {
            return false;
        }
    }

    public static boolean bypassRateLimit(String username) {
        return SystemPropertyEntities.GatewaySettings.Cache.loadTestUsersRateLimitBypassEnabled.getValue() != false && null != username && username.startsWith("loadtest");
    }

    static {
        for (int i = 0; i < DURATION_TYPES.length; ++i) {
            durationType2Name.put(DURATION_TYPES[i], DURATION_NAMES[i]);
            durationType2Value.put(DURATION_TYPES[i], DURATION_VALUES[i]);
        }
    }

    public static class FormatError
    extends Exception {
        public FormatError(String message) {
            super(message);
        }
    }

    public static class LimitExceeded
    extends Exception {
        private String expr;

        LimitExceeded(String message, String expr) {
            super(message);
            this.expr = expr;
        }

        public String getPrettyMessage() {
            return MemCachedRateLimiter.getPrettyHitPerDuration(this.expr);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NameSpace {
        VIRTUALGIFT,
        CONTACT,
        SENDSMS,
        PHONECALL,
        GENERIC_EMOTE,
        AUTH_AC,
        USER_REFERRAL,
        CHATROOM_ENTRY,
        EMAIL_REG_RATE_LIMIT_IP,
        EMAIL_REG_RATE_LIMIT_IP_STEP_2,
        EMAIL_REG_RATE_LIMIT_DOMAIN,
        EMAIL_REG_RATE_LIMIT_DOMAIN_STEP_2,
        EMAIL_REG_RATE_LIMIT_SANITIZED_EMAIL_LOCAL_AND_IP,
        EMAIL_REG_RATE_LIMIT_SANITIZED_EMAIL_LOCAL_AND_IP_STEP_2,
        EMAIL_REG_RATE_LIMIT_SANITIZED_EMAIL_LOCAL_DOMAIN,
        EMAIL_REG_RATE_LIMIT_SANITIZED_EMAIL_LOCAL_DOMAIN_STEP_2,
        EMAIL_REG_RATE_LIMIT_NON_WHITELISTED_DOMAIN_TOP_LEVEL_PART,
        EMAIL_REG_RATE_LIMIT_NON_WHITELISTED_DOMAIN_TOP_LEVEL_PART_STEP_2,
        REWARD_PRG,
        FUSIONREST_ALERTBATCH,
        OFFLINE_MESSAGE_SENDS_PER_USER,
        OFFLINE_MESSAGE_SENDS_PER_USER_PAIR,
        OFFLINE_MESSAGE_RECEIVES_PER_USER,
        INITIATE_PAYPAL_PAYMENT,
        OFFLINE_MESSAGE_SENDS_PER_SUSPECTED_ABUSER,
        FACEBOOK_MESSAGE_RECEIVE_PER_USER,
        FORGOT_PASSWORD_REQUEST,
        FORGOT_PASSWORD_RESPONSE,
        FORGOT_USERNAME_REQUEST,
        AWS_GLOBAL_RATE_LIMITS,
        AWS_LIVE_JOB_FLOWS_PER_TRANSFORMATION,
        AUTO_LOGIN,
        CHAT_SYNC_GLOBAL_RATE_LIMITS,
        CHAT_SYNC_MAX_GET_CHATS_PER_USER_PER_DAY,
        CHAT_SYNC_MAX_GET_MESSAGES_PER_USER_PER_DAY,
        CHAT_SYNC_MAX_CHATS_STORED_PER_USER_PER_DAY,
        CHAT_SYNC_MAX_MESSAGES_STORED_PER_USER_PER_DAY,
        UPLD_DATA,
        UPLD_TICKET_REQ,
        MANDATORY_RESET_UNREAD_ALERT_COUNT,
        CAMPAIGN;

    }

    private static class DurationEntry {
        String durationPattern;
        long maxHit;
        long duration;
        long durationMillis;
        Long durationTypeValue;
        String durationTypeName;
        String durationType;

        private DurationEntry() {
        }
    }
}

