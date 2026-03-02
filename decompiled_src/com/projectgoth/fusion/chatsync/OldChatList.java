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
import com.projectgoth.fusion.chatsync.ChatList;
import com.projectgoth.fusion.chatsync.ChatSyncCompression;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.ChatListIce;
import com.projectgoth.fusion.slice.FusionException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OldChatList
extends ChatList
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(OldChatList.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);

    public OldChatList(ChatListIce iceList) {
        super(iceList);
    }

    public OldChatList(int userID) {
        super(userID);
    }

    public OldChatList(int userID, int chatListVersion, String[] chatIDs) {
        super(userID, chatListVersion, chatIDs);
    }

    public OldChatList(int userID, byte[] chatListData) throws FusionException {
        super(userID);
        String gsonifiedChatList = ChatSyncCompression.adaptiveDecompressToString(chatListData);
        if (log.isDebugEnabled()) {
            log.debug("Unpacking gson chat list=" + gsonifiedChatList);
        }
        try {
            Gson gson = new GsonBuilder().setFieldNamingStrategy((FieldNamingStrategy)OldChatListStrategy.getInstance()).create();
            ChatListValue wrapper = (ChatListValue)gson.fromJson(gsonifiedChatList, ChatListValue.class);
            this.chatListVersion = wrapper.chatListVersion;
            this.chatIDs = new ArrayList<String>(Arrays.asList(wrapper.chatIDs));
        }
        catch (UnsupportedOperationException e) {
            log.warn("An error ocurred unpacking the chatlist. Using empty chat list");
            this.chatListVersion = 0;
            this.chatIDs = new ArrayList();
        }
    }

    @Override
    public String getValue() {
        ChatListValue wrapper = new ChatListValue();
        wrapper.chatListVersion = this.chatListVersion;
        String[] ids = new String[this.chatIDs.size()];
        wrapper.chatIDs = this.chatIDs.toArray(ids);
        Gson gson = new GsonBuilder().setFieldNamingStrategy((FieldNamingStrategy)OldChatListStrategy.getInstance()).create();
        return gson.toJson((Object)wrapper);
    }

    public void renewExpiry(SystemPropertyEntities.ChatSyncSettings toggle, ChatSyncStore store) throws FusionException {
        if (SystemProperty.getBool(toggle)) {
            int hrs = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.OLD_CHAT_LISTS_EXPIRY_HOURS);
            store.expire(this, hrs * 3600);
        }
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
        throw new FusionException("store() not implemented for " + this);
    }

    @Override
    public void unpack(String storedKey, String storedValue) throws FusionException {
        this.userID = new Integer(storedKey);
        Gson gson = new GsonBuilder().setFieldNamingStrategy((FieldNamingStrategy)OldChatListStrategy.getInstance()).create();
        ChatListValue wrapper = (ChatListValue)gson.fromJson(storedValue, ChatListValue.class);
        this.chatListVersion = wrapper.chatListVersion;
        this.chatIDs = new ArrayList<String>(Arrays.asList(wrapper.chatIDs));
    }

    public String[] findMissingChats(String[] chatSupersetIDs) {
        ArrayList<String> missingIDs = new ArrayList<String>();
        for (String chatID : chatSupersetIDs) {
            if (this.chatIDs.contains(chatID)) continue;
            missingIDs.add(chatID);
        }
        String[] result = new String[missingIDs.size()];
        return missingIDs.toArray(result);
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        throw new FusionException("retrieve() not implemented for " + this);
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
        String algo = SystemProperty.get(SystemPropertyEntities.ChatSyncSettings.OLD_CHAT_LISTS_COMPRESSION_ALGORITHM);
        String gson = this.getValue();
        if (log.isDebugEnabled()) {
            log.debug("Storing OldChatList gson=" + gson + "  Size before compression=" + Redis.safeEncoderEncode(gson).length + "  Compression algo=" + algo);
        }
        if (StringUtil.isBlank(algo)) {
            pipelineStore.rpush((ChatSyncEntity)this, gson);
        } else {
            byte[] compressed = ChatSyncCompression.compressFromString(gson, algo);
            if (log.isDebugEnabled()) {
                log.debug("Size after compression=" + compressed.length);
            }
            pipelineStore.rpush((ChatSyncEntity)this, compressed);
        }
        this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_WRITE_ENABLED, pipelineStore);
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        return startIndex + 2;
    }

    private static class OldChatListStrategy
    implements FieldNamingStrategy {
        private static final String CLV_ALIAS = "v";
        private static final String IDS_ALIAS = "i";

        private OldChatListStrategy() {
        }

        public static OldChatListStrategy getInstance() {
            return SingletonHolder.INSTANCE;
        }

        public String translateName(Field field) {
            if (field.getName().equals("chatListVersion")) {
                return CLV_ALIAS;
            }
            if (field.getName().equals("chatIDs")) {
                return IDS_ALIAS;
            }
            throw new RuntimeException("Unexpected field name in OldChatListStrategy: " + field.getName());
        }

        private static class SingletonHolder {
            public static final OldChatListStrategy INSTANCE = new OldChatListStrategy();

            private SingletonHolder() {
            }
        }
    }

    public static class ChatListValue {
        public int chatListVersion;
        public String[] chatIDs;
    }
}

