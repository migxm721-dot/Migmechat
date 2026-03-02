/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.AMD_ObjectCache_createUserObject;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.UserPrx;

public interface _ObjectCacheOperations {
    public void createUserObject_async(AMD_ObjectCache_createUserObject var1, String var2, Current var3) throws FusionException, ObjectExistsException;

    public UserPrx createUserObjectNonAsync(String var1, Current var2) throws FusionException, ObjectExistsException;

    public ChatRoomPrx createChatRoomObject(String var1, Current var2) throws FusionException, ObjectExistsException;

    public GroupChatPrx createGroupChatObject(String var1, String var2, String var3, String[] var4, Current var5) throws FusionException, ObjectExistsException;

    public void sendAlertMessageToAllUsers(String var1, String var2, short var3, Current var4) throws FusionException;

    public GroupChatPrx[] getAllGroupChats(Current var1) throws FusionException;

    public void purgeUserObject(String var1, Current var2);

    public void purgeGroupChatObject(String var1, Current var2);

    public MessageSwitchboardPrx getMessageSwitchboard(Current var1) throws FusionException;
}

