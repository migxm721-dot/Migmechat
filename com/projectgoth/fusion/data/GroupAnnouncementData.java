/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class GroupAnnouncementData
implements Serializable {
    public Integer id;
    public Integer groupID;
    public Date dateCreated;
    public String createdBy;
    public String title;
    public String text;
    public String smsText;
    public Date lastModifiedDate;
    public String lastModifiedBy;
    public StatusEnum status;
    public String picture;

    public GroupAnnouncementData() {
    }

    public GroupAnnouncementData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("id");
        this.groupID = (Integer)rs.getObject("groupID");
        this.dateCreated = rs.getTimestamp("dateCreated");
        this.createdBy = rs.getString("createdBy");
        this.title = rs.getString("title");
        this.text = rs.getString("text");
        this.smsText = rs.getString("smsText");
        this.lastModifiedDate = rs.getTimestamp("lastModifiedDate");
        this.lastModifiedBy = rs.getString("lastModifiedBy");
        Integer intVal = (Integer)rs.getObject("status");
        if (intVal != null) {
            this.status = StatusEnum.fromValue(intVal);
        }
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

