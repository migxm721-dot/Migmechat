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
package com.projectgoth.fusion.userevent.store;

import Ice.Application;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.userevent.store.BDBEventStoreI;
import com.projectgoth.fusion.userevent.store.EventStoreAdminI;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

public class EventStore
extends Application {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(EventStore.class));
    public static long startTime = System.currentTimeMillis();
    public static ObjectAdapter EventStoreAdapter = null;
    public static Properties properties = null;
    private String hostName;
    private BDBEventStoreI eventStoreServant;

    public void setEventStoreServant(BDBEventStoreI userEventCacheServant) {
        this.eventStoreServant = userEventCacheServant;
    }

    public int run(String[] arg0) {
        properties = EventStore.communicator().getProperties();
        log.info((java.lang.Object)("Configured endpoint [" + properties.getProperty("EventStoreAdapter.Endpoints") + "]"));
        try {
            this.hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            this.hostName = "UNKNOWN";
        }
        log.debug((java.lang.Object)"Initialising EventStore interface");
        EventStoreAdapter = EventStore.communicator().createObjectAdapter("EventStoreAdapter");
        EventStoreAdapter.add((Object)this.eventStoreServant, Util.stringToIdentity((String)"EventStore"));
        log.debug((java.lang.Object)"Initialising EventStoreAdmin interface");
        ObjectAdapter EventStoreAdminAdapter = EventStore.communicator().createObjectAdapter("EventStoreAdminAdapter");
        EventStoreAdminI EventStoreAdmin2 = new EventStoreAdminI(this.eventStoreServant);
        EventStoreAdminAdapter.add((Object)EventStoreAdmin2, Util.stringToIdentity((String)"EventStoreAdmin"));
        EventStoreAdminAdapter.activate();
        EventStoreAdapter.activate();
        log.info((java.lang.Object)"Service started");
        EventStore.communicator().waitForShutdown();
        if (EventStore.interrupted()) {
            log.fatal((java.lang.Object)("EventStore " + this.hostName + ": terminating"));
            this.eventStoreServant.shutdownStore();
        }
        return 0;
    }
}

