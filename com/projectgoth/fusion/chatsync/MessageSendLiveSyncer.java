/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.Sticker;
import com.projectgoth.fusion.emote.StickerDeliveredMessageData;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MessageSendLiveSyncer
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(MessageSendLiveSyncer.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private static final String SPACE = " ";
    private SessionPrx currentSession;
    private MessageDataIce msg;
    private UserDataIce senderUserData;
    private UserPrx sender;
    private GroupChatPrx groupChat;

    public MessageSendLiveSyncer(SessionPrx currentSession, MessageDataIce msg, UserDataIce senderUserData, UserPrx sender, GroupChatPrx groupChat) throws FusionException {
        this.currentSession = currentSession;
        this.msg = msg;
        this.senderUserData = senderUserData;
        this.sender = sender;
        this.groupChat = groupChat;
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug("MessageSendLiveSyncer.store() for sender=" + this.senderUserData.username);
        }
        this.forwardToSendersOtherSessions(this.currentSession, this.msg, this.senderUserData, this.sender);
    }

    private void forwardToSendersOtherSessions(SessionPrx currentSession, MessageDataIce msg, UserDataIce senderUserData, UserPrx sender) throws FusionException {
        SessionPrx[] senderSessions = sender.getSessions();
        if (log.isDebugEnabled()) {
            log.debug("forwardToSendersOtherSessions: sender=" + senderUserData.username + " session count=" + senderSessions.length);
        }
        MessageDataIce copy = new MessageData(msg).toIceObject();
        StickerDeliveredMessageData stickerMessageData = null;
        if (EmoteCommand.hasMessageVariables(copy.messageText)) {
            String newText = EmoteCommand.processMessageVariables(copy.messageText, senderUserData.username, senderUserData.username);
            if (!newText.equals(copy.messageText)) {
                if (log.isDebugEnabled()) {
                    log.debug("forwardToSendersOtherSessions: Substituted emote template:" + copy.messageText + " with:" + newText + "for live synched message");
                }
                copy.messageText = newText;
            }
        } else if (Sticker.isStickerCommand(new MessageData(msg))) {
            try {
                String[] args = msg.messageText.toLowerCase().split(SPACE);
                stickerMessageData = new StickerDeliveredMessageData(args, new MessageData(msg));
            }
            catch (Exception e) {
                log.warn("Sticker processing failed while live synching message=" + msg.messageText + " cause=" + e, e);
            }
        }
        String currentSessionID = currentSession.getSessionID();
        for (SessionPrx session : senderSessions) {
            if (!session.getSessionID().equals(currentSessionID)) {
                if (stickerMessageData != null) {
                    copy = Sticker.createStickerEmotesForSender(stickerMessageData.getMessageData(), stickerMessageData.getMessageToInstigator(), ClientType.fromValue(session.getDeviceTypeAsInt()), session.getClientVersionIce());
                }
                this.forwardToSenderSession(session, copy, senderUserData);
                continue;
            }
            if (!log.isDebugEnabled()) continue;
            log.debug("forwardToSendersOtherSessions: sender=" + senderUserData.username + " not ccing message to session=" + session + " as is current");
        }
    }

    private void forwardToSenderSession(SessionPrx session, MessageDataIce copy, UserDataIce senderUserData) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("forwardToSenderSession: sender=" + senderUserData.username + " ccing message to session=" + session);
            }
            session.putMessage(copy);
        }
        catch (Exception e) {
            log.warn("Exception live-syncing message to other sessions of sender=" + senderUserData.username + " : " + e, e);
        }
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.MESSAGE;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void unpack(String storedKey, String storedValue) throws FusionException {
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
    }

    @Override
    public int loadPipeline(List<Object> pipelineResults, int startIndex) throws FusionException {
        return 0;
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        return 0;
    }

    @Override
    public boolean canRetryReads() throws FusionException {
        return false;
    }

    @Override
    public boolean canRetryWrites() throws FusionException {
        return SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.RETRY_LIVESYNC_MESSAGE_ENABLED);
    }
}

