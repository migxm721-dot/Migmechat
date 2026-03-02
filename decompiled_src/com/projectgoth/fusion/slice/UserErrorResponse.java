/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class UserErrorResponse
implements Cloneable,
Serializable {
    public String reason;
    public boolean silentlyIgnore;
    public boolean error;

    public UserErrorResponse() {
    }

    public UserErrorResponse(String reason, boolean silentlyIgnore, boolean error) {
        this.reason = reason;
        this.silentlyIgnore = silentlyIgnore;
        this.error = error;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        UserErrorResponse _r = null;
        try {
            _r = (UserErrorResponse)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.reason != _r.reason && this.reason != null && !this.reason.equals(_r.reason)) {
                return false;
            }
            if (this.silentlyIgnore != _r.silentlyIgnore) {
                return false;
            }
            return this.error == _r.error;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        if (this.reason != null) {
            __h = 5 * __h + this.reason.hashCode();
        }
        __h = 5 * __h + (this.silentlyIgnore ? 1 : 0);
        __h = 5 * __h + (this.error ? 1 : 0);
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
        __os.writeString(this.reason);
        __os.writeBool(this.silentlyIgnore);
        __os.writeBool(this.error);
    }

    public void __read(BasicStream __is) {
        this.reason = __is.readString();
        this.silentlyIgnore = __is.readBool();
        this.error = __is.readBool();
    }
}

