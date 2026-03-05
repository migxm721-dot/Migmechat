/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

public final class EventPrivacySettingIce
implements Cloneable,
Serializable {
    public boolean statusUpdates;
    public boolean profileChanges;
    public boolean addFriends;
    public boolean photosPublished;
    public boolean contentPurchased;
    public boolean chatroomCreation;
    public boolean virtualGifting;

    public EventPrivacySettingIce() {
    }

    public EventPrivacySettingIce(boolean statusUpdates, boolean profileChanges, boolean addFriends, boolean photosPublished, boolean contentPurchased, boolean chatroomCreation, boolean virtualGifting) {
        this.statusUpdates = statusUpdates;
        this.profileChanges = profileChanges;
        this.addFriends = addFriends;
        this.photosPublished = photosPublished;
        this.contentPurchased = contentPurchased;
        this.chatroomCreation = chatroomCreation;
        this.virtualGifting = virtualGifting;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        EventPrivacySettingIce _r = null;
        try {
            _r = (EventPrivacySettingIce)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.statusUpdates != _r.statusUpdates) {
                return false;
            }
            if (this.profileChanges != _r.profileChanges) {
                return false;
            }
            if (this.addFriends != _r.addFriends) {
                return false;
            }
            if (this.photosPublished != _r.photosPublished) {
                return false;
            }
            if (this.contentPurchased != _r.contentPurchased) {
                return false;
            }
            if (this.chatroomCreation != _r.chatroomCreation) {
                return false;
            }
            return this.virtualGifting == _r.virtualGifting;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        __h = 5 * __h + (this.statusUpdates ? 1 : 0);
        __h = 5 * __h + (this.profileChanges ? 1 : 0);
        __h = 5 * __h + (this.addFriends ? 1 : 0);
        __h = 5 * __h + (this.photosPublished ? 1 : 0);
        __h = 5 * __h + (this.contentPurchased ? 1 : 0);
        __h = 5 * __h + (this.chatroomCreation ? 1 : 0);
        __h = 5 * __h + (this.virtualGifting ? 1 : 0);
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
        __os.writeBool(this.statusUpdates);
        __os.writeBool(this.profileChanges);
        __os.writeBool(this.addFriends);
        __os.writeBool(this.photosPublished);
        __os.writeBool(this.contentPurchased);
        __os.writeBool(this.chatroomCreation);
        __os.writeBool(this.virtualGifting);
    }

    public void __read(BasicStream __is) {
        this.statusUpdates = __is.readBool();
        this.profileChanges = __is.readBool();
        this.addFriends = __is.readBool();
        this.photosPublished = __is.readBool();
        this.contentPurchased = __is.readBool();
        this.chatroomCreation = __is.readBool();
        this.virtualGifting = __is.readBool();
    }
}

