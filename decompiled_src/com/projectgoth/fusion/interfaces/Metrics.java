/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.projectgoth.fusion.interfaces;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.EJBObject;

public interface Metrics
extends EJBObject {
    public boolean logMetricsSampleSummaries(String var1, String var2, Collection var3) throws RemoteException;
}

