/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.chatsync.ChatMessage;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncPipelineOp;
import com.projectgoth.fusion.chatsync.ChatSyncStats;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.ChatSyncStorePipeline;
import com.projectgoth.fusion.chatsync.ParticipantList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.gateway.packet.chatsync.FusionPktChat;
import com.projectgoth.fusion.slice.ChatDefinitionIce;
import com.projectgoth.fusion.slice.FusionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatDefinition
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(ChatDefinition.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String CHATROOM_ID_PREFIX = "CR#";
    private String chatStorageID;
    private String[] participantUsernames;
    private Byte chatType;
    private Integer unreadMessageCount;
    private Integer contactID;
    private String groupOwner;
    private Byte isClosedChat;
    private String displayGUID;
    private Byte messageType;
    private String chatName;
    private Boolean isPassivatedChat;
    private int writeOpCount;
    private ChatMessage latestMessage;

    public ChatDefinition() {
    }

    public ChatDefinition(String storageID) throws FusionException {
        this.chatStorageID = storageID;
        this.chatType = this.isPrivateChat() ? Byte.valueOf((byte)MessageDestinationData.TypeEnum.INDIVIDUAL.value()) : (this.isChatRoomDef() ? Byte.valueOf((byte)MessageDestinationData.TypeEnum.CHAT_ROOM.value()) : Byte.valueOf((byte)MessageDestinationData.TypeEnum.GROUP.value()));
    }

    public ChatDefinition(String chatID, byte chatType) throws FusionException {
        if (chatType == (byte)MessageDestinationData.TypeEnum.INDIVIDUAL.value()) {
            throw new FusionException("Invalid chat type for this ctor");
        }
        this.chatStorageID = this.userSuppliedToInternalChatID(chatType, null, chatID);
        this.chatType = chatType;
    }

    public ChatDefinition(String id, byte chatType, String username) throws FusionException {
        this.chatStorageID = this.userSuppliedToInternalChatID(chatType, username, id);
        this.chatType = chatType;
    }

    private String userSuppliedToInternalChatID(byte chatType, String username, String userSuppliedChatID) throws FusionException {
        if (chatType == (byte)MessageDestinationData.TypeEnum.INDIVIDUAL.value()) {
            String realChatID = this.makePrivateChatKey(username, userSuppliedChatID);
            if (log.isDebugEnabled()) {
                log.debug("user supplied chatID=" + userSuppliedChatID + " converted to=" + realChatID);
            }
            return realChatID;
        }
        if (chatType == (byte)MessageDestinationData.TypeEnum.CHAT_ROOM.value()) {
            return this.makeChatRoomKey(userSuppliedChatID);
        }
        return userSuppliedChatID;
    }

    private String makePrivateChatKey(String username, String otherUser) {
        if (username.compareTo(otherUser) < 0) {
            return username + COLON + otherUser;
        }
        return otherUser + COLON + username;
    }

    private String makeChatRoomKey(String chatRoomName) throws FusionException {
        try {
            int crID = MemCacheOrEJB.getChatRoomID(chatRoomName);
            return CHATROOM_ID_PREFIX + crID;
        }
        catch (FusionException e) {
            throw e;
        }
        catch (Exception e) {
            String errStr = "makeChatRoomKey for chatRoomName=" + chatRoomName + ": " + e;
            log.error(errStr, e);
            throw new FusionException(errStr);
        }
    }

    public ChatDefinition(ChatDefinitionIce cdi) throws FusionException {
        this.chatStorageID = cdi.chatStorageID;
        this.participantUsernames = cdi.participantUsernames;
        this.chatType = cdi.chatType;
        this.unreadMessageCount = cdi.unreadMessageCount;
        this.contactID = cdi.contactID;
        this.groupOwner = cdi.groupOwner;
        this.isClosedChat = cdi.isClosedChat == -128 ? null : Byte.valueOf(cdi.isClosedChat);
        this.displayGUID = cdi.displayGUID;
        this.messageType = cdi.messageType;
        this.chatName = cdi.chatName;
        this.isPassivatedChat = cdi.isPassivatedChat == -128 ? null : Boolean.valueOf(cdi.isPassivatedChat == 1);
    }

    public ChatDefinition(String username, String chatID, String[] participantUsernames, Byte chatType, Integer unreadMessageCount, Integer contactID, String groupOwner, Byte isClosedChat, String displayGUID, Byte messageType) throws FusionException {
        this(username, chatID, participantUsernames, chatType, unreadMessageCount, contactID, groupOwner, isClosedChat, displayGUID, messageType, null);
    }

    public ChatDefinition(String username, String chatID, String[] participantUsernames, Byte chatType, Integer unreadMessageCount, Integer contactID, String groupOwner, Byte isClosedChat, String displayGUID, Byte messageType, String chatName) throws FusionException {
        this.chatStorageID = this.userSuppliedToInternalChatID(chatType, username, chatID);
        this.participantUsernames = participantUsernames;
        this.chatType = chatType;
        this.unreadMessageCount = unreadMessageCount;
        this.contactID = contactID;
        this.groupOwner = groupOwner;
        this.isClosedChat = isClosedChat;
        this.displayGUID = displayGUID;
        this.messageType = messageType;
        this.chatName = chatName;
    }

    public ChatDefinition(String chatStorageID, ChatSyncStore store) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug("Loading ChatDefinition from redis with storage id=" + chatStorageID);
        }
        try {
            this.chatStorageID = chatStorageID;
            ChatSyncStore[] stores = new ChatSyncStore[]{store};
            this.retrieve(stores);
            if (log.isDebugEnabled()) {
                log.debug("Loaded ChatDefinition from redis with storage id=" + chatStorageID);
            }
        }
        catch (ChatDefinitionNotFoundException e) {
            throw e;
        }
        catch (FusionException e) {
            log.error("Exception loading ChatDefinition id=" + chatStorageID, (Throwable)((Object)e));
            throw e;
        }
        catch (Exception e) {
            log.error("Exception loading ChatDefinition id=" + chatStorageID, e);
            throw new FusionException(e.getMessage());
        }
    }

    private String loadOptionalString(Object oVal, String fieldName) throws FusionException {
        return this.loadString(oVal, fieldName);
    }

    private String loadString(Object oVal, String fieldName) throws FusionException {
        String val = Redis.getPipelineResult(oVal);
        if (log.isDebugEnabled()) {
            log.debug(fieldName + " val=" + val);
        }
        return val;
    }

    private Byte loadOptionalByte(Object oVal, String fieldName) throws FusionException {
        String val = Redis.getPipelineResult(oVal);
        if (log.isDebugEnabled()) {
            log.debug(fieldName + " val=" + val);
        }
        if (val == null || val.equals("null")) {
            return null;
        }
        return Byte.parseByte(val);
    }

    private Integer loadInteger(Object oVal, String fieldName) throws FusionException {
        String val = Redis.getPipelineResult(oVal);
        if (log.isDebugEnabled()) {
            log.debug(fieldName + " val=" + val);
        }
        if (val == null || val.equals("null")) {
            return null;
        }
        return Integer.parseInt(val);
    }

    private Long loadLong(Object oVal, String fieldName) throws FusionException {
        String val = Redis.getPipelineResult(oVal);
        if (log.isDebugEnabled()) {
            log.debug(fieldName + " val=" + val);
        }
        if (val == null || val.equals("null")) {
            return null;
        }
        return Long.parseLong(val);
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.CONVERSATION;
    }

    @Override
    public String getKey() {
        return this.chatStorageID;
    }

    @Override
    public String getValue() {
        return null;
    }

    public String getChatID() {
        return this.chatStorageID;
    }

    public byte getChatType() {
        return this.chatType;
    }

    public String getGroupOwner() {
        return this.groupOwner;
    }

    public String[] getParticipantUsernames() {
        return this.participantUsernames;
    }

    public String getChatName() {
        return this.chatName;
    }

    private void renewExpiry(SystemPropertyEntities.ChatSyncSettings toggle, ChatSyncStore store) throws FusionException {
        if (SystemProperty.getBool(toggle)) {
            int hours = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CHAT_DEFINITION_EXPIRY_HOURS);
            store.expire(this, hours * 3600);
            ++this.writeOpCount;
        }
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
        for (ChatSyncStore store : stores) {
            if (log.isDebugEnabled()) {
                log.debug("Storing ChatDefinition:  participantUsernames=" + this.participantUsernames + " chatType=" + this.chatType);
            }
            store.setMaster();
            ArrayList<ChatSyncPipelineOp> writeOps = new ArrayList<ChatSyncPipelineOp>();
            writeOps.add(new ChatSyncPipelineOp(this, ChatSyncPipelineOp.OpType.WRITE));
            store.pipelinedBinary(writeOps);
            ChatSyncStats.getInstance().incrementTotalChatsStored();
            log.debug("Stored ChatDefinition");
        }
    }

    @Override
    public void unpack(String storedKey, String storedValue) throws FusionException {
    }

    public FusionPktChat toFusionPktChat(short txnID, int chatListVersion, long chatListTimestamp, String recipientUsername) throws FusionException {
        FusionPktChat pkt = new FusionPktChat();
        pkt.setTransactionId(txnID);
        String returnChatID = this.internalToExternalChatID(recipientUsername);
        pkt.setChatIdentifier(returnChatID);
        if (!StringUtil.isBlank(this.chatName)) {
            pkt.setChatDisplayName(this.chatName);
            pkt.setIsRenamedChat((byte)1);
        } else {
            this.autoGenerateChatDisplayName(pkt, recipientUsername);
            pkt.setIsRenamedChat((byte)0);
        }
        pkt.setChatType(this.chatType);
        if (this.unreadMessageCount != null) {
            pkt.setUnreadMessageCount(this.unreadMessageCount);
        }
        if (this.contactID != null) {
            pkt.setContactId(this.contactID);
        }
        if (this.groupOwner != null) {
            pkt.setGroupOwner(this.groupOwner);
        }
        if (this.isClosedChat != null) {
            pkt.setIsClosedChat(this.isClosedChat);
        }
        if (this.displayGUID != null) {
            if (log.isDebugEnabled()) {
                log.debug("ChatDefinition.toFusionPktChat: setting pkt.displayGUID=" + this.displayGUID);
            }
            pkt.setDisplayGUID(this.displayGUID);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("ChatDefinition.toFusionPktChat: set pkt.displayGUID to blank string");
            }
            pkt.setDisplayGUID("");
        }
        if (this.messageType != null) {
            pkt.setMessageType(this.messageType);
        }
        if (this.latestMessage != null) {
            pkt.setTimestamp(this.latestMessage.getTimestamp());
        } else {
            pkt.setTimestamp(System.currentTimeMillis());
        }
        pkt.setChatListVersion(chatListVersion);
        pkt.setChatListTimestamp(chatListTimestamp);
        if (this.isPassivatedChat != null) {
            pkt.setIsPassivatedChat(this.isPassivatedChat != false ? (byte)1 : 0);
        }
        return pkt;
    }

    private void autoGenerateChatDisplayName(FusionPktChat pkt, String recipientUsername) {
        if (this.participantUsernames != null) {
            String commaSep = "";
            for (String p : this.participantUsernames) {
                if (p.equals(recipientUsername)) continue;
                commaSep = commaSep + (commaSep.length() != 0 ? COMMA : "") + p;
            }
            pkt.setChatDisplayName(commaSep);
        }
    }

    public String internalToExternalChatID(String parentUsername) throws FusionException {
        if (this.chatType == (byte)MessageDestinationData.TypeEnum.INDIVIDUAL.value()) {
            String[] particips = this.chatStorageID.split(COLON);
            return parentUsername.equals(particips[0]) ? particips[1] : particips[0];
        }
        if (this.chatType == (byte)MessageDestinationData.TypeEnum.CHAT_ROOM.value()) {
            try {
                String chatRoomID = this.chatStorageID.substring(CHATROOM_ID_PREFIX.length());
                return MemCacheOrEJB.getChatRoomName(Integer.parseInt(chatRoomID));
            }
            catch (Exception e) {
                String errStr = "Unable to find chatroom name for chatStorageID=" + this.chatStorageID + ": " + e;
                log.error(errStr, e);
                throw new FusionException(errStr);
            }
        }
        return this.chatStorageID;
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        try {
            if (this.isChatRoomDef()) {
                this.retrieveChatroom(stores);
            } else {
                this.retrieveInner(stores);
            }
        }
        catch (FusionException fe) {
            throw fe;
        }
        catch (Exception e) {
            log.error("For chatStorageID=" + this.chatStorageID, e);
            throw new FusionException(e.getMessage());
        }
    }

    private void retrieveChatroom(ChatSyncStore[] stores) throws FusionException {
        if (!SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.CHATROOM_SYNC_ENABLED)) {
            throw new ChatDefinitionNotFoundException(this.chatStorageID);
        }
        ChatSyncStore store = stores[0];
        this.chatType = (byte)MessageDestinationData.TypeEnum.CHAT_ROOM.value();
        try {
            String chatRoomID = this.chatStorageID.substring(CHATROOM_ID_PREFIX.length());
            this.chatName = MemCacheOrEJB.getChatRoomName(Integer.parseInt(chatRoomID));
        }
        catch (Exception e) {
            String sErr = "Unable to retrieve chatroom name for stored chatroom=" + this.chatStorageID + ": " + e;
            log.error(sErr, e);
            throw new FusionException(sErr);
        }
        store.setSlave();
        ChatMessage msgKey = new ChatMessage(this);
        Set<byte[]> msgs = store.zrangeBinary(msgKey, -1, -1);
        if (msgs != null && msgs.size() != 0) {
            ArrayList<byte[]> msgsList = new ArrayList<byte[]>(msgs);
            this.latestMessage = new ChatMessage(this, (byte[])msgsList.get(0));
        }
    }

    private void retrieveInner(ChatSyncStore[] stores) throws FusionException {
        ChatSyncStore store = stores[0];
        store.setMaster();
        this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_READ_ENABLED, store);
        store.setSlave();
        List<Object> results = store.pipelinedBinary(this, new ChatSyncStorePipeline(){

            public void execute(ChatSyncEntity entity, ChatSyncStore pipelineStore) throws FusionException {
                entity.retrievePipeline(pipelineStore);
            }
        });
        if (results == null) {
            throw new ChatDefinitionNotFoundException(this.chatStorageID);
        }
        this.loadPipeline(results, 0);
        ChatSyncStats.getInstance().incrementTotalChatsRetrieved();
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
        pipelineStore.hget(this, FieldName.CHAT_TYPE.toString());
        pipelineStore.hget(this, FieldName.UNREAD_MESSAGE_COUNT.toString());
        pipelineStore.hget(this, FieldName.CONTACT_ID.toString());
        pipelineStore.hget(this, FieldName.GROUP_OWNER.toString());
        pipelineStore.hget(this, FieldName.IS_CLOSED_CHAT.toString());
        pipelineStore.hget(this, FieldName.DISPLAY_GUID.toString());
        pipelineStore.hget(this, FieldName.MESSAGE_TYPE.toString());
        pipelineStore.hget(this, FieldName.CHAT_NAME.toString());
        ParticipantList participants = new ParticipantList(this);
        participants.retrievePipeline(pipelineStore);
        ChatMessage msgKey = new ChatMessage(this);
        pipelineStore.zrangeBinary(msgKey, -1, -1);
    }

    @Override
    public int loadPipeline(List<Object> pipelineResults, int startIndex) throws FusionException {
        ArrayList msgs;
        Object oChatType;
        int index = startIndex;
        if ((oChatType = pipelineResults.get(index++)) == null) {
            throw new ChatDefinitionNotFoundException(this.chatStorageID);
        }
        this.chatType = this.loadOptionalByte(oChatType, FieldName.CHAT_TYPE.toString());
        if (log.isDebugEnabled()) {
            log.debug("ChatDefinition: loaded chatType=" + this.chatType);
        }
        this.unreadMessageCount = this.loadInteger(pipelineResults.get(index++), FieldName.UNREAD_MESSAGE_COUNT.toString());
        this.contactID = this.loadInteger(pipelineResults.get(index++), FieldName.CONTACT_ID.toString());
        this.groupOwner = this.loadString(pipelineResults.get(index++), FieldName.GROUP_OWNER.toString());
        if (log.isDebugEnabled()) {
            log.debug("ChatDefinition: loaded groupOwner=" + this.groupOwner);
        }
        this.isClosedChat = this.loadOptionalByte(pipelineResults.get(index++), FieldName.IS_CLOSED_CHAT.toString());
        if (log.isDebugEnabled()) {
            log.debug("ChatDefinition: loaded closedChat=" + this.isClosedChat);
        }
        this.displayGUID = this.loadString(pipelineResults.get(index++), FieldName.DISPLAY_GUID.toString());
        this.messageType = this.loadOptionalByte(pipelineResults.get(index++), FieldName.MESSAGE_TYPE.toString());
        this.chatName = this.loadOptionalString(pipelineResults.get(index++), FieldName.CHAT_NAME.toString());
        ParticipantList participants = new ParticipantList(this);
        index = participants.loadPipeline(pipelineResults, index);
        if (participants.getUsernames() != null && participants.getUsernames().length != 0) {
            this.participantUsernames = participants.getUsernames();
        }
        if ((msgs = new ArrayList((Set)pipelineResults.get(index++))).size() != 0) {
            this.latestMessage = new ChatMessage(this, (byte[])msgs.get(0));
        }
        return index;
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
        this.writeOpCount = 0;
        pipelineStore.multi(this);
        ++this.writeOpCount;
        ParticipantList pl = new ParticipantList(this, this.participantUsernames);
        pl.storePipeline(pipelineStore);
        this.writeOpCount += pl.getWriteOpCount();
        pipelineStore.hset(this, FieldName.CHAT_TYPE.toString(), Byte.toString(this.chatType));
        ++this.writeOpCount;
        if (this.unreadMessageCount != null) {
            pipelineStore.hset(this, FieldName.UNREAD_MESSAGE_COUNT.toString(), this.unreadMessageCount.toString());
        } else {
            pipelineStore.hdel(this, FieldName.UNREAD_MESSAGE_COUNT.toString());
        }
        ++this.writeOpCount;
        if (this.contactID != null) {
            pipelineStore.hset(this, FieldName.CONTACT_ID.toString(), this.contactID.toString());
        } else {
            pipelineStore.hdel(this, FieldName.CONTACT_ID.toString());
        }
        ++this.writeOpCount;
        if (this.groupOwner != null) {
            pipelineStore.hset(this, FieldName.GROUP_OWNER.toString(), this.groupOwner.toString());
        } else {
            pipelineStore.hdel(this, FieldName.GROUP_OWNER.toString());
        }
        ++this.writeOpCount;
        if (this.isClosedChat != null) {
            pipelineStore.hset(this, FieldName.IS_CLOSED_CHAT.toString(), Byte.toString(this.isClosedChat));
        } else {
            pipelineStore.hdel(this, FieldName.IS_CLOSED_CHAT.toString());
        }
        ++this.writeOpCount;
        if (this.displayGUID != null) {
            pipelineStore.hset(this, FieldName.DISPLAY_GUID.toString(), this.displayGUID.toString());
        } else {
            pipelineStore.hdel(this, FieldName.DISPLAY_GUID.toString());
        }
        ++this.writeOpCount;
        if (this.messageType != null) {
            pipelineStore.hset(this, FieldName.MESSAGE_TYPE.toString(), this.messageType.toString());
        } else {
            pipelineStore.hdel(this, FieldName.MESSAGE_TYPE.toString());
        }
        ++this.writeOpCount;
        if (this.chatName != null) {
            pipelineStore.hset(this, FieldName.CHAT_NAME.toString(), this.chatName.toString());
        } else {
            pipelineStore.hdel(this, FieldName.CHAT_NAME.toString());
        }
        ++this.writeOpCount;
        this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_WRITE_ENABLED, pipelineStore);
        pipelineStore.execTxn(this);
        ++this.writeOpCount;
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        return startIndex + this.writeOpCount;
    }

    public ChatDefinitionIce toIceObject() {
        ChatDefinitionIce cdi = new ChatDefinitionIce();
        cdi.chatStorageID = this.chatStorageID;
        cdi.participantUsernames = this.participantUsernames;
        cdi.chatType = this.chatType;
        cdi.unreadMessageCount = this.unreadMessageCount == null ? Integer.MIN_VALUE : this.unreadMessageCount;
        cdi.contactID = this.contactID == null ? Integer.MIN_VALUE : this.contactID;
        cdi.groupOwner = this.groupOwner;
        cdi.isClosedChat = (byte)(this.isClosedChat == null ? -128 : (int)this.isClosedChat.byteValue());
        cdi.displayGUID = this.displayGUID;
        cdi.messageType = (byte)(this.messageType == null ? -128 : (int)this.messageType.byteValue());
        cdi.isPassivatedChat = (byte)(this.isPassivatedChat == null ? -128 : (this.isPassivatedChat != false ? 1 : 0));
        return cdi;
    }

    public Long getLatestMessageTimestamp() {
        if (this.latestMessage == null) {
            return null;
        }
        return this.latestMessage.getTimestamp();
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    @Override
    public boolean canRetryReads() throws FusionException {
        return false;
    }

    @Override
    public boolean canRetryWrites() throws FusionException {
        return false;
    }

    public boolean isPrivateChat() {
        if (this.chatType != null && this.chatType == (byte)MessageDestinationData.TypeEnum.INDIVIDUAL.value()) {
            return true;
        }
        return this.chatStorageID.contains(COLON);
    }

    public void setIsPassivatedChat(boolean isPassivated) {
        this.isPassivatedChat = isPassivated;
    }

    public boolean isChatRoomDef() {
        if (this.chatType != null && this.chatType == (byte)MessageDestinationData.TypeEnum.CHAT_ROOM.value()) {
            return true;
        }
        return this.chatStorageID.startsWith(CHATROOM_ID_PREFIX);
    }

    public boolean isGroupChat() {
        return false == this.isPrivateChat() && false == this.isChatRoomDef();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum FieldName {
        CHAT_TYPE,
        UNREAD_MESSAGE_COUNT,
        CONTACT_ID,
        GROUP_OWNER,
        IS_CLOSED_CHAT,
        DISPLAY_GUID,
        MESSAGE_TYPE,
        TIMESTAMP,
        CHAT_LIST_VERSION,
        CHAT_LIST_TIMESTAMP,
        CHAT_NAME;

    }

    public class ChatDefinitionNotFoundException
    extends FusionException {
        private String chatID;

        public String getChatID() {
            return this.chatID;
        }

        public ChatDefinitionNotFoundException(String chatID) {
            this.chatID = chatID;
        }
    }
}

