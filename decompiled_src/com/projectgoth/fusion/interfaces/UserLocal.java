/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlParameter;
import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.GroupMemberData;
import com.projectgoth.fusion.data.MerchantDetailsData;
import com.projectgoth.fusion.data.RegistrationContextData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.SecurityQuestion;
import com.projectgoth.fusion.data.ThirdPartyApplicationData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserDataAndRegistrationContextData;
import com.projectgoth.fusion.data.UserEmailAddressData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.data.UserRegistrationContextData;
import com.projectgoth.fusion.data.UserReputationScoreAndLevelData;
import com.projectgoth.fusion.data.UserSettingData;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.invitation.CreateInvitationsResult;
import com.projectgoth.fusion.invitation.InvitationData;
import com.projectgoth.fusion.invitation.InvitationResponseData;
import com.projectgoth.fusion.invitation.restapi.data.InvitationDetailsData;
import com.projectgoth.fusion.invitation.restapi.data.SendingInvitationData;
import com.projectgoth.fusion.restapi.data.RegistrationTokenData;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.fusion.restapi.data.UserActivationData;
import com.projectgoth.fusion.restapi.data.UserMigboProfileData;
import com.projectgoth.fusion.restapi.data.UserVerificationData;
import com.projectgoth.fusion.restapi.enums.RegistrationType;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface UserLocal
extends EJBLocalObject {
    public void checkUsername(String var1) throws EJBException, NoSuchFieldException;

    public UserDataAndRegistrationContextData getUserDataFromUserRegistrationTable(String var1, UserData var2) throws EJBException;

    public UserActivationData getVerificationDataFromUserRegistrationTable(String var1) throws EJBException;

    public UserData createPrepaidCardUser(String var1, String var2, AccountEntrySourceData var3) throws EJBException;

    public Map getRecentMobileActivationCountsWithMobilePrefixByIPAddress(String var1, int var2) throws EJBException;

    public boolean checkRecentCommonPrefixActivations(Integer var1, String var2);

    public UserData createUser(UserData var1, UserProfileData var2, boolean var3, UserRegistrationContextData var4, AccountEntrySourceData var5) throws EJBException;

    public UserData createUser(UserData var1, UserProfileData var2, boolean var3, UserRegistrationContextData var4, AccountEntrySourceData var5, boolean var6, boolean var7, boolean var8) throws EJBException;

    public UserData postCreateUser(UserData var1, UserProfileData var2, boolean var3, AccountEntrySourceData var4, UserRegistrationContextData var5) throws EJBException;

    public UserData postCreateUser(UserData var1, UserProfileData var2, boolean var3, AccountEntrySourceData var4, UserRegistrationContextData var5, boolean var6) throws EJBException;

    public void sendVerificationToken(String var1, String var2, String var3, RegistrationType var4) throws EJBException;

    public boolean isUsernameAvailable(String var1);

    public boolean emailAddressExists(String var1) throws EJBException;

    public UserVerificationData getVerificationDataFromToken(String var1, boolean var2) throws EJBException;

    public UserVerificationData getVerificationDataFromToken(String var1) throws EJBException;

    public boolean validateRegistrationToken(UserVerificationData var1);

    public void insertPartnerUser(int var1, int var2) throws EJBException;

    public Set loadBroadcastList(String var1, Connection var2) throws SQLException;

    public Set checkAndLoadBroadcastList(String var1, Connection var2) throws SQLException, CreateException, EJBException, NoSuchFieldException;

    public List getUserSettings(String var1) throws SQLException;

    public void checkUserAliasByUserid(int var1, String var2, boolean var3, Connection var4) throws EJBException;

    public void setUserAliasByUserid(int var1, String var2) throws EJBException;

    public String getUserAliasByUsername(String var1, Connection var2) throws EJBException;

    public String getUsernameByAlias(String var1, Connection var2) throws EJBException;

    public int getUseridByAlias(String var1, Connection var2) throws EJBException;

    public SecurityQuestion getSecurityQuestion(int var1) throws EJBException;

    public String getUserAliasByUserid(int var1, Connection var2) throws EJBException;

    public String getUsernameByUserid(int var1, Connection var2) throws EJBException;

    public boolean isBounceEmailAddress(String var1) throws FusionEJBException;

    public Map getEmailUserIdMapping(Collection var1) throws FusionEJBException;

    public CreateInvitationsResult createInvitation(int var1, SendingInvitationData var2, Connection var3) throws EJBException, FusionEJBException;

    public CreateInvitationsResult createInvitationForFBInvite(int var1, SendingInvitationData var2, Connection var3);

    public InvitationData getInvitationData(int var1, boolean var2, Connection var3);

    public ThirdPartyApplicationData getThirdPartyApplicationData(int var1, Connection var2);

    public InvitationData getInvitationDataForFBInvite(String var1, String var2, boolean var3, Connection var4);

    public InvitationResponseData logInvitationResponse(Connection var1, Date var2, InvitationData var3, InvitationResponseData.ResponseType var4, String var5, InvitationData.StatusFieldValue var6);

    public int getUserID(String var1, Connection var2) throws EJBException;

    public int getUserID(String var1, Connection var2, boolean var3) throws EJBException;

    public Map getLastLoggedInUsersWithVerifiedEmail(UserEmailAddressData.UserEmailAddressTypeEnum var1, Timestamp var2, Timestamp var3, Connection var4) throws EJBException;

    public UserData loadUserByUsernameOrAlias(String var1, boolean var2, boolean var3) throws EJBException;

    public UserData loadUser(String var1, boolean var2, boolean var3) throws EJBException;

    public UserData loadUserFromID(int var1);

    public UserData loadUserFromMobilePhone(String var1) throws EJBException;

    public UserData loadUserFromVoucherNumber(String var1) throws EJBException;

    public void updateUserDetail(UserData var1) throws EJBException;

    public void updateOtherIMDetail(String var1, ImType var2, String var3, String var4) throws EJBException;

    public void updateStatusMessage(int var1, String var2, String var3, ClientType var4, SSOEnums.View var5) throws EJBException;

    public void updateDisplayPicture(String var1, String var2) throws EJBException;

    public void removeDisplayPicture(String var1) throws EJBException;

    public UserProfileData.StatusEnum getUserProfileStatus(String var1) throws EJBException;

    public UserProfileData getUserProfile(String var1, String var2, boolean var3) throws EJBException, FusionEJBException;

    public boolean updateUserProfile(UserProfileData var1) throws EJBException;

    public void activateAccount(String var1, String var2, boolean var3, AccountEntrySourceData var4) throws EJBException;

    public void inviteFriend(String var1, String var2, String var3, Integer var4, String var5, String var6, String var7, AccountEntrySourceData var8) throws EJBException, FusionEJBException;

    public void resendVerificationCode(String var1, String var2, AccountEntrySourceData var3) throws EJBException;

    public void sendEmailVerification(int var1, String var2) throws EJBException;

    public void changeMobilePhone(String var1, String var2, AccountEntrySourceData var3) throws EJBException;

    public void changeMobilePhone(String var1, String var2, boolean var3, AccountEntrySourceData var4) throws EJBException;

    public void postChangeMobilePhone(UserData var1, String var2, String var3, String var4, String var5, boolean var6, AccountEntrySourceData var7) throws EJBException;

    public void cancelChangeMobilePhoneRequest(String var1, AccountEntrySourceData var2) throws EJBException;

    public String forgotPasswordViaEmail(String var1, String var2) throws FusionEJBException;

    public String forgotPasswordViaSecurityQuestion(String var1, String var2, int var3) throws FusionEJBException, EJBException;

    public boolean changePassword(String var1, String var2, String var3, String var4) throws FusionEJBException;

    public String forgotPasswordViaSMS(String var1, AccountEntrySourceData var2) throws FusionEJBException;

    public boolean allowForgotPasswordViaSMS(String var1) throws FusionEJBException;

    public void forgotPasswordWithMobileNumberOrEmail(String var1, boolean var2, AccountEntrySourceData var3) throws EJBException;

    public AuthenticationServiceResponseCodeEnum validateUserCredential(String var1, String var2, PasswordType var3);

    public void changePassword(String var1, String var2, String var3) throws EJBException;

    public void loginSucceeded(String var1, String var2, String var3, String var4) throws EJBException;

    public void loginFailed(String var1) throws EJBException;

    public void activateAccountFailed(String var1) throws EJBException;

    public void setAccountStatus(String var1, boolean var2) throws EJBException;

    public boolean getAccountStatus(String var1) throws EJBException;

    public void setFailedActivationAttempts(String var1, int var2) throws EJBException;

    public void setEmailAlertSent(String var1, boolean var2) throws EJBException;

    public UserData createUserMerchant(UserData var1, UserProfileData var2, AccountEntrySourceData var3) throws EJBException;

    public boolean midletRegistrationDisabled(String var1, String var2, String var3) throws EJBException;

    public GroupMemberData getGroupMember(String var1, int var2) throws EJBException;

    public GroupData getGroup(int var1) throws EJBException;

    public boolean isUserMobileVerified(String var1) throws EJBException;

    public boolean isUserMobileOrEmailVerified(String var1) throws EJBException;

    public boolean isUserMobileOrEmailVerifiedWithTxSupported(String var1) throws EJBException;

    public boolean isUserEmailVerified(String var1) throws FusionEJBException;

    public boolean isUserEmailVerifiedWithTxSupport(String var1) throws FusionEJBException;

    public AuthenticatedAccessControlParameter getUserAuthenticatedAccessControlParameter(String var1) throws EJBException;

    public boolean userAttemptedVerification(String var1, int var2) throws EJBException;

    public void updateUserSetting(String var1, UserSettingData.TypeEnum var2, int var3) throws EJBException;

    public UserSettingData.AnonymousCallEnum getAnonymousCallSetting(String var1) throws EJBException;

    public void updateAnonymousCallSetting(String var1, UserSettingData.AnonymousCallEnum var2) throws EJBException;

    public UserSettingData.MessageEnum getMessageSetting(String var1) throws EJBException;

    public void updateMessageSetting(String var1, UserSettingData.MessageEnum var2) throws EJBException;

    public UserSettingData.EmailSettingEnum getEmailNotificationSetting(String var1, UserSettingData.TypeEnum var2) throws EJBException;

    public void updateEmailNotificationSetting(String var1, UserSettingData.TypeEnum var2, UserSettingData.EmailSettingEnum var3) throws EJBException;

    public ReputationLevelData getReputationLevelByUserid(int var1) throws EJBException;

    public ReputationLevelData getReputationLevelByUserid(int var1, boolean var2) throws EJBException;

    public ReputationLevelData getReputationLevel(String var1) throws EJBException;

    public ReputationLevelData getReputationLevel(String var1, boolean var2) throws EJBException;

    public void addReputationScore(int var1, int var2, Connection var3) throws EJBException;

    public void updateReputationScore(int var1, int var2, boolean var3) throws EJBException;

    public UserReputationScoreAndLevelData updateReputationScoreAndGet(int var1, int var2, boolean var3) throws EJBException;

    public void invalidateCacheAndNotifyReputationScoreUpdated(int var1);

    public UserReputationScoreAndLevelData getReputationScoreAndLevel(int var1, Connection var2);

    public UserReputationScoreAndLevelData getReputationScoreAndLevel(int var1, Connection var2, boolean var3) throws EJBException;

    public UserReputationScoreAndLevelData getReputationScoreAndLevel(boolean var1, int var2, boolean var3) throws EJBException;

    public Map getReputationScoreAndLevelForUsers(List var1, Connection var2) throws EJBException;

    public void createLookout(String var1, String var2) throws EJBException;

    public double getPriceInUserCurrency(double var1, String var3, String var4) throws FusionEJBException;

    public UserMigboProfileData.AdminEnum getAdminLabel(String var1) throws FusionEJBException;

    public UserMigboProfileData.MerchantEnum getMerchantLabel(String var1) throws FusionEJBException;

    public MerchantDetailsData getBasicMerchantDetails(String var1) throws FusionEJBException;

    public boolean isMerchantMentor(String var1) throws FusionEJBException;

    public double getUserReferralSuccessRate(String var1, boolean var2) throws FusionEJBException;

    public boolean isUserInMigboAccessList(int var1, int var2, int var3) throws FusionEJBException;

    public boolean isUserLevelAllowedMigboAccess(int var1, int var2) throws FusionEJBException;

    public boolean addUserEmailAddress(int var1, String var2, UserEmailAddressData.UserEmailAddressTypeEnum var3) throws FusionEJBException;

    public boolean removeUserEmailAddressByType(int var1, UserEmailAddressData.UserEmailAddressTypeEnum var2) throws FusionEJBException;

    public boolean removeUserEmailAddress(int var1, String var2) throws FusionEJBException;

    public boolean updateUserEmailAddress(int var1, String var2, String var3, UserEmailAddressData.UserEmailAddressTypeEnum var4) throws FusionEJBException;

    public RegistrationTokenData verifyExternalEmailAddress(Integer var1, String var2) throws FusionEJBException;

    public void saveVerifiedUserPrimaryEmailAddressOnDatabase(int var1, String var2);

    public boolean isEmailRegistrationNotVerifiedForUsername(String var1);

    public boolean isEmailRegistrationNotVerifiedForUsername(String var1, String var2);

    public boolean isExternalEmailVerified(int var1, String var2) throws FusionEJBException;

    public UserEmailAddressData getUserEmailAddressByType(int var1, UserEmailAddressData.UserEmailAddressTypeEnum var2) throws FusionEJBException;

    public String getUsernameByEmailAddress(String var1, UserEmailAddressData.UserEmailAddressTypeEnum var2) throws FusionEJBException;

    public void forgotUsernameViaEmailAddress(String var1) throws FusionEJBException;

    public String[] getUsersInUserCategory(int var1) throws EJBException;

    public Map getUserCategoryNames(int var1) throws FusionEJBException;

    public boolean blacklistUser(int var1);

    public boolean removeUserFromBlacklist(int var1);

    public boolean banUser(int var1);

    public boolean suspendUser(int var1, int var2);

    public boolean disconnectUser(String var1, String var2);

    public int getChatroomsOwnedCount(int var1);

    public int getGiftsReceivedCount(int var1);

    public int getGroupsJoinedCount(int var1);

    public int getPhotosUploadedCount(int var1);

    public RegistrationContextData getRegistrationContextData(int var1);

    public Map getTaggedUsers(int var1, int var2, int var3) throws FusionEJBException;

    public Map getExpiringTaggedUsers(int var1, int var2) throws FusionEJBException;

    public MerchantDetailsData getFullMerchantDetails(String var1) throws FusionEJBException;

    public boolean setMerchantColorType(Integer var1, MerchantDetailsData.UserNameColorTypeEnum var2) throws FusionEJBException;

    public void responseToAlert(int var1, String var2, int var3, InvitationResponseData.ResponseType var4) throws FusionEJBException;

    public void addMutualFollowing(int var1, int var2) throws FusionEJBException;

    public void addMutualFollowingAndTriggerMigAlerts(UserData var1, UserData var2) throws FusionEJBException;

    public void addMutualFollowingAndTriggerMigAlertsToAllInviters(InvitationData var1, UserData var2) throws FusionEJBException;

    public InvitationDetailsData getInvitationDetailsData(String var1, boolean var2, Date var3) throws FusionEJBException;

    public InvitationDetailsData getInvitationDetailsDataForFBInvite(String var1, String var2, boolean var3, Date var4) throws FusionEJBException;

    public InvitationDetailsData convertInvitationDataToInvitationDetailsData(InvitationData var1, boolean var2, Date var3) throws FusionEJBException;

    public String getDisplayPicture(String var1);

    public UserData getInviterForSignUp(int var1);
}

