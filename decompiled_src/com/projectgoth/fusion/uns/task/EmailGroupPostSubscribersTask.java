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
public class EmailGroupPostSubscribersTask
extends EmailGroupTask {
    private int userPostId;

    public EmailGroupPostSubscribersTask(int userPostId, GroupMembershipDAO groupMembershipDAO, EmailUserNotification notificationNote, BlockingQueue<EmailNote> queue, String defaultMigEmailDomain, int groupEmailBlockSize) {
        super(0, groupMembershipDAO, notificationNote, queue, defaultMigEmailDomain, groupEmailBlockSize);
        this.userPostId = userPostId;
    }

    @Override
    public List<String> getGroupMembersToNotify() {
        return this.groupMembershipDAO.getGroupMemberUsernamesForNewGroupUserPostNotificationViaEmail(this.userPostId);
    }
}

