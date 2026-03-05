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
 */
package com.projectgoth.fusion.userevent.system;

import Ice.Application;
import Ice.LocalException;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.RegistryPrxHelper;
import com.projectgoth.fusion.userevent.system.EventSystemAdminI;
import com.projectgoth.fusion.userevent.system.EventSystemI;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

public class EventSystem
extends Application {
    private static Logger log = Logger.getLogger(EventSystem.class);
    public static ObjectAdapter EventSystemAdapter = null;
    public static Properties properties = null;
    public static long startTime = System.currentTimeMillis();
    private RegistryPrx registryProxy = null;
    private String hostName;
    private EventSystemI eventSystemServant;

    public void setEventSystemServant(EventSystemI eventSystemServant) {
        this.eventSystemServant = eventSystemServant;
    }

    private void findRegistryProxy() {
        String registryStringifiedProxy = EventSystem.communicator().getProperties().getProperty("RegistryProxy");
        ObjectPrx basePrx = EventSystem.communicator().stringToProxy(registryStringifiedProxy);
        log.info((java.lang.Object)("Connecting to [" + basePrx + "]"));
        try {
            this.registryProxy = RegistryPrxHelper.checkedCast(basePrx);
        }
        catch (LocalException e) {
            log.error((java.lang.Object)("Registry " + this.hostName + ": Connection to [" + this.registryProxy + "] failed. "), (Throwable)e);
            return;
        }
        if (this.registryProxy == null) {
            log.error((java.lang.Object)("Registry " + this.hostName + ": Connection to [" + this.registryProxy + "] failed"));
            return;
        }
    }

    public int run(String[] arg0) {
        this.eventSystemServant.createProxies(EventSystem.communicator());
        properties = EventSystem.communicator().getProperties();
        log.info((java.lang.Object)("Configured endpoint [" + properties.getProperty("EventSystemAdapter.Endpoints") + "]"));
        try {
            this.hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            this.hostName = "UNKNOWN";
        }
        log.debug((java.lang.Object)"Initialising EventSystem interface");
        EventSystemAdapter = EventSystem.communicator().createObjectAdapter("EventSystemAdapter");
        EventSystemAdapter.add((Object)this.eventSystemServant, Util.stringToIdentity((String)"EventSystem"));
        log.debug((java.lang.Object)"Initialising EventSystemAdmin interface");
        ObjectAdapter EventSystemAdminAdapter = EventSystem.communicator().createObjectAdapter("EventSystemAdminAdapter");
        EventSystemAdminI EventSystemAdmin2 = new EventSystemAdminI(this.eventSystemServant);
        EventSystemAdminAdapter.add((Object)EventSystemAdmin2, Util.stringToIdentity((String)"EventSystemAdmin"));
        EventSystemAdminAdapter.activate();
        this.findRegistryProxy();
        while (this.registryProxy == null) {
            try {
                log.warn((java.lang.Object)"Still waiting for registry proxy to become available...");
                Thread.sleep(1000L);
                this.findRegistryProxy();
            }
            catch (InterruptedException e) {}
        }
        if (this.registryProxy == null) {
            return -1;
        }
        this.eventSystemServant.setRegistryProxy(this.registryProxy);
        EventSystemAdapter.activate();
        log.info((java.lang.Object)"Service started");
        EventSystem.communicator().waitForShutdown();
        if (EventSystem.interrupted()) {
            log.fatal((java.lang.Object)("EventSystem " + this.hostName + ": terminating"));
            this.eventSystemServant.shutdown();
        }
        return 0;
    }
}

