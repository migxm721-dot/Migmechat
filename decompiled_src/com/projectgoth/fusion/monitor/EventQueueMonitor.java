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
import com.projectgoth.fusion.slice.EventQueueWorkerServiceAdminPrx;
import com.projectgoth.fusion.slice.EventQueueWorkerServiceAdminPrxHelper;
import com.projectgoth.fusion.slice.EventQueueWorkerServiceStats;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.swt.widgets.TreeItem;

public class EventQueueMonitor
extends BaseStatsMonitor {
    private EventQueueWorkerServiceStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;
    private EventQueueWorkerServiceAdminPrx adminPrx = null;
    private static final String FRIENDS_ADDED = "Friends Added";
    private static final String VG_SENT = "Virtual Gifts Sent";
    protected LinkedHashMap<String, TreeItem> eventsTreeItems;
    protected TreeItem VirtualGiftSentTreeItem;
    protected TreeItem eventsProcessedTreeItem = new TreeItem(this.baseTreeItem, 0);
    protected TreeItem queueSizeTreeItem = new TreeItem(this.baseTreeItem, 0);

    public EventQueueMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.eventsTreeItems = new LinkedHashMap();
        this.eventsTreeItems.put(FRIENDS_ADDED, new TreeItem(this.baseTreeItem, 0));
        this.eventsTreeItems.put(VG_SENT, new TreeItem(this.baseTreeItem, 0));
        for (Map.Entry<String, TreeItem> entry : this.eventsTreeItems.entrySet()) {
            TreeItem t = entry.getValue();
            String key = entry.getKey();
            t.setText(String.format(" %s events processed: - current rate: - peak rate -", key));
        }
        this.eventsProcessedTreeItem.setText("Total Events processed: - current rate: - peak rate: -");
        this.queueSizeTreeItem.setText("Queue Size: - max: - ");
        this.port = port == null || port <= 0 ? 7903 : port;
        String stringifiedProxy = "EventQueueWorkerAdmin:tcp -h " + hostName + " -p " + port;
        System.out.println("Obtaining EventQueueWorkerServiceAdminPrx via " + stringifiedProxy);
        ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
        this.adminPrx = EventQueueWorkerServiceAdminPrxHelper.uncheckedCast(basePrx);
        if (this.adminPrx == null) {
            System.out.println("Obtaining EventQueueWorkerServiceAdminPrx via " + stringifiedProxy + "[FAILED]");
        }
    }

    public void run() {
        if (!this.latestStatsLoaded) {
            this.getStats();
        }
        this.latestStatsLoaded = false;
        if (this.latestException != null) {
            this.isOnline = false;
            this.updateWithLatestStats(this.latestStats, this.isOnline);
            this.sendAlert("Connection to EventQueueWorkerAdmin on " + this.hostName + " failed");
        }
        try {
            if (this.latestStats != null) {
                if (!this.isOnline) {
                    this.isOnline = true;
                    this.sendAlert("Connection to EventQueueWorkerAdmin on " + this.hostName + " restored");
                }
                this.updateWithLatestStats(this.latestStats, this.isOnline);
                this.eventsProcessedTreeItem.setText("Total Events Processed: " + millionFormatter.format(this.latestStats.eventsProcessed) + " current rate: " + millionFormatter.format(this.latestStats.currentEventsProcessedRate) + " peak rate: " + millionFormatter.format(this.latestStats.peakEventsProcessedRate));
                this.queueSizeTreeItem.setText("Queue Size: " + millionFormatter.format(this.latestStats.queueSize) + " max: " + millionFormatter.format(this.latestStats.maxQueueSize));
                for (Map.Entry<String, TreeItem> entry : this.eventsTreeItems.entrySet()) {
                    TreeItem t = entry.getValue();
                    String key = entry.getKey();
                    String totalEvents = "-";
                    String currentRate = "-";
                    String peakRate = "-";
                    if (key.equals(FRIENDS_ADDED)) {
                        totalEvents = millionFormatter.format(this.latestStats.friendsAddedEvents);
                        currentRate = millionFormatter.format(this.latestStats.currentFriendsAddedEventRate);
                        peakRate = millionFormatter.format(this.latestStats.peakFriendsAddedEventRate);
                    } else if (key.equals(VG_SENT)) {
                        totalEvents = millionFormatter.format(this.latestStats.virtualGiftSentEvents);
                        currentRate = millionFormatter.format(this.latestStats.currentVirtualGiftEventRate);
                        peakRate = millionFormatter.format(this.latestStats.peakVirtualGiftEventRate);
                    }
                    t.setText(String.format(" %s events received: %s current rate: %s peak rate: %s", key, totalEvents, currentRate, peakRate));
                }
            }
        }
        catch (Exception e) {
            System.err.println("WARNING: Unable to save stats for the EventQueueWorkerAdmin on " + this.hostName);
            e.printStackTrace();
        }
    }

    public void getStats() {
        try {
            if (this.adminPrx != null) {
                this.latestStats = this.adminPrx.getStats();
                this.latestException = null;
            } else {
                System.err.println("WARNING: Unable to  retrieve EventQueueWorkerServiceAdminPrx for " + this.hostName);
            }
        }
        catch (Exception e) {
            this.latestStats = null;
            this.latestException = e;
            System.err.println("WARNING: Unable to save stats for the EventQueueWorkerAdmin on " + this.hostName + ": " + e.getMessage());
            e.printStackTrace();
        }
        this.latestStatsLoaded = true;
    }
}

