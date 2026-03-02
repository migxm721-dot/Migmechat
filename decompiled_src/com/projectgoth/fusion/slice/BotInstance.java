/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import com.projectgoth.fusion.slice.BotServicePrx;
import com.projectgoth.fusion.slice.BotServicePrxHelper;
import java.io.Serializable;

public final class BotInstance
implements Cloneable,
Serializable {
    public String id;
    public int type;
    public String displayName;
    public String description;
    public String startedBy;
    public BotServicePrx botServiceProxy;

    public BotInstance() {
    }

    public BotInstance(String id, int type, String displayName, String description, String startedBy, BotServicePrx botServiceProxy) {
        this.id = id;
        this.type = type;
        this.displayName = displayName;
        this.description = description;
        this.startedBy = startedBy;
        this.botServiceProxy = botServiceProxy;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        BotInstance _r = null;
        try {
            _r = (BotInstance)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.id != _r.id && this.id != null && !this.id.equals(_r.id)) {
                return false;
            }
            if (this.type != _r.type) {
                return false;
            }
            if (this.displayName != _r.displayName && this.displayName != null && !this.displayName.equals(_r.displayName)) {
                return false;
            }
            if (this.description != _r.description && this.description != null && !this.description.equals(_r.description)) {
                return false;
            }
            if (this.startedBy != _r.startedBy && this.startedBy != null && !this.startedBy.equals(_r.startedBy)) {
                return false;
            }
            return this.botServiceProxy == _r.botServiceProxy || this.botServiceProxy == null || this.botServiceProxy.equals(_r.botServiceProxy);
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        if (this.id != null) {
            __h = 5 * __h + this.id.hashCode();
        }
        __h = 5 * __h + this.type;
        if (this.displayName != null) {
            __h = 5 * __h + this.displayName.hashCode();
        }
        if (this.description != null) {
            __h = 5 * __h + this.description.hashCode();
        }
        if (this.startedBy != null) {
            __h = 5 * __h + this.startedBy.hashCode();
        }
        if (this.botServiceProxy != null) {
            __h = 5 * __h + this.botServiceProxy.hashCode();
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
        __os.writeString(this.id);
        __os.writeInt(this.type);
        __os.writeString(this.displayName);
        __os.writeString(this.description);
        __os.writeString(this.startedBy);
        BotServicePrxHelper.__write(__os, this.botServiceProxy);
    }

    public void __read(BasicStream __is) {
        this.id = __is.readString();
        this.type = __is.readInt();
        this.displayName = __is.readString();
        this.description = __is.readString();
        this.startedBy = __is.readString();
        this.botServiceProxy = BotServicePrxHelper.__read(__is);
    }
}

