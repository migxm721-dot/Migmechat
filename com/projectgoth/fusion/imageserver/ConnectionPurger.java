/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.imageserver;

import com.projectgoth.fusion.imageserver.Connection;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class ConnectionPurger
extends TimerTask {
    private Logger logger;
    private Set<Connection> connectionQueue = new HashSet<Connection>();
    private int timeoutInterval;

    public ConnectionPurger(int timeoutInterval, Logger logger) {
        this.timeoutInterval = timeoutInterval;
        this.logger = logger;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(Connection connection) {
        Set<Connection> set = this.connectionQueue;
        synchronized (set) {
            this.connectionQueue.add(connection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void remove(Connection connection) {
        Set<Connection> set = this.connectionQueue;
        synchronized (set) {
            this.connectionQueue.remove(connection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void monitor(Connection connection) {
        Set<Connection> set = this.connectionQueue;
        synchronized (set) {
            this.connectionQueue.add(connection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void purge(Connection connection) {
        try {
            connection.disconnect();
            Set<Connection> set = this.connectionQueue;
            synchronized (set) {
                this.connectionQueue.remove(connection);
            }
        }
        catch (Exception e) {
            this.logger.warn((Object)(e.getClass().getName() + " occured when purging connection - " + e.getMessage()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        this.logger.debug((Object)"Purging idle connections ...");
        if (this.timeoutInterval > 0) {
            HashSet<Connection> connectionsToPurge = new HashSet<Connection>();
            long cutoffTime = System.currentTimeMillis() - (long)this.timeoutInterval;
            Set<Connection> set = this.connectionQueue;
            synchronized (set) {
                for (Connection connection : this.connectionQueue) {
                    if (connection.getLastAccessed() >= cutoffTime) continue;
                    connectionsToPurge.add(connection);
                }
            }
            for (Connection connection : connectionsToPurge) {
                this.purge(connection);
            }
        }
        Runtime runtime = Runtime.getRuntime();
        long memoryAllocated = runtime.totalMemory() / 1024L;
        long memoryUsed = memoryAllocated - runtime.freeMemory() / 1024L;
        this.logger.debug((Object)("Purging task completed. " + this.connectionQueue.size() + " connection(s) left on server"));
        this.logger.debug((Object)("JVM Memory: Total Allocated: " + memoryAllocated + "KB. Used: " + memoryUsed + "KB\n"));
    }
}

