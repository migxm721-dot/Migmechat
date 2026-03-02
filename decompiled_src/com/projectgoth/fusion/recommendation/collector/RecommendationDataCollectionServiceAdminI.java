/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.recommendation.collector;

import Ice.Current;
import com.projectgoth.fusion.recommendation.collector.DataCollectorContext;
import com.projectgoth.fusion.recommendation.collector.DataCollectorStatsCounter;
import com.projectgoth.fusion.slice.FusionExceptionWithRefCode;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServiceStats;
import com.projectgoth.fusion.slice._RecommendationDataCollectionServiceAdminDisp;

public class RecommendationDataCollectionServiceAdminI
extends _RecommendationDataCollectionServiceAdminDisp {
    private final DataCollectorContext dataCollectorCtx;

    public RecommendationDataCollectionServiceStats getStats(Current __current) throws FusionExceptionWithRefCode {
        DataCollectorStatsCounter statsCounter = this.dataCollectorCtx.getStatsCounter();
        RecommendationDataCollectionServiceStats rdcsStats = new RecommendationDataCollectionServiceStats();
        statsCounter.populate(rdcsStats);
        return rdcsStats;
    }

    public RecommendationDataCollectionServiceAdminI(DataCollectorContext dataCollectorCtx) {
        this.dataCollectorCtx = dataCollectorCtx;
    }
}

