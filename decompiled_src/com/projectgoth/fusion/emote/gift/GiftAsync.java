/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote.gift;

import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountBalanceData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.ChatRoomEmoteLogData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.StoreItemData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.data.VirtualGiftReceivedData;
import com.projectgoth.fusion.datagrid.DataGrid;
import com.projectgoth.fusion.datagrid.DataGridFactory;
import com.projectgoth.fusion.datagrid.LocalFauxDataGrid;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.Gift;
import com.projectgoth.fusion.emote.gift.GiftAllBiller;
import com.projectgoth.fusion.emote.gift.GiftAllBillingMessageData;
import com.projectgoth.fusion.emote.gift.GiftAllBuyer;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.NoLongerInGroupChatExceptionIce;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GiftAsync {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GiftAsync.class));
    public static final String EST_BALANCE_MSG = "and your estimated remaining balance after gifting will be";
    private static final String GIFT_ALL_KEY_STUB = "GiftAllLock:";

    private static final String makeGiftAllKey(String buyerUsername) {
        return GIFT_ALL_KEY_STUB + buyerUsername;
    }

    public static boolean giftAll(ChatRoomData chatroomData, String giftMessage, int maxMessageLength, boolean lowPriceGift, VirtualGiftReceivedData.PurchaseLocationEnum purchaseLocation, List<String> allRecipients, VirtualGiftData gift, MessageData messageData, EmoteCommandData emoteCommandData, ChatSource chatSource, IcePrxFinder icePrxFinder) throws FusionException {
        String buyerUsername = messageData.username;
        Lock buyerLock = null;
        try {
            buyerLock = GiftAsync.getGiftGrid().getLock(GiftAsync.makeGiftAllKey(buyerUsername));
            if (buyerLock == null) {
                String err = "Unable to obtain distributed gift all lock for buyer=" + buyerUsername;
                log.error((Object)err);
                throw new FusionException(err);
            }
            boolean gotIt = buyerLock.tryLock();
            if (!gotIt) {
                String err = "You are already sending a gift shower: please wait a little while";
                log.warn((Object)("For buyer=" + buyerUsername + ": " + "You are already sending a gift shower: please wait a little while"));
                throw new FusionException("You are already sending a gift shower: please wait a little while");
            }
            Content contentBean = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            AccountEntrySourceData accountEntrySourceData = chatSource.getSessionI() != null ? new AccountEntrySourceData(chatSource.getSessionI()) : (chatSource.getSessionPrx() != null ? new AccountEntrySourceData(chatSource.getSessionPrx()) : new AccountEntrySourceData(Gift.class));
            Account accountBean = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
            AccountBalanceData initialBalanceData = accountBean.getAccountBalance(buyerUsername);
            if (initialBalanceData.fundedBalance < 0.0) {
                throw new FusionException("gift all prohibited for buyer=" + buyerUsername + " as fundedBalance=" + initialBalanceData.fundedBalance + " is negative");
            }
            Future<Boolean> billerFuture = GiftAsync.buyVirtualGiftForMultipleUsers(buyerUsername, allRecipients, gift, purchaseLocation.value(), false, giftMessage, false, true, accountEntrySourceData, buyerLock, icePrxFinder);
            try {
                contentBean.incrementStoreItemSoldByNumber(StoreItemData.TypeEnum.VIRTUAL_GIFT, gift.getId(), allRecipients.size(), null);
            }
            catch (RemoteException e) {
                log.error((Object)"Failed to increment storeitem sold by numver for VG purchase: ", (Throwable)e);
            }
            catch (SQLException e) {
                log.error((Object)"Failed to increment storeitem sold by numver for VG purchase: ", (Throwable)e);
            }
            try {
                GiftAsync.sendGiftShowerMessageToAllUsersInChat(giftMessage, maxMessageLength, lowPriceGift, gift, messageData, emoteCommandData, chatSource, allRecipients);
                GiftAllBillingMessageData billingMsg = new GiftAllBillingMessageData(gift, messageData, allRecipients, initialBalanceData);
                chatSource.sendMessageToSender(billingMsg);
            }
            catch (NoLongerInGroupChatExceptionIce e) {
                log.warn((Object)("Buyer=" + buyerUsername + " no longer in group chat after gifting all [typical bot behaviour]. Billing and delivery will continue"));
            }
            catch (Exception e) {
                log.error((Object)("Exception in GiftAsync.giftAll for buyer=" + buyerUsername + ". Billing and delivery will continue. e=" + e), (Throwable)e);
            }
            try {
                GiftAsync.logToEmoteLog(chatroomData, gift, messageData, allRecipients);
            }
            catch (Exception e) {
                log.error((Object)("Exception logging gift all to emote log. Billing and delivery will continue. e=" + e), (Throwable)e);
            }
            boolean result = billerFuture.get(SystemProperty.getInt(SystemPropertyEntities.GiftSettings.FUTURE_TIMEOUT_SECONDS), TimeUnit.SECONDS);
            if (!result) {
                log.error((Object)("Billing failed for giftall for sender=" + buyerUsername));
            }
        }
        catch (Exception e) {
            log.error((Object)("Failed to execute gift all for sender=" + buyerUsername + " with message source=" + messageData.source + " e=" + e), (Throwable)e);
            throw new FusionException("Sorry, /giftall is not available right now. Please try again later.");
        }
        finally {
            if (buyerLock != null) {
                try {
                    buyerLock.unlock();
                }
                catch (Exception e) {
                    log.warn((Object)("Exception unlocking distributed giftall lock for buyer=" + buyerUsername + " ex=" + e));
                }
            }
            if (buyerLock != null) {
                try {
                    GiftAsync.getGiftGrid().destroyLock(buyerLock);
                }
                catch (Exception e) {
                    log.warn((Object)("Exception destroying distributed giftall lock for buyer=" + buyerUsername + " ex=" + e));
                }
            }
        }
        return false;
    }

    private static Future<Boolean> buyVirtualGiftForMultipleUsers(String buyerUsername, List<String> recipientUsernames, VirtualGiftData gift, int purchaseLocation, boolean privateGift, String messageParam, boolean broadcastChatroomMessage, boolean isGiftAll, AccountEntrySourceData accountEntrySourceData, Lock buyerLock, IcePrxFinder icePrxFinder) throws FusionException {
        String message = StringUtil.stripHTML(messageParam);
        if (recipientUsernames == null || recipientUsernames.isEmpty()) {
            throw new FusionException("Please provide one or more recipient for this gift");
        }
        if (gift == null) {
            throw new FusionException("Unknown gift");
        }
        if (gift.getStatus() == VirtualGiftData.StatusEnum.INACTIVE) {
            throw new FusionException("The " + gift.getName() + " gift is no longer available.");
        }
        if (gift.getNumAvailable() != null && gift.getNumAvailable() > 1 && gift.getNumSold() + recipientUsernames.size() >= gift.getNumAvailable()) {
            throw new FusionException("The " + gift.getName() + " gift is sold out or not enough for all users!");
        }
        try {
            Account accountBean;
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            if (!AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.BUY_VIRTUALGIFT, userBean.getUserAuthenticatedAccessControlParameter(buyerUsername)) && SystemProperty.getBool("StoreItemPurchaseDisabledForUnauthenticatedUsers", false)) {
                throw new FusionException("You must be authenticated before you can purchase a virtual gift.");
            }
            if (gift.getPrice() == 0.0) {
                if (recipientUsernames.size() == 1) {
                    try {
                        Content contentBean = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
                        contentBean.enforceFreeGiftToReferrerRules(buyerUsername, recipientUsernames.get(0));
                    }
                    catch (RemoteException re) {
                        String err = "Unable to enforce free gift to referrer rules: " + re;
                        log.error((Object)err, (Throwable)re);
                        throw new FusionException(err);
                    }
                } else {
                    throw new FusionException("You can only purchase free gifts to your referrer");
                }
            }
            if (!(accountBean = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class)).userCanAffordCost(buyerUsername, gift.getRoundedPrice() * (double)recipientUsernames.size(), gift.getCurrency(), null)) {
                log.warn((Object)(buyerUsername + " doesn't have enough credit to gift shower [" + gift.getName() + "] priced [" + gift.getPrice() + " " + gift.getCurrency() + " X " + recipientUsernames.size() + "]"));
                throw new FusionException("You do not have enough credit to purchase the gift");
            }
            boolean isGiftShower = recipientUsernames.size() != 1;
            List<String> giftShowerEventRecips = null;
            if (isGiftShower) {
                giftShowerEventRecips = GiftAsync.getGiftShowerEventRecipients(recipientUsernames);
            }
            String resultsMapGUID = UUID.randomUUID().toString();
            GiftAsync.getGiftGrid().configMap(resultsMapGUID, SystemProperty.getInt(SystemPropertyEntities.GiftSettings.DISTRIBUTED_RESULTS_MAP_TTL_SECONDS));
            for (String recipient : recipientUsernames) {
                boolean sendGiftShowerEvent = giftShowerEventRecips != null && giftShowerEventRecips.contains(recipient);
                UserData recipientUserData = userBean.loadUser(recipient, false, false);
                if (recipientUserData == null) continue;
                int totalGiftShowerRecipients = recipientUsernames.size();
                GiftAllBuyer buyer = new GiftAllBuyer(buyerUsername, recipient, recipientUserData, gift, purchaseLocation, privateGift, message, accountEntrySourceData, isGiftShower, sendGiftShowerEvent, totalGiftShowerRecipients, icePrxFinder, resultsMapGUID);
                ExecutorService executorService = GiftAsync.getGiftGrid().getDefaultExecutorService();
                Future<Integer> ignored = executorService.submit(buyer);
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("Queued GiftAllBuyer for recipient=" + recipient));
            }
            ExecutorService executorService = GiftAsync.getGiftGrid().getDefaultExecutorService();
            GiftAllBiller biller = new GiftAllBiller(buyerUsername, gift, recipientUsernames.size(), resultsMapGUID, accountEntrySourceData);
            return executorService.submit(biller);
        }
        catch (RemoteException e) {
            log.error((Object)("Exception in buyVirtualGiftForMultipleUsers: e=" + e), (Throwable)e);
            throw new FusionException(e.getMessage());
        }
        catch (Exception e) {
            log.error((Object)("Exception in buyVirtualGiftForMultipleUsers: e=" + e), (Throwable)e);
            throw new FusionException(e.getMessage());
        }
    }

    private static List<String> getGiftShowerEventRecipients(List<String> allRecipients) {
        int totalRecipients = allRecipients.size();
        ArrayList<String> candidates = new ArrayList<String>();
        candidates.addAll(allRecipients);
        ArrayList<String> eventRecipients = new ArrayList<String>();
        int giftShowerEvents = Math.min(totalRecipients, SystemProperty.getInt(SystemPropertyEntities.GiftSettings.GIFT_SHOWER_EVENTS_TO_SEND));
        int index = 0;
        Random rand = new Random();
        for (int i = 0; i < giftShowerEvents; ++i) {
            index = rand.nextInt(candidates.size());
            String recip = (String)candidates.get(index);
            eventRecipients.add(recip);
            candidates.remove(recip);
        }
        return eventRecipients;
    }

    private static void sendGiftShowerMessageToAllUsersInChat(String giftMessage, int maxMessageLength, boolean lowPriceGift, VirtualGiftData gift, MessageData messageData, EmoteCommandData emoteCommandData, ChatSource chatSource, List<String> allRecipients) throws FusionException {
        messageData.contentType = MessageData.ContentTypeEnum.EMOTE;
        String giftAllMessage = gift.getGiftAllMessage();
        if (giftAllMessage == null || giftAllMessage == "") {
            giftAllMessage = "GIFT SHOWER";
        }
        String returnMsg = "(shower) *" + giftAllMessage + "* " + Gift.formatUserNameWithLevel(messageData.username) + " gives ";
        returnMsg = StringUtil.startsWithaVowel(gift.getName()) ? returnMsg + "an " : returnMsg + "a ";
        returnMsg = returnMsg + gift.getName() + " " + gift.getHotKey() + " to %s! Hurray!";
        if (giftMessage != null) {
            returnMsg = returnMsg + " -- " + giftMessage;
        }
        returnMsg = String.format(returnMsg, StringUtil.implodeUserList(allRecipients, 5));
        if (maxMessageLength != -1 && returnMsg.length() > maxMessageLength - 6) {
            returnMsg = returnMsg.substring(0, maxMessageLength - 6 - 3) + "...";
        }
        messageData.messageText = "<< " + returnMsg + " >>";
        messageData.emoticonKeys = new LinkedList<String>();
        messageData.emoticonKeys.add("(shower)");
        messageData.emoticonKeys.add(gift.getHotKey());
        messageData.messageColour = lowPriceGift ? 0 : 0xFF00FF;
        emoteCommandData.updateMessageData(messageData, false);
        messageData.setMimeTypeAndData(gift, messageData.username, allRecipients, VirtualGiftData.GiftingType.GIFT_SHOWER, giftMessage);
        chatSource.sendMessageToAllUsersInChat(messageData);
    }

    private static void logToEmoteLog(ChatRoomData chatroomData, VirtualGiftData gift, MessageData messageData, List<String> allRecipients) throws FusionException {
        ChatRoomEmoteLogData logData = null;
        try {
            logData = new ChatRoomEmoteLogData(messageData.source, "", "/gift all", chatroomData != null && chatroomData.id != null ? chatroomData.id : -1, chatroomData != null && chatroomData.groupID != null ? chatroomData.groupID : -1, -1, gift.getId() + ";" + allRecipients.size());
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            messageEJB.addChatRoomEmoteLog(logData);
        }
        catch (Exception e) {
            log.error((Object)("Failed to log gift all emote action: " + logData.toString()), (Throwable)e);
        }
    }

    public static Future<Boolean> queueWorker(Callable<Boolean> worker) {
        ExecutorService executorService = GiftAsync.getGiftGrid().getDefaultExecutorService();
        Future<Boolean> future = executorService.submit(worker);
        return future;
    }

    public static DataGrid getGiftGrid() {
        if (SystemProperty.getBool(SystemPropertyEntities.GiftSettings.TRUE_GRID_ENABLED)) {
            return DataGridFactory.getInstance().getGrid();
        }
        return GiftAsyncDataGrid.getInstance();
    }

    public static class GiftAsyncDataGrid
    extends LocalFauxDataGrid {
        private static volatile boolean initialized = false;

        protected int getMapExpirySeconds() {
            return SystemProperty.getInt(SystemPropertyEntities.GiftSettings.DISTRIBUTED_RESULTS_MAP_TTL_SECONDS);
        }

        protected int getMapConcurrencyLevel() {
            return SystemProperty.getInt(SystemPropertyEntities.GiftSettings.DISTRIBUTED_RESULTS_MAP_CONCURRENCY_LEVEL);
        }

        public static GiftAsyncDataGrid getInstance() {
            GiftAsyncDataGrid instance = SingletonHolder.INSTANCE;
            initialized = true;
            return instance;
        }

        public static boolean isInitialized() {
            return initialized;
        }

        private static class SingletonHolder {
            public static final GiftAsyncDataGrid INSTANCE = new GiftAsyncDataGrid();

            private SingletonHolder() {
            }
        }
    }
}

