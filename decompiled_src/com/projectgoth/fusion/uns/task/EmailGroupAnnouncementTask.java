/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.uns.task;

import com.projectgoth.fusion.dao.GroupMembershipDAO;
import com.projectgoth.fusion.slice.EmailUserNotification;
import com.projectgoth.fusion.uns.domain.EmailNote;
import com.projectgoth.fusion.uns.task.EmailGroupTask;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EmailGroupAnnouncementTask
extends EmailGroupTask {
    public EmailGroupAnnouncementTask(int groupId, GroupMembershipDAO groupMembershipDAO, EmailUserNotification notificationNote, BlockingQueue<EmailNote> queue, String defaultMigEmailDomain, int groupEmailBlockSize) {
        super(groupId, groupMembershipDAO, notificationNote, queue, defaultMigEmailDomain, groupEmailBlockSize);
    }

    @Override
    public List<String> getGroupMembersToNotify() {
        return this.groupMembershipDAO.getGroupMemberUsernamesForEmailNotification(this.groupId);
    }
}

