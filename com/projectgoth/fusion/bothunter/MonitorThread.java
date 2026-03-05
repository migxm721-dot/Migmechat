/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jpcap.JpcapCaptor
 *  jpcap.NetworkInterface
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.bothunter;

import com.projectgoth.fusion.bothunter.AnalysisThread;
import com.projectgoth.fusion.bothunter.JpcapPacketSource;
import com.projectgoth.fusion.bothunter.LogPacketSource;
import com.projectgoth.fusion.bothunter.PacketDetails;
import com.projectgoth.fusion.bothunter.PacketSource;
import com.projectgoth.fusion.bothunter.PacketsPerIP;
import com.projectgoth.fusion.bothunter.Params;
import com.projectgoth.fusion.bothunter.StatsCollector;
import com.projectgoth.fusion.common.ConfigUtils;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import org.apache.log4j.Logger;

public class MonitorThread {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MonitorThread.class));
    private boolean debugEnabled;
    private String nicOrLog;
    private String pcapFilter;
    private int analysisThreads;
    private boolean integratedKick;
    private PacketSource packetSource;
    private ConcurrentHashMap<String, PacketsPerIP> ipBuckets = new ConcurrentHashMap();
    private int captureCount = 0;
    private long lTotalNanos = 0L;
    private long lStartTimeMillis;
    private long lLastStatsMs;

    public MonitorThread(String nicOrLog, String pcapFilter, int analysisThreads, boolean integratedKick) {
        log.info((Object)("java.library.path=" + System.getProperty("java.library.path")));
        this.debugEnabled = log.isDebugEnabled();
        this.nicOrLog = nicOrLog;
        this.pcapFilter = pcapFilter;
        this.analysisThreads = analysisThreads;
        this.integratedKick = integratedKick;
        try {
            File test = new File(nicOrLog);
            this.packetSource = test.exists() ? new LogPacketSource(nicOrLog) : new JpcapPacketSource(nicOrLog, pcapFilter);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void monitor() {
        try {
            this.traceParams();
            this.printNetworkIFs();
            this.monitorInner();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void traceParams() {
        log.info((Object)"");
        log.info((Object)"BotHunter starting with:");
        log.info((Object)("PacketSource=" + this.nicOrLog));
        log.info((Object)("--> using " + this.packetSource));
        log.info((Object)("PcapFilter=" + this.pcapFilter));
        log.info((Object)("AnalysisThreadCount=" + this.analysisThreads));
        log.info((Object)"");
        log.info((Object)("MinAnalysisPackets=" + Params.MIN_ANALYSIS_PACKETS_PER_SOCKET));
        log.info((Object)("AnalyseSequence=" + Params.SEQUENCE_ANALYSIS));
        log.info((Object)("Mitc=" + Params.MIN_INTERLEAVE_TRANSITIONS));
        log.info((Object)("AnalyseRatios=" + Params.RATIO_ANALYSIS));
        if (Params.RATIO_ANALYSIS) {
            log.info((Object)("AnalyseRatiosSensitivity=" + Params.ANALYSE_RATIOS_SENSITIVITY));
        }
        log.info((Object)"");
        log.info((Object)("AutoKick=" + Params.AUTOKICK));
        if (Params.AUTOKICK) {
            log.info((Object)("RegistryProxy=" + Params.REGISTRY_PROXY));
        }
        log.info((Object)("StatsInterval=" + Params.STATS_INTERVAL_SECONDS));
        log.info((Object)"");
        log.info((Object)("MaxPacketsPerSocket=" + Params.MAX_PACKETS_PER_SOCKET));
        log.info((Object)("ClientIpTimeout=" + Params.CLIENT_IP_TIMEOUT_SECS));
        log.info((Object)("ClientPortTimeout=" + Params.CLIENT_PORT_TIMEOUT_SECS));
        log.info((Object)("DuplicateReportInterval=" + Params.DUPLICATE_REPORT_INTERVAL_SECS));
        log.info((Object)"");
    }

    private void printNetworkIFs() {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        log.info((Object)"Network interfaces:");
        for (NetworkInterface d : devices) {
            log.info((Object)("  " + d.name + "(" + d.description + ")"));
        }
    }

    private void monitorInner() throws Exception {
        for (int i = 0; i < this.analysisThreads; ++i) {
            Thread at = new Thread(new AnalysisThread(i, this.ipBuckets, this.integratedKick));
            at.setPriority(1);
            at.start();
        }
        this.lStartTimeMillis = System.currentTimeMillis();
        this.lLastStatsMs = System.currentTimeMillis();
        do {
            PacketDetails pkt;
            long startNanos = System.nanoTime();
            try {
                pkt = this.packetSource.nextPacket();
            }
            catch (Exception e) {
                log.error((Object)e, (Throwable)e);
                pkt = null;
            }
            if (pkt == null) continue;
            PacketsPerIP ppip = this.ipBuckets.get(pkt.getClientIP());
            if (ppip == null) {
                ppip = new PacketsPerIP(pkt.getClientIP());
                this.ipBuckets.put(pkt.getClientIP(), ppip);
            }
            ppip.addPacket(pkt);
            this.lTotalNanos += System.nanoTime() - startNanos;
            ++this.captureCount;
            if ((System.currentTimeMillis() - this.lLastStatsMs) / 1000L < (long)Params.STATS_INTERVAL_SECONDS) continue;
            this.updatePacketCaptureStats();
            this.lLastStatsMs = System.currentTimeMillis();
        } while (!this.packetSource.finished());
    }

    private void updatePacketCaptureStats() {
        long lElapsedTime = System.currentTimeMillis() - this.lStartTimeMillis;
        double average = (double)this.lTotalNanos / (double)this.captureCount / 1000.0;
        double pps = (double)this.captureCount / ((double)lElapsedTime / 1000.0);
        long portCount = 0L;
        long packetCount = 0L;
        for (String ip : this.ipBuckets.keySet()) {
            PacketsPerIP bkt = this.ipBuckets.get(ip);
            int[] counts = bkt.getPortAndPacketCount();
            portCount += (long)counts[0];
            packetCount += (long)counts[1];
        }
        StatsCollector.getInstance().updatePacketCaptureStats(this.lTotalNanos, this.captureCount, average, pps, this.ipBuckets.keySet().size(), portCount, packetCount);
        if (this.debugEnabled) {
            log.debug((Object)("AVERAGE PROCESSING TIME PER PACKET (microsecs)=" + average));
            log.debug((Object)("packets captured=" + this.captureCount));
            log.debug((Object)("packets/sec = " + pps));
            log.debug((Object)("ipBuckets ip count=" + this.ipBuckets.keySet().size()));
            log.debug((Object)("ipBuckets total packet count=" + packetCount));
        }
    }
}

