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
import com.projectgoth.fusion.slice.EmailUserNotification;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupEvent;
import com.projectgoth.fusion.slice.SMSUserNotification;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _JobSchedulingServiceDel
extends _ObjectDel {
    public String scheduleFusionGroupEventNotificationViaEmail(int var1, int var2, long var3, EmailUserNotification var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public String scheduleFusionGroupEventNotificationViaSMS(int var1, int var2, long var3, SMSUserNotification var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public String scheduleFusionGroupEventNotificationViaAlert(int var1, int var2, long var3, String var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public int scheduleFusionGroupEvent(GroupEvent var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void unscheduleFusionGroupEvent(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void rescheduleFusionGroupEvent(GroupEvent var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void triggerJob(String var1, String var2, Map<String, String> var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;
}

