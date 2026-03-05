/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.enums.UserPermissionType;
import com.projectgoth.fusion.fdl.packets.FusionPktDataSetPermission;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public class FusionPktSetPermission
extends FusionPktDataSetPermission {
    public FusionPktSetPermission(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktSetPermission(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            String username = this.getUsername();
            if (username == null) {
                throw new Exception("You must specify an username");
            }
            UserPermissionType permission = this.getPermission();
            if (permission == null) {
                throw new Exception("You must specify permission");
            }
            switch (permission) {
                case ALLOW: {
                    Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
                    contactEJB.unblockContact(connection.getUsername(), username, false);
                    break;
                }
                case BLOCK: {
                    Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
                    contactEJB.blockContact(connection.getUserID(), connection.getUsername(), username);
                    break;
                }
                default: {
                    throw new Exception("Invalid permission type " + (Object)((Object)permission));
                }
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

