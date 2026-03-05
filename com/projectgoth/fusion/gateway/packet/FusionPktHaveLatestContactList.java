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
import com.projectgoth.fusion.fdl.enums.AlertContentType;
import com.projectgoth.fusion.fdl.packets.FusionPktDataAlert;
import com.projectgoth.fusion.fdl.packets.FusionPktDataHaveLatestContactList;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktAlert;
import com.projectgoth.fusion.gateway.packet.FusionPktContact;
import com.projectgoth.fusion.gateway.packet.FusionPktDisplayPicture;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktMidletTab;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionPktPresence;
import com.projectgoth.fusion.gateway.packet.FusionPktServerQuestion;
import com.projectgoth.fusion.gateway.packet.FusionPktStatusMessage;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardI;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.ContactDataIce;
import com.projectgoth.fusion.slice.ContactList;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.LinkedList;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktHaveLatestContactList
extends FusionPktDataHaveLatestContactList {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktHaveLatestContactList.class));

    public FusionPktHaveLatestContactList() {
    }

    public FusionPktHaveLatestContactList(short transactionId) {
        super(transactionId);
    }

    public FusionPktHaveLatestContactList(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktHaveLatestContactList(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            String infoText;
            Long statusTimestamp = this.getTimestamp();
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
                    fusionPkts.add(new FusionPktContact(this.transactionId, contactData, connection));
                    continue;
                }
                if (contactData.isOnline()) {
                    fusionPkts.add(new FusionPktPresence(this.transactionId, contactData, connection));
                }
                long contactStatusTimestamp = 0L;
                if (contactData.statusTimeStamp != null) {
                    contactStatusTimestamp = contactData.statusTimeStamp.getTime();
                }
                if (contactStatusTimestamp <= statusTimestamp) continue;
                FusionPktStatusMessage statusMessage = new FusionPktStatusMessage();
                statusMessage.setContactId(contactData.id);
                statusMessage.setTimestamp(System.currentTimeMillis());
                if (contactData.statusMessage != null) {
                    statusMessage.setStatusMessage(contactData.statusMessage);
                }
                fusionPkts.add(statusMessage);
                FusionPktDisplayPicture displayPicture = new FusionPktDisplayPicture(this.transactionId);
                displayPicture.setContactId(contactData.id);
                displayPicture.setTimestamp(System.currentTimeMillis());
                if (contactData.displayPicture != null) {
                    displayPicture.setDisplayPictureGuid(contactData.displayPicture);
                }
                fusionPkts.add(displayPicture);
            }
            MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
            FusionPktServerQuestion serverQuestion = connection.getServerQuestion();
            if (serverQuestion != null) {
                fusionPkts.add(serverQuestion);
            }
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

