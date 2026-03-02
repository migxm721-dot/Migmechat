/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;

public class Silence
extends EmoteCommand {
    private static final String USAGE_STR = "Usage: /silence [timeout] or /silence [username] [timeout]";

    public Silence(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] args = messageData.getArgs();
        int timeout = -1;
        if (args.length == 2) {
            try {
                timeout = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                throw new FusionException(USAGE_STR);
            }
            int silenceTimeoutForChatroomMin = SystemProperty.getInt(SystemPropertyEntities.Emote.SILENCE_TIMEOUT_CHATROOM_MIN);
            int silenceTimeoutForChatroomMax = SystemProperty.getInt(SystemPropertyEntities.Emote.SILENCE_TIMEOUT_CHATROOM_MAX);
            if (timeout != 0 && (timeout < silenceTimeoutForChatroomMin || timeout > silenceTimeoutForChatroomMax)) {
                throw new FusionException("Timeout must be between " + silenceTimeoutForChatroomMin + " and " + silenceTimeoutForChatroomMax + " seconds");
            }
            String rateLimitPerUser = SystemProperty.get(SystemPropertyEntities.Emote.SILENCE_RATE_LIMIT);
            super.checkRateLimit(Silence.class, "s:" + messageData.source, rateLimitPerUser);
            ChatRoomPrx chatRoomPrx = chatSource.castToLocalChatRoomChatSource().getChatRoomPrx();
            chatRoomPrx.silence(messageData.source, timeout);
        } else if (args.length == 3) {
            String usernameToSilence = args[1];
            try {
                timeout = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException e) {
                throw new FusionException(USAGE_STR);
            }
            int silenceTimeoutForIndividualMin = SystemProperty.getInt(SystemPropertyEntities.Emote.SILENCE_TIMEOUT_INDIVIDUAL_MIN);
            int silenceTimeoutForIndividualMax = SystemProperty.getInt(SystemPropertyEntities.Emote.SILENCE_TIMEOUT_INDIVIDUAL_MAX);
            if (timeout != 0 && (timeout < silenceTimeoutForIndividualMin || timeout > silenceTimeoutForIndividualMax)) {
                throw new FusionException("Timeout must be between " + silenceTimeoutForIndividualMin + " and " + silenceTimeoutForIndividualMax + " seconds");
            }
            String rateLimitPerUser = SystemProperty.get(SystemPropertyEntities.Emote.SILENCE_RATE_LIMIT);
            super.checkRateLimit(Silence.class, "s:" + messageData.source, rateLimitPerUser);
            ChatRoomPrx chatRoomPrx = chatSource.castToLocalChatRoomChatSource().getChatRoomPrx();
            chatRoomPrx.silenceUser(messageData.source, usernameToSilence, timeout);
        } else {
            throw new FusionException(USAGE_STR);
        }
        return EmoteCommand.ResultType.HANDLED_AND_STOP;
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }
}

