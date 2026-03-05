/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.ObjectPrx
 *  org.eclipse.swt.widgets.TreeItem
 */
package com.projectgoth.fusion.monitor;

import Ice.LocalException;
import Ice.ObjectPrx;
import com.projectgoth.fusion.common.PortRegistry;
import com.projectgoth.fusion.monitor.BaseStatsMonitor;
import com.projectgoth.fusion.monitor.Monitor;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserNotificationServiceAdminPrx;
import com.projectgoth.fusion.slice.UserNotificationServiceAdminPrxHelper;
import com.projectgoth.fusion.slice.UserNotificationServiceStats;
import org.eclipse.swt.widgets.TreeItem;

public class UserNotificationServiceMonitor
extends BaseStatsMonitor {
    private UserNotificationServiceStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;
    private UserNotificationServiceAdminPrx userNotificationServiceAdminPrx = null;
    protected TreeItem sentTreeItem = new TreeItem(this.baseTreeItem, 0);
    protected TreeItem queueSizeTreeItem = new TreeItem(this.baseTreeItem, 0);

    public UserNotificationServiceMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.port = port == null || port <= 0 ? PortRegistry.USER_NOTIFICATION_SERVICE_ADMIN.getPort() : port.intValue();
    }

    private synchronized UserNotificationServiceAdminPrx getUserNotificationServiceAdminProxy() {
        if (this.userNotificationServiceAdminPrx == null) {
            String stringifiedProxy = "UserNotificationServiceAdmin:tcp -h " + this.hostName + " -p " + this.port + " -t 2000";
            ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
            this.userNotificationServiceAdminPrx = UserNotificationServiceAdminPrxHelper.checkedCast(basePrx);
        }
        return this.userNotificationServiceAdminPrx;
    }

    public void getStats() {
        try {
            UserNotificationServiceAdminPrx localUserNotificationServiceAdminPrx = this.getUserNotificationServiceAdminProxy();
            if (localUserNotificationServiceAdminPrx != null) {
                this.latestStats = localUserNotificationServiceAdminPrx.getStats();
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
            this.sendAlert("Connection to UserNotificationService on " + this.hostName + " failed");
        }
        try {
            block8: {
                try {
                    if (this.latestStats == null) break block8;
                    if (!this.isOnline) {
                        this.isOnline = true;
                        this.sendAlert("Connection to UserNotificationService on " + this.hostName + " restored");
                    }
                    this.updateWithLatestStats(this.latestStats, this.isOnline);
                    this.sentTreeItem.setText("Messages Sent, Alerts: " + this.latestStats.alertsSent + ", Email: " + this.latestStats.emailsSent + ", SMS: " + this.latestStats.smsSent + ", Notifications: " + this.latestStats.notificationsSent);
                    this.queueSizeTreeItem.setText("Queue Sizes, Alerts: " + this.latestStats.alertQueueSize + ", Email: " + this.latestStats.emailQueueSize + ", SMS: " + this.latestStats.smsQueueSize + "" + ", Notifications: " + this.latestStats.notificationQueueSize);
                }
                catch (Exception e) {
                    System.err.println("WARNING: Unable to save stats for the UserNotificationService on " + this.hostName);
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

