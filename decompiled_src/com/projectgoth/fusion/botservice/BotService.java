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
 */
package com.projectgoth.fusion.botservice;

import Ice.Application;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.botservice.BotServiceAdminI;
import com.projectgoth.fusion.botservice.BotServiceI;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.slice.BotServiceAdminPrx;
import com.projectgoth.fusion.slice.BotServiceAdminPrxHelper;
import com.projectgoth.fusion.slice.BotServicePrx;
import com.projectgoth.fusion.slice.BotServicePrxHelper;
import com.projectgoth.fusion.slice.BotServiceStats;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.RegistryPrxHelper;
import com.projectgoth.fusion.stats.IceStats;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class BotService
extends Application {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BotService.class));
    private ScheduledExecutorService executor;
    private BotServiceI botServiceServant;
    private String registryLocation;
    private int maxThreadPoolSize;
    private int botPurgerInterval;

    private boolean getSettings() {
        Properties properties = BotService.communicator().getProperties();
        this.registryLocation = properties.getProperty("RegistryProxy");
        this.maxThreadPoolSize = properties.getPropertyAsIntWithDefault("MaxThreadPoolSize", Integer.MAX_VALUE);
        this.botPurgerInterval = properties.getPropertyAsIntWithDefault("BotPurgerInterval", 60) * 1000;
        return true;
    }

    public BotServiceI getBotServiceServant() {
        return this.botServiceServant;
    }

    public void setBotServiceServant(BotServiceI botServiceServant) {
        this.botServiceServant = botServiceServant;
    }

    public BotServiceStats getStats() {
        return this.botServiceServant.getStats();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int run(String[] arg0) {
        block7: {
            int n;
            block6: {
                try {
                    try {
                        if (!this.getSettings()) {
                            n = -1;
                            Object var12_5 = null;
                            break block6;
                        }
                        this.executor = Executors.newScheduledThreadPool(this.maxThreadPoolSize > 0 ? this.maxThreadPoolSize : 1);
                        this.cleanupPendingPots();
                        log.info((Object)("Configured endpoint [" + BotService.communicator().getProperties().getProperty("BotServiceAdapter.Endpoints") + "]"));
                        ObjectAdapter serviceAdapter = BotService.communicator().createObjectAdapter("BotServiceAdapter");
                        ObjectPrx baseServiceProxy = serviceAdapter.add((Ice.Object)this.botServiceServant, Util.stringToIdentity((String)"BotService"));
                        BotServicePrx serviceProxy = BotServicePrxHelper.checkedCast(baseServiceProxy);
                        this.botServiceServant.setProxy(serviceProxy);
                        this.botServiceServant.setExecutor(this.executor);
                        serviceAdapter.activate();
                        IceStats.getInstance().setIceObjects(BotService.communicator(), serviceAdapter, null);
                        this.startIdleBotPurger();
                        ObjectAdapter adminAdapter = BotService.communicator().createObjectAdapter("BotServiceAdminAdapter");
                        ObjectPrx baseAdminProxy = adminAdapter.add((Ice.Object)new BotServiceAdminI(this), Util.stringToIdentity((String)"BotServiceAdmin"));
                        BotServiceAdminPrx adminProxy = BotServiceAdminPrxHelper.checkedCast(baseAdminProxy);
                        adminAdapter.activate();
                        String hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
                        ObjectPrx baseRegistryProxy = BotService.communicator().stringToProxy(this.registryLocation);
                        RegistryPrx registryProxy = RegistryPrxHelper.checkedCast(baseRegistryProxy);
                        registryProxy.registerBotService(hostName, 0, serviceProxy, adminProxy);
                        log.info((Object)("Registered with " + registryProxy + ". Service started"));
                        BotService.communicator().waitForShutdown();
                        log.info((Object)"Terminating server");
                        this.botServiceServant.shutdown();
                        break block7;
                    }
                    catch (Exception e) {
                        log.error((Object)"BotService could not be initialized", (Throwable)e);
                        Object var12_7 = null;
                        if (!BotService.interrupted()) return 0;
                        log.fatal((Object)"BotService: terminating");
                        this.botServiceServant.shutdown();
                        return 0;
                    }
                }
                catch (Throwable throwable) {
                    Object var12_8 = null;
                    if (!BotService.interrupted()) throw throwable;
                    log.fatal((Object)"BotService: terminating");
                    this.botServiceServant.shutdown();
                    throw throwable;
                }
            }
            if (!BotService.interrupted()) return n;
            log.fatal((Object)"BotService: terminating");
            this.botServiceServant.shutdown();
            return n;
        }
        Object var12_6 = null;
        if (!BotService.interrupted()) return 0;
        log.fatal((Object)"BotService: terminating");
        this.botServiceServant.shutdown();
        return 0;
    }

    private void startIdleBotPurger() {
        this.executor.scheduleAtFixedRate(new IdleBotPurger(), 0L, this.botPurgerInterval, TimeUnit.SECONDS);
    }

    private void cleanupPendingPots() {
        log.debug((Object)"Running PotCleanupHandler...");
        try {
            Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
            accountEJB.cancelAllPots(new AccountEntrySourceData(BotService.class));
        }
        catch (Exception e) {
            log.error((Object)"cleanupPendingPots threw an exception: ", (Throwable)e);
        }
        log.debug((Object)"End running PotCleanupHandler...");
    }

    private class IdleBotPurger
    implements Runnable {
        private IdleBotPurger() {
        }

        public void run() {
            log.debug((Object)"Running IdleBotPurger...");
            BotService.this.botServiceServant.purgeIdleBots();
            log.debug((Object)"End running IdleBotPurger...");
        }
    }
}

