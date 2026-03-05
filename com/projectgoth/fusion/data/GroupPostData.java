/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class GroupPostData
implements Serializable {
    public Integer id;
    public Integer groupModuleID;
    public String teaser;
    public String body;
    public Date dateCreated;
    public String createdBy;
    public Date lastModifiedDate;
    public String lastModifiedBy;
    public StatusEnum status;

    public GroupPostData() {
    }

    public GroupPostData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("id");
        this.groupModuleID = (Integer)rs.getObject("groupModuleID");
        this.teaser = rs.getString("teaser");
        this.body = rs.getString("body");
        this.dateCreated = rs.getTimestamp("dateCreated");
        this.createdBy = rs.getString("createdBy");
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
        ACTIVE(1),
        PREVIEW(2);

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

