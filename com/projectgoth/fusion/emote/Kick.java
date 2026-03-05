/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatRoomEmoteLogData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;

public class Kick
extends EmoteCommand {
    private static final String KICK_USAGE_STR = "Usage: /kick [username]";
    private static final String KICK_CLEAR_USAGE_STR = "Usage: /kick clear [username] or /kick c [username]";

    public Kick(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        boolean isClearKickCommand;
        String[] args = messageData.getArgs();
        if (args.length == 1) {
            throw new FusionException(KICK_USAGE_STR);
        }
        boolean bl = isClearKickCommand = args[1].equals("clear") || args[1].equals("c");
        if (isClearKickCommand) {
            if (!SystemProperty.getBool(SystemPropertyEntities.Emote.KICK_CLEAR_ENABLED)) {
                throw new FusionException("The command /kick clear is currently not available");
            }
            if (args.length != 3) throw new FusionException(KICK_CLEAR_USAGE_STR);
            String rateLimitPerUser = SystemProperty.get(SystemPropertyEntities.Emote.KICK_CLEAR_RATE_LIMIT);
            super.checkRateLimit(Kick.class, "s:" + messageData.source + ":kc", rateLimitPerUser);
            ChatRoomPrx chatRoomPrx = chatSource.castToLocalChatRoomChatSource().getChatRoomPrx();
            chatRoomPrx.clearUserKick(messageData.source, args[2]);
            if (!SystemProperty.getBool(SystemPropertyEntities.Emote.LOG_ALL_EMOTES_ENABLED)) return EmoteCommand.ResultType.HANDLED_AND_STOP;
            ChatRoomDataIce roomData = chatRoomPrx.getRoomData();
            ChatRoomEmoteLogData logData = new ChatRoomEmoteLogData(messageData.source, args[1], args[0], roomData.id, roomData.groupID, -1, null);
            chatSource.getSessionI().logEmoteData(logData);
            return EmoteCommand.ResultType.HANDLED_AND_STOP;
        } else {
            String rateLimitPerUser = SystemProperty.get("KickPerInstigatorRateLimitExpr", "60/1M");
            super.checkRateLimit(Kick.class, "s:" + messageData.source, rateLimitPerUser);
            ChatRoomPrx chatRoomPrx = chatSource.castToLocalChatRoomChatSource().getChatRoomPrx();
            ChatRoomDataIce roomData = chatRoomPrx.getRoomData();
            String rateLimitPerChatroom = SystemProperty.get("KickPerChatroomRateLimitExpr", "5/1S");
            super.checkRateLimit(Kick.class, "c:" + roomData.id, rateLimitPerChatroom);
            chatRoomPrx.voteToKickUser(messageData.source, args[1]);
            if (!SystemProperty.getBool(SystemPropertyEntities.Emote.LOG_ALL_EMOTES_ENABLED)) return EmoteCommand.ResultType.HANDLED_AND_STOP;
            ChatRoomEmoteLogData logData = new ChatRoomEmoteLogData(messageData.source, args[1], args[0], roomData.id, roomData.groupID, -1, null);
            chatSource.getSessionI().logEmoteData(logData);
        }
        return EmoteCommand.ResultType.HANDLED_AND_STOP;
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }
}

