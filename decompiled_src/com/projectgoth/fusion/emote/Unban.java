/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.Enums;
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

public class Unban
extends EmoteCommand {
    public Unban(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] args = messageData.getArgs();
        if (args.length < 3) {
            throw new FusionException("Usage: /unban [username] " + Enums.GroupUnbanReasonEnum.stringifyValues());
        }
        super.checkRateLimit(Unban.class, "s:" + messageData.source, "");
        int reasonCode = -1;
        try {
            reasonCode = Integer.parseInt(args[2]);
            if (!Enums.GroupUnbanReasonEnum.isValid(reasonCode)) {
                throw new FusionException("Please provide a valid reason code. Valid reason codes are: " + Enums.GroupUnbanReasonEnum.stringifyValues());
            }
        }
        catch (NumberFormatException nfe) {
            throw new FusionException("Please provide a valid reason code. Valid reason codes are: " + Enums.GroupUnbanReasonEnum.stringifyValues());
        }
        ChatRoomPrx chatRoomPrx = chatSource.castToLocalChatRoomChatSource().getChatRoomPrx();
        ChatRoomDataIce roomData = chatRoomPrx.getRoomData();
        super.checkRateLimit(Unban.class, "t:" + args[1] + ":" + roomData.id + ":" + roomData.groupID, SystemProperty.get(SystemPropertyEntities.Emote.UNBAN_RATE_LIMIT));
        chatRoomPrx.unbanGroupMember(args[1], messageData.source, reasonCode);
        if (SystemProperty.getBool(SystemPropertyEntities.Emote.LOG_ALL_EMOTES_ENABLED)) {
            ChatRoomEmoteLogData logData = new ChatRoomEmoteLogData(messageData.source, args[1], args[0], roomData.id, roomData.groupID, reasonCode, null);
            chatSource.getSessionI().logEmoteData(logData);
        }
        return EmoteCommand.ResultType.HANDLED_AND_STOP;
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }
}

