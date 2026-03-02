/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Communicator
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.ObjectPrx
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.registry;

import Ice.Application;
import Ice.Communicator;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.registry.RegistryAdminI;
import com.projectgoth.fusion.registry.RegistryContextBuilder;
import com.projectgoth.fusion.registry.RegistryI;
import com.projectgoth.fusion.registry.RegistryNodeI;
import com.projectgoth.fusion.slice.RegistryNodePrx;
import com.projectgoth.fusion.slice.RegistryNodePrxHelper;
import com.projectgoth.fusion.stats.IceStats;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class Registry
extends Application {
    private static final String APP_NAME = "Registry";
    private static final String CONFIG_FILE = "Registry.cfg";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Registry.class));
    private static ObjectAdapter registryAdapter = null;
    private static Properties properties = null;
    private static RegistryNodePrx thisNodePrx = null;
    private static RegistryI registry = null;
    private static RegistryNodeI registryNode = null;
    private static String hostName = null;
    public static long startTime = System.currentTimeMillis();
    private static Communicator communicator;

    public static void main(String[] args) {
        int status;
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        log.info((java.lang.Object)"Registry version @version@");
        log.info((java.lang.Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        Registry app = new Registry();
        if (args.length >= 1) {
            log.info((java.lang.Object)("Using custom configuration file: " + args[0]));
            status = app.main(APP_NAME, args, args[0]);
        } else {
            status = app.main(APP_NAME, args, CONFIG_FILE);
        }
        System.exit(status);
    }

    public int run(String[] arg0) {
        properties = Registry.communicator().getProperties();
        try {
            hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            hostName = "UNKNOWN";
        }
        RegistryContextBuilder applicationContext = new RegistryContextBuilder();
        applicationContext.setProperties(properties).setCommunicator(Registry.communicator());
        log.debug((java.lang.Object)"Initialising RegistryNode interface");
        ObjectAdapter registryNodeAdapter = Registry.communicator().createObjectAdapter("RegistryNodeAdapter");
        registryNode = new RegistryNodeI(applicationContext);
        applicationContext.setRegistryNode(registryNode);
        ObjectPrx basePrx = registryNodeAdapter.add((Object)registryNode, Util.stringToIdentity((String)"RegistryNode"));
        thisNodePrx = RegistryNodePrxHelper.uncheckedCast(basePrx);
        applicationContext.setThisNodePrx(thisNodePrx);
        registryNodeAdapter.activate();
        log.debug((java.lang.Object)"Initialising Registry interface");
        registryAdapter = Registry.communicator().createObjectAdapter("RegistryAdapter");
        IceStats.getInstance().setIceObjects(Registry.communicator(), registryAdapter, null);
        registry = new RegistryI(applicationContext);
        applicationContext.setRegistry(registry);
        registryAdapter.add((Object)registry, Util.stringToIdentity((String)APP_NAME));
        applicationContext.build();
        log.debug((java.lang.Object)"Initialising RegistryAdmin interface");
        ObjectAdapter registryAdminAdapter = Registry.communicator().createObjectAdapter("RegistryAdminAdapter");
        RegistryAdminI registryAdmin = new RegistryAdminI(applicationContext);
        registryAdminAdapter.add((Object)registryAdmin, Util.stringToIdentity((String)"RegistryAdmin"));
        registryAdminAdapter.activate();
        try {
            registryNode.convergeWithCluster();
        }
        catch (Exception e) {
            return 0;
        }
        registryAdapter.activate();
        log.info((java.lang.Object)"Service started");
        Registry.communicator().waitForShutdown();
        if (Registry.interrupted()) {
            log.fatal((java.lang.Object)("Registry " + hostName + ": terminating"));
        }
        return 0;
    }
}

