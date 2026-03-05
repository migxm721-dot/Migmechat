/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class ContactDataIce
implements Cloneable,
Serializable {
    public int id;
    public String username;
    public String displayName;
    public String firstName;
    public String lastName;
    public String fusionUsername;
    public String msnUsername;
    public String aimUsername;
    public String yahooUsername;
    public String facebookUsername;
    public String gtalkUsername;
    public String emailAddress;
    public String mobilePhone;
    public String homePhone;
    public String officePhone;
    public int defaultIM;
    public int defaultPhoneNumber;
    public int contactGroupId;
    public int shareMobilePhone;
    public int displayOnPhone;
    public int status;
    public int fusionPresence;
    public int msnPresence;
    public int aimPresence;
    public int yahooPresence;
    public int facebookPresence;
    public int gtalkPresence;
    public String displayPicture;
    public String statusMessage;
    public long statusTimeStamp;

    public ContactDataIce() {
    }

    public ContactDataIce(int id, String username, String displayName, String firstName, String lastName, String fusionUsername, String msnUsername, String aimUsername, String yahooUsername, String facebookUsername, String gtalkUsername, String emailAddress, String mobilePhone, String homePhone, String officePhone, int defaultIM, int defaultPhoneNumber, int contactGroupId, int shareMobilePhone, int displayOnPhone, int status, int fusionPresence, int msnPresence, int aimPresence, int yahooPresence, int facebookPresence, int gtalkPresence, String displayPicture, String statusMessage, long statusTimeStamp) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fusionUsername = fusionUsername;
        this.msnUsername = msnUsername;
        this.aimUsername = aimUsername;
        this.yahooUsername = yahooUsername;
        this.facebookUsername = facebookUsername;
        this.gtalkUsername = gtalkUsername;
        this.emailAddress = emailAddress;
        this.mobilePhone = mobilePhone;
        this.homePhone = homePhone;
        this.officePhone = officePhone;
        this.defaultIM = defaultIM;
        this.defaultPhoneNumber = defaultPhoneNumber;
        this.contactGroupId = contactGroupId;
        this.shareMobilePhone = shareMobilePhone;
        this.displayOnPhone = displayOnPhone;
        this.status = status;
        this.fusionPresence = fusionPresence;
        this.msnPresence = msnPresence;
        this.aimPresence = aimPresence;
        this.yahooPresence = yahooPresence;
        this.facebookPresence = facebookPresence;
        this.gtalkPresence = gtalkPresence;
        this.displayPicture = displayPicture;
        this.statusMessage = statusMessage;
        this.statusTimeStamp = statusTimeStamp;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        ContactDataIce _r = null;
        try {
            _r = (ContactDataIce)rhs;
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
            if (this.displayName != _r.displayName && this.displayName != null && !this.displayName.equals(_r.displayName)) {
                return false;
            }
            if (this.firstName != _r.firstName && this.firstName != null && !this.firstName.equals(_r.firstName)) {
                return false;
            }
            if (this.lastName != _r.lastName && this.lastName != null && !this.lastName.equals(_r.lastName)) {
                return false;
            }
            if (this.fusionUsername != _r.fusionUsername && this.fusionUsername != null && !this.fusionUsername.equals(_r.fusionUsername)) {
                return false;
            }
            if (this.msnUsername != _r.msnUsername && this.msnUsername != null && !this.msnUsername.equals(_r.msnUsername)) {
                return false;
            }
            if (this.aimUsername != _r.aimUsername && this.aimUsername != null && !this.aimUsername.equals(_r.aimUsername)) {
                return false;
            }
            if (this.yahooUsername != _r.yahooUsername && this.yahooUsername != null && !this.yahooUsername.equals(_r.yahooUsername)) {
                return false;
            }
            if (this.facebookUsername != _r.facebookUsername && this.facebookUsername != null && !this.facebookUsername.equals(_r.facebookUsername)) {
                return false;
            }
            if (this.gtalkUsername != _r.gtalkUsername && this.gtalkUsername != null && !this.gtalkUsername.equals(_r.gtalkUsername)) {
                return false;
            }
            if (this.emailAddress != _r.emailAddress && this.emailAddress != null && !this.emailAddress.equals(_r.emailAddress)) {
                return false;
            }
            if (this.mobilePhone != _r.mobilePhone && this.mobilePhone != null && !this.mobilePhone.equals(_r.mobilePhone)) {
                return false;
            }
            if (this.homePhone != _r.homePhone && this.homePhone != null && !this.homePhone.equals(_r.homePhone)) {
                return false;
            }
            if (this.officePhone != _r.officePhone && this.officePhone != null && !this.officePhone.equals(_r.officePhone)) {
                return false;
            }
            if (this.defaultIM != _r.defaultIM) {
                return false;
            }
            if (this.defaultPhoneNumber != _r.defaultPhoneNumber) {
                return false;
            }
            if (this.contactGroupId != _r.contactGroupId) {
                return false;
            }
            if (this.shareMobilePhone != _r.shareMobilePhone) {
                return false;
            }
            if (this.displayOnPhone != _r.displayOnPhone) {
                return false;
            }
            if (this.status != _r.status) {
                return false;
            }
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
            if (this.facebookPresence != _r.facebookPresence) {
                return false;
            }
            if (this.gtalkPresence != _r.gtalkPresence) {
                return false;
            }
            if (this.displayPicture != _r.displayPicture && this.displayPicture != null && !this.displayPicture.equals(_r.displayPicture)) {
                return false;
            }
            if (this.statusMessage != _r.statusMessage && this.statusMessage != null && !this.statusMessage.equals(_r.statusMessage)) {
                return false;
            }
            return this.statusTimeStamp == _r.statusTimeStamp;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        __h = 5 * __h + this.id;
        if (this.username != null) {
            __h = 5 * __h + this.username.hashCode();
        }
        if (this.displayName != null) {
            __h = 5 * __h + this.displayName.hashCode();
        }
        if (this.firstName != null) {
            __h = 5 * __h + this.firstName.hashCode();
        }
        if (this.lastName != null) {
            __h = 5 * __h + this.lastName.hashCode();
        }
        if (this.fusionUsername != null) {
            __h = 5 * __h + this.fusionUsername.hashCode();
        }
        if (this.msnUsername != null) {
            __h = 5 * __h + this.msnUsername.hashCode();
        }
        if (this.aimUsername != null) {
            __h = 5 * __h + this.aimUsername.hashCode();
        }
        if (this.yahooUsername != null) {
            __h = 5 * __h + this.yahooUsername.hashCode();
        }
        if (this.facebookUsername != null) {
            __h = 5 * __h + this.facebookUsername.hashCode();
        }
        if (this.gtalkUsername != null) {
            __h = 5 * __h + this.gtalkUsername.hashCode();
        }
        if (this.emailAddress != null) {
            __h = 5 * __h + this.emailAddress.hashCode();
        }
        if (this.mobilePhone != null) {
            __h = 5 * __h + this.mobilePhone.hashCode();
        }
        if (this.homePhone != null) {
            __h = 5 * __h + this.homePhone.hashCode();
        }
        if (this.officePhone != null) {
            __h = 5 * __h + this.officePhone.hashCode();
        }
        __h = 5 * __h + this.defaultIM;
        __h = 5 * __h + this.defaultPhoneNumber;
        __h = 5 * __h + this.contactGroupId;
        __h = 5 * __h + this.shareMobilePhone;
        __h = 5 * __h + this.displayOnPhone;
        __h = 5 * __h + this.status;
        __h = 5 * __h + this.fusionPresence;
        __h = 5 * __h + this.msnPresence;
        __h = 5 * __h + this.aimPresence;
        __h = 5 * __h + this.yahooPresence;
        __h = 5 * __h + this.facebookPresence;
        __h = 5 * __h + this.gtalkPresence;
        if (this.displayPicture != null) {
            __h = 5 * __h + this.displayPicture.hashCode();
        }
        if (this.statusMessage != null) {
            __h = 5 * __h + this.statusMessage.hashCode();
        }
        __h = 5 * __h + (int)this.statusTimeStamp;
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
        __os.writeString(this.displayName);
        __os.writeString(this.firstName);
        __os.writeString(this.lastName);
        __os.writeString(this.fusionUsername);
        __os.writeString(this.msnUsername);
        __os.writeString(this.aimUsername);
        __os.writeString(this.yahooUsername);
        __os.writeString(this.facebookUsername);
        __os.writeString(this.gtalkUsername);
        __os.writeString(this.emailAddress);
        __os.writeString(this.mobilePhone);
        __os.writeString(this.homePhone);
        __os.writeString(this.officePhone);
        __os.writeInt(this.defaultIM);
        __os.writeInt(this.defaultPhoneNumber);
        __os.writeInt(this.contactGroupId);
        __os.writeInt(this.shareMobilePhone);
        __os.writeInt(this.displayOnPhone);
        __os.writeInt(this.status);
        __os.writeInt(this.fusionPresence);
        __os.writeInt(this.msnPresence);
        __os.writeInt(this.aimPresence);
        __os.writeInt(this.yahooPresence);
        __os.writeInt(this.facebookPresence);
        __os.writeInt(this.gtalkPresence);
        __os.writeString(this.displayPicture);
        __os.writeString(this.statusMessage);
        __os.writeLong(this.statusTimeStamp);
    }

    public void __read(BasicStream __is) {
        this.id = __is.readInt();
        this.username = __is.readString();
        this.displayName = __is.readString();
        this.firstName = __is.readString();
        this.lastName = __is.readString();
        this.fusionUsername = __is.readString();
        this.msnUsername = __is.readString();
        this.aimUsername = __is.readString();
        this.yahooUsername = __is.readString();
        this.facebookUsername = __is.readString();
        this.gtalkUsername = __is.readString();
        this.emailAddress = __is.readString();
        this.mobilePhone = __is.readString();
        this.homePhone = __is.readString();
        this.officePhone = __is.readString();
        this.defaultIM = __is.readInt();
        this.defaultPhoneNumber = __is.readInt();
        this.contactGroupId = __is.readInt();
        this.shareMobilePhone = __is.readInt();
        this.displayOnPhone = __is.readInt();
        this.status = __is.readInt();
        this.fusionPresence = __is.readInt();
        this.msnPresence = __is.readInt();
        this.aimPresence = __is.readInt();
        this.yahooPresence = __is.readInt();
        this.facebookPresence = __is.readInt();
        this.gtalkPresence = __is.readInt();
        this.displayPicture = __is.readString();
        this.statusMessage = __is.readString();
        this.statusTimeStamp = __is.readLong();
    }
}

