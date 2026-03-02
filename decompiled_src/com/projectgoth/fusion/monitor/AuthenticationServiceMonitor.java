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
import com.projectgoth.fusion.slice.AuthenticationServiceAdminPrx;
import com.projectgoth.fusion.slice.AuthenticationServiceAdminPrxHelper;
import com.projectgoth.fusion.slice.AuthenticationServiceStats;
import com.projectgoth.fusion.slice.FusionException;
import java.util.Date;
import org.eclipse.swt.widgets.TreeItem;

public class AuthenticationServiceMonitor
extends BaseStatsMonitor {
    private AuthenticationServiceStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;
    private AuthenticationServiceAdminPrx authenticationServiceAdminPrx = null;
    protected TreeItem successesTreeItem = new TreeItem(this.baseTreeItem, 0);
    protected TreeItem failsTreeItem;

    public AuthenticationServiceMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.successesTreeItem.setText("Successful Authentications: - Rate: - Max Rate: - Date of Max Rate: -");
        this.failsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.failsTreeItem.setText("Failed Authentications: - Rate: - Max Rate: - Date of Max Rate: -");
        this.port = port == null || port <= 0 ? PortRegistry.AUTHENTICATION_SERVICE_ADMIN.getPort() : port.intValue();
    }

    private synchronized AuthenticationServiceAdminPrx getAuthenticationServiceAdminProxy() {
        if (this.authenticationServiceAdminPrx == null) {
            String stringifiedProxy = "AuthenticationServiceAdmin:tcp -h " + this.hostName + " -p " + this.port + " -t 2000";
            ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
            this.authenticationServiceAdminPrx = AuthenticationServiceAdminPrxHelper.checkedCast(basePrx);
        }
        return this.authenticationServiceAdminPrx;
    }

    public void getStats() {
        try {
            AuthenticationServiceAdminPrx localAuthenticationServiceAdminPrx = this.getAuthenticationServiceAdminProxy();
            if (localAuthenticationServiceAdminPrx != null) {
                this.latestStats = localAuthenticationServiceAdminPrx.getStats();
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
            this.sendAlert("Connection to AuthenticationService on " + this.hostName + " failed");
        }
        try {
            block8: {
                try {
                    if (this.latestStats == null) break block8;
                    if (!this.isOnline) {
                        this.isOnline = true;
                        this.sendAlert("Connection to AuthenticationService on " + this.hostName + " restored");
                    }
                    this.updateWithLatestStats(this.latestStats, this.isOnline);
                    this.successesTreeItem.setText("Successful Authentications: " + millionFormatter.format(this.latestStats.successfulAuthentications) + " Rate: " + millionFormatter.format(this.latestStats.successfulAuthenticationRate) + " Max Rate: " + millionFormatter.format(this.latestStats.peakSuccessfulAuthenticationRate) + " Date of Max Rate: " + new Date(this.latestStats.peakSuccessfulAuthenticationRateDate));
                    this.failsTreeItem.setText("Failed Authentications: " + millionFormatter.format(this.latestStats.failedAuthentications) + " Rate: " + millionFormatter.format(this.latestStats.failedAuthenticationRate) + " Max Rate: " + millionFormatter.format(this.latestStats.peakFailedAuthenticationRate) + " Date of Max Rate: " + new Date(this.latestStats.peakFailedAuthenticationRateDate));
                }
                catch (Exception e) {
                    System.err.println("WARNING: Unable to save stats for the AuthenticationService on " + this.hostName);
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

