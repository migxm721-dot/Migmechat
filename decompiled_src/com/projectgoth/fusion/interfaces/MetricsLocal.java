/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import java.util.Collection;
import javax.ejb.EJBLocalObject;

public interface MetricsLocal
extends EJBLocalObject {
    public boolean logMetricsSampleSummaries(String var1, String var2, Collection var3);
}

