/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class SessionStatisticsIce
implements Cloneable,
Serializable {
    public int uniquePrivateChatUsers;
    public int profileEdits;

    public SessionStatisticsIce() {
    }

    public SessionStatisticsIce(int uniquePrivateChatUsers, int profileEdits) {
        this.uniquePrivateChatUsers = uniquePrivateChatUsers;
        this.profileEdits = profileEdits;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        SessionStatisticsIce _r = null;
        try {
            _r = (SessionStatisticsIce)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.uniquePrivateChatUsers != _r.uniquePrivateChatUsers) {
                return false;
            }
            return this.profileEdits == _r.profileEdits;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        __h = 5 * __h + this.uniquePrivateChatUsers;
        __h = 5 * __h + this.profileEdits;
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
        __os.writeInt(this.uniquePrivateChatUsers);
        __os.writeInt(this.profileEdits);
    }

    public void __read(BasicStream __is) {
        this.uniquePrivateChatUsers = __is.readInt();
        this.profileEdits = __is.readInt();
    }
}

