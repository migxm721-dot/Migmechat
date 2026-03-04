module com {
module projectgoth {
module fusion {
module slice {


///////////////////////////////////////////////////////////
// Start of exceptions

exception FusionException {
	string message;
};

exception FusionServerException extends FusionException {
	int errorCode;
};

exception FusionBusinessException extends FusionException {
	int errorCode;
};

exception FusionExceptionWithErrorCauseCode extends FusionException {
	string errorCauseCode;
};

exception FusionExceptionWithRefCode extends FusionExceptionWithErrorCauseCode {
	string errorRef;
};

exception ObjectNotFoundException extends FusionException {};
exception ObjectExistsException extends FusionException {};
exception InsufficientFundsExcepion extends FusionException {};
exception NoLongerInGroupChatExceptionIce extends FusionException {};

// End of exceptions
///////////////////////////////////////////////////////////

sequence<byte> ByteArray;
sequence<string> StringArray;
sequence<int> IntArray;
dictionary<string, string> ParamMap;

///////////////////////////////////////////////////////////
// Start of data objects

struct UserDataIce {
	int userID;
	string username;
	long dateRegistered;
	string password;
	string displayName;
	string displayPicture;
	string avatar;
	string statusMessage;
	int countryID;
	string language;
	string emailAddress;
	int onMailingList;
	int chatRoomAdmin;
	int chatRoomBans;
	string registrationIPAddress;
	string registrationDevice;
	long firstLoginDate;
	long lastLoginDate;
	int failedLoginAttempts;
	int failedActivationAttempts;
	string mobilePhone;
	string mobileDevice;
	string userAgent;
	int mobileVerified;
	string verificationCode;
	int emailActivated;
	int emailAlert;
	int emailAlertSent;
	long emailActivationDate;
	int allowBuzz;
	double UTCOffset;
	int type;
	int affiliateID;
	string merchantCreated;
	string referredBy;
	int referralLevel;
	int bonusProgramID;
	string currency;
	double balance;
	double fundedBalance;
	string notes;
	int status;
	string fullbodyAvatar;
	int accountVerified;
	int accountType;
	string aboutMeVerified; 
	int emailVerified;
};

struct MessageDestinationDataIce {
	int id;
	int messageID;
	int contactID;
	int type;
	string destination;
	int IDDCode;
	double cost;
	int gateway;
	long dateDispatched;
	string providerTransactionID;
	int status;
};

sequence<MessageDestinationDataIce> MessageDestinationSequence;

struct MessageDataIce {
	int id;
	string username;
	long dateCreated;
	long requestReceivedTimestamp; // the time the request that forms this messagedata is received by gateway
	int type;
	string messageText;
	int messageColour;
	int contentType;
	ByteArray binaryData;
	int sendReceive;
	int sourceContactID;
	string source;
	int sourceType;
	string sourceDisplayPicture;
	int sourceColour;
	int fromAdministrator;
	StringArray emoticonKeys;
	MessageDestinationSequence messageDestinations;
	string guid; // BE-974
	// BE-1230/1250: chat sync extension fields
	long messageTimestamp;
	string groupChatName;
	string groupChatOwner;
	byte emoteContentType; //BE-1235
	string mimeType;
	string mimeTypeData;
};

sequence<MessageDataIce> MessageDataSequence;

/** pt75161644 */
struct MessageStatusEventIce {
	int messageType;
	string messageSource;
	int messageDestinationType;    
	string messageDestination;
	string messageGUID;
    int messageStatus;
    bool serverGenerated;
    long messageTimestamp;
};

/** pt75161644 */
sequence<MessageStatusEventIce> MessageStatusEventSequence;

struct PresenceAndCapabilityIce {
	int fusionPresence;
	int msnPresence;
	int aimPresence;
	int yahooPresence;
	int gtalkPresence;
	int facebookPresence;
};

struct ContactDataIce {
	int id;
	string username;
	string displayName;
	string firstName;
	string lastName;
	string fusionUsername;
	string msnUsername;
	string aimUsername;
	string yahooUsername;
	string facebookUsername;
	string gtalkUsername;
	string emailAddress;
	string mobilePhone;
	string homePhone;
	string officePhone;
	int defaultIM;
	int defaultPhoneNumber;
	int contactGroupId;
	int shareMobilePhone;
	int displayOnPhone;
	int status;
	int fusionPresence;
	int msnPresence;
	int aimPresence;
	int yahooPresence;
	int facebookPresence;
	int gtalkPresence;
	string displayPicture;
	string statusMessage;
	long statusTimeStamp;
};

sequence<ContactDataIce> ContactDataIceArray;

struct ContactGroupDataIce {
	int id;
	string username;
	string name;
};

sequence<ContactGroupDataIce> ContactGroupDataIceArray;

struct ContactList {
	ContactGroupDataIceArray contactGroups;
	ContactDataIceArray contacts;
	int version;
};

struct CallDataIce {
	int id;
	string username;
	int contactID;
	long dateCreated;
	string source;
	int sourceType;
	int sourceProtocol;
	int sourceIDDCode;
	string destination;
	int destinationType;
	int destinationProtocol;
	int destinationIDDCode;
	int makeReceive;
	int initialLeg;
	long sourceDuration;
	long destinationDuration;
	long billedDuration; 
	double signallingFee;
	double rate;
	int type;
	int claimable;
	int gateway;
	int sourceProvider;
	int destinationProvider;
	int failReasonCode;
	string failReason;
	int status;
	string didNumber;
	int destinationFirstProvider;
	int destinationNextProvider;
	string sourceDialCommand;
	string destinationDialCommand;
	string destinationFirstDialCommand;
	string destinationNextDialCommand;
	int maxDuration;
};

sequence<CallDataIce> CallDataIceArray;

struct SystemSMSDataIce {
	int id;
	string username;
	long dateCreated;
	int type;
	int subType;
	string source;
	string destination;
	int IDDCode;
	string messageText;
	int gateway;
	long dateDispatched;
	string providerTransactionID;
	int status;
	string registrationIP;
};

struct ChatRoomDataIce {
	int id;
	string name;
	string description;
	int type;
	string creator;
	int primaryCountryID;
	int secondaryCountryID;
	int groupID;
	int locationID;
	int botID;
	int adultOnly;
	int maximumSize;
	int userOwned;
	string newOwner;
	int allowKicking;
	int allowUserKeywords;
	int allowBots;
	string language;
	long dateCreated;
	long dateLastAccessed;
	int status;
	int size;
	StringArray keywords;
	int themeID;
	// [VC-43] /lock command, [VC-44] /unlock command, Zehua, 2011/01/20
	// the user who locks the chat room, null if room is not locked. not persisted to DB
	string lockUser;
	// [VC-34] [VC-35] /announce commands, Zehua, 2011/01/27 <begin>
	// the user who made the chat room announcement, null if no announcement
	string announcer;
	// the current announcement in the chat room, null if no announcement
	string announceMessage;
	int minMigLevel;
	string rateLimitByIp;
	int blockPeriodByIpInSeconds;
};

sequence<ChatRoomDataIce> ChatRoomDataIceArray;

struct CurrencyDataIce {
	string code;
	string name;
	string symbol;
	double exchangeRate;
	long lastUpdated;
};

struct SessionStatisticsIce {
	int uniquePrivateChatUsers;
	int profileEdits;
};

struct SessionIce {
	int userID;
	string username;
	string language;
	int sourceCountryID;
	bool authenticated;
	int deviceType; // DeviceEnum
	int connectionType; // ConnectionEnum
	int port; 
	int remotePort;
	string ipAddress;
	string mobileDevice;
	short clientVersion;
	long startDateTime;
	long endDateTime;
};

class UserEventIce {
	long timestamp;
	string generatingUsername;
	string generatingUserDisplayPicture;
	string text;
};

sequence<UserEventIce> UserEventIceArray;

class AddingFriendUserEventIce extends UserEventIce {
	string friend1;
};

class AddingTwoFriendsUserEventIce extends AddingFriendUserEventIce {
	string friend2;
};

class AddingMultipleFriendsUserEventIce extends AddingTwoFriendsUserEventIce {
	int additionalFriends;
};

class PurchasedVirtualGoodsUserEventIce extends UserEventIce {
	byte itemType;
	int itemId;
	string itemName;
};

class ShortTextStatusUserEventIce extends UserEventIce {
	string status;
};

class ProfileUpdatedUserEventIce extends UserEventIce {
};

class PhotoUploadedUserEventIce extends UserEventIce {
	int scrapbookid;
	string title;
};

class CreatedChatroomUserEventIce extends UserEventIce {
	string chatroom;
};

class VirtualGiftUserEventIce extends UserEventIce {
	string recipient;
	string giftName;
	int virtualGiftReceivedId;
};

class GiftShowerUserEventIce extends UserEventIce {
	string recipient;
	string giftName;
	int virtualGiftReceivedId;
	int totalRecipients;
};

class UserWallPostUserEventIce extends UserEventIce {
	string wallOwnerUsername;
	string postPrefix;
	int userWallPostId;
};

class GroupUserEventIce extends UserEventIce {
	int groupId;
	string groupName;
};

class GroupDonationUserEventIce extends GroupUserEventIce {
};

class GroupJoinedUserEventIce extends GroupUserEventIce {
};

// possible system event in future
class GroupUserPostUserEventIce extends GroupUserEventIce {
	int userPostId;
	int topicId;
	string topicText;
};

// TODO: replace with system event
class GroupAnnouncementUserEventIce extends GroupUserEventIce {
	int groupAnnouncementId; 
	string groupAnnouncementTitle;
};

class GenericApplicationUserEventIce extends UserEventIce {
	ParamMap urls;
};

struct SessionMetricsIce {
	short uniqueUsersPrivateChattedWith;
	short privateMessagesSent;
	short groupMessagesSent;
	short chatroomMessagesSent;
	short groupChatsEntered;
	short chatroomsEntered;
	short uniqueChatroomsEntered;
	short statusMessagesSet;
	short profileEdited;
	short photosUploaded;
	short inviteByPhoneNumber;
	short inviteByUsername;
	short themeUpdated;
};

class Credential {
	int userID;
	string username;
	string password;
	byte passwordType; // fusion, yahoo, gtalk, aim, msn, facebook etc.. 
};

class FusionCredential extends Credential {
	int clientHash;
	string loginChallenge;
};

sequence<Credential> CredentialArray;

// End of data objects
///////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////
// Base Service Stats

dictionary<string, int> IntStatsMap;
dictionary<string, long> LongStatsMap;
dictionary<string, double> DoubleStatsMap;

class BaseServiceStats {
	string hostname;
    string version;
    
	long jvmTotalMemory;  // In bytes
	long jvmFreeMemory;   // In bytes
	long uptime;   // In milliseconds
	
	long lastUpdatedTime; // BE-1341: System clock time when stats last updated
	
	// All times in seconds. "ize" since "ice" is a reserved prefix in ice IDL
	bool izeRequestStatsEnabled;
	long izeRequestCount;	
	LongStatsMap izeRequestCountByOrigin;	
	LongStatsMap izeRequestCountByOperation;	
	DoubleStatsMap izeRequestMeanProcessingTimeByOperation;	
	DoubleStatsMap izeRequestMaxProcessingTimeByOperation;	
	DoubleStatsMap izeRequestStdevProcessingTimeByOperation;	
	DoubleStatsMap izeRequest95thPercentileProcessingTimeByOperation;	
	DoubleStatsMap izeRequestTotalProcessingTimeByOperation;	
	
	bool izeThreadStatsEnabled;
	long izeObjectAdapterThreadPoolRunning;	
	long izeObjectAdapterThreadPoolInUse;
	long izeObjectAdapterThreadPoolInUseHighWatermark;
	double izeObjectAdapterThreadPoolLoad;
	long izeObjectAdapterThreadPoolSize;
	long izeObjectAdapterThreadPoolSizeMax;
	long izeObjectAdapterThreadPoolSizeWarn;

	bool izeClientThreadPoolStatsEnabled;
	long izeClientThreadPoolRunning;	
	long izeClientThreadPoolInUse;
	long izeClientThreadPoolInUseHighWatermark;
	double izeClientThreadPoolLoad;
	long izeClientThreadPoolSize;
	long izeClientThreadPoolSizeMax;
	long izeClientThreadPoolSizeWarn;

	long amdObjectAdapterThreadPoolRunning;	
	long amdObjectAdapterThreadPoolInUse;
	long amdObjectAdapterThreadPoolInUseHighWatermark;
	long amdObjectAdapterThreadPoolSize;
	long amdObjectAdapterThreadPoolSizeMax;
	long amdObjectAdapterThreadPoolQueueLength; // pt65945494
	
	string dataGridStats; // pt67005372	
};


interface User;  // Forward declaration
interface Session; // Forward declaration
interface ChatRoom; // Forward declaration
interface GroupChat;  // Forward declaration
interface MessageSwitchboard;  // Forward declaration
interface MessageSwitchboardAdmin;  // Forward declaration


class Message;//forward declaration
class ObjectCacheStats;//foward declaration
/*
 * The Connection object lives in a TCP or HTTP Server. It represents a connection to a client app.
 */
interface Connection {	
	string getUsername();
	ChatRoomDataIceArray getPopularChatRooms() throws FusionException;
	string getRemoteIPAddress();
	string getMobileDevice();
	string getUserAgent();
	int getDeviceTypeAsInt();
	short getClientVersion();
	User* getUserObject();
	Session* getSessionObject();
	bool processPacket(Connection* requestingConnection, ByteArray packet) throws FusionException;
	void packetProcessed(ByteArray result);
	void disconnect(string reason) throws FusionException;
	void accountBalanceChanged(double balance, double fundedBalance, CurrencyDataIce currency) throws FusionException;
	void contactChangedPresenceOneWay(int contactID, int imType, int presence);
	void contactChangedDisplayPictureOneWay(int contactID, string displayPicture, long timeStamp);
	void contactChangedStatusMessageOneWay(int contactID, string statusMessage, long timeStamp);
	void contactRequest(string contactUsername, int outstandingRequests) throws FusionException;
	void contactRequestAccepted(ContactDataIce contact, int contactListVersion, int outstandingRequests) throws FusionException;
	void contactRequestRejected(string contactUsername, int outstandingRequests) throws FusionException;
	void contactGroupAdded(ContactGroupDataIce contactGroup, int contactListVersion) throws FusionException;
	void contactGroupRemoved(int contactGroupID, int contactListVersion) throws FusionException;
	void contactAdded(ContactDataIce contact, int contactListVersion, bool guaranteedIsNew) throws FusionException;
	void contactRemoved(int contactID, int contactListVersion) throws FusionException;
	void otherIMLoggedIn(int imType) throws FusionException;
	void otherIMLoggedOut(int imType, string reason) throws FusionException;
	void otherIMConferenceCreated(int imType, string conferenceID, string creator) throws FusionException;
	void privateChatNowAGroupChat(string groupChatID, string creator) throws FusionException;
	void putEvent(UserEventIce event) throws FusionException;
	void putMessage(MessageDataIce message) throws FusionException;
	["ami","amd"] void putMessageAsync(MessageDataIce message) throws FusionException;
	void putMessageOneWay(MessageDataIce message);
	void putMessages(MessageDataSequence messages) throws FusionException;
	void putAlertMessage(string message, string title, short timeout) throws FusionException;
	void putAlertMessageOneWay(string message, string title, short timeout);
	void putServerQuestion(string message, string url) throws FusionException;
	void putWebCallNotification(string source, string destination, int gateway, string gatewayName, int protocol) throws FusionException;
	void putAnonymousCallNotification(string requestingUsername, string requestingMobilePhone) throws FusionException;
	void putFileReceived(MessageDataIce message) throws FusionException; 
	void putGenericPacket(ByteArray packet) throws FusionException;
	void emailNotification(int unreadEmailCount) throws FusionException;
	void emoticonsChanged(StringArray hotKeys, StringArray alternateKeys) throws FusionException;
	void themeChanged(string themeLocation) throws FusionException;
	void avatarChanged(string displayPicture, string statusMessage) throws FusionException;
	void silentlyDropIncomingPackets();
	void pushNotification(Message msg) throws FusionException;
	void logout();
	
	// BE-1230 chat sync
	void putSerializedPacket(ByteArray packet) throws FusionException;
	void putSerializedPacketOneWay(ByteArray packet);
	
	// pt75161644    
    void putMessageStatusEvent(MessageStatusEventIce mseIce) throws FusionException;
    void putMessageStatusEvents(MessageStatusEventSequence events, short requestTxnId) throws FusionException;
    
};

/**SE-526: websocket connection */
interface ConnectionWS extends Connection {
    void accessed();    
    void addRemoteChildConnectionWS(string uuid, ConnectionWS * childConnectionWS);    
    void removeRemoteChildConnectionWS(string uuid, ConnectionWS * childConnectionWS);    
};

// BE-1230 chat sync
struct ChatListIce {
    int userID;
	int chatListVersion;
	StringArray chatIDs;
};

/*
 * The Session object lives in a User object, which in turn lives in an ObjectCache.
 * A User object may have one or more Session objects. Each Session object represents a
 * user session with the Fusion system.
 * For example, if a user is connected to the Fusion system via an HTTP client app (midlet)
 * and a desktop app, the User object will contain two Session objects.
 */
interface Session {
	["amd"] void sendMessage(MessageDataIce message) throws FusionException;
	void setPresence(int presence) throws FusionException;
	["amd"] void endSession() throws FusionException;
	void endSessionOneWay();
	void touch() throws FusionException;
	["amd"] void putMessage(MessageDataIce message) throws FusionException;
	void putMessageOneWay(MessageDataIce message);
	void sendMessageBackToUserAsEmote(MessageDataIce message) throws FusionException;
	void putAlertMessage(string message, string title, short timeout) throws FusionException;
	void putAlertMessageOneWay(string message, string title, short timeout);
	string getParentUsername() throws FusionException;
	User* getUserProxy(string username) throws FusionException;

	// callbacks for metrics
	void profileEdited();
	void groupChatJoined(string id);
	void groupChatJoinedMultiple(string id,int increment);
	void chatroomJoined(ChatRoom* roomProxy, string name);
	void statusMessageSet();
	void photoUploaded();
	void friendInvitedByPhoneNumber();
	void friendInvitedByUsername();
	void themeUpdated();
	
	// BE-313
	void silentlyDropIncomingPackets();
	
	// BE-401
	string getSessionID();

    string getRemoteIPAddress();
    string getMobileDeviceIce();
    string getUserAgentIce();

	// BE-974
	short getClientVersionIce();
	int getDeviceTypeAsInt();

	// ENG-162
	void setLanguage(string language);

	//android v2.0
	void notifyUserLeftChatRoomOneWay(string chatroomname, string username);
	void notifyUserJoinedChatRoomOneWay(string chatroomname, string username, bool isAdministrator, bool isMuted);
	
	void notifyUserLeftGroupChat(string groupChatId, string username) throws FusionException;
	void notifyUserJoinedGroupChat(string groupChatId, string username, bool isMuted) throws FusionException;
	void sendGroupChatParticipants(string groupChatId, byte imType, string participants, string mutedParticipants) throws FusionException;
	void sendGroupChatParticipantArrays(string groupChatId, byte imType, StringArray participants, StringArray mutedParticipants) throws FusionException;

	// Chat sync
	int getChatListVersion() throws FusionException;
	void setChatListVersion(int version) throws FusionException;
	void putSerializedPacket(ByteArray packet) throws FusionException;
	void putSerializedPacketOneWay(ByteArray packet);
	GroupChat* findGroupChatObject(string groupChatID) throws FusionException;
	MessageSwitchboard* getMessageSwitchboard() throws FusionException;
	bool privateChattedWith(string username);
	
	SessionMetricsIce getSessionMetrics(); // only used by tests (to eliminate some arbitrary waits)
	
	// pt71813844    
    void setCurrentChatListGroupChatSubset(ChatListIce ccl);
};

sequence<Session*> SessionProxyArray;

struct UserErrorResponse {
	string reason;
	bool silentlyIgnore;
	bool error;
};

/*
 * The User object represents a user connected to the Fusion system.
 */
interface User {
	["amd"] Session* createSession(string sessionID, int presence, int deviceType, int connectionType, int imType, int port, int remotePort, string IP, string mobileDevice, string userAgent, short clientVersion, string language, Connection* connectionProxy) throws FusionException;
	["amd"] void putMessage(MessageDataIce message) throws FusionException;
	void contactChangedPresenceOneWay(int imType, string source, int presence);
	void contactChangedDisplayPictureOneWay(string source, string displayPicture, long timeStamp);
	void contactChangedStatusMessageOneWay(string source, string statusMessage, long timeStamp);
	/** Called via PHP */
	int getOverallFusionPresence(string requestingUsername);
	int getContactListVersion();
	ContactList getContactList();
	ContactDataIceArray getContacts();
	/** Called via PHP */
	ContactDataIceArray getOtherIMContacts();
	/** Called via PHP */
	CredentialArray getOtherIMCredentials();
	StringArray getOtherIMConferenceParticipants(int imType, string otherIMConferenceID);
	UserDataIce getUserData();
	bool isOnContactList(string contactUsername);
	bool isOnBlockList(string contactUsername);
	["amd"] void otherIMLogin(int imType, int presence, bool showOfflineContacts) throws FusionException;
	void otherIMLogout(int imType);
	void otherIMSendMessage(int imType, string otherIMUsername, string message) throws FusionException;
	string otherIMInviteToConference(int imType, string otherIMConferenceID, string otherIMUsername) throws FusionException;
	void otherIMLeaveConference(int imType, string otherIMConferenceID);
	void otherIMAddContact(int imType, string otherIMUsername) throws FusionException;
	void otherIMRemoveContact(int contactId) throws FusionException;
	void otherIMRemoved(int imType);
	StringArray getBlockList();
	StringArray getBlockListFromUsernames(StringArray usernames);
	StringArray getBroadcastList();
	void accountBalanceChanged(double balance, double fundedBalance, CurrencyDataIce currency);
	void privateChatNowAGroupChat(string groupChatID, string creator) throws FusionException;
	void putEvent(UserEventIce event) throws FusionException;
	void putAlertMessage(string message, string title, short timeout) throws FusionException;
	void putServerQuestion(string message, string url) throws FusionException;
	void putWebCallNotification(string source, string destination, int gateway, string gatewayName, int protocol) throws FusionException;
	void putAnonymousCallNotification(string requestingUsername, string requestingMobilePhone) throws FusionException;
	void putFileReceived(MessageDataIce message) throws FusionException;
	void contactDetailChanged(ContactDataIce contact, int contactListVersion);
	void contactGroupDetailChanged(ContactGroupDataIce contactGroup, int contactListVersion);
	void contactGroupDeleted(int contactGroupID, int contactListVersion);
	void userDetailChanged(UserDataIce user);
	void userReputationChanged();
	void userDisplayPictureChanged(string displayPicture, long timeStamp);
	void userStatusMessageChanged(string statusMessage, long timeStamp);
	void messageSettingChanged(int setting);
	void anonymousCallSettingChanged(int setting);
	IntArray getConnectedOtherIMs();
	bool supportsBinaryMessage();
	int getUnreadEmailCount();
	void emailNotification(int unreadEmailCount);
	StringArray getEmoticonHotKeys();
	StringArray getEmoticonAlternateKeys();
	void emoticonPackActivated(int emoticonPackId);
	void themeChanged(string themeLocation) throws FusionException;
	
	/* broadcast list changes http://fannie/mediawiki-1.12.0/index.php/Replace_ObjectCache's_AllowList_and_WatchList_with_BroadcastList */
	void addContact(ContactDataIce contact, int contactListVersion);
	void addToContactAndBroadcastLists(ContactDataIce contact, int contactListVersion);
	void addPendingContact(string username);
	ContactDataIce acceptContactRequest(ContactDataIce contact, User* contactProxy, int inviterContactListVersion, int inviteeContactListVersion);
	ContactDataIce contactRequestWasAccepted(ContactDataIce contact, string statusMessage, string displayPicture, int overallFusionPresence, int contactListVersion);
	void blockUser(string username, int contactListVersion);
	void unblockUser(string username);
    void contactRequestWasRejected(string contactRequestUsername, int contactListVersion);
    void rejectContactRequest(string inviterUsername);
	void stopBroadcastingTo(string username);
	void removeContact(int contactid, int contactListVersion);
	PresenceAndCapabilityIce contactUpdated(ContactDataIce contact, string oldusername, bool acceptedContactRequest, bool changedFusionContact, User* newContactUserProxy, int contactListVersion) throws FusionException;
	void oldUserContactUpdated(string usernameThatWasModified) throws FusionException;
	void newUserContactUpdated(string usernameThatWasModified, bool acceptedContactRequest); 
	void notifySessionsOfNewContact(ContactDataIce newContact, int contactListVersion, bool guaranteedIsNew);
	
	/* BE-401 */
	SessionProxyArray getSessions();
	void disconnect(string reason);
	void disconnectFlooder(string reason);
	bool privateChattedWith(string username);	
	UserErrorResponse userCanContactMe(string username, MessageDataIce message);
	
	void enteringGroupChat(bool isCreator) throws FusionException;
	void leavingGroupChat();
	void pushNotification(Message msg) throws FusionException;
	
	/** Called via PHP */
	int getOnlineContactsCount();

	int executeEmoteCommandWithState(string emoteCommand, MessageDataIce message, Session* sessionProxy) throws FusionException;
	
	void addToCurrentChatroomList ( string chatroom ) throws FusionException;
	void removeFromCurrentChatroomList ( string chatroom );
	/** Called via PHP */
	StringArray getCurrentChatrooms ();
	
	int getReputationDataLevel();

    void notifyUserLeftGroupChat(string groupChatId, string username);
    void notifyUserJoinedGroupChat(string groupChatId, string username, bool isMuted);

	// pt71813844    
    void setCurrentChatListGroupChatSubset(ChatListIce ccl);

	// pt75161644    
    void putMessageStatusEvent(MessageStatusEventIce mseIce) throws FusionException;
    
	// SE-526
    Session * findSession(string sid) throws FusionException;
};

sequence<User*> UserProxyArray;

/*
 * The BotInstance structure which holds information about a bot instance
 */
 
interface BotService; // Forward declaration

struct BotInstance {
   	string id;
	int type;
	string displayName;
	string description;
	string startedBy;
	BotService* botServiceProxy;
};

/*
 * The BotChannel interface will be implemented by ChatRoom and GroupChat objects
 * to support gaming and administrative bots
 */
interface BotChannel {
	void startBot(string username, string botCommandName) throws FusionException;
	void stopBot(string username, string botCommandName) throws FusionException;	
	void stopAllBots(string username, int timeout) throws FusionException; // BE-169 move this command to BotChannel so that it could be shared by GroupChat and ChatRoom
	void botKilled(string botInstanceID) throws FusionException;
	void sendMessageToBots(string username, string message, long receivedTimestamp) throws FusionException;
	void putBotMessage(string botInstanceID, string username, string message, StringArray emoticonHotKeys, bool displayPopUp) throws FusionException; 
	void putBotMessageToUsers(string botInstanceID, StringArray usernames, string message, StringArray emoticonHotKeys, bool displayPopUp) throws FusionException;
	void putBotMessageToAllUsers(string botInstanceID, string message, StringArray emoticonHotKeys, bool displayPopUp) throws FusionException;
	void sendGamesHelpToUser(string username) throws FusionException;
	bool isParticipant(string username) throws FusionException;
	/** Called via PHP */
	StringArray getParticipants(string requestingUsername);
};

/*
 * The BotService object hosts various bots
 */
interface BotService {
	BotInstance addBotToChannel(BotChannel* channelProxy, string botCommandName, string starterUsername, bool purgeIfIdle) throws FusionException;
	void removeBot(string botInstanceID, bool stopEvenIfGameInProgress) throws FusionException;
	void sendMessageToBot(string botInstanceID, string username, string message, long receivedTimestamp) throws FusionException;
	void sendMessageToBotsInChannel(string channelID, string username, string message, long receivedTimestamp) throws FusionException;
	void sendNotificationToBotsInChannel(string channelID, string username, int notification) throws FusionException;                                                                                                                     
};

///////////////////////////////////////////////////////////
// Chat Room

/*
 * The ChatRoom object lives in an ObjectCache.
 * A ChatRoom object is used to allow Fusion users to participate in a public chat.
 */
interface ChatRoom extends BotChannel {
	void addParticipantOld(User* userProxy, UserDataIce userData, Session* sessionProxy, string sessionID, string ipAddress, string mobileDevice, string userAgent) throws FusionException;
    void addParticipant(User* userProxy, UserDataIce userData, Session* sessionProxy, string sessionID, string ipAddress, string mobileDevice, string userAgent, short clientVersion, int deviceType) throws FusionException;

	void removeParticipant(string username) throws FusionException;
	void removeParticipantOneWay(string username, bool removeFromUsersChatRoomList);
	void addModerator(string username);
	void removeModerator(string username);
	void banUser(string username);
	void unbanUser(string username);
	void banGroupMembers(StringArray banList, string instigator, int reasonCode) throws FusionException;
	void unbanGroupMember(string target, string instigator, int reasonCode) throws FusionException;
	void banMultiIds(string username) throws FusionException;
	void inviteUserToGroup(string invitee, string inviter) throws FusionException;
	void broadcastMessage(string instigator, string message) throws FusionException;
	
	void setMaximumSize(int maximumSize);
	void setDescription(string description);
	void updateDescription(string instigator, string description) throws FusionException;
	void setAllowKicking(bool allowKicking);
	void setAdultOnly(bool adultOnly);
	void changeOwner(string oldOwnerUsername, string newOwnerUsername);	
	
	ChatRoomDataIce getRoomData();
	StringArray getAllParticipants(string requestingUsername);
	StringArray getAdministrators(string requestingUsername);
	
	/** Called via PHP */
	int getNumParticipants();
	
	bool isVisibleParticipant(string username) throws FusionException;
	void listParticipants(string requestingUsername, int size, int startIndex) throws FusionException;
	void banIndexes(IntArray indexes, string bannedBy, int reasonCode) throws FusionException;
	void kickIndexes(IntArray indexes, string bannedBy) throws FusionException;
	void bumpUser(string instigator,string target) throws FusionException;
	void warnUser(string instigator,string target,string message) throws FusionException; 
	void voteToKickUser(string voter, string target) throws FusionException;
	void clearUserKick(string instigator, string target) throws FusionException;
	void putMessage(MessageDataIce message, string sessionID) throws FusionException;
	void putSystemMessage(string messageText, StringArray emoticonKeys);	
	void putSystemMessageWithColour(string messageText, StringArray emoticonKeys, int messageColour);
	int getMaximumMessageLength(string sender);
	void addGroupModerator(string instigator,string targetUser) throws FusionException;
	void removeGroupModerator(string instigator,string targetUser) throws FusionException;
	StringArray getGroupModerators(string instigator) throws FusionException;
	void mute(string username, string target) throws FusionException;
	void unmute(string username, string target) throws FusionException;
	void unsilence(string username) throws FusionException;
	void unsilenceUser(string instigator, string target) throws FusionException;
	void silence(string username, int timeout) throws FusionException;
	void silenceUser(string instigator, string target, int timeout) throws FusionException;
	void setNumberOfFakeParticipants(string username, int number);

	/** Called via PHP */	
	void convertIntoUserOwnedChatRoom() throws FusionException;
	
	/** Called via PHP */
	void convertIntoGroupChatRoom(int groupID, string groupName) throws FusionException;
	
	// [VC-43] /lock command, [VC-44] /unlock command, Zehua, 2011/01/20
	bool isLocked();
	void lock(string locker) throws FusionException;
	void unlock(string unlocker) throws FusionException;

	// [VC-34] [VC-35] /chatroom announce command, Zehua, 2011/01/27
	void announceOff(string announcer) throws FusionException;
	void announceOn(string announcer, string announceMessage, int waitTime) throws FusionException;
	
	void adminAnnounce(string announceMessage, int waitTime) throws FusionException;

	ParamMap getTheme();

	int executeEmoteCommandWithState(string emoteCommand, MessageDataIce message, Session* sessionProxy) throws FusionException;
	void submitGiftAllTask(int giftId, string giftMessage, MessageDataIce message) throws FusionException;
	
	void updateExtraData(ChatRoomDataIce data);
	void updateGroupModeratorStatus(string username, bool promote);
};

sequence<ChatRoom*> ChatRoomProxyArray;

/*
 * The GroupChat object lives in an ObjectCache.
 * A GroupChat object is used to allow Fusion users to participate in a private group chat.
 */
interface GroupChat extends BotChannel {

	/**pt61094672 - On-demand debug logging for ChatGroup.addParticipant() */
	void addParticipantInner(string inviterUsername, string inviteeUsername, bool debug) throws FusionException;

	void addParticipant(string inviterUsername, string inviteeUsername) throws FusionException;
	bool removeParticipant(string username) throws FusionException;
	void putMessage(MessageDataIce message) throws FusionException;
	void putFileReceived(MessageDataIce message) throws FusionException;
	void sendInitialMessages();
	int getNumParticipants();
	bool supportsBinaryMessage(string usernameToExclude);

	int executeEmoteCommandWithState(string emoteCommand, MessageDataIce message, Session* sessionProxy) throws FusionException;
	
	// chat sync
	string getId();
	string getCreatorUsername();
	int getCreatorUserID();
	int getPrivateChatPartnerUserID();
	string listOfParticipants();
	IntArray getParticipantUserIDs();		
	void addParticipants(string inviterUsername, StringArray inviteeUsernames) throws FusionException;
	
	/**pt61094672 - On-demand debug logging */
	void addUserToGroupChatDebug(string participant, bool b, bool c) throws FusionException;	
};

/*Chat sync*/
sequence<GroupChat*> GroupChatArray;

/*
 * An ObjectCache is used to store User, Session, GroupChat and ChatRoom objects.
 */
interface ObjectCache {
	["amd"] User *createUserObject(string username) throws ObjectExistsException, FusionException;
	User *createUserObjectNonAsync(string username) throws ObjectExistsException, FusionException;
	ChatRoom* createChatRoomObject(string name) throws ObjectExistsException, FusionException;
	GroupChat *createGroupChatObject(string id, string creator, string privateChatPartner, StringArray otherPartyList) throws ObjectExistsException, FusionException;
	void sendAlertMessageToAllUsers(string message, string title, short timeout) throws FusionException;

	// Chat sync
	GroupChatArray getAllGroupChats() throws FusionException;
    void purgeUserObject(string username);
    void purgeGroupChatObject(string id);    
    
    // Temporary measure until MessageSwitchboard fully deployed
	["ami"] MessageSwitchboard * getMessageSwitchboard() throws FusionException;

};

interface ObjectCacheAdmin;  // Forward declaration
interface BotServiceAdmin;	 // Forward declaration

dictionary<string,User*> UsernameToProxyMap;

/*
 * A Registry stores a list of proxies to the ObjectCaches currently running in the
 * Fusion system, and stores proxies to all the User, Conference and ChatRoom objects
 * stored by those ObjectCaches.
 */
interface Registry {
	/** Called via PHP */
	User *findUserObject(string username) throws ObjectNotFoundException;
	UserProxyArray findUserObjects(StringArray usernames);
	UsernameToProxyMap findUserObjectsMap(StringArray usernames);
	void registerUserObject(string username, User* userProxy, string objectCacheHostname) throws ObjectExistsException;
	void deregisterUserObject(string username, string objectCacheHostname);

	/** Called via PHP */
	Connection *findConnectionObject(string sessionID) throws ObjectNotFoundException;
	void registerConnectionObject(string sessionID, Connection* connectionProxy) throws ObjectExistsException;
	void deregisterConnectionObject(string sessionID);
	
	/** Called via PHP */
	ChatRoom *findChatRoomObject(string name) throws ObjectNotFoundException;
	ChatRoomProxyArray findChatRoomObjects(StringArray chatRoomNames);
	void registerChatRoomObject(string name, ChatRoom* chatRoomProxy) throws ObjectExistsException;
	void deregisterChatRoomObject(string name);
	
	/** Called via PHP */
	GroupChat *findGroupChatObject(string id) throws ObjectNotFoundException;
	void registerGroupChatObject(string id, GroupChat* groupChatProxy);
	void deregisterGroupChatObject(string id);
	
	ObjectCache* getLowestLoadedObjectCache() throws ObjectNotFoundException;
	void registerObjectCache(string hostName, ObjectCache* cacheProxy, ObjectCacheAdmin* adminProxy);
	void deregisterObjectCache(string hostName);

	BotService* getLowestLoadedBotService() throws ObjectNotFoundException;
	void registerBotService(string hostName, int load, BotService* serviceProxy, BotServiceAdmin* adminProxy);
	void deregisterBotService(string hostName);
	
	void sendAlertMessageToAllUsers(string message, string title, short timeout) throws FusionException;
	
	int newGatewayID();

	void registerObjectCacheStats(string objectCacheHostName, ObjectCacheStats stats) throws ObjectNotFoundException;

	int getUserCount();
	
	// BE-1304 chat sync
	void registerMessageSwitchboard(string hostName, MessageSwitchboard* msbProxy,
										MessageSwitchboardAdmin* adminProxy);
	void deregisterMessageSwitchboard(string hostName);
	["amd"] MessageSwitchboard * getMessageSwitchboard() throws FusionException;
};

/*
 * The RegistryNode interface is used by Registries when there are two or more
 * Registries running in a cluster.
 */
interface RegistryNode {
	void registerUserObject(string username, User* userProxy, string objectCacheHostname) throws ObjectExistsException;
	void deregisterUserObject(string username, string objectCacheHostname);

	void registerConnectionObject(string sessionID, Connection* connectionProxy) throws ObjectExistsException;
	void deregisterConnectionObject(string sessionID);
	
	void registerChatRoomObject(string name, ChatRoom* chatRoomProxy) throws ObjectExistsException;
	void deregisterChatRoomObject(string name);
	
	void registerGroupChatObject(string id, GroupChat* groupChatProxy);
	void deregisterGroupChatObject(string id);
	
	void registerObjectCache(string hostName,  ObjectCache* cacheProxy, ObjectCacheAdmin* adminProxy);
	void deregisterObjectCache(string hostName);

	void registerBotService(string hostName, int load, BotService* serviceProxy, BotServiceAdmin* adminProxy);
	void deregisterBotService(string hostName);
	
	// Called by a newly instantiated registry. If 'replicate' is true, this registry
	// will replicate itself to the new registry by calling its registerUserObject and
	// registerObjectCache methods. Returns the host name to the new registry for logging purposes.
	string registerNewNode(RegistryNode* newNodeProxy, string hostName, bool replicate) throws FusionException;

	void registerObjectCacheStats(string objectCacheHostName, ObjectCacheStats stats) throws ObjectNotFoundException;
	
	void registerMessageSwitchboard(string hostName,  MessageSwitchboard* cacheProxy,
		MessageSwitchboardAdmin* adminProxy);
	void deregisterMessageSwitchboard(string hostName);
};

/*
 * The CallMaker object lives in a Voice Engine.
 * A CallMaker object is used to allow Fusion users to make voice calls.
 */
interface CallMaker {
	CallDataIce requestCallback(CallDataIce call, int maxDuration, int retries) throws FusionException;
};

/*
 * The SMSSender object lives in a SMS Engine.
 * A SMSSender object is used to allow Fusion users to send SMS.
 */
interface SMSSender {
	void sendSMS(MessageDataIce message, long delay) throws FusionException;
	void sendSystemSMS(SystemSMSDataIce message, long delay) throws FusionException;
};

//////////////////////////////////////////////////////////////////////
// Start of interfaces used for system administration and monitoring

class ObjectCacheStats extends BaseServiceStats {
	int numUserObjects;
	int maxUserObjects;
	int numOnlineUserObjects;
	int maxOnlineUserObjects;
	int numSessionObjects;
	int maxSessionObjects;
	int numChatRoomObjects;
	int maxChatRoomObjects;
	int numGroupChatObjects;
	int maxGroupChatObjects;
	int distributionServiceQueueSize;
	int stadiumDistributionServiceQueueSize;
	float requestsPerSecond;
	float maxRequestsPerSecond;
	long eldestUserObject;   // Time (in milliseconds) when the eldest user object was created
	int weightage;
	int numSessionsInChatrooms;
	int numSessionsInGroupChats;
	int totalChatUserSessionsIntermediateFails;
	int totalChatUserSessionsFinalFails;
};

/*
 * This interface is implemented by ObjectCaches to:
 * 1. Allow registries to check that they're alive, and if they are, to receive the
 *    ObjectCache's current load (ie. number of objects cached).
 * 2. Return an ObjectCacheStats struct to a monitoring application.
 */
interface ObjectCacheAdmin {
	int ping();  // Called to check the app is still alive. Returns the load of the app.
	ObjectCacheStats getStats() throws FusionException;
	StringArray getUsernames();  // Returns the usernames of all the user objects cached
	void reloadEmotes();  // Reloads a list of emotes from database
	// void shutdown();  // Call unregisterObjectCache(me) on the registry, then move all my objects to
	                     // other object caches (and register them with the registry)
	void setLoadWeightage( int weightage );
	int getLoadWeightage( );
};

class RegistryStats extends BaseServiceStats {
	int numUserProxies;
	int maxUserProxies;
	int numConnectionProxies;
	int maxConnectionProxies;
	int numChatRoomProxies;
	int maxChatRoomProxies;
	int numGroupChatProxies;
	int maxGroupChatProxies;
	string objectCaches;  // Names of all known Object Caches, separated by "; "
	string otherRegistries;  // Names of all other Registries in the cluster, separated by "; "
	float requestsPerSecond;
	float maxRequestsPerSecond;
};

/*
 * This interface is implemented by Registries to return a RegistryStats struct to
 * a monitoring application.
 */
interface RegistryAdmin {
	/** Called via PHP */
	RegistryStats getStats() throws FusionException;
};

struct GatewayThreadPoolStats {
	string name;
	int threadPoolSize;
	int maxThreadPoolSize;
	int threadPoolQueueSize;
	int maxThreadPoolQueueSize;
	float requestsPerSecond;
	float maxRequestsPerSecond;
};

sequence<GatewayThreadPoolStats> GatewayThreadPoolStatsArray;

class GatewayStats extends BaseServiceStats {
	string serverType;
	int port;
	int numConnectionObjects;
	int maxConnectionObjects;
	GatewayThreadPoolStatsArray threadPoolStats;
	int connectionsRejected;
	int timesTooBusy;
	long lastTimeTooBusy;
	bool tooBusy;	
	float connectionsPerRemoteIP; // pt65945494

	// pt70911004 - OPEN_URL stats	
	int openUrlAttempts;
	int openUrlFailures;
	float openUrlFailurePercent;
	float averageSuccessfulProcessingTimeSeconds;
	IntStatsMap openUrlFailuresByUrl;		
};

/*
 * This interface is implemented by Gateways to return a GatewayStats struct to
 * a monitoring application.
 */
interface GatewayAdmin {
	GatewayStats getStats() throws FusionException;
	void sendAlertToAllConnections(string message, string title);
};

class SMSEngineStats extends BaseServiceStats {
	float requestsPerSecond;
	float maxRequestsPerSecond;
	int requestsReceived;
	int requestsDispatched;
};

/*
 * This interface is implemented by SMSEngines to return a SMSEngineStats struct to
 * a monitoring application.
 */
interface SMSEngineAdmin {
	SMSEngineStats getStats() throws FusionException;
};

class BotServiceStats extends BaseServiceStats {
	int numBotObjects;
	int maxBotObjects;
	int numBotChannelObjects;
	int maxBotChannelObjects;
	int threadPoolSize;
	int maxThreadPoolSize;
	int threadPoolQueueSize;
	int maxThreadPoolQueueSize;
	float requestsPerSecond;
	float maxRequestsPerSecond;
};

/*
 * This interface is implemented by BotService to return a BotServiceStats struct to
 * a monitoring application.
 */
interface BotServiceAdmin {
	int ping();  // Called to check the app is still alive. Returns the load of the app.
	BotServiceStats getStats() throws FusionException;
};

/*
 * A MessageLogger receives messages from ObjectCaches and logs them to file for archiving.
 * The types of messages logged are:
 *  - sending private Fusion, MSN, Yahoo! and AIM messages,
 *  - Fusion chatroom messages, and
 *  - SMS messages.
 */
interface MessageLogger {
	void logMessage(int type, int sourceCountryID, string source, string destination, int numRecipients, string messageText);
};

class MessageLoggerStats extends BaseServiceStats {
	float numMessagesReceivedPerSecond;
	float maxMessagesReceivedPerSecond;
	float numMessagesLoggedPerSecond;
	float maxMessagesLoggedPerSecond;
	int numMessagesQueued;
	int maxMessagesQueued;
};

/*
 * This interface is implemented by the MessageLogger application to return a
 * MessageLoggerStats struct to a monitoring application.
 */
interface MessageLoggerAdmin {
	MessageLoggerStats getStats() throws FusionException;
};

class SessionCacheStats extends BaseServiceStats {
	float sessionsReceivedPerSecond;
	float maxSessionsReceivedPerSecond;
	long dateOfMaxSessionsReceivedPerSecond;
	int sessionsQueuedToBeArchived;
	bool uniqueSummariesTaskRunning;
	int abortedBatches;
};

/*
 * Session data is sent to SessionCache for summarization and archival
 */
interface SessionCache {
	void logSession(SessionIce session, SessionMetricsIce sessionMetrics);
};

interface SessionCacheAdmin {
	SessionCacheStats getStats() throws FusionException;
};

class EventStoreStats extends BaseServiceStats {
	long generatorEvents;
	int generatorEventRate;
	int maxGeneratorEventRate;
	
	long events;
	int eventRate;
	int maxEventRate;
	
	long cacheExpiredEvents;
	int cacheExpiredEventRate;
	int maxCacheExpiredEventRate;
	
    long persistBufferEvents;
    int persistBufferRate;
    int maxPersistBufferRate;
    int persistBufferSize;
    
	int cacheSize;
};

struct EventPrivacySettingIce {
     bool statusUpdates;
     bool profileChanges;
     bool addFriends;
     bool photosPublished;
     bool contentPurchased;
     bool chatroomCreation;
     bool virtualGifting;
};

/*
 * UserEvents are sent to EventStore 
 */
interface EventStore {
	void storeUserEvent(string username, UserEventIce event) throws FusionException;
	void storeGeneratorEvent(string username, UserEventIce event) throws FusionException;
	UserEventIceArray getUserEventsForUser(string username) throws FusionException;
	UserEventIceArray getUserEventsGeneratedByUser(string username) throws FusionException;
	void deleteUserEvents(string username) throws FusionException;
	
	// event privacy settings
	/** Called via PHP */
	EventPrivacySettingIce getPublishingPrivacyMask(string username) throws FusionException; 
	/** Called via PHP */
	void setPublishingPrivacyMask(string username, EventPrivacySettingIce mask) throws FusionException; 
	/** Called via PHP */
	EventPrivacySettingIce getReceivingPrivacyMask(string username) throws FusionException; 
	/** Called via PHP */
	void setReceivingPrivacyMask(string username, EventPrivacySettingIce mask) throws FusionException; 
};

interface EventStoreAdmin {
	EventStoreStats getStats() throws FusionException;
};

class EventSystemStats extends BaseServiceStats {
	float eventsReceivedPerSecond;

    long genericApplicationEvents;	
    long setProfileStatusEvents;
    long madePhotoPublicEvents;
    long createdPublicChatrooomStatusEvents;
    long addedFriendEvents;
    long updatedProfileEvents;
    long purchasedVirtualGoodsEvents;
    long virtualGiftsEvents;
    long giftShowerEvents;
    long userWallPostEvents;
    long totalEvents;
    long droppedEvents;
    long streamedEvents;
    long distributedEvents;
    
    int genericApplicationEventRate;
    int giftShowerEventRate;
    int setProfileStatusRate;
    int madePhotoPublicRate;
    int createdPublicChatrooomStatusRate;
    int addedFriendRate;
    int updatedProfileRate;
    int purchasedVirtualGoodsRate;
    int virtualGiftsRate;
    int userWallPostRate;
    int totalRate;
    int maxTotalRate;
    int droppedRate;
    int maxDroppedRate;
    int streamedRate;
    int maxStreamedRate;
    int distributedRate;
    int maxDistributedRate;
};

interface EventSystem {
	UserEventIceArray getUserEventsForUser(string username) throws FusionException;
	UserEventIceArray getUserEventsGeneratedByUser(string username) throws FusionException;
	void updateAllowList(string username, StringArray watchers) throws FusionException;
	void streamEventsToLoggingInUser(string username, Connection* connectionProxy) throws FusionException;
	void deleteUserEvents(string username) throws FusionException;

	// events
	void madePhotoPublic(string username, int scrapbookid, string title) throws FusionException;
	void setProfileStatus(string username, string status) throws FusionException;
	void createdPublicChatroom(string username, string chatroomName) throws FusionException;
	void addedFriend(string username, string friend) throws FusionException;
	void updatedProfile(string username) throws FusionException;
	void purchasedVirtualGoods(string username, byte itemType, int itemid, string itemName) throws FusionException;
	void virtualGift(string username, string recipient, string giftName, int virtualGiftReceivedId);
	void userWallPost(string username, string wallOwnerUsername, string postContent, int userWallPostId) throws FusionException;
	
	void groupDonation(string username, int groupId) throws FusionException;
	void groupJoined(string username, int groupId) throws FusionException;
	void groupAnnouncement(string username, int groupId, int groupAnnoucementId) throws FusionException;
	void madeGroupUserPost(string username, int userPostId, int groupId) throws FusionException;

	void genericApplicationEvent(string username, string appId, string text, ParamMap customDeviceURL) throws FusionException;
	void giftShowerEvent(string username, string recipient, string giftName, int virtualGiftReceivedId, int totalRecipients);
	
	// privacy settings
	EventPrivacySettingIce getPublishingPrivacyMask(string username) throws FusionException; 
	void setPublishingPrivacyMask(string username, EventPrivacySettingIce mask) throws FusionException; 
	EventPrivacySettingIce getReceivingPrivacyMask(string username) throws FusionException; 
	void setReceivingPrivacyMask(string username, EventPrivacySettingIce mask) throws FusionException; 
};

interface EventSystemAdmin {
	EventSystemStats getStats() throws FusionException;
};

/*
 * The EmailAlert app receives notification of new emails from the SurgeMail email system. It then
 * notifies the user (if they are online) and sends an SMS alert to the user (if required).
 * This interface allows the Gateway app to asynchronously request the EmailAlert app notify the
 * Connection proxy with how many unread emails the user has in their inbox.
 */
interface EmailAlert {
	void requestUnreadEmailCount(string username, string password, User* userProxy);
};

struct EmailAlertStats {
	string hostName;
	float numNotificationsReceivedPerSecond;
	float maxNotificationsReceivedPerSecond;
	float numNotificationsProcessedPerSecond;
	float maxNotificationsProcessedPerSecond;
	int notificationsThreadPoolSize;
	int notificationsMaxThreadPoolSize;
	int notificationsThreadPoolQueueSize;
	int gatewayQueriesThreadPoolSize;
	int gatewayQueriesMaxThreadPoolSize;
	int gatewayQueriesThreadPoolQueueSize;
	float numGatewayQueriesReceivedPerSecond;
	float maxGatewayQueriesReceivedPerSecond;
	float numGatewayQueriesProcessedPerSecond;
	float maxGatewayQueriesProcessedPerSecond;
	float numGatewayQueriesDiscardedPerSecond;
	float maxGatewayQueriesDiscardedPerSecond;
	long jvmTotalMemory;  // In bytes
	long jvmFreeMemory;   // In bytes
	long uptime;   // In milliseconds
};

/*
 * This interface is implemented by the EmailAlert application to return an
 * EmailAlertStats struct to a monitoring application.
 */
interface EmailAlertAdmin {
	EmailAlertStats getStats() throws FusionException;
};

class ImageServerStats extends BaseServiceStats {
	int port;
	int numConnectionObjects;
	int maxConnectionObjects;
	float requestsPerSecond;
	float maxRequestsPerSecond;
	string cacheInfo;
	int threadPoolSize;
	int maxThreadPoolSize;
	int threadPoolQueueSize;
};

/*
 * This interface is implemented by ImageServer to return a ImageServerStats struct to
 * a monitoring application.
 */
interface ImageServerAdmin {
	ImageServerStats getStats() throws FusionException;
};

struct WebServiceResponse {
	string responseData;
	int responseCode;
};

//////////////////////////////////////////////////////////////////////
// Blue Label Service

class BlueLabelServiceStats extends BaseServiceStats {
	int recentErrors;
};

interface BlueLabelServiceAdmin {
	BlueLabelServiceStats getStats() throws FusionException;
};

struct BlueLabelOneVoucher {
	string number;
	string amountRedeemed;
	string value; // full value of the voucher
	string currency;
	string transactionReference;
};

interface BlueLabelService {
	WebServiceResponse registerAccount(string username, string password, int countryCode, string mobileNumber, int secretQuestionCode, string secretQuestionAnswer, string firstName, string lastName, string nickName, string dateOfBirth, string sex, string emailAddress) throws FusionException;
	WebServiceResponse fullVoucherRedemption(string migUsername, string userTicket, BlueLabelOneVoucher voucher) throws FusionException;
	WebServiceResponse getAccountStatus(string liveid) throws FusionException;
	WebServiceResponse authenticate(string username) throws FusionException;
};

//////////////////////////////////////////////////////////////////////
// Event Queue Worker

interface EventQueueWorker {
};

class EventQueueWorkerServiceStats extends BaseServiceStats {
    long queueSize;
    long maxQueueSize;

    long eventsProcessed;
    int currentEventsProcessedRate;
    int peakEventsProcessedRate;
    
    long friendsAddedEvents;
    int currentFriendsAddedEventRate;
    int peakFriendsAddedEventRate; 

    long virtualGiftSentEvents;
    int currentVirtualGiftEventRate;
    int peakVirtualGiftEventRate;
};

interface EventQueueWorkerServiceAdmin {
        EventQueueWorkerServiceStats getStats() throws FusionException;
};

//////////////////////////////////////////////////////////////////////
// User Notification Service

class UserNotification {
	string message;
};

class EmailUserNotification extends UserNotification {
	string subject;
	string emailAddress; // optional
};

class SMSUserNotification extends UserNotification {
	string phoneNumber; // optional
	int smsSubType; // optional subtype
};


dictionary<int, int> NotificationMap;
dictionary<string,string> NotificationDataParam;
dictionary<string,NotificationDataParam> NotificationDataEntry;
dictionary<int, NotificationDataEntry> NotificationDataMap;

class Message {
	string key; //an unique identifier to identify this message. Unique across the notification type
	int toUserId;
	string toUsername;
	int notificationType;
	long dateCreated; 
	ParamMap parameters;
};

interface UserNotificationService {
	// alert (popup)
	void notifyFusionGroupViaAlert(int groupId, string message) throws FusionException;
	void notifyFusionUserViaAlert(string username, string message) throws FusionException;

	// email
	void notifyFusionGroupAnnouncementViaEmail(int groupId, EmailUserNotification note) throws FusionException;
	void notifyFusionGroupPostSubscribersViaEmail(int userPostId, EmailUserNotification note) throws FusionException;
	void notifyFusionUserViaEmail(string username, EmailUserNotification note) throws FusionException;
	/** Called via PHP */
	void notifyUserViaEmail(EmailUserNotification note) throws FusionException;	
	void notifyUsersViaFusionEmail(string sender, string senderPassword, StringArray recipients, EmailUserNotification note) throws FusionException;
	void sendEmailFromNoReply(string destinationAddress, string subject, string body) throws FusionException;

	// BE-1365
	void sendEmailFromNoReplyWithType(string destinationAddress, string subject, string body, string mimeType) throws FusionException;

	// SMS
	void notifyFusionGroupAnnouncementViaSMS(int groupId, SMSUserNotification note) throws FusionException;
	void notifyFusionGroupEventViaSMS(int groupId, SMSUserNotification note) throws FusionException;
	void notifyFusionUserViaSMS(string username, SMSUserNotification note) throws FusionException;
	
	//push/pull notifications
	/** Called via PHP */
	void notifyFusionUser(Message msg) throws FusionException;
	/** Called via PHP */
	NotificationMap getPendingNotificationsForUser(int userId) throws FusionException;
	/** Called via PHP */
	void clearNotificationsForUser(int userId, int notfnType, StringArray keys) throws FusionException;
	/** Called via PHP */
	void clearAllNotificationsForUser(int userId) throws FusionException;
	/** Called via PHP */
	void clearAllNotificationsByTypeForUser(int userId, int notfnType) throws FusionException;
	NotificationMap getUnreadNotificationCountForUser(int userId) throws FusionException;
	void clearAllUnreadNotificationCountForUser(int userId, bool resetAll) throws FusionException;
	NotificationDataMap getPendingNotificationDataForUser(int userId) throws FusionException;
	NotificationDataMap getUnreadPendingNotificationDataForUser(int userId) throws FusionException;
	NotificationDataEntry getPendingNotificationDataForUserByType(int userId, int notificationType) throws FusionException;
	void sendNotificationCounterToUser(int userId);

	// BE-1365: Templatized emails	
	void sendTemplatizedEmailFromNoReply(string destinationEmailAddress,
			int templateId, ParamMap templateParam) throws FusionException;
};

class UserNotificationServiceStats extends BaseServiceStats {
	long alertsSent;
	long emailsSent;
	long smsSent;
	long notificationsSent;
	int alertQueueSize;
	int emailQueueSize;
	int smsQueueSize;
	int notificationQueueSize;
};

interface UserNotificationServiceAdmin {
	UserNotificationServiceStats getStats() throws FusionException;
};

//////////////////////////////////////////////////////////////////////
// Job Scheduling Service

class GroupEvent {
	int id;
	int groupId;
	string description;
	long startTime;
	int duration;
	string chatRoomName;
	int chatRoomCategoryID;
	long dateCreated;
	int status;
};

interface JobSchedulingService {
	string scheduleFusionGroupEventNotificationViaEmail(int eventId, int groupId, long time, EmailUserNotification note) throws FusionException;
	string scheduleFusionGroupEventNotificationViaSMS(int eventId, int groupId, long time, SMSUserNotification note) throws FusionException;
	string scheduleFusionGroupEventNotificationViaAlert(int eventId, int groupId, long time, string message) throws FusionException;
	
	// temporary service methods until we find a proper place for them
	int scheduleFusionGroupEvent(GroupEvent event) throws FusionException;
	void unscheduleFusionGroupEvent(int groupEventID) throws FusionException;
	void rescheduleFusionGroupEvent(GroupEvent event) throws FusionException;
	
	void triggerJob(string jobName, string jobGroup, ParamMap jobDataMap) throws FusionException;
};

class JobSchedulingServiceStats extends BaseServiceStats {
	StringArray currentJobs;
};

interface JobSchedulingServiceAdmin {
	JobSchedulingServiceStats getStats() throws FusionException;
};

//////////////////////////////////////////////////////////////////////
// Authentication Service

enum AuthenticationServiceResponseCodeEnum {
	Failed,
	Success,
	UnknownUsername,
	UnknownCredential,
	InvalidCredential,
	CredentialsExpired,
	AuthenticationRateExceeded,
	InvalidRequestingIP,
	CredentialAlreadyExists,
	InternalError,
	UnknownError
};

class AuthenticationServiceCredentialResponse {
	AuthenticationServiceResponseCodeEnum code;
	Credential userCredential;
};

interface AuthenticationService {
	AuthenticationServiceResponseCodeEnum authenticate(Credential userCredential, string clientIP) throws FusionException; 
	/** Called via PHP */
	AuthenticationServiceResponseCodeEnum exists(int userid, byte passwordType) throws FusionException; 
	/** Called via PHP */
	AuthenticationServiceResponseCodeEnum createCredential(Credential userCredential) throws FusionException;
	AuthenticationServiceResponseCodeEnum updateCredential(Credential userCredential) throws FusionException;
	AuthenticationServiceResponseCodeEnum updateFusionCredential(Credential userCredential, string oldPassword) throws FusionException;
	/** Called via PHP */
	AuthenticationServiceResponseCodeEnum removeCredential(Credential userCredential) throws FusionException;
	/** Called via PHP */
	AuthenticationServiceCredentialResponse getCredential(int userid, byte passwordType) throws FusionException; 
	ByteArray availableCredentialTypes(int userid) throws FusionException;
	CredentialArray getCredentialsForTypes(int userid, ByteArray passwordTypes) throws FusionException;
	CredentialArray getAllCredentials(int userid) throws FusionException;
	CredentialArray getAllCredentialsFromOldSource(int userid) throws FusionException;
	void migrateUserCredentials(int userid) throws FusionException;
	int userIDForFusionUsername(string username) throws FusionException;
	AuthenticationServiceCredentialResponse getLatestCredentialByUsernameAndPasswordType(string username, byte passwordType);
	AuthenticationServiceResponseCodeEnum checkCredential(Credential userCredential);
    AuthenticationServiceResponseCodeEnum checkCredentialByUserId(int userid, string password, byte passwordType);
    AuthenticationServiceResponseCodeEnum checkCredentialByUsername(string username, string password, byte passwordType);
};

class AuthenticationServiceStats extends BaseServiceStats {
	long successfulAuthentications;
	long failedAuthentications;
	int successfulAuthenticationRate;
	int failedAuthenticationRate;
	int peakSuccessfulAuthenticationRate;
	int peakFailedAuthenticationRate;
	long peakSuccessfulAuthenticationRateDate;
	long peakFailedAuthenticationRateDate;
};

interface AuthenticationServiceAdmin {
	AuthenticationServiceStats getStats() throws FusionException;
};

//////////////////////////////////////////////////////////////////////
// Reputation

struct ScoreAndLevel {
	int score;
	int level;
};

sequence<ScoreAndLevel> ScoreAndLevelSequence;

interface ReputationService {
	void gatherAndProcess() throws FusionException;
	void processPreviouslyDumpedData(string runDateString) throws FusionException;
	void processPreviouslySortedData(string runDateString) throws FusionException;
	void updateScoreFromPreviouslyProcessedData(string runDateString) throws FusionException;
	void updateLastRunDate() throws FusionException;
	int getUserLevel(string username) throws FusionException;
	ScoreAndLevelSequence getUserScoreAndLevels(IntArray userIDs) throws FusionException;
};

class ReputationServiceStats extends BaseServiceStats {
	long lastTimeRunCompleted;
	bool processing;
};

interface ReputationServiceAdmin {
	ReputationServiceStats getStats() throws FusionException;
};

//////////////////////////////////////////////////////////////////////
// BotHunter

struct SuspectIce {
	string clientIP;
	int clientPort;
	long lastAddedTo;
	double meanTcpTimestamp;
	double meanTcpTimestampOverArrivalTime;
	string username;
};

sequence<SuspectIce> SuspectIceArray;

struct SuspectGroupIce {
	SuspectIceArray members;
	int innocentPortCount;
};

sequence<SuspectGroupIce> SuspectGroupIceArray;

/*
 * This interface is provided by the Bot Hunter ice app to allow clients (specifically
 * the BotKicker ice app) to retrieve the latest suspects.
 */
interface BotHunter {
	SuspectGroupIceArray getLatestSuspects() throws FusionException;
};

class BotHunterStats extends BaseServiceStats {
	// Instance stats
	long statsIntervalSeconds;

	// Packet capture stats
	long totalPacketProcessingNanos;
	long packetsCaptured;
	double averageProcessingTimePerPacketMicrosec;
	double packetsPerSecond;
	long ipsCached;
	long portsCached;
	long packetsCached;
	
	// Analysis stats
	long totalSequencePairsAnalyzed;
	long totalSequenceTransitions;
	long sequenceSuspectPairs;
	long ratioSuspectPairs;
	long suspectIPsReported;
	long suspectPortsReported;	
};

/*
 * This interface is implemented by BotHunters to return a BotHunterStats struct to
 * a monitoring application.
 */
interface BotHunterAdmin {
	BotHunterStats getStats() throws FusionException;
};

//////////////////////////////////////////////////////////////////////
// RecommendationGenerationService

interface RecommendationGenerationService {
	void runTransformation(int transformationID);
};

class RecommendationGenerationServiceStats extends BaseServiceStats {
	long totalJobs;
	long successfulJobs;
	long failedJobs;
	long failedJobsDueToAlreadyRunning;

	long totalRecommendationsGenerated;	
	long totalPipelinesUsed;

	long totalGenerationTimeSeconds;		
	long shortestGenerationTimeSeconds;
	long longestGenerationTimeSeconds;
};

/*
 * This interface is implemented by BotHunters to return a BotHunterStats struct to
 * a monitoring application.
 */
interface RecommendationGenerationServiceAdmin {
	RecommendationGenerationServiceStats getStats() throws FusionException;
};

//////////////////////////////////////////////////////////////////////
// MessageSwitchboard

struct ChatDefinitionIce {
    string chatStorageID;
    StringArray participantUsernames;
    byte chatType;
    int unreadMessageCount;
    int contactID;
    string groupOwner;
    byte isClosedChat;
    string displayGUID;
    byte messageType;
    string chatName;
    byte isPassivatedChat; // pt60829322
};

sequence<ChatDefinitionIce> ChatDefinitionIceArray;

interface MessageSwitchboard {

	/** Called via PHP */
    bool isUserChatSyncEnabled(Connection* cxn,
    		string username,int userID) throws FusionException;

	/** Called via PHP */
	ChatDefinitionIceArray getChats(int userID,
		int chatListVersion, int limit, byte chatType) throws FusionException;

	ChatDefinitionIceArray getChats2(int userID,
		int chatListVersion, int limit, byte chatType,Connection* cxn) throws FusionException;

	void onGetChats(Connection* cxn, int userID,
		int chatListVersion, int limit, byte chatType,
		short transactionId, string parentUsername) throws FusionException;

	/** Called via PHP. Maintained for backward compatibility */
	void getAndPushMessages(string username, byte chatType, string suppliedChatID,
		long oldestMessageTimestamp, long newestMessageTimestamp,
		int limit,
		Connection* cxn) throws FusionException;

	/** PHP should switch to calling this (currently isnt) */
	void getAndPushMessages2(string username, byte chatType, string suppliedChatID,
		long oldestMessageTimestamp, long newestMessageTimestamp,
		int limit,
		Connection* cxn,
		int deviceType, short clientVersion,
		short fusionPktTransactionId) throws FusionException;

    void onCreateGroupChat(ChatDefinitionIce storedGroupChat,
		string creatorUsername, string privateChatPartnerUsername,
		GroupChat* groupChatRemote) throws FusionException;

	/**pt61094672 - On-demand debug logging for ChatGroup.addParticipant() */
	void onJoinGroupChat(string username,int userID,string groupChatGUID, bool debug, User* userProxy)
		throws FusionException;

    void onLeaveGroupChat(string username,int userID,string groupChatGUID,
    	User* userProxy) throws FusionException;

	/**pt55378230: chatroom support*/
	void onJoinChatRoom(string username,int userID,string chatRoomName) throws FusionException;

	/**pt55378230: chatroom support*/
    void onLeaveChatRoom(string username,int userID,string chatRoomName,User* userProxy) throws FusionException;

    bool onSendFusionMessageToIndividual(
    		Session* currentSession,
    		User* parentUser,
    		MessageDataIce messageData,
    		string destinationUsername,
    		StringArray uniqueUsersPrivateChattedWith,
    		int deviceType,short clientVersion,
    		UserDataIce senderUserData,
    		string recipientDisplayPicture) throws FusionException;
	
	void onSendFusionMessageToGroupChat(
    		Session* currentSession, User* parentUser,
    		MessageDataIce messageData, string groupChatID,
    		int deviceType, short clientVersion) throws FusionException;

	// pt55378230: chatroom support
	void onSendFusionMessageToChatRoom(
    		Session* currentSession, User* parentUser,
    		MessageDataIce messageData, string chatRoomName,
    		int deviceType, short clientVersion) throws FusionException;

    bool onSendMessageToAllUsersInChat(
       		Session* currentSession,
    		User* parentUser,
    		MessageDataIce messageData,
    		UserDataIce senderUserData) throws FusionException;
	
	void onCreatePrivateChat(int userID,string username,string otherUser,
    		int deviceType,short clientVersion,
    		UserDataIce senderUserData, string recipientDisplayPicture) throws FusionException;
	
	void onLeavePrivateChat(int userID,string username,string otherUser,
    		int deviceType,short clientVersion) throws FusionException;
		
	GroupChat* ensureGroupChatExists(Session* currentSession, string groupChatID) throws FusionException;
	
	void onLogon(int userID, Session* sess, short transactionID, string parentUsername) throws FusionException;
	
	void setChatName(string parentUsername,string suppliedChatID,byte chatType,string chatName,
						Registry* regy) throws FusionException;				
};

class MessageSwitchboardStats extends BaseServiceStats {
};

interface MessageSwitchboardAdmin {
	MessageSwitchboardStats getStats() throws FusionException;
};
//////////////////////////////////////////////////////////////////////
// RecommendationDataCollectionService

struct ServiceStatsLongFieldValue{
	long value;
	long lastUpdatedTime;
};

dictionary<int,ServiceStatsLongFieldValue> IntToServiceStatsLongFieldValueMap;
dictionary<string,ServiceStatsLongFieldValue> StringToServiceStatsLongFieldValueMap;

class RecommendationDataCollectionServiceStats extends BaseServiceStats {
	ServiceStatsLongFieldValue totalReceivedDataCount;
	IntToServiceStatsLongFieldValueMap totalReceivedDataCountByDataType;
	ServiceStatsLongFieldValue totalSuccessfullyProcessedDataCount;
	IntToServiceStatsLongFieldValueMap totalSuccessfullyProcessedDataCountByDataType; 
	ServiceStatsLongFieldValue totalFailedProcessedDataCount; 
	StringToServiceStatsLongFieldValueMap totalFailedProcessedDataCountByErrorCauseCode;
};


class CollectedDataIce {
	int dataType;
	long createTimestamp;
};

class CollectedAddressBookDataIce extends CollectedDataIce{
	int submitterUserId;
	int contactType;
	StringArray contactValues;
};

class CollectedRewardProgramTriggerSummaryDataIce extends CollectedDataIce{
	string id;
	string host;
	string instance;
	int programType;
	long minReceivedTimestamp;
	long maxReceivedTimestamp;
	long receivedCount;
	long droppedCount;
	long failedCount;
	long successfulCount;
	long dequeuedCount;
	long minTimeSpentInQueue;
	long maxTimeSpentInQueue;
	double varianceTimeSpentInQueue;
	double meanTimeSpentInQueue;
	long minProcessingTimeAfterDequeue;
	long maxProcessingTimeAfterDequeue;
	double varianceProcessingTimeAfterDequeue;
	double meanProcessingTimeAfterDequeue;
};

interface RecommendationDataCollectionServiceAdmin{
	RecommendationDataCollectionServiceStats getStats() throws FusionExceptionWithRefCode;
};

interface RecommendationDataCollectionService {
	void logData(CollectedDataIce dataIce) throws FusionExceptionWithRefCode;
};


// End of interfaces used for system administration and monitoring
//////////////////////////////////////////////////////////////////////
}; // module slice
}; // module fusion
}; // module projectgoth
}; // module com
