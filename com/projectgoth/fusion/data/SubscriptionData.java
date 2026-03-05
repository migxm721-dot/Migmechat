/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.springframework.util.StringUtils;

public class SubscriptionData
implements Serializable {
    public Integer id;
    public String username;
    public Integer serviceID;
    public Date dateCreated;
    public TypeEnum type;
    public String ipAddress;
    public String mobilePhone;
    public Date expiryDate;
    public Boolean expiryReminderSent;
    public Date cancellationDate;
    public Integer billingAttempts;
    public Date lastBillingAttempt;
    public StatusEnum status;
    public String serviceName;

    public SubscriptionData() {
    }

    public SubscriptionData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("id");
        this.username = rs.getString("username");
        this.serviceID = (Integer)rs.getObject("serviceID");
        this.dateCreated = rs.getTimestamp("dateCreated");
        this.ipAddress = rs.getString("ipAddress");
        this.mobilePhone = rs.getString("mobilePhone");
        this.expiryDate = rs.getTimestamp("expiryDate");
        this.cancellationDate = rs.getTimestamp("cancellationDate");
        this.billingAttempts = (Integer)rs.getObject("billingAttempts");
        this.lastBillingAttempt = rs.getTimestamp("lastBillingAttempt");
        Integer intVal = (Integer)rs.getObject("type");
        if (intVal != null) {
            this.type = TypeEnum.fromValue(intVal);
        }
        if ((intVal = (Integer)rs.getObject("expiryReminderSent")) != null) {
            this.expiryReminderSent = intVal == 1;
        }
        if ((intVal = (Integer)rs.getObject("status")) != null) {
            this.status = StatusEnum.fromValue(intVal);
        }
        try {
            String serviceName = rs.getString("servicename");
            if (StringUtils.hasLength((String)serviceName)) {
                this.serviceName = serviceName;
            }
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        PENDING(0),
        ACTIVE(1),
        CANCELLED(2),
        EXPIRED(3),
        FAILED(4);

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
        FREE_TRIAL(1),
        PAID(2);

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

