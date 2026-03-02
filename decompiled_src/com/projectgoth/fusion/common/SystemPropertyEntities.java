/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Level
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.RedisShardAssignmentMode;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntryFallbackCreator;
import com.projectgoth.fusion.common.SystemPropertyEntryFallbackCreatorRegistry;
import com.projectgoth.fusion.common.SystemPropertyEntryWithParent;
import com.projectgoth.fusion.common.metrics.MetricsEnums;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.CreditTransferData;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserEmailAddressData;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.recommendation.collector.CollectedDataTypeEnum;
import com.projectgoth.fusion.recommendation.delivery.Enums;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SystemPropertyEntities {
    private static final String SEPARATOR = ":";
    private static final String COLLECTEDDATATYPESPECIFICSETTINGS_LOCAL_NAMESPACE = "CDT";

    public static String getNameWithNamespace(String namespace, String name) {
        return String.format("%s%s%s", namespace, SEPARATOR, name);
    }

    private static Map<String, SystemPropertyEntryInterface> buildSystemPropertyEntryInterfaceMap() {
        HashMap<String, SystemPropertyEntryInterface> propertyNamesToEntriesMap = new HashMap<String, SystemPropertyEntryInterface>();
        for (Class<?> clazz : SystemPropertyEntities.class.getDeclaredClasses()) {
            for (Class<?> interfaze : clazz.getInterfaces()) {
                if (!SystemPropertyEntryInterface.class.equals(interfaze) || !clazz.isEnum()) continue;
                Class<?> theClazz = clazz;
                for (SystemPropertyEntryInterface e : (SystemPropertyEntryInterface[])theClazz.getEnumConstants()) {
                    propertyNamesToEntriesMap.put(e.getName(), e);
                }
            }
        }
        return Collections.unmodifiableMap(propertyNamesToEntriesMap);
    }

    public static SystemPropertyEntryInterface getEntry(String propertyNameWithNamespace) {
        return PropertyNamesSingletonHolder.PROPERTY_NAMES_TO_ENTRIES_MAP.get(propertyNameWithNamespace);
    }

    private static String getCollectedDataTypeNameSpace(String parentNameSpace, CollectedDataTypeEnum dataType) {
        return SystemPropertyEntities.getNameWithNamespace(parentNameSpace, "CDT[" + dataType.getCode() + "]");
    }

    static /* synthetic */ Map access$000() {
        return SystemPropertyEntities.buildSystemPropertyEntryInterfaceMap();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Temp implements SystemPropertyEntryInterface
    {
        ER170_ENABLED(new SystemPropertyEntryBoolean("ER170ChangesEnabled", true)),
        SE511_ENABLED(new SystemPropertyEntryBoolean("SE511ChangesEnabled", true)),
        ER78_ENABLED(new SystemPropertyEntryBoolean("ER78ChangesEnabled", true)),
        ER79_ENABLED(new SystemPropertyEntryBoolean("ER79ChangesEnabled", true)),
        ER74_ENABLED(new SystemPropertyEntryBoolean("ER74ChangesEnabled", true)),
        SE425_ENABLED(new SystemPropertyEntryBoolean("SE425ChangesEnabled", true)),
        SE433_ENABLED(new SystemPropertyEntryBoolean("SE433ChangesEnabled", true)),
        ER68_ENABLED(new SystemPropertyEntryBoolean("ER68ChangesEnabled", true)),
        ER62_ENABLED(new SystemPropertyEntryBoolean("ER62ChangesEnabled", true)),
        WW422_ENABLED(new SystemPropertyEntryBoolean("WW422ChangesEnabled", true)),
        PT73368964_ENABLED(new SystemPropertyEntryBoolean("PT73368964ChangesEnabled", true)),
        PT73368964_UseAuthForChangePwd_ENABLED(new SystemPropertyEntryBoolean("PT73368964UseAuthForChangePwdChangesEnabled", true)),
        PT74760224_ENABLED(new SystemPropertyEntryBoolean("PT74760224ChangesEnabled", true)),
        WW119_IMAGE_SERVER_ENABLED_REQUEST_URL_PATHS_REGEX(new SystemPropertyEntryString("WW119ImageServerEnabledRequestUrlPathsRegex", "^/(a|u|avatar|dp)/.*")),
        WW384_FALL_BACK_ON_ISO_COUNTRY_CODE_ENABLED(new SystemPropertyEntryBoolean("WW384FallBackOnISOCountryCodeEnabled", true)),
        SE423_LOAD_INVITEE_USES_MASTER_DB(new SystemPropertyEntryBoolean("SE423LoadInviteeUsesMasterDB", true)),
        SE426_SHARD_ASSIGNMENT_MODE(new SystemPropertyEntryInteger("SE426ShardAssignmentMode", RedisShardAssignmentMode.SETNX_AND_GET_PIPELINE_WITH_DIST_LOCK.getEnumValue())),
        SE454_CHATROOM_SYSTEM_MESSAGES_SEND_MIMETYPES(new SystemPropertyEntryBoolean("AD585ChatroomSystemMessageSendMimeTypesEnabled", true)),
        SE456_PACKET208_CHANGES_ENABLED(new SystemPropertyEntryBoolean("SE456Packet208ChangesEnabled", true)),
        SE464_LATEST_MESSAGES_DIGEST_CHANGES_ENABLED(new SystemPropertyEntryBoolean("SE464LatestMessagesDigestChangesEnabled", true)),
        WW519_EMAIL_NOTIFICATION_USER_SETTINGS_ENABLED(new SystemPropertyEntryBoolean("WW519EmailNotificationUserSettingsEnabled", true)),
        SE351_CHAT_ROOM_DATA_CONCURRENT_COLLECTIONS_ENABLED(new SystemPropertyEntryBoolean("SE351ChatRoomDataConcurrentCollectionsEnabled", false)),
        SE218_ENABLED(new SystemPropertyEntryBoolean("SE218Enabled", true)),
        SE472_CHAT_CXN_PUT_MSG_LOGGING_CHANGES_ENABLED(new SystemPropertyEntryBoolean("SE472ChatConnectionPutMessageLoggingChangesEnabled", true)),
        SE501_CLEAR_MEMCAHCHED_0N_SSO_DISCONNECT_WAP_ENABLED(new SystemPropertyEntryBoolean("SE501ClearMemCachedOnSsoDisconnectWAP", true)),
        SE493_WEB_SOCKETS_ENABLED(new SystemPropertyEntryBoolean("SE493WebSocketsEnabled", true)),
        SE445_FAILED_AUTHS_EXPIRY_FIX_ENABLED(new SystemPropertyEntryBoolean("SE445FailedAuthsExpiryFixEnabled", true)),
        SE503_EXTEND_EXCEPTION_ON_INSUFFICIENT_CREDIT_ENABLED(new SystemPropertyEntryBoolean("SE503ExtendExceptionOnInsufficientCreditEnabled", true)),
        SE524_CREATE_CHATROOMPKT_ADDITIONAL_DATA_SUPPORT_ENABLED(new SystemPropertyEntryBoolean("SE524CreateChatRoomPktAdditionalDataSupportEnabled", true)),
        SE523_ALERT_USER_MENTIONED_IN_MIGBO_POST_ENABLED(new SystemPropertyEntryBoolean("SE523AlertUserMentionedInMigboPostEnabled", true)),
        SE504_ENABLED(new SystemPropertyEntryBoolean("SE504_ENABLED", true)),
        SE378_HOTKEY_PACKET_FULLURLDATA_ENABLED(new SystemPropertyEntryBoolean("SE378HotkeyPacketFullUrlDataEnabled", true)),
        SE545_CHAT_USER_END_SESSION_SYNC_CHANGES_ENABLED(new SystemPropertyEntryBoolean("SE545ChatUserEndSessionSyncChangesEnabled", true)),
        SE546_ENABLED(new SystemPropertyEntryBoolean("SE546_ENABLED", true)),
        SE552_NPE_FIX_ENABLED(new SystemPropertyEntryBoolean("SE552NPEFixEnabled", true)),
        FI_83_WEEK2_USE_AUTOGENERATED_PACKETS(new SystemPropertyEntryBoolean("FI83Week2UseAutogeneratedPacketsEnabled", true)),
        FI_83_WEEK3_USE_AUTOGENERATED_PACKETS(new SystemPropertyEntryBoolean("FI83Week3UseAutogeneratedPacketsEnabled", true)),
        SE599_FIX_ENABLED(new SystemPropertyEntryBoolean("SE599FixEnabled", true)),
        SE606_NPE_FIX_ENABLED(new SystemPropertyEntryBoolean("SE606NpeFixEnabled", true)),
        SE_604_USER_AGENT_TRACKING_ENABLED(new SystemPropertyEntryBoolean("SE604UserAgentTrackingEnabled", true)),
        SE605_EMAIL_HYPHENS_ENABLED(new SystemPropertyEntryBoolean("SE605EmailHyphensEnabled", true)),
        SE_639_ONE_WAY_PROXY_ENABLED(new SystemPropertyEntryBoolean("SE639OneWayProxyEnabled", true));

        private SystemPropertyEntry<?> value;
        private static final String NS = "Temp";

        private Temp(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        public static class Cache {
            public static LazyLoader<Boolean> ER170_ENABLED = new LazyLoader<Boolean>("ER170_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(Temp.ER170_ENABLED);
                }
            };
            public static LazyLoader<Boolean> SE511_ENABLED = new LazyLoader<Boolean>("SE511_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(Temp.SE511_ENABLED);
                }
            };
            public static LazyLoader<Boolean> ER74_ENABLED = new LazyLoader<Boolean>("ER74_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(Temp.ER74_ENABLED);
                }
            };
            public static LazyLoader<Boolean> SE433_ENABLED = new LazyLoader<Boolean>("SE433_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(Temp.SE433_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se423LoadInviteeUsesMasterDB = new LazyLoader<Boolean>("SE423_LOAD_INVITEE_USES_MASTER_DB", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE423_LOAD_INVITEE_USES_MASTER_DB);
                }
            };
            public static final LazyLoader<RedisShardAssignmentMode> se426ShardAssignmentMode = new LazyLoader<RedisShardAssignmentMode>("SE426_SHARD_ASSIGNMENT_MODE", 60000L){

                @Override
                protected RedisShardAssignmentMode fetchValue() throws Exception {
                    int modeCode = SystemProperty.getInt(SE426_SHARD_ASSIGNMENT_MODE);
                    RedisShardAssignmentMode mode = RedisShardAssignmentMode.fromCode(modeCode);
                    if (mode == null) {
                        RedisShardAssignmentMode fallbackMode = RedisShardAssignmentMode.SETNX_AND_GET_PIPELINE_WITH_DIST_LOCK;
                        this.getLogger().error((Object)("Invalid setting. ModeCode [" + modeCode + "] is unsupported. Falling back to [" + (Object)((Object)fallbackMode) + "]"));
                        return fallbackMode;
                    }
                    return mode;
                }
            };
            public static LazyLoader<Boolean> se454ChatroomSystemMessageSendMimeTypesEnabled = new LazyLoader<Boolean>("SE454_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE454_CHATROOM_SYSTEM_MESSAGES_SEND_MIMETYPES);
                }
            };
            public static LazyLoader<Boolean> se456Packet208ChangesEnabled = new LazyLoader<Boolean>("SE456_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE456_PACKET208_CHANGES_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se464LmdChangesEnabled = new LazyLoader<Boolean>("SE464_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE464_LATEST_MESSAGES_DIGEST_CHANGES_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se351ChatRoomDataConcurrentCollectionsEnabled = new LazyLoader<Boolean>("SE351_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE351_CHAT_ROOM_DATA_CONCURRENT_COLLECTIONS_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se218Enabled = new LazyLoader<Boolean>("SE218_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE218_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se472ChatCxnLoggingChangesEnabled = new LazyLoader<Boolean>("SE472_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE472_CHAT_CXN_PUT_MSG_LOGGING_CHANGES_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se501ClearMemcachedOnSsoDisconnectWapEnabled = new LazyLoader<Boolean>("SE501_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE501_CLEAR_MEMCAHCHED_0N_SSO_DISCONNECT_WAP_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se493WebSocketsEnabled = new LazyLoader<Boolean>("SE493_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE493_WEB_SOCKETS_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se445FailedAuthsExpiryFixEnabled = new LazyLoader<Boolean>("SE445_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE445_FAILED_AUTHS_EXPIRY_FIX_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se524CreateChatRoomPktAdditionalDataSupportEnabled = new LazyLoader<Boolean>("SE524_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE524_CREATE_CHATROOMPKT_ADDITIONAL_DATA_SUPPORT_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se378HotkeyPacketFullUrlDataEnabled = new LazyLoader<Boolean>("SE378_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE378_HOTKEY_PACKET_FULLURLDATA_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se504Enabled = new LazyLoader<Boolean>("SE504_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE504_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se545ChatUserEndSessionSyncChangesEnabled = new LazyLoader<Boolean>("SE545_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE545_CHAT_USER_END_SESSION_SYNC_CHANGES_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se552NPEFixEnabled = new LazyLoader<Boolean>("SE552_NPE_FIX_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE552_NPE_FIX_ENABLED);
                }
            };
            public static LazyLoader<Boolean> fi83Week2UseAutogeneratedPacketsEnabled = new LazyLoader<Boolean>("FI83_WEEK2_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(FI_83_WEEK2_USE_AUTOGENERATED_PACKETS);
                }
            };
            public static LazyLoader<Boolean> fi83Week3UseAutogeneratedPacketsEnabled = new LazyLoader<Boolean>("FI83_WEEK3_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(FI_83_WEEK3_USE_AUTOGENERATED_PACKETS);
                }
            };
            public static LazyLoader<Boolean> se599FixEnabled = new LazyLoader<Boolean>("SE599_FIX_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE599_FIX_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se606NpeFixEnabled = new LazyLoader<Boolean>("SE606_NPE_FIX_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE606_NPE_FIX_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se604UserAgentTrackingEnabled = new LazyLoader<Boolean>("SE_604_USER_AGENT_TRACKING_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE_604_USER_AGENT_TRACKING_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se605EmailHyphensEnabled = new LazyLoader<Boolean>("SE_605_EMAIL_HYPHENS_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE605_EMAIL_HYPHENS_ENABLED);
                }
            };
            public static LazyLoader<Boolean> se639OneWayPrxEnabled = new LazyLoader<Boolean>("SE_639_ONE_WAY_PROXY_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(SE_639_ONE_WAY_PROXY_ENABLED);
                }
            };
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MimeDataSettings implements SystemPropertyEntryInterface
    {
        IMAGE_THUMBNAIL_WIDTH(new SystemPropertyEntryInteger("ImageThumbnailWidth", 200));

        private SystemPropertyEntry<?> value;
        private static final String NS = "MimeData";

        private MimeDataSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ChatUserSessionsSettings implements SystemPropertyEntryInterface
    {
        FINAL_FAILURE_LOG_LEVEL(new SystemPropertyEntryInteger("FinalFailureLogLevel", Level.ERROR.toInt())),
        IMMEDIATE_PURGING_DISABLED(new SystemPropertyEntryBoolean("ImmediatePurgingDisabled", true)),
        RETRIES_ENABLED(new SystemPropertyEntryBoolean("RetriesEnabled", true)),
        TRIES_LIMIT(new SystemPropertyEntryInteger("TriesLimit", 3)),
        RETRY_INTERVAL_MILLIS(new SystemPropertyEntryInteger("RetryIntervalMillis", 1000)),
        RETRY_EXECUTOR_CORE_SIZE(new SystemPropertyEntryInteger("RetryExecutorCoreSize", 5)),
        PT73110676_CHANGES_ENABLED(new SystemPropertyEntryBoolean("PT73110676ChangesEnabled", true)),
        RETRY_STATS_ENABLED(new SystemPropertyEntryBoolean("RetryStatsEnabled", true));

        private SystemPropertyEntry<?> value;
        private static final String NS = "ChatUserSessions";

        private ChatUserSessionsSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum S3UploaderSettings implements SystemPropertyEntryInterface
    {
        ACCESS_KEY(new SystemPropertyEntryString("AccessKey", "")),
        SECRET_KEY(new SystemPropertyEntryString("SecretKey", "")),
        S3_BASE_DOMAIN(new SystemPropertyEntryString("S3BaseDomain", "s3.amazonaws.com")),
        BUCKET_NAME(new SystemPropertyEntryString("BucketName", "")),
        MAX_AGE_FOR_CACHE(new SystemPropertyEntryInteger("MaxAgeForCache", 86400)),
        USE_CDN_DOMAIN(new SystemPropertyEntryBoolean("UseCdnDomain", true)),
        CDN_DOMAIN(new SystemPropertyEntryString("CdnDomain", "http://b-img.cdn.mig33.com")),
        AWS_REFRESH(new SystemPropertyEntryInteger("AwsRefresh", 300000)),
        MAX_BUCKET_FAILURES(new SystemPropertyEntryInteger("MaxBucketFailures", 3)),
        MAX_UPLOAD_FILE_SIZE(new SystemPropertyEntryInteger("MaxUploadFileSize", 0x500000));

        private SystemPropertyEntry<?> value;
        private static final String NS = "S3Uploader";

        private S3UploaderSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum IceThreadMonitorSettings implements SystemPropertyEntryInterface
    {
        OBJC_TPOOL_MONITOR_STARTUP_ENABLED(new SystemPropertyEntryBoolean("ObjectCacheStartupEnabled", true)),
        OBJC_TPOOL_MONITOR_THREAD_COUNT(new SystemPropertyEntryInteger("ObjectCacheThreadCount", 50)),
        OBJC_TPOOL_MONITOR_MINIMUM_INTER_DUMP_DURATION(new SystemPropertyEntryInteger("ObjectCacheInterDumpDurationInSeconds", 3600)),
        GWAY_TPOOL_MONITOR_STARTUP_ENABLED(new SystemPropertyEntryBoolean("GatewayStartupEnabled", true)),
        GWAY_TPOOL_MONITOR_THREAD_COUNT(new SystemPropertyEntryInteger("GatewayThreadCount", 50)),
        GWAY_TPOOL_MONITOR_MINIMUM_INTER_DUMP_DURATION(new SystemPropertyEntryInteger("GatewayInterDumpDurationInSeconds", 3600));

        private SystemPropertyEntry<?> value;
        private static final String NS = "IceThreadMonitor";

        private IceThreadMonitorSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum IceAsyncSettings implements SystemPropertyEntryInterface
    {
        OBJECT_CACHE_AMD_ENABLED(new SystemPropertyEntryBoolean("ObjectCacheAMDEnabled", false)),
        CREATEUSEROBJECT_AMD_ENABLED(new SystemPropertyEntryBoolean("CreateUserObjectAMDEnabled", false)),
        OBJC_TPOOL_CORE_SIZE(new SystemPropertyEntryInteger("ObjcThreadPoolCoreSize", 100)),
        OBJC_TPOOL_MAX_SIZE(new SystemPropertyEntryInteger("ObjcThreadPoolMaxSize", 200)),
        OBJC_TPOOL_KEEP_ALIVE_SECONDS(new SystemPropertyEntryInteger("ObjcThreadPoolKeepAliveSeconds", 0)),
        OBJC_TPOOL_TASK_QUEUE_MAX_SIZE(new SystemPropertyEntryInteger("ObjcThreadPoolTaskQueueMaxSize", 5000)),
        OBJC_REFRESH_THREAD_POOL_PROPERTIES_ENABLED(new SystemPropertyEntryBoolean("RefreshThreadPoolPropertiesEnabled", true)),
        OBJC_TPOOL_PROPS_REFRESH_INTERVAL_SECS(new SystemPropertyEntryInteger("ObjcThreadPoolPropsRefreshIntervalSeconds", 300)),
        REGISTRY_AMD_ENABLED(new SystemPropertyEntryBoolean("RegistryAMDEnabled", true)),
        REGISTRY_TPOOL_CORE_SIZE(new SystemPropertyEntryInteger("RegistryThreadPoolCoreSize", 10)),
        REGISTRY_TPOOL_MAX_SIZE(new SystemPropertyEntryInteger("RegistryThreadPoolMaxSize", 25)),
        REGISTRY_TPOOL_KEEP_ALIVE_SECONDS(new SystemPropertyEntryInteger("RegistryThreadPoolKeepAliveSeconds", 0)),
        REGISTRY_TPOOL_TASK_QUEUE_MAX_SIZE(new SystemPropertyEntryInteger("RegistryThreadPoolTaskQueueMaxSize", 5000)),
        REGISTRY_REFRESH_THREAD_POOL_PROPERTIES_ENABLED(new SystemPropertyEntryBoolean("RegistryThreadPoolPropertiesEnabled", true)),
        REGISTRY_TPOOL_PROPS_REFRESH_INTERVAL_SECS(new SystemPropertyEntryInteger("RegistryThreadPoolPropsRefreshIntervalSeconds", 300)),
        GATEWAY_AMD_ENABLED(new SystemPropertyEntryBoolean("GatewayAMDEnabled", true)),
        GATEWAY_TPOOL_CORE_SIZE(new SystemPropertyEntryInteger("GatewayThreadPoolCoreSize", 100)),
        GATEWAY_TPOOL_MAX_SIZE(new SystemPropertyEntryInteger("GatewayThreadPoolMaxSize", 200)),
        GATEWAY_TPOOL_KEEP_ALIVE_SECONDS(new SystemPropertyEntryInteger("GatewayThreadPoolKeepAliveSeconds", 0)),
        GATEWAY_TPOOL_TASK_QUEUE_MAX_SIZE(new SystemPropertyEntryInteger("GatewayThreadPoolTaskQueueMaxSize", 5000)),
        GATEWAY_REFRESH_THREAD_POOL_PROPERTIES_ENABLED(new SystemPropertyEntryBoolean("GatewayRefreshThreadPoolPropertiesEnabled", true)),
        GATEWAY_TPOOL_PROPS_REFRESH_INTERVAL_SECS(new SystemPropertyEntryInteger("GatewayThreadPoolPropsRefreshIntervalSeconds", 300));

        private SystemPropertyEntry<?> value;
        private static final String NS = "IceAsyncSettings";

        private IceAsyncSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Store implements SystemPropertyEntryInterface
    {
        STORE_OLD_ENABLED(new SystemPropertyEntryBoolean("StoreOldEnabled", false));

        private SystemPropertyEntry<?> value;
        private static final String NS = "Store";

        private Store(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RegistrySettings implements SystemPropertyEntryInterface
    {
        USE_AMD_AMI_GET_MESSAGE_SWITCHBOARD(new SystemPropertyEntryBoolean("UseAmdAmiGetMessageSwitchboard", true));

        private SystemPropertyEntry<?> value;
        private static final String NS = "Registry";

        private RegistrySettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum CoreChatSettings implements SystemPropertyEntryInterface
    {
        EMAIL_BASED_OFFLINE_MESSAGE_DISABLED(new SystemPropertyEntryBoolean("EmailBasedOfflineMessagingDisabled", true)),
        STATS_COLLECTION_ENABLED(new SystemPropertyEntryBoolean("StatsCollectionEnabled", true)),
        STATS_COLLECTION_INTERVAL_MINUTES(new SystemPropertyEntryInteger("StatsCollectionIntervalMinutes", 15)),
        MIME_TYPE_MESSAGES_ENABLED(new SystemPropertyEntryBoolean("MimeTypeMessagesEnabled", false));

        private SystemPropertyEntry<?> value;
        private static final String NS = "CoreChat";

        private CoreChatSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum DataGridSettings implements SystemPropertyEntryInterface
    {
        ENABLED(new SystemPropertyEntryBoolean("Enabled", false)),
        DEFAULT_EXECUTOR_SERVICE(new SystemPropertyEntryString("DefaultExecutorService", "FES"));

        private SystemPropertyEntry<?> value;
        private static final String NS = "DataGrid";

        private DataGridSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum GiftSettings implements SystemPropertyEntryInterface
    {
        TRUE_GRID_ENABLED(new SystemPropertyEntryBoolean("TrueGridEnabled", false)),
        DISTRIBUTED_RESULTS_MAP_TTL_SECONDS(new SystemPropertyEntryInteger("DistributedResultsMapTtlSeconds", 1800)),
        DISTRIBUTED_RESULTS_MAP_CONCURRENCY_LEVEL(new SystemPropertyEntryInteger("DistributedResultsMapConcurrencyLevel", 4)),
        FUTURE_TIMEOUT_SECONDS(new SystemPropertyEntryInteger("FutureTimeoutSeconds", 60)),
        GIFT_EVENT_ENABLED(new SystemPropertyEntryBoolean("GiftEventEnabled", false)),
        VIRTUAL_GIFT_RECEIVED_URL(new SystemPropertyEntryString("VirtualGiftReceivedURL", "")),
        VIRTUAL_GIFT_NOTIFICATION_SMS(new SystemPropertyEntryString("VirtualGiftNotificationSMS", "")),
        GIFT_SHOWER_EVENTS_TO_SEND(new SystemPropertyEntryInteger("GiftShowerEventsToSend", 5)),
        GIFT_ALERT_OR_SMS_ENABLED(new SystemPropertyEntryBoolean("GiftAlertOrSmsEnabled", false)),
        GIFT_RECEIVE_TIMESTAMP_FORMAT(new SystemPropertyEntryString("GiftReceiveTimestampFormat", "dd MMMM yyyy 'AT' hh:mma"));

        private SystemPropertyEntry<?> value;
        private static final String NS = "Gift";

        private GiftSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum PersistentGroupChatSettings implements SystemPropertyEntryInterface
    {
        ENABLED(new SystemPropertyEntryBoolean("Enabled", true)),
        GROUP_CHAT_PARTICIPANT_REVIVAL_ENABLED(new SystemPropertyEntryBoolean("GroupChatParticipantRevivalEnabled", true)),
        GROUP_CHAT_IDLE_NOTIFICATION_ENABLED(new SystemPropertyEntryBoolean("GroupChatIdleNotificationEnabled", false)),
        GROUP_CHAT_IDLE_TIMEOUT_MESSAGE(new SystemPropertyEntryString("GroupChatIdleTimeoutMessage", "This group chat has been idle for too long, closing down..."));

        private SystemPropertyEntry<?> value;
        private static final String NS = "PersistentGroupChat";

        private PersistentGroupChatSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ChatSyncSettings implements SystemPropertyEntryInterface
    {
        CHAT_SYNC_ENABLED(new SystemPropertyEntryBoolean("Enabled", true)),
        STANDALONE_MESSAGE_SWITCHBOARD_ENABLED(new SystemPropertyEntryBoolean("StandaloneMessageSwitchboardEnabled", false)),
        REFRESH_THREAD_POOL_PROPERTIES_ENABLED(new SystemPropertyEntryBoolean("RefreshThreadPoolPropertiesEnabled", false)),
        LATEST_MESSAGES_DIGEST_PACKET_SEND_ENABLED(new SystemPropertyEntryBoolean("LatestMessagesDigestPacketSendEnabled", true)),
        LATEST_MESSAGES_DIGEST_SNIPPET_MAX_LENGTH(new SystemPropertyEntryInteger("LatestMessagesDigestSnippetMaxLength", 50)),
        CHATROOM_SYNC_ENABLED(new SystemPropertyEntryBoolean("ChatRoomSyncEnabled", true)),
        CHATROOM_CC_ALL_SESSIONS_ENABLED(new SystemPropertyEntryBoolean("ChatRoomCcAllSessionsEnabled", false)),
        END_SESSION_LEAVE_CHATROOMS_NEW_LOGIC_ENABLED(new SystemPropertyEntryBoolean("EndSessionLeaveChatRoomsNewLogicEnabled", false)),
        LOG_EXCLUSION_FILTER(new SystemPropertyEntryString("LogExclusionFilter", "")),
        MAX_TRIES_READ_ASYNC(new SystemPropertyEntryInteger("MaxTriesReadAsync", 3)),
        MAX_TRIES_READ_SYNC(new SystemPropertyEntryInteger("MaxTriesReadSync", 3)),
        MAX_TRIES_WRITE_ASYNC(new SystemPropertyEntryInteger("MaxTriesWriteAsync", 3)),
        MAX_TRIES_WRITE_SYNC(new SystemPropertyEntryInteger("MaxTriesWriteSync", 3)),
        RETRY_STORE_MESSAGE_ENABLED(new SystemPropertyEntryBoolean("RetryStoreMessageEnabled", true)),
        RETRY_LIVESYNC_MESSAGE_ENABLED(new SystemPropertyEntryBoolean("RetryLiveSyncMessageEnabled", true)),
        RETRY_CLOSED_CHAT_NOTIFICATION_ENABLED(new SystemPropertyEntryBoolean("RetryClosedChatNotificationEnabled", true)),
        RETRY_CURRENT_CHAT_LIST_UPDATE_ENABLED(new SystemPropertyEntryBoolean("RetryCurrentChatListUpdateEnabled", true)),
        RETRY_GET_CHATS_ENABLED(new SystemPropertyEntryBoolean("RetryGetChatsEnabled", true)),
        RETRY_GET_MESSAGES_ENABLED(new SystemPropertyEntryBoolean("RetryGetMessagesEnabled", true)),
        RETRY_LATEST_MESSAGES_DIGEST_ENABLED(new SystemPropertyEntryBoolean("RetryLatestMessagesDigestEnabled", true)),
        RETRY_RENAME_CHAT_ENABLED(new SystemPropertyEntryBoolean("RetryRenameChatEnabled", true)),
        RETRY_STORE_GROUP_CHAT_ENABLED(new SystemPropertyEntryBoolean("RetryStoreGroupChatEnabled", true)),
        RETRY_GET_MESSAGE_STATUS_EVENTS_ENABLED(new SystemPropertyEntryBoolean("RetryGetMessageStatusEventsEnabled", true)),
        RETRY_STORE_MESSAGE_STATUS_EVENTS_ENABLED(new SystemPropertyEntryBoolean("RetryStoreMessageStatusEventsEnabled", true)),
        STATS_COLLECTION_INTERVAL_MINUTES(new SystemPropertyEntryInteger("StatsCollectionIntervalMinutes", 15)),
        CPU_TIME_STATS_ENABLED(new SystemPropertyEntryBoolean("CPUTimeStatsEnabled", false)),
        WALLCLOCK_TIME_STATS_ENABLED(new SystemPropertyEntryBoolean("WallclockTimeStatsEnabled", false)),
        MESSAGE_COMPRESSION_ALGORITHM(new SystemPropertyEntryString("MessageCompressionAlgorithm", "")),
        OLD_CHAT_LISTS_COMPRESSION_ALGORITHM(new SystemPropertyEntryString("OldChatListsCompressionAlgorithm", "")),
        RENEW_EXPIRY_ON_READ_ENABLED(new SystemPropertyEntryBoolean("RenewExpiryOnReadEnabled", false)),
        RENEW_EXPIRY_ON_WRITE_ENABLED(new SystemPropertyEntryBoolean("RenewExpiryOnWriteEnabled", true)),
        CHAT_DEFINITION_EXPIRY_HOURS(new SystemPropertyEntryInteger("ChatDefinitionExpiryHours", 96)),
        CHAT_CONTENT_EXPIRY_HOURS(new SystemPropertyEntryInteger("ChatExpiryHours", 96)),
        CURRENT_CHAT_LIST_EXPIRY_HOURS(new SystemPropertyEntryInteger("CurrentChatListExpiryHours", 96)),
        OLD_CHAT_LISTS_EXPIRY_HOURS(new SystemPropertyEntryInteger("OldChatListsExpiryHours", 96)),
        CHAT_LIST_VERSION_EXPIRY_HOURS(new SystemPropertyEntryInteger("ChatListVersionExpiryHours", 96)),
        CURRENT_CHAT_LIST_MAX_LENGTH(new SystemPropertyEntryInteger("CurrentChatListMaxLength", 100)),
        OLD_CHAT_LISTS_MAX_LENGTH(new SystemPropertyEntryInteger("OldChatListsMaxLength", 100)),
        CHAT_MAX_LENGTH(new SystemPropertyEntryInteger("ChatMaxLength", 1000)),
        CHAT_MAX_LENGTH_TRUNCATION(new SystemPropertyEntryInteger("ChatMaxLengthTruncation", 950)),
        J2ME_CHAT_MANAGER_ENABLED(new SystemPropertyEntryBoolean("J2MEChatManagerEnabled", true)),
        MAX_GET_CHAT_REQUESTS_PER_MINUTE(new SystemPropertyEntryInteger("MaxGetChatRequestsPerMinute", 10000)),
        MAX_GET_MESSAGE_REQUESTS_PER_MINUTE(new SystemPropertyEntryInteger("MaxGetMessageRequestsPerMinute", 10000)),
        MAX_CHATS_STORED_PER_MINUTE(new SystemPropertyEntryInteger("MaxChatsStoredPerMinute", 10000)),
        MAX_MESSAGES_STORED_PER_MINUTE(new SystemPropertyEntryInteger("MaxMessagesStoredPerMinute", 10000)),
        MAX_J2ME_PUSHES_PER_MINUTE(new SystemPropertyEntryInteger("MaxJ2MEPushesPerMinute", 10000)),
        MAX_CHAT_LIST_UPDATES_PER_MINUTE(new SystemPropertyEntryInteger("MaxChatListUpdatesPerMinute", 10000)),
        MAX_GET_CHATS_REQUESTS_PER_USER_PER_DAY(new SystemPropertyEntryInteger("MaxGetChatsRequestsPerUserPerDay", 1000)),
        MAX_GET_MESSAGES_REQUESTS_PER_USER_PER_DAY(new SystemPropertyEntryInteger("MaxGetMessagesRequestsPerUserPerDay", 1000)),
        MAX_CHATS_STORED_PER_USER_PER_DAY(new SystemPropertyEntryInteger("MaxChatsStoredPerUserPerDay", 200)),
        MAX_MESSAGES_STORED_PER_USER_PER_DAY(new SystemPropertyEntryInteger("MaxMessagesStoredPerUserPerDay", 2000)),
        GET_MESSAGES_SYSADMIN_LIMIT(new SystemPropertyEntryInteger("GetMessagesSysAdminLimit", 50)),
        TPOOL_CORE_SIZE(new SystemPropertyEntryInteger("ThreadPoolCoreSize", 100)),
        TPOOL_MAX_SIZE(new SystemPropertyEntryInteger("ThreadPoolMaxSize", 200)),
        TPOOL_KEEP_ALIVE_SECONDS(new SystemPropertyEntryInteger("ThreadPoolKeepAliveSeconds", 0)),
        TPOOL_TASK_QUEUE_MAX_SIZE(new SystemPropertyEntryInteger("ThreadPoolTaskQueueMaxSize", 5000)),
        TPOOL_PROPS_REFRESH_INTERVAL_SECS(new SystemPropertyEntryInteger("ThreadPoolPropsRefreshIntervalSeconds", 300)),
        STORAGE_TIMEOUT_MILLIS(new SystemPropertyEntryInteger("StorageTimeoutMillis", 10000)),
        RETRIEVAL_TIMEOUT_MILLIS(new SystemPropertyEntryInteger("RetrievalTimeoutMillis", 10000));

        private SystemPropertyEntry<?> value;
        private static final String NS = "ChatSync";

        private ChatSyncSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ContentService implements SystemPropertyEntryInterface,
    EnumUtils.IEnumValueGetter<String>
    {
        RETRIEVE_VIRTUAL_GIFT_STORE_ITEM_ID(new SystemPropertyEntryBoolean("RetrieveVirtualGiftStoreItem", true)),
        VIRTUAL_GIFT_MESSAGE_LENGTH(new SystemPropertyEntryInteger("VirtualGiftMessageLength", 40)),
        VIRTUAL_GIFT_POST_MESSAGES(new SystemPropertyEntryString("VirtualGiftPostMessages", "Can you feel the love?  I sent the %s gift to %s %s")),
        VIRTUAL_GIFT_POST_MESSAGE(new SystemPropertyEntryString("VirtualGiftPostMessage", "I put a smile on someone's face today! I sent a %s gift to %s %s")),
        VIRTUAL_GIFT_POST_MESSAGE_CUSTOM(new SystemPropertyEntryString("VirtualGiftPostMessageCustom", "I sent a %s gift to %s %s \"%s\" ")),
        ENABLE_FUSION_PKT_GET_EMOTICON_HIGH_RES(new SystemPropertyEntryBoolean("EnableFusionPktGetEmoticonHighRes", false)),
        VIRTUAL_GIFT_RESOLUTIONS_DEFAULT(new SystemPropertyEntryInteger("VirtualGiftResolutionsDefault", 64)),
        VIRTUAL_GIFT_RESOLUTIONS(new SystemPropertyEntryShortArray("VirtualGiftResolutions", new short[]{12, 14, 16})),
        VIRTUAL_GIFT_RESOLUTIONS_GIF(new SystemPropertyEntryShortArray("VirtualGiftResolutions[GIF]", new short[]{12, 14, 16})),
        VIRTUAL_GIFT_RESOLUTIONS_PNG(new SystemPropertyEntryShortArray("VirtualGiftResolutions[PNG]", new short[]{12, 14, 16, 64})),
        VIRTUAL_GIFT_IMAGE_FORMATS(new SystemPropertyEntryStringArray("VirtualGiftImageFormats", new String[]{"PNG", "GIF"})),
        ENABLE_STICKERS(new SystemPropertyEntryBoolean("EnableStickers", false)),
        MIN_REPUTATION_LEVEL_FOR_SENDING_STICKERS(new SystemPropertyEntryInteger("MinReputationLevelForSendingStickers", 1)),
        DEFAULT_FREE_STICKER_PACKS(new SystemPropertyEntryStringArray("DefaultFreeStickerPacks", new String[0])),
        STICKER_PACK_IMAGE_RESOLUTION(new SystemPropertyEntryShortArray("StickerPackImageResolution", new short[]{36, 48, 72, 96})),
        STICKER_PACK_THUMBNAIL_BASE_FOLDER_URL(new SystemPropertyEntryString("StickerPackThumbnailBaseUrl", "images/emoticons/stickers")),
        STICKER_PACK_THUMBNAIL_DEFAULT_EXTENSION(new SystemPropertyEntryString("StickerPackThumbnailDefaultExtension", ".png")),
        STICKER_IMAGE_BASE_FOLDER_URL(new SystemPropertyEntryString("StickerImageBaseFolderUrl", "images/emoticons/stickers")),
        STICKER_IMAGE_PHYSICAL_FOLDER(new SystemPropertyEntryString("StickerImagePhyiscalFolder", "/usr/fusion/emoticons/stickers")),
        LOAD_EMOTICONS_FROM_REDIS(new SystemPropertyEntryBoolean("LoadEmoticonsFromRedis", false)),
        LOAD_THUMBNAIL_STICKER_DIR_AS_PREVIEW(new SystemPropertyEntryBoolean("LoadThumbnailStickerDirForPreview", true)),
        MINIBLOG_TEXT_CONTENT_MAX_LENGTH(new SystemPropertyEntryInteger("MiniblogTextContentMaxLength", 180)),
        IGNORE_ON_ALREADY_OWNED_STICKER_OR_EMOTICON_PACK(new SystemPropertyEntryBoolean("IgnoreOnAlreadyOwnedStickerOrEmoticonPack", true)),
        ENABLE_NEW_IMPLEMENTATION_FOR_CREATE_MINI_BLOG_TEXT(new SystemPropertyEntryBoolean("EnableNewImplementationForCreateMiniBlogText", true));

        private SystemPropertyEntry<?> value;
        private static String NS;
        private static final short[] EMPTY_SHORT;

        private ContentService(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        public String getEnumValue() {
            return StringUtil.trimmedUpperCase(this.getEntry().getName());
        }

        public static SystemPropertyEntryInterface getVirtualGiftResolutionSettingEnum(String imageFormatName) {
            if (StringUtil.isBlank(imageFormatName)) {
                return VIRTUAL_GIFT_RESOLUTIONS;
            }
            final String propertyName = (VIRTUAL_GIFT_RESOLUTIONS.getEntry().getName() + "[" + imageFormatName.trim() + "]").toUpperCase();
            ContentService contentServiceEnum = SingletonHolder.lookupByName.get(propertyName);
            if (contentServiceEnum != null) {
                return contentServiceEnum;
            }
            return new SystemPropertyEntryInterface(){

                @Override
                public SystemPropertyEntry<?> getEntry() {
                    return new SystemPropertyEntryShortArray(propertyName, EMPTY_SHORT);
                }

                @Override
                public String getName() {
                    return SystemPropertyEntities.getNameWithNamespace(NS, this.getEntry().getName());
                }
            };
        }

        static {
            NS = "ContentService";
            EMPTY_SHORT = new short[0];
        }

        private static class SingletonHolder {
            public static final Map<String, ContentService> lookupByName = EnumUtils.buildLookUpMap(new HashMap(), ContentService.class);

            private SingletonHolder() {
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum CreditTransferFeeTypeSettings implements SystemPropertyEntryInterface
    {
        ENABLED(new SystemPropertyEntryBoolean("Enabled", false)),
        FEE_PERCENTAGE(new SystemPropertyEntryDouble("FeePercentage", 5.0)),
        MINIMUM_TRANSFER_AMOUNT_REQUIRED_IN_USD(new SystemPropertyEntryDouble("MinimumTransferAmountRequiredInUSD", 0.2));

        private SystemPropertyEntry<?> value;
        private static final String NS = "CreditTransferFeeSetting";

        private CreditTransferFeeTypeSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        public CreditTransferFeeSpecificSetting forTransferFee(CreditTransferData.CreditTransferFeeEnum e) {
            return new CreditTransferFeeSpecificSetting(e, this);
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private class CreditTransferFeeSpecificSetting
        implements SystemPropertyEntryInterface {
            private CreditTransferData.CreditTransferFeeEnum creditTransferFeeType;
            private SystemPropertyEntry<?> value;

            @Override
            public SystemPropertyEntry<?> getEntry() {
                return this.value;
            }

            @Override
            public String getName() {
                return SystemPropertyEntities.getNameWithNamespace(SystemPropertyEntities.getNameWithNamespace(CreditTransferFeeTypeSettings.NS, this.creditTransferFeeType.name()), this.value.getName());
            }

            CreditTransferFeeSpecificSetting(CreditTransferData.CreditTransferFeeEnum creditFeeTransferType, SystemPropertyEntryInterface parentProperty) {
                this.value = parentProperty.getEntry();
                this.creditTransferFeeType = creditFeeTransferType;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum CreditTransferFee implements SystemPropertyEntryInterface
    {
        ENABLED(new SystemPropertyEntryBoolean("Enabled", false)),
        ENABLED_TO_PUBLIC(new SystemPropertyEntryBoolean("EnabledToPublic", false)),
        REVERSE_ENABLED(new SystemPropertyEntryBoolean("ReverseEnabled", false));

        private SystemPropertyEntry<?> value;
        private static final String NS = "CreditTransferFee";

        private CreditTransferFee(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Reputation implements SystemPropertyEntryInterface
    {
        SCORE_TO_LEVEL_CACHE_ENTRY_TTL_MILLIS(new SystemPropertyEntryLong("ScoreToLevelCacheEntryTtlMillis", 600000L));

        private SystemPropertyEntry<?> value;
        private static final String NS = "Reputation";

        private Reputation(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum RecommendationServiceSettings_CollectedAddressBookContactData implements SystemPropertyEntryInterface
    {
        __UNIT_TEST_ENTRY_LONG1(new SystemPropertyEntryLong("__unitTestEntryLong1", 1200L)),
        __UNIT_TEST_ENTRY_STRING0(new SystemPropertyEntryString("__unitTestEntryString0", "CollectedAddressBookContactData_String0default")),
        DATA_COLLECTION_SERVICE_ENABLED(new SystemPropertyEntryBoolean("DataCollectionServiceEnabled", false)),
        UPLOAD_TICKET_CIPHER_CIPHER_PASSPHRASE(new SystemPropertyEntryString("UploadTicketCipherPassphrase", "*mR.$2rk04#VELG!")),
        UPLOAD_TICKET_EXPIRY_SECS(new SystemPropertyEntryLong("UploadTicketExpirySecs", 300L)),
        UPLOAD_DATA_MIN_MIG_LEVEL(new SystemPropertyEntryInteger("UploadDataMinMigLevel", -1)),
        RATE_LIMIT_UPLOAD_TICKET_REQUEST(new SystemPropertyEntryString("RateLimitUploadTicketRequest", "1000/1M")),
        RATE_LIMIT_UPLOAD_DATA(new SystemPropertyEntryString("RateLimitUploadData", "1000/1M")),
        RATE_LIMIT_UPLOAD_TICKET_REQUEST_PER_USER_ID(new SystemPropertyEntryString("RateLimitUploadTicketRequestPerUserID", "1/30S")),
        RATE_LIMIT_UPLOAD_DATA_PER_USER_ID(new SystemPropertyEntryString("RateLimitUploadDataPerUserID", "1/30S"));

        private SystemPropertyEntry<?> entry;
        private static final String NS;

        private RecommendationServiceSettings_CollectedAddressBookContactData(SystemPropertyEntry<?> entry) {
            this.entry = entry;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.entry.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.entry;
        }

        static {
            NS = SystemPropertyEntities.getCollectedDataTypeNameSpace("RecommendationService", CollectedDataTypeEnum.ADDRESSBOOKCONTACT);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RecommendationServiceSettings implements SystemPropertyEntryInterfaceWithNamespace
    {
        __UNIT_TEST_ENTRY_LONG0(new SystemPropertyEntryLong("__unitTestEntryLong0", 100L)),
        __UNIT_TEST_ENTRY_LONG1(new SystemPropertyEntryLong("__unitTestEntryLong1", 200L)),
        __UNIT_TEST_ENTRY_STRING0(new SystemPropertyEntryString("__unitTestEntryString0", "String0default")),
        __UNIT_TEST_ENTRY_STRING1(new SystemPropertyEntryString("__unitTestEntryString1", "String1default")),
        DATA_COLLECTION_SERVICE_ENABLED(new SystemPropertyEntryBoolean("DataCollectionServiceEnabled", false)),
        DATA_COLLECTION_SERVICE_ENDPOINTS(new SystemPropertyEntryString("DataCollectionServiceEndPoints", "")),
        ENABLE_RDCS_PROXY_WITH_CACHED_CONNECTION(new SystemPropertyEntryBoolean("EnableRDCSProxyWithCachedConnection", false)),
        UPLOAD_TICKET_CIPHER_CIPHER_PASSPHRASE(new SystemPropertyEntryString("UploadTicketCipherPassphrase", "*mR.$2rk04#VELG!")),
        UPLOAD_TICKET_EXPIRY_SECS(new SystemPropertyEntryLong("UploadTicketExpirySecs", 300L)),
        UPLOAD_DATA_MIN_MIG_LEVEL(new SystemPropertyEntryInteger("UploadDataMinMigLevel", -1)),
        RATE_LIMIT_UPLOAD_TICKET_REQUEST(new SystemPropertyEntryString("RateLimitUploadTicketRequest", "1000/1M")),
        RATE_LIMIT_UPLOAD_DATA(new SystemPropertyEntryString("RateLimitUploadData", "1000/1M")),
        RATE_LIMIT_UPLOAD_TICKET_REQUEST_PER_USER_ID(new SystemPropertyEntryString("RateLimitUploadTicketRequestPerUserID", "1/30S")),
        RATE_LIMIT_UPLOAD_DATA_PER_USER_ID(new SystemPropertyEntryString("RateLimitUploadDataPerUserID", "1/30S")),
        MAX_NUMBER_OF_ENTRIES_PER_UPLOAD(new SystemPropertyEntryInteger("MaxNumberOfEntriesPerUpload", 1000)),
        MAX_TOTAL_ENTRY_SIZES_PER_UPLOAD(new SystemPropertyEntryLong("MaxTotalEntrySizesPerUpload", 1000L)),
        GENERATION_SERVICE_VIA_INTERNAL_TIMER_ENABLED(new SystemPropertyEntryBoolean("GenerationServiceViaInternalTimerEnabled", true)),
        GENERATION_SERVICE_VIA_QUARTZ_SCHEDULER_ENABLED(new SystemPropertyEntryBoolean("GenerationServiceViaQuartzSchedulerEnabled", false)),
        RGS_INTERNAL_TIMER_REFRESH_INTERVAL_SECONDS(new SystemPropertyEntryInteger("RgsInternalTimerRefreshIntervalSeconds", 300)),
        RGS_INTERNAL_TIMER_SCHEDULE_GAP_SECONDS(new SystemPropertyEntryInteger("RgsInternalTimerScheduleGapSeconds", 600)),
        RGS_MAX_PIPELINE_LENGTH(new SystemPropertyEntryInteger("RgsMaxPipelineLength", 1000)),
        DELIVERY_SERVICE_ENABLED(new SystemPropertyEntryBoolean("DeliveryServiceEnabled", false)),
        HIVE2_JDBC_DRIVER_CLASS(new SystemPropertyEntryString("Hive2JdbcDriverClass", "org.apache.hive.jdbc.HiveDriver")),
        HIVE2_JDBC_URL(new SystemPropertyEntryString("Hive2JdbcUrl", "jdbc:hive2://log01")),
        HIVE2_JDBC_USERNAME(new SystemPropertyEntryString("Hive2JdbcUsername", "hive")),
        HIVE2_JDBC_PASSWORD(new SystemPropertyEntryString("Hive2JdbcPassword", "")),
        FEATURED(new SystemPropertyEntryStringArray("Featured", new String[0])),
        BLACKLISTED(new SystemPropertyEntryStringArray("Blacklisted", new String[0])),
        FEATURED_RECOMMENDATIONS_ENABLED(new SystemPropertyEntryBoolean("FeaturedRecommendationsEnabled", true)),
        RECOMMENDATIONS_CACHE_ENABLED(new SystemPropertyEntryBoolean("RecommendationsCacheEnabled", false)),
        RECOMMENDATIONS_CACHE_EXPIRY_IN_SECONDS(new SystemPropertyEntryLong("RecommendationsCacheExpiryInSeconds", 3600L)),
        RECOMMENDATIONS_BLACKLIST_FILTER_ENABLED(new SystemPropertyEntryBoolean("RecommendationsBlacklistFilterEnabled", true)),
        INDIVIDUAL_USER_RECOMMENDATION_REASON_DETAILS_PART1(new SystemPropertyEntryString("IndividualUserRecommendationReasonDetailsPart1", "Also followed by $1")),
        INDIVIDUAL_USER_RECOMMENDATION_REASON_DETAILS_PART2(new SystemPropertyEntryString("IndividualUserRecommendationReasonDetailsPart2", " and %s other people")),
        GLOBAL_USER_RECOMMENDATION_REASON(new SystemPropertyEntryString("GlobalUserRecommendationReason", "People you might want to know")),
        REGIONAL_USER_RECOMMENDATION_REASON(new SystemPropertyEntryString("RegionalUserRecommendationReason", "Most active users in your country")),
        USER_RECOMMENDATION_WEIGHT_FOR_FRIENDS(new SystemPropertyEntryDouble("UserRecommendationWeightForFriends", 3.0)),
        USER_RECOMMENDATION_WEIGHT_FOR_FOLLOWING(new SystemPropertyEntryDouble("UserRecommendationWeightForFollowing", 2.0)),
        USER_RECOMMENDATION_WEIGHT_FOR_FOLLOWER(new SystemPropertyEntryDouble("UserRecommendationWeightForFollower", 1.0)),
        MIX_USER_RECOMMENDATION_ENABLE(new SystemPropertyEntryBoolean("MixUserRecommendationEnable", false)),
        MIX_USER_RECOMMENDATION_WEIGHT(new SystemPropertyEntryDouble("MixUserRecommendationWeight", 1000.0)),
        GLOBAL_VERIFIED_USER_RECOMMENDATION_ENABLE(new SystemPropertyEntryBoolean("GlobalVerifiedUserRecommendationEnable", true)),
        REGIONAL_MOST_ACTIVE_USER_RECOMMENDATION_ENABLE(new SystemPropertyEntryBoolean("RegionalMostActiveUserRecommendationEnable", false)),
        USER_RECOMMENDATION_MIN_THRESHOLD(new SystemPropertyEntryInteger("UserRecommendationMinThreshold", 10)),
        BOOST_GLOBAL_VERIFIED_USER_RECOMMENDATION_ENABLE(new SystemPropertyEntryBoolean("BoostGlobalVerifiedUserRecommendationEnable", true)),
        GLOBAL_VERIFIED_USER_RECOMMENDATION_BOOST_WEIGHT(new SystemPropertyEntryDouble("GlobalVerifiedUserRecommendationBoostWeight", 1000.0)),
        REGIONAL_MOST_ACTIVE_USER_SPAM_THRESHOLD(new SystemPropertyEntryDouble("RegionalMostActiveUserSpamThreshold", 10000.0)),
        DEFAULT_RESULT_SIZE(new SystemPropertyEntryInteger("DefaultResultSize", 10)),
        MAX_ALLOWED_RESULT_SIZE(new SystemPropertyEntryInteger("MaxAllowedResultSize", 1000)),
        REDIS_LOCATION_ERROR_LOGGING_ENABLED(new SystemPropertyEntryBoolean("RedisLocationErrorLoggingEnabled", true));

        private SystemPropertyEntry<?> entry;
        private static final String NS = "RecommendationService";

        @Override
        public String getNamespace() {
            return NS;
        }

        public RecommendationTypeSpecificSetting forRecommendationType(Enums.RecommendationTypeEnum recType) {
            return new RecommendationTypeSpecificSetting(recType, this);
        }

        public SystemPropertyEntryInterface forCollectedDataType(CollectedDataTypeEnum dataType) {
            SystemPropertyEntryInterface entryInterface = SystemPropertyEntities.getEntry(SystemPropertyEntities.getNameWithNamespace(SystemPropertyEntities.getCollectedDataTypeNameSpace(NS, dataType), this.entry.getName()));
            if (entryInterface != null) {
                return entryInterface;
            }
            return new CollectedDataTypeSpecificSetting(dataType, this);
        }

        private RecommendationServiceSettings(SystemPropertyEntry<?> entry) {
            this.entry = entry;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.entry;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.entry.getName());
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static final class CollectedDataTypeSpecificSetting
        implements SystemPropertyEntryInterface {
            private final SystemPropertyEntry<?> entry;
            private final String name;

            @Override
            public SystemPropertyEntry<?> getEntry() {
                return this.entry;
            }

            @Override
            public String getName() {
                return this.name;
            }

            CollectedDataTypeSpecificSetting(CollectedDataTypeEnum dataType, SystemPropertyEntryInterface parentProperty) {
                this.entry = parentProperty.getEntry();
                this.name = dataType != null ? SystemPropertyEntities.getNameWithNamespace(SystemPropertyEntities.getCollectedDataTypeNameSpace(RecommendationServiceSettings.NS, dataType), this.entry.getName()) : SystemPropertyEntities.getNameWithNamespace(RecommendationServiceSettings.NS, this.entry.getName());
            }
        }

        public static class RecommendationTypeSpecificSetting
        extends SystemPropertyEntryWithParent {
            RecommendationTypeSpecificSetting(Enums.RecommendationTypeEnum recType, RecommendationServiceSettings parentProperty) {
                super(SystemPropertyEntryFallbackCreatorRegistry.getInstance(parentProperty), recType == null ? null : recType.name(), parentProperty);
            }

            public RecommendationTargetSpecificSetting forTargetType(Enums.RecommendationTargetEnum targetType) {
                return new RecommendationTargetSpecificSetting(this.getSystemPropertyEntryFallbackCreator(), targetType, (SystemPropertyEntryInterfaceWithNamespace)this);
            }
        }

        public static class RecommendationTargetSpecificSetting
        extends SystemPropertyEntryWithParent {
            RecommendationTargetSpecificSetting(SystemPropertyEntryFallbackCreator sysPropEntryFallbackCreator, Enums.RecommendationTargetEnum targetType, SystemPropertyEntryInterfaceWithNamespace parentProperty) {
                super(sysPropEntryFallbackCreator, targetType == null ? null : targetType.value(), parentProperty);
            }

            public RecommendationViewSpecificSetting forViewType(SSOEnums.View viewType) {
                return new RecommendationViewSpecificSetting(this.getSystemPropertyEntryFallbackCreator(), viewType, (SystemPropertyEntryInterfaceWithNamespace)this);
            }

            public RecommendationCountrySpecificSetting forCountryId(Integer countryId) {
                return new RecommendationCountrySpecificSetting(this.getSystemPropertyEntryFallbackCreator(), countryId, (SystemPropertyEntryInterfaceWithNamespace)this);
            }
        }

        public static class RecommendationCountrySpecificSetting
        extends SystemPropertyEntryWithParent {
            RecommendationCountrySpecificSetting(SystemPropertyEntryFallbackCreator sysPropEntryFallbackCreator, Integer countryId, SystemPropertyEntryInterfaceWithNamespace parentProperty) {
                super(sysPropEntryFallbackCreator, countryId == null ? null : String.valueOf(countryId), parentProperty);
            }
        }

        public static class RecommendationViewSpecificSetting
        extends SystemPropertyEntryWithParent {
            RecommendationViewSpecificSetting(SystemPropertyEntryFallbackCreator sysPropEntryFallbackCreator, SSOEnums.View viewType, SystemPropertyEntryInterfaceWithNamespace parentProperty) {
                super(sysPropEntryFallbackCreator, viewType == null ? null : String.valueOf(viewType.value()), parentProperty);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum SmsSettings implements SystemPropertyEntryInterface
    {
        LOG_REFUSED_TO_SEND(new SystemPropertyEntryBoolean("LogWhenRefusedToSend", false)),
        SMS_ENGINE_ENABLED(new SystemPropertyEntryBoolean("SmsEngineEnabled", false)),
        SEND_ACTIVATION_CODE_ENABLED(new SystemPropertyEntryBoolean("SendActivationCodeEnabled", false)),
        SEND_FORGOT_PASSWORD_ENABLED(new SystemPropertyEntryBoolean("SendForgotPasswordEnabled", false)),
        SEND_USER_REFERRAL_ENABLED(new SystemPropertyEntryBoolean("SendUserReferralEnabled", false)),
        SEND_USER_REFERRAL_ACTIVATION_ENABLED(new SystemPropertyEntryBoolean("SendUserReferralActivationEnabled", false)),
        SEND_MIG33_WAP_PUSH_ENABLED(new SystemPropertyEntryBoolean("SendMig33WapPushEnabled", false)),
        SEND_MIG33_PREMIUM_SMS_ENABLED(new SystemPropertyEntryBoolean("SendMig33PremiumSmsEnabled", false)),
        SEND_TT_NOTIFICATION_ENABLED(new SystemPropertyEntryBoolean("SendTtNotificationEnabled", false)),
        SEND_SMS_CALLBACK_HELP_ENABLED(new SystemPropertyEntryBoolean("SendSmsCallbackHelpEnabled", false)),
        SEND_SMS_CALLBACK_BALANCE_ENABLED(new SystemPropertyEntryBoolean("SendSmsCallbackBalanceEnabled", false)),
        SEND_EMAIL_ALERT_ENABLED(new SystemPropertyEntryBoolean("SendEmailAlertEnabled", false)),
        SEND_BANK_TRANSFER_CONFIRMATION_ENABLED(new SystemPropertyEntryBoolean("SendBankTransferConfirmationEnabled", false)),
        SEND_LOW_BALANCE_ALERT_ENABLED(new SystemPropertyEntryBoolean("SendLowBalanceAlertEnabled", false)),
        SEND_MERCHANT_USER_ACTIVATION_ENABLED(new SystemPropertyEntryBoolean("SendMerchantUserActivationEnabled", false)),
        SEND_BUZZ_ENABLED(new SystemPropertyEntryBoolean("SendBuzzEnabled", false)),
        SEND_LOOKOUT_ENABLED(new SystemPropertyEntryBoolean("SendLookoutEnabled", false)),
        SEND_WESTERN_UNION_CONFIRMATION_ENABLED(new SystemPropertyEntryBoolean("SendWesternUnionConfirmationEnabled", false)),
        SEND_MOBILE_CONTENT_DOWNLOAD_ENABLED(new SystemPropertyEntryBoolean("SendMobileContentDownloadEnabled", false)),
        SEND_SMS_VOUCHER_RECHARGE_ENABLED(new SystemPropertyEntryBoolean("SendSmsVoucherRechargeEnabled", false)),
        SEND_VIRTUAL_GIFT_NOTIFICATION_ENABLED(new SystemPropertyEntryBoolean("SendVirtualGiftNotificationEnabled", false)),
        SEND_GROUP_ANNOUNCEMENT_NOTIFICATION_ENABLED(new SystemPropertyEntryBoolean("SendGroupAnnouncementNotificationEnabled", false)),
        SEND_BANGLALINK_URL_DOWNLOAD_ENABLED(new SystemPropertyEntryBoolean("SendBanglalinkUrlDownloadEnabled", false)),
        SEND_BANGLALINK_VOUCHER_ENABLED(new SystemPropertyEntryBoolean("SendBanglalinkVoucherEnabled", false)),
        SEND_GROUP_EVENT_NOTIFICATION_ENABLED(new SystemPropertyEntryBoolean("SendGroupEventNotificationEnabled", false)),
        SEND_INDOSAT_URL_DOWNLOAD_ENABLED(new SystemPropertyEntryBoolean("SendIndosatUrlDownloadEnabled", false)),
        SEND_SUBSCRIPTION_EXPIRY_NOTIFICATION_ENABLED(new SystemPropertyEntryBoolean("SendSubscriptionExpiryNotificationEnabled", false)),
        SEND_MARKETING_REWARD_NOTIFICATION_ENABLED(new SystemPropertyEntryBoolean("SendMarketingRewardNotificationEnabled", false)),
        SEND_USER_REFERRAL_VIA_GAMES_ENABLED(new SystemPropertyEntryBoolean("SendUserReferralViaGamesEnabled", false)),
        SEND_INVITATION_ENABLED(new SystemPropertyEntryBoolean("SendInvitationEnabled", false)),
        SEND_VERIFICATION_CODE_ENABLED(new SystemPropertyEntryBoolean("SendVerificationCodeEnabled", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private SmsSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Sms";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TokenizerSettings implements SystemPropertyEntryInterface
    {
        DEFAULT_CHARSET(new SystemPropertyEntryString("DefaultCharset", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")),
        MINIMUM_LENGTH(new SystemPropertyEntryInteger("MinimumLength", 20)),
        DEFAULT_EXPIRATION_IN_MILLIS(new SystemPropertyEntryLong("DefaultExpirationInMillis", 600000L));

        private SystemPropertyEntry<?> value;
        private static final String NS = "Tokenizer";

        private TokenizerSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum IMSettings implements SystemPropertyEntryInterface
    {
        MINMIGLEVEL(new SystemPropertyEntryInteger("MinMigLevel", Integer.MIN_VALUE)),
        MAX_CONCURRENT_CONNECTIONS(new SystemPropertyEntryInteger("MaxConcurrentConnections", 500)),
        DISCARD_MESSAGE_ABOVE_THRESHOLD_ENABLED(new SystemPropertyEntryBoolean("DiscardMessageAboveThresholdEnabled", false)),
        MAX_MESSAGES_RECEIVE_RATE_LIMIT(new SystemPropertyEntryString("MaxMessagesReceiveRateLimit", "60/1M")),
        DEFAULT_ONLINE_MESSAGE(new SystemPropertyEntryString("DefaultOnlineString", "I'm using migme (http://mig.me)..."));

        private SystemPropertyEntry<?> value;
        private static final String NS = "IMSettings";

        private IMSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        public IMSpecificSetting forIM(ImType e) {
            return new IMSpecificSetting(e, this);
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private class IMSpecificSetting
        implements SystemPropertyEntryInterface {
            private ImType imType;
            private SystemPropertyEntry<?> value;

            @Override
            public SystemPropertyEntry<?> getEntry() {
                return this.value;
            }

            @Override
            public String getName() {
                return SystemPropertyEntities.getNameWithNamespace(SystemPropertyEntities.getNameWithNamespace(IMSettings.NS, this.imType.name()), this.value.getName());
            }

            IMSpecificSetting(ImType imType, SystemPropertyEntryInterface parentProperty) {
                this.value = parentProperty.getEntry();
                this.imType = imType;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ForgotUsername implements SystemPropertyEntryInterface
    {
        REQUEST_RATE_LIMIT(new SystemPropertyEntryString("RequestRateLimit", "10/1D")),
        EMAIL_SUBJECT(new SystemPropertyEntryString("EmailSubject", "Welcome back to migme! %1")),
        EMAIL_CONTENT(new SystemPropertyEntryString("EmailContent", "Hi %1,\n\nWelcome back to migme. Your username on migme is:\n\n%2\n\nClick the below link to login migme and have fun.\n\nhttps://login.mig.me\n\nThanks,\nThe migme Team"));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private ForgotUsername(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "ForgotUsername";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ForgotPassword implements SystemPropertyEntryInterface
    {
        ENABLED(new SystemPropertyEntryBoolean("Enabled", false)),
        ENABLED_FORGOT_PASSWORD_VIA_SECURITY_QUESTION(new SystemPropertyEntryBoolean("EnabledForgotPasswordViaSecurityQuestion", true)),
        ENABLED_FORGOT_PASSWORD_VIA_SMS(new SystemPropertyEntryBoolean("EnabledForgotPasswordViaSMS", true)),
        ENABLED_FORGOT_PASSWORD_VIA_EMAIL(new SystemPropertyEntryBoolean("EnabledForgotPasswordViaEmail", true)),
        ENABLED_CHECK_COUNTRY_FOR_FORGOT_PASSWORD_VIA_SMS(new SystemPropertyEntryBoolean("EnabledCheckCountryForForgotPasswordViaSMS", true)),
        ENABLED_CHECK_RETRIEVE_TIME_FOR_FORGOT_PASSWORD_VIA_SMS(new SystemPropertyEntryBoolean("EnabledCheckRetrieveTimeForForgotPasswordViaSMS", true)),
        ENABLED_RATELIMIT_PER_IP(new SystemPropertyEntryBoolean("EnabledRateLimitPerIP", true)),
        WHITELIST_COUNTRIES_FOR_SMS(new SystemPropertyEntryIntegerArray("WhitelistCountriesForSMS", new int[]{106, 107, 157})),
        ATTEMPT_RATE_LIMIT(new SystemPropertyEntryString("AttemptRateLimit", "10/1D")),
        ATTEMPT_RATE_LIMIT_VIA_EMAIL(new SystemPropertyEntryString("AttemptRateLimitViaEmail", "3/1D")),
        ATTEMPT_RATE_LIMIT_VIA_SQ(new SystemPropertyEntryString("AttemptRateLimitViaSQ", "3/2D")),
        ATTEMPT_RATE_LIMIT_VIA_SMS(new SystemPropertyEntryString("AttemptRateLimitViaSMS", "3/5D")),
        ATTEMPT_RATE_LIMIT_PER_IP(new SystemPropertyEntryString("AttemptRateLimitPerIP", "10/1D")),
        ENABLED_CHECK_FORGOT_PASSWORD_VIA_SMS_MAX_RETRIEVE_TIME(new SystemPropertyEntryBoolean("EnabledCheckForgotPasswordViaSMSMaxRetrieveTime", true)),
        FORGOT_PASSWORD_VIA_SMS_MAX_RETRIEVE_TIME(new SystemPropertyEntryInteger("ForgotPasswordViaSMSMaxRetrieveTime", 2)),
        FORGOT_PASSWORD_SMS_MESSAGE(new SystemPropertyEntryString("ForgotPasswordSMSMessage", "Please reset your migme password using this code:%s. Ignore this message if you didn't request a password reset.")),
        EMAIL_CONTENT(new SystemPropertyEntryString("EmailContent", "Hi %1,\n\nYou recently asked to reset your migme password. To complete your request, please follow this link: %2\n\nIf you did not request a new password, please ignore this email.\n\nThanks,\n\nThe migme Team")),
        EMAIL_SUBJECT(new SystemPropertyEntryString("EmailSubject", "Reset your migme Password")),
        RESPONSE_RATE_LIMIT(new SystemPropertyEntryString("ResponseRateLimit", "10/1M")),
        CHANGE_PASSWORD_URL(new SystemPropertyEntryString("ChangePasswordUrl", "https://login.mig33.com/recoverpassword/%1/%2")),
        MIGME_CHANGE_PASSWORD_URL(new SystemPropertyEntryString("MigmeChangePasswordUrl", "https://login.mig.me/recoverpassword/%1/%2")),
        THROW_CAPTCHA_ON_CHANGE_PASSWORD_RATE_LIMIT_HIT(new SystemPropertyEntryBoolean("ThrowCaptchaOnChangePasswordRateLimitHit", false)),
        TOKEN_EXPIRATION_IN_MILLIS(new SystemPropertyEntryLong("TokenExpirationInMillis", 69120000L));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private ForgotPassword(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "ForgotPassword";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum FacebookConnect implements SystemPropertyEntryInterface
    {
        ENABLED(new SystemPropertyEntryBoolean("Enabled", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private FacebookConnect(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "FacebookConnect";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Payments_MIMOPAY_ASYNCH_UPDATE implements SystemPropertyEntryInterface
    {
        ENABLED(new SystemPropertyEntryBoolean("Enabled", true)),
        SLEEP_MILLIS_AFTER_ONE_RECORD(new SystemPropertyEntryInteger("SleepMillisAfterOneRecord", 5)),
        SLEEP_MILLIS_AFTER_ONE_BATCH(new SystemPropertyEntryInteger("SleepMillisAfterOneBatch", 15000)),
        MAX_REC_COUNT_PER_FETCH(new SystemPropertyEntryInteger("MaxRecCountPerFetch", 1000)),
        MAX_PENDING_LIFE_SPAN_SEC(new SystemPropertyEntryLong("MaxPendingLifeSpanSec", 604800L));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Payments_MIMOPAY_ASYNCH_UPDATE(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = SystemPropertyEntities.getNameWithNamespace(Payments_MIMOPAY.NS, "AsynchUpdate");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Payments_PAYPAL_ASYNCH_UPDATE implements SystemPropertyEntryInterface
    {
        ENABLED(new SystemPropertyEntryBoolean("Enabled", true)),
        SLEEP_MILLIS_AFTER_ONE_RECORD(new SystemPropertyEntryInteger("SleepMillisAfterOneRecord", 5)),
        SLEEP_MILLIS_AFTER_ONE_BATCH(new SystemPropertyEntryInteger("SleepMillisAfterOneBatch", 15000)),
        MAX_REC_COUNT_PER_FETCH(new SystemPropertyEntryInteger("MaxRecCountPerFetch", 1000)),
        MAX_PENDING_LIFE_SPAN_SEC(new SystemPropertyEntryLong("MaxPendingLifeSpanSec", 1200L));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Payments_PAYPAL_ASYNCH_UPDATE(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = SystemPropertyEntities.getNameWithNamespace(Payments_PAYPAL.NS, "AsynchUpdate");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Payments_PAYPAL_RateLimit implements SystemPropertyEntryInterface
    {
        ENABLED(new SystemPropertyEntryBoolean("Enabled", false)),
        NAMESPACE(new SystemPropertyEntryLong("NameSpace", 1L)),
        TOTAL_AMOUNT_USD_MIG33(new SystemPropertyEntryDouble("TotalAmountUsdUser", 100.0)),
        TOTAL_AMOUNT_USD_MIG33_MERCHANT(new SystemPropertyEntryDouble("TotalAmountUsdMerchant", 300.0)),
        TOTAL_AMOUNT_USD_MIG33_TOP_MERCHANT(new SystemPropertyEntryDouble("TotalAmountUsdTopMerchant", 5000.0)),
        TOTAL_AMOUNT_USD_INTERVAL_SECS(new SystemPropertyEntryInteger("TotalAmountUsdIntervalSeconds", 86400)),
        TOTAL_AMOUNT_USD_CACHE_EXPIRY_SECS(new SystemPropertyEntryInteger("TotalAmountUsdCacheExpirySecs", 900)),
        TOTAL_AMOUNT_ALL_TRANSACTIONS_USD_MIG33(new SystemPropertyEntryDouble("TotalAmountAllTransactionsUsdUser", 1000.0)),
        TOTAL_AMOUNT_ALL_TRANSACTIONS_USD_MIG33_MERCHANT(new SystemPropertyEntryDouble("TotalAmountAllTransactionsUsdMerchant", 3000.0)),
        TOTAL_AMOUNT_ALL_TRANSACTIONS_USD_MIG33_TOP_MERCHANT(new SystemPropertyEntryDouble("TotalAmountAllTransactionsUsdTopMerchant", 20000.0)),
        TOTAL_AMOUNT_ALL_TRANSACTIONS_USD_INTERVAL_SECS(new SystemPropertyEntryInteger("TotalAmountAllTransactionsUsdIntervalSeconds", 86400)),
        TOTAL_AMOUNT_ALL_TRANSACTIONS_USD_CACHE_EXPIRY_SECS(new SystemPropertyEntryInteger("TotalAmountAllTransactionsUsdCacheExpirySecs", 900)),
        TOTAL_AMOUNT_COUNTRY_TRANSACTIONS_USD_MIG33(new SystemPropertyEntryDouble("TotalAmountCountryTransactionsUsdUser", 500.0)),
        TOTAL_AMOUNT_COUNTRY_TRANSACTIONS_USD_MIG33_MERCHANT(new SystemPropertyEntryDouble("TotalAmountCountryTransactionsUsdMerchant", 1000.0)),
        TOTAL_AMOUNT_COUNTRY_TRANSACTIONS_USD_MIG33_TOP_MERCHANT(new SystemPropertyEntryDouble("TotalAmountCountryTransactionsUsdTopMerchant", 10000.0)),
        TOTAL_AMOUNT_COUNTRY_TRANSACTIONS_USD_INTERVAL_SECS(new SystemPropertyEntryInteger("TotalAmountCountryTransactionsUsdIntervalSeconds", 86400)),
        TOTAL_AMOUNT_COUNTRY_TRANSACTIONS_USD_CACHE_EXPIRY_SECS(new SystemPropertyEntryInteger("TotalAmountCountryTransactionsUsdCacheExpirySecs", 900)),
        NUMBER_TRANSACTIONS_MIG33(new SystemPropertyEntryInteger("NumberTransactionsUser", 5)),
        NUMBER_TRANSACTIONS_MIG33_MERCHANT(new SystemPropertyEntryInteger("NumberTransactionsMerchant", 5)),
        NUMBER_TRANSACTIONS_MIG33_TOP_MERCHANT(new SystemPropertyEntryInteger("NumberTransactionsTopMerchant", 10)),
        NUMBER_TRANSACTIONS_INTERVAL_SECS(new SystemPropertyEntryInteger("NumberTransactionsIntervalSeconds", 86400000)),
        NUMBER_TRANSACTIONS_CACHE_EXPIRY_SECS(new SystemPropertyEntryInteger("NumberTransactionsCacheExpirySeconds", 900)),
        NUMBER_ALL_TRANSACTIONS_MIG33(new SystemPropertyEntryInteger("NumberAllTransactionsUser", 500)),
        NUMBER_ALL_TRANSACTIONS_MIG33_MERCHANT(new SystemPropertyEntryInteger("NumberAllTransactionsMerchant", 500)),
        NUMBER_ALL_TRANSACTIONS_MIG33_TOP_MERCHANT(new SystemPropertyEntryInteger("NumberAllTransactionsTopMerchant", 1000)),
        NUMBER_ALL_TRANSACTIONS_INTERVAL_SECS(new SystemPropertyEntryInteger("NumberAllTransactionsIntervalSeconds", 86400000)),
        NUMBER_ALL_TRANSACTIONS_CACHE_EXPIRY_SECS(new SystemPropertyEntryInteger("NumberAllTransactionsExpirySecs", 900)),
        NUMBER_PAYPAL_ACCOUNT_TRANSACTIONS_MIG33(new SystemPropertyEntryInteger("NumberPaypalAccountTransactionsUser", 10)),
        NUMBER_PAYPAL_ACCOUNT_TRANSACTIONS_MIG33_MERCHANT(new SystemPropertyEntryInteger("NumberPaypalAccountTransactionsMerchant", 10)),
        NUMBER_PAYPAL_ACCOUNT_TRANSACTIONS_MIG33_TOP_MERCHANT(new SystemPropertyEntryInteger("NumberPaypalAccountTransactionsTopMerchant", 10)),
        NUMBER_PAYPAL_ACCOUNT_TRANSACTIONS_INTERVAL_SECS(new SystemPropertyEntryInteger("NumberPaypalAccountTransactionsIntervalSeconds", 86400)),
        NUMBER_PAYPAL_ACCOUNT_TRANSACTIONS_CACHE_EXPIRY_SECS(new SystemPropertyEntryInteger("NumberPaypalAccountTransactionsExpirySecs", 900)),
        NUMBER_COUNTRY_TRANSACTIONS_MIG33(new SystemPropertyEntryInteger("NumberCountryTransactionsUser", 50)),
        NUMBER_COUNTRY_TRANSACTIONS_MIG33_MERCHANT(new SystemPropertyEntryInteger("NumberCountryTransactionsMerchant", 50)),
        NUMBER_COUNTRY_TRANSACTIONS_MIG33_TOP_MERCHANT(new SystemPropertyEntryInteger("NumberCountryTransactionsTopMerchant", 100)),
        NUMBER_COUNTRY_TRANSACTIONS_INTERVAL_SECS(new SystemPropertyEntryInteger("NumberCountryTransactionsIntervalSeconds", 86400)),
        NUMBER_COUNTRY_TRANSACTIONS_CACHE_EXPIRY_SECS(new SystemPropertyEntryInteger("NumberCountryTransactionsCacheExpirySecs", 900)),
        STATS_QUERY_THRESHOLD_SECS(new SystemPropertyEntryInteger("StatsQueryThresholdSecs", 2592000)),
        INITIATE_PAYMENT_ACCESS_RATELIMIT(new SystemPropertyEntryString("InitiatePaymentRateLimit", "1/1S;10/1M"));

        private SystemPropertyEntry<?> value;
        public static final String NS;

        private Payments_PAYPAL_RateLimit(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = SystemPropertyEntities.getNameWithNamespace(Payments_PAYPAL.NS, "RateLimit");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Payments_PAYPAL implements SystemPropertyEntryInterface
    {
        SUPPORTED_CURRENCIES(new SystemPropertyEntryStringArray("SupportedCurrencies", new String[]{"AUD", "CAD", "EUR", "GBP", "JPY", "USD", "NZD", "CHF", "HKD", "SGD", "SEK", "DKK", "PLN", "NOK", "HUF", "CZK", "ILS", "MXN", "BRL", "MYR", "PHP", "TWD", "THB", "TRY"})),
        NON_TOP_MERCHANTS_ACCESS_ONLY_ENABLED(new SystemPropertyEntryBoolean("NonTopMerchantsAccessOnlyEnabled", false)),
        VENDOR_BASE_URL(new SystemPropertyEntryString("VendorBaseURL", "")),
        SET_EXPRESS_CHECKOUT_URL(new SystemPropertyEntryString("SetExpressCheckoutUrl", "")),
        API_VERSION(new SystemPropertyEntryInteger("APIVersion", 95)),
        USERNAME(new SystemPropertyEntryString("Username", "")),
        PASSWORD(new SystemPropertyEntryString("Password", "")),
        SIGNATURE(new SystemPropertyEntryString("Signature", "")),
        CANCEL_URL(new SystemPropertyEntryString("CancelUrl", "")),
        RETURN_URL(new SystemPropertyEntryString("ReturnUrl", "")),
        KEEP_ALIVE_IN_SEC(new SystemPropertyEntryInteger("KeepAliveInSec", 60)),
        MAX_CONNECTIONS(new SystemPropertyEntryInteger("MaxConnections", 200)),
        STRICT_HTTPS_CERT_CHECK(new SystemPropertyEntryBoolean("StrictHttpsCertCheck", true)),
        ENABLE_GATEWAY_REQUEST_RESPONSE_LOGGING(new SystemPropertyEntryBoolean("EnableGatewayRequestReponseLogging", true)),
        TIME_OUT_IN_MILLIS(new SystemPropertyEntryInteger("TimeOutInMillis", 30000)),
        ENABLED_TO_ALL_USERS(new SystemPropertyEntryBoolean("EnabledToAllUsers", false)),
        EXPRESS_CHECKOUT_SESSION_TIMEOUT_MILLIS(new SystemPropertyEntryInteger("ExpressCheckoutSessionTimeoutMillis", 300000)),
        BLACKLIST_USERNAME(new SystemPropertyEntryStringArray("BlacklistUsername", new String[0])),
        BLACKLIST_IP_ADDRESS(new SystemPropertyEntryStringArray("BlacklistIpAddress", new String[0])),
        BLACKLIST_PAYPAL_ACCOUNT(new SystemPropertyEntryStringArray("BlasklistPaypalAccount", new String[0])),
        BLACKLIST_COUNTRY(new SystemPropertyEntryIntegerArray("BlasklistCountry", new int[0])),
        WHITELIST_USERNAME(new SystemPropertyEntryStringArray("WhitelistUsername", new String[0])),
        MIN_AMOUNT_USD_MIG33(new SystemPropertyEntryDouble("MinAmountUSDUser", 0.5)),
        MIN_AMOUNT_USD_MIG33_MERCHANT(new SystemPropertyEntryDouble("MinAmountUSDMerchant", 0.5)),
        MIN_AMOUNT_USD_MIG33_TOP_MERCHANT(new SystemPropertyEntryDouble("MinAmountUSDTopMerchant", 0.5)),
        MAX_AMOUNT_USD_MIG33(new SystemPropertyEntryDouble("MaxAmountUSDUser", 20.0)),
        MAX_AMOUNT_USD_MIG33_MERCHANT(new SystemPropertyEntryDouble("MaxAmountUSDMerchant", 100.0)),
        MAX_AMOUNT_USD_MIG33_TOP_MERCHANT(new SystemPropertyEntryDouble("MaxAmountUSDTopMerchant", 1000.0)),
        DEFAULT_CURRENCY(new SystemPropertyEntryString("DefaultCurrency", "USD")),
        CLIENT_DEFINED_RETURN_URLS_ENABLED(new SystemPropertyEntryBoolean("ClientDefinedReturnUrlsEnabled", false));

        private SystemPropertyEntry<?> value;
        public static final String NS;

        private Payments_PAYPAL(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = SystemPropertyEntities.getNameWithNamespace("Payments", "PAYPAL");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Payments_MIMOPAY implements SystemPropertyEntryInterface
    {
        GATEWAY_USERNAME(new SystemPropertyEntryString("GatewayUsername", "")),
        GATEWAY_PASSWORD(new SystemPropertyEntryString("GatewayPassword", "")),
        GATEWAY_SHARED_SECRET(new SystemPropertyEntryString("GatewaySharedSecret", "")),
        MERCHANT_CODE(new SystemPropertyEntryString("MerchantCode", "")),
        GAME_CODE(new SystemPropertyEntryString("GameCode", "")),
        CREDIT_RELOAD_PTYPE(new SystemPropertyEntryString("CreditReloadPType", "")),
        VENDOR_BASE_URL(new SystemPropertyEntryString("VendorBaseURL", "")),
        CREDIT_RELOAD_PATH(new SystemPropertyEntryString("CreditReloadPath", "")),
        QUERY_TRANSACTION_STATUS_PATH(new SystemPropertyEntryString("QueryTransactionStatusPath", "")),
        RELOAD_VALUE_CURRENY(new SystemPropertyEntryString("ReloadValueCurrency", "IDR")),
        SUPPORTED_CURRENCIES(new SystemPropertyEntryStringArray("SupportedCurrencies", new String[]{"IDR"})),
        DEFAULT_CURRENCY(new SystemPropertyEntryString("DefaultCurrency", "USD")),
        NON_TOP_MERCHANTS_ACCESS_ONLY_ENABLED(new SystemPropertyEntryBoolean("NonTopMerchantsAccessOnlyEnabled", true)),
        KEEP_ALIVE_IN_SEC(new SystemPropertyEntryInteger("KeepAliveInSec", 60)),
        MAX_CONNECTIONS(new SystemPropertyEntryInteger("MaxConnections", 200)),
        STRICT_HTTPS_CERT_CHECK(new SystemPropertyEntryBoolean("StrictHttpsCertCheck", true)),
        TIME_OUT_IN_MILLIS(new SystemPropertyEntryInteger("TimeOutInMillis", 30000)),
        MAXIMUM_PENDING_PAYMENTS_COUNT(new SystemPropertyEntryInteger("MaximumPendingPaymentsCount", 0)),
        ENABLE_RELOAD_KEY_VALIDATION(new SystemPropertyEntryBoolean("EnableReloadKeyValidation", true)),
        EXPECTED_RELOAD_KEY_LENGTH(new SystemPropertyEntryInteger("ExpectedReloadKeyLength", 16)),
        ENABLED_TO_ALL_USERS(new SystemPropertyEntryBoolean("EnabledToAllUsers", false)),
        ENABLE_GATEWAY_REQUEST_RESPONSE_LOGGING(new SystemPropertyEntryBoolean("EnableGatewayRequestResponseLogging", true)),
        RELOAD_KEY_RECYCLE_PERIOD_DAYS(new SystemPropertyEntryInteger("ReloadKeyRecyclePeriodDays", 730)),
        ONLY_FOR_INDONESIAN_USERS(new SystemPropertyEntryBoolean("OnlyForIndonesianUsers", true));

        private SystemPropertyEntry<?> value;
        public static final String NS;

        private Payments_MIMOPAY(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = SystemPropertyEntities.getNameWithNamespace("Payments", "MIMOPAY");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class MessageStatusEventSettings
    implements SystemPropertyEntryInterface {
        public static final MessageStatusEventSettings MESSAGE_STATUS_EVENTS_ENABLED = new MessageStatusEventSettings(new SystemPropertyEntryBoolean("Enabled", false));
        public static final MessageStatusEventSettings RATE_LIMITING_ENABLED = new MessageStatusEventSettings(new SystemPropertyEntryBoolean("RateLimitingEnabled", true));
        public static final MessageStatusEventSettings MIN_GUID_CHARS = new MessageStatusEventSettings(new SystemPropertyEntryInteger("MinGUIDChars", 4));
        public static final MessageStatusEventSettings MAX_GUID_CHARS = new MessageStatusEventSettings(new SystemPropertyEntryInteger("MaxGUIDChars", 128));
        public static final MessageStatusEventSettings OTHER_CLIENTS_RX_ENABLED = new MessageStatusEventSettings(new SystemPropertyEntryBoolean("ReceiveFromOtherClientsEnabled", false));
        public static final MessageStatusEventSettings OTHER_CLIENTS_TX_ENABLED = new MessageStatusEventSettings(new SystemPropertyEntryBoolean("SendToOtherClientsEnabled", false));
        public static final MessageStatusEventSettings MAX_GET_MESSAGE_STATUS_EVENTS_REQUESTS_PER_MINUTE = new MessageStatusEventSettings(new SystemPropertyEntryInteger("MaxGetMessageStatusEventsRequestsPerMinute", 10000));
        private static final MinVersionLookup rxFromLookup = new MinVersionLookup("ReceiveFrom");
        private static final MinVersionLookup sendToLookup = new MinVersionLookup("SendTo");
        private SystemPropertyEntry<?> value;
        private static String NS = "MsgStatusEvent";

        public static MessageStatusEventSettings getReceiveFromMinVersionProp(ClientType device) {
            return rxFromLookup.getProp(device);
        }

        public static MessageStatusEventSettings getSendToMinVersionProp(ClientType device) {
            return sendToLookup.getProp(device);
        }

        public static Short getReceiveFromMinVersion(ClientType device) {
            return (Short)((Map)rxFromLookup.getValue()).get((Object)device);
        }

        public static Short getSendToMinVersion(ClientType device) {
            return (Short)((Map)sendToLookup.getValue()).get((Object)device);
        }

        MessageStatusEventSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static class MinVersionLookup
        extends LazyLoader<Map<ClientType, Short>> {
            private final String propPrefix;
            private final Map<ClientType, Short> map = new HashMap<ClientType, Short>();

            public MinVersionLookup(String propPrefix) {
                super(propPrefix + "_LOOKUP", 60000L);
                this.propPrefix = propPrefix;
            }

            public MessageStatusEventSettings getProp(ClientType device) {
                String propName = this.propPrefix + device.toString() + "MinVersion";
                return new MessageStatusEventSettings(new SystemPropertyEntryInteger(propName, Integer.MAX_VALUE));
            }

            @Override
            protected Map<ClientType, Short> fetchValue() throws Exception {
                this.map.clear();
                for (ClientType device : ClientType.values()) {
                    int minVersion = SystemProperty.getInt(this.getProp(device));
                    if (minVersion == Integer.MAX_VALUE) continue;
                    this.map.put(device, (short)minVersion);
                }
                return this.map;
            }
        }

        public static class Cache {
            public static LazyLoader<Boolean> enabled = new LazyLoader<Boolean>("MESSAGE_STATUS_EVENTS_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(MESSAGE_STATUS_EVENTS_ENABLED);
                }
            };
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Payments implements SystemPropertyEntryInterface,
    EnumUtils.IEnumValueGetter<String>
    {
        MIG33_AJAX_ENABLED_VENDORS(new SystemPropertyEntryStringArray("MIG33_AJAX_EnabledVendors", new String[0])),
        WAP_ENABLED_VENDORS(new SystemPropertyEntryStringArray("WAP_EnabledVendors", new String[0])),
        MIDLET_ENABLED_VENDORS(new SystemPropertyEntryStringArray("MIDLET_EnabledVendors", new String[0])),
        MTK_MRE_ENABLED_VENDORS(new SystemPropertyEntryStringArray("MTK_MRE_EnabledVendors", new String[0])),
        TOUCH_ENABLED_VENDORS(new SystemPropertyEntryStringArray("TOUCH_EnabledVendors", new String[0])),
        BLACKBERRY_ENABLED_VENDORS(new SystemPropertyEntryStringArray("BLACKBERRY_EnabledVendors", new String[0])),
        IOS_ENABLED_VENDORS(new SystemPropertyEntryStringArray("IOS_EnabledVendors", new String[0])),
        MIG33_CORPORATE_ENABLED_VENDORS(new SystemPropertyEntryStringArray("MIG33_CORPORATE_EnabledVendors", new String[0])),
        MIN_AMOUNT(new SystemPropertyEntryDouble("MinAmount", 0.0)),
        BONUS_ENABLED(new SystemPropertyEntryBoolean("StrictAmountCheckEnabled", false)),
        DEFAULT_CURRENCY(new SystemPropertyEntryString("DefaultCurrency", "USD")),
        DEFAULT_RATE_LIMIT_CURRENCY(new SystemPropertyEntryString("DefaultRateLimitCurrency", "USD")),
        THIRD_PARTY_PAYMENT_ENABLED(new SystemPropertyEntryBoolean("ThirdPartyPaymentEnabled", true));

        private SystemPropertyEntry<?> value;
        public static final String NS = "Payments";
        private static HashMap<String, Payments> lookupMap;

        private Payments(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        public String getEnumValue() {
            return this.getName().trim().toUpperCase();
        }

        public static Payments getSystemPropertyKeyForEnabledVendors(String viewTypeName) {
            return lookupMap.get(SystemPropertyEntities.getNameWithNamespace(NS, viewTypeName.trim() + "_EnabledVendors").toUpperCase());
        }

        static {
            lookupMap = new HashMap();
            EnumUtils.populateLookUpMap(lookupMap, Payments.class);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Payments_MOL implements SystemPropertyEntryInterface
    {
        MERCHANT_ID(new SystemPropertyEntryString("MerchantID", "201210090174")),
        MERCHANT_PIN(new SystemPropertyEntryString("MerchantPIN", "")),
        VENDOR_BASE_URL(new SystemPropertyEntryString("VendorBaseURL", "")),
        HEARTBEAT_PATH(new SystemPropertyEntryString("HeartbeatPath", "")),
        PURCHASE_PATH(new SystemPropertyEntryString("PurchasePath", "")),
        MIN_AMOUNT(new SystemPropertyEntryDouble("MinAmount", 0.0)),
        QUERY_TRANSACTION_STATUS_PATH(new SystemPropertyEntryString("QueryTransactionStatusPath", "")),
        MAX_AMOUNT(new SystemPropertyEntryDouble("MaxAmount", 1000.0)),
        HEARTBEAT_URL(new SystemPropertyEntryString("HeartbeatUrl", "/api/login/s_module/heartbeat.asmx/GetHeartBeat")),
        PURCHASE_URL(new SystemPropertyEntryString("PurchaseUrl", "/api/login/s_module/heartbeat.asmx/GetHeartBeat")),
        SUPPORTED_CURRENCIES(new SystemPropertyEntryStringArray("SupportedCurrencies", new String[]{"USD", "MYR", "SGD", "INR", "PHP", "IDR", "THB"})),
        DEFAULT_CURRENCY(new SystemPropertyEntryString("DefaultCurrency", "USD")),
        NON_TOP_MERCHANTS_ACCESS_ONLY_ENABLED(new SystemPropertyEntryBoolean("NonTopMerchantsAccessOnlyEnabled", true)),
        KEEP_ALIVE_IN_SEC(new SystemPropertyEntryInteger("KeepAliveInSec", 60)),
        MAX_CONNECTIONS(new SystemPropertyEntryInteger("MaxConnections", 200)),
        STRICT_HTTPS_CERT_CHECK(new SystemPropertyEntryBoolean("StrictHttpsCertCheck", true)),
        TIME_OUT_IN_MILLIS(new SystemPropertyEntryInteger("TimeOutInMillis", 30000)),
        MAXIMUM_PENDING_PAYMENTS_COUNT(new SystemPropertyEntryInteger("MaximumPendingPaymentsCount", 0)),
        ENABLED_TO_ALL_USERS(new SystemPropertyEntryBoolean("EnabledToAllUsers", false)),
        ENABLE_STRICT_AMOUNT_CHECK(new SystemPropertyEntryBoolean("EnableStrictAmountCheck", true));

        private SystemPropertyEntry<?> value;
        public static final String NS;

        private Payments_MOL(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = SystemPropertyEntities.getNameWithNamespace("Payments", "MOL");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Payments_CREDITCARD implements SystemPropertyEntryInterface
    {
        HML_ENABLED(new SystemPropertyEntryBoolean("Enabled", true)),
        HML_GUARDSET_ENABLED(new SystemPropertyEntryBoolean("HMLGuardSetEnabled", true)),
        HML_CALLBACK_URL(new SystemPropertyEntryString("HMLCallBackUrl", "")),
        DEFAULT_CURRENCY(new SystemPropertyEntryString("DefaultCurrency", "USD")),
        NON_HML_CC_PAYMENT_ENABLED(new SystemPropertyEntryBoolean("NonHmlCcPaymentEnabled", false));

        private SystemPropertyEntry<?> value;
        public static final String NS;

        private Payments_CREDITCARD(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = SystemPropertyEntities.getNameWithNamespace("Payments", "CREDITCARD");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Payments_MOL_ASYNCH_UPDATE implements SystemPropertyEntryInterface
    {
        ENABLED(new SystemPropertyEntryBoolean("Enabled", true)),
        SLEEP_MILLIS_AFTER_ONE_RECORD(new SystemPropertyEntryInteger("SleepMillisAfterOneRecord", 5)),
        SLEEP_MILLIS_AFTER_ONE_BATCH(new SystemPropertyEntryInteger("SleepMillisAfterOneBatch", 15000)),
        MAX_REC_COUNT_PER_FETCH(new SystemPropertyEntryInteger("MaxRecCountPerFetch", 1000)),
        MAX_PENDING_LIFE_SPAN_SEC(new SystemPropertyEntryLong("MaxPendingLifeSpanSec", 1800L));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Payments_MOL_ASYNCH_UPDATE(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = SystemPropertyEntities.getNameWithNamespace(Payments_MOL.NS, "AsynchUpdate");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum OfflineMessageSettings implements SystemPropertyEntryInterface
    {
        OLD_STYLE_OLM_STORAGE_ENABLED(new SystemPropertyEntryBoolean("OldStyleOLMStorageEnabled", false)),
        OFFLINE_V2_EMOTES_ENABLED(new SystemPropertyEntryBoolean("OfflineV2EmotesEnabled", false)),
        SEND_ENABLED(new SystemPropertyEntryBoolean("SendEnabled", false)),
        PUSH_TO_MIGBO_ENABLED(new SystemPropertyEntryBoolean("PushToMigboEnabled", false)),
        CHECK_YOUR_MIGBO_MSGS_ALERT_ENABLED(new SystemPropertyEntryBoolean("CheckYourMigboMessagesAlertEnabled", false)),
        STATS_COLLECTION_ENABLED(new SystemPropertyEntryBoolean("StatsCollectionEnabled", false)),
        SENDING_TO_NON_FRIENDS_ENABLED(new SystemPropertyEntryBoolean("SendingToNonFriendsEnabled", false)),
        REFRESH_THREAD_POOL_PROPERTIES_ENABLED(new SystemPropertyEntryBoolean("RefreshThreadPoolPropertiesEnabled", false)),
        OFFLINE_MESSAGE_GUARDSET_ENABLED(new SystemPropertyEntryBoolean("OfflineMessageGuardsetEnabled", true)),
        MSG_STORED_CONF_ENABLED(new SystemPropertyEntryBoolean("MessageStoredConfirmationEnabled", false)),
        RATE_LIMITING_ENABLED(new SystemPropertyEntryBoolean("RateLimitingEnabled", false)),
        CHECK_FOR_DEACTIVATED_RECIPIENT_ENABLED(new SystemPropertyEntryBoolean("CheckForDeactivatedRecipientEnabled", false)),
        MIN_MIG_LEVEL_FOR_SENDING(new SystemPropertyEntryInteger("MinMigLevelForSending", 0)),
        MIN_MIG_LEVEL_FOR_RECEIVING(new SystemPropertyEntryInteger("MinMigLevelForReceiving", 0)),
        RX_LIMIT_ANDROID_3PLUS(new SystemPropertyEntryInteger("ReceiveLimitAndroid3Plus", 30)),
        RX_LIMIT_BLACKBERRY(new SystemPropertyEntryInteger("ReceiveLimitBlackberry", 50)),
        RX_LIMIT_J2ME(new SystemPropertyEntryInteger("ReceiveLimitJ2ME", 10)),
        RX_LIMIT_MRE(new SystemPropertyEntryInteger("ReceiveLimitMRE", 5)),
        RX_LIMIT_DEFAULT(new SystemPropertyEntryInteger("ReceiveLimitDefault", 10)),
        EXPIRY_DAYS(new SystemPropertyEntryInteger("ExpiryDays", 30)),
        TPOOL_CORE_SIZE(new SystemPropertyEntryInteger("ThreadPoolCoreSize", 100)),
        TPOOL_MAX_SIZE(new SystemPropertyEntryInteger("ThreadPoolMaxSize", 200)),
        TPOOL_KEEP_ALIVE_SECONDS(new SystemPropertyEntryInteger("ThreadPoolKeepAliveSeconds", 0)),
        TPOOL_TASK_QUEUE_MAX_SIZE(new SystemPropertyEntryInteger("ThreadPoolTaskQueueMaxSize", 5000)),
        TPOOL_PROPS_REFRESH_INTERVAL_SECS(new SystemPropertyEntryInteger("ThreadPoolPropsRefreshIntervalSeconds", 300)),
        STORAGE_TIMEOUT_MILLIS(new SystemPropertyEntryInteger("StorageTimeoutMillis", 10000)),
        TX_DAILY_LIMIT_PER_USER(new SystemPropertyEntryInteger("SendDailyLimit", 2000)),
        TX_DAILY_LIMIT_PER_USER_PAIR(new SystemPropertyEntryInteger("SendDailyLimitPerUserPair", 200)),
        RX_DAILY_LIMIT_PER_USER(new SystemPropertyEntryInteger("ReceiveDailyLimit", 2000)),
        TX_DAILY_LIMIT_PER_SUSPECTED_ABUSER(new SystemPropertyEntryInteger("SendDailyLimitPerSuspectedAbuser", 5)),
        THREAD_LENGTH_LIMIT(new SystemPropertyEntryInteger("ThreadLengthLimit", 50)),
        STATS_COLLECTION_INTERVAL_MINUTES(new SystemPropertyEntryInteger("StatsCollectionIntervalMinutes", 5));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private OfflineMessageSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "OfflineMsg";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MerchantTagSettings implements SystemPropertyEntryInterface
    {
        GET_MERCHANT_TAG_DETAILS_ENABLED(new SystemPropertyEntryBoolean("GetMerchantTagDetailsEnabled", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private MerchantTagSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "MerchantTagSettings";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum UserProfileSettings implements SystemPropertyEntryInterface
    {
        GET_ADMIN_LABELS_VERSION_1(new SystemPropertyEntryBoolean("GetAdminLabelsVersion1", false)),
        GET_USER_MERCHANT_LABELS_ENABLED(new SystemPropertyEntryBoolean("GetUserMerchantLabelsEnabled", false)),
        GET_USER_LABELS_ENABLED(new SystemPropertyEntryBoolean("GetUserLabelsEnabled", false)),
        DO_SYNCHRONOUS_PROFILE_CACHE_INVALIDATION_ON_MIGBO_UPON_PROFILE_SETTING_UPDATE(new SystemPropertyEntryBoolean("DoSyncMigboProfileCacheInvalidationUponProfileSettingUpdate", false)),
        FAIL_PROFILE_SETTING_UPDATE_IF_FAIL_DO_SYNCHRONOUS_PROFILE_CACHE_INVALIDATION_ON_MIGBO(new SystemPropertyEntryBoolean("FailProfileSettingUpdateIfFailDoSyncMigboProfileCacheInvalidation", false)),
        DO_SYNCHRONOUS_PRIVACY_CACHE_INVALIDATION_ON_MIGBO_UPON_COMM_SETTING_UPDATE(new SystemPropertyEntryBoolean("DoSyncMigboPrivacyCacheInvalidationUponCommSettingUpdate", false)),
        FAIL_COMM_SETTING_UPDATE_IF_FAIL_DO_SYNCHRONOUS_PRIVACY_CACHE_INVALIDATION_ON_MIGBO(new SystemPropertyEntryBoolean("FailCommSettingUpdateIfFailDoSyncMigboPrivacyCacheInvalidation", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private UserProfileSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "UserProfileSettings";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ObjectCacheLoadBalancing implements SystemPropertyEntryInterface
    {
        FILTER_MASK(new SystemPropertyEntryInteger("FilterMask", 1)),
        GC_MULTIPLIER(new SystemPropertyEntryDouble("GCMultiplier", 1.0)),
        CHATROOM_MULTIPLIER(new SystemPropertyEntryDouble("ChatRoomMultiplier", 1.0)),
        USER_OBJECT_MULTIPLIER(new SystemPropertyEntryDouble("UserObjectMultiplier", 1.0)),
        SESSION_OBJECT_MULTIPLIER(new SystemPropertyEntryDouble("SessionObjectMultiplier", 1.0)),
        MEMORY_UTILIZATION_MULTIPLIER(new SystemPropertyEntryDouble("MemoryUtilizationMultiplier", 100.0)),
        OLDEST_ADMISSIBLE_STATS_SECONDS(new SystemPropertyEntryInteger("OldestAdmissibleStatsSeconds", 300));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private ObjectCacheLoadBalancing(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "ObjectCacheLoadBalancing";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum AccountTransaction implements SystemPropertyEntryInterface
    {
        AUTO_APPROVE_CREDIT_CARD_PAYMENT_ENABLED(new SystemPropertyEntryBoolean("CreditCardAutoApproveEnabled", false)),
        MAX_NEW_USER_CC_PURCHASE_USD(new SystemPropertyEntryDouble("MaxNewUserPurchaseUsd", 20.0)),
        NEW_USER_THRESHOLD_HOURS(new SystemPropertyEntryInteger("NewUserThresholdHours", 48)),
        GLOBAL_COLLECT_URL(new SystemPropertyEntryString("GlobalCollectURL", "")),
        AUTO_APPROVE_CREDIT_CARD_RISKY_COUNTRIES(new SystemPropertyEntryIntegerArray("AutoApproveCreditCardRiskyCountries", new int[0])),
        AUTO_CREDIT_CARD_RECHARGE_BONUS_ENABLED(new SystemPropertyEntryBoolean("AutoCreditCardRechargeBonusEnabled", false)),
        CREDIT_CARD_RECHARGE_BONUS_ENABLED(new SystemPropertyEntryBoolean("CreditCardRechargeBonusEnabled", false)),
        CREDIT_CARD_PAYMENT_STRICT_SSL_CHECK(new SystemPropertyEntryBoolean("CreditCardPaymentStrictSslCheck", true)),
        CREDIT_CARD_PAYMENT_STRICT_CC_COUNTRY_CHECK(new SystemPropertyEntryBoolean("CreditCardPaymentStrictCcCountryCheck", true)),
        CREDIT_CARD_MAXIMUM_AUTO_APPROVE_AMOUNT_USD(new SystemPropertyEntryDouble("CreditCardMaximumAutoApproveAmountUsd", 50.0)),
        CREDIT_CARD_FIRST_NAME_LAST_NAME_CHECK_ENABLED(new SystemPropertyEntryBoolean("CreditCardFirstNameLastNameCheckEnabled", false)),
        TRANSACTION_TYPES_FOR_MIN_BALANCE_CALC(new SystemPropertyEntryIntegerArray("TransactionTypesForMinBalanceCalc", new int[]{AccountEntryData.TypeEnum.MARKETING_REWARD.value(), AccountEntryData.TypeEnum.REFERRAL_CREDIT.value(), AccountEntryData.TypeEnum.ACTIVATION_CREDIT.value(), AccountEntryData.TypeEnum.SMS_CHARGE.value(), AccountEntryData.TypeEnum.CALL_CHARGE.value(), AccountEntryData.TypeEnum.SUBSCRIPTION.value(), AccountEntryData.TypeEnum.CHATROOM_KICK_CHARGE.value(), AccountEntryData.TypeEnum.CREDIT_EXPIRED.value(), AccountEntryData.TypeEnum.EMOTICON_PURCHASE.value(), AccountEntryData.TypeEnum.AVATAR_PURCHASE.value(), AccountEntryData.TypeEnum.GAME_ITEM_PURCHASE.value(), AccountEntryData.TypeEnum.GAME_REWARD.value(), AccountEntryData.TypeEnum.THIRD_PARTY_API_DEBIT.value(), AccountEntryData.TypeEnum.VIRTUAL_GIFT_PURCHASE.value(), AccountEntryData.TypeEnum.EMOTE_PURCHASE.value()})),
        MIN_BALANCE_MAX_LOOK_BACK_DAYS(new SystemPropertyEntryInteger("MinBalanceMaxLookBackDays", 90)),
        ENABLE_MIN_BALANCE_CALC_ON_TRANSFER(new SystemPropertyEntryBoolean("EnableMinBalanceCalcOnTransfer", false)),
        ENABLE_MIN_BALANCE_CALC_ON_POT_GAMES(new SystemPropertyEntryBoolean("EnableMinBalanceCalcOnPotGames", false)),
        USE_NESTED_QUERY_FOR_MERCHANT_TAG_RETRIEVAL(new SystemPropertyEntryBoolean("UseNestedQueryForMerchantTagRetrieval", false)),
        ENABLE_REVERSAL_TRANSFER_BIGINT_AE_ID(new SystemPropertyEntryBoolean("EnableReversalTransferBigIntAEID", true));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private AccountTransaction(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "AccountTransaction";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum AccountTransaction_CreditTransfer implements SystemPropertyEntryInterface
    {
        ENABLE_MIG_LEVEL_CHECK(new SystemPropertyEntryBoolean("EnableMigLevelCheck", false)),
        SKIP_FETCH_CACHED_REPUTATION_SCORE(new SystemPropertyEntryBoolean("SkipFetchCachedReputationScore", true)),
        MIN_MIG_LEVEL(new SystemPropertyEntryInteger("MinMigLevel", 10)),
        USER_TYPES_TO_APPLY_MIG_LEVEL_CHECK(new SystemPropertyEntryIntegerArray("UserTypesToApplyMigLevelCheck", new int[]{UserData.TypeEnum.MIG33.value()})),
        USERIDS_EXEMPTED_FROM_MIG_LEVEL_CHECK(new SystemPropertyEntryIntegerArray("UserIDSExemptedFromMigLevelChecks", new int[0])),
        ENABLE_MIG_LEVEL_CHECK_FOR_ACCOUNT_VERIFIED_USER(new SystemPropertyEntryBoolean("EnableMigLevelCheckForAccountVerified", false)),
        INSUFFICIENT_MIG_LEVEL_ERROR_MESSAGE(new SystemPropertyEntryString("InsufficientMigLevelErrorMessage", "Hi, using migCredits wisely will help you grow migLevel faster! You can transfer migCredits after reaching level 10.")),
        LOG_SENDERS_PASSING_TRANSFER_ENTITLEMENT_CHECKS(new SystemPropertyEntryBoolean("LogSendersPassingTransferEntitlementChecks", true));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private AccountTransaction_CreditTransfer(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "AccountTransaction:CreditTransfer";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum AccountEntry implements SystemPropertyEntryInterface
    {
        MAX_DESC_LENGTH(new SystemPropertyEntryInteger("MaxDescLength", 128)),
        BANNED_CREDIT_SENDERS(new SystemPropertyEntryStringArray("BannedCreditSenders", new String[]{"loadtest"}));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private AccountEntry(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "AccountEntry";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EJBCacheDuration implements SystemPropertyEntryInterface
    {
        MIS_CLIENT_TEXT("ClientText"),
        MIS_CURRENCIES("Currencies"),
        MIS_COUNTRIES("Countries"),
        MIS_MIDLET_MENU_ITEMS("MenuItems"),
        CONTENT_EMOTICONS("Emoticons"),
        CONTENT_EMOTICONPACKS("EmoticonPacks"),
        CONTENT_EMOTICONHOTKEYS("EmoticonHotkeys");

        private SystemPropertyEntry<?> value;
        private static String NS;

        private EJBCacheDuration(String entryName, Long duration) {
            this.value = new SystemPropertyEntryLong(entryName, duration);
        }

        private EJBCacheDuration(String entryName) {
            this(entryName, new Long(300L));
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "EJBCacheDuration";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Invitation implements SystemPropertyEntryInterface
    {
        REFERRAL_INVITATION_ENABLED(new SystemPropertyEntryBoolean("ReferralInvitationEnabled", false)),
        FACEBOOK_INVITATION_ENABLED(new SystemPropertyEntryBoolean("FacebookInvitationEnabled", false)),
        INTERNAL_INVITATION_ENABLED(new SystemPropertyEntryBoolean("InternalInvitationEnabled", false)),
        REFERRAL_INVITATION_CIPHER_PASSPHRASE(new SystemPropertyEntryString("ReferralInvitationCipherPassPhrase", "mig.33~2012$12#11")),
        REFERRAL_EXPIRE_PERIOD_IN_DAY(new SystemPropertyEntryInteger("ReferralExpirePeriodInDay", 30)),
        TRACKING_UNVERIFIED_SIGN_UP_ENABLED(new SystemPropertyEntryBoolean("TrackingUnverifiedSignUpEnabled", true)),
        MARKETING_MECHANICS_ENABLED(new SystemPropertyEntryBoolean("MarketingMechanicsEnabled", true)),
        MARKETING_MECHANICS_CHAIN_ENABLED(new SystemPropertyEntryBoolean("MarketingMechanicsChainEnabled", false)),
        MARKETING_MECHANICS_FOR_REFERRAL_ENABLED(new SystemPropertyEntryBoolean("MarketingMechanicsForReferralEnabled", true)),
        MARKETING_MECHANICS_FOR_ACCEPT_INVITATION_ENABLED(new SystemPropertyEntryBoolean("MarketingMechanicsForAcceptInvitationEnabled", false)),
        MARKETING_MECHANICS_FOR_LOGIN_USING_EXISTING_ACCOUNT_ENABLED(new SystemPropertyEntryBoolean("MarketingMechanicsForLoginUsingExistingAccountEnabled", false)),
        REFERRAL_RATE_LIMIT(new SystemPropertyEntryString("ReferralRateLimit", "10/1D")),
        FACEBOOK_REFERRAL_RATE_LIMIT(new SystemPropertyEntryString("FacebookReferralRateLimit", "50/1D")),
        INTERNAL_REFERRAL_RATE_LIMIT(new SystemPropertyEntryString("InternalReferralRateLimit", "50/1D")),
        GUARDSET_FOR_REFERRAL_ENABLED(new SystemPropertyEntryBoolean("GuardsetForReferralEnabled", true)),
        ENABLE_MUTUAL_FOLLOWING_FOR_ALL_INVITERS(new SystemPropertyEntryBoolean("EnableMutualFollowingForAllInviters", true)),
        ENABLE_LOG_INVITATION_RESPONSE_FOR_ALL_INVITERS_OF_COLLAPSE_ALERT(new SystemPropertyEntryBoolean("EnableLogInvitationResponseFprAllInvitersOfCollapseAlert", true)),
        ENABLE_GET_INVITER_FROM_MOBILE_REFERRER(new SystemPropertyEntryBoolean("EnableGetInviterFromMobileReferrer", true)),
        ENABLE_REFERRAL_ACK_EMAIL(new SystemPropertyEntryBoolean("EnableReferralAckEmail", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Invitation(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Invitation";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Email implements SystemPropertyEntryInterface
    {
        MAX_RETRIEVE_COUNT(new SystemPropertyEntryInteger("MaxRetrieveCount", 5000)),
        TIME_SCOPE_FOR_DAILY_DIGEST_TO_USERS_BEING_FOLLOWERED_IN_HOUR(new SystemPropertyEntryInteger("MaxRetrieveCount", 24)),
        ENABLE_EXTERNAL_EMAIL_VERIFIED_EVENT_TRIGGER(new SystemPropertyEntryBoolean("EnableExternalEmailVerifiedEventTrigger", true)),
        ENSURE_TRANSACTED_USER_EMAIL_ADDRESS_UPDATE(new SystemPropertyEntryBoolean("EnsureTransactedUserEmailAddressUpdate", true)),
        ENABLE_UPDATE_EMAIL_ADDRESS_FIELD_ON_USER_TABLE(new SystemPropertyEntryBoolean("EnableUpdateEmailAddressFieldOnUserTable", false)),
        THROW_ON_FAILED_UPDATE_EMAIL_ADDRESS_FIELD_ON_USER_TABLE(new SystemPropertyEntryBoolean("ThrowOnFailedUpdateEmailAddressFieldOnUserTable", false)),
        SEND_EMAIL_VERIFICATION_AFTER_EMAIL_ADDRESS_UPDATE(new SystemPropertyEntryBoolean("SendEmailVerificationAfterEmailAddressUpdate", true)),
        ENABLE_USER_ID_CHECK_WHEN_DEREFERENCING_TOKENS(new SystemPropertyEntryBoolean("EnableUserIDCheckWhenDereferencingTokens", false)),
        ENABLED_EMAIL_ADDRESS_VRIFICATION_WITH_TEMPLATE(new SystemPropertyEntryBoolean("EnabledEmailAddressVerificationWithTemplate", true)),
        EMAIL_ADDRESS_VRIFICATION_TEMPLATE_ID(new SystemPropertyEntryInteger("EmailAddressVerificationTemplateID", 103)),
        EXTERNAL_EMAIL_ADDRESS_VERIFICATION_LINK(new SystemPropertyEntryString("ExternalEmailAddressVerificationLink", "http://www.mig.me/sites/ajax/settings/account_email_verify?token=")),
        ENABLED_FORGOT_USERNAME_EMAIL_WITH_TEMPLATE(new SystemPropertyEntryBoolean("EnabledForgotUsernameEmailWithTemplate", true)),
        FORGOT_USERNAME_EMAIL_TEMPLATE_ID(new SystemPropertyEntryInteger("ForgotUsernameEmailTemplateID", 104)),
        ENABLED_FORGOT_PASSWORD_EMAIL_WITH_TEMPLATE(new SystemPropertyEntryBoolean("EnabledForgotPasswordEmailWithTemplate", true)),
        FORGOT_PASSWORD_EMAIL_TEMPLATE_ID(new SystemPropertyEntryInteger("ForgotPasswordEmailTemplateID", 105)),
        ENABLED_SEND_VERIFICATION_TOKEN_WITH_TEMPLATE(new SystemPropertyEntryBoolean("EnabledSendVerificationTokenWithTemplate", true)),
        PASS_THROUGH_EMAIL_TEMPLATE_IDS(new SystemPropertyEntryIntegerArray("PassThroughEmailTemplateIds", new int[]{103, 104, 105})),
        EMAIL_BASIC_CHECKS_REGEX(new SystemPropertyEntryString("EmailBasicChecksRegex", "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-]+\\.)+([A-Za-z]{2,})$"));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Email(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "EMAIL";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Alert implements SystemPropertyEntryInterface
    {
        MIGBO_SYS_ALERTS_FOR_NEW_USER(new SystemPropertyEntryString("MigboSysAlertsForNewUser", "")),
        PT72238252_MIGBO_PERSISTENT_ALERTS_EXCLUDE_EXPIRE_ENABLED(new SystemPropertyEntryBoolean("PT72238252MigboPersistentAlertsExludeExpireEnabled", true)),
        MIGBO_SYS_ALERTS_FOR_NEW_USER_ENABLED(new SystemPropertyEntryBoolean("MigboSysAlertsForNewUserEnabled", false)),
        MIGBO_SYS_ALERTS_FOR_INVITER_ENABLED(new SystemPropertyEntryBoolean("MigboSysAlertsForInviterEnabled", false)),
        MIGBO_SYS_ALERTS_FOR_INVITER_ENABLED_WHEN_INVITEE_ACCEPT_INVITATION(new SystemPropertyEntryBoolean("MigboSysAlertsForInviterEnabledWhenInviteeAcceptInvitation", false)),
        ENABLED_MANDATORY_RESET_UNREAD_COUNT(new SystemPropertyEntryBoolean("EnabledMandatoryResetUnreadCount", true)),
        MANDATORY_RESET_UNREAD_COUNT_RATE_LIMIT(new SystemPropertyEntryString("MandatoryResetUnreadCountRateLimit", "1/1M"));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Alert(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Alert";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Migbo implements SystemPropertyEntryInterface
    {
        ELASTIC_SEARCH_DISABLED(new SystemPropertyEntryInteger("ElasticSearchDisabled", 0)),
        MIGBO_DISABLED(new SystemPropertyEntryInteger("MigboDisabled", 0)),
        WATCHLIST_DISABLED(new SystemPropertyEntryInteger("WatchListDisabled", 1)),
        ONEWAY_MIGBO_API_CALLS_ENABLED(new SystemPropertyEntryBoolean("OneWayMigboApiCallsEnabled", true)),
        VALHALLA_MIGBO_URL(new SystemPropertyEntryString("ValhallaMigboURL", "http://v.mig33.com")),
        VALHALLA_MIGBO_DATASVC_HOST(new SystemPropertyEntryString("ValhallaMigboDataSvcHost", "v-api.mig33.com")),
        VALHALLA_MIGBO_DATASVC_PORT(new SystemPropertyEntryInteger("ValhallaMigboDataSvcPort", 8080)),
        VALHALLA_MIGBO_DATASVC_USEHTTPS(new SystemPropertyEntryBoolean("ValhallaMigboDataSvcUseHttps", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Migbo(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Migbo";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Emote implements SystemPropertyEntryInterface
    {
        BAN_INDEX_MAX(new SystemPropertyEntryInteger("BanIndexMax", 5)),
        BAN_INDEX_RATE_LIMIT(new SystemPropertyEntryString("BanIndexRateLimit", "15/1M")),
        KICK_INDEX_MAX(new SystemPropertyEntryInteger("KickIndexMax", 5)),
        KICK_INDEX_RATE_LIMIT(new SystemPropertyEntryString("KickIndexRateLimit", "15/1M")),
        LIST_RATE_LIMIT(new SystemPropertyEntryString("ListRateLimit", "15/1M")),
        FOLLOW_RATE_LIMIT(new SystemPropertyEntryString("FollowRateLimit", "10/1M")),
        UNFOLLOW_RATE_LIMIT(new SystemPropertyEntryString("UnfollowRateLimit", "10/1M")),
        BUMP_RATE_LIMIT(new SystemPropertyEntryString("BumpRateLimit", "10/1M")),
        WARN_RATE_LIMIT(new SystemPropertyEntryString("WarnRateLimit", "10/1M")),
        ADD_MODERATOR_RATE_LIMIT(new SystemPropertyEntryString("AddModeratorRateLimit", "10/1M")),
        REMOVE_MODERATOR_RATE_LIMIT(new SystemPropertyEntryString("RemoveModeratorRateLimit", "10/1M")),
        LIST_MODERATOR_RATE_LIMIT(new SystemPropertyEntryString("ListModeratorRateLimit", "10/1M")),
        SILENCE_RATE_LIMIT(new SystemPropertyEntryString("SilenceRateLimit", "30/1M")),
        KICK_CLEAR_RATE_LIMIT(new SystemPropertyEntryString("KickClearRateLimit", "10/1M")),
        KICK_CLEAR_ENABLED(new SystemPropertyEntryBoolean("KickClearEnabled", false)),
        EMOTE_COMMAND_RELOAD_INTERVAL_IN_SECONDS(new SystemPropertyEntryInteger("CommandReloadIntervalInSeconds", 600)),
        HELP_COMMAND_INFO_MESSAGE_ENABLED(new SystemPropertyEntryBoolean("HelpCommandInfoMessageEnabled", false)),
        HELP_COMMAND_INFO_MESSAGE(new SystemPropertyEntryString("HelpCommandInfoMessage", "Type /help anytime to learn more about Making Friends, Gifting/Emoticons, Miniblog, migLevels, Games, migCredits, Emotes & Username Colors!")),
        HELP_COMMAND_URL_SUFFIX(new SystemPropertyEntryString("HelpCommandUrlSuffix", "/sites/index.php?c=migworld&a=get_page&page_id=13995")),
        NEW_TAB_CLIENT_VERSION_MIN(new SystemPropertyEntryInteger("NewTabClientVersionMin", 400)),
        MAX_USERNAME_LENGTH_DISPLAY(new SystemPropertyEntryInteger("MaxUserNameLengthDisplay", 30)),
        MAX_CHATROOMNAME_LENGTH_DISPLAY(new SystemPropertyEntryInteger("MaxChatRoomNameLengthDisplay", 30)),
        MAX_MODERATOR_LIST_LENGTH_DISPLAY(new SystemPropertyEntryInteger("MaxModeratorListLengthDisplay", 1500)),
        BUMP_DURATION_SEC(new SystemPropertyEntryInteger("BumpDurationSec", 300)),
        SILENCE_TIMEOUT_CHATROOM_MIN(new SystemPropertyEntryInteger("SilenceTimeoutChatroomMin", 10)),
        SILENCE_TIMEOUT_CHATROOM_MAX(new SystemPropertyEntryInteger("SilenceTimeoutChatroomMax", 600)),
        SILENCE_TIMEOUT_INDIVIDUAL_MIN(new SystemPropertyEntryInteger("SilenceTimeoutIndividualMin", 10)),
        SILENCE_TIMEOUT_INDIVIDUAL_MAX(new SystemPropertyEntryInteger("SilenceTimeoutIndividualMax", 900)),
        WARN_MESSAGE_MAX_LENGTH(new SystemPropertyEntryInteger("WarnMessageMaxLength", 256)),
        COMMUNITY_EMOTES_MIN_CLIENT_VERSION(new SystemPropertyEntryInteger("CommunityEmotesMinClientVersion", 400)),
        STICKER_RATE_LIMIT(new SystemPropertyEntryString("StickerRateLimit", "10/1M")),
        UNBAN_RATE_LIMIT(new SystemPropertyEntryString("UnbanRateLimit", "1/5M")),
        BAN_RATE_LIMIT(new SystemPropertyEntryString("BanRateLimit", "1/5M")),
        KILL_RATE_LIMIT(new SystemPropertyEntryString("KillRateLimit", "2/1M")),
        GIFT_INVENTORY_ENABLED(new SystemPropertyEntryBoolean("GiftInventoryEnabled", true)),
        LOG_ALL_EMOTES_ENABLED(new SystemPropertyEntryBoolean("LogAllEmotesEnabled", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Emote(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Emote";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum BOT implements SystemPropertyEntryInterface
    {
        PAYOUT_FAILURE_MESSAGE(new SystemPropertyEntryString("PayoutFailureErrorMessage", "Cancelled the game due to an internal error, you were not charged")),
        SEND_BOT_SPENDING_TRIGGER_AFTER_PAYOUT_TRANSACTION(new SystemPropertyEntryBoolean("SendBotSpendingTriggerAfterPayoutTransaction", true)),
        SEND_BOT_GAME_WON_TRIGGER_AFTER_PAYOUT_TRANSACTION(new SystemPropertyEntryBoolean("SendBotGameWonTriggerAfterPayoutTransaction", true));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private BOT(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "BOT";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MigLevel implements SystemPropertyEntryInterface
    {
        NEW_USER_MAX(new SystemPropertyEntryInteger("NewUserMax", 5)),
        GROUP_CREATE_USER_POST_MIN(new SystemPropertyEntryInteger("GroupCreateUserPostMin", 3)),
        REPUTATIONSLEVEL_DATA_REFRESH_IN_SECONDS(new SystemPropertyEntryInteger("ReputationLevelDataRefreshInSeconds", 600));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private MigLevel(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "MigLevel";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Registration implements SystemPropertyEntryInterface
    {
        REGISTRATION_DISABLED(new SystemPropertyEntryBoolean("RegistrationDisabled", false)),
        REGISTRATION_DISABLED_ERROR_MESSAGE(new SystemPropertyEntryString("RegistrationDisabledErrMsg", "Registration has been disabled at the moment, please try again later.")),
        REGISTRATION_CONTEXT_ENABLED(new SystemPropertyEntryBoolean("RegistrationContextEnabled", false)),
        EMAIL_REGISTRATION_RATE_LIMIT_BY_IP(new SystemPropertyEntryString("EmailRegistrationRateLimitByIP", "2000/1D")),
        EMAIL_REGISTRATION_RATE_LIMIT_BY_IP_WHITELIST(new SystemPropertyEntryStringArray("EmailRegistrationRateLimitByIPWhitelist", new String[0])),
        EMAIL_REGISTRATION_RATE_LIMIT_BY_DOMAIN(new SystemPropertyEntryString("EmailRegistrationRateLimitByDomain", "2000/1D")),
        EMAIL_REGISTRATION_RATE_LIMIT_BY_SANITIZIED_LOCAL_AND_IP(new SystemPropertyEntryString("EmailRegistrationRateLimitBySanitizedLocalAndIp", "10/1D")),
        EMAIL_REGISTRATION_RATE_LIMIT_BY_SANITIZIED_LOCAL_AND_DOMAIN(new SystemPropertyEntryString("EmailRegistrationRateLimitBySanitizedLocalAndDomain", "10/1D")),
        EMAIL_REGISTRATION_RATE_LIMIT_BY_NONWHITELISTED_DOMAINS_TOP_LEVEL_PART(new SystemPropertyEntryString("EmailRegistrationRateLimitByNonWhitelistedDomainsTopLevelPart", "10/1D")),
        EMAIL_REGISTRATION_RATE_LIMIT_BY_DOMAIN_WHITELIST(new SystemPropertyEntryStringArray("EmailRegistrationRateLimitByDomainWhitelist", new String[0])),
        EMAIL_REGISTRATION_TIMELIMIT_IN_MINUTES(new SystemPropertyEntryInteger("EmailRegistrationTimeLimitInMinutes", 2880)),
        EMAIL_REGISTRATION_GRACE_PERIOD_IN_MINUTES(new SystemPropertyEntryInteger("EmailRegistrationGracePeriodInMinutes", 60)),
        EMAIL_REGISTRATION_PATH1_ENABLED(new SystemPropertyEntryBoolean("EmailPath1Enabled", false)),
        EMAIL_REGISTRATION_PATH2_ENABLED(new SystemPropertyEntryBoolean("EmailPath2Enabled", false)),
        REGISTRATION_V2_PATH(new SystemPropertyEntryString("RegV2Path", "")),
        POST_VERIFICATION_REDIRECT_URL(new SystemPropertyEntryString("PostVerificationRedirectURL", "")),
        RESEND_VERIFICATION_EMAIL_ENABLED(new SystemPropertyEntryBoolean("ResendVerificationEmailEnabled", true)),
        MOBILE_REGISTRATION_DISABLED(new SystemPropertyEntryBoolean("mobileRegistrationDisabled", false)),
        MOBILE_REGISTRATION_DISABLED_MESSAGE(new SystemPropertyEntryString("mobileRegistrationDisabledMessage", "Sorry, mobile registration has been disabled for this version of migme. Please go to m.mig33.com to register.")),
        DO_SYNCHRONOUS_PROFILE_CACHE_INVALIDATION_ON_MIGBO_UPON_MOBILE_AUTH(new SystemPropertyEntryBoolean("DoSyncMigboProfileCacheInvalidationUponMobileAuth", false)),
        SANITIZED_EMAIL_LOCAL_PART_AND_IP_RATE_LIMIT_ENABLED(new SystemPropertyEntryBoolean("SanitizedEmailLocalPartAndIpRateLimitEnabled", false)),
        SANITIZED_EMAIL_LOCAL_PART_AND_DOMAIN_RATE_LIMIT_ENABLED(new SystemPropertyEntryBoolean("SanitizedEmailLocalPartAndDomainRateLimitEnabled", false)),
        NON_WHITELISTED_DOMAINS_TOP_LEVEL_PART_RATE_LIMIT_ENABLED(new SystemPropertyEntryBoolean("NonWhitelistedDomainsTopLevelPartRateLimitEnabled", false)),
        COMMON_PREFIX_CHECK_FOR_MOBILE_REGISTRATION_ENABLED(new SystemPropertyEntryBoolean("CommonPrefixCheckEnabled", false)),
        MAX_COMMON_PREFIX_RECENT_ACTIVATIONS_ALLOWED(new SystemPropertyEntryInteger("MaxCommonPrefixRecentActivationsAllowed", 3)),
        NUMBER_OF_RECENT_ACTIVATIONS_FOR_COMMON_PREFIX_CHECK(new SystemPropertyEntryInteger("NumberOfRecentActivationsForCommonPrefixCheck", 40000)),
        IPWHITELIST_PREFIX_FOR_COMMON_PREFIX_CHECK(new SystemPropertyEntryStringArray("IPWhiteListPrefixForCommonPrefixCheck", new String[]{"10."})),
        RANDOMIZE_MIDLET_REGISTRATION_CAPTCHA_ENABLED(new SystemPropertyEntryBoolean("RandomizeMidletRegistrationCaptchaEnabled", false)),
        MAX_WORD_LENGTH_FOR_RANDOMIZED_MIDLET_REGISTRATION_CAPTCHA(new SystemPropertyEntryInteger("MaxWordLengthForRandomizedMidletRegistrationCaptcha", 5)),
        MIN_WORD_LENGTH_FOR_RANDOMIZED_MIDLET_REGISTRATION_CAPTCHA(new SystemPropertyEntryInteger("MinWordLengthForRandomizedMidletRegistrationCaptcha", 3)),
        SPECIAL_VALIDATION_FOR_GMAIL_ENABLED(new SystemPropertyEntryBoolean("SpecialValidationForGmailEnabled", false)),
        NUMBER_OF_PERIODS_ALLOWED_IN_GMAIL_ADDRESSES(new SystemPropertyEntryInteger("NumberOfPeriodsAllowedInGmailAddresses", 3)),
        POST_VERIFICATION_ALLOW_MULTIPLE_AUTO_LOGIN_ENABLED(new SystemPropertyEntryBoolean("PostVerificationAllowMultipleAutoLoginEnabled", true)),
        STRIP_PERIODS_FROM_GMAIL_ADDRESS_ENABLED(new SystemPropertyEntryBoolean("StripPeriodsFromGmailAddressEnabled", false)),
        EMAIL_EXCLUSION_REGULAR_EXPRESSIONS(new SystemPropertyEntryString("EmailExclusionRegularExpressions", "")),
        ALLOW_OVERRIDING_USER_REGISTRATION_DATE_FOR_UNIT_TESTING(new SystemPropertyEntryBoolean("AllowOverridingUserRegistrationDateForUnitTesting", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Registration(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Registration";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum GroupChat implements SystemPropertyEntryInterface
    {
        GROUP_CHAT_PARTICIPANT_LIMIT(new SystemPropertyEntryInteger("ParticipantLimit", 10));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private GroupChat(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "GroupChat";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Group implements SystemPropertyEntryInterface
    {
        MODERATOR_MIN_USER_LEVEL(new SystemPropertyEntryInteger("ModeratorMinUserLevel", 10));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Group(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Group";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum FusionRestRateLimit implements SystemPropertyEntryInterface
    {
        EVENT_ALERTBATCH(new SystemPropertyEntryString("EventAlertBatch", "1/15M"));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private FusionRestRateLimit(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "FusionRestRateLimit";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EventQueueSettings implements SystemPropertyEntryInterface
    {
        MAXIMUM_WORKER_QUEUE_SIZE(new SystemPropertyEntryInteger("MaximumWorkerQueueSize", 10000)),
        WORKER_THREAD_POOL_SIZE(new SystemPropertyEntryInteger("WorkerThreadPoolSize", 1)),
        QUEUE_CHECK_INTERVAL_IN_SECONDS(new SystemPropertyEntryInteger("QueueCheckIntervalInSeconds", 5)),
        RETRY_INTERVAL_IN_SECONDS(new SystemPropertyEntryInteger("RetryIntervalInSeconds", 5)),
        SHUTDOWN_WAIT_IN_SECONDS(new SystemPropertyEntryInteger("ShutdownWaitlInSeconds", 5)),
        QUEUE_TYPE(new SystemPropertyEntryString("QueueType", "redis")),
        PERSISTENT_REDIS_CONNECTION_ENABLED(new SystemPropertyEntryBoolean("PersistentRedisConnectionEnabled", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private EventQueueSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "EventQueueSettings";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Chatroom implements SystemPropertyEntryInterface
    {
        CHATROOM_WELCOME_MESSAGE_ENABLED(new SystemPropertyEntryBoolean("ChatroomWelcomeMessageEnabled", true)),
        INSUFFICIENT_MIGLEVEL_TO_CREATE_CHATROOM_MESSAGE(new SystemPropertyEntryString("InsufficientMiglevelToCreateChatroomMessage", "You need to be mig level 10 and above to create a chat room")),
        MULTI_ID_CONTROL_ENABLED(new SystemPropertyEntryBoolean("MultiIDControlEnabled", false)),
        BLOCK_PERIOD_BY_IP_IN_SECONDS(new SystemPropertyEntryInteger("BlockPeriodByIPInSeconds", 1800)),
        BANNED_USERS_PAGE_SIZE(new SystemPropertyEntryInteger("BannedUsersPageSize", MemCachedKeySpaces.CommonKeySpace.CHATROOM_BANNED_USERS.getPageSize())),
        MULTI_ID_CONTROL_IP_WHITELIST(new SystemPropertyEntryStringArray("MultiIDControlIPWhitelist", new String[0])),
        NEGATIVE_CACHE_ENABLED(new SystemPropertyEntryBoolean("NegativeCacheEnabled", false)),
        MESSAGE_QUEUE_CHECK_SIZE_BEFORE_PUTMESSAGE_ENABLED(new SystemPropertyEntryBoolean("MsgQueueCheckSizeBeforePutMessageEnabled", true)),
        MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN(new SystemPropertyEntryLong("MsgQueueAddTimeoutMillisAdmin", 10000L)),
        MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS(new SystemPropertyEntryLong("MsgQueueAddTimeoutMillis", 1000L)),
        MESSAGE_QUEUE_VIA_LIST_MAX_SIZE(new SystemPropertyEntryInteger("MsgQueueViaListMaxSize", Integer.MAX_VALUE)),
        MESSAGE_QUEUE_VIA_LIST_ADD_WAIT_INTERVAL_IN_MILLIS(new SystemPropertyEntryLong("MsgQueueViaListAddWaitIntervalInMillis", 500L)),
        MESSAGE_QUEUE_DISPATCH_WINDOW_MAX_DURATION_IN_MILLIS(new SystemPropertyEntryLong("MsgQueueMaxDurInMillisPerDispatchWindow", 1000L)),
        MESSAGE_QUEUE_DISPATCH_WINDOW_MAX_MESSAGE(new SystemPropertyEntryInteger("MsgQueueMaxMsgPerDispatchWindow", 5)),
        NUE_NEWBIE_CATEGORIES_ENABLED(new SystemPropertyEntryBoolean("NueNewbieCategoriesEnabled", false)),
        MAX_NEWBIE_MIG33_LEVEL(new SystemPropertyEntryInteger("MaxNewbieMig33Level", 10)),
        CATEGORIES_ALWAYS_INCLUDED_IN_LOGIN(new SystemPropertyEntryIntegerArray("CategoriesAlwaysIncludedInLogin", new int[0])),
        CATEGORIES_LIST_CACHE_TIME_IN_MILLIS(new SystemPropertyEntryLong("CategoriesListCacheTimeInMillis", 3600000L)),
        CHATROOM_LIST_CACHE_TIME_IN_MILLIS(new SystemPropertyEntryLong("ChatroomListCacheTimeInMillis", 3600000L)),
        MAX_NAME_LENGTH(new SystemPropertyEntryInteger("MaxNameLength", 15)),
        BANNED_NAMES(new SystemPropertyEntryString("BannedNames", "")),
        ENABLE_OVERRIDE_EXPIRED_KICK_USER_VOTE(new SystemPropertyEntryBoolean("EnableOverrideExpiredKickUserVote", false)),
        DROP_MESSAGES_FROM_MUTED_USERS(new SystemPropertyEntryBoolean("DropMessagesFromMutedUsers", true)),
        MAX_RECOMMENDED_CHATROOMS(new SystemPropertyEntryInteger("MaxRecommendedChatrooms", 10)),
        PARTICIPANT_CACHE_DATA(new SystemPropertyEntryBoolean("ParticipantCacheData", true)),
        CHECK_USER_PRESENT_BEFORE_DISPATCH(new SystemPropertyEntryBoolean("CheckUserPresentBeforeDispatch", true)),
        DELAY_USER_MESSAGE_TIME(new SystemPropertyEntryInteger("UserMessageDelayInMs", 100)),
        ENTER_EXIT_MESSAGE_DELAY_IN_MS(new SystemPropertyEntryInteger("EnterExitMessageDelayInMs", 2000)),
        SILENCE_FAST_EXIT_MESSAGES(new SystemPropertyEntryBoolean("SilenceFastExitMessages", true)),
        EXIT_SILENCE_TIME_IN_MS(new SystemPropertyEntryInteger("ExitSilenceTimeInMs", 2500)),
        USE_ENTER_EXIT_MESSAGE_CHECKS(new SystemPropertyEntryBoolean("UseEnterExitMessageChecks", true)),
        PT67727788_USE_LOCKLESS_ADMIN_KICK(new SystemPropertyEntryBoolean("UseLocklessAdminKick", true)),
        ENABLE_MUTE(new SystemPropertyEntryBoolean("EnableMute", false)),
        USE_IP_RATELIMIT(new SystemPropertyEntryBoolean("UseIpRatelimit", true)),
        OVERRIDE_IP_RATELIMIT(new SystemPropertyEntryString("OverrideIpRatelimit", "3/1S")),
        DISABLE_VOTE_KICK_TIME_IN_SECONDS(new SystemPropertyEntryInteger("DisableVoteKickTimeInSeconds", 10)),
        ENABLED_REFINED_SQL_FOR_SEARCHING_CHATROOM(new SystemPropertyEntryBoolean("EnabledRefinedSqlForSearchingChatroom", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Chatroom(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Chatroom";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum LoggingSections implements SystemPropertyEntryInterface
    {
        ENABLED(new SystemPropertyEntryBoolean("Enabled", true));

        private SystemPropertyEntry<?> value;
        public static final String NS = "LoggingSections";

        private LoggingSections(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Credentials implements SystemPropertyEntryInterface
    {
        PASSWORD_REG_EX(new SystemPropertyEntryString("PasswordRegEx", "^(?=.*[a-zA-Z])(?=.*[0-9]).*$")),
        INVALID_PASSWORD_MESSAGE(new SystemPropertyEntryString("InvalidPasswordMessage", "Password must contain at least 6 letters or numbers, and not the same as username"));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Credentials(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Credentials";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum CashReceipt implements SystemPropertyEntryInterface
    {
        MATCH_UPON_CREATION_ENABLED(new SystemPropertyEntryBoolean("MatchUponCreationEnabled", false)),
        VALIDATE_CASH_RECEIPT_STATUS_BEFORE_MATCH(new SystemPropertyEntryBoolean("ValidateCashReceiptStatusBeforeMatch", true)),
        ENABLE_CASH_RECEIPT_ID_WITH_NEW_ID_RETRIEVAL(new SystemPropertyEntryBoolean("ValidateCashReceiptStatusBeforeMatch", true));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private CashReceipt(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "CashReceipt";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Contacts implements SystemPropertyEntryInterface
    {
        ADD_CONTACT_ON_NEW_FOLLOWING_EVENT_ENABLED(new SystemPropertyEntryBoolean("AddContactOnNewFollowingEvent", false)),
        FOLLOW_ON_ADD_CONTACT_ENABLED(new SystemPropertyEntryBoolean("FollowOnAddContactEnabled", false)),
        HANDLE_FOLLOWING_EVENTS_FROM_MIGBO_ENABLED(new SystemPropertyEntryBoolean("HandleFollowingEventsFromMigboEnabled", false)),
        HANDLE_FRIENDINVITE_EVENTS_FROM_MIGBO_ENABLED(new SystemPropertyEntryBoolean("HandleFriendInviteEventsFromMigboEnabled", true)),
        ONEWAY_MIGBO_API_CALLS_ENABLED(new SystemPropertyEntryBoolean("OneWayMigboApiCallsEnabled", true)),
        MAX_PENDING_CONTACTS_TO_RETRIEVE(new SystemPropertyEntryInteger("MaxPendingContactsToRetrieve", 15)),
        MIGBO_INTEGRATION_ENABLED(new SystemPropertyEntryBoolean("MigboIntegrationEnabled", false)),
        PUSH_NEW_FOLLOWERS_AS_PENDING_CONTACTS_ENABLED(new SystemPropertyEntryBoolean("PushNewFollowersAsPendingContactsEnabled", false)),
        REMOVE_CONTACT_ON_REMOVE_FOLLOWING_EVENT_ENABLED(new SystemPropertyEntryBoolean("RemoveContactOnRemoveFollowingEvent", true)),
        UNFOLLOW_ON_REMOVE_CONTACT_ENABLED(new SystemPropertyEntryBoolean("UnfollowOnRemoveContactEnabled", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Contacts(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Contacts";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum UserNotificationServiceSettings implements SystemPropertyEntryInterface
    {
        MAX_NOTIFICATION_THREAD_BACKOFF_IN_SECONDS(new SystemPropertyEntryLong("MaxNotificationThreadBackoffInSeconds", 120L)),
        MAX_NOTIFICATION_SERVICE_QUEUE_SIZE(new SystemPropertyEntryLong("MaxNotificationServiceQueueSize", 1000L)),
        USER_NOTIFICATION_TRIM_ON_READ_ENABLED(new SystemPropertyEntryBoolean("UserNotificationTrimOnReadEnabled", true)),
        USER_NOTIFICATION_TRIM_ON_WRITE_ENABLED(new SystemPropertyEntryBoolean("UserNotificationTrimOnWriteEnabled", false)),
        USER_NOTIFICATION_USER_MUTEX_TIMEOUT_MINS(new SystemPropertyEntryInteger("UserNotificationUserMutexTimeoutMins", 30)),
        MAX_NOTIFICATIONS_TO_SEND_TO_USER(new SystemPropertyEntryInteger("MaxNotificationsToSendToUser", 100)),
        MAX_NOTIFICATIONS_TO_SEND_TO_USER_TRUNCATION(new SystemPropertyEntryInteger("MaxNotificationsToSendToUserTruncation", 80)),
        PERSISTENT_ALERT_RECOUNT_ENABLED(new SystemPropertyEntryBoolean("PersistentAlertRecountEnabled", false)),
        NEW_EMAIL_SENDING_LOGIC_ENABLED(new SystemPropertyEntryBoolean("NewEmailSendingLogicEnabled", true)),
        MAX_EMAIL_MESSAGE_LENGTH_FOR_ENQUEUE_LOG(new SystemPropertyEntryInteger("MaxEmailMessageLengthForEnqueueLog", 100)),
        MAX_EMAIL_SUBJECT_LENGTH_FOR_ENQUEUE_LOG(new SystemPropertyEntryInteger("MaxEmailSubjectLengthForEnqueueLog", 50)),
        MAX_EMAIL_SENDING_ERROR_MSG_LENGTH(new SystemPropertyEntryInteger("MaxEmailSendingErrorMsgLength", 1000)),
        STRIP_PATTERN_FOR_ENQUEUE_LOG(new SystemPropertyEntryString("StripPatternForEnqueueLog", "\r|\n|\\|")),
        LOG_BEFORE_SENDING_EMAIL_TO_SMTP_ENABLED(new SystemPropertyEntryBoolean("LogBeforeSendingEmailToSMTPEnabled", true)),
        LOG_AFTER_SENDING_EMAIL_TO_SMTP_ENABLED(new SystemPropertyEntryBoolean("LogAfterSendingEmailToSMTPEnabled", true)),
        LOG_ENQUEUE_EMAIL_ENABLED(new SystemPropertyEntryBoolean("LogEnqueueEmailEnabled", true)),
        INTERNAL_EMAIL_ENABLED(new SystemPropertyEntryBoolean("InternalEmailEnabled", true)),
        INTERNAL_EMAIL_WHITELIST(new SystemPropertyEntryStringArray("InternalEmailWhiteList", new String[]{"contact@mig.me", "donotreply@mig.me", "noreply@mig.me", "mig@mig.me"})),
        INQUERY_TO_EMAIL_ADDRESS(new SystemPropertyEntryString("InqueryFromEmailAddress", "contact@mig.me")),
        NEW_TEMPLATE_PROCESSOR_ENABLED(new SystemPropertyEntryBoolean("NewTemplateProcessorEnabled", true)),
        ENABLED_SEND_TO_TRANSIENT_EMAIL(new SystemPropertyEntryBoolean("EnabledSendToTransientEmail", true)),
        CHECK_EMAIL_BOUNCEDB_ENABLED(new SystemPropertyEntryBoolean("CheckEmailBounceDBEnabled", true));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private UserNotificationServiceSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "UserNotificationServiceSettings";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum SSO implements SystemPropertyEntryInterface
    {
        SUPPORT_ANDROID_VERSION(new SystemPropertyEntryInteger("SupportAndroidVersion", 0)),
        SUPPORT_MIDLET_VERSION(new SystemPropertyEntryInteger("SupportMidletVersion", 0)),
        FUSION_EID_ANDROID_VERSION(new SystemPropertyEntryInteger("FusionEIDAndroidVersion", 0)),
        FUSION_EID_MIDLET_VERSION(new SystemPropertyEntryInteger("FusionEIDMidletVersion", 0)),
        EID_ENCRPYTION_KEY(new SystemPropertyEntryString("EncryptedSessionIDEncryptionKey", "EeHDV5r|~OLODVD4;:4-fSQ%[w{C#<tu")),
        SSO_SESSION_ALLOWED_VIEWS(new SystemPropertyEntryIntegerArray("SSOSessionAllowedViews", new int[0])),
        FUSION_SESSION_ALLOWED_VIEWS(new SystemPropertyEntryIntegerArray("FusionSessionAllowedDeviceTypes", new int[0])),
        CREATE_SSO_SESSION_FROM_FUSION(new SystemPropertyEntryBoolean("CreateSSOSessionFromFusionEnabled", false)),
        SESSION_ARCHIVE_ENABLED(new SystemPropertyEntryBoolean("SessionArchiveEnabled", false)),
        VAS_LOG_ENABLED(new SystemPropertyEntryBoolean("VASLogEnabled", false)),
        SESSION_EXPIRY_IN_SECONDS(new SystemPropertyEntryLong("SessionExpiryInSeconds", 604800L)),
        SESSION_INITIAL_EXPIRY_IN_SECONDS(new SystemPropertyEntryLong("SessionInitialExpiryInSeconds", 604800L)),
        MAX_FAILED_LOGIN_ATTEMPTS(new SystemPropertyEntryInteger("MaxFailedLoginAttempts", 10)),
        MIN_LOGIN_UPDATE_PERIOD_IN_SECONDS(new SystemPropertyEntryInteger("MinLoginUpdatePeriod", 60)),
        TEMP_LOGIN_SUSPENSION_ERROR_MESSAGE(new SystemPropertyEntryString("TempLoginSuspensionLoginErrorMessage", "Unable to login right now. Please try again later.")),
        EMAIL_UNAUTH_USER_LOGIN_DISABLED(new SystemPropertyEntryBoolean("EmailUnauthUserLoginDisabled", true)),
        AUTHENTICATION_FAILED_ATTEMPT_EXIPRY_IN_SECONDS(new SystemPropertyEntryInteger("AuthenticationFailedAttemptExpiryInSeconds", 86400)),
        MAX_FAILED_AUTHENTICATION_ATTEMPTS(new SystemPropertyEntryInteger("MaxFailedAuthenticationAttempts", 10)),
        DEVICES_REQUIRING_FULL_LOGIN_RESPONSE_ON_CREATE_SESSION(new SystemPropertyEntryStringArray("DevicesRequiringFullLoginResponseOnCreateSession", StringUtil.EMPTY_STRING_ARRAY)),
        AUTO_LOGIN_ENABLED(new SystemPropertyEntryBoolean("AutoLoginEnabled", false)),
        AUTO_LOGIN_RATE_LIMIT(new SystemPropertyEntryString("AutoLoginRateLimit", "3/1D")),
        ENABLE_PRELOGIN_USERNAME_CHAR_VALIDATION(new SystemPropertyEntryBoolean("EnablePreloginUsernameCharValidation", false)),
        LOG_INVALID_USERNAME_ON_PRELOGIN(new SystemPropertyEntryBoolean("LogInvalidUsernameOnPrelogin", true)),
        FAILED_AUTHS_PER_IP_HALF_LIFE_SECS(new SystemPropertyEntryInteger("FailedAuthsPerIpHalfLifeSecs", 300)),
        FAILED_AUTHS_PER_IP_THRESHOLD(new SystemPropertyEntryInteger("FailedAuthsPerIpThreshold", 1000)),
        FAILED_AUTHS_PER_IP_PACKET_DELAY_UNIT_SECS(new SystemPropertyEntryInteger("FailedAuthsPerIpPacketDelayUnitSecs", 5)),
        FAILED_AUTHS_PER_IP_PACKET_DELAY_POWER(new SystemPropertyEntryDouble("FailedAuthsPerIpPacketDelayPower", 0.5)),
        FAILED_AUTHS_PER_IP_PACKET_DROP_THRESHOLD(new SystemPropertyEntryInteger("FailedAuthsPerIpPacketDropThreshold", 4000)),
        FAILED_AUTHS_PER_IP_CAS_TIMEOUT_MILLIS(new SystemPropertyEntryLong("FailedAuthsPerIpCasTimeoutMillis", 5000L)),
        MAX_SESSIONID_COUNT_FOR_USER(new SystemPropertyEntryLong("MaxSessionIdCountForUser", 50L)),
        SESSION_KEY_CONTAINER_EXPIRY_TIME_SECS(new SystemPropertyEntryInteger("SessionKeyContainerExpiryTimeSecs", 604800));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private SSO(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "SSO";
        }

        public static class Cache {
            public static LazyLoader<Long> MAX_SESSIONID_COUNT_FOR_USER = new LazyLoader<Long>("MAX_SESSIONID_COUNT_FOR_USER", 60000L){

                @Override
                protected Long fetchValue() throws Exception {
                    return SystemProperty.getLong(SSO.MAX_SESSIONID_COUNT_FOR_USER);
                }
            };
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class GuardsetEnabled
    implements SystemPropertyEntryInterface {
        public static final GuardsetEnabled FUSION_PKT_GET_EMOTICON_HI_RES_SUPPORT = new GuardsetEnabled(GuardCapabilityEnum.FUSION_PKT_GET_EMOTICON_HI_RES_SUPPORT);
        public static final GuardsetEnabled SEND_STICKERS_ALLOWED = new GuardsetEnabled(GuardCapabilityEnum.SEND_STICKERS_ALLOWED);
        public static final GuardsetEnabled RECEIVE_STICKERS_NATIVE_SUPPORT = new GuardsetEnabled(GuardCapabilityEnum.RECEIVE_STICKERS_NATIVE_SUPPORT);
        public static final GuardsetEnabled UPLOAD_ADDRESSBOOK_DATA = new GuardsetEnabled(GuardCapabilityEnum.UPLOAD_ADDRESSBOOK_DATA);
        private SystemPropertyEntryBoolean value;
        private static String NS = "GuardsetEnabled";

        public GuardsetEnabled(GuardCapabilityEnum guard) {
            this.value = new SystemPropertyEntryBoolean(guard == null ? "" : guard.name(), true);
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class DeprecatedPacketFallbackURL
    implements SystemPropertyEntryInterface {
        private SystemPropertyEntryString value;
        private static String NS = "DeprecatedPacketFallbackURL";

        public DeprecatedPacketFallbackURL(FusionRequest packet) {
            this.value = new SystemPropertyEntryString(packet.getSimpleName(), "");
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class GatewayRateLimitByChatRoom
    implements SystemPropertyEntryInterface {
        private SystemPropertyEntryString value;
        private boolean isGlobalAdmin;
        private static String NS = "GatewayRateLimitByChatRoom";
        private static String NSA = "GatewayRateLimitByChatRoomForGlobalAdmins";

        public GatewayRateLimitByChatRoom(FusionRequest packet) {
            this.value = new SystemPropertyEntryString(packet.getSimpleName(), SystemProperty.get(GatewaySettings.DEFAULT_REQUEST_RATELIMIT));
            this.isGlobalAdmin = false;
        }

        public GatewayRateLimitByChatRoom(FusionRequest packet, boolean isGlobalAdmin) {
            this.value = new SystemPropertyEntryString(packet.getSimpleName(), SystemProperty.get(GatewaySettings.DEFAULT_REQUEST_RATELIMIT));
            this.isGlobalAdmin = isGlobalAdmin;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(this.isGlobalAdmin ? NSA : NS, this.value.getName());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class GatewayRateLimit
    implements SystemPropertyEntryInterface {
        private SystemPropertyEntryString value;
        private boolean isGlobalAdmin;
        private static String NS = "GatewayRateLimit";
        private static String NSA = "GatewayRateLimitForGlobalAdmins";

        public GatewayRateLimit(FusionRequest packet) {
            this.value = new SystemPropertyEntryString(packet.getSimpleName(), SystemProperty.get(GatewaySettings.DEFAULT_REQUEST_RATELIMIT));
            this.isGlobalAdmin = false;
        }

        public GatewayRateLimit(FusionRequest packet, boolean isGlobalAdmin) {
            this.value = new SystemPropertyEntryString(packet.getSimpleName(), SystemProperty.get(GatewaySettings.DEFAULT_REQUEST_RATELIMIT));
            this.isGlobalAdmin = isGlobalAdmin;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(this.isGlobalAdmin ? NSA : NS, this.value.getName());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class MetricsLoggingEnabled
    implements SystemPropertyEntryInterface {
        private SystemPropertyEntryBoolean value;
        private static String NS = "MetricsLoggingEnabled";

        public MetricsLoggingEnabled(MetricsEnums.MetricsEntryInterface entry) {
            this.value = new SystemPropertyEntryBoolean(entry.getSimpleName(), false);
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class MechanicsEngineCategoryDailyMaximum
    implements SystemPropertyEntryInterface {
        private SystemPropertyEntryInteger value;
        private static String NS = "MechanicsEngine:CategoryDailyMax";

        public MechanicsEngineCategoryDailyMaximum(RewardProgramData.CategoryEnum category) {
            this.value = new SystemPropertyEntryInteger(category.toString(), SystemProperty.getInt(MechanicsEngineSettings.DEFAULT_CATEGORY_DAILYMAX));
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MechanicsEngineSettings implements SystemPropertyEntryInterface
    {
        ENABLE_MM_V1(new SystemPropertyEntryBoolean("EnableMMv1", true)),
        DEFAULT_CATEGORY_DAILYMAX(new SystemPropertyEntryInteger("DefaultCategoryDailyMaximum", 100)),
        REWARD_CENTRE_QUEUE_MAXSIZE(new SystemPropertyEntryInteger("RewardCentreMaxQueueSize", 10000)),
        REWARD_CENTRE_SCORECAP_REFRESH_MILLIS(new SystemPropertyEntryLong("RewardCentreMaxQueueSize", 300000L)),
        REWARD_CENTRE_SCORECAP_CHECKED_FIRST_ENABLED(new SystemPropertyEntryBoolean("RewardCentreScoreCapCheckFirstEnabled", true)),
        REWARD_CENTRE_LEVEL_SCORECAP_ENABLED(new SystemPropertyEntryBoolean("RewardCentreLevelScorecapEnabled", true)),
        REWARD_PROCESSOR_MATCH_FROM_INVENTORY_ENABLED(new SystemPropertyEntryBoolean("RewardProcessorMatchFromInventoryEnabled", true)),
        EXCLUDE_TYPE_INVITEE_REWARDED_TRACKING(new SystemPropertyEntryIntegerArray("ExcludeTypeInviteeRewardedTracking", new int[]{43})),
        EXCLUDE_REWARD_ID_INVITEE_REWARDED_TRACKING(new SystemPropertyEntryIntegerArray("ExcludeRewardIdInviteeRewardedTracking", new int[0])),
        ENABLE_INVITEE_REWARDED_TRACKING(new SystemPropertyEntryBoolean("EnableInviteeRewardedTracking", true)),
        UNIQUE_INVITEE_REWARDED_MAX_COUNT(new SystemPropertyEntryInteger("UniqueInviteeRewardedMaxCount", 30)),
        UNIQUE_INVITEE_REWARDED_REDIS_MAX_COUNT(new SystemPropertyEntryInteger("UniqueInviteeRewardedRedisMaxCount", 60)),
        CHATROOM_MESSAGE_TRIGGER_ENABLED(new SystemPropertyEntryBoolean("ChatroomMessageTriggerEnabled", false)),
        MIGLEVEL_INCREASE_PROCESSING_ENABLED(new SystemPropertyEntryBoolean("MigLevelIncreaseProcessingEnabled", true)),
        DEFAULT_USER_EMAIL_ADDRESS_TYPE_FOR_EMAIL_NOTIFICATION(new SystemPropertyEntryInteger("DefaultUserEmailAddressTypeForEmailNotification", UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY.value)),
        ENABLE_PROCESSING_OUTCOME_AS_USERID_STRING(new SystemPropertyEntryBoolean("EnableProcessingOutcomeAsUserIDString", true)),
        ENABLE_TRIGGER_ON_REFFERED_USER_REWARDED(new SystemPropertyEntryBoolean("EnableTriggerOnReferredUserRewarded", true)),
        DISPATCHABLE_REFERRED_USER_REWARDED_TRIGGER_TYPES(new SystemPropertyEntryShortArray("DispatchableReferredUserRewardedTriggerTypes", new short[]{(short)RewardProgramData.TypeEnum.REFERRED_USER_REWARDED_WITH_MIGLEVEL.value(), (short)RewardProgramData.TypeEnum.MERCHANT_TAGGED_USER_MEETS_REWARD_CRITERIA.value()})),
        ENABLE_TRIGGER_ON_USER_REWARDED(new SystemPropertyEntryBoolean("EnableTriggerOnUserRewarded", false)),
        DISPATCHABLE_USER_REWARDED_TRIGGER_TYPES(new SystemPropertyEntryShortArray("DispatchableUserRewardedTriggerTypes", new short[]{(short)RewardProgramData.TypeEnum.USER_MEETS_REWARD_CRITERIA.value()})),
        DEFAULT_CURRENCY(new SystemPropertyEntryString("DefaultCurrency", "USD")),
        SAMPLE_SUMMARIZER_MAX_TASK_SIZE(new SystemPropertyEntryInteger("SampleSummarizerMaxTaskSize", 10000)),
        EJB_SAMPLE_SUMMARY_SINK_MAX_TASK_SIZE(new SystemPropertyEntryInteger("EJBSampleSummarySinkMaxTaskSize", 10)),
        ENABLE_TRIGGER_METRICS_LOGGING(new SystemPropertyEntryBoolean("EnableTriggerMetrics", false)),
        ENABLE_SAMPLE_SUMMARY_PERIODICAL_FLUSH(new SystemPropertyEntryBoolean("EnableSampleSummaryPeriodicalFlush", true)),
        ENABLE_MINIMISE_REDIS_THREADS_ENGINE(new SystemPropertyEntryBoolean("EnableMinimiseRedisThreadsEngine", true)),
        POLL_INTERVAL_SETTINGS_SECS(new SystemPropertyEntryInteger("PollIntervalSettingsSecs", 5)),
        TPOOL_CORE_SIZE(new SystemPropertyEntryInteger("TPoolCoreSize", 5)),
        TPOOL_MAX_THREAD_MULTIPLIER(new SystemPropertyEntryDouble("TPoolMaxThreadMultiplier", 1.0)),
        TPOOL_KEEPALIVE_SECS(new SystemPropertyEntryInteger("TPoolKeepAliveSecs", 5)),
        MAX_WORKERS_QUEUED(new SystemPropertyEntryInteger("MaxWorkersQueued", 50)),
        WAIT_ENGINE_SHUTDOWN_SECS(new SystemPropertyEntryInteger("WaitEngineShutdownSecs", 120)),
        LOG_SCORE_REWARDINGS_THAT_CAUSE_LEVEL_UPS(new SystemPropertyEntryBoolean("LogScoreRewardingsThatCauseLevelUps", true)),
        LOG_SCORE_REWARDINGS_THAT_DO_NOT_CAUSE_LEVEL_UPS(new SystemPropertyEntryBoolean("LogScoreRewardingsThatDoNotCauseLevelUps", false)),
        ENABLE_TRIGGER_FOR_MERCHANT_TAGGER_ON_TAGGED_USER_REWARDED(new SystemPropertyEntryBoolean("EnableTriggerForMerchantTaggerWhenTaggedUserRewarded", false)),
        IM_NOTIFICATION_MAX_MESSAGE_LENGTH(new SystemPropertyEntryInteger("IMNotificationMaxMessageLength", 1024)),
        FETCH_USER_REPUTATION_FROM_MASTER(new SystemPropertyEntryBoolean("FetchUserReputationFromMaster", false)),
        ENABLE_SEND_TRIGGER_FROM_REWARDS_BEAN(new SystemPropertyEntryBoolean("EnableSendTriggerFromRewardsBean", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private MechanicsEngineSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "MechanicsEngine";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RewardDispatcherSettings implements SystemPropertyEntryInterface
    {
        ENABLE_REST_INTERFACE(new SystemPropertyEntryBoolean("EnableRESTInterface", false)),
        ENABLE_LOGGING_RECEIVED_OUTCOMES_FROM_QUEUE(new SystemPropertyEntryBoolean("EnableLoggingReceivedOutcomesFromQueue", true)),
        ENABLE_LOGGING_RECEIVED_OUTCOMES_FROM_REST_INTERFACE(new SystemPropertyEntryBoolean("EnableLoggingReceivedOutcomesFromRESTInterface", true));

        private SystemPropertyEntry<?> value;
        private static final String NS = "RewardDispatcherSettings";

        private RewardDispatcherSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MarketingMechanicsV2_RMQ_TriggerRouting implements SystemPropertyEntryInterfaceWithNamespace
    {
        ROUTING_KEY(new SystemPropertyEntryString("RoutingKey", "eventq1")),
        PERSISTENT_MODE(new SystemPropertyEntryBoolean("PersistentMode", true)),
        SYNC_DISPATCH(new SystemPropertyEntryBoolean("SyncDispatch", true));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private MarketingMechanicsV2_RMQ_TriggerRouting(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        @Override
        public String getNamespace() {
            return NS;
        }

        public TriggerRoutingForEventTypeSetting forEventTypeID(int eventTypeID) {
            return new TriggerRoutingForEventTypeSetting(String.valueOf(eventTypeID), this);
        }

        static {
            NS = "MarketingMechanicsV2:RMQ:TriggerRouting";
        }

        public static class TriggerRoutingForEventTypeSetting
        extends SystemPropertyEntryWithParent {
            protected TriggerRoutingForEventTypeSetting(String subNameSpace, SystemPropertyEntryInterfaceWithNamespace parentProperty) {
                super(SystemPropertyEntryFallbackCreatorRegistry.getInstance(parentProperty), subNameSpace, parentProperty);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MarketingMechanicsV2_RMQ implements SystemPropertyEntryInterface
    {
        WAIT_CHANNEL_RETURN_TIMEOUT_MILLIS(new SystemPropertyEntryInteger("WaitChannelReturnTimeoutMillis", 5000)),
        HOST_ADDR(new SystemPropertyEntryString("HostAddr", "127.0.0.1")),
        HOST_PORT(new SystemPropertyEntryInteger("HostPort", 5672)),
        MAX_PENDING_ASYNC_CLOSE_CONN_TASK(new SystemPropertyEntryInteger("MaxPendingAsyncCloseConnTask", 10)),
        VIRTUAL_HOST(new SystemPropertyEntryString("VirtualHost", "leto")),
        USERNAME(new SystemPropertyEntryString("Username", "leto")),
        PASSWORD(new SystemPropertyEntryString("Password", "ten20304051")),
        CONNECT_TIMEOUT_MILLIS(new SystemPropertyEntryInteger("ConnectTimeoutMillis", 10000)),
        EXCHANGE_NAME(new SystemPropertyEntryString("ExchangeName", "events")),
        MAX_DISPATCH_TASK_SIZE(new SystemPropertyEntryInteger("MaxDispatchTaskSize", 10000)),
        REQUESTED_HEARTBEAT_SECS(new SystemPropertyEntryInteger("RequestedHeartbeatSecs", 20));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private MarketingMechanicsV2_RMQ(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "MarketingMechanicsV2:RMQ";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MarketingMechanicsV2 implements SystemPropertyEntryInterface
    {
        ENABLE_OUTBOUND_TRIGGERS(new SystemPropertyEntryBoolean("EnableOutboundTriggers", false)),
        SEND_WHITELIST_TRIGGER_TYPES_ONLY(new SystemPropertyEntryBoolean("SendWhitelistedTriggerTypesOnly", false)),
        TRIGGER_TYPE_WHITELIST(new SystemPropertyEntryIntegerArray("TriggerTypeWhiteList", new int[0]));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private MarketingMechanicsV2(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "MarketingMechanicsV2";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum CampaignSetting implements SystemPropertyEntryInterface
    {
        DEFAULT_RATE_LIMIT(new SystemPropertyEntryString("DefaultRateLimit", "3/1M"));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private CampaignSetting(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "CampaignSetting";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Campaign implements SystemPropertyEntryInterface
    {
        CAMPAIGN_TYPE_WHITELIST(new SystemPropertyEntryIntegerArray("CampaignTypeWhiteList", new int[]{43}));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Campaign(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Campaign";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum DailyDigest implements SystemPropertyEntryInterface
    {
        SEND_DIGEST_TO_BLACK_LIST_DOMAIN_USER_ENABLED(new SystemPropertyEntryBoolean("SendDailyDigestEmailToBlackListDomainUserEnabled", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private DailyDigest(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "DailyDigest";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum JobSchedulerSettings implements SystemPropertyEntryInterface
    {
        REPUTATION_DAILY_GATHER_AND_PROCESS_ENABLED(new SystemPropertyEntryBoolean("ReputationDailyGatherAndProcessEnabled", true)),
        DAILY_DIGEST_FOR_USERS_BEING_FOLLOWED_ENABLED(new SystemPropertyEntryBoolean("DailyDigestForUsersBeingFollowedEnabled", true));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private JobSchedulerSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "JobSchedulerSettings";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum JedisPoolConfig implements SystemPropertyEntryInterface
    {
        JEDIS_POOL_SOCKET_TIMEOUT(new SystemPropertyEntryInteger("JedisPoolSocketTimeout", 0)),
        JEDIS_NON_POOL_SOCKET_TIMEOUT(new SystemPropertyEntryInteger("JedisNonPoolSocketTimeout", 30000)),
        JEDIS_POOL_ENABLED(new SystemPropertyEntryBoolean("JedisPoolEnabled", true)),
        PROPS_REFRESH_INTERVAL_SECS(new SystemPropertyEntryInteger("PropsRefreshIntervalSeconds", 600)),
        PROPS_LOG_ENABLED(new SystemPropertyEntryBoolean("PropsLogEnabled", true)),
        BLOCK_WHEN_EXHAUSTED(new SystemPropertyEntryBoolean("BlockWhenExhausted", true)),
        MAX_TOTAL(new SystemPropertyEntryInteger("MaxTotal", 200)),
        MAX_IDLE(new SystemPropertyEntryInteger("MaxIdle", 100)),
        MIN_IDLE(new SystemPropertyEntryInteger("MinIdle", 1)),
        TEST_ON_BORROW(new SystemPropertyEntryBoolean("TestOnBorrow", true)),
        TEST_ON_RETURN(new SystemPropertyEntryBoolean("TestOnReturn", false)),
        TEST_WHILE_IDLE(new SystemPropertyEntryBoolean("TestWhileIdle", true)),
        NUM_TESTS_PER_EVICTION_RUN(new SystemPropertyEntryInteger("NumTestsPerEvictionRun", 200)),
        MIN_EVICTABLE_IDLE_TIME_MILLIS(new SystemPropertyEntryLong("MinEvictableIdleTimeMillis", 60000L)),
        TIME_BETWEEN_EVICTION_RUN_MILLIS(new SystemPropertyEntryLong("TimeBetweenEvictionRunsMillis", 30000L)),
        MAX_WAIT(new SystemPropertyEntryLong("maxWait", 3000L));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private JedisPoolConfig(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "JedisPoolConfig";
        }

        public static class Cache {
            public static LazyLoader<Boolean> enabled = new LazyLoader<Boolean>("JEDIS_POOL_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(JEDIS_POOL_ENABLED);
                }
            };
            public static LazyLoader<Integer> poolSocketTimeout = new LazyLoader<Integer>("JEDIS_POOL_SOCKET_TIMEOUT", 60000L){

                @Override
                protected Integer fetchValue() throws Exception {
                    return SystemProperty.getInt(JEDIS_POOL_SOCKET_TIMEOUT);
                }
            };
            public static LazyLoader<Integer> nonPoolSocketTimeout = new LazyLoader<Integer>("JEDIS_NON_POOL_SOCKET_TIMEOUT", 60000L){

                @Override
                protected Integer fetchValue() throws Exception {
                    return SystemProperty.getInt(JEDIS_NON_POOL_SOCKET_TIMEOUT);
                }
            };
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Mig33Voucher_Issuance implements SystemPropertyEntryInterface
    {
        ENABLE_VOUCHER_ISSUANCE_CHECK(new SystemPropertyEntryBoolean("EnableVoucherIssuanceCheck", false)),
        ENABLE_USER_TYPE_CHECK(new SystemPropertyEntryBoolean("EnableUserTypeCheck", true)),
        USER_TYPES_ALLOWED_TO_ISSUE(new SystemPropertyEntryIntegerArray("UserTypesAllowedToIssue", new int[]{UserData.TypeEnum.MIG33_TOP_MERCHANT.value()})),
        DISALLOWED_USER_TYPE_ERROR_MESSAGE(new SystemPropertyEntryString("DisallowedUserTypeErrorMessage", "##user.type.label## users are not allowed to create vouchers.")),
        ENABLE_MIG_LEVEL_CHECK(new SystemPropertyEntryBoolean("EnableMigLevelCheck", true)),
        SKIP_FETCH_CACHED_REPUTATION_SCORE(new SystemPropertyEntryBoolean("SkipFetchCachedReputationScore", true)),
        MIN_MIG_LEVEL(new SystemPropertyEntryInteger("MinMigLevel", 10)),
        INSUFFICIENT_MIG_LEVEL_ERROR_MESSAGE(new SystemPropertyEntryString("InsufficientMigLevelErrorMessage", "Your mig level has to be at least ##min.miglevel## before you are allowed to create vouchers."));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Mig33Voucher_Issuance(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Mig33Voucher:Issuance";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Mig33Voucher_Redemption implements SystemPropertyEntryInterface
    {
        ALLOW_EMAIL_VERIFIED_USER(new SystemPropertyEntryBoolean("AllowEmailVerifiedUser", true));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Mig33Voucher_Redemption(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Mig33Voucher:Redemption";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Mig33Voucher implements SystemPropertyEntryInterface
    {
        CREATE_BATCH_ENABLED(new SystemPropertyEntryBoolean("CreateBatchEnabled", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private Mig33Voucher(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "Mig33Voucher";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum DAOSettings implements SystemPropertyEntryInterface
    {
        ENABLED_GUARDSET_DAO(new SystemPropertyEntryBoolean("EnabledGuardsetDao", false)),
        ENABLED_USERDATA_DAO(new SystemPropertyEntryBoolean("EnabledUserDataDao", false)),
        ENABLED_EMOANDSTICKER_DAO(new SystemPropertyEntryBoolean("EnabledEmoAndStickerDao", false)),
        ENABLED_GROUP_DAO(new SystemPropertyEntryBoolean("EnabledGroupDao", false)),
        ENABLED_CHATROOM_DAO(new SystemPropertyEntryBoolean("EnabledChatRoomDao", false)),
        ENABLED_BOT_DAO(new SystemPropertyEntryBoolean("EnabledBotDao", false)),
        ENABLED_MESSAGE_DAO(new SystemPropertyEntryBoolean("EnabledMessageDao", false)),
        ENABLED_EMAIL_DAO(new SystemPropertyEntryBoolean("EnabledEmailDao", false)),
        ENABLED_EJB_NODE_GETUSERDATA(new SystemPropertyEntryBoolean("EnabledEJBNodeGetUserData", true)),
        ENABLED_EJB_NODE_GETBCL(new SystemPropertyEntryBoolean("EnabledEJBNodeGetBCL", true)),
        ENABLED_EJB_NODE_GETUSERSETTINGS(new SystemPropertyEntryBoolean("EnabledEJBNodeGetUserSettings", true)),
        ENABLED_EJB_NODE_GETGROUPLIST(new SystemPropertyEntryBoolean("EnabledEJBNodeGetGroupList", true)),
        ENABLED_EJB_NODE_GECONTACTLIST(new SystemPropertyEntryBoolean("EnabledEJBNodeGetContactist", true)),
        ENABLED_EJB_NODE_ASSIGNDPSM(new SystemPropertyEntryBoolean("EnabledEJBNodeAssignDPSM", true)),
        ENABLED_EJB_NODE_GETUSERID(new SystemPropertyEntryBoolean("EnabledEJBNodeGetUserID", true)),
        ENABLED_EJB_NODE_GETUSERNAME(new SystemPropertyEntryBoolean("EnabledEJBNodeGetUserName", true)),
        ENABLED_EJB_NODE_GETUSERREPUTATIONSCOREANDLEVEL(new SystemPropertyEntryBoolean("EnabledEJBNodeGetUserReputationAndLevel", true)),
        ENABLED_EJB_NODE_GETREPUTATIONLEVELDATA(new SystemPropertyEntryBoolean("EnabledEJBNodeGetReputationLevelData", true)),
        ENABLED_EJB_NODE_GETBASICMERCHANTDETAILS(new SystemPropertyEntryBoolean("EnabledEJBNodeGetBasicMerchatDetails", true)),
        ENABLED_EJB_NODE_GETGROUPDATA(new SystemPropertyEntryBoolean("EnabledEJBNodeGetGroupData", true)),
        ENABLED_EJB_NODE_GETINFOTEXT(new SystemPropertyEntryBoolean("EnabledEJBNodeGetInfoText", true)),
        ENABLED_EJB_NODE_ISUSERINMIGBOACCESSLIST(new SystemPropertyEntryBoolean("EnabledEJBNodeIsUserInMigboAccessList", true)),
        ENABLED_EJB_NODE_GETGROUPMEMBER(new SystemPropertyEntryBoolean("EnabledEJBNodeGetGroupMember", true)),
        ENABLED_EJB_NODE_ISUSERBLACKLISTEDINGROUP(new SystemPropertyEntryBoolean("EnabledEJBNodeIsUserBlackListedInGroup", true)),
        ENABLED_EJB_NODE_GETACCOUNTBALANCE(new SystemPropertyEntryBoolean("EnabledEJBNodeGetAccountBalance", true)),
        ENABLED_EJB_NODE_GETMODERATORUSERNAMES(new SystemPropertyEntryBoolean("EnabledEJBNodeGetModeratorUserNames", true)),
        ENABLED_EJB_NODE_GETGROUPCHATROOMS(new SystemPropertyEntryBoolean("EnabledEJBNodeGetGroupChatrooms", true)),
        ENABLED_EJB_NODE_GETMINCLIENTVERSIONFORACCESS(new SystemPropertyEntryBoolean("EnabledEJBNodeGetMinimumClientVersionForAccess", true)),
        ENABLED_EJB_NODE_GETCHATROOMNAMESPERCATEGORY(new SystemPropertyEntryBoolean("EnabledEJBNodeGetChatroomNamesPerCategory", true)),
        ENABLED_EJB_NODE_GETCHATROOMS(new SystemPropertyEntryBoolean("EnabledEJBNodeGetChatroomNames", true)),
        ENABLED_EJB_NODE_GETFAVCHATROOMS(new SystemPropertyEntryBoolean("EnabledEJBNodeGetFavouriteChatRooms", true)),
        ENABLED_EJB_NODE_GETRECENTCHATROOMS(new SystemPropertyEntryBoolean("EnabledEJBNodeGetRecentChatRooms", true)),
        ENABLED_EJB_NODE_GETCHATROOM(new SystemPropertyEntryBoolean("EnabledEJBNodeGetChatRoom", true)),
        ENABLED_EJB_NODE_GETBOT(new SystemPropertyEntryBoolean("EnabledEJBNodeGetBot", true)),
        ENABLED_EJB_NODE_GETOPTIMALEMOTICONHEIGHT(new SystemPropertyEntryBoolean("EnabledEJBNodeGetOptimalEmoticonHeight", true)),
        ENABLED_EJB_NODE_GETSIMPLECHATROOM(new SystemPropertyEntryBoolean("EnabledEJBNodeGetSimpleChatRoom", true)),
        DEFAULT_GETREPUTATIONDATA_USING_CACHE(new SystemPropertyEntryBoolean("DefaultGetGroupChatRooms", false)),
        EMOTICON_PACKS_REFRESH_PERIOD_IN_MS(new SystemPropertyEntryLong("EmoticonPacksRefreshPeriodInMS", 3600000L)),
        EMOTICONS_REFRESH_PERIOD_IN_MS(new SystemPropertyEntryLong("EmoticonsRefreshPeriodInMS", 3600000L)),
        ENABLED_CHAIN_ON_IF_SHOULD_NOT_REACH_HERE_HAPPENS(new SystemPropertyEntryBoolean("EnableChainOnIfShouldNotReachHereHappens", true)),
        BANNED_USERNAMES_PATTERN(new SystemPropertyEntryString("BannedUsernamesPattern", "admin|owner|operator|mig33|miq33|migg|pcgan|crazygrape|ksandy|mig.me|migme|miq.me|miq.me|miqme|loadtest"));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private DAOSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "DAOSettings";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum SessionCacheSettings implements SystemPropertyEntryInterface
    {
        USE_REPUTATION_SERVICE_FOR_SCORE_RETRIEVAL(new SystemPropertyEntryBoolean("UseReputationServiceForScoreRetrieval", true)),
        IPV6_SUPPORTED(new SystemPropertyEntryBoolean("IPV6Supported", true));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private SessionCacheSettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "SessionCacheSettings";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum GatewaySettings implements SystemPropertyEntryInterface
    {
        DEFAULT_REQUEST_RATELIMIT(new SystemPropertyEntryString("DefaultFusionRequestPktRateLimitExpr", "10/1M")),
        INSTRUMENT_DISCARDED_PACKETS(new SystemPropertyEntryBoolean("InstrumentDiscardedPackets", false)),
        INSTRUMENT_BLOCKED_PACKETS(new SystemPropertyEntryBoolean("InstrumentBlockedPackets", false)),
        INSTRUMENT_PACKETS(new SystemPropertyEntryBoolean("InstrumentPackets", true)),
        DEFAULT_RESPONSE_DELAY(new SystemPropertyEntryLong("DefaultFusionPacketDelayInMillis", 5000L)),
        BLOCKED_FUSION_PACKETS(new SystemPropertyEntryStringArray("BlockedFusionPackets", new String[0])),
        DELAYED_FUSION_PACKETS(new SystemPropertyEntryStringArray("DelayedFusionPackets", new String[0])),
        DISCARDED_FUSION_PACKETS(new SystemPropertyEntryStringArray("DiscardedFusionPackets", new String[0])),
        REQUESTS_TO_RATE_LIMIT(new SystemPropertyEntryShortArray("RequestsToRateLimit", new short[]{202, 500, 703, 704, 751, 752, 900, 564})),
        RATELIMITED_FUSION_PACKETS(new SystemPropertyEntryStringArray("RateLimitedFusionPackets", new String[0])),
        RATELIMITED_SUSPENDABLE_FUSION_PACKETS(new SystemPropertyEntryStringArray("RateLimitedSuspendableFusionPackets", new String[0])),
        RATELIMITED_FUSION_PACKETS_BY_CHATROOM(new SystemPropertyEntryStringArray("RateLimitedFusionPacketsByChatRoom", new String[0])),
        RATELIMITED_SUSPENDABLE_FUSION_PACKETS_BY_CHATROOM(new SystemPropertyEntryStringArray("RateLimitedSuspendableFusionPacketsByChatRoom", new String[0])),
        RATELIMIT_BY_CHATROOM_AND_REMOTEADDRESS(new SystemPropertyEntryBoolean("RateLimitByChatroomNameAndRemoteAddress", false)),
        RATELIMIT_BY_REMOTEADDRESS_WHITELIST(new SystemPropertyEntryStringArray("RateLimitByRemoteAddressWhiteList", new String[0])),
        DEPRECATED_PACKETS(new SystemPropertyEntryStringArray("DeprecatedPackets", new String[0])),
        MAX_MIGLEVEL_FOR_FUSION_PACKETS_RATELIMIT_BY_CHATROOM(new SystemPropertyEntryInteger("MaxMigLevelForFusionPacketRateLimitByChatroom", Integer.MAX_VALUE)),
        MAX_MIGLEVEL_FOR_FUSION_PACKETS_RATELIMIT(new SystemPropertyEntryInteger("MaxMigLevelForFusionPacketRateLimit", Integer.MAX_VALUE)),
        AJAX_CONSIDERED_ABOVE_MIDLET_VERSION_CHECKS(new SystemPropertyEntryBoolean("AjaxConsideredAboveMidletVersionChecks", false)),
        USE_GUARDSET_FOR_CONTACT_REQUEST_ACCEPTED(new SystemPropertyEntryBoolean("UseGuardsetForContactRequestAccepted", false)),
        CAPTCHA_IP_BLACKLIST(new SystemPropertyEntryStringArray("CaptchaIPBlacklist", new String[0])),
        GENERIC_NATIVECAPTCHA_ENABLED(new SystemPropertyEntryBoolean("GenericNativeCaptchaEnabled", false)),
        INVITEFRIEND_CAPTCHA_ENABLED(new SystemPropertyEntryBoolean("InviteFriendCaptchaEnabled", false)),
        INVITEFRIEND_CAPTCHA_ODDS(new SystemPropertyEntryDouble("InviteFriendCaptchaOdds", 0.5)),
        INVITEFRIEND_CAPTCHAFALLBACK_URL(new SystemPropertyEntryString("InviteFriendCaptchaFallbackUrl", "http://mig.me/sites/%1/invite/refer_friend")),
        INVITEFRIEND_CAPTCHAFALLBACK_MESSAGE(new SystemPropertyEntryString("InviteFriendCaptchaFallbackMessage", "Invitations on your version of migme have been discontinued. Please upgrade at http://m.mig.me.")),
        INVITEFRIEND_PAGELETFALLBACK_MESSAGE(new SystemPropertyEntryString("InviteFriendPageletFallbackMessage", "Invitations on your version of migme have been discontinued. Please upgrade at http://m.mig.me.")),
        PACKET_DISABLED_MESSAGE(new SystemPropertyEntryString("PacketDisabledMessage", "The feature you've requested has been disabled for your version of mig33. Please upgrade at http://m.mig.me.")),
        BLOCKED_PACKETS_UPON_SLEEPMODE(new SystemPropertyEntryShortArray("BlockedPacketsUponSleepMode", new short[]{422, 404, 421, 720})),
        PKT_GET_STICKER_PACK_MAX_PACKIDS_LENGTH(new SystemPropertyEntryInteger("PktGetStickerPackMaxPackIDSLength", Short.MAX_VALUE)),
        ALWAYS_SEND_PKT_GET_EMOTICONS_COMPLETE(new SystemPropertyEntryBoolean("AlwaysSendPktGetEmoticonsComplete", true)),
        LOG_EID_DECRYPTION_FAILED_REMOTE_ADDRESS_ENABLED(new SystemPropertyEntryBoolean("LogEidDecryptionFailedRemoteAddressEnabled", false)),
        TRACK_CONNECTIONS_PER_REMOTE_IP_ENABLED(new SystemPropertyEntryBoolean("TrackConnectionsPerRemoteIPEnabled", false)),
        PT66915380_ENABLE_CONNECTION_TRACKING(new SystemPropertyEntryBoolean("PT66915380EnableConnectionTracking", true)),
        DISCONNECT_ON_SESSION_FAILURE_ENABLED(new SystemPropertyEntryBoolean("DisconnectOnSessionFailureEnabled", true)),
        MAX_SESSION_TOUCH_FAILURES(new SystemPropertyEntryInteger("MaxSessionTouchFailures", 5)),
        OPEN_URL_DETAILED_EXCEPTION_LOGGING(new SystemPropertyEntryBoolean("OpenUrlDetailedExceptionLogging", true)),
        OPEN_URL_STATS_ENABLED(new SystemPropertyEntryBoolean("OpenUrlStatsEnabled", false)),
        ASYNC_PUT_MESSAGE_ENABLED(new SystemPropertyEntryBoolean("AsyncPutMessageEnabled", false)),
        ASYNC_PUT_MESSAGE_TIMEOUT_MILLIS(new SystemPropertyEntryLong("AsyncPutMessageTimeoutMillis", 60000L)),
        LOADTEST_USERS_RATE_LIMIT_BYPASS_ENABLED(new SystemPropertyEntryBoolean("LoadTestUsersRateLimitBypassEnabled", false));

        private SystemPropertyEntry<?> value;
        private static String NS;

        private GatewaySettings(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return SystemPropertyEntities.getNameWithNamespace(NS, this.value.getName());
        }

        static {
            NS = "GatewaySettings";
        }

        public static class Cache {
            public static LazyLoader<Boolean> asyncPutMessageEnabled = new LazyLoader<Boolean>("ASYNC_PUT_MESSAGE_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(ASYNC_PUT_MESSAGE_ENABLED);
                }
            };
            public static LazyLoader<Long> asyncPutMessageTimeoutMillis = new LazyLoader<Long>("ASYNC_PUT_MESSAGE_TIMEOUT_MILLIS", 60000L){

                @Override
                protected Long fetchValue() throws Exception {
                    return SystemProperty.getLong(ASYNC_PUT_MESSAGE_TIMEOUT_MILLIS);
                }
            };
            public static LazyLoader<Boolean> loadTestUsersRateLimitBypassEnabled = new LazyLoader<Boolean>("LOADTEST_USERS_RATELIMIT_BYPASS_ENABLED", 60000L){

                @Override
                protected Boolean fetchValue() throws Exception {
                    return SystemProperty.getBool(LOADTEST_USERS_RATE_LIMIT_BYPASS_ENABLED);
                }
            };
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Default implements SystemPropertyEntryInterface
    {
        SSO_LOGIN_ENABLED(new SystemPropertyEntryBoolean("SSOLoginEnabled", false)),
        ALLOWED_VERIFICATION_WINDOW_IN_HOURS(new SystemPropertyEntryInteger("AllowedVerificationWindowInHours", 96)),
        UNAUTH_USER_LOGIN_BLOCK_ENABLED(new SystemPropertyEntryBoolean("UnauthUserLoginBlockEnabled", false)),
        UNAUTH_USER_ALLOW_LOGIN_IN_DAYS(new SystemPropertyEntryInteger("UnauthUserAllowLoginInDays", 90)),
        UNAUTH_USER_LOGIN_BLOCK_MESSAGE(new SystemPropertyEntryString("UnauthUserLoginBlockMessage", "Authenticate your account at http://m.mig.me/auth by stating your USER ID, MOBILE NO & COUNTRY")),
        EMAIL_VERIFICATION_TOKEN_EXPIRY_IN_SECONDS(new SystemPropertyEntryInteger("ExternalEmailVerificationExpirationInSeconds", 604800)),
        EMAIL_VERIFICATION_WITHOUT_USERNAME_ENABLED(new SystemPropertyEntryBoolean("EmailVerificationWithoutUsernameEnabled", false)),
        MERCHANT_REGISTRATION_NEW_USER_ENABLED(new SystemPropertyEntryBoolean("NewUserMerchantRegistrationEnabled", false)),
        MERCHANT_REGISTRATION_WITHOUT_MOBILE_ENABLED(new SystemPropertyEntryBoolean("MerchantRegistrationWithoutMobileEnabled", false)),
        NOTIFICATIONS_COUNTER_ON_LOGIN(new SystemPropertyEntryBoolean("NotificationsCounterOnLogin", false)),
        BLACKLISTED_EXTERNAL_MAIL_DOMAINS(new SystemPropertyEntryStringArray("BlacklistedExternalEmailDomains", new String[0])),
        MAX_ACCOUNTENTRY_PERIOD_BEFORE_ARCHIVAL_IN_DAYS(new SystemPropertyEntryInteger("MaxAccountEntryPeriodBeforeArchivalInDays", 120)),
        FREE_VIRTUAL_GIFTS_ENABLED(new SystemPropertyEntryBoolean("FreeVirtualGiftsEnabled", false)),
        EVENT_SYSTEM_ENABLED(new SystemPropertyEntryBoolean("EventSystemEnabled", true)),
        VIRTUALGIFT_ONEWAY_EVENT_TRIGGER(new SystemPropertyEntryBoolean("VirtualGiftOnewayTrigger", false)),
        EVENT_SYSTEM_WELCOME_MESSAGE(new SystemPropertyEntryString("EventSystemWelcomeMessage", "Please visit the miniblog to view the activities of your friends.")),
        CREATE_ACCOUNT_ENTRY_RESTRICTED_ALL(new SystemPropertyEntryBoolean("CreateAccountEntryRestrictedAll", false)),
        CREATE_ACCOUNT_ENTRY_RESTRICTION_NONTOPMERCHANT(new SystemPropertyEntryBoolean("CreateAccountEntryRestrictedNonTopMerchant", false)),
        CREATED_ACCOUNT_ENTRY_RESTRICTED_TYPES(new SystemPropertyEntryStringArray("CreateAccountEntryRestrictedTypes", new String[0])),
        MAX_CONCURRENT_SESSIONS_PER_USER(new SystemPropertyEntryInteger("MaxConcurrentSessionsPerUser", 100)),
        MAX_CONCURRENT_SESSIONS_PER_ADMINUSER(new SystemPropertyEntryInteger("MaxConcurrentSessionsPerAdminUser", 100)),
        MAX_CONCURRENT_SESSIONS_BREACHED_LOCKDOWN(new SystemPropertyEntryLong("MaxConcurrentSessionsBreachedLockdownPeriod", MemCachedKeySpaces.RateLimitKeySpace.MAX_CONCURRENT_SESSIONS_BREACHED.getCacheTime())),
        MAX_CONCURRENT_SESSIONS_BREACHED_LOCKDOWN_ENABLED(new SystemPropertyEntryBoolean("MaxConcurrentSessionsBreachedLockdownEnabled", false)),
        BCLPERSISTED_ENABLED(new SystemPropertyEntryBoolean("BclPersistedEnabled", false)),
        WHITELISTED_RECIPIENT_EMAIL_DOMAINS(new SystemPropertyEntryStringArray("WhitelistedRecipientEmailDomains", new String[]{"mig33.com"})),
        SUPPORT_EMAIL_ALIASES(new SystemPropertyEntryStringArray("SupportEmailAliases", new String[]{"contact@mig.me"})),
        DEFAULT_EMAIL_DOMAIN(new SystemPropertyEntryString("MailDomain", "mig33.com")),
        MAX_USERNAME_LENGTH(new SystemPropertyEntryInteger("MaxUsernameLength", 128)),
        REFERRAL_SUCCESS_RATIO_EVALUATION_WINDOW_IN_DAYS(new SystemPropertyEntryInteger("ReferralSuccessRatioEvaluationWindowInDays", 30)),
        RESET_REFERRAL_SUCCESS_RATIO_ON_CREDIT_AWARD(new SystemPropertyEntryBoolean("ResetReferralSuccessRatioOnCreditAward", true)),
        MIN_SYSTEMSMS_ID_FOR_30_DAY_SCAN(new SystemPropertyEntryInteger("MinSystemSMSIDFor30DayScan", 209000000)),
        MIN_ACCOUNTENTRY_ID_FOR_30_DAY_SCAN(new SystemPropertyEntryInteger("MinAccountEntryIDFor30DayScan", 1350000000)),
        MIN_MIDLET_VERSION_FOR_NATIVE_CAPTCHA(new SystemPropertyEntryInteger("MinMidletVersionForNativeCaptcha", 450)),
        USERPROFILE_URL(new SystemPropertyEntryString("UserProfileURL", "http://mig.me/sites/%1/profile/home?username=")),
        MAX_CHATROOM_BANS(new SystemPropertyEntryInteger("MaxChatRoomBans", 6)),
        CHATROOM_BANS_BEFORE_SUSPENSION(new SystemPropertyEntryInteger("ChatRoomBansBeforeSuspension", 3)),
        CHATROOM_BAN_ERROR_MESSAGE(new SystemPropertyEntryString("ChatRoomBanErrorMessage", "You are banned from chatrooms. Please try again later.")),
        SHARE_REDIS_DIRECTORY_SLAVE_CONNECTION_ENABLED(new SystemPropertyEntryBoolean("ShareRedisDirectorySlaveConnectionEnabled", false)),
        SHARE_REDIS_DIRECTORY_MASTER_CONNECTION_ENABLED(new SystemPropertyEntryBoolean("ShareRedisDirectoryMasterConnectionEnabled", false)),
        SHARE_REDIS_DIRECTORY_SLAVE_CONNECTION_WAIT_IN_MS(new SystemPropertyEntryLong("SharedRedisDirectorySlaveWaitInMs", 500L)),
        SHARE_REDIS_DIRECTORY_MASTER_CONNECTION_WAIT_IN_MS(new SystemPropertyEntryLong("SharedRedisDirectoryMasterWaitInMs", 500L)),
        SHARED_REDIS_CONNECTION_MAINTENANCE_TASK_RUN_INTERVAL_IN_MS(new SystemPropertyEntryLong("SharedRedisConnMaintenanceTaskRunIntervalInMs", 60000L)),
        VIEW_GROUP_MODERATORS_ENABLED(new SystemPropertyEntryBoolean("ViewGroupModeratorsEnabled", true)),
        VALIDATE_FUSION_MSG_RECIPIENT_USERNAME_CHARACTERS(new SystemPropertyEntryBoolean("ValidateFusionMsgRecipientUsernameCharacters", true)),
        MIG33_WEB_BASE_URL(new SystemPropertyEntryString("Mig33WebBaseURL", "http://www.mig33.com")),
        IMG_WEB_BASE_URL(new SystemPropertyEntryString("ImgWebBaseURL", "http://img.mig.me")),
        MIN_MIG_LEVEL_BEFORE_LOGIN_BAN(new SystemPropertyEntryInteger("minMigLevelBeforeLoginBan", 0)),
        MIN_MIG_LEVEL_BEFORE_LOGIN_BAN_LOG_ENABLED(new SystemPropertyEntryBoolean("minMigLevelBeforeLoginBanLogEnabled", false)),
        GMAIL_DOMAINS(new SystemPropertyEntryStringArray("GmailDomains", new String[]{"gmail.com", "googlemail.com"})),
        LOGIN_URL(new SystemPropertyEntryString("LoginUrl", "https://login.mig.me")),
        USERNAME_AS_SEARCH_KEYWORD_REGEX(new SystemPropertyEntryString("UsernameAsSearchKeywordRegEx", "^[a-zA-Z0-9\\.\\-_]{1,20}$")),
        DEFAULT_LANGUAGE(new SystemPropertyEntryString("DefaultLanguage", "ENG")),
        BANNEDPASSWORDS(new SystemPropertyEntryStringArray("BannedPasswords", new String[]{"\\.adgjm", "0123", "1234", "2345", "3456", "4567", "5678", "6789", "7890", "abcd", "xyz", "(\\w)\\1{3,}", "passwd", "password", "multi", "multy", "sandi", "laluan", "pasavarda", "katavuccollai", "abc123", "123abc", "(\\D\\d)\\1{1,}", "(\\d\\D)\\1{1,}"})),
        MIN_PASSWORD_LENGTH(new SystemPropertyEntryInteger("MinPasswordLength", 6)),
        MAX_PASSWORD_LENGTH(new SystemPropertyEntryInteger("MaxPasswordLength", 60)),
        SESSIONI_LOG_ICELOCALEXCEPTIONS_ENABLED(new SystemPropertyEntryBoolean("SessionILogIceLocalExceptionsEnabled", false)),
        REASON_FOR_DISCONNECTING_SPAMMER(new SystemPropertyEntryString("ReasonForDisconnectingSpammer", "")),
        LOG_USER_SUSPENSION(new SystemPropertyEntryBoolean("LogUserSuspension", true)),
        LOG_SUCCESSFUL_LOGINS(new SystemPropertyEntryBoolean("LogSuccessfulLogins", true)),
        CAPTCHA_REQUIRED_ENABLED(new SystemPropertyEntryBoolean("CaptchaRequiredEnabled", false)),
        FROM_EMAIL_ADDRESS(new SystemPropertyEntryString("FromEmailAddress", "donotreply@mig.me")),
        ICE_CONNECTION_STATS(new SystemPropertyEntryBoolean("IceConnectionStats", false)),
        ICE_THREAD_STATS(new SystemPropertyEntryBoolean("IceThreadStats", false)),
        ASYNC_GIFT_ALL_ENABLED(new SystemPropertyEntryBoolean("AsyncGiftAllEnabled", false)),
        VERIFICATION_EMAIL_TEMPLATE_ID(new SystemPropertyEntryInteger("VerificationEmailTemplateID", 92)),
        CHECK_AND_POPULATEBCL(new SystemPropertyEntryBoolean("CheckAndPopulateBCL", true)),
        ENABLE_LOOKUP_UNS_VIA_ICE(new SystemPropertyEntryBoolean("EnableLookupUnsViaIce", false));

        private SystemPropertyEntry<?> value;

        private Default(SystemPropertyEntry<?> value) {
            this.value = value;
        }

        @Override
        public SystemPropertyEntry<?> getEntry() {
            return this.value;
        }

        @Override
        public String getName() {
            return this.value.getName();
        }
    }

    private static class PropertyNamesSingletonHolder {
        public static final Map<String, SystemPropertyEntryInterface> PROPERTY_NAMES_TO_ENTRIES_MAP = SystemPropertyEntities.access$000();

        private PropertyNamesSingletonHolder() {
        }
    }

    public static interface SystemPropertyEntryInterfaceWithNamespace
    extends SystemPropertyEntryInterface {
        public String getNamespace();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface SystemPropertyEntryInterface {
        public SystemPropertyEntry<?> getEntry();

        public String getName();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SystemPropertyEntryDoubleArray
    extends SystemPropertyEntry<double[]> {
        public SystemPropertyEntryDoubleArray(String name, double[] defaultValue) {
            super(name, defaultValue);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SystemPropertyEntryIntegerArray
    extends SystemPropertyEntry<int[]> {
        public SystemPropertyEntryIntegerArray(String name, int[] defaultValue) {
            super(name, defaultValue);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SystemPropertyEntryShortArray
    extends SystemPropertyEntry<short[]> {
        public SystemPropertyEntryShortArray(String name, short[] defaultValue) {
            super(name, defaultValue);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SystemPropertyEntryStringArray
    extends SystemPropertyEntry<String[]> {
        private static String[] ZERO_LENGTH_STRING = new String[0];

        public SystemPropertyEntryStringArray(String name, String[] defaultValue) {
            super(name, defaultValue);
        }

        public SystemPropertyEntryStringArray(String name) {
            super(name, ZERO_LENGTH_STRING);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SystemPropertyEntryDouble
    extends SystemPropertyEntry<Double> {
        public SystemPropertyEntryDouble(String name, Double defaultValue) {
            super(name, defaultValue);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SystemPropertyEntryLong
    extends SystemPropertyEntry<Long> {
        public SystemPropertyEntryLong(String name, Long defaultValue) {
            super(name, defaultValue);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SystemPropertyEntryByte
    extends SystemPropertyEntry<Byte> {
        public SystemPropertyEntryByte(String name, Byte defaultValue) {
            super(name, defaultValue);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SystemPropertyEntryInteger
    extends SystemPropertyEntry<Integer> {
        public SystemPropertyEntryInteger(String name, Integer defaultValue) {
            super(name, defaultValue);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SystemPropertyEntryBoolean
    extends SystemPropertyEntry<Boolean> {
        public SystemPropertyEntryBoolean(String name, Boolean defaultValue) {
            super(name, defaultValue);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SystemPropertyEntryString
    extends SystemPropertyEntry<String> {
        public SystemPropertyEntryString(String name, String defaultValue) {
            super(name, defaultValue);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class SystemPropertyEntry<ValueType> {
        protected String name;
        protected ValueType defaultValue;

        public SystemPropertyEntry(String name, ValueType defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return this.name;
        }

        public ValueType getDefaultValue() {
            return this.defaultValue;
        }

        public void setDefaultValue(ValueType val) {
            this.defaultValue = val;
        }
    }
}

