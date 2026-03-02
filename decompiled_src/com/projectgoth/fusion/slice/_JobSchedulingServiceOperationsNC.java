/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.EmailUserNotification;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupEvent;
import com.projectgoth.fusion.slice.SMSUserNotification;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _JobSchedulingServiceOperationsNC {
    public String scheduleFusionGroupEventNotificationViaEmail(int var1, int var2, long var3, EmailUserNotification var5) throws FusionException;

    public String scheduleFusionGroupEventNotificationViaSMS(int var1, int var2, long var3, SMSUserNotification var5) throws FusionException;

    public String scheduleFusionGroupEventNotificationViaAlert(int var1, int var2, long var3, String var5) throws FusionException;

    public int scheduleFusionGroupEvent(GroupEvent var1) throws FusionException;

    public void unscheduleFusionGroupEvent(int var1) throws FusionException;

    public void rescheduleFusionGroupEvent(GroupEvent var1) throws FusionException;

    public void triggerJob(String var1, String var2, Map<String, String> var3) throws FusionException;
}

