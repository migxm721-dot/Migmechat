/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class CurrencyDataIce
implements Cloneable,
Serializable {
    public String code;
    public String name;
    public String symbol;
    public double exchangeRate;
    public long lastUpdated;

    public CurrencyDataIce() {
    }

    public CurrencyDataIce(String code, String name, String symbol, double exchangeRate, long lastUpdated) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.exchangeRate = exchangeRate;
        this.lastUpdated = lastUpdated;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        CurrencyDataIce _r = null;
        try {
            _r = (CurrencyDataIce)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.code != _r.code && this.code != null && !this.code.equals(_r.code)) {
                return false;
            }
            if (this.name != _r.name && this.name != null && !this.name.equals(_r.name)) {
                return false;
            }
            if (this.symbol != _r.symbol && this.symbol != null && !this.symbol.equals(_r.symbol)) {
                return false;
            }
            if (this.exchangeRate != _r.exchangeRate) {
                return false;
            }
            return this.lastUpdated == _r.lastUpdated;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        if (this.code != null) {
            __h = 5 * __h + this.code.hashCode();
        }
        if (this.name != null) {
            __h = 5 * __h + this.name.hashCode();
        }
        if (this.symbol != null) {
            __h = 5 * __h + this.symbol.hashCode();
        }
        __h = 5 * __h + (int)Double.doubleToLongBits(this.exchangeRate);
        __h = 5 * __h + (int)this.lastUpdated;
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
        __os.writeString(this.code);
        __os.writeString(this.name);
        __os.writeString(this.symbol);
        __os.writeDouble(this.exchangeRate);
        __os.writeLong(this.lastUpdated);
    }

    public void __read(BasicStream __is) {
        this.code = __is.readString();
        this.name = __is.readString();
        this.symbol = __is.readString();
        this.exchangeRate = __is.readDouble();
        this.lastUpdated = __is.readLong();
    }
}

