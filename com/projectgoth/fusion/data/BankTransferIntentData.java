/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.util.Date;

public class BankTransferIntentData
implements Serializable {
    public Integer id;
    public String username;
    public Date dateCreated;
    public Integer countryID;
    public Integer paymentProductID;
    public String surname;
    public String firstname;
    public String middlename;
    public String fiscalNumber;
    public Double amount;
    public String currency;
    public String countryDescription;
    public Integer statusID;
    public String additionalReference;
    public String accountHolder;
    public String bankName;
    public String externalReference;
    public Integer effortID;
    public String paymentReference;
    public Integer attemptID;
    public Integer merchantID;
    public String bankAccountNumber;
    public String statusDate;
    public String city;
    public Integer orderID;
    public String specialID;
    public String swiftCode;
    public StatusEnum status;

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

