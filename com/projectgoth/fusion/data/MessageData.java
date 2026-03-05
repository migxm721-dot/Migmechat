/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageDestinationDataIce;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MessageData
implements Serializable {
    public Integer id;
    public String username;
    public Date dateCreated;
    public Date requestReceivedTimestamp;
    public MessageType type;
    public String messageText;
    public Integer messageColour;
    public ContentTypeEnum contentType;
    public byte[] binaryData;
    public SendReceiveEnum sendReceive;
    public Integer sourceContactID;
    public String source;
    public SourceTypeEnum sourceType;
    public String sourceDisplayPicture;
    public Integer sourceColour;
    public Boolean fromAdministrator;
    public List<String> emoticonKeys;
    public List<MessageDestinationData> messageDestinations;
    public String guid;
    public Long messageTimestamp;
    public String groupChatName;
    public String groupChatOwner;
    public EmoteContentTypeEnum emoteContentType;
    public String mimeType;
    public String mimeTypeData;

    public MessageData() {
    }

    public MessageData(MessageData cloneFrom) {
        this(cloneFrom.toIceObject());
    }

    public MessageData(MessageDataIce messageIce) {
        this.id = messageIce.id == Integer.MIN_VALUE ? null : Integer.valueOf(messageIce.id);
        this.username = messageIce.username.equals("\u0000") ? null : messageIce.username;
        this.dateCreated = messageIce.dateCreated == Long.MIN_VALUE ? null : new Date(messageIce.dateCreated);
        this.requestReceivedTimestamp = messageIce.requestReceivedTimestamp == Long.MIN_VALUE ? null : new Date(messageIce.requestReceivedTimestamp);
        this.type = messageIce.type == Integer.MIN_VALUE ? null : MessageType.fromValue(messageIce.type);
        this.messageText = messageIce.messageText.equals("\u0000") ? null : messageIce.messageText;
        this.messageColour = messageIce.messageColour == Integer.MIN_VALUE ? null : Integer.valueOf(messageIce.messageColour);
        this.contentType = messageIce.contentType == Integer.MIN_VALUE ? null : ContentTypeEnum.fromValue(messageIce.contentType);
        this.binaryData = messageIce.binaryData.length == 0 ? null : messageIce.binaryData;
        this.sendReceive = messageIce.sendReceive == Integer.MIN_VALUE ? null : SendReceiveEnum.fromValue(messageIce.sendReceive);
        this.sourceContactID = messageIce.sourceContactID == Integer.MIN_VALUE ? null : Integer.valueOf(messageIce.sourceContactID);
        this.source = messageIce.source.equals("\u0000") ? null : messageIce.source;
        this.sourceType = messageIce.sourceType == Integer.MIN_VALUE ? null : SourceTypeEnum.fromValue(messageIce.sourceType);
        this.sourceDisplayPicture = messageIce.sourceDisplayPicture.equals("\u0000") ? null : messageIce.sourceDisplayPicture;
        Integer n = this.sourceColour = messageIce.sourceColour == Integer.MIN_VALUE ? null : Integer.valueOf(messageIce.sourceColour);
        Boolean bl = messageIce.fromAdministrator == Integer.MIN_VALUE ? null : (this.fromAdministrator = Boolean.valueOf(messageIce.fromAdministrator == 1));
        if (messageIce.emoticonKeys != null) {
            for (String emoticonKey : messageIce.emoticonKeys) {
                if (this.emoticonKeys == null) {
                    this.emoticonKeys = new LinkedList<String>();
                }
                this.emoticonKeys.add(emoticonKey);
            }
        }
        if (messageIce.messageDestinations != null) {
            for (MessageDestinationDataIce messageDestIce : messageIce.messageDestinations) {
                if (this.messageDestinations == null) {
                    this.messageDestinations = new LinkedList<MessageDestinationData>();
                }
                this.messageDestinations.add(new MessageDestinationData(messageDestIce));
            }
        }
        this.guid = messageIce.guid == null || messageIce.guid.equals("\u0000") ? null : messageIce.guid;
        this.messageTimestamp = messageIce.messageTimestamp == Long.MIN_VALUE ? null : Long.valueOf(messageIce.messageTimestamp);
        this.groupChatName = messageIce.groupChatName == null || messageIce.groupChatName.equals("\u0000") ? null : messageIce.groupChatName;
        this.groupChatOwner = messageIce.groupChatOwner == null || messageIce.groupChatOwner.equals("\u0000") ? null : messageIce.groupChatOwner;
        this.emoteContentType = EmoteContentTypeEnum.fromValue(messageIce.emoteContentType);
        if (SystemProperty.getBool(SystemPropertyEntities.CoreChatSettings.MIME_TYPE_MESSAGES_ENABLED)) {
            this.mimeType = messageIce.mimeType.equals("\u0000") ? null : messageIce.mimeType;
            this.mimeTypeData = messageIce.mimeTypeData.equals("\u0000") ? null : messageIce.mimeTypeData;
        }
    }

    public MessageDataIce toIceObject() {
        int i;
        MessageDataIce messageIce = new MessageDataIce();
        messageIce.id = this.id == null ? Integer.MIN_VALUE : this.id;
        messageIce.username = this.username == null ? "\u0000" : this.username;
        messageIce.dateCreated = this.dateCreated == null ? Long.MIN_VALUE : this.dateCreated.getTime();
        messageIce.requestReceivedTimestamp = this.requestReceivedTimestamp == null ? Long.MIN_VALUE : this.requestReceivedTimestamp.getTime();
        messageIce.type = this.type == null ? Integer.MIN_VALUE : (int)this.type.value();
        messageIce.messageText = this.messageText == null ? "\u0000" : this.messageText;
        messageIce.messageColour = this.messageColour == null ? Integer.MIN_VALUE : this.messageColour;
        messageIce.contentType = this.contentType == null ? Integer.MIN_VALUE : this.contentType.value();
        messageIce.binaryData = this.binaryData == null ? new byte[]{} : this.binaryData;
        messageIce.sendReceive = this.sendReceive == null ? Integer.MIN_VALUE : this.sendReceive.value();
        messageIce.sourceContactID = this.sourceContactID == null ? Integer.MIN_VALUE : this.sourceContactID;
        messageIce.source = this.source == null ? "\u0000" : this.source;
        messageIce.sourceType = this.sourceType == null ? Integer.MIN_VALUE : this.sourceType.value();
        messageIce.sourceDisplayPicture = this.sourceDisplayPicture == null ? "\u0000" : this.sourceDisplayPicture;
        int n = messageIce.sourceColour = this.sourceColour == null ? Integer.MIN_VALUE : this.sourceColour;
        int n2 = this.fromAdministrator == null ? Integer.MIN_VALUE : (messageIce.fromAdministrator = this.fromAdministrator != false ? 1 : 0);
        if (this.emoticonKeys != null && this.emoticonKeys.size() > 0) {
            messageIce.emoticonKeys = new String[this.emoticonKeys.size()];
            for (i = 0; i < this.emoticonKeys.size(); ++i) {
                messageIce.emoticonKeys[i] = this.emoticonKeys.get(i);
            }
        }
        if (this.messageDestinations != null && this.messageDestinations.size() > 0) {
            messageIce.messageDestinations = new MessageDestinationDataIce[this.messageDestinations.size()];
            for (i = 0; i < this.messageDestinations.size(); ++i) {
                messageIce.messageDestinations[i] = this.messageDestinations.get(i).toIceObject();
            }
        }
        messageIce.guid = this.guid == null ? "\u0000" : this.guid;
        messageIce.messageTimestamp = this.messageTimestamp == null ? Long.MIN_VALUE : this.messageTimestamp;
        messageIce.groupChatName = this.groupChatName == null ? "\u0000" : this.groupChatName;
        messageIce.groupChatOwner = this.groupChatOwner == null ? "\u0000" : this.groupChatOwner;
        byte by = messageIce.emoteContentType = this.emoteContentType != null ? this.emoteContentType.value() : EmoteContentTypeEnum.PLAIN.value();
        if (SystemProperty.getBool(SystemPropertyEntities.CoreChatSettings.MIME_TYPE_MESSAGES_ENABLED)) {
            messageIce.mimeType = this.mimeType == null ? "\u0000" : this.mimeType;
            messageIce.mimeTypeData = this.mimeTypeData == null ? "\u0000" : this.mimeTypeData;
        }
        return messageIce;
    }

    public boolean isSystemMessage() {
        return this.sourceType.value() >= SourceTypeEnum.SYSTEM_GENERAL.value();
    }

    public static boolean isSystemMessage(MessageDataIce message) {
        return message.sourceType >= SourceTypeEnum.SYSTEM_GENERAL.value();
    }

    public static boolean isFusionMessage(MessageDataIce message) {
        return message.type == MessageType.FUSION.value();
    }

    public static boolean isMessageToAChatRoom(MessageDataIce message) {
        return message.messageDestinations[0].type == MessageDestinationData.TypeEnum.CHAT_ROOM.value();
    }

    public static boolean isMessageToAGroupChat(MessageDataIce message) {
        return message.messageDestinations[0].type == MessageDestinationData.TypeEnum.GROUP.value();
    }

    public static boolean isMessageToAnIndividual(MessageDataIce message) {
        return message.messageDestinations[0].type == MessageDestinationData.TypeEnum.INDIVIDUAL.value();
    }

    public boolean isMessageToAnIndividual() {
        return this.messageDestinations.get((int)0).type == MessageDestinationData.TypeEnum.INDIVIDUAL;
    }

    public static boolean hasDestinations(MessageDataIce message) {
        return message.messageDestinations != null && message.messageDestinations.length > 0;
    }

    public static MessageData newChatRoomMessage(String chatRoomName, String message, String[] emoticonKeys) {
        MessageDestinationData destinationData = new MessageDestinationData();
        destinationData.type = MessageDestinationData.TypeEnum.CHAT_ROOM;
        destinationData.destination = chatRoomName;
        MessageData messageData = new MessageData();
        messageData.type = MessageType.FUSION;
        messageData.sendReceive = SendReceiveEnum.SEND;
        messageData.source = chatRoomName;
        messageData.sourceType = SourceTypeEnum.CHATROOM;
        messageData.contentType = ContentTypeEnum.TEXT;
        messageData.messageText = message;
        messageData.messageDestinations = new ArrayList<MessageDestinationData>();
        messageData.messageDestinations.add(destinationData);
        if (emoticonKeys != null) {
            messageData.emoticonKeys = Arrays.asList(emoticonKeys);
        }
        return messageData;
    }

    public static String toString(MessageDataIce i) {
        return new MessageData(i).toString();
    }

    private String nullOrString(Object o) {
        return o == null ? "null" : o.toString();
    }

    private String nullOrStringForColor(Integer c) {
        return c == null ? "null" : String.format("%06X", c);
    }

    public String toString() {
        ArrayList<String> mdl;
        String binaryDataStr = MessageData.convertByteArrayToString(this.binaryData);
        if (this.messageDestinations != null && this.messageDestinations.size() > 0) {
            mdl = new ArrayList(this.messageDestinations == null ? 0 : this.messageDestinations.size());
            for (MessageDestinationData md : this.messageDestinations) {
                mdl.add(md.toString());
            }
        } else {
            mdl = new ArrayList<String>();
        }
        return String.format("MsgData: id=%s, username=%s, dateCreated=%s, reqReceivedTS=%s, type=%s, messageText=%s, messageColour=%s, contentType=%s, binaryData=%s, sendReceive=%s, sourceContactID=%s, source=%s, sourceType=%s, sourceDisplayPicture=%s, sourceColour=%s, fromAdministrator=%s, emoticonKeys=[%s], messageDestinations=[%s], mimeType=[%s], mimeTypeData=[%s]", this.nullOrString(this.id), this.username, this.dateCreated, this.requestReceivedTimestamp, this.nullOrString((Object)this.type), this.messageText, this.nullOrStringForColor(this.messageColour), this.nullOrString((Object)this.contentType), binaryDataStr, this.nullOrString((Object)this.sendReceive), this.nullOrString(this.sourceContactID), this.source, this.nullOrString((Object)this.sourceType), this.sourceDisplayPicture, this.nullOrStringForColor(this.sourceColour), this.nullOrString(this.fromAdministrator), StringUtil.join(this.emoticonKeys, ","), StringUtil.join(mdl, ","), this.nullOrString(this.mimeType), this.nullOrString(this.mimeTypeData));
    }

    public String[] getArgs() {
        return this.messageText.toLowerCase().split(" ");
    }

    private static String convertByteArrayToString(byte[] data) {
        String str = "";
        if (data != null && data.length > 0) {
            StringBuilder sb = new StringBuilder(2 * data.length);
            for (byte b : data) {
                sb.append(String.format("%02X", b));
            }
            str = sb.toString();
        }
        return str;
    }

    public void clearMimeTypeAndData() {
        if (!SystemProperty.getBool(SystemPropertyEntities.CoreChatSettings.MIME_TYPE_MESSAGES_ENABLED)) {
            return;
        }
        this.mimeType = null;
        this.mimeTypeData = null;
    }

    public void setMimeTypeAndData(VirtualGiftData gift, String sender, List<String> allRecipients, VirtualGiftData.GiftingType giftingType, String giftMessage) {
        if (!SystemProperty.getBool(SystemPropertyEntities.CoreChatSettings.MIME_TYPE_MESSAGES_ENABLED)) {
            return;
        }
        if (gift == null || StringUtil.isBlank(sender) || allRecipients == null || giftingType == null) {
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("name", (Object)gift.getName());
            json.put("hotkey", (Object)gift.getHotKey());
            json.put("type", (Object)giftingType.name());
            json.put("message", (Object)giftMessage);
            json.put("sender", (Object)sender);
            json.put("recipient", (Object)StringUtil.join(allRecipients, ","));
        }
        catch (JSONException e) {
            // empty catch block
        }
        this.mimeType = "emote/gift";
        this.mimeTypeData = json.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum SendReceiveEnum {
        SEND(1),
        RECEIVE(2);

        private int value;

        private SendReceiveEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static SendReceiveEnum fromValue(int value) {
            for (SendReceiveEnum e : SendReceiveEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum SourceTypeEnum {
        USER(1, Integer.MIN_VALUE),
        ADMIN_USER(2, Integer.MIN_VALUE),
        GROUP_ADMIN_USER(3, 16565508),
        MODERATOR_USER(4, 16565508),
        SYSTEM_GENERAL(10, Integer.MIN_VALUE),
        CHATROOM(11, Integer.MIN_VALUE),
        TOP_MERCHANT_LVL1(12, 0x990099),
        TOP_MERCHANT_LVL2(13, 16723623),
        TOP_MERCHANT_LVL3(15, 0xFF0000),
        BOT(16, 8112384),
        GLOBAL_ADMIN(17, 16020514);

        private int value;
        private int colorHex;

        private SourceTypeEnum(int value, int colorHex) {
            this.value = value;
            this.colorHex = colorHex;
        }

        public int value() {
            return this.value;
        }

        public int colorHex() {
            return this.colorHex;
        }

        public static SourceTypeEnum fromValue(int value) {
            for (SourceTypeEnum e : SourceTypeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ContentTypeEnum {
        TEXT(1),
        IMAGE(2),
        AUDIO(3),
        VIDEO(4),
        EXISTING_FILE(5),
        EMOTE(6);

        private int value;

        private ContentTypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static ContentTypeEnum fromValue(int value) {
            for (ContentTypeEnum e : ContentTypeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EmoteContentTypeEnum implements EnumUtils.IEnumValueGetter<Byte>
    {
        PLAIN((byte)0),
        STICKERS((byte)1);

        private Byte value;

        private EmoteContentTypeEnum(Byte value) {
            this.value = value;
        }

        public Byte getEnumValue() {
            return this.value;
        }

        public static EmoteContentTypeEnum fromValue(byte value) {
            return SingletonHolder.lookupByCode.get(value);
        }

        public byte value() {
            return this.value;
        }

        private static class SingletonHolder {
            public static final HashMap<Byte, EmoteContentTypeEnum> lookupByCode = (HashMap)EnumUtils.buildLookUpMap(new HashMap(), EmoteContentTypeEnum.class);

            private SingletonHolder() {
            }
        }
    }
}

