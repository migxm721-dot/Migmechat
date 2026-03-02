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
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktAlertOld;
import com.projectgoth.fusion.gateway.packet.FusionPktContactOld;
import com.projectgoth.fusion.gateway.packet.FusionPktDisplayPictureOld;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktMidletTab;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionPktPresenceOld;
import com.projectgoth.fusion.gateway.packet.FusionPktServerQuestionOld;
import com.projectgoth.fusion.gateway.packet.FusionPktStatusMessageOld;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardI;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.ContactDataIce;
import com.projectgoth.fusion.slice.ContactList;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.rmi.RemoteException;
import java.util.LinkedList;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktHaveLatestContactListOld
extends FusionRequest {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktHaveLatestContactListOld.class));

    public FusionPktHaveLatestContactListOld() {
        super((short)419);
    }

    public FusionPktHaveLatestContactListOld(short transactionId) {
        super((short)419, transactionId);
    }

    public FusionPktHaveLatestContactListOld(FusionPacket packet) {
        super(packet);
    }

    public Long getStatusTimestamp() {
        return this.getLongField((short)1);
    }

    public void setStatusTimestamp(String statusTimestamp) {
        this.setField((short)1, statusTimestamp);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            String infoText;
            Long statusTimestamp = this.getStatusTimestamp();
            if (statusTimestamp == null) {
                throw new Exception("Status timestamp not specified");
            }
            statusTimestamp = statusTimestamp - 300000L;
            UserPrx userPrx = connection.getUserPrx();
            if (userPrx == null) {
                FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "User not logged in");
                return new FusionPacket[]{pktError};
            }
            ContactList contactList = userPrx.getContactList();
            LinkedList<FusionPacket> fusionPkts = new LinkedList<FusionPacket>();
            fusionPkts.add(new FusionPktOk(this.transactionId));
            for (ContactDataIce contact : contactList.contacts) {
                ContactData contactData = new ContactData(contact);
                if (contactData.isOtherIMOnly()) {
                    fusionPkts.add(new FusionPktContactOld(this.transactionId, contactData, connection));
                    continue;
                }
                if (contactData.isOnline()) {
                    fusionPkts.add(new FusionPktPresenceOld(this.transactionId, contactData, connection));
                }
                long contactStatusTimestamp = 0L;
                if (contactData.statusTimeStamp != null) {
                    contactStatusTimestamp = contactData.statusTimeStamp.getTime();
                }
                if (contactStatusTimestamp <= statusTimestamp) continue;
                FusionPktStatusMessageOld statusMessage = new FusionPktStatusMessageOld();
                statusMessage.setContactID(contactData.id);
                statusMessage.setStatusTimeStamp(System.currentTimeMillis());
                if (contactData.statusMessage != null) {
                    statusMessage.setStatusMessage(contactData.statusMessage);
                }
                fusionPkts.add(statusMessage);
                FusionPktDisplayPictureOld displayPicture = new FusionPktDisplayPictureOld(this.transactionId);
                displayPicture.setContactID(contactData.id);
                displayPicture.setStatusTimeStamp(System.currentTimeMillis());
                if (contactData.displayPicture != null) {
                    displayPicture.setDisplayPicture(contactData.displayPicture);
                }
                fusionPkts.add(displayPicture);
            }
            MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
            FusionPktServerQuestionOld serverQuestion = connection.getServerQuestionOld();
            if (serverQuestion != null) {
                fusionPkts.add(serverQuestion);
            }
            if (serverQuestion == null && contactList.contacts.length < 2 && (infoText = misEJB.getInfoText(40)) != null) {
                FusionPktAlertOld pktAlert = new FusionPktAlertOld(this.transactionId);
                pktAlert.setAlertType((short)1);
                pktAlert.setContentType((byte)1);
                pktAlert.setContent(infoText);
                fusionPkts.add(pktAlert);
            }
            for (FusionPktMidletTab pktMidletTab : connection.getMidletTabsAfterLogin()) {
                pktMidletTab.setTransactionId(this.getTransactionId());
                fusionPkts.add(pktMidletTab);
            }
            connection.clearMidletTabAfterLogin();
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
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Contact list check failed").toArray();
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, e.getMessage()).toArray();
        }
    }
}

