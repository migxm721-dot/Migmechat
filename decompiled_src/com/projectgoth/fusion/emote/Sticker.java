/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ContentUtils;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatRoomEmoteLogData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandException;
import com.projectgoth.fusion.emote.EmoteCommandFactory;
import com.projectgoth.fusion.emote.FilteringEmoteCommand;
import com.projectgoth.fusion.emote.StickerDeliveredMessageData;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardDispatcher;
import com.projectgoth.fusion.objectcache.Emote;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import org.apache.log4j.Logger;

public class Sticker
extends FilteringEmoteCommand {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Sticker.class));
    private static final String SPACE = " ";

    public Sticker(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    protected Logger getLog() {
        return log;
    }

    protected FilteringEmoteCommand.ProcessingResult doExecute(String[] cmdArgs, MessageData messageData, ChatSource chatSource) throws EmoteCommandException {
        try {
            if (chatSource.getChatType() != ChatSource.ChatType.CHATROOM_CHAT || SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.CHATROOM_SYNC_ENABLED)) {
                try {
                    MessageSwitchboardDispatcher.getInstance().onSendMessageToAllUsersInChat(messageData, messageData.username, chatSource.getSessionPrx(), chatSource.getSessionI());
                }
                catch (Exception e) {
                    log.warn((Object)"While storing sticker in chatsync: ", (Throwable)e);
                }
            }
            String senderUsername = messageData.source;
            StickerDeliveredMessageData forDelivery = new StickerDeliveredMessageData(cmdArgs, messageData);
            chatSource.sendStickerEmotes(forDelivery);
            int chatRoomID = -1;
            int chatRoomGroupID = -1;
            if (chatSource.getChatType() == ChatSource.ChatType.CHATROOM_CHAT) {
                ChatRoomPrx chatRoomPrx = chatSource.castToLocalChatRoomChatSource().chatRoomPrx;
                ChatRoomDataIce roomDataIce = chatRoomPrx.getRoomData();
                chatRoomID = roomDataIce.id;
                chatRoomGroupID = roomDataIce.groupID;
            }
            ChatRoomEmoteLogData logData = new ChatRoomEmoteLogData(senderUsername, cmdArgs[1], cmdArgs[0], chatRoomID, chatRoomGroupID, -1, null);
            chatSource.getSessionI().logEmoteData(logData);
            return new FilteringEmoteCommand.ProcessingResult(EmoteCommand.ResultType.HANDLED_AND_STOP, true);
        }
        catch (FusionException ex) {
            throw new EmoteCommandException((Throwable)((Object)ex));
        }
        catch (Exception ex) {
            throw new EmoteCommandException(ex);
        }
    }

    protected void checkSyntax(String[] cmdArgs, MessageData messageData, ChatSource chatSource) throws FusionException {
        if (!ContentUtils.isStickersEnabled()) {
            throw new FusionException("Invalid or disabled command.");
        }
        if (cmdArgs.length != 2) {
            String cmd = this.getCommand(cmdArgs);
            throw new FusionException(String.format("Usage: %s [sticker name]", cmd));
        }
    }

    protected String getRateLimitThreshold(String[] cmdArgs, MessageData messageData, ChatSource chatSource) {
        return SystemProperty.get(SystemPropertyEntities.Emote.STICKER_RATE_LIMIT);
    }

    protected void checkDevice(String[] cmdArgs, MessageData messageData, ChatSource chatSource) throws FusionException {
    }

    public static MessageDataIce createStickerEmotesForSender(MessageData messageData, String msgToInstigator, ClientType senderDeviceType, short senderClientVersion) throws FusionException {
        MessageDataIce messageIce = messageData.toIceObject();
        if (!ContentUtils.deviceCanReceiveStickersNatively(senderDeviceType, senderClientVersion) && messageIce.messageDestinations[0].type == MessageDestinationData.TypeEnum.INDIVIDUAL.value()) {
            String tmp = messageIce.source;
            messageIce.source = messageIce.messageDestinations[0].destination;
            messageIce.messageDestinations[0].destination = tmp;
        }
        messageIce.emoteContentType = MessageData.EmoteContentTypeEnum.STICKERS.value();
        messageIce.messageText = msgToInstigator;
        return messageIce;
    }

    public static MessageDataIce createStickerEmotesForRecipients(MessageData messageData, String msgToRecipient) throws FusionException {
        MessageDataIce messageIce = messageData.toIceObject();
        messageIce.messageText = msgToRecipient;
        messageIce.emoteContentType = MessageData.EmoteContentTypeEnum.STICKERS.value();
        return messageIce;
    }

    public static boolean isStickerCommand(MessageData msgData) {
        if (Emote.isEmote(msgData.messageText)) {
            String[] args = msgData.messageText.toLowerCase().split(SPACE);
            String command = args[0].substring(1);
            IcePrxFinder nullIPF = null;
            EmoteCommand ec = EmoteCommandFactory.getEmoteCommand(command, ChatSource.ChatType.fromDestinationType(msgData.messageDestinations.get((int)0).type), nullIPF);
            return ec instanceof Sticker;
        }
        return false;
    }
}

