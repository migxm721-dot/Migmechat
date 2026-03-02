/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.FieldNamingStrategy
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.annotations.Expose
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncPipelineOp;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.MessageStatusEvent;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.gateway.packet.FusionPktMessage;
import com.projectgoth.fusion.gateway.packet.FusionPktMessageStatusEvent;
import com.projectgoth.fusion.slice.FusionException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MessageStatusEventPersistable
extends MessageStatusEvent {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(MessageStatusEventPersistable.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    @Deprecated
    private int writeOpCount = 0;
    @Expose
    private int persistedMessageStatus;
    @Expose
    private int persistedServerGenerated;

    public int getPersistedMessageStatus() {
        return this.persistedMessageStatus;
    }

    public int getPersistedServerGenerated() {
        return this.persistedServerGenerated;
    }

    public MessageStatusEventPersistable() throws FusionException {
    }

    public MessageStatusEventPersistable(MessageStatusEventPersistable msep) throws FusionException {
        super(msep);
    }

    public MessageStatusEventPersistable(FusionPktMessageStatusEvent pkt) throws FusionException {
        super(pkt);
    }

    public MessageStatusEventPersistable(ChatDefinition chatKey, long msgTimestamp, String mseGson, String requestor) throws FusionException {
        super(chatKey, requestor);
        Gson gson = new GsonBuilder().setFieldNamingStrategy((FieldNamingStrategy)MSEStrategy.getInstance()).create();
        MessageStatusEventPersistable unpacked = (MessageStatusEventPersistable)gson.fromJson(mseGson, MessageStatusEventPersistable.class);
        this.messageType = MessageType.FUSION;
        this.messageSource = requestor;
        this.messageDestination = chatKey.internalToExternalChatID(requestor);
        this.messageGUID = unpacked.getMessageGUID();
        this.messageStatus = Enums.MessageStatusEventTypeEnum.fromValue(unpacked.getPersistedMessageStatus());
        this.serverGenerated = unpacked.getPersistedServerGenerated() == 1;
        this.messageTimestamp = msgTimestamp;
    }

    public MessageStatusEventPersistable(FusionPktMessage msg, Enums.MessageStatusEventTypeEnum status, boolean serverGenerated) throws FusionException {
        super(new ChatDefinition(msg.getDestination(), msg.getDestinationType(), msg.getSource()), msg.getSource());
        this.messageType = MessageType.fromValue(msg.getMessageType());
        this.messageSource = msg.getSource();
        this.messageDestination = msg.getDestination();
        this.messageGUID = msg.getGUID();
        this.messageStatus = status;
        this.serverGenerated = serverGenerated;
        this.messageTimestamp = msg.getTimestamp();
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug("MessageStatusEventPersistable.store for guid=" + this.messageGUID);
        }
        ArrayList<ChatSyncPipelineOp> writeOps = new ArrayList<ChatSyncPipelineOp>();
        writeOps.add(new ChatSyncPipelineOp(this, ChatSyncPipelineOp.OpType.WRITE));
        for (ChatSyncStore store : stores) {
            store.setMaster();
            store.pipelined(writeOps);
        }
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.MESSAGE_STATUS_EVENT;
    }

    @Override
    public String getValue() {
        this.persistedMessageStatus = this.messageStatus.value();
        this.persistedServerGenerated = this.serverGenerated ? 1 : 0;
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setFieldNamingStrategy((FieldNamingStrategy)MSEStrategy.getInstance()).create();
        return gson.toJson((Object)this);
    }

    @Override
    public void unpack(String storedKey, String storedValue) throws FusionException {
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        for (ChatSyncStore store : stores) {
            ArrayList<ChatSyncPipelineOp> readOps = new ArrayList<ChatSyncPipelineOp>();
            readOps.add(new ChatSyncPipelineOp(this, ChatSyncPipelineOp.OpType.READ));
            store.setSlave();
            store.pipelined(readOps);
        }
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
        pipelineStore.zrangeByScore(this, this.messageTimestamp, this.messageTimestamp);
    }

    @Override
    public int loadPipeline(List<Object> pipelineResults, int startIndex) throws FusionException {
        List eventsAtTimestamp = (List)pipelineResults.get(startIndex);
        Enums.MessageStatusEventTypeEnum highestStatusForMsg = Enums.MessageStatusEventTypeEnum.COMPOSING;
        for (String gson : eventsAtTimestamp) {
            MessageStatusEventPersistable mse = new MessageStatusEventPersistable(this.chatID, this.messageTimestamp, gson, this.messageSource);
            if (!this.messageGUID.equals(mse.getMessageGUID()) || mse.getMessageStatus().value() <= highestStatusForMsg.value()) continue;
            this.messageType = mse.getMessageType();
            this.messageSource = mse.getMessageSource();
            this.messageDestination = mse.getMessageDestination();
            this.messageStatus = highestStatusForMsg = mse.getMessageStatus();
            this.serverGenerated = mse.getServerGenerated();
        }
        return startIndex + 1;
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug("Storing MessageStatusEvent for msg guid=" + this.messageGUID);
        }
        pipelineStore.zadd((ChatSyncEntity)this, (double)this.messageTimestamp, this.getValue());
        ++this.writeOpCount;
        if (this.getMessageStatus() == Enums.MessageStatusEventTypeEnum.READ) {
            MessageStatusEventPersistable received = new MessageStatusEventPersistable(this);
            received.setMessageStatus(Enums.MessageStatusEventTypeEnum.RECEIVED);
            pipelineStore.zrem(received, received.getValue());
            ++this.writeOpCount;
        }
        this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_WRITE_ENABLED, pipelineStore);
    }

    private void renewExpiry(SystemPropertyEntities.ChatSyncSettings toggle, ChatSyncStore store) throws FusionException {
        if (SystemProperty.getBool(toggle)) {
            int hrs = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CHAT_CONTENT_EXPIRY_HOURS);
            store.expire(this, hrs * 3600);
            ++this.writeOpCount;
        }
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        return startIndex + this.writeOpCount;
    }

    @Override
    public boolean canRetryReads() throws FusionException {
        return SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.RETRY_GET_MESSAGE_STATUS_EVENTS_ENABLED);
    }

    @Override
    public boolean canRetryWrites() throws FusionException {
        return SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.RETRY_STORE_MESSAGE_STATUS_EVENTS_ENABLED);
    }

    @Deprecated
    public int getWriteOpCount() {
        return this.writeOpCount;
    }

    public void setMessageStatus(Enums.MessageStatusEventTypeEnum status) {
        this.messageStatus = status;
    }

    protected static class MSEStrategy
    implements FieldNamingStrategy {
        private HashMap<String, String> map = new HashMap();

        public static MSEStrategy getInstance() {
            return SingletonHolder.INSTANCE;
        }

        private MSEStrategy() {
            this.map.put("messageGUID", "G");
            this.map.put("persistedMessageStatus", "S");
            this.map.put("persistedServerGenerated", "W");
        }

        public String translateName(Field field) {
            String alias = this.map.get(field.getName());
            if (alias != null) {
                return alias;
            }
            return field.getName();
        }

        private static class SingletonHolder {
            public static final MSEStrategy INSTANCE = new MSEStrategy();

            private SingletonHolder() {
            }
        }
    }
}

