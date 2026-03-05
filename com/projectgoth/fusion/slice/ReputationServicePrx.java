/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ScoreAndLevel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ReputationServicePrx
extends ObjectPrx {
    public void gatherAndProcess() throws FusionException;

    public void gatherAndProcess(Map<String, String> var1) throws FusionException;

    public void processPreviouslyDumpedData(String var1) throws FusionException;

    public void processPreviouslyDumpedData(String var1, Map<String, String> var2) throws FusionException;

    public void processPreviouslySortedData(String var1) throws FusionException;

    public void processPreviouslySortedData(String var1, Map<String, String> var2) throws FusionException;

    public void updateScoreFromPreviouslyProcessedData(String var1) throws FusionException;

    public void updateScoreFromPreviouslyProcessedData(String var1, Map<String, String> var2) throws FusionException;

    public void updateLastRunDate() throws FusionException;

    public void updateLastRunDate(Map<String, String> var1) throws FusionException;

    public int getUserLevel(String var1) throws FusionException;

    public int getUserLevel(String var1, Map<String, String> var2) throws FusionException;

    public ScoreAndLevel[] getUserScoreAndLevels(int[] var1) throws FusionException;

    public ScoreAndLevel[] getUserScoreAndLevels(int[] var1, Map<String, String> var2) throws FusionException;
}

