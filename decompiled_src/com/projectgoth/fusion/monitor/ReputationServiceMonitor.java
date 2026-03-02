/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.ObjectPrx
 *  org.eclipse.swt.graphics.Color
 *  org.eclipse.swt.graphics.Device
 *  org.eclipse.swt.widgets.Display
 *  org.eclipse.swt.widgets.TreeItem
 */
package com.projectgoth.fusion.monitor;

import Ice.LocalException;
import Ice.ObjectPrx;
import com.projectgoth.fusion.common.PortRegistry;
import com.projectgoth.fusion.monitor.BaseStatsMonitor;
import com.projectgoth.fusion.monitor.Monitor;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ReputationServiceAdminPrx;
import com.projectgoth.fusion.slice.ReputationServiceAdminPrxHelper;
import com.projectgoth.fusion.slice.ReputationServiceStats;
import java.util.Date;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

public class ReputationServiceMonitor
extends BaseStatsMonitor {
    private ReputationServiceStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;
    private ReputationServiceAdminPrx reputationServiceAdminPrx = null;
    protected TreeItem lastTimeRunCompletedTreeItem = new TreeItem(this.baseTreeItem, 0);

    public ReputationServiceMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.port = port == null || port <= 0 ? PortRegistry.REPUTATION_SERVICE_ADMIN.getPort() : port.intValue();
    }

    private synchronized ReputationServiceAdminPrx getReputationServiceAdminProxy() {
        if (this.reputationServiceAdminPrx == null) {
            String stringifiedProxy = "ReputationServiceAdmin:tcp -h " + this.hostName + " -p " + this.port + " -t 2000";
            ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
            this.reputationServiceAdminPrx = ReputationServiceAdminPrxHelper.checkedCast(basePrx);
        }
        return this.reputationServiceAdminPrx;
    }

    public void getStats() {
        try {
            ReputationServiceAdminPrx localReputationServiceAdminPrx = this.getReputationServiceAdminProxy();
            if (localReputationServiceAdminPrx != null) {
                this.latestStats = localReputationServiceAdminPrx.getStats();
                this.latestException = null;
            }
        }
        catch (Exception e) {
            this.latestStats = null;
            this.latestException = e;
        }
        this.latestStatsLoaded = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        if (!this.latestStatsLoaded) {
            this.getStats();
        }
        this.latestStatsLoaded = false;
        if (this.latestException instanceof FusionException) {
            return;
        }
        if (this.latestException instanceof LocalException && this.isOnline) {
            this.isOnline = false;
            this.updateWithLatestStats(this.latestStats, this.isOnline);
            this.sendAlert("Connection to ReputationService on " + this.hostName + " failed");
        }
        try {
            block11: {
                try {
                    if (this.latestStats == null) break block11;
                    if (!this.isOnline) {
                        this.isOnline = true;
                        this.sendAlert("Connection to ReputationService on " + this.hostName + " restored");
                    }
                    this.updateWithLatestStats(this.latestStats, this.isOnline);
                    if (this.latestStats.processing) {
                        this.baseTreeItem.setForeground(new Color((Device)Display.getCurrent(), 0, 0, 200));
                        this.baseTreeItem.setText(this.baseTreeItem.getText() + " DAILY PROCESS RUNNING");
                    } else if (System.currentTimeMillis() - this.latestStats.lastTimeRunCompleted > 172800000L) {
                        this.baseTreeItem.setForeground(new Color((Device)Display.getCurrent(), 255, 110, 0));
                        this.baseTreeItem.setText(this.baseTreeItem.getText() + " (daily process haven't completed successfully in " + (System.currentTimeMillis() - this.latestStats.lastTimeRunCompleted) / 86400000L + " days)");
                    }
                    this.lastTimeRunCompletedTreeItem.setText("Last Time Run Completed Successfully: " + (this.latestStats.lastTimeRunCompleted == 0L ? "-" : new Date(this.latestStats.lastTimeRunCompleted)));
                }
                catch (Exception e) {
                    System.err.println("WARNING: Unable to save stats for the ReputationService on " + this.hostName);
                    e.printStackTrace();
                    Object var3_2 = null;
                }
            }
            Object var3_1 = null;
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            throw throwable;
        }
    }
}

