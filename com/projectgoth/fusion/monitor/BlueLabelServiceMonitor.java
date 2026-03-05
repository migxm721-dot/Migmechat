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
import com.projectgoth.fusion.slice.BlueLabelServiceAdminPrx;
import com.projectgoth.fusion.slice.BlueLabelServiceAdminPrxHelper;
import com.projectgoth.fusion.slice.BlueLabelServiceStats;
import com.projectgoth.fusion.slice.FusionException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

public class BlueLabelServiceMonitor
extends BaseStatsMonitor {
    private BlueLabelServiceStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;
    private BlueLabelServiceAdminPrx blueLabelServiceAdminPrx = null;
    protected TreeItem recentErrorsTreeItem = new TreeItem(this.baseTreeItem, 0);

    public BlueLabelServiceMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.recentErrorsTreeItem.setText("Recent Errors: -");
        this.port = port == null || port <= 0 ? PortRegistry.BLUE_LABEL_SERVICE_ADMIN.getPort() : port.intValue();
    }

    private synchronized BlueLabelServiceAdminPrx getBlueLabelServiceAdminProxy() {
        if (this.blueLabelServiceAdminPrx == null) {
            String stringifiedProxy = "BlueLabelServiceAdmin:tcp -h " + this.hostName + " -p " + this.port + " -t 2000";
            ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
            this.blueLabelServiceAdminPrx = BlueLabelServiceAdminPrxHelper.checkedCast(basePrx);
        }
        return this.blueLabelServiceAdminPrx;
    }

    public void getStats() {
        try {
            BlueLabelServiceAdminPrx localBlueLabelServiceAdminPrx = this.getBlueLabelServiceAdminProxy();
            if (localBlueLabelServiceAdminPrx != null) {
                this.latestStats = localBlueLabelServiceAdminPrx.getStats();
                this.latestException = null;
            }
        }
        catch (Exception e) {
            this.latestStats = null;
            this.latestException = e;
            e.printStackTrace();
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
            this.sendAlert("Connection to BlueLabelService on " + this.hostName + " failed");
        }
        try {
            block9: {
                try {
                    if (this.latestStats == null) break block9;
                    if (!this.isOnline) {
                        this.isOnline = true;
                        this.sendAlert("Connection to BlueLabelService on " + this.hostName + " restored");
                    }
                    this.updateWithLatestStats(this.latestStats, this.isOnline);
                    if (this.latestStats.recentErrors >= 3) {
                        this.baseTreeItem.setForeground(new Color((Device)Display.getCurrent(), 255, 110, 0));
                    }
                    this.recentErrorsTreeItem.setText("Recent Errors: " + this.latestStats.recentErrors);
                }
                catch (Exception e) {
                    System.err.println("WARNING: Unable to save stats for the BlueLabelService on " + this.hostName);
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

