/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

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

public interface _RegistryNodeOperationsNC {
    public void registerUserObject(String var1, UserPrx var2, String var3) throws ObjectExistsException;

    public void deregisterUserObject(String var1, String var2);

    public void registerConnectionObject(String var1, ConnectionPrx var2) throws ObjectExistsException;

    public void deregisterConnectionObject(String var1);

    public void registerChatRoomObject(String var1, ChatRoomPrx var2) throws ObjectExistsException;

    public void deregisterChatRoomObject(String var1);

    public void registerGroupChatObject(String var1, GroupChatPrx var2);

    public void deregisterGroupChatObject(String var1);

    public void registerObjectCache(String var1, ObjectCachePrx var2, ObjectCacheAdminPrx var3);

    public void deregisterObjectCache(String var1);

    public void registerBotService(String var1, int var2, BotServicePrx var3, BotServiceAdminPrx var4);

    public void deregisterBotService(String var1);

    public String registerNewNode(RegistryNodePrx var1, String var2, boolean var3) throws FusionException;

    public void registerObjectCacheStats(String var1, ObjectCacheStats var2) throws ObjectNotFoundException;

    public void registerMessageSwitchboard(String var1, MessageSwitchboardPrx var2, MessageSwitchboardAdminPrx var3);

    public void deregisterMessageSwitchboard(String var1);
}

