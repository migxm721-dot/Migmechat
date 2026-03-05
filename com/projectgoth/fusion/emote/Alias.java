/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandFactory;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.slice.FusionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class Alias
extends EmoteCommand {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Alias.class));
    private static final Pattern COMMAND_PARAMETERS_PATTERN = Pattern.compile("^(?:[^ ]+)(.*)$");
    private final String aliasCommand;

    public Alias(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
        String[] args = emoteCommandData.getMessageText().toLowerCase().split(" ");
        if (args.length != 0 && args[0].length() >= 2) {
            this.aliasCommand = args[0].substring(1);
        } else {
            log.error((Object)"Expected alias command to have a messageText set to the target command. eg. \"/gift all\"");
            this.aliasCommand = "";
        }
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        String newMessageText;
        Matcher m = COMMAND_PARAMETERS_PATTERN.matcher(messageData.messageText);
        if (!m.matches()) {
            throw new FusionException("Unable to parse messageData to detect command!");
        }
        messageData.messageText = newMessageText = this.emoteCommandData.getMessageText() + m.group(1);
        EmoteCommand ec = EmoteCommandFactory.getEmoteCommand(this.aliasCommand, chatSource.chatType, null);
        if (ec == null) {
            throw new FusionException("Unable to process emote command alias: " + this.aliasCommand);
        }
        return ec.execute(messageData, chatSource);
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        EmoteCommand ec = EmoteCommandFactory.getEmoteCommand(this.aliasCommand, chatType, null);
        if (ec == null) {
            log.error((Object)("Unable to get emote command alias: " + this.aliasCommand));
            return null;
        }
        return ec.createDefaultState(chatType);
    }
}

