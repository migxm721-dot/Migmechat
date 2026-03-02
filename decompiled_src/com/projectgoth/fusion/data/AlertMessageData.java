/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.fdl.enums.AlertContentType;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AlertMessageData
implements Serializable {
    public Integer id;
    public Integer countryID;
    public Date dateCreated;
    public Date startDate;
    public Date expiryDate;
    public TypeEnum type;
    public Boolean onceOnly;
    public Double weighting;
    public Integer minMidletVersion;
    public Integer maxMidletVersion;
    public AlertContentType contentType;
    public String content;
    public String url;
    public StatusEnum status;
    public CategoryTypeEnum categoryType;

    public AlertMessageData() {
    }

    public AlertMessageData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("id");
        this.countryID = (Integer)rs.getObject("countryID");
        this.dateCreated = rs.getTimestamp("dateCreated");
        this.startDate = rs.getTimestamp("startDate");
        this.expiryDate = rs.getTimestamp("expiryDate");
        this.onceOnly = rs.getInt("onceOnly") == 1;
        this.weighting = (Double)rs.getObject("weighting");
        this.minMidletVersion = (Integer)rs.getObject("minMidletVersion");
        this.maxMidletVersion = (Integer)rs.getObject("maxMidletVersion");
        this.content = rs.getString("content");
        this.url = rs.getString("url");
        Integer intVal = (Integer)rs.getObject("type");
        if (intVal != null) {
            this.type = TypeEnum.fromValue(intVal);
        }
        if ((intVal = (Integer)rs.getObject("contentType")) != null) {
            this.contentType = AlertContentType.fromValue(intVal);
        }
        if ((intVal = (Integer)rs.getObject("status")) != null) {
            this.status = StatusEnum.fromValue(intVal);
        }
        intVal = rs.getInt("category");
        if (!rs.wasNull()) {
            this.categoryType = CategoryTypeEnum.fromValue(intVal);
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
        LOGIN(1),
        CHAT_ROOM(2),
        PRELOGIN(3),
        CHAT_ROOM_WELCOME_MESSAGE(4);

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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum CategoryTypeEnum {
        CAMPAIGNS(1),
        PRODUCT(2),
        MERCHANT(3),
        SUPPORT(4),
        OTHERS(5);

        private int value;

        private CategoryTypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static CategoryTypeEnum fromValue(int value) {
            for (CategoryTypeEnum e : CategoryTypeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

