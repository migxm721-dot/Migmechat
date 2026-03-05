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

public class FusionPktSetPermissionOld
extends FusionRequest {
    public FusionPktSetPermissionOld() {
        super((short)417);
    }

    public FusionPktSetPermissionOld(short transactionId) {
        super((short)417, transactionId);
    }

    public FusionPktSetPermissionOld(FusionPacket packet) {
        super(packet);
    }

    public String getUsername() {
        return this.getStringField((short)1);
    }

    public void setUsername(String username) {
        this.setField((short)1, username);
    }

    public Byte getPermission() {
        return this.getByteField((short)2);
    }

    public void setPermission(byte permission) {
        this.setField((short)2, permission);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            String username = this.getUsername();
            if (username == null) {
                throw new Exception("You must specify an username");
            }
            Byte byteVal = this.getPermission();
            if (byteVal == null) {
                throw new Exception("You must specify permission");
            }
            int permission = byteVal.intValue();
            if (permission == 1) {
                Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
                contactEJB.unblockContact(connection.getUsername(), username, false);
            } else if (permission == 2) {
                Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
                contactEJB.blockContact(connection.getUserID(), connection.getUsername(), username);
            } else {
                throw new Exception("Invalid permission type " + permission);
            }
            return new FusionPktOk(this.transactionId).toArray();
        }
        catch (CreateException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to set permission - Failed to create ContactEJB").toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to set permission - " + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to set permission - " + e.getMessage()).toArray();
        }
    }
}

