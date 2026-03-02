/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.EmailAlertStats;
import com.projectgoth.fusion.slice.FusionException;

public interface _EmailAlertAdminOperations {
    public EmailAlertStats getStats(Current var1) throws FusionException;
}

