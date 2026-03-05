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
import com.projectgoth.fusion.slice.JobSchedulingServiceAdminPrx;
import com.projectgoth.fusion.slice.JobSchedulingServiceAdminPrxHelper;
import com.projectgoth.fusion.slice.JobSchedulingServiceStats;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

public class JobSchedulingServiceMonitor
extends BaseStatsMonitor {
    private JobSchedulingServiceStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;
    private JobSchedulingServiceAdminPrx jobSchedulingServiceAdminPrx = null;
    protected TreeItem jobsTreeItem = new TreeItem(this.baseTreeItem, 0);
    protected String jobsTreeItemText = "Jobs Currently Running:";

    public JobSchedulingServiceMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.jobsTreeItem.setText(this.jobsTreeItemText);
        this.jobsTreeItem.setExpanded(true);
        this.port = port == null || port <= 0 ? PortRegistry.JOB_SCHEDULING_SERVICE_ADMIN.getPort() : port.intValue();
    }

    private synchronized JobSchedulingServiceAdminPrx getJobSchedulingServiceAdminProxy() {
        if (this.jobSchedulingServiceAdminPrx == null) {
            String stringifiedProxy = "JobSchedulingServiceAdmin:tcp -h " + this.hostName + " -p " + this.port + " -t 2000";
            ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
            this.jobSchedulingServiceAdminPrx = JobSchedulingServiceAdminPrxHelper.checkedCast(basePrx);
        }
        return this.jobSchedulingServiceAdminPrx;
    }

    public void getStats() {
        try {
            JobSchedulingServiceAdminPrx localJobSchedulingServiceAdminPrx = this.getJobSchedulingServiceAdminProxy();
            if (localJobSchedulingServiceAdminPrx != null) {
                this.latestStats = localJobSchedulingServiceAdminPrx.getStats();
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
            this.sendAlert("Connection to JobSchedulingService on " + this.hostName + " failed");
        }
        try {
            try {
                if (this.latestStats != null) {
                    if (!this.isOnline) {
                        this.isOnline = true;
                        this.sendAlert("Connection to JobSchedulingService on " + this.hostName + " restored");
                    }
                    this.updateWithLatestStats(this.latestStats, this.isOnline);
                    if (this.latestStats.currentJobs != null && this.latestStats.currentJobs.length > 0) {
                        this.baseTreeItem.setForeground(new Color((Device)Display.getCurrent(), 0, 0, 200));
                        this.baseTreeItem.setText(this.baseTreeItem.getText() + " (" + this.latestStats.currentJobs.length + " jobs running)");
                    }
                    this.jobsTreeItem.setText(this.jobsTreeItemText + " (" + (this.latestStats == null ? 0 : this.latestStats.currentJobs.length) + " jobs running)");
                    this.jobsTreeItem.removeAll();
                    for (int i = 0; i < this.latestStats.currentJobs.length; ++i) {
                        new TreeItem(this.jobsTreeItem, 0);
                        this.jobsTreeItem.getItem(i).setText(this.latestStats.currentJobs[i]);
                    }
                }
                Object var3_3 = null;
            }
            catch (Exception e) {
                System.err.println("WARNING: Unable to save stats for the JobSchedulingService on " + this.hostName);
                e.printStackTrace();
                Object var3_4 = null;
            }
        }
        catch (Throwable throwable) {
            Object var3_5 = null;
            throw throwable;
        }
    }
}

