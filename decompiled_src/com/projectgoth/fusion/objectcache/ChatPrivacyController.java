/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.UsernameUtils;
import com.projectgoth.fusion.common.UsernameValidationException;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.exception.UserNotOnlineException;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.objectcache.OfflineMessageHelper;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import com.projectgoth.fusion.restapi.enums.MigboAccessMemberTypeEnum;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class ChatPrivacyController {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatPrivacyController.class));
    private static String chatMessageBlacklistRegexString;
    private static Pattern chatMessageBlacklistRegexPattern;

    public static boolean checkOfflineMsgGuardset(int userID, String username, MessageData messageData, String destinationUsername, User userEJB, int recipientId) throws FusionException {
        try {
            boolean senderInGuardset = userEJB.isUserInMigboAccessList(userID, MigboAccessMemberTypeEnum.WHITELIST.value(), GuardCapabilityEnum.OFFLINE_MESSAGING.value());
            boolean recipientInGuardset = userEJB.isUserInMigboAccessList(recipientId, MigboAccessMemberTypeEnum.WHITELIST.value(), GuardCapabilityEnum.OFFLINE_MESSAGING.value());
            if (senderInGuardset && recipientInGuardset) {
                log.info((Object)("Can send offline messages: sender " + username + " and recipient " + destinationUsername + " both in offline messaging guardset"));
                return true;
            }
            log.info((Object)("Cannot send offline messages: " + username + " and/or " + destinationUsername + " not in offline messaging guardset"));
            throw new UserNotOnlineException(destinationUsername);
        }
        catch (FusionException e) {
            throw e;
        }
        catch (Exception e2) {
            log.error((Object)("Exception in checkOfflineMsgGuardset(): " + e2));
            throw new FusionException(e2.getMessage());
        }
    }

    public static boolean storeOfflineFusionMessageForIndividual(int userID, MessageData messageData, String destinationUsername, int recipientId) throws FusionException {
        try {
            OfflineMessageHelper.StorageResult result = OfflineMessageHelper.getInstance().scheduleOfflineMessageStorageAndWait(messageData, userID, recipientId);
            if (result.failed()) {
                throw new FusionException(result.getError());
            }
            return SystemProperty.getBool(SystemPropertyEntities.OfflineMessageSettings.MSG_STORED_CONF_ENABLED);
        }
        catch (FusionException e) {
            throw e;
        }
        catch (Exception e2) {
            log.error((Object)("Exception in storeOfflineFusionMessageForIndividual(): " + e2));
            throw new FusionException(e2.getMessage());
        }
    }

    public static boolean userCanContactOfflineRecipient(String sender, String recipient, User userEJB, Contact contactEJB) throws Exception {
        if (contactEJB.isBlocking(recipient, sender)) {
            return false;
        }
        boolean recipientMustBeFriend = !SystemProperty.getBool(SystemPropertyEntities.OfflineMessageSettings.SENDING_TO_NON_FRIENDS_ENABLED) || userEJB.getUserProfileStatus(recipient).equals((Object)UserProfileData.StatusEnum.CONTACTS_ONLY);
        return !recipientMustBeFriend || contactEJB.isFriend(sender, recipient);
    }

    public static void doOfflineMessagingRateLimiting(String senderUsername, String destinationUsername) throws FusionException {
        if (!SystemProperty.getBool(SystemPropertyEntities.OfflineMessageSettings.RATE_LIMITING_ENABLED)) {
            return;
        }
        int maxSendsPerSuspectedAbuserPerDay = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TX_DAILY_LIMIT_PER_SUSPECTED_ABUSER);
        if (!MemCachedRateLimiter.checkWithoutHit(MemCachedRateLimiter.NameSpace.OFFLINE_MESSAGE_SENDS_PER_SUSPECTED_ABUSER.toString(), senderUsername, (long)maxSendsPerSuspectedAbuserPerDay, 86400000L)) {
            throw new FusionException("You have reached your limit for offline messaging for today.");
        }
        int maxSendsPerUserPerDay = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TX_DAILY_LIMIT_PER_USER);
        int maxSendsPerUserRecipientPerDay = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TX_DAILY_LIMIT_PER_USER_PAIR);
        int maxReceivesPerUserPerDay = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.RX_DAILY_LIMIT_PER_USER);
        if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.OFFLINE_MESSAGE_SENDS_PER_USER.toString(), senderUsername, (long)maxSendsPerUserPerDay, 86400000L)) {
            throw new FusionException("You have reached your limit for offline messaging for today.");
        }
        if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.OFFLINE_MESSAGE_SENDS_PER_USER_PAIR.toString(), senderUsername + ":" + destinationUsername, (long)maxSendsPerUserRecipientPerDay, 86400000L)) {
            throw new FusionException("You have reached your limit for offline messaging for today.");
        }
        if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.OFFLINE_MESSAGE_RECEIVES_PER_USER.toString(), destinationUsername, (long)maxReceivesPerUserPerDay, 86400000L)) {
            throw new FusionException(destinationUsername + " is unable to receive more offline messages at the moment.");
        }
    }

    public static void mediaSharing(String localUsername, UserPrx destUserPrx, int contentType) throws FusionException {
        if (destUserPrx != null && SystemProperty.getBool("MediaSharingFriendsOnly", true) && contentType != MessageData.ContentTypeEnum.TEXT.value() && !destUserPrx.isOnContactList(localUsername)) {
            throw new FusionException("You can only share media with friends.");
        }
    }

    public static void mediaSharing2(UserPrx destUserPrx, String localUsername, String destinationUsername, MessageDataIce message, Contact contactEJB, User userEJB) throws FusionException {
        if (destUserPrx != null && destUserPrx.userCanContactMe((String)localUsername, (MessageDataIce)message).error) {
            throw new FusionException(String.format("%s is unable to receive messages at the moment.", destinationUsername));
        }
        if (destUserPrx == null && SystemProperty.getBool(SystemPropertyEntities.OfflineMessageSettings.SEND_ENABLED)) {
            try {
                if (contactEJB == null) {
                    contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
                }
                if (userEJB == null) {
                    userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                }
                if (!ChatPrivacyController.userCanContactOfflineRecipient(localUsername, destinationUsername, userEJB, contactEJB)) {
                    throw new FusionException(String.format("The privacy settings of %s do not allow offline messages from you.", destinationUsername));
                }
            }
            catch (FusionException e) {
                log.debug((Object)e);
                throw e;
            }
            catch (Exception e) {
                log.error((Object)e);
                throw new FusionException(String.format("%s is unable to receive messages at the moment.", destinationUsername));
            }
        }
    }

    public static boolean dropMessageNotOnContactList(UserData userData, boolean senderOnRecipientContactList) {
        if (!AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.SEND_PRIVATE_MESSAGE_TO_NON_CONTACT, userData) && !senderOnRecipientContactList) {
            log.info((Object)"Silently failing to send message because recipient not on sender's contact list and can't send message to non-contacts");
            return true;
        }
        return false;
    }

    public static boolean validUsername(String destinationUsername, String messageSource) {
        if (SystemProperty.getBool(SystemPropertyEntities.Default.VALIDATE_FUSION_MSG_RECIPIENT_USERNAME_CHARACTERS)) {
            try {
                UsernameUtils.validateUsernameCharacters(destinationUsername, false);
            }
            catch (UsernameValidationException uve) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("POSSIBLE ABUSE: Attempt by sender=" + messageSource + " to message username with invalid chars=" + destinationUsername + " (exception=" + uve + ")"));
                }
                return false;
            }
        }
        return true;
    }

    public static void onOfflineMessageToNonExistentRecipient(String sender, String recipient) {
        log.error((Object)("POSSIBLE ABUSE: Attempt by user " + sender + " to send offline message to non-existent user " + recipient));
        int maxSendsPerSuspectedAbuserPerDay = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TX_DAILY_LIMIT_PER_SUSPECTED_ABUSER);
        MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.OFFLINE_MESSAGE_SENDS_PER_SUSPECTED_ABUSER.toString(), sender, (long)maxSendsPerSuspectedAbuserPerDay, 86400000L);
    }

    public static void verifyCanCreateSession(String username, Boolean isChatRoomAdmin, int sessionCount) throws FusionException {
        int allowedConcurrentSessions = SystemProperty.getInt(isChatRoomAdmin != false ? SystemPropertyEntities.Default.MAX_CONCURRENT_SESSIONS_PER_ADMINUSER : SystemPropertyEntities.Default.MAX_CONCURRENT_SESSIONS_PER_USER);
        if (sessionCount >= allowedConcurrentSessions) {
            if (SystemProperty.getBool(SystemPropertyEntities.Default.MAX_CONCURRENT_SESSIONS_BREACHED_LOCKDOWN_ENABLED)) {
                MemCachedClientWrapper.set(MemCachedKeySpaces.RateLimitKeySpace.MAX_CONCURRENT_SESSIONS_BREACHED, username, "1", SystemProperty.getLong(SystemPropertyEntities.Default.MAX_CONCURRENT_SESSIONS_BREACHED_LOCKDOWN));
            }
            throw new FusionException("You cannot login at this time, please try again");
        }
    }

    public static void verifyCanSendBinaryData(UserData newUserData) throws FusionException {
        if (!AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.SEND_BINARY_DATA, newUserData)) {
            throw new FusionException("Please authenticate your migme account to send images. For more information, please email contact@mig.me");
        }
    }

    public static boolean scrubMessageText(String messageText, String password) throws FusionException {
        String messageLowerCase = messageText.toLowerCase();
        if (!SystemProperty.getBool(SystemPropertyEntities.Temp.PT73368964_ENABLED) && messageLowerCase.contains(password.toLowerCase())) {
            FusionException fe = new FusionException();
            try {
                MIS misBean = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
                fe.message = misBean.getInfoText(22);
            }
            catch (Exception e) {
                fe.message = "The message must not contain your password";
            }
            throw fe;
        }
        return !messageLowerCase.contains("document.cookie") && !messageLowerCase.contains("<script");
    }

    public static void cleanMessageText(MessageDataIce message) {
        message.messageText = message.messageText != null ? message.messageText.replaceAll("[\\p{InCombiningDiacriticalMarks}\\p{Me}]", "") : null;
    }

    public static boolean truncateMessageText(MessageDataIce message, int maxMessageSize) {
        if (message.messageText.length() > maxMessageSize) {
            message.messageText = message.messageText.substring(0, maxMessageSize + 1);
            return true;
        }
        return false;
    }

    public static boolean containsBlacklistedPatterns(String messageText) throws FusionException {
        Matcher m;
        if (messageText.startsWith("/")) {
            return false;
        }
        String trimmed_message = messageText.toLowerCase();
        trimmed_message = trimmed_message.replace(" ", "");
        String[] blacklistedPatternsSimple = SystemProperty.getArray("ChatMessageBlacklistSimple", new String[0]);
        for (int i = 0; i < blacklistedPatternsSimple.length; ++i) {
            if (!trimmed_message.contains(blacklistedPatternsSimple[i].trim())) continue;
            return true;
        }
        if (!SystemProperty.get("ChatMessageBlacklistRegex", "").equals(chatMessageBlacklistRegexString)) {
            chatMessageBlacklistRegexString = SystemProperty.get("ChatMessageBlacklistRegex", "");
            chatMessageBlacklistRegexPattern = !StringUtil.isBlank(chatMessageBlacklistRegexString) ? Pattern.compile(chatMessageBlacklistRegexString, 2) : null;
        }
        return chatMessageBlacklistRegexPattern != null && (m = chatMessageBlacklistRegexPattern.matcher(messageText)).matches();
    }
}

