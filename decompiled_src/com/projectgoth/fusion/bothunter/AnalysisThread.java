/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.bothunter;

import com.projectgoth.fusion.bothunter.BotHunterI;
import com.projectgoth.fusion.bothunter.ClientAddressToUsername;
import com.projectgoth.fusion.bothunter.KickThread;
import com.projectgoth.fusion.bothunter.PacketDetails;
import com.projectgoth.fusion.bothunter.PacketsPerClientSocket;
import com.projectgoth.fusion.bothunter.PacketsPerIP;
import com.projectgoth.fusion.bothunter.Params;
import com.projectgoth.fusion.bothunter.StatsCollector;
import com.projectgoth.fusion.bothunter.Suspect;
import com.projectgoth.fusion.bothunter.SuspectGroup;
import com.projectgoth.fusion.common.ConfigUtils;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AnalysisThread
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AnalysisThread.class));
    private StatsCollector statsCollectorInstance;
    private final boolean debugEnabled;
    private final int threadIndex;
    private Random gen = new Random(System.currentTimeMillis() * (long)this.hashCode());
    private ConcurrentHashMap<String, PacketsPerIP> ipBuckets;
    private final boolean integratedKick;
    private final double RATIO_ANALYSIS_RATIO_MIN;
    private final double RATIO_ANALYSIS_RATIO_MAX;

    public AnalysisThread(int threadIndex, ConcurrentHashMap<String, PacketsPerIP> ipBuckets, boolean integratedKick) {
        this.debugEnabled = log.isDebugEnabled();
        this.threadIndex = threadIndex;
        this.ipBuckets = ipBuckets;
        this.integratedKick = integratedKick;
        this.RATIO_ANALYSIS_RATIO_MIN = 1.0 - Params.ANALYSE_RATIOS_SENSITIVITY / 100.0;
        this.RATIO_ANALYSIS_RATIO_MAX = 1.0 + Params.ANALYSE_RATIOS_SENSITIVITY / 100.0;
    }

    @Override
    public void run() {
        log.info((Object)("Starting AnalysisThread " + this.threadIndex));
        log.debug((Object)("with RATIO_ANALYSIS_RATIO_MIN=" + this.RATIO_ANALYSIS_RATIO_MIN + " and RATIO_ANALYSIS_RATIO_MAX=" + this.RATIO_ANALYSIS_RATIO_MAX));
        this.statsCollectorInstance = StatsCollector.getInstance();
        while (true) {
            this.onePass();
            try {
                Thread.sleep(10L);
            }
            catch (Exception exception) {
            }
        }
    }

    private void onePass() {
        PacketsPerIP ppip;
        Set ips = this.ipBuckets.keySet();
        if (ips.size() == 0) {
            return;
        }
        do {
            int index = this.gen.nextInt(ips.size());
            String key = (String)ips.toArray()[index];
            ppip = this.ipBuckets.get(key);
            if (ppip == null) {
                ips = this.ipBuckets.keySet();
                continue;
            }
            try {
                if (this.debugEnabled) {
                    log.debug((Object)("Trying to analyze client IP " + ppip.getIP()));
                }
                ppip.setUnderAnalysis();
                if (this.debugEnabled) {
                    log.debug((Object)("Analyzing client IP " + ppip.getIP()));
                }
                ppip.doCleanupDuringAnalysis();
            }
            catch (PacketsPerIP.BeingAnalyzedException e) {
                ppip = null;
            }
            catch (PacketsPerIP.TimedOutException e) {
                this.ipBuckets.remove(key);
                ppip = null;
            }
        } while (ppip == null);
        PacketsPerIP ppipCopy = (PacketsPerIP)ppip.clone();
        this.findBots(ppipCopy);
        ppip.clearUnderAnalysis();
    }

    public void findBots(PacketsPerIP ppip) {
        String ip = ppip.getIP();
        Set<Integer> socketSet = ppip.getSockets();
        Integer[] sockets = new Integer[socketSet.size()];
        socketSet.toArray(sockets);
        HashSet<Suspect> uniqueSuspects = new HashSet<Suspect>();
        for (int i = 0; i < socketSet.size(); ++i) {
            for (int j = i + 1; j < socketSet.size(); ++j) {
                PacketsPerClientSocket iPackets = ppip.getPacketsPerClientSocket(sockets[i]);
                PacketsPerClientSocket jPackets = ppip.getPacketsPerClientSocket(sockets[j]);
                boolean suspect = this.isSocketPairSuspect(sockets[i], sockets[j], iPackets, jPackets);
                if (!suspect) continue;
                if (this.debugEnabled) {
                    log.debug((Object)("AnalysisThread: IP " + ip + " sockets " + sockets[i] + " and " + sockets[j] + " are suspect"));
                }
                Suspect suspectI = new Suspect(ip, sockets[i], iPackets);
                Suspect suspectJ = new Suspect(ip, sockets[j], jPackets);
                uniqueSuspects.add(suspectI);
                uniqueSuspects.add(suspectJ);
            }
        }
        if (uniqueSuspects.size() != 0) {
            Suspect[] uniquesArray = new Suspect[uniqueSuspects.size()];
            uniqueSuspects.toArray(uniquesArray);
            for (Suspect spt : uniquesArray) {
                String username = ClientAddressToUsername.getInstance().getUsername(spt);
                spt.setUsername(username);
            }
            int totalPorts = ppip.getPortAndPacketCount()[0];
            SuspectGroup group = new SuspectGroup(uniquesArray, totalPorts - uniquesArray.length);
            if (this.integratedKick) {
                KickThread.getInstance(true).addSuspects(group);
            } else {
                BotHunterI.addSuspects(group);
            }
            this.statsCollectorInstance.addSuspects(uniqueSuspects);
        }
    }

    private boolean isSocketPairSuspect(int socketI, int socketJ, PacketsPerClientSocket iPackets, PacketsPerClientSocket jPackets) {
        if (iPackets.size() < Params.MIN_ANALYSIS_PACKETS_PER_SOCKET) {
            return false;
        }
        if (jPackets.size() < Params.MIN_ANALYSIS_PACKETS_PER_SOCKET) {
            return false;
        }
        if (this.debugEnabled) {
            log.debug((Object)("AnalysisThread: comparing sockets " + socketI + "," + socketJ));
            log.debug((Object)("    with packet counts " + iPackets.size() + "," + jPackets.size()));
        }
        if (Params.SEQUENCE_ANALYSIS) {
            int ii = 0;
            int jj = 0;
            int transitionCount = 0;
            Long lastArrivalDiff = null;
            while (ii < iPackets.size() && jj < jPackets.size()) {
                long tsDiff;
                PacketDetails iPkt = (PacketDetails)iPackets.get(ii);
                PacketDetails jPkt = (PacketDetails)jPackets.get(jj);
                long arrivalDiff = jPkt.getArrivalTime() - iPkt.getArrivalTime();
                if (arrivalDiff * (tsDiff = jPkt.getTcpTimestamp() - iPkt.getTcpTimestamp()) < 0L) {
                    if (this.debugEnabled) {
                        log.debug((Object)("AnalysisThread: Sockets " + socketI + " and " + socketJ + " don't have consistently ordered timestamps so not suspect"));
                    }
                    return false;
                }
                if (lastArrivalDiff != null && arrivalDiff * lastArrivalDiff < 0L) {
                    ++transitionCount;
                }
                if (arrivalDiff > 0L) {
                    ++ii;
                } else {
                    ++jj;
                }
                lastArrivalDiff = arrivalDiff;
            }
            this.statsCollectorInstance.incrementTotalSequencePairsAnalyzed();
            this.statsCollectorInstance.incrementTotalSequenceTransitions(transitionCount);
            if (transitionCount < Params.MIN_INTERLEAVE_TRANSITIONS) {
                if (this.debugEnabled) {
                    log.debug((Object)("AnalysisThread: Sockets " + socketI + " and " + socketJ + " have consistently ordered timestamps but they're not interleaved, " + " so not suspect"));
                }
                return false;
            }
            if (this.debugEnabled) {
                log.debug((Object)("AnalysisThread: Sockets " + socketI + " and " + socketJ + " have consistently ordered interleaved timestamps, transitionCount=" + transitionCount));
            }
            this.statsCollectorInstance.incrementSequenceSuspectPairs();
        }
        if (Params.RATIO_ANALYSIS) {
            double jMean;
            double iMean = iPackets.getMeanTcpTimestampOverArrivalTime();
            if (iMean / (jMean = jPackets.getMeanTcpTimestampOverArrivalTime()) > this.RATIO_ANALYSIS_RATIO_MAX || iMean / jMean < this.RATIO_ANALYSIS_RATIO_MIN) {
                if (this.debugEnabled) {
                    log.debug((Object)("AnalysisThread: Sockets " + socketI + " and " + socketJ + " not suspect because their time ratio means are not sufficiently similar"));
                    log.debug((Object)("i ratio/j ratio=" + iMean / jMean));
                }
                return false;
            }
            if (this.debugEnabled) {
                log.debug((Object)("AnalysisThread: Sockets " + socketI + " and " + socketJ + " time ratio means:"));
                log.debug((Object)("  i ratio=" + iMean));
                log.debug((Object)("  j ratio=" + jMean));
                log.debug((Object)("  ratio of ratios=" + iMean / jMean));
            }
            this.statsCollectorInstance.incrementRatioSuspectPairs();
        }
        return true;
    }
}

