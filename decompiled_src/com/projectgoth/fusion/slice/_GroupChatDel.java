/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice._BotChannelDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _GroupChatDel
extends _BotChannelDel {
    public void addParticipantInner(String var1, String var2, boolean var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void addParticipant(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public boolean removeParticipant(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putMessage(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putFileReceived(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void sendInitialMessages(Map<String, String> var1) throws LocalExceptionWrapper;

    public int getNumParticipants(Map<String, String> var1) throws LocalExceptionWrapper;

    public boolean supportsBinaryMessage(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public String getId(Map<String, String> var1) throws LocalExceptionWrapper;

    public String getCreatorUsername(Map<String, String> var1) throws LocalExceptionWrapper;

    public int getCreatorUserID(Map<String, String> var1) throws LocalExceptionWrapper;

    public int getPrivateChatPartnerUserID(Map<String, String> var1) throws LocalExceptionWrapper;

    public String listOfParticipants(Map<String, String> var1) throws LocalExceptionWrapper;

    public int[] getParticipantUserIDs(Map<String, String> var1) throws LocalExceptionWrapper;

    public void addParticipants(String var1, String[] var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void addUserToGroupChatDebug(String var1, boolean var2, boolean var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;
}

