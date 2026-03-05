/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 *  org.eclipse.swt.widgets.TreeItem
 */
package com.projectgoth.fusion.monitor;

import Ice.ObjectPrx;
import com.projectgoth.fusion.monitor.BaseStatsMonitor;
import com.projectgoth.fusion.monitor.Monitor;
import com.projectgoth.fusion.slice.BotHunterAdminPrx;
import com.projectgoth.fusion.slice.BotHunterAdminPrxHelper;
import com.projectgoth.fusion.slice.BotHunterStats;
import java.util.ArrayList;
import org.eclipse.swt.widgets.TreeItem;

public class BotHunterMonitor
extends BaseStatsMonitor {
    public BotHunterStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;
    private BotHunterAdminPrx botHunterAdminPrx;
    private int port = 0;
    private ArrayList<TreeItemHolder> allItems = new ArrayList();
    private String offlinePrompt;
    private String onlinePrompt;
    private TreeItemHolder statsIntervalSecondsItem;
    private TreeItemHolder packetsCapturedItem;
    private TreeItemHolder averageProcessingTimeItem;
    private TreeItemHolder packetsPerSecondItem;
    private TreeItemHolder ipsCachedItem;
    private TreeItemHolder portsCachedItem;
    private TreeItemHolder packetsCachedItem;
    private TreeItemHolder totalSequencePairsAnalyzedItem;
    private TreeItemHolder averageSequenceTransitionsItem;
    private TreeItemHolder sequenceSuspectPairsItem;
    private TreeItemHolder ratioSuspectPairsItem;
    private TreeItemHolder suspectIPsReportedItem;
    private TreeItemHolder suspectPortsReportedItem;
    private String serverType = "";

    public BotHunterMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.offlinePrompt = hostName + " (" + this.serverType + ":" + port + ") OFFLINE";
        this.onlinePrompt = hostName + " (" + this.serverType + ":" + port + ") ONLINE";
        this.statsIntervalSecondsItem = this.createItem("Stats collection interval/seconds: ");
        this.packetsCapturedItem = this.createItem("Packets captured: ");
        this.averageProcessingTimeItem = this.createItem("Average processing time/packet (microsecs): ");
        this.packetsPerSecondItem = this.createItem("Packets per second: ");
        this.ipsCachedItem = this.createItem("IPs cached: ");
        this.portsCachedItem = this.createItem("Ports cached: ");
        this.packetsCachedItem = this.createItem("Packets cached: ");
        this.totalSequencePairsAnalyzedItem = this.createItem("Total seq pairs analyzed: ");
        this.averageSequenceTransitionsItem = this.createItem("Average transitions/sequence analysis: ");
        this.sequenceSuspectPairsItem = this.createItem("Suspect socket pairs by sequence analysis: ");
        this.ratioSuspectPairsItem = this.createItem("Suspect socket pairs by ratio analysis (may undercount): ");
        this.suspectIPsReportedItem = this.createItem("Total suspects reported by IP: ");
        this.suspectPortsReportedItem = this.createItem("Total suspects reported by client port: ");
        String stringifiedProxy = "BotHunterAdmin:tcp -h " + hostName + " -p " + (port == null ? "9998" : port);
        ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
        this.botHunterAdminPrx = BotHunterAdminPrxHelper.uncheckedCast(basePrx);
    }

    private TreeItemHolder createItem(String prompt) {
        TreeItemHolder t = new TreeItemHolder(this.baseTreeItem, 0, prompt);
        this.allItems.add(t);
        return t;
    }

    public void getStats() {
        try {
            this.latestStats = this.botHunterAdminPrx.getStats();
            this.latestException = null;
        }
        catch (Exception e) {
            this.latestStats = null;
            this.latestException = e;
        }
        this.latestStatsLoaded = true;
    }

    public void run() {
        if (!this.latestStatsLoaded) {
            this.getStats();
        }
        this.updateWithLatestStats(this.latestStats, this.latestStats != null);
        if (this.latestStats == null) {
            for (TreeItemHolder item : this.allItems) {
                item.offline();
            }
        } else {
            this.statsIntervalSecondsItem.update(this.latestStats.statsIntervalSeconds);
            this.packetsCapturedItem.update(this.latestStats.packetsCaptured);
            this.averageProcessingTimeItem.update(this.latestStats.averageProcessingTimePerPacketMicrosec);
            this.packetsPerSecondItem.update(this.latestStats.packetsPerSecond);
            this.ipsCachedItem.update(this.latestStats.ipsCached);
            this.portsCachedItem.update(this.latestStats.portsCached);
            this.packetsCachedItem.update(this.latestStats.packetsCached);
            double averageSeqTransitions = (double)this.latestStats.totalSequenceTransitions / (double)this.latestStats.totalSequencePairsAnalyzed;
            this.totalSequencePairsAnalyzedItem.update(this.latestStats.totalSequencePairsAnalyzed);
            this.averageSequenceTransitionsItem.update(averageSeqTransitions);
            this.sequenceSuspectPairsItem.update(this.latestStats.sequenceSuspectPairs);
            this.ratioSuspectPairsItem.update(this.latestStats.ratioSuspectPairs);
            this.suspectIPsReportedItem.update(this.latestStats.suspectIPsReported);
            this.suspectPortsReportedItem.update(this.latestStats.suspectPortsReported);
        }
        this.latestStatsLoaded = false;
    }

    private class TreeItemHolder {
        private String prompt = "";
        private TreeItem ti;

        public TreeItemHolder(TreeItem parentItem, int style, String prompt) {
            this.prompt = prompt;
            this.ti = new TreeItem(parentItem, style);
            this.ti.setText(prompt);
        }

        public void update(String s) {
            this.ti.setText(this.prompt + s);
        }

        public void update(Number value) {
            this.update(value.toString());
        }

        public void offline() {
            this.update("Offline");
        }
    }
}

