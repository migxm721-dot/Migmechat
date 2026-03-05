/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class SessionIce
implements Cloneable,
Serializable {
    public int userID;
    public String username;
    public String language;
    public int sourceCountryID;
    public boolean authenticated;
    public int deviceType;
    public int connectionType;
    public int port;
    public int remotePort;
    public String ipAddress;
    public String mobileDevice;
    public short clientVersion;
    public long startDateTime;
    public long endDateTime;

    public SessionIce() {
    }

    public SessionIce(int userID, String username, String language, int sourceCountryID, boolean authenticated, int deviceType, int connectionType, int port, int remotePort, String ipAddress, String mobileDevice, short clientVersion, long startDateTime, long endDateTime) {
        this.userID = userID;
        this.username = username;
        this.language = language;
        this.sourceCountryID = sourceCountryID;
        this.authenticated = authenticated;
        this.deviceType = deviceType;
        this.connectionType = connectionType;
        this.port = port;
        this.remotePort = remotePort;
        this.ipAddress = ipAddress;
        this.mobileDevice = mobileDevice;
        this.clientVersion = clientVersion;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        SessionIce _r = null;
        try {
            _r = (SessionIce)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.userID != _r.userID) {
                return false;
            }
            if (this.username != _r.username && this.username != null && !this.username.equals(_r.username)) {
                return false;
            }
            if (this.language != _r.language && this.language != null && !this.language.equals(_r.language)) {
                return false;
            }
            if (this.sourceCountryID != _r.sourceCountryID) {
                return false;
            }
            if (this.authenticated != _r.authenticated) {
                return false;
            }
            if (this.deviceType != _r.deviceType) {
                return false;
            }
            if (this.connectionType != _r.connectionType) {
                return false;
            }
            if (this.port != _r.port) {
                return false;
            }
            if (this.remotePort != _r.remotePort) {
                return false;
            }
            if (this.ipAddress != _r.ipAddress && this.ipAddress != null && !this.ipAddress.equals(_r.ipAddress)) {
                return false;
            }
            if (this.mobileDevice != _r.mobileDevice && this.mobileDevice != null && !this.mobileDevice.equals(_r.mobileDevice)) {
                return false;
            }
            if (this.clientVersion != _r.clientVersion) {
                return false;
            }
            if (this.startDateTime != _r.startDateTime) {
                return false;
            }
            return this.endDateTime == _r.endDateTime;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        __h = 5 * __h + this.userID;
        if (this.username != null) {
            __h = 5 * __h + this.username.hashCode();
        }
        if (this.language != null) {
            __h = 5 * __h + this.language.hashCode();
        }
        __h = 5 * __h + this.sourceCountryID;
        __h = 5 * __h + (this.authenticated ? 1 : 0);
        __h = 5 * __h + this.deviceType;
        __h = 5 * __h + this.connectionType;
        __h = 5 * __h + this.port;
        __h = 5 * __h + this.remotePort;
        if (this.ipAddress != null) {
            __h = 5 * __h + this.ipAddress.hashCode();
        }
        if (this.mobileDevice != null) {
            __h = 5 * __h + this.mobileDevice.hashCode();
        }
        __h = 5 * __h + this.clientVersion;
        __h = 5 * __h + (int)this.startDateTime;
        __h = 5 * __h + (int)this.endDateTime;
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
        __os.writeInt(this.userID);
        __os.writeString(this.username);
        __os.writeString(this.language);
        __os.writeInt(this.sourceCountryID);
        __os.writeBool(this.authenticated);
        __os.writeInt(this.deviceType);
        __os.writeInt(this.connectionType);
        __os.writeInt(this.port);
        __os.writeInt(this.remotePort);
        __os.writeString(this.ipAddress);
        __os.writeString(this.mobileDevice);
        __os.writeShort(this.clientVersion);
        __os.writeLong(this.startDateTime);
        __os.writeLong(this.endDateTime);
    }

    public void __read(BasicStream __is) {
        this.userID = __is.readInt();
        this.username = __is.readString();
        this.language = __is.readString();
        this.sourceCountryID = __is.readInt();
        this.authenticated = __is.readBool();
        this.deviceType = __is.readInt();
        this.connectionType = __is.readInt();
        this.port = __is.readInt();
        this.remotePort = __is.readInt();
        this.ipAddress = __is.readString();
        this.mobileDevice = __is.readString();
        this.clientVersion = __is.readShort();
        this.startDateTime = __is.readLong();
        this.endDateTime = __is.readLong();
    }
}

