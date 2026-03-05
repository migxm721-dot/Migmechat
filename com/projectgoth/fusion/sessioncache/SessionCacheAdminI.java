/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.sessioncache;

import Ice.Current;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.sessioncache.SessionCache;
import com.projectgoth.fusion.sessioncache.SessionCacheI;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SessionCacheStats;
import com.projectgoth.fusion.slice._SessionCacheAdminDisp;

public class SessionCacheAdminI
extends _SessionCacheAdminDisp {
    private SessionCacheI sessionCache;

    public SessionCacheAdminI(SessionCacheI sessionCacheI) {
        this.sessionCache = sessionCacheI;
    }

    public SessionCacheStats getStats(Current __current) throws FusionException {
        SessionCacheStats stats = ServiceStatsFactory.getSessionCacheStats(SessionCache.startTime);
        try {
            stats.sessionsReceivedPerSecond = this.sessionCache.getReceivedSessionsCounter().getRequestsPerSecond();
            stats.maxSessionsReceivedPerSecond = this.sessionCache.getReceivedSessionsCounter().getMaxRequestsPerSecond();
            stats.dateOfMaxSessionsReceivedPerSecond = this.sessionCache.getReceivedSessionsCounter().getDateOfMaxRequestsPerSecond().getTime();
            stats.sessionsQueuedToBeArchived = this.sessionCache.getSessionsWaitingToBeArchived();
            stats.uniqueSummariesTaskRunning = this.sessionCache.isUniqueSummariesTaskRunning();
            stats.abortedBatches = this.sessionCache.getAbortedBatches().intValue();
        }
        catch (Exception e) {
            throw new FusionException("Initialisation incomplete");
        }
        return stats;
    }
}

