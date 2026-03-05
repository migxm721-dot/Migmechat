/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.FloodControl;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.packets.FusionPktDataSetStatusMessage;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public class FusionPktSetStatusMessage
extends FusionPktDataSetStatusMessage {
    public FusionPktSetStatusMessage(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktSetStatusMessage(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            FloodControl.detectFlooding(connection.getUsername(), connection.getUserPrx(), FloodControl.Action.SET_STATUS);
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            userEJB.updateStatusMessage(connection.getUserID(), connection.getUsername(), this.getStatusMessage(), connection.getDeviceType(), null);
            connection.getSessionPrx().statusMessageSet();
            return new FusionPktOk(this.transactionId).toArray();
        }
        catch (CreateException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to set status message - Failed to create UserEJB").toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to set status message - " + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to set status message - " + e.getMessage()).toArray();
        }
    }
}

