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
import com.projectgoth.fusion.chatsync.CurrentChatList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.ChatListIce;
import com.projectgoth.fusion.slice.FusionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ChatList
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(ChatList.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    protected int userID;
    protected int chatListVersion;
    protected List<String> chatIDs;

    public ChatList(ChatListIce iceList) {
        this.userID = iceList.userID;
        this.chatListVersion = iceList.chatListVersion;
        this.chatIDs = Arrays.asList(iceList.chatIDs);
    }

    public ChatList(int userID) {
        this.userID = userID;
        this.chatListVersion = 0;
        this.chatIDs = new ArrayList<String>();
    }

    public ChatList(int userID, int chatListVersion, String[] chatIDs) {
        this.userID = userID;
        this.chatListVersion = chatListVersion;
        this.chatIDs = new ArrayList<String>(Arrays.asList(chatIDs));
    }

    public int getUserID() {
        return this.userID;
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.USER;
    }

    @Override
    public String getKey() {
        return Integer.toString(this.userID);
    }

    public int getVersion() {
        return this.chatListVersion;
    }

    public String[] getChatIDs() {
        String[] result = new String[this.chatIDs.size()];
        return this.chatIDs.toArray(result);
    }

    public void setVersion(int version) {
        this.chatListVersion = version;
    }

    public ChatListIce toIceObject() {
        ChatListIce ice = new ChatListIce();
        ice.userID = this.userID;
        ice.chatListVersion = this.chatListVersion;
        String[] arr = new String[this.chatIDs.size()];
        ice.chatIDs = this.chatIDs.toArray(arr);
        return ice;
    }

    public ChatListIce groupChatSubsetToIceObject() throws FusionException {
        ChatListIce ice = new ChatListIce();
        ice.userID = this.userID;
        ice.chatListVersion = this.chatListVersion;
        ArrayList<String> subset = new ArrayList<String>();
        for (String id : this.chatIDs) {
            ChatDefinition key = new ChatDefinition(id);
            if (!key.isGroupChat()) continue;
            subset.add(id);
        }
        ice.chatIDs = subset.toArray(new String[subset.size()]);
        return ice;
    }

    public boolean containsChat(ChatDefinition cd) {
        return this.containsChat(cd.getKey());
    }

    public boolean containsChat(String key) {
        for (String id : this.chatIDs) {
            if (!id.equals(key)) continue;
            return true;
        }
        return false;
    }

    public void addChat(ChatDefinition chat) {
        String key;
        if (chat != null && !this.chatIDs.contains(key = chat.getKey())) {
            this.chatIDs.add(key);
        }
    }

    public void removeChat(ChatDefinition chat) {
        if (chat != null) {
            String key = chat.getKey();
            this.chatIDs.remove(key);
        }
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
        throw new FusionException("retrievePipeline not implemented for " + this);
    }

    @Override
    public int loadPipeline(List<Object> pipelineResults, int startIndex) throws FusionException {
        throw new FusionException("loadPipeline not implemented for " + this);
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
        throw new FusionException("storePipeline not implemented for " + this);
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        throw new FusionException("processPipelineStoreResults not implemented for " + this);
    }

    @Override
    public boolean canRetryReads() throws FusionException {
        return false;
    }

    @Override
    public boolean canRetryWrites() throws FusionException {
        return false;
    }

    public ChatList clone() {
        return new CurrentChatList(this.toIceObject());
    }
}

