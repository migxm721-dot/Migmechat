/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;

public class DiscountTierData
implements Serializable {
    public Integer id;
    public String name;
    public TypeEnum type;
    public Double actualMin;
    public Double displayMin;
    public Double max;
    public String currency;
    public Double percentageDiscount;
    public boolean applyToCreditCard;
    public boolean applyToBankTransfer;
    public boolean applyToWesternUnion;
    public boolean applyToVoucher;
    public boolean applyToTelegraphicTransfer;
    public StatusEnum status;
    public Double discountAmount;
    public Double adjustmentAmount;
    public boolean canBeApplied;
    public boolean appliedThisMonth;
    public Double dbActualMin;
    public Double dbDisplayMin;
    public Double dbMax;
    public String dbCurrency;

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
    public static enum TypeEnum {
        FIRST_TIME_ONLY(1),
        RECURRING(2);

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

