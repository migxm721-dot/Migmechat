/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import com.projectgoth.fusion.slice.StringArrayHelper;
import java.io.Serializable;
import java.util.Arrays;

public final class ChatDefinitionIce
implements Cloneable,
Serializable {
    public String chatStorageID;
    public String[] participantUsernames;
    public byte chatType;
    public int unreadMessageCount;
    public int contactID;
    public String groupOwner;
    public byte isClosedChat;
    public String displayGUID;
    public byte messageType;
    public String chatName;
    public byte isPassivatedChat;

    public ChatDefinitionIce() {
    }

    public ChatDefinitionIce(String chatStorageID, String[] participantUsernames, byte chatType, int unreadMessageCount, int contactID, String groupOwner, byte isClosedChat, String displayGUID, byte messageType, String chatName, byte isPassivatedChat) {
        this.chatStorageID = chatStorageID;
        this.participantUsernames = participantUsernames;
        this.chatType = chatType;
        this.unreadMessageCount = unreadMessageCount;
        this.contactID = contactID;
        this.groupOwner = groupOwner;
        this.isClosedChat = isClosedChat;
        this.displayGUID = displayGUID;
        this.messageType = messageType;
        this.chatName = chatName;
        this.isPassivatedChat = isPassivatedChat;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        ChatDefinitionIce _r = null;
        try {
            _r = (ChatDefinitionIce)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (this.chatStorageID != _r.chatStorageID && this.chatStorageID != null && !this.chatStorageID.equals(_r.chatStorageID)) {
                return false;
            }
            if (!Arrays.equals(this.participantUsernames, _r.participantUsernames)) {
                return false;
            }
            if (this.chatType != _r.chatType) {
                return false;
            }
            if (this.unreadMessageCount != _r.unreadMessageCount) {
                return false;
            }
            if (this.contactID != _r.contactID) {
                return false;
            }
            if (this.groupOwner != _r.groupOwner && this.groupOwner != null && !this.groupOwner.equals(_r.groupOwner)) {
                return false;
            }
            if (this.isClosedChat != _r.isClosedChat) {
                return false;
            }
            if (this.displayGUID != _r.displayGUID && this.displayGUID != null && !this.displayGUID.equals(_r.displayGUID)) {
                return false;
            }
            if (this.messageType != _r.messageType) {
                return false;
            }
            if (this.chatName != _r.chatName && this.chatName != null && !this.chatName.equals(_r.chatName)) {
                return false;
            }
            return this.isPassivatedChat == _r.isPassivatedChat;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        if (this.chatStorageID != null) {
            __h = 5 * __h + this.chatStorageID.hashCode();
        }
        if (this.participantUsernames != null) {
            for (int __i0 = 0; __i0 < this.participantUsernames.length; ++__i0) {
                if (this.participantUsernames[__i0] == null) continue;
                __h = 5 * __h + this.participantUsernames[__i0].hashCode();
            }
        }
        __h = 5 * __h + this.chatType;
        __h = 5 * __h + this.unreadMessageCount;
        __h = 5 * __h + this.contactID;
        if (this.groupOwner != null) {
            __h = 5 * __h + this.groupOwner.hashCode();
        }
        __h = 5 * __h + this.isClosedChat;
        if (this.displayGUID != null) {
            __h = 5 * __h + this.displayGUID.hashCode();
        }
        __h = 5 * __h + this.messageType;
        if (this.chatName != null) {
            __h = 5 * __h + this.chatName.hashCode();
        }
        __h = 5 * __h + this.isPassivatedChat;
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
        __os.writeString(this.chatStorageID);
        StringArrayHelper.write(__os, this.participantUsernames);
        __os.writeByte(this.chatType);
        __os.writeInt(this.unreadMessageCount);
        __os.writeInt(this.contactID);
        __os.writeString(this.groupOwner);
        __os.writeByte(this.isClosedChat);
        __os.writeString(this.displayGUID);
        __os.writeByte(this.messageType);
        __os.writeString(this.chatName);
        __os.writeByte(this.isPassivatedChat);
    }

    public void __read(BasicStream __is) {
        this.chatStorageID = __is.readString();
        this.participantUsernames = StringArrayHelper.read(__is);
        this.chatType = __is.readByte();
        this.unreadMessageCount = __is.readInt();
        this.contactID = __is.readInt();
        this.groupOwner = __is.readString();
        this.isClosedChat = __is.readByte();
        this.displayGUID = __is.readString();
        this.messageType = __is.readByte();
        this.chatName = __is.readString();
        this.isPassivatedChat = __is.readByte();
    }
}

