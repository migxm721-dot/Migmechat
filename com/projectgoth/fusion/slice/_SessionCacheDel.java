/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice._ObjectDel
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice._ObjectDel;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.SessionIce;
import com.projectgoth.fusion.slice.SessionMetricsIce;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _SessionCacheDel
extends _ObjectDel {
    public void logSession(SessionIce var1, SessionMetricsIce var2, Map<String, String> var3) throws LocalExceptionWrapper;
}

