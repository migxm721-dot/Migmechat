/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ScoreAndLevel;

public interface _ReputationServiceOperationsNC {
    public void gatherAndProcess() throws FusionException;

    public void processPreviouslyDumpedData(String var1) throws FusionException;

    public void processPreviouslySortedData(String var1) throws FusionException;

    public void updateScoreFromPreviouslyProcessedData(String var1) throws FusionException;

    public void updateLastRunDate() throws FusionException;

    public int getUserLevel(String var1) throws FusionException;

    public ScoreAndLevel[] getUserScoreAndLevels(int[] var1) throws FusionException;
}

