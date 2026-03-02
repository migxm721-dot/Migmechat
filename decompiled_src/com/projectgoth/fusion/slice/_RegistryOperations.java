/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.AMD_Registry_getMessageSwitchboard;
import com.projectgoth.fusion.slice.BotServiceAdminPrx;
import com.projectgoth.fusion.slice.BotServicePrx;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardAdminPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.ObjectCacheAdminPrx;
import com.projectgoth.fusion.slice.ObjectCachePrx;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _RegistryOperations {
    public UserPrx findUserObject(String var1, Current var2) throws ObjectNotFoundException;

    public UserPrx[] findUserObjects(String[] var1, Current var2);

    public Map<String, UserPrx> findUserObjectsMap(String[] var1, Current var2);

    public void registerUserObject(String var1, UserPrx var2, String var3, Current var4) throws ObjectExistsException;

    public void deregisterUserObject(String var1, String var2, Current var3);

    public ConnectionPrx findConnectionObject(String var1, Current var2) throws ObjectNotFoundException;

    public void registerConnectionObject(String var1, ConnectionPrx var2, Current var3) throws ObjectExistsException;

    public void deregisterConnectionObject(String var1, Current var2);

    public ChatRoomPrx findChatRoomObject(String var1, Current var2) throws ObjectNotFoundException;

    public ChatRoomPrx[] findChatRoomObjects(String[] var1, Current var2);

    public void registerChatRoomObject(String var1, ChatRoomPrx var2, Current var3) throws ObjectExistsException;

    public void deregisterChatRoomObject(String var1, Current var2);

    public GroupChatPrx findGroupChatObject(String var1, Current var2) throws ObjectNotFoundException;

    public void registerGroupChatObject(String var1, GroupChatPrx var2, Current var3);

    public void deregisterGroupChatObject(String var1, Current var2);

    public ObjectCachePrx getLowestLoadedObjectCache(Current var1) throws ObjectNotFoundException;

    public void registerObjectCache(String var1, ObjectCachePrx var2, ObjectCacheAdminPrx var3, Current var4);

    public void deregisterObjectCache(String var1, Current var2);

    public BotServicePrx getLowestLoadedBotService(Current var1) throws ObjectNotFoundException;

    public void registerBotService(String var1, int var2, BotServicePrx var3, BotServiceAdminPrx var4, Current var5);

    public void deregisterBotService(String var1, Current var2);

    public void sendAlertMessageToAllUsers(String var1, String var2, short var3, Current var4) throws FusionException;

    public int newGatewayID(Current var1);

    public void registerObjectCacheStats(String var1, ObjectCacheStats var2, Current var3) throws ObjectNotFoundException;

    public int getUserCount(Current var1);

    public void registerMessageSwitchboard(String var1, MessageSwitchboardPrx var2, MessageSwitchboardAdminPrx var3, Current var4);

    public void deregisterMessageSwitchboard(String var1, Current var2);

    public void getMessageSwitchboard_async(AMD_Registry_getMessageSwitchboard var1, Current var2) throws FusionException;
}

