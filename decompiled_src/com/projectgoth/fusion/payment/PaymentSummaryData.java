/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.payment;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentSummaryData
implements Serializable {
    public int count = 0;
    public double cummValue = 0.0;

    public void populateFrom(ResultSet rs) throws SQLException {
        Object cummValueObj;
        Object countObj = rs.getObject("count");
        if (countObj != null) {
            this.count = rs.getInt("count");
        }
        if ((cummValueObj = rs.getObject("cummValue")) != null) {
            this.cummValue = rs.getDouble("cummValue");
        }
    }
}

