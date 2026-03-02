/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.exceptions.JedisException
 */
package com.projectgoth.fusion.uns;

import com.google.gson.Gson;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.uns.UserNotificationPurger;
import com.projectgoth.fusion.uns.UserNotificationServiceI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class NotificationQueueWorkerThread
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(NotificationQueueWorkerThread.class));
    private static final AtomicInteger redisErrorCounter = new AtomicInteger();
    private Message message;
    private RegistryPrx registryPrx;
    private boolean dryRun;
    private UserNotificationServiceI parentUserNotificationService;
    private Gson gson;

    public NotificationQueueWorkerThread(Message msg, RegistryPrx registryProxy, boolean dryRun, Gson gson, UserNotificationServiceI parentUserNotificationService) {
        this.message = msg;
        this.registryPrx = registryProxy;
        this.dryRun = dryRun;
        this.parentUserNotificationService = parentUserNotificationService;
        this.gson = gson;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void run() {
        block28: {
            block29: {
                boolean isForMigCore;
                int totalPending;
                block30: {
                    boolean redisErrorEncountered;
                    block27: {
                        totalPending = 0;
                        if (this.dryRun) break block29;
                        isForMigCore = Enums.NotificationTypeEnum.isForMigCore(this.message.notificationType);
                        boolean isForMigBo = Enums.NotificationTypeEnum.isForMigBo(this.message.notificationType);
                        boolean isForCollapse = Enums.NotificationTypeEnum.isForCollapse(this.message.notificationType);
                        boolean needToIncreaseUnreadCount = true;
                        Jedis handle = null;
                        redisErrorEncountered = false;
                        try {
                            try {
                                Integer ret;
                                String previousJsonMessage;
                                handle = Redis.getMasterInstanceForUserID(this.message.toUserId);
                                if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.USER_NOTIFICATION_TRIM_ON_WRITE_ENABLED)) {
                                    UserNotificationPurger.trimUserNotificationItemsIfNeededOnWrite(handle, handle, this.message.toUserId);
                                }
                                if (isForCollapse && (previousJsonMessage = handle.hget(UserNotificationServiceI.getUnsKey(this.message.toUserId, this.message.notificationType), this.message.key)) != null && SystemProperty.getBool("Notifications.storeJson", false)) {
                                    needToIncreaseUnreadCount = false;
                                    Message previousMsg = (Message)((Object)this.gson.fromJson(previousJsonMessage, Message.class));
                                    if (previousMsg.parameters.containsKey("inviterUserId") && this.message.parameters.containsKey("inviterUserId")) {
                                        HashSet<String> invitersSet;
                                        Object[] inviters;
                                        String previousInviter = previousMsg.parameters.get("inviterUserId");
                                        String currentInviter = this.message.parameters.get("inviterUserId");
                                        if (previousMsg.parameters.containsKey("collapseInviterUserIdKey") && !StringUtil.isBlank(previousMsg.parameters.get("collapseInviterUserIdKey"))) {
                                            String invitersRaw = previousMsg.parameters.get("collapseInviterUserIdKey");
                                            inviters = invitersRaw.split(":");
                                            invitersSet = new HashSet<String>(Arrays.asList(inviters));
                                        } else {
                                            invitersSet = new HashSet<String>();
                                        }
                                        invitersSet.add(previousInviter);
                                        invitersSet.add(currentInviter);
                                        inviters = invitersSet.toArray(new String[0]);
                                        this.message.parameters.put("collapseInviterUserIdKey", StringUtil.join(inviters, ":"));
                                    }
                                }
                                String messageToStore = SystemProperty.getBool("Notifications.storeJson", false) ? this.gson.toJson((Object)this.message) : Integer.toString(this.message.toUserId);
                                Long resultLong = handle.hset(UserNotificationServiceI.getUnsKey(this.message.toUserId, this.message.notificationType), this.message.key, messageToStore);
                                Integer n = ret = resultLong == null ? null : Integer.valueOf(resultLong.intValue());
                                if (log.isDebugEnabled()) {
                                    log.debug((Object)("Store JSON: " + SystemProperty.getBool("Notifications.storeJson", false)));
                                    log.debug((Object)("messateToStore: " + messageToStore));
                                    log.debug((Object)("uns: type " + this.message.notificationType + " for: " + this.message.toUsername + " rows created: " + ret));
                                    log.debug((Object)("uns key: " + UserNotificationServiceI.getUnsKey(this.message.toUserId, this.message.notificationType)));
                                }
                                if (needToIncreaseUnreadCount && isForMigBo) {
                                    Integer unreadCount;
                                    Long resultLong2 = handle.incr(UserNotificationServiceI.getUnreadCountUnsKey(this.message.toUserId, this.message.notificationType));
                                    Integer n2 = unreadCount = resultLong2 == null ? null : Integer.valueOf(resultLong2.intValue());
                                    if (log.isDebugEnabled()) {
                                        log.debug((Object)String.format("incremented unread count of migalert type %s of user %s (%d) to %d", Enums.NotificationTypeEnum.fromType(this.message.notificationType).name(), this.message.toUsername, this.message.toUserId, unreadCount));
                                    }
                                }
                                if (isForMigCore) {
                                    for (Enums.NotificationTypeEnum notfnType : Enums.NotificationTypeEnum.MIGCORE_SET) {
                                        try {
                                            totalPending = (int)((long)totalPending + handle.hlen(Redis.KeySpace.USER_NOTIFICATION.append(this.message.toUserId) + ":" + notfnType.getType()));
                                        }
                                        catch (JedisException je) {}
                                    }
                                }
                                redisErrorCounter.set(0);
                            }
                            catch (Exception e) {
                                log.error((Object)("Failed to insert notification type [" + this.message.notificationType + "] for user " + this.message.toUsername), (Throwable)e);
                                redisErrorEncountered = true;
                                Object var16_23 = null;
                                Redis.disconnect(handle, log);
                                break block27;
                            }
                            Object var16_22 = null;
                        }
                        catch (Throwable throwable) {
                            Object var16_24 = null;
                            Redis.disconnect(handle, log);
                            throw throwable;
                        }
                        Redis.disconnect(handle, log);
                    }
                    if (!redisErrorEncountered) break block30;
                    long maxSleepTimeInSeconds = SystemProperty.getLong(SystemPropertyEntities.UserNotificationServiceSettings.MAX_NOTIFICATION_THREAD_BACKOFF_IN_SECONDS);
                    long sleepTimeInMs = Math.min(10L * (long)Math.pow(2.0, redisErrorCounter.incrementAndGet()), 1000L * maxSleepTimeInSeconds);
                    log.info((Object)("Redis error encountered, sleeping for " + sleepTimeInMs + " ms before next run."));
                    try {
                        Thread.sleep(sleepTimeInMs);
                    }
                    catch (InterruptedException ie) {
                        log.warn((Object)("Thread sleep interrupted :" + ie.getMessage()));
                    }
                    break block28;
                }
                if (isForMigCore) {
                    try {
                        UserPrx user = this.registryPrx.findUserObject(this.message.toUsername);
                        if (user != null) {
                            String url = "";
                            String msgString = "migAlerts (%1)";
                            if (this.message.parameters != null) {
                                if (this.message.parameters.containsKey("message")) {
                                    msgString = this.message.parameters.get("message");
                                }
                                url = this.message.parameters.containsKey("url") ? this.message.parameters.get("url") : SystemProperty.get("NotificationsURL", "");
                            } else {
                                this.message.parameters = new HashMap<String, String>();
                                url = SystemProperty.get("NotificationsURL", "");
                            }
                            msgString = msgString.replaceAll("%1", totalPending + "");
                            this.message.parameters.put("message", msgString);
                            this.message.parameters.put("totalPending", Integer.toString(totalPending));
                            this.message.parameters.put("url", url);
                            user.pushNotification(this.message);
                            if (log.isDebugEnabled()) {
                                log.debug((Object)("Notification " + this.message.notificationType + " pushed for user:" + this.message.toUsername));
                            }
                        }
                        break block28;
                    }
                    catch (ObjectNotFoundException e) {
                        log.warn((Object)("Failed to send notification type [" + this.message.notificationType + "] for user " + this.message.toUsername + "... user isn't online?!? "));
                    }
                    catch (FusionException e) {
                        log.error((Object)("Failed to send notification type [" + this.message.notificationType + "] for user " + this.message.toUsername), (Throwable)((Object)e));
                    }
                }
                break block28;
            }
            log.debug((Object)("Pushing notification " + this.gson.toJson((Object)this.message) + " to user [" + this.message.toUsername + "]"));
        }
        this.parentUserNotificationService.doneNotifyFusionUser();
    }
}

