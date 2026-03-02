/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class PresenceAndCapabilityIce
implements Cloneable,
Serializable {
    public int fusionPresence;
    public int msnPresence;
    public int aimPresence;
    public int yahooPresence;
    public int gtalkPresence;
    public int facebookPresence;

    public PresenceAndCapabilityIce() {
    }

    public PresenceAndCapabilityIce(int fusionPresence, int msnPresence, int aimPresence, int yahooPresence, int gtalkPresence, int facebookPresence) {
        this.fusionPresence = fusionPresence;
        this.msnPresence = msnPresence;
        this.aimPresence = aimPresence;
        this.yahooPresence = yahooPresence;
        this.gtalkPresence = gtalkPresence;
        this.facebookPresence = facebookPresence;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        PresenceAndCapabilityIce _r = null;
        try {
            _r = (PresenceAndCapabilityIce)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.fusionPresence != _r.fusionPresence) {
                return false;
            }
            if (this.msnPresence != _r.msnPresence) {
                return false;
            }
            if (this.aimPresence != _r.aimPresence) {
                return false;
            }
            if (this.yahooPresence != _r.yahooPresence) {
                return false;
            }
            if (this.gtalkPresence != _r.gtalkPresence) {
                return false;
            }
            return this.facebookPresence == _r.facebookPresence;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        __h = 5 * __h + this.fusionPresence;
        __h = 5 * __h + this.msnPresence;
        __h = 5 * __h + this.aimPresence;
        __h = 5 * __h + this.yahooPresence;
        __h = 5 * __h + this.gtalkPresence;
        __h = 5 * __h + this.facebookPresence;
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
        __os.writeInt(this.fusionPresence);
        __os.writeInt(this.msnPresence);
        __os.writeInt(this.aimPresence);
        __os.writeInt(this.yahooPresence);
        __os.writeInt(this.gtalkPresence);
        __os.writeInt(this.facebookPresence);
    }

    public void __read(BasicStream __is) {
        this.fusionPresence = __is.readInt();
        this.msnPresence = __is.readInt();
        this.aimPresence = __is.readInt();
        this.yahooPresence = __is.readInt();
        this.gtalkPresence = __is.readInt();
        this.facebookPresence = __is.readInt();
    }
}

