/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Entity
 *  com.sleepycat.persist.model.PrimaryKey
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.store;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.userevent.domain.AddingFriendUserEvent;
import com.projectgoth.fusion.userevent.domain.AddingMultipleFriendsUserEvent;
import com.projectgoth.fusion.userevent.domain.AddingTwoFriendsUserEvent;
import com.projectgoth.fusion.userevent.domain.EventPrivacySetting;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Entity(version=1)
public class StoreUserEvent {
    private static final transient Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(StoreUserEvent.class));
    @PrimaryKey
    private String username;
    private List<UserEvent> events;
    private EventPrivacySetting receivingMask;

    public StoreUserEvent() {
    }

    public StoreUserEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<UserEvent> getEvents() {
        return this.events;
    }

    public void setEvents(List<UserEvent> events) {
        this.events = events;
    }

    public EventPrivacySetting getReceivingMask() {
        if (this.receivingMask == null) {
            return new EventPrivacySetting();
        }
        return this.receivingMask;
    }

    public void setReceivingMask(EventPrivacySetting receivingMask) {
        this.receivingMask = receivingMask;
    }

    private void trimMaximumEvents(int eventsPerUser) {
        if (this.events.size() >= eventsPerUser) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("discarding oldest [" + this.events.get(this.events.size() - 1).getTimestamp() + " user event for user [" + this.username + "]"));
            }
            this.events.remove(this.events.size() - 1);
        }
    }

    private void trimOlderEvents() {
        long cutoff = System.currentTimeMillis() - 1209600000L;
        while (!this.events.isEmpty() && this.events.get(this.events.size() - 1).getTimestamp() < cutoff) {
            this.events.remove(this.events.get(this.events.size() - 1));
        }
    }

    private boolean aggregateAddedFriendEvents(UserEvent newEvent) {
        if (newEvent != null && newEvent instanceof AddingFriendUserEvent) {
            UserEvent eventToMerge = null;
            Iterator<UserEvent> ite = this.events.iterator();
            while (ite.hasNext()) {
                UserEvent currentEvent = ite.next();
                if (!(currentEvent instanceof AddingFriendUserEvent) || !currentEvent.getGeneratingUsername().equals(newEvent.getGeneratingUsername())) continue;
                if (AddingFriendUserEvent.areTheSame(currentEvent, newEvent)) {
                    ite.remove();
                    return false;
                }
                eventToMerge = currentEvent;
                ite.remove();
                break;
            }
            if (eventToMerge == null) {
                log.debug((Object)"no previous addingFriend* events found for this user");
                return false;
            }
            AddingTwoFriendsUserEvent eventToAdd = null;
            if (eventToMerge instanceof AddingMultipleFriendsUserEvent) {
                ((AddingMultipleFriendsUserEvent)eventToMerge).setAdditionalFriends(((AddingMultipleFriendsUserEvent)eventToMerge).getAdditionalFriends() + 1);
                eventToAdd = eventToMerge;
            } else if (eventToMerge instanceof AddingTwoFriendsUserEvent) {
                eventToAdd = new AddingMultipleFriendsUserEvent((AddingTwoFriendsUserEvent)eventToMerge, 1);
            } else if (eventToMerge instanceof AddingFriendUserEvent) {
                eventToAdd = new AddingTwoFriendsUserEvent((AddingFriendUserEvent)eventToMerge, ((AddingFriendUserEvent)newEvent).getFriend());
            }
            if (eventToAdd != null) {
                eventToAdd.setTimestamp(newEvent.getTimestamp());
                if (log.isDebugEnabled()) {
                    log.debug((Object)("newly merged event to add [" + eventToAdd + "]"));
                }
                this.addEventToEvents(eventToAdd);
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean hasInvertedDuplicate(UserEvent newEvent) {
        if (newEvent != null && newEvent instanceof AddingFriendUserEvent) {
            for (UserEvent event : this.events) {
                if (event instanceof AddingFriendUserEvent && AddingFriendUserEvent.areInvertedTheSame(event, newEvent)) {
                    return true;
                }
                if (!(event instanceof AddingTwoFriendsUserEvent) || !AddingTwoFriendsUserEvent.areInvertedTheSame(event, newEvent)) continue;
                return true;
            }
        }
        return false;
    }

    private void addEventToEvents(UserEvent userEvent) {
        if (this.events.isEmpty()) {
            this.events.add(userEvent);
        } else {
            int index = 0;
            for (UserEvent currentEvent : this.events) {
                if (userEvent.getTimestamp() >= currentEvent.getTimestamp()) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("adding event [" + userEvent + "] at index " + index));
                    }
                    this.events.add(index, userEvent);
                    break;
                }
                ++index;
            }
        }
    }

    public void addEvent(int eventsPerUser, UserEvent userEvent) {
        if (this.events == null) {
            this.events = new ArrayList<UserEvent>(eventsPerUser);
        }
        if (!this.getReceivingMask().applyMask(userEvent)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("not adding event [" + userEvent + "] since receivingMask [" + this.receivingMask + "] returned false"));
            }
            return;
        }
        if (this.events.isEmpty()) {
            log.debug((Object)("adding first event [" + userEvent + "] for user [" + this.username + "]"));
            this.events.add(userEvent);
            return;
        }
        if (this.hasInvertedDuplicate(userEvent)) {
            return;
        }
        this.trimMaximumEvents(eventsPerUser);
        this.trimOlderEvents();
        if (this.events.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("adding first event [" + userEvent + "] for user [" + this.username + "]"));
            }
            this.events.add(userEvent);
            return;
        }
        boolean addedEvent = this.aggregateAddedFriendEvents(userEvent);
        if (!addedEvent) {
            this.addEventToEvents(userEvent);
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("username [").append(this.username).append("] with [").append(this.events != null ? this.events.size() : 0).append("] events");
        buffer.append("\tmask [").append(this.receivingMask).append("]");
        return buffer.toString();
    }
}

