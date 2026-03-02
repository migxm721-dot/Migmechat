/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice._ObjectDel
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice._ObjectDel;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _ObjectCacheDel
extends _ObjectDel {
    public UserPrx createUserObject(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException, ObjectExistsException;

    public UserPrx createUserObjectNonAsync(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException, ObjectExistsException;

    public ChatRoomPrx createChatRoomObject(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException, ObjectExistsException;

    public GroupChatPrx createGroupChatObject(String var1, String var2, String var3, String[] var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException, ObjectExistsException;

    public void sendAlertMessageToAllUsers(String var1, String var2, short var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public GroupChatPrx[] getAllGroupChats(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public void purgeUserObject(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void purgeGroupChatObject(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;
}

