/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class WebServiceResponse
implements Cloneable,
Serializable {
    public String responseData;
    public int responseCode;

    public WebServiceResponse() {
    }

    public WebServiceResponse(String responseData, int responseCode) {
        this.responseData = responseData;
        this.responseCode = responseCode;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        WebServiceResponse _r = null;
        try {
            _r = (WebServiceResponse)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.responseData != _r.responseData && this.responseData != null && !this.responseData.equals(_r.responseData)) {
                return false;
            }
            return this.responseCode == _r.responseCode;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        if (this.responseData != null) {
            __h = 5 * __h + this.responseData.hashCode();
        }
        __h = 5 * __h + this.responseCode;
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
        __os.writeString(this.responseData);
        __os.writeInt(this.responseCode);
    }

    public void __read(BasicStream __is) {
        this.responseData = __is.readString();
        this.responseCode = __is.readInt();
    }
}

