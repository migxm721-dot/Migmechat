/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 */
package com.projectgoth.fusion.gateway.packet;

import Ice.LocalException;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardDispatcher;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.RegistryPrx;

public class FusionPktLeavePrivateChatOld
extends FusionRequest {
    public FusionPktLeavePrivateChatOld() {
        super((short)507);
    }

    public FusionPktLeavePrivateChatOld(short transactionId) {
        super((short)507, transactionId);
    }

    public FusionPktLeavePrivateChatOld(FusionPacket packet) {
        super(packet);
    }

    public String getDestinationUsername() {
        return this.getStringField((short)1);
    }

    public void setDestinationUsername(String destUsername) {
        this.setField((short)1, destUsername);
    }

    public Byte getIMType() {
        return this.getByteField((short)2);
    }

    public void setIMType(byte imType) {
        this.setField((short)2, imType);
    }

    public boolean sessionRequired() {
        return true;
    }

    private FusionPacket[] leaveFusionPrivateChat(ConnectionI connection) throws Exception {
        RegistryPrx registryPrx = connection.findRegistry();
        if (registryPrx == null) {
            throw new Exception("Unable to locate registry");
        }
        String dest = this.getDestinationUsername();
        if (dest == null) {
            throw new Exception("You must specify the username of the person you were private chatting with");
        }
        ClientType dt = ClientType.fromValue(connection.getDeviceTypeAsInt());
        MessageSwitchboardDispatcher.getInstance().onLeavePrivateChat(connection, connection.getUserID(), connection.getUsername(), dest, dt, connection.getClientVersion());
        return new FusionPktOk(this.transactionId).toArray();
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            ImType imType = ImType.FUSION;
            Byte byteVal = this.getIMType();
            if (byteVal != null && (imType = ImType.fromValue(byteVal)) == null) {
                throw new Exception("Invalid IM type " + byteVal);
            }
            switch (imType) {
                case FUSION: {
                    return this.leaveFusionPrivateChat(connection);
                }
            }
            throw new Exception("Group chat is not supported for IM type " + imType);
        }
        catch (FusionException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to leave private chat - " + e.message).toArray();
        }
        catch (LocalException e) {
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Failed to leave private chat").toArray();
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to leave private chat - " + e.getMessage()).toArray();
        }
    }
}

