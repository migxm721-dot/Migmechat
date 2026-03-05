/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.uns.task;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.dao.GroupMembershipDAO;
import com.projectgoth.fusion.domain.UsernameAndMobileNumber;
import com.projectgoth.fusion.slice.SMSUserNotification;
import com.projectgoth.fusion.uns.domain.SMSNote;
import com.projectgoth.fusion.uns.task.GroupTask;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class SMSGroupTask
extends GroupTask {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SMSGroupTask.class));
    private SMSUserNotification notificationNote;
    private BlockingQueue<SMSNote> queue;

    public SMSGroupTask(int groupId, GroupMembershipDAO groupMembershipDAO, SMSUserNotification notificationNote, BlockingQueue<SMSNote> queue) {
        super(groupId, groupMembershipDAO);
        this.notificationNote = notificationNote;
        this.queue = queue;
    }

    public abstract List<UsernameAndMobileNumber> getUsersToNotify();

    @Override
    public void run() {
        List<UsernameAndMobileNumber> groupMembersAndNumbers = this.getUsersToNotify();
        if (log.isDebugEnabled()) {
            log.debug((Object)("found " + groupMembersAndNumbers.size() + " members to notify via sms"));
        }
        if (this.notificationNote.smsSubType < 1) {
            log.warn((Object)("unable to send SMS notification [" + this.notificationNote.message + "] for groupId [" + this.groupId + "], smsSubType was not specified, aborting..."));
            return;
        }
        for (UsernameAndMobileNumber member : groupMembersAndNumbers) {
            SMSNote note = new SMSNote(this.notificationNote.message, this.notificationNote.smsSubType);
            note.setPhoneNumber(member.getMobileNumber());
            note.setUsername(member.getUsername());
            this.queue.add(note);
        }
    }
}

