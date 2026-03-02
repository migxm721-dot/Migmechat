/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserCategory {
    public int id;
    public String name;
    public UserCategoryTypeEnum type;

    public UserCategory(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.type = UserCategoryTypeEnum.fromValue(rs.getInt("type"));
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum UserCategoryTypeEnum {
        PUBLIC(1),
        AD_GROUPS(2);

        private int value;

        private UserCategoryTypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static UserCategoryTypeEnum fromValue(int value) {
            for (UserCategoryTypeEnum e : UserCategoryTypeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

