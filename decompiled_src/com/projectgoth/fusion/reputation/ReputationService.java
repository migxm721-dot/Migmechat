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
package com.projectgoth.fusion.reputation;

import Ice.Application;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.reputation.ReputationServiceAdminI;
import com.projectgoth.fusion.reputation.ReputationServiceI;
import com.projectgoth.fusion.stats.IceStats;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class ReputationService
extends Application {
    private static Logger log = Logger.getLogger(ReputationService.class);
    public static ObjectAdapter ReputationServiceAdapter = null;
    public static Properties properties = null;
    public static long startTime = System.currentTimeMillis();
    private String hostName;
    private ReputationServiceI reputationServiceServant;

    @Required
    public void setReputationServiceServant(ReputationServiceI reputationServiceServant) {
        this.reputationServiceServant = reputationServiceServant;
    }

    private void configureServant(Properties properties) throws Exception {
        IcePrxFinder icePrxFinder = new IcePrxFinder(ReputationService.communicator(), ReputationService.properties);
        this.reputationServiceServant.setIcePrxFinder(icePrxFinder);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int run(String[] arg0) {
        properties = ReputationService.communicator().getProperties();
        try {
            this.hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            this.hostName = "UNKNOWN";
        }
        try {
            try {
                this.configureServant(properties);
                log.info((java.lang.Object)("Configured endpoint [" + properties.getProperty("ReputationServiceAdapter.Endpoints") + "]"));
                log.debug((java.lang.Object)"Initialising ReputationServiceAdapter interface");
                ReputationServiceAdapter = ReputationService.communicator().createObjectAdapter("ReputationServiceAdapter");
                ReputationServiceAdapter.add((Object)this.reputationServiceServant, Util.stringToIdentity((String)"ReputationService"));
                IceStats.getInstance().setIceObjects(ReputationService.communicator(), ReputationServiceAdapter, null);
                log.debug((java.lang.Object)"Initialising ReputationServiceAdmin interface");
                ObjectAdapter reputationServiceAdminAdapter = ReputationService.communicator().createObjectAdapter("ReputationServiceAdminAdapter");
                ReputationServiceAdminI reputationServiceAdmin = new ReputationServiceAdminI(this.reputationServiceServant);
                reputationServiceAdminAdapter.add((Object)reputationServiceAdmin, Util.stringToIdentity((String)"ReputationServiceAdmin"));
                reputationServiceAdminAdapter.activate();
                ReputationServiceAdapter.activate();
                log.info((java.lang.Object)"Service started");
                ReputationService.communicator().waitForShutdown();
            }
            catch (Exception e) {
                log.error((java.lang.Object)"failed to configure servant", (Throwable)e);
                java.lang.Object var5_7 = null;
                if (!ReputationService.interrupted()) return 0;
                log.fatal((java.lang.Object)("ReputationService " + this.hostName + ": terminating"));
                this.reputationServiceServant.shutdown();
                return 0;
            }
            java.lang.Object var5_6 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var5_8 = null;
            if (!ReputationService.interrupted()) throw throwable;
            log.fatal((java.lang.Object)("ReputationService " + this.hostName + ": terminating"));
            this.reputationServiceServant.shutdown();
            throw throwable;
        }
        if (!ReputationService.interrupted()) return 0;
        log.fatal((java.lang.Object)("ReputationService " + this.hostName + ": terminating"));
        this.reputationServiceServant.shutdown();
        return 0;
    }
}

