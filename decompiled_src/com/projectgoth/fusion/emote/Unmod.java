/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatRoomEmoteLogData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandException;
import com.projectgoth.fusion.emote.EmoteCommandUtils;
import com.projectgoth.fusion.emote.FilteringEmoteCommand;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import org.apache.log4j.Logger;

public class Unmod
extends FilteringEmoteCommand {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Unmod.class));

    public Unmod(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    protected Logger getLog() {
        return log;
    }

    protected String getRateLimitThreshold(String[] cmdArgs, MessageData messageData, ChatSource chatSource) {
        return SystemProperty.get(SystemPropertyEntities.Emote.REMOVE_MODERATOR_RATE_LIMIT);
    }

    protected void checkSyntax(String[] cmdArgs, MessageData messageData, ChatSource chatSource) throws FusionException {
        if (cmdArgs.length != 2) {
            String cmd = this.getCommand(cmdArgs);
            throw new FusionException(String.format("Usage: %s [username]", cmd));
        }
    }

    protected FilteringEmoteCommand.ProcessingResult doExecute(String[] cmdArgs, MessageData messageData, ChatSource chatSource) throws EmoteCommandException {
        boolean accepted = false;
        try {
            String targetUsername = cmdArgs[1];
            String sanitizedTargetUsername = EmoteCommandUtils.getSanitizedUsername(targetUsername);
            ChatRoomPrx chatRoomPrx = chatSource.castToLocalChatRoomChatSource().chatRoomPrx;
            ChatRoomDataIce roomDataIce = chatRoomPrx.getRoomData();
            chatRoomPrx.removeGroupModerator(messageData.source, sanitizedTargetUsername);
            if (SystemProperty.getBool(SystemPropertyEntities.Emote.LOG_ALL_EMOTES_ENABLED)) {
                ChatRoomEmoteLogData logData = new ChatRoomEmoteLogData(messageData.source, targetUsername, cmdArgs[0], roomDataIce.id, roomDataIce.groupID, -1, null);
                chatSource.getSessionI().logEmoteData(logData);
            }
            return new FilteringEmoteCommand.ProcessingResult(EmoteCommand.ResultType.HANDLED_AND_STOP, accepted);
        }
        catch (EmoteCommandException ex) {
            throw ex;
        }
        catch (FusionException ex) {
            throw new EmoteCommandException((Throwable)((Object)ex));
        }
    }
}

