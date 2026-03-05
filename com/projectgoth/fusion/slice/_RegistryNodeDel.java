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
import com.projectgoth.fusion.slice.RegistryNodePrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _RegistryNodeDel
extends _ObjectDel {
    public void registerUserObject(String var1, UserPrx var2, String var3, Map<String, String> var4) throws LocalExceptionWrapper, ObjectExistsException;

    public void deregisterUserObject(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void registerConnectionObject(String var1, ConnectionPrx var2, Map<String, String> var3) throws LocalExceptionWrapper, ObjectExistsException;

    public void deregisterConnectionObject(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void registerChatRoomObject(String var1, ChatRoomPrx var2, Map<String, String> var3) throws LocalExceptionWrapper, ObjectExistsException;

    public void deregisterChatRoomObject(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void registerGroupChatObject(String var1, GroupChatPrx var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void deregisterGroupChatObject(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void registerObjectCache(String var1, ObjectCachePrx var2, ObjectCacheAdminPrx var3, Map<String, String> var4) throws LocalExceptionWrapper;

    public void deregisterObjectCache(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void registerBotService(String var1, int var2, BotServicePrx var3, BotServiceAdminPrx var4, Map<String, String> var5) throws LocalExceptionWrapper;

    public void deregisterBotService(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public String registerNewNode(RegistryNodePrx var1, String var2, boolean var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void registerObjectCacheStats(String var1, ObjectCacheStats var2, Map<String, String> var3) throws LocalExceptionWrapper, ObjectNotFoundException;

    public void registerMessageSwitchboard(String var1, MessageSwitchboardPrx var2, MessageSwitchboardAdminPrx var3, Map<String, String> var4) throws LocalExceptionWrapper;

    public void deregisterMessageSwitchboard(String var1, Map<String, String> var2) throws LocalExceptionWrapper;
}

