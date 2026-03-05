/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.TieBase
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.TieBase;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._ChatRoomDisp;
import com.projectgoth.fusion.slice._ChatRoomOperations;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class _ChatRoomTie
extends _ChatRoomDisp
implements TieBase {
    private _ChatRoomOperations _ice_delegate;

    public _ChatRoomTie() {
    }

    public _ChatRoomTie(_ChatRoomOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_ChatRoomOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _ChatRoomTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_ChatRoomTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    @Override
    public void botKilled(String botInstanceID, Current __current) throws FusionException {
        this._ice_delegate.botKilled(botInstanceID, __current);
    }

    @Override
    public String[] getParticipants(String requestingUsername, Current __current) {
        return this._ice_delegate.getParticipants(requestingUsername, __current);
    }

    @Override
    public boolean isParticipant(String username, Current __current) throws FusionException {
        return this._ice_delegate.isParticipant(username, __current);
    }

    @Override
    public void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp, Current __current) throws FusionException {
        this._ice_delegate.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp, __current);
    }

    @Override
    public void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp, Current __current) throws FusionException {
        this._ice_delegate.putBotMessageToAllUsers(botInstanceID, message, emoticonHotKeys, displayPopUp, __current);
    }

    @Override
    public void putBotMessageToUsers(String botInstanceID, String[] usernames, String message, String[] emoticonHotKeys, boolean displayPopUp, Current __current) throws FusionException {
        this._ice_delegate.putBotMessageToUsers(botInstanceID, usernames, message, emoticonHotKeys, displayPopUp, __current);
    }

    @Override
    public void sendGamesHelpToUser(String username, Current __current) throws FusionException {
        this._ice_delegate.sendGamesHelpToUser(username, __current);
    }

    @Override
    public void sendMessageToBots(String username, String message, long receivedTimestamp, Current __current) throws FusionException {
        this._ice_delegate.sendMessageToBots(username, message, receivedTimestamp, __current);
    }

    @Override
    public void startBot(String username, String botCommandName, Current __current) throws FusionException {
        this._ice_delegate.startBot(username, botCommandName, __current);
    }

    @Override
    public void stopAllBots(String username, int timeout, Current __current) throws FusionException {
        this._ice_delegate.stopAllBots(username, timeout, __current);
    }

    @Override
    public void stopBot(String username, String botCommandName, Current __current) throws FusionException {
        this._ice_delegate.stopBot(username, botCommandName, __current);
    }

    @Override
    public void addGroupModerator(String instigator, String targetUser, Current __current) throws FusionException {
        this._ice_delegate.addGroupModerator(instigator, targetUser, __current);
    }

    @Override
    public void addModerator(String username, Current __current) {
        this._ice_delegate.addModerator(username, __current);
    }

    @Override
    public void addParticipant(UserPrx userProxy, UserDataIce userData, SessionPrx sessionProxy, String sessionID, String ipAddress, String mobileDevice, String userAgent, short clientVersion, int deviceType, Current __current) throws FusionException {
        this._ice_delegate.addParticipant(userProxy, userData, sessionProxy, sessionID, ipAddress, mobileDevice, userAgent, clientVersion, deviceType, __current);
    }

    @Override
    public void addParticipantOld(UserPrx userProxy, UserDataIce userData, SessionPrx sessionProxy, String sessionID, String ipAddress, String mobileDevice, String userAgent, Current __current) throws FusionException {
        this._ice_delegate.addParticipantOld(userProxy, userData, sessionProxy, sessionID, ipAddress, mobileDevice, userAgent, __current);
    }

    @Override
    public void adminAnnounce(String announceMessage, int waitTime, Current __current) throws FusionException {
        this._ice_delegate.adminAnnounce(announceMessage, waitTime, __current);
    }

    @Override
    public void announceOff(String announcer, Current __current) throws FusionException {
        this._ice_delegate.announceOff(announcer, __current);
    }

    @Override
    public void announceOn(String announcer, String announceMessage, int waitTime, Current __current) throws FusionException {
        this._ice_delegate.announceOn(announcer, announceMessage, waitTime, __current);
    }

    @Override
    public void banGroupMembers(String[] banList, String instigator, int reasonCode, Current __current) throws FusionException {
        this._ice_delegate.banGroupMembers(banList, instigator, reasonCode, __current);
    }

    @Override
    public void banIndexes(int[] indexes, String bannedBy, int reasonCode, Current __current) throws FusionException {
        this._ice_delegate.banIndexes(indexes, bannedBy, reasonCode, __current);
    }

    @Override
    public void banMultiIds(String username, Current __current) throws FusionException {
        this._ice_delegate.banMultiIds(username, __current);
    }

    @Override
    public void banUser(String username, Current __current) {
        this._ice_delegate.banUser(username, __current);
    }

    @Override
    public void broadcastMessage(String instigator, String message, Current __current) throws FusionException {
        this._ice_delegate.broadcastMessage(instigator, message, __current);
    }

    @Override
    public void bumpUser(String instigator, String target, Current __current) throws FusionException {
        this._ice_delegate.bumpUser(instigator, target, __current);
    }

    @Override
    public void changeOwner(String oldOwnerUsername, String newOwnerUsername, Current __current) {
        this._ice_delegate.changeOwner(oldOwnerUsername, newOwnerUsername, __current);
    }

    @Override
    public void clearUserKick(String instigator, String target, Current __current) throws FusionException {
        this._ice_delegate.clearUserKick(instigator, target, __current);
    }

    @Override
    public void convertIntoGroupChatRoom(int groupID, String groupName, Current __current) throws FusionException {
        this._ice_delegate.convertIntoGroupChatRoom(groupID, groupName, __current);
    }

    @Override
    public void convertIntoUserOwnedChatRoom(Current __current) throws FusionException {
        this._ice_delegate.convertIntoUserOwnedChatRoom(__current);
    }

    @Override
    public int executeEmoteCommandWithState(String emoteCommand, MessageDataIce message, SessionPrx sessionProxy, Current __current) throws FusionException {
        return this._ice_delegate.executeEmoteCommandWithState(emoteCommand, message, sessionProxy, __current);
    }

    @Override
    public String[] getAdministrators(String requestingUsername, Current __current) {
        return this._ice_delegate.getAdministrators(requestingUsername, __current);
    }

    @Override
    public String[] getAllParticipants(String requestingUsername, Current __current) {
        return this._ice_delegate.getAllParticipants(requestingUsername, __current);
    }

    @Override
    public String[] getGroupModerators(String instigator, Current __current) throws FusionException {
        return this._ice_delegate.getGroupModerators(instigator, __current);
    }

    @Override
    public int getMaximumMessageLength(String sender, Current __current) {
        return this._ice_delegate.getMaximumMessageLength(sender, __current);
    }

    @Override
    public int getNumParticipants(Current __current) {
        return this._ice_delegate.getNumParticipants(__current);
    }

    @Override
    public ChatRoomDataIce getRoomData(Current __current) {
        return this._ice_delegate.getRoomData(__current);
    }

    @Override
    public Map<String, String> getTheme(Current __current) {
        return this._ice_delegate.getTheme(__current);
    }

    @Override
    public void inviteUserToGroup(String invitee, String inviter, Current __current) throws FusionException {
        this._ice_delegate.inviteUserToGroup(invitee, inviter, __current);
    }

    @Override
    public boolean isLocked(Current __current) {
        return this._ice_delegate.isLocked(__current);
    }

    @Override
    public boolean isVisibleParticipant(String username, Current __current) throws FusionException {
        return this._ice_delegate.isVisibleParticipant(username, __current);
    }

    @Override
    public void kickIndexes(int[] indexes, String bannedBy, Current __current) throws FusionException {
        this._ice_delegate.kickIndexes(indexes, bannedBy, __current);
    }

    @Override
    public void listParticipants(String requestingUsername, int size, int startIndex, Current __current) throws FusionException {
        this._ice_delegate.listParticipants(requestingUsername, size, startIndex, __current);
    }

    @Override
    public void lock(String locker, Current __current) throws FusionException {
        this._ice_delegate.lock(locker, __current);
    }

    @Override
    public void mute(String username, String target, Current __current) throws FusionException {
        this._ice_delegate.mute(username, target, __current);
    }

    @Override
    public void putMessage(MessageDataIce message, String sessionID, Current __current) throws FusionException {
        this._ice_delegate.putMessage(message, sessionID, __current);
    }

    @Override
    public void putSystemMessage(String messageText, String[] emoticonKeys, Current __current) {
        this._ice_delegate.putSystemMessage(messageText, emoticonKeys, __current);
    }

    @Override
    public void putSystemMessageWithColour(String messageText, String[] emoticonKeys, int messageColour, Current __current) {
        this._ice_delegate.putSystemMessageWithColour(messageText, emoticonKeys, messageColour, __current);
    }

    @Override
    public void removeGroupModerator(String instigator, String targetUser, Current __current) throws FusionException {
        this._ice_delegate.removeGroupModerator(instigator, targetUser, __current);
    }

    @Override
    public void removeModerator(String username, Current __current) {
        this._ice_delegate.removeModerator(username, __current);
    }

    @Override
    public void removeParticipant(String username, Current __current) throws FusionException {
        this._ice_delegate.removeParticipant(username, __current);
    }

    @Override
    public void removeParticipantOneWay(String username, boolean removeFromUsersChatRoomList, Current __current) {
        this._ice_delegate.removeParticipantOneWay(username, removeFromUsersChatRoomList, __current);
    }

    @Override
    public void setAdultOnly(boolean adultOnly, Current __current) {
        this._ice_delegate.setAdultOnly(adultOnly, __current);
    }

    @Override
    public void setAllowKicking(boolean allowKicking, Current __current) {
        this._ice_delegate.setAllowKicking(allowKicking, __current);
    }

    @Override
    public void setDescription(String description, Current __current) {
        this._ice_delegate.setDescription(description, __current);
    }

    @Override
    public void setMaximumSize(int maximumSize, Current __current) {
        this._ice_delegate.setMaximumSize(maximumSize, __current);
    }

    @Override
    public void setNumberOfFakeParticipants(String username, int number, Current __current) {
        this._ice_delegate.setNumberOfFakeParticipants(username, number, __current);
    }

    @Override
    public void silence(String username, int timeout, Current __current) throws FusionException {
        this._ice_delegate.silence(username, timeout, __current);
    }

    @Override
    public void silenceUser(String instigator, String target, int timeout, Current __current) throws FusionException {
        this._ice_delegate.silenceUser(instigator, target, timeout, __current);
    }

    @Override
    public void submitGiftAllTask(int giftId, String giftMessage, MessageDataIce message, Current __current) throws FusionException {
        this._ice_delegate.submitGiftAllTask(giftId, giftMessage, message, __current);
    }

    @Override
    public void unbanGroupMember(String target, String instigator, int reasonCode, Current __current) throws FusionException {
        this._ice_delegate.unbanGroupMember(target, instigator, reasonCode, __current);
    }

    @Override
    public void unbanUser(String username, Current __current) {
        this._ice_delegate.unbanUser(username, __current);
    }

    @Override
    public void unlock(String unlocker, Current __current) throws FusionException {
        this._ice_delegate.unlock(unlocker, __current);
    }

    @Override
    public void unmute(String username, String target, Current __current) throws FusionException {
        this._ice_delegate.unmute(username, target, __current);
    }

    @Override
    public void unsilence(String username, Current __current) throws FusionException {
        this._ice_delegate.unsilence(username, __current);
    }

    @Override
    public void unsilenceUser(String instigator, String target, Current __current) throws FusionException {
        this._ice_delegate.unsilenceUser(instigator, target, __current);
    }

    @Override
    public void updateDescription(String instigator, String description, Current __current) throws FusionException {
        this._ice_delegate.updateDescription(instigator, description, __current);
    }

    @Override
    public void updateExtraData(ChatRoomDataIce data, Current __current) {
        this._ice_delegate.updateExtraData(data, __current);
    }

    @Override
    public void updateGroupModeratorStatus(String username, boolean promote, Current __current) {
        this._ice_delegate.updateGroupModeratorStatus(username, promote, __current);
    }

    @Override
    public void voteToKickUser(String voter, String target, Current __current) throws FusionException {
        this._ice_delegate.voteToKickUser(voter, target, __current);
    }

    @Override
    public void warnUser(String instigator, String target, String message, Current __current) throws FusionException {
        this._ice_delegate.warnUser(instigator, target, message, __current);
    }
}

