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
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktRemoveFavouriteChatRoomOld
extends FusionRequest {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktRemoveFavouriteChatRoomOld.class));

    public FusionPktRemoveFavouriteChatRoomOld() {
        super((short)712);
    }

    public FusionPktRemoveFavouriteChatRoomOld(short transactionId) {
        super((short)712, transactionId);
    }

    public FusionPktRemoveFavouriteChatRoomOld(FusionPacket packet) {
        super(packet);
    }

    public Short getCategory() {
        return this.getShortField((short)1);
    }

    public void setCategory(short category) {
        this.setField((short)1, category);
    }

    public String getChatRoomName() {
        return this.getStringField((short)2);
    }

    public void setChatRoomName(String chatRoomName) {
        this.setField((short)2, chatRoomName);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            String chatRoomName = this.getChatRoomName();
            chatRoomName = ChatRoomUtils.validateChatRoomNameForAccess(chatRoomName);
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            messageEJB.removeFavouriteChatRoom(connection.getUsername(), chatRoomName);
            return new FusionPktOk(this.transactionId).toArray();
        }
        catch (CreateException e) {
            log.error((Object)"Exception while trying to remove favorite chat room.", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to remove favorite chat room - Internal Error").toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to remove favorite chat room - " + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (ChatRoomValidationException e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to remove favorite chat room - " + e.getMessage());
            return new FusionPacket[]{pktError};
        }
        catch (Exception e) {
            log.error((Object)"Exception while trying to remove favorite chat room.", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to remove favorite chat room - Internal Error").toArray();
        }
    }
}

