/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ErrorCause;
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
import com.projectgoth.fusion.slice.FusionExceptionWithErrorCauseCode;
import org.apache.log4j.Logger;

public class Warn
extends FilteringEmoteCommand {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Warn.class));

    public Warn(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    protected Logger getLog() {
        return log;
    }

    protected String getRateLimitThreshold(String[] cmdArgs, MessageData messageData, ChatSource chatSource) {
        return SystemProperty.get(SystemPropertyEntities.Emote.WARN_RATE_LIMIT);
    }

    protected void checkSyntax(String[] cmdArgs, MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] args = messageData.getArgs();
        String cmd = this.getCommand(cmdArgs);
        if (args.length < 2 || args.length == 3) {
            throw new FusionException(String.format("Usage: %s [username] -m [message]", cmd));
        }
    }

    protected FilteringEmoteCommand.ProcessingResult doExecute(String[] cmdArgs, MessageData messageData, ChatSource chatSource) throws EmoteCommandException {
        boolean accepted = false;
        StringBuilder customWarnMessage = new StringBuilder();
        for (int i = 3; i < cmdArgs.length; ++i) {
            if (customWarnMessage.length() > 0) {
                customWarnMessage.append(" ");
            }
            customWarnMessage.append(cmdArgs[i]);
        }
        try {
            if (customWarnMessage.length() > SystemProperty.getInt(SystemPropertyEntities.Emote.WARN_MESSAGE_MAX_LENGTH)) {
                throw new FusionExceptionWithErrorCauseCode("Warning message is too long.", ErrorCause.EmoteCommandError.MESSAGE_TOO_LONG.getCode());
            }
            String targetUsername = cmdArgs[1];
            String sanitizedTargetUsername = EmoteCommandUtils.getSanitizedUsername(targetUsername);
            ChatRoomPrx chatRoomPrx = chatSource.castToLocalChatRoomChatSource().chatRoomPrx;
            ChatRoomDataIce roomData = chatRoomPrx.getRoomData();
            chatRoomPrx.warnUser(messageData.source, sanitizedTargetUsername, customWarnMessage.toString());
            if (SystemProperty.getBool(SystemPropertyEntities.Emote.LOG_ALL_EMOTES_ENABLED)) {
                ChatRoomEmoteLogData logData = new ChatRoomEmoteLogData(messageData.source, targetUsername, messageData.getArgs()[0], roomData.id, roomData.groupID, -1, null);
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

