/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 */
package com.projectgoth.fusion.recommendation.generation;

import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.recommendation.RecommendationTransform;
import com.projectgoth.fusion.recommendation.RecommendationTransformParam;
import com.projectgoth.fusion.recommendation.delivery.Enums;
import com.projectgoth.fusion.recommendation.delivery.RecommendationDeliveryUtils;
import com.projectgoth.fusion.recommendation.generation.ShardPipeline;
import com.projectgoth.fusion.slice.RecommendationGenerationServiceStats;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

public class RecommendationGenerationTaskHS2
extends TimerTask {
    private static final Logger log = Logger.getLogger(RecommendationGenerationTaskHS2.class);
    private static final String EXTRACT_PARAMS_REGEX = "\\{(.*?)\\}";
    private static final Pattern EXTRACT_PARAMS_PATTERN = Pattern.compile("\\{(.*?)\\}");
    private static ConcurrentHashMap<Integer, Semaphore> transformToSemaphoreMap = new ConcurrentHashMap();
    private static AtomicLong totalJobs = new AtomicLong();
    private static AtomicLong successfulJobs = new AtomicLong();
    private static AtomicLong failedJobs = new AtomicLong();
    private static AtomicLong failedJobsDueToAlreadyRunning = new AtomicLong();
    private static AtomicLong totalRecommendationsGenerated = new AtomicLong();
    private static AtomicLong totalGenerationTimeSeconds = new AtomicLong();
    private static AtomicLong shortestGenerationTimeSeconds = new AtomicLong(Long.MAX_VALUE);
    private static AtomicLong longestGenerationTimeSeconds = new AtomicLong();
    private final int transformID;

    public RecommendationGenerationTaskHS2(int transformID) {
        this.transformID = transformID;
    }

    private boolean tryAcquireTransformSemaphore() {
        return this.getTransformSemaphore().tryAcquire();
    }

    private void releaseTransformSemaphore() {
        this.getTransformSemaphore().release();
    }

    private Semaphore getTransformSemaphore() {
        Semaphore semaphore = transformToSemaphoreMap.get(this.transformID);
        if (null == semaphore) {
            semaphore = new Semaphore(1);
            transformToSemaphoreMap.putIfAbsent(this.transformID, semaphore);
            semaphore = transformToSemaphoreMap.get(this.transformID);
        }
        return semaphore;
    }

    public void run() {
        if (!SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.GENERATION_SERVICE_VIA_INTERNAL_TIMER_ENABLED)) {
            log.warn((Object)"Not running RecommendationGenerationTask via internal timer since disabled in system property");
            return;
        }
        this.transform();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void transform() {
        totalJobs.incrementAndGet();
        if (!this.tryAcquireTransformSemaphore()) {
            log.warn((Object)("Another invocation of RecommendationGenerationTask for transform " + this.transformID + " is already in progress. Exiting..."));
            failedJobsDueToAlreadyRunning.incrementAndGet();
            return;
        }
        try {
            try {
                RecommendationTransform transform = DAOFactory.getInstance().getRecommendationDAO().getRecommendationTransform(this.transformID);
                if (transform.getStatus() == TransformStatusEnum.DISABLED.value()) {
                    log.info((Object)("Not starting RecommendationGenerationTaskHS2 for transformation id=" + this.transformID + " as status==DISABLED"));
                    Object var5_3 = null;
                    this.releaseTransformSemaphore();
                    return;
                }
                long startTime = System.currentTimeMillis();
                this.transformInner(transform);
                this.setPostTransformStats(startTime);
            }
            catch (Exception e) {
                log.error((Object)("Exception running transformation with id=" + this.transformID + " e=" + e), (Throwable)e);
                failedJobs.incrementAndGet();
                Object var5_5 = null;
                this.releaseTransformSemaphore();
                return;
            }
        }
        catch (Throwable throwable) {
            Object var5_6 = null;
            this.releaseTransformSemaphore();
            throw throwable;
        }
        Object var5_4 = null;
        this.releaseTransformSemaphore();
    }

    private void setPostTransformStats(long startTime) {
        CasHelper chLongest;
        CasHelper chShortest;
        long genTime = (System.currentTimeMillis() - startTime) / 1000L;
        totalGenerationTimeSeconds.addAndGet(genTime);
        if (genTime < shortestGenerationTimeSeconds.get() && !(chShortest = new CasHelper(shortestGenerationTimeSeconds, genTime){

            public boolean stillNeedsSetting(long currentValue) {
                return this.newValue < currentValue;
            }
        }).compareAndSet()) {
            log.warn((Object)"Unable to update shortestGenerationTimeSeconds stat due to CAS collisions");
        }
        if (genTime > longestGenerationTimeSeconds.get() && !(chLongest = new CasHelper(longestGenerationTimeSeconds, genTime){

            public boolean stillNeedsSetting(long currentValue) {
                return this.newValue > currentValue;
            }
        }).compareAndSet()) {
            log.warn((Object)"Unable to update longestGenerationTimeSeconds stat due to CAS collisions");
        }
        successfulJobs.incrementAndGet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void transformInner(RecommendationTransform transform) throws Exception {
        log.info((Object)("Starting RecommendationGenerationTaskHS2 for transformation id=" + this.transformID));
        log.info((Object)("Got transform=" + transform));
        String precompScript = transform.getPrecompiledScript();
        if (log.isDebugEnabled()) {
            log.debug((Object)("retrieved precompiled hive script=" + precompScript));
        }
        if (StringUtil.isBlank(precompScript)) {
            log.info((Object)("Exiting RecommendationGenerationTaskHS2 for transformation id=" + this.transformID + " as precompiled script missing"));
            return;
        }
        String driver = SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.HIVE2_JDBC_DRIVER_CLASS);
        Class.forName(driver);
        log.info((Object)("Instantiated Hive JDBC driver=" + driver + "ok"));
        String jdbcURL = SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.HIVE2_JDBC_URL);
        String jdbcUser = SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.HIVE2_JDBC_USERNAME);
        String jdbcPassword = SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.HIVE2_JDBC_PASSWORD);
        Connection hiveConn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            log.info((Object)("Getting Hive connection with url=" + jdbcURL));
            hiveConn = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
            log.info((Object)("Got hive connection=" + hiveConn));
            String sql = transform.getPrecompiledScript();
            log.info((Object)("sql=" + sql));
            String replacedSQL = sql.replaceAll(EXTRACT_PARAMS_REGEX, "?");
            log.info((Object)("replacedSQL=" + replacedSQL));
            ps = hiveConn.prepareStatement(replacedSQL);
            this.setParams(ps, sql);
            if (log.isDebugEnabled()) {
                log.debug((Object)"Executing precompiled script");
            }
            int rows = ps.executeUpdate();
            log.info((Object)("Transform " + this.transformID + ": hive_precompile_script generated " + rows + " rows"));
            ps.clearParameters();
            rs = ps.executeQuery(transform.getRetrievalQuery());
            log.info((Object)("Transform " + this.transformID + ": hive_retrieval_query executed ok"));
            RecommendationGenerationTaskHS2.storeResultsInRedis(transform, rs);
            log.info((Object)("Transform " + this.transformID + ": hive_retrieval_query got " + rs.getRow() + " rows"));
            Object var14_13 = null;
        }
        catch (Throwable throwable) {
            Object var14_14 = null;
            DBUtils.closeResource(rs, ps, hiveConn, log);
            throw throwable;
        }
        DBUtils.closeResource(rs, ps, hiveConn, log);
    }

    private void setParams(PreparedStatement ps, String query) throws Exception {
        log.info((Object)("Set params with ps=" + ps + " query=" + query));
        Map<String, RecommendationTransformParam> paramsMap = DAOFactory.getInstance().getRecommendationDAO().getRecommendationTransformParamsByName(this.transformID);
        ArrayList matches = new ArrayList();
        Matcher m = EXTRACT_PARAMS_PATTERN.matcher(query);
        int paramIndex = 1;
        while (m.find()) {
            String paramNameBraces = m.group();
            String paramName = paramNameBraces.substring(1, paramNameBraces.length() - 1);
            log.info((Object)("Setting param=" + paramName + " to value=" + paramsMap.get(paramName).getValue()));
            ps.setString(paramIndex++, paramsMap.get(paramName).getValue());
        }
        log.info((Object)("Transform " + this.transformID + ": set " + (paramIndex - 1) + " params"));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void storeResultsInRedis(RecommendationTransform transform, ResultSet rs) throws Exception {
        Iterator i$2;
        Enums.RecommendationTargetEnum target;
        Enums.RecommendationTypeEnum recType = Enums.RecommendationTypeEnum.fromValue(transform.getType());
        if (transform.getDomain().equals("U")) {
            target = Enums.RecommendationTargetEnum.INDIVIDUAL;
        } else {
            if (!transform.getDomain().equals("CO")) {
                throw new Exception("Transform ID=" + transform.getID() + " has invalid domain=" + transform.getDomain());
            }
            target = Enums.RecommendationTargetEnum.COUNTRY;
        }
        HashMap<String, ShardPipeline> shardPipelines = new HashMap<String, ShardPipeline>();
        try {
            try {
                while (rs.next()) {
                    ShardPipeline pipeline;
                    String recommendation = rs.getString(1);
                    float weight = rs.getFloat(2);
                    int entityID = rs.getInt(3);
                    String shardDirKey = Redis.getShardKeyForEntityID(target.getRedisKeySpace(), entityID);
                    String shardID = Redis.getShardID(shardDirKey, null);
                    if (null == shardID) {
                        shardID = Redis.getNewShardID(shardDirKey, null);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("assigned new shardID=" + shardID + " for entityID=" + entityID));
                        }
                    }
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("entityID=" + entityID + ": shardDirKey=" + shardDirKey + " shardID=" + shardID));
                    }
                    if (null == (pipeline = (ShardPipeline)shardPipelines.get(shardID))) {
                        Jedis shard = Redis.getMasterInstanceForEntityID(target.getRedisKeySpace(), entityID);
                        pipeline = new ShardPipeline(shard);
                        shardPipelines.put(shardID, pipeline);
                    }
                    String key = RecommendationDeliveryUtils.generateRecommendationKey(recType, target, entityID, transform.getSubType());
                    pipeline.getPipeline().zadd(key, (double)weight, recommendation);
                    pipeline.getPipeline().expire(key, (int)transform.getExpiry());
                    pipeline.syncIfNeeded(2);
                }
                log.info((Object)("Doing final sync of " + shardPipelines.size() + " pipelines"));
                for (ShardPipeline pipeline : shardPipelines.values()) {
                    pipeline.syncFinal();
                }
                totalRecommendationsGenerated.addAndGet(rs.getRow());
            }
            catch (Exception e) {
                log.error((Object)("Exception whilst storing recommendation results in redis: e=" + e), (Throwable)e);
                throw e;
            }
            Object var13_14 = null;
            i$2 = shardPipelines.values().iterator();
        }
        catch (Throwable throwable) {
            Object var13_15 = null;
            Iterator i$2 = shardPipelines.values().iterator();
            while (true) {
                if (!i$2.hasNext()) {
                    throw throwable;
                }
                ShardPipeline shardPipeline = (ShardPipeline)i$2.next();
                shardPipeline.disconnect();
            }
        }
        while (i$2.hasNext()) {
            ShardPipeline shardPipeline = (ShardPipeline)i$2.next();
            shardPipeline.disconnect();
        }
        return;
    }

    public static void getStats(RecommendationGenerationServiceStats stats) {
        stats.totalJobs = totalJobs.get();
        stats.successfulJobs = successfulJobs.get();
        stats.failedJobs = failedJobs.get();
        stats.failedJobsDueToAlreadyRunning = failedJobsDueToAlreadyRunning.get();
        stats.totalRecommendationsGenerated = totalRecommendationsGenerated.get();
        stats.totalPipelinesUsed = ShardPipeline.getTotalPipelinesUsed();
        stats.totalGenerationTimeSeconds = totalGenerationTimeSeconds.get();
        stats.shortestGenerationTimeSeconds = shortestGenerationTimeSeconds.get();
        stats.longestGenerationTimeSeconds = longestGenerationTimeSeconds.get();
    }

    private abstract class CasHelper {
        protected AtomicLong target;
        protected long newValue;

        public CasHelper(AtomicLong target, long newValue) {
            this.target = target;
            this.newValue = newValue;
        }

        public abstract boolean stillNeedsSetting(long var1);

        public boolean compareAndSet() {
            boolean success;
            long casStart = System.currentTimeMillis();
            do {
                long current;
                if (!this.stillNeedsSetting(current = this.target.get())) {
                    return false;
                }
                success = this.target.compareAndSet(current, this.newValue);
            } while (!success && System.currentTimeMillis() - casStart < 5000L);
            return success;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TransformStatusEnum {
        DISABLED(0),
        ENABLED(1);

        final int status;

        private TransformStatusEnum(int status) {
            this.status = status;
        }

        public int value() {
            return this.status;
        }
    }
}

