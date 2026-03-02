/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.packets.FusionPktDataUnmuteChatroomParticipant;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public class FusionPktUnmuteChatroomParticipant
extends FusionPktDataUnmuteChatroomParticipant {
    public FusionPktUnmuteChatroomParticipant(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktUnmuteChatroomParticipant(FusionPacket packet) {
        super(packet);
    }

    public String getChatRoomNameForRateLimit() {
        return this.getChatroomName();
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            String chatRoomName = this.getChatroomName();
            if (chatRoomName == null) {
                throw new Exception("You must specify a chat room name");
            }
            String userToUnmute = this.getUsername();
            if (userToUnmute == null) {
                throw new Exception("You must specify a user to unmute");
            }
            Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
            contactEJB.unblockContact(connection.getUsername(), userToUnmute, false);
            MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
            FusionPktOk pkt = new FusionPktOk(this.transactionId);
            pkt.setServerResponse(misEJB.getInfoText(39).replaceAll("%u", userToUnmute));
            return new FusionPacket[]{pkt};
        }
        catch (CreateException e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to unmute user - Failed to create ContactEJB");
            return new FusionPacket[]{pktError};
        }
        catch (RemoteException e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to unmute user - " + RMIExceptionHelper.getRootMessage(e));
            return new FusionPacket[]{pktError};
        }
        catch (Exception e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to unmute user - " + e.getMessage());
            return new FusionPacket[]{pktError};
        }
    }
}

