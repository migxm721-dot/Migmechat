/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.emote.LoveMatch;
import com.projectgoth.fusion.slice.FusionException;
import org.apache.log4j.Logger;

public class FindMyMatch
extends EmoteCommand {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FindMyMatch.class));
    private static final String DEFAULT_FINDMYMATCH_NOMATCH_MESSAGE = "No Match - there are no other users in the chat";

    public FindMyMatch(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] allUsers = chatSource.getVisibleUsernamesInChat(false);
        if (allUsers.length == 0) {
            throw new FusionException(SystemProperty.get("EmoteFindMyMatchNoMatch", DEFAULT_FINDMYMATCH_NOMATCH_MESSAGE));
        }
        int maxScore = -1;
        String maxUsername = null;
        int myCode = LoveMatch.getLoveCode(messageData.source);
        for (String username : allUsers) {
            int curScore = LoveMatch.getLoveMatchScore(myCode, LoveMatch.getLoveCode(username));
            if (curScore <= maxScore) continue;
            maxScore = curScore;
            maxUsername = username;
        }
        messageData.messageText = String.format(this.emoteCommandData.getMessageText(), maxUsername, maxScore);
        this.emoteCommandData.updateMessageData(messageData);
        chatSource.sendMessageToAllUsersInChat(messageData);
        return EmoteCommand.ResultType.HANDLED_AND_STOP;
    }
}

