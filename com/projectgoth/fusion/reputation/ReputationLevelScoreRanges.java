/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.reputation;

import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.jdbc.ConnectionCreator;
import com.projectgoth.fusion.common.jdbc.JdbcHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ReputationLevelScoreRanges {
    private static final Logger log = Logger.getLogger(ReputationLevelScoreRanges.class);
    private static final int ROW_TYPE_LOWER_BOUND = 0;
    private static final int ROW_TYPE_UPPER_BOUND = 1;
    public static final int MIN_SCORE = 0;
    public static final int MAX_SCORE = Integer.MAX_VALUE;
    private final NavigableMap<Integer, LevelScoreRangeEntry> levelScoreRangeEntryCache = new ConcurrentSkipListMap<Integer, LevelScoreRangeEntry>();
    private final CacheStatsHolder cacheStatsHolder = new CacheStatsHolder();
    public static volatile ReputationLevelScoreRanges instanceOverride;

    protected Long getEntryTTLMillis() {
        return SystemProperty.getLong(SystemPropertyEntities.Reputation.SCORE_TO_LEVEL_CACHE_ENTRY_TTL_MILLIS);
    }

    protected LevelScoreRangeEntry getScoreToLevelRangeFromDB(Connection conn, final int score) throws Exception {
        String query = "select scoreRange.rowtype,scoreRange.score,r3.level from (  select 0 as rowtype ,max(r1.score) as score from reputationscoretolevel r1  where r1.score <= ?  union all  select 1 as rowtype ,min(r2.score) as score from reputationscoretolevel r2  where ? < r2.score) scoreRange join reputationscoretolevel r3 on scoreRange.score = r3.score";
        if (log.isDebugEnabled()) {
            log.debug((Object)("Query=[select scoreRange.rowtype,scoreRange.score,r3.level from (  select 0 as rowtype ,max(r1.score) as score from reputationscoretolevel r1  where r1.score <= ?  union all  select 1 as rowtype ,min(r2.score) as score from reputationscoretolevel r2  where ? < r2.score) scoreRange join reputationscoretolevel r3 on scoreRange.score = r3.score] score=[" + score + "]"));
        }
        final LevelScoreRangeEntry[] result = new LevelScoreRangeEntry[]{null};
        JdbcHelper.execQuery(conn, "select scoreRange.rowtype,scoreRange.score,r3.level from (  select 0 as rowtype ,max(r1.score) as score from reputationscoretolevel r1  where r1.score <= ?  union all  select 1 as rowtype ,min(r2.score) as score from reputationscoretolevel r2  where ? < r2.score) scoreRange join reputationscoretolevel r3 on scoreRange.score = r3.score", (JdbcHelper.QueryHandler)new JdbcHelper.QueryHandlerAdapter(){

            public void setParamValues(PreparedStatement preparedStatement) throws SQLException {
                if (score >= 0) {
                    preparedStatement.setInt(1, score);
                    preparedStatement.setInt(2, score);
                } else {
                    preparedStatement.setInt(1, 0);
                    preparedStatement.setInt(2, 0);
                }
            }

            public void handle(ResultSet rs) throws SQLException {
                int scoreStarts = 0;
                int scoreEnds = Integer.MAX_VALUE;
                int currentLevel = 1;
                while (rs.next()) {
                    int rowType = rs.getInt("rowtype");
                    int score2 = rs.getInt("score");
                    int level = rs.getInt("level");
                    switch (rowType) {
                        case 0: {
                            scoreStarts = score2;
                            currentLevel = level;
                            break;
                        }
                        case 1: {
                            scoreEnds = score2;
                        }
                    }
                }
                long now = System.currentTimeMillis();
                result[0] = new LevelScoreRangeEntry(scoreStarts, scoreEnds, currentLevel, now);
            }
        });
        return result[0];
    }

    private LevelScoreRangeEntry getScoreFromDBThenCacheIt(Connection conn, int score) throws Exception {
        LevelScoreRangeEntry dbScoreEntry = this.getScoreToLevelRangeFromDB(conn, score);
        if (dbScoreEntry != null) {
            this.levelScoreRangeEntryCache.put(dbScoreEntry.scoreStart, dbScoreEntry);
        }
        return dbScoreEntry;
    }

    public LevelScoreRangeEntry getLevelScoreRange(int requestedScore, Connection conn) throws Exception {
        int score = requestedScore <= 0 ? 0 : requestedScore;
        Map.Entry<Integer, LevelScoreRangeEntry> e = this.levelScoreRangeEntryCache.floorEntry(score);
        if (e == null) {
            this.cacheStatsHolder.missedNonExist();
            return this.getScoreFromDBThenCacheIt(conn, score);
        }
        long now = System.currentTimeMillis();
        LevelScoreRangeEntry se = e.getValue();
        if (score < se.scoreEnd) {
            if (now - se.loadTime <= this.getEntryTTLMillis()) {
                this.cacheStatsHolder.hit();
                return se;
            }
            this.cacheStatsHolder.missedExpired();
            return this.getScoreFromDBThenCacheIt(conn, score);
        }
        this.cacheStatsHolder.missedNonExist();
        return this.getScoreFromDBThenCacheIt(conn, score);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LevelScoreRangeEntry getLevelScoreRange(int score, ConnectionCreator connectionCreator) throws Exception {
        LevelScoreRangeEntry levelScoreRangeEntry;
        Connection conn = connectionCreator.create();
        try {
            levelScoreRangeEntry = this.getLevelScoreRange(score, conn);
            Object var6_5 = null;
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            conn.close();
            throw throwable;
        }
        conn.close();
        return levelScoreRangeEntry;
    }

    public void invalidateCache() {
        this.levelScoreRangeEntryCache.clear();
    }

    public void resetCacheStats() {
        this.cacheStatsHolder.reset();
    }

    public CacheStats getStats() {
        return this.cacheStatsHolder.getSnapshot();
    }

    public static final ReputationLevelScoreRanges getInstance() {
        if (instanceOverride != null) {
            return instanceOverride;
        }
        return Instance.defaultInstance;
    }

    public static void setInstance(ReputationLevelScoreRanges instanceOverride) {
        ReputationLevelScoreRanges.instanceOverride = instanceOverride;
    }

    protected NavigableMap<Integer, LevelScoreRangeEntry> getLevelScoreRangeEntryCache() {
        return this.levelScoreRangeEntryCache;
    }

    public static class Instance {
        static final ReputationLevelScoreRanges defaultInstance = new ReputationLevelScoreRanges();
    }

    private static class CacheStatsHolder {
        private final AtomicLong hitCount = new AtomicLong(0L);
        private final AtomicLong missedExpiredCount = new AtomicLong(0L);
        private final AtomicLong missedNonExistCount = new AtomicLong(0L);

        private CacheStatsHolder() {
        }

        public void reset() {
            this.hitCount.set(0L);
            this.missedExpiredCount.set(0L);
            this.missedNonExistCount.set(0L);
        }

        public void hit() {
            this.hitCount.incrementAndGet();
        }

        public void missedExpired() {
            this.missedExpiredCount.incrementAndGet();
        }

        public void missedNonExist() {
            this.missedNonExistCount.incrementAndGet();
        }

        public CacheStats getSnapshot() {
            return new CacheStats(this.hitCount.get(), this.missedExpiredCount.get(), this.missedNonExistCount.get());
        }
    }

    public static class CacheStats {
        public final long hitCount;
        public final long missedExpired;
        public final long missedNonExist;

        public CacheStats(long hitCount, long missedExpired, long missedNonExist) {
            this.hitCount = hitCount;
            this.missedExpired = missedExpired;
            this.missedNonExist = missedNonExist;
        }
    }

    public static class LevelScoreRangeEntry {
        public final int scoreStart;
        public final int scoreEnd;
        public final int level;
        public final long loadTime;

        public LevelScoreRangeEntry(int scoreStart, int scoreEnd, int level, long loadTime) {
            this.scoreStart = scoreStart;
            this.scoreEnd = scoreEnd;
            this.level = level;
            this.loadTime = loadTime;
        }
    }
}

