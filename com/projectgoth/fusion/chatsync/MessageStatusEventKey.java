/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.slice.FusionException;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MessageStatusEventKey
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(MessageStatusEventKey.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    protected final ChatDefinition chatID;
    protected String messageSource;
    protected String messageDestination;

    protected MessageStatusEventKey() {
        this.chatID = null;
    }

    public MessageStatusEventKey(ChatDefinition chatID, String messageSource) throws FusionException {
        this(chatID, messageSource, chatID.internalToExternalChatID(messageSource));
    }

    public MessageStatusEventKey(ChatDefinition chatID, String messageSource, String messageDestination) {
        this.chatID = chatID;
        this.messageSource = messageSource;
        this.messageDestination = messageDestination;
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.MESSAGE_STATUS_EVENT;
    }

    public ChatDefinition getChatID() {
        return this.chatID;
    }

    public String getMessageSource() {
        return this.messageSource;
    }

    public String getMessageDestination() {
        return this.messageDestination;
    }

    public MessageDestinationData.TypeEnum getMessageDestinationType() {
        return MessageDestinationData.TypeEnum.fromValue(this.chatID.getChatType());
    }

    @Override
    public String getKey() {
        String suffix = this.messageSource.compareTo(this.messageDestination) < 0 ? "A" : "B";
        return this.chatID.getKey() + ":" + suffix;
    }

    public String getChatKey() {
        return this.chatID.getKey();
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
        throw new FusionException("Unimplemented");
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        throw new FusionException("Unimplemented");
    }

    @Override
    public boolean canRetryReads() throws FusionException {
        return false;
    }

    @Override
    public boolean canRetryWrites() throws FusionException {
        return false;
    }
}

