/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface MessageLoggerPrx
extends ObjectPrx {
    public void logMessage(int var1, int var2, String var3, String var4, int var5, String var6);

    public void logMessage(int var1, int var2, String var3, String var4, int var5, String var6, Map<String, String> var7);
}

