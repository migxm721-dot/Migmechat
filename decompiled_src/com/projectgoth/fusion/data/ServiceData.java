/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceData
implements Serializable {
    public Integer id;
    public String name;
    public String description;
    public Integer freeTrialDays;
    public Integer durationDays;
    public Double awardedCredit;
    public String awardedCreditCurrency;
    public BillingMethodEnum billingMethod;
    public Double cost;
    public String costCurrency;
    public String billingConfirmationSMS;
    public String expiryReminderSMS;
    public StatusEnum status;

    public ServiceData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("id");
        this.name = rs.getString("name");
        this.description = rs.getString("description");
        this.freeTrialDays = (Integer)rs.getObject("freeTrialDays");
        this.durationDays = (Integer)rs.getObject("durationDays");
        this.awardedCredit = (Double)rs.getObject("awardedCredit");
        this.awardedCreditCurrency = rs.getString("awardedCreditCurrency");
        this.cost = (Double)rs.getObject("cost");
        this.costCurrency = rs.getString("costCurrency");
        this.billingConfirmationSMS = rs.getString("billingConfirmationSMS");
        this.expiryReminderSMS = rs.getString("expiryReminderSMS");
        Integer intVal = (Integer)rs.getObject("billingMethod");
        if (intVal != null) {
            this.billingMethod = BillingMethodEnum.fromValue(intVal);
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
    public static enum BillingMethodEnum {
        USER_ACCOUNT(1),
        PREMIUM_SMS(2);

        private int value;

        private BillingMethodEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static BillingMethodEnum fromValue(int value) {
            for (BillingMethodEnum e : BillingMethodEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

