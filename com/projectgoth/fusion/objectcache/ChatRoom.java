/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  com.danga.MemCached.MemCachedClient
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  org.apache.log4j.Logger
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.objectcache;

import Ice.LocalException;
import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.botservice.BotChannelHelper;
import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.cache.RecentChatRoomList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.common.EJBExceptionWithErrorCause;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.ExceptionHelper;
import com.projectgoth.fusion.common.ExceptionWithErrorCause;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.common.RateLimiter;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.WebCommon;
import com.projectgoth.fusion.data.AccountBalanceData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.AlertMessageData;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.GroupMemberData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emote.ChatRoomWelcomeMessageData;
import com.projectgoth.fusion.emote.EmoteCommandStateStorage;
import com.projectgoth.fusion.emote.GiftAllTask;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.Group;
import com.projectgoth.fusion.interfaces.GroupHome;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.interfaces.Web;
import com.projectgoth.fusion.interfaces.WebHome;
import com.projectgoth.fusion.messagelogger.MessageToLog;
import com.projectgoth.fusion.objectcache.ChatObjectManagerRoom;
import com.projectgoth.fusion.objectcache.ChatRoomDataWrapper;
import com.projectgoth.fusion.objectcache.ChatRoomDataWrapperPreSE351;
import com.projectgoth.fusion.objectcache.ChatRoomInfo;
import com.projectgoth.fusion.objectcache.ChatRoomParticipant;
import com.projectgoth.fusion.objectcache.ChatRoomParticipantListener;
import com.projectgoth.fusion.objectcache.ChatRoomParticipants;
import com.projectgoth.fusion.objectcache.ChatSourceRoom;
import com.projectgoth.fusion.objectcache.ChatroomEntrantSnapshot;
import com.projectgoth.fusion.objectcache.MultiIdFinder;
import com.projectgoth.fusion.objectcache.ObjectCache;
import com.projectgoth.fusion.paintwars.Painter;
import com.projectgoth.fusion.slice.BotInstance;
import com.projectgoth.fusion.slice.BotServicePrx;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.FusionExceptionWithErrorCauseCode;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatRoom
implements ChatSourceRoom,
ChatRoomParticipantListener {
    private ChatObjectManagerRoom objectManager;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatRoom.class));
    private static Logger auditLog = Logger.getLogger((String)"ChatroomAudit");
    private static MemCachedClient recentChatRoomMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.recentChatRooms);
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20);
    private final ChatRoomDataWrapper chatRoomData;
    private final ChatRoomParticipants participants;
    private int numFakeParticipants;
    private Map<String, BannedInfo> bannedUsers = new ConcurrentHashMap<String, BannedInfo>();
    private Map<String, Long> recentlyLeftUsers = new ConcurrentHashMap<String, Long>();
    private Map<String, Long> silencedUsers = new ConcurrentHashMap<String, Long>();
    private Map<String, Boolean> mutedUsers = new ConcurrentHashMap<String, Boolean>();
    private boolean useBoundedQueueForChatRoomMessageDispatch = false;
    private int chatRoomDispatchBoundedQueueMaxSize = Integer.MAX_VALUE;
    private AbstractBlockingQueue<MessageToDispatch> messageQueue = null;
    private Semaphore messageSemaphore = new Semaphore(1);
    private Runnable messageDispatcher = new Runnable(){

        public void run() {
            ChatRoom.this.dispatchMessages();
        }
    };
    private Map<String, BotInstance> bots = new ConcurrentHashMap<String, BotInstance>();
    private Semaphore botSemaphore = new Semaphore(1);
    private String botChannelID;
    private KickUserVote kickUserVote;
    private final Object kickUserVoteMonitor = new Object();
    private final Object adminKickMonitor = new Object();
    private RateLimiter enterLeaveNotificationLimiter;
    private boolean purging;
    private long lastTimeMessageSent = System.currentTimeMillis();
    private int maxMessageLength = 320;
    private int maxStatdiumAdminMessageLength = 500;
    private int maxUserDuration = 28800;
    private int maxMessageRepetitions = 5;
    private int maxEnterLeaveNotifications = 3;
    private int minKickVotesRequired = 3;
    private int minSpamMessageLength = 7;
    private int enterLeaveNotificationInterval = 1;
    private int kickUserVoteDuration = 60;
    private int kickUserVoteUpdateInterval = 20;
    private int timeBetweenKickVotes = 0;
    private int timeBetweenKickingSameUser = 300;
    private int banDuration = 900;
    private int globalAdminBanDuration = 3600;
    private int adminBanDuration = 7200;
    private int grpAdminSuspendDuration = 3600;
    private int grpModSuspendDuration = 3600;
    private int userIdleTimeout = 600;
    private int reenterInterval = 20;
    private long silenceExpiry = 0L;
    private int defaultRoomSilencePeriod = 20;
    private int defaultUserSilencePeriod = 300;
    private int listEntrantBufferSize = 50;
    private int listLockPeriod = 45;
    private int dbUpdateInterval = 1800;
    private long blockBotsUntilTimestamp = 0L;
    private Timer announceTimer;
    ChatroomEntrantSnapshot chatroomEntrantSnapshot;
    private EmoteCommandStateStorage emoteCommandStates;
    private static Pattern MULTI_KICK_PATTERN_CHECK = Pattern.compile("[\\.A-Za-z0-9_-]{1}");
    private static String MIMETYPE_DESCRIPTION = "system/chatroom-description";
    private static String MIMETYPE_MANAGED_BY = "system/chatroom-managed-by";
    private static String MIMETYPE_LOCKED = "system/chatroom-locked";
    private static String MIMETYPE_PARTICIPANTS = "system/chatroom-participants";
    private static String MIMETYPE_ANNOUNCE = "system/chatroom-announce";
    private static String MIMETYPE_STADIUM = "system/chatroom-stadium";
    private static String MIMETYPE_HELP = "system/chatroom-help";
    private static String MIMETYPE_WELCOME = "system/chatroom-welcome";
    private static String MIMETYPE_PARTICPANT_ENTER = "system/chatroom-participant-enter";
    private static String MIMETYPE_PARTICPANT_EXIT = "system/chatroom-participant-exit";
    private static String EMPTY_MIMETYPE_DATA = "{}";

    protected ChatRoom() {
        this.chatRoomData = null;
        this.participants = null;
    }

    public ChatRoom(ChatObjectManagerRoom objectManager, ChatRoomData chatRoomData, GroupData groupData) {
        this.objectManager = objectManager;
        this.chatRoomData = SystemPropertyEntities.Temp.Cache.se351ChatRoomDataConcurrentCollectionsEnabled.getValue() != false ? new ChatRoomDataWrapper(this, chatRoomData, groupData) : new ChatRoomDataWrapperPreSE351(this, chatRoomData, groupData);
        this.participants = new ChatRoomParticipants(chatRoomData.name);
        this.maxMessageLength = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomMaxMessageLength", this.maxMessageLength);
        this.maxStatdiumAdminMessageLength = this.objectManager.getProperties().getPropertyAsIntWithDefault("MaxStatdiumAdminMessageLength", this.maxStatdiumAdminMessageLength);
        this.maxUserDuration = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomMaxUserDuration", this.maxUserDuration) * 1000;
        this.maxMessageRepetitions = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomMaxMessageRepetition", this.maxMessageRepetitions);
        this.maxEnterLeaveNotifications = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomMaxEnterLeaveNotifications", this.maxEnterLeaveNotifications);
        this.minKickVotesRequired = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomKickUserVoteMinVotesRequired", this.minKickVotesRequired);
        this.minSpamMessageLength = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomMinSpamMessageLength", this.minSpamMessageLength);
        this.enterLeaveNotificationInterval = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomEnterLeaveNotificationInterval", this.enterLeaveNotificationInterval) * 1000;
        this.kickUserVoteDuration = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomKickUserVoteDuration", this.kickUserVoteDuration) * 1000;
        this.kickUserVoteUpdateInterval = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomKickUserVoteUpdateInterval", this.kickUserVoteUpdateInterval) * 1000;
        this.timeBetweenKickVotes = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomTimeBetweenKickVotes", this.timeBetweenKickVotes) * 1000;
        this.timeBetweenKickingSameUser = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomTimeBetweenKickingSameUser", this.timeBetweenKickingSameUser) * 1000;
        this.banDuration = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomBanDuration", this.banDuration) * 1000;
        this.globalAdminBanDuration = this.objectManager.getProperties().getPropertyAsIntWithDefault("GlobalAdminBanDuration", this.globalAdminBanDuration) * 1000;
        this.adminBanDuration = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomAdminBanDuration", this.adminBanDuration) * 1000;
        this.grpAdminSuspendDuration = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomGroupAdminSuspendDuration", this.grpAdminSuspendDuration) * 1000;
        this.grpModSuspendDuration = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomModSuspendDuration", this.grpModSuspendDuration) * 1000;
        this.userIdleTimeout = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomUserIdleTimeout", this.userIdleTimeout) * 1000;
        this.reenterInterval = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomReenterInterval", this.reenterInterval) * 1000;
        this.dbUpdateInterval = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomDBUpdateInterval", this.dbUpdateInterval) * 1000;
        this.defaultRoomSilencePeriod = this.objectManager.getProperties().getPropertyAsIntWithDefault("DefaultSilencePeriod", this.defaultRoomSilencePeriod);
        this.defaultUserSilencePeriod = this.objectManager.getProperties().getPropertyAsIntWithDefault("DefaultUserSilencePeriod", this.defaultUserSilencePeriod);
        this.listEntrantBufferSize = this.objectManager.getProperties().getPropertyAsIntWithDefault("ListEntrantBufferSize", this.listEntrantBufferSize);
        this.listLockPeriod = this.objectManager.getProperties().getPropertyAsIntWithDefault("ListLockPeriod", this.listLockPeriod);
        this.useBoundedQueueForChatRoomMessageDispatch = StringUtil.toBooleanOrDefault(this.objectManager.getProperties().getPropertyWithDefault("UseBoundedQueueForChatRoomMessageDispatch", "0"), false);
        if (this.useBoundedQueueForChatRoomMessageDispatch) {
            this.chatRoomDispatchBoundedQueueMaxSize = this.objectManager.getProperties().getPropertyAsIntWithDefault("ChatRoomDispatchBoundedQueueMaxSize", Integer.MAX_VALUE);
            log.info((Object)String.format("Using bounded queue as chatroom message queue, chatroom=%s, maxSize=%d", this.chatRoomData.getName(), this.chatRoomDispatchBoundedQueueMaxSize));
            this.messageQueue = new BlockingQueueViaBlockingQueue(new ArrayBlockingQueue(this.chatRoomDispatchBoundedQueueMaxSize, false));
        } else {
            log.info((Object)String.format("Using linkedlist as chatroom message queue, chatroom=%s, maxSize by sys prop ChatRoom:MsgQueueViaListMaxSize, current value=%d", this.chatRoomData.getName(), SystemProperty.getInt(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_VIA_LIST_MAX_SIZE)));
            this.messageQueue = new BlockingQueueViaList(Collections.synchronizedList(new LinkedList()));
        }
        this.enterLeaveNotificationLimiter = new RateLimiter(this.maxEnterLeaveNotifications, this.enterLeaveNotificationInterval);
        chatRoomData.dateLastAccessed = new Date();
        this.chatroomEntrantSnapshot = new ChatroomEntrantSnapshot(this.listEntrantBufferSize, this.listLockPeriod);
        this.emoteCommandStates = new EmoteCommandStateStorage(objectManager.getIcePrxFinder());
    }

    public void prepareForPurge() {
        this.purging = true;
        log.warn((Object)("Chatroom being purged: " + this.chatRoomData.getName()));
    }

    public boolean isIdle() {
        return this.participants.size() == 0 && this.lastTimeMessageSent < System.currentTimeMillis() - this.objectManager.getChatRoomIdleTimeout();
    }

    public ChatRoomDataIce getRoomData() {
        return this.chatRoomData.getRoomData();
    }

    public boolean isStadium() {
        return this.chatRoomData.isStadium();
    }

    public boolean isUserOwned() {
        return this.chatRoomData.isUserOwned();
    }

    public String getCreator() {
        return this.chatRoomData.getCreator();
    }

    public void setMaximumSize(int maximumSize) {
        this.chatRoomData.setMaximumSize(maximumSize);
    }

    public void setAllowKicking(boolean allowKicking) {
        this.chatRoomData.setAllowKicking(allowKicking);
    }

    public void setDescription(String description) {
        this.chatRoomData.setDescription(description);
    }

    public void setAdultOnly(boolean adultOnly) {
        this.chatRoomData.setAdultOnly(adultOnly);
    }

    public void updateDescription(String instigator, String description) throws FusionException {
        ChatRoomParticipant participant = this.participants.verifyYouAreParticipant(instigator);
        if (this.isGroupLinkedChatroom()) {
            if (!participant.isGroupAdmin() && !participant.isGroupMod()) {
                throw new FusionException("You must be a group admin or moderator to set description");
            }
        } else if (!participant.hasAdminOrModeratorRights()) {
            throw new FusionException("You must be a chatroom admin or moderator to set description");
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            messageEJB.updateRoomDescription(this.chatRoomData.getName(), description);
            this.queueAdminMessage(instigator + " updated description to \"" + description + "\"", null, null);
        }
        catch (Exception e) {
            log.warn((Object)("Failed to update chatroom description. " + e.getMessage()));
            throw new FusionException("Failed to update room description to " + description);
        }
    }

    public void mute(String username, String target) throws FusionException {
        ChatRoomParticipant participant;
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.ENABLE_MUTE)) {
            ChatRoomParticipant targetParticipant;
            participant = this.participants.verifyYouAreParticipant(username);
            if (this.isGroupLinkedChatroom()) {
                if (!participant.isGroupAdmin() && !participant.isGroupMod()) {
                    throw new FusionException("/mute can only be done by group owners and moderators");
                }
            } else if (!participant.hasAdminOrModeratorRights()) {
                throw new FusionException("/mute can only be done by chatroom admins and moderators");
            }
            if ((targetParticipant = this.participants.get(target)) == null) {
                throw new FusionException("The user " + target + " is not in the room");
            }
            if (targetParticipant.hasAdminOrModeratorRights()) {
                throw new FusionException("The user " + target + " is an admin and can't be muted");
            }
            if (this.mutedUsers.containsKey(target)) {
                throw new FusionException("The user " + target + " is already muted");
            }
        } else {
            throw new FusionException("Mute has been deprecated . Use silence instead.");
        }
        this.mutedUsers.put(target, Boolean.TRUE);
        this.queueAdminMessage("You have muted " + target, participant.getUsername(), null);
    }

    public void unmute(String username, String target) throws FusionException {
        ChatRoomParticipant participant;
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.ENABLE_MUTE)) {
            Boolean b;
            participant = this.participants.verifyYouAreParticipant(username);
            if (this.isGroupLinkedChatroom()) {
                if (!participant.isGroupAdmin() && !participant.isGroupMod()) {
                    throw new FusionException("/unmute can only be done by group owners and moderators");
                }
            } else if (!participant.hasAdminOrModeratorRights()) {
                throw new FusionException("/unmute can only be done by chatroom admins and moderators");
            }
            if ((b = this.mutedUsers.remove(target)) == null) {
                throw new FusionException("The user " + target + " is not muted!");
            }
        } else {
            throw new FusionException("Mute has been deprecated . Use silence instead.");
        }
        this.queueAdminMessage("You have unmuted " + target, participant.getUsername(), null);
    }

    public void silence(String username, int timeout) throws FusionException {
        ChatRoomParticipant senderParticipant = this.participants.verifyYouAreParticipant(username);
        if (!senderParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("/silence can only be used by admins and moderators");
        }
        if (timeout < 0) {
            timeout = this.defaultRoomSilencePeriod;
        }
        this.silenceExpiry = System.currentTimeMillis() + (long)(timeout * 1000);
        this.queueAdminMessage("You have silenced this room for " + timeout + "s", username, null);
        this.queueAdminMessage("This room has been silenced by " + username + " for " + timeout + "s. You cannot send messages at the moment.", null, username);
    }

    public void unsilence(String username) throws FusionException {
        ChatRoomParticipant senderParticipant = this.participants.verifyYouAreParticipant(username);
        if (!senderParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("Unsilence can only be used by admins and moderators");
        }
        if (this.silenceExpiry < System.currentTimeMillis()) {
            throw new FusionException("This room it not currently silenced");
        }
        this.silenceExpiry = 0L;
        this.queueAdminMessage("This room has been unsilenced by " + username, null, null);
    }

    public void silenceUser(String instigator, String target, int timeout) throws FusionException {
        ChatRoomParticipant senderParticipant = this.participants.verifyYouAreParticipant(instigator);
        if (!senderParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("/silence can only be used by admins and moderators");
        }
        ChatRoomParticipant targetParticipant = this.participants.verifyIsParticipant(target);
        if (targetParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("Admins and moderators cannot be silenced");
        }
        if (timeout < 0) {
            timeout = this.defaultUserSilencePeriod;
        }
        long expiry = System.currentTimeMillis() + (long)(timeout * 1000);
        this.silencedUsers.put(target, expiry);
        this.queueAdminMessage("You have been silenced by " + instigator + ". You will not be able to send messages for the next " + timeout + " seconds", target, null);
        this.queueAdminMessage(target + " has been silenced by " + instigator + " for " + timeout + " seconds", null, target);
    }

    public void unsilenceUser(String instigator, String target) throws FusionException {
        ChatRoomParticipant senderParticipant = this.participants.verifyYouAreParticipant(instigator);
        if (!senderParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("/unsilence can only be used by admins and moderators");
        }
        Long expiry = this.silencedUsers.get(target);
        if (expiry == null) {
            throw new FusionException(target + " is not currently silenced");
        }
        this.silencedUsers.remove(target);
        this.queueAdminMessage("You have unsilenced " + target, instigator, null);
        this.queueAdminMessage("You have been unsilenced by " + instigator, target, null);
    }

    public void convertIntoGroupChatRoom(int groupID, String groupName) throws FusionException {
        GroupData tmpGroupData;
        try {
            if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_GROUP_DAO)) {
                tmpGroupData = DAOFactory.getInstance().getGroupDAO().getGroup(groupID);
            } else {
                User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                tmpGroupData = userEJB.getGroup(groupID);
            }
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
        if (tmpGroupData == null) {
            throw new FusionException("Invalid group ID " + groupID);
        }
        GroupData groupData = this.chatRoomData.convertIntoGroupChatRoom(tmpGroupData);
        this.queueAdminMessage("This chat room has been attached to the group '" + groupData.name + "' by " + this.chatRoomData.getCreator() + ". You will be disconnected from the room.", null, null);
        for (String participant : this.participants.getAllNames()) {
            this.removeParticipant(participant);
        }
        this.recentlyLeftUsers.clear();
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            messageEJB.resetRoomModerators(this.chatRoomData.getName());
        }
        catch (Exception e) {
            log.error((Object)("Failed to reset moderators " + e.getMessage()));
        }
    }

    public void convertIntoUserOwnedChatRoom() throws FusionException {
        GroupData groupDataRemoved = this.chatRoomData.convertIntoUserChatRoom();
        if (groupDataRemoved == null) {
            return;
        }
        this.chatRoomData.makeUserOwned();
        this.queueAdminMessage("This chat room has been removed from the group '" + groupDataRemoved.name + "' by " + groupDataRemoved.createdBy, null, null);
        for (String participant : this.participants.getAllNames()) {
            this.removeParticipant(participant);
        }
        this.recentlyLeftUsers.clear();
    }

    public void changeOwner(String oldOwnerUsername, String newOwnerUsername) {
        this.chatRoomData.setCreator(newOwnerUsername);
    }

    public void addParticipant(UserPrx userPrx, UserData userData, SessionPrx sessionPrx, String sessionID, String ipAddress, String mobileDevice, String userAgent, short clientVersion, int deviceType) throws FusionException {
        ChatRoomParticipant participant = new ChatRoomParticipant(this.chatRoomData, userPrx, sessionPrx, sessionID, ipAddress, mobileDevice, userAgent, userData, clientVersion, deviceType);
        this.addParticipantInner(participant, userData);
    }

    public void addParticipant(UserPrx userPrx, UserData userData, SessionPrx sessionPrx, String sessionID, String ipAddress, String mobileDevice, String userAgent) throws FusionException {
        ChatRoomParticipant participant = new ChatRoomParticipant(this.chatRoomData, userPrx, sessionPrx, sessionID, ipAddress, mobileDevice, userAgent, userData);
        this.addParticipantInner(participant, userData);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addParticipantInner(final ChatRoomParticipant participant, UserData userData) throws FusionException {
        boolean alreadyInChatRoom;
        ChatRoomData chatRoomDataSnapshot;
        if (participant.isTopMerchant()) {
            try {
                if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
                    participant.setMerchantDetails(new UserObject(participant.getUsername()).getBasicMerchantDetails());
                } else {
                    User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                    participant.setMerchantDetails(userEJB.getBasicMerchantDetails(participant.getUsername()));
                }
            }
            catch (Exception e) {
                throw new FusionException(e.getMessage());
            }
        }
        this.screenUserForEntrance(userData, participant);
        this.removeIdleParticipants();
        ChatRoomDataWrapper chatRoomDataWrapper = this.chatRoomData;
        synchronized (chatRoomDataWrapper) {
            if (!participant.hasAdminOrModeratorRights() && this.participants.size() >= this.chatRoomData.getMaximumSize()) {
                throw new FusionException("Chat room is full");
            }
            chatRoomDataSnapshot = this.chatRoomData.snapshotChatRoomData();
            participant.setOnRemoveListener(this);
            alreadyInChatRoom = this.participants.add(participant) != null;
        }
        this.sendGreetingMessagesAsync(participant, chatRoomDataSnapshot);
        RecentChatRoomList.addRecentChatRoom(recentChatRoomMemcache, participant.getUsername(), this.chatRoomData.getName());
        if (!(alreadyInChatRoom || participant.isHiddenAdmin() || this.isStadium() && !participant.hasAdminOrModeratorRights() || !this.enterLeaveNotificationLimiter.hit())) {
            this.objectManager.onRoomSessionAdded();
            this.queueEntryExitAdminMessage(participant, true);
            this.objectManager.getDistributionService().execute(new Runnable(){

                public void run() {
                    try {
                        ChatRoom.this.notifyUserJoinedChatRoom(participant.getUsername());
                    }
                    catch (Exception e) {
                        log.error((Object)("Exception caught notifying chatroom that user [" + participant.getUsername() + "] joined"), (Throwable)e);
                    }
                }
            });
        }
        for (BotInstance bot : this.bots.values()) {
            bot.botServiceProxy.sendNotificationToBotsInChannel(this.botChannelID, participant.getUsername(), 1);
        }
        participant.addToUsersCurrentChatroomList();
        this.updateChatRoomDetailInDB();
        try {
            int migLevel = MemCacheOrEJB.getUserReputationLevel(userData.username, userData.userID);
            ChatroomEntrantSnapshot chatroomEntrantSnapshot = this.chatroomEntrantSnapshot;
            synchronized (chatroomEntrantSnapshot) {
                this.chatroomEntrantSnapshot.addEntrant(participant.getUsername(), migLevel, participant.getIPAddress());
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to check mig level: " + e));
        }
    }

    @Override
    public void removeParticipantOnException(ChatRoomParticipant participant) {
        try {
            this.removeParticipant(participant.getUsername());
        }
        catch (Exception e) {
            log.warn((Object)("Failed to remove participant [" + participant.getUsername() + "] from chat room [" + this.chatRoomData.getName() + "]"), (Throwable)e);
        }
    }

    public void removeParticipant(String username) throws FusionException {
        this.removeParticipant(username, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeParticipant(String username, boolean removeFromUsersChatRoomList) throws FusionException {
        Object participantName;
        ChatRoomParticipant participant = this.participants.remove(username);
        if (participant == null) {
            return;
        }
        Object object = this.kickUserVoteMonitor;
        synchronized (object) {
            if (this.kickUserVote != null) {
                this.kickUserVote.removeYesVote(username);
            }
        }
        if (!this.isStadium()) {
            this.recentlyLeftUsers.put(username, System.currentTimeMillis());
        }
        if (!participant.isHiddenAdmin() && (!this.isStadium() || participant.hasAdminOrModeratorRights()) && this.enterLeaveNotificationLimiter.hit()) {
            this.objectManager.onRoomSessionRemoved();
            if (!this.participants.isEmpty()) {
                if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.SILENCE_FAST_EXIT_MESSAGES)) {
                    if (participant.getTimeInRoomMillis() > (long)SystemProperty.getInt(SystemPropertyEntities.Chatroom.EXIT_SILENCE_TIME_IN_MS)) {
                        this.queueEntryExitAdminMessage(participant, false);
                    }
                } else {
                    this.queueEntryExitAdminMessage(participant, false);
                }
                participantName = participant.getUsername();
                this.objectManager.getDistributionService().execute(new Runnable((String)participantName){
                    final /* synthetic */ String val$participantName;
                    {
                        this.val$participantName = string;
                    }

                    public void run() {
                        try {
                            ChatRoom.this.notifyUserLeftChatRoom(this.val$participantName);
                        }
                        catch (Exception e) {
                            log.error((Object)("Exception caught notifying chatroom [" + ChatRoom.this.chatRoomData.getName() + "] that user [" + this.val$participantName + "] left"), (Throwable)e);
                        }
                    }
                });
            }
        }
        if (participant.hasAdminOrModeratorRights()) {
            participantName = this.chatRoomData;
            synchronized (participantName) {
                if (this.chatRoomData.isLocked() && username.equals(this.chatRoomData.getLocker())) {
                    this.chatRoomData.unlock();
                    this.queueAdminMessage("This chat room has been unlocked because [" + participant.getUsername() + "] the user who locked it has left", null, null);
                }
            }
        }
        if (this.getNumParticipants() == 0) {
            participantName = this.chatRoomData;
            synchronized (participantName) {
                if (this.chatRoomData.isAnnouncementOn()) {
                    this.turnAnnouncementOff(participant, false);
                }
            }
        }
        for (BotInstance bot : this.bots.values()) {
            bot.botServiceProxy.sendNotificationToBotsInChannel(this.botChannelID, username, BotData.BotCommandEnum.QUIT.value());
        }
        if (removeFromUsersChatRoomList) {
            participant.removeFromUsersCurrentChatroomList();
        }
    }

    private void notifyUserJoinedChatRoom(String username) throws FusionException {
        boolean isBanned = this.chatRoomData.isOnBannedList(username);
        this.participants.notifyUserJoinedChatRoom(isBanned, username);
    }

    private void notifyUserLeftChatRoom(String username) throws FusionException {
        this.participants.notifyUserLeftChatRoom(username);
    }

    public boolean isAdministrator(String requestingUsername) {
        return this.participants.isAdministrator(requestingUsername);
    }

    @Override
    public String[] getParticipants(String requestingUsername) {
        if (this.isStadium()) {
            return this.getAdministrators(requestingUsername);
        }
        return this.participants.getAllParticipantsExceptHiddenAdmins(requestingUsername);
    }

    @Override
    public String[] getAllParticipants(String requestingUsername) {
        return this.participants.getAllParticipants(requestingUsername);
    }

    public String[] getAdministrators(String requestingUsername) {
        return this.participants.getAdministrators(requestingUsername);
    }

    public int getNumParticipants() {
        return Math.min(this.participants.size() + this.numFakeParticipants, this.chatRoomData.getMaximumSize());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setNumberOfFakeParticipants(String username, int number) {
        ChatRoomParticipant participant = this.participants.get(username);
        if (participant != null && participant.hasAdminOrModeratorRights() && this.isStadium() && number > 0) {
            ChatRoomDataWrapper chatRoomDataWrapper = this.chatRoomData;
            synchronized (chatRoomDataWrapper) {
                this.numFakeParticipants = number;
            }
            this.queueAdminMessage("Reserved participants set to " + number, username, null);
        }
    }

    @Override
    public boolean isParticipant(String username) throws FusionException {
        return this.participants.isParticipant(username);
    }

    @Override
    public boolean isVisibleParticipant(String username) throws FusionException {
        ChatRoomParticipant p = this.participants.get(username);
        return p != null && !p.isHiddenAdmin() && (!this.isStadium() || p.hasAdminOrModeratorRights());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void listParticipants(String requestingUsername, int size, int startIndex) throws FusionException {
        ChatRoomParticipant requestor = this.participants.verifyYouAreParticipant(requestingUsername);
        if (!requestor.hasAdminOrModeratorRights()) {
            throw new FusionException("Only admins and moderators may use the list command");
        }
        ChatroomEntrantSnapshot chatroomEntrantSnapshot = this.chatroomEntrantSnapshot;
        synchronized (chatroomEntrantSnapshot) {
            if (this.chatroomEntrantSnapshot.isCurrentSnapshotRunning() && this.chatroomEntrantSnapshot.hasLockExpired()) {
                this.chatRoomData.unlock();
                this.chatroomEntrantSnapshot.clearSnapshot();
            }
            if (!this.chatroomEntrantSnapshot.isCurrentSnapshotRunning()) {
                this.lock(requestingUsername);
                this.queueAdminMessage("This chat room has been locked for " + this.listLockPeriod + " seconds.", null, null);
                this.silence(requestingUsername, this.defaultRoomSilencePeriod);
                this.chatroomEntrantSnapshot.initLockExpiry();
            }
            String entrantsList = this.chatroomEntrantSnapshot.getEntrantListStr(size, startIndex);
            this.queueAdminMessage(entrantsList, requestingUsername, null);
        }
    }

    @Override
    public int getMaximumMessageLength(String sender) {
        ChatRoomParticipant senderParticipant = this.participants.get(sender);
        return this.getMaximumMessageLength(senderParticipant);
    }

    @Override
    public ChatRoomData getNewRoomData() {
        return this.chatRoomData.getNewChatRoomData();
    }

    private int getMaximumMessageLength(ChatRoomParticipant sender) {
        return sender != null && this.isStadium() && sender.hasAdminOrModeratorRights() ? this.maxStatdiumAdminMessageLength : this.maxMessageLength;
    }

    @Override
    public void putMessage(final MessageDataIce message, String sessionID) throws FusionException {
        ChatRoomParticipant senderParticipant;
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_CHECK_SIZE_BEFORE_PUTMESSAGE_ENABLED)) {
            int maxQueueSize = SystemProperty.getInt(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_VIA_LIST_MAX_SIZE);
            int currentQueueSize = this.messageQueue.size();
            if (currentQueueSize > maxQueueSize) {
                log.warn((Object)("ChatroomMaxDispatchQueueSize [" + maxQueueSize + "] exceeded. CurrentSize[" + currentQueueSize + "]. User[" + message.source + "] Chatroom[" + this.chatRoomData.getName() + "]"));
                throw new FusionException("Unable to send chat messages now. Please try again later.");
            }
        }
        if ((senderParticipant = this.participants.get(message.source)) == null) {
            throw new FusionException("You are not in the " + this.chatRoomData.getName() + " chat room");
        }
        if (!senderParticipant.getSessionID().equals(sessionID)) {
            return;
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.DROP_MESSAGES_FROM_MUTED_USERS, true) && this.mutedUsers.containsKey(message.source)) {
            this.queueAdminMessage("You have been muted in this room and cannot send messages.", senderParticipant.getUsername(), null);
            return;
        }
        if (this.silenceExpiry >= System.currentTimeMillis() && !senderParticipant.hasAdminOrModeratorRights()) {
            this.queueAdminMessage("This room has been silenced at the moment, you cannot send messages.", senderParticipant.getUsername(), null);
            return;
        }
        Long userSilenceTimeout = this.silencedUsers.get(message.source);
        if (userSilenceTimeout != null) {
            if (userSilenceTimeout >= System.currentTimeMillis()) {
                this.queueAdminMessage("You cannot send messages while silenced.", message.source, null);
                return;
            }
            this.silencedUsers.remove(message.source);
        }
        int maxLength = this.getMaximumMessageLength(senderParticipant);
        if (message.messageText.length() > maxLength) {
            throw new FusionException("Your message is too long. Please keep messages under " + maxLength + " characters");
        }
        if (senderParticipant.isSpamming(message.messageText, this.maxMessageRepetitions, this.minSpamMessageLength)) {
            log.warn((Object)("Disconnect user [" + message.source + "] with session ID [" + senderParticipant.getSessionID() + "] for spamming message [" + message.messageText + "] in chat room [" + this.chatRoomData.getName() + "]"));
            senderParticipant.silentlyDropIncomingPackets();
            return;
        }
        message.messageDestinations[0].destination = this.chatRoomData.getName();
        Integer specialColor = senderParticipant.getMessageSourceColorOverride();
        if (specialColor != null) {
            message.sourceColour = specialColor;
        }
        if (senderParticipant.hasAdminOrModeratorRights() && !senderParticipant.isTopMerchant()) {
            message.fromAdministrator = 1;
        }
        message.messageDestinations[0].status = MessageDestinationData.StatusEnum.SENT.value();
        message.messageDestinations[0].dateDispatched = System.currentTimeMillis();
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.CHECK_USER_PRESENT_BEFORE_DISPATCH)) {
            final UserMessageToDispatchToRoom mtd = new UserMessageToDispatchToRoom(message, message.source);
            int messageDelay = SystemProperty.getInt(SystemPropertyEntities.Chatroom.DELAY_USER_MESSAGE_TIME);
            if (messageDelay > 0) {
                this.objectManager.getDistributionService().schedule(new Runnable(){

                    public void run() {
                        if (!ChatRoom.this.queueMessage(mtd, message.source, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS))) {
                            log.warn((Object)String.format("Unable to queue message from [%s] to chat room: [%s]" + message.source, ChatRoom.this.chatRoomData.getName()));
                        }
                    }
                }, (long)messageDelay, TimeUnit.MILLISECONDS);
            } else if (!this.queueMessage(mtd, message.source, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS))) {
                throw new FusionException("Unable to send chat messages now. Please try again later.");
            }
        } else if (!this.queueMessage(message, null, message.source, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS))) {
            throw new FusionException("Unable to send chat messages now. Please try again later.");
        }
        this.logMessage(senderParticipant, message.messageText);
        senderParticipant.updateLastTimeMessageSent();
        if (this.isStadium() && !senderParticipant.hasAdminOrModeratorRights()) {
            this.queueAdminMessage("Thank you for your message. It has been sent to moderators", senderParticipant.getUsername(), null);
        }
    }

    public void putSystemMessage(String messageText, String[] emoticonKeys) {
        this.putSystemMessageWithColour(messageText, emoticonKeys, -1);
    }

    public void putSystemMessageWithColour(String messageText, String[] emoticonKeys, int messageColour) {
        MessageData messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), messageText, emoticonKeys);
        messageData.contentType = MessageData.ContentTypeEnum.EMOTE;
        if (messageColour >= 0) {
            messageData.messageColour = messageColour;
        }
        this.queueMessage(messageData.toIceObject(), null, null, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN));
        this.logMessage(null, messageText);
    }

    public void addModerator(String username) {
        this.chatRoomData.addModerator(username);
    }

    public void removeModerator(String username) {
        this.chatRoomData.removeModerator(username);
    }

    public boolean isModerator(String username) {
        return this.chatRoomData.isModerator(username);
    }

    public Map<String, String> getTheme() {
        return this.chatRoomData.getTheme();
    }

    public void banUser(String username) {
        this.chatRoomData.addBannedUser(username);
        ChatRoomParticipant participant = this.participants.get(username);
        if (participant != null) {
            try {
                MessageData messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), "You have been banned from this chat room", null);
                participant.putMessage(messageData.toIceObject());
            }
            catch (Exception e) {
                // empty catch block
            }
            try {
                this.removeParticipant(username);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public void unbanUser(String username) {
        this.chatRoomData.removeBannedUser(username);
    }

    public void banGroupMembers(String[] bannedList, String bannedby, int reason) throws FusionException {
        GroupData groupData = this.chatRoomData.getGroupData();
        if (groupData == null) {
            throw new FusionException("/ban is not a valid command");
        }
        ChatRoomParticipant instigator = this.participants.get(bannedby);
        if (instigator == null) {
            throw new FusionException("You are no longer in the room " + this.chatRoomData.getName());
        }
        if (!instigator.isGroupAdmin() && !instigator.isGroupMod()) {
            throw new FusionException("You need to be a group admin or moderator to ban users");
        }
        String errorMsg = "";
        for (int i = 0; i < bannedList.length; ++i) {
            String banned = bannedList[i];
            ChatRoomParticipant target = this.participants.get(banned);
            if (target != null) {
                if (target.isGroupAdmin() || target.isGlobalAdmin()) {
                    errorMsg = errorMsg + "You cannot ban admin (" + banned + "). ";
                    continue;
                }
                if (target.isGroupMod() && !instigator.isGroupAdmin()) {
                    errorMsg = errorMsg + "You cannot ban a group moderator (" + banned + "). ";
                    continue;
                }
            }
            Message messageEJB = null;
            try {
                String strReason = Enums.GroupBanReasonEnum.getDescription(reason);
                messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                messageEJB.banGroupMember(instigator.getUsername(), groupData, banned);
                if (target != null) {
                    this.removeParticipant(banned);
                    StringBuffer message = new StringBuffer();
                    message.append("You have been banned from the group (and all the rooms in this group) ");
                    message.append(groupData.name);
                    message.append(" by ");
                    if (instigator.isGroupAdmin()) {
                        message.append(" group admin ");
                    } else {
                        message.append(" group moderator ");
                    }
                    message.append(this.formatUserNameWithLevel(instigator));
                    message.append(", reason: " + strReason);
                    MessageData messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), message.toString(), null);
                    target.putMessage(messageData.toIceObject());
                }
                this.queueAdminMessage(this.formatUserNameWithLevel(banned) + " has been banned by " + this.formatUserNameWithLevel(instigator.getUsername()) + ", reason: " + strReason, null, banned);
                continue;
            }
            catch (RemoteException re) {
                errorMsg = errorMsg + "Unable to ban user " + banned + ". ";
                continue;
            }
            catch (Exception e) {
                log.warn((Object)("Unable to perma ban " + banned + " from room: " + this.chatRoomData.getName() + ", reason: " + e.getMessage()));
                errorMsg = errorMsg + banned + " is either inactive/banned or not part of this group. ";
            }
        }
        if (errorMsg != null && errorMsg.length() != 0) {
            throw new FusionException(errorMsg);
        }
    }

    public void unbanGroupMember(String unbanned, String unbannedby, int reason) throws FusionException {
        GroupData groupData = this.chatRoomData.getGroupData();
        if (groupData == null) {
            throw new FusionException("/unban is not a valid command");
        }
        ChatRoomParticipant instigator = this.participants.verifyYouAreParticipant(unbannedby);
        if (!instigator.isGroupAdmin() && !instigator.isGroupMod()) {
            throw new FusionException("You need to be a group admin or moderator to unban users");
        }
        Message messageEJB = null;
        try {
            String strReason = Enums.GroupUnbanReasonEnum.getDescription(reason);
            messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            messageEJB.unbanGroupMember(instigator.getUsername(), groupData, unbanned);
            this.queueAdminMessage(this.formatUserNameWithLevel(unbanned) + " has been unbanned by " + this.formatUserNameWithLevel(instigator) + ", reason: " + strReason, null, null);
        }
        catch (Exception e) {
            log.warn((Object)("Unable to unban " + unbanned + " from room: " + this.chatRoomData.getName() + ", reason: " + e.getMessage()));
            throw new FusionException(unbanned + " is either active or not a member of this group");
        }
    }

    public void banIndexes(int[] indexes, String bannedBy, int reason) throws FusionException {
        if (!this.isGroupLinkedChatroom()) {
            throw new FusionException("/banindex is not a valid command");
        }
        if (!this.chatroomEntrantSnapshot.isCurrentSnapshotRunning() || this.chatroomEntrantSnapshot.hasLockExpired()) {
            throw new FusionException("/banindex can only be used after the list command");
        }
        String[] bannedUsernames = this.chatroomEntrantSnapshot.getSnapshotUsernamesFromIndexes(indexes);
        this.banGroupMembers(bannedUsernames, bannedBy, reason);
    }

    public void banMultiIds(String requestingUsername) throws FusionException {
        ChatRoomParticipant requestor = this.participants.verifyYouAreParticipant(requestingUsername);
        if (!requestor.hasAdminOrModeratorRights()) {
            throw new FusionException("Only admins and moderators may use the kill command");
        }
        Set<String> banList = MultiIdFinder.getMultiIds(this.getIpToUserMap(), this.chatRoomData.getName());
        String banMsg = "";
        if (banList.isEmpty()) {
            banMsg = "No users have met the criteria for banning";
        } else {
            banMsg = "The following users have been banned from this chatroom:\n";
            for (String username : banList) {
                if (username.equals(requestingUsername)) continue;
                try {
                    Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                    if (!messageEJB.updateChatroomBannedList(this.chatRoomData.getName(), username)) continue;
                    this.banUser(username);
                    banMsg = banMsg + username + "\n";
                }
                catch (Exception e) {
                    log.warn((Object)("Unable to ban User [" + username + "] from Room [" + this.chatRoomData.getName() + "]"), (Throwable)e);
                }
            }
        }
        this.queueAdminMessage(banMsg, requestingUsername, null);
    }

    private HashMap<String, List<String>> getIpToUserMap() {
        return this.participants.getIpToUserMap();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void kickIndexes(int[] indexes, String kickedBy) throws FusionException {
        if (!this.chatroomEntrantSnapshot.isCurrentSnapshotRunning() || this.chatroomEntrantSnapshot.hasLockExpired()) {
            throw new FusionException("/kickindex can only be used after the list command");
        }
        ChatRoomParticipant voterParticipant = this.participants.verifyYouAreParticipant(kickedBy);
        if (!this.chatRoomData.isAllowKicking() && !voterParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("Only admins and moderators may kick in this room");
        }
        String[] kickUsernames = this.chatroomEntrantSnapshot.getSnapshotUsernamesFromIndexes(indexes);
        String errorMsg = "";
        for (int i = 0; i < kickUsernames.length; ++i) {
            String target = kickUsernames[i];
            ChatRoomParticipant targetParticipant = this.participants.get(target);
            if (targetParticipant == null) {
                errorMsg = errorMsg + target + " is no longer in the chat room " + this.chatRoomData.getName() + ". ";
                continue;
            }
            if (targetParticipant.hasAdminOrModeratorRights()) {
                errorMsg = errorMsg + "User " + target + " is an admin or moderator and cannot be kicked. ";
                continue;
            }
            Object object = this.adminKickMonitor;
            synchronized (object) {
                if (this.participants.get(target) == null) {
                    errorMsg = errorMsg + target + " is no longer in the chat room " + this.chatRoomData.getName() + ". ";
                    continue;
                }
                this.kickParticipant(voterParticipant, targetParticipant, null);
                continue;
            }
        }
        if (errorMsg != null && errorMsg.length() != 0) {
            throw new FusionException(errorMsg);
        }
    }

    public void inviteUserToGroup(String invitee, String inviter) throws FusionException {
        GroupData groupData = this.chatRoomData.getGroupData();
        if (groupData == null) {
            throw new FusionException("/invite is valid only in group chat rooms");
        }
        ChatRoomParticipant inviterParticipant = this.participants.verifyYouAreParticipant(inviter);
        if (!(groupData.type != GroupData.TypeEnum.CLOSED && groupData.type != GroupData.TypeEnum.UNLISTED || inviterParticipant.isGroupAdmin() || inviterParticipant.isGroupMod())) {
            throw new FusionException("You need to be a group admin or moderator to invite users to a group");
        }
        try {
            Web webBean = (Web)EJBHomeCache.getObject("ejb/Web", WebHome.class);
            String result = webBean.inviteUserToGroup(inviter, invitee, groupData.id);
            if (!result.equals("TRUE")) {
                throw new FusionException(ExceptionHelper.removeErrorMessagePrefix(result));
            }
            this.queueAdminMessage("An invite has been sent to " + invitee + " to join group " + groupData.name, inviter, null);
        }
        catch (FusionException e2) {
            throw new FusionException(e2.message);
        }
        catch (Exception e) {
            log.debug((Object)"Unable to invite user to group: ", (Throwable)e);
            throw new FusionException("Unable to invite " + invitee + " to group " + groupData.name);
        }
    }

    public void broadcastMessage(String instigator, String message) throws FusionException {
        GroupData groupData = this.chatRoomData.getGroupData();
        if (groupData == null) {
            throw new FusionException("/broadcast is valid only in group chat rooms");
        }
        ChatRoomParticipant instigatorParticipant = this.participants.verifyYouAreParticipant(instigator);
        if (!instigatorParticipant.isGroupAdmin() && !instigatorParticipant.isGroupMod()) {
            throw new FusionException("You need to be a group admin or moderator to broadcast to a group");
        }
        String broadcastMsg = String.format("%s (%s) BROADCAST: %s", groupData.name, instigator, message);
        try {
            String[] chatrooms = null;
            if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHATROOM_DAO)) {
                try {
                    chatrooms = DAOFactory.getInstance().getChatRoomDAO().getGroupChatRooms(groupData.id);
                }
                catch (DAOException e) {
                    log.warn((Object)String.format("DAO: Failed to get GroupChatRooms for groupid:%s", groupData.id), (Throwable)e);
                }
            } else {
                Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                chatrooms = messageEJB.getGroupChatRooms(groupData.id);
            }
            ChatRoomPrx[] chatRoomProxies = null;
            if (chatrooms != null && chatrooms.length > 0) {
                chatRoomProxies = this.objectManager.getRegistryPrx().findChatRoomObjects(chatrooms);
            }
            if (chatRoomProxies != null) {
                for (ChatRoomPrx chatRoomPrx : chatRoomProxies) {
                    if (chatRoomPrx == null) continue;
                    chatRoomPrx.putSystemMessageWithColour(broadcastMsg, null, SystemProperty.getInt("ChatroomBroadcastMessageColor", 0x770000));
                }
            }
        }
        catch (LocalException e) {
            throw new EJBException("Failed to broadcast message to chat rooms: " + e.getMessage());
        }
        catch (Exception e) {
            throw new FusionException("Unable to broadcast message [" + message + "] to group " + groupData.name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void voteToKickUser(String voter, String target) throws FusionException {
        ChatRoomParticipant voterParticipant = this.participants.verifyYouAreParticipant(voter);
        if (!this.chatRoomData.isAllowKicking() && !voterParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("Only admins and moderators may kick in this room");
        }
        if (voterParticipant.hasAdminOrModeratorRights()) {
            if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.PT67727788_USE_LOCKLESS_ADMIN_KICK)) {
                if (target.contains("#")) {
                    if (!MULTI_KICK_PATTERN_CHECK.matcher(target).find()) {
                        throw new FusionException("target with # must contain at least a letter, number, period (.), hyphens (-), or underscore (_)");
                    }
                    Pattern p = Pattern.compile(target.replaceAll("#", "[\\.A-Za-z0-9_-]+"));
                    boolean found = false;
                    for (String participantName : this.participants.getAllNames()) {
                        ChatRoomParticipant participant;
                        Matcher m = p.matcher(participantName);
                        if (!m.matches() || (participant = this.participants.get(participantName)) == null) continue;
                        if (participant.hasAdminOrModeratorRights()) {
                            throw new FusionException("User is an admin or moderator and cannot be kicked");
                        }
                        this.kickParticipant(voterParticipant, participant, null);
                        found = true;
                    }
                    if (!found) {
                        throw new FusionException("there is no participants match for " + target);
                    }
                } else {
                    ChatRoomParticipant targetParticipant = this.participants.verifyIsParticipant(target);
                    if (targetParticipant.hasAdminOrModeratorRights()) {
                        throw new FusionException("User is an admin or moderator and cannot be kicked");
                    }
                    this.kickParticipant(voterParticipant, targetParticipant, null);
                }
                return;
            }
            Object targetParticipant = this.adminKickMonitor;
            synchronized (targetParticipant) {
                if (target.contains("#")) {
                    if (!Pattern.compile("[\\.A-Za-z0-9_-]{1}").matcher(target).find()) {
                        throw new FusionException("target with # must contain at least a letter, number, period (.), hyphens (-), or underscore (_)");
                    }
                    Pattern p = Pattern.compile(target.replaceAll("#", "[\\.A-Za-z0-9_-]+"));
                    boolean found = false;
                    for (String participantName : this.participants.getAllNames()) {
                        Matcher m = p.matcher(participantName);
                        if (!m.matches()) continue;
                        ChatRoomParticipant participant = this.participants.get(participantName);
                        if (participant.hasAdminOrModeratorRights()) {
                            throw new FusionException("User is an admin or moderator and cannot be kicked");
                        }
                        this.kickParticipant(voterParticipant, participant, null);
                        found = true;
                    }
                    if (!found) {
                        throw new FusionException("there is no participants match for " + target);
                    }
                } else {
                    ChatRoomParticipant targetParticipant2 = this.participants.verifyIsParticipant(target);
                    if (targetParticipant2.hasAdminOrModeratorRights()) {
                        throw new FusionException("User is an admin or moderator and cannot be kicked");
                    }
                    this.kickParticipant(voterParticipant, targetParticipant2, null);
                }
                return;
            }
        }
        int disableVoteKickTimeInMs = SystemProperty.getInt(SystemPropertyEntities.Chatroom.DISABLE_VOTE_KICK_TIME_IN_SECONDS) * 1000;
        if (voterParticipant.getTimeInRoomMillis() < (long)disableVoteKickTimeInMs) {
            throw new FusionException("You must be in a room longer before you can vote kick other users");
        }
        ChatRoomParticipant targetParticipant = this.participants.verifyIsParticipant(target);
        if (targetParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("User is an admin or moderator and cannot be kicked");
        }
        boolean enableOverrideExpiredKickUserVote = SystemProperty.getBool(SystemPropertyEntities.Chatroom.ENABLE_OVERRIDE_EXPIRED_KICK_USER_VOTE);
        Object object = this.kickUserVoteMonitor;
        synchronized (object) {
            if (this.participants.get(target) == null) {
                throw new FusionException(target + " is no longer in the chat room " + this.chatRoomData.getName());
            }
            boolean hasKickUserVoteInProgress = false;
            if (enableOverrideExpiredKickUserVote) {
                hasKickUserVoteInProgress = this.kickUserVote != null && !this.kickUserVote.isVotingExpired();
            } else {
                boolean bl = hasKickUserVoteInProgress = this.kickUserVote != null;
            }
            if (hasKickUserVoteInProgress) {
                if (!target.equals(this.kickUserVote.getTarget())) {
                    throw new FusionException("A vote to kick " + this.kickUserVote.getTarget() + " is currently in progress");
                }
                this.kickUserVote.addYesVote(voterParticipant);
            } else {
                boolean detectedExpiredVoting;
                boolean bl = detectedExpiredVoting = this.kickUserVote != null && this.kickUserVote.isVotingExpired();
                if (detectedExpiredVoting) {
                    this.logWarn("Detected a vote to kick (" + this.kickUserVote.toString() + ") that has been running too long and has not completed.");
                }
                if (System.currentTimeMillis() - voterParticipant.getLastTimeKickVoteInitiated() < (long)this.timeBetweenKickVotes) {
                    throw new FusionException("You recently started a kick vote. Please wait a short while before starting another one");
                }
                if (System.currentTimeMillis() - targetParticipant.getLastTimeTargetOfKickVote() < (long)this.timeBetweenKickingSameUser) {
                    throw new FusionException("A vote to kick " + target + " recently failed. Please wait a short while before trying to kick " + target + " again");
                }
                try {
                    double cost = SystemProperty.getDouble("ChatRoomKickCost", 0.0);
                    if (cost > 0.0) {
                        AccountBalanceData balanceData = null;
                        Account accountEJB = null;
                        if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
                            balanceData = new UserObject(voter).getAccountBalance();
                        } else {
                            accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
                            balanceData = accountEJB.getAccountBalance(voter);
                        }
                        double balance = balanceData.getBaseBalance();
                        if (balance < cost) {
                            throw new FusionException("You do not have enough credit to start a kick. Please recharge your account");
                        }
                        AccountEntrySourceData accountEntrySourceData = new AccountEntrySourceData(voterParticipant.getIPAddress(), voterParticipant.getSessionID(), voterParticipant.getMobileDevice(), voterParticipant.getUserAgent());
                        if (accountEJB == null) {
                            accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
                        }
                        accountEJB.chargeUserForChatRoomKick(voter, target, "Started vote to kick " + target + " from a chatroom", cost, accountEntrySourceData);
                    }
                }
                catch (CreateException e) {
                    throw new FusionException("Unable to create EJB to charge for kick");
                }
                catch (RemoteException e) {
                    throw new FusionException(RMIExceptionHelper.getRootMessage(e));
                }
                catch (DAOException e) {
                    log.error((Object)String.format("DAO: Failed to get Account Balance Data for user:%s", voter), (Throwable)e);
                    throw new FusionException(String.format("Failed to get Account Balance Data for user:%s", voter));
                }
                voterParticipant.setLastTimeKickVoteInitiated(System.currentTimeMillis());
                targetParticipant.setLastTimeTargetOfKickVote(System.currentTimeMillis());
                try {
                    this.kickUserVote = new KickUserVote(voterParticipant, this.chatRoomData.getName(), target);
                }
                catch (Throwable t) {
                    this.logError("Failed to start vote kick. Voter:[" + voter + "].Target:[" + target + "]", t);
                    throw new FusionException("Failed to start vote kick");
                }
            }
        }
    }

    public void clearUserKick(String instigator, String target) throws FusionException {
        ChatRoomParticipant instigatorParticipant = this.participants.verifyYouAreParticipant(instigator);
        if (!instigatorParticipant.isGlobalAdmin()) {
            throw new FusionException("Only global admins may clear kicks in this room");
        }
        instigatorParticipant.verifyClientMeetsMinVersion("/kick");
        BannedInfo bannedInfo = this.bannedUsers.get(target);
        if (bannedInfo == null) {
            throw new FusionException("User " + target + " was never kicked from the chat room " + this.chatRoomData.getName());
        }
        if (bannedInfo.getInstigator() != null && !bannedInfo.getInstigator().equals(instigator)) {
            throw new FusionException("Only the admin who kicked the user is allowed to clear the kick");
        }
        if (bannedInfo.getReason() != BannedInfo.ReasonEnum.KICK) {
            throw new FusionException("You can only clear the kick on a kicked user");
        }
        if (this.bannedUsers.remove(target) == null) {
            throw new FusionException("User " + target + " was never kicked from the chat room " + this.chatRoomData.getName());
        }
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            userEJB.decrementChatroomBanCounter(target);
        }
        catch (Exception e) {
            log.warn((Object)"Unable to log chat room ban", (Throwable)e);
        }
        this.queueAdminMessage(instigator + " has cleared the last kick made on " + target, null, null);
    }

    private String formatLogMessage(String stmt) {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("Room:");
        if (this.chatRoomData != null) {
            msgBuilder.append("[");
            msgBuilder.append(this.chatRoomData.getName());
            msgBuilder.append("]");
        } else {
            msgBuilder.append("null");
        }
        msgBuilder.append(";");
        msgBuilder.append(stmt);
        return msgBuilder.toString();
    }

    private void logWarn(String msg) {
        log.warn((Object)this.formatLogMessage(msg));
    }

    private void logError(String msg, Throwable t) {
        log.error((Object)this.formatLogMessage(msg), t);
    }

    @Override
    public void startBot(String username, String botName) throws FusionException {
        boolean semaphoreAcquired = false;
        try {
            try {
                ChatRoomParticipant participant = this.participants.get(username);
                boolean startByChatRoom = this.chatRoomData.getName().equals(username);
                if (!startByChatRoom && this.participants == null) {
                    throw new FusionException("You are no longer in the room");
                }
                this.chatRoomData.verifyBotsAllowed();
                if (participant != null && participant.hasAdminOrModeratorRights()) {
                    this.blockBotsUntilTimestamp = 0L;
                } else if (System.currentTimeMillis() < this.blockBotsUntilTimestamp) {
                    if (this.blockBotsUntilTimestamp == Long.MAX_VALUE) {
                        throw new FusionException("Bots have been blocked in this room.");
                    }
                    throw new FusionException("Bots have been temporarily blocked. Please wait " + (this.blockBotsUntilTimestamp - System.currentTimeMillis()) / 1000L + "s");
                }
                semaphoreAcquired = this.botSemaphore.tryAcquire();
                if (!semaphoreAcquired) {
                    throw new FusionException("Another user is starting or stopping a bot. Please try again later");
                }
                Iterator<BotInstance> i$ = this.bots.values().iterator();
                if (i$.hasNext()) {
                    BotInstance botInstance = i$.next();
                    throw new FusionException("A bot of type [" + botInstance.displayName + "] is already running");
                }
                this.chatRoomData.verifySpecificBotOnly(botName);
                boolean purgeIfIdle = !startByChatRoom;
                ChatRoomPrx chatRoomPrx = this.objectManager.findChatRoomPrx(this.chatRoomData.getName());
                this.botChannelID = chatRoomPrx.ice_getIdentity().name;
                BotServicePrx botServicePrx = this.objectManager.getRegistryPrx().getLowestLoadedBotService();
                BotInstance newBotInstance = botServicePrx.addBotToChannel(chatRoomPrx, botName, username, purgeIfIdle);
                this.bots.put(newBotInstance.id, newBotInstance);
            }
            catch (CreateException e) {
                throw new FusionException("migGames are temporarily unavailable. Please try again later");
            }
            catch (RemoteException e) {
                throw new FusionException("migGames are temporarily unavailable. Please try again later");
            }
            catch (ObjectNotFoundException e) {
                throw new FusionException("migGames are temporarily unavailable. Please try again later");
            }
            Object var11_15 = null;
            if (semaphoreAcquired) {
                this.botSemaphore.release();
            }
        }
        catch (Throwable throwable) {
            Object var11_16 = null;
            if (semaphoreAcquired) {
                this.botSemaphore.release();
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stopBot(String username, String botName) throws FusionException {
        boolean semaphoreAcquired = false;
        try {
            ChatRoomParticipant participant = this.participants.verifyYouAreParticipant(username);
            if (this.bots.size() == 0) {
                throw new FusionException("There is currently no bot running in this room");
            }
            if (this.chatRoomData.botID() != null) {
                throw new FusionException("You do not have permission to stop the bot at this time");
            }
            semaphoreAcquired = this.botSemaphore.tryAcquire();
            if (!semaphoreAcquired) {
                throw new FusionException("Another user is starting or stopping a bot. Please try again later");
            }
            int botsRemoved = 0;
            for (BotInstance botInstance : this.bots.values()) {
                if (!participant.hasAdminOrModeratorRights() && !botInstance.startedBy.equals(username) && this.participants.get(botInstance.startedBy) != null) continue;
                botInstance.botServiceProxy.removeBot(botInstance.id, false);
                this.bots.remove(botInstance.id);
                this.queueAdminMessage("Bot '" + botInstance.displayName + "' has been stopped by " + username, null, null);
                ++botsRemoved;
            }
            if (botsRemoved == 0) {
                throw new FusionException("You do not have permission to stop the bot at this time");
            }
            Object var9_8 = null;
            if (semaphoreAcquired) {
                this.botSemaphore.release();
            }
        }
        catch (Throwable throwable) {
            Object var9_9 = null;
            if (semaphoreAcquired) {
                this.botSemaphore.release();
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stopAllBots(String username, int timeout) throws FusionException {
        boolean semaphoreAcquired = false;
        try {
            ChatRoomParticipant participant = this.participants.verifyYouAreParticipant(username);
            if (!participant.hasAdminOrModeratorRights()) {
                throw new FusionException("/botstop ! can only be done by admins and moderators");
            }
            semaphoreAcquired = this.botSemaphore.tryAcquire();
            if (!semaphoreAcquired) {
                throw new FusionException("Another user is starting or stopping a bot. Please try again later");
            }
            int botsRemoved = 0;
            for (BotInstance botInstance : this.bots.values()) {
                botInstance.botServiceProxy.removeBot(botInstance.id, true);
                this.bots.remove(botInstance.id);
                this.queueAdminMessage("Bot '" + botInstance.displayName + "' has been stopped by " + username, null, null);
                ++botsRemoved;
            }
            if (timeout > 0) {
                this.blockBotsUntilTimestamp = System.currentTimeMillis() + (long)(timeout * 1000);
                this.queueAdminMessage("Bots may not be started for " + timeout + "s", null, null);
            } else {
                this.blockBotsUntilTimestamp = Long.MAX_VALUE;
                this.queueAdminMessage("Bots may not be started except by owners and moderators", null, null);
            }
            Object var9_8 = null;
            if (semaphoreAcquired) {
                this.botSemaphore.release();
            }
        }
        catch (Throwable throwable) {
            Object var9_9 = null;
            if (semaphoreAcquired) {
                this.botSemaphore.release();
            }
            throw throwable;
        }
    }

    public void botKilled(String botInstanceID) throws FusionException {
        BotInstance botInstance = this.bots.remove(botInstanceID);
        if (botInstance != null) {
            this.queueAdminMessage("Bot '" + botInstance.displayName + "' was stopped for being idle too long.", null, null);
        }
    }

    public void sendMessageToBots(String username, String message, long receivedTimestamp) throws FusionException {
        for (BotInstance botInstance : this.bots.values()) {
            botInstance.botServiceProxy.sendMessageToBot(botInstance.id, username, message, receivedTimestamp);
        }
        this.participants.updateLastTimeMessageSent(username);
    }

    public void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        ChatRoomParticipant participant = this.participants.get(username);
        if (participant != null) {
            MessageData messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), "[PVT] " + message, emoticonHotKeys);
            BotInstance botInstance = this.bots.get(botInstanceID);
            if (botInstance != null) {
                messageData.source = botInstance.displayName;
                messageData.sourceColour = MessageData.SourceTypeEnum.BOT.colorHex();
                messageData.messageColour = 34734;
            }
            MessageToDispatchTypeEnum type = MessageToDispatchTypeEnum.TEXT;
            if (displayPopUp) {
                type = MessageToDispatchTypeEnum.TEXT_AND_POPUP;
            }
            this.queueMessage(messageData.toIceObject(), username, null, type, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN));
        } else {
            for (BotInstance bot : this.bots.values()) {
                bot.botServiceProxy.sendNotificationToBotsInChannel(this.botChannelID, username, BotData.BotCommandEnum.PART.value());
            }
        }
    }

    public void putBotMessageToUsers(String botInstanceID, String[] usernames, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        for (String username : usernames) {
            try {
                this.putBotMessage(botInstanceID, username, message, emoticonHotKeys, displayPopUp);
            }
            catch (FusionException e) {
                // empty catch block
            }
        }
    }

    public void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        MessageData messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), message, emoticonHotKeys);
        BotInstance botInstance = this.bots.get(botInstanceID);
        if (botInstance != null) {
            messageData.source = botInstance.displayName;
            messageData.sourceColour = MessageData.SourceTypeEnum.BOT.colorHex();
            messageData.messageColour = 34734;
        }
        MessageToDispatchTypeEnum type = MessageToDispatchTypeEnum.TEXT;
        if (displayPopUp) {
            type = MessageToDispatchTypeEnum.TEXT_AND_POPUP;
        }
        this.queueMessage(messageData.toIceObject(), null, null, type, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN));
        this.logMessage(null, message);
    }

    @Override
    public void sendGamesHelpToUser(String username) throws FusionException {
        if (!this.chatRoomData.isAllowBots()) {
            throw new FusionException("No games in this room.");
        }
        ChatRoomParticipant participant = this.participants.verifyYouAreParticipant(username);
        for (BotData botData : BotChannelHelper.getGames()) {
            MessageData messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), "To start " + botData.getGame() + ", type: /bot " + botData.getCommandName(), null);
            messageData.contentType = MessageData.ContentTypeEnum.EMOTE;
            this.queueMessage(messageData.toIceObject(), username, null, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN));
        }
        MessageData messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), "For help, see: migWorld", null);
        messageData.contentType = MessageData.ContentTypeEnum.EMOTE;
        this.queueMessage(messageData.toIceObject(), username, null, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN));
    }

    private void removeIdleParticipants() {
        for (ChatRoomParticipant participant : this.participants.getAll()) {
            if (!participant.isIdle(this.userIdleTimeout, this.maxUserDuration)) continue;
            try {
                this.removeParticipant(participant.getUsername());
            }
            catch (Exception e) {
                log.error((Object)("Unable to remove idle user [" + participant.getUsername() + "] from chat room [" + this.chatRoomData.getName() + "]"), (Throwable)e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void screenUserForEntrance(UserData userData, ChatRoomParticipant participant) throws FusionException {
        GroupMemberData groupMemberData;
        GroupData groupData;
        boolean ipWhitelisted;
        String mcKeyBlockIP;
        block53: {
            if (this.purging) {
                throw new FusionException("You cannot enter the " + this.chatRoomData.getName() + " room at this time. Please try again later");
            }
            UserObject user = new UserObject(userData.username);
            if (!AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.ENTER_CHATROOM, userData)) {
                throw new FusionException("Please authenticate your migme account to access chat rooms. For more information, please email contact@mig.me");
            }
            mcKeyBlockIP = String.format("%d%s%s", this.chatRoomData.getID(), "/", participant.getIPAddress());
            ipWhitelisted = false;
            if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.MULTI_ID_CONTROL_ENABLED) && !participant.hasAdminOrModeratorRights()) {
                String[] ipWhitelist = SystemProperty.getArray(SystemPropertyEntities.Chatroom.MULTI_ID_CONTROL_IP_WHITELIST);
                if (ipWhitelist != null) {
                    for (String ip : ipWhitelist) {
                        if (!participant.getIPAddress().equalsIgnoreCase(ip)) continue;
                        ipWhitelisted = true;
                        break;
                    }
                }
                if (ipWhitelisted) {
                    log.info((Object)String.format("user %s from whitelisted IP %s allowed into chatroom %s", userData.username, participant.getIPAddress(), this.getRoomData().name));
                } else if (MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BLOCK_IP, mcKeyBlockIP) != null) {
                    log.info((Object)String.format("ip suspended to enter chatroom %s due to IP rate limit exceeded, ip %s, user %s, rate limit %s", this.getRoomData().name, participant.getIPAddress(), userData.username, this.chatRoomData.getRateLimitByIp()));
                    throw new FusionException("Too many users have entered the chat room from your IP. Please try again later");
                }
            }
            if (userData.chatRoomBans != null) {
                int maxAdminBansAllowed = SystemProperty.getInt(SystemPropertyEntities.Default.MAX_CHATROOM_BANS);
                if (userData.chatRoomBans >= maxAdminBansAllowed || MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BAN, userData.username) != null) {
                    throw new FusionException("You have been banned from chat rooms");
                }
                int bansBeforeSuspension = SystemProperty.getInt(SystemPropertyEntities.Default.CHATROOM_BANS_BEFORE_SUSPENSION);
                if (userData.chatRoomBans == bansBeforeSuspension && MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BAN, userData.username) != null) {
                    throw new FusionException("You have been banned from chat rooms for " + WebCommon.toNiceDuration(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BAN.getCacheTime()));
                }
            }
            groupData = this.chatRoomData.getGroupData();
            if (!participant.isGlobalAdmin()) {
                if (groupData == null && this.chatRoomData.isOnBannedList(userData.username)) {
                    throw new FusionException("You have been banned from the chat room " + this.chatRoomData.getName());
                }
                BannedInfo bannedInfo = this.bannedUsers.get(userData.username);
                if (bannedInfo != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime < bannedInfo.getExpiredTime()) {
                        if (bannedInfo.getReason() == BannedInfo.ReasonEnum.BUMP) {
                            double dblPeriod = (double)(bannedInfo.getExpiredTime() - currentTime) / 60000.0;
                            if (dblPeriod <= 1.0) {
                                dblPeriod = 1.0;
                            }
                            long period = (long)Math.ceil(dblPeriod);
                            throw new FusionException("Sorry you have been bumped out of the chat-room for violating the rules of this chat-room. Please try again after " + period + (period > 1L ? " minutes" : " minute") + ".");
                        }
                        throw new FusionException("You were recently " + bannedInfo.getReason().getPrevAction() + " from the " + this.chatRoomData.getName() + " chat room. You may not rejoin at this time");
                    }
                    this.bannedUsers.remove(userData.username);
                }
            }
            User userEJB = null;
            this.chatRoomData.verifyMigLevel(userData, participant.hasAdminOrModeratorRights());
            groupMemberData = null;
            if (groupData != null) {
                if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
                    try {
                        groupMemberData = user.getGroupMember(groupData.id);
                    }
                    catch (DAOException e) {
                        log.error((Object)String.format("DAO: Failed to get GroupMemberData for user:%s, group:%s", user, groupData.id));
                        throw new FusionException("Unable to verify group membership");
                    }
                }
                try {
                    if (userEJB == null) {
                        userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                    }
                    groupMemberData = userEJB.getGroupMember(userData.username, groupData.id);
                }
                catch (Exception e) {
                    throw new FusionException("Unable to verify group membership");
                }
                if (!(groupMemberData != null || groupData == null || groupData.type == GroupData.TypeEnum.OPEN || groupData.type != GroupData.TypeEnum.CLOSED && groupData.allowNonMembersToJoinRooms.booleanValue() || participant.isGlobalAdmin())) {
                    throw new FusionException("You must be a member of the " + groupData.name + " group to enter the " + this.chatRoomData.getName() + " chat room");
                }
                if (groupMemberData != null && groupMemberData.status == GroupMemberData.StatusEnum.BANNED) {
                    throw new FusionException("You have been banned from the group " + groupData.name);
                }
                if (MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.GROUP_SUSPENSION, groupData.id + "/" + participant.getUsername()) != null && !participant.isGlobalAdmin()) {
                    throw new FusionException("You are temporarily suspended from all the chat rooms in " + groupData.name + " group for " + WebCommon.toNiceDuration(this.grpAdminSuspendDuration));
                }
                if (groupMemberData == null) {
                    try {
                        if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
                            try {
                                if (user.isUserBlackListedInGroup(groupData.id)) {
                                    throw new FusionException("You have been blacklisted from the group " + groupData.name + ". Please contact the admin or a moderator of this group.");
                                }
                                break block53;
                            }
                            catch (DAOException e) {
                                log.error((Object)String.format("DAO: Failed to check isUserBlackListedInGroup for user:%s, group:%s", user, groupData.id), (Throwable)e);
                                throw new FusionException("You have been blacklisted from the group " + groupData.name + ". Please contact the admin or a moderator of this group.");
                            }
                        }
                        Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                        if (messageEJB.isUserBlackListedInGroup(userData.username, groupData.id)) {
                            throw new FusionException("You have been blacklisted from the group " + groupData.name + ". Please contact the admin or a moderator of this group.");
                        }
                    }
                    catch (CreateException e) {
                        log.error((Object)("Failed to check blacklist: " + (Object)((Object)e)));
                    }
                    catch (RemoteException e) {
                        log.error((Object)("Failed to check blacklist: " + e));
                    }
                }
            }
        }
        if (this.isStadium()) {
            participant.setGroupAdmin(groupMemberData != null && (groupMemberData.isAdministrator() || groupMemberData.isModerator()));
        } else if (groupData != null) {
            participant.setGroupAdmin(groupMemberData != null && groupMemberData.isAdministrator());
            participant.setGroupMod(groupMemberData != null && groupMemberData.isModerator());
        }
        if (!participant.hasAdminOrModeratorRights()) {
            ChatRoomDataWrapper e = this.chatRoomData;
            synchronized (e) {
                if (this.chatRoomData.isLocked()) {
                    if (this.chatroomEntrantSnapshot.isCurrentSnapshotRunning() && this.chatroomEntrantSnapshot.hasLockExpired()) {
                        this.chatRoomData.unlock();
                        this.chatroomEntrantSnapshot.clearSnapshot();
                    } else {
                        throw new FusionException("[" + this.chatRoomData.getLocker() + "] has locked the room.");
                    }
                }
            }
        }
        for (Map.Entry<String, Long> e : this.recentlyLeftUsers.entrySet()) {
            if (System.currentTimeMillis() - e.getValue() <= (long)this.reenterInterval) continue;
            this.recentlyLeftUsers.remove(e.getKey());
        }
        if (!participant.hasAdminOrModeratorRights() && this.recentlyLeftUsers.containsKey(userData.username)) {
            throw new FusionException("You have recently left the " + this.chatRoomData.getName() + " chat room. You may not rejoin at this time");
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.USE_IP_RATELIMIT) && SystemProperty.getBool(SystemPropertyEntities.Chatroom.MULTI_ID_CONTROL_ENABLED) && !participant.hasAdminOrModeratorRights() && !StringUtil.isBlank(this.chatRoomData.getRateLimitByIp()) && !ipWhitelisted) {
            try {
                String rateLimit = SystemProperty.get(SystemPropertyEntities.Chatroom.OVERRIDE_IP_RATELIMIT);
                if (StringUtil.isBlank(rateLimit)) {
                    rateLimit = this.chatRoomData.getRateLimitByIp();
                }
                MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.CHATROOM_ENTRY.toString(), mcKeyBlockIP, rateLimit);
            }
            catch (MemCachedRateLimiter.LimitExceeded e) {
                int secondsToBlock = this.chatRoomData.getSecondsToBlock();
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BLOCK_IP, mcKeyBlockIP, "1", secondsToBlock * 1000);
                log.info((Object)String.format("rate limit of ip for chatroom %s has been exceeded, blocking for %d seconds, ip %s, user %s, rate limit %s", this.chatRoomData.getName(), secondsToBlock, participant.getIPAddress(), userData.username, this.chatRoomData.getRateLimitByIp()));
                throw new FusionException("Too many users have entered the chat room from your IP. Please try again later");
            }
            catch (MemCachedRateLimiter.FormatError e) {
                log.error((Object)String.format("incorrect rate limit by ip '%s' for chatroom %s, error '%s', ignoring it...", this.chatRoomData.getRateLimitByIp(), this.chatRoomData.getName(), e.getMessage()));
            }
        }
    }

    private void sendGreetingMessagesAsync(ChatRoomParticipant participant, ChatRoomData chatRoomData) throws FusionException {
        MessageData announceMessageData;
        MimeDataJSONObject mimeData;
        MimeDataJSONObject mimeData2;
        if (chatRoomData.description != null && chatRoomData.description.length() > 0) {
            this.queueAdminMessage(chatRoomData.description, participant.getUsername(), null, MIMETYPE_DESCRIPTION, EMPTY_MIMETYPE_DATA);
        }
        if (chatRoomData.isUserOwned()) {
            String creatorMessageText = "This room is managed by " + chatRoomData.getCreator();
            mimeData2 = new MimeDataJSONObject();
            mimeData2.put("managedBy", chatRoomData.getCreator());
            this.queueAdminMessage(creatorMessageText, participant.getUsername(), null, MIMETYPE_MANAGED_BY, mimeData2.toString());
        }
        if (participant.hasAdminOrModeratorRights() && chatRoomData.isLocked()) {
            String lockedMessageText = "This room is currently locked by " + chatRoomData.getLocker();
            mimeData2 = new MimeDataJSONObject();
            mimeData2.put("lockedBy", chatRoomData.getLocker());
            this.queueAdminMessage(lockedMessageText, participant.getUsername(), null, MIMETYPE_LOCKED, mimeData2.toString());
        }
        if (chatRoomData.isStadium()) {
            if (!participant.hasAdminOrModeratorRights()) {
                String stadiumMessageText = "This room is read-only. Messages you write will only be seen by moderators of this room";
                this.queueAdminMessage(stadiumMessageText, participant.getUsername(), null, MIMETYPE_STADIUM, EMPTY_MIMETYPE_DATA);
            }
        } else {
            String participantsString = StringUtil.join(this.getParticipants(participant.getUsername()), ",");
            participantsString = participantsString.length() == 0 ? participant.getUsername() : participant.getUsername() + ", " + participantsString;
            String participantsMessageText = "Currently in the room: " + participantsString;
            mimeData = new MimeDataJSONObject();
            JSONArray participantArray = new JSONArray();
            for (String participantName : this.getParticipants(participant.getUsername())) {
                participantArray.put((Object)participantName);
            }
            mimeData.put("participants", participantArray);
            this.queueAdminMessage(participantsMessageText, participant.getUsername(), null, MIMETYPE_PARTICIPANTS, mimeData.toString());
        }
        if (chatRoomData.isAnnouncementOn()) {
            int messageColor = SystemProperty.getInt("ChatroomAnnouncementColor", 0x770000);
            announceMessageData = this.formatAnnounceMessage(chatRoomData.getAnnounceMessage(), messageColor);
            mimeData = new MimeDataJSONObject();
            mimeData.put("announcer", chatRoomData.getAnnouncer());
            mimeData.put("message", chatRoomData.getAnnounceMessage());
            announceMessageData.mimeType = MIMETYPE_ANNOUNCE;
            announceMessageData.mimeTypeData = mimeData.toString();
            this.queueMessage(announceMessageData.toIceObject(), participant.getUsername(), null, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN));
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Emote.HELP_COMMAND_INFO_MESSAGE_ENABLED)) {
            String helpCommandInfoMessage;
            int minClientVersion = SystemProperty.getInt(SystemPropertyEntities.Emote.NEW_TAB_CLIENT_VERSION_MIN);
            if (participant.getClientVersionIce() >= minClientVersion && !StringUtil.isBlank(helpCommandInfoMessage = SystemProperty.get(SystemPropertyEntities.Emote.HELP_COMMAND_INFO_MESSAGE))) {
                this.queueAdminMessage(helpCommandInfoMessage, participant.getUsername(), null, MIMETYPE_HELP, EMPTY_MIMETYPE_DATA);
            }
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.CHATROOM_WELCOME_MESSAGE_ENABLED)) {
            try {
                AlertMessageData amd = null;
                if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_MESSAGE_DAO)) {
                    amd = DAOFactory.getInstance().getMessageDAO().getLatestAlertMessage(participant.getClientVersionIce(), AlertMessageData.TypeEnum.CHAT_ROOM_WELCOME_MESSAGE, participant.getCountryID(), participant.getLastLoginDate(), null, participant.getDeviceTypeAsInt());
                } else {
                    User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                    amd = userEJB.getLatestAlertMessage(participant.getClientVersionIce(), AlertMessageData.TypeEnum.CHAT_ROOM_WELCOME_MESSAGE, participant.getCountryID(), participant.getLastLoginDate(), null, participant.getDeviceTypeAsInt());
                }
                if (amd != null) {
                    announceMessageData = new ChatRoomWelcomeMessageData(chatRoomData.name, amd.content);
                    ((ChatRoomWelcomeMessageData)announceMessageData).mimeType = MIMETYPE_WELCOME;
                    ((ChatRoomWelcomeMessageData)announceMessageData).mimeTypeData = EMPTY_MIMETYPE_DATA;
                    this.queueMessage(announceMessageData.toIceObject(), participant.getUsername(), null, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN));
                }
            }
            catch (Exception e) {
                log.error((Object)("Exception occured while getting chat welcome message." + e.getMessage()), (Throwable)e);
                throw new FusionException("Unexpected Exception:" + e.getMessage());
            }
        }
    }

    private void queueCheckedEntryExitAdminMessage(MessageData messageData, final String username, boolean isEntering) {
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.USE_ENTER_EXIT_MESSAGE_CHECKS)) {
            final MessageToDispatchToRoomExcludingUser mtd = isEntering ? new EnterMessageToDispatch(messageData.toIceObject(), username) : new ExitMessageToDispatch(messageData.toIceObject(), username);
            int enterExitDelay = SystemProperty.getInt(SystemPropertyEntities.Chatroom.ENTER_EXIT_MESSAGE_DELAY_IN_MS);
            if (enterExitDelay > 0) {
                this.objectManager.getDistributionService().schedule(new Runnable(){

                    public void run() {
                        ChatRoom.this.queueMessage(mtd, username, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN));
                    }
                }, (long)enterExitDelay, TimeUnit.MILLISECONDS);
            } else {
                this.queueMessage(mtd, username, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN));
            }
        } else {
            this.queueMessage(messageData.toIceObject(), null, username, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN));
        }
    }

    private void queueEntryExitAdminMessage(ChatRoomParticipant participant, boolean isEntering) throws FusionException {
        MessageData messageData;
        String suffix;
        String username = participant.getUsername();
        int userLevel = participant.getLevel();
        String usernameWithLevel = userLevel == 0 ? username : username + "[" + userLevel + "]";
        String string = suffix = isEntering ? " has entered" : " has left";
        if (Painter.isClean(username, participant.getUserID())) {
            messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), usernameWithLevel + suffix, null);
        } else {
            String emoticon = "(paintwars-paintemoticon)";
            String message = this.formatUserNameWithLevel(username) + " " + emoticon + suffix;
            String[] emoticonKey = new String[]{emoticon};
            messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), message, emoticonKey);
        }
        MimeDataJSONObject mimeData = new MimeDataJSONObject();
        mimeData.put("username", username);
        mimeData.put("level", userLevel);
        messageData.mimeType = isEntering ? MIMETYPE_PARTICPANT_ENTER : MIMETYPE_PARTICPANT_EXIT;
        messageData.mimeTypeData = mimeData.toString();
        this.queueCheckedEntryExitAdminMessage(messageData, username, isEntering);
    }

    private void queueAdminMessage(String message, String usernameToReceive, String usernameToExclude) {
        MessageData messageData;
        if (message != null && message.length() > 0 && !this.queueMessage((messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), message, null)).toIceObject(), usernameToReceive, usernameToExclude, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN))) {
            log.warn((Object)String.format("failed to queueAdminMessage to chatroom. CurrentSize[%d]. UserToRec[%s] UserToExcl[%s] Chatroom[%s]", this.messageQueue.size(), usernameToReceive, usernameToExclude, this.chatRoomData.getName()));
        }
    }

    private void queueAdminMessage(String message, String usernameToReceive, String usernameToExclude, String mimeType, String mimeData) {
        if (message != null && message.length() > 0) {
            MessageData messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), message, null);
            messageData.mimeType = mimeType;
            messageData.mimeTypeData = mimeData;
            if (!this.queueMessage(messageData.toIceObject(), usernameToReceive, usernameToExclude, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS_ADMIN))) {
                log.warn((Object)String.format("failed to queueAdminMessage to chatroom. CurrentSize[%d]. UserToRec[%s] UserToExcl[%s] Chatroom[%s]", this.messageQueue.size(), usernameToReceive, usernameToExclude, this.chatRoomData.getName()));
            }
        }
    }

    private boolean queueMessage(MessageToDispatch mtd, String username, long addTimeoutInMillis) {
        if (!this.messageQueue.offer(mtd, addTimeoutInMillis)) {
            log.warn((Object)("could not queue enter message to chatroom. CurrentSize[" + this.messageQueue.size() + "]. User[" + username + "] Chatroom[" + this.chatRoomData.getName() + "]"));
            return false;
        }
        if (this.messageSemaphore.tryAcquire()) {
            this.objectManager.getDistributionService().execute(this.messageDispatcher);
        }
        return true;
    }

    private boolean queueMessage(MessageDataIce message, String usernameToReceive, String usernameToExclude, MessageToDispatchTypeEnum type, long addTimeoutInMillis) {
        MessageToDispatch mtd = usernameToReceive != null ? new MessageToDispatchToSingleUser(message, type, usernameToReceive) : (usernameToExclude != null ? new MessageToDispatchToRoomExcludingUser(message, type, usernameToExclude) : new MessageToDispatchToRoom(message, type));
        if (!this.messageQueue.offer(mtd, addTimeoutInMillis)) {
            log.warn((Object)("could not queue message to chatroom. CurrentSize[" + this.messageQueue.size() + "]. User[" + message.source + "] Chatroom[" + this.chatRoomData.getName() + "]"));
            return false;
        }
        if (this.messageSemaphore.tryAcquire()) {
            this.objectManager.getDistributionService().execute(this.messageDispatcher);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void dispatchMessages() {
        long maxDispatchWindowDurationInMs = SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_DISPATCH_WINDOW_MAX_DURATION_IN_MILLIS);
        int maxMessagesPerDispatchWindow = SystemProperty.getInt(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_DISPATCH_WINDOW_MAX_MESSAGE);
        try {
            int messagesSentThisRound = 0;
            long roundStartTime = System.currentTimeMillis();
            MessageToDispatch msg = this.messageQueue.takeIfAvailable();
            while (msg != null) {
                msg.dispatch();
                this.lastTimeMessageSent = System.currentTimeMillis();
                long currentDuration = System.currentTimeMillis() - roundStartTime;
                if (currentDuration > maxDispatchWindowDurationInMs || ++messagesSentThisRound > maxMessagesPerDispatchWindow) {
                    log.warn((Object)("Dispatch window limit exceeded. chatroom[" + this.chatRoomData.getName() + "] maxDuration[" + maxDispatchWindowDurationInMs + "] maxMessages[" + maxMessagesPerDispatchWindow + "] curDuration[" + currentDuration + "] curMessageQueueSize[" + this.messageQueue.size() + "]"));
                    break;
                }
                msg = this.messageQueue.takeIfAvailable();
            }
            Object var11_7 = null;
            this.messageSemaphore.release();
            if (this.messageQueue.isEmpty() || !this.messageSemaphore.tryAcquire()) return;
        }
        catch (Throwable throwable) {
            Object var11_8 = null;
            this.messageSemaphore.release();
            if (this.messageQueue.isEmpty() || !this.messageSemaphore.tryAcquire()) throw throwable;
            this.objectManager.getDistributionService().execute(this.messageDispatcher);
            throw throwable;
        }
        this.objectManager.getDistributionService().execute(this.messageDispatcher);
    }

    private void kickParticipant(ChatRoomParticipant instigator, ChatRoomParticipant target, String reason) throws FusionException {
        String kickedByMessage = "";
        String implication = "";
        long duration = this.banDuration;
        if (instigator != null) {
            if (instigator.isGlobalAdmin()) {
                duration = this.globalAdminBanDuration;
                kickedByMessage = " by administrator " + this.formatUserNameWithLevel(instigator);
                implication = " You are suspended from this chatroom for " + WebCommon.toNiceDuration(duration);
                if (target.getNumOfPriorKicks() + 1 == 2 || target.getNumOfPriorKicks() + 1 == 5) {
                    duration = this.globalAdminBanDuration * 2;
                } else if (target.getNumOfPriorKicks() + 1 == SystemProperty.getInt(SystemPropertyEntities.Default.CHATROOM_BANS_BEFORE_SUSPENSION)) {
                    implication = " You have been suspended from all the chatrooms for " + WebCommon.toNiceDuration(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BAN.getCacheTime());
                } else if (target.getNumOfPriorKicks() + 1 >= SystemProperty.getInt(SystemPropertyEntities.Default.MAX_CHATROOM_BANS)) {
                    implication = " You are permanently banned from entering chatrooms.";
                }
                try {
                    User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                    userEJB.bannedFromChatRoom(this.chatRoomData.getName(), instigator.getUsername(), target.getUsername(), reason);
                }
                catch (Exception e) {
                    log.warn((Object)"Unable to log chat room ban", (Throwable)e);
                }
            } else if (instigator.isGroupAdmin()) {
                duration = this.grpAdminSuspendDuration;
                kickedByMessage = " by group administrator " + this.formatUserNameWithLevel(instigator);
                implication = " You have been suspended from all the chatrooms in this group for " + WebCommon.toNiceDuration(duration);
                this.suspendGroupMember(target.getUsername(), duration);
            } else if (instigator.isGroupMod()) {
                duration = this.grpModSuspendDuration;
                kickedByMessage = " by group moderator " + this.formatUserNameWithLevel(instigator);
                implication = " You have been suspended from all the chatrooms in this group for " + WebCommon.toNiceDuration(duration);
                this.suspendGroupMember(target.getUsername(), duration);
            } else if (instigator.isRoomOwner()) {
                duration = this.adminBanDuration;
                kickedByMessage = " by chatroom admin " + this.formatUserNameWithLevel(instigator);
                implication = " You have been suspended from this chatroom for " + WebCommon.toNiceDuration(duration);
                try {
                    Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                    messageEJB.banUserFromRoom(instigator.getUsername(), this.chatRoomData.getName(), target.getUsername());
                    implication = " You are permanently banned from entering this chatroom.";
                }
                catch (Exception e) {
                    log.warn((Object)("Unable to perma ban " + target.getUsername() + " from room: " + this.chatRoomData.getName()), (Throwable)e);
                }
            } else if (this.isModerator(instigator.getUsername())) {
                duration = this.adminBanDuration;
                kickedByMessage = " by chatroom moderator " + this.formatUserNameWithLevel(instigator);
                implication = " You have been suspended from this chatroom for " + WebCommon.toNiceDuration(duration);
            } else {
                throw new FusionException(instigator.getUsername() + " doesn't have admin/moderator rights to kick users");
            }
        }
        reason = reason == null || reason.length() == 0 ? "." : " (" + reason + ").";
        this.participants.remove(target.getUsername());
        this.bannedUsers.put(target.getUsername(), new BannedInfo(System.currentTimeMillis() + duration, BannedInfo.ReasonEnum.KICK, instigator == null ? null : instigator.getUsername()));
        for (Map.Entry<String, BannedInfo> e : this.bannedUsers.entrySet()) {
            BannedInfo bannedInfo = e.getValue();
            if (bannedInfo == null || System.currentTimeMillis() <= bannedInfo.getExpiredTime()) continue;
            this.bannedUsers.remove(e.getKey());
        }
        String targetUNameWithLevel = this.formatUserNameWithLevel(target);
        this.queueAdminMessage(targetUNameWithLevel + " has been kicked" + kickedByMessage + reason, null, null);
        ChatRoomParticipant source = instigator == null ? target : instigator;
        this.logMessage(source, "<" + target.getUsername() + " was kicked" + kickedByMessage + reason + ">");
        auditLog.info((Object)(target.getUsername() + " has been kicked" + kickedByMessage + reason + " from room [" + this.chatRoomData.getName() + "]. Prior recorded kicks: " + target.getNumOfPriorKicks()));
        MessageData messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), "You have been kicked" + kickedByMessage + reason + implication, null);
        target.putMessage(messageData.toIceObject());
    }

    private void suspendGroupMember(String username, long duration) {
        GroupData groupData = this.chatRoomData.getGroupData();
        try {
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.GROUP_SUSPENSION, groupData.id + "/" + username, "", duration);
        }
        catch (Exception e) {
            log.warn((Object)(username + " suspended from room [" + this.chatRoomData.getName() + "], but suspending from group [" + groupData.name + "] failed!"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void endKickUserVote() {
        Object object = this.kickUserVoteMonitor;
        synchronized (object) {
            this.kickUserVote = null;
        }
    }

    private void logMessage(ChatRoomParticipant participant, String message) {
        if (ObjectCache.logMessagesToFile) {
            try {
                this.objectManager.logMessage(MessageToLog.TypeEnum.CHATROOM, participant == null ? 0 : participant.getCountryID(), participant == null ? this.chatRoomData.getName() : participant.getUsername(), this.chatRoomData.getName(), this.participants.size(), message);
            }
            catch (LocalException e) {
                this.logWarn("Unable to log chat room message to the MessageLogger. Exception: " + e.toString());
            }
            catch (Exception e) {
                this.logError("Unable to log chat room message to the MessageLogger. Exception: " + e.toString(), e);
            }
        }
    }

    private void updateChatRoomDetailInDB() {
        if (!this.chatRoomData.updateDBAccessed(this.dbUpdateInterval)) {
            return;
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            messageEJB.chatRoomAccessed(this.chatRoomData.getID(), this.chatRoomData.getName(), this.chatRoomData.getPrimaryCountryID(), this.chatRoomData.getSecondaryCountryID());
        }
        catch (Exception e) {
            log.warn((Object)("Unable to update chat room data [" + this.chatRoomData.getName() + "]"), (Throwable)e);
        }
    }

    private String formatUserNameWithLevel(String username) {
        return ChatRoom.formatUserNameWithLevel(username, null);
    }

    private String formatUserNameWithLevel(ChatRoomParticipant p) {
        return ChatRoom.formatUserNameWithLevel(p.getUsername(), p.getUserID());
    }

    private static String formatUserNameWithLevel(String username, Integer userid) {
        try {
            int userReputation = MemCacheOrEJB.getUserReputationLevel(username, userid);
            return username + " [" + userReputation + "]";
        }
        catch (Exception e) {
            return username;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isLocked() {
        ChatRoomDataWrapper chatRoomDataWrapper = this.chatRoomData;
        synchronized (chatRoomDataWrapper) {
            return this.chatRoomData.isLocked();
        }
    }

    public void lock(String locker) throws FusionException {
        ChatRoomParticipant lockerParticipant = this.participants.verifyYouAreParticipant(locker);
        if (!lockerParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("Only admins and moderators may lock this room");
        }
        this.chatRoomData.tryLock(locker);
        this.queueAdminMessage("This chat room has been locked by " + locker + ". Only admins and moderators may join and/or unlock this chat room.", null, null);
        this.logMessage(lockerParticipant, "<chat room has been locked by " + locker + ">");
        auditLog.info((Object)("chat room [" + this.chatRoomData.getName() + "] has been locked by " + locker));
    }

    public void unlock(String unlocker) throws FusionException {
        ChatRoomParticipant unlockerParticipant = this.participants.verifyYouAreParticipant(unlocker);
        if (!unlockerParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("Only admins and moderators may unlock this room");
        }
        if (this.chatroomEntrantSnapshot.isCurrentSnapshotRunning()) {
            if (this.chatroomEntrantSnapshot.hasLockExpired()) {
                this.chatRoomData.unlock();
                this.chatroomEntrantSnapshot.clearSnapshot();
                throw new FusionException("This chat room is not locked. You can not unlock an unlocked room.");
            }
            throw new FusionException("Please wait until the list command lockout period has expired");
        }
        String locker = this.chatRoomData.tryUnlock();
        this.queueAdminMessage("This chat room has been unlocked by " + unlocker + " (previously locked by " + locker + ").", null, null);
        this.logMessage(unlockerParticipant, "<chat room has been unlocked by " + unlocker + " (previously locked by " + locker + ")>");
        auditLog.info((Object)("chat room [" + this.chatRoomData.getName() + "] has been unlocked by " + unlocker + " (previously locked by " + locker + ")"));
    }

    private MessageData formatAnnounceMessage(String msg, int messageColor) {
        String message = String.format("%s Announcement: <<%s>>", "(announce)", msg);
        MessageData messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), message, new String[]{"(announce)"});
        messageData.contentType = MessageData.ContentTypeEnum.EMOTE;
        messageData.messageColour = messageColor != -1 ? messageColor : 0x770000;
        return messageData;
    }

    private void turnAnnouncementOff(ChatRoomParticipant participant, boolean issuedByUser) {
        if (this.announceTimer != null) {
            this.announceTimer.cancel();
            this.announceTimer = null;
        }
        String curAnnouncer = this.chatRoomData.getAnnouncer();
        String msg = issuedByUser ? "[" + participant.getUsername() + "] has turned off the announcement made previously by [" + curAnnouncer + "]." : "The announcement made previously by [" + curAnnouncer + "] has been turned off because the room is empty.";
        if (issuedByUser) {
            this.queueAdminMessage(msg, null, null);
        }
        this.logMessage(participant, "<" + msg + ">");
        auditLog.info((Object)(msg + " chat room [" + this.chatRoomData.getName() + "]"));
        this.chatRoomData.setAnnouncementOff();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void announceOff(String announcer) throws FusionException {
        ChatRoomParticipant announcerParticipant = this.participants.verifyYouAreParticipant(announcer);
        if (!announcerParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("Only admins and moderators may do an announce in this room");
        }
        ChatRoomDataWrapper chatRoomDataWrapper = this.chatRoomData;
        synchronized (chatRoomDataWrapper) {
            if (!this.chatRoomData.isAnnouncementOn()) {
                throw new FusionException("There is no announcement in this room");
            }
            this.turnAnnouncementOff(announcerParticipant, true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void announceOn(String announcer, String announceMessage, int waitTime) throws FusionException {
        ChatRoomParticipant announcerParticipant = this.participants.verifyYouAreParticipant(announcer);
        if (!announcerParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("Only admins and moderators may do an announce in this room");
        }
        ChatRoomDataWrapper chatRoomDataWrapper = this.chatRoomData;
        synchronized (chatRoomDataWrapper) {
            if (this.chatRoomData.isAnnouncementOn()) {
                throw new FusionException("There is already one announcement made by [" + this.chatRoomData.getAnnouncer() + "]. Please use /announce off to turn it off first.");
            }
            if (this.announceTimer != null) {
                this.announceTimer.cancel();
            }
            this.chatRoomData.setAnnouncementOn(announcer, announceMessage);
            int messageColor = SystemProperty.getInt("ChatroomAnnouncementColor", 0x770000);
            if (waitTime > 0) {
                this.announceTimer = new Timer("ChatRoom Announcer " + this.getRoomData().name, true);
                AnnounceTask announceTask = new AnnounceTask(announceMessage, messageColor, this);
                this.announceTimer.scheduleAtFixedRate((TimerTask)announceTask, waitTime * 1000, (long)(waitTime * 1000));
            }
            String msg = waitTime > 0 ? "[" + announcer + "] has initiated an announcement \"" + announceMessage + "\" for every " + waitTime + " seconds" : "[" + announcer + "] has initiated an one-time announcement \"" + announceMessage + "\"";
            this.queueAdminMessage(msg + ".", null, null);
            this.queueMessage(this.formatAnnounceMessage(announceMessage, messageColor).toIceObject(), null, null, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS));
            this.logMessage(announcerParticipant, "<" + msg + ">");
            auditLog.info((Object)(msg + " in chat room [" + this.chatRoomData.getName() + "]"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void adminAnnounce(String announceMessage, int waitTime) throws FusionException {
        try {
            ChatRoomDataWrapper chatRoomDataWrapper = this.chatRoomData;
            synchronized (chatRoomDataWrapper) {
                if (this.chatRoomData.isAnnouncementOn()) {
                    String curAnnouncer = this.chatRoomData.getAnnouncer();
                    String msg = "The announcement made previously by [" + curAnnouncer + "] has been turned off.";
                    this.queueAdminMessage(msg, null, null);
                    auditLog.info((Object)(msg + " chat room [" + this.chatRoomData.getName() + "]"));
                    this.chatRoomData.setAnnouncementOff();
                }
                if (this.announceTimer != null) {
                    this.announceTimer.cancel();
                }
                String announcer = SystemProperty.get("AdminChatroomAnnouncer", "migme");
                int messageColor = SystemProperty.getInt("AdminChatroomAnnouncementColor", 0x770000);
                this.chatRoomData.setAnnouncementOn(announcer, announceMessage);
                if (waitTime > 0) {
                    this.announceTimer = new Timer("ChatRoom Announcer " + this.getRoomData().name, true);
                    AnnounceTask announceTask = new AnnounceTask(announceMessage, messageColor, this);
                    this.announceTimer.scheduleAtFixedRate((TimerTask)announceTask, waitTime * 1000, (long)(waitTime * 1000));
                }
                String msg = waitTime > 0 ? "[" + announcer + "] has initiated an announcement \"" + announceMessage + "\" for every " + waitTime + " seconds" : "[" + announcer + "] has initiated an one-time announcement \"" + announceMessage + "\"";
                this.queueAdminMessage(msg + ".", null, null);
                this.queueMessage(this.formatAnnounceMessage(announceMessage, messageColor).toIceObject(), null, null, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS));
                auditLog.info((Object)(msg + " in chat room [" + this.chatRoomData.getName() + "]"));
            }
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
    }

    private boolean isGroupLinkedChatroom() {
        return this.chatRoomData.hasGroupData();
    }

    public int executeEmoteCommandWithState(String emoteCommand, MessageDataIce message, SessionPrx sessionProxy) throws FusionException {
        return this.emoteCommandStates.executeEmoteCommandWithState(emoteCommand, message, sessionProxy, this);
    }

    public void submitGiftAllTask(int giftId, String giftMessage, MessageDataIce message) throws FusionException {
        if (this.purging) {
            throw new FusionException("Chatroom marked for purging...");
        }
        ChatRoomParticipant participant = this.participants.get(message.username);
        if (participant == null) {
            throw new FusionException(message.username + " has left the chat");
        }
        GiftAllTask task = participant.createGiftAllTask(giftId, giftMessage, new MessageData(message), this, this.objectManager);
        this.objectManager.getDistributionService().execute(task);
    }

    public void updateExtraData(ChatRoomDataIce newChatRoomDataWithExtraData) {
        this.chatRoomData.updateExtraData(newChatRoomDataWithExtraData);
    }

    public void updateGroupModeratorStatus(String username, boolean promote) {
        ChatRoomParticipant user = this.participants.get(username);
        if (user != null) {
            user.setGroupMod(promote);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Chatroom [" + this.chatRoomData.getName() + "]-user [" + username + "] moderator status is changed to " + promote));
            }
        }
        String announcement = promote ? String.format("Congratulations! '%s' is now a group moderator!", username) : String.format("'%s' is no longer a group moderator", username);
        try {
            this.putSystemMessage(announcement, null);
        }
        catch (Exception ex) {
            log.warn((Object)("Unable to send announcement:[" + announcement + "]" + ex.getMessage()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void bumpUser(String instigator, String target) throws FusionException {
        ChatRoomParticipant instigatorParticipant = this.participants.verifyYouAreParticipant(instigator, ErrorCause.EmoteCommandError.INSTIGATOR_NOT_IN_CHATROOM);
        if (!instigatorParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("Bumps are reserved only for owners, admins and moderators");
        }
        instigatorParticipant.verifyClientMeetsMinVersion("/bump");
        ChatRoomParticipant targetParticipant = this.participants.verifyYouAreParticipant(target, ErrorCause.EmoteCommandError.TARGET_NOT_IN_CHATROOM);
        if (targetParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("You cannot bump owners, admins and moderators");
        }
        Object object = this.adminKickMonitor;
        synchronized (object) {
            targetParticipant = this.participants.get(target);
            if (targetParticipant == null) {
                throw new FusionException(target + " is no longer in the chat room " + this.chatRoomData.getName());
            }
            this.bannedUsers.put(target, new BannedInfo(System.currentTimeMillis() + (long)(SystemProperty.getInt(SystemPropertyEntities.Emote.BUMP_DURATION_SEC) * 1000), BannedInfo.ReasonEnum.BUMP, instigator));
            this.removeParticipant(target);
        }
        auditLog.info((Object)(instigator + " has bumped " + target + " from " + this.chatRoomData.getName()));
        this.logMessage(instigatorParticipant, "has bumped " + target + " from " + this.chatRoomData.getName());
        if (targetParticipant != null) {
            String chatRoomName = StringUtil.truncateWithEllipsis(this.chatRoomData.getName(), SystemProperty.getInt(SystemPropertyEntities.Emote.MAX_CHATROOMNAME_LENGTH_DISPLAY));
            MessageData messageData = MessageData.newChatRoomMessage(this.chatRoomData.getName(), String.format("You have just been bumped out the chatroom '%s' due to violating the rules of this chatroom by administrator %s", chatRoomName, instigator), null);
            targetParticipant.putMessage(messageData.toIceObject());
        }
        this.queueAdminMessage(target + " has been bumped by administrator " + instigator, null, null);
    }

    public void warnUser(String instigator, String target, String message) throws FusionException {
        ChatRoomParticipant instigatorParticipant = this.participants.verifyYouAreParticipant(instigator, ErrorCause.EmoteCommandError.INSTIGATOR_NOT_IN_CHATROOM);
        if (!instigatorParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("Issuing warnings are reserved only for owners, admins and moderators");
        }
        instigatorParticipant.verifyClientMeetsMinVersion("/warn");
        ChatRoomParticipant targetParticipant = this.participants.verifyIsParticipant(target, ErrorCause.EmoteCommandError.TARGET_NOT_IN_CHATROOM);
        if (targetParticipant.hasAdminOrModeratorRights()) {
            throw new FusionException("You cannot issue warnings to owners, admins and moderators");
        }
        auditLog.info((Object)(instigator + " has warned " + target + " in " + this.chatRoomData.getName() + ".Message: <" + message + ">"));
        this.logMessage(instigatorParticipant, "has warned " + target + " in " + this.chatRoomData.getName() + ".Message: <" + message + ">");
        ClientType device = targetParticipant.getDeviceType();
        boolean isWeb = false;
        switch (device) {
            case AJAX1: 
            case AJAX2: {
                isWeb = true;
                break;
            }
            default: {
                isWeb = false;
            }
        }
        StringBuilder warningMessage = new StringBuilder("[PVT]");
        if (StringUtil.isBlank(message)) {
            warningMessage.append("Your chatroom moderator has warned you. Do adhere to chatroom Do's and Don'ts at all times.");
        } else {
            warningMessage.append(String.format("Your chatroom moderator has a warning message for you: %s. Do adhere to chatroom Do's and Don'ts at all times.", message));
        }
        if (!isWeb) {
            warningMessage.append("Go to migWorld to find out more.");
        }
        auditLog.info((Object)(instigator + " has warned " + target + " from " + this.chatRoomData.getName()));
        this.logMessage(instigatorParticipant, "has warned " + target + " from " + this.chatRoomData.getName());
        this.queueAdminMessage(warningMessage.toString(), target, null);
        this.queueAdminMessage(String.format("'%s' has been warned", target), instigator, null);
    }

    public void addGroupModerator(String instigator, String target) throws FusionException {
        block20: {
            if (!this.isGroupLinkedChatroom()) {
                throw new FusionExceptionWithErrorCauseCode("Command is only available for group-linked chatrooms", ErrorCause.EmoteCommandError.INVALID_CHATROOM_TYPE.getCode());
            }
            ChatRoomParticipant instigatorParticipant = this.participants.verifyYouAreParticipant(instigator, ErrorCause.EmoteCommandError.INSTIGATOR_NOT_IN_CHATROOM);
            if (!instigatorParticipant.isGroupAdmin()) {
                throw new FusionExceptionWithErrorCauseCode("Command is only available for group admins", ErrorCause.EmoteCommandError.INVALID_USER_TYPE.getCode());
            }
            instigatorParticipant.verifyClientMeetsMinVersion("/mod");
            ChatRoomInfo info = this.chatRoomData.getInfo();
            String chatRoomName = info.chatRoomName;
            Integer groupID = info.groupID;
            String groupName = info.groupName;
            String groupOwner = info.groupOwner;
            try {
                String messageText;
                GroupMemberData targetGroupMemberData = null;
                UserObject user = new UserObject(target);
                if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
                    try {
                        targetGroupMemberData = user.getGroupMember(groupID);
                    }
                    catch (DAOException e) {
                        log.error((Object)String.format("DAO: Failed to get group member data for user:%s, group:%s", target, groupID), (Throwable)e);
                    }
                } else {
                    User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                    targetGroupMemberData = userEJB.getGroupMember(target, groupID);
                }
                if (targetGroupMemberData == null) {
                    log.info((Object)String.format("'%s' cannot be added as a group moderator for group '%s' since he/she is not a group member", target, groupName));
                    messageText = String.format("[PVT] '%s' is not a group member yet", target);
                    MessageData messageDataToInstigator = MessageData.newChatRoomMessage(chatRoomName, messageText, null);
                    instigatorParticipant.putMessage(messageDataToInstigator.toIceObject());
                    break block20;
                }
                if (targetGroupMemberData.type == GroupMemberData.TypeEnum.MODERATOR) {
                    log.info((Object)String.format("'%s' cannot be added as a group moderator for group '%s' since he/she is already a moderator", target, groupName));
                    messageText = String.format("[PVT] '%s' is already a group moderator", target);
                    MessageData messageDataToInstigator = MessageData.newChatRoomMessage(chatRoomName, messageText, null);
                    instigatorParticipant.putMessage(messageDataToInstigator.toIceObject());
                    break block20;
                }
                if (targetGroupMemberData.type == GroupMemberData.TypeEnum.ADMINISTRATOR) {
                    log.info((Object)String.format("'%s' cannot be added as a group moderator for group '%s' since he/she is already a moderator", target, groupName));
                    messageText = String.format("[PVT] '%s' is a group administrator", target);
                    MessageData messageDataToInstigator = MessageData.newChatRoomMessage(chatRoomName, messageText, null);
                    instigatorParticipant.putMessage(messageDataToInstigator.toIceObject());
                    break block20;
                }
                if (targetGroupMemberData.type == GroupMemberData.TypeEnum.REGULAR) {
                    boolean isUserBlackListedInGroup = false;
                    if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
                        try {
                            isUserBlackListedInGroup = user.isUserBlackListedInGroup(groupID);
                        }
                        catch (DAOException e) {
                            log.warn((Object)String.format("DAO: Failed to check isUserBlackListedInGroup for user:%s, group:%s", user, groupID), (Throwable)e);
                        }
                    } else {
                        Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                        isUserBlackListedInGroup = messageEJB.isUserBlackListedInGroup(target, groupID);
                    }
                    if (isUserBlackListedInGroup) {
                        log.info((Object)String.format("'%s' cannot be added as a group moderator for group '%s' since he/she is blacklisted", target, groupName));
                        String messageText2 = String.format("[PVT] '%s' has been banned. you can\u2019t add to be a group moderator.", target);
                        MessageData messageDataToInstigator = MessageData.newChatRoomMessage(chatRoomName, messageText2, null);
                        instigatorParticipant.putMessage(messageDataToInstigator.toIceObject());
                    } else {
                        Group groupEJB = (Group)EJBHomeCache.getObject("ejb/Group", GroupHome.class);
                        groupEJB.giveGroupMemberModeratorRights(groupOwner, groupID, target);
                        String logMessage = instigator + " has added " + target + " into the group moderator list for group " + groupName;
                        log.info((Object)logMessage);
                        auditLog.info((Object)logMessage);
                        this.logMessage(instigatorParticipant, " has added " + target + " into the group moderator list for group " + groupName);
                        ChatRoomParticipant targetParticipant = this.participants.get(target);
                        if (targetParticipant != null) {
                            String messageText3 = String.format("[PVT] Congratulations! %s is now a group moderator for group '%s'", target, groupName);
                            MessageData messageDataToTarget = MessageData.newChatRoomMessage(chatRoomName, messageText3, null);
                            targetParticipant.putMessage(messageDataToTarget.toIceObject());
                        }
                    }
                    break block20;
                }
                throw new FusionExceptionWithErrorCauseCode("Target group member type is invalid/not supported", ErrorCause.EmoteCommandError.INVALID_USER_TYPE.getCode());
            }
            catch (CreateException e) {
                log.error((Object)"Caught create exception", (Throwable)e);
                throw new FusionExceptionWithErrorCauseCode("Internal error", ErrorCause.EmoteCommandError.INTERNAL_ERROR.getCode());
            }
            catch (RemoteException e) {
                ChatRoom.interceptExceptionCause(e);
            }
        }
    }

    public void removeGroupModerator(String instigator, String target) throws FusionException {
        block14: {
            if (!this.isGroupLinkedChatroom()) {
                throw new FusionExceptionWithErrorCauseCode("Command is only available for group-linked chatrooms", ErrorCause.EmoteCommandError.INVALID_CHATROOM_TYPE.getCode());
            }
            ChatRoomParticipant instigatorParticipant = this.participants.verifyYouAreParticipant(instigator, ErrorCause.EmoteCommandError.INSTIGATOR_NOT_IN_CHATROOM);
            if (!instigatorParticipant.isGroupAdmin()) {
                throw new FusionExceptionWithErrorCauseCode("Command is only available for group admins", ErrorCause.EmoteCommandError.INVALID_USER_TYPE.getCode());
            }
            instigatorParticipant.verifyClientMeetsMinVersion("/unmod");
            ChatRoomInfo info = this.chatRoomData.getInfo();
            String chatRoomName = info.chatRoomName;
            Integer groupID = info.groupID;
            String groupName = info.groupName;
            String groupOwner = info.groupOwner;
            try {
                String messageText;
                GroupMemberData targetGroupMemberData = null;
                UserObject user = new UserObject(target);
                if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
                    try {
                        targetGroupMemberData = user.getGroupMember(groupID);
                    }
                    catch (DAOException e) {
                        log.error((Object)String.format("DAO: Failed to get GroupMember for user:%s, group:%s", user, groupID));
                    }
                } else {
                    User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                    targetGroupMemberData = userEJB.getGroupMember(target, groupID);
                }
                if (targetGroupMemberData == null) {
                    log.info((Object)String.format("'%s' cannot be removed from the group moderator list for group '%s' since he/she is not a group member", target, groupName));
                    messageText = String.format("[PVT] '%s' is not a group member yet", target);
                    MessageData messageDataToInstigator = MessageData.newChatRoomMessage(chatRoomName, messageText, null);
                    instigatorParticipant.putMessage(messageDataToInstigator.toIceObject());
                    break block14;
                }
                if (targetGroupMemberData.type == GroupMemberData.TypeEnum.REGULAR) {
                    log.info((Object)String.format("'%s' cannot be removed from the group moderator list for group '%s' since he/she is not yet a moderator", target, groupName));
                    messageText = String.format("[PVT] '%s' is not yet a group moderator", target);
                    MessageData messageDataToInstigator = MessageData.newChatRoomMessage(chatRoomName, messageText, null);
                    instigatorParticipant.putMessage(messageDataToInstigator.toIceObject());
                    break block14;
                }
                if (targetGroupMemberData.type == GroupMemberData.TypeEnum.ADMINISTRATOR) {
                    log.info((Object)String.format("'%s' cannot be removed from the group moderator for group '%s' since he/she is a group administrator", target, groupName));
                    messageText = String.format("[PVT] '%s' is a group administrator", target);
                    MessageData messageDataToInstigator = MessageData.newChatRoomMessage(chatRoomName, messageText, null);
                    instigatorParticipant.putMessage(messageDataToInstigator.toIceObject());
                    break block14;
                }
                if (targetGroupMemberData.type == GroupMemberData.TypeEnum.MODERATOR) {
                    Group groupEJB = (Group)EJBHomeCache.getObject("ejb/Group", GroupHome.class);
                    groupEJB.removeGroupMemberModeratorRights(groupOwner, groupID, target);
                    String logMessage = instigator + " has removed " + target + " from the group moderator list for group " + groupName;
                    log.info((Object)logMessage);
                    auditLog.info((Object)logMessage);
                    this.logMessage(instigatorParticipant, " has removed " + target + " from the group moderator list for group " + groupName);
                    String messageText2 = String.format("[PVT] %s is no longer a moderator for group '%s'", target, groupName);
                    MessageData messageDataToTarget = MessageData.newChatRoomMessage(chatRoomName, messageText2, null);
                    ChatRoomParticipant targetParticipant = this.participants.get(target);
                    if (targetParticipant != null) {
                        targetParticipant.putMessage(messageDataToTarget.toIceObject());
                    }
                    break block14;
                }
                throw new FusionExceptionWithErrorCauseCode("Target group member type is invalid/not supported", ErrorCause.EmoteCommandError.INVALID_USER_TYPE.getCode());
            }
            catch (CreateException e) {
                log.error((Object)"Caught create exception", (Throwable)e);
                throw new FusionExceptionWithErrorCauseCode("Internal error", ErrorCause.EmoteCommandError.INTERNAL_ERROR.getCode());
            }
            catch (RemoteException e) {
                ChatRoom.interceptExceptionCause(e);
            }
        }
    }

    private static Throwable getCause(Throwable t) {
        if (t instanceof EJBException) {
            return ((EJBException)t).getCausedByException();
        }
        return t.getCause();
    }

    private static void interceptExceptionCause(RemoteException re) throws FusionException {
        Throwable cause = ChatRoom.getCause(re);
        while (true) {
            if (cause == null) {
                log.error((Object)"Caught Remote Exception", (Throwable)re);
                throw new FusionException("Internal error");
            }
            if (cause instanceof EJBExceptionWithErrorCause) {
                EJBExceptionWithErrorCause ejbExWithCause = (EJBExceptionWithErrorCause)((Object)cause);
                throw new FusionExceptionWithErrorCauseCode(ejbExWithCause.getMessage(), ejbExWithCause.getErrorCause().getCode());
            }
            if (cause instanceof EJBException) {
                EJBException ejbEx = (EJBException)cause;
                if ((cause = ChatRoom.getCause(ejbEx)) != null) continue;
                log.error((Object)"Caught Remote Exception due to EJBException", (Throwable)re);
                throw new FusionException(ejbEx.getMessage());
            }
            if (cause instanceof ExceptionWithErrorCause) {
                ExceptionWithErrorCause exWithCause = (ExceptionWithErrorCause)cause;
                throw new FusionExceptionWithErrorCauseCode(exWithCause.getMessage(), exWithCause.getErrorCause().getCode());
            }
            cause = ChatRoom.getCause(cause);
        }
    }

    public String[] getGroupModerators(String instigator) throws FusionException {
        if (!this.isGroupLinkedChatroom()) {
            throw new FusionExceptionWithErrorCauseCode("Command is only available for group-linked chatrooms", ErrorCause.EmoteCommandError.INVALID_CHATROOM_TYPE.getCode());
        }
        ChatRoomParticipant instigatorParticipant = this.participants.verifyYouAreParticipant(instigator, ErrorCause.EmoteCommandError.INSTIGATOR_NOT_IN_CHATROOM);
        if (!instigatorParticipant.isGroupAdmin()) {
            throw new FusionExceptionWithErrorCauseCode("Command is only available for group admins", ErrorCause.EmoteCommandError.INVALID_USER_TYPE.getCode());
        }
        Integer groupID = this.chatRoomData.getGroupID();
        try {
            Set moderatorNames = null;
            if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_GROUP_DAO)) {
                try {
                    moderatorNames = DAOFactory.getInstance().getGroupDAO().getModeratorUserNames(groupID, false);
                }
                catch (DAOException e) {
                    log.error((Object)String.format("Failed to get ModeratorUserNames for group:%s", groupID), (Throwable)e);
                }
            } else {
                Group groupEJB = (Group)EJBHomeCache.getObject("ejb/Group", GroupHome.class);
                moderatorNames = groupEJB.getModeratorUserNames(groupID, false);
            }
            if (moderatorNames != null) {
                String[] moderators = new String[moderatorNames.size()];
                int i = 0;
                Iterator i$ = moderatorNames.iterator();
                while (i$.hasNext()) {
                    String mods;
                    moderators[i] = mods = (String)i$.next();
                    ++i;
                }
                return moderators;
            }
            throw new FusionExceptionWithErrorCauseCode(groupID + " not valid.", ErrorCause.EmoteCommandError.INVALID_GROUP.getCode());
        }
        catch (CreateException e) {
            log.error((Object)"Caught create exception", (Throwable)e);
            throw new FusionExceptionWithErrorCauseCode("Internal error", ErrorCause.EmoteCommandError.INTERNAL_ERROR.getCode());
        }
        catch (RemoteException e) {
            ChatRoom.interceptExceptionCause(e);
            log.error((Object)"Caught remote exception", (Throwable)e);
            throw new FusionException("INTERNAL ERROR");
        }
    }

    private class KickUserVote
    implements Runnable {
        private ChatRoomParticipant instigator;
        private String target;
        private HashSet<String> yesVoters = new HashSet();
        private boolean votePassed;
        private int iteration;
        private final long createTime;
        private final String roomName;
        private final String instanceID;

        public KickUserVote(ChatRoomParticipant instigator, String roomName, String target) {
            this.instigator = instigator;
            this.target = target;
            this.yesVoters.add(instigator.getUsername());
            this.createTime = System.currentTimeMillis();
            this.roomName = roomName;
            this.instanceID = UUID.randomUUID().toString();
            scheduler.execute(this);
        }

        private String formatLogMessage(String stmt) {
            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append("Room:[");
            msgBuilder.append(this.roomName);
            msgBuilder.append("];Vote Instance:[");
            msgBuilder.append(this.instanceID);
            msgBuilder.append("];");
            msgBuilder.append(stmt);
            return msgBuilder.toString();
        }

        private void logInfo(String msg) {
            log.info((Object)this.formatLogMessage(msg));
        }

        private void logError(String msg, Throwable t) {
            log.error((Object)this.formatLogMessage(msg), t);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            try {
                boolean enableOverrideExpiredKickUserVote = SystemProperty.getBool(SystemPropertyEntities.Chatroom.ENABLE_OVERRIDE_EXPIRED_KICK_USER_VOTE);
                Object object = ChatRoom.this.kickUserVoteMonitor;
                synchronized (object) {
                    String progressMessage;
                    KickUserVote currentKickUserVote = ChatRoom.this.kickUserVote;
                    if (enableOverrideExpiredKickUserVote && currentKickUserVote != null && currentKickUserVote != this) {
                        this.logInfo("The vote to kick (" + this.toString() + ") has ended (due replaced by another vote to kick " + currentKickUserVote.toString() + ".");
                        ChatRoom.this.queueAdminMessage("The vote to kick " + this.target + " has failed", null, null);
                        return;
                    }
                    if (this.votePassed) {
                        return;
                    }
                    int secondsRemaining = (ChatRoom.this.kickUserVoteDuration - ChatRoom.this.kickUserVoteUpdateInterval * this.iteration) / 1000;
                    if (secondsRemaining <= 0) {
                        this.logInfo("The vote to kick (" + this.toString() + ") has ended (due to max voting duration reached)");
                        ChatRoom.this.queueAdminMessage("The vote to kick " + this.target + " has failed", null, null);
                        ChatRoom.this.endKickUserVote();
                        return;
                    }
                    BannedInfo bannedInfo = (BannedInfo)ChatRoom.this.bannedUsers.get(this.target);
                    if (bannedInfo != null && bannedInfo.getExpiredTime() > System.currentTimeMillis()) {
                        this.logInfo("The vote to kick (" + this.toString() + ") has ended (due to target already " + bannedInfo.getReason().getPrevAction() + ")");
                        ChatRoom.this.endKickUserVote();
                        return;
                    }
                    int numYesVotes = this.yesVoters.size();
                    int numVotesRequired = this.numYesVotesRequired() - numYesVotes;
                    if (this.iteration++ == 0) {
                        progressMessage = "A vote to kick " + this.target + " has been started by " + this.instigator.getUsername() + ". ";
                        progressMessage = progressMessage + numVotesRequired + " more vote" + (numVotesRequired == 1 ? " " : "s ");
                        progressMessage = progressMessage + "needed, " + secondsRemaining + "s remaining";
                        ChatRoom.this.logMessage(this.instigator, "<" + this.instigator.getUsername() + " started a vote to kick " + this.target + ">");
                    } else {
                        progressMessage = "Vote to kick " + this.target + ": " + numYesVotes + " vote" + (numYesVotes == 1 ? "" : "s") + ", ";
                        progressMessage = progressMessage + numVotesRequired + " more needed. " + secondsRemaining + "s remaining";
                    }
                    ChatRoom.this.queueAdminMessage(progressMessage, null, null);
                    scheduler.schedule(this, (long)ChatRoom.this.kickUserVoteUpdateInterval, TimeUnit.MILLISECONDS);
                }
            }
            catch (Throwable t) {
                StringBuilder logDetails = new StringBuilder();
                logDetails.append("Unhandled exception on KickUserVote.run();");
                logDetails.append("ErrorID:<" + UUID.randomUUID().toString() + ">;");
                logDetails.append("KickUserVote:<").append(this.toString()).append(">;");
                logDetails.append("Exception:<").append(t.getMessage()).append(">");
                this.logError(logDetails.toString(), t);
                ChatRoom.this.logMessage(this.instigator, logDetails.toString());
            }
        }

        public String toString() {
            String instigatorName = this.instigator != null ? this.instigator.getUsername() : "";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("roomName:[").append(this.roomName).append("];");
            stringBuilder.append("instigator:[").append(instigatorName).append("];");
            stringBuilder.append("iteration:[").append(this.iteration).append("];");
            stringBuilder.append("target:[").append(this.target).append("];");
            stringBuilder.append("votePassed:[").append(this.votePassed).append("];");
            stringBuilder.append("yesVoters:[").append(this.yesVoters).append("];");
            stringBuilder.append("createTime:[").append(DateTimeUtils.getStringForTimestamp(new Date(this.createTime))).append("];");
            stringBuilder.append("instanceID:[").append(this.instanceID).append("];");
            return stringBuilder.toString();
        }

        public boolean isVotingExpired() {
            return System.currentTimeMillis() - this.createTime > (long)(ChatRoom.this.kickUserVoteDuration * 3 / 2);
        }

        public void addYesVote(ChatRoomParticipant voterParticipant) throws FusionException {
            if (this.votePassed) {
                return;
            }
            if (!this.yesVoters.add(voterParticipant.getUsername())) {
                throw new FusionException("You have already voted to kick " + this.target);
            }
            ChatRoom.this.logMessage(voterParticipant, "<" + voterParticipant.getUsername() + " voted to kick " + this.target + ">");
            if (this.yesVoters.size() >= this.numYesVotesRequired()) {
                this.votePassed = true;
                try {
                    ChatRoomParticipant targetParticipant = ChatRoom.this.participants.get(this.target);
                    if (targetParticipant != null) {
                        ChatRoom.this.kickParticipant(null, targetParticipant, null);
                    }
                }
                catch (FusionException e) {
                }
                catch (Exception e) {
                    this.logError("kickParticipant [" + this.target + "] failed. Vote to kick (" + this.toString() + ")", e);
                }
                ChatRoom.this.endKickUserVote();
            }
        }

        public void removeYesVote(String username) {
            this.yesVoters.remove(username);
        }

        public String getTarget() {
            return this.target;
        }

        public long getCreateTime() {
            return this.createTime;
        }

        private int numYesVotesRequired() {
            return (int)Math.max((double)ChatRoom.this.minKickVotesRequired, Math.ceil((double)ChatRoom.this.participants.size() / 3.0));
        }
    }

    private class ExitMessageToDispatch
    extends MessageToDispatchToRoomExcludingUser {
        public ExitMessageToDispatch(MessageDataIce message, String userLeaving) {
            super(message, MessageToDispatchTypeEnum.TEXT, userLeaving);
        }

        boolean shouldSendMessage() {
            BannedInfo bi = (BannedInfo)ChatRoom.this.bannedUsers.get(this.usernameToExclude);
            if (bi != null && bi.getExpiredTime() < System.currentTimeMillis()) {
                return false;
            }
            if (ChatRoom.this.chatRoomData.isOnBannedList(this.usernameToExclude)) {
                return false;
            }
            return super.shouldSendMessage();
        }
    }

    private class EnterMessageToDispatch
    extends MessageToDispatchToRoomExcludingUser {
        public EnterMessageToDispatch(MessageDataIce message, String usernameToExclude) {
            super(message, MessageToDispatchTypeEnum.TEXT, usernameToExclude);
        }

        boolean shouldSendMessage() {
            if (!ChatRoom.this.participants.isParticipant(this.usernameToExclude)) {
                return false;
            }
            return super.shouldSendMessage();
        }
    }

    private class UserMessageToDispatchToRoom
    extends MessageToDispatchToRoomExcludingUser {
        public UserMessageToDispatchToRoom(MessageDataIce message, String usernameToExclude) {
            super(message, MessageToDispatchTypeEnum.TEXT, usernameToExclude);
        }

        boolean shouldSendMessage() {
            if (!ChatRoom.this.participants.isParticipant(this.usernameToExclude)) {
                return false;
            }
            return super.shouldSendMessage();
        }
    }

    private class MessageToDispatchToRoomExcludingUser
    extends MessageToDispatchToRoom {
        protected String usernameToExclude;

        public MessageToDispatchToRoomExcludingUser(MessageDataIce message, MessageToDispatchTypeEnum type, String usernameToExclude) {
            super(message, type);
            this.usernameToExclude = usernameToExclude;
        }

        boolean shouldSendToParticipant(ChatRoomParticipant participant, boolean toModerators) {
            if (participant.getUsername().equals(this.usernameToExclude)) {
                return false;
            }
            return super.shouldSendToParticipant(participant, toModerators);
        }
    }

    private class MessageToDispatchToRoom
    extends MessageToDispatch {
        public MessageToDispatchToRoom(MessageDataIce message, MessageToDispatchTypeEnum type) {
            super(message, type);
        }

        public void dispatch() {
            if (this.shouldSendMessage()) {
                boolean toModerators = this.message.fromAdministrator != 1 && ChatRoom.this.isStadium();
                for (ChatRoomParticipant participant : ChatRoom.this.participants.getAll()) {
                    if (!this.shouldSendToParticipant(participant, toModerators)) continue;
                    this.putMessage(participant);
                }
            }
        }

        boolean shouldSendMessage() {
            boolean sysMsg;
            boolean bl = sysMsg = this.message.sourceType == MessageData.SourceTypeEnum.CHATROOM.value() || this.message.sourceType == MessageData.SourceTypeEnum.SYSTEM_GENERAL.value();
            if (sysMsg) {
                return true;
            }
            boolean mutedUser = ChatRoom.this.mutedUsers.containsKey(this.message.source);
            if (mutedUser) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Ignoring dispatch of message from muted user:" + this.message.source));
                }
                return false;
            }
            return true;
        }

        boolean shouldSendToParticipant(ChatRoomParticipant participant, boolean toModerators) {
            return !toModerators || participant.hasAdminOrModeratorRights();
        }
    }

    private class MessageToDispatchToSingleUser
    extends MessageToDispatch {
        private String usernameToReceive;

        public MessageToDispatchToSingleUser(MessageDataIce message, MessageToDispatchTypeEnum type, String usernameToReceive) {
            super(message, type);
            this.usernameToReceive = usernameToReceive;
        }

        public void dispatch() {
            ChatRoomParticipant participant = ChatRoom.this.participants.get(this.usernameToReceive);
            if (participant != null) {
                this.putMessage(participant);
            }
        }
    }

    private abstract class MessageToDispatch {
        protected MessageDataIce message;
        private MessageToDispatchTypeEnum type;

        protected MessageToDispatch(MessageDataIce message, MessageToDispatchTypeEnum type) {
            this.message = message;
            this.type = type;
        }

        public abstract void dispatch();

        protected void putMessage(ChatRoomParticipant participant) {
            participant.putMessage_async(this.message);
            if (this.type == MessageToDispatchTypeEnum.POPUP || this.type == MessageToDispatchTypeEnum.TEXT_AND_POPUP) {
                participant.putAlertMessage_async(this.message.messageText);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum MessageToDispatchTypeEnum {
        TEXT,
        POPUP,
        TEXT_AND_POPUP;

    }

    private class AnnounceTask
    extends TimerTask {
        String announceMessage;
        ChatRoom chatRoom;
        int messageColor = -1;

        AnnounceTask(String announceMessage, int messageColor, ChatRoom chatRoom2) {
            this.announceMessage = announceMessage;
            this.chatRoom = chatRoom2;
            this.messageColor = messageColor;
        }

        public void run() {
            this.chatRoom.queueMessage(ChatRoom.this.formatAnnounceMessage(this.announceMessage, this.messageColor).toIceObject(), null, null, MessageToDispatchTypeEnum.TEXT, SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_ADD_TIMEOUT_IN_MILLIS));
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class BlockingQueueViaList<T>
    implements AbstractBlockingQueue<T> {
        private List<T> list;

        BlockingQueueViaList(List<T> list) {
            this.list = list;
        }

        @Override
        public boolean offer(T e, long timeoutInMillis) {
            long waitedTime = 0L;
            if (timeoutInMillis >= 0L) {
                long sleepInterval;
                long waitInterval = SystemProperty.getLong(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_VIA_LIST_ADD_WAIT_INTERVAL_IN_MILLIS);
                long l = sleepInterval = timeoutInMillis - waitedTime > waitInterval ? waitInterval : timeoutInMillis - waitedTime;
                while (waitedTime < timeoutInMillis && this.list.size() > SystemProperty.getInt(SystemPropertyEntities.Chatroom.MESSAGE_QUEUE_VIA_LIST_MAX_SIZE)) {
                    try {
                        Thread.sleep(sleepInterval);
                    }
                    catch (InterruptedException e1) {
                        log.error((Object)String.format("BlockingQueueViaList.offer interruppted while adding %s with %d timeout", e, timeoutInMillis), (Throwable)e1);
                        return false;
                    }
                    sleepInterval = timeoutInMillis - (waitedTime += sleepInterval) > waitInterval ? waitInterval : timeoutInMillis - waitedTime;
                }
            }
            if (timeoutInMillis == -1L || waitedTime < timeoutInMillis) {
                return this.list.add(e);
            }
            log.error((Object)String.format("BlockingQueueViaList.offer dropping msg %s with %d timeout", e, timeoutInMillis));
            return false;
        }

        @Override
        public T takeIfAvailable() {
            try {
                return this.list.remove(0);
            }
            catch (IndexOutOfBoundsException e) {
                return null;
            }
            catch (NoSuchElementException e) {
                return null;
            }
            catch (Exception e) {
                log.error((Object)String.format("Unexpected exception in BlockingQueueViaList.takeIfAvailable", new Object[0]), (Throwable)e);
                return null;
            }
        }

        @Override
        public int size() {
            return this.list.size();
        }

        @Override
        public boolean isEmpty() {
            return this.list.isEmpty();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class BlockingQueueViaBlockingQueue<T>
    implements AbstractBlockingQueue<T> {
        private BlockingQueue<T> queue;

        BlockingQueueViaBlockingQueue(BlockingQueue<T> queue) {
            this.queue = queue;
        }

        @Override
        public boolean offer(T e, long timeoutInMillis) {
            try {
                return this.queue.offer(e, timeoutInMillis, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e1) {
                log.error((Object)String.format("BlockingQueueViaBlockingQueue.offer interruppted while adding %s with %d timeout", e, timeoutInMillis), (Throwable)e1);
                return false;
            }
        }

        @Override
        public T takeIfAvailable() {
            try {
                return this.queue.poll(0L, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                log.error((Object)String.format("BlockingQueueViaBlockingQueue.takeIfAvailable interruppted", new Object[0]), (Throwable)e);
                return null;
            }
        }

        @Override
        public int size() {
            return this.queue.size();
        }

        @Override
        public boolean isEmpty() {
            return this.queue.isEmpty();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static interface AbstractBlockingQueue<T> {
        public boolean offer(T var1, long var2);

        public T takeIfAvailable();

        public int size();

        public boolean isEmpty();
    }

    static class MimeDataJSONObject
    extends JSONObject {
        MimeDataJSONObject() {
        }

        public MimeDataJSONObject put(String key, int value) {
            try {
                super.put(key, value);
            }
            catch (JSONException e) {
                log.error((Object)String.format("Unable to serialize key [%s], value [%d]", key, value), (Throwable)e);
            }
            return this;
        }

        public MimeDataJSONObject put(String key, Object value) {
            try {
                super.put(key, value);
            }
            catch (JSONException e) {
                log.error((Object)String.format("Unable to serialize key [%s], value [%s]", key, value), (Throwable)e);
            }
            return this;
        }
    }

    private static class BannedInfo {
        private long expiredTime;
        private ReasonEnum reason;
        private String instigator;

        public BannedInfo(long expiredTime, ReasonEnum reason, String instigator) {
            this.expiredTime = expiredTime;
            this.reason = reason;
            this.instigator = instigator;
        }

        public long getExpiredTime() {
            return this.expiredTime;
        }

        public ReasonEnum getReason() {
            return this.reason;
        }

        public String getInstigator() {
            return this.instigator;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ReasonEnum {
            KICK("kicked"),
            BUMP("bumped");

            private String prevAction;

            private ReasonEnum(String prevAction) {
                this.prevAction = prevAction;
            }

            public String getPrevAction() {
                return this.prevAction;
            }
        }
    }
}

