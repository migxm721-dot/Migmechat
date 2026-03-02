/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class MoneyTransferData
implements Serializable {
    public Integer id;
    public String username;
    public Date dateCreated;
    public String receiptNumber;
    public String fullName;
    public Double amount;
    public TypeEnum type;

    public MoneyTransferData() {
    }

    public MoneyTransferData(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.username = rs.getString("username");
        this.dateCreated = new Date(rs.getTimestamp("dateCreated").getTime());
        this.type = TypeEnum.fromValue(rs.getInt("type"));
        this.receiptNumber = rs.getString("receiptNumber");
        this.fullName = rs.getString("fullName");
        this.amount = rs.getDouble("amount");
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum {
        TELEGRAPHIC_TRANSFER(0),
        WESTERN_UNION(1);

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

