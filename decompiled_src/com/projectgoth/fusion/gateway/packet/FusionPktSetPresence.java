/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 */
package com.projectgoth.fusion.gateway.packet;

import Ice.LocalException;
import com.projectgoth.fusion.fdl.packets.FusionPktDataSetPresence;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.SessionPrx;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FusionPktSetPresence
extends FusionPktDataSetPresence {
    public FusionPktSetPresence(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktSetPresence(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            SessionPrx sessionPrx = connection.getSessionPrx();
            if (sessionPrx == null) {
                FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "User not logged in");
                return new FusionPacket[]{pktError};
            }
            sessionPrx.setPresence(this.getPresence().value());
            return new FusionPacket[]{new FusionPktOk(this.transactionId)};
        }
        catch (LocalException e) {
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Failed to set presence").toArray();
        }
        catch (Exception e) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to set presence - " + e.getMessage());
            return new FusionPacket[]{pktError};
        }
    }
}

