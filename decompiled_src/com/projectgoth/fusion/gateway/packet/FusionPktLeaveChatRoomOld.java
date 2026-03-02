/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.ChatRoomUtils;
import com.projectgoth.fusion.common.ChatRoomValidationException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardDispatcher;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.RegistryPrx;
import org.apache.log4j.Logger;

public class FusionPktLeaveChatRoomOld
extends FusionRequest {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktLeaveChatRoomOld.class));

    public FusionPktLeaveChatRoomOld() {
        super((short)704);
    }

    public FusionPktLeaveChatRoomOld(short transactionId) {
        super((short)704, transactionId);
    }

    public FusionPktLeaveChatRoomOld(FusionPacket packet) {
        super(packet);
    }

    public String getChatRoomName() {
        return this.getStringField((short)1);
    }

    public void setChatRoomName(String chatRoomName) {
        this.setField((short)1, chatRoomName);
    }

    public boolean sessionRequired() {
        return true;
    }

    public String getChatRoomNameForRateLimit() {
        return this.getChatRoomName();
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            RegistryPrx reg = connection.findRegistry();
            MessageSwitchboardDispatcher.getInstance().onLeaveChatRoom(reg, connection.getUsername(), connection.getUserID(), this.getChatRoomName(), connection.getUserPrx());
            if (log.isDebugEnabled()) {
                log.debug((Object)("Successfully called MSB.onLeaveChatRoom for user=" + connection.getUsername()));
            }
        }
        catch (Exception e) {
            log.error((Object)("Exception calling MessageSwitchboard.onLeaveChatRoom: " + e), (Throwable)e);
        }
        try {
            String chatRoomName = this.getChatRoomName();
            chatRoomName = ChatRoomUtils.validateChatRoomNameForAccess(chatRoomName);
            RegistryPrx registryPrx = connection.findRegistry();
            if (registryPrx != null) {
                ChatRoomPrx chatRoomPrx = registryPrx.findChatRoomObject(chatRoomName);
                chatRoomPrx.removeParticipant(connection.getUsername());
            }
        }
        catch (ChatRoomValidationException e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to leave chat room - " + e.getMessage());
            return new FusionPacket[]{pktError};
        }
        catch (FusionException e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to leave chat room - " + e.getMessage());
            return new FusionPacket[]{pktError};
        }
        catch (Exception e) {
            log.error((Object)"Caught exception while trying to leave chatroom", (Throwable)e);
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to leave chat room - Internal Error");
            return new FusionPacket[]{pktError};
        }
        return new FusionPacket[]{new FusionPktOk(this.transactionId)};
    }
}

