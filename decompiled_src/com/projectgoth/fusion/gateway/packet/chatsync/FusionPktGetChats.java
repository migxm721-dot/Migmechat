/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet.chatsync;

import Ice.LocalException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardDispatcher;
import com.projectgoth.fusion.packet.FusionPacket;
import org.apache.log4j.Logger;

public class FusionPktGetChats
extends FusionRequest {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetChats.class));

    public FusionPktGetChats() {
        super((short)551);
    }

    public FusionPktGetChats(short transactionId) {
        super((short)551, transactionId);
    }

    public FusionPktGetChats(FusionPacket packet) {
        super(packet);
    }

    public Integer getVersion() {
        return this.getIntField((short)1);
    }

    public void setVersion(int ver) {
        this.setField((short)1, ver);
    }

    public Integer getLimit() {
        return this.getIntField((short)2);
    }

    public void setLimit(int limit) {
        this.setField((short)2, limit);
    }

    public Byte getChatType() {
        return this.getByteField((short)3);
    }

    public void setChatType(byte chatType) {
        this.setField((short)3, chatType);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            log.debug((Object)"FusionPktGetChats.processRequest");
            if (!MemCachedRateLimiter.bypassRateLimit(connection.getUsername())) {
                int maxPerMin = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.MAX_GET_CHAT_REQUESTS_PER_MINUTE);
                if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.CHAT_SYNC_GLOBAL_RATE_LIMITS.toString(), "maxGetChatRequestsPerMin", (long)maxPerMin, 60000L)) {
                    return new FusionPacket[]{new FusionPktError(this.transactionId)};
                }
            }
            if (connection.getSessionPrx().getChatListVersion() <= 0) {
                connection.getSessionPrx().setChatListVersion(this.getVersion());
            }
            MessageSwitchboardDispatcher.getInstance().onGetChats(connection, connection.getUserID(), this.getVersion(), this.getLimit() != null ? this.getLimit() : Integer.MIN_VALUE, this.getChatType() != null ? (byte)this.getChatType() : (byte)-128, this.getTransactionId(), connection.getUsername());
            return new FusionPacket[]{new FusionPktOk(this.transactionId)};
        }
        catch (LocalException e) {
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Failed to get chats").toArray();
        }
        catch (Exception e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get chats - " + e.getMessage());
            return new FusionPacket[]{pktError};
        }
    }
}

