/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.gateway.GatewayContext;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PurgeConnectionTask
extends TimerTask {
    private final Gateway gateway;
    private final GatewayContext gatewayContext;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Gateway.class));
    private Set<ConnectionI> connections = new HashSet<ConnectionI>();

    public PurgeConnectionTask(Gateway gateway, GatewayContext gatewayContext) {
        this.gateway = gateway;
        this.gatewayContext = gatewayContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(ConnectionI connection) {
        Set<ConnectionI> set = this.connections;
        synchronized (set) {
            this.connections.add(connection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void remove(ConnectionI connection) {
        Set<ConnectionI> set = this.connections;
        synchronized (set) {
            this.connections.remove(connection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void monitor(ConnectionI connection) {
        Set<ConnectionI> set = this.connections;
        synchronized (set) {
            this.connections.add(connection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void purge(ConnectionI connection) {
        try {
            connection.onSessionTerminated();
            connection.disconnect();
        }
        catch (Exception e) {
            log.warn((Object)(e.getClass().getName() + " occured when purging connection " + connection.getSessionID() + " [" + connection.getUsername() + "] - "), (Throwable)e);
        }
        finally {
            Set<ConnectionI> set = this.connections;
            synchronized (set) {
                this.connections.remove(connection);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Set<ConnectionI> getIdleConnectionsToPurge(long cutoffTime) {
        HashSet<ConnectionI> connectionsToPurge = new HashSet<ConnectionI>();
        Set<ConnectionI> set = this.connections;
        synchronized (set) {
            for (ConnectionI connection : this.connections) {
                if (connection.getLastAccessed() >= cutoffTime) continue;
                connectionsToPurge.add(connection);
            }
        }
        return connectionsToPurge;
    }

    @Override
    public void run() {
        log.debug((Object)("Purging idle " + (Object)((Object)this.gateway.getServerType()) + " connections ..."));
        if (this.gateway.getTimeoutInterval() > 0L) {
            long cutoffTime = System.currentTimeMillis() - this.gateway.getTimeoutInterval();
            Set<ConnectionI> connectionsToPurge = this.getIdleConnectionsToPurge(cutoffTime);
            for (ConnectionI connection : connectionsToPurge) {
                this.purge(connection);
            }
            if (connectionsToPurge.size() > 0 && log.isDebugEnabled()) {
                log.debug((Object)("Purged [" + connectionsToPurge.size() + "] connections"));
            }
        }
        try {
            this.gatewayContext.getIcePrxFinder().getRegistry(false).ice_ping();
        }
        catch (Exception e) {
            try {
                log.warn((Object)"registry proxy not responding to ping", (Throwable)e);
                this.gatewayContext.getIcePrxFinder().getRegistry(true);
            }
            catch (Exception eInner) {
                log.error((Object)"unable to relocate registry proxy", (Throwable)eInner);
            }
        }
        if (SystemPropertyEntities.Temp.Cache.se493WebSocketsEnabled.getValue().booleanValue()) {
            this.gateway.logDebugPurgingCompleted();
        } else {
            log.debug((Object)("Purging task completed. " + this.gateway.connectionCount.get() + " " + (Object)((Object)this.gateway.getServerType()) + " connection(s) left on the adaptor. " + this.gateway.getSelector().keys().size() + " key(s) left on selector"));
        }
    }
}

