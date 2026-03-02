/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.slice.GroupEvent;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupEventData
implements Serializable {
    public Integer id;
    public Integer groupID;
    public String description;
    public Date startTime;
    public Integer durationMinutes;
    public String chatRoomName;
    public Integer chatRoomCategoryID;
    public Date dateCreated;
    public boolean alertSent;
    public StatusEnum status;
    public String remainingTime;
    public String chatRoomCategoryName;

    public static GroupEventData fromResultSet(ResultSet rs) throws SQLException {
        Date now;
        GroupEventData groupEventData = new GroupEventData();
        groupEventData.id = (Integer)rs.getObject("id");
        groupEventData.groupID = (Integer)rs.getObject("groupID");
        groupEventData.description = rs.getString("description");
        groupEventData.startTime = rs.getTimestamp("startTime");
        groupEventData.chatRoomName = rs.getString("chatRoomName");
        groupEventData.chatRoomCategoryID = rs.getInt("chatRoomCategoryID");
        groupEventData.durationMinutes = rs.getInt("DurationMinutes");
        if (groupEventData.durationMinutes == 0) {
            groupEventData.durationMinutes = 60;
        }
        groupEventData.dateCreated = rs.getTimestamp("dateCreated");
        groupEventData.alertSent = rs.getBoolean("alertSent");
        Integer intVal = (Integer)rs.getObject("status");
        if (intVal != null) {
            groupEventData.status = StatusEnum.fromValue(intVal);
        }
        if (groupEventData.startTime.after(now = new Date())) {
            String timeLeft = DateTimeUtils.getRemainingTime(groupEventData.startTime);
            groupEventData.remainingTime = timeLeft == null || timeLeft.length() == 0 ? "Starts in less than a minute!" : "Starts in " + timeLeft;
        } else if (DateTimeUtils.plusMinutes(groupEventData.startTime, groupEventData.durationMinutes).after(now)) {
            groupEventData.remainingTime = "ON NOW!";
        } else {
            SimpleDateFormat df = new SimpleDateFormat("d MMM HH:mm");
            groupEventData.remainingTime = "Started " + df.format(groupEventData.startTime) + " GMT";
        }
        return groupEventData;
    }

    public static GroupEventData fromResultSetWithChatRoomCategoryName(ResultSet rs) throws SQLException {
        GroupEventData groupEventData = GroupEventData.fromResultSet(rs);
        groupEventData.chatRoomCategoryName = rs.getString("chatroomcategoryname");
        return groupEventData;
    }

    public static GroupEventData fromGroupEvent(GroupEvent groupEvent) {
        GroupEventData groupEventData = new GroupEventData();
        groupEventData.chatRoomCategoryID = groupEvent.chatRoomCategoryID;
        groupEventData.chatRoomName = groupEvent.chatRoomName;
        groupEventData.dateCreated = new Date(groupEvent.dateCreated);
        groupEventData.description = groupEvent.description;
        groupEventData.durationMinutes = groupEvent.duration;
        groupEventData.groupID = groupEvent.groupId;
        groupEventData.id = groupEvent.id;
        groupEventData.startTime = new Date(groupEvent.startTime);
        groupEventData.status = StatusEnum.fromValue(groupEvent.status);
        return groupEventData;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        INACTIVE(0),
        ACTIVE(1);

        private int value;

        private StatusEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static StatusEnum fromValue(int value) {
            for (StatusEnum e : StatusEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

