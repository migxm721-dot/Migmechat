/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.messagelogger;

import Ice.Current;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.messagelogger.MessageLogger;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageLoggerStats;
import com.projectgoth.fusion.slice._MessageLoggerAdminDisp;

public class MessageLoggerAdminI
extends _MessageLoggerAdminDisp {
    public MessageLoggerStats getStats(Current __current) throws FusionException {
        MessageLoggerStats stats = ServiceStatsFactory.getMessageLoggerStats(MessageLogger.startTime);
        try {
            stats.numMessagesReceivedPerSecond = MessageLogger.messageLogger.receivedMessagesCounter.getRequestsPerSecond();
            stats.maxMessagesReceivedPerSecond = MessageLogger.messageLogger.receivedMessagesCounter.getMaxRequestsPerSecond();
            stats.numMessagesLoggedPerSecond = MessageLogger.messageLogger.loggedMessagesCounter.getRequestsPerSecond();
            stats.maxMessagesLoggedPerSecond = MessageLogger.messageLogger.loggedMessagesCounter.getMaxRequestsPerSecond();
            stats.numMessagesQueued = MessageLogger.messageLogger.numMessagesQueued;
            stats.maxMessagesQueued = MessageLogger.messageLogger.maxMessagesQueued;
        }
        catch (Exception e) {
            throw new FusionException("Initialisation incomplete");
        }
        return stats;
    }
}

