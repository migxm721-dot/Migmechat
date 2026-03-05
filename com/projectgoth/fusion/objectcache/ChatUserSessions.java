/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.TimeoutException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import Ice.TimeoutException;
import com.projectgoth.fusion.chat.external.ChatConnectionInterface;
import com.projectgoth.fusion.chatsync.MessageStatusEvent;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.ContactGroupData;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.objectcache.ChatObjectManagerUser;
import com.projectgoth.fusion.objectcache.ChatSession;
import com.projectgoth.fusion.objectcache.ChatSessionState;
import com.projectgoth.fusion.objectcache.ChatUser;
import com.projectgoth.fusion.slice.CurrencyDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.UserEventIce;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatUserSessions {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatUserSessions.class));
    private static AtomicInteger totalIntermediateFails = new AtomicInteger(0);
    private static AtomicInteger totalFinalFails = new AtomicInteger(0);
    private long sessionIdleTimeout;
    private final ConcurrentMap<String, ChatSession> sessions = new ConcurrentHashMap<String, ChatSession>();
    private final ChatUser chatUser;
    private final ScheduledExecutorService retryService = Executors.newScheduledThreadPool(SystemProperty.getInt(SystemPropertyEntities.ChatUserSessionsSettings.RETRY_EXECUTOR_CORE_SIZE));

    public ChatUserSessions(ChatObjectManagerUser objectManager, ChatUser chatUser) {
        this.chatUser = chatUser;
        this.sessionIdleTimeout = objectManager.getProperties().getPropertyAsIntWithDefault("SessionIdleTimeout", 3600) * 1000;
    }

    private boolean immediatePurgingEnabled() {
        return false == SystemProperty.getBool(SystemPropertyEntities.ChatUserSessionsSettings.IMMEDIATE_PURGING_DISABLED);
    }

    private boolean retriesEnabled() {
        return SystemProperty.getBool(SystemPropertyEntities.ChatUserSessionsSettings.RETRIES_ENABLED);
    }

    public boolean removeSession(String sessionID, boolean userIsBeingPurged) {
        ChatSession session = (ChatSession)this.sessions.remove(sessionID);
        if (session == null) {
            return false;
        }
        this.chatUser.onSessionRemoved(session);
        return true;
    }

    public void removeSessions(ArrayList<String> sessionsToPurge, boolean userIsBeingPurged) {
        if (sessionsToPurge == null) {
            return;
        }
        for (String sessionID : sessionsToPurge) {
            this.removeSession(sessionID, userIsBeingPurged);
        }
    }

    public int getSessionCount() {
        return this.sessions.size();
    }

    public ChatSession getSession(String sessionID) {
        for (ChatSession session : this.sessions.values()) {
            if (!session.getSessionID().equals(sessionID)) continue;
            return session;
        }
        return null;
    }

    public void onSignInSuccess(ChatConnectionInterface source) {
        for (ChatSession session : this.sessions.values()) {
            try {
                session.otherIMLoggedIn(source.getImType().value());
            }
            catch (Exception e) {}
        }
    }

    public void onSignInFailed(ChatConnectionInterface source, String reason) {
        for (ChatSession session : this.sessions.values()) {
            try {
                session.otherIMLoggedOut(source.getImType().value(), reason);
            }
            catch (Exception e) {}
        }
    }

    private void logOnSessionException(int iLevel, ChatSession session, String message, Exception e) {
        String err = message + " session=" + (session != null ? session.getSessionID() : session) + " e=" + e;
        if (iLevel == 40000) {
            log.error((Object)err, (Throwable)e);
        } else {
            log.warn((Object)err, (Throwable)e);
        }
    }

    public void contactAdded(final ContactData contact, final int contactListVersion, final boolean guaranteedIsNew) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, true){

                public void onCall() throws Exception {
                    session.contactAdded(contact, contactListVersion, guaranteedIsNew);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in contactAdded, contactData=" + contact, e);
                }
            }.call();
        }
    }

    public void contactChangedPresence(final Integer contactID, final int imType, final PresenceType presence) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, true){

                public void onCall() throws Exception {
                    session.contactChangedPresence(contactID, imType, presence.value());
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in contactChangedPresence, contactID=" + contactID + " imType=" + imType + "presence=" + presence + " purgeOnException=" + ChatUserSessions.this.immediatePurgingEnabled(), e);
                }
            }.call();
        }
    }

    public void contactChangedDisplayPicture(final int contactId, final String displayPicture, final long timeStamp) throws FusionException {
        Object sessionsToPurge = null;
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, true){

                public void onCall() throws Exception {
                    session.contactChangedDisplayPicture(contactId, displayPicture, timeStamp);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in contactChangedDisplayPicture, contactId=" + contactId + " displayPicture=" + displayPicture + " timeStamp=" + timeStamp, e);
                }
            }.call();
        }
    }

    public void contactChangedStatusMessage(final Integer contactId, final String statusMessage, final long timeStamp) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, true){

                public void onCall() throws Exception {
                    session.contactChangedStatusMessage(contactId, statusMessage, timeStamp);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in contactChangedStatusMessage, contactId=" + contactId + " statusMessage=" + statusMessage + " timeStamp=" + timeStamp, e);
                }
            }.call();
        }
    }

    public void otherIMConferenceCreated(final ImType imType, final String conferenceID, final String creator) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.otherIMConferenceCreated(imType, conferenceID, creator);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in otherIMConferenceCreated, imType=" + imType + " conferenceID=" + conferenceID + " creator=" + creator, e);
                }
            }.call();
        }
    }

    public void notifyUserJoinedGroupChat(String groupChatId, String username, boolean isMuted) {
        for (ChatSession session : this.sessions.values()) {
            try {
                session.notifyUserJoinedGroupChat(groupChatId, username, isMuted);
            }
            catch (Exception e) {
                log.info((Object)("notifyUserJoinedGroupChat error: " + e.getMessage()));
            }
        }
    }

    public void notifyUserLeftGroupChat(String groupChatId, String username) {
        for (ChatSession session : this.sessions.values()) {
            try {
                session.notifyUserLeftGroupChat(groupChatId, username);
            }
            catch (Exception e) {
                log.info((Object)("notifyUserLeftGroupChat error: " + e.getMessage()));
            }
        }
    }

    public void notifySessionsOfNewContact(final ContactData contact, final int contactListVersion, final boolean guaranteedIsNew) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, true){

                public void onCall() throws Exception {
                    session.contactAdded(contact, contactListVersion, guaranteedIsNew);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in notifySessionsOfNewContact, contact=" + contact + " contactListVersion=" + contactListVersion + " guaranteedIsNew=" + guaranteedIsNew, e);
                }
            }.call();
        }
    }

    public void notifySessionsOfRemovedContact(final int contactID, final int contactListVersion) {
        Object sessionsToPurge = null;
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, true){

                public void onCall() throws Exception {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("sending contact removed packet to session [" + session + "]"));
                    }
                    session.contactRemoved(contactID, contactListVersion);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in notifySessionsOfRemovedContact, contactID=" + contactID + " contactListVersion=" + contactListVersion, e);
                }
            }.call();
        }
    }

    public void sendGroupChatParticipantArrays(String conferenceID, byte imType, String[] participants, String[] mutedParticipants) {
        for (ChatSession session : this.sessions.values()) {
            try {
                session.sendGroupChatParticipantArrays(conferenceID, imType, participants, mutedParticipants);
            }
            catch (Exception e) {
                log.info((Object)("sendGroupChatParticipants error: " + e.getMessage()));
            }
        }
    }

    public void sendGroupChatParticipants(String conferenceID, byte imType, String participants, String mutedParticipants) {
        for (ChatSession session : this.sessions.values()) {
            try {
                session.sendGroupChatParticipants(conferenceID, imType, participants, mutedParticipants);
            }
            catch (Exception e) {
                log.info((Object)("sendGroupChatParticipants error: " + e.getMessage()));
            }
        }
    }

    public void putMessage(final MessageDataIce message) throws FusionException {
        final AtomicBoolean messageForwardedOK = new AtomicBoolean(false);
        if (this.sessions.size() == 0) {
            throw new FusionException("The user is no longer online");
        }
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, true){

                public void onCall() throws Exception {
                    session.putMessageLocal(message);
                    messageForwardedOK.set(true);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in putMessage, message=" + message + " purgeOnException=" + ChatUserSessions.this.immediatePurgingEnabled(), e);
                }
            }.call();
        }
        if (!messageForwardedOK.get()) {
            throw new FusionException("The message could not be sent");
        }
    }

    public void emoticonsChanged(final String[] emoticonHotKeys, final String[] emoticonAlternateKeys) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.emoticonsChanged(emoticonHotKeys, emoticonAlternateKeys);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in emoticonsChanged, emoticonHotKeys=" + emoticonHotKeys + " emoticonAlternateKeys=" + emoticonAlternateKeys, e);
                }
            }.call();
        }
    }

    public boolean hasPrivateChattedWith(String username) {
        for (ChatSession session : this.sessions.values()) {
            if (!session.hasPrivateChattedWith(username)) continue;
            return true;
        }
        return false;
    }

    public boolean dispose() {
        String[] sessionsToRemove = null;
        if (this.sessions.size() > 0) {
            sessionsToRemove = this.sessions.keySet().toArray(new String[this.sessions.size()]);
        }
        if (sessionsToRemove != null) {
            for (String sessionID : sessionsToRemove) {
                this.removeSession(sessionID, true);
            }
            if (sessionsToRemove.length > 0) {
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void purgeExpiredSessions() {
        ArrayList<String> sessionsToPurge = new ArrayList<String>();
        ChatUserSessions chatUserSessions = this;
        synchronized (chatUserSessions) {
            long minimumLastTouchedTime = System.currentTimeMillis() - this.sessionIdleTimeout;
            for (ChatSession session : this.sessions.values()) {
                if (session.getTimeLastTouched() >= minimumLastTouchedTime) continue;
                sessionsToPurge.add(session.getSessionID());
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("Session expired - user:" + session.getUsername() + " sessionId:" + session.getSessionID()));
            }
        }
        for (String sessionID : sessionsToPurge) {
            this.removeSession(sessionID, false);
        }
    }

    public PresenceType getPresence(PresenceType newPresence) {
        for (ChatSession session : this.sessions.values()) {
            if (session.getPresence().value() >= newPresence.value()) continue;
            newPresence = session.getPresence();
        }
        return newPresence;
    }

    public void otherIMLoggedOut(ImType imType, String reason) {
        for (ChatSession session : this.sessions.values()) {
            try {
                session.otherIMLoggedOut(imType.value(), reason);
            }
            catch (Exception e) {
                log.warn((Object)("Unable to notify user:" + this.chatUser.getUsername() + " session:" + session.getSessionID() + " of IM disconnect from:" + imType.name()), (Throwable)e);
            }
        }
    }

    public void contactRequestAccepted(final ContactData newContact, final int inviteeContactListVersion, final int outstandingPendingContacts) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.contactRequestAccepted(newContact, inviteeContactListVersion, outstandingPendingContacts);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in contactRequestAccepted, newContact=" + newContact + " inviteeContactListVersion=" + inviteeContactListVersion, e);
                }
            }.call();
        }
    }

    public void contactRequestRejected(final String inviterUsername, final int outstandingPendingContacts) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.contactRequestRejected(inviterUsername, outstandingPendingContacts);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in contactRequestRejected, inviterUsername=" + inviterUsername + " outstandingPendingContacts=" + outstandingPendingContacts, e);
                }
            }.call();
        }
    }

    public void avatarChanged(final String displayPicture, final String statusMessage) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.avatarChanged(displayPicture, statusMessage);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in avatarChanged, displayPicture=" + displayPicture + " statusMessage=" + statusMessage, e);
                }
            }.call();
        }
    }

    public List<ChatSession> disconnect(String reason, String username) {
        ArrayList<ChatSession> sessionsToDisconnect = new ArrayList<ChatSession>(this.sessions.size());
        for (ChatSession session : this.sessions.values()) {
            sessionsToDisconnect.add(session);
        }
        for (ChatSession session : sessionsToDisconnect) {
            try {
                session.disconnect(reason);
            }
            catch (FusionException e) {
                log.error((Object)("Failed to disconnect session [" + session.getSessionID() + "] for user [" + username + "]"), (Throwable)((Object)e));
            }
        }
        return sessionsToDisconnect;
    }

    public boolean isEmpty() {
        return this.sessions.isEmpty();
    }

    public void putFileReceived(final MessageDataIce messageIce) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.putFileReceived(messageIce);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in putFileReceived, messageIce=" + messageIce, e);
                }
            }.call();
        }
    }

    public Set<String> contactRequest(Set<String> userList) {
        LinkedList sessionsToNotify = new LinkedList();
        HashSet<String> contactRequestSentList = new HashSet<String>();
        sessionsToNotify.addAll(this.sessions.values());
        for (ChatSession sessionToNotify : sessionsToNotify) {
            for (String username : userList) {
                try {
                    sessionToNotify.contactRequest(username, userList.size());
                    contactRequestSentList.add(username);
                }
                catch (Exception e) {
                    int level = SystemProperty.getInt(SystemPropertyEntities.ChatUserSessionsSettings.FINAL_FAILURE_LOG_LEVEL);
                    if (level == Integer.MAX_VALUE) continue;
                    this.logOnSessionException(level, sessionToNotify, "Exception in contactRequest, userList=" + userList, e);
                }
            }
        }
        return contactRequestSentList;
    }

    public void pushNotification(final Message message) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.pushNotification(message);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in pushNotification, message=" + (Object)((Object)message), e);
                }
            }.call();
        }
    }

    public boolean putAnonymousCallNotifiaction(String requestingUsername, String requestingMobilePhone) {
        boolean found = false;
        for (ChatSession session : this.sessions.values()) {
            try {
                session.putAnonymousCallNotification(requestingUsername, requestingMobilePhone);
                found = true;
            }
            catch (Exception e) {
                int level = SystemProperty.getInt(SystemPropertyEntities.ChatUserSessionsSettings.FINAL_FAILURE_LOG_LEVEL);
                if (level == Integer.MAX_VALUE) continue;
                this.logOnSessionException(level, session, "Exception in putAnonymousCallNotifiaction, requestingUsername=" + requestingUsername + "requestingMobilePhone=" + requestingMobilePhone, e);
            }
        }
        return found;
    }

    public boolean supportsBinaryMessage() {
        for (ChatSession session : this.sessions.values()) {
            ClientType device = session.getDeviceType();
            if (device != ClientType.AJAX1 && device != ClientType.AJAX2 && device != ClientType.MIDP2 && device != ClientType.WINDOWS_MOBILE && device != ClientType.ANDROID && device != ClientType.BLACKBERRY && device != ClientType.MRE && device != ClientType.IOS) continue;
            return true;
        }
        return false;
    }

    public void putWebCallNotification(String source, String destination, int gateway, String gatewayName, int protocol) throws FusionException {
        FusionException fe = new FusionException();
        fe.message = this.sessions.size() == 0 ? destination + " is currently offline" : destination + " does not have VOIP capability";
        throw fe;
    }

    public void putEvent(final UserEventIce event) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.putEvent(event);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in putEvent, event=" + (Object)((Object)event), e);
                }
            }.call();
        }
    }

    public void putServerQuestion(final String message, final String url) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.putServerQuestion(message, url);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in putServerQuestion, message=" + message + " url=" + url, e);
                }
            }.call();
        }
    }

    public void putAlertMessage(final String message, final String title, final short timeout) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.putAlertMessage(message, title, timeout);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in putAlertMessage, message=" + message + " title=" + title + " timeout=" + timeout, e);
                }
            }.call();
        }
    }

    public void putPrivateChatNowAGroupChat(final String groupChatID, final String creator) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.privateChatNowAGroupChat(groupChatID, creator);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in putPrivateChatNowAGroupChat, groupChatID=" + groupChatID + " creator=" + creator, e);
                }
            }.call();
        }
    }

    public void accountBalanceChanged(final double balance, final double fundedBalance, final CurrencyDataIce currency) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.accountBalanceChanged(balance, fundedBalance, currency);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in accountBalanceChanged, balance=" + balance + " fundedBalancee=" + fundedBalance + " currency=" + currency, e);
                }
            }.call();
        }
    }

    public void notifySessionsOfNewContactGroup(final ContactGroupData contactGroup, final int contactListVersion) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, true){

                public void onCall() throws Exception {
                    session.contactGroupAdded(contactGroup, contactListVersion);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in notifySessionsOfNewContactGroup, contactGroup=" + contactGroup + " contactListVersion=" + contactListVersion, e);
                }
            }.call();
        }
    }

    public void notifySessionsOfRemovedContactGroup(final int contactGroupID, final int contactListVersion) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, true){

                public void onCall() throws Exception {
                    session.contactGroupRemoved(contactGroupID, contactListVersion);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in notifySessionsOfRemovedContactGroup, contactGroupID=" + contactGroupID + " contactListVersion=" + contactListVersion, e);
                }
            }.call();
        }
    }

    public String[] getSessions() {
        ArrayList<String> sessionProxies = new ArrayList<String>(this.sessions.size());
        for (ChatSession session : this.sessions.values()) {
            sessionProxies.add(session.getSessionID());
        }
        return sessionProxies.toArray(new String[this.sessions.size()]);
    }

    public void addSession(String sessionID, ChatSession session) {
        this.sessions.put(sessionID, session);
    }

    public int verifyCanCreateSession(String sessionID) throws FusionException {
        if (this.sessions.containsKey(sessionID)) {
            throw new FusionException("A session with the ID '" + sessionID + "' already exists");
        }
        return this.sessions.size();
    }

    public void emailNotification(final int unreadEmailCount) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.emailNotification(unreadEmailCount);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in emailNotification, unreadEmailCount=" + unreadEmailCount, e);
                }
            }.call();
        }
    }

    public void putSerializedPacket(byte[] packet) throws FusionException {
        for (ChatSession session : this.sessions.values()) {
            session.putSerializedPacket(packet);
        }
    }

    public void themeChanged(final String themeLocation) {
        for (final ChatSession session : this.sessions.values()) {
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.themeChanged(themeLocation);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in themeChanged, themeLocation=" + themeLocation, e);
                }
            }.call();
        }
    }

    public void putMessageStatusEvent(final MessageStatusEvent mse) {
        for (final ChatSession session : this.sessions.values()) {
            if (!session.isMessageStatusEventCapable()) continue;
            new ChatUserSessionsCallable(session, false){

                public void onCall() throws Exception {
                    session.putMessageStatusEvent(mse);
                }

                public void logException(Exception e, int level) {
                    ChatUserSessions.this.logOnSessionException(level, session, "Exception in putMessageStatusEvent, event=" + mse + " e=" + e, e);
                }
            }.call();
        }
    }

    public ChatSessionState[] getState() {
        ArrayList<ChatSessionState> sessionProxies = new ArrayList<ChatSessionState>(this.sessions.size());
        for (ChatSession session : this.sessions.values()) {
            sessionProxies.add(session.getState());
        }
        return sessionProxies.toArray(new ChatSessionState[this.sessions.size()]);
    }

    public static int getTotalIntermediateFails() {
        return totalIntermediateFails.get();
    }

    public static int getTotalFinalFails() {
        return totalFinalFails.get();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public abstract class ChatUserSessionsCallable
    implements Callable<Boolean> {
        private final ChatSession session;
        private final boolean purgeOnFail;
        private int tries = 0;

        protected abstract void onCall() throws Exception;

        protected abstract void logException(Exception var1, int var2);

        public ChatUserSessionsCallable(ChatSession session, boolean purgeOnFail) {
            this.session = session;
            this.purgeOnFail = purgeOnFail;
        }

        public Boolean call_prePt73110676() {
            try {
                this.onCall();
                return true;
            }
            catch (Exception e) {
                if (ChatUserSessions.this.retriesEnabled() && ++this.tries < SystemProperty.getInt(SystemPropertyEntities.ChatUserSessionsSettings.TRIES_LIMIT)) {
                    int interval = SystemProperty.getInt(SystemPropertyEntities.ChatUserSessionsSettings.RETRY_INTERVAL_MILLIS) * this.tries;
                    ChatUserSessions.this.retryService.schedule(this, (long)interval, TimeUnit.MILLISECONDS);
                } else if (this.purgeOnFail && ChatUserSessions.this.immediatePurgingEnabled()) {
                    ArrayList<String> sessionsToPurge = new ArrayList<String>();
                    sessionsToPurge.add(this.session.getSessionID());
                    ChatUserSessions.this.removeSessions(sessionsToPurge, false);
                }
                int level = SystemProperty.getInt(SystemPropertyEntities.ChatUserSessionsSettings.FINAL_FAILURE_LOG_LEVEL);
                if (level != Integer.MAX_VALUE) {
                    this.logException(e, level);
                }
                return false;
            }
        }

        @Override
        public Boolean call() {
            if (!SystemProperty.getBool(SystemPropertyEntities.ChatUserSessionsSettings.PT73110676_CHANGES_ENABLED)) {
                return this.call_prePt73110676();
            }
            try {
                this.onCall();
                return true;
            }
            catch (TimeoutException timeouts) {
                int triesLimit = SystemProperty.getInt(SystemPropertyEntities.ChatUserSessionsSettings.TRIES_LIMIT);
                if (ChatUserSessions.this.retriesEnabled() && ++this.tries < triesLimit) {
                    int interval = SystemProperty.getInt(SystemPropertyEntities.ChatUserSessionsSettings.RETRY_INTERVAL_MILLIS) * this.tries;
                    ChatUserSessions.this.retryService.schedule(this, (long)interval, TimeUnit.MILLISECONDS);
                } else if (this.purgeOnFail && ChatUserSessions.this.immediatePurgingEnabled()) {
                    ArrayList<String> sessionsToPurge = new ArrayList<String>();
                    sessionsToPurge.add(this.session.getSessionID());
                    ChatUserSessions.this.removeSessions(sessionsToPurge, false);
                }
                if (ChatUserSessions.this.retriesEnabled() && this.tries < triesLimit) {
                    totalIntermediateFails.incrementAndGet();
                    this.logException((Exception)((Object)timeouts), 30000);
                } else {
                    totalFinalFails.incrementAndGet();
                    int level = SystemProperty.getInt(SystemPropertyEntities.ChatUserSessionsSettings.FINAL_FAILURE_LOG_LEVEL);
                    if (level != Integer.MAX_VALUE) {
                        this.logException((Exception)((Object)timeouts), level);
                    }
                }
                return false;
            }
            catch (Exception everythingElse) {
                this.logException(everythingElse, 40000);
                return false;
            }
        }
    }
}

