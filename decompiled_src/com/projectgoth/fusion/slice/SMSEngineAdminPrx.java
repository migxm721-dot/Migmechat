/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SMSEngineStats;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface SMSEngineAdminPrx
extends ObjectPrx {
    public SMSEngineStats getStats() throws FusionException;

    public SMSEngineStats getStats(Map<String, String> var1) throws FusionException;
}

