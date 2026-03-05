/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.EmailUserNotification;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.SMSUserNotification;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface UserNotificationServicePrx
extends ObjectPrx {
    public void notifyFusionGroupViaAlert(int var1, String var2) throws FusionException;

    public void notifyFusionGroupViaAlert(int var1, String var2, Map<String, String> var3) throws FusionException;

    public void notifyFusionUserViaAlert(String var1, String var2) throws FusionException;

    public void notifyFusionUserViaAlert(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void notifyFusionGroupAnnouncementViaEmail(int var1, EmailUserNotification var2) throws FusionException;

    public void notifyFusionGroupAnnouncementViaEmail(int var1, EmailUserNotification var2, Map<String, String> var3) throws FusionException;

    public void notifyFusionGroupPostSubscribersViaEmail(int var1, EmailUserNotification var2) throws FusionException;

    public void notifyFusionGroupPostSubscribersViaEmail(int var1, EmailUserNotification var2, Map<String, String> var3) throws FusionException;

    public void notifyFusionUserViaEmail(String var1, EmailUserNotification var2) throws FusionException;

    public void notifyFusionUserViaEmail(String var1, EmailUserNotification var2, Map<String, String> var3) throws FusionException;

    public void notifyUserViaEmail(EmailUserNotification var1) throws FusionException;

    public void notifyUserViaEmail(EmailUserNotification var1, Map<String, String> var2) throws FusionException;

    public void notifyUsersViaFusionEmail(String var1, String var2, String[] var3, EmailUserNotification var4) throws FusionException;

    public void notifyUsersViaFusionEmail(String var1, String var2, String[] var3, EmailUserNotification var4, Map<String, String> var5) throws FusionException;

    public void sendEmailFromNoReply(String var1, String var2, String var3) throws FusionException;

    public void sendEmailFromNoReply(String var1, String var2, String var3, Map<String, String> var4) throws FusionException;

    public void sendEmailFromNoReplyWithType(String var1, String var2, String var3, String var4) throws FusionException;

    public void sendEmailFromNoReplyWithType(String var1, String var2, String var3, String var4, Map<String, String> var5) throws FusionException;

    public void notifyFusionGroupAnnouncementViaSMS(int var1, SMSUserNotification var2) throws FusionException;

    public void notifyFusionGroupAnnouncementViaSMS(int var1, SMSUserNotification var2, Map<String, String> var3) throws FusionException;

    public void notifyFusionGroupEventViaSMS(int var1, SMSUserNotification var2) throws FusionException;

    public void notifyFusionGroupEventViaSMS(int var1, SMSUserNotification var2, Map<String, String> var3) throws FusionException;

    public void notifyFusionUserViaSMS(String var1, SMSUserNotification var2) throws FusionException;

    public void notifyFusionUserViaSMS(String var1, SMSUserNotification var2, Map<String, String> var3) throws FusionException;

    public void notifyFusionUser(Message var1) throws FusionException;

    public void notifyFusionUser(Message var1, Map<String, String> var2) throws FusionException;

    public Map<Integer, Integer> getPendingNotificationsForUser(int var1) throws FusionException;

    public Map<Integer, Integer> getPendingNotificationsForUser(int var1, Map<String, String> var2) throws FusionException;

    public void clearNotificationsForUser(int var1, int var2, String[] var3) throws FusionException;

    public void clearNotificationsForUser(int var1, int var2, String[] var3, Map<String, String> var4) throws FusionException;

    public void clearAllNotificationsForUser(int var1) throws FusionException;

    public void clearAllNotificationsForUser(int var1, Map<String, String> var2) throws FusionException;

    public void clearAllNotificationsByTypeForUser(int var1, int var2) throws FusionException;

    public void clearAllNotificationsByTypeForUser(int var1, int var2, Map<String, String> var3) throws FusionException;

    public Map<Integer, Integer> getUnreadNotificationCountForUser(int var1) throws FusionException;

    public Map<Integer, Integer> getUnreadNotificationCountForUser(int var1, Map<String, String> var2) throws FusionException;

    public void clearAllUnreadNotificationCountForUser(int var1, boolean var2) throws FusionException;

    public void clearAllUnreadNotificationCountForUser(int var1, boolean var2, Map<String, String> var3) throws FusionException;

    public Map<Integer, Map<String, Map<String, String>>> getPendingNotificationDataForUser(int var1) throws FusionException;

    public Map<Integer, Map<String, Map<String, String>>> getPendingNotificationDataForUser(int var1, Map<String, String> var2) throws FusionException;

    public Map<Integer, Map<String, Map<String, String>>> getUnreadPendingNotificationDataForUser(int var1) throws FusionException;

    public Map<Integer, Map<String, Map<String, String>>> getUnreadPendingNotificationDataForUser(int var1, Map<String, String> var2) throws FusionException;

    public Map<String, Map<String, String>> getPendingNotificationDataForUserByType(int var1, int var2) throws FusionException;

    public Map<String, Map<String, String>> getPendingNotificationDataForUserByType(int var1, int var2, Map<String, String> var3) throws FusionException;

    public void sendNotificationCounterToUser(int var1);

    public void sendNotificationCounterToUser(int var1, Map<String, String> var2);

    public void sendTemplatizedEmailFromNoReply(String var1, int var2, Map<String, String> var3) throws FusionException;

    public void sendTemplatizedEmailFromNoReply(String var1, int var2, Map<String, String> var3, Map<String, String> var4) throws FusionException;
}

