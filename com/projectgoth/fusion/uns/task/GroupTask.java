/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.uns.task;

import com.projectgoth.fusion.dao.GroupMembershipDAO;
import com.projectgoth.fusion.uns.task.Task;

public abstract class GroupTask
extends Task {
    protected int groupId;
    protected GroupMembershipDAO groupMembershipDAO;

    public GroupTask(int groupId, GroupMembershipDAO groupMembershipDAO) {
        this.groupId = groupId;
        this.groupMembershipDAO = groupMembershipDAO;
    }
}

