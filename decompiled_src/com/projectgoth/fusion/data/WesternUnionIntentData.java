/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.util.Date;

public class WesternUnionIntentData
implements Serializable {
    public Integer id;
    public String username;
    public Date dateCreated;
    public Integer countryID;
    public Integer paymentProductID;
    public String surname;
    public Double amount;
    public String currency;
    public String returnMAC;
    public Integer statusID;
    public String additionalReference;
    public String ref;
    public String formMethod;
    public String externalReference;
    public Integer effortID;
    public String mac;
    public String paymentReference;
    public String formAction;
    public Integer attemptID;
    public Integer merchantID;
    public String statusDate;
    public Integer orderID;
    public StatusEnum status;
    public String companyName;
    public String companyCode;
    public String countryCode;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        OPEN(0),
        MATCHED(1);

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

