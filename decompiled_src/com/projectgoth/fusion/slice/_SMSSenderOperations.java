/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SystemSMSDataIce;

public interface _SMSSenderOperations {
    public void sendSMS(MessageDataIce var1, long var2, Current var4) throws FusionException;

    public void sendSystemSMS(SystemSMSDataIce var1, long var2, Current var4) throws FusionException;
}

