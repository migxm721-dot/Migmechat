/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ScoreAndLevel;

public interface _ReputationServiceOperations {
    public void gatherAndProcess(Current var1) throws FusionException;

    public void processPreviouslyDumpedData(String var1, Current var2) throws FusionException;

    public void processPreviouslySortedData(String var1, Current var2) throws FusionException;

    public void updateScoreFromPreviouslyProcessedData(String var1, Current var2) throws FusionException;

    public void updateLastRunDate(Current var1) throws FusionException;

    public int getUserLevel(String var1, Current var2) throws FusionException;

    public ScoreAndLevel[] getUserScoreAndLevels(int[] var1, Current var2) throws FusionException;
}

