/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.Current
 *  Ice.ObjectNotExistException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.messageswitchboard;

import Ice.Communicator;
import Ice.Current;
import Ice.ObjectNotExistException;
import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatRenamer;
import com.projectgoth.fusion.chatsync.ChatSyncRetrievalExecutor;
import com.projectgoth.fusion.chatsync.ChatSyncStats;
import com.projectgoth.fusion.chatsync.ChatSyncStorageExecutor;
import com.projectgoth.fusion.chatsync.ClosedChatNotificationPusher;
import com.projectgoth.fusion.chatsync.CurrentChatListUpdater;
import com.projectgoth.fusion.chatsync.GroupChatCreationHandler;
import com.projectgoth.fusion.chatsync.LastNChatMessagesPusher;
import com.projectgoth.fusion.chatsync.LatestMessagesDigestPusher;
import com.projectgoth.fusion.chatsync.MessageSendHandler;
import com.projectgoth.fusion.chatsync.MessageSendLiveSyncer;
import com.projectgoth.fusion.chatsync.MessageStatusEventsRetriever;
import com.projectgoth.fusion.chatsync.UserMissingChats;
import com.projectgoth.fusion.chatsync.UserMissingChatsPusher;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.objectcache.Emote;
import com.projectgoth.fusion.slice.ChatDefinitionIce;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._MessageSwitchboardDisp;
import java.util.HashSet;
import org.apache.log4j.Logger;

public class MessageSwitchboardI
extends _MessageSwitchboardDisp {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(MessageSwitchboardI.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private IcePrxFinder icePrxFinder;
    private Communicator communicator;

    public void initialize(Communicator communicator) throws Exception {
        this.communicator = communicator;
    }

    void shutdown() {
    }

    public void setIcePrxFinder(IcePrxFinder icePrxFinder) {
        this.icePrxFinder = icePrxFinder;
    }

    public boolean isUserChatSyncEnabled(ConnectionPrx cxn, String username, int userID, Current __current) throws FusionException {
        return false != SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.CHAT_SYNC_ENABLED);
    }

    public ChatDefinitionIce[] getChats(int userID, int chatListVersion, int limit, byte chatType, Current __current) throws FusionException {
        return this.getChats2(userID, chatListVersion, limit, chatType, null);
    }

    public ChatDefinitionIce[] getChats2(int userID, int chatListVersion, int limit, byte chatType, ConnectionPrx connection, Current __current) throws FusionException {
        if (!SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.J2ME_CHAT_MANAGER_ENABLED)) {
            log.debug("J2ME_CHAT_MANAGER_ENABLED==false so returning null from getChats");
            return null;
        }
        int maxPerMin = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.MAX_GET_CHAT_REQUESTS_PER_MINUTE);
        if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.CHAT_SYNC_GLOBAL_RATE_LIMITS.toString(), "maxGetChatRequestsPerMin", (long)maxPerMin, 60000L)) {
            log.debug("GET_CHATS global rate limit exceeeded so returning null from getChats");
            return null;
        }
        int maxPerDay = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.MAX_GET_CHATS_REQUESTS_PER_USER_PER_DAY);
        if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.CHAT_SYNC_MAX_GET_CHATS_PER_USER_PER_DAY.toString(), Integer.toString(userID), (long)maxPerDay, 86400000L)) {
            if (log.isDebugEnabled()) {
                log.debug("GET_CHATS rate limit exceeded for userID=" + userID + " so returning null from getChats");
            }
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Entering MessageSwitchboardI.getChats with userID=" + userID + " chatType=" + chatType + " limit=" + limit + " chatListVersion=" + chatListVersion);
        }
        Integer iLimit = limit == Integer.MIN_VALUE ? null : Integer.valueOf(limit);
        Byte bChatType = chatType == -128 ? null : Byte.valueOf(chatType);
        UserMissingChats missingChats = new UserMissingChats(userID, chatListVersion, iLimit, bChatType, connection);
        UserMissingChats missingChatsResult = (UserMissingChats)ChatSyncRetrievalExecutor.getInstance().scheduleEntityRetrievalAndWait(missingChats);
        if (missingChatsResult != null) {
            ChatDefinition[] mc = missingChatsResult.getMissingChats();
            ChatDefinitionIce[] results = new ChatDefinitionIce[mc.length];
            for (int i = 0; i < mc.length; ++i) {
                results[i] = mc[i].toIceObject();
            }
            return results;
        }
        return new ChatDefinitionIce[0];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onGetChats(ConnectionPrx cxn, int userID, int chatListVersion, int limit, byte chatType, short transactionId, String parentUsername, Current __current) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug("onGetChats for user=" + userID);
        }
        long startCpu = ChatSyncStats.getInstance().getCpuTime();
        try {
            ChatSyncStats.getInstance().incrementTotalGetChatsReceived();
            Byte bChatType = chatType == -128 ? null : Byte.valueOf(chatType);
            Integer iLimit = limit == Integer.MIN_VALUE ? null : Integer.valueOf(limit);
            UserMissingChatsPusher userMissingChats = new UserMissingChatsPusher(userID, cxn, chatListVersion, iLimit, bChatType, transactionId, parentUsername);
            ChatSyncRetrievalExecutor.getInstance().scheduleEntityRetrieval(userMissingChats);
            Object var15_13 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_RETRIEVAL);
        }
        catch (Throwable throwable) {
            Object var15_14 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_RETRIEVAL);
            throw throwable;
        }
    }

    public void getAndPushMessages(String username, byte chatType, String suppliedChatID, long oldestMessageTimestamp, long newestMessageTimestamp, int limit, ConnectionPrx cxn, Current __current) throws FusionException {
        this.getAndPushMessages2(username, chatType, suppliedChatID, oldestMessageTimestamp, newestMessageTimestamp, limit, cxn, cxn.getDeviceTypeAsInt(), cxn.getClientVersion(), (short)Short.MIN_VALUE);
    }

    public void getAndPushMessages2(String username, byte chatType, String suppliedChatID, long oldestMessageTimestamp, long newestMessageTimestamp, int limit, ConnectionPrx cxn, int deviceType, short clientVersion, short fusionPktTransactionId, Current __current) throws FusionException {
        Integer iLimit;
        int maxPerDay = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.MAX_GET_MESSAGES_REQUESTS_PER_USER_PER_DAY);
        if (log.isDebugEnabled()) {
            log.debug("Rate limiting getAndPushMessages for user=" + username + " to " + maxPerDay + " per day");
        }
        if (!MemCachedRateLimiter.bypassRateLimit(username) && !MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.CHAT_SYNC_MAX_GET_MESSAGES_PER_USER_PER_DAY.toString(), username, (long)maxPerDay, 86400000L)) {
            if (log.isDebugEnabled()) {
                log.debug("getAndPushMessages rate limit exceeded for user=" + username);
            }
            return;
        }
        Long oldest = oldestMessageTimestamp == Long.MIN_VALUE ? null : Long.valueOf(oldestMessageTimestamp);
        Long newest = newestMessageTimestamp == Long.MIN_VALUE ? null : Long.valueOf(newestMessageTimestamp);
        Integer n = iLimit = limit == Integer.MIN_VALUE ? null : Integer.valueOf(limit);
        if (log.isDebugEnabled()) {
            log.debug("getAndPushMessages: invoking LastNChatMessagesPusher for user=" + username);
        }
        ChatDefinition chatKey = new ChatDefinition(suppliedChatID, chatType, username);
        Short sTxnId = fusionPktTransactionId == Short.MIN_VALUE ? null : Short.valueOf(fusionPktTransactionId);
        LastNChatMessagesPusher cm = new LastNChatMessagesPusher(chatKey, chatType, oldest, newest, iLimit, cxn.getSessionObject(), username, ClientType.fromValue(deviceType), clientVersion, sTxnId);
        ChatSyncRetrievalExecutor.getInstance().scheduleEntityRetrieval(cm);
    }

    public void onCreateGroupChat(ChatDefinitionIce cdiGroupChat, String creatorUsername, String privateChatPartnerUsername, GroupChatPrx groupChat, Current __current) throws FusionException {
        ChatDefinition parentPrivateChatID = privateChatPartnerUsername != null ? new ChatDefinition(privateChatPartnerUsername, (byte)MessageDestinationData.TypeEnum.INDIVIDUAL.value(), creatorUsername) : null;
        try {
            ChatDefinition cdGroupChat = new ChatDefinition(cdiGroupChat);
            GroupChatCreationHandler handler = new GroupChatCreationHandler(cdGroupChat, creatorUsername, privateChatPartnerUsername, groupChat, parentPrivateChatID);
            ChatSyncStorageExecutor.getInstance().scheduleStorage(handler);
        }
        catch (Exception e) {
            log.error("While constructing ChatDefinition:", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onJoinGroupChat(String username, int userID, String groupChatGUID, boolean debug, UserPrx userProxy, Current __current) throws FusionException {
        long startCpu = ChatSyncStats.getInstance().getCpuTime();
        try {
            if (log.isDebugEnabled() || debug) {
                log.info("onJoinGroupChat for " + username + ": creating groupChatDef");
            }
            ChatDefinition groupChatDef = new ChatDefinition(groupChatGUID, (byte)MessageDestinationData.TypeEnum.GROUP.value());
            if (log.isDebugEnabled() || debug) {
                log.info("onJoinGroupChat for " + username + ": calling updateChatList");
            }
            this.updateChatList(username, userID, groupChatDef, null, debug, userProxy);
            if (log.isDebugEnabled() || debug) {
                log.info("onJoinGroupChat for " + username + ": called updateChatList");
            }
            Object var11_9 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
        }
        catch (Throwable throwable) {
            Object var11_10 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onLeaveGroupChat(String username, int userID, String groupChatGUID, UserPrx userProxy, Current __current) throws FusionException {
        block9: {
            try {
                long startCpu = ChatSyncStats.getInstance().getCpuTime();
                try {
                    block8: {
                        if (userProxy != null) {
                            try {
                                SessionPrx[] sessionPrxs = userProxy.getSessions();
                                this.pushClosedChatNotification(userID, username, (byte)MessageDestinationData.TypeEnum.GROUP.value(), groupChatGUID, (short)0, sessionPrxs);
                            }
                            catch (Exception e) {
                                if (!log.isDebugEnabled()) break block8;
                                log.debug("Exception pushing closed chat notification to sessions of user=" + username + ": " + e, e);
                            }
                        }
                    }
                    ChatDefinition groupChatDef = new ChatDefinition(groupChatGUID, (byte)MessageDestinationData.TypeEnum.GROUP.value());
                    this.updateChatList(username, userID, null, groupChatDef, userProxy);
                    if (log.isDebugEnabled()) {
                        log.debug("Group guid=" + groupChatGUID + " removed from chat list of userID=" + userID);
                    }
                    Object var10_10 = null;
                    ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
                }
                catch (Throwable throwable) {
                    Object var10_11 = null;
                    ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
                    throw throwable;
                }
            }
            catch (ObjectNotExistException e) {
                if (!log.isDebugEnabled()) break block9;
                log.debug("MessageSwitchboardI.onLeaveGroupChat: UserPrx of user=" + username + "who has left group chat is already invalid");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onJoinChatRoom(String username, int userID, String chatRoomName, Current __current) throws FusionException {
        long startCpu = ChatSyncStats.getInstance().getCpuTime();
        try {
            ChatDefinition chatRoomDef = new ChatDefinition(chatRoomName, (byte)MessageDestinationData.TypeEnum.CHAT_ROOM.value());
            this.updateChatList(username, userID, chatRoomDef, null, null);
            Object var9_7 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
        }
        catch (Throwable throwable) {
            Object var9_8 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onLeaveChatRoom(String username, int userID, String chatRoomName, UserPrx userProxy, Current __current) throws FusionException {
        block5: {
            try {
                long startCpu = ChatSyncStats.getInstance().getCpuTime();
                try {
                    SessionPrx[] sessionPrxs = userProxy.getSessions();
                    this.pushClosedChatNotification(userID, username, (byte)MessageDestinationData.TypeEnum.CHAT_ROOM.value(), chatRoomName, (short)0, sessionPrxs);
                    ChatDefinition chatRoomDef = new ChatDefinition(chatRoomName, (byte)MessageDestinationData.TypeEnum.CHAT_ROOM.value());
                    this.updateChatList(username, userID, null, chatRoomDef, null);
                    if (log.isDebugEnabled()) {
                        log.debug("Chatroom=" + chatRoomName + " removed from chat list of userID=" + userID);
                    }
                    Object var11_10 = null;
                    ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
                }
                catch (Throwable throwable) {
                    Object var11_11 = null;
                    ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
                    throw throwable;
                }
            }
            catch (ObjectNotExistException e) {
                if (!log.isDebugEnabled()) break block5;
                log.debug("MessageSwitchboardI.onLeaveChatRoom: UserPrx of user=" + username + "who has left chatroom is already invalid");
            }
        }
    }

    private void pushClosedChatNotification(int userID, String username, byte chatType, String chatID, short txnID, SessionPrx[] sessions) throws FusionException {
        ClosedChatNotificationPusher pusher = new ClosedChatNotificationPusher(userID, username, chatType, chatID, txnID, sessions);
        ChatSyncRetrievalExecutor.getInstance().scheduleEntityRetrieval(pusher);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean onSendFusionMessageToIndividual(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String destinationUsername, String[] uniqueUsersPrivateChattedWith, int deviceType, short clientVersion, UserDataIce senderUserData, String recipientDisplayPicture, Current __current) throws FusionException {
        ClientType dt = ClientType.fromValue(deviceType);
        HashSet<String> hs = new HashSet<String>();
        for (String s : uniqueUsersPrivateChattedWith) {
            hs.add(s);
        }
        long startCpu = ChatSyncStats.getInstance().getCpuTime();
        try {
            this.onSendFusionMessage(currentSession, messageData, parentUser.getUserData(), parentUser);
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.MESSAGE_STORAGE);
            startCpu = ChatSyncStats.getInstance().getCpuTime();
            if (!hs.contains(destinationUsername)) {
                UserDataIce parentUserData = parentUser.getUserData();
                this.onCreatePrivateChat(parentUserData.userID, parentUserData.username, destinationUsername, deviceType, clientVersion, senderUserData, recipientDisplayPicture);
            }
            boolean bl = true;
            {
                Object var18_21 = null;
                ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
            }
            return bl;
        }
        catch (Throwable throwable) {
            try {
                Object var18_22 = null;
                ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
                throw throwable;
            }
            catch (Exception e) {
                log.error("Exception in onSendFusionMessageToIndividual for destinationUsername=" + destinationUsername, e);
                return false;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onSendFusionMessageToGroupChat(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String groupChatID, int deviceType, short clientVersion, Current __current) throws FusionException {
        long startCpu = ChatSyncStats.getInstance().getCpuTime();
        try {
            block4: {
                try {
                    if (messageData.contentType != MessageData.ContentTypeEnum.TEXT.value() || Emote.isEmote(messageData.messageText) || EmoteCommand.hasMessageVariables(messageData.messageText)) break block4;
                    this.onSendFusionMessage(currentSession, messageData, parentUser.getUserData(), parentUser, groupChatID);
                }
                catch (Exception e) {
                    log.error("Exception in onSendFusionMessageToGroupChat for groupChatID=" + groupChatID, e);
                    Object var12_10 = null;
                    ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.MESSAGE_STORAGE);
                }
            }
            Object var12_9 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.MESSAGE_STORAGE);
        }
        catch (Throwable throwable) {
            Object var12_11 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.MESSAGE_STORAGE);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onSendFusionMessageToChatRoom(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, String chatRoomName, int deviceType, short clientVersion, Current __current) throws FusionException {
        long startCpu = ChatSyncStats.getInstance().getCpuTime();
        try {
            block4: {
                try {
                    if (messageData.contentType != MessageData.ContentTypeEnum.TEXT.value() || Emote.isEmote(messageData.messageText) || EmoteCommand.hasMessageVariables(messageData.messageText)) break block4;
                    this.onSendFusionMessage(currentSession, messageData, parentUser.getUserData(), parentUser);
                }
                catch (Exception e) {
                    log.error("Exception in onSendFusionMessageToChatRoom for chatRoomName=" + chatRoomName, e);
                    Object var12_10 = null;
                    ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.MESSAGE_STORAGE);
                }
            }
            Object var12_9 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.MESSAGE_STORAGE);
        }
        catch (Throwable throwable) {
            Object var12_11 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.MESSAGE_STORAGE);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean onSendMessageToAllUsersInChat(SessionPrx currentSession, UserPrx parentUser, MessageDataIce messageData, UserDataIce senderUserData, Current __current) throws FusionException {
        long startCpu = ChatSyncStats.getInstance().getCpuTime();
        try {
            this.onSendFusionMessage(currentSession, messageData, senderUserData, parentUser);
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.MESSAGE_STORAGE);
            if (MessageData.isMessageToAnIndividual(messageData)) {
                String recipientDisplayPicture;
                startCpu = ChatSyncStats.getInstance().getCpuTime();
                String recipUsername = messageData.messageDestinations[0].destination;
                try {
                    User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                    recipientDisplayPicture = userEJB.getDisplayPicture(recipUsername);
                }
                catch (Exception e) {
                    throw new FusionException(e.getMessage());
                }
                if (!currentSession.privateChattedWith(recipUsername)) {
                    int deviceType = currentSession.getDeviceTypeAsInt();
                    short clientVersion = currentSession.getClientVersionIce();
                    this.onCreatePrivateChat(senderUserData.userID, senderUserData.username, recipUsername, deviceType, clientVersion, senderUserData, recipientDisplayPicture);
                }
            }
            boolean bl = true;
            Object var13_14 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
            return bl;
        }
        catch (Throwable throwable) {
            Object var13_15 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
            throw throwable;
        }
    }

    public void onCreatePrivateChat(int userID, String username, String otherUser, int deviceType, short clientVersion, UserDataIce senderUserData, String recipientDisplayPicture, Current __current) throws FusionException {
        long startCpu = ChatSyncStats.getInstance().getCpuTime();
        try {
            try {
                this.onCreatePrivateChatInner(userID, username, otherUser, senderUserData, recipientDisplayPicture);
            }
            catch (FusionException e) {
                throw e;
            }
            catch (Exception e) {
                log.error("onCreatePrivateChat:", e);
                throw new FusionException(e.getMessage());
            }
            Object var13_10 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
        }
        catch (Throwable throwable) {
            Object var13_11 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
            throw throwable;
        }
    }

    private void onCreatePrivateChatInner(int userID, String username, String otherUser, UserDataIce senderUserData, String recipientDisplayPicture) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("onCreatePrivateChat for user=" + username + " otherUser=" + otherUser);
        }
        User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        int recipientID = userEJB.getUserID(otherUser, null);
        try {
            log.debug("onCreatePrivateChat: constructing ChatDefinition");
            String[] participants = new String[]{username, otherUser};
            ChatDefinition def = new ChatDefinition(username, otherUser, participants, (byte)MessageDestinationData.TypeEnum.INDIVIDUAL.value(), null, null, null, null, recipientDisplayPicture, MessageType.FUSION.value());
            log.debug("onCreatePrivateChat: ChatDefinition constructed");
            ChatSyncStorageExecutor.getInstance().scheduleStorage(def);
            log.debug("onCreatePrivateChat: storage scheduled");
        }
        catch (Exception e) {
            log.error("While constructing ChatDefinition:" + e);
        }
        ChatDefinition chatKey = new ChatDefinition(otherUser, (byte)MessageDestinationData.TypeEnum.INDIVIDUAL.value(), username);
        this.updateChatList(username, userID, chatKey, null, null);
        this.updateChatList(otherUser, recipientID, chatKey, null, null);
    }

    private void updateChatList(String username, int userID, ChatDefinition addChatID, ChatDefinition removeChatID, UserPrx userProxy) throws FusionException {
        this.updateChatList(username, userID, addChatID, removeChatID, false, userProxy);
    }

    private void updateChatList(String username, int userID, ChatDefinition addChatID, ChatDefinition removeChatID, boolean debug, UserPrx userProxy) throws FusionException {
        if (!MemCachedRateLimiter.bypassRateLimit(username)) {
            int maxPerMin = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.MAX_CHAT_LIST_UPDATES_PER_MINUTE);
            if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.CHAT_SYNC_GLOBAL_RATE_LIMITS.toString(), "maxChatListUpdatesPerMin", (long)maxPerMin, 60000L)) {
                log.warn("MAX_CHAT_LIST_UPDATES_PER_MINUTE rate limit exceeded: could not update chat list for userID=" + userID);
                return;
            }
        }
        if (log.isDebugEnabled() || debug) {
            log.info("Creating and scheduling CurrentChatListUpdater for " + username);
        }
        CurrentChatListUpdater updater = new CurrentChatListUpdater(username, userID, addChatID, removeChatID, debug, userProxy);
        ChatSyncRetrievalExecutor.getInstance().scheduleEntityRetrieval(updater);
        if (log.isDebugEnabled() || debug) {
            log.info("Created and scheduled CurrentChatListUpdater for " + username);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onLeavePrivateChat(int userID, String username, String otherUser, int deviceType, short clientVersion, Current __current) throws FusionException {
        long startCpu = ChatSyncStats.getInstance().getCpuTime();
        try {
            ChatDefinition chatKey = new ChatDefinition(otherUser, (byte)MessageDestinationData.TypeEnum.INDIVIDUAL.value(), username);
            this.updateChatList(username, userID, null, chatKey, null);
            Object var11_9 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
        }
        catch (Throwable throwable) {
            Object var11_10 = null;
            ChatSyncStats.getInstance().addCpuTime(startCpu, ChatSyncStats.ChatSyncOpCategory.CHAT_STORAGE);
            throw throwable;
        }
    }

    public GroupChatPrx ensureGroupChatExists(SessionPrx currentSession, String groupChatID, Current __current) throws FusionException {
        return currentSession.findGroupChatObject(groupChatID);
    }

    public void onLogon(int userID, SessionPrx session, short transactionID, String parentUsername, Current __current) throws FusionException {
        if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.LATEST_MESSAGES_DIGEST_PACKET_SEND_ENABLED)) {
            if (log.isDebugEnabled()) {
                log.debug("onLogon: Scheduling LatestMessagesDigestPusher for userID=" + userID);
            }
            LatestMessagesDigestPusher pusher = new LatestMessagesDigestPusher(userID, session, transactionID, parentUsername);
            ChatSyncRetrievalExecutor.getInstance().scheduleEntityRetrieval(pusher);
        }
    }

    private void onSendFusionMessage(SessionPrx currentSession, MessageDataIce msg, UserDataIce senderUserData, UserPrx sender) throws FusionException {
        this.onSendFusionMessage(currentSession, msg, senderUserData, sender, null);
    }

    private void onSendFusionMessage(SessionPrx currentSession, MessageDataIce msg, UserDataIce senderUserData, UserPrx sender, String groupChatID) throws FusionException {
        GroupChatPrx groupChatPrx = null;
        if (groupChatID != null) {
            groupChatPrx = currentSession.findGroupChatObject(groupChatID);
        }
        MessageSendHandler msh = new MessageSendHandler(currentSession, msg, senderUserData, sender, groupChatPrx);
        ChatSyncStorageExecutor.getInstance().scheduleStorage(msh);
        if (!MessageData.isMessageToAChatRoom(msg)) {
            MessageSendLiveSyncer syncer = new MessageSendLiveSyncer(currentSession, msg, senderUserData, sender, groupChatPrx);
            ChatSyncStorageExecutor.getInstance().scheduleStorage(syncer);
        }
    }

    public void setChatName(String parentUsername, String suppliedChatID, byte chatType, String chatName, RegistryPrx regy, Current __current) throws FusionException {
        ChatDefinition chatKey = new ChatDefinition(suppliedChatID, chatType, parentUsername);
        ChatRenamer renamer = new ChatRenamer(chatKey, chatName, regy, parentUsername);
        ChatSyncStorageExecutor.getInstance().scheduleStorage(renamer);
    }

    public void getAndPushMessageStatusEvents(ConnectionI connection, String parentUsername, byte chatType, String suppliedChatID, Long startTime, Long endTime, int limit, ConnectionPrx cxn, int deviceType, short clientVersion, short requestTxnId) throws FusionException {
        ChatDefinition chatKey = new ChatDefinition(suppliedChatID, chatType, parentUsername);
        MessageStatusEventsRetriever retriever = new MessageStatusEventsRetriever(chatKey, startTime, endTime, (Integer)limit, parentUsername, cxn, requestTxnId);
        ChatSyncRetrievalExecutor.getInstance().scheduleEntityRetrieval(retriever);
    }

    public void getAndPushMessageStatusEvents(ConnectionI connection, String parentUsername, byte chatType, String suppliedChatID, String[] messageGUIDs, long[] messageTimestamps, int limit, ConnectionPrx cxn, int deviceType, short clientVersion, short requestTxnId) throws FusionException {
        ChatDefinition chatKey = new ChatDefinition(suppliedChatID, chatType, parentUsername);
        MessageStatusEventsRetriever retriever = new MessageStatusEventsRetriever(chatKey, messageGUIDs, messageTimestamps, (Integer)limit, parentUsername, cxn, requestTxnId);
        ChatSyncRetrievalExecutor.getInstance().scheduleEntityRetrieval(retriever);
    }
}

