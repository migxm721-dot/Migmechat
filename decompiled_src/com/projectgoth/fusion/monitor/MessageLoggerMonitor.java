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
import com.projectgoth.fusion.monitor.BaseStatsMonitor;
import com.projectgoth.fusion.monitor.Monitor;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageLoggerAdminPrx;
import com.projectgoth.fusion.slice.MessageLoggerAdminPrxHelper;
import com.projectgoth.fusion.slice.MessageLoggerStats;
import org.eclipse.swt.widgets.TreeItem;

public class MessageLoggerMonitor
extends BaseStatsMonitor {
    public MessageLoggerStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;
    private MessageLoggerAdminPrx messageLoggerAdminPrx;
    private TreeItem numMessagesReceivedPerSecondTreeItem = new TreeItem(this.baseTreeItem, 0);
    private String numMessagesReceivedPerSecondText = "Messages Received Per Second";
    private TreeItem maxMessagesReceivedPerSecondTreeItem;
    private String maxMessagesReceivedPerSecondText = "Max. Messages Received Per Second";
    private TreeItem numMessagesLoggedPerSecondTreeItem;
    private String numMessagesLoggedPerSecondText = "Messages Logged Per Second";
    private TreeItem maxMessagesLoggedPerSecondTreeItem;
    private String maxMessagesLoggedPerSecondText = "Max. Messages Logged Per Second";
    private TreeItem numMessagesQueuedTreeItem;
    private String numMessagesQueuedText = "Messages Queued";
    private TreeItem maxMessagesQueuedTreeItem;
    private String maxMessagesQueuedText = "Max. Messages Queued";

    public MessageLoggerMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.numMessagesReceivedPerSecondTreeItem.setText(this.numMessagesReceivedPerSecondText + ":");
        this.maxMessagesReceivedPerSecondTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.maxMessagesReceivedPerSecondTreeItem.setText(this.maxMessagesReceivedPerSecondText + ":");
        this.numMessagesLoggedPerSecondTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.numMessagesLoggedPerSecondTreeItem.setText(this.numMessagesLoggedPerSecondText + ":");
        this.maxMessagesLoggedPerSecondTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.maxMessagesLoggedPerSecondTreeItem.setText(this.maxMessagesLoggedPerSecondText + ":");
        this.numMessagesQueuedTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.numMessagesQueuedTreeItem.setText(this.numMessagesQueuedText + ":");
        this.maxMessagesQueuedTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.maxMessagesQueuedTreeItem.setText(this.maxMessagesQueuedText + ":");
        String stringifiedProxy = "MessageLoggerAdmin:tcp -h " + hostName + " -p " + (port == null ? "9996" : port);
        ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
        this.messageLoggerAdminPrx = MessageLoggerAdminPrxHelper.uncheckedCast(basePrx);
    }

    public void getStats() {
        try {
            this.latestStats = this.messageLoggerAdminPrx.getStats();
            this.latestException = null;
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
            this.sendAlert("Connection to MessageLogger on " + this.hostName + " failed");
        }
        try {
            block8: {
                try {
                    if (this.latestStats == null) break block8;
                    if (!this.isOnline) {
                        this.isOnline = true;
                        this.sendAlert("Connection to MessageLogger on " + this.hostName + " restored");
                    }
                    this.updateWithLatestStats(this.latestStats, this.isOnline);
                    this.numMessagesReceivedPerSecondTreeItem.setText(this.numMessagesReceivedPerSecondText + ": " + this.latestStats.numMessagesReceivedPerSecond);
                    this.maxMessagesReceivedPerSecondTreeItem.setText(this.maxMessagesReceivedPerSecondText + ": " + this.latestStats.maxMessagesReceivedPerSecond);
                    this.numMessagesLoggedPerSecondTreeItem.setText(this.numMessagesLoggedPerSecondText + ": " + this.latestStats.numMessagesLoggedPerSecond);
                    this.maxMessagesLoggedPerSecondTreeItem.setText(this.maxMessagesLoggedPerSecondText + ": " + this.latestStats.maxMessagesLoggedPerSecond);
                    this.numMessagesQueuedTreeItem.setText(this.numMessagesQueuedText + ": " + this.latestStats.numMessagesQueued);
                    this.maxMessagesQueuedTreeItem.setText(this.maxMessagesQueuedText + ": " + this.latestStats.maxMessagesQueued);
                }
                catch (Exception e) {
                    System.err.println("WARNING: Unable to save stats for the MessageLogger on " + this.hostName);
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

