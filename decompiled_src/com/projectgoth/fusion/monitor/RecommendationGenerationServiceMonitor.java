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
import com.projectgoth.fusion.monitor.TreeItemAndPrompt;
import com.projectgoth.fusion.slice.RecommendationGenerationServiceAdminPrx;
import com.projectgoth.fusion.slice.RecommendationGenerationServiceAdminPrxHelper;
import com.projectgoth.fusion.slice.RecommendationGenerationServiceStats;
import org.eclipse.swt.widgets.TreeItem;

public class RecommendationGenerationServiceMonitor
extends BaseStatsMonitor {
    private static final int DEFAULT_PORT = 24750;
    private final String offlinePrompt;
    private final String onlinePrompt;
    private final RecommendationGenerationServiceAdminPrx rgsAdminPrx;
    private final TreeItemAndPrompt totalJobsItem;
    private final TreeItemAndPrompt successfulJobsItem;
    private final TreeItemAndPrompt failedJobsItem;
    private final TreeItemAndPrompt failedJobsDueToAlreadyRunningItem;
    private final TreeItemAndPrompt totalRecommendationsGeneratedItem;
    private final TreeItemAndPrompt totalPipelinesUsedItem;
    private final TreeItemAndPrompt totalGenerationTimeSecondsItem;
    private final TreeItemAndPrompt shortestGenerationTimeSecondsItem;
    private final TreeItemAndPrompt longestGenerationTimeSecondsItem;
    private String serverType = "";
    public RecommendationGenerationServiceStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;

    public RecommendationGenerationServiceMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.offlinePrompt = hostName + " (" + this.serverType + ":" + port + ") OFFLINE";
        this.onlinePrompt = hostName + " (" + this.serverType + ":" + port + ") ONLINE";
        this.totalJobsItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Total jobs (including in-progress)");
        this.successfulJobsItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Successful jobs");
        this.failedJobsItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Failed jobs");
        this.failedJobsDueToAlreadyRunningItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Failed jobs due to already running");
        this.totalRecommendationsGeneratedItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Total recommendations generated");
        this.totalPipelinesUsedItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Total pipelines used");
        this.totalGenerationTimeSecondsItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Total generation time seconds");
        this.shortestGenerationTimeSecondsItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Shortest generation time seconds");
        this.longestGenerationTimeSecondsItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Longest generation time seconds");
        String stringifiedProxy = "RecommendationGenerationServiceAdmin:tcp -h " + hostName + " -p " + (port == null ? Integer.toString(24750) : port);
        ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
        this.rgsAdminPrx = RecommendationGenerationServiceAdminPrxHelper.uncheckedCast(basePrx);
    }

    public void getStats() {
        try {
            this.latestStats = this.rgsAdminPrx.getStats();
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
        if (null != this.latestStats) {
            this.totalJobsItem.update(this.latestStats.totalJobs);
            this.successfulJobsItem.update(this.latestStats.successfulJobs);
            this.failedJobsItem.update(this.latestStats.failedJobs);
            this.failedJobsDueToAlreadyRunningItem.update(this.latestStats.failedJobsDueToAlreadyRunning);
            this.totalRecommendationsGeneratedItem.update(this.latestStats.totalRecommendationsGenerated);
            this.totalPipelinesUsedItem.update(this.latestStats.totalPipelinesUsed);
            this.totalGenerationTimeSecondsItem.update(this.latestStats.totalGenerationTimeSeconds);
            this.shortestGenerationTimeSecondsItem.update(this.latestStats.shortestGenerationTimeSeconds);
            this.longestGenerationTimeSecondsItem.update(this.latestStats.longestGenerationTimeSeconds);
        }
        this.latestStatsLoaded = false;
    }
}

