/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.smsengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.data.SystemSMSData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.smsengine.DispatchThread;
import com.projectgoth.fusion.smsengine.SMSEngine;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class RetryPendingSMSTask
extends TimerTask {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RetryPendingSMSTask.class));
    private SMSEngine engine;
    private Date dateOfLastRetry;
    private int pullMessagesCreatedBefore;

    public RetryPendingSMSTask(SMSEngine engine, int pullMessagesCreatedBefore) {
        this.engine = engine;
        this.pullMessagesCreatedBefore = pullMessagesCreatedBefore;
    }

    public void run() {
        this.retryPendingUserSMS();
        this.retryPendingSystemSMS();
    }

    public void retryPendingUserSMS() {
        List userSMSList = null;
        try {
            if (this.dateOfLastRetry == null) {
                Calendar cal = Calendar.getInstance();
                cal.add(11, -this.pullMessagesCreatedBefore);
                this.dateOfLastRetry = cal.getTime();
            }
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            userSMSList = messageEJB.getMessages(null, Integer.valueOf(MessageType.SMS.value()), MessageData.SendReceiveEnum.SEND.value(), this.dateOfLastRetry, null, MessageDestinationData.StatusEnum.PENDING.value());
            this.dateOfLastRetry = new Date();
        }
        catch (Exception e) {
            log.warn((Object)("Failed to load pending User SMS from database - " + e.toString()));
        }
        if (userSMSList == null || userSMSList.size() == 0) {
            log.debug((Object)"No pending User SMS in database");
            return;
        }
        log.debug((Object)(userSMSList.size() + " pending User SMS loaded from database"));
        for (MessageData message : userSMSList) {
            this.engine.queueDispatchThread(new DispatchThread(this.engine, message));
        }
    }

    public void retryPendingSystemSMS() {
        List systemSMSList = null;
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            systemSMSList = messageEJB.getSystemSMS(null, null, null, SystemSMSData.StatusEnum.PENDING.value());
        }
        catch (Exception e) {
            log.warn((Object)("Failed to load pending System SMS from database - " + e.toString()));
        }
        if (systemSMSList == null || systemSMSList.size() == 0) {
            log.debug((Object)"No pending System SMS in database");
            return;
        }
        log.debug((Object)(systemSMSList.size() + " pending System SMS loaded from database"));
        for (SystemSMSData systemSMS : systemSMSList) {
            this.engine.queueDispatchThread(new DispatchThread(this.engine, systemSMS));
        }
    }
}

