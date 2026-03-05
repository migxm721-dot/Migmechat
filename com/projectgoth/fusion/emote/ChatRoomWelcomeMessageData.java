/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.fdl.enums.MessageType;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatRoomWelcomeMessageData
extends MessageData {
    public static final String CHATROOM_WELCOME_MESSAGE_EMOTE_HOTKEY = "(chatwelcomemessage)";
    public static final int CHATROOM_WELCOME_MESSAGE_COLOUR = 0xFF8800;

    public ChatRoomWelcomeMessageData(String chatRoomName, String msg) {
        int messageColor = SystemProperty.getInt("ChatroomWelcomeColor", 0xFF8800);
        String message = String.format("%s %s", CHATROOM_WELCOME_MESSAGE_EMOTE_HOTKEY, msg);
        MessageDestinationData destinationData = new MessageDestinationData();
        destinationData.type = MessageDestinationData.TypeEnum.CHAT_ROOM;
        destinationData.destination = chatRoomName;
        this.type = MessageType.FUSION;
        this.sendReceive = MessageData.SendReceiveEnum.SEND;
        this.source = chatRoomName;
        this.sourceType = MessageData.SourceTypeEnum.CHATROOM;
        this.messageText = message;
        this.messageDestinations = new ArrayList();
        this.messageDestinations.add(destinationData);
        this.emoticonKeys = Arrays.asList(CHATROOM_WELCOME_MESSAGE_EMOTE_HOTKEY);
        this.contentType = MessageData.ContentTypeEnum.EMOTE;
        this.messageColour = messageColor != -1 ? messageColor : 0xFF8800;
    }
}

