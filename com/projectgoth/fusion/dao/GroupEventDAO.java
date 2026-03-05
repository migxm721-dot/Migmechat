/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao;

import com.projectgoth.fusion.data.GroupEventData;

public interface GroupEventDAO {
    public GroupEventData getGroupEvent(int var1);

    public int persistGroupEvent(GroupEventData var1);

    public int updateGroupEvent(GroupEventData var1);

    public int updateGroupEventStatus(int var1, int var2);
}

