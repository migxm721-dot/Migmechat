/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice.tests;

import Ice.ObjectPrx;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface PrinterPrx
extends ObjectPrx {
    public void printString(String var1);

    public void printString(String var1, Map<String, String> var2);

    public void circular(String var1, int var2);

    public void circular(String var1, int var2, Map<String, String> var3);
}

