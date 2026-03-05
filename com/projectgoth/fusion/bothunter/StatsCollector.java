/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.bothunter;

import com.projectgoth.fusion.bothunter.Suspect;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.slice.BaseServiceStats;
import com.projectgoth.fusion.slice.BotHunterStats;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StatsCollector {
    private static StatsCollector instance;
    private long startTime;
    private BotHunterStats stats = new BotHunterStats();
    private final Object DUMMY_VALUE = new Object();
    private ConcurrentHashMap<String, Object> distinctClientIPs = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Object> distinctSuspects = new ConcurrentHashMap();
    private AtomicLong totalSequencePairsAnalyzed = new AtomicLong(0L);
    private AtomicLong totalSequenceTransitions = new AtomicLong(0L);
    private AtomicLong sequenceSuspectPairs = new AtomicLong(0L);
    private AtomicLong ratioSuspectPairs = new AtomicLong(0L);

    private StatsCollector() {
        this.startTime = System.currentTimeMillis();
    }

    public static synchronized StatsCollector getInstance() {
        if (instance == null) {
            instance = new StatsCollector();
        }
        return instance;
    }

    public void setInstanceStats(long statsIntervalSeconds) {
        this.stats.statsIntervalSeconds = statsIntervalSeconds;
    }

    public void updatePacketCaptureStats(long totalPacketProcessingNanos, long packetsCaptured, double averageProcessingTimePerPacketMicrosec, double packetsPerSecond, long ipsCached, long portsCached, long packetsCached) {
        this.stats.totalPacketProcessingNanos = totalPacketProcessingNanos;
        this.stats.packetsCaptured = packetsCaptured;
        this.stats.averageProcessingTimePerPacketMicrosec = averageProcessingTimePerPacketMicrosec;
        this.stats.packetsPerSecond = packetsPerSecond;
        this.stats.ipsCached = ipsCached;
        this.stats.portsCached = portsCached;
        this.stats.packetsCached = packetsCached;
    }

    public void addSuspects(Set<Suspect> suspects) {
        for (Suspect s : suspects) {
            this.distinctClientIPs.put(s.getClientIP(), this.DUMMY_VALUE);
            this.distinctSuspects.put(s.toString(), this.DUMMY_VALUE);
        }
    }

    public int getDistinctIPsCount() {
        return this.distinctClientIPs.keySet().size();
    }

    public int getDistinctSuspectsCount() {
        return this.distinctSuspects.keySet().size();
    }

    public void incrementTotalSequencePairsAnalyzed() {
        this.totalSequencePairsAnalyzed.incrementAndGet();
    }

    public void incrementTotalSequenceTransitions(long lDiff) {
        this.totalSequenceTransitions.addAndGet(lDiff);
    }

    public void incrementSequenceSuspectPairs() {
        this.sequenceSuspectPairs.incrementAndGet();
    }

    public void incrementRatioSuspectPairs() {
        this.ratioSuspectPairs.incrementAndGet();
    }

    public BotHunterStats getStats() {
        BaseServiceStats bss = ServiceStatsFactory.getBaseServiceStats(this.startTime);
        this.stats.jvmFreeMemory = bss.jvmFreeMemory;
        this.stats.jvmTotalMemory = bss.jvmTotalMemory;
        this.stats.uptime = bss.uptime;
        this.stats.totalSequencePairsAnalyzed = this.totalSequencePairsAnalyzed.get();
        this.stats.totalSequenceTransitions = this.totalSequenceTransitions.get();
        this.stats.sequenceSuspectPairs = this.sequenceSuspectPairs.get();
        this.stats.ratioSuspectPairs = this.ratioSuspectPairs.get();
        this.stats.suspectIPsReported = this.distinctClientIPs.keySet().size();
        this.stats.suspectPortsReported = this.distinctSuspects.keySet().size();
        return this.stats;
    }
}

