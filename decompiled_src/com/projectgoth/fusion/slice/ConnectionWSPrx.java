/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.ConnectionPrx;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ConnectionWSPrx
extends ConnectionPrx {
    public void accessed();

    public void accessed(Map<String, String> var1);

    public void addRemoteChildConnectionWS(String var1, ConnectionWSPrx var2);

    public void addRemoteChildConnectionWS(String var1, ConnectionWSPrx var2, Map<String, String> var3);

    public void removeRemoteChildConnectionWS(String var1, ConnectionWSPrx var2);

    public void removeRemoteChildConnectionWS(String var1, ConnectionWSPrx var2, Map<String, String> var3);
}

