/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.eventqueue;

import Ice.Current;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.RequestAndRateLongCounter;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.eventqueue.Worker;
import com.projectgoth.fusion.eventqueue.WorkerI;
import com.projectgoth.fusion.slice.EventQueueWorkerServiceStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._EventQueueWorkerServiceAdminDisp;

public class WorkerAdminI
extends _EventQueueWorkerServiceAdminDisp {
    private WorkerI worker;

    public WorkerAdminI(WorkerI workerServant) {
        this.worker = workerServant;
    }

    public EventQueueWorkerServiceStats getStats(Current __current) throws FusionException {
        EventQueueWorkerServiceStats stats = ServiceStatsFactory.getEventQueueWorkerServiceStats(Worker.hostName, Worker.startTime);
        RequestAndRateLongCounter total = this.worker.getTotalCounter();
        stats.eventsProcessed = total.getTotalRequests();
        stats.currentEventsProcessedRate = total.getRequestsPerSecond();
        stats.peakEventsProcessedRate = total.getMaxRequestsPerSecond();
        stats.queueSize = this.worker.getQueueSize();
        stats.maxQueueSize = this.worker.getMaxQueueSize();
        for (Enums.EventTypeEnum e : Enums.EventTypeEnum.getAllTypes()) {
            RequestAndRateLongCounter c = this.worker.getEventCounter(e);
            if (c == null) continue;
            if (e.equals((Object)Enums.EventTypeEnum.FRIEND_ADDED)) {
                stats.friendsAddedEvents = c.getTotalRequests();
                stats.currentFriendsAddedEventRate = c.getRequestsPerSecond();
                stats.peakFriendsAddedEventRate = c.getMaxRequestsPerSecond();
                continue;
            }
            if (!e.equals((Object)Enums.EventTypeEnum.VIRTUAL_GIFT_PURCHASED)) continue;
            stats.virtualGiftSentEvents = c.getTotalRequests();
            stats.currentVirtualGiftEventRate = c.getRequestsPerSecond();
            stats.peakVirtualGiftEventRate = c.getMaxRequestsPerSecond();
        }
        return stats;
    }
}

