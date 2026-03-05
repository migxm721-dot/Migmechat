/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._BotChannelOperations;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _ChatRoomOperations
extends _BotChannelOperations {
    public void addParticipantOld(UserPrx var1, UserDataIce var2, SessionPrx var3, String var4, String var5, String var6, String var7, Current var8) throws FusionException;

    public void addParticipant(UserPrx var1, UserDataIce var2, SessionPrx var3, String var4, String var5, String var6, String var7, short var8, int var9, Current var10) throws FusionException;

    public void removeParticipant(String var1, Current var2) throws FusionException;

    public void removeParticipantOneWay(String var1, boolean var2, Current var3);

    public void addModerator(String var1, Current var2);

    public void removeModerator(String var1, Current var2);

    public void banUser(String var1, Current var2);

    public void unbanUser(String var1, Current var2);

    public void banGroupMembers(String[] var1, String var2, int var3, Current var4) throws FusionException;

    public void unbanGroupMember(String var1, String var2, int var3, Current var4) throws FusionException;

    public void banMultiIds(String var1, Current var2) throws FusionException;

    public void inviteUserToGroup(String var1, String var2, Current var3) throws FusionException;

    public void broadcastMessage(String var1, String var2, Current var3) throws FusionException;

    public void setMaximumSize(int var1, Current var2);

    public void setDescription(String var1, Current var2);

    public void updateDescription(String var1, String var2, Current var3) throws FusionException;

    public void setAllowKicking(boolean var1, Current var2);

    public void setAdultOnly(boolean var1, Current var2);

    public void changeOwner(String var1, String var2, Current var3);

    public ChatRoomDataIce getRoomData(Current var1);

    public String[] getAllParticipants(String var1, Current var2);

    public String[] getAdministrators(String var1, Current var2);

    public int getNumParticipants(Current var1);

    public boolean isVisibleParticipant(String var1, Current var2) throws FusionException;

    public void listParticipants(String var1, int var2, int var3, Current var4) throws FusionException;

    public void banIndexes(int[] var1, String var2, int var3, Current var4) throws FusionException;

    public void kickIndexes(int[] var1, String var2, Current var3) throws FusionException;

    public void bumpUser(String var1, String var2, Current var3) throws FusionException;

    public void warnUser(String var1, String var2, String var3, Current var4) throws FusionException;

    public void voteToKickUser(String var1, String var2, Current var3) throws FusionException;

    public void clearUserKick(String var1, String var2, Current var3) throws FusionException;

    public void putMessage(MessageDataIce var1, String var2, Current var3) throws FusionException;

    public void putSystemMessage(String var1, String[] var2, Current var3);

    public void putSystemMessageWithColour(String var1, String[] var2, int var3, Current var4);

    public int getMaximumMessageLength(String var1, Current var2);

    public void addGroupModerator(String var1, String var2, Current var3) throws FusionException;

    public void removeGroupModerator(String var1, String var2, Current var3) throws FusionException;

    public String[] getGroupModerators(String var1, Current var2) throws FusionException;

    public void mute(String var1, String var2, Current var3) throws FusionException;

    public void unmute(String var1, String var2, Current var3) throws FusionException;

    public void unsilence(String var1, Current var2) throws FusionException;

    public void unsilenceUser(String var1, String var2, Current var3) throws FusionException;

    public void silence(String var1, int var2, Current var3) throws FusionException;

    public void silenceUser(String var1, String var2, int var3, Current var4) throws FusionException;

    public void setNumberOfFakeParticipants(String var1, int var2, Current var3);

    public void convertIntoUserOwnedChatRoom(Current var1) throws FusionException;

    public void convertIntoGroupChatRoom(int var1, String var2, Current var3) throws FusionException;

    public boolean isLocked(Current var1);

    public void lock(String var1, Current var2) throws FusionException;

    public void unlock(String var1, Current var2) throws FusionException;

    public void announceOff(String var1, Current var2) throws FusionException;

    public void announceOn(String var1, String var2, int var3, Current var4) throws FusionException;

    public void adminAnnounce(String var1, int var2, Current var3) throws FusionException;

    public Map<String, String> getTheme(Current var1);

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3, Current var4) throws FusionException;

    public void submitGiftAllTask(int var1, String var2, MessageDataIce var3, Current var4) throws FusionException;

    public void updateExtraData(ChatRoomDataIce var1, Current var2);

    public void updateGroupModeratorStatus(String var1, boolean var2, Current var3);
}

