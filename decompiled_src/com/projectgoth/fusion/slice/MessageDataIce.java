/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import com.projectgoth.fusion.slice.ByteArrayHelper;
import com.projectgoth.fusion.slice.MessageDestinationDataIce;
import com.projectgoth.fusion.slice.MessageDestinationSequenceHelper;
import com.projectgoth.fusion.slice.StringArrayHelper;
import java.io.Serializable;
import java.util.Arrays;

public final class MessageDataIce
implements Cloneable,
Serializable {
    public int id;
    public String username;
    public long dateCreated;
    public long requestReceivedTimestamp;
    public int type;
    public String messageText;
    public int messageColour;
    public int contentType;
    public byte[] binaryData;
    public int sendReceive;
    public int sourceContactID;
    public String source;
    public int sourceType;
    public String sourceDisplayPicture;
    public int sourceColour;
    public int fromAdministrator;
    public String[] emoticonKeys;
    public MessageDestinationDataIce[] messageDestinations;
    public String guid;
    public long messageTimestamp;
    public String groupChatName;
    public String groupChatOwner;
    public byte emoteContentType;
    public String mimeType;
    public String mimeTypeData;

    public MessageDataIce() {
    }

    public MessageDataIce(int id, String username, long dateCreated, long requestReceivedTimestamp, int type, String messageText, int messageColour, int contentType, byte[] binaryData, int sendReceive, int sourceContactID, String source, int sourceType, String sourceDisplayPicture, int sourceColour, int fromAdministrator, String[] emoticonKeys, MessageDestinationDataIce[] messageDestinations, String guid, long messageTimestamp, String groupChatName, String groupChatOwner, byte emoteContentType, String mimeType, String mimeTypeData) {
        this.id = id;
        this.username = username;
        this.dateCreated = dateCreated;
        this.requestReceivedTimestamp = requestReceivedTimestamp;
        this.type = type;
        this.messageText = messageText;
        this.messageColour = messageColour;
        this.contentType = contentType;
        this.binaryData = binaryData;
        this.sendReceive = sendReceive;
        this.sourceContactID = sourceContactID;
        this.source = source;
        this.sourceType = sourceType;
        this.sourceDisplayPicture = sourceDisplayPicture;
        this.sourceColour = sourceColour;
        this.fromAdministrator = fromAdministrator;
        this.emoticonKeys = emoticonKeys;
        this.messageDestinations = messageDestinations;
        this.guid = guid;
        this.messageTimestamp = messageTimestamp;
        this.groupChatName = groupChatName;
        this.groupChatOwner = groupChatOwner;
        this.emoteContentType = emoteContentType;
        this.mimeType = mimeType;
        this.mimeTypeData = mimeTypeData;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        MessageDataIce _r = null;
        try {
            _r = (MessageDataIce)rhs;
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
            if (this.requestReceivedTimestamp != _r.requestReceivedTimestamp) {
                return false;
            }
            if (this.type != _r.type) {
                return false;
            }
            if (this.messageText != _r.messageText && this.messageText != null && !this.messageText.equals(_r.messageText)) {
                return false;
            }
            if (this.messageColour != _r.messageColour) {
                return false;
            }
            if (this.contentType != _r.contentType) {
                return false;
            }
            if (!Arrays.equals(this.binaryData, _r.binaryData)) {
                return false;
            }
            if (this.sendReceive != _r.sendReceive) {
                return false;
            }
            if (this.sourceContactID != _r.sourceContactID) {
                return false;
            }
            if (this.source != _r.source && this.source != null && !this.source.equals(_r.source)) {
                return false;
            }
            if (this.sourceType != _r.sourceType) {
                return false;
            }
            if (this.sourceDisplayPicture != _r.sourceDisplayPicture && this.sourceDisplayPicture != null && !this.sourceDisplayPicture.equals(_r.sourceDisplayPicture)) {
                return false;
            }
            if (this.sourceColour != _r.sourceColour) {
                return false;
            }
            if (this.fromAdministrator != _r.fromAdministrator) {
                return false;
            }
            if (!Arrays.equals(this.emoticonKeys, _r.emoticonKeys)) {
                return false;
            }
            if (!Arrays.equals(this.messageDestinations, _r.messageDestinations)) {
                return false;
            }
            if (this.guid != _r.guid && this.guid != null && !this.guid.equals(_r.guid)) {
                return false;
            }
            if (this.messageTimestamp != _r.messageTimestamp) {
                return false;
            }
            if (this.groupChatName != _r.groupChatName && this.groupChatName != null && !this.groupChatName.equals(_r.groupChatName)) {
                return false;
            }
            if (this.groupChatOwner != _r.groupChatOwner && this.groupChatOwner != null && !this.groupChatOwner.equals(_r.groupChatOwner)) {
                return false;
            }
            if (this.emoteContentType != _r.emoteContentType) {
                return false;
            }
            if (this.mimeType != _r.mimeType && this.mimeType != null && !this.mimeType.equals(_r.mimeType)) {
                return false;
            }
            return this.mimeTypeData == _r.mimeTypeData || this.mimeTypeData == null || this.mimeTypeData.equals(_r.mimeTypeData);
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
        __h = 5 * __h + (int)this.requestReceivedTimestamp;
        __h = 5 * __h + this.type;
        if (this.messageText != null) {
            __h = 5 * __h + this.messageText.hashCode();
        }
        __h = 5 * __h + this.messageColour;
        __h = 5 * __h + this.contentType;
        if (this.binaryData != null) {
            for (int __i0 = 0; __i0 < this.binaryData.length; ++__i0) {
                __h = 5 * __h + this.binaryData[__i0];
            }
        }
        __h = 5 * __h + this.sendReceive;
        __h = 5 * __h + this.sourceContactID;
        if (this.source != null) {
            __h = 5 * __h + this.source.hashCode();
        }
        __h = 5 * __h + this.sourceType;
        if (this.sourceDisplayPicture != null) {
            __h = 5 * __h + this.sourceDisplayPicture.hashCode();
        }
        __h = 5 * __h + this.sourceColour;
        __h = 5 * __h + this.fromAdministrator;
        if (this.emoticonKeys != null) {
            for (int __i1 = 0; __i1 < this.emoticonKeys.length; ++__i1) {
                if (this.emoticonKeys[__i1] == null) continue;
                __h = 5 * __h + this.emoticonKeys[__i1].hashCode();
            }
        }
        if (this.messageDestinations != null) {
            for (int __i2 = 0; __i2 < this.messageDestinations.length; ++__i2) {
                if (this.messageDestinations[__i2] == null) continue;
                __h = 5 * __h + this.messageDestinations[__i2].hashCode();
            }
        }
        if (this.guid != null) {
            __h = 5 * __h + this.guid.hashCode();
        }
        __h = 5 * __h + (int)this.messageTimestamp;
        if (this.groupChatName != null) {
            __h = 5 * __h + this.groupChatName.hashCode();
        }
        if (this.groupChatOwner != null) {
            __h = 5 * __h + this.groupChatOwner.hashCode();
        }
        __h = 5 * __h + this.emoteContentType;
        if (this.mimeType != null) {
            __h = 5 * __h + this.mimeType.hashCode();
        }
        if (this.mimeTypeData != null) {
            __h = 5 * __h + this.mimeTypeData.hashCode();
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
        __os.writeLong(this.requestReceivedTimestamp);
        __os.writeInt(this.type);
        __os.writeString(this.messageText);
        __os.writeInt(this.messageColour);
        __os.writeInt(this.contentType);
        ByteArrayHelper.write(__os, this.binaryData);
        __os.writeInt(this.sendReceive);
        __os.writeInt(this.sourceContactID);
        __os.writeString(this.source);
        __os.writeInt(this.sourceType);
        __os.writeString(this.sourceDisplayPicture);
        __os.writeInt(this.sourceColour);
        __os.writeInt(this.fromAdministrator);
        StringArrayHelper.write(__os, this.emoticonKeys);
        MessageDestinationSequenceHelper.write(__os, this.messageDestinations);
        __os.writeString(this.guid);
        __os.writeLong(this.messageTimestamp);
        __os.writeString(this.groupChatName);
        __os.writeString(this.groupChatOwner);
        __os.writeByte(this.emoteContentType);
        __os.writeString(this.mimeType);
        __os.writeString(this.mimeTypeData);
    }

    public void __read(BasicStream __is) {
        this.id = __is.readInt();
        this.username = __is.readString();
        this.dateCreated = __is.readLong();
        this.requestReceivedTimestamp = __is.readLong();
        this.type = __is.readInt();
        this.messageText = __is.readString();
        this.messageColour = __is.readInt();
        this.contentType = __is.readInt();
        this.binaryData = ByteArrayHelper.read(__is);
        this.sendReceive = __is.readInt();
        this.sourceContactID = __is.readInt();
        this.source = __is.readString();
        this.sourceType = __is.readInt();
        this.sourceDisplayPicture = __is.readString();
        this.sourceColour = __is.readInt();
        this.fromAdministrator = __is.readInt();
        this.emoticonKeys = StringArrayHelper.read(__is);
        this.messageDestinations = MessageDestinationSequenceHelper.read(__is);
        this.guid = __is.readString();
        this.messageTimestamp = __is.readLong();
        this.groupChatName = __is.readString();
        this.groupChatOwner = __is.readString();
        this.emoteContentType = __is.readByte();
        this.mimeType = __is.readString();
        this.mimeTypeData = __is.readString();
    }
}

