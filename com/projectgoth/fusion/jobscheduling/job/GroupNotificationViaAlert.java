/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.quartz.JobDataMap
 *  org.quartz.JobExecutionContext
 *  org.quartz.JobExecutionException
 */
package com.projectgoth.fusion.jobscheduling.job;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.jobscheduling.job.GroupNotificationJob;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class GroupNotificationViaAlert
extends GroupNotificationJob {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GroupNotificationViaAlert.class));

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        int groupId = jobDataMap.getInt("groupId");
        String message = (String)jobDataMap.get((Object)"message");
        log.info((Object)("triggering GroupNotificationViaAlert for group [" + groupId + "] with message [" + message + "]"));
        try {
            this.findUserNotificationServicePrx(context.getScheduler().getContext());
            if (log.isDebugEnabled()) {
                log.debug((Object)("contacting UNS [" + this.userNotificationServiceProxy + "] about group [" + groupId + "] and message [" + message + "]"));
            }
            this.userNotificationServiceProxy.notifyFusionGroupViaAlert(groupId, message);
        }
        catch (Exception e) {
            log.error((Object)"failed to execute job ", (Throwable)e);
            throw new JobExecutionException((Throwable)e);
        }
    }
}

