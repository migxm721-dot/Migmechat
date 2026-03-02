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
import com.projectgoth.fusion.gateway.packet.FusionPktChatRoomParticipantsOld;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import java.util.Arrays;
import org.apache.log4j.Logger;

public class FusionPktGetChatRoomParticipantsOld
extends FusionRequest {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetChatRoomParticipantsOld.class));

    public FusionPktGetChatRoomParticipantsOld() {
        super((short)707);
    }

    public FusionPktGetChatRoomParticipantsOld(short transactionId) {
        super((short)707, transactionId);
    }

    public FusionPktGetChatRoomParticipantsOld(FusionPacket packet) {
        super(packet);
    }

    public String getChatRoomName() {
        return this.getStringField((short)1);
    }

    public void setChatRoomName(String chatRoomName) {
        this.setField((short)1, chatRoomName);
    }

    private String concat(String[] stringArray, String seperator) {
        StringBuilder builder = new StringBuilder();
        if (stringArray != null) {
            Arrays.sort(stringArray);
            for (String s : stringArray) {
                builder.append(s).append(seperator);
            }
        }
        return builder.toString();
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            String mutedUsers;
            String administrators;
            String chatRoomName = this.getChatRoomName();
            chatRoomName = ChatRoomUtils.validateChatRoomNameForAccess(chatRoomName);
            RegistryPrx registryPrx = connection.findRegistry();
            if (registryPrx == null) {
                throw new Exception("Unable to locate registry");
            }
            ChatRoomPrx chatRoomPrx = registryPrx.findChatRoomObject(chatRoomName);
            FusionPktChatRoomParticipantsOld reply = new FusionPktChatRoomParticipantsOld(this.transactionId);
            reply.setChatRoomName(chatRoomName);
            String[] participantArray = chatRoomPrx.getParticipants(connection.getUsername());
            String participants = this.concat(participantArray, ";");
            if (participants != null && participants.length() > 0) {
                reply.setParticipants(participants);
            }
            if ((administrators = this.concat(chatRoomPrx.getAdministrators(connection.getUsername()), ";")) != null && administrators.length() > 0) {
                reply.setAdministrators(administrators);
            }
            if ((mutedUsers = this.concat(connection.getUserPrx().getBlockListFromUsernames(participantArray), ";")) != null && mutedUsers.length() > 0) {
                reply.setMutedParticipants(mutedUsers);
            }
            return new FusionPacket[]{reply};
        }
        catch (ChatRoomValidationException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get chat room participants - " + e.getMessage()).toArray();
        }
        catch (ObjectNotFoundException e) {
            FusionPktChatRoomParticipantsOld reply = new FusionPktChatRoomParticipantsOld(this.transactionId);
            reply.setChatRoomName(this.getChatRoomName());
            return new FusionPacket[]{reply};
        }
        catch (Exception e) {
            log.error((Object)"Exception while trying to get chat room participants.", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get chat room participants - Internal Error").toArray();
        }
    }
}

