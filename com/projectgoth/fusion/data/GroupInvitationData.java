/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class GroupInvitationData
implements Serializable {
    public Integer id;
    public String username;
    public Integer groupID;
    public Date dateCreated;
    public String inviter;
    public StatusEnum status;

    public GroupInvitationData() {
    }

    public GroupInvitationData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("id");
        this.username = rs.getString("username");
        this.groupID = (Integer)rs.getObject("groupID");
        this.dateCreated = rs.getTimestamp("dateCreated");
        this.inviter = rs.getString("inviter");
        Integer intVal = (Integer)rs.getObject("status");
        if (intVal != null) {
            this.status = StatusEnum.fromValue(intVal);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        PENDING(0),
        ACCEPTED(1),
        DECLINED(2);

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

