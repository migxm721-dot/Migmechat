/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectAdapter
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import Ice.ObjectAdapter;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.objectcache.ObjectCacheContext;
import com.projectgoth.fusion.slice.AuthenticationServicePrx;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SessionCachePrx;
import org.apache.log4j.Logger;

public class ChatProxyProvider {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatProxyProvider.class));
    String uniqueID;
    ObjectCacheContext objectCache;
    RequestCounter requestCounter;
    RegistryPrx registryPrx;
    AuthenticationServicePrx authServicePrx;
    SessionCachePrx sessionCachePrx;
    ObjectAdapter cacheAdapter;
    IcePrxFinder icePrxFinder;

    public ChatProxyProvider(ObjectCacheContext applicationContext, RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
        this.icePrxFinder = new IcePrxFinder(applicationContext.getCommunicator(), applicationContext.getProperties());
        this.authServicePrx = this.icePrxFinder.waitForAuthenticationServiceProxy();
        this.registryPrx = this.objectCache.getRegistryPrx();
        this.cacheAdapter = this.objectCache.getCacheAdapter();
        this.sessionCachePrx = this.objectCache.getSessionCachePrx();
        this.uniqueID = this.objectCache.getUniqueID();
    }
}

