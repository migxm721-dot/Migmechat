/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.HostUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.datagrid.DataGridFactory;
import com.projectgoth.fusion.emote.gift.GiftAsync;
import com.projectgoth.fusion.objectcache.ChatUserSessions;
import com.projectgoth.fusion.slice.AuthenticationServiceStats;
import com.projectgoth.fusion.slice.BaseServiceStats;
import com.projectgoth.fusion.slice.BlueLabelServiceStats;
import com.projectgoth.fusion.slice.BotServiceStats;
import com.projectgoth.fusion.slice.EventQueueWorkerServiceStats;
import com.projectgoth.fusion.slice.EventStoreStats;
import com.projectgoth.fusion.slice.EventSystemStats;
import com.projectgoth.fusion.slice.GatewayStats;
import com.projectgoth.fusion.slice.ImageServerStats;
import com.projectgoth.fusion.slice.JobSchedulingServiceStats;
import com.projectgoth.fusion.slice.MessageLoggerStats;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice.RegistryStats;
import com.projectgoth.fusion.slice.ReputationServiceStats;
import com.projectgoth.fusion.slice.SMSEngineStats;
import com.projectgoth.fusion.slice.SessionCacheStats;
import com.projectgoth.fusion.slice.UserNotificationServiceStats;
import com.projectgoth.fusion.stats.IceStats;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ServiceStatsFactory {
    public static BaseServiceStats getBaseServiceStats(String hostname, long startTime) {
        BaseServiceStats stats = ServiceStatsFactory.getBaseServiceStats(startTime);
        stats.hostname = hostname;
        return stats;
    }

    public static BaseServiceStats getBaseServiceStats(long startTime) {
        BaseServiceStats stats = new BaseServiceStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    private static BaseServiceStats initializeBaseServiceStats(BaseServiceStats stats, String hostname, long startTime) {
        return ServiceStatsFactory.initializeServiceStats(stats, hostname, startTime, true);
    }

    public static <TStats extends BaseServiceStats> TStats initializeServiceStats(TStats stats, String hostname, long startTime) {
        return ServiceStatsFactory.initializeServiceStats(stats, hostname, startTime, false);
    }

    public static <TStats extends BaseServiceStats> TStats initializeServiceStats(TStats stats, String hostname, long startTime, boolean includeIceStats) {
        stats.version = "@version@";
        stats.hostname = hostname;
        stats.uptime = System.currentTimeMillis() - startTime;
        stats.jvmTotalMemory = Runtime.getRuntime().totalMemory();
        stats.jvmFreeMemory = Runtime.getRuntime().freeMemory();
        stats.lastUpdatedTime = System.currentTimeMillis();
        if (includeIceStats && SystemProperty.getBool(SystemPropertyEntities.Default.ICE_CONNECTION_STATS)) {
            stats.izeRequestStatsEnabled = true;
            IceStats.getInstance().initializeRequestStats(stats);
        } else {
            stats.izeRequestStatsEnabled = false;
        }
        if (includeIceStats && SystemProperty.getBool(SystemPropertyEntities.Default.ICE_THREAD_STATS)) {
            stats.izeThreadStatsEnabled = true;
            IceStats.getInstance().initializeThreadStats(stats);
        } else {
            stats.izeThreadStatsEnabled = false;
        }
        return stats;
    }

    public static <TStats extends BaseServiceStats> TStats initializeServiceStats(TStats stats, long startTime) {
        String hostname = HostUtils.getHostname();
        return ServiceStatsFactory.initializeServiceStats(stats, hostname, startTime);
    }

    public static ObjectCacheStats getObjectCacheStats(String hostname, long startTime) {
        ObjectCacheStats stats = new ObjectCacheStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, hostname, startTime);
        if (SystemProperty.getBool(SystemPropertyEntities.DataGridSettings.ENABLED)) {
            stats.dataGridStats = DataGridFactory.getInstance().getGrid().getStats();
        }
        if (GiftAsync.GiftAsyncDataGrid.isInitialized()) {
            stats.dataGridStats = stats.dataGridStats + (stats.dataGridStats != null ? "\r\n" : "") + GiftAsync.getGiftGrid().getStats();
        }
        if (SystemProperty.getBool(SystemPropertyEntities.ChatUserSessionsSettings.RETRY_STATS_ENABLED)) {
            stats.totalChatUserSessionsIntermediateFails = ChatUserSessions.getTotalIntermediateFails();
            stats.totalChatUserSessionsFinalFails = ChatUserSessions.getTotalFinalFails();
        }
        return stats;
    }

    public static RegistryStats getRegistryStats(String hostname, long startTime) {
        RegistryStats stats = new RegistryStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, hostname, startTime);
        return stats;
    }

    public static GatewayStats getGatewayStats(long startTime) {
        GatewayStats stats = new GatewayStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static EventSystemStats getEventSystemStats(long startTime) {
        EventSystemStats stats = new EventSystemStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static EventStoreStats getEventStoreStats(long startTime) {
        EventStoreStats stats = new EventStoreStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static SessionCacheStats getSessionCacheStats(long startTime) {
        SessionCacheStats stats = new SessionCacheStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static MessageLoggerStats getMessageLoggerStats(long startTime) {
        MessageLoggerStats stats = new MessageLoggerStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static SMSEngineStats getSMEngineStats(long startTime) {
        SMSEngineStats stats = new SMSEngineStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static ImageServerStats getImageServerStats(long startTime) {
        ImageServerStats stats = new ImageServerStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static UserNotificationServiceStats getUserNotificationServiceStats(long startTime) {
        UserNotificationServiceStats stats = new UserNotificationServiceStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static JobSchedulingServiceStats getJobSchedulingServiceStats(long startTime) {
        JobSchedulingServiceStats stats = new JobSchedulingServiceStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static BlueLabelServiceStats getBlueLabelServiceStats(long startTime) {
        BlueLabelServiceStats stats = new BlueLabelServiceStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static AuthenticationServiceStats getAuthenticationServiceStats(long startTime) {
        AuthenticationServiceStats stats = new AuthenticationServiceStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static ReputationServiceStats getReputationServiceStats(long startTime) {
        ReputationServiceStats stats = new ReputationServiceStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static BotServiceStats getBotServiceStats(long startTime) {
        BotServiceStats stats = new BotServiceStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, null, startTime);
        return stats;
    }

    public static EventQueueWorkerServiceStats getEventQueueWorkerServiceStats(String hostname, long startTime) {
        EventQueueWorkerServiceStats stats = new EventQueueWorkerServiceStats();
        ServiceStatsFactory.initializeBaseServiceStats(stats, hostname, startTime);
        return stats;
    }
}

