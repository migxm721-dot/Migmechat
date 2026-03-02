/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.InitializationData
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import Ice.Communicator;
import Ice.InitializationData;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

public class BasicIceServerApp {
    private final Communicator communicator;
    private final LinkedHashMap<String, ObjectAdapter> endPointToObjectAdapterMap = new LinkedHashMap();
    private final Logger logger;

    private static Logger getLogger(String name) {
        return Logger.getLogger((String)(ConfigUtils.getLoggerName(BasicIceServerApp.class) + (StringUtil.isBlank(name) ? "" : "|" + name)));
    }

    private static Communicator buildCommunicator(String configFile, Properties overrideProperties) {
        Ice.Properties iceProperties = Util.createProperties();
        if (!StringUtil.isBlank(configFile)) {
            iceProperties.load(configFile);
        }
        if (overrideProperties != null && !overrideProperties.isEmpty()) {
            for (Map.Entry<java.lang.Object, java.lang.Object> entry : overrideProperties.entrySet()) {
                if (entry.getKey() == null) continue;
                String overridenKey = entry.getKey().toString();
                if (entry.getValue() == null) continue;
                String overridenValue = entry.getValue().toString();
                iceProperties.setProperty(overridenKey, overridenValue);
            }
        }
        InitializationData iceInitData = new InitializationData();
        iceInitData.properties = iceProperties;
        return Util.initialize((InitializationData)iceInitData);
    }

    public BasicIceServerApp(String name, String configFile) {
        this(name, configFile, null);
    }

    public BasicIceServerApp(String name, String configFile, Properties overrideProperties) {
        this.logger = BasicIceServerApp.getLogger(name);
        this.communicator = BasicIceServerApp.buildCommunicator(configFile, overrideProperties);
    }

    public BasicIceServerApp(String name, Communicator communicator) {
        this.logger = BasicIceServerApp.getLogger(name);
        this.communicator = communicator;
    }

    private ObjectAdapter addObjectAdapter(String endPointName) {
        ObjectAdapter adapter = this.communicator.createObjectAdapter(endPointName);
        this.endPointToObjectAdapterMap.put(endPointName, adapter);
        return adapter;
    }

    public void addServantObject(String endPointName, Object iceObject, String identityStr) {
        ObjectAdapter objectAdapter = this.endPointToObjectAdapterMap.get(endPointName);
        if (objectAdapter == null) {
            objectAdapter = this.addObjectAdapter(endPointName);
        }
        objectAdapter.add(iceObject, Util.stringToIdentity((String)identityStr));
    }

    private void activateAdapters() {
        for (Map.Entry<String, ObjectAdapter> entry : this.endPointToObjectAdapterMap.entrySet()) {
            String endPointName = entry.getKey();
            ObjectAdapter objectAdapter = entry.getValue();
            this.logger.info((java.lang.Object)("Activating endpoint adapter:[" + endPointName + "]"));
            objectAdapter.activate();
            this.logger.info((java.lang.Object)("Endpoint adapter:[" + endPointName + "] activated."));
        }
    }

    public void activateAdaptersAndWaitTillShutdown() {
        this.activateAdapters();
        this.logger.info((java.lang.Object)"Services started.");
        this.communicator.waitForShutdown();
    }

    public void shutdown() {
        this.logger.info((java.lang.Object)"Shutting down communicator");
        this.communicator.shutdown();
    }

    public static Thread startInANewThread(String threadName, boolean isDaemon, final BasicIceServerApp serverApp) {
        Thread thread = new Thread(new Runnable(){

            public void run() {
                serverApp.activateAdaptersAndWaitTillShutdown();
                serverApp.logger.info((java.lang.Object)"Servicing thread terminating...");
            }
        }, threadName);
        thread.setDaemon(isDaemon);
        serverApp.logger.info((java.lang.Object)"Starting initiator thread for ice app");
        thread.start();
        return thread;
    }
}

