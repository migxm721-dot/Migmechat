/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.Pipeline
 *  redis.clients.jedis.Response
 *  redis.clients.jedis.exceptions.JedisException
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.chatsync.MessageStatusEventKey;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.common.LRUCache;
import com.projectgoth.fusion.common.RedisConnectionManager;
import com.projectgoth.fusion.common.SharedRedisConnection;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.jedis.NonPooledJedis;
import com.projectgoth.fusion.slice.FusionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Redis {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Redis.class));
    private static final String PROPERTIES_FILENAME = "redis.properties";
    private static Properties properties;
    private static String shardDirectoryMasterServer;
    private static String shardDirectorySlaveServer;
    private static String leaderboardsMasterServer;
    private static String leaderboardsSlaveServer;
    private static String queuesMasterServer;
    private static String queuesSlaveServer;
    private static String gamesSlaveServer;
    private static String gamesMasterServer;
    private static String webResourcesSlaveServer;
    private static String webResourcesMasterServer;
    private static SecureRandom random;
    private static ShardSettingsCache shardSettingsCache;
    private static LRUCache<String, String> userShardMappingCache;
    private static int userShardMappingCacheMaxSize;
    private static int userShardMappingCacheExpiryInSeconds;
    private static SharedRedisConnection sharedDirectorySlaveConn;
    private static SharedRedisConnection sharedDirectoryMasterConn;
    public static final String KEYSPACE_SEPARATOR = ":";
    public static final String PROTOCOL_DOT_CHARSET = "UTF-8";

    public static int getUserShardMappingCacheMaxSize() {
        return userShardMappingCacheMaxSize;
    }

    public static int getUserShardMappingCacheExpiryInSeconds() {
        return userShardMappingCacheExpiryInSeconds;
    }

    static String getShardKeyForUserID(int userID) {
        return KeySpaceShardDirectory.REDIS_USER_ID.append(userID);
    }

    public static String getShardKeyForEntityID(KeySpace entity, int entityId) {
        return entity.getDirectoryKeySpace().append(entityId);
    }

    public static String getUserDetailKey(int userId) {
        return KeySpace.USER.append(userId);
    }

    public static String getOfflineMessageKey(int recipientID) {
        return Redis.getOfflineMessageKey(recipientID, new Date());
    }

    public static String getOfflineMessageKey(int recipientID, Date date) {
        String sDate = DateTimeUtils.OFFLINE_MSG_KEY_DATE_FORMAT.get().format(date);
        return KeySpace.USER_ENTITY.toString() + recipientID + ":OLMSG:" + sDate;
    }

    public static String getConversationMessageKey(String conversationID) {
        return KeySpace.CONVERSATION_ENTITY.toString() + conversationID + ":M";
    }

    public static String getMessageStatusEventKey(MessageStatusEventKey mseKey) throws FusionException {
        if (mseKey.getMessageDestinationType() != MessageDestinationData.TypeEnum.INDIVIDUAL) {
            throw new FusionException("Tried to retrieve message status events for chatID=" + mseKey.getMessageDestination() + " chat type=" + (Object)((Object)mseKey.getMessageDestinationType()) + " : Only supported for private messaging");
        }
        return KeySpace.CONVERSATION_ENTITY.toString() + mseKey.getKey() + ":E";
    }

    public static String getConversationDefinitionKey(String conversationID) {
        return KeySpace.CONVERSATION_ENTITY.toString() + conversationID + ":D";
    }

    public static String getConversationParticipantsKey(String conversationID) {
        return KeySpace.CONVERSATION_ENTITY.toString() + conversationID + ":P";
    }

    public static String getOldChatListsKey(int userID) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("getOldChatListsKey with userid=" + userID));
        }
        return KeySpace.USER_ENTITY.toString() + userID + ":CLO";
    }

    public static String getCurrentChatListKey(int userID) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("getCurrentChatListKey with userid=" + userID));
        }
        return KeySpace.USER_ENTITY.toString() + userID + ":CLC";
    }

    public static String getChatListVersionKey(int userID) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("getChatListVersionKey with userid=" + userID));
        }
        return KeySpace.USER_ENTITY.toString() + userID + ":CLV";
    }

    public static String getFailedAuthsPerIPKey(String ip) {
        return KeySpace.IP_ADDRESS.toString() + ip + ":F";
    }

    public static String getRecentWebOnlySidsKey(int userID) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("getRecentSidsKey with userid=" + userID));
        }
        return KeySpace.USER_ENTITY.toString() + userID + ":RS";
    }

    public static Jedis getMasterInstanceForUserID(int userID) throws Exception {
        if (userID <= 0) {
            throw new Exception("Invalid User ID");
        }
        return Redis.getInstance(Redis.getShardKeyForUserID(userID), InstanceType.MASTER);
    }

    public static Jedis getSlaveInstanceForUserID(int userID) throws Exception {
        if (userID <= 0) {
            throw new Exception("Invalid User ID");
        }
        return Redis.getInstance(Redis.getShardKeyForUserID(userID), InstanceType.SLAVE);
    }

    public static Jedis getMasterInstanceForEntityID(KeySpace entity, int entityID) throws Exception {
        return Redis.getInstance(Redis.getShardKeyForEntityID(entity, entityID), InstanceType.MASTER);
    }

    public static Jedis getSlaveInstanceForEntityID(KeySpace entity, int entityID) throws Exception {
        return Redis.getInstance(Redis.getShardKeyForEntityID(entity, entityID), InstanceType.SLAVE);
    }

    public static Jedis getLeaderboardsMasterInstance() throws Exception {
        return Redis.getNonPooledRedisConnection(leaderboardsMasterServer);
    }

    public static Jedis getLeaderboardsSlaveInstance() throws Exception {
        return Redis.getNonPooledRedisConnection(leaderboardsSlaveServer);
    }

    public static Jedis getGamesMasterInstance() throws Exception {
        return Redis.getNonPooledRedisConnection(gamesMasterServer);
    }

    public static Jedis getGamesSlaveInstance() throws Exception {
        return Redis.getNonPooledRedisConnection(gamesSlaveServer);
    }

    public static Jedis getQueuesMasterInstance() throws Exception {
        return Redis.getNonPooledRedisConnection(queuesMasterServer);
    }

    public static Jedis getQueuesSlaveInstance() throws Exception {
        return Redis.getNonPooledRedisConnection(queuesSlaveServer);
    }

    public static Jedis getWebResourcesMasterInstance() throws Exception {
        return Redis.getNonPooledRedisConnection(webResourcesMasterServer);
    }

    public static Jedis getWebResourcesSlaveInstance() throws Exception {
        return Redis.getNonPooledRedisConnection(webResourcesSlaveServer);
    }

    public static String getShardID(String shardDirectoryKey, Jedis shardDirectoryConn) throws Exception {
        String shardID = userShardMappingCache.get(shardDirectoryKey);
        if (StringUtil.isBlank(shardID)) {
            Jedis shardDirectorySlave;
            if (shardDirectoryConn == null) {
                try {
                    shardDirectorySlave = Redis.getRedisConnection(shardDirectorySlaveServer);
                }
                catch (Exception e) {
                    log.error((Object)("Unable to connect to Redis shard directory slave server at " + shardDirectorySlaveServer), (Throwable)e);
                    throw new Exception("Unable to connect to shard directory slave");
                }
            } else {
                shardDirectorySlave = shardDirectoryConn;
            }
            try {
                shardID = shardDirectorySlave.get(shardDirectoryKey);
            }
            catch (Exception e) {
                log.error((Object)("Unable to get shard ID or shard Instance: " + e.getLocalizedMessage()));
                throw e;
            }
            finally {
                if (shardDirectoryConn == null) {
                    Redis.disconnect(shardDirectorySlave, log);
                }
            }
        }
        return shardID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Jedis getInstance(String shardDirectoryKey, InstanceType instanceType) throws Exception {
        String shardServer;
        String shardID;
        long timeoutInMs;
        Jedis sharedConn;
        if (SystemProperty.getBool(SystemPropertyEntities.Default.SHARE_REDIS_DIRECTORY_SLAVE_CONNECTION_ENABLED)) {
            sharedConn = null;
            try {
                timeoutInMs = SystemProperty.getLong(SystemPropertyEntities.Default.SHARE_REDIS_DIRECTORY_SLAVE_CONNECTION_WAIT_IN_MS);
                sharedConn = sharedDirectorySlaveConn.getConnection(timeoutInMs);
                shardID = Redis.getShardID(shardDirectoryKey, sharedConn);
            }
            finally {
                if (sharedConn != null) {
                    sharedDirectorySlaveConn.returnConnection();
                }
            }
        } else {
            shardID = Redis.getShardID(shardDirectoryKey, null);
        }
        if (shardID != null) {
            shardServer = instanceType == InstanceType.MASTER ? shardSettingsCache.getMasterServerForShardID(shardID) : shardSettingsCache.getSlaveServerForShardID(shardID);
        } else {
            if (instanceType != InstanceType.MASTER) {
                return null;
            }
            if (SystemProperty.getBool(SystemPropertyEntities.Default.SHARE_REDIS_DIRECTORY_MASTER_CONNECTION_ENABLED)) {
                sharedConn = null;
                try {
                    timeoutInMs = SystemProperty.getLong(SystemPropertyEntities.Default.SHARE_REDIS_DIRECTORY_MASTER_CONNECTION_WAIT_IN_MS);
                    sharedConn = sharedDirectoryMasterConn.getConnection(timeoutInMs);
                    shardID = Redis.getNewShardID(shardDirectoryKey, sharedConn);
                }
                finally {
                    if (sharedConn != null) {
                        sharedDirectoryMasterConn.returnConnection();
                    }
                }
            } else {
                shardID = Redis.getNewShardID(shardDirectoryKey, null);
            }
            shardServer = shardSettingsCache.getMasterServerForShardID(shardID);
        }
        if (shardServer == null) {
            throw new Exception("Configuration for shard " + shardID + KEYSPACE_SEPARATOR + instanceType.value() + " not found");
        }
        if (!StringUtil.isBlank(shardID)) {
            userShardMappingCache.put(shardDirectoryKey, shardID);
        }
        return Redis.getRedisConnection(shardServer);
    }

    /*
     * Exception decompiling
     */
    public static String getNewShardID(String shardDirectoryKey, Jedis shardDirectoryConn) throws Exception {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [15[FORLOOP]], but top level block is 6[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private static String tryAssignAndGetShard(Jedis shardDirectoryMaster, String shardDirectoryKey, String shardID) throws Exception {
        Pipeline pipeline = shardDirectoryMaster.pipelined();
        pipeline.setnx(shardDirectoryKey, shardID);
        Response actualShardIDResponse = pipeline.get(shardDirectoryKey);
        pipeline.sync();
        String storedShardID = (String)actualShardIDResponse.get();
        if (storedShardID == null) {
            throw new Exception("getNewShardID(" + shardDirectoryKey + ") Pipeline not returning the actual shard stored in the master directory");
        }
        return storedShardID;
    }

    private static synchronized void loadProperties() throws Exception {
        properties = new Properties();
        String propertiesLocation = ConfigUtils.getConfigDirectory() + PROPERTIES_FILENAME;
        log.info((Object)("Loading Redis configuration file " + propertiesLocation));
        try {
            FileInputStream inputStream = new FileInputStream(new File(propertiesLocation));
            properties.load(inputStream);
            shardDirectoryMasterServer = Redis.loadServerProperty("shardDirectory.master", "Shard directory master");
            shardDirectorySlaveServer = Redis.loadServerProperty("shardDirectory.slave", "Shard directory slave");
            leaderboardsMasterServer = Redis.loadServerProperty("leaderboards.master", "Leaderboards master");
            leaderboardsSlaveServer = Redis.loadServerProperty("leaderboards.slave", "Leaderboards slave");
            gamesMasterServer = Redis.loadServerProperty("games.master", "Games master");
            gamesSlaveServer = Redis.loadServerProperty("games.slave", "Games slave");
            queuesMasterServer = Redis.loadServerProperty("queues.master", "Queues master");
            queuesSlaveServer = Redis.loadServerProperty("queues.slave", "Queues slave");
            webResourcesMasterServer = Redis.loadServerProperty("webresources.master", "Web resources master");
            webResourcesSlaveServer = Redis.loadServerProperty("webresources.slave", "Web resources slave");
            userShardMappingCacheMaxSize = StringUtil.toIntOrDefault(properties.getProperty("cache.usershard.maxsize"), 5000);
            userShardMappingCacheExpiryInSeconds = StringUtil.toIntOrDefault(properties.getProperty("cache.usershard.expiry.seconds"), 0);
            log.info((Object)"Redis configuration successfully loaded");
        }
        catch (Exception e) {
            log.error((Object)("Unable to load Redis configuration file: " + e.getMessage()));
        }
    }

    private static String loadServerProperty(String propName, String description) throws Exception {
        String server = properties.getProperty(propName);
        if (server == null) {
            throw new Exception(propName + " not specified");
        }
        log.info((Object)(description + ": " + server));
        return server;
    }

    static Jedis getRedisConnection(String server) throws Exception {
        int colonPos = server.indexOf(58);
        String host = server.substring(0, colonPos);
        int port = Integer.parseInt(server.substring(colonPos + 1, server.length()));
        Jedis jedis = RedisConnectionManager.getInstance().getConnection(host, port);
        return jedis;
    }

    static Jedis getNonPooledRedisConnection(String server) throws Exception {
        int colonPos = server.indexOf(58);
        String host = server.substring(0, colonPos);
        int port = Integer.parseInt(server.substring(colonPos + 1, server.length()));
        NonPooledJedis jedis = new NonPooledJedis(host, port, SystemProperty.getInt(SystemPropertyEntities.JedisPoolConfig.JEDIS_NON_POOL_SOCKET_TIMEOUT));
        jedis.connect();
        return jedis;
    }

    public static void disconnect(Jedis connection, Logger callerLog) {
        if (connection != null) {
            try {
                if (connection instanceof NonPooledJedis) {
                    connection.disconnect();
                } else {
                    RedisConnectionManager.getInstance().closeConnection(connection);
                }
            }
            catch (Exception e) {
                String msg = "Failed to close Redis connection, reason: " + e;
                if (callerLog != null) {
                    callerLog.error((Object)msg, (Throwable)e);
                }
                log.error((Object)msg, (Throwable)e);
            }
        }
    }

    public static void appendToListAndTrim(Jedis redisConnection, String key, String value, int maxElements) throws Exception {
        redisConnection.rpush(key, new String[]{value});
        redisConnection.ltrim(key, (long)(-maxElements), -1L);
    }

    public static void addToSortedSetWithTimestampAndTrimByAge(Jedis redisConnection, String key, String value, int maxAgeSeconds, boolean setExpiry) throws Exception {
        long nowInSeconds = System.currentTimeMillis() / 1000L;
        log.debug((Object)String.format("addToSortedSetWithTimestampAndTrimByAge called with key[%s] value [%s] maxAgeSeconds[%d] nowInSeconds[%d]", key, value, maxAgeSeconds, nowInSeconds));
        redisConnection.zremrangeByScore(key, -1.7976931348623157E308, (double)(nowInSeconds - (long)maxAgeSeconds));
        redisConnection.zadd(key, (double)nowInSeconds, value);
        if (setExpiry) {
            redisConnection.expire(key, maxAgeSeconds);
        }
    }

    static boolean removeShardMappingForUserID(int userID) throws Exception {
        Jedis shardDirectoryMaster;
        try {
            shardDirectoryMaster = Redis.getRedisConnection(shardDirectoryMasterServer);
        }
        catch (Exception e) {
            log.error((Object)("Unable to connect to Redis shard directory master server at " + shardDirectoryMasterServer), (Throwable)e);
            throw new Exception("Unabel to connect to shard directory master");
        }
        try {
            String shardKey = KeySpaceShardDirectory.REDIS_USER_ID.append(userID);
            boolean bl = 1L == shardDirectoryMaster.del(shardKey);
            return bl;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            Redis.disconnect(shardDirectoryMaster, log);
        }
    }

    static boolean removeShardMapping(String shardKey) throws Exception {
        Jedis shardDirectoryMaster;
        try {
            shardDirectoryMaster = Redis.getRedisConnection(shardDirectoryMasterServer);
        }
        catch (Exception e) {
            log.error((Object)("Unable to connect to Redis shard directory master server at " + shardDirectoryMasterServer), (Throwable)e);
            throw new Exception("Unabel to connect to shard directory master");
        }
        try {
            boolean e = 1L == shardDirectoryMaster.del(shardKey);
            return e;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            Redis.disconnect(shardDirectoryMaster, log);
        }
    }

    static void clearShardMappingCache() {
        userShardMappingCache.clear();
    }

    static String getCachedShardID(String shardDirectoryKey) {
        return userShardMappingCache.get(shardDirectoryKey);
    }

    public static String getPipelineResult(List<Object> pipelineResults, int index) throws FusionException {
        Object value = pipelineResults.get(index);
        return Redis.getPipelineResult(value);
    }

    public static String getPipelineResult(Object value) throws FusionException {
        if (value == null) {
            return null;
        }
        if (value instanceof byte[]) {
            return Redis.safeEncoderEncode((byte[])value);
        }
        if (value instanceof String) {
            return (String)value;
        }
        throw new FusionException("Pipeline result is unexpected type=" + value.getClass());
    }

    public static String safeEncoderEncode(byte[] b) {
        if (b == null) {
            return null;
        }
        return Redis.safeEncoderEncodeInner(b);
    }

    public static byte[] safeEncoderEncode(String str) {
        try {
            if (str == null) {
                throw new JedisException("value sent to redis cannot be null");
            }
            return str.getBytes(PROTOCOL_DOT_CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new JedisException((Throwable)e);
        }
    }

    private static String safeEncoderEncodeInner(byte[] data) {
        try {
            return new String(data, PROTOCOL_DOT_CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new JedisException((Throwable)e);
        }
    }

    static {
        random = new SecureRandom();
        try {
            Redis.loadProperties();
            shardSettingsCache = new ShardSettingsCache();
            sharedDirectoryMasterConn = new SharedRedisConnection(shardDirectoryMasterServer);
            sharedDirectorySlaveConn = new SharedRedisConnection(shardDirectorySlaveServer);
            userShardMappingCache = new LRUCache(userShardMappingCacheMaxSize, userShardMappingCacheExpiryInSeconds);
        }
        catch (Exception e) {
            log.error((Object)e.getMessage());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ShardSettingsCache {
        private final Semaphore refreshCacheSemaphore = new Semaphore(1);
        private int cacheTimeout = 600000;
        private volatile long timeOfNextUpdate = 0L;
        private volatile ShardSettings shardSettings;

        public ShardSettingsCache() {
            String cacheTimeoutString = properties.getProperty("shardingSettings.cacheTime");
            if (cacheTimeoutString != null) {
                try {
                    this.cacheTimeout = Integer.parseInt(cacheTimeoutString);
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            this.refreshCacheIfNecessary();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void refreshCacheIfNecessary() {
            long now = System.currentTimeMillis();
            if (now < this.timeOfNextUpdate) {
                return;
            }
            if (!this.refreshCacheSemaphore.tryAcquire()) {
                return;
            }
            try {
                if (now < this.timeOfNextUpdate) {
                    return;
                }
                this.shardSettings = this.getShardSettings();
                this.timeOfNextUpdate = System.currentTimeMillis() + (long)this.cacheTimeout;
            }
            catch (Exception e) {
                log.error((Object)"Unable to update Redis shard settings", (Throwable)e);
            }
            finally {
                this.refreshCacheSemaphore.release();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private ShardSettings getShardSettings() throws Exception {
            Jedis shardDirectorySlave = null;
            shardDirectorySlave = Redis.getRedisConnection(shardDirectorySlaveServer);
            try {
                Map shardMasters = shardDirectorySlave.hgetAll(KeySpaceShardDirectory.REDIS_SHARD_MASTERS.value);
                Map shardSlaves = shardDirectorySlave.hgetAll(KeySpaceShardDirectory.REDIS_SHARD_SLAVES.value);
                List shardWeights = shardDirectorySlave.lrange(KeySpaceShardDirectory.REDIS_SHARD_WEIGHTS.value, 0L, -1L);
                ShardSettings shardSettings = new ShardSettings(shardMasters, shardSlaves, shardWeights);
                return shardSettings;
            }
            finally {
                Redis.disconnect(shardDirectorySlave, log);
            }
        }

        public String getMasterServerForShardID(String shardID) throws Exception {
            this.refreshCacheIfNecessary();
            if (this.shardSettings == null) {
                throw new Exception("Shard settings have not been initialised");
            }
            return this.shardSettings.getShardMasters().get(shardID);
        }

        public String getSlaveServerForShardID(String shardID) throws Exception {
            this.refreshCacheIfNecessary();
            if (this.shardSettings == null) {
                throw new Exception("Shard settings have not been initialised");
            }
            return this.shardSettings.getShardSlaves().get(shardID);
        }

        public List<Integer> getShardWeights() throws Exception {
            this.refreshCacheIfNecessary();
            if (this.shardSettings == null) {
                throw new Exception("Shard settings have not been initialised");
            }
            return this.shardSettings.getShardWeights();
        }

        public int getSumOfShardWeights() throws Exception {
            this.refreshCacheIfNecessary();
            if (this.shardSettings == null) {
                throw new Exception("Shard settings have not been initialised");
            }
            return this.shardSettings.getSumOfShardWeights();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private class ShardSettings {
            private Map<String, String> shardMasters;
            private Map<String, String> shardSlaves;
            private List<Integer> shardWeights;
            private int sumOfShardWeights;

            public ShardSettings(Map<String, String> shardMasters, Map<String, String> shardSlaves, List<String> shardWeightsAsStrings) {
                this.shardMasters = shardMasters;
                this.shardSlaves = shardSlaves;
                this.shardWeights = new ArrayList<Integer>(shardWeightsAsStrings.size());
                for (String weightAsString : shardWeightsAsStrings) {
                    int weight = Integer.parseInt(weightAsString);
                    this.shardWeights.add(weight);
                    this.sumOfShardWeights += weight;
                }
            }

            public Map<String, String> getShardMasters() {
                return this.shardMasters;
            }

            public Map<String, String> getShardSlaves() {
                return this.shardSlaves;
            }

            public List<Integer> getShardWeights() {
                return this.shardWeights;
            }

            public int getSumOfShardWeights() {
                return this.sumOfShardWeights;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum FieldGroupDetails {
        TAGS("Tags");

        private String value;

        private FieldGroupDetails(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum FieldUserDetails {
        AVATAR("Avatar"),
        AVATAR_VOTES_COUNTER("AvatarVotes"),
        AVATAR_COMMENTS_COUNTER("NumOfAvatarComments"),
        CONTACT_LIST_VERSION("ContactListVersion"),
        DISPLAY_PICTURE("DispPic"),
        GIFTS_RECEIVED_COUNTER("NumOfGiftsRecieved"),
        IS_VERIFIED("IsVerified"),
        USER_LIKES_SET(":LK:UL"),
        THIRD_PARTY_GAMES_PLAYED_COUNTER(":Application:INDEX"),
        VERIFIED_ABOUT_ME("VerifiedAbout");

        private String value;

        private FieldUserDetails(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum SubKeySpaceUserEntity {
        CONFIG("Config"),
        REWARDED_INVITEE_TIMESTAMP("RewardedInviteeTimestamp");

        private String value;

        private SubKeySpaceUserEntity(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }

        public String getFullKey(int userId) {
            return String.format("%s%s%s", KeySpace.USER_ENTITY.append(userId), Redis.KEYSPACE_SEPARATOR, this.value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum KeySpace {
        USER("User:", KeySpaceShardDirectory.REDIS_USER_ID),
        USER_ENTITY("U:", KeySpaceShardDirectory.REDIS_USER_ID),
        USER_ACTIVITY("UserActivity:", KeySpaceShardDirectory.REDIS_USER_ID),
        USER_LIKES("UserLikes:", KeySpaceShardDirectory.REDIS_USER_ID),
        USER_FOOTPRINTS("UserFootprints:", KeySpaceShardDirectory.REDIS_USER_ID),
        GROUP("Group:", KeySpaceShardDirectory.REDIS_GROUP_ID),
        GROUP_ACTIVITY("GroupActivity:", KeySpaceShardDirectory.REDIS_GROUP_ID),
        USER_NOTIFICATION("UserNotification:", KeySpaceShardDirectory.REDIS_USER_ID),
        CAPTCHA("Captcha:", KeySpaceShardDirectory.REDIS_USER_ID),
        EXTERNAL_EMAIL_VERIFICATION_TOKEN("ExternalEmailVerificationToken:", KeySpaceShardDirectory.REDIS_USER_ID),
        CONVERSATION_ENTITY("CV:", KeySpaceShardDirectory.REDIS_CONVERSATION_ID),
        COUNTRY_ENTITY("CO:", KeySpaceShardDirectory.REDIS_COUNTRY_ID),
        WEB_RESOURCE_ENTITY("WR:", null),
        IP_ADDRESS("I:", KeySpaceShardDirectory.REDIS_IP);

        private String value;
        private KeySpaceShardDirectory directoryKeySpace;

        private KeySpace(String value, KeySpaceShardDirectory directoryKeySpace) {
            this.value = value;
            this.directoryKeySpace = directoryKeySpace;
        }

        public String toString() {
            return this.value;
        }

        public String append(int i) {
            return this.value + i;
        }

        public String append(String s) {
            return this.value + s;
        }

        public KeySpaceShardDirectory getDirectoryKeySpace() {
            return this.directoryKeySpace;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum KeySpaceQueuesInstance {
        TOP_MERCHANT_TAG("TopMerchantTagTmp:");

        private String value;

        private KeySpaceQueuesInstance(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }

        public String append(int i) {
            return this.value + i;
        }

        public String append(String s) {
            return this.value + s;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum KeySpaceShardDirectory {
        REDIS_SHARD_MASTERS("R:MASTERS"),
        REDIS_SHARD_SLAVES("R:SLAVES"),
        REDIS_SHARD_WEIGHTS("R:WEIGHTS"),
        REDIS_USER_ID("R:U:"),
        REDIS_GROUP_ID("R:G:"),
        REDIS_CHAT_ROOM_ID("R:C:"),
        REDIS_CONVERSATION_ID("R:CV:"),
        REDIS_COUNTRY_ID("R:CO:"),
        REDIS_IP("R:I:");

        private String value;

        private KeySpaceShardDirectory(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }

        public String append(int i) {
            return this.value + i;
        }

        public String append(String s) {
            return this.value + s;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum InstanceType {
        MASTER('M'),
        SLAVE('S');

        private char value;

        private InstanceType(char value) {
            this.value = value;
        }

        public char value() {
            return this.value;
        }
    }
}

