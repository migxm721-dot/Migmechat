/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import Ice.Current;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.objectcache.ChatRoom;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._ChatRoomDisp;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatRoomRpcI
extends _ChatRoomDisp {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatRoomRpcI.class));
    private ChatRoom room;

    public ChatRoomRpcI(ChatRoom room) {
        this.room = room;
    }

    @Override
    public ChatRoomDataIce getRoomData(Current current) {
        return this.room.getRoomData();
    }

    @Override
    public void setMaximumSize(int maximumSize, Current current) {
        this.room.setMaximumSize(maximumSize);
    }

    @Override
    public void setAllowKicking(boolean allowKicking, Current current) {
        this.room.setAllowKicking(allowKicking);
    }

    @Override
    public void setDescription(String description, Current current) {
        this.room.setDescription(description);
    }

    @Override
    public void updateDescription(String instigator, String description, Current __current) throws FusionException {
        this.room.updateDescription(instigator, description);
    }

    @Override
    public void setAdultOnly(boolean adultOnly, Current current) {
        this.room.setAdultOnly(adultOnly);
    }

    @Override
    public void mute(String username, String target, Current current) throws FusionException {
        this.room.mute(username, target);
    }

    @Override
    public void unmute(String username, String target, Current current) throws FusionException {
        this.room.unmute(username, target);
    }

    @Override
    public void silence(String username, int timeout, Current current) throws FusionException {
        this.room.silence(username, timeout);
    }

    @Override
    public void silenceUser(String instigator, String target, int timeout, Current current) throws FusionException {
        this.room.silenceUser(instigator, target, timeout);
    }

    @Override
    public void unsilence(String username, Current current) throws FusionException {
        this.room.unsilence(username);
    }

    @Override
    public void unsilenceUser(String instigator, String target, Current current) throws FusionException {
        this.room.unsilenceUser(instigator, target);
    }

    @Override
    public void convertIntoGroupChatRoom(int groupID, String groupName, Current current) throws FusionException {
        this.room.convertIntoGroupChatRoom(groupID, groupName);
    }

    @Override
    public void convertIntoUserOwnedChatRoom(Current current) throws FusionException {
        this.room.convertIntoUserOwnedChatRoom();
    }

    @Override
    public void changeOwner(String oldOwnerUsername, String newOwnerUsername, Current current) {
        this.room.changeOwner(oldOwnerUsername, newOwnerUsername);
    }

    @Override
    public void addParticipantOld(UserPrx userPrx, UserDataIce userData, SessionPrx sessionPrx, String sessionID, String ipAddress, String mobileDevice, String userAgent, Current current) throws FusionException {
        UserData ud = new UserData(userData);
        this.room.addParticipant(userPrx, ud, sessionPrx, sessionID, ipAddress, mobileDevice, userAgent);
    }

    @Override
    public void addParticipant(UserPrx userPrx, UserDataIce userData, SessionPrx sessionPrx, String sessionID, String ipAddress, String mobileDevice, String userAgent, short clientVersion, int deviceType, Current current) throws FusionException {
        UserData ud = new UserData(userData);
        this.room.addParticipant(userPrx, ud, sessionPrx, sessionID, ipAddress, mobileDevice, userAgent, clientVersion, deviceType);
    }

    @Override
    public void removeParticipant(String username, Current current) throws FusionException {
        this.room.removeParticipant(username);
    }

    @Override
    public void removeParticipantOneWay(String username, boolean removeFromUsersChatRoomList, Current current) {
        block2: {
            try {
                this.room.removeParticipant(username, removeFromUsersChatRoomList);
            }
            catch (Exception e) {
                if (!log.isDebugEnabled()) break block2;
                log.debug((Object)("Exception caught when removing user: [" + username + "] from chat room: [" + this.room.getRoomData().name + "]: " + e.getMessage()), (Throwable)e);
            }
        }
    }

    @Override
    public String[] getParticipants(String requestingUsername, Current current) {
        return this.room.getParticipants(requestingUsername);
    }

    @Override
    public String[] getAllParticipants(String requestingUsername, Current current) {
        return this.room.getAllParticipants(requestingUsername);
    }

    @Override
    public String[] getAdministrators(String requestingUsername, Current current) {
        return this.room.getAdministrators(requestingUsername);
    }

    @Override
    public int getNumParticipants(Current current) {
        return this.room.getNumParticipants();
    }

    @Override
    public void setNumberOfFakeParticipants(String username, int number, Current current) {
        this.room.setNumberOfFakeParticipants(username, number);
    }

    @Override
    public boolean isParticipant(String username, Current current) throws FusionException {
        return this.room.isParticipant(username);
    }

    @Override
    public boolean isVisibleParticipant(String username, Current current) throws FusionException {
        return this.room.isVisibleParticipant(username);
    }

    @Override
    public void listParticipants(String requestingUsername, int size, int startIndex, Current current) throws FusionException {
        this.room.listParticipants(requestingUsername, size, startIndex);
    }

    @Override
    public int getMaximumMessageLength(String sender, Current current) {
        return this.room.getMaximumMessageLength(sender);
    }

    @Override
    public void putMessage(MessageDataIce message, String sessionID, Current current) throws FusionException {
        this.room.putMessage(message, sessionID);
    }

    @Override
    public void putSystemMessage(String messageText, String[] emoticonKeys, Current current) {
        this.room.putSystemMessageWithColour(messageText, emoticonKeys, -1);
    }

    @Override
    public void putSystemMessageWithColour(String messageText, String[] emoticonKeys, int messageColour, Current current) {
        this.room.putSystemMessageWithColour(messageText, emoticonKeys, messageColour);
    }

    @Override
    public void addModerator(String username, Current current) {
        this.room.addModerator(username);
    }

    @Override
    public void removeModerator(String username, Current current) {
        this.room.removeModerator(username);
    }

    @Override
    public Map<String, String> getTheme(Current current) {
        return this.room.getTheme();
    }

    @Override
    public void banUser(String username, Current current) {
        this.room.banUser(username);
    }

    @Override
    public void unbanUser(String username, Current current) {
        this.room.unbanUser(username);
    }

    @Override
    public void banGroupMembers(String[] bannedList, String bannedby, int reason, Current __current) throws FusionException {
        this.room.banGroupMembers(bannedList, bannedby, reason);
    }

    @Override
    public void unbanGroupMember(String unbanned, String unbannedby, int reason, Current __current) throws FusionException {
        this.room.unbanGroupMember(unbanned, unbannedby, reason);
    }

    @Override
    public void banIndexes(int[] indexes, String bannedBy, int reason, Current __current) throws FusionException {
        this.room.banIndexes(indexes, bannedBy, reason);
    }

    @Override
    public void banMultiIds(String requestingUsername, Current __current) throws FusionException {
        this.room.banMultiIds(requestingUsername);
    }

    @Override
    public void kickIndexes(int[] indexes, String kickedBy, Current __current) throws FusionException {
        this.room.kickIndexes(indexes, kickedBy);
    }

    @Override
    public void inviteUserToGroup(String invitee, String inviter, Current __current) throws FusionException {
        this.room.inviteUserToGroup(invitee, inviter);
    }

    @Override
    public void broadcastMessage(String instigator, String message, Current __current) throws FusionException {
        this.room.broadcastMessage(instigator, message);
    }

    @Override
    public void voteToKickUser(String voter, String target, Current current) throws FusionException {
        this.room.voteToKickUser(voter, target);
    }

    @Override
    public void clearUserKick(String instigator, String target, Current current) throws FusionException {
        this.room.clearUserKick(instigator, target);
    }

    @Override
    public void startBot(String username, String botName, Current current) throws FusionException {
        this.room.startBot(username, botName);
    }

    @Override
    public void stopBot(String username, String botName, Current current) throws FusionException {
        this.room.stopBot(username, botName);
    }

    @Override
    public void stopAllBots(String username, int timeout, Current current) throws FusionException {
        this.room.stopAllBots(username, timeout);
    }

    @Override
    public void botKilled(String botInstanceID, Current current) throws FusionException {
        this.room.botKilled(botInstanceID);
    }

    @Override
    public void sendMessageToBots(String username, String message, long receivedTimestamp, Current current) throws FusionException {
        this.room.sendMessageToBots(username, message, receivedTimestamp);
    }

    @Override
    public void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp, Current current) throws FusionException {
        this.room.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp);
    }

    @Override
    public void putBotMessageToUsers(String botInstanceID, String[] usernames, String message, String[] emoticonHotKeys, boolean displayPopUp, Current current) throws FusionException {
        this.room.putBotMessageToUsers(botInstanceID, usernames, message, emoticonHotKeys, displayPopUp);
    }

    @Override
    public void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp, Current current) throws FusionException {
        this.room.putBotMessageToAllUsers(botInstanceID, message, emoticonHotKeys, displayPopUp);
    }

    @Override
    public void sendGamesHelpToUser(String username, Current current) throws FusionException {
        this.room.sendGamesHelpToUser(username);
    }

    @Override
    public boolean isLocked(Current __current) {
        return this.room.isLocked();
    }

    @Override
    public void lock(String locker, Current __current) throws FusionException {
        this.room.lock(locker);
    }

    @Override
    public void unlock(String unlocker, Current __current) throws FusionException {
        this.room.unlock(unlocker);
    }

    @Override
    public void announceOff(String announcer, Current __current) throws FusionException {
        this.room.announceOff(announcer);
    }

    @Override
    public void announceOn(String announcer, String announceMessage, int waitTime, Current __current) throws FusionException {
        this.room.announceOn(announcer, announceMessage, waitTime);
    }

    @Override
    public void adminAnnounce(String announceMessage, int waitTime, Current __current) throws FusionException {
        this.room.adminAnnounce(announceMessage, waitTime);
    }

    @Override
    public int executeEmoteCommandWithState(String emoteCommand, MessageDataIce message, SessionPrx sessionProxy, Current __current) throws FusionException {
        return this.room.executeEmoteCommandWithState(emoteCommand, message, sessionProxy);
    }

    @Override
    public void submitGiftAllTask(int giftId, String giftMessage, MessageDataIce message, Current __current) throws FusionException {
        this.room.submitGiftAllTask(giftId, giftMessage, message);
    }

    @Override
    public void updateExtraData(ChatRoomDataIce newChatRoomDataWithExtraData, Current current) {
        this.room.updateExtraData(newChatRoomDataWithExtraData);
    }

    @Override
    public void updateGroupModeratorStatus(String username, boolean promote, Current __current) {
        this.room.updateGroupModeratorStatus(username, promote);
    }

    @Override
    public void bumpUser(String instigator, String target, Current __current) throws FusionException {
        this.room.bumpUser(instigator, target);
    }

    @Override
    public void warnUser(String instigator, String target, String message, Current __current) throws FusionException {
        this.room.warnUser(instigator, target, message);
    }

    @Override
    public void addGroupModerator(String instigator, String target, Current current) throws FusionException {
        this.room.addGroupModerator(instigator, target);
    }

    @Override
    public void removeGroupModerator(String instigator, String target, Current current) throws FusionException {
        this.room.removeGroupModerator(instigator, target);
    }

    @Override
    public String[] getGroupModerators(String instigator, Current current) throws FusionException {
        return this.room.getGroupModerators(instigator);
    }

    public boolean isIdle() {
        return this.room.isIdle();
    }

    public void prepareForPurge() {
        this.room.prepareForPurge();
    }
}

