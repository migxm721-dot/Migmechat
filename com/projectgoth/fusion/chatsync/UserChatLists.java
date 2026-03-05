/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncPipelineOp;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.CurrentChatList;
import com.projectgoth.fusion.chatsync.OldChatLists;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.FusionException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UserChatLists
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(UserChatLists.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    protected int userID;
    protected CurrentChatList currentChatList;
    protected OldChatLists oldChatLists;

    public UserChatLists(int userID) {
        if (log.isDebugEnabled()) {
            log.debug("Entering UserChatLists.UserChatLists with userID=" + userID);
        }
        this.userID = userID;
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.USER;
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug("Entering UserChatLists.retrieve with userID=" + this.userID);
        }
        ArrayList<ChatSyncPipelineOp> ops = new ArrayList<ChatSyncPipelineOp>();
        try {
            this.oldChatLists = new OldChatLists(this.userID);
            this.currentChatList = new CurrentChatList(this.userID);
            ops.add(new ChatSyncPipelineOp(this.oldChatLists, ChatSyncPipelineOp.OpType.READ));
            ops.add(new ChatSyncPipelineOp(this.currentChatList, ChatSyncPipelineOp.OpType.READ));
            for (ChatSyncStore store : stores) {
                store.setSlave();
                ChatSyncPipelineOp[] opsArray = ops.toArray(new ChatSyncPipelineOp[0]);
                store.pipelinedBinary(opsArray);
                store.setMaster();
                this.oldChatLists.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_READ_ENABLED, store);
                this.currentChatList.renewExpiry(store);
                if (!log.isDebugEnabled()) continue;
                log.debug("Found " + this.currentChatList.getChatIDs().length + " current chats and " + this.oldChatLists.size() + " old chat lists " + "for userID=" + this.userID + " in store " + store);
            }
        }
        catch (FusionException e) {
            log.error((Object)e);
        }
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
    public void unpack(String storedKey, String storedValue) throws FusionException {
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
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
}

