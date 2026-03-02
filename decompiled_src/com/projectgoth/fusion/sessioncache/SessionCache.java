/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.annotation.Required
 */
package com.projectgoth.fusion.sessioncache;

import Ice.Application;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.sessioncache.SessionCacheAdminI;
import com.projectgoth.fusion.sessioncache.SessionCacheI;
import com.projectgoth.fusion.slice.ReputationServicePrx;
import com.projectgoth.fusion.stats.IceStats;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class SessionCache
extends Application {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SessionCache.class));
    public static ObjectAdapter sessionCacheAdapter = null;
    public static Properties properties = null;
    public static String hostName = null;
    public static long startTime = System.currentTimeMillis();
    private SessionCacheI sessionCacheServant;
    private IcePrxFinder icePrxFinder;

    public SessionCache() {
        log.debug((java.lang.Object)(((java.lang.Object)((java.lang.Object)this)).getClass() + " INSTANTIATED!!"));
    }

    @Required
    public void setSessionCacheServant(SessionCacheI sessionCacheServant) {
        this.sessionCacheServant = sessionCacheServant;
    }

    private void configureServant(Properties properties) throws Exception {
        this.icePrxFinder = new IcePrxFinder(SessionCache.communicator(), properties);
        ReputationServicePrx reputationServicePrx = this.icePrxFinder.waitForReputationServiceProxy();
        this.sessionCacheServant.setReputationServicePrx(reputationServicePrx);
        this.sessionCacheServant.createArchiveThread();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int run(String[] arg0) {
        properties = SessionCache.communicator().getProperties();
        log.info((java.lang.Object)("Configured endpoint [" + properties.getProperty("SessionCacheAdapter.Endpoints") + "]"));
        try {
            hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            hostName = "UNKNOWN";
        }
        try {
            try {
                this.configureServant(properties);
                log.debug((java.lang.Object)"Initialising SessionCache interface");
                sessionCacheAdapter = SessionCache.communicator().createObjectAdapter("SessionCacheAdapter");
                sessionCacheAdapter.add((Object)this.sessionCacheServant, Util.stringToIdentity((String)"SessionCache"));
                IceStats.getInstance().setIceObjects(SessionCache.communicator(), sessionCacheAdapter, null);
                log.debug((java.lang.Object)"Initialising SessionCacheAdmin interface");
                ObjectAdapter SessionCacheAdminAdapter = SessionCache.communicator().createObjectAdapter("SessionCacheAdminAdapter");
                SessionCacheAdminI SessionCacheAdmin2 = new SessionCacheAdminI(this.sessionCacheServant);
                SessionCacheAdminAdapter.add((Object)SessionCacheAdmin2, Util.stringToIdentity((String)"SessionCacheAdmin"));
                SessionCacheAdminAdapter.activate();
                sessionCacheAdapter.activate();
                log.info((java.lang.Object)"Service started");
                SessionCache.communicator().waitForShutdown();
            }
            catch (Exception e) {
                log.error((java.lang.Object)"failed to configure servant", (Throwable)e);
                java.lang.Object var5_7 = null;
                if (!SessionCache.interrupted()) return 0;
                log.fatal((java.lang.Object)("SessionCache " + hostName + ": terminating"));
                this.sessionCacheServant.terminating();
                return 0;
            }
            java.lang.Object var5_6 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var5_8 = null;
            if (!SessionCache.interrupted()) throw throwable;
            log.fatal((java.lang.Object)("SessionCache " + hostName + ": terminating"));
            this.sessionCacheServant.terminating();
            throw throwable;
        }
        if (!SessionCache.interrupted()) return 0;
        log.fatal((java.lang.Object)("SessionCache " + hostName + ": terminating"));
        this.sessionCacheServant.terminating();
        return 0;
    }
}

