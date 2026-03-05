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
import com.projectgoth.fusion.fdl.packets.FusionPktDataLeavePrivateChat;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardDispatcher;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.RegistryPrx;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FusionPktLeavePrivateChat
extends FusionPktDataLeavePrivateChat {
    public FusionPktLeavePrivateChat(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktLeavePrivateChat(FusionPacket packet) {
        super(packet);
    }

    private FusionPacket[] leaveFusionPrivateChat(ConnectionI connection) throws Exception {
        RegistryPrx registryPrx = connection.findRegistry();
        if (registryPrx == null) {
            throw new Exception("Unable to locate registry");
        }
        String dest = this.getChatId();
        if (dest == null) {
            throw new Exception("You must specify the username of the person you were private chatting with");
        }
        ClientType dt = ClientType.fromValue(connection.getDeviceTypeAsInt());
        MessageSwitchboardDispatcher.getInstance().onLeavePrivateChat(connection, connection.getUserID(), connection.getUsername(), dest, dt, connection.getClientVersion());
        return new FusionPktOk(this.transactionId).toArray();
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            ImType imType = this.getImType();
            switch (imType != null ? imType : ImType.FUSION) {
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

