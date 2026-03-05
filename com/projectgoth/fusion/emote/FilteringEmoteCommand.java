/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandException;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.emote.EmoteCommandUtils;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.FusionExceptionWithErrorCauseCode;
import org.apache.log4j.Logger;

public abstract class FilteringEmoteCommand
extends EmoteCommand {
    protected abstract Logger getLog();

    public FilteringEmoteCommand(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] cmdArgs = messageData.getArgs();
        this.filterAndValidate(cmdArgs, messageData, chatSource);
        try {
            ProcessingResult result = this.doExecute(cmdArgs, messageData, chatSource);
            if (!result.accepted) {
                this.getLog().warn((Object)String.format("Emote %s rejected", this.getCommand(cmdArgs)));
            }
            return result.resultType;
        }
        catch (EmoteCommandException ex) {
            if (ex.getErrorCause() == ErrorCause.EmoteCommandError.INTERNAL_ERROR) {
                if (ex.getCause() instanceof FusionException) {
                    throw (FusionException)((Object)ex.getCause());
                }
                this.getLog().error((Object)"Internal error", (Throwable)ex);
            } else {
                this.getLog().warn((Object)String.format("Emote %s failed %s", this.getCommand(cmdArgs), ex.getErrorCause().getCode()));
            }
            throw new FusionExceptionWithErrorCauseCode(ex.getMessage(), ex.getErrorCause().getCode());
        }
    }

    protected String getCommand(String[] cmdArgs) {
        String cmd = "";
        cmd = cmdArgs.length > 0 ? cmdArgs[0] : this.getClass().getName().toLowerCase();
        return cmd;
    }

    protected abstract ProcessingResult doExecute(String[] var1, MessageData var2, ChatSource var3) throws EmoteCommandException;

    private void filterAndValidate(String[] cmdArgs, MessageData messageData, ChatSource chatSource) throws FusionException {
        this.checkSyntax(cmdArgs, messageData, chatSource);
        this.checkRateLimit(this.getClass(), MemCachedKeyUtils.getFullKeyFromStrings("s:" + messageData.source, this.getInstigatorRateLimitLocalKeySuffix(cmdArgs, messageData, chatSource)), this.getRateLimitThreshold(cmdArgs, messageData, chatSource));
        this.checkDevice(cmdArgs, messageData, chatSource);
    }

    protected void checkDevice(String[] cmdArgs, MessageData messageData, ChatSource chatSource) throws FusionException {
        if (!EmoteCommandUtils.clientMeetsMinVersion(chatSource.getSessionI().getClientVersion(), chatSource.getSessionI().getDeviceType())) {
            throw new FusionExceptionWithErrorCauseCode(this.getCommand(cmdArgs) + " is not supported on this client device and version", ErrorCause.EmoteCommandError.UNSUPPORTED_CLIENT_DEVICE.getCode());
        }
    }

    protected String getInstigatorRateLimitLocalKeySuffix(String[] cmdArgs, MessageData messageData, ChatSource chatSource) {
        return "";
    }

    protected abstract void checkSyntax(String[] var1, MessageData var2, ChatSource var3) throws FusionException;

    protected abstract String getRateLimitThreshold(String[] var1, MessageData var2, ChatSource var3);

    public static class ProcessingResult {
        public EmoteCommand.ResultType resultType;
        public boolean accepted;

        public ProcessingResult(EmoteCommand.ResultType resultType, boolean valid) {
            this.resultType = resultType;
            this.accepted = valid;
        }
    }
}

