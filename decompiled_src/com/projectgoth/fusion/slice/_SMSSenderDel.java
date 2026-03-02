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
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SystemSMSDataIce;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _SMSSenderDel
extends _ObjectDel {
    public void sendSMS(MessageDataIce var1, long var2, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void sendSystemSMS(SystemSMSDataIce var1, long var2, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;
}

