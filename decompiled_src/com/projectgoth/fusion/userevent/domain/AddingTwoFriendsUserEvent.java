/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.AddingTwoFriendsUserEventIce;
import com.projectgoth.fusion.userevent.domain.AddingFriendUserEvent;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent
public class AddingTwoFriendsUserEvent
extends AddingFriendUserEvent {
    public static final String EVENT_NAME = "ADDING_TWO_FRIENDS";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AddingTwoFriendsUserEvent.class));
    private String friend2;

    public AddingTwoFriendsUserEvent() {
    }

    public AddingTwoFriendsUserEvent(AddingFriendUserEvent event, String friend2) {
        super(event, event.getFriend());
        this.friend2 = friend2;
    }

    public AddingTwoFriendsUserEvent(AddingTwoFriendsUserEventIce event) {
        super(event);
        this.friend2 = event.friend2;
    }

    public void setFriend2(String friend2) {
        this.friend2 = friend2;
    }

    public String getFriend2() {
        return this.friend2;
    }

    public static boolean areInvertedTheSame(AddingTwoFriendsUserEvent lhs, AddingFriendUserEvent rhs) {
        return lhs.getGeneratingUsername().equals(rhs.getFriend()) && lhs.friend2.equals(rhs.getGeneratingUsername());
    }

    public static boolean areInvertedTheSame(UserEvent lhs, UserEvent rhs) {
        return AddingTwoFriendsUserEvent.areInvertedTheSame((AddingFriendUserEvent)lhs, (AddingFriendUserEvent)rhs);
    }

    @Override
    public AddingTwoFriendsUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        AddingTwoFriendsUserEventIce iceEvent = new AddingTwoFriendsUserEventIce(this.getTimestamp(), this.getGeneratingUsername(), null, this.getText(), this.getFriend(), this.friend2);
        return iceEvent;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" friend2 [").append(this.friend2).append("]");
        return buffer.toString();
    }

    public static Map<String, String> findSubstitutionParameters(AddingTwoFriendsUserEventIce event) {
        Map<String, String> map = UserEvent.findSubstitutionParameters(event);
        map.put("friend1", event.friend1);
        map.put("friend2", event.friend2);
        return map;
    }
}

