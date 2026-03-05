/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.Properties
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.chat.external;

import Ice.LocalException;
import Ice.Properties;
import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.chat.external.ChatConnectionInterface;
import com.projectgoth.fusion.chat.external.ChatConnectionListenerInterface;
import com.projectgoth.fusion.chat.external.aim.AIMConnection;
import com.projectgoth.fusion.chat.external.facebook.FacebookSession;
import com.projectgoth.fusion.chat.external.gtalk.GTalkSession;
import com.projectgoth.fusion.chat.external.msn.MSNConnection;
import com.projectgoth.fusion.chat.external.yahoo.YahooConnection;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.messagelogger.MessageToLog;
import com.projectgoth.fusion.objectcache.ChatObjectManagerUser;
import com.projectgoth.fusion.rewardsystem.RewardCentre;
import com.projectgoth.fusion.rewardsystem.triggers.ThirdPartyInstantMessageTrigger;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatExternal {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatExternal.class));
    private Credential[] otherIMCredentials;
    private Properties properties;
    private ChatConnectionInterface chatMsn;
    private ChatConnectionInterface chatYahoo;
    private ChatConnectionInterface chatAim;
    private ChatConnectionInterface chatGTalk;
    private ChatConnectionInterface chatFacebook;
    private ChatConnectionListenerInterface listener;
    private String username;
    private boolean showOfflineContacts;
    ChatObjectManagerUser objectManager;

    public ChatExternal(ChatObjectManagerUser objectManager, ChatConnectionListenerInterface listener, int userID, String username) throws FusionException {
        this.listener = listener;
        this.objectManager = objectManager;
        this.username = username;
        this.otherIMCredentials = objectManager.getUserCredentials(userID, PasswordType.OTHER_IM_TYPES);
        this.properties = objectManager.getProperties();
    }

    public Credential getIMCredential(ImType imType) {
        if (this.otherIMCredentials == null || this.otherIMCredentials.length == 0) {
            return null;
        }
        for (Credential credential : this.otherIMCredentials) {
            if (PasswordType.forIMEnum(imType).value() != credential.passwordType) continue;
            return credential;
        }
        return null;
    }

    public ChatConnectionInterface create(boolean showOfflineContacts, ImType imTypeEnum) throws FusionException {
        ChatConnectionInterface connection = ChatConnectionFactory.create(this.listener, imTypeEnum, this.getIMCredential(imTypeEnum), this.properties, showOfflineContacts);
        this.setChatConnection(connection, imTypeEnum);
        return connection;
    }

    public boolean getShowOfflineContacts() {
        return this.showOfflineContacts;
    }

    public void setShowOfflineContacts(boolean value) {
        this.showOfflineContacts = value;
    }

    public ChatConnectionInterface getChatConnection(ImType imType) throws Exception {
        switch (imType) {
            case AIM: {
                return this.chatMsn;
            }
            case FACEBOOK: {
                return this.chatFacebook;
            }
            case GTALK: {
                return this.chatGTalk;
            }
            case MSN: {
                return this.chatMsn;
            }
            case YAHOO: {
                return this.chatYahoo;
            }
        }
        throw new Exception("IM Type:" + imType + " is not supported");
    }

    public String getIMDisplayName(ImType imType) {
        switch (imType) {
            case MSN: {
                return Enums.IMEnum.MSN.getName();
            }
            case YAHOO: {
                return Enums.IMEnum.YAHOO.getName();
            }
            case AIM: {
                return Enums.IMEnum.AIM.getName();
            }
            case FACEBOOK: {
                return Enums.IMEnum.FACEBOOK.getName();
            }
            case GTALK: {
                return Enums.IMEnum.GTALK.getName();
            }
        }
        return "";
    }

    public MessageType getMessageTypeEnum(ImType imType) {
        switch (imType) {
            case AIM: {
                return MessageType.AIM;
            }
            case FACEBOOK: {
                return MessageType.FACEBOOK;
            }
            case FUSION: {
                return MessageType.FUSION;
            }
            case GTALK: {
                return MessageType.GTALK;
            }
            case MSN: {
                return MessageType.MSN;
            }
            case YAHOO: {
                return MessageType.YAHOO;
            }
            case ICQ: {
                break;
            }
        }
        return null;
    }

    public List<ChatConnectionInterface> getActiveChatConnections() {
        LinkedList<ChatConnectionInterface> list = new LinkedList<ChatConnectionInterface>();
        if (this.chatMsn != null && this.chatMsn.isSignedIn()) {
            list.add(this.chatMsn);
        }
        if (this.chatYahoo != null && this.chatYahoo.isSignedIn()) {
            list.add(this.chatYahoo);
        }
        if (this.chatAim != null && this.chatAim.isSignedIn()) {
            list.add(this.chatAim);
        }
        if (this.chatGTalk != null && this.chatGTalk.isSignedIn()) {
            list.add(this.chatGTalk);
        }
        if (this.chatFacebook != null && this.chatFacebook.isSignedIn()) {
            list.add(this.chatFacebook);
        }
        return list;
    }

    public int[] getConnectedOtherIMs() {
        ArrayList<ImType> connectedOtherIMs = new ArrayList<ImType>();
        if (this.chatMsn != null && this.chatMsn.isConnected()) {
            connectedOtherIMs.add(ImType.MSN);
        }
        if (this.chatYahoo != null && this.chatYahoo.isConnected()) {
            connectedOtherIMs.add(ImType.YAHOO);
        }
        if (this.chatAim != null && this.chatAim.isConnected()) {
            connectedOtherIMs.add(ImType.AIM);
        }
        if (this.chatGTalk != null && this.chatGTalk.isConnected()) {
            connectedOtherIMs.add(ImType.GTALK);
        }
        if (this.chatFacebook != null && this.chatFacebook.isConnected()) {
            connectedOtherIMs.add(ImType.FACEBOOK);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("found connection " + connectedOtherIMs.size() + " connected other IMs for user [" + this.username + "]"));
        }
        if (connectedOtherIMs.size() > 0) {
            int[] returnArray = new int[connectedOtherIMs.size()];
            for (int i = 0; i < connectedOtherIMs.size(); ++i) {
                returnArray[i] = ((ImType)connectedOtherIMs.get(i)).value();
            }
            return returnArray;
        }
        return null;
    }

    public void dispose() {
        if (this.chatGTalk != null) {
            log.warn((Object)"Gtalk is still connected... disconnecting now");
            this.chatGTalk.signOut();
        }
        if (this.chatFacebook != null) {
            log.warn((Object)"Facebook is still connected... disconnecting now");
            this.chatFacebook.signOut();
        }
    }

    public void setStatus(String statusMessage, PresenceType overallFusionPresence) {
        for (ChatConnectionInterface connection : this.getActiveChatConnections()) {
            try {
                connection.setStatus(overallFusionPresence, statusMessage);
            }
            catch (Exception e) {
                log.warn((Object)("Unable to change " + connection.getImType() + " presence for the user '" + this.username + "'. Exception: " + e.getMessage()));
            }
        }
    }

    public void logout(ImType imType) {
        switch (imType) {
            case MSN: {
                if (this.chatMsn == null) break;
                this.chatMsn.signOut();
                break;
            }
            case YAHOO: {
                if (this.chatYahoo == null) break;
                this.chatYahoo.signOut();
                break;
            }
            case AIM: {
                if (this.chatAim == null) break;
                this.chatAim.signOut();
                break;
            }
            case GTALK: {
                if (this.chatGTalk == null) break;
                this.chatGTalk.signOut();
                break;
            }
            case FACEBOOK: {
                if (this.chatFacebook == null) break;
                this.chatFacebook.signOut();
                break;
            }
            default: {
                log.warn((Object)("otherIMLogout() error: IM type " + imType.name() + " is not supported"));
            }
        }
    }

    public void sendMessage(int imType, String otherIMUsername, String message) throws FusionException {
        ChatConnectionInterface connection;
        ImType imTypeEnum = ImType.fromValue(imType);
        try {
            connection = this.getChatConnection(imTypeEnum);
        }
        catch (Exception e2) {
            log.warn((Object)("otherIMSendMessage() user:" + this.username + " error: IM type " + imTypeEnum + " is not supported"));
            throw new FusionException(e2.getMessage());
        }
        if (!connection.isSignedIn()) {
            throw new FusionException("You are not signed in to " + imTypeEnum);
        }
        try {
            connection.sendMessage(otherIMUsername, message);
        }
        catch (Exception e) {
            throw new FusionException(imTypeEnum + " user:" + this.username + " error: " + e.getMessage());
        }
    }

    public void addContact(int imType, String otherIMUsername) throws FusionException {
        ChatConnectionInterface connection;
        ImType imTypeEnum = ImType.fromValue(imType);
        try {
            connection = this.getChatConnection(imTypeEnum);
        }
        catch (Exception e2) {
            log.warn((Object)("otherIMAddContact() user:" + this.username + " error: IM type " + imTypeEnum + " is not supported"));
            throw new FusionException("IM type " + imTypeEnum + " is not supported");
        }
        if (connection == null || !connection.isSignedIn()) {
            throw new FusionException("You are not signed in to " + imTypeEnum);
        }
        try {
            connection.addContact(otherIMUsername);
        }
        catch (Exception e) {
            throw new FusionException(imTypeEnum + " Error: " + e.getMessage());
        }
    }

    public void removeContact(ContactData contactData) throws FusionException {
        String imUserName;
        ChatConnectionInterface connection;
        ImType imEnumType = contactData.defaultIM;
        try {
            connection = this.getChatConnection(imEnumType);
        }
        catch (Exception e2) {
            log.warn((Object)("otherIMRemoveContact() user:" + this.username + " error: IM type " + imEnumType + " is not supported"));
            throw new FusionException("IM type " + imEnumType + " is not supported");
        }
        switch (imEnumType) {
            case MSN: {
                imUserName = contactData.msnUsername;
                break;
            }
            case YAHOO: {
                imUserName = contactData.yahooUsername;
                break;
            }
            case AIM: {
                imUserName = contactData.aimUsername;
                break;
            }
            case GTALK: {
                imUserName = contactData.gtalkUsername;
                break;
            }
            case FACEBOOK: {
                imUserName = contactData.facebookUsername;
                break;
            }
            default: {
                log.warn((Object)("otherIMRemoveContact() error: IM type " + imEnumType + " is not supported"));
                throw new FusionException("IM type " + imEnumType + " is not supported");
            }
        }
        try {
            connection.removeContact(imUserName);
        }
        catch (Exception e) {
            throw new FusionException(imEnumType + " Error: " + e.getMessage());
        }
    }

    public void leaveConference(ImType imTypeEnum, String otherIMConferenceID) {
        ChatConnectionInterface connection = null;
        try {
            connection = this.getChatConnection(imTypeEnum);
        }
        catch (Exception e2) {
            log.warn((Object)("otherIMLeaveConference() user:" + this.username + " error: IM type " + imTypeEnum + " is not supported"));
        }
        if (connection == null || !connection.isSignedIn()) {
            return;
        }
        try {
            connection.leaveConference(otherIMConferenceID);
        }
        catch (Exception e) {
            log.warn((Object)("otherIMLeaveConference() user:" + this.username + " error: IM type " + imTypeEnum + " is not supported"));
        }
    }

    public String inviteToConference(ImType imTypeEnum, String otherIMConferenceID, String otherIMUsername) throws FusionException {
        ChatConnectionInterface connection;
        try {
            connection = this.getChatConnection(imTypeEnum);
        }
        catch (Exception e2) {
            log.warn((Object)("otherIMInviteToConference() user:" + this.username + " error: IM type " + imTypeEnum + " is not supported"));
            throw new FusionException(e2.getMessage());
        }
        if (connection == null || !connection.isSignedIn()) {
            throw new FusionException("You are not signed in to " + imTypeEnum);
        }
        try {
            return connection.inviteToConference(otherIMConferenceID, otherIMUsername);
        }
        catch (Exception e) {
            throw new FusionException(imTypeEnum + " user:" + this.username + " error: " + e.getMessage());
        }
    }

    public String[] getConferenceParticipants(ImType imTypeEnum, String otherIMConferenceID) {
        try {
            ChatConnectionInterface connection = this.getChatConnection(imTypeEnum);
            String[] participants = connection.getConferenceParticipants(otherIMConferenceID).toArray(new String[0]);
            return participants;
        }
        catch (Exception ex) {
            log.warn((Object)("otherIMGetConferenceParticipants() error: IM type " + imTypeEnum.name() + " is not supported"));
            return new String[0];
        }
    }

    public String getUsername(ImType imType) {
        return this.getIMCredential((ImType)imType).username;
    }

    public Credential[] getOtherIMCredentials() {
        return this.otherIMCredentials;
    }

    public void setIMCredentials(Credential[] credentialsForTypes) {
        this.otherIMCredentials = credentialsForTypes;
    }

    public void login(ImType imTypeEnum) throws FusionException {
        Credential credential = this.getIMCredential(imTypeEnum);
        if (credential == null || !StringUtils.hasLength((String)credential.username) || !StringUtils.hasLength((String)credential.password)) {
            throw new FusionException(imTypeEnum.name() + " details missing. Please specify your login details.");
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("attempting other IM login [" + imTypeEnum.name() + "] for user [" + this.username + "] with username [" + credential.username + "]"));
        }
        try {
            ChatConnectionInterface connection = this.getChatConnection(imTypeEnum);
            if (connection == null) {
                connection = this.create(this.showOfflineContacts, imTypeEnum);
                this.setChatConnection(connection, imTypeEnum);
            } else {
                connection.signIn(credential.username, credential.password);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void setChatConnection(ChatConnectionInterface connection, ImType imTypeEnum) {
        switch (imTypeEnum) {
            case AIM: {
                this.chatAim = connection;
                break;
            }
            case FACEBOOK: {
                this.chatFacebook = connection;
                break;
            }
            case FUSION: {
                break;
            }
            case GTALK: {
                this.chatGTalk = connection;
                break;
            }
            case ICQ: {
                break;
            }
            case MSN: {
                this.chatMsn = connection;
                break;
            }
            case YAHOO: {
                this.chatYahoo = connection;
                break;
            }
        }
    }

    public void verifyIsLoggedIn(MessageType msgType) throws FusionException {
        switch (msgType) {
            case MSN: {
                if (this.chatMsn != null && this.chatMsn.isSignedIn()) break;
                throw new FusionException("You are not connected to MSN");
            }
            case YAHOO: {
                if (this.chatYahoo != null && this.chatYahoo.isSignedIn()) break;
                throw new FusionException("You are not connected to Yahoo!");
            }
            case AIM: {
                if (this.chatAim != null && this.chatAim.isSignedIn()) break;
                throw new FusionException("You are not connected to AIM");
            }
            case GTALK: {
                if (this.chatGTalk != null && this.chatGTalk.isSignedIn()) break;
                throw new FusionException("You are not connected to GTalk");
            }
            case FACEBOOK: {
                if (this.chatFacebook != null && this.chatFacebook.isSignedIn()) break;
                throw new FusionException("You are not connected to Facebook");
            }
            case EMAIL: {
                break;
            }
            case FUSION: {
                break;
            }
            case OFFLINE_MESSAGE: {
                break;
            }
            case SMS: {
                break;
            }
        }
    }

    public void sendMessage(MessageType type, String destination, String messageText) throws Exception {
        switch (type) {
            case MSN: {
                this.chatMsn.sendMessage(destination, messageText);
                break;
            }
            case YAHOO: {
                this.chatYahoo.sendMessage(destination, messageText);
                break;
            }
            case AIM: {
                this.chatAim.sendMessage(destination, messageText);
                break;
            }
            case GTALK: {
                this.chatGTalk.sendMessage(destination, messageText);
                break;
            }
            case FACEBOOK: {
                this.chatFacebook.sendMessage(destination, messageText);
                break;
            }
            case EMAIL: {
                break;
            }
            case FUSION: {
                break;
            }
            case OFFLINE_MESSAGE: {
                break;
            }
            case SMS: {
                break;
            }
        }
    }

    public void sendMessage(MessageData messageData) throws Exception {
        switch (messageData.type) {
            case MSN: {
                this.chatMsn.sendMessage(messageData.messageDestinations.get((int)0).destination, messageData.messageText);
                break;
            }
            case YAHOO: {
                this.chatYahoo.sendMessage(messageData.messageDestinations.get((int)0).destination, messageData.messageText);
                break;
            }
            case AIM: {
                this.chatAim.sendMessage(messageData.messageDestinations.get((int)0).destination, messageData.messageText);
                break;
            }
            case GTALK: {
                this.chatGTalk.sendMessage(messageData.messageDestinations.get((int)0).destination, messageData.messageText);
                break;
            }
            case FACEBOOK: {
                this.chatFacebook.sendMessage(messageData.messageDestinations.get((int)0).destination, messageData.messageText);
                break;
            }
            case EMAIL: {
                break;
            }
            case FUSION: {
                break;
            }
            case OFFLINE_MESSAGE: {
                break;
            }
            case SMS: {
                break;
            }
        }
    }

    public void logout() {
        if (this.chatMsn != null) {
            this.chatMsn.signOut();
        }
        if (this.chatYahoo != null) {
            this.chatYahoo.signOut();
        }
        if (this.chatAim != null) {
            this.chatAim.signOut();
        }
        if (this.chatGTalk != null) {
            this.chatGTalk.signOut();
        }
        if (this.chatFacebook != null) {
            this.chatFacebook.signOut();
        }
    }

    public String getIMUsername(ContactData contactData, ImType imEnumType) throws FusionException {
        String imUserName;
        switch (imEnumType) {
            case MSN: {
                imUserName = contactData.msnUsername;
                break;
            }
            case YAHOO: {
                imUserName = contactData.yahooUsername;
                break;
            }
            case AIM: {
                imUserName = contactData.aimUsername;
                break;
            }
            case GTALK: {
                imUserName = contactData.gtalkUsername;
                break;
            }
            case FACEBOOK: {
                imUserName = contactData.facebookUsername;
                break;
            }
            default: {
                log.warn((Object)("otherIMRemoveContact() error: IM type " + contactData.defaultIM + " is not supported"));
                throw new FusionException("IM type " + imEnumType + " is not supported");
            }
        }
        return imUserName;
    }

    public void logMessage(MessageDataIce message, MessageType type, Integer countryID) {
        if (!this.objectManager.isLogMessagesToFile()) {
            return;
        }
        try {
            switch (type) {
                case MSN: {
                    this.objectManager.logMessage(MessageToLog.TypeEnum.MSN_SENT, countryID, message.source, message.messageDestinations[0].destination, 1, message.messageText);
                    break;
                }
                case YAHOO: {
                    this.objectManager.logMessage(MessageToLog.TypeEnum.YAHOO_SENT, countryID, message.source, message.messageDestinations[0].destination, 1, message.messageText);
                    break;
                }
                case AIM: {
                    this.objectManager.logMessage(MessageToLog.TypeEnum.AIM_SENT, countryID, message.source, message.messageDestinations[0].destination, 1, message.messageText);
                    break;
                }
                case GTALK: {
                    this.objectManager.logMessage(MessageToLog.TypeEnum.GTALK_SENT, countryID, message.source, message.messageDestinations[0].destination, 1, message.messageText);
                    break;
                }
                case FACEBOOK: {
                    this.objectManager.logMessage(MessageToLog.TypeEnum.FACEBOOK_SENT, countryID, message.source, message.messageDestinations[0].destination, 1, message.messageText);
                    break;
                }
                default: {
                    log.warn((Object)("Unable to log unknown sent IM message type " + message.type));
                    break;
                }
            }
        }
        catch (LocalException e) {
            log.warn((Object)("Unable to send an other IM message to the MessageLogger application. Exception: " + e.toString()));
        }
    }

    public void triggerReward(UserData userData, MessageType type, ThirdPartyInstantMessageTrigger.EventTypeEnum eventEnum) {
        try {
            ThirdPartyInstantMessageTrigger trigger = new ThirdPartyInstantMessageTrigger(userData, type, eventEnum);
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.warn((Object)("Unable to notify reward system. Exception: " + e.toString()));
        }
    }

    private static class ChatConnectionFactory {
        private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatExternal.class));

        private ChatConnectionFactory() {
        }

        public static ChatConnectionInterface create(ChatConnectionListenerInterface listener, ImType imType, Credential credential, Properties properties, boolean showOfflineContacts) throws FusionException {
            ChatConnectionInterface con;
            if (listener == null) {
                throw new IllegalArgumentException("Listener cannot be null");
            }
            if (credential == null) {
                throw new IllegalArgumentException("Credentials cannot be null");
            }
            switch (imType) {
                case MSN: {
                    con = ChatConnectionFactory.createMsn(listener, credential, showOfflineContacts, properties);
                    break;
                }
                case YAHOO: {
                    con = ChatConnectionFactory.createYahoo(listener, credential, showOfflineContacts, properties);
                    break;
                }
                case AIM: {
                    con = ChatConnectionFactory.createAIM(listener, credential, showOfflineContacts, properties);
                    break;
                }
                case GTALK: {
                    con = ChatConnectionFactory.createGTalk(listener, credential, showOfflineContacts, properties);
                    break;
                }
                case FACEBOOK: {
                    con = ChatConnectionFactory.createFacebook(listener, credential, showOfflineContacts, properties);
                    break;
                }
                default: {
                    String msg = "IM type " + imType + " is not supported";
                    log.warn((Object)("ChatConnectionFactory.Create() error: " + msg));
                    throw new FusionException(msg);
                }
            }
            return con;
        }

        private static void cleanup(ChatConnectionInterface con) {
            if (con == null) {
                return;
            }
            try {
                con.signOut();
            }
            catch (Exception ex) {
                log.error((Object)("Error occurred in clearing " + con.getImType().name() + " session."), (Throwable)ex);
            }
        }

        private static ChatConnectionInterface createFacebook(ChatConnectionListenerInterface listener, Credential credential, boolean showOfflineContacts, Properties p) throws FusionException {
            FacebookSession con = null;
            try {
                con = new FacebookSession(listener);
                if (!con.isConnected()) {
                    con.signIn(credential.username, credential.password);
                }
            }
            catch (Exception e) {
                ChatConnectionFactory.cleanup(con);
                con = null;
                throw new FusionException(e.getMessage());
            }
            return con;
        }

        private static ChatConnectionInterface createGTalk(ChatConnectionListenerInterface listener, Credential credential, boolean showOfflineContacts, Properties p) throws FusionException {
            GTalkSession con = null;
            try {
                con = new GTalkSession(listener, p.getProperty("GTalkDisplayPicture"));
                con.signIn(credential.username, credential.password);
            }
            catch (Exception e) {
                ChatConnectionFactory.cleanup(con);
                con = null;
                throw new FusionException(e.getMessage());
            }
            return con;
        }

        private static ChatConnectionInterface createAIM(ChatConnectionListenerInterface listener, Credential credential, boolean showOfflineContacts, Properties p) throws FusionException {
            AIMConnection con = null;
            try {
                con = new AIMConnection(listener, p.getProperty("AIMDisplayPicture"), p.getPropertyAsIntWithDefault("OtherIMConnectionTimeout", 5));
                con.signIn(credential.username, credential.password);
            }
            catch (Exception e) {
                ChatConnectionFactory.cleanup(con);
                con = null;
                throw new FusionException(e.getMessage());
            }
            return con;
        }

        private static ChatConnectionInterface createYahoo(ChatConnectionListenerInterface user, Credential credential, boolean showOfflineContacts, Properties p) throws FusionException {
            YahooConnection con = null;
            try {
                int maxConnections = p.getPropertyAsIntWithDefault("MaxConcurrentYahooConnections", Integer.MAX_VALUE);
                int connectionTimeout = p.getPropertyAsIntWithDefault("OtherIMConnectionTimeout", 5);
                con = new YahooConnection(user, maxConnections, connectionTimeout);
                con.signIn(credential.username, credential.password);
            }
            catch (Exception e) {
                ChatConnectionFactory.cleanup(con);
                con = null;
                throw new FusionException(e.getMessage());
            }
            return con;
        }

        private static ChatConnectionInterface createMsn(ChatConnectionListenerInterface listener, Credential credential, boolean showOfflineContacts, Properties p) throws FusionException {
            MSNConnection con = null;
            try {
                int connectionTimeout = p.getPropertyAsIntWithDefault("OtherIMConnectionTimeout", 5);
                con = new MSNConnection(listener, p.getProperty("MSNDisplayPicture"), connectionTimeout);
                con.signIn(credential.username, credential.password);
            }
            catch (Exception e) {
                ChatConnectionFactory.cleanup(con);
                con = null;
                throw new FusionException(e.getMessage());
            }
            return con;
        }
    }
}

