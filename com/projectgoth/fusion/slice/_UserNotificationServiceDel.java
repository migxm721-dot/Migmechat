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
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.SMSUserNotification;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _UserNotificationServiceDel
extends _ObjectDel {
    public void notifyFusionGroupViaAlert(int var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void notifyFusionUserViaAlert(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void notifyFusionGroupAnnouncementViaEmail(int var1, EmailUserNotification var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void notifyFusionGroupPostSubscribersViaEmail(int var1, EmailUserNotification var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void notifyFusionUserViaEmail(String var1, EmailUserNotification var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void notifyUserViaEmail(EmailUserNotification var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void notifyUsersViaFusionEmail(String var1, String var2, String[] var3, EmailUserNotification var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public void sendEmailFromNoReply(String var1, String var2, String var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void sendEmailFromNoReplyWithType(String var1, String var2, String var3, String var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public void notifyFusionGroupAnnouncementViaSMS(int var1, SMSUserNotification var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void notifyFusionGroupEventViaSMS(int var1, SMSUserNotification var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void notifyFusionUserViaSMS(String var1, SMSUserNotification var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void notifyFusionUser(Message var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public Map<Integer, Integer> getPendingNotificationsForUser(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void clearNotificationsForUser(int var1, int var2, String[] var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void clearAllNotificationsForUser(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void clearAllNotificationsByTypeForUser(int var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public Map<Integer, Integer> getUnreadNotificationCountForUser(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void clearAllUnreadNotificationCountForUser(int var1, boolean var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public Map<Integer, Map<String, Map<String, String>>> getPendingNotificationDataForUser(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public Map<Integer, Map<String, Map<String, String>>> getUnreadPendingNotificationDataForUser(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public Map<String, Map<String, String>> getPendingNotificationDataForUserByType(int var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void sendNotificationCounterToUser(int var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void sendTemplatizedEmailFromNoReply(String var1, int var2, Map<String, String> var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;
}

