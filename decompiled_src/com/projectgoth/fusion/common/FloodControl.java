/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.Date;
import org.apache.log4j.Logger;

public class FloodControl {
    private static final String FLOOD_CONTROL_NAMESPACE = "FC";
    private static MemCachedClient rateLimitMemcached = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.rateLimit);
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FloodControl.class));

    public static void detectFlooding(String username, UserPrx userPrx, Action[] actions) throws Exception {
        for (Action action : actions) {
            FloodControl.detectFlooding(username, userPrx, action);
        }
    }

    public static void detectFlooding(String username, UserPrx userPrx, Action action) throws Exception {
        String key = MemCachedUtils.getCacheKeyInNamespace(FLOOD_CONTROL_NAMESPACE, username + "/" + (Object)((Object)action));
        Date expiry = new Date(System.currentTimeMillis() + action.duration);
        if (rateLimitMemcached.add(key, (Object)1, expiry)) {
            return;
        }
        if (rateLimitMemcached.incr(key) > action.maxHits) {
            if (userPrx != null) {
                userPrx.disconnectFlooder("Flooding. Action: " + (Object)((Object)action));
            }
            throw new Exception("You have been disconnected");
        }
    }

    public static String getRateLimitKeyByUsername(FusionRequest packet, String username) {
        return MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.RateLimitKeySpace.GENERIC_FUSION_REQUEST_RATE_LIMIT, String.format("%s:%s:%s", packet.getSimpleName(), "U", username));
    }

    public static void detectFloodingOfFusionRequestPacketByUser(String username, UserPrx userPrx, boolean isChatRoomAdmin, FusionRequest packet, boolean disconnect) throws Exception {
        String rateLimit = SystemProperty.get(new SystemPropertyEntities.GatewayRateLimit(packet, isChatRoomAdmin));
        try {
            MemCachedRateLimiter.hit(FLOOD_CONTROL_NAMESPACE, FloodControl.getRateLimitKeyByUsername(packet, username), rateLimit);
        }
        catch (MemCachedRateLimiter.LimitExceeded e) {
            if (userPrx != null && disconnect) {
                userPrx.disconnectFlooder(String.format("Flooding. Action: %s U:%s", packet.getSimpleName(), username));
                throw new Exception("You have been disconnected.");
            }
            throw new Exception("Please try again later.");
        }
        catch (MemCachedRateLimiter.FormatError e) {
            throw new Exception("Internal Error");
        }
    }

    public static String getRateLimitKeyByChatRoom(FusionRequest packet, String chatRoomName) {
        return MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.RateLimitKeySpace.GENERIC_FUSION_REQUEST_RATE_LIMIT, String.format("%s:%s:%s", packet.getSimpleName(), "C", chatRoomName));
    }

    public static String getRateLimitKeyByChatRoomAndRemoteAddress(FusionRequest packet, String chatRoomName, String remoteAddress) {
        return MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.RateLimitKeySpace.GENERIC_FUSION_REQUEST_RATE_LIMIT, String.format("%s:%s:%s:%s", packet.getSimpleName(), "C", chatRoomName, remoteAddress));
    }

    public static void detectFloodingOfFusionRequestPacketByChatroom(String chatRoomName, String remoteAddress, UserPrx userPrx, boolean isChatRoomAdmin, FusionRequest packet, boolean disconnect) throws Exception {
        if (StringUtil.isBlank(chatRoomName)) {
            return;
        }
        chatRoomName = chatRoomName.toLowerCase();
        String rateLimit = SystemProperty.get(new SystemPropertyEntities.GatewayRateLimitByChatRoom(packet, isChatRoomAdmin));
        Boolean useRemoteAddressInRateLimit = SystemProperty.getBool(SystemPropertyEntities.GatewaySettings.RATELIMIT_BY_CHATROOM_AND_REMOTEADDRESS);
        if (useRemoteAddressInRateLimit.booleanValue() && SystemProperty.isValueInArray(remoteAddress, SystemPropertyEntities.GatewaySettings.RATELIMIT_BY_REMOTEADDRESS_WHITELIST)) {
            log.debug((Object)("Skipping ratelimit check on ip " + remoteAddress + " due to whitelisting"));
            return;
        }
        try {
            String rateLimitKey = useRemoteAddressInRateLimit != false ? FloodControl.getRateLimitKeyByChatRoomAndRemoteAddress(packet, chatRoomName, remoteAddress) : FloodControl.getRateLimitKeyByChatRoom(packet, chatRoomName);
            MemCachedRateLimiter.hit(FLOOD_CONTROL_NAMESPACE, rateLimitKey, rateLimit);
        }
        catch (MemCachedRateLimiter.LimitExceeded e) {
            if (userPrx != null && disconnect) {
                userPrx.disconnectFlooder(String.format("Flooding. Action: %s C:%s", packet.getSimpleName(), chatRoomName));
                throw new Exception("You have been disconnected.");
            }
            throw new Exception("Please try again later.");
        }
        catch (MemCachedRateLimiter.FormatError e) {
            throw new Exception("Internal Error");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Action {
        FILE_UPLOAD_PER_MINUTE(10L, 60000L),
        FILE_UPLOAD_DAILY(1000L, 86400000L),
        INVITE_FRIEND_DAILY(100L, 86400000L),
        INVITE_FRIEND_PER_MINUTE(10L, 60000L),
        SEND_EMAIL(10L, 60000L),
        SET_STATUS(10L, 300000L),
        PHONE_CALL(3L, 1000L),
        DEFAULT_DAILY(200L, 86400000L),
        DEFAULT_PER_MINUTE(10L, 60000L);

        private long maxHits;
        private long duration;

        private Action(long maxHit, long duration) {
            this.maxHits = maxHit;
            this.duration = duration;
        }

        public Action setMaxHits(long maxHits) {
            this.maxHits = maxHits;
            return this;
        }
    }
}

