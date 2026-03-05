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
package com.projectgoth.fusion.authentication;

import Ice.Application;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.authentication.AuthenticationServiceAdminI;
import com.projectgoth.fusion.authentication.AuthenticationServiceI;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.stats.IceStats;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class AuthenticationService
extends Application {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AuthenticationService.class));
    private static ObjectAdapter AuthenticationServiceAdapter = null;
    private static Properties properties = null;
    public static long startTime = System.currentTimeMillis();
    private String hostName;
    private AuthenticationServiceI authenticationServiceServant;

    @Required
    public void setAuthenticationServiceServant(AuthenticationServiceI authenticationServiceServant) {
        this.authenticationServiceServant = authenticationServiceServant;
    }

    private void configureServant(Properties properties) throws Exception {
        this.authenticationServiceServant.setSurgeMailPassword(properties.getPropertyWithDefault("SurgeMailPassword", "surgemail"));
        this.authenticationServiceServant.setMinimumAuthenticationsPerIP(properties.getPropertyAsIntWithDefault("MinimumAuthenticationsPerIP", 100));
        this.authenticationServiceServant.setBruteForceIPRatio(properties.getPropertyAsIntWithDefault("BruteForceIPRatio", 25000));
        this.authenticationServiceServant.setSuspectIPRatio(properties.getPropertyAsIntWithDefault("SuspectIPRatio", 5000));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int run(String[] arg0) {
        properties = AuthenticationService.communicator().getProperties();
        try {
            try {
                this.configureServant(properties);
                log.info((java.lang.Object)("Configured endpoint [" + properties.getProperty("AuthenticationServiceAdapter.Endpoints") + "]"));
                try {
                    this.hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
                }
                catch (UnknownHostException e) {
                    this.hostName = "UNKNOWN";
                }
                log.debug((java.lang.Object)"Initialising AuthenticationServiceAdapter interface");
                AuthenticationServiceAdapter = AuthenticationService.communicator().createObjectAdapter("AuthenticationServiceAdapter");
                AuthenticationServiceAdapter.add((Object)this.authenticationServiceServant, Util.stringToIdentity((String)"AuthenticationService"));
                IceStats.getInstance().setIceObjects(AuthenticationService.communicator(), AuthenticationServiceAdapter, null);
                log.debug((java.lang.Object)"Initialising AuthenticationServiceAdmin interface");
                ObjectAdapter authenticationServiceAdminAdapter = AuthenticationService.communicator().createObjectAdapter("AuthenticationServiceAdminAdapter");
                AuthenticationServiceAdminI authenticationServiceAdmin = new AuthenticationServiceAdminI(this.authenticationServiceServant);
                authenticationServiceAdminAdapter.add((Object)authenticationServiceAdmin, Util.stringToIdentity((String)"AuthenticationServiceAdmin"));
                authenticationServiceAdminAdapter.activate();
                AuthenticationServiceAdapter.activate();
                log.info((java.lang.Object)"Service started");
                AuthenticationService.communicator().waitForShutdown();
            }
            catch (Exception e) {
                log.error((java.lang.Object)"failed to configure servant", (Throwable)e);
                java.lang.Object var5_7 = null;
                if (!AuthenticationService.interrupted()) return 0;
                log.fatal((java.lang.Object)("AuthenticationService " + this.hostName + ": terminating"));
                this.authenticationServiceServant.shutdown();
                return 0;
            }
            java.lang.Object var5_6 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var5_8 = null;
            if (!AuthenticationService.interrupted()) throw throwable;
            log.fatal((java.lang.Object)("AuthenticationService " + this.hostName + ": terminating"));
            this.authenticationServiceServant.shutdown();
            throw throwable;
        }
        if (!AuthenticationService.interrupted()) return 0;
        log.fatal((java.lang.Object)("AuthenticationService " + this.hostName + ": terminating"));
        this.authenticationServiceServant.shutdown();
        return 0;
    }
}

