/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.uns;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.SystemSMSData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.uns.UserNotificationService;
import com.projectgoth.fusion.uns.domain.SMSNote;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SMSQueueWorkerThread
extends Thread {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SMSQueueWorkerThread.class));
    private BlockingQueue<SMSNote> queue;
    private boolean dryRun;
    private String twoWaySMSNumber;
    private Message messageBean;
    private AtomicLong smsSent;

    public SMSQueueWorkerThread(BlockingQueue<SMSNote> queue, boolean dryRun, String twoWaySMSNumber, AtomicLong smsSent) throws CreateException {
        this.queue = queue;
        this.dryRun = dryRun;
        this.twoWaySMSNumber = twoWaySMSNumber;
        this.smsSent = smsSent;
        this.messageBean = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
    }

    @Override
    public void run() {
        AccountEntrySourceData accountEntrySource = new AccountEntrySourceData(UserNotificationService.class);
        while (true) {
            try {
                while (true) {
                    SMSNote note = this.queue.take();
                    SystemSMSData message = new SystemSMSData();
                    message.type = SystemSMSData.TypeEnum.STANDARD;
                    message.subType = note.getSubType();
                    message.source = this.twoWaySMSNumber;
                    message.messageText = note.getText();
                    message.username = note.getUsername();
                    message.destination = note.getPhoneNumber();
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("sending sms [" + message.messageText + "] to [" + message.destination + "]"));
                    }
                    if (this.dryRun) continue;
                    this.messageBean.sendSystemSMS(message, accountEntrySource);
                    this.smsSent.incrementAndGet();
                }
            }
            catch (Exception e) {
                log.error((Object)"failed to send sms ", (Throwable)e);
                continue;
            }
            break;
        }
    }
}

