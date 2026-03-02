/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.uns.task;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.dao.GroupMembershipDAO;
import com.projectgoth.fusion.uns.domain.AlertNote;
import com.projectgoth.fusion.uns.task.GroupTask;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AlertGroupTask
extends GroupTask {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AlertGroupTask.class));
    private String message;
    private BlockingQueue<AlertNote> queue;
    private int blockSize = 100;

    public AlertGroupTask(int groupId, String message, BlockingQueue<AlertNote> queue, GroupMembershipDAO groupMembershipDAO, int blockSize) {
        super(groupId, groupMembershipDAO);
        this.message = message;
        this.queue = queue;
        this.blockSize = blockSize;
    }

    @Override
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("building alert notes for group [" + this.groupId + "]"));
        }
        List<String> users = this.groupMembershipDAO.getGroupMemberUsernamesForGroupEventNotification(this.groupId);
        AlertNote note = new AlertNote(this.message);
        int count = 1;
        for (String user : users) {
            note.addUser(user);
            if (count++ % this.blockSize != 0) continue;
            this.queue.add(note);
            note = new AlertNote(this.message);
        }
        --count;
        if (note.hasRecipients()) {
            this.queue.add(note);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("done adding " + count + " alerts for group " + this.groupId + " to queue"));
        }
    }
}

