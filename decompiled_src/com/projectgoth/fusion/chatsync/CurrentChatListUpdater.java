/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.CurrentChatList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserPrx;
import org.apache.log4j.Logger;

public class CurrentChatListUpdater
extends CurrentChatList {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(CurrentChatListUpdater.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private final String username;
    private final ChatDefinition addChatKey;
    private final ChatDefinition removeChatKey;
    private final boolean debug;
    private final UserPrx userProxy;

    public CurrentChatListUpdater(String username, int userID, ChatDefinition addChatKey, ChatDefinition removeChatKey, boolean debug, UserPrx userProxy) throws FusionException {
        super(userID);
        this.username = username;
        this.addChatKey = addChatKey;
        this.removeChatKey = removeChatKey;
        this.debug = debug;
        this.userProxy = userProxy;
        if (log.isDebugEnabled() || debug) {
            log.info("CurrentChatListUpdater(): addChatKey=" + addChatKey + " removeChatKey=" + removeChatKey);
        }
    }

    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        if (log.isDebugEnabled() || this.debug) {
            log.info("In CurrentChatListUpdater.retrieve() for " + this.username);
        }
        ChatSyncStore store = stores[0];
        super.update(store, this.addChatKey, this.removeChatKey, this.username, this.debug);
        if (this.userProxy != null) {
            try {
                this.userProxy.setCurrentChatListGroupChatSubset(this.toIceObject());
            }
            catch (Exception e) {
                log.error("Call to setCurrentChatListGroupChatSubset failed for user=" + this.username + " e=" + e, e);
            }
        }
        if (log.isDebugEnabled() || this.debug) {
            log.info("CurrentChatListUpdater.retrieve() for " + this.username + ": called update()");
        }
    }

    public boolean canRetryReads() throws FusionException {
        return SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.RETRY_CURRENT_CHAT_LIST_UPDATE_ENABLED);
    }
}

