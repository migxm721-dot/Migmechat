/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.ConnectionWSPrx;
import com.projectgoth.fusion.slice._ConnectionOperations;

public interface _ConnectionWSOperations
extends _ConnectionOperations {
    public void accessed(Current var1);

    public void addRemoteChildConnectionWS(String var1, ConnectionWSPrx var2, Current var3);

    public void removeRemoteChildConnectionWS(String var1, ConnectionWSPrx var2, Current var3);
}

