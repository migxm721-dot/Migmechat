/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 */
package com.projectgoth.fusion.reputation.util;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.reputation.cache.ScoreFormulaParameters;

public class SetScoreFormulaParameters {
    public static MemCachedClient memCached = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.broadcastList);

    public static void main(String[] args) {
        ScoreFormulaParameters params = new ScoreFormulaParameters();
        ScoreFormulaParameters.setScoreFormulaParameters(memCached, params);
    }
}

