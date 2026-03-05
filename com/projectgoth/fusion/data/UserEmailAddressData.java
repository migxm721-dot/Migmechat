/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class UserEmailAddressData
implements Serializable {
    private static final long serialVersionUID = -4388724723576487201L;
    public int userId;
    public String emailAddress;
    public UserEmailAddressTypeEnum type;
    public Date verifiedDate;
    public boolean verified;

    public UserEmailAddressData(ResultSet rs) throws SQLException {
        this.userId = rs.getInt("userid");
        this.emailAddress = rs.getString("emailaddress");
        this.type = UserEmailAddressTypeEnum.fromValue(rs.getInt("type"));
        this.verifiedDate = rs.getTimestamp("dateverified");
        this.verified = rs.getBoolean("verified");
    }

    public boolean isVerified() {
        return this.verified;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum UserEmailAddressTypeEnum {
        PRIMARY(1, "Primary"),
        SECONDARY(2, "Secondary");

        public int value;
        public String label;

        private UserEmailAddressTypeEnum(int code, String label) {
            this.value = code;
            this.label = label;
        }

        public static UserEmailAddressTypeEnum fromValue(int value) {
            for (UserEmailAddressTypeEnum e : UserEmailAddressTypeEnum.values()) {
                if (e.value != value) continue;
                return e;
            }
            return null;
        }
    }
}

