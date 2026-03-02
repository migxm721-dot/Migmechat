/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.MessageStatusEventPersistable;
import com.projectgoth.fusion.chatsync.MessageStatusEvents;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageStatusEventIce;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class MessageStatusEventsRetriever
extends MessageStatusEvents {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(MessageStatusEventsRetriever.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private final String parentUsername;
    private final ConnectionPrx parentConnection;
    private final short requestTxnId;

    public MessageStatusEventsRetriever(ChatDefinition chatKey, Long startTime, Long endTime, Integer maxResults, String parentUsername, ConnectionPrx parentCxn, short requestTxnId) throws FusionException {
        super(chatKey, startTime, endTime, maxResults, parentUsername);
        this.parentUsername = parentUsername;
        this.parentConnection = parentCxn;
        this.requestTxnId = requestTxnId;
    }

    public MessageStatusEventsRetriever(ChatDefinition chatKey, String[] messageGuids, long[] messageTimestamps, Integer maxResults, String parentUsername, ConnectionPrx parentCxn, short requestTxnId) throws FusionException {
        super(chatKey, messageTimestamps, messageGuids, maxResults, parentUsername);
        this.parentUsername = parentUsername;
        this.parentConnection = parentCxn;
        this.requestTxnId = requestTxnId;
    }

    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving message status events for user=" + this.parentUsername);
        }
        super.retrieve(stores);
        ArrayList<MessageStatusEventIce> eventsIce = new ArrayList<MessageStatusEventIce>();
        for (MessageStatusEventPersistable mse : this.events) {
            eventsIce.add(mse.toIceObject());
        }
        MessageStatusEventIce[] arr = eventsIce.toArray(new MessageStatusEventIce[eventsIce.size()]);
        if (log.isDebugEnabled()) {
            log.debug("Pushing " + arr.length + " retrieved message status events to user=" + this.parentUsername);
        }
        this.parentConnection.putMessageStatusEvents(arr, this.requestTxnId);
    }
}

