/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.AddingFriendUserEventIce;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent
public class AddingFriendUserEvent
extends UserEvent {
    public static final String EVENT_NAME = "ADDING_FRIEND";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AddingFriendUserEvent.class));
    private String friend;

    public AddingFriendUserEvent() {
    }

    public AddingFriendUserEvent(UserEvent event, String friend) {
        super(event);
        this.friend = friend;
    }

    public AddingFriendUserEvent(AddingFriendUserEventIce event) {
        super(event);
        this.friend = event.friend1;
    }

    public String getFriend() {
        return this.friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public static boolean areTheSame(AddingFriendUserEvent lhs, AddingFriendUserEvent rhs) {
        return lhs.getGeneratingUsername().equals(rhs.getGeneratingUsername()) && lhs.friend.equals(rhs.friend);
    }

    public static boolean areTheSame(UserEvent lhs, UserEvent rhs) {
        return AddingFriendUserEvent.areTheSame((AddingFriendUserEvent)lhs, (AddingFriendUserEvent)rhs);
    }

    public static boolean areInvertedTheSame(AddingFriendUserEvent lhs, AddingFriendUserEvent rhs) {
        return lhs.getGeneratingUsername().equals(rhs.friend) && lhs.friend.equals(rhs.getGeneratingUsername());
    }

    public static boolean areInvertedTheSame(UserEvent lhs, UserEvent rhs) {
        return AddingFriendUserEvent.areInvertedTheSame((AddingFriendUserEvent)lhs, (AddingFriendUserEvent)rhs);
    }

    @Override
    public AddingFriendUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        AddingFriendUserEventIce iceEvent = new AddingFriendUserEventIce(this.getTimestamp(), this.getGeneratingUsername(), null, this.getText(), this.friend);
        return iceEvent;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" friend [").append(this.friend).append("]");
        return buffer.toString();
    }

    public static Map<String, String> findSubstitutionParameters(AddingFriendUserEventIce event) {
        Map<String, String> map = UserEvent.findSubstitutionParameters(event);
        map.put("friend1", event.friend1);
        return map;
    }
}

