/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.UserWallPostUserEventIce;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent(version=1)
public class UserWallPostUserEvent
extends UserEvent {
    public static final String EVENT_NAME = "USER_WALL_POST";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserWallPostUserEvent.class));
    private String wallOwnerUsername;
    private String postPrefix;
    private int userWallPostId;

    public UserWallPostUserEvent() {
    }

    public UserWallPostUserEvent(UserEvent event, String wallOwnerUsername, String postPrefix, int userWallPostId) {
        super(event);
        this.wallOwnerUsername = wallOwnerUsername;
        this.postPrefix = postPrefix;
        this.userWallPostId = userWallPostId;
    }

    public UserWallPostUserEvent(UserWallPostUserEventIce event) {
        super(event);
        this.wallOwnerUsername = event.wallOwnerUsername;
        this.postPrefix = event.postPrefix;
        this.userWallPostId = event.userWallPostId;
    }

    public String getWallOwnerUsername() {
        return this.wallOwnerUsername;
    }

    public String getPostPrefix() {
        return this.postPrefix;
    }

    public int getUserWallPostId() {
        return this.userWallPostId;
    }

    @Override
    public UserWallPostUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        UserWallPostUserEventIce iceEvent = new UserWallPostUserEventIce(this.getTimestamp(), this.getGeneratingUsername(), null, this.getText(), this.wallOwnerUsername, this.postPrefix, this.userWallPostId);
        return iceEvent;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" wallOwnerUsername [").append(this.wallOwnerUsername).append("]");
        buffer.append(" postPrefix [").append(this.postPrefix).append("]");
        buffer.append(" userWallPostId [").append(this.userWallPostId).append("]");
        return buffer.toString();
    }

    public static Map<String, String> findSubstitutionParameters(UserWallPostUserEventIce event) {
        Map<String, String> map = UserEvent.findSubstitutionParameters(event);
        map.put("wallOwnerUsername", event.wallOwnerUsername);
        map.put("postPrefix", event.postPrefix);
        map.put("userWallPostId", Integer.toString(event.userWallPostId));
        return map;
    }
}

