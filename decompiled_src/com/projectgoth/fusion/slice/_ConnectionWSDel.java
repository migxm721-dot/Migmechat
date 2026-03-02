/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.ConnectionWSPrx;
import com.projectgoth.fusion.slice._ConnectionDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _ConnectionWSDel
extends _ConnectionDel {
    public void accessed(Map<String, String> var1) throws LocalExceptionWrapper;

    public void addRemoteChildConnectionWS(String var1, ConnectionWSPrx var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void removeRemoteChildConnectionWS(String var1, ConnectionWSPrx var2, Map<String, String> var3) throws LocalExceptionWrapper;
}

