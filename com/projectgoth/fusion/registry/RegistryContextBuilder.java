/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.ObjectAdapter
 *  Ice.Properties
 */
package com.projectgoth.fusion.registry;

import Ice.Communicator;
import Ice.ObjectAdapter;
import Ice.Properties;
import com.projectgoth.fusion.common.FusionApplicationContext;
import com.projectgoth.fusion.registry.RegistryContext;
import com.projectgoth.fusion.registry.RegistryI;
import com.projectgoth.fusion.registry.RegistryNodeI;
import com.projectgoth.fusion.slice.RegistryNodePrx;
import java.lang.ref.WeakReference;

public class RegistryContextBuilder
extends FusionApplicationContext
implements RegistryContext {
    private WeakReference<Properties> properties;
    private WeakReference<Communicator> communicator;
    private WeakReference<RegistryNodeI> registryNode;
    private WeakReference<RegistryI> registry;
    private WeakReference<ObjectAdapter> registryAdapter;
    private String hostName;
    private WeakReference<RegistryNodePrx> thisNodePrx;

    public Properties getProperties() {
        return this.extractProperty(this.properties);
    }

    public RegistryContextBuilder setProperties(Properties properties) {
        this.properties = new WeakReference<Properties>(properties);
        return this;
    }

    public Communicator getCommunicator() {
        return this.extractProperty(this.communicator);
    }

    public RegistryContextBuilder setCommunicator(Communicator communicator) {
        this.communicator = new WeakReference<Communicator>(communicator);
        return this;
    }

    public RegistryNodeI getRegistryNode() {
        return this.extractProperty(this.registryNode);
    }

    public RegistryContextBuilder setRegistryNode(RegistryNodeI registryNode) {
        this.registryNode = new WeakReference<RegistryNodeI>(registryNode);
        return this;
    }

    public RegistryI getRegistry() {
        return this.extractProperty(this.registry);
    }

    public RegistryContextBuilder setRegistry(RegistryI registry) {
        this.registry = new WeakReference<RegistryI>(registry);
        return this;
    }

    public ObjectAdapter getRegistryAdapter() {
        return this.extractProperty(this.registryAdapter);
    }

    public RegistryContextBuilder setRegistryAdapter(ObjectAdapter registryAdapter) {
        this.registryAdapter = new WeakReference<ObjectAdapter>(registryAdapter);
        return this;
    }

    public String getHostName() {
        return this.hostName;
    }

    public RegistryContextBuilder setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public RegistryNodePrx getThisNodePrx() {
        return this.extractProperty(this.thisNodePrx);
    }

    public RegistryContextBuilder setThisNodePrx(RegistryNodePrx thisNodePrx) {
        this.thisNodePrx = new WeakReference<RegistryNodePrx>(thisNodePrx);
        return this;
    }
}

