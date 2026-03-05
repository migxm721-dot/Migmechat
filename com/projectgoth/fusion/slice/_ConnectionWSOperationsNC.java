/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.ConnectionWSPrx;
import com.projectgoth.fusion.slice._ConnectionOperationsNC;

public interface _ConnectionWSOperationsNC
extends _ConnectionOperationsNC {
    public void accessed();

    public void addRemoteChildConnectionWS(String var1, ConnectionWSPrx var2);

    public void removeRemoteChildConnectionWS(String var1, ConnectionWSPrx var2);
}

