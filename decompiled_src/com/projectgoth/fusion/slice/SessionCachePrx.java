/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.SessionIce;
import com.projectgoth.fusion.slice.SessionMetricsIce;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface SessionCachePrx
extends ObjectPrx {
    public void logSession(SessionIce var1, SessionMetricsIce var2);

    public void logSession(SessionIce var1, SessionMetricsIce var2, Map<String, String> var3);
}

