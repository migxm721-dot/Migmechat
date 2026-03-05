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
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.FusionException;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatListVersion
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(ChatListVersion.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private int userID;
    private Integer version;
    private int storeOps;

    public ChatListVersion(int userID) {
        this.userID = userID;
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.USER;
    }

    public int getUserID() {
        return this.userID;
    }

    public int getVersion() {
        return this.version;
    }

    @Override
    public String getKey() {
        return Integer.toString(this.userID);
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
    }

    @Override
    public void unpack(String storedKey, String storedValue) throws FusionException {
    }

    public void renewExpiry(SystemPropertyEntities.ChatSyncSettings toggle, ChatSyncStore store) throws FusionException {
        if (SystemProperty.getBool(toggle)) {
            int hrs = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CHAT_LIST_VERSION_EXPIRY_HOURS);
            store.expire(this, hrs * 3600);
            ++this.storeOps;
        }
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        stores[0].setMaster();
        this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_READ_ENABLED, stores[0]);
        stores[0].setSlave();
        String value = stores[0].get(this);
        this.version = value == null ? 0 : Integer.parseInt(value);
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
        pipelineStore.get(this);
    }

    @Override
    public int loadPipeline(List<Object> pipelineResults, int startIndex) throws FusionException {
        String sValue = Redis.getPipelineResult(pipelineResults, startIndex);
        this.version = sValue == null ? 0 : Integer.parseInt(sValue);
        return startIndex + 1;
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
        this.storeOps = 0;
        pipelineStore.incr(this);
        ++this.storeOps;
        this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_WRITE_ENABLED, pipelineStore);
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        return startIndex + this.storeOps;
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

