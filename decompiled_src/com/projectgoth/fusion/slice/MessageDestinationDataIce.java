/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class MessageDestinationDataIce
implements Cloneable,
Serializable {
    public int id;
    public int messageID;
    public int contactID;
    public int type;
    public String destination;
    public int IDDCode;
    public double cost;
    public int gateway;
    public long dateDispatched;
    public String providerTransactionID;
    public int status;

    public MessageDestinationDataIce() {
    }

    public MessageDestinationDataIce(int id, int messageID, int contactID, int type, String destination, int IDDCode, double cost, int gateway, long dateDispatched, String providerTransactionID, int status) {
        this.id = id;
        this.messageID = messageID;
        this.contactID = contactID;
        this.type = type;
        this.destination = destination;
        this.IDDCode = IDDCode;
        this.cost = cost;
        this.gateway = gateway;
        this.dateDispatched = dateDispatched;
        this.providerTransactionID = providerTransactionID;
        this.status = status;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        MessageDestinationDataIce _r = null;
        try {
            _r = (MessageDestinationDataIce)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.id != _r.id) {
                return false;
            }
            if (this.messageID != _r.messageID) {
                return false;
            }
            if (this.contactID != _r.contactID) {
                return false;
            }
            if (this.type != _r.type) {
                return false;
            }
            if (this.destination != _r.destination && this.destination != null && !this.destination.equals(_r.destination)) {
                return false;
            }
            if (this.IDDCode != _r.IDDCode) {
                return false;
            }
            if (this.cost != _r.cost) {
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
            return this.status == _r.status;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        __h = 5 * __h + this.id;
        __h = 5 * __h + this.messageID;
        __h = 5 * __h + this.contactID;
        __h = 5 * __h + this.type;
        if (this.destination != null) {
            __h = 5 * __h + this.destination.hashCode();
        }
        __h = 5 * __h + this.IDDCode;
        __h = 5 * __h + (int)Double.doubleToLongBits(this.cost);
        __h = 5 * __h + this.gateway;
        __h = 5 * __h + (int)this.dateDispatched;
        if (this.providerTransactionID != null) {
            __h = 5 * __h + this.providerTransactionID.hashCode();
        }
        __h = 5 * __h + this.status;
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
        __os.writeInt(this.messageID);
        __os.writeInt(this.contactID);
        __os.writeInt(this.type);
        __os.writeString(this.destination);
        __os.writeInt(this.IDDCode);
        __os.writeDouble(this.cost);
        __os.writeInt(this.gateway);
        __os.writeLong(this.dateDispatched);
        __os.writeString(this.providerTransactionID);
        __os.writeInt(this.status);
    }

    public void __read(BasicStream __is) {
        this.id = __is.readInt();
        this.messageID = __is.readInt();
        this.contactID = __is.readInt();
        this.type = __is.readInt();
        this.destination = __is.readString();
        this.IDDCode = __is.readInt();
        this.cost = __is.readDouble();
        this.gateway = __is.readInt();
        this.dateDispatched = __is.readLong();
        this.providerTransactionID = __is.readString();
        this.status = __is.readInt();
    }
}

