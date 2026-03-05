/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.migme.common.DecayingScoreData
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.Transaction
 */
package com.projectgoth.fusion.authentication;

import com.migme.common.DecayingScoreData;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.FusionException;
import java.util.List;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class DecayingFailedAuthsByIPScore
extends DecayingScoreData {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DecayingFailedAuthsByIPScore.class));

    public DecayingFailedAuthsByIPScore() {
    }

    public DecayingFailedAuthsByIPScore(double score) throws Exception {
        super(score);
    }

    public DecayingFailedAuthsByIPScore(String json) throws Exception {
        this.loadFromJson(json);
    }

    public double getHalfLifeMillis() {
        return (double)SystemProperty.getInt(SystemPropertyEntities.SSO.FAILED_AUTHS_PER_IP_HALF_LIFE_SECS) * 1000.0;
    }

    protected double getScoreScale() {
        return this.getHalfLifeMillis() / Math.log(2.0);
    }

    protected double getInverseHalfLife() {
        return 1.0 / this.getHalfLifeMillis();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static DecayingFailedAuthsByIPScore load(String clientIP) throws Exception {
        DecayingFailedAuthsByIPScore decayingFailedAuthsByIPScore;
        Jedis shard;
        block4: {
            shard = null;
            String json = null;
            try {
                shard = Redis.getSlaveInstanceForEntityID(Redis.KeySpace.IP_ADDRESS, clientIP.hashCode());
                if (null != shard) {
                    String key = Redis.getFailedAuthsPerIPKey(clientIP);
                    json = shard.get(key);
                }
                if (null == json) break block4;
                decayingFailedAuthsByIPScore = new DecayingFailedAuthsByIPScore(json);
                Object var5_4 = null;
            }
            catch (Throwable throwable) {
                Object var5_6 = null;
                Redis.disconnect(shard, log);
                throw throwable;
            }
            Redis.disconnect(shard, log);
            return decayingFailedAuthsByIPScore;
        }
        decayingFailedAuthsByIPScore = new DecayingFailedAuthsByIPScore(0.0);
        Object var5_5 = null;
        Redis.disconnect(shard, log);
        return decayingFailedAuthsByIPScore;
    }

    public static boolean incrementScore(String clientIP, double scoreIncrement) throws FusionException {
        boolean bl;
        Jedis shard = null;
        try {
            boolean success;
            shard = Redis.getMasterInstanceForEntityID(Redis.KeySpace.IP_ADDRESS, clientIP.hashCode());
            boolean timedOut = false;
            long startTime = System.currentTimeMillis();
            long timeout = SystemProperty.getLong(SystemPropertyEntities.SSO.FAILED_AUTHS_PER_IP_CAS_TIMEOUT_MILLIS);
            do {
                String key = Redis.getFailedAuthsPerIPKey(clientIP);
                shard.watch(new String[]{key});
                String json = shard.get(key);
                DecayingFailedAuthsByIPScore score = json != null ? new DecayingFailedAuthsByIPScore(json) : new DecayingFailedAuthsByIPScore();
                score.addScore(scoreIncrement);
                Transaction t = shard.multi();
                t.set(key, score.toJson());
                if (SystemPropertyEntities.Temp.Cache.se445FailedAuthsExpiryFixEnabled.getValue().booleanValue()) {
                    long millisUntilHalfScore = (long)score.getTimeForUnitScore() - System.currentTimeMillis() + (long)score.getHalfLifeMillis();
                    t.expire(key, (int)(millisUntilHalfScore / 1000L));
                } else {
                    t.expire(key, (int)(score.getHalfLifeMillis() * 2.0 * 1000.0));
                }
                List results = t.exec();
                boolean bl2 = success = results != null;
                if (success) continue;
                boolean bl3 = timedOut = System.currentTimeMillis() - startTime > timeout;
                if (!timedOut) continue;
                log.warn((Object)("CAS operation on key=" + key + " timed out"));
            } while (!success && !timedOut);
            bl = success;
            Object var17_15 = null;
        }
        catch (Exception e) {
            try {
                log.error((Object)("Exception loading failed auths for [IP:" + clientIP + "] from redis, shard=" + shard + " e=" + e), (Throwable)e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var17_16 = null;
                Redis.disconnect(shard, log);
                throw throwable;
            }
        }
        Redis.disconnect(shard, log);
        return bl;
    }

    public long getLoginResponseResultDelayMillis(int scoreThreshold) {
        int delayUnit = SystemProperty.getInt(SystemPropertyEntities.SSO.FAILED_AUTHS_PER_IP_PACKET_DELAY_UNIT_SECS);
        double delayPower = SystemProperty.getDouble(SystemPropertyEntities.SSO.FAILED_AUTHS_PER_IP_PACKET_DELAY_POWER);
        double excess = this.score - (double)scoreThreshold;
        return (long)((double)delayUnit + Math.pow(excess, delayPower) * (double)delayUnit) * 1000L;
    }
}

