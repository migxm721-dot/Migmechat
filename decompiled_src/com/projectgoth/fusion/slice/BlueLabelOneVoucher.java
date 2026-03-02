/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class BlueLabelOneVoucher
implements Cloneable,
Serializable {
    public String number;
    public String amountRedeemed;
    public String value;
    public String currency;
    public String transactionReference;

    public BlueLabelOneVoucher() {
    }

    public BlueLabelOneVoucher(String number, String amountRedeemed, String value, String currency, String transactionReference) {
        this.number = number;
        this.amountRedeemed = amountRedeemed;
        this.value = value;
        this.currency = currency;
        this.transactionReference = transactionReference;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        BlueLabelOneVoucher _r = null;
        try {
            _r = (BlueLabelOneVoucher)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.number != _r.number && this.number != null && !this.number.equals(_r.number)) {
                return false;
            }
            if (this.amountRedeemed != _r.amountRedeemed && this.amountRedeemed != null && !this.amountRedeemed.equals(_r.amountRedeemed)) {
                return false;
            }
            if (this.value != _r.value && this.value != null && !this.value.equals(_r.value)) {
                return false;
            }
            if (this.currency != _r.currency && this.currency != null && !this.currency.equals(_r.currency)) {
                return false;
            }
            return this.transactionReference == _r.transactionReference || this.transactionReference == null || this.transactionReference.equals(_r.transactionReference);
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        if (this.number != null) {
            __h = 5 * __h + this.number.hashCode();
        }
        if (this.amountRedeemed != null) {
            __h = 5 * __h + this.amountRedeemed.hashCode();
        }
        if (this.value != null) {
            __h = 5 * __h + this.value.hashCode();
        }
        if (this.currency != null) {
            __h = 5 * __h + this.currency.hashCode();
        }
        if (this.transactionReference != null) {
            __h = 5 * __h + this.transactionReference.hashCode();
        }
        return __h;
    }

    public Object clone() {
        Object o;
        block2: {
            o = null;
            try {
                o = super.clone();
            }
            catch (CloneNotSupportedException ex) {
                if ($assertionsDisabled) break block2;
                throw new AssertionError();
            }
        }
        return o;
    }

    public void __write(BasicStream __os) {
        __os.writeString(this.number);
        __os.writeString(this.amountRedeemed);
        __os.writeString(this.value);
        __os.writeString(this.currency);
        __os.writeString(this.transactionReference);
    }

    public void __read(BasicStream __is) {
        this.number = __is.readString();
        this.amountRedeemed = __is.readString();
        this.value = __is.readString();
        this.currency = __is.readString();
        this.transactionReference = __is.readString();
    }
}

