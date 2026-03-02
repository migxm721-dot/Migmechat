/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.userevent.store;

import Ice.Current;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.slice.EventStoreStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._EventStoreAdminDisp;
import com.projectgoth.fusion.userevent.store.BDBEventStoreI;
import com.projectgoth.fusion.userevent.store.EventStore;

public class EventStoreAdminI
extends _EventStoreAdminDisp {
    private BDBEventStoreI eventStore;

    public EventStoreAdminI(BDBEventStoreI eventStore) {
        this.eventStore = eventStore;
    }

    public EventStoreStats getStats(Current __current) throws FusionException {
        EventStoreStats stats = ServiceStatsFactory.getEventStoreStats(EventStore.startTime);
        try {
            stats.eventRate = this.eventStore.getEventsCounter().getRequestsPerSecond();
            stats.events = this.eventStore.getEventsCounter().getTotalRequests();
            stats.maxEventRate = this.eventStore.getEventsCounter().getMaxRequestsPerSecond();
            stats.generatorEventRate = this.eventStore.getGeneratorEventsCounter().getRequestsPerSecond();
            stats.generatorEvents = this.eventStore.getGeneratorEventsCounter().getTotalRequests();
            stats.maxGeneratorEventRate = this.eventStore.getGeneratorEventsCounter().getMaxRequestsPerSecond();
            stats.cacheSize = this.eventStore.getBuffer().size();
            stats.cacheExpiredEvents = this.eventStore.getCacheExpiredCounter().getTotalRequests();
            stats.cacheExpiredEventRate = this.eventStore.getCacheExpiredCounter().getRequestsPerSecond();
            stats.maxCacheExpiredEventRate = this.eventStore.getCacheExpiredCounter().getMaxRequestsPerSecond();
            stats.persistBufferEvents = this.eventStore.getPersistBufferCounter().getTotalRequests();
            stats.persistBufferRate = this.eventStore.getPersistBufferCounter().getRequestsPerSecond();
            stats.persistBufferSize = this.eventStore.getPersistBuffer().size();
            stats.maxPersistBufferRate = this.eventStore.getPersistBufferCounter().getMaxRequestsPerSecond();
        }
        catch (Exception e) {
            throw new FusionException("Initialisation incomplete");
        }
        return stats;
    }
}

