/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.util.Date;

public class BankTransferReceivedData
implements Serializable {
    public Integer id;
    public Integer bankTransferIntentID;
    public Date dateCreated;
    public TypeEnum type;
    public String fileName;
    public Integer row;
    public String paymentReference;
    public String invoiceNumber;
    public String customerID;
    public String additionalReference;
    public Integer effortNumber;
    public String invoiceCurrencyDeliv;
    public Double invoiceAmountDeliv;
    public String invoiceCurrencyLocal;
    public Double invoiceAmountLocal;
    public String paymentMethod;
    public String creditCardCompany;
    public String uncleanIndicator;
    public String paymentCurrency;
    public Double paymentAmount;
    public String currencyDue;
    public Double amountDue;
    public Integer dateDue;
    public String reversalCurrency;
    public Double reversalAmount;
    public String reversalReasonID;
    public String reversalReasonDescription;
    public Integer dateCollect;
    public StatusEnum status;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        NEW(0),
        PROCESSED(1);

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
        PAYMENT(1),
        CORRECTION(2),
        REVERSAL(3),
        REVERSAL_CORRECTION(4);

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

