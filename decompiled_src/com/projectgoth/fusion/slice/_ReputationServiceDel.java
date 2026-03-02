/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice._ObjectDel
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice._ObjectDel;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ScoreAndLevel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _ReputationServiceDel
extends _ObjectDel {
    public void gatherAndProcess(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public void processPreviouslyDumpedData(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void processPreviouslySortedData(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void updateScoreFromPreviouslyProcessedData(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void updateLastRunDate(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public int getUserLevel(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public ScoreAndLevel[] getUserScoreAndLevels(int[] var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;
}

