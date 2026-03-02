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
 */
package com.projectgoth.fusion.recommendation.generation;

import Ice.Application;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.recommendation.generation.RecommendationGenerationServiceAdminI;
import com.projectgoth.fusion.recommendation.generation.RecommendationGenerationServiceI;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

public class RecommendationGenerationService
extends Application {
    private static final Logger log = Logger.getLogger(RecommendationGenerationService.class);
    public static ObjectAdapter rgsAdapter = null;
    public static Properties properties = null;
    public static long startTime = System.currentTimeMillis();
    private String hostName;
    private RecommendationGenerationServiceI rgsServant;

    public RecommendationGenerationService(RecommendationGenerationServiceI servant) {
        this.rgsServant = servant;
        log.info((java.lang.Object)"Set servant ok");
    }

    private void configureServant(Properties properties) throws Exception {
        IcePrxFinder icePrxFinder = new IcePrxFinder(RecommendationGenerationService.communicator(), RecommendationGenerationService.properties);
        this.rgsServant.setIcePrxFinder(icePrxFinder);
        if (log.isDebugEnabled()) {
            log.debug((java.lang.Object)"Configured servant ok");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int run(String[] arg0) {
        String jdbcDriverClass = SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.HIVE2_JDBC_DRIVER_CLASS);
        try {
            Class.forName(jdbcDriverClass);
        }
        catch (Exception e) {
            log.error((java.lang.Object)("App cannot start as unable to instantiate Hive2 JDBC driver: " + jdbcDriverClass + " It needs to be explicitly specified in the classpath. e=" + e), (Throwable)e);
            Runtime.getRuntime().exit(-1);
        }
        properties = RecommendationGenerationService.communicator().getProperties();
        try {
            this.hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            this.hostName = "UNKNOWN";
        }
        try {
            try {
                this.configureServant(properties);
                log.info((java.lang.Object)("Configured endpoint [" + properties.getProperty("RecommendationGenerationServiceAdapter.Endpoints") + "]"));
                if (log.isDebugEnabled()) {
                    log.debug((java.lang.Object)"Initialising RecommendationGenerationServiceAdapter interface");
                }
                rgsAdapter = RecommendationGenerationService.communicator().createObjectAdapter("RecommendationGenerationServiceAdapter");
                rgsAdapter.add((Object)this.rgsServant, Util.stringToIdentity((String)"RecommendationGenerationService"));
                this.createAdminInterface();
                rgsAdapter.activate();
                log.info((java.lang.Object)"Recommendation Generation Service started");
                this.rgsServant.initialize();
                if (log.isDebugEnabled()) {
                    log.debug((java.lang.Object)"Initialized servant");
                }
                while (!RecommendationGenerationService.communicator().isShutdown()) {
                    try {
                        int refreshIntervalSecs = SystemProperty.getInt(SystemPropertyEntities.RecommendationServiceSettings.RGS_INTERNAL_TIMER_REFRESH_INTERVAL_SECONDS);
                        Thread.sleep(refreshIntervalSecs * 1000);
                        this.rgsServant.reinitialize();
                    }
                    catch (InterruptedException e) {}
                }
                java.lang.Object var5_9 = null;
            }
            catch (Exception e) {
                log.error((java.lang.Object)"failed to configure servant", (Throwable)e);
                java.lang.Object var5_10 = null;
                if (!RecommendationGenerationService.interrupted()) return 0;
                log.fatal((java.lang.Object)("RecommendationGenerationService " + this.hostName + ": terminating"));
                this.rgsServant.shutdown();
                return 0;
            }
        }
        catch (Throwable throwable) {
            java.lang.Object var5_11 = null;
            if (!RecommendationGenerationService.interrupted()) throw throwable;
            log.fatal((java.lang.Object)("RecommendationGenerationService " + this.hostName + ": terminating"));
            this.rgsServant.shutdown();
            throw throwable;
        }
        if (!RecommendationGenerationService.interrupted()) return 0;
        log.fatal((java.lang.Object)("RecommendationGenerationService " + this.hostName + ": terminating"));
        this.rgsServant.shutdown();
        return 0;
    }

    private void createAdminInterface() throws Exception {
        log.info((java.lang.Object)"Initialising RecommendationGenerationServiceAdmin interface");
        ObjectAdapter rgsAdminAdapter = RecommendationGenerationService.communicator().createObjectAdapter("RecommendationGenerationServiceAdminAdapter");
        RecommendationGenerationServiceAdminI rgsAdmin = new RecommendationGenerationServiceAdminI(this.rgsServant);
        rgsAdminAdapter.add((Object)rgsAdmin, Util.stringToIdentity((String)"RecommendationGenerationServiceAdmin"));
        rgsAdminAdapter.activate();
    }
}

