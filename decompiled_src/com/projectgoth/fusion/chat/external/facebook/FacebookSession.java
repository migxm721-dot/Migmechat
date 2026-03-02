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
 *  org.jivesoftware.smack.SASLAuthentication
 *  org.jivesoftware.smack.SmackConfiguration
 *  org.jivesoftware.smack.XMPPConnection
 *  org.jivesoftware.smack.XMPPException
 *  org.jivesoftware.smack.packet.Packet
 *  org.jivesoftware.smack.packet.Presence
 */
package com.projectgoth.fusion.chat.external.facebook;

import com.projectgoth.fusion.chat.external.ChatConnectionInterface;
import com.projectgoth.fusion.chat.external.ChatConnectionListenerInterface;
import com.projectgoth.fusion.chat.external.facebook.FacebookConnectSASLMechanism;
import com.projectgoth.fusion.chat.external.facebook.FacebookException;
import com.projectgoth.fusion.chat.external.facebook.FacebookListener;
import com.projectgoth.fusion.chat.external.facebook.FacebookStatus;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FacebookSession
implements ChatConnectionInterface {
    private static final Logger log = Logger.getLogger(FacebookSession.class);
    private ChatConnectionListenerInterface listener;
    private FacebookListener fbListener;
    private Roster roster;
    private ChatManager chatManager;
    private static String connectURL = "chat.facebook.com";
    private static int port = 5222;
    private String username;
    private static String apiKey = SystemProperty.get("FacebookApiKey", "161865877194414");
    private XMPPConnection xmppConnection;
    private static AtomicInteger concurrentConnectionsCount = new AtomicInteger();

    public FacebookSession(ChatConnectionListenerInterface listener) throws FacebookException {
        if (!SystemProperty.getBool("FacebookEnabled", true)) {
            throw new FacebookException("Facebook is disabled for a while...");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        this.listener = listener;
        this.fbListener = new FacebookListener(this, listener);
    }

    private void connectToServer() throws FacebookException {
        ConnectionConfiguration config = new ConnectionConfiguration(connectURL, port, "chat.facebook.com");
        config.setSASLAuthenticationEnabled(true);
        config.setCompressionEnabled(true);
        config.setReconnectionAllowed(false);
        config.setRosterLoadedAtLogin(true);
        if (this.xmppConnection == null) {
            this.xmppConnection = new XMPPConnection(config);
        }
        try {
            if (!this.xmppConnection.isConnected()) {
                this.xmppConnection.connect();
            }
        }
        catch (Exception e) {
            log.error((Object)("Connecting to chat.facebook.com failed: " + e.getMessage()), (Throwable)e);
            this.listener.onSignInFailed(this, e.getMessage());
            throw new FacebookException("Connection to facebook server failed. Please try again later.");
        }
    }

    @Override
    public void signIn(String login, String accessToken) throws FacebookException {
        this.username = login;
        int maxConcurrentConnections = SystemProperty.getInt(SystemPropertyEntities.IMSettings.MAX_CONCURRENT_CONNECTIONS.forIM(ImType.FACEBOOK));
        if (concurrentConnectionsCount.incrementAndGet() > maxConcurrentConnections) {
            concurrentConnectionsCount.decrementAndGet();
            log.info((Object)("Maximum number of concurrent sessions exceeded. [" + concurrentConnectionsCount.get() + "] max: " + maxConcurrentConnections));
            throw new FacebookException("Unable to connect to facebook at the moment. Please try to connect at a later time.");
        }
        try {
            String userId;
            if (!(this.xmppConnection != null && this.xmppConnection.isConnected())) {
                this.connectToServer();
            }
            if (!this.xmppConnection.isAuthenticated()) {
                this.xmppConnection.login(apiKey, accessToken);
            }
            if ((userId = this.getUserId()) == null || userId == "" || userId.startsWith("-0@")) {
                throw new Exception("invalid access token");
            }
            this.listener.onSignInSuccess(this);
            this.roster = this.xmppConnection.getRoster();
            this.roster.addRosterListener((RosterListener)this.fbListener);
            this.roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
            this.propagateContacts(this.roster);
            this.chatManager = this.xmppConnection.getChatManager();
            this.chatManager.addChatListener((ChatManagerListener)this.fbListener);
            this.xmppConnection.addConnectionListener((ConnectionListener)this.fbListener);
            this.setStatus(FacebookStatus.AVAILABLE, SystemProperty.get(SystemPropertyEntities.IMSettings.DEFAULT_ONLINE_MESSAGE));
        }
        catch (Exception e) {
            log.warn((Object)("Facebook login failed for user:" + this.username + " error: "), (Throwable)e);
            try {
                this.listener.onSignInFailed(this, e.getMessage());
                this.stopSession();
            }
            catch (Exception ex) {
                log.error((Object)"Error occurred in cleaning up session fo facebook after failed login.", (Throwable)ex);
            }
            throw new FacebookException("Facebook login failed. Please try re-authenticating your account with facebook again.");
        }
    }

    public void propagateContacts(Roster roster) {
        for (RosterEntry entry : roster.getEntries()) {
            this.listener.onContactDetail(this, entry.getUser(), entry.getName());
            Presence presence = roster.getPresence(entry.getUser());
            if (!presence.isAvailable() && !presence.isAway()) continue;
            this.listener.onContactStatusChanged(this, entry.getUser(), FacebookStatus.AVAILABLE.toFusionPresence());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void stopSession() {
        log.debug((Object)"Stopping facebook session...");
        try {
            try {
                if (this.xmppConnection == null) {
                    this.listener.onDisconnected(this, "Signed out off facebook");
                    Object var3_1 = null;
                    this.xmppConnection = null;
                    concurrentConnectionsCount.decrementAndGet();
                    return;
                }
                if (this.roster != null) {
                    this.roster.removeRosterListener((RosterListener)this.fbListener);
                }
                if (this.chatManager != null) {
                    this.chatManager.removeChatListener((ChatManagerListener)this.fbListener);
                    this.chatManager = null;
                }
                this.xmppConnection.removeConnectionListener((ConnectionListener)this.fbListener);
                this.xmppConnection.disconnect();
                this.listener.onDisconnected(this, "Signed out off facebook");
            }
            catch (Exception e) {
                log.warn((Object)"Exception occurred in signing off from Facebook.", (Throwable)e);
                Object var3_3 = null;
                this.xmppConnection = null;
                concurrentConnectionsCount.decrementAndGet();
                return;
            }
        }
        catch (Throwable throwable) {
            Object var3_4 = null;
            this.xmppConnection = null;
            concurrentConnectionsCount.decrementAndGet();
            throw throwable;
        }
        Object var3_2 = null;
        this.xmppConnection = null;
        concurrentConnectionsCount.decrementAndGet();
    }

    @Override
    public void sendMessage(String userId, String messageContent) throws FacebookException {
        try {
            this.chatManager.createChat(userId, (MessageListener)this.fbListener).sendMessage(messageContent);
        }
        catch (XMPPException e) {
            log.error((Object)("Unable to send facebook message from:" + this.getUserId() + " to:" + userId + " error: "), (Throwable)e);
            this.listener.onMessageFailed(this, null, userId, messageContent, null);
            throw new FacebookException(e);
        }
        catch (Exception ise) {
            log.error((Object)("Unable to send facebook message from:" + this.getUserId() + " to:" + userId + " error: "), (Throwable)ise);
            this.stopSession();
            throw new FacebookException(ise);
        }
    }

    @Override
    public void addContact(String contact) throws FacebookException {
        if (this.roster == null) {
            return;
        }
        this.roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        RosterEntry toAdd = this.roster.getEntry(contact);
        if (toAdd != null) {
            throw new FacebookException(contact + " already exists in your friends list");
        }
        try {
            this.roster.createEntry(contact, contact, null);
        }
        catch (XMPPException e) {
            log.error((Object)"Unable to add facebook contact", (Throwable)e);
            throw new FacebookException(e);
        }
        catch (Exception ise) {
            log.error((Object)"Unable to add facebook contact", (Throwable)ise);
            this.stopSession();
            throw new FacebookException(ise);
        }
    }

    @Override
    public void removeContact(String contact) throws FacebookException {
        if (this.roster == null) {
            return;
        }
        RosterEntry toRemove = this.roster.getEntry(contact);
        if (toRemove == null) {
            throw new FacebookException(contact + " is not on your friends list");
        }
        try {
            this.roster.removeEntry(toRemove);
        }
        catch (XMPPException e) {
            log.error((Object)"Unable to remove facebook contact", (Throwable)e);
            throw new FacebookException(e);
        }
        catch (Exception ise) {
            log.error((Object)"Unable to remove facebook contact", (Throwable)ise);
            this.stopSession();
            throw new FacebookException(ise);
        }
    }

    @Override
    public boolean isConnected() {
        return this.xmppConnection != null && this.xmppConnection.isConnected();
    }

    @Override
    public boolean isSignedIn() {
        return this.xmppConnection != null && this.xmppConnection.isConnected() && this.xmppConnection.isAuthenticated() && !this.xmppConnection.getUser().startsWith("-0@");
    }

    @Override
    public String getUsername() {
        return this.isSignedIn() ? this.xmppConnection.getUser() : "";
    }

    public String getUserId() {
        return this.isSignedIn() ? this.xmppConnection.getUser() : "";
    }

    public void setStatus(FacebookStatus status, String personalMessage) throws FacebookException {
        if (!(this.xmppConnection != null && this.xmppConnection.isConnected() && this.xmppConnection.isAuthenticated())) {
            throw new FacebookException("User is not logged in");
        }
        Presence packet = status.toXMPPPresence();
        packet.setStatus(personalMessage);
        try {
            this.xmppConnection.sendPacket((Packet)packet);
        }
        catch (Exception e) {
            log.error((Object)"Unable to set facebook status", (Throwable)e);
            this.stopSession();
            throw new FacebookException(e);
        }
    }

    @Override
    public ImType getImType() {
        return ImType.FACEBOOK;
    }

    @Override
    public void signOut() {
        this.stopSession();
    }

    @Override
    public void setAvatar(String fileLocation) throws Exception {
        throw new UnsupportedOperationException("Facebook chat does not support changing profile pictures");
    }

    @Override
    public void setStatus(PresenceType status, String message) throws Exception {
        this.setStatus(FacebookStatus.fromFusionPresence(status), message);
    }

    @Override
    public String inviteToConference(String conferenceID, String username) throws Exception {
        throw new UnsupportedOperationException("Facebook chat does not support conferences");
    }

    @Override
    public void leaveConference(String conferenceID) throws Exception {
        throw new UnsupportedOperationException("Facebook chat does not support conferences");
    }

    @Override
    public List<String> getConferenceParticipants(String conferenceID) throws Exception {
        throw new UnsupportedOperationException("Facebook chat does not support conferences");
    }

    static {
        SmackConfiguration.setPacketReplyTimeout((int)5000);
        SASLAuthentication.registerSASLMechanism((String)"X-FACEBOOK-PLATFORM", FacebookConnectSASLMechanism.class);
        SASLAuthentication.supportSASLMechanism((String)"X-FACEBOOK-PLATFORM", (int)0);
        SmackConfiguration.setKeepAliveInterval((int)-1);
        log.info((Object)("Connecting to Facebook using smack version " + SmackConfiguration.getVersion()));
    }
}

