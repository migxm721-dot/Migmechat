/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public class FusionPktRemoveGroupOld
extends FusionRequest {
    public FusionPktRemoveGroupOld() {
        super((short)409);
    }

    public FusionPktRemoveGroupOld(short transactionId) {
        super((short)409, transactionId);
    }

    public FusionPktRemoveGroupOld(FusionPacket packet) {
        super(packet);
    }

    public Integer getGroupID() {
        return this.getIntField((short)1);
    }

    public void setGroupID(int groupID) {
        this.setField((short)1, groupID);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            Integer groupID = this.getGroupID();
            if (groupID == null) {
                return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Group ID not set").toArray();
            }
            Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
            contactEJB.removeGroup(connection.getUserID(), connection.getUsername(), groupID);
            return new FusionPktOk(this.transactionId).toArray();
        }
        catch (CreateException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to create ContactEJB").toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to remove group - " + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
    }
}

