/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  redis.clients.jedis.Transaction
 *  redis.clients.jedis.Tuple
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncPipelineOp;
import com.projectgoth.fusion.chatsync.ChatSyncStorePipeline;
import com.projectgoth.fusion.slice.FusionException;
import java.util.List;
import java.util.Set;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ChatSyncStore {
    public StoreType getStoreType();

    public StorePrimacy getStorePrimacy();

    public void setMaster();

    public void setSlave();

    public List<Object> pipelined(ChatSyncPipelineOp[] var1) throws FusionException;

    public List<Object> pipelined(List<ChatSyncPipelineOp> var1) throws FusionException;

    public List<Object> pipelinedBinary(ChatSyncPipelineOp[] var1) throws FusionException;

    public List<Object> pipelinedBinary(List<ChatSyncPipelineOp> var1) throws FusionException;

    public List<Object> pipelined(ChatSyncEntity var1, ChatSyncStorePipeline var2) throws FusionException;

    public List<Object> pipelinedBinary(ChatSyncEntity var1, ChatSyncStorePipeline var2) throws FusionException;

    public String lpop(ChatSyncEntity var1) throws FusionException;

    public Integer llen(ChatSyncEntity var1) throws FusionException;

    public void lrem(ChatSyncEntity var1, int var2, String var3) throws FusionException;

    public int rpush(ChatSyncEntity var1, String var2) throws FusionException;

    public int rpush(ChatSyncEntity var1, byte[] var2) throws FusionException;

    public List<String> lrange(ChatSyncEntity var1, int var2, int var3) throws FusionException;

    public List<byte[]> lrangeBinary(ChatSyncEntity var1, int var2, int var3) throws FusionException;

    public String get(ChatSyncEntity var1) throws FusionException;

    public Integer incr(ChatSyncEntity var1) throws FusionException;

    public String hget(ChatSyncEntity var1, String var2) throws FusionException;

    public void hset(ChatSyncEntity var1, String var2, String var3) throws FusionException;

    public Integer hdel(ChatSyncEntity var1, String var2) throws FusionException;

    public Integer sadd(ChatSyncEntity var1, String var2) throws FusionException;

    public Set<String> smembers(ChatSyncEntity var1) throws FusionException;

    public Integer srem(ChatSyncEntity var1, String var2) throws FusionException;

    public Integer zadd(ChatSyncEntity var1, double var2, String var4) throws FusionException;

    public Integer zadd(ChatSyncEntity var1, double var2, byte[] var4) throws FusionException;

    public Set<String> zrange(ChatSyncEntity var1, int var2, int var3) throws FusionException;

    public Set<byte[]> zrangeBinary(ChatSyncEntity var1, int var2, int var3) throws FusionException;

    public Set<String> zrangeByScore(ChatSyncEntity var1, double var2, double var4) throws FusionException;

    public Set<Tuple> zrangeByScoreWithScores(ChatSyncEntity var1, double var2, double var4) throws FusionException;

    public Set<String> zrangeByScore(ChatSyncEntity var1, double var2, double var4, int var6, int var7) throws FusionException;

    public Set<String> zrevrangeByScore(ChatSyncEntity var1, double var2, double var4, int var6, int var7) throws FusionException;

    public Set<Tuple> zrevrangeByScoreWithScores(ChatSyncEntity var1, double var2, double var4, int var6, int var7) throws FusionException;

    public Integer zrank(ChatSyncEntity var1, String var2) throws FusionException;

    public Integer zrankBinary(ChatSyncEntity var1, byte[] var2) throws FusionException;

    public Integer zrem(ChatSyncEntity var1, String var2) throws FusionException;

    public Integer zremrangeByRank(ChatSyncEntity var1, int var2, int var3) throws FusionException;

    public Integer zcount(ChatSyncEntity var1, double var2, double var4) throws FusionException;

    public Integer expire(ChatSyncEntity var1, int var2) throws FusionException;

    public Integer ttl(ChatSyncEntity var1) throws FusionException;

    public Transaction multi(ChatSyncEntity var1) throws FusionException;

    public void execTxn(ChatSyncEntity var1) throws FusionException;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StorePrimacy {
        MASTER,
        SLAVE;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StoreType {
        REDIS,
        MYSQL;

    }
}

