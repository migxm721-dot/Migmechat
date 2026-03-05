/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;

public class Reserve
extends EmoteCommand {
    public Reserve(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        int reserve;
        String[] args = messageData.getArgs();
        try {
            reserve = Integer.parseInt(args[1]);
        }
        catch (Exception e) {
            throw new FusionException("Invalid argument to /reserve");
        }
        ChatRoomPrx chatRoomPrx = chatSource.castToLocalChatRoomChatSource().getChatRoomPrx();
        chatRoomPrx.setNumberOfFakeParticipants(messageData.source, reserve);
        return EmoteCommand.ResultType.HANDLED_AND_STOP;
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }
}

