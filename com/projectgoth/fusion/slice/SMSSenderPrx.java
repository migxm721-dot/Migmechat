/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SystemSMSDataIce;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface SMSSenderPrx
extends ObjectPrx {
    public void sendSMS(MessageDataIce var1, long var2) throws FusionException;

    public void sendSMS(MessageDataIce var1, long var2, Map<String, String> var4) throws FusionException;

    public void sendSystemSMS(SystemSMSDataIce var1, long var2) throws FusionException;

    public void sendSystemSMS(SystemSMSDataIce var1, long var2, Map<String, String> var4) throws FusionException;
}

