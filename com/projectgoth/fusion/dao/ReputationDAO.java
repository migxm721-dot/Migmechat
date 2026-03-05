/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao;

import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ReputationDAO {
    public SortedMap<Integer, Integer> readLevelTable();

    public int getUserScore(String var1);

    public Map<Integer, Integer> getUserScores(int[] var1);

    public int updateDailyScoreDistribution(Date var1, SortedMap<Integer, Integer> var2);

    public int updateDailyLevelDistribution(Date var1, SortedMap<Integer, Integer> var2);
}

