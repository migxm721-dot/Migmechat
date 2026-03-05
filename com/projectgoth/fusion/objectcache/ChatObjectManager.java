/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.AlreadyRegisteredException
 *  Ice.Identity
 *  Ice.LocalException
 *  Ice.NotRegisteredException
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.ObjectImpl
 *  Ice.ObjectPrx
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import Ice.AlreadyRegisteredException;
import Ice.Identity;
import Ice.LocalException;
import Ice.NotRegisteredException;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.ObjectImpl;
import Ice.ObjectPrx;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.RedisChatSyncStore;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.LRUCache;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.exception.ExceptionWithDiagnosticCode;
import com.projectgoth.fusion.exception.GroupChatNoLongerActiveException;
import com.projectgoth.fusion.exception.InternalServerErrorException;
import com.projectgoth.fusion.exception.UserNotOnlineException;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.messagelogger.MessageToLog;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardDispatcher;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardI;
import com.projectgoth.fusion.objectcache.ChatContentStore;
import com.projectgoth.fusion.objectcache.ChatGroup;
import com.projectgoth.fusion.objectcache.ChatObjectManagerGroup;
import com.projectgoth.fusion.objectcache.ChatObjectManagerRoom;
import com.projectgoth.fusion.objectcache.ChatObjectManagerSession;
import com.projectgoth.fusion.objectcache.ChatObjectManagerUser;
import com.projectgoth.fusion.objectcache.ChatRoom;
import com.projectgoth.fusion.objectcache.ChatRoomPreSE454;
import com.projectgoth.fusion.objectcache.ChatRoomRpcI;
import com.projectgoth.fusion.objectcache.ChatSession;
import com.projectgoth.fusion.objectcache.ChatUser;
import com.projectgoth.fusion.objectcache.GroupChatRpcI;
import com.projectgoth.fusion.objectcache.ObjectCache;
import com.projectgoth.fusion.objectcache.ObjectCacheContext;
import com.projectgoth.fusion.objectcache.SessionRpcI;
import com.projectgoth.fusion.objectcache.UserRpcI;
import com.projectgoth.fusion.slice.AuthenticationServicePrx;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ChatRoomPrxHelper;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.GroupChatPrxHelper;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrxHelper;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SessionCachePrx;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.SessionPrxHelper;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import com.projectgoth.fusion.stats.FusionStatsIceDispatchInterceptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Logger;

public class ChatObjectManager
implements ChatObjectManagerUser,
ChatObjectManagerSession,
ChatObjectManagerRoom,
ChatObjectManagerGroup {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatObjectManager.class));
    private final ScheduledThreadPoolExecutor distributionService;
    private final IcePrxFinder icePrxFinder;
    private final AuthenticationServicePrx authenticationServiceProxy;
    private final String iceID;
    private AtomicReference<MessageSwitchboardPrx> localMessageSwitchboardPrx = new AtomicReference<java.lang.Object>(null);
    private final IdleSessionPurger idleSessionPurger;
    private final IdleRoomPurger idleRoomPurger;
    private final IdleGroupChatPurger idleGroupPurger;
    private ObjectMetrics metrics;
    private String instanceID;
    ObjectCacheContext applicationContext;
    private ChatContentStore chatContentStore;
    final ConcurrentMap<String, UserRpcI> userObjects = new ConcurrentHashMap<String, UserRpcI>();
    final ConcurrentMap<String, SessionRpcI> sessionObjects = new ConcurrentHashMap<String, SessionRpcI>();
    final ConcurrentMap<String, ChatRoomRpcI> chatRoomObjects = new ConcurrentHashMap<String, ChatRoomRpcI>();
    final ConcurrentMap<String, GroupChatRpcI> groupChatObjects = new ConcurrentHashMap<String, GroupChatRpcI>();
    LRUCache<String, UserPrx> recentUserProxiesCache;
    LRUCache<String, SessionPrx> recentSessionProxiesCache;
    LRUCache<String, ChatRoomPrx> recentChatRoomProxiesCache;
    LRUCache<String, GroupChatPrx> recentGroupChatProxiesCache;
    private int loadWeightage;
    private Semaphore giftAllSemaphore;
    private static boolean logSessions;

    public ObjectCacheStats getStats(ObjectCacheStats stats) {
        if (stats == null) {
            stats = new ObjectCacheStats();
        }
        this.metrics.getStats(stats);
        return stats;
    }

    public String[] getUsernames() {
        return this.userObjects.keySet().toArray(new String[this.userObjects.size()]);
    }

    public int getUserCount() {
        return this.userObjects.size();
    }

    public ChatObjectManager(ObjectCacheContext applicationContext, String iceID) {
        this.applicationContext = applicationContext;
        this.iceID = iceID;
        this.idleSessionPurger = new IdleSessionPurger(applicationContext.getProperties());
        this.idleRoomPurger = new IdleRoomPurger(applicationContext.getProperties());
        this.idleGroupPurger = new IdleGroupChatPurger(applicationContext.getProperties());
        this.icePrxFinder = new IcePrxFinder(applicationContext.getCommunicator(), applicationContext.getProperties());
        this.authenticationServiceProxy = this.icePrxFinder.waitForAuthenticationServiceProxy();
        int maxDistributionThreadPoolSize = applicationContext.getProperties().getPropertyAsIntWithDefault("MaxDistributionThreadPoolSize", 25);
        this.distributionService = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(maxDistributionThreadPoolSize);
        this.recentUserProxiesCache = new LRUCache(applicationContext.getProperties().getPropertyAsIntWithDefault("UserProxiesCacheSize", 5000), applicationContext.getProperties().getPropertyAsIntWithDefault("UserProxiesCacheExpiryTime", 60));
        this.recentChatRoomProxiesCache = new LRUCache(applicationContext.getProperties().getPropertyAsIntWithDefault("ChatRoomProxiesCacheSize", 5000), applicationContext.getProperties().getPropertyAsIntWithDefault("ChatRoomProxiesCacheExpiryTime", 60));
        this.recentGroupChatProxiesCache = new LRUCache(applicationContext.getProperties().getPropertyAsIntWithDefault("GroupChatProxiesCacheSize", 1000), applicationContext.getProperties().getPropertyAsIntWithDefault("GroupChatProxiesCacheExpiryTime", 60));
        this.metrics = new ObjectMetrics(applicationContext.getProperties().getPropertyAsIntWithDefault("RequestCounterInterval", 10));
        this.chatContentStore = new ChatContentStore(applicationContext);
        this.giftAllSemaphore = new Semaphore(applicationContext.getProperties().getPropertyAsIntWithDefault("OverallGiftAllPermits", 100), true);
        logSessions = applicationContext.getProperties().getPropertyAsIntWithDefault("LogSessions", 1) == 1;
        ChatUser.setLogSessions(logSessions);
        this.loadWeightage = applicationContext.getProperties().getPropertyAsIntWithDefault("LoadWeightage", 100);
    }

    public void setLoadWeightage(int weightage) {
        this.loadWeightage = weightage;
    }

    public int getLoadWeightage() {
        return this.loadWeightage;
    }

    public ScheduledExecutorService getDistributionService() {
        return this.distributionService;
    }

    public int getDistributionServiceQueueSize() {
        return ((ThreadPoolExecutor)this.distributionService).getQueue().size();
    }

    private ObjectPrx addToCacheAdapter(ObjectImpl object, Identity id) {
        if (SystemProperty.getBool(SystemPropertyEntities.Default.ICE_CONNECTION_STATS)) {
            if (log.isDebugEnabled()) {
                log.debug((java.lang.Object)("Enabling FusionStatsIceDispatchInterceptor for " + object));
            }
            FusionStatsIceDispatchInterceptor interceptor = new FusionStatsIceDispatchInterceptor((Object)object);
            return this.getCacheAdapter().add((Object)interceptor, id);
        }
        return this.getCacheAdapter().add((Object)object, id);
    }

    private ObjectAdapter getCacheAdapter() {
        return this.applicationContext.getCacheAdapter();
    }

    public RegistryPrx getRegistryPrx() {
        return this.applicationContext.getRegistryPrx();
    }

    public SessionCachePrx getSessionCachePrx() {
        return this.applicationContext.getSessionCachePrx();
    }

    private String getUniqueID() {
        return this.applicationContext.getUniqueID();
    }

    private Identity sessionIdentity(String sessionID) {
        return Util.stringToIdentity((String)("S" + sessionID));
    }

    private Identity userIdentity(String username) {
        return Util.stringToIdentity((String)("U" + username));
    }

    public UserPrx createUser(String username) throws ObjectExistsException, FusionException {
        UserPrx userPrx = null;
        UserRpcI existingUser = (UserRpcI)this.userObjects.get(username);
        if (existingUser != null) {
            log.warn((java.lang.Object)("Request to create a User object for the user '" + username + "' when an object already exists"));
            ObjectPrx basePrx = this.getCacheAdapter().createDirectProxy(this.userIdentity(username));
            userPrx = UserPrxHelper.uncheckedCast(basePrx);
            if (log.isDebugEnabled()) {
                log.debug((java.lang.Object)("Registering the (existing) User object [" + username + "] with the Registry"));
            }
            try {
                this.getRegistryPrx().registerUserObject(username, userPrx, this.getUniqueID());
            }
            catch (LocalException e) {
                log.error((java.lang.Object)("Object Cache " + ObjectCache.hostName + ": Unable to register the User object '" + username + "': Unable to communicate with the Registry"), (Throwable)e);
                FusionException fe = new FusionException();
                fe.message = "Unable to register the User object with the Registry";
                throw fe;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((java.lang.Object)("Creating the User object '" + username + "'"));
        }
        ChatUser chatUser = new ChatUser(this, username);
        try {
            chatUser.loadFromDB();
            if (log.isDebugEnabled()) {
                log.debug((java.lang.Object)("loaded user [" + username + "] from DB"));
            }
        }
        catch (Exception e) {
            log.error((java.lang.Object)("Unable to load the user [" + username + "] from the database"), (Throwable)e);
            throw new FusionException(e.getMessage());
        }
        UserRpcI user = new UserRpcI(this, chatUser);
        ObjectPrx basePrx = null;
        try {
            basePrx = this.addToCacheAdapter(user, this.userIdentity(username));
        }
        catch (Exception e) {
            log.error((java.lang.Object)("Object Cache " + ObjectCache.hostName + ": Ice.AlreadyRegisteredException when adding a new User object '" + username + "' to the object adapter"));
            throw new FusionException("Ice.AlreadyRegisteredException when adding a new User object '" + username + "' to the object adapter");
        }
        userPrx = UserPrxHelper.uncheckedCast(basePrx);
        if (log.isDebugEnabled()) {
            log.debug((java.lang.Object)("Registering the User object [" + username + "] with the Registry"));
        }
        try {
            this.getRegistryPrx().registerUserObject(username, userPrx, this.getUniqueID());
        }
        catch (LocalException e) {
            log.error((java.lang.Object)("Object Cache " + ObjectCache.hostName + ": Unable to register the User object '" + username + "': Unable to communicate with the Registry"));
            throw new FusionException("Unable to communicate with the Registry");
        }
        this.userObjects.put(username, user);
        this.metrics.userAdded(this.userObjects.size());
        this.metrics.request();
        return userPrx;
    }

    public SessionPrx createSession(String sessionID, ChatSession session) {
        SessionRpcI newSession = new SessionRpcI(this, session);
        ObjectPrx basePrx = this.addToCacheAdapter(newSession, this.sessionIdentity(sessionID));
        SessionPrx sessionPrx = SessionPrxHelper.uncheckedCast(basePrx);
        this.metrics.sessionAdded();
        return sessionPrx;
    }

    public ChatRoomPrx createRoom(String name) throws ObjectExistsException, FusionException {
        ChatRoomPrx chatRoomPrx;
        ChatRoomRpcI chatRoom;
        ChatRoomData chatRoomData;
        if (this.chatRoomObjects.containsKey(name = name.toLowerCase())) {
            throw new ObjectExistsException();
        }
        Identity identity = Util.stringToIdentity((String)("C" + name));
        try {
            if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHATROOM_DAO)) {
                chatRoomData = DAOFactory.getInstance().getChatRoomDAO().getChatRoom(name);
            } else {
                Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                chatRoomData = messageEJB.getChatRoom(name);
            }
            if (chatRoomData == null) {
                throw new FusionException("No such room [" + name + "]");
            }
            GroupData groupData = null;
            if (chatRoomData.groupID != null) {
                if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_GROUP_DAO)) {
                    groupData = DAOFactory.getInstance().getGroupDAO().getGroup(chatRoomData.groupID);
                } else {
                    User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                    groupData = userEJB.getGroup(chatRoomData.groupID);
                }
            }
            ChatRoom room = SystemPropertyEntities.Temp.Cache.se454ChatroomSystemMessageSendMimeTypesEnabled.getValue() != false ? new ChatRoom(this, chatRoomData, groupData) : new ChatRoomPreSE454(this, chatRoomData, groupData);
            chatRoom = new ChatRoomRpcI(room);
            chatRoomPrx = ChatRoomPrxHelper.uncheckedCast(this.getCacheAdapter().add((Object)chatRoom, identity));
        }
        catch (AlreadyRegisteredException e) {
            throw new ObjectExistsException();
        }
        catch (FusionException e) {
            throw e;
        }
        catch (Exception e) {
            log.error((java.lang.Object)("Unable to create chat room object [" + name + "] - " + e.getMessage()));
            throw new ExceptionWithDiagnosticCode("Internal server error. Please try again later", e, name);
        }
        try {
            this.getRegistryPrx().registerChatRoomObject(name, chatRoomPrx);
        }
        catch (ObjectExistsException e) {
            this.getCacheAdapter().remove(identity);
            throw e;
        }
        catch (LocalException e) {
            log.error((java.lang.Object)("Unable to register the Chat Room object [" + name + "]"), (Throwable)e);
            this.getCacheAdapter().remove(identity);
            throw new FusionException("Internal server error (Unable to communicate with Registry)");
        }
        if (chatRoomData.botID != null) {
            try {
                BotData botData = null;
                if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_BOT_DAO)) {
                    botData = DAOFactory.getInstance().getBotDAO().getBot(chatRoomData.botID);
                } else {
                    Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                    botData = messageEJB.getBot(chatRoomData.botID);
                }
                if (botData != null) {
                    chatRoom.startBot(chatRoomData.name, botData.getCommandName());
                }
            }
            catch (Exception e) {
                log.warn((java.lang.Object)("Unable to start bot in chat room [" + name + "]"), (Throwable)e);
                this.getRegistryPrx().deregisterChatRoomObject(name);
                this.getCacheAdapter().remove(identity);
                throw new FusionException("Internal server error (Unable to start attached bot)");
            }
        }
        this.chatRoomObjects.put(name, chatRoom);
        this.metrics.request();
        return chatRoomPrx;
    }

    public GroupChatPrx createGroupChatObject(String id, String creator, String privateChatParticipantIce, String[] otherPartyList) throws FusionException, ObjectExistsException {
        String privateChatParticipant = privateChatParticipantIce.equals("\u0000") ? null : privateChatParticipantIce;
        for (String p : otherPartyList) {
            if (creator.equals(p)) {
                throw new FusionException("You cannot invite yourself");
            }
            if (privateChatParticipant == null || !privateChatParticipant.equals(p)) continue;
            throw new FusionException("You are already chatting with " + privateChatParticipant);
        }
        GroupChatPair groupChatPair = this.createGroupChatObjectCore(id, creator);
        try {
            groupChatPair.local.addInitialParticipants(creator, privateChatParticipant, otherPartyList);
        }
        catch (FusionException e) {
            this.purgeGroupChat(id);
            throw e;
        }
        MessageSwitchboardDispatcher.getInstance().onCreateGroupChat(this.applicationContext, groupChatPair.local.toChatDefinition(), creator, privateChatParticipant, groupChatPair.remote, groupChatPair.local.getCreatorUserID());
        return groupChatPair.remote;
    }

    private GroupChatPair createGroupChatObjectCore(String id, String creator) throws FusionException, ObjectExistsException {
        GroupChatPrx groupChatPrx = null;
        GroupChatRpcI existingGroupChat = (GroupChatRpcI)this.groupChatObjects.get(id);
        GroupChatRpcI groupChat = null;
        if (existingGroupChat != null) {
            log.error((java.lang.Object)("ObjectCache " + ObjectCache.hostName + ": Received request to create the GroupChat object '" + id + "' that already exists"));
            throw new ObjectExistsException();
        }
        log.debug((java.lang.Object)("Creating the GroupChat object '" + id + "'"));
        ChatGroup group = new ChatGroup(this, id);
        groupChat = new GroupChatRpcI(group);
        ObjectPrx basePrx = this.addToCacheAdapter(groupChat, Util.stringToIdentity((String)("G" + id)));
        groupChatPrx = GroupChatPrxHelper.uncheckedCast(basePrx);
        groupChat.setGroupChatPrx(groupChatPrx);
        log.debug((java.lang.Object)("Registering the GroupChat object '" + id + "' with the Registry"));
        try {
            this.getRegistryPrx().registerGroupChatObject(id, groupChatPrx);
        }
        catch (LocalException e) {
            log.error((java.lang.Object)("Object Cache " + ObjectCache.hostName + ": Unable to register the GroupChat object '" + id + "': Unable to communicate with the Registry"));
            throw new FusionException("Unable to communicate with the Registry");
        }
        this.groupChatObjects.put(id, groupChat);
        this.metrics.groupAdded(this.groupChatObjects.size());
        this.metrics.request();
        GroupChatPair pair = new GroupChatPair();
        pair.remote = groupChatPrx;
        pair.local = groupChat;
        return pair;
    }

    public UserPrx findUserPrx(String username) throws FusionException {
        UserPrx userPrx = this.recentUserProxiesCache.get(username);
        if (userPrx != null) {
            try {
                userPrx.ice_ping();
                return userPrx;
            }
            catch (Exception e) {
                this.recentUserProxiesCache.remove(username);
            }
        }
        try {
            userPrx = this.getRegistryPrx().findUserObject(username);
            this.recentUserProxiesCache.put(username, userPrx);
            return userPrx;
        }
        catch (ObjectNotFoundException e) {
            throw new UserNotOnlineException(username);
        }
        catch (Exception e) {
            log.warn((java.lang.Object)"Exception in findUserPrx()", (Throwable)e);
            throw new InternalServerErrorException(e, "findUserPrx:" + username);
        }
    }

    public UserPrx makeUserPrx(String username) {
        return UserPrxHelper.uncheckedCast(this.applicationContext.getCacheAdapter().createProxy(this.userIdentity(username)));
    }

    public UserPrx findUserPrxFromRegistry(String fusionUsername) throws ObjectNotFoundException {
        return this.getRegistryPrx().findUserObject(fusionUsername);
    }

    public ChatRoomPrx findChatRoomPrx(String name) throws FusionException {
        ChatRoomPrx chatRoomPrx = this.recentChatRoomProxiesCache.get(name);
        if (chatRoomPrx != null) {
            try {
                chatRoomPrx.ice_ping();
                return chatRoomPrx;
            }
            catch (Exception e) {
                this.recentChatRoomProxiesCache.remove(name);
            }
        }
        try {
            chatRoomPrx = this.getRegistryPrx().findChatRoomObject(name);
            this.recentChatRoomProxiesCache.put(name, chatRoomPrx);
            return chatRoomPrx;
        }
        catch (ObjectNotFoundException e) {
            throw new FusionException("The chat room '" + name + "' could not be found");
        }
        catch (Exception e) {
            log.warn((java.lang.Object)"Exception in findChatRoomPrx()", (Throwable)e);
            throw new InternalServerErrorException(e, name);
        }
    }

    public GroupChatPrx findGroupChatPrx(String groupChatID) throws FusionException {
        GroupChatPrx groupChat;
        try {
            groupChat = this.findGroupChatPrxInner(groupChatID);
        }
        catch (Exception e) {
            if (!SystemProperty.getBool(SystemPropertyEntities.PersistentGroupChatSettings.ENABLED)) {
                if (e instanceof FusionException) {
                    throw (FusionException)((java.lang.Object)e);
                }
                throw new FusionException("In findGroupChatPrx: " + e.getMessage());
            }
            try {
                ChatDefinition groupChatDef = new ChatDefinition(groupChatID);
                RedisChatSyncStore store = new RedisChatSyncStore(ChatSyncStore.StorePrimacy.SLAVE);
                ChatSyncStore[] stores = new ChatSyncStore[]{store};
                groupChatDef.retrieve(stores);
                groupChat = this.restoreGroupChatObject(groupChatID, groupChatDef.getGroupOwner(), groupChatDef.getParticipantUsernames());
            }
            catch (ChatDefinition.ChatDefinitionNotFoundException cdnfe) {
                log.info((java.lang.Object)("Group chat " + groupChatID + " unrestorable, has expired from storage"));
                throw cdnfe;
            }
            catch (FusionException fe) {
                log.error((java.lang.Object)("Exception restoring group chat id=" + groupChatID + " e=" + (java.lang.Object)((java.lang.Object)fe)), (Throwable)((java.lang.Object)fe));
                throw fe;
            }
            catch (Exception e2) {
                log.error((java.lang.Object)("Exception restoring group chat id=" + groupChatID + " e=" + e2), (Throwable)e2);
                throw new FusionException(e2.getMessage());
            }
        }
        return groupChat;
    }

    private GroupChatPrx findGroupChatPrxInner(String id) throws FusionException {
        GroupChatPrx groupChatPrx = this.recentGroupChatProxiesCache.get(id);
        if (groupChatPrx != null) {
            try {
                groupChatPrx.ice_ping();
                return groupChatPrx;
            }
            catch (Exception e) {
                this.recentGroupChatProxiesCache.remove(id);
            }
        }
        try {
            groupChatPrx = this.getRegistryPrx().findGroupChatObject(id);
            this.recentGroupChatProxiesCache.put(id, groupChatPrx);
            return groupChatPrx;
        }
        catch (ObjectNotFoundException e) {
            throw new GroupChatNoLongerActiveException(id);
        }
        catch (Exception e) {
            log.warn((java.lang.Object)"Exception in findGroupChatPrx()", (Throwable)e);
            throw new InternalServerErrorException(e, id);
        }
    }

    public void purgeUser(String username) {
        UserRpcI user;
        if (log.isDebugEnabled()) {
            log.debug((java.lang.Object)("Purging the User object '" + username + "'"));
        }
        if ((user = (UserRpcI)this.userObjects.get(username)) == null) {
            log.warn((java.lang.Object)("Unable to purge the User object '" + username + "': The user was not found in the cache"));
            return;
        }
        user.prepareForPurge();
        this.userObjects.remove(username);
        try {
            this.getRegistryPrx().deregisterUserObject(username, this.getUniqueID());
        }
        catch (LocalException e) {
            log.warn((java.lang.Object)("Unable to deregister the User object '" + username + "': Unable to communicate with the Registry"));
        }
        try {
            this.getCacheAdapter().remove(this.userIdentity(username));
        }
        catch (NotRegisteredException e) {
            log.warn((java.lang.Object)("Caught Ice.NotRegisteredException when attempting to remove the User object '" + username + "' from the cache adapter"));
        }
    }

    public void purgeRoom(String name) {
        if (log.isDebugEnabled()) {
            log.debug((java.lang.Object)("Purging the chat room [" + name + "]"));
        }
        log.info((java.lang.Object)("Purging the chat room [" + name + "]"));
        ChatRoomRpcI chatRoom = (ChatRoomRpcI)this.chatRoomObjects.remove(name);
        if (chatRoom == null) {
            log.warn((java.lang.Object)("Unable to purge the chat room object '" + name + "': The chat room was not found in the cache"));
        }
        try {
            this.getRegistryPrx().deregisterChatRoomObject(name);
        }
        catch (LocalException e) {
            log.warn((java.lang.Object)("Unable to deregister the chat room object '" + name + "': Unable to communicate with the Registry"));
        }
        try {
            this.getCacheAdapter().remove(Util.stringToIdentity((String)("C" + name)));
        }
        catch (NotRegisteredException e) {
            log.warn((java.lang.Object)("Caught Ice.NotRegisteredException when attempting to remove the chat room object '" + name + "' from the cache adapter"));
        }
    }

    public GroupChatPrx[] getGroupChats() throws FusionException {
        try {
            Collection gcis = this.groupChatObjects.values();
            ArrayList<GroupChatPrx> prxs = new ArrayList<GroupChatPrx>();
            for (GroupChatRpcI gci : gcis) {
                GroupChatPrx prx = this.findGroupChatPrxInner(gci.getId());
                prxs.add(prx);
            }
            return prxs.toArray(new GroupChatPrx[prxs.size()]);
        }
        catch (FusionException e) {
            log.error((java.lang.Object)("Exception getting group chats: e=" + (java.lang.Object)((java.lang.Object)e)), (Throwable)((java.lang.Object)e));
            throw e;
        }
        catch (Exception e) {
            log.error((java.lang.Object)("Exception getting group chats: e=" + e), (Throwable)e);
            throw new FusionException(e.getMessage());
        }
    }

    public void purgeGroupChat(String id) {
        GroupChatRpcI groupChat;
        if (log.isDebugEnabled()) {
            log.debug((java.lang.Object)("Purging the GroupChat object '" + id + "'"));
        }
        if ((groupChat = (GroupChatRpcI)this.groupChatObjects.remove(id)) == null) {
            log.warn((java.lang.Object)("Unable to purge the GroupChat object '" + id + "': The group chat was not found in the cache"));
            return;
        }
        try {
            this.getRegistryPrx().deregisterGroupChatObject(id);
        }
        catch (LocalException e) {
            log.warn((java.lang.Object)("Unable to deregister the GroupChat object '" + id + "': Unable to communicate with the Registry"));
        }
        try {
            this.getCacheAdapter().remove(Util.stringToIdentity((String)("G" + id)));
        }
        catch (NotRegisteredException e) {
            log.warn((java.lang.Object)("Caught Ice.NotRegisteredException when attempting to remove the GroupChat object '" + id + "' from the cache adapter"));
        }
        if (!MessageSwitchboardDispatcher.getInstance().isFeatureEnabled()) {
            groupChat.removeAllParticipants();
        }
    }

    public final SessionPrx findSessionPrx(String sessionID) {
        return this.makeSessionPrx(sessionID);
    }

    public final SessionPrx makeSessionPrx(String sessionID) {
        return SessionPrxHelper.uncheckedCast(this.applicationContext.getCacheAdapter().createProxy(this.sessionIdentity(sessionID)));
    }

    public void sendAlertMessageToAllUsers(String message, String title, short timeout) {
        for (UserRpcI user : this.userObjects.values()) {
            try {
                user.putAlertMessage(message, title, timeout);
            }
            catch (Exception e) {}
        }
    }

    private GroupChatPrx restoreGroupChatObject(String id, String creator, String[] participants) throws FusionException, ObjectExistsException {
        if (log.isDebugEnabled()) {
            log.debug((java.lang.Object)("Restoring group chat id=" + id + ", creator=" + creator));
        }
        GroupChatPair groupChatPair = this.createGroupChatObjectCore(id, creator);
        for (String participant : participants) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug((java.lang.Object)("Restoring group chat id=" + id + ": adding participant=" + participant));
                }
                groupChatPair.local.addUserToGroupChat(participant, participant.equals(creator), false);
            }
            catch (Exception ex) {
                log.warn((java.lang.Object)("restoreGroupChatObject: failed to add user=" + participant + " to restored group chat=" + id + ": ex=" + ex), (Throwable)ex);
            }
        }
        groupChatPair.local.setCreatorParticipant(creator);
        if (log.isDebugEnabled()) {
            log.debug((java.lang.Object)("Restored groupchat with id=" + id + " prx=" + groupChatPair.remote));
        }
        return groupChatPair.remote;
    }

    public Properties getProperties() {
        return this.applicationContext.getProperties();
    }

    public RequestCounter getRequestCounter() {
        return this.metrics.getRequestCounter();
    }

    public ChatSession createSession(ChatUser chatUser, String sessionID, int presence, int deviceType, int connectionType, int imType, int port, int remotePort, String remoteAddress, String mobileDevice, String userAgent, short clientVersion, String language, ConnectionPrx connectionProxy) {
        return ChatSession.create(this, chatUser, sessionID, presence, deviceType, connectionType, imType, port, remotePort, remoteAddress, mobileDevice, userAgent, clientVersion, language, connectionProxy);
    }

    public AuthenticationServicePrx getAuthenticationServiceProxy() {
        return this.authenticationServiceProxy;
    }

    public IcePrxFinder getIcePrxFinder() {
        return this.icePrxFinder;
    }

    public void removeSession(String sessionID, boolean userIsBeingPurged) {
        SessionRpcI session = (SessionRpcI)this.sessionObjects.remove(sessionID);
        if (session == null) {
            log.warn((java.lang.Object)("ChatSession " + sessionID + " is not held by this ObjectCache"));
            return;
        }
        this.getCacheAdapter().remove(this.sessionIdentity(sessionID));
    }

    public SessionPrx onSessionCreated(ChatSession session) {
        String sessionID = session.getSessionID();
        SessionRpcI newSession = new SessionRpcI(this, session);
        ObjectPrx basePrx = this.addToCacheAdapter(newSession, this.sessionIdentity(sessionID));
        SessionPrx sessionPrx = SessionPrxHelper.uncheckedCast(basePrx);
        this.sessionObjects.put(sessionID, newSession);
        this.metrics.sessionAdded();
        this.updateUserState(session.user);
        return sessionPrx;
    }

    public void onSessionRemoved(ChatUser user) {
        this.updateUserState(user);
        this.metrics.sessionRemoved();
    }

    public void removeUser(String username) {
        this.purgeUser(username);
    }

    public SessionPrx getSessionPrx(String sessionID) {
        return SessionPrxHelper.uncheckedCast(this.applicationContext.getCacheAdapter().createProxy(this.sessionIdentity(sessionID)));
    }

    public SessionPrx[] findSessionsPrx(String[] sessionIDs) {
        ArrayList<SessionPrx> sessionProxies = new ArrayList<SessionPrx>();
        for (String sessionID : sessionIDs) {
            SessionPrx sessionPrx = SessionPrxHelper.uncheckedCast(this.getCacheAdapter().createProxy(this.sessionIdentity(sessionID)));
            sessionProxies.add(sessionPrx);
        }
        return sessionProxies.toArray(new SessionPrx[0]);
    }

    public Credential[] getUserCredentials(int userID, byte[] types) throws FusionException {
        return this.authenticationServiceProxy.getCredentialsForTypes(userID, types);
    }

    private void updateUserState(ChatUser user) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MessageSwitchboardPrx getMessageSwitchboardPrx() throws FusionException {
        if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.STANDALONE_MESSAGE_SWITCHBOARD_ENABLED)) {
            MessageSwitchboardPrx msp = this.applicationContext.getRegistryPrx().getMessageSwitchboard();
            return msp;
        }
        if (this.localMessageSwitchboardPrx.get() == null) {
            ChatObjectManager chatObjectManager = this;
            synchronized (chatObjectManager) {
                if (this.localMessageSwitchboardPrx.get() == null) {
                    MessageSwitchboardI msi = new MessageSwitchboardI();
                    ObjectPrx basePrx = this.addToCacheAdapter(msi, Util.stringToIdentity((String)("msp_" + this.iceID)));
                    MessageSwitchboardPrx msp = MessageSwitchboardPrxHelper.uncheckedCast(basePrx);
                    this.localMessageSwitchboardPrx.set(msp);
                }
            }
        }
        return this.localMessageSwitchboardPrx.get();
    }

    public GroupChatPrx findGroupChatPrxFromRegistry(String groupChatID) throws ObjectNotFoundException {
        return this.applicationContext.getRegistryPrx().findGroupChatObject(groupChatID);
    }

    public ChatRoomPrx findChatRoomPrxFromRegistry(String destination) throws ObjectNotFoundException {
        return this.applicationContext.getRegistryPrx().findChatRoomObject(destination);
    }

    public void onGroupSessionAdded() {
        this.metrics.groupSessionAdded();
    }

    public void onGroupSessionRemoved() {
        this.metrics.groupSessionRemoved();
    }

    public void onRoomSessionAdded() {
        this.metrics.roomSessionAdded();
    }

    public void onRoomSessionRemoved() {
        this.metrics.roomSessionRemoved();
    }

    public void logMessage(MessageToLog.TypeEnum logType, int localUserCountryID, String source, String destination, int numRecipients, String messageText) {
        this.applicationContext.getMessageLoggerPrx().logMessage(logType.value(), localUserCountryID, source, destination, numRecipients, messageText);
    }

    public boolean isLogMessagesToFile() {
        return ObjectCache.logMessagesToFile;
    }

    public boolean isLogMessagesToDB() {
        return ObjectCache.logMessagesToDB;
    }

    public long getChatRoomIdleTimeout() {
        return this.idleRoomPurger.getIdleTimeout();
    }

    public Semaphore getGiftAllSemaphore() {
        return this.giftAllSemaphore;
    }

    public ObjectCacheContext getApplicationContext() {
        return this.applicationContext;
    }

    public ChatContentStore getFileStore() {
        return this.chatContentStore;
    }

    private class IdleGroupChatPurger
    extends ObjectPurger {
        public IdleGroupChatPurger(Properties properties) {
            int idleTimeout = properties.getPropertyAsIntWithDefault("GroupChatIdleTimeout", 1800) * 1000;
            int purgeInterval = properties.getPropertyAsIntWithDefault("GroupChatPurgerInterval", 60) * 1000;
            this.start(idleTimeout, purgeInterval);
        }

        public void run() {
            if (log.isDebugEnabled()) {
                log.debug((java.lang.Object)("Running IdleAndRemovedGroupChatPurger, examining " + ChatObjectManager.this.groupChatObjects.size() + " group chat objects"));
            }
            try {
                LinkedList groupChatIdList = new LinkedList(ChatObjectManager.this.groupChatObjects.keySet());
                for (String groupChatId : groupChatIdList) {
                    GroupChatRpcI groupChat = (GroupChatRpcI)ChatObjectManager.this.groupChatObjects.get(groupChatId);
                    if (groupChat == null) continue;
                    if (groupChat.isMarkedForRemoval()) {
                        if (log.isDebugEnabled()) {
                            log.debug((java.lang.Object)("purging group chat [" + groupChatId + "] marked for removal"));
                        }
                        ChatObjectManager.this.purgeGroupChat(groupChatId);
                        continue;
                    }
                    if (this.idleTimeout == 0 || groupChat.getTimeLastMessageSent() >= System.currentTimeMillis() - (long)this.idleTimeout) continue;
                    if (SystemProperty.getBool(SystemPropertyEntities.PersistentGroupChatSettings.GROUP_CHAT_IDLE_NOTIFICATION_ENABLED)) {
                        groupChat.sendAdminMessageToParticipants(SystemProperty.get(SystemPropertyEntities.PersistentGroupChatSettings.GROUP_CHAT_IDLE_TIMEOUT_MESSAGE), null);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug((java.lang.Object)("purging timed out group chat [" + groupChatId + "]"));
                    }
                    ChatObjectManager.this.purgeGroupChat(groupChatId);
                }
                groupChatIdList.clear();
            }
            catch (Exception e) {
                log.error((java.lang.Object)"Failed to purge group chat objects", (Throwable)e);
            }
        }
    }

    private class IdleRoomPurger
    extends ObjectPurger {
        public IdleRoomPurger(Properties properties) {
            int idleTimeout = properties.getPropertyAsIntWithDefault("ChatRoomIdleTimeout", 1800) * 1000;
            int purgeInterval = properties.getPropertyAsIntWithDefault("ChatRoomPurgerInterval", 60) * 1000;
            this.start(idleTimeout, purgeInterval);
        }

        public void run() {
            if (log.isDebugEnabled()) {
                log.debug((java.lang.Object)("Running IdleChatRoomPurger, examining " + ChatObjectManager.this.chatRoomObjects.size() + " chat room objects"));
            }
            try {
                for (String chatRoomName : ChatObjectManager.this.chatRoomObjects.keySet()) {
                    ChatRoomRpcI chatRoom = (ChatRoomRpcI)ChatObjectManager.this.chatRoomObjects.get(chatRoomName);
                    if (chatRoom == null || !chatRoom.isIdle()) continue;
                    if (log.isDebugEnabled()) {
                        log.debug((java.lang.Object)("Running IdleChatRoomPurger, purging:" + chatRoomName));
                    }
                    chatRoom.prepareForPurge();
                    ChatObjectManager.this.purgeRoom(chatRoomName);
                }
            }
            catch (Exception e) {
                log.error((java.lang.Object)"Failed to purge chat rooms", (Throwable)e);
            }
        }
    }

    private class IdleSessionPurger
    extends ObjectPurger {
        public IdleSessionPurger(Properties properties) {
            int idleTimeout = properties.getPropertyAsIntWithDefault("SessionIdleTimeout", 3600) * 1000;
            int purgeInterval = properties.getPropertyAsIntWithDefault("IdleSessionPurgeInterval", 600) * 1000;
            this.start(idleTimeout, purgeInterval);
        }

        public void run() {
            for (UserRpcI user : ChatObjectManager.this.userObjects.values()) {
                if (log.isDebugEnabled()) {
                    log.debug((java.lang.Object)("Purging expired sessions for the user '" + user.getUserData().username + "'"));
                }
                user.purgeExpiredSessions();
            }
        }
    }

    private abstract class ObjectPurger
    extends TimerTask {
        protected int idleTimeout;
        protected int purgeInterval;

        private ObjectPurger() {
        }

        public int getIdleTimeout() {
            return this.idleTimeout;
        }

        public int getPurgeInterval() {
            return this.purgeInterval;
        }

        protected void start(int idleTimeout, int purgeInterval) {
            this.idleTimeout = idleTimeout;
            this.purgeInterval = purgeInterval;
            Timer timer = new Timer(true);
            timer.schedule((TimerTask)this, idleTimeout, (long)purgeInterval);
        }
    }

    private class ObjectMetrics {
        private int maxUserObjects = 0;
        private int maxChatRoomObjects = 0;
        private int maxGroupChatObjects = 0;
        private int maxSessionObjects = 0;
        private AtomicInteger numSessionObjects = new AtomicInteger(0);
        private AtomicInteger numSessionsInChatrooms = new AtomicInteger(0);
        private AtomicInteger numSessionsInGroupChats = new AtomicInteger(0);
        private RequestCounter requestCounter;

        public ObjectMetrics(long requestCounterInterval) {
            this.requestCounter = new RequestCounter(requestCounterInterval);
        }

        public void userAdded(int size) {
            if (size > this.maxUserObjects) {
                this.maxUserObjects = size;
            }
        }

        public void groupAdded(int size) {
            if (size > this.maxGroupChatObjects) {
                this.maxGroupChatObjects = size;
            }
        }

        public void sessionAdded() {
            this.maxSessionObjects = Math.max(this.numSessionObjects.incrementAndGet(), this.maxSessionObjects);
        }

        public void sessionRemoved() {
            this.numSessionObjects.decrementAndGet();
        }

        public void request() {
            this.requestCounter.add();
        }

        public RequestCounter getRequestCounter() {
            return this.requestCounter;
        }

        public void groupSessionRemoved() {
            this.numSessionsInGroupChats.decrementAndGet();
        }

        public void groupSessionAdded() {
            this.numSessionsInGroupChats.incrementAndGet();
        }

        public void roomSessionRemoved() {
            this.numSessionsInChatrooms.decrementAndGet();
        }

        public void roomSessionAdded() {
            this.numSessionsInChatrooms.incrementAndGet();
        }

        public void getStats(ObjectCacheStats stats) {
            stats.numUserObjects = ChatObjectManager.this.userObjects.size();
            stats.maxUserObjects = this.maxUserObjects;
            stats.numOnlineUserObjects = stats.numUserObjects;
            stats.maxOnlineUserObjects = stats.maxUserObjects;
            stats.eldestUserObject = 0L;
            stats.numSessionObjects = this.numSessionObjects.get();
            stats.numSessionsInGroupChats = this.numSessionsInGroupChats.get();
            stats.numSessionsInChatrooms = this.numSessionsInChatrooms.get();
            stats.maxSessionObjects = this.maxSessionObjects;
            stats.numChatRoomObjects = ChatObjectManager.this.chatRoomObjects.size();
            if (stats.numChatRoomObjects > this.maxChatRoomObjects) {
                this.maxChatRoomObjects = stats.numChatRoomObjects;
            }
            stats.maxChatRoomObjects = this.maxChatRoomObjects;
            stats.numGroupChatObjects = ChatObjectManager.this.groupChatObjects.size();
            stats.maxGroupChatObjects = this.maxGroupChatObjects;
            stats.distributionServiceQueueSize = ChatObjectManager.this.getDistributionServiceQueueSize();
            stats.stadiumDistributionServiceQueueSize = 0;
            stats.requestsPerSecond = this.requestCounter.getRequestsPerSecond();
            stats.maxRequestsPerSecond = this.requestCounter.getMaxRequestsPerSecond();
            stats.weightage = ChatObjectManager.this.getLoadWeightage();
        }
    }

    private class GroupChatPair {
        public GroupChatPrx remote;
        public GroupChatRpcI local;

        private GroupChatPair() {
        }
    }
}

