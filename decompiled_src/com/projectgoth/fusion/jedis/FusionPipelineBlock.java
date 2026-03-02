/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  redis.clients.jedis.BuilderFactory
 *  redis.clients.jedis.Client
 *  redis.clients.jedis.PipelineBlock
 *  redis.clients.jedis.Protocol
 *  redis.clients.jedis.Response
 *  redis.clients.jedis.Tuple
 */
package com.projectgoth.fusion.jedis;

import java.util.List;
import java.util.Set;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.Client;
import redis.clients.jedis.PipelineBlock;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.Response;
import redis.clients.jedis.Tuple;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class FusionPipelineBlock
extends PipelineBlock {
    private Client cli;

    public Response<Set<byte[]>> zrangeBinary(byte[] key, int start, int end) {
        this.cli.zrange(key, (long)start, (long)end);
        return this.getResponse(BuilderFactory.BYTE_ARRAY_ZSET);
    }

    public Response<Set<Tuple>> zrevrangeByScoreWithScores(byte[] key, double max, double min, int offset, int count) {
        this.cli.zrevrangeByScoreWithScores(key, Protocol.toByteArray((double)max), Protocol.toByteArray((double)min), offset, count);
        return this.getResponse(BuilderFactory.TUPLE_ZSET_BINARY);
    }

    public Response<Set<Tuple>> zrangeByScoreWithScores(byte[] key, double max, double min) {
        this.cli.zrangeByScoreWithScores(key, Protocol.toByteArray((double)max), Protocol.toByteArray((double)min));
        return this.getResponse(BuilderFactory.TUPLE_ZSET_BINARY);
    }

    public Response<Set<byte[]>> zrevrangeByScoreBinary(byte[] key, double max, double min, int offset, int count) {
        this.cli.zrevrangeByScore(key, Protocol.toByteArray((double)max), Protocol.toByteArray((double)min), offset, count);
        return this.getResponse(BuilderFactory.BYTE_ARRAY_ZSET);
    }

    public Response<List<byte[]>> lrangeBinary(byte[] key, long start, long end) {
        this.cli.lrange(key, start, end);
        return this.getResponse(BuilderFactory.BYTE_ARRAY_LIST);
    }

    public void setClient(Client client) {
        super.setClient(client);
        this.cli = client;
    }
}

