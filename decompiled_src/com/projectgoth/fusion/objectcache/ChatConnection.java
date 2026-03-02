/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.UserException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import Ice.LocalException;
import Ice.UserException;
import com.projectgoth.fusion.chatsync.MessageStatusEvent;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.ContactGroupData;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.slice.AMI_Connection_putMessageAsync;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.ConnectionPrxHelper;
import com.projectgoth.fusion.slice.CurrencyDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.UserEventIce;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Logger;

public class ChatConnection {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatConnection.class));
    private ConnectionPrx connectionProxy;
    private ConnectionPrx oneWayConnectionProxy;

    public ChatConnection(ConnectionPrx connectionProxy) {
        this.connectionProxy = connectionProxy;
        this.oneWayConnectionProxy = ConnectionPrxHelper.uncheckedCast(connectionProxy.ice_oneway());
        this.oneWayConnectionProxy = (ConnectionPrx)this.oneWayConnectionProxy.ice_connectionId("OneWayProxyGroup");
    }

    public ConnectionPrx getConnectionProxy() {
        return this.connectionProxy;
    }

    public void silentlyDropIncomingPackets() {
        this.connectionProxy.silentlyDropIncomingPackets();
    }

    public void disconnect(String reason) throws FusionException {
        this.connectionProxy.disconnect(reason);
    }

    public void putMessage(MessageDataIce message) throws FusionException {
        if (SystemPropertyEntities.GatewaySettings.Cache.asyncPutMessageEnabled.getValue().booleanValue()) {
            this.putMessageAsync(message);
        } else {
            this.putMessageSync(message);
        }
    }

    public void putMessageSync(MessageDataIce message) throws FusionException {
        try {
            this.connectionProxy.putMessage(message);
        }
        catch (LocalException e) {
            if (SystemPropertyEntities.Temp.Cache.se472ChatCxnLoggingChangesEnabled.getValue().booleanValue()) {
                String destType = message.messageDestinations != null && message.messageDestinations.length != 0 ? "" + message.messageDestinations[0].type : "NA";
                log.error((Object)("[MSGTYPE:" + message.type + "][DESTTYPE:" + destType + "] " + "Ice.LocalException in ChatConnection.putMessage e=" + (Object)((Object)e)), (Throwable)e);
            }
            throw new FusionException("The user is no longer connected");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void putMessageAsync(MessageDataIce message) throws FusionException {
        try {
            final Object monitor = new Object();
            final AtomicReference failure = new AtomicReference();
            final AtomicBoolean completed = new AtomicBoolean(false);
            this.connectionProxy.putMessageAsync_async(new AMI_Connection_putMessageAsync(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void ice_response() {
                    Object object = monitor;
                    synchronized (object) {
                        completed.set(true);
                        monitor.notify();
                    }
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void ice_exception(UserException ex) {
                    Object object = monitor;
                    synchronized (object) {
                        failure.set(ex.getCause());
                        monitor.notify();
                    }
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void ice_exception(LocalException ex) {
                    Object object = monitor;
                    synchronized (object) {
                        failure.set(ex.getCause());
                        monitor.notify();
                    }
                }
            }, message);
            Object object = monitor;
            synchronized (object) {
                long endTime = System.currentTimeMillis() + SystemPropertyEntities.GatewaySettings.Cache.asyncPutMessageTimeoutMillis.getValue();
                while (!completed.get() && null == failure.get() && System.currentTimeMillis() < endTime) {
                    try {
                        monitor.wait(SystemPropertyEntities.GatewaySettings.Cache.asyncPutMessageTimeoutMillis.getValue());
                    }
                    catch (InterruptedException e) {
                        // empty catch block
                    }
                    if (null != failure.get()) {
                        throw (Throwable)failure.get();
                    }
                    if (System.currentTimeMillis() < endTime) continue;
                    String err = "Timed out waiting for completion of ChatConnection.putMessage!";
                    log.warn((Object)"Timed out waiting for completion of ChatConnection.putMessage!");
                    throw new Exception("Timed out waiting for completion of ChatConnection.putMessage!");
                }
            }
        }
        catch (Throwable t) {
            String destType = message.messageDestinations != null && message.messageDestinations.length != 0 ? "" + message.messageDestinations[0].type : "NA";
            log.error((Object)("[MSGTYPE:" + message.type + "][DESTTYPE:" + destType + "] " + "Exception (Throwable) in ChatConnection.putMessage t=" + t), t);
            throw new FusionException("The user is no longer connected");
        }
    }

    public void putMessageOneWay(MessageDataIce message) {
        try {
            this.oneWayConnectionProxy.putMessageOneWay(message);
        }
        catch (LocalException localException) {
            // empty catch block
        }
    }

    public void putMessages(MessageDataIce[] messages) throws FusionException {
        try {
            this.connectionProxy.putMessages(messages);
        }
        catch (LocalException e) {
            throw new FusionException("The user is no longer connected");
        }
    }

    public void contactChangedPresence(int contactID, int imType, int presence) throws Exception {
        this.oneWayConnectionProxy.contactChangedPresenceOneWay(contactID, imType, presence);
    }

    public void contactChangedDisplayPicture(int contactID, String displayPicture, long timeStamp) throws Exception {
        this.oneWayConnectionProxy.contactChangedDisplayPictureOneWay(contactID, displayPicture, timeStamp);
    }

    public void contactChangedStatusMessage(int contactID, String statusMessage, long timeStamp) throws Exception {
        this.oneWayConnectionProxy.contactChangedStatusMessageOneWay(contactID, statusMessage, timeStamp);
    }

    public void contactRequest(String username, int outstandingRequests) throws Exception {
        this.connectionProxy.contactRequest(username, outstandingRequests);
    }

    public void contactRequestAccepted(ContactData contact, int contactListVersion, int outstandingRequests) throws Exception {
        this.connectionProxy.contactRequestAccepted(contact.toIceObject(), contactListVersion, outstandingRequests);
    }

    public void contactRequestRejected(String username, int outstandingRequests) throws Exception {
        this.connectionProxy.contactRequestRejected(username, outstandingRequests);
    }

    public void contactAdded(ContactData contact, int contactListVersion, boolean guaranteedIsNew) throws FusionException {
        this.connectionProxy.contactAdded(contact.toIceObject(), contactListVersion, guaranteedIsNew);
    }

    public void contactRemoved(int contactID, int contactListVersion) throws FusionException {
        this.connectionProxy.contactRemoved(contactID, contactListVersion);
    }

    public void contactGroupAdded(ContactGroupData contactGroup, int contactListVersion) throws FusionException {
        this.connectionProxy.contactGroupAdded(contactGroup.toIceObject(), contactListVersion);
    }

    public void contactGroupRemoved(int contactGroupID, int contactListVersion) throws FusionException {
        this.connectionProxy.contactGroupRemoved(contactGroupID, contactListVersion);
    }

    public void otherIMLoggedIn(int imType) throws FusionException {
        this.connectionProxy.otherIMLoggedIn(imType);
    }

    public void otherIMLoggedOut(int imType, String reason) throws FusionException {
        this.connectionProxy.otherIMLoggedOut(imType, reason);
    }

    public void otherIMConferenceCreated(ImType imType, String conferenceID, String creator) throws FusionException {
        this.connectionProxy.otherIMConferenceCreated(imType.value(), conferenceID, creator);
    }

    public void privateChatNowAGroupChat(String groupChatID, String creator) throws FusionException {
        this.connectionProxy.privateChatNowAGroupChat(groupChatID, creator);
    }

    public void putEvent(UserEventIce event) throws FusionException {
        this.connectionProxy.putEvent(event);
    }

    public void putAlertMessage(String message, String title, short timeout) throws FusionException {
        this.connectionProxy.putAlertMessage(message, title, timeout);
    }

    public void putAlertMessageOneWay(String message, String title, short timeout) {
        this.oneWayConnectionProxy.putAlertMessageOneWay(message, title, timeout);
    }

    public void putServerQuestion(String message, String url) throws FusionException {
        this.connectionProxy.putServerQuestion(message, url);
    }

    public void putWebCallNotification(String source, String destination, int gateway, String gatewayName, int protocol) throws FusionException {
        this.connectionProxy.putWebCallNotification(source, destination, gateway, gatewayName, protocol);
    }

    public void putAnonymoustCallNotification(String requestingUsername, String requestingMobilePhone) throws FusionException {
        this.connectionProxy.putAnonymousCallNotification(requestingUsername, requestingMobilePhone);
    }

    public void putFileReceived(MessageDataIce message) throws FusionException {
        this.connectionProxy.putFileReceived(message);
    }

    public void emailNotification(int unreadEmailCount) throws FusionException {
        this.connectionProxy.emailNotification(unreadEmailCount);
    }

    public void emoticonsChanged(String[] hotKeys, String[] alternateKeys) throws FusionException {
        this.connectionProxy.emoticonsChanged(hotKeys, alternateKeys);
    }

    public void themeChanged(String themeLocation) throws FusionException {
        this.connectionProxy.themeChanged(themeLocation);
    }

    public void accountBalanceChanged(double balance, double fundedBalance, CurrencyDataIce currency) throws FusionException {
        this.connectionProxy.accountBalanceChanged(balance, fundedBalance, currency);
    }

    public void avatarChanged(String displayPicture, String statusMessage) throws FusionException {
        this.connectionProxy.avatarChanged(displayPicture, statusMessage);
    }

    public void pushNotification(Message message) throws FusionException {
        this.connectionProxy.pushNotification(message);
    }

    public void putSerializedPacket(byte[] packet) throws FusionException {
        this.connectionProxy.putSerializedPacket(packet);
    }

    public void putSerializedPacketOneWay(byte[] packet) {
        this.connectionProxy.putSerializedPacketOneWay(packet);
    }

    public void putMessageStatusEvent(MessageStatusEvent mse) throws FusionException {
        this.connectionProxy.putMessageStatusEvent(mse.toIceObject());
    }
}

