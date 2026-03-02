/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chat;

import com.projectgoth.fusion.chatsync.WallclockTime;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LazyStats;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;

public class CoreChatStats
extends LazyStats {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(CoreChatStats.class));
    private AtomicLong totalMessagePacketsReceived = new AtomicLong(0L);
    private WallclockTime messagePacketProcessRequestWallclockTime = new WallclockTime();
    private WallclockTime messagePacketProcessRequestWallclockTimePeak = new WallclockTime();
    private WallclockTime messagePacketProcessRequestWallclockTimeOffpeak = new WallclockTime();
    private WallclockTime sendFusionMessageWallclockTime = new WallclockTime();
    private WallclockTime sendFusionMessageWallclockTimePeak = new WallclockTime();
    private WallclockTime sendFusionMessageWallclockTimeOffpeak = new WallclockTime();

    protected boolean isStatsEnabled() {
        return SystemProperty.getBool(SystemPropertyEntities.CoreChatSettings.STATS_COLLECTION_ENABLED);
    }

    protected int getStatsIntervalMinutes() {
        return SystemProperty.getInt(SystemPropertyEntities.CoreChatSettings.STATS_COLLECTION_INTERVAL_MINUTES);
    }

    private CoreChatStats() {
    }

    public static CoreChatStats getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void incrementTotalMessagePacketsReceived() {
        if (this.isStatsEnabled()) {
            this.totalMessagePacketsReceived.incrementAndGet();
            this.logStatsPeriodically();
        }
    }

    public void addMessagePacketProcessRequestWallclockTime(long millis) {
        if (this.isStatsEnabled()) {
            this.messagePacketProcessRequestWallclockTime.addRequest(millis);
            if (this.isPeakTime()) {
                this.messagePacketProcessRequestWallclockTimePeak.addRequest(millis);
            } else {
                this.messagePacketProcessRequestWallclockTimeOffpeak.addRequest(millis);
            }
            this.logStatsPeriodically();
        }
    }

    public void addSendFusionMessageWallclockTime(long millis) {
        if (this.isStatsEnabled()) {
            this.sendFusionMessageWallclockTime.addRequest(millis);
            if (this.isPeakTime()) {
                this.sendFusionMessageWallclockTimePeak.addRequest(millis);
            } else {
                this.sendFusionMessageWallclockTimeOffpeak.addRequest(millis);
            }
            this.logStatsPeriodically();
        }
    }

    protected void doLog() {
        log.info((Object)"CoreChat: stats");
        log.info((Object)("CoreChat: total MESSAGE packets received=" + this.totalMessagePacketsReceived.get()));
        log.info((Object)("CoreChat: MESSAGE.processRequest wallclock time=" + this.messagePacketProcessRequestWallclockTime));
        log.info((Object)("CoreChat: MESSAGE.processRequest wallclock time (peak)=" + this.messagePacketProcessRequestWallclockTimePeak));
        log.info((Object)("CoreChat: MESSAGE.processRequest wallclock time (offpeak)=" + this.messagePacketProcessRequestWallclockTimeOffpeak));
        log.info((Object)("CoreChat: sendFusionMessage wallclock time=" + this.sendFusionMessageWallclockTime));
        log.info((Object)("CoreChat: sendFusionMessage wallclock time (peak)=" + this.sendFusionMessageWallclockTimePeak));
        log.info((Object)("CoreChat: sendFusionMessage wallclock time (offpeak)=" + this.sendFusionMessageWallclockTimeOffpeak));
    }

    private static class SingletonHolder {
        public static final CoreChatStats INSTANCE = new CoreChatStats();

        private SingletonHolder() {
        }
    }
}

