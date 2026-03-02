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
import com.projectgoth.fusion.slice.EventStoreAdminPrx;
import com.projectgoth.fusion.slice.EventStoreAdminPrxHelper;
import com.projectgoth.fusion.slice.EventStoreStats;
import com.projectgoth.fusion.slice.FusionException;
import org.eclipse.swt.widgets.TreeItem;

public class EventStoreMonitor
extends BaseStatsMonitor {
    public EventStoreStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;
    private EventStoreAdminPrx eventStoreAdminPrx = null;
    private int port = 21050;
    private TreeItem eventsTreeItem = new TreeItem(this.baseTreeItem, 0);
    private String eventsText = "Events received: [";
    private TreeItem generatorEventsTreeItem;
    private String generatorEventsText = "Generator events received: [";
    private TreeItem cacheExpiredEventsTreeItem;
    private String cacheExpiredEventsText = "] unqiue users/storeUserEvents written to bdb: [";
    private String cacheSizeText = "LRU Buffer Size: )";
    private TreeItem persistBufferTreeItem;
    private String persistBufferSizeText = "Persist Buffer Size: [";
    private String eventRateText = "] rate: [";
    private String generatorEventRateText = "] rate: [";

    public EventStoreMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.eventsTreeItem.setText(this.eventsText);
        this.generatorEventsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.generatorEventsTreeItem.setText(this.generatorEventsText);
        this.cacheExpiredEventsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.cacheExpiredEventsTreeItem.setText(this.cacheSizeText);
        this.persistBufferTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.persistBufferTreeItem.setText(this.persistBufferSizeText);
        this.port = port;
    }

    private synchronized EventStoreAdminPrx getEventSystemProxy() {
        if (this.eventStoreAdminPrx == null) {
            String stringifiedProxy = "EventStoreAdmin:tcp -h " + this.hostName + " -p " + this.port + " -t 2000";
            ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
            this.eventStoreAdminPrx = EventStoreAdminPrxHelper.checkedCast(basePrx);
        }
        return this.eventStoreAdminPrx;
    }

    public void getStats() {
        try {
            EventStoreAdminPrx localEventStoreAdmPrx = this.getEventSystemProxy();
            if (localEventStoreAdmPrx != null) {
                this.latestStats = localEventStoreAdmPrx.getStats();
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
            this.sendAlert("Connection to EventSystem on " + this.hostName + " failed");
        }
        try {
            block8: {
                try {
                    if (this.latestStats == null) break block8;
                    if (!this.isOnline) {
                        this.isOnline = true;
                        this.sendAlert("Connection to MessageLogger on " + this.hostName + " restored");
                    }
                    this.eventsTreeItem.setText(this.eventsText + millionFormatter.format(this.latestStats.events) + this.eventRateText + millionFormatter.format(this.latestStats.eventRate) + "]");
                    this.generatorEventsTreeItem.setText(this.generatorEventsText + millionFormatter.format(this.latestStats.generatorEvents) + this.generatorEventRateText + millionFormatter.format(this.latestStats.generatorEventRate) + "]");
                    this.cacheExpiredEventsTreeItem.setText(this.cacheSizeText + "[" + millionFormatter.format(this.latestStats.cacheSize) + "] total storeUserEvents expired: [" + millionFormatter.format(this.latestStats.persistBufferEvents) + "] outgoing rate [" + millionFormatter.format(this.latestStats.persistBufferRate) + "] outgoing peak rate [" + millionFormatter.format(this.latestStats.maxPersistBufferRate) + "]");
                    this.persistBufferTreeItem.setText(this.persistBufferSizeText + this.latestStats.persistBufferSize + this.cacheExpiredEventsText + millionFormatter.format(this.latestStats.cacheExpiredEvents) + "] persistence rate [" + millionFormatter.format(this.latestStats.cacheExpiredEventRate) + "] peak persistence rate [" + millionFormatter.format(this.latestStats.maxCacheExpiredEventRate) + "]");
                    this.updateWithLatestStats(this.latestStats, this.isOnline);
                    this.jvmMemoryUsageTreeItem.setText(this.jvmMemoryUsageText + this.toMegaBytes(this.latestStats.jvmTotalMemory) + " allocated: " + this.toMegaBytes(this.latestStats.jvmTotalMemory - this.latestStats.jvmFreeMemory) + " used");
                    this.uptimeTreeItem.setText(this.uptimeText + this.toNiceDuration(this.latestStats.uptime));
                }
                catch (Exception e) {
                    System.err.println("WARNING: Unable to save stats for the EventStore on " + this.hostName);
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

