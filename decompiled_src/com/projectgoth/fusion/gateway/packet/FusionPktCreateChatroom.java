/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.ChatRoomUtils;
import com.projectgoth.fusion.common.ChatRoomValidationException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.packets.FusionPktDataCreateChatroom;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktJoinChatroom;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktCreateChatroom
extends FusionPktDataCreateChatroom {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktCreateChatroom.class));

    public FusionPktCreateChatroom() {
    }

    public FusionPktCreateChatroom(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktCreateChatroom(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            String chatRoomName = this.getChatroomName();
            chatRoomName = ChatRoomUtils.validateChatRoomNameForCreation(chatRoomName);
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            ChatRoomData chatRoom = new ChatRoomData();
            chatRoom.creator = connection.getUsername();
            chatRoom.name = chatRoomName;
            chatRoom.userOwned = true;
            chatRoom.allowBots = true;
            String keywords = null;
            if (SystemPropertyEntities.Temp.Cache.se524CreateChatRoomPktAdditionalDataSupportEnabled.getValue().booleanValue()) {
                chatRoom.description = this.getDescription();
                keywords = this.getKeywords();
                String language = this.getLanguage();
                if (language == null) {
                    language = SystemProperty.get(SystemPropertyEntities.Default.DEFAULT_LANGUAGE);
                }
                chatRoom.language = language;
                Boolean allowKicking = this.getAllowKicking();
                if (allowKicking != null) {
                    chatRoom.allowKicking = allowKicking;
                }
            }
            messageEJB.createChatRoom(chatRoom, keywords);
            FusionPktJoinChatroom join = new FusionPktJoinChatroom(this.transactionId);
            join.setChatroomName(chatRoomName);
            return join.process(connection);
        }
        catch (ChatRoomValidationException cve) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to create chat room - " + cve.getMessage()).toArray();
        }
        catch (CreateException e) {
            log.error((Object)"Caught Exception while trying to create chat room.", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to create chat room - Internal Error").toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to create chat room - " + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (Exception e) {
            log.error((Object)"Caught Exception while trying to create chat room.", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to create chat room - Internal Error").toArray();
        }
    }
}

