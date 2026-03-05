/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Communicator
 *  Ice.LocalException
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.ObjectPrx
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.objectcache;

import Ice.Application;
import Ice.Communicator;
import Ice.LocalException;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.datagrid.DataGridFactory;
import com.projectgoth.fusion.ice.IceThreadMonitor;
import com.projectgoth.fusion.mogilefs.MogileFSManager;
import com.projectgoth.fusion.objectcache.ObjectCacheAdminI;
import com.projectgoth.fusion.objectcache.ObjectCacheContextBuilder;
import com.projectgoth.fusion.objectcache.ObjectCacheIceAmdInvoker;
import com.projectgoth.fusion.objectcache.ObjectCacheInterface;
import com.projectgoth.fusion.objectcache.ObjectCacheRpcI;
import com.projectgoth.fusion.slice.MessageLoggerPrx;
import com.projectgoth.fusion.slice.MessageLoggerPrxHelper;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.ObjectCacheAdminPrx;
import com.projectgoth.fusion.slice.ObjectCacheAdminPrxHelper;
import com.projectgoth.fusion.slice.ObjectCachePrx;
import com.projectgoth.fusion.slice.ObjectCachePrxHelper;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.RegistryPrxHelper;
import com.projectgoth.fusion.slice.SessionCachePrx;
import com.projectgoth.fusion.slice.SessionCachePrxHelper;
import com.projectgoth.fusion.stats.FusionStatsIceDispatchInterceptor;
import com.projectgoth.fusion.stats.IceStats;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class ObjectCache
extends Application {
    private static final String APP_NAME = "ObjectCache";
    private static final String CONFIG_FILE = "ObjectCache.cfg";
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ObjectCache.class));
    private static ObjectAdapter cacheAdapter;
    private static RegistryPrx registryPrx;
    private static MessageSwitchboardPrx messageSwitchboardPrx;
    private static SessionCachePrx sessionCachePrx;
    private static MessageLoggerPrx messageLoggerPrx;
    private static ObjectCachePrx objectCachePrx;
    private static Communicator communicator;
    private static Properties properties;
    private static ObjectCacheInterface objectCache;
    public static String hostName;
    public static long startTime;
    public static boolean logMessagesToDB;
    public static boolean logMessagesToFile;
    private static MogileFSManager mogileFSManager;
    public static final int BOT_MESSAGE_COLOUR = 34734;
    public static final int GIFT_ALL_MESSAGE_COLOUR = 0xFF00FF;
    public static final int GIFT_ALL_MESSAGE_COLOUR_LOW_PRICE = 0;
    public static final String GIFT_ALL_EMOTE_HOTKEY = "(shower)";
    public static final int CHATROOM_ANNOUNCE_MESSAGE_COLOUR = 0x770000;
    public static final String CHATROOM_ANNOUNCE_EMOTE_HOTKEY = "(announce)";
    public static final int CHATROOM_BROADCAST_MESSAGE_COLOUR = 0x770000;
    private static String uniqueID;
    private static IceThreadMonitor iceThreadMonitor;
    private ObjectCacheAdminPrx adminPrx;

    public static void main(String[] args) {
        int status;
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        log.info((Object)"ObjectCache version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        ObjectCache app = new ObjectCache();
        if (args.length >= 1) {
            log.info((Object)("Using custom configuration file: " + args[0]));
            status = app.main(APP_NAME, args, args[0]);
        } else {
            status = app.main(APP_NAME, args, CONFIG_FILE);
        }
        System.exit(status);
    }

    public int run(String[] arg0) {
        ObjectCacheContextBuilder ctx = new ObjectCacheContextBuilder();
        communicator = ObjectCache.communicator();
        properties = ObjectCache.communicator().getProperties();
        ctx.setProperties(properties);
        ctx.setCommunicator(communicator);
        try {
            hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            hostName = "UNKNOWN";
        }
        logMessagesToDB = ObjectCache.communicator().getProperties().getPropertyAsIntWithDefault("LogMessagesToDB", 1) == 1;
        logMessagesToFile = ObjectCache.communicator().getProperties().getPropertyAsIntWithDefault("LogMessagesToFile", 0) == 1;
        try {
            String domain = ObjectCache.communicator().getProperties().getProperty("MogileFSDomain");
            String[] trackers = ObjectCache.communicator().getProperties().getProperty("MogileFSTrackers").split("[,;]");
            int connectionsPerTracker = ObjectCache.communicator().getProperties().getPropertyAsIntWithDefault("MogileFSConnectionsPerTracker", 1);
            mogileFSManager = new MogileFSManager(domain, trackers, connectionsPerTracker, log);
            ctx.setMogileFSManager(mogileFSManager);
        }
        catch (Exception e) {
            log.fatal((Object)("Object Cache " + hostName + ": Failed to initialize MogileFS. Exception: " + e.toString()));
            return 1;
        }
        String registryStringifiedProxy = ObjectCache.communicator().getProperties().getProperty("RegistryProxy");
        ObjectPrx basePrx = ObjectCache.communicator().stringToProxy(registryStringifiedProxy);
        log.info((Object)("Connecting to [" + basePrx + "]"));
        try {
            registryPrx = RegistryPrxHelper.checkedCast(basePrx);
            ctx.setRegistryPrx(registryPrx);
        }
        catch (LocalException e) {
            log.fatal((Object)("Object Cache " + hostName + ": Connection to [" + registryPrx + "] failed. "), (Throwable)e);
            return 1;
        }
        if (registryPrx == null) {
            log.fatal((Object)("Object Cache " + hostName + ": Connection to [" + registryPrx + "] failed"));
            return 1;
        }
        String sessionCacheStringifiedProxy = ObjectCache.communicator().getProperties().getProperty("SessionCacheProxy");
        log.info((Object)("Connecting to a SessionCache [" + sessionCacheStringifiedProxy + "]"));
        basePrx = ObjectCache.communicator().stringToProxy(sessionCacheStringifiedProxy);
        try {
            sessionCachePrx = SessionCachePrxHelper.uncheckedCast(basePrx.ice_oneway());
            sessionCachePrx = (SessionCachePrx)sessionCachePrx.ice_connectionId("OneWayProxyGroup");
            ctx.setSessionCachePrx(sessionCachePrx);
        }
        catch (LocalException e) {
            log.fatal((Object)("Object Cache " + hostName + ": Connection to a SessionCache failed."), (Throwable)e);
            return 1;
        }
        if (sessionCachePrx == null) {
            log.fatal((Object)("Object Cache " + hostName + ": Connection to a SessionCache failed, the session cache proxy is null."));
            return 1;
        }
        if (logMessagesToFile) {
            String messageLoggerStringifiedProxy = ObjectCache.communicator().getProperties().getProperty("MessageLoggerProxy");
            log.info((Object)"Connecting to a MessageLogger");
            basePrx = ObjectCache.communicator().stringToProxy(messageLoggerStringifiedProxy);
            try {
                messageLoggerPrx = MessageLoggerPrxHelper.uncheckedCast(basePrx.ice_oneway());
                messageLoggerPrx = (MessageLoggerPrx)messageLoggerPrx.ice_connectionId("OneWayProxyGroup");
                ctx.setMessageLoggerPrx(messageLoggerPrx);
            }
            catch (LocalException e) {
                log.fatal((Object)("Object Cache " + hostName + ": Connection to a MessageLogger failed. Exception: " + e.toString()));
                return 1;
            }
            if (messageLoggerPrx == null) {
                log.fatal((Object)("Object Cache " + hostName + ": Connection to a MessageLogger failed"));
                return 1;
            }
        }
        log.debug((Object)"Initialising ObjectCacheAdmin interface");
        ObjectAdapter adminAdapter = ObjectCache.communicator().createObjectAdapter("ObjectCacheAdminAdapter");
        ObjectCacheAdminI admin = new ObjectCacheAdminI(ctx);
        basePrx = adminAdapter.add((Ice.Object)admin, Util.stringToIdentity((String)"ObjectCacheAdmin"));
        this.adminPrx = ObjectCacheAdminPrxHelper.uncheckedCast(basePrx);
        ctx.setAdminPrx(this.adminPrx);
        adminAdapter.activate();
        log.debug((Object)"Initialising ObjectCache interface");
        cacheAdapter = ObjectCache.communicator().createObjectAdapter("ObjectCacheAdapter");
        ctx.setCacheAdapter(cacheAdapter);
        objectCache = new ObjectCacheRpcI(ctx);
        ctx.setObjectCache(objectCache);
        IceStats.getInstance().setIceObjects(ObjectCache.communicator(), cacheAdapter, ObjectCacheIceAmdInvoker.getExecutor());
        if (SystemProperty.getBool(SystemPropertyEntities.IceThreadMonitorSettings.OBJC_TPOOL_MONITOR_STARTUP_ENABLED)) {
            iceThreadMonitor = new IceThreadMonitor(cacheAdapter, SystemPropertyEntities.IceThreadMonitorSettings.OBJC_TPOOL_MONITOR_THREAD_COUNT, SystemPropertyEntities.IceThreadMonitorSettings.OBJC_TPOOL_MONITOR_MINIMUM_INTER_DUMP_DURATION);
            iceThreadMonitor.start(60);
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Default.ICE_CONNECTION_STATS)) {
            log.info((Object)"Enabling FusionStatsIceDispatchInterceptor for ObjectCache itself");
            FusionStatsIceDispatchInterceptor interceptor = new FusionStatsIceDispatchInterceptor(objectCache);
            basePrx = cacheAdapter.add((Ice.Object)interceptor, Util.stringToIdentity((String)APP_NAME));
        } else {
            basePrx = cacheAdapter.add((Ice.Object)objectCache, Util.stringToIdentity((String)APP_NAME));
        }
        objectCachePrx = ObjectCachePrxHelper.uncheckedCast(basePrx);
        cacheAdapter.activate();
        uniqueID = objectCachePrx.ice_getEndpoints()[0].toString();
        ctx.setUniqueID(uniqueID);
        ctx.build();
        registryPrx.registerObjectCache(uniqueID, objectCachePrx, this.adminPrx);
        try {
            if (SystemProperty.getBool(SystemPropertyEntities.DataGridSettings.ENABLED)) {
                log.info((Object)"Object cache: starting data grid");
                DataGridFactory.getInstance().getGrid().prepare();
            }
        }
        catch (Exception e) {
            log.fatal((Object)("Object Cache " + hostName + ": Failed to initialize data grid. Exception: " + e.toString()));
            return 1;
        }
        admin.startTimer();
        log.info((Object)"Service started");
        ObjectCache.communicator().waitForShutdown();
        if (ObjectCache.interrupted()) {
            log.fatal((Object)"Application was interrupted, shutting down");
        } else {
            log.info((Object)"Application is shutting down");
        }
        return 0;
    }

    static {
        startTime = System.currentTimeMillis();
    }
}

