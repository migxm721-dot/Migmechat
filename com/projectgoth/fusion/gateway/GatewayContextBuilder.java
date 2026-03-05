/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.ObjectAdapter
 *  Ice.Properties
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway;

import Ice.Communicator;
import Ice.ObjectAdapter;
import Ice.Properties;
import com.projectgoth.fusion.common.CaptchaService;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.FusionApplicationContext;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.gateway.GatewayContext;
import com.projectgoth.fusion.gateway.InstrumentedThreadPool;
import com.projectgoth.fusion.gateway.PurgeConnectionTask;
import com.projectgoth.fusion.gateway.SamplingTask;
import com.projectgoth.fusion.mogilefs.DFSManager;
import com.projectgoth.fusion.slice.RegistryPrx;
import java.lang.ref.WeakReference;
import java.nio.channels.Selector;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GatewayContextBuilder
extends FusionApplicationContext
implements GatewayContext {
    static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GatewayContextBuilder.class));
    private Properties properties;
    private Communicator communicator;
    private long startTime;
    private WeakReference<Map<Gateway.ThreadPoolName, InstrumentedThreadPool>> pools;
    private WeakReference<Selector> selector;
    private WeakReference<PurgeConnectionTask> purger;
    private WeakReference<ObjectAdapter> connectionAdapter;
    private WeakReference<DFSManager> mogileFSManager;
    private WeakReference<IcePrxFinder> icePrxFinder;
    private WeakReference<SamplingTask> samplingTask;
    private WeakReference<CaptchaService> captchaService;
    private AtomicReference<RegistryPrx> registryPrx = new AtomicReference();

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    public GatewayContextBuilder setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public Communicator getCommunicator() {
        return this.communicator;
    }

    public GatewayContextBuilder setCommunicator(Communicator communicator) {
        this.communicator = communicator;
        return this;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    public GatewayContextBuilder setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    public GatewayContextBuilder setGatewayThreadPool(Map<Gateway.ThreadPoolName, InstrumentedThreadPool> pools) {
        this.pools = new WeakReference<Map<Gateway.ThreadPoolName, InstrumentedThreadPool>>(pools);
        return this;
    }

    @Override
    public Map<Gateway.ThreadPoolName, InstrumentedThreadPool> getGatewayThreadPool() {
        return this.extractProperty(this.pools);
    }

    public GatewayContextBuilder setSelector(Selector selector) {
        this.selector = new WeakReference<Selector>(selector);
        return this;
    }

    @Override
    public Selector getSelector() {
        return this.extractProperty(this.selector);
    }

    public GatewayContextBuilder setPurger(PurgeConnectionTask purger) {
        this.purger = new WeakReference<PurgeConnectionTask>(purger);
        return this;
    }

    @Override
    public PurgeConnectionTask getPurger() {
        return this.extractProperty(this.purger);
    }

    public GatewayContextBuilder setConnectionAdapter(ObjectAdapter connAdapter) {
        this.connectionAdapter = new WeakReference<ObjectAdapter>(connAdapter);
        return this;
    }

    @Override
    public ObjectAdapter getConnectionAdapter() {
        return this.extractProperty(this.connectionAdapter);
    }

    public GatewayContextBuilder setMogileFSManager(DFSManager mogileFSManager) {
        this.mogileFSManager = new WeakReference<DFSManager>(mogileFSManager);
        return this;
    }

    @Override
    public DFSManager getMogileFSManager() {
        return this.extractProperty(this.mogileFSManager);
    }

    public GatewayContextBuilder setIcePrxFinder(IcePrxFinder icePrxFinder) {
        this.icePrxFinder = new WeakReference<IcePrxFinder>(icePrxFinder);
        return this;
    }

    @Override
    public IcePrxFinder getIcePrxFinder() {
        return this.extractProperty(this.icePrxFinder);
    }

    public GatewayContextBuilder setSamplingTask(SamplingTask samplingTask) {
        this.samplingTask = new WeakReference<SamplingTask>(samplingTask);
        return this;
    }

    @Override
    public SamplingTask getSamplingTask() {
        return this.extractProperty(this.samplingTask);
    }

    public GatewayContextBuilder setCaptchaService(CaptchaService captchaService) {
        this.captchaService = new WeakReference<CaptchaService>(captchaService);
        return this;
    }

    @Override
    public CaptchaService getCaptchaService() {
        return this.extractProperty(this.captchaService);
    }

    public synchronized void setRegistryPrx(RegistryPrx rp) {
        this.registryPrx.set(rp);
    }

    @Override
    public RegistryPrx getRegistryPrx() {
        return this.registryPrx.get();
    }
}

