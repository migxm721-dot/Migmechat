/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.ObjectPrx
 *  org.eclipse.swt.graphics.Color
 *  org.eclipse.swt.graphics.Device
 *  org.eclipse.swt.widgets.Display
 *  org.eclipse.swt.widgets.Event
 *  org.eclipse.swt.widgets.Listener
 *  org.eclipse.swt.widgets.TreeItem
 */
package com.projectgoth.fusion.monitor;

import Ice.LocalException;
import Ice.ObjectPrx;
import com.projectgoth.fusion.monitor.BaseStatsMonitor;
import com.projectgoth.fusion.monitor.Monitor;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectCacheAdminPrx;
import com.projectgoth.fusion.slice.ObjectCacheAdminPrxHelper;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import java.text.DateFormat;
import java.util.Date;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

public class ObjectCacheMonitor
extends BaseStatsMonitor {
    public ObjectCacheStats latestStats = null;
    private Exception latestException = null;
    private boolean latestStatsLoaded = false;
    private ObjectCacheAdminPrx objectCacheAdminPrx;
    private TreeItem numUserObjectsTreeItem = new TreeItem(this.baseTreeItem, 0);
    private String numUserObjectsText = "No. User Objects";
    private TreeItem maxUserObjectsTreeItem;
    private String maxUserObjectsText = "Max. User Objects";
    private TreeItem numOnlineUserObjectsTreeItem;
    private String numOnlineUserObjectsText = "No. Online User Objects";
    private TreeItem maxOnlineUserObjectsTreeItem;
    private String maxOnlineUserObjectsText = "Max. Online User Objects";
    private TreeItem eldestUserObjectTreeItem;
    private String eldestUserObjectText = "Eldest User Object";
    private TreeItem numSessionObjectsTreeItem;
    private String numSessionObjectsText = "No. Session Objects";
    private TreeItem maxSessionObjectsTreeItem;
    private String maxSessionObjectsText = "Max. Session Objects";
    private TreeItem numChatRoomObjectsTreeItem;
    private String numChatRoomObjectsText = "No. ChatRoom Objects";
    private TreeItem maxChatRoomObjectsTreeItem;
    private String maxChatRoomObjectsText = "Max. ChatRoom Objects";
    private TreeItem numSessionInChatRoomsTreeItem;
    private String numSessionInChatRoomsText = "No. Sessions in ChatRooms";
    private TreeItem numGroupChatObjectsTreeItem;
    private String numGroupChatObjectsText = "No. GroupChat Objects";
    private TreeItem maxGroupChatObjectsTreeItem;
    private String maxGroupChatObjectsText = "Max. GroupChat Objects";
    private TreeItem numSessionInGroupChatsTreeItem;
    private String numSessionInGroupChatsText = "No. Sessions in GroupChats";
    private TreeItem distributionQueueSizeTreeItem;
    private String distributionQueueSizeText = "ChatRoom Distribution Queue Size";
    private TreeItem stadiumDistributionQueueSizeTreeItem;
    private String stadiumDistributionQueueSizeText = "Stadium Distribution Queue Size";
    private TreeItem requestsPerSecondTreeItem;
    private String requestsPerSecondText = "Requests Per Second";
    private TreeItem maxRequestsPerSecondTreeItem;
    private String maxRequestsPerSecondText = "Max. Requests Per Second";
    private final String dataGridStatsPrompt = "Data grid stats: ";
    private TreeItem dataGridStatsTreeItem;
    private final String cusIntermediateFailsPrompt = "Total ChatUserSessions intermediate fails: ";
    private TreeItem cusIntermediateFailsTreeItem;
    private final String cusFinalFailsPrompt = "Total ChatUserSessions final fails: ";
    private TreeItem cusFinalFailsTreeItem;
    public TreeItem userObjectsTreeItem;
    private TreeItem loadingUserObjectsTreeItem;

    public ObjectCacheMonitor(String hostName, Integer port, TreeItem parentTreeItem) {
        super(hostName, parentTreeItem);
        this.numUserObjectsTreeItem.setText(this.numUserObjectsText + ":");
        this.maxUserObjectsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.maxUserObjectsTreeItem.setText(this.maxUserObjectsText + ":");
        this.numOnlineUserObjectsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.numOnlineUserObjectsTreeItem.setText(this.numOnlineUserObjectsText + ":");
        this.maxOnlineUserObjectsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.maxOnlineUserObjectsTreeItem.setText(this.maxOnlineUserObjectsText + ":");
        this.eldestUserObjectTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.eldestUserObjectTreeItem.setText(this.eldestUserObjectText + ":");
        this.numSessionObjectsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.numSessionObjectsTreeItem.setText(this.numSessionObjectsText + ":");
        this.maxSessionObjectsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.maxSessionObjectsTreeItem.setText(this.maxSessionObjectsText + ":");
        this.numChatRoomObjectsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.numChatRoomObjectsTreeItem.setText(this.numChatRoomObjectsText + ":");
        this.maxChatRoomObjectsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.maxChatRoomObjectsTreeItem.setText(this.maxChatRoomObjectsText + ":");
        this.numSessionInChatRoomsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.numSessionInChatRoomsTreeItem.setText(this.numSessionInChatRoomsText + ":");
        this.numGroupChatObjectsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.numGroupChatObjectsTreeItem.setText(this.numGroupChatObjectsText + ":");
        this.maxGroupChatObjectsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.maxGroupChatObjectsTreeItem.setText(this.maxGroupChatObjectsText + ":");
        this.numSessionInGroupChatsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.numSessionInGroupChatsTreeItem.setText(this.numSessionInGroupChatsText + ":");
        this.distributionQueueSizeTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.distributionQueueSizeTreeItem.setText(this.distributionQueueSizeText + ":");
        this.stadiumDistributionQueueSizeTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.stadiumDistributionQueueSizeTreeItem.setText(this.stadiumDistributionQueueSizeText + ":");
        this.requestsPerSecondTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.requestsPerSecondTreeItem.setText(this.requestsPerSecondText + ":");
        this.maxRequestsPerSecondTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.maxRequestsPerSecondTreeItem.setText(this.maxRequestsPerSecondText + ":");
        this.dataGridStatsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.dataGridStatsTreeItem.setText("Data grid stats: ");
        this.cusIntermediateFailsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.cusIntermediateFailsTreeItem.setText("Total ChatUserSessions intermediate fails: ");
        this.cusFinalFailsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.cusFinalFailsTreeItem.setText("Total ChatUserSessions final fails: ");
        this.userObjectsTreeItem = new TreeItem(this.baseTreeItem, 0);
        this.userObjectsTreeItem.setText("User Objects");
        this.userObjectsTreeItem.addListener(17, (Listener)new LoadUserObjectsListener());
        this.loadingUserObjectsTreeItem = new TreeItem(this.userObjectsTreeItem, 0);
        this.loadingUserObjectsTreeItem.setText("Loading...");
        String stringifiedProxy = "ObjectCacheAdmin:tcp -h " + hostName + " -p " + (port == null ? "9000" : port);
        ObjectPrx basePrx = Monitor.communicator().stringToProxy(stringifiedProxy);
        this.objectCacheAdminPrx = ObjectCacheAdminPrxHelper.uncheckedCast(basePrx);
    }

    public void loadUserObjects() {
        this.userObjectsTreeItem.removeAll();
        String[] usernames = null;
        try {
            usernames = this.objectCacheAdminPrx.getUsernames();
        }
        catch (Exception e) {
            // empty catch block
        }
        if (usernames == null) {
            TreeItem errorTreeItem = new TreeItem(this.userObjectsTreeItem, 0);
            errorTreeItem.setText("Unable to retrieve usernames");
            return;
        }
        if (usernames.length == 0) {
            TreeItem noUsersTreeItem = new TreeItem(this.userObjectsTreeItem, 0);
            noUsersTreeItem.setText("[None]");
            return;
        }
        for (String username : usernames) {
            TreeItem usernameTreeItem = new TreeItem(this.userObjectsTreeItem, 0);
            usernameTreeItem.setText(username);
        }
    }

    public void getStats() {
        try {
            this.latestStats = this.objectCacheAdminPrx.getStats();
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
            this.latestException.printStackTrace();
            return;
        }
        if (this.latestException instanceof LocalException) {
            this.latestException.printStackTrace();
            if (this.isOnline) {
                this.isOnline = false;
                this.updateWithLatestStats(this.latestStats, this.isOnline);
                this.sendAlert("Connection to Object Cache on " + this.hostName + " failed");
            }
        }
        try {
            block13: {
                try {
                    if (this.latestStats == null) break block13;
                    if (!this.isOnline) {
                        this.isOnline = true;
                        this.sendAlert("Connection to Object Cache on " + this.hostName + " restored");
                    }
                    this.latestStats.version = "(" + this.objectCacheAdminPrx.ice_getEndpoints()[0] + ") " + this.latestStats.version;
                    this.updateWithLatestStats(this.latestStats, this.isOnline);
                    this.numUserObjectsTreeItem.setText(this.numUserObjectsText + ": " + this.latestStats.numUserObjects);
                    this.maxUserObjectsTreeItem.setText(this.maxUserObjectsText + ": " + this.latestStats.maxUserObjects);
                    this.numOnlineUserObjectsTreeItem.setText(this.numOnlineUserObjectsText + ": " + this.latestStats.numOnlineUserObjects);
                    this.maxOnlineUserObjectsTreeItem.setText(this.maxOnlineUserObjectsText + ": " + this.latestStats.maxOnlineUserObjects);
                    if (this.latestStats.eldestUserObject == 0L) {
                        this.eldestUserObjectTreeItem.setText(this.eldestUserObjectText + ": N/A");
                    } else {
                        this.eldestUserObjectTreeItem.setText(this.eldestUserObjectText + ": " + DateFormat.getDateTimeInstance(2, 3).format(new Date(this.latestStats.eldestUserObject)));
                    }
                    this.numSessionObjectsTreeItem.setText(this.numSessionObjectsText + ": " + this.latestStats.numSessionObjects);
                    this.maxSessionObjectsTreeItem.setText(this.maxSessionObjectsText + ": " + this.latestStats.maxSessionObjects);
                    this.numChatRoomObjectsTreeItem.setText(this.numChatRoomObjectsText + ": " + this.latestStats.numChatRoomObjects);
                    this.maxChatRoomObjectsTreeItem.setText(this.maxChatRoomObjectsText + ": " + this.latestStats.maxChatRoomObjects);
                    this.numSessionInChatRoomsTreeItem.setText(this.numSessionInChatRoomsText + ": " + this.latestStats.numSessionsInChatrooms);
                    this.numGroupChatObjectsTreeItem.setText(this.numGroupChatObjectsText + ": " + this.latestStats.numGroupChatObjects);
                    this.maxGroupChatObjectsTreeItem.setText(this.maxGroupChatObjectsText + ": " + this.latestStats.maxGroupChatObjects);
                    this.numSessionInGroupChatsTreeItem.setText(this.numSessionInGroupChatsText + ": " + this.latestStats.numSessionsInGroupChats);
                    this.distributionQueueSizeTreeItem.setText(this.distributionQueueSizeText + ": " + this.latestStats.distributionServiceQueueSize);
                    this.stadiumDistributionQueueSizeTreeItem.setText(this.stadiumDistributionQueueSizeText + ": " + this.latestStats.stadiumDistributionServiceQueueSize);
                    this.requestsPerSecondTreeItem.setText(this.requestsPerSecondText + ": " + this.latestStats.requestsPerSecond);
                    this.maxRequestsPerSecondTreeItem.setText(this.maxRequestsPerSecondText + ": " + this.latestStats.maxRequestsPerSecond);
                    if (this.latestStats.dataGridStats != null) {
                        this.dataGridStatsTreeItem.setText("Data grid stats: " + this.latestStats.dataGridStats);
                    }
                    this.cusIntermediateFailsTreeItem.setText("Total ChatUserSessions intermediate fails: " + this.latestStats.totalChatUserSessionsIntermediateFails);
                    this.cusFinalFailsTreeItem.setText("Total ChatUserSessions final fails: " + this.latestStats.totalChatUserSessionsFinalFails);
                    if (this.latestStats.requestsPerSecond < 100.0f) {
                        this.baseTreeItem.setForeground(new Color((Device)Display.getCurrent(), 255, 110, 0));
                        break block13;
                    }
                    this.baseTreeItem.setForeground(new Color((Device)Display.getCurrent(), 0, 160, 0));
                }
                catch (Exception e) {
                    System.err.println("WARNING: Unable to save stats for the Object Cache on " + this.hostName);
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

    private class LoadUserObjectsListener
    implements Listener {
        private LoadUserObjectsListener() {
        }

        public void handleEvent(Event arg0) {
            System.out.println("handleEvent() " + arg0.toString());
        }
    }
}

