/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.AMD_ObjectCache_createUserObject;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.UserPrx;

public interface _ObjectCacheOperationsNC {
    public void createUserObject_async(AMD_ObjectCache_createUserObject var1, String var2) throws FusionException, ObjectExistsException;

    public UserPrx createUserObjectNonAsync(String var1) throws FusionException, ObjectExistsException;

    public ChatRoomPrx createChatRoomObject(String var1) throws FusionException, ObjectExistsException;

    public GroupChatPrx createGroupChatObject(String var1, String var2, String var3, String[] var4) throws FusionException, ObjectExistsException;

    public void sendAlertMessageToAllUsers(String var1, String var2, short var3) throws FusionException;

    public GroupChatPrx[] getAllGroupChats() throws FusionException;

    public void purgeUserObject(String var1);

    public void purgeGroupChatObject(String var1);

    public MessageSwitchboardPrx getMessageSwitchboard() throws FusionException;
}

