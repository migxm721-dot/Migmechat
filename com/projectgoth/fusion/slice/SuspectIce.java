/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class SuspectIce
implements Cloneable,
Serializable {
    public String clientIP;
    public int clientPort;
    public long lastAddedTo;
    public double meanTcpTimestamp;
    public double meanTcpTimestampOverArrivalTime;
    public String username;

    public SuspectIce() {
    }

    public SuspectIce(String clientIP, int clientPort, long lastAddedTo, double meanTcpTimestamp, double meanTcpTimestampOverArrivalTime, String username) {
        this.clientIP = clientIP;
        this.clientPort = clientPort;
        this.lastAddedTo = lastAddedTo;
        this.meanTcpTimestamp = meanTcpTimestamp;
        this.meanTcpTimestampOverArrivalTime = meanTcpTimestampOverArrivalTime;
        this.username = username;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        SuspectIce _r = null;
        try {
            _r = (SuspectIce)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.clientIP != _r.clientIP && this.clientIP != null && !this.clientIP.equals(_r.clientIP)) {
                return false;
            }
            if (this.clientPort != _r.clientPort) {
                return false;
            }
            if (this.lastAddedTo != _r.lastAddedTo) {
                return false;
            }
            if (this.meanTcpTimestamp != _r.meanTcpTimestamp) {
                return false;
            }
            if (this.meanTcpTimestampOverArrivalTime != _r.meanTcpTimestampOverArrivalTime) {
                return false;
            }
            return this.username == _r.username || this.username == null || this.username.equals(_r.username);
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        if (this.clientIP != null) {
            __h = 5 * __h + this.clientIP.hashCode();
        }
        __h = 5 * __h + this.clientPort;
        __h = 5 * __h + (int)this.lastAddedTo;
        __h = 5 * __h + (int)Double.doubleToLongBits(this.meanTcpTimestamp);
        __h = 5 * __h + (int)Double.doubleToLongBits(this.meanTcpTimestampOverArrivalTime);
        if (this.username != null) {
            __h = 5 * __h + this.username.hashCode();
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
        __os.writeString(this.clientIP);
        __os.writeInt(this.clientPort);
        __os.writeLong(this.lastAddedTo);
        __os.writeDouble(this.meanTcpTimestamp);
        __os.writeDouble(this.meanTcpTimestampOverArrivalTime);
        __os.writeString(this.username);
    }

    public void __read(BasicStream __is) {
        this.clientIP = __is.readString();
        this.clientPort = __is.readInt();
        this.lastAddedTo = __is.readLong();
        this.meanTcpTimestamp = __is.readDouble();
        this.meanTcpTimestampOverArrivalTime = __is.readDouble();
        this.username = __is.readString();
    }
}

