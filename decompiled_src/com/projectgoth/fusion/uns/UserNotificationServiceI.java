/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  com.google.gson.Gson
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Required
 *  org.springframework.mail.MailSender
 *  org.springframework.util.StringUtils
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.exceptions.JedisException
 */
package com.projectgoth.fusion.uns;

import Ice.Current;
import com.google.gson.Gson;
import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.EmailUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.TemplateStringProcessor;
import com.projectgoth.fusion.dao.GroupMembershipDAO;
import com.projectgoth.fusion.dao.SystemDAO;
import com.projectgoth.fusion.dao.UserDAO;
import com.projectgoth.fusion.data.EmailTemplateData;
import com.projectgoth.fusion.data.SystemSMSData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.slice.EmailUserNotification;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SMSUserNotification;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._UserNotificationServiceDisp;
import com.projectgoth.fusion.smsengine.SMSControl;
import com.projectgoth.fusion.uns.AlertQueueWorkerThread;
import com.projectgoth.fusion.uns.EmailQueueWorkerThread;
import com.projectgoth.fusion.uns.NotificationQueueWorkerThread;
import com.projectgoth.fusion.uns.SMSQueueWorkerThread;
import com.projectgoth.fusion.uns.UserNotificationPurger;
import com.projectgoth.fusion.uns.domain.AlertNote;
import com.projectgoth.fusion.uns.domain.EmailNote;
import com.projectgoth.fusion.uns.domain.SMSNote;
import com.projectgoth.fusion.uns.task.AlertGroupTask;
import com.projectgoth.fusion.uns.task.EmailGroupAnnouncementTask;
import com.projectgoth.fusion.uns.task.EmailGroupPostSubscribersTask;
import com.projectgoth.fusion.uns.task.SMSGroupAnnouncementTask;
import com.projectgoth.fusion.uns.task.SMSGroupEventTask;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.mail.MailSender;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UserNotificationServiceI
extends _UserNotificationServiceDisp
implements InitializingBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserNotificationServiceI.class));
    private static final Logger emailNoteEnqueueLog = Logger.getLogger((String)"EmailNoteEnqueueLog");
    private static final String emailNoteEnqueueLogMSG = "|%s|%s|%s|%s|%s|%s";
    private static final int TASK_THREAD_POOL_SIZE = 5;
    private GroupMembershipDAO groupMembershipDAO;
    private SystemDAO systemDAO;
    private UserDAO userDAO;
    private RegistryPrx registryProxy;
    private MailSender mailSender;
    private int emailQueueWorkerThreadCount = 20;
    private int smsQueueWorkerThreadCount = 10;
    private int alertQueueWorkerThreadCount = 10;
    private int notificationQueueWorkerThreadCount = 10;
    private int groupEmailBlockSize = 100;
    private String defaultMigEmailDomain;
    private int alertBlockSize = 100;
    private boolean dryRun = false;
    private String mailServer;
    private String twoWaySMSNumber;
    private AtomicLong emailsSent = new AtomicLong(0L);
    private AtomicLong smsSent = new AtomicLong(0L);
    private AtomicLong alertsSent = new AtomicLong(0L);
    private AtomicLong notificationsSent = new AtomicLong(0L);
    private EmailQueueWorkerThread[] emailQueueWorkerThreads;
    private SMSQueueWorkerThread[] smsQueueWorkerThreads;
    private AlertQueueWorkerThread[] alertQueueWorkerThreads;
    private AtomicInteger notificationServiceQueueSize = new AtomicInteger(0);
    private ExecutorService notificationService;
    private ExecutorService taskService = (ThreadPoolExecutor)Executors.newFixedThreadPool(5);
    private BlockingQueue<EmailNote> emailQueue = new LinkedBlockingQueue<EmailNote>();
    private BlockingQueue<SMSNote> smsQueue = new LinkedBlockingQueue<SMSNote>();
    private BlockingQueue<AlertNote> alertQueue = new LinkedBlockingQueue<AlertNote>();
    public static final String ALERT_KEY = "alert_key";
    public static final String TEXT_HTML_MIME_TYPE = "text/html";
    private Gson gson = new Gson();

    public void afterPropertiesSet() throws Exception {
        log.info((Object)("Dry run? [" + this.dryRun + "]"));
        this.mailServer = this.systemDAO.getSystemProperty("MailServer");
        if (!StringUtils.hasLength((String)this.mailServer)) {
            log.fatal((Object)"unable to load MailServer from system table");
            throw new Exception("unable to load MailServer from system table");
        }
        this.twoWaySMSNumber = this.systemDAO.getSystemProperty("TwoWaySMSNumber");
        if (!StringUtils.hasLength((String)this.twoWaySMSNumber)) {
            log.fatal((Object)"unable to load TwoWaySMSNumber from system table");
            throw new Exception("unable to load TwoWaySMSNumber from system table");
        }
    }

    public void initializeQueues() throws Exception {
        int i;
        log.info((Object)"creating worker threads...");
        this.emailQueueWorkerThreads = new EmailQueueWorkerThread[this.emailQueueWorkerThreadCount];
        for (i = 0; i < this.emailQueueWorkerThreadCount; ++i) {
            this.emailQueueWorkerThreads[i] = new EmailQueueWorkerThread(this.emailQueue, this.mailSender, this.mailServer, this.dryRun, this.emailsSent);
            this.emailQueueWorkerThreads[i].start();
        }
        log.info((Object)("created " + this.emailQueueWorkerThreadCount + " email queue worker threads"));
        this.smsQueueWorkerThreads = new SMSQueueWorkerThread[this.smsQueueWorkerThreadCount];
        for (i = 0; i < this.smsQueueWorkerThreadCount; ++i) {
            this.smsQueueWorkerThreads[i] = new SMSQueueWorkerThread(this.smsQueue, this.dryRun, this.twoWaySMSNumber, this.smsSent);
            this.smsQueueWorkerThreads[i].start();
        }
        log.info((Object)("created " + this.smsQueueWorkerThreadCount + " sms queue worker threads"));
        this.alertQueueWorkerThreads = new AlertQueueWorkerThread[this.alertQueueWorkerThreadCount];
        for (i = 0; i < this.alertQueueWorkerThreadCount; ++i) {
            this.alertQueueWorkerThreads[i] = new AlertQueueWorkerThread(this.alertQueue, this.registryProxy, this.dryRun, this.alertsSent);
            this.alertQueueWorkerThreads[i].start();
        }
        log.info((Object)("created " + this.alertQueueWorkerThreadCount + " alert queue worker threads"));
        this.notificationService = (ThreadPoolExecutor)Executors.newFixedThreadPool(this.notificationQueueWorkerThreadCount);
        log.info((Object)("created " + this.notificationQueueWorkerThreadCount + " notification queue worker threads"));
    }

    void shutdown() {
    }

    public void shutdownExecutorThreads() {
        this.notificationService.shutdown();
    }

    @Required
    public void setRegistryProxy(RegistryPrx registryProxy) {
        this.registryProxy = registryProxy;
    }

    @Override
    public void notifyFusionGroupAnnouncementViaEmail(int groupId, EmailUserNotification note, Current __current) throws FusionException {
        this.taskService.execute(new EmailGroupAnnouncementTask(groupId, this.groupMembershipDAO, note, this.emailQueue, this.defaultMigEmailDomain, this.groupEmailBlockSize));
    }

    @Override
    public void notifyFusionGroupAnnouncementViaSMS(int groupId, SMSUserNotification note, Current __current) throws FusionException {
        if (note.smsSubType == 0) {
            note.smsSubType = SystemSMSData.SubTypeEnum.GROUP_ANNOUNCEMENT_NOTIFICATION.value();
        }
        if (SMSControl.isSendEnabledForSubtype(note.smsSubType)) {
            this.taskService.execute(new SMSGroupAnnouncementTask(groupId, this.groupMembershipDAO, note, this.smsQueue));
        }
    }

    @Override
    public void notifyFusionGroupEventViaSMS(int groupId, SMSUserNotification note, Current __current) throws FusionException {
        if (note.smsSubType == 0) {
            note.smsSubType = SystemSMSData.SubTypeEnum.GROUP_EVENT_NOTIFICATION.value();
        }
        if (SMSControl.isSendEnabledForSubtype(note.smsSubType)) {
            this.taskService.execute(new SMSGroupEventTask(groupId, this.groupMembershipDAO, note, this.smsQueue));
        }
    }

    @Override
    public void notifyFusionGroupViaAlert(int groupId, String message, Current __current) throws FusionException {
        this.taskService.execute(new AlertGroupTask(groupId, message, this.alertQueue, this.groupMembershipDAO, this.alertBlockSize));
    }

    @Override
    public void notifyFusionUserViaEmail(String username, EmailUserNotification note, Current __current) throws FusionException {
        EmailNote emailNote = new EmailNote(note.message, note.subject);
        emailNote.addRecipient(username + this.defaultMigEmailDomain);
        this.addEmailNoteIntoQueue(emailNote);
    }

    @Override
    public void notifyUsersViaFusionEmail(String sender, String senderPassword, String[] recipients, EmailUserNotification note, Current current) throws FusionException {
        EmailNote emailNote = new EmailNote(sender + this.defaultMigEmailDomain, senderPassword, note.message, note.subject);
        for (String recipient : recipients) {
            emailNote.addRecipient(recipient);
        }
        this.addEmailNoteIntoQueue(emailNote);
    }

    @Override
    public void notifyFusionGroupPostSubscribersViaEmail(int userPostId, EmailUserNotification note, Current __current) throws FusionException {
        this.taskService.execute(new EmailGroupPostSubscribersTask(userPostId, this.groupMembershipDAO, note, this.emailQueue, this.defaultMigEmailDomain, this.groupEmailBlockSize));
    }

    @Override
    public void sendEmailFromNoReply(String destinationAddress, String subject, String body, Current __current) throws FusionException {
        this.sendEmailFromNoReplyWithType(destinationAddress, subject, body, null);
    }

    @Override
    public void sendEmailFromNoReplyWithType(String destinationAddress, String subject, String body, String mimeType, Current __current) throws FusionException {
        this.sendEmailFromNoReplyWithTypeAndParts(destinationAddress, subject, body, mimeType, null);
    }

    private void sendEmailFromNoReplyWithTypeAndParts(String destinationAddress, String subject, String body, String mimeType, Collection<EmailNote.EmailPart> extraParts) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("sendEmailFromNoReplyWithTypeAndParts(destinationAddress=[" + destinationAddress + "],subject=[" + subject + "],body=[" + body + "],mimeType=[" + mimeType + "],parts=[" + extraParts + "]"));
        }
        EmailNote emailNote = new EmailNote(body, subject, mimeType);
        emailNote.addRecipient(destinationAddress);
        if (extraParts != null) {
            for (EmailNote.EmailPart extraPart : extraParts) {
                emailNote.addExtraPart(extraPart);
            }
        }
        this.addEmailNoteIntoQueue(emailNote);
    }

    @Override
    public void notifyFusionUserViaSMS(String username, SMSUserNotification note, Current __current) throws FusionException {
        if (!SMSControl.isSendEnabledForSubtype(note.smsSubType, username)) {
            return;
        }
        String phoneNumber = StringUtils.hasLength((String)note.phoneNumber) ? note.phoneNumber : this.userDAO.getMobileNumberForUser(username);
        if (!StringUtils.hasLength((String)phoneNumber)) {
            log.error((Object)("failed to find a mobile phone for user [" + username + "]"));
            throw new FusionException("failed to find a mobile phone for user [" + username + "]");
        }
        if (note.smsSubType < 1) {
            log.error((Object)("failed to find a sms subtype for user [" + username + "]"));
            throw new FusionException("failed to find a sms subtype for user [" + username + "]");
        }
        SMSNote smsNote = new SMSNote(note.message, phoneNumber, username, note.smsSubType);
        this.smsQueue.add(smsNote);
    }

    @Override
    public void notifyUserViaEmail(EmailUserNotification note, Current __current) throws FusionException {
        EmailNote emailNote = new EmailNote(note.message, note.subject);
        emailNote.addRecipient(note.emailAddress);
        this.addEmailNoteIntoQueue(emailNote);
    }

    @Override
    public void notifyFusionUserViaAlert(String username, String message, Current __current) throws FusionException {
        AlertNote note = new AlertNote(message);
        note.addUser(username);
        this.alertQueue.add(note);
    }

    @Required
    public void setGroupMembershipDAO(GroupMembershipDAO groupMembershipDAO) {
        this.groupMembershipDAO = groupMembershipDAO;
    }

    @Required
    public void setSystemDAO(SystemDAO systemDAO) {
        this.systemDAO = systemDAO;
    }

    @Required
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Required
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setEmailQueueWorkerThreadCount(int emailQueueWorkerThreadCount) {
        this.emailQueueWorkerThreadCount = emailQueueWorkerThreadCount;
    }

    public void setSmsQueueWorkerThreadCount(int smsQueueWorkerThreadCount) {
        this.smsQueueWorkerThreadCount = smsQueueWorkerThreadCount;
    }

    @Required
    public void setDefaultMigEmailDomain(String defaultMigEmailDomain) {
        this.defaultMigEmailDomain = defaultMigEmailDomain;
    }

    public void setGroupEmailBlockSize(int groupEmailBlockSize) {
        this.groupEmailBlockSize = groupEmailBlockSize;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public void setNotificationQueueWorkerThreadCount(int notificationQueueWorkerThreadCount) {
        this.notificationQueueWorkerThreadCount = notificationQueueWorkerThreadCount;
    }

    public void setAlertQueueWorkerThreadCount(int alertQueueWorkerThreadCount) {
        this.alertQueueWorkerThreadCount = alertQueueWorkerThreadCount;
    }

    public long getEmailsSent() {
        return this.emailsSent.longValue();
    }

    public long getSmsSent() {
        return this.smsSent.longValue();
    }

    public long getAlertsSent() {
        return this.alertsSent.longValue();
    }

    public long getNotificationsSent() {
        return this.notificationsSent.longValue();
    }

    public int getEmailQueueSize() {
        return this.emailQueue.size();
    }

    public int getSmsQueueSize() {
        return this.smsQueue.size();
    }

    public int getAlertQueueSize() {
        return this.alertQueue.size();
    }

    public int getNotificationQueueSize() {
        return this.notificationServiceQueueSize.get();
    }

    @Override
    public void notifyFusionUser(Message msg, Current __current) throws FusionException {
        if (msg != null) {
            if (StringUtil.isBlank(msg.key)) {
                throw new FusionException("key is required");
            }
            if (msg.toUserId < 0) {
                throw new FusionException("Invalid userId provided");
            }
            if (StringUtil.isBlank(msg.toUsername)) {
                throw new FusionException("toUsername is required");
            }
            if (!Enums.NotificationTypeEnum.isValid(msg.notificationType)) {
                throw new FusionException("Invalid notification type " + msg.notificationType);
            }
            long notificationServiceMaxQueueSize = SystemProperty.getLong(SystemPropertyEntities.UserNotificationServiceSettings.MAX_NOTIFICATION_SERVICE_QUEUE_SIZE);
            if ((long)this.notificationServiceQueueSize.incrementAndGet() <= notificationServiceMaxQueueSize) {
                this.notificationService.execute(new NotificationQueueWorkerThread(msg, this.registryProxy, this.dryRun, this.gson, this));
            } else {
                log.warn((Object)("NotificationServiceMaxQueueSize[" + notificationServiceMaxQueueSize + "] exceeded. Dropping message type [" + msg.notificationType + "] for user [" + msg.toUsername + "]"));
                this.notificationServiceQueueSize.decrementAndGet();
            }
        }
    }

    public void doneNotifyFusionUser() {
        this.notificationsSent.incrementAndGet();
        this.notificationServiceQueueSize.decrementAndGet();
    }

    private static String getReadUnsKey(int userId, int notfnType) {
        return UserNotificationServiceI.getUnsKey(userId, notfnType) + ":read";
    }

    public static String getUnsKey(int userId, int notfnType) {
        return Redis.KeySpace.USER_NOTIFICATION.append(userId) + ":" + notfnType;
    }

    public static String getUnreadCountUnsKey(int userId, int notfnType) {
        return UserNotificationServiceI.getUnsKey(userId, notfnType) + ":unreadc";
    }

    @Override
    public void sendNotificationCounterToUser(int userId, Current __current) {
        String url = SystemProperty.get("NotificationsURL", "");
        String msgString = SystemProperty.get("NotificationsMessage", "migAlerts (%1)");
        Message message = new Message();
        try {
            UserPrx user;
            Map<Integer, Integer> mapNtfn = this.getPendingNotificationsForUser(userId);
            int notificationCount = 0;
            for (int type : mapNtfn.keySet()) {
                notificationCount += mapNtfn.get(type).intValue();
            }
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            String username = userBean.getUsernameByUserid(userId, null);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Sending notification count to user:" + username));
            }
            if ((user = this.registryProxy.findUserObject(username)) != null) {
                message.toUsername = username;
                message.parameters = new HashMap<String, String>();
                msgString = msgString.replaceAll("%1", notificationCount + "");
                message.parameters.put("message", msgString);
                message.parameters.put("totalPending", Integer.toString(notificationCount));
                message.parameters.put("url", url);
                message.notificationType = Enums.NotificationTypeEnum.TOTAL_COUNT.getType();
                user.pushNotification(message);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Notification " + message.notificationType + " pushed for user:" + message.toUsername));
                }
            }
        }
        catch (ObjectNotFoundException e) {
            log.info((Object)("Unable to push notification for userid:" + userId + " user not online"));
        }
        catch (Exception e) {
            log.error((Object)("Unable to push notification for user:" + message.toUsername), (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void clearNotificationsForUser(int userId, int notfnType, String[] keys, Current __current) throws FusionException {
        log.debug((Object)("Clearing multiple type[" + notfnType + "] notifications for user:" + userId));
        Jedis handle = null;
        try {
            try {
                handle = Redis.getMasterInstanceForUserID(userId);
                TreeMap<String, String> backup = new TreeMap<String, String>();
                for (String field : keys) {
                    String value = handle.hget(UserNotificationServiceI.getUnsKey(userId, notfnType), field);
                    if (value == null) continue;
                    backup.put(field, value);
                    handle.hdel(UserNotificationServiceI.getUnsKey(userId, notfnType), new String[]{field});
                }
                if (SystemProperty.getBool("Notifications.storeRead", false) && backup != null && backup.size() > 0) {
                    handle.hmset(UserNotificationServiceI.getReadUnsKey(userId, notfnType), backup);
                }
                this.sendNotificationCounterToUser(userId);
            }
            catch (Exception e) {
                log.error((Object)("Failed to clear pending notifications for user[" + userId + "], for type: " + notfnType), (Throwable)e);
                Object var13_14 = null;
                Redis.disconnect(handle, log);
                return;
            }
            Object var13_13 = null;
        }
        catch (Throwable throwable) {
            Object var13_15 = null;
            Redis.disconnect(handle, log);
            throw throwable;
        }
        Redis.disconnect(handle, log);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void clearAllNotificationsByTypeForUser(int userId, int notfnType, Current __current) throws FusionException {
        log.debug((Object)("Clearing all type[" + notfnType + "] notifications for user:" + userId));
        Jedis handle = null;
        try {
            try {
                handle = Redis.getMasterInstanceForUserID(userId);
                Map backup = handle.hgetAll(UserNotificationServiceI.getUnsKey(userId, notfnType));
                handle.del(new String[]{UserNotificationServiceI.getUnsKey(userId, notfnType)});
                if (SystemProperty.getBool("Notifications.storeRead", false) && backup != null && backup.size() > 0) {
                    handle.hmset(UserNotificationServiceI.getReadUnsKey(userId, notfnType), backup);
                }
                this.sendNotificationCounterToUser(userId);
            }
            catch (Exception e) {
                log.error((Object)("Failed to clear pending notifications for user[" + userId + "], for type: " + notfnType), (Throwable)e);
                Object var7_8 = null;
                Redis.disconnect(handle, log);
                return;
            }
            Object var7_7 = null;
        }
        catch (Throwable throwable) {
            Object var7_9 = null;
            Redis.disconnect(handle, log);
            throw throwable;
        }
        Redis.disconnect(handle, log);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Map<Integer, Integer> getPendingNotificationsForUser(int userId, Current __current) throws FusionException {
        Jedis handle = null;
        TreeMap<Integer, Integer> notfnMap = new TreeMap<Integer, Integer>();
        try {
            try {
                Jedis master = null;
                handle = Redis.getSlaveInstanceForUserID(userId);
                if (handle == null) {
                    master = handle = Redis.getMasterInstanceForUserID(userId);
                }
                if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.USER_NOTIFICATION_TRIM_ON_READ_ENABLED)) {
                    UserNotificationPurger.trimUserNotificationItemsIfNeeded(handle, master, userId);
                }
                for (Enums.NotificationTypeEnum notfnType : Enums.NotificationTypeEnum.values()) {
                    try {
                        Long resultLong = handle.hlen(UserNotificationServiceI.getUnsKey(userId, notfnType.getType()));
                        Integer result = resultLong == null ? null : Integer.valueOf(resultLong.intValue());
                        notfnMap.put(notfnType.getType(), result);
                    }
                    catch (JedisException je) {
                        log.warn((Object)("Failed to retrieve pending notifications for user[" + userId + "], for type: " + notfnType.getType()), (Throwable)je);
                    }
                }
                Object var13_14 = null;
            }
            catch (Exception e) {
                log.error((Object)("Failed to retrieve pending notifications for user[" + userId + "]"), (Throwable)e);
                Object var13_15 = null;
                Redis.disconnect(handle, log);
                return notfnMap;
            }
        }
        catch (Throwable throwable) {
            Object var13_16 = null;
            Redis.disconnect(handle, log);
            throw throwable;
        }
        Redis.disconnect(handle, log);
        return notfnMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void clearAllNotificationsForUser(int userId, Current __current) throws FusionException {
        log.debug((Object)("Clearing all notifications for user:" + userId));
        Jedis handle = null;
        try {
            try {
                handle = Redis.getMasterInstanceForUserID(userId);
                LinkedList<String> keys = new LinkedList<String>();
                for (Enums.NotificationTypeEnum notfnType : Enums.NotificationTypeEnum.values()) {
                    String key = UserNotificationServiceI.getUnsKey(userId, notfnType.getType());
                    Map backup = handle.hgetAll(key);
                    if (SystemProperty.getBool("Notifications.storeRead", false) && backup != null && backup.size() > 0) {
                        handle.hmset(UserNotificationServiceI.getReadUnsKey(userId, notfnType.getType()), backup);
                    }
                    keys.add(key);
                }
                handle.del(keys.toArray(new String[keys.size()]));
            }
            catch (JedisException je) {
                log.error((Object)("Failed to clear pending notifications for user[" + userId + "]"), (Throwable)je);
                Object var12_14 = null;
                Redis.disconnect(handle, log);
                return;
            }
            catch (Exception e) {
                log.error((Object)("Failed to clear pending notifications for user[" + userId + "]"), (Throwable)e);
                Object var12_15 = null;
                Redis.disconnect(handle, log);
                return;
            }
            Object var12_13 = null;
        }
        catch (Throwable throwable) {
            Object var12_16 = null;
            Redis.disconnect(handle, log);
            throw throwable;
        }
        Redis.disconnect(handle, log);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void clearAllUnreadNotificationCountForUser(int userId, boolean resetAll, Current __current) throws FusionException {
        log.debug((Object)("Clearing all notification unread counts for user:" + userId));
        Jedis handle = null;
        try {
            try {
                handle = Redis.getMasterInstanceForUserID(userId);
                LinkedList<String> keys = new LinkedList<String>();
                EnumSet<Enums.NotificationTypeEnum> resetSet = resetAll ? Enums.NotificationTypeEnum.MIGBO_SET : Enums.NotificationTypeEnum.MIGBO_NON_PERSISTENT_SET;
                for (Enums.NotificationTypeEnum notfnType : resetSet) {
                    String key = UserNotificationServiceI.getUnreadCountUnsKey(userId, notfnType.getType());
                    if (log.isDebugEnabled()) {
                        log.debug((Object)String.format(" clearing notification unread count %s", key));
                    }
                    keys.add(key);
                }
                handle.del(keys.toArray(new String[keys.size()]));
            }
            catch (JedisException je) {
                log.error((Object)("Failed to clear notification unread counts for user[" + userId + "]"), (Throwable)je);
                Object var11_13 = null;
                Redis.disconnect(handle, log);
                return;
            }
            catch (Exception e) {
                log.error((Object)("Failed to clear notification unread counts for user[" + userId + "]"), (Throwable)e);
                Object var11_14 = null;
                Redis.disconnect(handle, log);
                return;
            }
            Object var11_12 = null;
        }
        catch (Throwable throwable) {
            Object var11_15 = null;
            Redis.disconnect(handle, log);
            throw throwable;
        }
        Redis.disconnect(handle, log);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Map<Integer, Integer> getUnreadNotificationCountForUser(int userId, Current __current) throws FusionException {
        log.debug((Object)("Getting all notification unread counts for user:" + userId));
        Jedis handle = null;
        TreeMap<Integer, Integer> notfnMap = new TreeMap<Integer, Integer>();
        try {
            try {
                handle = Redis.getSlaveInstanceForUserID(userId);
                if (handle == null) {
                    handle = Redis.getMasterInstanceForUserID(userId);
                }
                for (Enums.NotificationTypeEnum notfnType : Enums.NotificationTypeEnum.MIGBO_SET) {
                    try {
                        String v = handle.get(UserNotificationServiceI.getUnreadCountUnsKey(userId, notfnType.getType()));
                        if (v == null) continue;
                        int vInt = StringUtil.toIntOrDefault(v, -1);
                        if (vInt == -1) {
                            log.warn((Object)String.format("Failed to retrieve notification unread counts for user[%d], for type: %d - value of unread count '%s' is not an integer", userId, notfnType.getType(), v));
                            continue;
                        }
                        notfnMap.put(notfnType.getType(), vInt);
                    }
                    catch (JedisException je) {
                        log.warn((Object)("Failed to retrieve notification unread counts for user[" + userId + "], for type: " + notfnType.getType()), (Throwable)je);
                    }
                }
                Object var10_11 = null;
            }
            catch (Exception e) {
                log.error((Object)("Failed to retrieve notification unread counts for user[" + userId + "]"), (Throwable)e);
                Object var10_12 = null;
                Redis.disconnect(handle, log);
                return notfnMap;
            }
        }
        catch (Throwable throwable) {
            Object var10_13 = null;
            Redis.disconnect(handle, log);
            throw throwable;
        }
        Redis.disconnect(handle, log);
        return notfnMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Map<Integer, Map<String, Map<String, String>>> getPendingNotificationDataForUser(int userId, Current __current) throws FusionException {
        log.debug((Object)("Getting all notification alerts for user:" + userId));
        Jedis handle = null;
        TreeMap<Integer, Map<String, Map<String, String>>> notfnMap = new TreeMap<Integer, Map<String, Map<String, String>>>();
        try {
            try {
                handle = Redis.getMasterInstanceForUserID(userId);
                if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.USER_NOTIFICATION_TRIM_ON_READ_ENABLED)) {
                    UserNotificationPurger.trimUserNotificationItemsIfNeeded(handle, handle, userId);
                }
                for (Enums.NotificationTypeEnum notfnType : Enums.NotificationTypeEnum.MIGBO_SET) {
                    String v;
                    int vInt;
                    HashMap<String, Map<String, String>> finaldata;
                    Map data;
                    block18: {
                        data = null;
                        finaldata = new HashMap<String, Map<String, String>>();
                        vInt = 0;
                        v = null;
                        try {
                            if (Enums.NotificationTypeEnum.isForPersistent(notfnType.getType())) break block18;
                            v = handle.getSet(UserNotificationServiceI.getUnreadCountUnsKey(userId, notfnType.getType()), "0");
                        }
                        catch (JedisException je) {
                            log.warn((Object)("Failed to retrieve notification unread counts for user[" + userId + "], for type: " + notfnType.getType()), (Throwable)je);
                            continue;
                        }
                    }
                    try {
                        data = handle.hgetAll(UserNotificationServiceI.getUnsKey(userId, notfnType.getType()));
                    }
                    catch (JedisException je) {
                        log.warn((Object)("Failed to retrieve notification alerts for user[" + userId + "], for type: " + notfnType.getType()), (Throwable)je);
                        continue;
                    }
                    if (data == null || data.isEmpty()) continue;
                    if (v != null && (vInt = StringUtil.toIntOrDefault(v, -1)) == -1) {
                        vInt = 0;
                        log.warn((Object)String.format("Failed to convert notification unread counts for user[%d], for type: %d - value of unread count '%s' is not an integer", userId, notfnType.getType(), v));
                    }
                    for (String key : data.keySet()) {
                        String jsonMessage = (String)data.get(key);
                        int x = StringUtil.toIntOrDefault(jsonMessage, -1);
                        if (x != -1) {
                            finaldata.put(key, StringUtil.EMPTY_STRING_MAP);
                            continue;
                        }
                        log.debug((Object)String.format("key [%s] json[%s]", key, jsonMessage));
                        try {
                            Message message = (Message)((Object)this.gson.fromJson(jsonMessage, Message.class));
                            if (Enums.NotificationTypeEnum.isForPersistent(message.notificationType)) {
                                message.parameters.put("timestamp", Long.toString(message.dateCreated));
                                finaldata.put(key, message.parameters);
                                continue;
                            }
                            int pendingNotificationsExpiryInSeconds = SystemProperty.getInt("pendingNotificationsExpiryInSeconds", 7776000);
                            Date expiryDate = new Date(message.dateCreated + (long)pendingNotificationsExpiryInSeconds * 1000L);
                            if (expiryDate.before(new Date())) {
                                log.debug((Object)String.format("Deleting old pending notification [%s] since it has expired datecreated[%s] expiryInSeconds[%d] expirydate[%s]", key, new Date(message.dateCreated), pendingNotificationsExpiryInSeconds, expiryDate));
                                handle.hdel(UserNotificationServiceI.getUnsKey(userId, notfnType.getType()), new String[]{key});
                                continue;
                            }
                            message.parameters.put("timestamp", Long.toString(message.dateCreated));
                            finaldata.put(key, message.parameters);
                        }
                        catch (Exception e) {
                            log.error((Object)String.format("Error while parsing json message [%s]", jsonMessage), (Throwable)e);
                        }
                    }
                    notfnMap.put(notfnType.getType(), finaldata);
                }
                Object var19_22 = null;
            }
            catch (Exception e) {
                log.error((Object)("Failed to retrieve notification alerts for user[" + userId + "]"), (Throwable)e);
                Object var19_23 = null;
                Redis.disconnect(handle, log);
                return notfnMap;
            }
        }
        catch (Throwable throwable) {
            Object var19_24 = null;
            Redis.disconnect(handle, log);
            throw throwable;
        }
        Redis.disconnect(handle, log);
        return notfnMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Map<Integer, Map<String, Map<String, String>>> getUnreadPendingNotificationDataForUser(int userId, Current __current) throws FusionException {
        log.debug((Object)("Getting all notification alerts for user:" + userId));
        Jedis handle = null;
        TreeMap<Integer, Map<String, Map<String, String>>> notfnMap = new TreeMap<Integer, Map<String, Map<String, String>>>();
        try {
            try {
                handle = Redis.getMasterInstanceForUserID(userId);
                for (Enums.NotificationTypeEnum notfnType : Enums.NotificationTypeEnum.MIGBO_SET) {
                    Map data = null;
                    HashMap<String, Map<String, String>> finaldata = new HashMap<String, Map<String, String>>();
                    int vInt = 0;
                    String v = null;
                    try {
                        v = handle.get(UserNotificationServiceI.getUnreadCountUnsKey(userId, notfnType.getType()));
                    }
                    catch (JedisException je) {
                        log.warn((Object)("Failed to retrieve notification unread counts for user[" + userId + "], for type: " + notfnType.getType()), (Throwable)je);
                        continue;
                    }
                    try {
                        data = handle.hgetAll(UserNotificationServiceI.getUnsKey(userId, notfnType.getType()));
                    }
                    catch (JedisException je) {
                        log.warn((Object)("Failed to retrieve notification alerts for user[" + userId + "], for type: " + notfnType.getType()), (Throwable)je);
                        continue;
                    }
                    if (data == null || data.isEmpty()) continue;
                    if (v != null && (vInt = StringUtil.toIntOrDefault(v, -1)) == -1) {
                        vInt = 0;
                        log.warn((Object)String.format("Failed to convert notification unread counts for user[%d], for type: %d - value of unread count '%s' is not an integer", userId, notfnType.getType(), v));
                    }
                    TreeMap timestampToKey = new TreeMap(Collections.reverseOrder());
                    for (String key : data.keySet()) {
                        String jsonMessage = (String)data.get(key);
                        int x = StringUtil.toIntOrDefault(jsonMessage, -1);
                        if (x != -1) {
                            finaldata.put(key, StringUtil.EMPTY_STRING_MAP);
                            continue;
                        }
                        log.debug((Object)String.format("key [%s] json[%s]", key, jsonMessage));
                        try {
                            Message message = (Message)((Object)this.gson.fromJson(jsonMessage, Message.class));
                            int pendingNotificationsExpiryInSeconds = SystemProperty.getInt("pendingNotificationsExpiryInSeconds", 7776000);
                            Date expiryDate = new Date(message.dateCreated + (long)pendingNotificationsExpiryInSeconds * 1000L);
                            if (SystemProperty.getBool(SystemPropertyEntities.Alert.PT72238252_MIGBO_PERSISTENT_ALERTS_EXCLUDE_EXPIRE_ENABLED)) {
                                if (expiryDate.before(new Date()) && !Enums.NotificationTypeEnum.isForPersistent(message.notificationType)) {
                                    log.debug((Object)String.format("Deleting old pending notification [%s] since it has expired datecreated[%s] expiryInSeconds[%d] expirydate[%s]", key, new Date(message.dateCreated), pendingNotificationsExpiryInSeconds, expiryDate));
                                    handle.hdel(UserNotificationServiceI.getUnsKey(userId, notfnType.getType()), new String[]{key});
                                    continue;
                                }
                                message.parameters.put("timestamp", Long.toString(message.dateCreated));
                                finaldata.put(key, message.parameters);
                                timestampToKey.put(message.dateCreated, key);
                                continue;
                            }
                            if (expiryDate.before(new Date())) {
                                log.debug((Object)String.format("Deleting old pending notification [%s] since it has expired datecreated[%s] expiryInSeconds[%d] expirydate[%s]", key, new Date(message.dateCreated), pendingNotificationsExpiryInSeconds, expiryDate));
                                handle.hdel(UserNotificationServiceI.getUnsKey(userId, notfnType.getType()), new String[]{key});
                                continue;
                            }
                            message.parameters.put("timestamp", Long.toString(message.dateCreated));
                            finaldata.put(key, message.parameters);
                            timestampToKey.put(message.dateCreated, key);
                        }
                        catch (Exception e) {
                            log.error((Object)String.format("Error while parsing json message [%s]", jsonMessage), (Throwable)e);
                        }
                    }
                    HashMap<String, Map> latestUnreadAlerts = new HashMap<String, Map>(vInt);
                    int count = 0;
                    for (Map.Entry entry : timestampToKey.entrySet()) {
                        if (count >= vInt) break;
                        String key = (String)entry.getValue();
                        Map value = (Map)finaldata.get(key);
                        latestUnreadAlerts.put(key, value);
                        ++count;
                    }
                    notfnMap.put(notfnType.getType(), latestUnreadAlerts);
                }
                Object var20_26 = null;
            }
            catch (Exception e) {
                log.error((Object)("Failed to retrieve notification alerts for user[" + userId + "]"), (Throwable)e);
                Object var20_27 = null;
                Redis.disconnect(handle, log);
                return notfnMap;
            }
        }
        catch (Throwable throwable) {
            Object var20_28 = null;
            Redis.disconnect(handle, log);
            throw throwable;
        }
        Redis.disconnect(handle, log);
        return notfnMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Map<String, Map<String, String>> getPendingNotificationDataForUserByType(int userId, int notificationType, Current __current) throws FusionException {
        HashMap<String, Map<String, String>> notfnMap;
        Jedis handle;
        block19: {
            HashMap<String, Map<String, String>> je2;
            block18: {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Getting all notification alerts of type " + notificationType + " for user:" + userId));
                }
                handle = null;
                notfnMap = new HashMap<String, Map<String, String>>();
                Enums.NotificationTypeEnum notfnType = Enums.NotificationTypeEnum.fromType(notificationType);
                if (notfnType == null) {
                    log.error((Object)("Invalid notificationType provided :" + notificationType));
                    return notfnMap;
                }
                try {
                    try {
                        handle = Redis.getSlaveInstanceForUserID(userId);
                        Jedis master = null;
                        if (handle == null) {
                            master = handle = Redis.getMasterInstanceForUserID(userId);
                        }
                        if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.USER_NOTIFICATION_TRIM_ON_READ_ENABLED)) {
                            UserNotificationPurger.trimUserNotificationItemsIfNeeded(handle, master, userId);
                        }
                        Map data = null;
                        try {
                            data = handle.hgetAll(UserNotificationServiceI.getUnsKey(userId, notfnType.getType()));
                        }
                        catch (JedisException je2) {
                            log.warn((Object)("Failed to retrieve notification alerts for user[" + userId + "], for type: " + notfnType.getType()), (Throwable)je2);
                            HashMap<String, Map<String, String>> hashMap = notfnMap;
                            Object var17_15 = null;
                            Redis.disconnect(handle, log);
                            return hashMap;
                        }
                        if (data == null || data.isEmpty()) {
                            je2 = notfnMap;
                            Object var17_16 = null;
                            break block18;
                        }
                        for (String key : data.keySet()) {
                            boolean oldFormatAlerts;
                            String jsonMessage = (String)data.get(key);
                            boolean bl = oldFormatAlerts = -1 != StringUtil.toIntOrDefault(jsonMessage, -1);
                            if (oldFormatAlerts) {
                                notfnMap.put(key, StringUtil.EMPTY_STRING_MAP);
                                continue;
                            }
                            if (log.isDebugEnabled()) {
                                log.debug((Object)String.format("key [%s] json[%s]", key, jsonMessage));
                            }
                            try {
                                Message message = (Message)((Object)this.gson.fromJson(jsonMessage, Message.class));
                                int pendingNotificationsExpiryInSeconds = SystemProperty.getInt("pendingNotificationsExpiryInSeconds", 7776000);
                                Date expiryDate = new Date(message.dateCreated + (long)pendingNotificationsExpiryInSeconds * 1000L);
                                if (expiryDate.before(new Date())) {
                                    handle.hdel(UserNotificationServiceI.getUnsKey(userId, notfnType.getType()), new String[]{key});
                                    continue;
                                }
                                message.parameters.put("timestamp", Long.toString(message.dateCreated));
                                notfnMap.put(key, message.parameters);
                            }
                            catch (Exception e) {
                                log.error((Object)String.format("Error while parsing json message [%s]", jsonMessage), (Throwable)e);
                            }
                        }
                        break block19;
                    }
                    catch (Exception e) {
                        log.error((Object)("Failed to retrieve notification alerts for user[" + userId + "]"), (Throwable)e);
                        Object var17_18 = null;
                        Redis.disconnect(handle, log);
                        return notfnMap;
                    }
                }
                catch (Throwable throwable) {
                    Object var17_19 = null;
                    Redis.disconnect(handle, log);
                    throw throwable;
                }
            }
            Redis.disconnect(handle, log);
            return je2;
        }
        Object var17_17 = null;
        Redis.disconnect(handle, log);
        return notfnMap;
    }

    private static String replaceTemplateVariables(String templateString, Map<String, String> parameters) throws IOException {
        if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.NEW_TEMPLATE_PROCESSOR_ENABLED)) {
            return TemplateStringProcessor.process(templateString, parameters);
        }
        return StringUtil.replaceNamedValues(templateString, parameters);
    }

    @Override
    public void sendTemplatizedEmailFromNoReply(String destinationEmailAddress, int templateId, Map<String, String> templateVarValues, Current __current) throws FusionException {
        try {
            EmailTemplateData template = MemCacheOrEJB.getEmailTemplateData(templateId);
            if (template == null) {
                throw new FusionException("Unknown templateId:" + templateId);
            }
            String body = UserNotificationServiceI.replaceTemplateVariables(template.bodyTemplate, templateVarValues);
            String subject = UserNotificationServiceI.replaceTemplateVariables(template.subjectTemplate, templateVarValues);
            if (template.getExtraPartCount() == 0) {
                if (template.mimeType.equals(TEXT_HTML_MIME_TYPE)) {
                    this.sendEmailFromNoReplyWithType(destinationEmailAddress, subject, body, template.mimeType);
                } else {
                    this.sendEmailFromNoReply(destinationEmailAddress, subject, body);
                }
            } else {
                ArrayList<EmailNote.EmailPart> extraParts = new ArrayList<EmailNote.EmailPart>();
                for (int i = 0; i < template.getExtraPartCount(); ++i) {
                    EmailTemplateData.PartTemplateData partTemplate = template.getExtraPart(i);
                    String extraPartContent = UserNotificationServiceI.replaceTemplateVariables(partTemplate.contentTemplate, templateVarValues);
                    String extraPartMIMEType = partTemplate.mimeType;
                    extraParts.add(new EmailNote.EmailPart(extraPartContent, extraPartMIMEType));
                }
                this.sendEmailFromNoReplyWithTypeAndParts(destinationEmailAddress, subject, body, template.mimeType, extraParts);
            }
        }
        catch (IOException e) {
            log.error((Object)("Error while sending a templatized email to " + destinationEmailAddress), (Throwable)e);
            throw new FusionException(e.getMessage());
        }
        catch (FusionException e) {
            log.error((Object)("Error while sending a templatized email to " + destinationEmailAddress), (Throwable)((Object)e));
            throw e;
        }
        catch (Exception e) {
            log.error((Object)("Unhandled exception while sending a templatized email to " + destinationEmailAddress), (Throwable)e);
            throw new FusionException(e.getMessage());
        }
    }

    private void addEmailNoteIntoQueue(EmailNote emailNote) {
        Set<String> recipients = emailNote.getRecipients();
        Iterator<String> i = recipients.iterator();
        while (i.hasNext()) {
            String recipient = i.next();
            if (this.userDAO.isBounceEmailAddress(recipient)) {
                log.info((Object)(" Removing email address: " + recipient + " from recepients list. please verify if the receipient is not bounce"));
                i.remove();
                continue;
            }
            if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.INTERNAL_EMAIL_ENABLED) || !EmailUtils.isInternalEmailAddress(recipient, this.defaultMigEmailDomain)) continue;
            i.remove();
        }
        if (recipients.size() == 0) {
            return;
        }
        long ts = System.currentTimeMillis();
        this.emailQueue.add(emailNote);
        if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.LOG_ENQUEUE_EMAIL_ENABLED)) {
            int maxEmailMsgLength = SystemProperty.getInt(SystemPropertyEntities.UserNotificationServiceSettings.MAX_EMAIL_MESSAGE_LENGTH_FOR_ENQUEUE_LOG, 100);
            int maxEmailSubjectLenth = SystemProperty.getInt(SystemPropertyEntities.UserNotificationServiceSettings.MAX_EMAIL_SUBJECT_LENGTH_FOR_ENQUEUE_LOG, 50);
            emailNoteEnqueueLog.info((Object)String.format(emailNoteEnqueueLogMSG, ts, emailNote.getID(), emailNote.getSender(), emailNote.getRecipients(), StringUtil.customizeStringForLogging(emailNote.getSubject(), maxEmailSubjectLenth), StringUtil.customizeStringForLogging(emailNote.getText(), maxEmailMsgLength)));
        }
    }
}

