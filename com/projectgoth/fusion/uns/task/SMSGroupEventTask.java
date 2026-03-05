/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.uns.task;

import com.projectgoth.fusion.dao.GroupMembershipDAO;
import com.projectgoth.fusion.domain.UsernameAndMobileNumber;
import com.projectgoth.fusion.slice.SMSUserNotification;
import com.projectgoth.fusion.uns.domain.SMSNote;
import com.projectgoth.fusion.uns.task.SMSGroupTask;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SMSGroupEventTask
extends SMSGroupTask {
    public SMSGroupEventTask(int groupId, GroupMembershipDAO groupMembershipDAO, SMSUserNotification notificationNote, BlockingQueue<SMSNote> queue) {
        super(groupId, groupMembershipDAO, notificationNote, queue);
    }

    @Override
    public List<UsernameAndMobileNumber> getUsersToNotify() {
        return this.groupMembershipDAO.getGroupMemberUsernamesAndMobileNumbersForGroupEventSMSNotification(this.groupId);
    }
}

