/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import Ice.LocalException;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.botservice.BotChannelHelper;
import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emote.EmoteCommandStateStorage;
import com.projectgoth.fusion.exception.InternalServerErrorException;
import com.projectgoth.fusion.exception.NoLongerInGroupChatException;
import com.projectgoth.fusion.exception.UserNotOnlineException;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.messagelogger.MessageToLog;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardDispatcher;
import com.projectgoth.fusion.objectcache.ChatGroupParticipant;
import com.projectgoth.fusion.objectcache.ChatGroupParticipants;
import com.projectgoth.fusion.objectcache.ChatObjectManagerGroup;
import com.projectgoth.fusion.objectcache.ChatParticipant;
import com.projectgoth.fusion.objectcache.ChatSourceGroup;
import com.projectgoth.fusion.slice.BotInstance;
import com.projectgoth.fusion.slice.BotServicePrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;

public class ChatGroup
implements ChatSourceGroup {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatGroup.class));
    private ChatObjectManagerGroup objectManager;
    private static final String GROUP_CHAT_INVITE_FAILED_NOT_FRIENDED_ERROR = "Unable to add participant. Ensure you are both following each other and try again.";
    private GroupChatPrx groupChatPrx;
    private String id;
    private boolean markedForRemoval = false;
    private final ChatGroupParticipants participants;
    protected Map<String, BotInstance> bots = new ConcurrentHashMap<String, BotInstance>();
    protected Semaphore botSemaphore = new Semaphore(1);
    private long timeLastMessageSent = System.currentTimeMillis();
    private ChatGroupParticipant creatorParticipant;
    private ChatGroupParticipant privateChatPartnerParticipant;
    private ArrayList<ChatGroupParticipant> otherPartyParticipants = new ArrayList();
    private long blockBotsUntilTimestamp = 0L;
    private EmoteCommandStateStorage emoteCommandStates;
    private boolean checkForMutualFriendsOnGroupChatCreation;

    public ChatGroup(ChatObjectManagerGroup objectManager, String id) {
        this.objectManager = objectManager;
        this.id = id;
        this.participants = new ChatGroupParticipants(id);
        this.checkForMutualFriendsOnGroupChatCreation = Boolean.parseBoolean(objectManager.getProperties().getPropertyWithDefault("CheckForMutualFriendsOnGroupChatCreation", "true"));
        this.emoteCommandStates = new EmoteCommandStateStorage(objectManager.getIcePrxFinder());
    }

    public void setGroupChatPrx(GroupChatPrx groupChatPrx) {
        this.groupChatPrx = groupChatPrx;
    }

    private void markForRemoval() {
        this.markedForRemoval = true;
    }

    private boolean sendMessageToParticipant(ChatGroupParticipant participant, MessageDataIce message, boolean notifyAboutRemovedUsers, boolean displayPopUp) {
        ChatParticipant removedParticipant;
        block14: {
            if (!participant.updateLiveness(this.objectManager)) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Cannot send msg to participant=" + participant.getUsername() + " as participant user data null and cannot retrieve user prx"));
                }
                return false;
            }
            removedParticipant = null;
            try {
                participant.putMessage(message);
                if (displayPopUp) {
                    participant.putAlertMessage(message.messageText);
                }
            }
            catch (Exception e) {
                boolean remove = true;
                if (MessageSwitchboardDispatcher.getInstance().isFeatureEnabled()) {
                    remove = false;
                }
                if (SystemProperty.getBool(SystemPropertyEntities.PersistentGroupChatSettings.GROUP_CHAT_PARTICIPANT_REVIVAL_ENABLED) && !remove) {
                    if (participant.updateLiveness(this.objectManager)) {
                        try {
                            participant.putMessage(message);
                        }
                        catch (Exception e2) {
                            if (log.isDebugEnabled()) {
                                log.debug((Object)("sendMessageToParticipant failed to putMessage to participant=" + participant.getUsername() + ": " + e2), (Throwable)e2);
                            }
                        }
                    } else if (log.isDebugEnabled()) {
                        log.debug((Object)("sendMessageToParticipant: cannot putMessage to participant=" + participant.getUsername() + " as is not loaded"));
                    }
                }
                if (!remove) break block14;
                removedParticipant = this.participants.remove(participant.getUsername());
                if (this.participants.size() != 0) break block14;
                this.markForRemoval();
            }
        }
        if (notifyAboutRemovedUsers && removedParticipant != null) {
            this.sendAdminMessageToParticipants(removedParticipant.getUsername() + " has left.", null);
            return false;
        }
        return true;
    }

    public void addInitialParticipants(String creator, String privateChatter, String[] otherPartyList) throws FusionException {
        try {
            ArrayList<String> fullOtherPartyList = new ArrayList<String>();
            if (privateChatter != null) {
                fullOtherPartyList.add(privateChatter);
            }
            Collections.addAll(fullOtherPartyList, otherPartyList);
            if (this.checkForMutualFriendsOnGroupChatCreation) {
                UserPrx creatorUserPrx = this.objectManager.findUserPrx(creator);
                for (String p : fullOtherPartyList) {
                    boolean creationAllowed = this.isMutualFriend(creator, p) || creatorUserPrx.privateChattedWith(p);
                    if (creationAllowed) continue;
                    throw new FusionException(GROUP_CHAT_INVITE_FAILED_NOT_FRIENDED_ERROR);
                }
            }
            this.creatorParticipant = this.addUserToGroupChat(creator, true);
            if (!this.creatorParticipant.isAuthenticated(AuthenticatedAccessControlTypeEnum.CREATE_GROUP_CHAT)) {
                throw new FusionException("You must be authenticated before creating group chat");
            }
            for (String participant : fullOtherPartyList) {
                ChatGroupParticipant pobj = this.addUserToGroupChat(participant, false);
                if (pobj.isAnyoneOnBlockList(new String[]{creator, participant})) {
                    throw new FusionException("You may not create group chat at this time");
                }
                if (participant.equals(privateChatter)) {
                    this.privateChatPartnerParticipant = pobj;
                } else {
                    this.otherPartyParticipants.add(pobj);
                }
                if (privateChatter == null || !participant.equals(privateChatter)) continue;
                try {
                    pobj.putPrivateChatNowGroupChat(this.id, creator);
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)("While informing participant " + participant + " privateChatNowAGroupChat:" + e));
                }
            }
        }
        catch (FusionException fe) {
            this.participants.removeAll();
            this.markForRemoval();
            throw fe;
        }
    }

    public void sendInitialMessages() {
        MessageData message = new MessageData();
        message.type = MessageType.FUSION;
        message.sendReceive = MessageData.SendReceiveEnum.SEND;
        message.source = this.id;
        message.contentType = MessageData.ContentTypeEnum.TEXT;
        message.messageText = "You have created a new group chat. Current participants: " + this.listOfParticipants();
        MessageDestinationData messageDest = new MessageDestinationData();
        messageDest.type = MessageDestinationData.TypeEnum.GROUP;
        messageDest.destination = this.id;
        message.messageDestinations = new ArrayList<MessageDestinationData>();
        message.messageDestinations.add(messageDest);
        this.sendMessageToParticipant(this.creatorParticipant, message.toIceObject(), true, false);
        if (this.privateChatPartnerParticipant != null) {
            message.messageText = this.creatorParticipant.getUsername() + " has invited " + this.getOtherPartyListDisplayString() + " to the chat. Current participants: " + this.listOfParticipants();
            this.sendMessageToParticipant(this.privateChatPartnerParticipant, message.toIceObject(), true, false);
        }
        for (ChatGroupParticipant p : this.otherPartyParticipants) {
            message.messageText = "You have been invited to a group chat by " + this.creatorParticipant.getUsername() + ". Current participants: " + this.listOfParticipants();
            this.sendMessageToParticipant(p, message.toIceObject(), true, false);
        }
        try {
            this.objectManager.logMessage(MessageToLog.TypeEnum.GROUPCHAT, this.creatorParticipant.getCountryID(this.objectManager), this.creatorParticipant.getUsername(), this.listOfParticipants(";"), 3, this.creatorParticipant.getUsername() + " created a group chat");
        }
        catch (LocalException e) {
            log.error((Object)"failed to log message to messagelogger");
        }
        try {
            this.sendGroupChatParticipants(this.creatorParticipant);
            if (this.privateChatPartnerParticipant != null) {
                this.sendGroupChatParticipants(this.privateChatPartnerParticipant);
            }
            for (ChatGroupParticipant p : this.otherPartyParticipants) {
                this.sendGroupChatParticipants(p);
            }
        }
        catch (FusionException e) {
            log.error((Object)("sendGroupChatParticipants() call failed for groupchat=" + this.id), (Throwable)((Object)e));
        }
    }

    private void sendGroupChatParticipants(ChatGroupParticipant participant) throws FusionException {
        if (!participant.hasUserProxy()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("GroupChatI.sendGroupChatParticipants: not sending to " + participant + " as participant is offline"));
            }
            return;
        }
        if (SystemPropertyEntities.Temp.Cache.fi83Week3UseAutogeneratedPacketsEnabled.getValue().booleanValue()) {
            String[] participantArray = this.getParticipants(participant.getUsername());
            String[] mutedUsers = participant.getBlockListFromUsernames(participantArray);
            participant.sendGroupChatParticipantArrays(this.id, ImType.FUSION, participantArray, mutedUsers);
        } else {
            String mutedUsers;
            Object[] participantArray = this.getParticipants(participant.getUsername());
            String participants = StringUtil.join(participantArray, ";");
            if (participants != null && participants.length() > 0) {
                participants = participants + ";";
            }
            if ((mutedUsers = StringUtil.join(participant.getBlockListFromUsernames((String[])participantArray), ";")) != null && mutedUsers.length() > 0) {
                mutedUsers = mutedUsers + ";";
            }
            participant.sendGroupChatParticipants(this.id, ImType.FUSION, participants, mutedUsers);
        }
    }

    private void notifyUserJoinedGroupChat(ChatGroupParticipant user) throws FusionException {
        this.participants.notifyUserJoined(user);
    }

    private void notifyUserLeftGroupChat(ChatGroupParticipant user) throws FusionException {
        user.chatSyncNotifyUserLeft(this.objectManager, this.id);
        this.participants.notifyUserLeft(user);
    }

    public String listOfParticipants() {
        return this.listOfParticipants(", ");
    }

    private String listOfParticipants(String separator) {
        return this.participants.getList(separator);
    }

    public void addParticipants(String inviterUsername, String[] inviteeUsernames) throws FusionException {
        for (String invitee : inviteeUsernames) {
            try {
                this.addParticipant(inviterUsername, invitee);
            }
            catch (Exception e) {
                log.warn((Object)("addParticipant() during addParticipants() failed for inviterUsername=" + inviterUsername + " invitee=" + invitee), (Throwable)e);
            }
        }
    }

    public void addParticipant(String inviterUsername, String inviteeUsername) throws FusionException {
        this.addParticipant(inviterUsername, inviteeUsername, false);
    }

    public void addParticipant(String inviterUsername, String inviteeUsername, boolean debug) throws FusionException {
        if (log.isDebugEnabled() || debug) {
            log.info((Object)("Entering addParticipant for inviter=" + inviterUsername + " invitee=" + inviteeUsername));
        }
        if (this.participants.isParticipant(inviteeUsername)) {
            FusionException fe = new FusionException();
            fe.message = inviteeUsername + " is already in the group chat";
            throw fe;
        }
        if (log.isDebugEnabled() || debug) {
            log.info((Object)("addParticipant(): invitee=" + inviteeUsername + " passed already-in-chat test"));
        }
        if (!this.isMutualFriend(inviterUsername, inviteeUsername)) {
            FusionException fe = new FusionException();
            fe.message = GROUP_CHAT_INVITE_FAILED_NOT_FRIENDED_ERROR;
            throw fe;
        }
        if (log.isDebugEnabled() || debug) {
            log.info((Object)("addParticipant(): invitee=" + inviteeUsername + " passed already-in-chat test"));
        }
        ChatGroupParticipant participant = this.addUserToGroupChat(inviteeUsername, false, true, debug);
        if (log.isDebugEnabled() || debug) {
            log.info((Object)"addParticipant(): called addUserToGroupChat ok");
        }
        if (!participant.isInitallyOffline()) {
            MessageData message = new MessageData();
            message.type = MessageType.FUSION;
            message.sendReceive = MessageData.SendReceiveEnum.SEND;
            message.source = this.id;
            message.contentType = MessageData.ContentTypeEnum.TEXT;
            MessageDestinationData messageDest = new MessageDestinationData();
            messageDest.type = MessageDestinationData.TypeEnum.GROUP;
            messageDest.destination = this.id;
            message.messageDestinations = new ArrayList<MessageDestinationData>();
            message.messageDestinations.add(messageDest);
            StringBuffer sb = new StringBuffer("You have been invited to a group chat by ");
            sb.append(inviterUsername);
            sb.append(". Current participants: ");
            sb.append(this.listOfParticipants());
            message.messageText = sb.toString();
            if (!this.sendMessageToParticipant(participant, message.toIceObject(), true, false)) {
                log.warn((Object)"Unable to send a list of group chat participants to a new participant");
                throw new FusionException("Unable to send a message to the new group chat participant");
            }
            this.sendGroupChatParticipants(participant);
            try {
                this.objectManager.logMessage(MessageToLog.TypeEnum.GROUPCHAT, this.creatorParticipant.getCountryID(this.objectManager), message.source, this.listOfParticipants(";"), this.participants.size(), sb.toString());
            }
            catch (LocalException e) {
                log.error((Object)"failed to log message to messagelogger");
            }
        }
        this.sendAdminMessageToParticipants(inviterUsername + " has invited " + participant.getUsername() + " to the group chat", inviteeUsername);
        this.notifyUserJoinedGroupChat(participant);
        if (this.isBotLoaded()) {
            BotChannelHelper.updateBots(inviteeUsername, BotData.BotCommandEnum.JOIN, this.bots, this.groupChatPrx.ice_getIdentity().name);
        }
        this.objectManager.onGroupSessionAdded();
    }

    private boolean isMutualFriend(String inviterUsername, String inviteeUsername) throws FusionException {
        try {
            UserPrx userPrx = this.objectManager.findUserPrx(inviteeUsername);
            return userPrx.isOnContactList(inviterUsername);
        }
        catch (UserNotOnlineException e) {
            return this.isMutualFriend_InviteeOffline(inviterUsername, inviteeUsername);
        }
        catch (Exception e) {
            throw new InternalServerErrorException(e, "isMutualFriend:" + inviterUsername + "," + inviteeUsername);
        }
    }

    private boolean isMutualFriend_InviteeOffline(String inviterUsername, String inviteeUsername) throws FusionException {
        try {
            Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
            ContactData cd = contactEJB.getContact(inviteeUsername, inviterUsername);
            return cd.id != null;
        }
        catch (Exception e2) {
            throw new FusionException(e2.getMessage());
        }
    }

    public ChatGroupParticipant addUserToGroupChat(String username, boolean isCreator) throws FusionException {
        return this.addUserToGroupChat(username, isCreator, true, false);
    }

    public ChatGroupParticipant addUserToGroupChat(String username, boolean isCreator, boolean isInitialJoin) throws FusionException {
        return this.addUserToGroupChat(username, isCreator, isInitialJoin, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ChatGroupParticipant addUserToGroupChat(String username, boolean isCreator, boolean isInitialJoin, boolean debug) throws FusionException {
        int participantLimit = this.participants.getParticipantLimit();
        ChatGroupParticipants chatGroupParticipants = this.participants;
        synchronized (chatGroupParticipants) {
            if (log.isDebugEnabled() || debug) {
                log.info((Object)("Adding user to GroupChat. Participant limit: " + participantLimit + ", current size: " + this.participants.size()));
            }
            if (isInitialJoin) {
                if (this.participants.size() >= participantLimit) {
                    log.error((Object)("GroupChatI.addUserToGroupChat() failed. Maximum number of participants reached. Rejected user: " + username + ", current participants: " + this.listOfParticipants()));
                    throw new FusionException("Cannot add " + username + " to this group chat. Maximum number of participants reached.");
                }
                if (this.participants.isParticipant(username)) {
                    throw new FusionException(username + " is already in the group chat");
                }
            }
            ChatGroupParticipant participant = this.loadParticipant(username, isCreator, isInitialJoin);
            if (log.isDebugEnabled() || debug) {
                log.info((Object)"addUserToGroupChat: calling participants.put");
            }
            this.participants.add(username, participant);
            if (log.isDebugEnabled() || debug) {
                log.info((Object)"addUserToGroupChat: called participants.put");
            }
            if (isInitialJoin) {
                participant.chatSyncNotifyUserJoined(this.objectManager, this.id, false);
            }
            this.objectManager.onGroupSessionAdded();
            return participant;
        }
    }

    private ChatGroupParticipant loadParticipant(String username, boolean isCreator, boolean isInitialJoin) throws FusionException {
        ChatGroupParticipant participant;
        UserPrx userProxy = null;
        try {
            userProxy = this.objectManager.findUserPrx(username);
        }
        catch (ObjectNotFoundException e) {
        }
        catch (FusionException e) {
            if (!(e instanceof UserNotOnlineException)) {
                throw e;
            }
        }
        catch (Exception e) {
            log.warn((Object)("ChatGroup.loadParticipant() failed: " + e));
            throw new InternalServerErrorException(e, this.id + ":" + username);
        }
        if (userProxy == null) {
            participant = new ChatGroupParticipant(username);
        } else {
            UserDataIce userDataIce;
            if (isInitialJoin) {
                int destinationPresence = userProxy.getOverallFusionPresence(null);
                if (!isCreator && PresenceType.BUSY.value() == destinationPresence) {
                    String presence = PresenceType.fromValue(destinationPresence).toString();
                    throw new FusionException(username + " is " + presence + " and cannot be added to a group chat");
                }
                userProxy.enteringGroupChat(isCreator);
            }
            try {
                userDataIce = userProxy.getUserData();
            }
            catch (Exception e) {
                throw new FusionException("Unable to obtain details of the user");
            }
            participant = new ChatGroupParticipant(new UserData(userDataIce), userProxy);
            participant.loadMerchantDetailsData();
        }
        return participant;
    }

    private void sendMessageToParticipants(MessageDataIce messageIce, String usernameToExclude, boolean notifyAboutRemovedUsers) {
        for (ChatGroupParticipant participant : this.participants.getParticipants()) {
            if (usernameToExclude != null && usernameToExclude.equals(participant.getUsername())) continue;
            this.sendMessageToParticipant(participant, messageIce, notifyAboutRemovedUsers, false);
            this.timeLastMessageSent = System.currentTimeMillis();
        }
    }

    public void sendAdminMessageToParticipants(String messageText, String usernameToExclude) {
        MessageData message = this.createTextMessageForGroupChat(messageText);
        this.sendMessageToParticipants(message.toIceObject(), usernameToExclude, false);
    }

    private MessageData createTextMessageForGroupChat(String messageText) {
        MessageData message = new MessageData();
        message.type = MessageType.FUSION;
        message.messageText = messageText;
        message.sendReceive = MessageData.SendReceiveEnum.SEND;
        message.source = this.id;
        message.contentType = MessageData.ContentTypeEnum.TEXT;
        MessageDestinationData messageDest = new MessageDestinationData();
        messageDest.type = MessageDestinationData.TypeEnum.GROUP;
        messageDest.destination = this.id;
        message.messageDestinations = new ArrayList<MessageDestinationData>();
        message.messageDestinations.add(messageDest);
        return message;
    }

    private MessageData createBotMessageForGroupChat(String botInstanceID, String messageText, String[] emoticonHotKeys) {
        MessageData message = new MessageData();
        message.type = MessageType.FUSION;
        message.messageText = messageText;
        message.sendReceive = MessageData.SendReceiveEnum.SEND;
        message.contentType = MessageData.ContentTypeEnum.TEXT;
        BotInstance botInstance = this.bots.get(botInstanceID);
        if (botInstance == null) {
            message.source = this.id;
        } else {
            message.source = botInstance.displayName;
            message.sourceColour = MessageData.SourceTypeEnum.BOT.colorHex();
            message.messageColour = 34734;
        }
        if (emoticonHotKeys != null) {
            message.emoticonKeys = Arrays.asList(emoticonHotKeys);
        }
        MessageDestinationData messageDest = new MessageDestinationData();
        messageDest.type = MessageDestinationData.TypeEnum.GROUP;
        messageDest.destination = this.id;
        message.messageDestinations = new ArrayList<MessageDestinationData>();
        message.messageDestinations.add(messageDest);
        return message;
    }

    public boolean removeParticipant(String username) throws FusionException {
        ChatGroupParticipant participant = this.participants.remove(username);
        if (this.participants.size() == 0) {
            this.markForRemoval();
        }
        if (participant != null) {
            this.sendAdminMessageToParticipants(participant.getUsername() + " has left", null);
            this.notifyUserLeftGroupChat(participant);
            if (this.isBotLoaded()) {
                BotChannelHelper.updateBots(username, BotData.BotCommandEnum.QUIT, this.bots, this.groupChatPrx.ice_getIdentity().name);
            }
            this.objectManager.onGroupSessionRemoved();
        }
        this.objectManager.onGroupSessionRemoved();
        return participant != null;
    }

    public void removeAllParticipants() {
        this.participants.removeAllParticipants();
    }

    public void putMessage(MessageDataIce messageIce) throws FusionException {
        String sender = messageIce.source;
        ChatGroupParticipant senderParticipant = this.participants.get(sender);
        if (senderParticipant == null) {
            throw new NoLongerInGroupChatException();
        }
        if (!senderParticipant.updateLiveness(this.objectManager)) {
            log.warn((Object)("Participant " + senderParticipant.getUsername() + " unable to send group chat " + "message, userprx anomalously unavailable or failed liveness test"));
            throw new FusionException("Cannot send group chat message. Please try later");
        }
        senderParticipant.applyMessageColor(messageIce);
        messageIce.messageDestinations[0].status = MessageDestinationData.StatusEnum.SENT.value();
        messageIce.messageDestinations[0].dateDispatched = System.currentTimeMillis();
        if (this.objectManager.isLogMessagesToDB()) {
            try {
                Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                messageEJB.saveSentMessage(new MessageData(messageIce));
            }
            catch (Exception e) {
                log.warn((Object)("Unable to save a Fusion group chat message to the database. Exception: " + e.getMessage()));
            }
        }
        if (this.objectManager.isLogMessagesToFile()) {
            try {
                int numRecipients = this.participants.size() - 1;
                if (numRecipients < 0) {
                    numRecipients = 0;
                }
                this.objectManager.logMessage(MessageToLog.TypeEnum.GROUPCHAT, senderParticipant.getCountryID(this.objectManager), sender, this.listOfParticipants(";"), numRecipients, messageIce.messageText);
            }
            catch (LocalException e) {
                log.warn((Object)("Unable to send a Fusion group chat message to the MessageLogger application. Exception: " + e.toString()));
            }
        }
        this.sendMessageToParticipants(messageIce, sender, true);
    }

    public void putFileReceived(MessageDataIce messageIce) throws FusionException {
        this.participants.putFileReceived(messageIce);
        this.timeLastMessageSent = System.currentTimeMillis();
    }

    public boolean isBotLoaded() {
        return this.bots != null && !this.bots.isEmpty();
    }

    public void startBot(String username, String botName) throws FusionException {
        boolean locked = false;
        try {
            ChatGroupParticipant participant = this.getParticipant(username);
            if (participant == null) {
                throw new FusionException("You are no longer in the room");
            }
            Iterator<BotInstance> i$ = this.bots.values().iterator();
            if (i$.hasNext()) {
                BotInstance botInstance = i$.next();
                throw new FusionException("A bot of type [" + botInstance.displayName + "] is already running");
            }
            if (participant == this.creatorParticipant || participant.isAdmin()) {
                this.blockBotsUntilTimestamp = 0L;
            } else if (System.currentTimeMillis() < this.blockBotsUntilTimestamp) {
                if (this.blockBotsUntilTimestamp == Long.MAX_VALUE) {
                    throw new FusionException("Bots have been blocked in this room.");
                }
                throw new FusionException("Bots have been temporarily blocked. Please wait " + (this.blockBotsUntilTimestamp - System.currentTimeMillis()) / 1000L + "s");
            }
            locked = this.botSemaphore.tryAcquire();
            if (!locked) {
                throw new FusionException("Another user is starting or stopping a bot. Please try again later");
            }
            BotServicePrx botServicePrx = this.objectManager.getRegistryPrx().getLowestLoadedBotService();
            BotInstance newBotInstance = botServicePrx.addBotToChannel(this.groupChatPrx, botName, username, true);
            this.bots.put(newBotInstance.id, newBotInstance);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Bot '" + newBotInstance.id + "' loaded in group chat " + this.id));
            }
        }
        catch (ObjectNotFoundException e) {
            throw new FusionException("migGames are temporarily unavailable. Please try again later");
        }
        finally {
            if (locked) {
                this.botSemaphore.release();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopBot(String username, String botName) throws FusionException {
        boolean semaphoreAquired = false;
        try {
            ChatGroupParticipant participant = this.getParticipant(username);
            if (participant == null) {
                throw new NoLongerInGroupChatException();
            }
            if (this.bots.size() == 0) {
                throw new FusionException("There is currently no bot running in this group chat");
            }
            semaphoreAquired = this.botSemaphore.tryAcquire();
            if (!semaphoreAquired) {
                throw new FusionException("Another user is starting or stopping a bot. Please try again later");
            }
            int botsRemoved = 0;
            for (BotInstance botInstance : this.bots.values()) {
                if (!botInstance.startedBy.equals(username) && this.getParticipant(botInstance.startedBy) != null) continue;
                botInstance.botServiceProxy.removeBot(botInstance.id, false);
                this.bots.remove(botInstance.id);
                this.sendAdminMessageToParticipants("Bot '" + botInstance.displayName + "' left the chat", null);
                ++botsRemoved;
            }
            if (botsRemoved == 0) {
                throw new FusionException("You do not have permission to stop the bot at this time");
            }
        }
        finally {
            if (semaphoreAquired) {
                this.botSemaphore.release();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopAllBots(String username, int timeout) throws FusionException {
        boolean semaphoreAcquired = false;
        try {
            ChatGroupParticipant participant = this.participants.get(username);
            if (participant == null) {
                throw new FusionException("You are no longer in the room");
            }
            if (!participant.isAdmin() && this.creatorParticipant != participant) {
                throw new FusionException("/botstop ! can only be done by admins or the creator of this group chat");
            }
            semaphoreAcquired = this.botSemaphore.tryAcquire();
            if (!semaphoreAcquired) {
                throw new FusionException("Another user is starting or stopping a bot. Please try again later");
            }
            int botsRemoved = 0;
            for (BotInstance botInstance : this.bots.values()) {
                botInstance.botServiceProxy.removeBot(botInstance.id, true);
                this.bots.remove(botInstance.id);
                this.sendAdminMessageToParticipants("Bot '" + botInstance.displayName + "' has been stopped by " + username, null);
                ++botsRemoved;
            }
            if (timeout > 0) {
                this.blockBotsUntilTimestamp = System.currentTimeMillis() + (long)(timeout * 1000);
                this.sendAdminMessageToParticipants("Bots may not be started for " + timeout + "s", null);
            } else {
                this.blockBotsUntilTimestamp = Long.MAX_VALUE;
                this.sendAdminMessageToParticipants("Bots may not be started except by admins or the creator of this group chat", null);
            }
        }
        finally {
            if (semaphoreAcquired) {
                this.botSemaphore.release();
            }
        }
    }

    public void botKilled(String botInstanceID) throws FusionException {
        BotInstance botInstance = this.bots.remove(botInstanceID);
        if (botInstance != null) {
            this.sendAdminMessageToParticipants("Bot '" + botInstance.displayName + "' was stopped for being idle too long.", null);
        }
    }

    public void sendMessageToBots(String username, String message, long receivedTimestamp) throws FusionException {
        for (BotInstance botInstance : this.bots.values()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Sending bot message from group chat '" + this.id + "' to bot" + "'" + botInstance.displayName + "'. Text [" + message + "]"));
            }
            botInstance.botServiceProxy.sendMessageToBot(botInstance.id, username, message, receivedTimestamp);
        }
    }

    public void putBotMessage(String botInstanceID, String username, String message, String[] emoticonHotKeys, boolean displayPopUp) throws FusionException {
        ChatGroupParticipant participant;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Sent bot message to " + username + " in group chat " + this.id + " Text [" + message + "]"));
        }
        if ((participant = this.getParticipant(username)) != null) {
            MessageData messageData = this.createBotMessageForGroupChat(botInstanceID, "[PVT] " + message, emoticonHotKeys);
            this.sendMessageToParticipant(participant, messageData.toIceObject(), false, displayPopUp);
        } else {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Could not send private message. Participant not found: " + username + ". Updating bots..."));
            }
            BotChannelHelper.updateBots(username, BotData.BotCommandEnum.PART, this.bots, this.groupChatPrx.ice_getIdentity().name);
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

    public void putBotMessageToAllUsers(String botInstanceID, String message, String[] emoticonHotKeys, boolean displayPopUp) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Sending bot message to all users in group chat " + this.id + " :" + message));
        }
        MessageData messageData = this.createBotMessageForGroupChat(botInstanceID, message, emoticonHotKeys);
        this.sendMessageToParticipants(messageData.toIceObject(), null, false);
    }

    public void sendGamesHelpToUser(String username) throws FusionException {
        ChatGroupParticipant participant = this.getParticipant(username);
        if (participant != null) {
            List<BotData> games = BotChannelHelper.getGames();
            if (games != null && !games.isEmpty()) {
                for (int i = 0; i < games.size(); ++i) {
                    StringBuilder helpText = new StringBuilder();
                    BotData botData = games.get(i);
                    helpText.append("To start ").append(botData.getGame()).append(", type: /bot ").append(botData.getCommandName());
                    MessageData messageData = this.createTextMessageForGroupChat(helpText.toString());
                    messageData.contentType = MessageData.ContentTypeEnum.EMOTE;
                    this.sendMessageToParticipant(participant, messageData.toIceObject(), false, false);
                }
                MessageData messageData = this.createTextMessageForGroupChat("For help, see: migWorld");
                messageData.contentType = MessageData.ContentTypeEnum.EMOTE;
                this.sendMessageToParticipant(participant, messageData.toIceObject(), false, false);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Sent help message from group chat id[" + this.id + "] to participant " + participant.getUsername()));
                }
            } else if (log.isDebugEnabled()) {
                log.debug((Object)"No games found.");
            }
        } else {
            log.warn((Object)("Could not send games help message. Participant not found: " + username + "."));
        }
    }

    public ChatGroupParticipant getParticipant(String username) {
        return this.participants.get(username);
    }

    public int getNumParticipants() {
        return this.participants.size();
    }

    public String[] getParticipants(String requestingUsername) {
        return this.participants.getParticipants(requestingUsername);
    }

    public int[] getParticipantUserIDs() {
        return this.participants.getUserIDs();
    }

    public boolean supportsBinaryMessage(String usernameToExclude) {
        return this.participants.supportsBinaryMessage(usernameToExclude);
    }

    public boolean isParticipant(String username) throws FusionException {
        return this.participants.isParticipant(username);
    }

    public boolean isMarkedForRemoval() {
        return this.markedForRemoval;
    }

    public long getTimeLastMessageSent() {
        return this.timeLastMessageSent;
    }

    public int executeEmoteCommandWithState(String emoteCommand, MessageDataIce message, SessionPrx sessionProxy) throws FusionException {
        return this.emoteCommandStates.executeEmoteCommandWithState(emoteCommand, message, sessionProxy, this);
    }

    public ChatDefinition toChatDefinition() throws FusionException {
        String[] sUsernames = this.participants.getUserNames();
        ChatDefinition def = new ChatDefinition(this.creatorParticipant.getUsername(), this.id, sUsernames, (byte)MessageDestinationData.TypeEnum.GROUP.value(), null, null, this.creatorParticipant.getUsername(), (byte)0, null, MessageType.FUSION.value());
        return def;
    }

    public String getCreatorUsername() {
        return this.creatorParticipant.getUsername();
    }

    public int getCreatorUserID() {
        return this.creatorParticipant.getUserID();
    }

    public int getPrivateChatPartnerUserID() {
        return this.privateChatPartnerParticipant != null ? this.privateChatPartnerParticipant.getUserID() : Integer.MIN_VALUE;
    }

    public String getId() {
        return this.id;
    }

    public void setCreatorParticipant(String creator) throws FusionException {
        this.creatorParticipant = this.participants.get(creator);
        if (this.creatorParticipant == null) {
            this.creatorParticipant = this.loadParticipant(creator, true, false);
        }
    }

    private String getOtherPartyListDisplayString() {
        String result = new String();
        for (ChatGroupParticipant p : this.otherPartyParticipants) {
            result = result + (result.length() != 0 ? ", " : "") + p.getUsername();
        }
        return result;
    }
}

