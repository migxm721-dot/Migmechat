/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet.chatsync;

import Ice.LocalException;
import com.projectgoth.fusion.chatsync.ChatSyncStats;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.chatsync.FusionPktGetMessagesBase;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardDispatcher;
import com.projectgoth.fusion.packet.FusionPacket;
import org.apache.log4j.Logger;

public class FusionPktGetMessages
extends FusionPktGetMessagesBase {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetMessages.class));

    public FusionPktGetMessages() {
        super((short)550);
    }

    public FusionPktGetMessages(short transactionId) {
        super((short)550, transactionId);
    }

    public FusionPktGetMessages(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            if (!MessageSwitchboardDispatcher.getInstance().isFeatureEnabled()) {
                return new FusionPacket[]{new FusionPktError(this.transactionId)};
            }
            if (!MemCachedRateLimiter.bypassRateLimit(connection.getUsername())) {
                int maxPerMin = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.MAX_GET_MESSAGE_REQUESTS_PER_MINUTE);
                if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.CHAT_SYNC_GLOBAL_RATE_LIMITS.toString(), "maxGetMessageRequestsPerMin", (long)maxPerMin, 60000L)) {
                    return new FusionPacket[]{new FusionPktError(this.transactionId)};
                }
            }
            int sysadminLimit = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.GET_MESSAGES_SYSADMIN_LIMIT);
            Integer suppliedLimit = this.getLimit();
            int effectiveLimit = suppliedLimit == null || suppliedLimit > sysadminLimit ? sysadminLimit : suppliedLimit;
            if (log.isDebugEnabled()) {
                log.debug((Object)("GET_MESSAGES request received with chatID=" + this.getChatIdentifier() + " start=" + this.getOldestMessageTimestamp() + " end=" + this.getLatestMessageTimestamp() + " supplied limit=" + suppliedLimit + " sysadmin limit=" + sysadminLimit + " effective limit=" + effectiveLimit));
            }
            ChatSyncStats.getInstance().incrementTotalGetMessagesReceived();
            long lOldest = this.getOldestMessageTimestamp() != null ? this.getOldestMessageTimestamp() : Long.MIN_VALUE;
            long lLatest = this.getLatestMessageTimestamp() != null ? this.getLatestMessageTimestamp() : Long.MIN_VALUE;
            MessageSwitchboardDispatcher.getInstance().getAndPushMessages(connection, connection.getUsername(), this.getChatType(), this.getChatIdentifier(), lOldest, lLatest, effectiveLimit, connection.getConnectionPrx(), connection.getDeviceTypeAsInt(), connection.getClientVersion(), this.transactionId);
            return new FusionPacket[]{new FusionPktOk(this.transactionId)};
        }
        catch (LocalException e) {
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Failed to get messages").toArray();
        }
        catch (Exception e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get messages - " + e.getMessage());
            return new FusionPacket[]{pktError};
        }
    }
}

