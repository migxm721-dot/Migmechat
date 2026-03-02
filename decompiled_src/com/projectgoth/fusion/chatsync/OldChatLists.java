/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.OldChatList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.FusionException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OldChatLists
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(OldChatLists.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private int userID;
    private ArrayList<OldChatList> oldChatLists = new ArrayList();

    public OldChatLists(int userID) {
        this.userID = userID;
    }

    @Override
    public String getKey() {
        return Integer.toString(this.userID);
    }

    @Override
    public String getValue() {
        return null;
    }

    public int getUserID() {
        return this.userID;
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.USER;
    }

    public void renewExpiry(SystemPropertyEntities.ChatSyncSettings toggle, ChatSyncStore store) throws FusionException {
        if (SystemProperty.getBool(toggle)) {
            int hrs = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.OLD_CHAT_LISTS_EXPIRY_HOURS);
            store.expire(this, hrs * 3600);
        }
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
    }

    public static void truncateIfNeeded(int userID, ChatSyncStore store) throws FusionException {
        int maxLength = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.OLD_CHAT_LISTS_MAX_LENGTH);
        if (maxLength == 0) {
            log.debug("Old chat lists max length truncation disabled");
            return;
        }
        OldChatLists oclKey = new OldChatLists(userID);
        store.setSlave();
        Integer size = store.llen(oclKey);
        if (size == null) {
            return;
        }
        store.setMaster();
        if (size > maxLength) {
            if (log.isDebugEnabled()) {
                log.debug("OldChatList.truncateIfNeeded: for userID=" + userID + " has length=" + size + " max length=" + maxLength + "...truncating");
            }
            for (int i = 0; i < size - maxLength; ++i) {
                store.lpop(oclKey);
            }
        }
    }

    @Override
    public void unpack(String storedKey, String storedValue) throws FusionException {
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
        pipelineStore.lrangeBinary(this, 0, -1);
    }

    @Override
    public int loadPipeline(List<Object> pipelineResults, int startIndex) throws FusionException {
        ArrayList ocls = (ArrayList)pipelineResults.get(startIndex);
        this.oldChatLists = new ArrayList();
        for (byte[] oclData : ocls) {
            OldChatList addMe = new OldChatList(this.userID, oclData);
            this.oldChatLists.add(addMe);
        }
        return startIndex + 1;
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        return 0;
    }

    public int size() {
        return this.oldChatLists.size();
    }

    public OldChatList get(int i) {
        return this.oldChatLists.get(i);
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

