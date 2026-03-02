/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.slice.SystemSMSDataIce;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SystemSMSData
implements Serializable {
    public Integer id;
    public String username;
    public Date dateCreated;
    public TypeEnum type;
    public SubTypeEnum subType;
    public String source;
    public String destination;
    public Integer IDDCode;
    public String messageText;
    public Integer gateway;
    public Date dateDispatched;
    public String providerTransactionID;
    public StatusEnum status;
    public double cost = 0.0;
    public String registrationIP;

    public SystemSMSData() {
    }

    public SystemSMSData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("ID");
        this.username = rs.getString("Username");
        this.dateCreated = rs.getDate("DateCreated");
        this.source = rs.getString("Source");
        this.destination = rs.getString("Destination");
        this.IDDCode = (Integer)rs.getObject("IDDCode");
        this.messageText = rs.getString("MessageText");
        this.gateway = (Integer)rs.getObject("Gateway");
        this.dateDispatched = rs.getDate("DateDispatched");
        Integer intVal = (Integer)rs.getObject("Type");
        if (intVal != null) {
            this.type = TypeEnum.fromValue(intVal);
        }
        if ((intVal = (Integer)rs.getObject("SubType")) != null) {
            this.subType = SubTypeEnum.fromValue(intVal);
        }
        if ((intVal = (Integer)rs.getObject("Status")) != null) {
            this.status = StatusEnum.fromValue(intVal);
        }
    }

    public SystemSMSData(SystemSMSDataIce systemSMSIce) {
        this.id = systemSMSIce.id == Integer.MIN_VALUE ? null : Integer.valueOf(systemSMSIce.id);
        this.username = systemSMSIce.username.equals("\u0000") ? null : systemSMSIce.username;
        this.dateCreated = systemSMSIce.dateCreated == Long.MIN_VALUE ? null : new Date(systemSMSIce.dateCreated);
        this.type = systemSMSIce.type == Integer.MIN_VALUE ? null : TypeEnum.fromValue(systemSMSIce.type);
        this.subType = systemSMSIce.subType == Integer.MIN_VALUE ? null : SubTypeEnum.fromValue(systemSMSIce.subType);
        this.source = systemSMSIce.source.equals("\u0000") ? null : systemSMSIce.source;
        this.destination = systemSMSIce.destination.equals("\u0000") ? null : systemSMSIce.destination;
        this.IDDCode = systemSMSIce.IDDCode == Integer.MIN_VALUE ? null : Integer.valueOf(systemSMSIce.IDDCode);
        this.messageText = systemSMSIce.messageText.equals("\u0000") ? null : systemSMSIce.messageText;
        this.gateway = systemSMSIce.gateway == Integer.MIN_VALUE ? null : Integer.valueOf(systemSMSIce.gateway);
        this.dateDispatched = systemSMSIce.dateDispatched == Long.MIN_VALUE ? null : new Date(systemSMSIce.dateDispatched);
        this.providerTransactionID = systemSMSIce.providerTransactionID.equals("\u0000") ? null : systemSMSIce.providerTransactionID;
        this.status = systemSMSIce.status == Integer.MIN_VALUE ? null : StatusEnum.fromValue(systemSMSIce.status);
        this.registrationIP = systemSMSIce.registrationIP;
    }

    public SystemSMSDataIce toIceObject() {
        SystemSMSDataIce systemSMSIce = new SystemSMSDataIce();
        systemSMSIce.id = this.id == null ? Integer.MIN_VALUE : this.id;
        systemSMSIce.username = this.username == null ? "\u0000" : this.username;
        systemSMSIce.dateCreated = this.dateCreated == null ? Long.MIN_VALUE : this.dateCreated.getTime();
        systemSMSIce.type = this.type == null ? Integer.MIN_VALUE : this.type.value();
        systemSMSIce.subType = this.subType == null ? Integer.MIN_VALUE : this.subType.value();
        systemSMSIce.source = this.source == null ? "\u0000" : this.source;
        systemSMSIce.destination = this.destination == null ? "\u0000" : this.destination;
        systemSMSIce.IDDCode = this.IDDCode == null ? Integer.MIN_VALUE : this.IDDCode;
        systemSMSIce.messageText = this.messageText == null ? "\u0000" : this.messageText;
        systemSMSIce.gateway = this.gateway == null ? Integer.MIN_VALUE : this.gateway;
        systemSMSIce.dateDispatched = this.dateDispatched == null ? Long.MIN_VALUE : this.dateDispatched.getTime();
        systemSMSIce.providerTransactionID = this.providerTransactionID == null ? "\u0000" : this.providerTransactionID;
        systemSMSIce.status = this.status == null ? Integer.MIN_VALUE : this.status.value();
        systemSMSIce.registrationIP = this.registrationIP;
        return systemSMSIce;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        PENDING(0),
        SENT(1),
        FAILED(2);

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
    public static enum SubTypeEnum {
        ACTIVATION_CODE(1),
        FORGOT_PASSWORD(2),
        USER_REFERRAL(3),
        USER_REFERRAL_ACTIVATION(4),
        MIG33_WAP_PUSH(5),
        MIG33_PREMIUM_SMS(6),
        TT_NOTIFICATION(7),
        SMS_CALLBACK_HELP(8),
        SMS_CALLBACK_BALANCE(9),
        EMAIL_ALERT(10),
        BANK_TRANSFER_CONFIRMATION(11),
        LOW_BALANCE_ALERT(12),
        MERCHANT_USER_ACTIVATION(13),
        BUZZ(14),
        LOOKOUT(15),
        WESTERN_UNION_CONFIRMATION(16),
        MOBILE_CONTENT_DOWNLOAD(17),
        SMS_VOUCHER_RECHARGE(18),
        VIRTUAL_GIFT_NOTIFICATION(19),
        GROUP_ANNOUNCEMENT_NOTIFICATION(20),
        BANGLALINK_URL_DOWNLOAD(21),
        BANGLALINK_VOUCHER(22),
        GROUP_EVENT_NOTIFICATION(23),
        INDOSAT_URL_DOWNLOAD(24),
        SUBSCRIPTION_EXPIRY_NOTIFICATION(25),
        MARKETING_REWARD_NOTIFICATION(26),
        USER_REFERRAL_VIA_GAMES(27);

        private int value;

        private SubTypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static SubTypeEnum fromValue(int value) {
            for (SubTypeEnum e : SubTypeEnum.values()) {
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
        STANDARD(1),
        WAP_PUSH(2),
        PREMIUM(3);

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

