/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.projectgoth.fusion.interfaces;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import javax.ejb.EJBObject;

public interface Web
extends EJBObject {
    public Vector getCountries() throws RemoteException;

    public Vector getCurrencies() throws RemoteException;

    public Vector getRateGrid() throws RemoteException;

    public Hashtable getHistoryEntry(long var1, String var3) throws RemoteException;

    public String setEmailAlert(String var1, boolean var2) throws RemoteException;

    public String setEmailAlertSent(String var1, boolean var2) throws RemoteException;

    public String sendSMS(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8) throws RemoteException;

    public String sendEmail(String var1, String var2, String var3, String var4, String var5) throws RemoteException;

    public String sendEmailFromNoReply(String var1, String var2, String var3) throws RemoteException;

    public String makeCall(String[] var1, String[] var2) throws RemoteException;

    public Hashtable evaluateCall(String[] var1, String[] var2) throws RemoteException;

    public Vector getContactList(String var1) throws RemoteException;

    public Vector getContactListEmailActivated(String var1, boolean var2) throws RemoteException;

    public Vector getAccountEntries(String var1, int var2, int var3) throws RemoteException;

    public Vector getMoneyTransferEntries(String var1, int var2, int var3) throws RemoteException;

    public Vector getSMSHistory(String var1, int var2, int var3) throws RemoteException;

    public Vector getCallHistory(String var1, int var2, int var3) throws RemoteException;

    public Hashtable registerUser(String[] var1, String[] var2, String[] var3, String[] var4, String var5, String var6, String var7, String var8) throws RemoteException;

    public Hashtable registerUser(String[] var1, String[] var2, String[] var3, String[] var4, String var5, String var6, String var7, String var8, String var9) throws RemoteException;

    public Hashtable registerUser(String[] var1, String[] var2, String[] var3, String[] var4, String var5, String var6, String var7, String var8, String var9, String var10) throws RemoteException;

    public Hashtable registerUserMerchant(String[] var1, String[] var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public Hashtable loadUserDetails(String var1) throws RemoteException;

    public Hashtable loadUserDetailsFromMobilePhone(String var1) throws RemoteException;

    public Hashtable loadUserProfile(String var1, String var2) throws RemoteException;

    public Hashtable getAccountBalance(String var1) throws RemoteException;

    public String getMerchantTagFromUsername(String var1) throws RemoteException;

    public Vector searchUserProfiles(String var1, String var2, String var3, String var4, String var5, String var6, String var7, int var8, int var9, String var10, String var11) throws RemoteException;

    public Vector getUsersWhoViewed(String var1) throws RemoteException;

    public String updateUser(String[] var1, String[] var2, String[] var3, String[] var4) throws RemoteException;

    public String updateUserDisplayPicture(String var1, String var2) throws RemoteException;

    public String updateUserDetails(String[] var1, String[] var2) throws RemoteException;

    public String updateUserProfile(String[] var1, String[] var2) throws RemoteException;

    public String updateUserStatusMessage(int var1, String var2, String var3) throws RemoteException;

    public String referFriendViaGame(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9) throws RemoteException;

    public String referFriend(String var1, String var2, String var3, String var4, String var5, String var6, String var7) throws RemoteException;

    public Hashtable transferCredit(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8) throws RemoteException;

    public String cleanAndValidatePhoneNumber(String var1) throws RemoteException;

    public Vector getScrapbook(String var1, int var2, int var3) throws RemoteException;

    public Vector getScrapbook(String var1, int var2, int var3, boolean var4) throws RemoteException;

    public Vector getGallery(String var1, String var2, int var3, int var4) throws RemoteException;

    public boolean reportPhotoAbuse(String var1, String var2, int var3) throws RemoteException;

    public Hashtable getWall(String var1, int var2, int var3, boolean var4) throws RemoteException;

    public Hashtable getPhoto(int var1, String var2, String var3) throws RemoteException;

    public Hashtable getScrapbookEntry(int var1) throws RemoteException;

    public String updateScrapbookEntry(String[] var1, String[] var2) throws RemoteException;

    public Hashtable getFile(String var1) throws RemoteException;

    public String newFileID() throws RemoteException;

    public String saveFileToScrapbook(String[] var1, String[] var2) throws RemoteException;

    public String saveExistingFileToScrapbooks(String var1, String[] var2, String var3, String var4) throws RemoteException;

    public String publishFileFromScrapbook(String var1, int var2, String var3, boolean var4) throws RemoteException;

    public String unpublishFileFromScrapbook(String var1, int var2) throws RemoteException;

    public String deleteFileFromScrapbook(String var1, int var2) throws RemoteException;

    public Hashtable getContact(int var1) throws RemoteException;

    public Hashtable getContact(String var1, String var2) throws RemoteException;

    public Hashtable addContact(int var1, String[] var2, String[] var3) throws RemoteException;

    public String updateContact(int var1, String[] var2, String[] var3) throws RemoteException;

    public String blockContact(int var1, String var2, String var3) throws RemoteException;

    public Hashtable getContactGroup(int var1) throws RemoteException;

    public Hashtable addGroup(int var1, String[] var2, String[] var3) throws RemoteException;

    public String updateGroup(int var1, String[] var2, String[] var3) throws RemoteException;

    public Hashtable creditCardPayment(String[] var1, String[] var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public String sendTTNotification(String[] var1, String[] var2) throws RemoteException;

    public Hashtable redeemVoucher(String var1, String var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public Hashtable redeemBlueLabelVoucher(String var1, String var2, String var3) throws RemoteException;

    public Hashtable getAffiliateOverview(String var1, String var2) throws RemoteException;

    public Vector getAffiliateRecentActivities(String var1, String var2) throws RemoteException;

    public Hashtable getAffiliateMoreStatistics(String var1, String var2) throws RemoteException;

    public Vector getVoucherBatches(String var1, int var2, int var3, int var4) throws RemoteException;

    public Vector getAllVoucherBatches(String var1) throws RemoteException;

    public Vector getVouchers(String var1, int var2, int var3, int var4, int var5, String var6, String var7) throws RemoteException;

    public String exportVoucherBatchToCSV(String var1, int var2, int var3) throws RemoteException;

    public String createVoucherBatch(String var1, String var2, String var3, int var4, String var5, boolean var6, String var7, String var8, String var9, String var10) throws RemoteException;

    public Hashtable searchForVoucher(String var1, String var2) throws RemoteException;

    public String cancelVoucher(String var1, int var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public String cancelVoucherBatch(String var1, int var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public String changeActiveVoucherToInactive(String var1, int var2) throws RemoteException;

    public String changeInactiveVoucherToActive(String var1, int var2) throws RemoteException;

    public String changeActiveVouchersInBatchToInactive(String var1, int var2) throws RemoteException;

    public String changeInactiveVouchersInBatchToActive(String var1, int var2) throws RemoteException;

    public String updateVoucherBatchNotes(String var1, int var2, String var3) throws RemoteException;

    public int[] creditCardPaymentFromMidlet(int[] var1, String var2, String var3, String var4, String var5) throws RemoteException;

    public boolean processSMSDeliveryReport(String var1, String var2, int var3, String var4, String var5, String var6, String var7) throws RemoteException;

    public String processMobileOrignatedSMS(String var1, String var2, String var3, boolean var4, String var5) throws RemoteException;

    public Vector getHandsetVendors() throws RemoteException;

    public Vector getHandsetVendorPrefixes() throws RemoteException;

    public Vector getHandsetDetails(String var1) throws RemoteException;

    public Vector getDefaultHandsetDetails() throws RemoteException;

    public String forgotPasswordWithMobileNumber(String var1, String var2, String var3, String var4) throws RemoteException;

    public String forgotPasswordWithEmail(String var1, String var2, String var3, String var4) throws RemoteException;

    public String changePassword(String var1, String var2, String var3) throws RemoteException;

    public Hashtable getCountryFromIPNumber(String var1) throws RemoteException;

    public boolean consoleOut(String var1) throws RemoteException;

    public String loginFailed(String var1) throws RemoteException;

    public String loginSucceeded(String var1) throws RemoteException;

    public String loginSucceeded(String var1, String var2, String var3, String var4) throws RemoteException;

    public String getBankTransferProductID(int var1) throws RemoteException;

    public Hashtable bankTransfer(String var1, int var2, int var3, String var4, String var5, float var6, String var7) throws RemoteException;

    public Hashtable bankTransfer(String var1, int var2, int var3, String var4, String var5, String var6, String var7, float var8, String var9) throws RemoteException;

    public String registerMerchant(String[] var1, String[] var2) throws RemoteException;

    public Hashtable loadMerchant(String var1) throws RemoteException;

    public String isBuzzPossible(String var1, int var2) throws RemoteException;

    public String sendBuzz(String var1, int var2, String var3, String var4, String var5, String var6, String var7) throws RemoteException;

    public String createLookout(String var1, String var2) throws RemoteException;

    public String isLookoutPossible(String var1, String var2) throws RemoteException;

    public int lookoutExists(String var1, String var2) throws RemoteException;

    public String removeLookout(String var1, String var2) throws RemoteException;

    public Vector getLookouts(String var1) throws RemoteException;

    public String setAllowBuzz(String var1, boolean var2) throws RemoteException;

    public Hashtable getApplicableDiscountTier(int var1, String var2, double var3) throws RemoteException;

    public Vector getDiscountTiers(int var1, String var2) throws RemoteException;

    public double[] getCreditCardPaymentAmounts(String var1) throws RemoteException;

    public double[] getCreditCardPaymentAmounts(String var1, boolean var2, String var3) throws RemoteException;

    public Vector getPossibleTransferRecipients(String var1) throws RemoteException;

    public Vector getResellerStates(int var1) throws RemoteException;

    public Vector getResellersInState(int var1, String var2) throws RemoteException;

    public Vector getResellers(int var1) throws RemoteException;

    public Vector getFixedCallRates(int var1) throws RemoteException;

    public Vector getEmoticonPacks(String var1, boolean var2, int var3, int var4) throws RemoteException;

    public Vector getEmoticonsInPack(int var1) throws RemoteException;

    public Vector getEmoticonDetailsFromHotkeys(String var1) throws RemoteException;

    public Hashtable getEmoticonPack(String var1, int var2) throws RemoteException;

    public String buyEmoticonPack(String var1, int var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public Vector getAllEmoticons() throws RemoteException;

    public Vector getTopWallpaper(int var1) throws RemoteException;

    public Vector getTopRingtones(int var1) throws RemoteException;

    public Vector getMobileContentCategories(String var1, int var2, int var3, int var4, int var5) throws RemoteException;

    public Vector getMobileContent(String var1, int var2, int var3, int var4, int var5) throws RemoteException;

    public Hashtable getMobileContentItem(String var1, int var2, boolean var3) throws RemoteException;

    public String buyMobileContentItem(String var1, int var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public String getMobileContentDownloadURL(String var1, int var2) throws RemoteException;

    public boolean processILoopAPICall(String var1, String var2, String var3, String var4, String var5, String var6, String var7) throws RemoteException;

    public boolean processOplayoAPICall(int var1, int var2, int var3, int var4, int var5) throws RemoteException;

    public Vector getPurchasedContent(String var1, int var2, int var3) throws RemoteException;

    public Hashtable getSmallProfile(String var1, String var2) throws RemoteException;

    public Hashtable getFriends(String var1, String var2, String var3, int var4, int var5) throws RemoteException;

    public Hashtable getCallRates(int var1, int var2, boolean var3, String var4) throws RemoteException;

    public int getUserProfilePrivacySetting(String var1) throws RemoteException;

    public boolean setUserProfilePrivacySetting(String var1, int var2) throws RemoteException;

    public int getUserBlockedListCount(String var1) throws RemoteException;

    public Hashtable getUserBlockedList(String var1, String var2, int var3, int var4, boolean var5, boolean var6) throws RemoteException;

    public boolean isContact(String var1, String var2) throws RemoteException;

    public boolean isContactFriend(String var1, String var2) throws RemoteException;

    public Hashtable getUserContactDetails(String var1, String var2) throws RemoteException;

    public boolean unblockUser(String var1, String var2) throws RemoteException;

    public boolean unblockAllUsers(String var1) throws RemoteException;

    public Hashtable getPagingUserEventGeneratedByUser(String var1, int var2, int var3) throws RemoteException;

    public Hashtable getPagingUserEvents(String var1, int var2, int var3) throws RemoteException;

    public boolean editPhotoCaption(String var1, int var2, String var3) throws RemoteException;

    public Hashtable getThemes(String var1, int var2, int var3) throws RemoteException;

    public boolean changeTheme(String var1, int var2) throws RemoteException;

    public Vector getLocationsWithMerchantsInCountry(int var1) throws RemoteException;

    public Vector getLocationsWithMerchantsInParentLocation(int var1) throws RemoteException;

    public Vector getCountriesWithMerchants() throws RemoteException;

    public Vector getLocationPath(int var1) throws RemoteException;

    public Vector getMerchantsInLocation(int var1) throws RemoteException;

    public Hashtable getMerchantLocation(int var1) throws RemoteException;

    public boolean countryHasMerchantLocations(int var1) throws RemoteException;

    public String[] getMerchantsUserMayPurchaseFrom(String var1) throws RemoteException;

    public Hashtable getBuzzCost(String var1, String var2) throws RemoteException;

    public String getSMSCost(String var1, String var2) throws RemoteException;

    public Hashtable getSMSCostTable(String var1, int var2, int var3) throws RemoteException;

    public Hashtable getLocalSMSCost(String var1, int var2) throws RemoteException;

    public Hashtable getLookoutCost(String var1) throws RemoteException;

    public Hashtable getGroupSMSNotificationCost(String var1) throws RemoteException;

    public Hashtable getKickCost(String var1) throws RemoteException;

    public Hashtable getEmailAlertCost(String var1) throws RemoteException;

    public Hashtable getTransactionSummaryByMonth(String var1, int var2, int var3, String var4) throws RemoteException;

    public Hashtable getTransactionSummaryForCustomerByDate(String var1, String var2, Date var3, Date var4, String var5) throws RemoteException;

    public Hashtable getTransactionSummaryByMonthForCustomer(String var1, String var2, int var3, int var4, String var5) throws RemoteException;

    public Hashtable getTransactionSummaryForCustomer(String var1, String var2, String var3) throws RemoteException;

    public Hashtable getTransactions(String var1, int var2, int var3, String var4) throws RemoteException;

    public Hashtable getTransactionsSinceDate(String var1, int var2, int var3, int var4, String var5) throws RemoteException;

    public Hashtable getTransactionsSinceDateAndType(String var1, int var2, int var3, int var4, int var5) throws RemoteException;

    public Hashtable getTransactionsByDate(String var1, Date var2, Date var3, int var4, int var5, String var6) throws RemoteException;

    public int getUserReferralCountByMonth(String var1, int var2, int var3) throws RemoteException;

    public int getUserReferralCount(String var1) throws RemoteException;

    public Hashtable getUserReferral(String var1, Date var2, Date var3, int var4, int var5) throws RemoteException;

    public Hashtable getUserReferralByMonth(String var1, int var2, int var3, int var4, int var5) throws RemoteException;

    public Hashtable getUserReferral(String var1, int var2, int var3) throws RemoteException;

    public String getPreviousMobileNumber(String var1) throws RemoteException;

    public String changeMobileNumber(String var1, String var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public String cancelMobileNumberChange(String var1, String var2, String var3, String var4, String var5) throws RemoteException;

    public Hashtable getUserVirtualGift(String var1, String var2, int var3) throws RemoteException;

    public boolean removeUserVirtualGift(String var1, int var2) throws RemoteException;

    public Hashtable getVirtualGiftForUser(String var1, String var2, int var3, int var4) throws RemoteException;

    public Hashtable getVirtualGiftReceivedSummary(String var1, String var2) throws RemoteException;

    public Hashtable getVirtualGift(String var1, int var2) throws RemoteException;

    public Hashtable getVirtualGiftContent(String var1, int var2, int var3, int var4) throws RemoteException;

    public String buyVirtualGift(String var1, String var2, int var3, int var4, boolean var5, String var6, String var7, String var8, String var9, String var10, String var11) throws RemoteException;

    public String buyAvatarItem(int var1, int var2, int var3, String var4, String var5, String var6, String var7) throws RemoteException;

    public Vector getExternalClientDownloadDetail(String var1) throws RemoteException;

    public float getUnredeemedVoucherValue(String var1) throws RemoteException;

    public Hashtable getMerchantCustomersAndFriends(String var1, int var2, int var3) throws RemoteException;

    public Hashtable getMerchantCustomers(String var1, int var2, int var3) throws RemoteException;

    public Hashtable getMerchantCustomerTransactions(String var1, String var2, int var3, int var4) throws RemoteException;

    public String getMostRecentlyVisitedChatroom(String var1, String var2) throws RemoteException;

    public Hashtable getGroup(int var1) throws RemoteException;

    public Hashtable getGroupAnnouncements(int var1, int var2, int var3) throws RemoteException;

    public Hashtable getGroupAnnouncement(int var1) throws RemoteException;

    public Hashtable createGroupAnnouncement(String var1, int var2, String var3, String var4, String var5) throws RemoteException;

    public Hashtable updateGroupAnnouncement(int var1, String var2, int var3, String var4, String var5) throws RemoteException;

    public Hashtable getGroupChatroomCategoriesAndStadiums(int var1, int var2, int var3, int var4) throws RemoteException;

    public Hashtable getGroupChatrooms(int var1, int var2, int var3, int var4) throws RemoteException;

    public Hashtable getGroupDonators(int var1, int var2, int var3) throws RemoteException;

    public String joinGroup(String var1, int var2, int var3, String var4, String var5, String var6, String var7, boolean var8, boolean var9, boolean var10, boolean var11, boolean var12, boolean var13) throws RemoteException;

    public String leaveGroup(String var1, int var2) throws RemoteException;

    public String declineAllGroupInvitations(String var1) throws RemoteException;

    public String declineGroupInvitation(String var1, int var2) throws RemoteException;

    public String setGroupMemberOptions(String var1, int var2, int var3, boolean var4, boolean var5, boolean var6, boolean var7, boolean var8, boolean var9) throws RemoteException;

    public Hashtable makeGroupDonation(String var1, int var2, String var3, boolean var4) throws RemoteException;

    public String inviteMobilePhoneToGroup(String var1, String var2, String var3, int var4, String var5, String var6, String var7, String var8, String var9) throws RemoteException;

    public String inviteUserToGroup(String var1, String var2, int var3) throws RemoteException;

    public String updateStadiumDescription(String var1, String var2) throws RemoteException;

    public Hashtable getLocations(int var1, int var2, int var3, int var4) throws RemoteException;

    public Hashtable getLocation(int var1) throws RemoteException;

    public Hashtable getContactList(String var1, int var2, int var3, int var4) throws RemoteException;

    public Vector getGroupModules(int var1) throws RemoteException;

    public Hashtable getGroupEvents(int var1, int var2, int var3) throws RemoteException;

    public Hashtable getGroupModule(int var1) throws RemoteException;

    public Hashtable getGroupPost(int var1) throws RemoteException;

    public Hashtable getGroupPosts(int var1, int var2, int var3) throws RemoteException;

    public Hashtable createGroupPost(String var1, int var2, String var3, String var4) throws RemoteException;

    public Hashtable createGroupPost(String var1, int var2, String var3, String var4, int var5) throws RemoteException;

    public String updateGroupPost(int var1, String var2, String var3, String var4) throws RemoteException;

    public String publishGroupPost(int var1, String var2) throws RemoteException;

    public String deleteGroupPost(int var1, String var2) throws RemoteException;

    public Hashtable createGroupModule(String var1, int var2, String var3, int var4) throws RemoteException;

    public String updateGroupModule(int var1, String var2, String var3, int var4) throws RemoteException;

    public String deleteGroupModule(int var1, String var2) throws RemoteException;

    public Hashtable createGroupUserPost(String var1, int var2, String var3, int var4) throws RemoteException;

    public Hashtable getGroupUserPosts(int var1, int var2, int var3) throws RemoteException;

    public Hashtable getUserPost(int var1) throws RemoteException;

    public Hashtable getUserPostReplies(int var1, int var2, int var3) throws RemoteException;

    public String deleteGroupUserPost(int var1, int var2, String var3) throws RemoteException;

    public String createChatroom(String var1, String var2, String var3, String var4, String var5, boolean var6) throws RemoteException;

    public String updateRoomDetails(String var1, String var2, String var3, String var4) throws RemoteException;

    public String updateRoomKickingRule(String var1, String var2, boolean var3) throws RemoteException;

    public String addRoomModerator(String var1, String var2, String var3) throws RemoteException;

    public String removeRoomModerator(String var1, String var2, String var3) throws RemoteException;

    public String banUserFromRoom(String var1, String var2, String var3) throws RemoteException;

    public String unbanUserFromRoom(String var1, String var2, String var3) throws RemoteException;

    public String sendChangeRoomOwnerEmail(String var1, String var2, String var3) throws RemoteException;

    public String changeRoomOwner(String var1, String var2, String var3) throws RemoteException;

    public Vector getRoomModerators(String var1, String var2) throws RemoteException;

    public Hashtable getRoomBannedUsers(String var1, String var2, int var3, int var4) throws RemoteException;

    public String updateRoomKeywords(String var1, String var2, String var3, int var4) throws RemoteException;

    public Hashtable getChatroom(String var1) throws RemoteException;

    public Hashtable getUserOwnedChatrooms(String var1, int var2, int var3) throws RemoteException;

    public boolean isModeratorOfChatRoom(String var1, String var2) throws RemoteException;

    public Hashtable searchChatroom(int var1, String var2, String var3, boolean var4, boolean var5, int var6, int var7) throws RemoteException;

    public Hashtable getPendingContact(String var1, int var2) throws RemoteException;

    public int getPendingContactCount(String var1) throws RemoteException;

    public String acceptPendingContact(int var1, String var2, String var3, int var4, boolean var5) throws RemoteException;

    public Vector getContactGroupList(String var1) throws RemoteException;

    public String rejectContactInvitation(int var1, String var2, String var3) throws RemoteException;

    public int getAnonymousCallSetting(String var1) throws RemoteException;

    public boolean updateAnonymousCallSetting(String var1, int var2) throws RemoteException;

    public int getMessageSetting(String var1) throws RemoteException;

    public boolean updateMessageSetting(String var1, int var2) throws RemoteException;

    public String addIMDetail(String var1, int var2, String var3, String var4) throws RemoteException;

    public Hashtable getIMContacts(String var1, int var2, int var3) throws RemoteException;

    public String inviteIMContact(String var1, int var2, String var3) throws RemoteException;

    public boolean isIndosatIP(String var1) throws RemoteException;

    public Vector getActiveSubscriptions(String var1) throws RemoteException;

    public String cancelSubscription(String var1, int var2) throws RemoteException;

    public Hashtable getGroupHasExclusiveContent(int var1) throws RemoteException;

    public Vector getLanguages() throws RemoteException;

    public Hashtable getUserLevel(String var1) throws RemoteException;

    public Hashtable getBotList(int var1, int var2) throws RemoteException;

    public Hashtable chargeUserForGameItem(String var1, String var2, String var3, double var4, String var6, String var7, String var8, String var9, String var10) throws RemoteException;

    public Hashtable giveGameReward(String var1, String var2, String var3, double var4, double var6, String var8, String var9, String var10, String var11, String var12) throws RemoteException;

    public Hashtable thirdPartyAPIDebit(String var1, String var2, String var3, double var4, String var6, String var7, String var8, String var9, String var10) throws RemoteException;

    public String activateAccount(String var1, String var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public boolean updateEmoticonPackStatus(String var1, int var2, int var3) throws RemoteException;

    public String approveCreditCardPayment(String var1, int var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public String rejectCreditCardPayment(String var1, int var2, String var3, String var4, String var5, String var6, String var7) throws RemoteException;

    public String creditUserAndSendSMS(String var1, double var2, double var4, String var6, String var7, String var8, String var9, String var10) throws RemoteException;

    public Vector getCreditCardTransactions(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, int var9) throws Exception, RemoteException;

    public String disconnectUserIce(String var1, String var2) throws RemoteException;

    public String updateUserDetailsIce(String var1) throws RemoteException;

    public String deregisterChatroomIce(String var1) throws RemoteException;

    public String resetMerchantPin(String var1) throws RemoteException;

    public boolean resendVerificationCode(String var1, String var2, String var3, String var4, String var5, String var6) throws RemoteException;

    public String blacklistUsersFromGroup(int var1, String var2, String[] var3) throws RemoteException;

    public String removeUsersFromGroupBlacklist(int var1, String var2, String[] var3) throws RemoteException;

    public String sendApplicationEvent(String var1, String var2, String var3) throws RemoteException;

    public Hashtable getPaintWarsStats(String var1) throws RemoteException;

    public String getPaintWarsUserIdenticonIndex(String var1) throws RemoteException;

    public String executePaintWarsPaint(String var1, String var2) throws RemoteException;

    public String executePaintWarsClean(String var1, String var2) throws RemoteException;

    public String isPaintWarsClean(String var1) throws RemoteException;

    public String hadPaintWarsInteraction(String var1, String var2) throws RemoteException;

    public String hasPaintWarsFreePaintCredits(String var1) throws RemoteException;

    public String hasPaintWarsFreeCleanCredits(String var1) throws RemoteException;

    public String getPaintWarsPriceOfPaint() throws RemoteException;

    public String getPaintWarsPriceOfClean() throws RemoteException;

    public String getPaintWarsPriceOfIdenticon() throws RemoteException;

    public Vector getPaintWarsUserPaint(String var1) throws RemoteException;

    public String buyPaintWarsIdenticon(String var1) throws RemoteException;

    public Vector getPaintWarsStatsDetails(String var1, int var2, int var3, int var4) throws RemoteException;

    public Vector getPaintWarsSpecialItems() throws RemoteException;

    public Vector getPaintWarsUserInventory(String var1) throws RemoteException;

    public String buyPaintWarsSpecialItem(String var1, int var2) throws RemoteException;

    public String usePaintWarsSpecialItem(String var1, int var2) throws RemoteException;

    public String isPaintWarsPaintProof(String var1) throws RemoteException;

    public String hasPaintWarsDualPaint(String var1) throws RemoteException;

    public String hasPaintWarsStealthPaint(String var1) throws RemoteException;

    public String giveUnfundedCredits(String var1, String var2, String var3, double var4, String var6, String var7, String var8) throws RemoteException;
}

