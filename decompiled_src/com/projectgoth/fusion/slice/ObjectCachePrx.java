/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.AMI_ObjectCache_getMessageSwitchboard;
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
public interface ObjectCachePrx
extends ObjectPrx {
    public UserPrx createUserObject(String var1) throws FusionException, ObjectExistsException;

    public UserPrx createUserObject(String var1, Map<String, String> var2) throws FusionException, ObjectExistsException;

    public UserPrx createUserObjectNonAsync(String var1) throws FusionException, ObjectExistsException;

    public UserPrx createUserObjectNonAsync(String var1, Map<String, String> var2) throws FusionException, ObjectExistsException;

    public ChatRoomPrx createChatRoomObject(String var1) throws FusionException, ObjectExistsException;

    public ChatRoomPrx createChatRoomObject(String var1, Map<String, String> var2) throws FusionException, ObjectExistsException;

    public GroupChatPrx createGroupChatObject(String var1, String var2, String var3, String[] var4) throws FusionException, ObjectExistsException;

    public GroupChatPrx createGroupChatObject(String var1, String var2, String var3, String[] var4, Map<String, String> var5) throws FusionException, ObjectExistsException;

    public void sendAlertMessageToAllUsers(String var1, String var2, short var3) throws FusionException;

    public void sendAlertMessageToAllUsers(String var1, String var2, short var3, Map<String, String> var4) throws FusionException;

    public GroupChatPrx[] getAllGroupChats() throws FusionException;

    public GroupChatPrx[] getAllGroupChats(Map<String, String> var1) throws FusionException;

    public void purgeUserObject(String var1);

    public void purgeUserObject(String var1, Map<String, String> var2);

    public void purgeGroupChatObject(String var1);

    public void purgeGroupChatObject(String var1, Map<String, String> var2);

    public MessageSwitchboardPrx getMessageSwitchboard() throws FusionException;

    public MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> var1) throws FusionException;

    public boolean getMessageSwitchboard_async(AMI_ObjectCache_getMessageSwitchboard var1);

    public boolean getMessageSwitchboard_async(AMI_ObjectCache_getMessageSwitchboard var1, Map<String, String> var2);
}

