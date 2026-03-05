/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import com.projectgoth.fusion.slice.SuspectIce;
import com.projectgoth.fusion.slice.SuspectIceArrayHelper;
import java.io.Serializable;
import java.util.Arrays;

public final class SuspectGroupIce
implements Cloneable,
Serializable {
    public SuspectIce[] members;
    public int innocentPortCount;

    public SuspectGroupIce() {
    }

    public SuspectGroupIce(SuspectIce[] members, int innocentPortCount) {
        this.members = members;
        this.innocentPortCount = innocentPortCount;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        SuspectGroupIce _r = null;
        try {
            _r = (SuspectGroupIce)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (!Arrays.equals(this.members, _r.members)) {
                return false;
            }
            return this.innocentPortCount == _r.innocentPortCount;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        if (this.members != null) {
            for (int __i0 = 0; __i0 < this.members.length; ++__i0) {
                if (this.members[__i0] == null) continue;
                __h = 5 * __h + this.members[__i0].hashCode();
            }
        }
        __h = 5 * __h + this.innocentPortCount;
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
        SuspectIceArrayHelper.write(__os, this.members);
        __os.writeInt(this.innocentPortCount);
    }

    public void __read(BasicStream __is) {
        this.members = SuspectIceArrayHelper.read(__is);
        this.innocentPortCount = __is.readInt();
    }
}

