/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.LocalException
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.ObjectPrx
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.annotation.Required
 */
package com.projectgoth.fusion.messageswitchboard;

import Ice.Application;
import Ice.LocalException;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardI;
import com.projectgoth.fusion.slice.MessageSwitchboardAdminPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrxHelper;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.RegistryPrxHelper;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class MessageSwitchboard
extends Application {
    private static final LogFilter log = new LogFilter(Logger.getLogger(MessageSwitchboard.class), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private static final String APP_NAME = "MessageSwitchboard";
    public static ObjectAdapter msgSwitchboardAdapter = null;
    public static Properties properties = null;
    public static long startTime = System.currentTimeMillis();
    private String hostName;
    private MessageSwitchboardI msgSwitchboardServant;

    @Required
    public void setMessageSwitchboardServant(MessageSwitchboardI msgSwitchboardServant) {
        log.debug("Setting servant");
        this.msgSwitchboardServant = msgSwitchboardServant;
        log.debug("Set servant ok");
    }

    private void configureServant(Properties properties) throws Exception {
        IcePrxFinder icePrxFinder = new IcePrxFinder(MessageSwitchboard.communicator(), MessageSwitchboard.properties);
        this.msgSwitchboardServant.setIcePrxFinder(icePrxFinder);
        log.debug("Configured servant ok");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int run(String[] arg0) {
        block9: {
            int n;
            block8: {
                properties = MessageSwitchboard.communicator().getProperties();
                try {
                    this.hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
                }
                catch (UnknownHostException e) {
                    this.hostName = "UNKNOWN";
                }
                try {
                    try {
                        this.configureServant(properties);
                        this.msgSwitchboardServant.initialize(MessageSwitchboard.communicator());
                        log.debug("Initialized servant");
                        log.info("Configured endpoint [" + properties.getProperty("MessageSwitchboardAdapter.Endpoints") + "]");
                        log.debug("Initialising MessageSwitchboardAdapter interface");
                        msgSwitchboardAdapter = MessageSwitchboard.communicator().createObjectAdapter("MessageSwitchboardAdapter");
                        ObjectPrx basePrx = msgSwitchboardAdapter.add((Ice.Object)this.msgSwitchboardServant, Util.stringToIdentity((String)APP_NAME));
                        MessageSwitchboardPrx messageSwitchboardPrx = MessageSwitchboardPrxHelper.uncheckedCast(basePrx);
                        MessageSwitchboardAdminPrx adminPrx = null;
                        msgSwitchboardAdapter.activate();
                        String uniqueID = messageSwitchboardPrx.ice_getEndpoints()[0].toString();
                        RegistryPrx registryPrx = this.getRegistryPrx();
                        if (registryPrx == null) {
                            n = 1;
                            Object var9_10 = null;
                            break block8;
                        }
                        registryPrx.registerMessageSwitchboard(uniqueID, messageSwitchboardPrx, adminPrx);
                        log.info("Service started");
                        MessageSwitchboard.communicator().waitForShutdown();
                        break block9;
                    }
                    catch (Exception e) {
                        log.error("failed to configure servant", e);
                        Object var9_12 = null;
                        if (!MessageSwitchboard.interrupted()) return 0;
                        log.fatal("MessageSwitchboard " + this.hostName + ": terminating");
                        this.msgSwitchboardServant.shutdown();
                        return 0;
                    }
                }
                catch (Throwable throwable) {
                    Object var9_13 = null;
                    if (!MessageSwitchboard.interrupted()) throw throwable;
                    log.fatal("MessageSwitchboard " + this.hostName + ": terminating");
                    this.msgSwitchboardServant.shutdown();
                    throw throwable;
                }
            }
            if (!MessageSwitchboard.interrupted()) return n;
            log.fatal("MessageSwitchboard " + this.hostName + ": terminating");
            this.msgSwitchboardServant.shutdown();
            return n;
        }
        Object var9_11 = null;
        if (!MessageSwitchboard.interrupted()) return 0;
        log.fatal("MessageSwitchboard " + this.hostName + ": terminating");
        this.msgSwitchboardServant.shutdown();
        return 0;
    }

    private RegistryPrx getRegistryPrx() {
        String registryStringifiedProxy = MessageSwitchboard.communicator().getProperties().getProperty("RegistryProxy");
        ObjectPrx basePrx = MessageSwitchboard.communicator().stringToProxy(registryStringifiedProxy);
        log.info("Connecting to [" + basePrx + "]");
        RegistryPrx registryPrx = null;
        try {
            registryPrx = RegistryPrxHelper.checkedCast(basePrx);
        }
        catch (LocalException e) {
            log.fatal("Connection to [" + registryPrx + "] failed. ", e);
            return null;
        }
        if (registryPrx == null) {
            log.fatal("Connection to [" + registryPrx + "] failed");
            return null;
        }
        return registryPrx;
    }
}

