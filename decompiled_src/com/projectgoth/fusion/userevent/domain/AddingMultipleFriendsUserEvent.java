/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.AddingMultipleFriendsUserEventIce;
import com.projectgoth.fusion.userevent.domain.AddingTwoFriendsUserEvent;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent
public class AddingMultipleFriendsUserEvent
extends AddingTwoFriendsUserEvent {
    public static final String EVENT_NAME = "ADDING_MULTIPLE_FRIENDS";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AddingMultipleFriendsUserEvent.class));
    private int additionalFriends;

    public AddingMultipleFriendsUserEvent() {
    }

    public AddingMultipleFriendsUserEvent(AddingTwoFriendsUserEvent event, int additionalFriends) {
        super(event, event.getFriend2());
        this.additionalFriends = additionalFriends;
    }

    public AddingMultipleFriendsUserEvent(AddingMultipleFriendsUserEventIce event) {
        super(event);
        this.additionalFriends = event.additionalFriends;
    }

    public int getAdditionalFriends() {
        return this.additionalFriends;
    }

    public void setAdditionalFriends(int additionalFriends) {
        this.additionalFriends = additionalFriends;
    }

    @Override
    public AddingMultipleFriendsUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        AddingMultipleFriendsUserEventIce iceEvent = new AddingMultipleFriendsUserEventIce(this.getTimestamp(), this.getGeneratingUsername(), null, this.getText(), this.getFriend(), this.getFriend2(), this.additionalFriends);
        return iceEvent;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" additionalFriends [").append(this.additionalFriends).append("]");
        return buffer.toString();
    }

    public static Map<String, String> findSubstitutionParameters(AddingMultipleFriendsUserEventIce event) {
        Map<String, String> map = UserEvent.findSubstitutionParameters(event);
        map.put("friend1", event.friend1);
        map.put("friend2", event.friend2);
        map.put("additionalFriends", Integer.toString(event.additionalFriends));
        return map;
    }
}

