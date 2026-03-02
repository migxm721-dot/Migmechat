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
import com.projectgoth.fusion.fdl.packets.FusionPktDataGetChatroomParticipants;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktChatroomParticipants;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.log4j.Logger;

public class FusionPktGetChatroomParticipants
extends FusionPktDataGetChatroomParticipants {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetChatroomParticipants.class));

    public FusionPktGetChatroomParticipants(short transactionId) {
        super(transactionId);
    }

    public FusionPktGetChatroomParticipants(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktGetChatroomParticipants(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            Object[] mutedUsers;
            Object[] administrators;
            String chatRoomName = this.getChatroomName();
            chatRoomName = ChatRoomUtils.validateChatRoomNameForAccess(chatRoomName);
            RegistryPrx registryPrx = connection.findRegistry();
            if (registryPrx == null) {
                throw new Exception("Unable to locate registry");
            }
            ChatRoomPrx chatRoomPrx = registryPrx.findChatRoomObject(chatRoomName);
            FusionPktChatroomParticipants reply = new FusionPktChatroomParticipants(this.transactionId);
            reply.setChatroomName(chatRoomName);
            Object[] participantArray = chatRoomPrx.getParticipants(connection.getUsername());
            if (participantArray != null && participantArray.length > 0) {
                Arrays.sort(participantArray);
                reply.setParticipantList((String[])participantArray);
            }
            if ((administrators = chatRoomPrx.getAdministrators(connection.getUsername())) != null && administrators.length > 0) {
                Arrays.sort(administrators);
                reply.setAdministratorList((String[])administrators);
            }
            if ((mutedUsers = connection.getUserPrx().getBlockListFromUsernames((String[])participantArray)) != null && mutedUsers.length > 0) {
                Arrays.sort(mutedUsers);
                reply.setMutedList((String[])mutedUsers);
            }
            return new FusionPacket[]{reply};
        }
        catch (ChatRoomValidationException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get chat room participants - " + e.getMessage()).toArray();
        }
        catch (ObjectNotFoundException e) {
            FusionPktChatroomParticipants reply = new FusionPktChatroomParticipants(this.transactionId);
            reply.setChatroomName(this.getChatroomName());
            return new FusionPacket[]{reply};
        }
        catch (Exception e) {
            log.error((Object)"Exception while trying to get chat room participants.", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get chat room participants - Internal Error").toArray();
        }
    }
}

