/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class MobileOriginatedSMSData
implements Serializable {
    public Integer id;
    public Date dateCreated;
    public TypeEnum type;
    public String receiver;
    public String sender;
    public String text;

    public boolean requiresAuthenticatedAccount() {
        switch (this.type) {
            case CALLBACK: 
            case BALANCE_REQUEST: 
            case INDOSAT_CANCEL_SUBSCRIPTION: {
                return true;
            }
        }
        return false;
    }

    public MobileOriginatedSMSData() {
    }

    public MobileOriginatedSMSData(ResultSet rs) throws SQLException {
        this.id = (Integer)rs.getObject("id");
        this.dateCreated = rs.getTimestamp("dateCreated");
        this.receiver = rs.getString("receiver");
        this.sender = rs.getString("sender");
        this.text = rs.getString("text");
        Integer intVal = (Integer)rs.getObject("type");
        if (intVal != null) {
            this.type = TypeEnum.fromValue(intVal);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum {
        UNKNOWN(1),
        CALLBACK(2),
        BALANCE_REQUEST(3),
        VOUCHER_REDEMPTION(4),
        INDOSAT_URL_DOWNLOAD(5),
        INDOSAT_SUBSCRIPTION(6),
        INDOSAT_CANCEL_SUBSCRIPTION(7);

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

