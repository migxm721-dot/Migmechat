/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.FieldNamingStrategy
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatSyncCompression;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncStats;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.ChatSyncStorePipeline;
import com.projectgoth.fusion.chatsync.MessageStatusEventKey;
import com.projectgoth.fusion.chatsync.ParticipantList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.gateway.packet.FusionPktMessage;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageDataIce;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatMessage
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(ChatMessage.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    public static final String LZF_ALGO = "LZF";
    public static final String GZIP_ALGO = "GZP";
    private ChatDefinition chatID;
    private MessageData message;
    private GroupChatPrx groupChat;

    public String getGUID() {
        return this.message.guid;
    }

    public void setGUID(String g) {
        this.message.guid = g;
    }

    public Long getTimestamp() {
        return this.message.messageTimestamp;
    }

    public MessageData cloneMessageData() {
        return new MessageData(this.message.toIceObject());
    }

    public void setMessageText(String txt) {
        this.message.messageText = txt;
    }

    public void setMessageData(MessageData md) {
        this.message = md;
    }

    public String getMessageText() {
        return this.message.messageText;
    }

    protected ChatMessage() {
    }

    public ChatMessage(ChatDefinition chatID) {
        this.chatID = chatID;
        this.message = null;
    }

    public ChatMessage(ChatDefinition chatID, MessageDataIce mdi) {
        this(chatID, mdi, null);
    }

    public ChatMessage(ChatDefinition chatID, MessageDataIce mdi, GroupChatPrx groupChat) {
        this.chatID = chatID;
        this.message = new MessageData(mdi);
        this.groupChat = groupChat;
    }

    public ChatMessage(ChatDefinition chatKey, byte[] storedValue) throws FusionException {
        this.decode(chatKey, storedValue);
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.MESSAGE;
    }

    @Override
    public String getKey() {
        return this.chatID.getKey();
    }

    @Override
    public String getValue() {
        Gson gson = new GsonBuilder().setFieldNamingStrategy((FieldNamingStrategy)ChatMessageStrategy.getInstance()).create();
        return gson.toJson((Object)this.message);
    }

    private void renewExpiry(SystemPropertyEntities.ChatSyncSettings toggle, ChatSyncStore store) throws FusionException {
        if (SystemProperty.getBool(toggle)) {
            int hrs = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CHAT_CONTENT_EXPIRY_HOURS);
            store.expire(this, hrs * 3600);
            int defHrs = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CHAT_DEFINITION_EXPIRY_HOURS);
            store.expire(this.chatID, hrs * 3600);
            if (SystemPropertyEntities.MessageStatusEventSettings.Cache.enabled.getValue().booleanValue() && this.chatID.isPrivateChat()) {
                MessageStatusEventKey mseKey1 = new MessageStatusEventKey(this.chatID, this.message.source);
                store.expire(mseKey1, hrs * 3600);
                MessageStatusEventKey mseKey2 = new MessageStatusEventKey(this.chatID, this.message.messageDestinations.get((int)0).destination);
                store.expire(mseKey2, hrs * 3600);
            }
            store.expire(new ParticipantList(this.chatID), hrs * 3600);
        }
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
        for (ChatSyncStore store : stores) {
            if (log.isDebugEnabled()) {
                log.debug("Storing message: " + this.message + " with messageTimestamp=" + this.message.messageTimestamp);
            }
            store.setMaster();
            store.pipelinedBinary(this, new ChatSyncStorePipeline(){

                public void execute(ChatSyncEntity entity, ChatSyncStore pipelineStore) throws FusionException {
                    String algo = SystemProperty.get(SystemPropertyEntities.ChatSyncSettings.MESSAGE_COMPRESSION_ALGORITHM);
                    long timestamp = ((ChatMessage)ChatMessage.this).message.messageTimestamp;
                    if (!StringUtil.isBlank(algo)) {
                        byte[] bytesToStore = ChatSyncCompression.compressFromString(ChatMessage.this.getValue(), algo);
                        pipelineStore.zadd(entity, (double)timestamp, bytesToStore);
                    } else {
                        pipelineStore.zadd(entity, (double)timestamp, ChatMessage.this.getValue());
                    }
                    ChatMessage.this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_WRITE_ENABLED, pipelineStore);
                }
            });
            this.truncateIfNeeded(store);
            ChatSyncStats.getInstance().incrementTotalMessagesStored();
            if (!log.isDebugEnabled()) continue;
            log.debug("Stored message: " + this.message + " with messageTimestamp=" + this.message.messageTimestamp);
        }
    }

    private void truncateIfNeeded(ChatSyncStore store) throws FusionException {
        int maxLength = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CHAT_MAX_LENGTH);
        if (maxLength == 0) {
            log.debug("truncateIfNeeded: Chat max messages truncation disabled");
            return;
        }
        store.setSlave();
        int count = store.zcount(this, Double.MIN_VALUE, Double.MAX_VALUE);
        if (count < maxLength) {
            return;
        }
        int newLength = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CHAT_MAX_LENGTH_TRUNCATION);
        if (newLength > maxLength) {
            log.warn("truncateIfNeeded: Invalid value for CHAT_MAX_LENGTH_TRUNCATION sysprop: " + SystemPropertyEntities.ChatSyncSettings.CHAT_MAX_LENGTH.getName() + "=" + maxLength + SystemPropertyEntities.ChatSyncSettings.CHAT_MAX_LENGTH_TRUNCATION + "=" + newLength + " ---not truncating");
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("ChatMessage.truncateIfNeeded: msgs for chatID=" + this.chatID + " has length=" + count + "; max length=" + maxLength + "; truncating to new length=" + newLength);
        }
        store.setMaster();
        int delta = count - newLength;
        store.zremrangeByRank(this, 0, delta - 1);
    }

    @Override
    public void unpack(String storedKey, String storedValue) throws FusionException {
        throw new FusionException("Not implemented");
    }

    private void decode(ChatDefinition chatKey, byte[] storedValue) throws FusionException {
        String decompressedValue = ChatSyncCompression.adaptiveDecompressToString(storedValue);
        this.decode(chatKey, decompressedValue);
    }

    private void decode(ChatDefinition chatKey, String decompressedValue) throws FusionException {
        this.chatID = chatKey;
        Gson gson = new GsonBuilder().setFieldNamingStrategy((FieldNamingStrategy)ChatMessageStrategy.getInstance()).create();
        this.message = (MessageData)gson.fromJson(decompressedValue, MessageData.class);
    }

    public FusionPktMessage toFusionPktMessage(String previousMessageGUID, boolean sessionIsJ2ME, String senderUsername, Short fusionPktTransactionId) {
        MessageData md = new MessageData(this.message);
        if (sessionIsJ2ME && this.message.isMessageToAnIndividual() && md.source.equals(senderUsername)) {
            md.source = md.messageDestinations.get((int)0).destination;
            md.messageDestinations.get((int)0).destination = senderUsername;
            md.messageText = "(" + senderUsername + "): " + md.messageText;
        }
        FusionPktMessage pkt = new FusionPktMessage(md);
        if (previousMessageGUID != null) {
            pkt.setPreviousMessageGUID(previousMessageGUID);
        }
        if (fusionPktTransactionId != null) {
            pkt.setTransactionId(fusionPktTransactionId);
        }
        return pkt;
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
        return false;
    }

    protected static class ChatMessageStrategy
    implements FieldNamingStrategy {
        private HashMap<String, String> map = new HashMap();

        public static ChatMessageStrategy getInstance() {
            return SingletonHolder.INSTANCE;
        }

        private ChatMessageStrategy() {
            this.putExclusive("messageData", "MD");
            this.putExclusive("id", "ID");
            this.putExclusive("username", "UN");
            this.putExclusive("dateCreated", "DC");
            this.putExclusive("requestReceivedTimestamp", "RRT");
            this.putExclusive("type", "T");
            this.putExclusive("messageText", "MT");
            this.putExclusive("messageColour", "MC");
            this.putExclusive("contentType", "CT");
            this.putExclusive("binaryData", "BD");
            this.putExclusive("sendReceive", "SR");
            this.putExclusive("sourceContactID", "SCI");
            this.putExclusive("source", "S");
            this.putExclusive("sourceType", "ST");
            this.putExclusive("sourceDisplayPicture", "SDP");
            this.putExclusive("sourceColour", "SC");
            this.putExclusive("fromAdministrator", "FA");
            this.putExclusive("emoticonKeys", "EK");
            this.putExclusive("messageDestinations", "MDE");
            this.putExclusive("messageID", "MI");
            this.putExclusive("contactID", "CI");
            this.putExclusive("destination", "D");
            this.putExclusive("IDDCode", "IC");
            this.putExclusive("cost", "C");
            this.putExclusive("gateway", "G");
            this.putExclusive("dateDispatched", "DD");
            this.putExclusive("providerTransactionID", "PTI");
            this.putExclusive("status", "SS");
            this.putExclusive("guid", "GU");
            this.putExclusive("messageTimestamp", "MTI");
            this.putExclusive("groupChatName", "GCN");
            this.putExclusive("groupChatOwner", "GCO");
            this.putExclusive("emoteContentType", "ECT");
            if (SystemProperty.getBool(SystemPropertyEntities.CoreChatSettings.MIME_TYPE_MESSAGES_ENABLED)) {
                this.putExclusive("mimeType", "MIT");
                this.putExclusive("mimeDataType", "MDT");
            }
        }

        private void putExclusive(String key, String value) throws RuntimeException {
            if (this.map.values().contains(value)) {
                throw new RuntimeException("Translated field name " + value + " already used");
            }
            this.map.put(key, value);
        }

        public String translateName(Field field) {
            String alias = this.map.get(field.getName());
            if (alias != null) {
                return alias;
            }
            return field.getName();
        }

        private static class SingletonHolder {
            public static final ChatMessageStrategy INSTANCE = new ChatMessageStrategy();

            private SingletonHolder() {
            }
        }
    }
}

