/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Object
 */
package com.projectgoth.fusion.objectcache;

import Ice.Object;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice.UserPrx;

public interface ObjectCacheInterface
extends Object {
    public void getStats(ObjectCacheStats var1);

    public String[] getUsernames();

    public int getUserCount();

    public void setLoadWeightage(int var1);

    public int getLoadWeightage();

    public void purgeUserObject(String var1);

    public UserPrx createUserObjectNonAsync(String var1) throws FusionException;

    public ChatRoomPrx createChatRoomObject(String var1) throws FusionException;

    public GroupChatPrx createGroupChatObject(String var1, String var2, String var3, String[] var4) throws FusionException;
}

