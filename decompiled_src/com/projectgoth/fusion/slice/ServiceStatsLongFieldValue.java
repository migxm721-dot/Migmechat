/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class ServiceStatsLongFieldValue
implements Cloneable,
Serializable {
    public long value;
    public long lastUpdatedTime;

    public ServiceStatsLongFieldValue() {
    }

    public ServiceStatsLongFieldValue(long value, long lastUpdatedTime) {
        this.value = value;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        ServiceStatsLongFieldValue _r = null;
        try {
            _r = (ServiceStatsLongFieldValue)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.value != _r.value) {
                return false;
            }
            return this.lastUpdatedTime == _r.lastUpdatedTime;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        __h = 5 * __h + (int)this.value;
        __h = 5 * __h + (int)this.lastUpdatedTime;
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
        __os.writeLong(this.value);
        __os.writeLong(this.lastUpdatedTime);
    }

    public void __read(BasicStream __is) {
        this.value = __is.readLong();
        this.lastUpdatedTime = __is.readLong();
    }
}

