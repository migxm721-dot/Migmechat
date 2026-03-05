/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao;

import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface LeaderboardDAO {
    public void generateLeaderboard(int var1);

    public List<String> getLastLoggedInForRedis(int var1);

    public List<String[]> getLastLoggedInUserScoreForRedis(int var1);
}

