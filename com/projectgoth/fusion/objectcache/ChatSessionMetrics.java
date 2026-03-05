/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.slice.SessionMetricsIce;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatSessionMetrics {
    RequestCounter requestCounter;
    private final Set<String> uniqueUsersPrivateChattedWith = new HashSet<String>();
    private final Set<String> uniqueChatRoomsEntered;
    private AtomicInteger privateMessagesSent = new AtomicInteger(0);
    private AtomicInteger groupChatMessagesSent = new AtomicInteger(0);
    private AtomicInteger groupChatsEntered = new AtomicInteger(0);
    private AtomicInteger chatRoomMessagesSent = new AtomicInteger(0);
    private AtomicInteger chatRoomsEntered = new AtomicInteger(0);
    private AtomicInteger statusMessagesSet = new AtomicInteger(0);
    private AtomicInteger profileEdited = new AtomicInteger(0);
    private AtomicInteger photosUploaded = new AtomicInteger(0);
    private AtomicInteger themeUpdated = new AtomicInteger(0);
    private AtomicInteger inviteByPhoneNumber = new AtomicInteger(0);
    private AtomicInteger inviteByUsername = new AtomicInteger(0);
    private Set<String> passwordWarningsSent;

    ChatSessionMetrics(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
        this.passwordWarningsSent = Collections.synchronizedSet(new HashSet());
        this.uniqueChatRoomsEntered = new HashSet<String>();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasPrivateChattedWith(String username) {
        Set<String> set = this.uniqueUsersPrivateChattedWith;
        synchronized (set) {
            return this.uniqueUsersPrivateChattedWith.contains(username);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private short uniqueChatRoomsEntered() {
        Set<String> set = this.uniqueChatRoomsEntered;
        synchronized (set) {
            return (short)this.uniqueChatRoomsEntered.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private short uniqueUsersPrivateChattedWith() {
        Set<String> set = this.uniqueUsersPrivateChattedWith;
        synchronized (set) {
            return (short)this.uniqueUsersPrivateChattedWith.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void privateMessageSent(String username) {
        this.privateMessagesSent.incrementAndGet();
        Set<String> set = this.uniqueUsersPrivateChattedWith;
        synchronized (set) {
            this.uniqueUsersPrivateChattedWith.add(username);
        }
    }

    public SessionMetricsIce getSessionMetricsIce() {
        SessionMetricsIce sessionMetrics = new SessionMetricsIce();
        sessionMetrics.chatroomMessagesSent = this.chatRoomMessagesSent.shortValue();
        sessionMetrics.chatroomsEntered = this.chatRoomsEntered.shortValue();
        sessionMetrics.groupChatsEntered = this.groupChatsEntered.shortValue();
        sessionMetrics.groupMessagesSent = this.groupChatMessagesSent.shortValue();
        sessionMetrics.inviteByPhoneNumber = this.inviteByPhoneNumber.shortValue();
        sessionMetrics.inviteByUsername = this.inviteByUsername.shortValue();
        sessionMetrics.photosUploaded = this.photosUploaded.shortValue();
        sessionMetrics.privateMessagesSent = this.privateMessagesSent.shortValue();
        sessionMetrics.profileEdited = this.profileEdited.shortValue();
        sessionMetrics.statusMessagesSet = this.statusMessagesSet.shortValue();
        sessionMetrics.themeUpdated = this.themeUpdated.shortValue();
        sessionMetrics.uniqueChatroomsEntered = this.uniqueChatRoomsEntered();
        sessionMetrics.uniqueUsersPrivateChattedWith = this.uniqueUsersPrivateChattedWith();
        return sessionMetrics;
    }

    public void groupMessageSent() {
        this.groupChatMessagesSent.incrementAndGet();
    }

    public void profileEdited() {
        this.profileEdited.incrementAndGet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void chatRoomJoined(String name) {
        this.chatRoomsEntered.incrementAndGet();
        Set<String> set = this.uniqueChatRoomsEntered;
        synchronized (set) {
            this.uniqueChatRoomsEntered.add(name);
        }
    }

    public void request() {
        this.requestCounter.add();
    }

    public void themeUpdated() {
        this.themeUpdated.incrementAndGet();
    }

    public void photoUploaded() {
        this.photosUploaded.incrementAndGet();
    }

    public void inviteByPhoneNumber() {
        this.inviteByPhoneNumber.incrementAndGet();
    }

    public void inviteByUsername() {
        this.inviteByUsername.incrementAndGet();
    }

    public void groupChatsEntered(int increment) {
        this.groupChatsEntered.addAndGet(increment);
    }

    public void statusMessageSet() {
        this.statusMessagesSet.incrementAndGet();
    }

    public void passwordWarning(String destinationUsername) {
        this.passwordWarningsSent.add(destinationUsername);
    }

    public void chatRoomMessageSent() {
        this.chatRoomMessagesSent.incrementAndGet();
    }

    public void groupChatMessageSent() {
        this.groupChatMessagesSent.incrementAndGet();
    }

    public boolean hasPasswordWarning(String destinationUsername) {
        return this.passwordWarningsSent.contains(destinationUsername);
    }

    public Set<String> getUniqueUsersPrivateChattedWith() {
        return this.uniqueUsersPrivateChattedWith;
    }
}

