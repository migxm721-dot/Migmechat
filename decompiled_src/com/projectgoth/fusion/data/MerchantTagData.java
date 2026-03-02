/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.ejb.CreateException;

public class MerchantTagData
implements Serializable {
    public Integer id;
    public Integer userID;
    public Integer merchantUserID;
    public String dateCreated;
    public String lastSalesDate;
    public StatusEnum status;
    public Double amount;
    public String currency;
    public Long accountEntryID;

    public MerchantTagData(ResultSet rs) throws SQLException {
        Number intVal;
        this.id = (Integer)rs.getObject("id");
        this.dateCreated = DateTimeUtils.getStringForMigcore(rs.getTimestamp("dateCreated"));
        this.lastSalesDate = DateTimeUtils.getStringForMigcore(rs.getTimestamp("lastSalesDate"));
        try {
            this.amount = rs.getDouble("amount");
            this.currency = rs.getString("currency");
        }
        catch (Exception e) {
            this.amount = 0.0;
            this.currency = null;
        }
        this.userID = rs.getInt("userID");
        if (this.userID == 0) {
            this.userID = null;
        }
        this.merchantUserID = rs.getInt("merchantUserID");
        if (this.merchantUserID == 0) {
            this.merchantUserID = null;
        }
        if ((intVal = (Number)rs.getObject("status")) != null) {
            this.status = StatusEnum.fromValue(intVal.intValue());
        }
        try {
            this.accountEntryID = rs.getLong("accountentryid");
        }
        catch (Exception e) {
            this.accountEntryID = null;
        }
    }

    public boolean isActive() {
        return this.status == StatusEnum.ACTIVE;
    }

    public Double getTransferTagAmount(String nCurrency) throws Exception {
        double transferRate = 1.0;
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            CurrencyData newCurrency = misBean.getCurrency(nCurrency);
            double minRequirementAUD = SystemProperty.getDouble("MinMerchantUserTagAmountAUD", 0.01);
            if (this.currency == null) {
                return Double.valueOf(df.format(minRequirementAUD * newCurrency.exchangeRate));
            }
            CurrencyData transactionCurrency = misBean.getCurrency(this.currency);
            transferRate = newCurrency.exchangeRate < transactionCurrency.exchangeRate ? newCurrency.exchangeRate.doubleValue() : transactionCurrency.exchangeRate.doubleValue();
            double tagAmount = 0.0;
            tagAmount = transferRate > 1.0 ? (this.amount / transactionCurrency.exchangeRate + minRequirementAUD) * newCurrency.exchangeRate : (this.amount + minRequirementAUD) / transactionCurrency.exchangeRate * newCurrency.exchangeRate;
            return Double.valueOf(df.format(tagAmount));
        }
        catch (CreateException e) {
            throw new Exception(e.getMessage());
        }
    }

    public long getLastSalesDateInMiliSeconds() throws ParseException {
        return DateTimeUtils.getTimeInMilisecondsFromString(this.lastSalesDate);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        INACTIVE(0),
        ACTIVE(1),
        PENDING(2);

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
        TOP_MERCHANT_TAG(1, SystemProperty.getInt("TopMerchantTagValidPeriod", 43200), "Top Merchant Tag"),
        NON_TOP_MERCHANT_TAG(2, SystemProperty.getInt("NonTopMerchantTagValidPeriod", 43200), "Non-Top Merchant Tag");

        private int value;
        private String description;
        private int validity;

        private TypeEnum(int value, int validity, String description) {
            this.value = value;
            this.validity = validity;
            this.description = description;
        }

        public int value() {
            return this.value;
        }

        public int validity() {
            return this.validity;
        }

        public String description() {
            return this.description;
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

