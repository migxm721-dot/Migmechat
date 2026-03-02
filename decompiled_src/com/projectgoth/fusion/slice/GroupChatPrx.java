/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.BotChannelPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface GroupChatPrx
extends BotChannelPrx {
    public void addParticipantInner(String var1, String var2, boolean var3) throws FusionException;

    public void addParticipantInner(String var1, String var2, boolean var3, Map<String, String> var4) throws FusionException;

    public void addParticipant(String var1, String var2) throws FusionException;

    public void addParticipant(String var1, String var2, Map<String, String> var3) throws FusionException;

    public boolean removeParticipant(String var1) throws FusionException;

    public boolean removeParticipant(String var1, Map<String, String> var2) throws FusionException;

    public void putMessage(MessageDataIce var1) throws FusionException;

    public void putMessage(MessageDataIce var1, Map<String, String> var2) throws FusionException;

    public void putFileReceived(MessageDataIce var1) throws FusionException;

    public void putFileReceived(MessageDataIce var1, Map<String, String> var2) throws FusionException;

    public void sendInitialMessages();

    public void sendInitialMessages(Map<String, String> var1);

    public int getNumParticipants();

    public int getNumParticipants(Map<String, String> var1);

    public boolean supportsBinaryMessage(String var1);

    public boolean supportsBinaryMessage(String var1, Map<String, String> var2);

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3) throws FusionException;

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3, Map<String, String> var4) throws FusionException;

    public String getId();

    public String getId(Map<String, String> var1);

    public String getCreatorUsername();

    public String getCreatorUsername(Map<String, String> var1);

    public int getCreatorUserID();

    public int getCreatorUserID(Map<String, String> var1);

    public int getPrivateChatPartnerUserID();

    public int getPrivateChatPartnerUserID(Map<String, String> var1);

    public String listOfParticipants();

    public String listOfParticipants(Map<String, String> var1);

    public int[] getParticipantUserIDs();

    public int[] getParticipantUserIDs(Map<String, String> var1);

    public void addParticipants(String var1, String[] var2) throws FusionException;

    public void addParticipants(String var1, String[] var2, Map<String, String> var3) throws FusionException;

    public void addUserToGroupChatDebug(String var1, boolean var2, boolean var3) throws FusionException;

    public void addUserToGroupChatDebug(String var1, boolean var2, boolean var3, Map<String, String> var4) throws FusionException;
}

