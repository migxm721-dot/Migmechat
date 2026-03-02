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
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.ContactGroupData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.enums.AlertContentType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.fdl.packets.FusionPktDataAlert;
import com.projectgoth.fusion.fdl.packets.FusionPktDataGetContacts;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktAlert;
import com.projectgoth.fusion.gateway.packet.FusionPktContact;
import com.projectgoth.fusion.gateway.packet.FusionPktContactListVersion;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktGetContactsComplete;
import com.projectgoth.fusion.gateway.packet.FusionPktGroup;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktMidletTab;
import com.projectgoth.fusion.gateway.packet.FusionPktServerQuestion;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardI;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.ContactDataIce;
import com.projectgoth.fusion.slice.ContactGroupDataIce;
import com.projectgoth.fusion.slice.ContactList;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.LinkedList;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktGetContacts
extends FusionPktDataGetContacts {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetContacts.class));

    public FusionPktGetContacts() {
    }

    public FusionPktGetContacts(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktGetContacts(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            UserPrx userPrx = connection.getUserPrx();
            if (userPrx == null) {
                FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "User not logged in");
                return new FusionPacket[]{pktError};
            }
            ContactList contactList = userPrx.getContactList();
            boolean defaultGroupPktRequired = !connection.isMidletVersionAndAbove(400);
            boolean contactGroupModified = false;
            if (this.getContactId() != null) {
                int contactId = this.getContactId();
                for (ContactDataIce contact : contactList.contacts) {
                    if (contact.id != contactId) continue;
                    ContactData contactData = new ContactData(contact);
                    if (contactData.contactGroupId == null && defaultGroupPktRequired) {
                        contactData.contactGroupId = -1;
                    }
                    return new FusionPacket[]{new FusionPktContact(this.transactionId, contactData, connection)};
                }
                return null;
            }
            LinkedList<FusionPacket> fusionPkts = new LinkedList<FusionPacket>();
            for (ContactGroupDataIce group : contactList.contactGroups) {
                fusionPkts.add(new FusionPktGroup(this.transactionId, new ContactGroupData(group)));
            }
            if (connection.isAjax() || connection.isMidletVersionAndAbove(440)) {
                fusionPkts.add(new FusionPktGroup(this.transactionId, -3, "Facebook"));
                fusionPkts.add(new FusionPktGroup(this.transactionId, -4, "Google Talk"));
                fusionPkts.add(new FusionPktGroup(this.transactionId, -5, "MSN"));
                fusionPkts.add(new FusionPktGroup(this.transactionId, -6, "Yahoo!"));
            }
            PresenceType presence = this.getPresence();
            for (ContactDataIce contact : contactList.contacts) {
                ContactData contactData = new ContactData(contact);
                if (contactData.contactGroupId == null && defaultGroupPktRequired) {
                    contactData.contactGroupId = -1;
                    contactGroupModified = true;
                }
                if (presence != null) {
                    if (presence.isOnline() != contactData.isOnline()) continue;
                    fusionPkts.add(new FusionPktContact(this.transactionId, contactData, connection));
                    continue;
                }
                fusionPkts.add(new FusionPktContact(this.transactionId, contactData, connection));
            }
            if (defaultGroupPktRequired && (contactList.contactGroups.length == 0 || contactGroupModified)) {
                fusionPkts.add(0, new FusionPktGroup(this.transactionId, -1, "migme"));
                connection.setDefaultGroupPktSent(true);
            }
            FusionPktContactListVersion pktVersion = new FusionPktContactListVersion(this.transactionId);
            pktVersion.setVersion(contactList.version);
            pktVersion.setTimestamp(System.currentTimeMillis());
            fusionPkts.add(pktVersion);
            connection.getLastContactListVersionSent().set(contactList.version);
            fusionPkts.add(new FusionPktGetContactsComplete(this.transactionId));
            FusionPktServerQuestion serverQuestion = connection.getServerQuestion();
            if (serverQuestion != null) {
                fusionPkts.add(serverQuestion);
            }
            if (connection.isMidletVersionAndAbove(400)) {
                String infoText;
                MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
                if (serverQuestion == null && contactList.contacts.length < 2 && (infoText = misEJB.getInfoText(40)) != null) {
                    FusionPktAlert pktAlert = new FusionPktAlert(this.transactionId);
                    pktAlert.setAlertType(FusionPktDataAlert.AlertType.INFORMATION);
                    pktAlert.setContentType(AlertContentType.TEXT);
                    pktAlert.setContent(infoText);
                    fusionPkts.add(pktAlert);
                }
                for (FusionPktMidletTab pktMidletTab : connection.getMidletTabsAfterLogin()) {
                    pktMidletTab.setTransactionId(this.getTransactionId());
                    fusionPkts.add(pktMidletTab);
                }
                connection.clearMidletTabAfterLogin();
            }
            if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.STANDALONE_MESSAGE_SWITCHBOARD_ENABLED)) {
                MessageSwitchboardPrx msp = connection.getGatewayContext().getRegistryPrx().getMessageSwitchboard();
                msp.onLogon(connection.getUserID(), connection.getSessionPrx(), this.transactionId, connection.getUsername());
            } else {
                MessageSwitchboardI msi = new MessageSwitchboardI();
                msi.onLogon(connection.getUserID(), connection.getSessionPrx(), this.transactionId, connection.getUsername());
            }
            return fusionPkts.toArray(new FusionPacket[fusionPkts.size()]);
        }
        catch (CreateException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to create MIS EJB").toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, e.getClass().getName() + ":" + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (LocalException e) {
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Failed to get contacts").toArray();
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, e.getMessage()).toArray();
        }
    }
}

