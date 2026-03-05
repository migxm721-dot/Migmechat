/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.PipelineBlock
 *  redis.clients.jedis.Transaction
 *  redis.clients.jedis.Tuple
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatListVersion;
import com.projectgoth.fusion.chatsync.ChatMessage;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncPipelineOp;
import com.projectgoth.fusion.chatsync.ChatSyncStats;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.ChatSyncStorePipeline;
import com.projectgoth.fusion.chatsync.CurrentChatList;
import com.projectgoth.fusion.chatsync.MessageStatusEvent;
import com.projectgoth.fusion.chatsync.MessageStatusEventKey;
import com.projectgoth.fusion.chatsync.MessageStatusEvents;
import com.projectgoth.fusion.chatsync.OldChatList;
import com.projectgoth.fusion.chatsync.OldChatLists;
import com.projectgoth.fusion.chatsync.ParticipantList;
import com.projectgoth.fusion.chatsync.RedisChatSyncPipelineStore;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.jedis.FusionPipelineBlock;
import com.projectgoth.fusion.slice.FusionException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.PipelineBlock;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RedisChatSyncStore
implements ChatSyncStore {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(RedisChatSyncStore.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private static AtomicInteger totalPipelineExceptions = new AtomicInteger(0);
    private ChatSyncStore.StorePrimacy primacy;
    private ChatSyncEntity lastEntity;
    private Jedis lastMasterInstance;
    private Jedis lastSlaveInstance;

    public static int getTotalPipelineExceptions() {
        return totalPipelineExceptions.get();
    }

    protected void logError(String message, Throwable t) {
        log.error(message, t);
        if (log.isDebugEnabled()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            log.error(sw.toString());
        }
    }

    public RedisChatSyncStore(ChatSyncStore.StorePrimacy primacy) {
        this.primacy = primacy;
    }

    @Override
    public ChatSyncStore.StorePrimacy getStorePrimacy() {
        return this.primacy;
    }

    @Override
    public void setMaster() {
        this.primacy = ChatSyncStore.StorePrimacy.MASTER;
    }

    @Override
    public void setSlave() {
        this.primacy = ChatSyncStore.StorePrimacy.SLAVE;
    }

    @Override
    public ChatSyncStore.StoreType getStoreType() {
        return ChatSyncStore.StoreType.REDIS;
    }

    protected Jedis getInstance(ChatSyncEntity entity) throws Exception {
        if (entity.getEntityType() == ChatSyncEntity.ChatSyncEntityType.MESSAGE_STATUS_EVENT) {
            MessageStatusEventKey mseKey;
            if (entity instanceof MessageStatusEvents) {
                MessageStatusEvents events = (MessageStatusEvents)entity;
                mseKey = events.getEventsKey();
            } else if (entity instanceof MessageStatusEventKey) {
                mseKey = (MessageStatusEventKey)entity;
            } else {
                throw new FusionException("Unknown entity of type MESSAGE_STATUS_EVENT, entity=" + entity);
            }
            if (this.primacy.equals((Object)ChatSyncStore.StorePrimacy.MASTER)) {
                return Redis.getMasterInstanceForEntityID(Redis.KeySpace.CONVERSATION_ENTITY, mseKey.getChatKey().hashCode());
            }
            return Redis.getSlaveInstanceForEntityID(Redis.KeySpace.CONVERSATION_ENTITY, mseKey.getChatKey().hashCode());
        }
        if (entity.getEntityType() == ChatSyncEntity.ChatSyncEntityType.CONVERSATION || entity.getEntityType() == ChatSyncEntity.ChatSyncEntityType.MESSAGE) {
            if (this.primacy.equals((Object)ChatSyncStore.StorePrimacy.MASTER)) {
                return Redis.getMasterInstanceForEntityID(Redis.KeySpace.CONVERSATION_ENTITY, entity.getKey().hashCode());
            }
            return Redis.getSlaveInstanceForEntityID(Redis.KeySpace.CONVERSATION_ENTITY, entity.getKey().hashCode());
        }
        if (entity.getEntityType() == ChatSyncEntity.ChatSyncEntityType.USER) {
            if (this.primacy.equals((Object)ChatSyncStore.StorePrimacy.MASTER)) {
                if (log.isDebugEnabled()) {
                    log.debug("Getting master Redis instance for user=" + entity.getKey());
                }
                return Redis.getMasterInstanceForUserID(Integer.parseInt(entity.getKey()));
            }
            return Redis.getSlaveInstanceForUserID(Integer.parseInt(entity.getKey()));
        }
        throw new FusionException("Invalid ChatSyncEntity type in getInstance: type=" + entity.getEntityType().toString());
    }

    public static String getRedisKey(ChatSyncEntity entity) throws FusionException {
        if (entity instanceof ChatDefinition) {
            return Redis.getConversationDefinitionKey(entity.getKey());
        }
        if (entity instanceof ChatMessage) {
            return Redis.getConversationMessageKey(entity.getKey());
        }
        if (entity instanceof MessageStatusEvent || entity instanceof MessageStatusEventKey) {
            MessageStatusEventKey mseKey = (MessageStatusEventKey)entity;
            return Redis.getMessageStatusEventKey(mseKey);
        }
        if (entity instanceof ParticipantList) {
            return Redis.getConversationParticipantsKey(entity.getKey());
        }
        if (entity instanceof OldChatList) {
            OldChatList cl = (OldChatList)entity;
            return Redis.getOldChatListsKey(cl.getUserID());
        }
        if (entity instanceof OldChatLists) {
            OldChatLists cl = (OldChatLists)entity;
            return Redis.getOldChatListsKey(cl.getUserID());
        }
        if (entity instanceof CurrentChatList) {
            CurrentChatList cl = (CurrentChatList)entity;
            return Redis.getCurrentChatListKey(cl.getUserID());
        }
        if (entity instanceof ChatListVersion) {
            ChatListVersion clv = (ChatListVersion)entity;
            return Redis.getChatListVersionKey(clv.getUserID());
        }
        throw new FusionException("Invalid ChatSyncEntity type in getRedisKey: entity=" + entity);
    }

    @Override
    public List<Object> pipelined(List<ChatSyncPipelineOp> pipelineOps) throws FusionException {
        ChatSyncPipelineOp[] opsArray = pipelineOps.toArray(new ChatSyncPipelineOp[pipelineOps.size()]);
        return this.pipelined(opsArray);
    }

    @Override
    public List<Object> pipelinedBinary(List<ChatSyncPipelineOp> pipelineOps) throws FusionException {
        ChatSyncPipelineOp[] opsArray = pipelineOps.toArray(new ChatSyncPipelineOp[pipelineOps.size()]);
        return this.pipelinedBinary(opsArray);
    }

    @Override
    public List<Object> pipelined(ChatSyncPipelineOp[] pipelineOps) throws FusionException {
        List<Object> results = this.pipelinedBinaryOps(pipelineOps);
        int startIndex = 0;
        for (ChatSyncPipelineOp op : pipelineOps) {
            startIndex = op.processResults(results, startIndex);
        }
        return results;
    }

    @Override
    public List<Object> pipelinedBinary(ChatSyncPipelineOp[] pipelineOps) throws FusionException {
        List<Object> results = this.pipelinedBinaryOps(pipelineOps);
        int startIndex = 0;
        for (ChatSyncPipelineOp op : pipelineOps) {
            startIndex = op.processResults(results, startIndex);
        }
        return results;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private List<Object> pipelinedBinaryOps(final ChatSyncPipelineOp[] pipelineOps) throws FusionException {
        List list;
        Jedis instance;
        block9: {
            List<Object> list2;
            block8: {
                instance = null;
                try {
                    try {
                        List results;
                        if (pipelineOps.length == 0) {
                            throw new FusionException("pipelineOps should not be empty");
                        }
                        boolean isStorage = this.primacy.equals((Object)ChatSyncStore.StorePrimacy.MASTER);
                        ChatSyncStats.getInstance().incrementRedisStats(pipelineOps[0].getEntity(), isStorage);
                        instance = this.getInstance(pipelineOps[0].getEntity());
                        if (instance == null) {
                            if (!this.primacy.equals((Object)ChatSyncStore.StorePrimacy.SLAVE)) throw new FusionException("Couldn't retrieve shard for entity " + pipelineOps[0].getEntity());
                            list2 = null;
                            Object var7_8 = null;
                            break block8;
                        }
                        list = results = instance.pipelined((PipelineBlock)new FusionPipelineBlock(){

                            public void execute() {
                                try {
                                    RedisChatSyncPipelineStore wrapper = new RedisChatSyncPipelineStore(this);
                                    for (ChatSyncPipelineOp op : pipelineOps) {
                                        ChatSyncEntity entity = op.getEntity();
                                        if (op.isRead()) {
                                            entity.retrievePipeline(wrapper);
                                            continue;
                                        }
                                        entity.storePipeline(wrapper);
                                    }
                                }
                                catch (FusionException e) {
                                    RedisChatSyncStore.this.logError("pipelined(ops)", (Throwable)((Object)e));
                                    totalPipelineExceptions.incrementAndGet();
                                    throw new RuntimeException((Throwable)((Object)e));
                                }
                            }
                        });
                        break block9;
                    }
                    catch (FusionException e) {
                        this.logError("pipelined(ops)", (Throwable)((Object)e));
                        throw e;
                    }
                    catch (Exception e) {
                        this.logError("pipelined(ops)", e);
                        throw new FusionException(e.getMessage());
                    }
                }
                catch (Throwable throwable) {
                    Object var7_10 = null;
                    Redis.disconnect(instance, log.getLogger());
                    throw throwable;
                }
            }
            Redis.disconnect(instance, log.getLogger());
            return list2;
        }
        Object var7_9 = null;
        Redis.disconnect(instance, log.getLogger());
        return list;
    }

    @Override
    public List<Object> pipelined(ChatSyncEntity entity, ChatSyncStorePipeline pipeline) throws FusionException {
        List<Object> results = this.pipelinedBinary(entity, pipeline);
        return results;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public List<Object> pipelinedBinary(final ChatSyncEntity entity, final ChatSyncStorePipeline pipeline) throws FusionException {
        List list;
        Jedis instance;
        block8: {
            List<Object> list2;
            block7: {
                instance = null;
                try {
                    try {
                        List results;
                        boolean isStorage = this.primacy.equals((Object)ChatSyncStore.StorePrimacy.MASTER);
                        ChatSyncStats.getInstance().incrementRedisStats(entity, isStorage);
                        instance = this.getInstance(entity);
                        if (instance == null) {
                            if (!this.primacy.equals((Object)ChatSyncStore.StorePrimacy.SLAVE)) throw new FusionException("Couldn't retrieve shard for entity " + entity);
                            list2 = null;
                            Object var8_9 = null;
                            break block7;
                        }
                        list = results = instance.pipelined((PipelineBlock)new FusionPipelineBlock(){

                            public void execute() {
                                try {
                                    RedisChatSyncPipelineStore wrapper = new RedisChatSyncPipelineStore(this);
                                    pipeline.execute(entity, wrapper);
                                }
                                catch (FusionException e) {
                                    RedisChatSyncStore.this.logError("pipelined(entity)", (Throwable)((Object)e));
                                    totalPipelineExceptions.incrementAndGet();
                                    throw new RuntimeException((Throwable)((Object)e));
                                }
                            }
                        });
                        break block8;
                    }
                    catch (FusionException e) {
                        this.logError("pipelined(entity)", (Throwable)((Object)e));
                        throw e;
                    }
                    catch (Exception e) {
                        this.logError("pipelined(entity)", e);
                        throw new FusionException(e.getMessage());
                    }
                }
                catch (Throwable throwable) {
                    Object var8_11 = null;
                    Redis.disconnect(instance, log.getLogger());
                    throw throwable;
                }
            }
            Redis.disconnect(instance, log.getLogger());
            return list2;
        }
        Object var8_10 = null;
        Redis.disconnect(instance, log.getLogger());
        return list;
    }

    @Override
    public int rpush(ChatSyncEntity entity, String value) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public int rpush(ChatSyncEntity entity, byte[] value) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public String lpop(ChatSyncEntity entityKey) throws FusionException {
        String string;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityKey, true);
            instance = this.getInstance(entityKey);
            if (instance != null) break block5;
            String string2 = null;
            Object var6_6 = null;
            Redis.disconnect(instance, log.getLogger());
            return string2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(entityKey);
            string = instance.lpop(key);
            Object var6_7 = null;
        }
        catch (Exception e) {
            try {
                this.logError("lpop", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var6_8 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return string;
    }

    @Override
    public Integer llen(ChatSyncEntity entityForKey) throws FusionException {
        Integer n;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityForKey, false);
            instance = this.getInstance(entityForKey);
            if (instance != null) break block5;
            Integer n2 = null;
            Object var7_6 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(entityForKey);
            Long result = instance.llen(key);
            n = result == null ? null : Integer.valueOf(result.intValue());
            Object var7_7 = null;
        }
        catch (Exception e) {
            try {
                this.logError("llen", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public List<String> lrange(ChatSyncEntity entityForKey, int start, int end) throws FusionException {
        List list;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityForKey, false);
            instance = this.getInstance(entityForKey);
            if (instance != null) break block5;
            List<String> list2 = null;
            Object var8_8 = null;
            Redis.disconnect(instance, log.getLogger());
            return list2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(entityForKey);
            list = instance.lrange(key, (long)start, (long)end);
            Object var8_9 = null;
        }
        catch (Exception e) {
            try {
                this.logError("lrange", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return list;
    }

    @Override
    public List<byte[]> lrangeBinary(ChatSyncEntity forKey, int start, int end) throws FusionException {
        List list;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(forKey, false);
            instance = this.getInstance(forKey);
            if (instance != null) break block5;
            List<byte[]> list2 = null;
            Object var10_8 = null;
            Redis.disconnect(instance, log.getLogger());
            return list2;
        }
        try {
            List results;
            String key = RedisChatSyncStore.getRedisKey(forKey);
            byte[] keyBytes = Redis.safeEncoderEncode(key);
            list = results = instance.lrange(keyBytes, (long)start, (long)end);
            Object var10_9 = null;
        }
        catch (Exception e) {
            try {
                this.logError("lrangeBinary", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var10_10 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return list;
    }

    @Override
    public String get(ChatSyncEntity entityForKey) throws FusionException {
        String string;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityForKey, false);
            instance = this.getInstance(entityForKey);
            if (instance != null) break block5;
            String string2 = null;
            Object var6_6 = null;
            Redis.disconnect(instance, log.getLogger());
            return string2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(entityForKey);
            string = instance.get(key);
            Object var6_7 = null;
        }
        catch (Exception e) {
            try {
                this.logError("get", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var6_8 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return string;
    }

    @Override
    public Integer incr(ChatSyncEntity entityForKey) throws FusionException {
        Integer n;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityForKey, true);
            instance = this.getInstance(entityForKey);
            if (instance != null) break block5;
            Integer n2 = null;
            Object var7_6 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(entityForKey);
            Long result = instance.incr(key);
            n = result == null ? null : Integer.valueOf(result.intValue());
            Object var7_7 = null;
        }
        catch (Exception e) {
            try {
                this.logError("incr", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public String hget(ChatSyncEntity entityForKey, String field) throws FusionException {
        String string;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityForKey, false);
            instance = this.getInstance(entityForKey);
            if (instance != null) break block5;
            String string2 = null;
            Object var7_7 = null;
            Redis.disconnect(instance, log.getLogger());
            return string2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(entityForKey);
            string = instance.hget(key, field);
            Object var7_8 = null;
        }
        catch (Exception e) {
            try {
                this.logError("hget", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_9 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return string;
    }

    @Override
    public void hset(ChatSyncEntity entityKey, String field, String value) throws FusionException {
        Jedis instance = null;
        try {
            try {
                ChatSyncStats.getInstance().incrementRedisStats(entityKey, true);
                instance = this.getInstance(entityKey);
                String key = RedisChatSyncStore.getRedisKey(entityKey);
                instance.hset(key, field, value);
            }
            catch (Exception e) {
                this.logError("hset", e);
                throw new FusionException(e.getMessage());
            }
            Object var7_7 = null;
        }
        catch (Throwable throwable) {
            Object var7_8 = null;
            Redis.disconnect(instance, log.getLogger());
            throw throwable;
        }
        Redis.disconnect(instance, log.getLogger());
    }

    @Override
    public Integer hdel(ChatSyncEntity entityForKey, String field) throws FusionException {
        Integer n;
        Jedis instance = null;
        try {
            Integer result;
            ChatSyncStats.getInstance().incrementRedisStats(entityForKey, false);
            instance = this.getInstance(entityForKey);
            String key = RedisChatSyncStore.getRedisKey(entityForKey);
            Long resultLong = instance.hdel(key, new String[]{field});
            n = result = resultLong == null ? null : Integer.valueOf(resultLong.intValue());
            Object var9_9 = null;
        }
        catch (Exception e) {
            try {
                this.logError("hdel", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public void lrem(ChatSyncEntity entityKey, int count, String value) throws FusionException {
        Jedis instance = null;
        try {
            try {
                ChatSyncStats.getInstance().incrementRedisStats(entityKey, true);
                instance = this.getInstance(entityKey);
                String key = RedisChatSyncStore.getRedisKey(entityKey);
                instance.lrem(key, (long)count, value);
            }
            catch (Exception e) {
                this.logError("lrem", e);
                throw new FusionException(e.getMessage());
            }
            Object var7_7 = null;
        }
        catch (Throwable throwable) {
            Object var7_8 = null;
            Redis.disconnect(instance, log.getLogger());
            throw throwable;
        }
        Redis.disconnect(instance, log.getLogger());
    }

    @Override
    public Integer sadd(ChatSyncEntity entityKey, String member) throws FusionException {
        Integer n;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityKey, true);
            instance = this.getInstance(entityKey);
            if (instance != null) break block5;
            Integer n2 = null;
            Object var8_7 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(entityKey);
            Long result = instance.sadd(key, new String[]{member});
            n = result == null ? null : Integer.valueOf(result.intValue());
            Object var8_8 = null;
        }
        catch (Exception e) {
            try {
                this.logError("sadd", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public Set<String> smembers(ChatSyncEntity entityKey) throws FusionException {
        Set set;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityKey, false);
            instance = this.getInstance(entityKey);
            if (instance != null) break block5;
            Set<String> set2 = null;
            Object var6_6 = null;
            Redis.disconnect(instance, log.getLogger());
            return set2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(entityKey);
            set = instance.smembers(key);
            Object var6_7 = null;
        }
        catch (Exception e) {
            try {
                this.logError("smembers", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var6_8 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return set;
    }

    @Override
    public Integer srem(ChatSyncEntity entityKey, String member) throws FusionException {
        Integer n;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityKey, true);
            instance = this.getInstance(entityKey);
            if (instance != null) break block5;
            Integer n2 = null;
            Object var8_7 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(entityKey);
            Long result = instance.srem(key, new String[]{member});
            n = result == null ? null : Integer.valueOf(result.intValue());
            Object var8_8 = null;
        }
        catch (Exception e) {
            try {
                this.logError("srem", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public Integer zadd(ChatSyncEntity forKey, double score, String member) throws FusionException {
        Integer n;
        Jedis instance;
        block7: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(forKey, true);
            instance = this.getInstance(forKey);
            if (instance != null) break block7;
            Integer n2 = null;
            Object var11_8 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            Long resultLong;
            Integer result;
            String key = RedisChatSyncStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zadd with instance=" + instance + " key=" + key + " score=" + score);
            }
            Integer n3 = result = (resultLong = instance.zadd(key, score, member)) == null ? null : Integer.valueOf(resultLong.intValue());
            if (log.isDebugEnabled()) {
                log.debug("Called Redis zadd with instance=" + instance + " key=" + key + " score=" + score + " result=" + result);
            }
            n = result;
            Object var11_9 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zadd", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var11_10 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public Integer zadd(ChatSyncEntity forKey, double score, byte[] member) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public Set<String> zrange(ChatSyncEntity forKey, int start, int end) throws FusionException {
        Set set;
        Jedis instance;
        block7: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(forKey, false);
            instance = this.getInstance(forKey);
            if (instance != null) break block7;
            Set<String> set2 = null;
            Object var9_8 = null;
            Redis.disconnect(instance, log.getLogger());
            return set2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrange with instance=" + instance + " key=" + key + " start=" + start + " end=" + end);
            }
            Set results = instance.zrange(key, (long)start, (long)end);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrange with instance=" + instance + " key=" + key + " start=" + start + " end=" + end + " results size=" + results.size());
            }
            set = results;
            Object var9_9 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zrange", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return set;
    }

    @Override
    public Set<byte[]> zrangeBinary(ChatSyncEntity forKey, int start, int end) throws FusionException {
        Set set;
        Jedis instance;
        block7: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(forKey, false);
            instance = this.getInstance(forKey);
            if (instance != null) break block7;
            Set<byte[]> set2 = null;
            Object var10_8 = null;
            Redis.disconnect(instance, log.getLogger());
            return set2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(forKey);
            byte[] keyBytes = Redis.safeEncoderEncode(key);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrangeBinary with instance=" + instance + " key=" + key + " start=" + start + " end=" + end);
            }
            Set results = instance.zrange(keyBytes, (long)start, (long)end);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrangeBinary with instance=" + instance + " key=" + key + " start=" + start + " end=" + end + " results size=" + results.size());
            }
            set = results;
            Object var10_9 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zrange", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var10_10 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return set;
    }

    @Override
    public Set<String> zrangeByScore(ChatSyncEntity forKey, double start, double end) throws FusionException {
        Set set;
        Jedis instance;
        block7: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(forKey, false);
            instance = this.getInstance(forKey);
            if (instance != null) break block7;
            Set<String> set2 = null;
            Object var11_8 = null;
            Redis.disconnect(instance, log.getLogger());
            return set2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrangebyscore with instance=" + instance + " key=" + key + " start=" + start + " end=" + end);
            }
            Set results = instance.zrangeByScore(key, start, end);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrangebyscore with instance=" + instance + " key=" + key + " start=" + start + " end=" + end + " results size=" + results.size());
            }
            set = results;
            Object var11_9 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zrangeByScore", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var11_10 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return set;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(ChatSyncEntity forKey, double start, double end) throws FusionException {
        Set set;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(forKey, false);
            instance = this.getInstance(forKey);
            if (instance != null) break block5;
            Set<Tuple> set2 = null;
            Object var10_8 = null;
            Redis.disconnect(instance, log.getLogger());
            return set2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(forKey);
            set = instance.zrangeByScoreWithScores(key, start, end);
            Object var10_9 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zrangeByScoreWithScores", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var10_10 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return set;
    }

    @Override
    public Set<String> zrangeByScore(ChatSyncEntity forKey, double start, double end, int offset, int count) throws FusionException {
        Set set;
        Jedis instance;
        block7: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(forKey, false);
            instance = this.getInstance(forKey);
            if (instance != null) break block7;
            Set<String> set2 = null;
            Object var13_10 = null;
            Redis.disconnect(instance, log.getLogger());
            return set2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrangebyscore with instance=" + instance + " key=" + key + " start=" + start + " end=" + end + " offset=" + offset + " count=" + count);
            }
            Set results = instance.zrangeByScore(key, start, end, offset, count);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrangebyscore with instance=" + instance + " key=" + key + " start=" + start + " end=" + end + " offset=" + offset + " count=" + count + " results size=" + results.size());
            }
            set = results;
            Object var13_11 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zrangeByScore with offset,count", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var13_12 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return set;
    }

    @Override
    public Integer zrank(ChatSyncEntity forKey, String member) throws FusionException {
        Integer n;
        Jedis instance;
        block7: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(forKey, false);
            instance = this.getInstance(forKey);
            if (instance != null) break block7;
            Integer n2 = null;
            Object var9_7 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            Long resultLong;
            Integer result;
            String key = RedisChatSyncStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrank with instance=" + instance + " key=" + key + " member=" + member);
            }
            Integer n3 = result = (resultLong = instance.zrank(key, member)) == null ? null : Integer.valueOf(resultLong.intValue());
            if (log.isDebugEnabled()) {
                log.debug("Redis zrank with instance=" + instance + " key=" + key + " member=" + member + " rank=" + result);
            }
            n = result;
            Object var9_8 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zrank", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_9 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public Integer zrankBinary(ChatSyncEntity forKey, byte[] member) throws FusionException {
        Integer n;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(forKey, false);
            instance = this.getInstance(forKey);
            if (instance != null) break block5;
            Integer n2 = null;
            Object var9_7 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(forKey);
            byte[] keyBytes = Redis.safeEncoderEncode(key);
            Long result = instance.zrank(keyBytes, member);
            n = result == null ? null : Integer.valueOf(result.intValue());
            Object var9_8 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zrank", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_9 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public Integer expire(ChatSyncEntity entityKey, int seconds) throws FusionException {
        Integer n;
        Jedis instance;
        block6: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityKey, true);
            instance = this.getInstance(entityKey);
            if (instance != null) break block6;
            Integer n2 = null;
            Object var8_7 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            Long result;
            String key = RedisChatSyncStore.getRedisKey(entityKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis expire with instance=" + instance + " key=" + key + " seconds=" + seconds);
            }
            n = (result = instance.expire(key, seconds)) == null ? null : Integer.valueOf(result.intValue());
            Object var8_8 = null;
        }
        catch (Exception e) {
            try {
                this.logError("expire", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public Integer ttl(ChatSyncEntity entityKey) throws FusionException {
        Integer n;
        Jedis instance;
        block6: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityKey, false);
            instance = this.getInstance(entityKey);
            if (instance != null) break block6;
            Integer n2 = null;
            Object var8_6 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            Integer ttlSeconds;
            String key = RedisChatSyncStore.getRedisKey(entityKey);
            Long result = instance.ttl(key);
            Integer n3 = ttlSeconds = result == null ? null : Integer.valueOf(result.intValue());
            if (log.isDebugEnabled()) {
                log.debug("Redis ttl with instance=" + instance + " key=" + key + " seconds=" + ttlSeconds);
            }
            n = ttlSeconds;
            Object var8_7 = null;
        }
        catch (Exception e) {
            try {
                this.logError("ttl", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_8 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public Integer zrem(ChatSyncEntity forKey, String member) throws FusionException {
        Integer n;
        Jedis instance;
        block7: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(forKey, true);
            instance = this.getInstance(forKey);
            if (instance != null) break block7;
            Integer n2 = null;
            Object var9_7 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            Long resultLong;
            Integer result;
            String key = RedisChatSyncStore.getRedisKey(forKey);
            if (log.isDebugEnabled()) {
                log.debug("Redis zrem with instance=" + instance + " key=" + key + " member=" + member);
            }
            Integer n3 = result = (resultLong = instance.zrem(key, new String[]{member})) == null ? null : Integer.valueOf(resultLong.intValue());
            if (log.isDebugEnabled()) {
                log.debug("Redis zrem with instance=" + instance + " key=" + key + " member=" + member + " result=" + result);
            }
            n = result;
            Object var9_8 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zrem", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_9 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public Integer zremrangeByRank(ChatSyncEntity forKey, int start, int end) throws FusionException {
        Integer n;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(forKey, true);
            instance = this.getInstance(forKey);
            if (instance != null) break block5;
            Integer n2 = null;
            Object var9_8 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(forKey);
            Long resultLong = instance.zremrangeByRank(key, (long)start, (long)end);
            n = resultLong == null ? null : Integer.valueOf(resultLong.intValue());
            Object var9_9 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zremrangeByRank", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    @Override
    public Set<String> zrevrangeByScore(ChatSyncEntity forKey, double max, double min, int offset, int count) throws FusionException {
        throw new FusionException("Only implemented in RedisChatSyncPipelineStore for now");
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(ChatSyncEntity forKey, double max, double min, int offset, int count) throws FusionException {
        throw new FusionException("Only implemented in RedisChatSyncPipelineStore for now");
    }

    @Override
    public Integer zcount(ChatSyncEntity entityKey, double min, double max) throws FusionException {
        Integer n;
        Jedis instance;
        block5: {
            instance = null;
            ChatSyncStats.getInstance().incrementRedisStats(entityKey, true);
            instance = this.getInstance(entityKey);
            if (instance != null) break block5;
            Integer n2 = null;
            Object var11_8 = null;
            Redis.disconnect(instance, log.getLogger());
            return n2;
        }
        try {
            String key = RedisChatSyncStore.getRedisKey(entityKey);
            Long resultLong = instance.zcount(key, min, max);
            n = resultLong == null ? null : Integer.valueOf(resultLong.intValue());
            Object var11_9 = null;
        }
        catch (Exception e) {
            try {
                this.logError("zcount", e);
                throw new FusionException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var11_10 = null;
                Redis.disconnect(instance, log.getLogger());
                throw throwable;
            }
        }
        Redis.disconnect(instance, log.getLogger());
        return n;
    }

    public static List<Tuple> getTupledList(List<byte[]> membersWithScores) throws FusionException {
        try {
            ArrayList<Tuple> list = new ArrayList<Tuple>();
            Iterator<byte[]> iterator = membersWithScores.iterator();
            while (iterator.hasNext()) {
                list.add((Tuple)iterator.next());
            }
            return list;
        }
        catch (Exception e) {
            log.error(e);
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public Transaction multi(ChatSyncEntity forKey) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public void execTxn(ChatSyncEntity forKey) throws FusionException {
        throw new FusionException("Not implemented");
    }
}

