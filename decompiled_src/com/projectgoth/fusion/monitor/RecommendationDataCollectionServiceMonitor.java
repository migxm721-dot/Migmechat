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
import com.projectgoth.fusion.slice.RecommendationDataCollectionServiceAdminPrx;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServiceAdminPrxHelper;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServiceStats;
import org.eclipse.swt.widgets.TreeItem;

public class RecommendationDataCollectionServiceMonitor
extends BaseStatsMonitor {
    private static final int DEFAULT_PORT = 24650;
    private static final String NEWLINE = "\n";
    private final String offlinePrompt;
    private final String onlinePrompt;
    private final RecommendationDataCollectionServiceAdminPrx rdcsAdminPrx;
    private final TreeItemAndPrompt totalReceivedDataCountItem;
    private final TreeItemAndPrompt totalReceivedDataCountByDataTypeItem;
    private final TreeItemAndPrompt totalSuccessfullyProcessedDataCountItem;
    private final TreeItemAndPrompt totalSuccessfullyProcessedDataCountByDataTypeItem;
    private final TreeItemAndPrompt totalFailedProcessedDataCountItem;
    private final TreeItemAndPrompt totalFailedProcessedDataCountByErrorCauseCodeItem;
    private String serverType = "";
    public RecommendationDataCollectionServiceStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;

    public RecommendationDataCollectionServiceMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.offlinePrompt = hostName + " (" + this.serverType + ":" + port + ") OFFLINE";
        this.onlinePrompt = hostName + " (" + this.serverType + ":" + port + ") ONLINE";
        this.totalReceivedDataCountItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Total received data count");
        this.totalReceivedDataCountByDataTypeItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Total received data count by type");
        this.totalSuccessfullyProcessedDataCountItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Total successfully processed data count");
        this.totalSuccessfullyProcessedDataCountByDataTypeItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Total successfully processed data count by type");
        this.totalFailedProcessedDataCountItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Total failed processed data count");
        this.totalFailedProcessedDataCountByErrorCauseCodeItem = new TreeItemAndPrompt(this.baseTreeItem, 0, "Total failed processed data count by error cause code");
        String stringifiedProxy = "RecommendationDataCollectionServiceAdmin:tcp -h " + hostName + " -p " + (port == null ? Integer.toString(24650) : port);
        ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
        this.rdcsAdminPrx = RecommendationDataCollectionServiceAdminPrxHelper.uncheckedCast(basePrx);
    }

    public void getStats() {
        try {
            this.latestStats = this.rdcsAdminPrx.getStats();
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
            this.totalReceivedDataCountItem.update(this.latestStats.totalReceivedDataCount.value);
            StringBuilder receivedBDT = new StringBuilder();
            for (int type : this.latestStats.totalReceivedDataCountByDataType.keySet()) {
                receivedBDT.append(NEWLINE + type + ": " + this.latestStats.totalReceivedDataCountByDataType.get((Object)Integer.valueOf((int)type)).value);
            }
            this.totalReceivedDataCountByDataTypeItem.update(receivedBDT.toString());
            this.totalSuccessfullyProcessedDataCountItem.update(this.latestStats.totalSuccessfullyProcessedDataCount.value);
            StringBuilder procBDT = new StringBuilder();
            for (int type : this.latestStats.totalSuccessfullyProcessedDataCountByDataType.keySet()) {
                procBDT.append(NEWLINE + type + ": " + this.latestStats.totalSuccessfullyProcessedDataCountByDataType.get((Object)Integer.valueOf((int)type)).value);
            }
            this.totalSuccessfullyProcessedDataCountByDataTypeItem.update(procBDT.toString());
            this.totalFailedProcessedDataCountItem.update(this.latestStats.totalFailedProcessedDataCount.value);
            StringBuilder failedBECC = new StringBuilder();
            for (String error : this.latestStats.totalFailedProcessedDataCountByErrorCauseCode.keySet()) {
                failedBECC.append(NEWLINE + error + ": " + this.latestStats.totalFailedProcessedDataCountByErrorCauseCode.get((Object)error).value);
            }
            this.totalFailedProcessedDataCountByErrorCauseCodeItem.update(failedBECC.toString());
        }
        this.latestStatsLoaded = false;
    }
}

