/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.GroupAnnouncementUserEventIce;
import com.projectgoth.fusion.userevent.domain.GroupUserEvent;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent
public class GroupAnnouncementUserEvent
extends GroupUserEvent {
    public static final String EVENT_NAME = "GROUP_ANNOUNCEMENT";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GroupAnnouncementUserEvent.class));
    private int groupAnnouncementId;
    private String groupAnnouncementTitle;

    public GroupAnnouncementUserEvent() {
    }

    public GroupAnnouncementUserEvent(GroupAnnouncementUserEventIce event) {
        super(event);
        this.groupAnnouncementId = event.groupAnnouncementId;
        this.groupAnnouncementTitle = event.groupAnnouncementTitle;
    }

    @Override
    public GroupAnnouncementUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        GroupAnnouncementUserEventIce iceEvent = new GroupAnnouncementUserEventIce(this.getTimestamp(), this.getGeneratingUsername(), null, this.getText(), this.getGroupId(), this.getGroupName(), this.groupAnnouncementId, this.groupAnnouncementTitle);
        return iceEvent;
    }

    public static Map<String, String> findSubstitutionParameters(GroupAnnouncementUserEventIce event) {
        Map<String, String> map = GroupUserEvent.findSubstitutionParameters(event);
        map.put("groupAnnouncementId", Integer.toString(event.groupAnnouncementId));
        map.put("groupAnnouncementTitle", event.groupAnnouncementTitle);
        return map;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" groupAnnouncementId [").append(this.groupAnnouncementId).append("]");
        buffer.append(" groupAnnouncementTitle [").append(this.groupAnnouncementTitle).append("]");
        return buffer.toString();
    }
}

