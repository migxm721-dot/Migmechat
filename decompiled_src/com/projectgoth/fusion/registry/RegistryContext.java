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
import com.projectgoth.fusion.registry.RegistryI;
import com.projectgoth.fusion.registry.RegistryNodeI;
import com.projectgoth.fusion.slice.RegistryNodePrx;

public interface RegistryContext {
    public Properties getProperties();

    public Communicator getCommunicator();

    public RegistryNodeI getRegistryNode();

    public RegistryI getRegistry();

    public ObjectAdapter getRegistryAdapter();

    public String getHostName();

    public RegistryNodePrx getThisNodePrx();
}

