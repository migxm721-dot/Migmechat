/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Properties
 *  Ice.Util
 *  com.danga.MemCached.MemCachedClient
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.gateway;

import Ice.Application;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.CaptchaService;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.gateway.GatewayAdminI;
import com.projectgoth.fusion.gateway.GatewayContextBuilder;
import com.projectgoth.fusion.gateway.GatewayWS;
import com.projectgoth.fusion.gateway.InstrumentedThreadPool;
import com.projectgoth.fusion.gateway.PurgeConnectionTask;
import com.projectgoth.fusion.gateway.SamplingTask;
import com.projectgoth.fusion.ice.IceThreadMonitor;
import com.projectgoth.fusion.mogilefs.MogileFSManager;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.stats.IceStats;
import java.io.IOException;
import java.nio.channels.Selector;
import java.util.EnumMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class GatewayMain
extends Application {
    protected static final String APP_NAME = "Gateway";
    protected static final String CONFIG_FILE = "Gateway.cfg";
    static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Gateway.class));
    Map<Gateway.ThreadPoolName, InstrumentedThreadPool> pools = new EnumMap<Gateway.ThreadPoolName, InstrumentedThreadPool>(Gateway.ThreadPoolName.class);
    Selector selector;
    private PurgeConnectionTask purger;
    Gateway.ServerType serverType;
    private ObjectAdapter connectionAdaptor;
    private ObjectAdapter adminAdaptor;
    private RegistryPrx registryPrx;
    private SamplingTask sampler;
    private MogileFSManager mogileFSManager;
    private CaptchaService captchaService;
    private MemCachedClient captchaMemcache;
    int timeoutInterval;
    private Gateway gatewayObject;
    private GatewayContextBuilder gatewayContext;
    private IcePrxFinder icePrxFinder;
    private Properties properties;
    private int id;
    private static IceThreadMonitor iceThreadMonitor;

    protected Gateway createGateway(GatewayContextBuilder gatewayContext) {
        String strServerType = gatewayContext.getProperties().getPropertyWithDefault("ServerType", "TCP");
        if (strServerType.equalsIgnoreCase("WS")) {
            return new GatewayWS(gatewayContext);
        }
        return new Gateway(gatewayContext);
    }

    public int run(String[] arg0) {
        try {
            log.info((Object)"Gateway version @version@");
            log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
            this.gatewayContext = new GatewayContextBuilder();
            this.icePrxFinder = new IcePrxFinder(GatewayMain.communicator(), GatewayMain.communicator().getProperties());
            this.gatewayContext.setStartTime(System.currentTimeMillis());
            this.properties = GatewayMain.communicator().getProperties();
            this.gatewayContext.setProperties(this.properties);
            this.gatewayContext.setCommunicator(GatewayMain.communicator());
            this.gatewayContext.setGatewayThreadPool(this.pools);
            this.gatewayContext.setIcePrxFinder(this.icePrxFinder);
            this.gatewayObject = SystemPropertyEntities.Temp.Cache.se493WebSocketsEnabled.getValue() != false ? this.createGateway(this.gatewayContext) : new Gateway(this.gatewayContext);
            this.registryPrx = this.icePrxFinder.getRegistry(true);
            this.gatewayContext.setRegistryPrx(this.registryPrx);
            if (this.registryPrx == null) {
                throw new Exception("Unable to locate Registry");
            }
            this.id = this.registryPrx.newGatewayID();
            this.mogileFSManager = new MogileFSManager(this.properties.getPropertyWithDefault("MogileFSDomain", ""), this.properties.getPropertyWithDefault("MogileFSTrackers", "").split("[,;]"), this.properties.getPropertyAsIntWithDefault("MogileFSConnectionsPerTracker", 1), log);
            this.gatewayContext.setMogileFSManager(this.mogileFSManager);
            this.captchaMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.captcha);
            if (this.captchaMemcache == null) {
                log.warn((Object)"Failed to initialize memcached for CAPTCHA service. Local cache will be used instead");
            }
            this.captchaService = new CaptchaService(this.captchaMemcache, true);
            this.gatewayContext.setCaptchaService(this.captchaService);
            for (Gateway.ThreadPoolName name : Gateway.ThreadPoolName.values()) {
                int maxSize = GatewayMain.communicator().getProperties().getPropertyAsIntWithDefault("ThreadPool." + (Object)((Object)name) + ".MaxSize", 40);
                int maxQueueSize = GatewayMain.communicator().getProperties().getPropertyAsIntWithDefault("ThreadPool." + (Object)((Object)name) + ".MaxQueueSize", Integer.MAX_VALUE);
                if (maxSize <= 0 || maxSize == Integer.MAX_VALUE) {
                    this.pools.put(name, new InstrumentedThreadPool());
                    continue;
                }
                this.pools.put(name, new InstrumentedThreadPool(maxSize, maxQueueSize));
            }
            this.selector = Selector.open();
            this.gatewayContext.setSelector(this.selector);
            this.gatewayObject.bindServerSocket();
            this.connectionAdaptor = GatewayMain.communicator().createObjectAdapter("ConnectionAdapter");
            this.connectionAdaptor.activate();
            this.gatewayContext.setConnectionAdapter(this.connectionAdaptor);
            IceStats.getInstance().setIceObjects(GatewayMain.communicator(), this.connectionAdaptor, null);
            if (SystemProperty.getBool(SystemPropertyEntities.IceThreadMonitorSettings.GWAY_TPOOL_MONITOR_STARTUP_ENABLED)) {
                iceThreadMonitor = new IceThreadMonitor(this.connectionAdaptor, SystemPropertyEntities.IceThreadMonitorSettings.GWAY_TPOOL_MONITOR_THREAD_COUNT, SystemPropertyEntities.IceThreadMonitorSettings.GWAY_TPOOL_MONITOR_MINIMUM_INTER_DUMP_DURATION);
                iceThreadMonitor.start(60);
            }
            this.adminAdaptor = GatewayMain.communicator().createObjectAdapter("AdminAdapter");
            this.adminAdaptor.add((Ice.Object)new GatewayAdminI(this.gatewayObject), Util.stringToIdentity((String)"GatewayAdmin"));
            this.adminAdaptor.activate();
            int scanInterval = GatewayMain.communicator().getProperties().getPropertyAsIntWithDefault("IdleConnetionScanIntrval", 60) * 1000;
            this.purger = new PurgeConnectionTask(this.gatewayObject, this.gatewayContext);
            this.gatewayContext.setPurger(this.purger);
            new Timer().scheduleAtFixedRate((TimerTask)this.purger, scanInterval, (long)scanInterval);
            this.sampler = new SamplingTask(this.gatewayContext);
            this.gatewayContext.setSamplingTask(this.sampler);
            this.gatewayContext.build();
            this.pools.get((Object)Gateway.ThreadPoolName.PRIMARY).execute(this.sampler);
            log.info((Object)("Server started - ID: " + this.id + " Type: " + (Object)((Object)this.serverType) + ", Port: " + this.gatewayObject.getLocalPort() + "\n"));
            this.pools.get((Object)Gateway.ThreadPoolName.PRIMARY).execute(this.gatewayObject);
            GatewayMain.communicator().waitForShutdown();
            log.info((Object)("Application is shutting down. Interrupted = " + GatewayMain.interrupted()));
            try {
                this.gatewayObject.closeServerChannel();
            }
            catch (IOException e) {
                // empty catch block
            }
            for (InstrumentedThreadPool pool : this.pools.values()) {
                pool.shutdown();
            }
            log.info((Object)"Server terminated");
            return 0;
        }
        catch (Exception e) {
            log.fatal((Object)"Exception occured. Server terminated", (Throwable)e);
            return -1;
        }
    }

    public static void main(String[] args) {
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        GatewayMain gateway = new GatewayMain();
        String configFile = args.length > 0 ? args[0] : CONFIG_FILE;
        int status = gateway.main(((Object)((Object)gateway)).getClass().getName(), args, configFile);
        System.exit(status);
    }
}

