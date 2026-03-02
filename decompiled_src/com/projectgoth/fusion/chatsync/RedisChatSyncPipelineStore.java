/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.Transaction
 *  redis.clients.jedis.Tuple
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncStats;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.ChatSyncStorePipeline;
import com.projectgoth.fusion.chatsync.RedisChatSyncStore;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.jedis.FusionPipelineBlock;
import com.projectgoth.fusion.slice.FusionException;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RedisChatSyncPipelineStore
extends RedisChatSyncStore {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(RedisChatSyncPipelineStore.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private FusionPipelineBlock client;

    public RedisChatSyncPipelineStore(FusionPipelineBlock client) {
        super(null);
        this.client = client;
    }

    @Override
    public ChatSyncStore.StoreType getStoreType() {
        return ChatSyncStore.StoreType.REDIS;
    }

    @Override
    public List<Object> pipelined(ChatSyncEntity entity, ChatSyncStorePipeline pipeline) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public int rpush(ChatSyncEntity entity, String value) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entity, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(entity);
            this.client.rpush(key, new String[]{value});
            return 0;
        }
        catch (Exception e) {
            this.logError("rpush (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public int rpush(ChatSyncEntity entity, byte[] value) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStats(entity, true);
            byte[] key = Redis.safeEncoderEncode(RedisChatSyncPipelineStore.getRedisKey(entity));
            this.client.rpush(key, new byte[][]{value});
            return 0;
        }
        catch (Exception e) {
            this.logError("rpush (pipelined binary)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public String lpop(ChatSyncEntity entityKey) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityKey);
            this.client.lpop(key);
            return null;
        }
        catch (Exception e) {
            this.logError("lpop (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer llen(ChatSyncEntity entityForKey) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityForKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityForKey);
            this.client.llen(key);
            return null;
        }
        catch (Exception e) {
            this.logError("llen (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public List<String> lrange(ChatSyncEntity entityForKey, int start, int end) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityForKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityForKey);
            this.client.lrange(key, start, end);
            return null;
        }
        catch (Exception e) {
            this.logError("lrange (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public List<byte[]> lrangeBinary(ChatSyncEntity entityForKey, int start, int end) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityForKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityForKey);
            this.client.lrangeBinary(Redis.safeEncoderEncode(key), start, end);
            return null;
        }
        catch (Exception e) {
            this.logError("lrange (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public String get(ChatSyncEntity entityForKey) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityForKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityForKey);
            this.client.get(key);
            return null;
        }
        catch (Exception e) {
            this.logError("get (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer incr(ChatSyncEntity entityForKey) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityForKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityForKey);
            this.client.incr(key);
            return null;
        }
        catch (Exception e) {
            this.logError("incr (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public String hget(ChatSyncEntity entityForKey, String field) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityForKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityForKey);
            this.client.hget(key, field);
            return null;
        }
        catch (Exception e) {
            this.logError("hget (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public void hset(ChatSyncEntity entityKey, String field, String value) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityKey);
            this.client.hset(key, field, value);
        }
        catch (Exception e) {
            this.logError("hset (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer hdel(ChatSyncEntity entityForKey, String field) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStats(entityForKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityForKey);
            this.client.hdel(key, new String[]{field});
            return null;
        }
        catch (Exception e) {
            this.logError("hdel (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public void lrem(ChatSyncEntity entityKey, int count, String value) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityKey);
            this.client.lrem(key, count, value);
        }
        catch (Exception e) {
            this.logError("lrem (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer sadd(ChatSyncEntity entityKey, String member) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityKey);
            this.client.sadd(key, new String[]{member});
            return null;
        }
        catch (Exception e) {
            this.logError("sadd (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Set<String> smembers(ChatSyncEntity entityKey) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityKey);
            this.client.smembers(key);
            return null;
        }
        catch (Exception e) {
            this.logError("smembers (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer srem(ChatSyncEntity entityKey, String member) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityKey);
            this.client.srem(key, new String[]{member});
            return null;
        }
        catch (Exception e) {
            this.logError("srem (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer zadd(ChatSyncEntity forKey, double score, String member) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zadd with client=" + (Object)((Object)this.client) + " key=" + key + " score=" + score);
            }
            this.client.zadd(key, score, member);
            if (log.isDebugEnabled()) {
                log.debug("Called Redis zadd with client=" + (Object)((Object)this.client) + " key=" + key + " score=" + score);
            }
            return null;
        }
        catch (Exception e) {
            this.logError("zadd (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer zadd(ChatSyncEntity forKey, double score, byte[] member) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zadd (binary) with client=" + (Object)((Object)this.client) + " key=" + key + " score=" + score);
            }
            this.client.zadd(Redis.safeEncoderEncode(key), score, member);
            if (log.isDebugEnabled()) {
                log.debug("Called Redis zadd (binary) with client=" + (Object)((Object)this.client) + " key=" + key + " score=" + score);
            }
            return null;
        }
        catch (Exception e) {
            this.logError("zadd (pipelined, binary)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Set<String> zrange(ChatSyncEntity forKey, int start, int end) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrange with client=" + (Object)((Object)this.client) + " key=" + key + " start=" + start + " end=" + end);
            }
            this.client.zrange(key, start, end);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrange with client=" + (Object)((Object)this.client) + " key=" + key + " start=" + start + " end=" + end);
            }
            return null;
        }
        catch (Exception e) {
            this.logError("zrange (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Set<byte[]> zrangeBinary(ChatSyncEntity forKey, int start, int end) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrange with client=" + (Object)((Object)this.client) + " key=" + key + " start=" + start + " end=" + end);
            }
            this.client.zrangeBinary(Redis.safeEncoderEncode(key), start, end);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrange with client=" + (Object)((Object)this.client) + " key=" + key + " start=" + start + " end=" + end);
            }
            return null;
        }
        catch (Exception e) {
            this.logError("zrange (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Set<String> zrangeByScore(ChatSyncEntity forKey, double start, double end) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrangebyscore with client=" + (Object)((Object)this.client) + " key=" + key + " start=" + start + " end=" + end);
            }
            this.client.zrangeByScore(key, start, end);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrangebyscore with client=" + (Object)((Object)this.client) + " key=" + key + " start=" + start + " end=" + end);
            }
            return null;
        }
        catch (Exception e) {
            this.logError("zrangeByScore (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(ChatSyncEntity forKey, double start, double end) throws FusionException {
        Set<Tuple> set;
        Jedis instance = null;
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            this.client.zrangeByScoreWithScores(Redis.safeEncoderEncode(key), start, end);
            set = null;
            Object var10_8 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zrangeByScoreWithScores (pipelined)", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var10_9 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return set;
    }

    @Override
    public Set<String> zrangeByScore(ChatSyncEntity forKey, double start, double end, int offset, int count) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrangebyscore with key=" + key + " start=" + start + " end=" + end + " offset=" + offset + " count=" + count);
            }
            this.client.zrangeByScore(key, start, end, offset, count);
            return null;
        }
        catch (Exception e) {
            this.logError("zrangeByScore (pipelined) with offset,count", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer zrank(ChatSyncEntity forKey, String member) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrank with client=" + (Object)((Object)this.client) + " key=" + key + " member=" + member);
            }
            this.client.zrank(key, member);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrank with client=" + (Object)((Object)this.client) + " key=" + key + " member=" + member);
            }
            return null;
        }
        catch (Exception e) {
            this.logError("zrank (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer zrankBinary(ChatSyncEntity forKey, byte[] member) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public Integer expire(ChatSyncEntity entityKey, int seconds) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis expire with client=" + (Object)((Object)this.client) + " key=" + key + " seconds=" + seconds);
            }
            this.client.expire(key, seconds);
            return null;
        }
        catch (Exception e) {
            this.logError("expire (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer ttl(ChatSyncEntity entityKey) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityKey);
            this.client.ttl(key);
            if (log.isDebugEnabled()) {
                log.debug("Redis ttl with client=" + (Object)((Object)this.client) + " key=" + key);
            }
            return null;
        }
        catch (Exception e) {
            this.logError("ttl (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer zrem(ChatSyncEntity forKey, String member) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrem with client=" + (Object)((Object)this.client) + " key=" + key + " member=" + member);
            }
            this.client.zrem(key, new String[]{member});
            return null;
        }
        catch (Exception e) {
            this.logError("zrem (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer zremrangeByRank(ChatSyncEntity forKey, int start, int end) throws FusionException {
        Object instance = null;
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            this.client.zremrangeByRank(key, start, end);
            return null;
        }
        catch (Exception e) {
            this.logError("zremrangeByRank (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Integer zcount(ChatSyncEntity entityKey, double min, double max) throws FusionException {
        Object instance = null;
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(entityKey, true);
            String key = RedisChatSyncPipelineStore.getRedisKey(entityKey);
            this.client.zcount(key, min, max);
            return null;
        }
        catch (Exception e) {
            this.logError("zcount (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Set<String> zrevrangeByScore(ChatSyncEntity forKey, double start, double end, int offset, int count) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrevrangebyscore with key=" + key + " start=" + start + " end=" + end + " offset=" + offset + " count=" + count);
            }
            this.zrevrangeByScore(this.client, key, start, end, offset, count, false);
            return null;
        }
        catch (Exception e) {
            this.logError("zrevrangeByScore (pipelined) with offset,count", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(ChatSyncEntity forKey, double start, double end, int offset, int count) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrevrangeByScoreWithScores with key=" + key + " start=" + start + " end=" + end + " offset=" + offset + " count=" + count);
            }
            this.zrevrangeByScore(this.client, key, start, end, offset, count, true);
            return null;
        }
        catch (Exception e) {
            this.logError("zrevrangeByScoreWithScores (pipelined) with offset,count", e);
            throw new FusionException(e.getMessage());
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(ChatSyncEntity forKey, double start, double end) throws FusionException {
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, false);
            String key = RedisChatSyncPipelineStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrevrangeByScoreWithScores with key=" + key + " start=" + start + " end=" + end);
            }
            this.client.zrevrangeByScoreWithScores(key, start, end);
            return null;
        }
        catch (Exception e) {
            this.logError("zrevrangeByScoreWithScores (pipelined) with offset,count", e);
            throw new FusionException(e.getMessage());
        }
    }

    private Object zrevrangeByScore(FusionPipelineBlock cli, String key, double max, double min, int offset, int count, boolean withScores) throws Exception {
        if (withScores) {
            return cli.zrevrangeByScoreWithScores(Redis.safeEncoderEncode(key), max, min, offset, count);
        }
        return cli.zrevrangeByScoreBinary(Redis.safeEncoderEncode(key), max, min, offset, count);
    }

    @Override
    public Transaction multi(ChatSyncEntity forKey) throws FusionException {
        Object instance = null;
        try {
            ChatSyncStats.getInstance().incrementRedisStatsPipelined(forKey, true);
            this.client.multi();
            return null;
        }
        catch (Exception e) {
            this.logError("multi (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public void execTxn(ChatSyncEntity forKey) throws FusionException {
        try {
            this.client.exec();
        }
        catch (Exception e) {
            this.logError("execTxn (pipelined)", e);
            throw new FusionException(e.getMessage());
        }
    }
}

