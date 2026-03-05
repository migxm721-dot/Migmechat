/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class SystemSMSDataIce
implements Cloneable,
Serializable {
    public int id;
    public String username;
    public long dateCreated;
    public int type;
    public int subType;
    public String source;
    public String destination;
    public int IDDCode;
    public String messageText;
    public int gateway;
    public long dateDispatched;
    public String providerTransactionID;
    public int status;
    public String registrationIP;

    public SystemSMSDataIce() {
    }

    public SystemSMSDataIce(int id, String username, long dateCreated, int type, int subType, String source, String destination, int IDDCode, String messageText, int gateway, long dateDispatched, String providerTransactionID, int status, String registrationIP) {
        this.id = id;
        this.username = username;
        this.dateCreated = dateCreated;
        this.type = type;
        this.subType = subType;
        this.source = source;
        this.destination = destination;
        this.IDDCode = IDDCode;
        this.messageText = messageText;
        this.gateway = gateway;
        this.dateDispatched = dateDispatched;
        this.providerTransactionID = providerTransactionID;
        this.status = status;
        this.registrationIP = registrationIP;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        SystemSMSDataIce _r = null;
        try {
            _r = (SystemSMSDataIce)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.id != _r.id) {
                return false;
            }
            if (this.username != _r.username && this.username != null && !this.username.equals(_r.username)) {
                return false;
            }
            if (this.dateCreated != _r.dateCreated) {
                return false;
            }
            if (this.type != _r.type) {
                return false;
            }
            if (this.subType != _r.subType) {
                return false;
            }
            if (this.source != _r.source && this.source != null && !this.source.equals(_r.source)) {
                return false;
            }
            if (this.destination != _r.destination && this.destination != null && !this.destination.equals(_r.destination)) {
                return false;
            }
            if (this.IDDCode != _r.IDDCode) {
                return false;
            }
            if (this.messageText != _r.messageText && this.messageText != null && !this.messageText.equals(_r.messageText)) {
                return false;
            }
            if (this.gateway != _r.gateway) {
                return false;
            }
            if (this.dateDispatched != _r.dateDispatched) {
                return false;
            }
            if (this.providerTransactionID != _r.providerTransactionID && this.providerTransactionID != null && !this.providerTransactionID.equals(_r.providerTransactionID)) {
                return false;
            }
            if (this.status != _r.status) {
                return false;
            }
            return this.registrationIP == _r.registrationIP || this.registrationIP == null || this.registrationIP.equals(_r.registrationIP);
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        __h = 5 * __h + this.id;
        if (this.username != null) {
            __h = 5 * __h + this.username.hashCode();
        }
        __h = 5 * __h + (int)this.dateCreated;
        __h = 5 * __h + this.type;
        __h = 5 * __h + this.subType;
        if (this.source != null) {
            __h = 5 * __h + this.source.hashCode();
        }
        if (this.destination != null) {
            __h = 5 * __h + this.destination.hashCode();
        }
        __h = 5 * __h + this.IDDCode;
        if (this.messageText != null) {
            __h = 5 * __h + this.messageText.hashCode();
        }
        __h = 5 * __h + this.gateway;
        __h = 5 * __h + (int)this.dateDispatched;
        if (this.providerTransactionID != null) {
            __h = 5 * __h + this.providerTransactionID.hashCode();
        }
        __h = 5 * __h + this.status;
        if (this.registrationIP != null) {
            __h = 5 * __h + this.registrationIP.hashCode();
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
        __os.writeInt(this.id);
        __os.writeString(this.username);
        __os.writeLong(this.dateCreated);
        __os.writeInt(this.type);
        __os.writeInt(this.subType);
        __os.writeString(this.source);
        __os.writeString(this.destination);
        __os.writeInt(this.IDDCode);
        __os.writeString(this.messageText);
        __os.writeInt(this.gateway);
        __os.writeLong(this.dateDispatched);
        __os.writeString(this.providerTransactionID);
        __os.writeInt(this.status);
        __os.writeString(this.registrationIP);
    }

    public void __read(BasicStream __is) {
        this.id = __is.readInt();
        this.username = __is.readString();
        this.dateCreated = __is.readLong();
        this.type = __is.readInt();
        this.subType = __is.readInt();
        this.source = __is.readString();
        this.destination = __is.readString();
        this.IDDCode = __is.readInt();
        this.messageText = __is.readString();
        this.gateway = __is.readInt();
        this.dateDispatched = __is.readLong();
        this.providerTransactionID = __is.readString();
        this.status = __is.readInt();
        this.registrationIP = __is.readString();
    }
}

