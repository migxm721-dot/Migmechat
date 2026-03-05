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
import com.projectgoth.fusion.slice.ChatDefinitionIce;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _MessageSwitchboardDel
extends _ObjectDel {
    public boolean isUserChatSyncEnabled(ConnectionPrx var1, String var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public ChatDefinitionIce[] getChats(int var1, int var2, int var3, byte var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public ChatDefinitionIce[] getChats2(int var1, int var2, int var3, byte var4, ConnectionPrx var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public void onGetChats(ConnectionPrx var1, int var2, int var3, int var4, byte var5, short var6, String var7, Map<String, String> var8) throws LocalExceptionWrapper, FusionException;

    public void getAndPushMessages(String var1, byte var2, String var3, long var4, long var6, int var8, ConnectionPrx var9, Map<String, String> var10) throws LocalExceptionWrapper, FusionException;

    public void getAndPushMessages2(String var1, byte var2, String var3, long var4, long var6, int var8, ConnectionPrx var9, int var10, short var11, short var12, Map<String, String> var13) throws LocalExceptionWrapper, FusionException;

    public void onCreateGroupChat(ChatDefinitionIce var1, String var2, String var3, GroupChatPrx var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public void onJoinGroupChat(String var1, int var2, String var3, boolean var4, UserPrx var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public void onLeaveGroupChat(String var1, int var2, String var3, UserPrx var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public void onJoinChatRoom(String var1, int var2, String var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void onLeaveChatRoom(String var1, int var2, String var3, UserPrx var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public boolean onSendFusionMessageToIndividual(SessionPrx var1, UserPrx var2, MessageDataIce var3, String var4, String[] var5, int var6, short var7, UserDataIce var8, String var9, Map<String, String> var10) throws LocalExceptionWrapper, FusionException;

    public void onSendFusionMessageToGroupChat(SessionPrx var1, UserPrx var2, MessageDataIce var3, String var4, int var5, short var6, Map<String, String> var7) throws LocalExceptionWrapper, FusionException;

    public void onSendFusionMessageToChatRoom(SessionPrx var1, UserPrx var2, MessageDataIce var3, String var4, int var5, short var6, Map<String, String> var7) throws LocalExceptionWrapper, FusionException;

    public boolean onSendMessageToAllUsersInChat(SessionPrx var1, UserPrx var2, MessageDataIce var3, UserDataIce var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public void onCreatePrivateChat(int var1, String var2, String var3, int var4, short var5, UserDataIce var6, String var7, Map<String, String> var8) throws LocalExceptionWrapper, FusionException;

    public void onLeavePrivateChat(int var1, String var2, String var3, int var4, short var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public GroupChatPrx ensureGroupChatExists(SessionPrx var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void onLogon(int var1, SessionPrx var2, short var3, String var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public void setChatName(String var1, String var2, byte var3, String var4, RegistryPrx var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;
}

