/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageSwitchboardStats;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface MessageSwitchboardAdminPrx
extends ObjectPrx {
    public MessageSwitchboardStats getStats() throws FusionException;

    public MessageSwitchboardStats getStats(Map<String, String> var1) throws FusionException;
}

