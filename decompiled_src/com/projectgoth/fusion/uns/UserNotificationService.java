/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.ObjectPrx
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.annotation.Required
 */
package com.projectgoth.fusion.uns;

import Ice.Application;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.RegistryPrxHelper;
import com.projectgoth.fusion.stats.IceStats;
import com.projectgoth.fusion.uns.UserNotificationServiceAdminI;
import com.projectgoth.fusion.uns.UserNotificationServiceI;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class UserNotificationService
extends Application {
    private static Logger log = Logger.getLogger(UserNotificationService.class);
    public static ObjectAdapter UserNotificationServiceAdapter = null;
    public static Properties properties = null;
    public static long startTime = System.currentTimeMillis();
    private String hostName;
    private UserNotificationServiceI userNotificationServiceServant;
    private String registryProxyLocation;
    private RegistryPrx registryProxy;

    @Required
    public void setUserNotificationServiceServant(UserNotificationServiceI userNotificationlServiceServant) {
        this.userNotificationServiceServant = userNotificationlServiceServant;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized RegistryPrx findUserNotificationProxy() {
        String string = this.registryProxyLocation;
        synchronized (string) {
            try {
                if (this.registryProxy == null) {
                    ObjectPrx basePrx = UserNotificationService.communicator().stringToProxy(this.registryProxyLocation);
                    if (basePrx == null) {
                        throw new Exception("communicator().stringToProxy() returned null");
                    }
                    this.registryProxy = RegistryPrxHelper.checkedCast(basePrx);
                    if (this.registryProxy == null) {
                        throw new Exception("RegistryPrxHelper.checkedCast() returned null");
                    }
                }
            }
            catch (Exception e) {
                log.warn((java.lang.Object)("failed to locate Registry at endpoint(s) " + this.registryProxyLocation + "]"), (Throwable)e);
                this.registryProxy = null;
            }
            return this.registryProxy;
        }
    }

    private void configureServant(Properties properties) throws Exception {
        this.registryProxyLocation = properties.getProperty("RegistryProxy");
        this.userNotificationServiceServant.setRegistryProxy(this.findUserNotificationProxy());
        this.userNotificationServiceServant.initializeQueues();
        log.info((java.lang.Object)("found registry [" + this.registryProxy + "]"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int run(String[] arg0) {
        properties = UserNotificationService.communicator().getProperties();
        try {
            try {
                this.configureServant(properties);
                log.info((java.lang.Object)("Configured endpoint [" + properties.getProperty("UserNotificationServiceAdapter.Endpoints") + "]"));
                try {
                    this.hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
                }
                catch (UnknownHostException e) {
                    this.hostName = "UNKNOWN";
                }
                log.debug((java.lang.Object)"Initialising UserNotificationService interface");
                UserNotificationServiceAdapter = UserNotificationService.communicator().createObjectAdapter("UserNotificationServiceAdapter");
                UserNotificationServiceAdapter.add((Object)this.userNotificationServiceServant, Util.stringToIdentity((String)"UserNotificationService"));
                IceStats.getInstance().setIceObjects(UserNotificationService.communicator(), UserNotificationServiceAdapter, null);
                log.debug((java.lang.Object)"Initialising UserNotificationServiceAdmin interface");
                ObjectAdapter userNotificationServiceAdminAdapter = UserNotificationService.communicator().createObjectAdapter("UserNotificationServiceAdminAdapter");
                UserNotificationServiceAdminI blueLabelServiceAdmin = new UserNotificationServiceAdminI(this.userNotificationServiceServant);
                userNotificationServiceAdminAdapter.add((Object)blueLabelServiceAdmin, Util.stringToIdentity((String)"UserNotificationServiceAdmin"));
                userNotificationServiceAdminAdapter.activate();
                UserNotificationServiceAdapter.activate();
                log.info((java.lang.Object)"Service started");
                UserNotificationService.communicator().waitForShutdown();
            }
            catch (Exception e) {
                log.error((java.lang.Object)"problem initializing UNS?", (Throwable)e);
                java.lang.Object var5_7 = null;
                if (!UserNotificationService.interrupted()) return 0;
                log.fatal((java.lang.Object)("UserNotificationService " + this.hostName + ": terminating"));
                this.userNotificationServiceServant.shutdown();
                return 0;
            }
            java.lang.Object var5_6 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var5_8 = null;
            if (!UserNotificationService.interrupted()) throw throwable;
            log.fatal((java.lang.Object)("UserNotificationService " + this.hostName + ": terminating"));
            this.userNotificationServiceServant.shutdown();
            throw throwable;
        }
        if (!UserNotificationService.interrupted()) return 0;
        log.fatal((java.lang.Object)("UserNotificationService " + this.hostName + ": terminating"));
        this.userNotificationServiceServant.shutdown();
        return 0;
    }
}

