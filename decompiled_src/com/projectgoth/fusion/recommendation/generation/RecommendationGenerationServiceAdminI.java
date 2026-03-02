/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.recommendation.generation;

import Ice.Current;
import com.projectgoth.fusion.recommendation.generation.RecommendationGenerationServiceI;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.RecommendationGenerationServiceStats;
import com.projectgoth.fusion.slice._RecommendationGenerationServiceAdminDisp;

public class RecommendationGenerationServiceAdminI
extends _RecommendationGenerationServiceAdminDisp {
    private final RecommendationGenerationServiceI service;

    public RecommendationGenerationServiceAdminI(RecommendationGenerationServiceI service) {
        this.service = service;
    }

    public RecommendationGenerationServiceStats getStats(Current __current) throws FusionException {
        try {
            return this.service.getStats();
        }
        catch (Exception e) {
            FusionException fe = new FusionException();
            fe.message = "Initialisation incomplete";
            throw fe;
        }
    }
}

