/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.ObjectAdapter
 *  Ice.Properties
 */
package com.projectgoth.fusion.gateway;

import Ice.Communicator;
import Ice.ObjectAdapter;
import Ice.Properties;
import com.projectgoth.fusion.common.CaptchaService;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.gateway.InstrumentedThreadPool;
import com.projectgoth.fusion.gateway.PurgeConnectionTask;
import com.projectgoth.fusion.gateway.SamplingTask;
import com.projectgoth.fusion.mogilefs.DFSManager;
import com.projectgoth.fusion.slice.RegistryPrx;
import java.nio.channels.Selector;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface GatewayContext {
    public Properties getProperties();

    public Communicator getCommunicator();

    public long getStartTime();

    public Map<Gateway.ThreadPoolName, InstrumentedThreadPool> getGatewayThreadPool();

    public Selector getSelector();

    public PurgeConnectionTask getPurger();

    public ObjectAdapter getConnectionAdapter();

    public DFSManager getMogileFSManager();

    public IcePrxFinder getIcePrxFinder();

    public SamplingTask getSamplingTask();

    public CaptchaService getCaptchaService();

    public RegistryPrx getRegistryPrx();
}

