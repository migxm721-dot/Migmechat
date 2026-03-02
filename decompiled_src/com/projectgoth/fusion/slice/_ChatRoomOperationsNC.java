/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._BotChannelOperationsNC;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _ChatRoomOperationsNC
extends _BotChannelOperationsNC {
    public void addParticipantOld(UserPrx var1, UserDataIce var2, SessionPrx var3, String var4, String var5, String var6, String var7) throws FusionException;

    public void addParticipant(UserPrx var1, UserDataIce var2, SessionPrx var3, String var4, String var5, String var6, String var7, short var8, int var9) throws FusionException;

    public void removeParticipant(String var1) throws FusionException;

    public void removeParticipantOneWay(String var1, boolean var2);

    public void addModerator(String var1);

    public void removeModerator(String var1);

    public void banUser(String var1);

    public void unbanUser(String var1);

    public void banGroupMembers(String[] var1, String var2, int var3) throws FusionException;

    public void unbanGroupMember(String var1, String var2, int var3) throws FusionException;

    public void banMultiIds(String var1) throws FusionException;

    public void inviteUserToGroup(String var1, String var2) throws FusionException;

    public void broadcastMessage(String var1, String var2) throws FusionException;

    public void setMaximumSize(int var1);

    public void setDescription(String var1);

    public void updateDescription(String var1, String var2) throws FusionException;

    public void setAllowKicking(boolean var1);

    public void setAdultOnly(boolean var1);

    public void changeOwner(String var1, String var2);

    public ChatRoomDataIce getRoomData();

    public String[] getAllParticipants(String var1);

    public String[] getAdministrators(String var1);

    public int getNumParticipants();

    public boolean isVisibleParticipant(String var1) throws FusionException;

    public void listParticipants(String var1, int var2, int var3) throws FusionException;

    public void banIndexes(int[] var1, String var2, int var3) throws FusionException;

    public void kickIndexes(int[] var1, String var2) throws FusionException;

    public void bumpUser(String var1, String var2) throws FusionException;

    public void warnUser(String var1, String var2, String var3) throws FusionException;

    public void voteToKickUser(String var1, String var2) throws FusionException;

    public void clearUserKick(String var1, String var2) throws FusionException;

    public void putMessage(MessageDataIce var1, String var2) throws FusionException;

    public void putSystemMessage(String var1, String[] var2);

    public void putSystemMessageWithColour(String var1, String[] var2, int var3);

    public int getMaximumMessageLength(String var1);

    public void addGroupModerator(String var1, String var2) throws FusionException;

    public void removeGroupModerator(String var1, String var2) throws FusionException;

    public String[] getGroupModerators(String var1) throws FusionException;

    public void mute(String var1, String var2) throws FusionException;

    public void unmute(String var1, String var2) throws FusionException;

    public void unsilence(String var1) throws FusionException;

    public void unsilenceUser(String var1, String var2) throws FusionException;

    public void silence(String var1, int var2) throws FusionException;

    public void silenceUser(String var1, String var2, int var3) throws FusionException;

    public void setNumberOfFakeParticipants(String var1, int var2);

    public void convertIntoUserOwnedChatRoom() throws FusionException;

    public void convertIntoGroupChatRoom(int var1, String var2) throws FusionException;

    public boolean isLocked();

    public void lock(String var1) throws FusionException;

    public void unlock(String var1) throws FusionException;

    public void announceOff(String var1) throws FusionException;

    public void announceOn(String var1, String var2, int var3) throws FusionException;

    public void adminAnnounce(String var1, int var2) throws FusionException;

    public Map<String, String> getTheme();

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3) throws FusionException;

    public void submitGiftAllTask(int var1, String var2, MessageDataIce var3) throws FusionException;

    public void updateExtraData(ChatRoomDataIce var1);

    public void updateGroupModeratorStatus(String var1, boolean var2);
}

