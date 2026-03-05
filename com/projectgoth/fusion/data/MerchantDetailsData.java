/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.data.MessageData;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MerchantDetailsData
implements Serializable {
    public static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    public Integer id;
    public String mentor;
    public String referrer;
    public UserNameColorTypeEnum usernameColorType;
    public String userName;
    public TypeEnum type;
    public String displayPicture;
    public String dateRegistered;
    public String lastTransfer;
    public String mobilePhone;

    public MerchantDetailsData() {
    }

    public MerchantDetailsData(ResultSet rs, boolean isFullDataSet) throws SQLException {
        Integer intVal;
        this.id = rs.getInt("id");
        this.mentor = rs.getString("mentor");
        this.referrer = rs.getString("referrer");
        if (this.mentor == null) {
            this.mentor = "";
        }
        if (this.referrer == null) {
            this.referrer = "";
        }
        UserNameColorTypeEnum userNameColorTypeEnum = this.usernameColorType = (intVal = Integer.valueOf(rs.getInt("username_color_type"))) != null ? UserNameColorTypeEnum.fromValue(intVal) : null;
        if (this.usernameColorType == null) {
            this.usernameColorType = UserNameColorTypeEnum.DEFAULT;
        }
        if (isFullDataSet) {
            this.userName = rs.getString("username");
            this.dateRegistered = DateTimeUtils.getStringForMigcore(rs.getDate("dateregistered"));
            this.displayPicture = rs.getString("displaypicture");
            this.mobilePhone = rs.getString("mobilephone");
            intVal = rs.getInt("type");
            this.type = TypeEnum.fromValue(intVal);
            try {
                this.lastTransfer = DateTimeUtils.getStringForMigcore(rs.getDate("lasttransfer"));
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    public boolean isMerchantMentor() {
        return this.mentor != null && this.mentor.equals("mentor");
    }

    public int getChatColorHex() {
        return this.usernameColorType.hex();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum UserNameColorTypeEnum {
        DEFAULT(0, MessageData.SourceTypeEnum.TOP_MERCHANT_LVL1.colorHex()),
        RED(1, MessageData.SourceTypeEnum.TOP_MERCHANT_LVL3.colorHex()),
        PINK(2, MessageData.SourceTypeEnum.TOP_MERCHANT_LVL2.colorHex());

        private int value;
        private int color;

        private UserNameColorTypeEnum(int value, int color) {
            this.value = value;
            this.color = color;
        }

        public int value() {
            return this.value;
        }

        public int hex() {
            return this.color;
        }

        public String toString() {
            return String.valueOf(this.value);
        }

        public static UserNameColorTypeEnum fromValue(int value) {
            for (UserNameColorTypeEnum e : UserNameColorTypeEnum.values()) {
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
        LEAD(1),
        TOP_MERCHANT(2);

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

