/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class VoucherData
implements Serializable {
    public Integer id;
    public Integer voucherBatchID;
    public String number;
    public Date lastUpdated;
    public StatusEnum status;
    public String notes;
    public String currency;
    public double amount;

    public VoucherData() {
    }

    public VoucherData(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.voucherBatchID = rs.getInt("VoucherBatchID");
        this.number = rs.getString("number");
        this.lastUpdated = rs.getTimestamp("lastupdated");
        this.status = StatusEnum.fromValue(rs.getInt("status"));
        this.notes = rs.getString("notes");
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        INACTIVE(0),
        ACTIVE(1),
        CANCELLED(2),
        REDEEMED(3),
        EXPIRED(4),
        FAILED(5);

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
}

