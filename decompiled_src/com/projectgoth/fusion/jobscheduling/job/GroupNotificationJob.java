/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.quartz.Job
 *  org.quartz.SchedulerContext
 */
package com.projectgoth.fusion.jobscheduling.job;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.jobscheduling.job.GroupNotificationViaAlert;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.SchedulerContext;

public abstract class GroupNotificationJob
implements Job {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GroupNotificationViaAlert.class));
    protected UserNotificationServicePrx userNotificationServiceProxy = null;

    protected synchronized UserNotificationServicePrx findUserNotificationServicePrx(SchedulerContext context) {
        if (this.userNotificationServiceProxy == null) {
            this.userNotificationServiceProxy = (UserNotificationServicePrx)context.get((Object)"userNotificationServiceProxy");
        }
        return this.userNotificationServiceProxy;
    }
}

