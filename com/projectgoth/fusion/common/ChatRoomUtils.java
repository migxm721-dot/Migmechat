/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ChatRoomValidationException;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatRoomData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatRoomUtils {
    private static final Pattern CHATROOM_NAME_PATTERN = Pattern.compile("^[a-zA-Z]{1}[ \\w.-]{2,}$");
    private static final String BANNED_CHATROOM_NAME_SEP = ";";

    public static String normalizeChatRoomName(String chatRoomInput) {
        if (chatRoomInput == null) {
            return null;
        }
        return chatRoomInput.trim().toLowerCase();
    }

    public static void invalidateChatRoomCache(String chatRoomName) {
        ChatRoomData cached;
        if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.CHATROOM_SYNC_ENABLED) && (cached = (ChatRoomData)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CHATROOM, ChatRoomUtils.normalizeChatRoomName(chatRoomName))) != null) {
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BY_ID, Integer.toString(cached.id));
        }
        MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CHATROOM, ChatRoomUtils.normalizeChatRoomName(chatRoomName));
    }

    public static String validateChatRoomNameForCreation(String chatRoomName) throws ChatRoomValidationException {
        String trimmedChatRoomName = ChatRoomUtils.validateChatRoomNameCharacters(chatRoomName);
        ChatRoomUtils.validateChatRoomNameForBannedWords(trimmedChatRoomName);
        return trimmedChatRoomName;
    }

    public static String validateChatRoomNameForAccess(String chatRoomName) throws ChatRoomValidationException {
        String trimmedChatRoomName = ChatRoomUtils.validateChatRoomNameCharacters(chatRoomName);
        ChatRoomUtils.validateChatRoomNameNotInNegativeCache(trimmedChatRoomName);
        return trimmedChatRoomName;
    }

    private static String validateChatRoomNameCharacters(String chatRoomName) throws ChatRoomValidationException {
        if (chatRoomName == null) {
            throw new ChatRoomValidationException("The chat room name is not specified");
        }
        if (chatRoomName.length() == 0) {
            throw new ChatRoomValidationException("The chat room name is blank");
        }
        if (chatRoomName.length() > MemCachedClientWrapper.MAX_KEY_LENGTH) {
            throw new ChatRoomValidationException("The chat room name is too long");
        }
        String trimmedChatRoomName = chatRoomName.trim();
        Matcher matcher = CHATROOM_NAME_PATTERN.matcher(trimmedChatRoomName);
        if (!matcher.matches()) {
            throw new ChatRoomValidationException("Chat room names must start with a letter and contain any combination of at least 3 letters, numbers, periods (.), hyphens (-), underscores (_), or spaces");
        }
        int maxChatRoomNameLength = SystemProperty.getInt(SystemPropertyEntities.Chatroom.MAX_NAME_LENGTH);
        if (trimmedChatRoomName.length() > maxChatRoomNameLength) {
            throw new ChatRoomValidationException("The chat room name must not contain more than " + maxChatRoomNameLength + " characters");
        }
        return trimmedChatRoomName;
    }

    private static void validateChatRoomNameNotInNegativeCache(String chatRoomName) throws ChatRoomValidationException {
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.NEGATIVE_CACHE_ENABLED) && MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CHATROOM_NEGATIVE_CACHE, ChatRoomUtils.normalizeChatRoomName(chatRoomName)) != null) {
            throw new ChatRoomValidationException("Chat room is not available yet.");
        }
    }

    private static void validateChatRoomNameForBannedWords(String trimmedChatRoomName) throws ChatRoomValidationException {
        String[] bannedChatRoomNames;
        String sysprop = SystemProperty.get(SystemPropertyEntities.Chatroom.BANNED_NAMES);
        if (StringUtil.isBlank(sysprop)) {
            return;
        }
        String normalizedChatRoomName = ChatRoomUtils.normalizeChatRoomName(trimmedChatRoomName);
        for (String bannedChatRoomName : bannedChatRoomNames = sysprop.split(BANNED_CHATROOM_NAME_SEP)) {
            if (normalizedChatRoomName.indexOf(ChatRoomUtils.normalizeChatRoomName(bannedChatRoomName)) == -1) continue;
            throw new ChatRoomValidationException("You cannot use the word " + bannedChatRoomName + " in a chat room name");
        }
    }
}

