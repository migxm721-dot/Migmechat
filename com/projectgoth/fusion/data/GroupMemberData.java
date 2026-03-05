/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class GroupMemberData
implements Serializable {
    public Integer id;
    public String username;
    public Integer groupID;
    public Integer locationID;
    public Date dateCreated;
    public TypeEnum type;
    public Date dateLeft;
    public Boolean smsNotification;
    public Boolean emailNotification;
    public Boolean eventNotification;
    public Boolean smsGroupEventNotification;
    public Boolean emailThreadUpdateNotification;
    public Boolean eventThreadUpdateNotification;
    public StatusEnum status;
    public Boolean vip;
    public String displayPicture;

    public GroupMemberData() {
    }

    public GroupMemberData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("id");
        this.username = rs.getString("username");
        this.groupID = (Integer)rs.getObject("groupID");
        this.locationID = (Integer)rs.getObject("locationID");
        this.dateCreated = rs.getTimestamp("dateCreated");
        this.dateLeft = rs.getTimestamp("dateLeft");
        Integer intVal = (Integer)rs.getObject("type");
        if (intVal != null) {
            this.type = TypeEnum.fromValue(intVal);
        }
        if ((intVal = (Integer)rs.getObject("smsNotification")) != null) {
            this.smsNotification = intVal != 0;
        }
        if ((intVal = (Integer)rs.getObject("emailNotification")) != null) {
            this.emailNotification = intVal != 0;
        }
        if ((intVal = (Integer)rs.getObject("eventNotification")) != null) {
            this.eventNotification = intVal != 0;
        }
        if ((intVal = (Integer)rs.getObject("smsGroupEventNotification")) != null) {
            this.smsGroupEventNotification = intVal != 0;
        }
        if ((intVal = (Integer)rs.getObject("emailThreadUpdateNotification")) != null) {
            this.emailThreadUpdateNotification = intVal != 0;
        }
        if ((intVal = (Integer)rs.getObject("eventThreadUpdateNotification")) != null) {
            this.eventThreadUpdateNotification = intVal != 0;
        }
        if ((intVal = (Integer)rs.getObject("status")) != null) {
            this.status = StatusEnum.fromValue(intVal);
        }
        this.vip = (intVal = (Integer)rs.getObject("vipsubscriptionid")) != null && intVal > 0;
    }

    public boolean isModerator() {
        return this.type == TypeEnum.MODERATOR;
    }

    public boolean isAdministrator() {
        return this.type == TypeEnum.ADMINISTRATOR;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        INACTIVE(0),
        ACTIVE(1),
        BANNED(2);

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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum {
        REGULAR(1),
        ADMINISTRATOR(2),
        MODERATOR(3);

        private int value;

        private TypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static TypeEnum fromValue(int value) {
            for (TypeEnum e : TypeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

