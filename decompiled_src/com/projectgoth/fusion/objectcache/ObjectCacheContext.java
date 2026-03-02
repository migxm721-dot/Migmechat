/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.ObjectAdapter
 *  Ice.Properties
 */
package com.projectgoth.fusion.objectcache;

import Ice.Communicator;
import Ice.ObjectAdapter;
import Ice.Properties;
import com.projectgoth.fusion.mogilefs.MogileFSManager;
import com.projectgoth.fusion.objectcache.ObjectCacheInterface;
import com.projectgoth.fusion.slice.MessageLoggerPrx;
import com.projectgoth.fusion.slice.ObjectCacheAdminPrx;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SessionCachePrx;

public interface ObjectCacheContext {
    public ObjectCacheAdminPrx getAdminPrx();

    public ObjectAdapter getCacheAdapter();

    public Communicator getCommunicator();

    public MessageLoggerPrx getMessageLoggerPrx();

    public MogileFSManager getMogileFSManager();

    public ObjectCacheInterface getObjectCache();

    public Properties getProperties();

    public RegistryPrx getRegistryPrx();

    public SessionCachePrx getSessionCachePrx();

    public String getUniqueID();
}

