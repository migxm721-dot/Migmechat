/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.GroupUserEventIce;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent
public class GroupUserEvent
extends UserEvent {
    public static final String EVENT_NAME = "GROUP";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GroupUserEvent.class));
    private int groupId;
    private String groupName;

    public GroupUserEvent() {
    }

    public GroupUserEvent(UserEvent event, int groupId, String groupName) {
        super(event);
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public GroupUserEvent(GroupUserEventIce event) {
        super(event);
        this.groupId = event.groupId;
        this.groupName = event.groupName;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public String getGroupName() {
        return this.groupName;
    }

    @Override
    public GroupUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        GroupUserEventIce iceEvent = new GroupUserEventIce(this.getTimestamp(), this.getGeneratingUsername(), null, this.getText(), this.groupId, this.groupName);
        return iceEvent;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" groupId [").append(this.groupId).append("]");
        buffer.append(" groupName [").append(this.groupName).append("]");
        return buffer.toString();
    }

    public static Map<String, String> findSubstitutionParameters(GroupUserEventIce event) {
        Map<String, String> map = UserEvent.findSubstitutionParameters(event);
        map.put("groupId", Integer.toString(event.groupId));
        map.put("groupName", event.groupName);
        return map;
    }
}

