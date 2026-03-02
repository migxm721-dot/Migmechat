/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.ChatRoomEmoteLogData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.data.StoreCategoryData;
import com.projectgoth.fusion.data.StoreItemData;
import com.projectgoth.fusion.data.StoreItemInventorySummaryData;
import com.projectgoth.fusion.data.StoreRatingSummaryData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.data.VirtualGiftReceivedData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.emote.GiftSentToUserMessageData;
import com.projectgoth.fusion.emote.gift.GiftAsync;
import com.projectgoth.fusion.gateway.packet.FusionPktMidletTab;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Gift
extends EmoteCommand {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Gift.class));
    private static final DecimalFormat TWO_DECIMAL_POINT_FORMAT = new DecimalFormat("0.00");
    private static final DecimalFormat ONE_DECIMAL_POINT_FORMAT = new DecimalFormat("0.0");
    private Pattern giftHelpPattern = Pattern.compile("^/g(ift)?( help)?$", 2);
    private Pattern giftInventoryPattern = Pattern.compile("^/g(?:ift)? (unlocked|ul)?$", 2);
    private Pattern giftListPattern = Pattern.compile("^/g(?:ift)? (list|featured|popular|new|recent)(?: (\\d+))?$", 2);
    private Pattern giftSearchPattern = Pattern.compile("^/g(?:ift)? (search|display) (.+)$", 2);
    private Pattern giftReceivedPattern = Pattern.compile("^/g(?:ift)? gifts(?: ([a-z0-9_.-]+))?(?: (\\d+))?$", 2);
    private Pattern giftAllPattern = Pattern.compile("^/g(?:ift)? all(?: (.+?))?(?: -m (\\S.+?))?$", 2);
    private Pattern giftCategoryPattern = Pattern.compile("^/g(?:ift)? category(?: (.+?)(?: (\\d+))?)?$", 2);
    private Pattern giftRatePattern = Pattern.compile("^/g(?:ift)? rate (.+?) (\\d+([.,]\\d*)?)$", 2);
    private Pattern giftToUserPattern = Pattern.compile("^/g(?:ift)? ([a-z0-9._-]+) (.+?)(?: -m (\\S.+?))?$", 2);
    private Pattern giftInventoryToUserPattern = Pattern.compile("^/g(?:ift)? -u ([a-z0-9._-]+) (.+?)(?: -m (\\S.+?))?$", 2);
    private Pattern giftInventoryNoUserPattern = Pattern.compile("^/g(?:ift)? -u?$", 2);

    public Gift(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    @Override
    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        try {
            boolean result = false;
            switch (chatSource.getChatType()) {
                case PRIVATE_CHAT: {
                    result = this.handleVirtualGiftCommand(messageData, chatSource, VirtualGiftReceivedData.PurchaseLocationEnum.PRIVATE_CHAT_COMMAND);
                    break;
                }
                case GROUP_CHAT: {
                    result = this.handleVirtualGiftCommand(messageData, chatSource, VirtualGiftReceivedData.PurchaseLocationEnum.GROUP_CHAT_COMMAND);
                    break;
                }
                case CHATROOM_CHAT: {
                    result = this.handleVirtualGiftCommand(messageData, chatSource, VirtualGiftReceivedData.PurchaseLocationEnum.CHATROOM_COMMAND);
                    break;
                }
            }
            if (!result) {
                return EmoteCommand.ResultType.HANDLED_AND_STOP;
            }
            return EmoteCommand.ResultType.HANDLED_AND_CONTINUE;
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }

    private boolean handleGiftHelpEmote(short clientVersion, ConnectionPrx connectionProxy, MessageData messageData, ChatSource chatSource) throws NoSuchFieldException, FusionException {
        if (clientVersion >= 400) {
            FusionPktMidletTab pktMidletTab = new FusionPktMidletTab();
            pktMidletTab.setURL(SystemProperty.get("WebServerURL") + SystemProperty.get("VirtualGiftEmoteHelpPath", "/sites/midlet/migworld/get_page?page_id=13995"));
            pktMidletTab.setFocus((byte)1);
            connectionProxy.putSerializedPacket(pktMidletTab.toSerializedBytes());
        } else {
            Object[] giftCommands = new String[]{"/gift", "/gift ul", "/gift unlocked", "/gift -u [username] [gift name]", "/gift list", "/gift new", "/gift featured", "/gift popular", "/gift recent", "/gift [username] [gift name]", "/gift [username] [gift name] -m [message]", "/gift all [gift name] -m [message]", "/gift search [text]", "/gift display [gift name]", "/gift category", "/gift category [category name]", "/gift rate [gift name] [rating]", "/gift gifts", "/gift gifts [username]"};
            messageData.messageText = "List of allowed gift commands: \n (" + StringUtil.join(giftCommands, "), \n(") + ")";
            this.emoteCommandData.updateMessageData(messageData);
            chatSource.sendMessageToSender(messageData);
        }
        return false;
    }

    private boolean handleGiftInventoryEmote(int userid, Matcher giftListPatternMatcher, Content contentBean, MessageData messageData, ChatSource chatSource) throws FusionException, RemoteException {
        List siisd = contentBean.getStoreItemsInventoryByType(userid, StoreItemData.TypeEnum.VIRTUAL_GIFT);
        if (siisd == null || siisd.isEmpty()) {
            messageData.messageText = "Sorry, there is no unlock gift available for your migLevel at the moment.";
        } else {
            this.createVirtualGiftInventoryListMessage(messageData, siisd);
        }
        this.emoteCommandData.updateMessageData(messageData, false);
        chatSource.sendMessageToSender(messageData);
        return false;
    }

    private boolean handleGiftListEmote(String parentUsername, Matcher giftListPatternMatcher, Content contentBean, MessageData messageData, ChatSource chatSource) throws FusionException, RemoteException {
        String listType = giftListPatternMatcher.group(1);
        int limit = 20;
        if (giftListPatternMatcher.group(2) != null) {
            int input_limit = Integer.parseInt(giftListPatternMatcher.group(2));
            if (input_limit == 5 || input_limit == 10 || input_limit == 20) {
                limit = input_limit;
            } else {
                messageData.messageText = "Limit for [/gift " + listType + "] can only be 5 or 10";
                this.emoteCommandData.updateMessageData(messageData);
                chatSource.sendMessageToSender(messageData);
            }
        }
        List gifts = null;
        if (SystemProperty.getBool("GiftEmoteCustomListsEnabled", true)) {
            if (listType.equalsIgnoreCase("featured")) {
                gifts = contentBean.getFeaturedVirtualGifts(parentUsername, 0, limit);
            } else if (listType.equalsIgnoreCase("popular")) {
                gifts = contentBean.getPopularVirtualGifts(parentUsername, 0, limit);
            } else if (listType.equalsIgnoreCase("new")) {
                gifts = contentBean.getNewVirtualGifts(parentUsername, 0, limit);
            } else if (listType.equalsIgnoreCase("recent")) {
                gifts = contentBean.getRecentGiftsSentBy(parentUsername, parentUsername, 0, limit);
            }
        }
        if (gifts == null) {
            gifts = contentBean.getFeaturedPopularNewVirtualGifts(parentUsername, 0, limit);
        }
        if (gifts == null || gifts.isEmpty()) {
            messageData.messageText = "Sorry, there is no gift available for your migLevel at the moment.";
        } else {
            this.createVirtualGiftListMessage(messageData, gifts);
        }
        this.emoteCommandData.updateMessageData(messageData, false);
        chatSource.sendMessageToSender(messageData);
        return false;
    }

    private boolean handleGiftSearchEmote(String parentUsername, Matcher giftSearchPatternMatcher, Content contentBean, MessageData messageData, ChatSource chatSource) throws RemoteException, FusionException {
        boolean isSearch = giftSearchPatternMatcher.group(1).equalsIgnoreCase("search");
        String search = giftSearchPatternMatcher.group(2);
        int searchMinLength = 3;
        if (isSearch && search.length() < searchMinLength) {
            messageData.messageText = "Search string is to short [" + search + "]. Use at least " + searchMinLength + " characters.";
        } else {
            List gifts = contentBean.searchVirtualGifts(parentUsername, 0, search, isSearch ? 20 : 1, isSearch);
            if (gifts.size() <= 0) {
                messageData.messageText = "No gift found for [" + search + "]";
            } else if (gifts.size() == 1) {
                StoreRatingSummaryData ratingSummary;
                VirtualGiftData gift = (VirtualGiftData)gifts.get(0);
                messageData.messageText = gift.getName() + " " + gift.getHotKey() + " (" + TWO_DECIMAL_POINT_FORMAT.format(gift.getRoundedPrice()) + " " + gift.getCurrency() + ")";
                StoreCategoryData cat = (gift = contentBean.getVirtualGiftDetails(gift)).getStoreCategory();
                if (cat != null) {
                    messageData.messageText = messageData.messageText + " -- category: " + cat.name;
                }
                if ((ratingSummary = gift.getStoreRatingSummary()) != null) {
                    messageData.messageText = messageData.messageText + " -- rating: " + ONE_DECIMAL_POINT_FORMAT.format(ratingSummary.average);
                }
                messageData.emoticonKeys = new LinkedList<String>();
                messageData.emoticonKeys.add(gift.getHotKey());
            } else {
                this.createVirtualGiftListMessage(messageData, gifts);
            }
        }
        this.emoteCommandData.updateMessageData(messageData, false);
        chatSource.sendMessageToSender(messageData);
        return false;
    }

    private boolean handleGiftReceivedEmote(short clientVersion, String parentUsername, Matcher giftReceivedPatternMatcher, ConnectionPrx connectionProxy, User userBean, Content contentBean, MessageData messageData, ChatSource chatSource) throws Exception {
        String username;
        if (giftReceivedPatternMatcher.group(1) == null && clientVersion >= 400) {
            String myGiftReceivedURL = SystemProperty.get("WebServerURL") + "/sites/index.php?c=profile&a=gifts_received&v=midlet";
            FusionPktMidletTab pktMidletTab = new FusionPktMidletTab();
            pktMidletTab.setURL(myGiftReceivedURL);
            pktMidletTab.setFocus((byte)1);
            connectionProxy.putSerializedPacket(pktMidletTab.toSerializedBytes());
            return false;
        }
        if (giftReceivedPatternMatcher.group(1) == null) {
            username = parentUsername;
        } else {
            username = giftReceivedPatternMatcher.group(1).toLowerCase();
            UserData targetUser = userBean.loadUser(username, true, false);
            if (targetUser == null) {
                throw new Exception(username + " is not a user");
            }
            UserProfileData.StatusEnum profileType = userBean.getUserProfileStatus(username);
            if (profileType.equals((Object)UserProfileData.StatusEnum.PRIVATE) || profileType.equals((Object)UserProfileData.StatusEnum.CONTACTS_ONLY) && !targetUser.broadcastList.contains(parentUsername)) {
                throw new Exception("You are not allowed to see " + username + "'s received gifts");
            }
        }
        int limit = 20;
        if (giftReceivedPatternMatcher.group(2) != null) {
            int input_limit = Integer.parseInt(giftReceivedPatternMatcher.group(2));
            if (input_limit == 5 || input_limit == 10 || input_limit == 20) {
                limit = input_limit;
            } else {
                messageData.messageText = "Limit for [/gift gifts] can only be 5 or 10";
                this.emoteCommandData.updateMessageData(messageData);
                chatSource.sendMessageToSender(messageData);
            }
        }
        List gifts = contentBean.getRecentGiftsReceivedBy(username, parentUsername, 0, limit);
        this.createVirtualGiftListMessage(messageData, gifts);
        messageData.messageText = "Recent gifts received by " + username + ": \n" + messageData.messageText;
        this.emoteCommandData.updateMessageData(messageData, false);
        chatSource.sendMessageToSender(messageData);
        return false;
    }

    private boolean handleUnlockedDefaultMessage(MessageData messageData, ChatSource chatSource) throws FusionException {
        messageData.messageText = "You can use the command: /gift -u <username> <gift name> to send a free unlocked gift to anyone in migme. Type /gift unlocked to see all the free unlocked gifts that you have.";
        this.emoteCommandData.updateMessageData(messageData);
        chatSource.sendMessageToSender(messageData);
        String defaultMessage = SystemProperty.get("GiftEmoteUnlockedDefaultMessage", "");
        if (!defaultMessage.equals("")) {
            messageData.messageText = defaultMessage;
            this.emoteCommandData.updateMessageData(messageData);
            chatSource.sendMessageToSender(messageData);
        }
        return false;
    }

    private boolean handleGiftAllEmote(String parentUsername, Matcher giftAllPatternMatcher, VirtualGiftReceivedData.PurchaseLocationEnum purchaseLocation, User userBean, Content contentBean, MessageData messageData, ChatSource chatSource) throws Exception {
        if (giftAllPatternMatcher.group(1) != null) {
            List<String> allRecipients;
            boolean isLowPriceGift;
            String giftName = giftAllPatternMatcher.group(1);
            String giftMessage = giftAllPatternMatcher.group(2);
            int systemWideRateLimit = SystemProperty.getInt("SystemWideGiftAllRateLimit", 50);
            if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.VIRTUALGIFT.toString(), MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.RateLimitKeySpace.VIRTUAL_GIFT_RATE_LIMIT, "systemwidegiftall"), (long)systemWideRateLimit, 1000L)) {
                log.warn((Object)("/giftall rate limit[" + systemWideRateLimit + "/sec] exceeded. Unable to process request for user: " + messageData.username));
                throw new Exception("Sorry, /giftall is not available right now. Please try again later.");
            }
            VirtualGiftData gift = null;
            try {
                gift = this.findVirtualGiftByName(giftName, parentUsername, messageData);
            }
            catch (VirtualGiftNotFoundException e) {
                chatSource.sendMessageToSender(messageData);
                return false;
            }
            int rateLimitSeconds = SystemProperty.getInt("GiftAllRateLimitInSeconds", 60);
            if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.VIRTUALGIFT.toString(), MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.RateLimitKeySpace.VIRTUAL_GIFT_RATE_LIMIT, parentUsername, "giftall"), 1L, (long)(rateLimitSeconds * 1000))) {
                throw new Exception(String.format("You can only use /gift all every %s. Try again later.", DateTimeUtils.timeInvertalInSecondsToPrettyString(rateLimitSeconds)));
            }
            if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.VIRTUALGIFT.toString(), MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.RateLimitKeySpace.VIRTUAL_GIFT_RATE_LIMIT, parentUsername, gift.getId().toString()), 1L, (long)(rateLimitSeconds * 1000))) {
                throw new Exception(String.format("You can only send the same gift every %s. Try sending a different gift.", DateTimeUtils.timeInvertalInSecondsToPrettyString(rateLimitSeconds)));
            }
            double lowPriceThreshold = contentBean.isGiftLowPrice(gift, parentUsername);
            boolean bl = isLowPriceGift = lowPriceThreshold >= 0.0;
            if (isLowPriceGift && giftMessage != null) {
                throw new Exception(String.format("Gifts priced %s %s and less can't have custom message. Please try another gift instead if you'd like to attach a message to your gift.", TWO_DECIMAL_POINT_FORMAT.format(lowPriceThreshold), gift.getCurrency()));
            }
            if (messageData.messageDestinations.get((int)0).type == MessageDestinationData.TypeEnum.CHAT_ROOM) {
                allRecipients = Arrays.asList(chatSource.getAllUsernamesInChat(false));
                if (allRecipients.size() == 0) {
                    messageData.messageText = "There are no other users in the room.";
                    this.emoteCommandData.updateMessageData(messageData);
                    chatSource.sendMessageToSender(messageData);
                    return false;
                }
            } else if (messageData.messageDestinations.get((int)0).type == MessageDestinationData.TypeEnum.GROUP) {
                try {
                    GroupChatPrx groupChatPrx = chatSource.getSessionI().findGroupChatPrx(messageData.messageDestinations.get((int)0).destination);
                    allRecipients = Arrays.asList(groupChatPrx.getParticipants(parentUsername));
                }
                catch (FusionException e) {
                    log.error((Object)("Failed to find group chat '" + messageData.messageDestinations.get((int)0).destination + "'"), (Throwable)((Object)e));
                    messageData.messageText = "Unable to find group chat.";
                    this.emoteCommandData.updateMessageData(messageData);
                    chatSource.sendMessageToSender(messageData);
                    return false;
                }
                if (allRecipients.size() == 0) {
                    messageData.messageText = "There are no other users in this group chat.";
                    this.emoteCommandData.updateMessageData(messageData);
                    chatSource.sendMessageToSender(messageData);
                    return false;
                }
            } else if (messageData.messageDestinations.get((int)0).type == MessageDestinationData.TypeEnum.INDIVIDUAL) {
                allRecipients = new ArrayList<String>(1);
                allRecipients.add(messageData.messageDestinations.get((int)0).destination);
            } else {
                log.debug((Object)("Unsupported message destination '" + messageData.messageDestinations.get((int)0).type.toString() + "' for /gift all"));
                throw new Exception("Unsupported message destination '" + messageData.messageDestinations.get((int)0).type.toString() + "' for /gift all");
            }
            if (isLowPriceGift && allRecipients.size() < 2) {
                throw new Exception(String.format("Gifts priced %s %s and less are only available via /gift all for more than 1 recipient. Try other gifts instead?", TWO_DECIMAL_POINT_FORMAT.format(lowPriceThreshold), gift.getCurrency()));
            }
            if (chatSource.getSessionI().getBalance() < gift.getPrice() * (double)allRecipients.size()) {
                log.warn((Object)(chatSource.getSessionI().getUsername() + " doesn't have enough credit[" + chatSource.getSessionI().getBalance() + "] to gift shower[ " + gift.getName() + "] priced [" + gift.getPrice() + " " + gift.getCurrency() + " X " + allRecipients.size() + "]"));
                throw new FusionEJBException("You do not have enough credit to purchase the gift");
            }
            if (chatSource.chatType == ChatSource.ChatType.CHATROOM_CHAT) {
                try {
                    ChatRoomPrx chatRoomPrx = chatSource.getSessionI().findChatRoomPrx(messageData.messageDestinations.get((int)0).destination);
                    if (SystemProperty.getBool(SystemPropertyEntities.Default.ASYNC_GIFT_ALL_ENABLED)) {
                        chatRoomPrx.submitGiftAllTask(gift.getId(), giftMessage, messageData.toIceObject());
                    }
                    Gift.giftAll(new ChatRoomData(chatRoomPrx.getRoomData()), giftMessage, chatRoomPrx.getMaximumMessageLength(parentUsername), isLowPriceGift, purchaseLocation, allRecipients, gift, messageData, this.emoteCommandData, chatSource, chatSource.getSessionI().getIcePrxFinder());
                }
                catch (FusionException e) {
                    log.error((Object)("Failed to find chat room '" + messageData.messageDestinations.get((int)0).destination + "'"), (Throwable)((Object)e));
                    messageData.messageText = "Unable to find chat room " + messageData.messageDestinations.get((int)0).destination;
                    this.emoteCommandData.updateMessageData(messageData);
                    chatSource.sendMessageToSender(messageData);
                    return false;
                }
            } else {
                Gift.giftAll(null, giftMessage, -1, isLowPriceGift, purchaseLocation, allRecipients, gift, messageData, this.emoteCommandData, chatSource, chatSource.getSessionI().getIcePrxFinder());
            }
            return false;
        }
        messageData.messageText = "To buy a gift for all users in this room, type \"/gift all <gift name>\". Type \"/gift list\" to see available gifts.";
        this.emoteCommandData.updateMessageData(messageData);
        chatSource.sendMessageToSender(messageData);
        return false;
    }

    public static boolean giftAll(ChatRoomData chatroomData, String giftMessage, int maxMessageLength, boolean lowPriceGift, VirtualGiftReceivedData.PurchaseLocationEnum purchaseLocation, List<String> allRecipients, VirtualGiftData gift, MessageData messageData, EmoteCommandData emoteCommandData, ChatSource chatSource, IcePrxFinder ipf) throws FusionException {
        return GiftAsync.giftAll(chatroomData, giftMessage, maxMessageLength, lowPriceGift, purchaseLocation, allRecipients, gift, messageData, emoteCommandData, chatSource, ipf);
    }

    private boolean handleGiftCategoryEmote(String parentUsername, Matcher giftCategoryPatternMatcher, Content contentBean, MessageData messageData, ChatSource chatSource) throws Exception {
        if (giftCategoryPatternMatcher.group(1) != null) {
            List gifts;
            String categoryName = giftCategoryPatternMatcher.group(1);
            int limit = 20;
            if (giftCategoryPatternMatcher.group(2) != null) {
                int input_limit = Integer.parseInt(giftCategoryPatternMatcher.group(2));
                if (input_limit == 5 || input_limit == 10 || input_limit == 20) {
                    limit = input_limit;
                } else {
                    messageData.messageText = "Limit for [/gift category] can only be 5 or 10";
                    this.emoteCommandData.updateMessageData(messageData);
                    chatSource.sendMessageToSender(messageData);
                }
            }
            if ((gifts = contentBean.getVirtualGiftForCategory(parentUsername, categoryName, 0, limit)).size() <= 0) {
                messageData.messageText = "No items in category [" + categoryName + "]";
            } else {
                this.createVirtualGiftListMessage(messageData, gifts);
            }
        } else {
            try {
                ArrayList categories = contentBean.getVirtualGiftCategoryNames();
                messageData.messageText = StringUtil.join(categories, "; ");
            }
            catch (Exception e) {
                messageData.messageText = "Sorry, unable to get virtualgift categories: " + e.getMessage();
            }
        }
        this.emoteCommandData.updateMessageData(messageData, false);
        chatSource.sendMessageToSender(messageData);
        return false;
    }

    private boolean handleGiftRateEmote(Matcher giftRatePatternMatcher, Content contentBean, MessageData messageData, ChatSource chatSource) throws Exception {
        int rating;
        String giftName = giftRatePatternMatcher.group(1);
        if (giftRatePatternMatcher.group(3) != null || (rating = Integer.parseInt(giftRatePatternMatcher.group(2))) > 5 || rating == 0) {
            messageData.messageText = "Rating value must be 1, 2, 3, 4, or 5";
            this.emoteCommandData.updateMessageData(messageData);
            chatSource.sendMessageToSender(messageData);
            return false;
        }
        try {
            boolean rated = contentBean.rateVirtualGift(chatSource.getSessionI().getUserID(), giftName, rating);
            messageData.messageText = rated ? "Thanks, your rating of " + rating + " for [" + giftName + "] was registered" : "No gift found for [" + giftName + "]";
        }
        catch (Exception e) {
            messageData.messageText = "Sorry, we couldn't register your rating";
        }
        this.emoteCommandData.updateMessageData(messageData);
        chatSource.sendMessageToSender(messageData);
        return false;
    }

    private boolean handleGiftInventoryToUserEmote(String parentUsername, Matcher giftToUserPatternMatcher, Content contentBean, VirtualGiftReceivedData.PurchaseLocationEnum purchaseLocation, MessageData messageData, ChatSource chatSource) throws Exception {
        String recipientUsername = giftToUserPatternMatcher.group(1).toLowerCase();
        String giftName = giftToUserPatternMatcher.group(2);
        String giftMessage = giftToUserPatternMatcher.group(3);
        VirtualGiftData gift = null;
        try {
            gift = this.findVirtualGiftByName(giftName, parentUsername, messageData);
        }
        catch (VirtualGiftNotFoundException e) {
            chatSource.sendMessageToSender(messageData);
            return false;
        }
        int rateLimitSeconds = SystemProperty.getInt("GiftSingleRateLimitInSeconds", 60);
        if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.VIRTUALGIFT.toString(), MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.RateLimitKeySpace.VIRTUAL_GIFT_RATE_LIMIT, parentUsername, recipientUsername, gift.getId().toString()), 1L, (long)(rateLimitSeconds * 1000))) {
            throw new Exception(String.format("You can only send the same gift to %s every %s. Try sending a different gift.", recipientUsername, DateTimeUtils.timeInvertalInSecondsToPrettyString(rateLimitSeconds)));
        }
        ArrayList<String> recipientUsernames = new ArrayList<String>();
        recipientUsernames.add(recipientUsername);
        StoreItemData storeItemData = new StoreItemData();
        storeItemData.id = gift.getStoreitemId();
        storeItemData.referenceData = gift;
        contentBean.giveVirtualGiftForMultipleUsers(chatSource.getSessionI().getUsername(), recipientUsernames, storeItemData, purchaseLocation.value(), false, giftMessage);
        messageData.contentType = MessageData.ContentTypeEnum.EMOTE;
        GiftSentToUserMessageData giftSentToUserMessageData = new GiftSentToUserMessageData(parentUsername, recipientUsername, giftMessage, gift);
        giftSentToUserMessageData.source = messageData.source;
        giftSentToUserMessageData.sourceType = messageData.sourceType;
        giftSentToUserMessageData.sendReceive = messageData.sendReceive;
        giftSentToUserMessageData.type = messageData.type;
        giftSentToUserMessageData.username = messageData.username;
        giftSentToUserMessageData.messageDestinations = messageData.messageDestinations;
        if (SystemProperty.getBool("logSingleGiftEmote", false)) {
            int chatRoomID = -1;
            int chatRoomGroupID = -1;
            if (messageData.messageDestinations.get((int)0).type == MessageDestinationData.TypeEnum.CHAT_ROOM) {
                ChatRoomPrx chatRoomPrx;
                try {
                    chatRoomPrx = chatSource.getSessionI().findChatRoomPrx(messageData.messageDestinations.get((int)0).destination);
                }
                catch (FusionException e) {
                    log.error((Object)("Failed to find chat room '" + messageData.messageDestinations.get((int)0).destination + "'"), (Throwable)((Object)e));
                    giftSentToUserMessageData.messageText = "Unable to find chat room " + messageData.messageDestinations.get((int)0).destination;
                    this.emoteCommandData.updateMessageData(giftSentToUserMessageData);
                    chatSource.sendMessageToSender(giftSentToUserMessageData);
                    return false;
                }
                ChatRoomDataIce roomData = chatRoomPrx.getRoomData();
                chatRoomID = roomData.id;
                chatRoomGroupID = roomData.groupID;
            }
            ChatRoomEmoteLogData logData = new ChatRoomEmoteLogData(messageData.source, "", "/gift", chatRoomID, chatRoomGroupID, -1, gift.getId() + ";1");
            chatSource.getSessionI().logEmoteData(logData);
        }
        this.emoteCommandData.updateMessageData(giftSentToUserMessageData, false);
        messageData.setMimeTypeAndData(gift, parentUsername, recipientUsernames, VirtualGiftData.GiftingType.GIFT, giftMessage);
        chatSource.sendMessageToAllUsersInChat(giftSentToUserMessageData);
        return false;
    }

    private boolean handleGiftToUserEmote(String parentUsername, Matcher giftToUserPatternMatcher, Content contentBean, VirtualGiftReceivedData.PurchaseLocationEnum purchaseLocation, MessageData messageData, ChatSource chatSource) throws Exception {
        String recipientUsername = giftToUserPatternMatcher.group(1).toLowerCase();
        String giftName = giftToUserPatternMatcher.group(2);
        String giftMessage = giftToUserPatternMatcher.group(3);
        VirtualGiftData gift = null;
        try {
            gift = this.findVirtualGiftByName(giftName, parentUsername, messageData);
        }
        catch (VirtualGiftNotFoundException e) {
            chatSource.sendMessageToSender(messageData);
            return false;
        }
        int rateLimitSeconds = SystemProperty.getInt("GiftSingleRateLimitInSeconds", 60);
        if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.VIRTUALGIFT.toString(), MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.RateLimitKeySpace.VIRTUAL_GIFT_RATE_LIMIT, parentUsername, recipientUsername, gift.getId().toString()), 1L, (long)(rateLimitSeconds * 1000))) {
            throw new Exception(String.format("You can only send the same gift to %s every %s. Try sending a different gift.", recipientUsername, DateTimeUtils.timeInvertalInSecondsToPrettyString(rateLimitSeconds)));
        }
        if (chatSource.getSessionI().getBalance() < gift.getPrice()) {
            log.warn((Object)(parentUsername + " doesn't have enough credit[" + chatSource.getSessionI().getBalance() + "] to purchase [" + gift.getName() + "] priced [" + gift.getPrice() + " " + gift.getCurrency() + "]"));
            throw new FusionEJBException("You do not have enough credit to purchase the gift");
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Temp.SE425_ENABLED)) {
            AccountEntrySourceData accountEntrySourceData = chatSource.getSessionI() != null ? new AccountEntrySourceData(chatSource.getSessionI()) : (chatSource.getSessionPrx() != null ? new AccountEntrySourceData(chatSource.getSessionPrx()) : new AccountEntrySourceData(Gift.class));
            contentBean.buyVirtualGift(chatSource.getSessionI().getUsername(), recipientUsername, gift.getId(), purchaseLocation.value(), false, giftMessage, null, accountEntrySourceData);
        } else {
            contentBean.buyVirtualGift(chatSource.getSessionI().getUsername(), recipientUsername, gift.getId(), purchaseLocation.value(), false, giftMessage, null, new AccountEntrySourceData(chatSource.getSessionI().getClass()));
        }
        messageData.contentType = MessageData.ContentTypeEnum.EMOTE;
        String returnMsg = Gift.formatUserNameWithLevel(parentUsername) + " gives ";
        returnMsg = StringUtil.startsWithaVowel(gift.getName()) ? returnMsg + "an " : returnMsg + "a ";
        returnMsg = returnMsg + gift.getName() + " " + gift.getHotKey() + " to " + Gift.formatUserNameWithLevel(recipientUsername) + "!";
        if (giftMessage != null) {
            returnMsg = returnMsg + " -- " + giftMessage;
        }
        messageData.messageText = "<< " + returnMsg + " >>";
        messageData.emoticonKeys = new LinkedList<String>();
        messageData.emoticonKeys.add(gift.getHotKey());
        if (SystemProperty.getBool("logSingleGiftEmote", false)) {
            int chatRoomID = -1;
            int chatRoomGroupID = -1;
            if (messageData.messageDestinations.get((int)0).type == MessageDestinationData.TypeEnum.CHAT_ROOM) {
                ChatRoomPrx chatRoomPrx;
                try {
                    chatRoomPrx = chatSource.getSessionI().findChatRoomPrx(messageData.messageDestinations.get((int)0).destination);
                }
                catch (FusionException e) {
                    log.error((Object)("Failed to find chat room '" + messageData.messageDestinations.get((int)0).destination + "'"), (Throwable)((Object)e));
                    messageData.messageText = "Unable to find chat room " + messageData.messageDestinations.get((int)0).destination;
                    this.emoteCommandData.updateMessageData(messageData);
                    chatSource.sendMessageToSender(messageData);
                    return false;
                }
                ChatRoomDataIce roomData = chatRoomPrx.getRoomData();
                chatRoomID = roomData.id;
                chatRoomGroupID = roomData.groupID;
            }
            ChatRoomEmoteLogData logData = new ChatRoomEmoteLogData(messageData.source, "", "/gift", chatRoomID, chatRoomGroupID, -1, gift.getId() + ";1");
            chatSource.getSessionI().logEmoteData(logData);
        }
        this.emoteCommandData.updateMessageData(messageData, false);
        messageData.setMimeTypeAndData(gift, parentUsername, Arrays.asList(recipientUsername), VirtualGiftData.GiftingType.GIFT, giftMessage);
        chatSource.sendMessageToAllUsersInChat(messageData);
        return false;
    }

    private boolean handleVirtualGiftCommand(MessageData messageData, ChatSource chatSource, VirtualGiftReceivedData.PurchaseLocationEnum purchaseLocation) throws Exception {
        try {
            Matcher giftToUserPatternMatcher;
            Matcher giftRatePatternMatcher;
            Matcher giftCategoryPatternMatcher;
            Matcher giftAllPatternMatcher;
            Matcher giftReceivedPatternMatcher;
            Matcher giftSearchPatternMatcher;
            Content contentBean = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            short clientVersion = chatSource.getSessionI().getClientVersion();
            String parentUsername = chatSource.getParentUsername();
            ConnectionPrx connectionProxy = chatSource.getSessionI().getConnectionProxy();
            String messageText = messageData.messageText.trim().replace("\\s+", " ");
            if (this.giftHelpPattern.matcher(messageText).matches()) {
                return this.handleGiftHelpEmote(clientVersion, connectionProxy, messageData, chatSource);
            }
            Matcher giftListPatternMatcher = this.giftListPattern.matcher(messageText);
            if (giftListPatternMatcher.matches()) {
                return this.handleGiftListEmote(parentUsername, giftListPatternMatcher, contentBean, messageData, chatSource);
            }
            if (SystemProperty.getBool("GiftEmoteSearchEnabled", true) && (giftSearchPatternMatcher = this.giftSearchPattern.matcher(messageText)).matches()) {
                return this.handleGiftSearchEmote(parentUsername, giftSearchPatternMatcher, contentBean, messageData, chatSource);
            }
            if (SystemProperty.getBool("GiftEmoteGiftsEnabled", true) && (giftReceivedPatternMatcher = this.giftReceivedPattern.matcher(messageText)).matches()) {
                return this.handleGiftReceivedEmote(clientVersion, parentUsername, giftReceivedPatternMatcher, connectionProxy, userBean, contentBean, messageData, chatSource);
            }
            if (SystemProperty.getBool("GiftEmoteAllEnabled", true) && (giftAllPatternMatcher = this.giftAllPattern.matcher(messageText)).matches()) {
                return this.handleGiftAllEmote(parentUsername, giftAllPatternMatcher, purchaseLocation, userBean, contentBean, messageData, chatSource);
            }
            if (SystemProperty.getBool("GiftEmoteCategoryEnabled", true) && (giftCategoryPatternMatcher = this.giftCategoryPattern.matcher(messageText)).matches()) {
                return this.handleGiftCategoryEmote(parentUsername, giftCategoryPatternMatcher, contentBean, messageData, chatSource);
            }
            if (SystemProperty.getBool("GiftEmoteRateEnabled", true) && (giftRatePatternMatcher = this.giftRatePattern.matcher(messageText)).matches()) {
                return this.handleGiftRateEmote(giftRatePatternMatcher, contentBean, messageData, chatSource);
            }
            if (SystemProperty.getBool(SystemPropertyEntities.Emote.GIFT_INVENTORY_ENABLED)) {
                Matcher giftInventoryPatternMatcher = this.giftInventoryPattern.matcher(messageText);
                if (giftInventoryPatternMatcher.matches()) {
                    return this.handleGiftInventoryEmote(chatSource.getSessionI().getUserID(), giftInventoryPatternMatcher, contentBean, messageData, chatSource);
                }
                Matcher giftInventoryToUserPatternMatcher = this.giftInventoryToUserPattern.matcher(messageText);
                if (giftInventoryToUserPatternMatcher.matches()) {
                    return this.handleGiftInventoryToUserEmote(parentUsername, giftInventoryToUserPatternMatcher, contentBean, purchaseLocation, messageData, chatSource);
                }
                Matcher giftInventoryNoUserPatternMatcher = this.giftInventoryNoUserPattern.matcher(messageText);
                if (giftInventoryNoUserPatternMatcher.matches()) {
                    return this.handleUnlockedDefaultMessage(messageData, chatSource);
                }
            }
            if ((giftToUserPatternMatcher = this.giftToUserPattern.matcher(messageText)).matches()) {
                return this.handleGiftToUserEmote(parentUsername, giftToUserPatternMatcher, contentBean, purchaseLocation, messageData, chatSource);
            }
            messageData.messageText = "To buy a gift for another user type \"/gift <username> <gift name>\". Type \"/gift list\" to see available gifts.";
            this.emoteCommandData.updateMessageData(messageData);
            chatSource.sendMessageToSender(messageData);
            String defaultMessage = SystemProperty.get("GiftEmoteDefaultMessage", "");
            if (!defaultMessage.equals("")) {
                messageData.messageText = defaultMessage;
                this.emoteCommandData.updateMessageData(messageData);
                chatSource.sendMessageToSender(messageData);
            }
            return false;
        }
        catch (RemoteException re) {
            throw new Exception(RMIExceptionHelper.getRootMessage(re));
        }
    }

    private void createVirtualGiftInventoryListMessage(MessageData messageData, List<StoreItemInventorySummaryData> siisdList) {
        String msg = "";
        boolean doneOne = false;
        messageData.emoticonKeys = new LinkedList<String>();
        messageData.messageColour = 3965073;
        long totalGift = 0L;
        for (StoreItemInventorySummaryData siisd : siisdList) {
            VirtualGiftData gift = (VirtualGiftData)siisd.getStoreItemData().referenceData;
            totalGift += (long)siisd.getCount();
            if (doneOne) {
                msg = msg + "\n";
            }
            msg = msg + gift.getHotKey() + " " + gift.getName() + " (" + TWO_DECIMAL_POINT_FORMAT.format(gift.getRoundedPrice()) + " " + gift.getCurrency() + "):" + siisd.getCount();
            doneOne = true;
            messageData.emoticonKeys.add(gift.getHotKey());
        }
        messageData.messageText = "You have " + totalGift + " unlocked gift.\n" + msg;
    }

    private void createVirtualGiftListMessage(MessageData messageData, List<VirtualGiftData> gifts) {
        String msg = "";
        boolean doneOne = false;
        messageData.emoticonKeys = new LinkedList<String>();
        for (VirtualGiftData gift : gifts) {
            if (doneOne) {
                msg = msg + "; ";
            }
            msg = msg + gift.getName() + " " + gift.getHotKey() + " (" + TWO_DECIMAL_POINT_FORMAT.format(gift.getRoundedPrice()) + " " + gift.getCurrency() + ")";
            doneOne = true;
            messageData.emoticonKeys.add(gift.getHotKey());
        }
        messageData.messageText = msg;
    }

    private VirtualGiftData findVirtualGiftByName(String giftName, String userName, MessageData messageData) throws VirtualGiftNotFoundException, RemoteException, CreateException, FusionEJBException {
        Content contentBean = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
        VirtualGiftData gift = contentBean.getVirtualGift(null, giftName, userName);
        if (gift == null) {
            List gifts = SystemProperty.getBool("GiftEmoteGiftSearchEnabled", true) ? contentBean.searchVirtualGifts(userName, 0, giftName, 5, true) : new LinkedList();
            if (gifts.size() <= 0) {
                messageData.messageText = "Sorry, there is no gift matching [" + giftName + "]";
                throw new VirtualGiftNotFoundException("No gift matching", messageData);
            }
            if (gifts.size() == 1) {
                gift = (VirtualGiftData)gifts.get(0);
            } else {
                this.createVirtualGiftListMessage(messageData, gifts);
                messageData.messageText = "Sorry, there is no gift matching [" + giftName + "], " + "here are some suggestions: " + messageData.messageText;
                throw new VirtualGiftNotFoundException("Multiple gifts matching", messageData);
            }
        }
        return gift;
    }

    public static String formatUserNameWithLevel(String username) {
        try {
            int userReputationLevel = MemCacheOrEJB.getUserReputationLevel(username);
            return username + " [" + userReputationLevel + "]";
        }
        catch (Exception e) {
            return username;
        }
    }

    private class VirtualGiftNotFoundException
    extends Exception {
        private static final long serialVersionUID = 1L;
        private MessageData messageData;

        public VirtualGiftNotFoundException(String errMsg, MessageData messageData) {
            super(errMsg);
            this.messageData = messageData;
        }
    }
}

