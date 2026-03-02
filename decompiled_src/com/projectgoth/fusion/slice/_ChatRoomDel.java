/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._BotChannelDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _ChatRoomDel
extends _BotChannelDel {
    public void addParticipantOld(UserPrx var1, UserDataIce var2, SessionPrx var3, String var4, String var5, String var6, String var7, Map<String, String> var8) throws LocalExceptionWrapper, FusionException;

    public void addParticipant(UserPrx var1, UserDataIce var2, SessionPrx var3, String var4, String var5, String var6, String var7, short var8, int var9, Map<String, String> var10) throws LocalExceptionWrapper, FusionException;

    public void removeParticipant(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void removeParticipantOneWay(String var1, boolean var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void addModerator(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void removeModerator(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void banUser(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void unbanUser(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void banGroupMembers(String[] var1, String var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void unbanGroupMember(String var1, String var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void banMultiIds(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void inviteUserToGroup(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void broadcastMessage(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void setMaximumSize(int var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void setDescription(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void updateDescription(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void setAllowKicking(boolean var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void setAdultOnly(boolean var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void changeOwner(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public ChatRoomDataIce getRoomData(Map<String, String> var1) throws LocalExceptionWrapper;

    public String[] getAllParticipants(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public String[] getAdministrators(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public int getNumParticipants(Map<String, String> var1) throws LocalExceptionWrapper;

    public boolean isVisibleParticipant(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void listParticipants(String var1, int var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void banIndexes(int[] var1, String var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void kickIndexes(int[] var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void bumpUser(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void warnUser(String var1, String var2, String var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void voteToKickUser(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void clearUserKick(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void putMessage(MessageDataIce var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void putSystemMessage(String var1, String[] var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void putSystemMessageWithColour(String var1, String[] var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper;

    public int getMaximumMessageLength(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void addGroupModerator(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void removeGroupModerator(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public String[] getGroupModerators(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void mute(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void unmute(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void unsilence(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void unsilenceUser(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void silence(String var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void silenceUser(String var1, String var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void setNumberOfFakeParticipants(String var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void convertIntoUserOwnedChatRoom(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public void convertIntoGroupChatRoom(int var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public boolean isLocked(Map<String, String> var1) throws LocalExceptionWrapper;

    public void lock(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void unlock(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void announceOff(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void announceOn(String var1, String var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void adminAnnounce(String var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public Map<String, String> getTheme(Map<String, String> var1) throws LocalExceptionWrapper;

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void submitGiftAllTask(int var1, String var2, MessageDataIce var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void updateExtraData(ChatRoomDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void updateGroupModeratorStatus(String var1, boolean var2, Map<String, String> var3) throws LocalExceptionWrapper;
}

