/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import Ice.LocalException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.FusionException;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktRemoveContactOld
extends FusionRequest {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktRemoveContactOld.class));

    public FusionPktRemoveContactOld() {
        super((short)406);
    }

    public FusionPktRemoveContactOld(short transactionId) {
        super((short)406, transactionId);
    }

    public FusionPktRemoveContactOld(FusionPacket packet) {
        super(packet);
    }

    public Integer getContactID() {
        return this.getIntField((short)1);
    }

    public void setContactID(int contactID) {
        this.setField((short)1, contactID);
    }

    public boolean sessionRequired() {
        return true;
    }

    private void removeIMEndPoint(ConnectionI connection, int contactId) throws FusionException {
        try {
            connection.getUserPrx().otherIMRemoveContact(contactId);
        }
        catch (FusionException e) {
            log.debug((Object)("Failed to remove contact[" + contactId + "] user from " + connection.getUsername() + "'s contact list - " + e.message));
            throw e;
        }
        catch (LocalException e) {
            log.debug((Object)("Failed to remove contact[" + contactId + "] user from " + connection.getUsername() + "'s contact list - " + e.getLocalizedMessage()));
        }
        catch (Exception e) {
            log.debug((Object)("Failed to remove contact[" + contactId + "] user from " + connection.getUsername() + "'s contact list - " + e.getLocalizedMessage()));
        }
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            Integer contactID = this.getContactID();
            if (contactID == null) {
                return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Contact ID not set").toArray();
            }
            Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
            if (SystemProperty.getBool(SystemPropertyEntities.Contacts.UNFOLLOW_ON_REMOVE_CONTACT_ENABLED)) {
                boolean unfollowContactOnMiniblog = SystemProperty.getBool(SystemPropertyEntities.Contacts.MIGBO_INTEGRATION_ENABLED);
                contactEJB.removeFusionUserFromContact(connection.getUserID(), connection.getUsername(), contactID, unfollowContactOnMiniblog);
            } else {
                contactEJB.removeContact(connection.getUserID(), connection.getUsername(), contactID);
            }
            this.removeIMEndPoint(connection, contactID);
            return new FusionPktOk(this.transactionId).toArray();
        }
        catch (CreateException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to create ContactEJB").toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to remove contact - " + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (FusionEJBException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to remove contact - " + e.getMessage()).toArray();
        }
        catch (FusionException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, e.message).toArray();
        }
        catch (Exception e) {
            return new FusionPktInternalServerError(this.transactionId, e, "Failed to remove contact").toArray();
        }
    }
}

