/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.data.PaidEmoteData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emote.ChatSourceVisitor;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.Sticker;
import com.projectgoth.fusion.emote.StickerDeliveredMessageData;
import com.projectgoth.fusion.exception.UserNotOnlineException;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardDispatcher;
import com.projectgoth.fusion.objectcache.ChatSourceGroup;
import com.projectgoth.fusion.objectcache.ChatSourceRoom;
import com.projectgoth.fusion.objectcache.ChatSourceSession;
import com.projectgoth.fusion.objectcache.ChatSourceUser;
import com.projectgoth.fusion.objectcache.OfflineMessageHelper;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserPrx;
import org.apache.log4j.Logger;

public abstract class ChatSource {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatSource.class));
    protected ChatType chatType;
    protected ChatSourceType chatSourceType;
    protected SessionPrx sessionPrx;
    protected ChatSourceSession sessionI;
    protected String parentUsername;

    private ChatSource(ChatType chatType, ChatSourceSession session) {
        this.chatSourceType = ChatSourceType.LOCAL_SESSION;
        this.chatType = chatType;
        this.sessionI = session;
    }

    private ChatSource(ChatType chatType, SessionPrx session) {
        this.chatSourceType = ChatSourceType.REMOTE_SESSION;
        this.chatType = chatType;
        this.sessionPrx = session;
    }

    public static ChatSource createChatSourceForChatRoom(ChatSourceSession session, ChatRoomPrx chatRoomPrx) {
        return new LocalChatRoomChatSource(session, chatRoomPrx);
    }

    public static ChatSource createChatSourceForChatRoom(SessionPrx session, ChatSourceRoom chatRoom) {
        return new RemoteChatRoomChatSource(session, chatRoom);
    }

    public static ChatSource createChatSourceForGroupChat(ChatSourceSession session, GroupChatPrx groupChatPrx) {
        return new LocalGroupChatChatSource(session, groupChatPrx);
    }

    public static ChatSource createChatSourceForGroupChat(SessionPrx session, ChatSourceGroup chatGroup) {
        return new RemoteGroupChatChatSource(session, chatGroup);
    }

    public static ChatSource createChatSourceForPrivateChat(ChatSourceSession session, String destinationUsername) {
        return new LocalPrivateChatChatSource(session, destinationUsername);
    }

    public static ChatSource createChatSourceForPrivateChat(SessionPrx session, ChatSourceUser userI, String senderUsername, String destinationUsername) {
        return new RemotePrivateChatChatSource(session, userI, senderUsername, destinationUsername);
    }

    public ChatType getChatType() {
        return this.chatType;
    }

    public ChatSourceSession getSessionI() {
        return this.sessionI;
    }

    protected String getTruncatedMessage(String messageText, String subMessageToTruncate, int maxLength, String parentUsername) throws FusionException {
        String msg = EmoteCommand.processMessageVariables(String.format(messageText, subMessageToTruncate), parentUsername, this);
        if (msg.length() > maxLength) {
            String newSubMsg = "";
            int subMessageToTruncateLen = subMessageToTruncate.length();
            if (subMessageToTruncateLen > msg.length() - maxLength + 3) {
                newSubMsg = subMessageToTruncate.substring(0, subMessageToTruncateLen - (msg.length() - maxLength) - 3) + "...";
            }
            msg = EmoteCommand.processMessageVariables(String.format(messageText, newSubMsg), parentUsername, this);
        }
        return msg;
    }

    public SessionPrx getSessionPrx() {
        return this.chatSourceType == ChatSourceType.REMOTE_SESSION ? this.sessionPrx : this.sessionI.findSessionPrx(this.sessionI.getSessionID());
    }

    public abstract boolean isUserInChat(String var1) throws FusionException;

    public abstract boolean isUserVisibleInChat(String var1) throws FusionException;

    public abstract String[] getVisibleUsernamesInChat(boolean var1) throws FusionException;

    public abstract String[] getAllUsernamesInChat(boolean var1) throws FusionException;

    public void sendMessageToAllUsersInChat(MessageData messageData) throws FusionException {
        try {
            MessageSwitchboardDispatcher.getInstance().onSendMessageToAllUsersInChat(messageData, messageData.username, this.sessionPrx, this.sessionI);
        }
        catch (Exception e) {
            log.error((Object)"While storing V2 emote in chatsync: ", (Throwable)e);
            throw new FusionException(e.getMessage());
        }
    }

    public abstract void sendToSender(MessageDataIce var1) throws FusionException;

    public abstract void sendToCounterParties(MessageDataIce var1) throws FusionException;

    public void sendMessageWithTruncationToAllUsersInChat(MessageData messageData, String subMessageToTruncate) throws FusionException {
        messageData.messageText = String.format(messageData.messageText, subMessageToTruncate);
        this.sendMessageToAllUsersInChat(messageData);
    }

    public abstract void sendMessageToSender(MessageData var1) throws FusionException;

    public abstract PaidEmoteData.EmotePurchaseLocationEnum getEmotePurchaseLocation();

    protected String getParentUsernameInternal() throws FusionException {
        return null;
    }

    public String getParentUsername() throws FusionException {
        if (this.parentUsername == null) {
            this.parentUsername = this.getParentUsernameInternal();
        }
        return this.parentUsername;
    }

    public EmoteCommand.ResultType executeEmoteCommandWithState(String emoteCommand, MessageDataIce message) throws FusionException {
        throw new FusionException("unimplemented execution of emote command with state");
    }

    public abstract void accept(ChatSourceVisitor var1) throws FusionException;

    public abstract ClientType getSenderDeviceType();

    public abstract short getSenderClientVersion();

    public LocalChatRoomChatSource castToLocalChatRoomChatSource() {
        return (LocalChatRoomChatSource)this;
    }

    private MessageDataIce createStickerEmotesForSender(MessageData messageData, String msgToInstigator) throws FusionException {
        return Sticker.createStickerEmotesForSender(messageData, msgToInstigator, this.getSenderDeviceType(), this.getSenderClientVersion());
    }

    public final void sendStickerEmotes(StickerDeliveredMessageData forDelivery) throws FusionException {
        this.sendToSender(this.createStickerEmotesForSender(forDelivery.getMessageData(), forDelivery.getMessageToInstigator()));
        this.sendToCounterParties(Sticker.createStickerEmotesForRecipients(forDelivery.getMessageData(), forDelivery.getMessageToRecipients()));
    }

    public static class RemotePrivateChatChatSource
    extends ChatSource {
        private String senderUsername;
        private String destinationUsername;
        protected ChatSourceUser currentUserI;

        RemotePrivateChatChatSource(SessionPrx session, ChatSourceUser userI, String senderUsername, String destinationUsername) {
            super(ChatType.PRIVATE_CHAT, session);
            this.currentUserI = userI;
            this.senderUsername = senderUsername;
            this.destinationUsername = destinationUsername;
        }

        public boolean isUserInChat(String username) throws FusionException {
            return !StringUtil.isBlank(username) && (username.equals(this.destinationUsername) || username.equals(this.senderUsername));
        }

        public boolean isUserVisibleInChat(String username) throws FusionException {
            return this.isUserInChat(username);
        }

        public String[] getVisibleUsernamesInChat(boolean includeParentUser) throws FusionException {
            return this.getAllUsernamesInChat(includeParentUser);
        }

        public String[] getAllUsernamesInChat(boolean includeParentUser) throws FusionException {
            String[] stringArray;
            if (includeParentUser) {
                String[] stringArray2 = new String[2];
                stringArray2[0] = this.senderUsername;
                stringArray = stringArray2;
                stringArray2[1] = this.destinationUsername;
            } else {
                String[] stringArray3 = new String[1];
                stringArray = stringArray3;
                stringArray3[0] = this.destinationUsername;
            }
            return stringArray;
        }

        public void sendMessageToAllUsersInChat(MessageData messageData) throws FusionException {
            super.sendMessageToAllUsersInChat(messageData);
            boolean isInSenderUserI = this.senderUsername.equals(this.currentUserI.getUsername());
            String messageText = messageData.messageText;
            MessageDataIce messageIce = messageData.toIceObject();
            messageIce.messageText = EmoteCommand.processMessageVariables(messageText, null, this);
            if (isInSenderUserI) {
                UserPrx destinationUserPrx = this.sessionPrx.getUserProxy(this.destinationUsername);
                if (destinationUserPrx == null) {
                    throw new FusionException(this.destinationUsername + " is not in the chat");
                }
                destinationUserPrx.putMessage(messageIce);
            } else {
                this.currentUserI.putMessage(messageIce);
            }
            messageIce.messageText = EmoteCommand.processMessageVariables(messageText, this.senderUsername, this);
            String tmp = messageIce.source;
            messageIce.source = messageIce.messageDestinations[0].destination;
            messageIce.messageDestinations[0].destination = tmp;
            this.sessionPrx.putMessage(messageIce);
        }

        public void sendMessageToSender(MessageData messageData) throws FusionException {
            String messageText = messageData.messageText;
            MessageDataIce messageIce = messageData.toIceObject();
            messageIce.messageText = EmoteCommand.processMessageVariables(messageText, this.senderUsername, this);
            String tmp = messageIce.source;
            messageIce.source = messageIce.messageDestinations[0].destination;
            messageIce.messageDestinations[0].destination = tmp;
            this.sessionPrx.putMessage(messageIce);
        }

        public PaidEmoteData.EmotePurchaseLocationEnum getEmotePurchaseLocation() {
            return PaidEmoteData.EmotePurchaseLocationEnum.PRIVATE_CHAT_COMMAND;
        }

        protected String getParentUsernameInternal() throws FusionException {
            return this.sessionPrx.getParentUsername();
        }

        public void accept(ChatSourceVisitor visitor) throws FusionException {
            visitor.visit(this);
        }

        public void sendToSender(MessageDataIce messageDataIce) throws FusionException {
            this.sessionPrx.putMessage(messageDataIce);
        }

        public void sendToCounterParties(MessageDataIce messageDataIce) throws FusionException {
            this.currentUserI.putMessage(messageDataIce);
        }

        public ClientType getSenderDeviceType() {
            return ClientType.fromValue(this.sessionPrx.getDeviceTypeAsInt());
        }

        public short getSenderClientVersion() {
            return this.sessionPrx.getClientVersionIce();
        }
    }

    public static class LocalPrivateChatChatSource
    extends ChatSource {
        protected String destinationUsername;
        protected UserPrx destinationUserPrx;

        LocalPrivateChatChatSource(ChatSourceSession session, String destinationUsername) {
            super(ChatType.PRIVATE_CHAT, session);
            this.destinationUsername = destinationUsername;
        }

        public boolean isUserInChat(String username) throws FusionException {
            return !StringUtil.isBlank(username) && (username.equals(this.destinationUsername) || username.equals(this.getParentUsername()));
        }

        public boolean isUserVisibleInChat(String username) throws FusionException {
            return this.isUserInChat(username);
        }

        public String[] getVisibleUsernamesInChat(boolean includeParentUser) throws FusionException {
            return this.getAllUsernamesInChat(includeParentUser);
        }

        public String[] getAllUsernamesInChat(boolean includeParentUser) throws FusionException {
            String[] stringArray;
            if (includeParentUser) {
                String[] stringArray2 = new String[2];
                stringArray2[0] = this.getParentUsername();
                stringArray = stringArray2;
                stringArray2[1] = this.destinationUsername;
            } else {
                String[] stringArray3 = new String[1];
                stringArray = stringArray3;
                stringArray3[0] = this.destinationUsername;
            }
            return stringArray;
        }

        public void sendMessageToAllUsersInChat(MessageData messageData) throws FusionException {
            super.sendMessageToAllUsersInChat(messageData);
            this.sessionI.sendMessageBackToUserAsEmote(messageData, EmoteCommand.processMessageVariables(messageData.messageText, this.getParentUsername(), this));
            MessageDataIce messageIce = messageData.toIceObject();
            messageIce.messageText = EmoteCommand.processMessageVariables(messageData.messageText, null, this);
            try {
                this.getDestinationUserPrx().putMessage(messageIce);
            }
            catch (FusionException ex) {
                if (ex.message != null && ex instanceof UserNotOnlineException) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Recipient is offline. Ex=" + (Object)((Object)ex)));
                    }
                    this.sendMessageToOfflineRecipient(messageData);
                }
                throw ex;
            }
        }

        public void sendMessageToOfflineRecipient(MessageData messageData) throws FusionException {
            try {
                OfflineMessageHelper.StorageResult result;
                User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                int srcId = userEJB.getUserID(messageData.source, null);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Sender=" + messageData.source + " id=" + srcId));
                }
                int destId = userEJB.getUserID(messageData.messageDestinations.get((int)0).destination, null);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Recipient=" + messageData.messageDestinations.get((int)0).destination + " id=" + destId));
                }
                if ((result = OfflineMessageHelper.getInstance().scheduleOfflineMessageStorageAndWait(messageData, srcId, destId)).failed()) {
                    throw new FusionException(result.getError());
                }
            }
            catch (FusionException e) {
                throw e;
            }
            catch (Exception e2) {
                log.error((Object)("Exception in ChatSource.sendMessageToOfflineRecipient(): " + e2));
                throw new FusionException(e2.getMessage());
            }
        }

        public void sendMessageToSender(MessageData messageData) throws FusionException {
            this.sessionI.sendMessageBackToUserAsEmote(messageData, EmoteCommand.processMessageVariables(messageData.messageText, this.getParentUsername(), this));
        }

        public PaidEmoteData.EmotePurchaseLocationEnum getEmotePurchaseLocation() {
            return PaidEmoteData.EmotePurchaseLocationEnum.PRIVATE_CHAT_COMMAND;
        }

        private UserPrx getDestinationUserPrx() throws FusionException {
            if (this.destinationUserPrx == null) {
                this.destinationUserPrx = this.sessionI.findUserPrx(this.destinationUsername);
            }
            return this.destinationUserPrx;
        }

        protected String getParentUsernameInternal() throws FusionException {
            return this.sessionI.getUsername();
        }

        public EmoteCommand.ResultType executeEmoteCommandWithState(String emoteCommand, MessageDataIce message) throws FusionException {
            UserPrx userPrx = null;
            String senderUsername = this.getParentUsername();
            userPrx = this.destinationUsername.compareTo(senderUsername) > 0 ? this.sessionI.findUserPrx(senderUsername) : this.getDestinationUserPrx();
            if (userPrx == null) {
                log.error((Object)String.format("Unable to obtain UserPrx to execute emote command with state '%s', sender='%s', dest='%s', msg=%s", emoteCommand, senderUsername, this.destinationUsername, MessageData.toString(message)));
                return EmoteCommand.ResultType.NOTHANDLED;
            }
            return EmoteCommand.ResultType.fromValue(userPrx.executeEmoteCommandWithState(emoteCommand, message, this.getSessionPrx()));
        }

        public void accept(ChatSourceVisitor visitor) throws FusionException {
            visitor.visit(this);
        }

        public void sendToSender(MessageDataIce messageDataIce) throws FusionException {
            this.sessionI.putMessage(messageDataIce);
        }

        public void sendToCounterParties(MessageDataIce messageDataIce) throws FusionException {
            block2: {
                try {
                    this.getDestinationUserPrx().putMessage(messageDataIce);
                }
                catch (UserNotOnlineException e) {
                    if (!log.isDebugEnabled()) break block2;
                    log.debug((Object)("Recipient of sticker from " + messageDataIce.source + " is offline: e=" + (Object)((Object)e)));
                }
            }
        }

        public ClientType getSenderDeviceType() {
            return this.sessionI.getDeviceType();
        }

        public short getSenderClientVersion() {
            return this.sessionI.getClientVersion();
        }
    }

    public static class RemoteGroupChatChatSource
    extends ChatSource {
        protected ChatSourceGroup chatGroup;

        public RemoteGroupChatChatSource(SessionPrx session, ChatSourceGroup chatGroup) {
            super(ChatType.GROUP_CHAT, session);
            this.chatGroup = chatGroup;
        }

        public boolean isUserInChat(String username) throws FusionException {
            return !StringUtil.isBlank(username) && this.chatGroup.isParticipant(username);
        }

        public boolean isUserVisibleInChat(String username) throws FusionException {
            return this.isUserInChat(username);
        }

        public String[] getVisibleUsernamesInChat(boolean includeParentUser) throws FusionException {
            return this.getAllUsernamesInChat(includeParentUser);
        }

        public String[] getAllUsernamesInChat(boolean includeParentUser) throws FusionException {
            return this.chatGroup.getParticipants(includeParentUser ? null : this.getParentUsername());
        }

        public void sendMessageToAllUsersInChat(MessageData messageData) throws FusionException {
            super.sendMessageToAllUsersInChat(messageData);
            String messageText = messageData.messageText;
            MessageDataIce messageIce = messageData.toIceObject();
            messageIce.messageText = EmoteCommand.processMessageVariables(messageText, this.getParentUsername(), this);
            this.sessionPrx.sendMessageBackToUserAsEmote(messageIce);
            messageIce.messageText = EmoteCommand.processMessageVariables(messageText, null, this);
            this.chatGroup.putMessage(messageIce);
        }

        public void sendMessageToSender(MessageData messageData) throws FusionException {
            String messageText = messageData.messageText;
            MessageDataIce messageIce = messageData.toIceObject();
            messageIce.messageText = EmoteCommand.processMessageVariables(messageText, this.getParentUsername(), this);
            this.sessionPrx.sendMessageBackToUserAsEmote(messageIce);
        }

        public PaidEmoteData.EmotePurchaseLocationEnum getEmotePurchaseLocation() {
            return PaidEmoteData.EmotePurchaseLocationEnum.GROUP_CHAT_COMMAND;
        }

        protected String getParentUsernameInternal() throws FusionException {
            return this.sessionPrx.getParentUsername();
        }

        public void accept(ChatSourceVisitor visitor) throws FusionException {
            visitor.visit(this);
        }

        public void sendToSender(MessageDataIce messageDataIce) throws FusionException {
            this.sessionPrx.putMessage(messageDataIce);
        }

        public void sendToCounterParties(MessageDataIce messageDataIce) throws FusionException {
            this.chatGroup.putMessage(messageDataIce);
        }

        public ClientType getSenderDeviceType() {
            return ClientType.fromValue(this.sessionPrx.getDeviceTypeAsInt());
        }

        public short getSenderClientVersion() {
            return this.sessionPrx.getClientVersionIce();
        }
    }

    public static class LocalGroupChatChatSource
    extends ChatSource {
        protected GroupChatPrx groupChatPrx;

        LocalGroupChatChatSource(ChatSourceSession session, GroupChatPrx groupChatPrx) {
            super(ChatType.GROUP_CHAT, session);
            this.groupChatPrx = groupChatPrx;
        }

        public boolean isUserInChat(String username) throws FusionException {
            return !StringUtil.isBlank(username) && this.groupChatPrx.isParticipant(username);
        }

        public boolean isUserVisibleInChat(String username) throws FusionException {
            return this.isUserInChat(username);
        }

        public String[] getVisibleUsernamesInChat(boolean includeParentUser) throws FusionException {
            return this.getAllUsernamesInChat(includeParentUser);
        }

        public String[] getAllUsernamesInChat(boolean includeParentUser) throws FusionException {
            return this.groupChatPrx.getParticipants(includeParentUser ? null : this.getParentUsername());
        }

        public void sendMessageToAllUsersInChat(MessageData messageData) throws FusionException {
            super.sendMessageToAllUsersInChat(messageData);
            this.sessionI.sendMessageBackToUserAsEmote(messageData, EmoteCommand.processMessageVariables(messageData.messageText, this.getParentUsername(), this));
            MessageDataIce messageIce = messageData.toIceObject();
            messageIce.messageText = EmoteCommand.processMessageVariables(messageData.messageText, null, this);
            this.groupChatPrx.putMessage(messageIce);
        }

        public void sendMessageToSender(MessageData messageData) throws FusionException {
            this.sessionI.sendMessageBackToUserAsEmote(messageData, EmoteCommand.processMessageVariables(messageData.messageText, this.getParentUsername(), this));
        }

        public PaidEmoteData.EmotePurchaseLocationEnum getEmotePurchaseLocation() {
            return PaidEmoteData.EmotePurchaseLocationEnum.GROUP_CHAT_COMMAND;
        }

        protected String getParentUsernameInternal() throws FusionException {
            return this.sessionI.getUsername();
        }

        public EmoteCommand.ResultType executeEmoteCommandWithState(String emoteCommand, MessageDataIce message) throws FusionException {
            return EmoteCommand.ResultType.fromValue(this.groupChatPrx.executeEmoteCommandWithState(emoteCommand, message, this.getSessionPrx()));
        }

        public void accept(ChatSourceVisitor visitor) throws FusionException {
            visitor.visit(this);
        }

        public void sendToSender(MessageDataIce messageDataIce) throws FusionException {
            this.sessionI.putMessage(messageDataIce);
        }

        public void sendToCounterParties(MessageDataIce messageDataIce) throws FusionException {
            this.groupChatPrx.putMessage(messageDataIce);
        }

        public ClientType getSenderDeviceType() {
            return this.sessionI.getDeviceType();
        }

        public short getSenderClientVersion() {
            return this.sessionI.getClientVersion();
        }
    }

    public static class RemoteChatRoomChatSource
    extends ChatSource {
        protected ChatSourceRoom chatRoom;

        public RemoteChatRoomChatSource(SessionPrx session, ChatSourceRoom chatRoom) {
            super(ChatType.CHATROOM_CHAT, session);
            this.chatRoom = chatRoom;
        }

        public boolean isUserInChat(String username) throws FusionException {
            return !StringUtil.isBlank(username) && this.chatRoom.isParticipant(username);
        }

        public boolean isUserVisibleInChat(String username) throws FusionException {
            return !StringUtil.isBlank(username) && this.chatRoom.isVisibleParticipant(username);
        }

        public String[] getVisibleUsernamesInChat(boolean includeParentUser) throws FusionException {
            return this.chatRoom.getParticipants(includeParentUser ? null : this.getParentUsername());
        }

        public String[] getAllUsernamesInChat(boolean includeParentUser) throws FusionException {
            return this.chatRoom.getAllParticipants(includeParentUser ? null : this.getParentUsername());
        }

        public void sendMessageToAllUsersInChat(MessageData messageData) throws FusionException {
            if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.CHATROOM_SYNC_ENABLED)) {
                super.sendMessageToAllUsersInChat(messageData);
            }
            String messageText = messageData.messageText;
            MessageDataIce messageIce = messageData.toIceObject();
            messageIce.messageText = EmoteCommand.processMessageVariables(messageText, this.getParentUsername(), this);
            this.sessionPrx.sendMessageBackToUserAsEmote(messageIce);
            messageIce = messageData.toIceObject();
            messageIce.messageText = EmoteCommand.processMessageVariables(messageText, null, this);
            this.chatRoom.putMessage(messageIce, this.sessionPrx.getSessionID());
        }

        public void sendMessageWithTruncationToAllUsersInChat(MessageData messageData, String subMessageToTruncate) throws FusionException {
            MessageDataIce messageIce = messageData.toIceObject();
            int maxLength = this.chatRoom.getMaximumMessageLength(this.getParentUsername());
            messageIce.messageText = this.getTruncatedMessage(messageData.messageText, subMessageToTruncate, maxLength, this.getParentUsername());
            this.sessionPrx.sendMessageBackToUserAsEmote(messageIce);
            messageIce = messageData.toIceObject();
            messageIce.messageText = this.getTruncatedMessage(messageData.messageText, subMessageToTruncate, maxLength, null);
            this.chatRoom.putMessage(messageIce, this.sessionPrx.getSessionID());
        }

        public void sendMessageToSender(MessageData messageData) throws FusionException {
            String messageText = messageData.messageText;
            MessageDataIce messageIce = messageData.toIceObject();
            messageIce.messageText = EmoteCommand.processMessageVariables(messageText, this.getParentUsername(), this);
            this.sessionPrx.sendMessageBackToUserAsEmote(messageIce);
        }

        public PaidEmoteData.EmotePurchaseLocationEnum getEmotePurchaseLocation() {
            return PaidEmoteData.EmotePurchaseLocationEnum.CHATROOM_COMMAND;
        }

        protected String getParentUsernameInternal() throws FusionException {
            return this.sessionPrx.getParentUsername();
        }

        public void accept(ChatSourceVisitor visitor) throws FusionException {
            visitor.visit(this);
        }

        public void sendToSender(MessageDataIce messageDataIce) throws FusionException {
            this.sessionPrx.putMessage(messageDataIce);
        }

        public void sendToCounterParties(MessageDataIce messageDataIce) throws FusionException {
            this.chatRoom.putMessage(messageDataIce, this.sessionI.getSessionID());
        }

        public ClientType getSenderDeviceType() {
            return ClientType.fromValue(this.sessionPrx.getDeviceTypeAsInt());
        }

        public short getSenderClientVersion() {
            return this.sessionPrx.getClientVersionIce();
        }
    }

    public static class LocalChatRoomChatSource
    extends ChatSource {
        protected ChatRoomPrx chatRoomPrx;

        LocalChatRoomChatSource(ChatSourceSession session, ChatRoomPrx chatRoomPrx) {
            super(ChatType.CHATROOM_CHAT, session);
            this.chatRoomPrx = chatRoomPrx;
        }

        public boolean isUserInChat(String username) throws FusionException {
            return !StringUtil.isBlank(username) && this.chatRoomPrx.isParticipant(username);
        }

        public boolean isUserVisibleInChat(String username) throws FusionException {
            return !StringUtil.isBlank(username) && this.chatRoomPrx.isVisibleParticipant(username);
        }

        public String[] getVisibleUsernamesInChat(boolean includeParentUser) throws FusionException {
            return this.chatRoomPrx.getParticipants(includeParentUser ? null : this.getParentUsername());
        }

        public String[] getAllUsernamesInChat(boolean includeParentUser) throws FusionException {
            return this.chatRoomPrx.getAllParticipants(includeParentUser ? null : this.getParentUsername());
        }

        public void sendMessageToAllUsersInChat(MessageData messageData) throws FusionException {
            if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.CHATROOM_SYNC_ENABLED)) {
                super.sendMessageToAllUsersInChat(messageData);
            }
            MessageDataIce messageIce = messageData.toIceObject();
            this.sessionI.sendMessageBackToUserAsEmote(messageData, EmoteCommand.processMessageVariables(messageData.messageText, this.getParentUsername(), this));
            messageIce.messageText = EmoteCommand.processMessageVariables(messageData.messageText, null, this);
            this.chatRoomPrx.putMessage(messageIce, this.sessionI.getSessionID());
        }

        public void sendMessageWithTruncationToAllUsersInChat(MessageData messageData, String subMessageToTruncate) throws FusionException {
            MessageDataIce messageIce = messageData.toIceObject();
            int maxLength = this.chatRoomPrx.getMaximumMessageLength(this.getParentUsername());
            this.sessionI.sendMessageBackToUserAsEmote(messageData, this.getTruncatedMessage(messageData.messageText, subMessageToTruncate, maxLength, this.getParentUsername()));
            messageIce.messageText = this.getTruncatedMessage(messageData.messageText, subMessageToTruncate, maxLength, null);
            this.chatRoomPrx.putMessage(messageIce, this.sessionI.getSessionID());
        }

        public void sendMessageToSender(MessageData messageData) throws FusionException {
            this.sessionI.sendMessageBackToUserAsEmote(messageData, EmoteCommand.processMessageVariables(messageData.messageText, this.getParentUsername(), this));
        }

        public PaidEmoteData.EmotePurchaseLocationEnum getEmotePurchaseLocation() {
            return PaidEmoteData.EmotePurchaseLocationEnum.CHATROOM_COMMAND;
        }

        protected String getParentUsernameInternal() throws FusionException {
            return this.sessionI.getUsername();
        }

        public EmoteCommand.ResultType executeEmoteCommandWithState(String emoteCommand, MessageDataIce message) throws FusionException {
            return EmoteCommand.ResultType.fromValue(this.chatRoomPrx.executeEmoteCommandWithState(emoteCommand, message, this.getSessionPrx()));
        }

        public void accept(ChatSourceVisitor visitor) throws FusionException {
            visitor.visit(this);
        }

        public ChatRoomPrx getChatRoomPrx() {
            return this.chatRoomPrx;
        }

        public void sendToSender(MessageDataIce messageDataIce) throws FusionException {
            this.sessionI.putMessage(messageDataIce);
        }

        public void sendToCounterParties(MessageDataIce messageDataIce) throws FusionException {
            this.chatRoomPrx.putMessage(messageDataIce, this.sessionI.getSessionID());
        }

        public ClientType getSenderDeviceType() {
            return this.sessionI.getDeviceType();
        }

        public short getSenderClientVersion() {
            return this.sessionI.getClientVersion();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ChatSourceType {
        LOCAL_SESSION,
        REMOTE_SESSION;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ChatType {
        PRIVATE_CHAT(1),
        GROUP_CHAT(2),
        CHATROOM_CHAT(4);

        private int value;

        private ChatType(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static ChatType fromValue(int value) {
            for (ChatType e : ChatType.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }

        public static ChatType fromDestinationType(MessageDestinationData.TypeEnum messageDestinationType) {
            if (messageDestinationType == MessageDestinationData.TypeEnum.INDIVIDUAL) {
                return PRIVATE_CHAT;
            }
            if (messageDestinationType == MessageDestinationData.TypeEnum.CHAT_ROOM) {
                return CHATROOM_CHAT;
            }
            if (messageDestinationType == MessageDestinationData.TypeEnum.GROUP) {
                return GROUP_CHAT;
            }
            return null;
        }

        public static int or(ChatType ... types) {
            int v = 0;
            for (int i = 0; i < types.length; ++i) {
                v |= types[i].value();
            }
            return v;
        }

        public static int allTypes() {
            return ChatType.or(ChatType.values());
        }

        public boolean isSupported(int types) {
            return (types & this.value) == this.value;
        }
    }
}

