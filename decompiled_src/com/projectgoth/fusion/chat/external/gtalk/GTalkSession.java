/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.jivesoftware.smack.ChatManager
 *  org.jivesoftware.smack.ChatManagerListener
 *  org.jivesoftware.smack.ConnectionConfiguration
 *  org.jivesoftware.smack.ConnectionListener
 *  org.jivesoftware.smack.MessageListener
 *  org.jivesoftware.smack.Roster
 *  org.jivesoftware.smack.Roster$SubscriptionMode
 *  org.jivesoftware.smack.RosterEntry
 *  org.jivesoftware.smack.RosterListener
 *  org.jivesoftware.smack.SmackConfiguration
 *  org.jivesoftware.smack.XMPPConnection
 *  org.jivesoftware.smack.XMPPException
 *  org.jivesoftware.smack.packet.Packet
 *  org.jivesoftware.smack.packet.Presence
 *  org.jivesoftware.smackx.packet.VCard
 */
package com.projectgoth.fusion.chat.external.gtalk;

import com.projectgoth.fusion.chat.external.ChatConnectionInterface;
import com.projectgoth.fusion.chat.external.ChatConnectionListenerInterface;
import com.projectgoth.fusion.chat.external.gtalk.Avatar;
import com.projectgoth.fusion.chat.external.gtalk.GTalkException;
import com.projectgoth.fusion.chat.external.gtalk.GTalkListener;
import com.projectgoth.fusion.chat.external.gtalk.GTalkStatus;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GTalkSession
implements ChatConnectionInterface {
    private static final Logger log = Logger.getLogger(GTalkSession.class);
    private static String connectURL = "talk.google.com";
    private static int port = 5222;
    private ChatConnectionListenerInterface listener;
    private GTalkListener gtalkListener;
    private Roster roster;
    private ChatManager chatManager;
    private XMPPConnection xmppConnection;
    private String avatarLocation;
    private Avatar avatar;

    public GTalkSession(ChatConnectionListenerInterface listener, String avatarLocation) throws GTalkException {
        if (!SystemProperty.getBool("GTalkEnabled", true)) {
            throw new GTalkException("GTalk is disabled for a while...");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        this.listener = listener;
        this.avatarLocation = avatarLocation;
        this.gtalkListener = new GTalkListener(this, listener);
    }

    private void connectToServer() throws GTalkException {
        ConnectionConfiguration config = new ConnectionConfiguration(connectURL, port, "gmail.com");
        config.setSASLAuthenticationEnabled(true);
        config.setCompressionEnabled(true);
        config.setReconnectionAllowed(false);
        config.setRosterLoadedAtLogin(true);
        if (this.xmppConnection == null) {
            this.xmppConnection = new XMPPConnection(config);
        }
        try {
            this.xmppConnection.connect();
        }
        catch (Exception e) {
            log.error((Object)("Connecting to gtalk server failed: " + e.getMessage()));
            this.listener.onSignInFailed(this, e.getMessage());
            throw new GTalkException("Connection to google server failed. Please try again later.");
        }
    }

    @Override
    public void signIn(String username, String password) throws GTalkException {
        if (this.xmppConnection == null || !this.xmppConnection.isConnected()) {
            this.connectToServer();
        }
        if (!StringUtil.isBlank(username)) {
            if (username.indexOf("@") == -1) {
                username = username + "@gmail.com";
            }
        } else {
            this.xmppConnection.disconnect();
            throw new GTalkException("Username is empty");
        }
        if (!this.xmppConnection.isAuthenticated()) {
            try {
                this.xmppConnection.login(username, password, "mig33");
            }
            catch (Exception e) {
                log.warn((Object)("GTalk login failed for user[" + username + "]: " + e.getMessage()));
                this.listener.onSignInFailed(this, e.getMessage());
                throw new GTalkException("GTalk login failed. Please try again later.");
            }
        }
        this.listener.onSignInSuccess(this);
        try {
            this.roster = this.xmppConnection.getRoster();
            this.propagateContacts(this.roster);
            this.roster.addRosterListener((RosterListener)this.gtalkListener);
            this.roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            this.chatManager = this.xmppConnection.getChatManager();
            this.chatManager.addChatListener((ChatManagerListener)this.gtalkListener);
            this.xmppConnection.addConnectionListener((ConnectionListener)this.gtalkListener);
            this.setStatus(GTalkStatus.AVAILABLE, "I'm using migme (http://mig.me)...");
            this.setAvatar(this.avatarLocation);
        }
        catch (Exception e) {
            log.error((Object)e.getMessage());
        }
    }

    @Override
    public void signOut() {
        log.debug((Object)"Stopping gtalk session...");
        try {
            if (this.xmppConnection == null) {
                this.listener.onDisconnected(this, "Signed out off gtalk");
                return;
            }
            if (this.roster != null) {
                this.roster.removeRosterListener((RosterListener)this.gtalkListener);
            }
            if (this.chatManager != null) {
                this.chatManager.removeChatListener((ChatManagerListener)this.gtalkListener);
                this.chatManager = null;
            }
            this.xmppConnection.removeConnectionListener((ConnectionListener)this.gtalkListener);
            this.xmppConnection.disconnect();
            this.xmppConnection = null;
            this.listener.onDisconnected(this, "Signed out off gtalk");
        }
        catch (Exception e) {
            log.error((Object)("Failed to signout from gtalk: " + e.getMessage()));
        }
    }

    public void propagateContacts(Roster roster) {
        for (RosterEntry entry : roster.getEntries()) {
            String displayName = entry.getName();
            String userName = entry.getUser();
            if (StringUtil.isBlank(displayName)) {
                displayName = userName.indexOf("@") == -1 ? userName : userName.substring(0, userName.indexOf("@"));
            }
            this.listener.onContactDetail(this, userName, displayName);
        }
    }

    @Override
    public void sendMessage(String id, String messageContent) throws GTalkException {
        try {
            this.chatManager.createChat(id, (MessageListener)this.gtalkListener).sendMessage(messageContent);
        }
        catch (XMPPException e) {
            this.listener.onMessageFailed(this, null, id, messageContent, null);
            throw new GTalkException(e);
        }
        catch (Exception ise) {
            this.signOut();
            throw new GTalkException(ise);
        }
    }

    @Override
    public void addContact(String contact) throws GTalkException {
        if (this.roster != null) {
            RosterEntry toAdd = this.roster.getEntry(contact);
            if (toAdd != null) {
                throw new GTalkException(contact + " already exists in your friends list");
            }
            try {
                this.roster.createEntry(contact, contact, null);
            }
            catch (Exception e) {
                throw new GTalkException(e);
            }
        }
    }

    @Override
    public void removeContact(String contact) throws GTalkException {
        if (this.roster != null) {
            RosterEntry toRemove = this.roster.getEntry(contact);
            if (toRemove != null) {
                try {
                    this.roster.removeEntry(toRemove);
                }
                catch (Exception e) {
                    throw new GTalkException(e);
                }
            } else {
                throw new GTalkException(contact + " is not on your friends list");
            }
        }
    }

    @Override
    public boolean isConnected() {
        return this.xmppConnection != null && this.xmppConnection.isConnected();
    }

    @Override
    public boolean isSignedIn() {
        return this.xmppConnection != null && this.xmppConnection.isConnected() && this.xmppConnection.isAuthenticated();
    }

    @Override
    public String getUsername() {
        return this.isSignedIn() ? this.xmppConnection.getUser() : "";
    }

    public String getUserId() {
        return this.isSignedIn() ? this.xmppConnection.getUser() : "";
    }

    public void setStatus(GTalkStatus status, String personalMessage) throws GTalkException {
        if (this.xmppConnection == null || !this.xmppConnection.isConnected() || !this.xmppConnection.isAuthenticated()) {
            throw new GTalkException("User is not logged in");
        }
        Presence packet = status.toXMPPPresence();
        packet.setStatus(personalMessage);
        try {
            this.xmppConnection.sendPacket((Packet)packet);
        }
        catch (Exception e) {
            this.signOut();
            throw new GTalkException(e);
        }
    }

    @Override
    public void setAvatar(String avatarLocation) throws GTalkException {
        try {
            this.avatarLocation = avatarLocation;
            this.avatar = new Avatar(avatarLocation);
            if (this.isSignedIn()) {
                VCard vcard = new VCard();
                vcard.load(this.xmppConnection, this.xmppConnection.getUser());
                vcard.setAvatar(this.avatar.getBytes());
                vcard.save(this.xmppConnection);
            }
        }
        catch (XMPPException e) {
            throw new GTalkException(e.getMessage());
        }
        catch (NoSuchAlgorithmException e) {
            throw new GTalkException(e.getMessage());
        }
        catch (Exception e) {
            throw new GTalkException(e.getMessage());
        }
    }

    @Override
    public ImType getImType() {
        return ImType.GTALK;
    }

    @Override
    public void setStatus(PresenceType status, String message) throws Exception {
        this.setStatus(GTalkStatus.fromFusionPresence(status), message);
    }

    @Override
    public String inviteToConference(String conferenceID, String username) throws Exception {
        throw new UnsupportedOperationException("GTalk chat does not support conferences");
    }

    @Override
    public void leaveConference(String conferenceID) throws Exception {
        throw new UnsupportedOperationException("GTalk chat does not support conferences");
    }

    @Override
    public List<String> getConferenceParticipants(String conferenceID) throws Exception {
        throw new UnsupportedOperationException("GTalk chat does not support conferences");
    }

    static {
        SmackConfiguration.setKeepAliveInterval((int)-1);
        log.info((Object)("Connecting to GTalk using smack version " + SmackConfiguration.getVersion()));
    }
}

