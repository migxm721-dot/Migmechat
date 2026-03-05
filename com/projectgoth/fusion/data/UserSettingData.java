/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSettingData
implements Serializable {
    private static final long serialVersionUID = 1L;
    public String username;
    public TypeEnum type;
    public Integer value;

    public UserSettingData() {
    }

    public UserSettingData(ResultSet rs) throws SQLException {
        this.username = rs.getString("username");
        this.value = (Integer)rs.getObject("value");
        Integer intVal = (Integer)rs.getObject("type");
        if (intVal != null) {
            this.type = TypeEnum.fromValue(intVal);
        }
    }

    public static boolean isValidEmailTypeEnum(TypeEnum type) {
        switch (type) {
            case EMAIL_ALL: 
            case EMAIL_MENTION: 
            case EMAIL_REPLY_TO_POST: 
            case EMAIL_RECEIVE_GIFT: 
            case EMAIL_NEW_FOLLOWER: {
                return true;
            }
        }
        return false;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MessageEnum {
        DISABLED(0),
        EVERYONE(1),
        FRIENDS_ONLY(2);

        private int value;

        private MessageEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static MessageEnum defaultValue() {
            return FRIENDS_ONLY;
        }

        public static MessageEnum fromValue(int value) {
            for (MessageEnum e : MessageEnum.values()) {
                if (value != e.value()) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum AnonymousCallEnum {
        DISABLED(0),
        ENABLED(1);

        private int value;

        private AnonymousCallEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static AnonymousCallEnum defaultValue() {
            return ENABLED;
        }

        public static AnonymousCallEnum fromValue(int value) {
            for (AnonymousCallEnum e : AnonymousCallEnum.values()) {
                if (value != e.value()) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EmailSettingEnum {
        DISABLED(0),
        ENABLED(1),
        PEOPLE_IM_FAN_OF(2),
        MY_FRIENDS(3);

        private int value;

        private EmailSettingEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static EmailSettingEnum defaultValue() {
            return ENABLED;
        }

        public static EmailSettingEnum fromValue(int value) {
            for (EmailSettingEnum e : EmailSettingEnum.values()) {
                if (value != e.value()) continue;
                return e;
            }
            return EmailSettingEnum.defaultValue();
        }

        public static EmailSettingEnum fromName(String name) {
            for (EmailSettingEnum e : EmailSettingEnum.values()) {
                if (!e.name().equalsIgnoreCase(name)) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum {
        ANONYMOUS_CALL(1),
        MESSAGE(2),
        SECURITY_QUESTION(3),
        EMAIL_MENTION(4),
        EMAIL_REPLY_TO_POST(5),
        EMAIL_RECEIVE_GIFT(6),
        EMAIL_NEW_FOLLOWER(7),
        EMAIL_ALL(8);

        private int value;

        private TypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static TypeEnum fromValue(int value) {
            for (TypeEnum e : TypeEnum.values()) {
                if (value != e.value()) continue;
                return e;
            }
            return null;
        }

        public static TypeEnum fromName(String name) {
            for (TypeEnum e : TypeEnum.values()) {
                if (!e.name().equalsIgnoreCase(name)) continue;
                return e;
            }
            return null;
        }
    }
}

