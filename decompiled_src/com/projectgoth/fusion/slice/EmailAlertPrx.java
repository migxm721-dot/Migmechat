/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface EmailAlertPrx
extends ObjectPrx {
    public void requestUnreadEmailCount(String var1, String var2, UserPrx var3);

    public void requestUnreadEmailCount(String var1, String var2, UserPrx var3, Map<String, String> var4);
}

