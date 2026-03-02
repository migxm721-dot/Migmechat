/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.objectcache.ChatRoom;
import com.projectgoth.fusion.objectcache.ChatRoomInfo;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.FusionException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;
import javax.ejb.CreateException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ChatRoomDataWrapper {
    private final ChatRoom chatRoom;
    private final ChatRoomData data;
    private GroupData groupData;

    public ChatRoomDataWrapper(ChatRoom chatRoom, ChatRoomData chatRoomData, GroupData groupData) {
        this.chatRoom = chatRoom;
        this.data = chatRoomData;
        this.groupData = groupData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ChatRoomData snapshotChatRoomData() {
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            return new ChatRoomData(this.data);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ChatRoomDataIce getRoomData() {
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            this.data.size = this.chatRoom.getNumParticipants();
            return this.data.toIceObject();
        }
    }

    public String getName() {
        return this.data.name;
    }

    public Integer getGroupID() {
        return this.data.groupID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateExtraData(ChatRoomDataIce newChatRoomDataWithExtraData) {
        ChatRoomData newData = new ChatRoomData(newChatRoomDataWithExtraData);
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            this.data.updateExtraData(newData);
        }
    }

    public void addModerator(String username) {
        this.data.moderators.add(username);
    }

    public void removeModerator(String username) {
        this.data.moderators.remove(username);
    }

    public boolean isModerator(String username) {
        return this.data.isModerator(username);
    }

    public Map<String, String> getTheme() {
        return this.data.theme;
    }

    public boolean isAllowBots() {
        return this.data.allowBots;
    }

    public Integer botID() {
        return this.data.botID;
    }

    public boolean isAllowKicking() {
        return this.data.allowKicking;
    }

    public boolean isStadium() {
        return this.data.isStadium();
    }

    public boolean isUserOwned() {
        return this.data.isUserOwned();
    }

    public String getCreator() {
        return this.data.creator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMaximumSize(int maximumSize) {
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            this.data.maximumSize = maximumSize;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAllowKicking(boolean allowKicking) {
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            this.data.allowKicking = allowKicking;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDescription(String description) {
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            this.data.description = description;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAdultOnly(boolean adultOnly) {
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            this.data.adultOnly = adultOnly;
        }
    }

    public Integer getPrimaryCountryID() {
        return this.data.primaryCountryID;
    }

    public Integer getSecondaryCountryID() {
        return this.data.secondaryCountryID;
    }

    public int getID() {
        return this.data.id;
    }

    public boolean isOnBannedList(String username) {
        return this.data.isOnBannedList(username);
    }

    public void addBannedUser(String username) {
        this.data.bannedUsers.add(username);
    }

    public void removeBannedUser(String username) {
        this.data.bannedUsers.remove(username);
    }

    public void setGroupID(Integer id) {
    }

    public void clearModerators() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setCreator(String newOwnerUsername) {
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            this.data.creator = newOwnerUsername;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean updateDBAccessed(int dbUpdateInterval) {
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            if (System.currentTimeMillis() - this.data.dateLastAccessed.getTime() <= (long)dbUpdateInterval) {
                return false;
            }
            this.data.dateLastAccessed = new Date();
        }
        return true;
    }

    public boolean isLocked() {
        return this.data.isLocked();
    }

    public void unlock() {
        this.data.unlock();
    }

    public String getLocker() {
        return this.data.getLocker();
    }

    public void lock(String locker) {
        this.data.lock(locker);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tryLock(String locker) throws FusionException {
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            if (this.data.isLocked()) {
                throw new FusionException("This chat room has already been locked by " + (locker.equals(this.data.getLocker()) ? "you" : "[" + this.data.getLocker() + "]"));
            }
            this.data.lock(locker);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String tryUnlock() throws FusionException {
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            if (!this.data.isLocked()) {
                throw new FusionException("This chat room is not locked. You can not unlock an unlocked room.");
            }
            return this.data.unlock();
        }
    }

    public String getAnnouncer() {
        return this.data.getAnnouncer();
    }

    public void verifySpecificBotOnly(String botName) throws FusionException, RemoteException, CreateException {
        Message messageEJB;
        BotData botData;
        if (this.data.botID != null && (botData = (messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class)).getBot(this.data.botID)) != null && !botData.getCommandName().equals(botName)) {
            throw new FusionException("Only " + botData.getDisplayName() + " is allowed to be run in this room");
        }
    }

    public void verifyBotsAllowed() throws FusionException {
        if (this.data.allowBots == null || !this.data.allowBots.booleanValue()) {
            throw new FusionException("Bots are not supported in this room");
        }
    }

    public int getMaximumSize() {
        return this.data.maximumSize;
    }

    public int getSecondsToBlock() {
        if (this.data.blockPeriodByIpInSeconds != null) {
            return this.data.blockPeriodByIpInSeconds;
        }
        return SystemProperty.getInt(SystemPropertyEntities.Chatroom.BLOCK_PERIOD_BY_IP_IN_SECONDS);
    }

    public void verifyMigLevel(UserData userData, boolean hasAdminOrModeratorRights) throws FusionException {
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.MULTI_ID_CONTROL_ENABLED) && !hasAdminOrModeratorRights && this.data.minMigLevel != null) {
            int migLevel = -1;
            try {
                migLevel = MemCacheOrEJB.getUserReputationLevel(userData.username, userData.userID);
            }
            catch (Exception e) {
                throw new FusionException("Unable to check your mig level for entering the " + this.data.name + " room at this time. Please try again later");
            }
            if (migLevel < this.data.minMigLevel) {
                throw new FusionException(String.format("You must have mig level of %d or above to enter the chat room", this.data.minMigLevel));
            }
        }
    }

    public String getRateLimitByIp() {
        return this.data.rateLimitByIp;
    }

    public ChatRoomData getNewChatRoomData() {
        return new ChatRoomData(this.getRoomData());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void makeUserOwned() {
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            this.data.groupID = null;
            this.data.userOwned = true;
        }
    }

    public boolean replaceMessageDescription(MessageData messageData) {
        if (this.data.description != null && this.data.description.length() > 0) {
            messageData.messageText = this.data.description;
            return true;
        }
        return false;
    }

    public String getAnnounceMessage() {
        return this.data.getAnnounceMessage();
    }

    public boolean isAnnouncementOn() {
        return this.data.isAnnouncementOn();
    }

    public void setAnnouncementOff() {
        this.data.setAnnouncementOff();
    }

    public void setAnnouncementOn(String announcer, String announceMessage) {
        this.data.setAnnouncementOn(announcer, announceMessage);
    }

    public GroupData getGroupData() {
        return this.groupData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ChatRoomInfo getInfo() {
        ChatRoomInfo info = new ChatRoomInfo();
        ChatRoomData chatRoomData = this.data;
        synchronized (chatRoomData) {
            info.chatRoomName = this.data.name;
            info.groupID = this.data.groupID;
            info.groupName = this.groupData.name;
            info.groupOwner = this.groupData.createdBy;
        }
        return info;
    }

    public GroupData convertIntoGroupChatRoom(GroupData tmpGroupData) {
        this.groupData = tmpGroupData;
        this.setGroupID(this.groupData.id);
        this.clearModerators();
        return this.groupData;
    }

    public boolean hasGroupData() {
        return this.groupData != null;
    }

    public GroupData convertIntoUserChatRoom() {
        if (this.groupData == null) {
            return null;
        }
        GroupData old = this.groupData;
        this.groupData = null;
        return old;
    }
}

