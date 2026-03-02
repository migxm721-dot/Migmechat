/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class GroupModuleData
implements Serializable {
    public Integer id;
    public Integer groupID;
    public String title;
    public Date dateCreated;
    public String createdBy;
    public Date lastModifiedDate;
    public String lastModifiedBy;
    public Integer position;
    public TypeEnum type;
    public StatusEnum status;

    public GroupModuleData() {
    }

    public GroupModuleData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("id");
        this.groupID = (Integer)rs.getObject("groupID");
        this.title = rs.getString("title");
        this.dateCreated = rs.getTimestamp("dateCreated");
        this.createdBy = rs.getString("createdBy");
        this.lastModifiedDate = rs.getTimestamp("lastModifiedDate");
        this.lastModifiedBy = rs.getString("lastModifiedBy");
        this.position = (Integer)rs.getObject("position");
        Integer intVal = (Integer)rs.getObject("type");
        if (intVal != null) {
            this.type = TypeEnum.fromValue(intVal);
        }
        if ((intVal = (Integer)rs.getObject("status")) != null) {
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum {
        POSTS(1),
        EVENTS(2);

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

