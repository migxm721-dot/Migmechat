/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktContactRequest;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktGetContactRequests
extends FusionRequest {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetContactRequests.class));

    public FusionPktGetContactRequests() {
        super((short)425);
    }

    public FusionPktGetContactRequests(short transactionId) {
        super((short)425, transactionId);
    }

    public FusionPktGetContactRequests(FusionPacket packet) {
        super(packet);
    }

    public boolean sessionRequired() {
        return true;
    }

    public FusionPacket[] processRequest(ConnectionI connection) {
        ArrayList<FusionPktContactRequest> packetsToReturn = new ArrayList<FusionPktContactRequest>();
        try {
            Set newFollowerUsernames;
            log.info((Object)("FusionPktGetContactRequests [" + connection.getUsername() + "]"));
            boolean pushNewFollowersAsPendingContacts = SystemProperty.getBool(SystemPropertyEntities.Contacts.PUSH_NEW_FOLLOWERS_AS_PENDING_CONTACTS_ENABLED);
            int maxPendingContacts = SystemProperty.getInt(SystemPropertyEntities.Contacts.MAX_PENDING_CONTACTS_TO_RETRIEVE);
            String url = SystemProperty.get(SystemPropertyEntities.Default.USERPROFILE_URL);
            HashSet userList = new HashSet();
            Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
            Set pendingContacts = contactEJB.getPendingContacts(connection.getUsername());
            if (pendingContacts != null && pendingContacts.size() > 0) {
                userList.addAll(pendingContacts);
            }
            if (pushNewFollowersAsPendingContacts && userList.size() < maxPendingContacts && (newFollowerUsernames = contactEJB.getRecentFollowers(connection.getUserID())) != null && newFollowerUsernames.size() > 0) {
                userList.addAll(newFollowerUsernames);
            }
            for (String username : userList) {
                if (packetsToReturn.size() == maxPendingContacts) break;
                FusionPktContactRequest contactRequestPacket = new FusionPktContactRequest(username, url + username);
                contactRequestPacket.setTransactionId(this.transactionId);
                packetsToReturn.add(contactRequestPacket);
            }
            log.info((Object)("FusionPktGetContactRequests [" + connection.getUsername() + "] returning [" + packetsToReturn.size() + "]"));
            return packetsToReturn.toArray(new FusionPacket[packetsToReturn.size()]);
        }
        catch (CreateException e) {
            log.error((Object)"Unable to create bean", (Throwable)e);
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Unable to get contact requests").toArray();
        }
        catch (RemoteException e) {
            log.error((Object)("Remote exception: " + e.getClass().getName()), (Throwable)e);
            return new FusionPktInternalServerError(this.transactionId, e, "Unable to get contact requests").toArray();
        }
        catch (NoSuchFieldException e) {
            log.error((Object)e.getMessage());
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get contact requests.").toArray();
        }
        catch (Exception e) {
            log.error((Object)e.getMessage());
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get contact requests.").toArray();
        }
    }
}

