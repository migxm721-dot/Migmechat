/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.Provider
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.SecurityQuestion;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserEmailAddressData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.data.UserSettingData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.eventqueue.EventQueue;
import com.projectgoth.fusion.eventqueue.events.UserDataUpdatedEvent;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.restapi.data.BooleanData;
import com.projectgoth.fusion.restapi.data.ChangePasswordData;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.EmailInqueryData;
import com.projectgoth.fusion.restapi.data.ForgotPasswordData;
import com.projectgoth.fusion.restapi.data.ForgotUsernameData;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.RegistrationTokenData;
import com.projectgoth.fusion.restapi.data.SettingsAccountCommunicationData;
import com.projectgoth.fusion.restapi.data.SettingsAccountPictureData;
import com.projectgoth.fusion.restapi.data.SettingsEnums;
import com.projectgoth.fusion.restapi.data.SettingsProfileDetailsData;
import com.projectgoth.fusion.restapi.data.SettingsUserAliasData;
import com.projectgoth.fusion.restapi.data.UserActivationData;
import com.projectgoth.fusion.restapi.data.UserEmailData;
import com.projectgoth.fusion.restapi.data.UserSettingRestData;
import com.projectgoth.fusion.restapi.util.MigboApiUtil;
import com.projectgoth.fusion.restapi.util.RedisDataUtil;
import com.projectgoth.fusion.thirdpartysites.ThirdPartySiteCredentialManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/settings")
public class SettingsResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SettingsResource.class));

    @GET
    @Path(value="/security_questions")
    @Produces(value={"application/json"})
    public DataHolder<List<SecurityQuestion>> getSecurityQuestions() throws FusionRestException {
        try {
            List<SecurityQuestion> questions = SecurityQuestion.getAllQuestions();
            return new DataHolder<List<SecurityQuestion>>(questions);
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to retrieve security questions due to EJB CreateException: %s", e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving security questions");
        }
    }

    @GET
    @Path(value="/{userid}/account/profile")
    @Produces(value={"application/json"})
    public DataHolder<SettingsProfileDetailsData> getProfileDetails(@PathParam(value="userid") String useridStr) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        try {
            Enum privacyNew;
            String username;
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = null;
            if (userid != -1) {
                userData = userEJB.loadUserFromID(userid);
            } else {
                username = useridStr;
                userData = userEJB.loadUser(username, false, false);
                if (userData == null) {
                    throw new FusionRestException(102, String.format("Unable to find user '%s'", useridStr));
                }
                userid = userData.userID;
            }
            if (userData == null) {
                throw new FusionRestException(102, String.format("Unable to find user '%s'", useridStr));
            }
            username = userData.username;
            UserProfileData upData = null;
            try {
                upData = userEJB.getUserProfile(null, username, false);
            }
            catch (EJBException e) {
                log.error((Object)String.format("Failed to retrieve user profile '%s' due to EJBException in UserBean.getUserProfile: %s", useridStr, e.getMessage()), (Throwable)e);
                throw new FusionRestException(101, "Internal error while retrieving user data");
            }
            catch (FusionEJBException e) {
                log.error((Object)String.format("Failed to retrieve user profile '%s' due to EJBFusionException in UserBean.getUserProfile: %s", useridStr, e.getMessage()), (Throwable)e);
                throw new FusionRestException(101, "Internal error while retrieving user data");
            }
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            CountryData countryData = misBean.getCountry(userData.countryID);
            Map<String, Integer> privacy = RedisDataUtil.getAccountProfilePrivacy(userData.userID);
            HashMap<String, Integer> privacyToWrite = new HashMap<String, Integer>();
            SettingsEnums.EveryoneOrFollowerAndFriend feed = SettingsResource.getFeedContentPrivacyWithMigration(userid, username, userEJB);
            if (!privacy.containsKey("DobPrivacy")) {
                SettingsEnums.Birthday dobNew = SettingsResource.getMigratedBirthdayPrivacy(userid, username, feed);
                privacy.put("DobPrivacy", dobNew.value());
                privacyToWrite.put("DobPrivacy", dobNew.value());
            }
            if (!privacy.containsKey("FLNamePv")) {
                privacyNew = SettingsProfileDetailsData.PRIVACY_DEFAULT_FIRSTLASTNAME;
                privacy.put("FLNamePv", ((SettingsEnums.ShowHide)privacyNew).value());
                privacyToWrite.put("FLNamePv", ((SettingsEnums.ShowHide)privacyNew).value());
            }
            if (!privacy.containsKey("ExtEmPv")) {
                privacyNew = SettingsProfileDetailsData.PRIVACY_DEFAULT_EXTERNALEMAIL;
                privacy.put("ExtEmPv", ((SettingsEnums.EveryoneFollowerFriendHide)privacyNew).value());
                privacyToWrite.put("ExtEmPv", ((SettingsEnums.EveryoneFollowerFriendHide)privacyNew).value());
            }
            if (privacyToWrite.size() > 0 && !RedisDataUtil.setAccountPrivacy(userid, privacyToWrite)) {
                log.error((Object)String.format("Failed to save user account communication privacy '%s'", username));
            }
            return new DataHolder<SettingsProfileDetailsData>(new SettingsProfileDetailsData(userData, upData, countryData, privacy));
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to retrieve user profile '%s' due to EJB CreateException: %s", useridStr, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user data");
        }
    }

    @POST
    @Path(value="/{username}/account/profile")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setProfileDetails(@PathParam(value="username") String username, DataHolder<SettingsProfileDetailsData> dataHolder) throws FusionRestException {
        block16: {
            SettingsProfileDetailsData data = (SettingsProfileDetailsData)dataHolder.data;
            if (StringUtil.isBlank(username) || !username.equalsIgnoreCase((String)data.username.value)) {
                log.error((Object)String.format("Failed to save user profile: username '%s' in REST path does not match username '%s' in data", username, data.username.value));
                throw new FusionRestException(-1, "Incorrect username or profile data specified");
            }
            try {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                UserProfileData upData = null;
                upData = new UserProfileData();
                upData.username = username;
                data.updateUserProfileData(upData);
                try {
                    boolean doInAsync;
                    boolean updatedUserEmailAddress;
                    userEJB.updateUserProfile(upData);
                    int userId = userEJB.getUserID(username, null);
                    UserData userData = userEJB.loadUser(username, false, false);
                    UserEmailAddressData emailAddress = userEJB.getUserEmailAddressByType(userData.userID, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY);
                    boolean sendEmailVerification = emailAddress == null ? data.externalEmail.value != null && userEJB.addUserEmailAddress(userId, (String)data.externalEmail.value, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY) : (data.externalEmail.value != null && !((String)data.externalEmail.value).equals(emailAddress.emailAddress) ? (updatedUserEmailAddress = userEJB.updateUserEmailAddress(userId, emailAddress.emailAddress, (String)data.externalEmail.value, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY)) && SystemProperty.getBool(SystemPropertyEntities.Email.SEND_EMAIL_VERIFICATION_AFTER_EMAIL_ADDRESS_UPDATE) : false);
                    if (sendEmailVerification) {
                        try {
                            userEJB.sendEmailVerification(userId, (String)data.externalEmail.value);
                        }
                        catch (Exception ex) {
                            log.error((Object)("Error sending email verification to userId[" + userId + "] email[" + (String)data.externalEmail.value + "]." + ex), (Throwable)ex);
                        }
                    }
                    Map<String, Integer> oldPrivacy = RedisDataUtil.getAccountProfilePrivacy(userId);
                    Map<String, Integer> newPrivacy = RedisDataUtil.removeUnchangedPrivacy(data.retrievePrivacy(), oldPrivacy);
                    if (newPrivacy.size() <= 0) break block16;
                    if (!RedisDataUtil.setAccountPrivacy(userId, newPrivacy)) {
                        log.error((Object)String.format("Failed to save user profile privacy '%s'", username));
                    }
                    boolean bl = doInAsync = !SystemProperty.getBool(SystemPropertyEntities.UserProfileSettings.DO_SYNCHRONOUS_PROFILE_CACHE_INVALIDATION_ON_MIGBO_UPON_PROFILE_SETTING_UPDATE);
                    if (!doInAsync) {
                        try {
                            MigboApiUtil api = MigboApiUtil.getInstance();
                            JSONObject obj = api.delete(String.format("/user/%d/cache/profile", userId));
                            log.debug((Object)String.format("Received JSON Response from migbo-datsvc : %s ", obj.toString()));
                        }
                        catch (Exception e) {
                            boolean rollback = SystemProperty.getBool(SystemPropertyEntities.UserProfileSettings.FAIL_PROFILE_SETTING_UPDATE_IF_FAIL_DO_SYNCHRONOUS_PROFILE_CACHE_INVALIDATION_ON_MIGBO);
                            log.error((Object)String.format("Exception caught while invalidating profile cache on migbo for user [%d %s], %s", userId, username, rollback ? "rolling back" : "doing it in async"), (Throwable)e);
                            if (rollback) {
                                Map<String, Integer> oldUpdatedPrivacy = RedisDataUtil.keepChangedPrivacy(oldPrivacy, newPrivacy);
                                if (!RedisDataUtil.setAccountPrivacy(userId, oldUpdatedPrivacy)) {
                                    log.error((Object)String.format("Failed to rollback user profile privacy changes '%s'", username));
                                }
                                throw new FusionRestException(101, "Internal error while saving user data");
                            }
                            doInAsync = true;
                        }
                    }
                    if (doInAsync) {
                        EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.PROFILE));
                    }
                }
                catch (EJBException e) {
                    log.error((Object)String.format("Failed to save user profile '%s' due to EJBException: %s", username, e.getMessage()));
                    throw new FusionRestException(101, "Error: " + e.getMessage());
                }
                catch (FusionEJBException e) {
                    log.error((Object)String.format("Failed to save user profile '%s' due to EJBException: %s", username, e.getMessage()));
                    throw new FusionRestException(101, "Error: " + e.getMessage());
                }
            }
            catch (CreateException e) {
                log.error((Object)String.format("Failed to save user profile '%s' due to EJB CreateException: %s", username, e.getMessage()));
                throw new FusionRestException(101, "Internal error while saving user data");
            }
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @GET
    @Path(value="/{username}/account/communication")
    @Produces(value={"application/json"})
    public DataHolder<SettingsAccountCommunicationData> getAccountCommunication(@PathParam(value="username") String username) throws FusionRestException {
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            int userId = -1;
            try {
                userId = userEJB.getUserID(username, null);
            }
            catch (EJBException e) {
                log.error((Object)String.format("Failed to retrieve account communication privacy '%s' due to EJBException in getting userid: %s", username, e.getMessage()));
                throw new FusionRestException(101, "Internal error while retrieving communication settings");
            }
            Map<String, Integer> privacy = RedisDataUtil.getAccountCommunicationPrivacy(userId);
            HashMap<String, Integer> privacyToWrite = new HashMap<String, Integer>();
            SettingsEnums.EveryoneOrFollowerAndFriend feed = null;
            if (!privacy.containsKey("FeedPv")) {
                feed = SettingsResource.getOldFeedContentPrivacy(userId, username, userEJB);
                privacy.put("FeedPv", feed.value());
                privacyToWrite.put("FeedPv", feed.value());
            } else {
                feed = SettingsEnums.EveryoneOrFollowerAndFriend.fromValue(privacy.get("FeedPv"));
            }
            if (!privacy.containsKey("ChatPv")) {
                SettingsEnums.EveryoneFollowerFriend chatNew = SettingsResource.getMigratedChatPrivacy(userId, username, userEJB, feed);
                privacy.put("ChatPv", chatNew.value());
                privacyToWrite.put("ChatPv", chatNew.value());
            }
            if (!privacy.containsKey("BuzzPv")) {
                SettingsEnums.OnOff buzzNew = SettingsResource.getMigratedBuzzPrivacy(userId, username, userEJB);
                privacy.put("BuzzPv", buzzNew.value());
                privacyToWrite.put("BuzzPv", buzzNew.value());
            }
            if (privacyToWrite.size() > 0 && !RedisDataUtil.setAccountPrivacy(userId, privacyToWrite)) {
                log.error((Object)String.format("Failed to save user account communication privacy '%s'", username));
            }
            return new DataHolder<SettingsAccountCommunicationData>(new SettingsAccountCommunicationData(privacy));
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to retrieve account communication privacy '%s' due to EJB CreateException: %s", username, e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving communication settings");
        }
    }

    public static SettingsEnums.EveryoneFollowerFriend getMigratedChatPrivacy(int userid, String username, UserLocal userEJB, SettingsEnums.EveryoneOrFollowerAndFriend feed) throws FusionRestException {
        log.info((Object)String.format("migrating chat privacy '%d'", userid));
        SettingsEnums.EveryoneFollowerFriend chatNew = SettingsAccountCommunicationData.PRIVACY_DEFAULT_CHAT;
        try {
            UserSettingData.MessageEnum chat = userEJB.getMessageSetting(username);
            if (chat == UserSettingData.MessageEnum.FRIENDS_ONLY) {
                chatNew = SettingsEnums.EveryoneFollowerFriend.FRIEND_ONLY;
            } else if (chat == UserSettingData.MessageEnum.EVERYONE) {
                chatNew = SettingsEnums.EveryoneFollowerFriend.EVERYONE;
            }
        }
        catch (EJBException e) {
            log.error((Object)String.format("Failed to retrieve user privacy '%d' due to EJBException in getting chat setting: %s", userid, e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving communication settings");
        }
        return chatNew;
    }

    public static SettingsEnums.Birthday getMigratedBirthdayPrivacy(int userid, String username, SettingsEnums.EveryoneOrFollowerAndFriend feed) {
        log.info((Object)String.format("migrating dob privacy from DB '%s'", username));
        SettingsEnums.Birthday dobNew = SettingsProfileDetailsData.PRIVACY_DEFAULT_BIRTHDAY;
        if (feed == SettingsEnums.EveryoneOrFollowerAndFriend.EVERYONE) {
            dobNew = SettingsEnums.Birthday.SHOW_FULL;
        } else if (feed == SettingsEnums.EveryoneOrFollowerAndFriend.FRIEND_OR_FOLLOWER) {
            dobNew = SettingsEnums.Birthday.HIDE;
        }
        return dobNew;
    }

    public static SettingsEnums.OnOff getMigratedBuzzPrivacy(int userid, String username, UserLocal userEJB) throws FusionRestException {
        log.info((Object)String.format("getting buzz privacy from DB '%s'", username));
        SettingsEnums.OnOff buzzNew = SettingsAccountCommunicationData.PRIVACY_DEFAULT_BUZZ;
        try {
            if (username == null) {
                username = userEJB.getUsernameByUserid(userid, null);
            }
            UserData userData = userEJB.loadUser(username, false, false);
            if (userData.allowBuzz != null) {
                buzzNew = userData.allowBuzz != false ? SettingsEnums.OnOff.ON : SettingsEnums.OnOff.OFF;
            }
        }
        catch (EJBException e) {
            log.error((Object)String.format("Failed to retrieve account communication privacy '%s' due to EJBException in getting buzz setting: %s", username, e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving communication settings");
        }
        return buzzNew;
    }

    public static SettingsEnums.EveryoneOrFollowerAndFriend getFeedContentPrivacyWithMigration(int userid, String username, UserLocal userEJB) throws FusionRestException {
        Map<String, Integer> privacy = RedisDataUtil.getAccountCommunicationPrivacy(userid);
        SettingsEnums.EveryoneOrFollowerAndFriend feed = null;
        if (!privacy.containsKey("FeedPv")) {
            log.info((Object)String.format("migrating feed privacy from DB '%s'", username));
            feed = SettingsResource.getOldFeedContentPrivacy(userid, username, userEJB);
            HashMap<String, Integer> privacyToWrite = new HashMap<String, Integer>();
            privacyToWrite.put("FeedPv", feed.value());
            if (!RedisDataUtil.setAccountPrivacy(userid, privacyToWrite)) {
                log.error((Object)String.format("Failed to save user account communication privacy '%s'", username));
            }
        } else {
            feed = SettingsEnums.EveryoneOrFollowerAndFriend.fromValue(privacy.get("FeedPv"));
        }
        return feed;
    }

    public static SettingsEnums.EveryoneOrFollowerAndFriend getOldFeedContentPrivacy(int userid, String username, UserLocal userEJB) throws FusionRestException {
        log.info((Object)String.format("getting feed privacy from DB '%s'", username));
        SettingsEnums.EveryoneOrFollowerAndFriend feedNew = SettingsAccountCommunicationData.PRIVACY_DEFAULT_FEED;
        try {
            UserProfileData upData;
            if (username == null) {
                username = userEJB.getUsernameByUserid(userid, null);
            }
            if ((upData = userEJB.getUserProfile(null, username, false)) == null) {
                return feedNew;
            }
            if (upData.status != null) {
                feedNew = upData.status == UserProfileData.StatusEnum.PUBLIC ? SettingsEnums.EveryoneOrFollowerAndFriend.EVERYONE : SettingsEnums.EveryoneOrFollowerAndFriend.FRIEND_OR_FOLLOWER;
            }
        }
        catch (EJBException e) {
            log.error((Object)String.format("Failed to retrieve account communication privacy '%s' due to EJBException in getting feed setting: %s", username, e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving communication settings");
        }
        catch (FusionEJBException e) {
            log.error((Object)String.format("Failed to retrieve account communication privacy '%s' due to EJBException in getting feed setting: %s", username, e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving communication settings");
        }
        return feedNew;
    }

    @POST
    @Path(value="/{username}/account/communication")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setAccountCommunication(@PathParam(value="username") String username, DataHolder<SettingsAccountCommunicationData> dataHolder) throws FusionRestException {
        block12: {
            SettingsAccountCommunicationData data = (SettingsAccountCommunicationData)dataHolder.data;
            if (StringUtil.isBlank(username)) {
                log.error((Object)String.format("Failed to save user account communication privacy: no username is specified", new Object[0]));
                throw new FusionRestException(-1, "Incorrect username specified");
            }
            try {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                try {
                    boolean doInAsync;
                    int userId = userEJB.getUserID(username, null);
                    Map<String, Integer> oldPrivacy = RedisDataUtil.getAccountCommunicationPrivacy(userId);
                    Map<String, Integer> newPrivacy = RedisDataUtil.removeUnchangedPrivacy(data.retrievePrivacy(), oldPrivacy);
                    if (newPrivacy.size() <= 0) break block12;
                    this.syncChatPrivacyToFusionDB(data, username, userEJB);
                    if (!RedisDataUtil.setAccountPrivacy(userId, newPrivacy)) {
                        log.error((Object)String.format("Failed to save user account communication privacy '%s'", username));
                    }
                    if (!newPrivacy.containsKey("ChatPv") && !newPrivacy.containsKey("FeedPv")) break block12;
                    boolean bl = doInAsync = !SystemProperty.getBool(SystemPropertyEntities.UserProfileSettings.DO_SYNCHRONOUS_PRIVACY_CACHE_INVALIDATION_ON_MIGBO_UPON_COMM_SETTING_UPDATE);
                    if (!doInAsync) {
                        try {
                            MigboApiUtil api = MigboApiUtil.getInstance();
                            JSONObject obj = api.delete(String.format("/user/%d/cache/privacy", userId));
                            log.debug((Object)String.format("Received JSON Response from migbo-datsvc : %s ", obj.toString()));
                        }
                        catch (Exception e) {
                            boolean rollback = SystemProperty.getBool(SystemPropertyEntities.UserProfileSettings.FAIL_COMM_SETTING_UPDATE_IF_FAIL_DO_SYNCHRONOUS_PRIVACY_CACHE_INVALIDATION_ON_MIGBO);
                            log.error((Object)String.format("Exception caught while invalidating privacy cache on migbo for user [%d %s], %s", userId, username, rollback ? "rolling back" : "doing it in async"), (Throwable)e);
                            if (rollback) {
                                Map<String, Integer> oldUpdatedPrivacy = RedisDataUtil.keepChangedPrivacy(oldPrivacy, newPrivacy);
                                if (!RedisDataUtil.setAccountPrivacy(userId, oldUpdatedPrivacy)) {
                                    log.error((Object)String.format("Failed to rollback user account communication privacy changes '%s'", username));
                                }
                                throw new FusionRestException(101, "Internal error while saving account communication privacy");
                            }
                            doInAsync = true;
                        }
                    }
                    if (doInAsync) {
                        EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.PROFILE));
                    }
                }
                catch (EJBException e) {
                    log.error((Object)String.format("Failed to save user account communication privacy '%s' due to EJBException: %s", username, e.getMessage()));
                    throw new FusionRestException(101, "Internal error while saving account communication privacy");
                }
            }
            catch (CreateException e) {
                log.error((Object)String.format("Failed to save user account communication privacy '%s' due to EJB CreateException: %s", username, e.getMessage()));
                throw new FusionRestException(101, "Internal error while saving account communication privacy");
            }
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    public void syncChatPrivacyToFusionDB(SettingsAccountCommunicationData data, String username, UserLocal userEJB) throws FusionRestException {
        UserSettingData.MessageEnum chatSetting;
        Map<String, Integer> privacyOptions = data.retrievePrivacy();
        Integer chatPrivacyOption = privacyOptions.get("ChatPv");
        if (chatPrivacyOption == null) {
            log.error((Object)("Invalid chat privacy for user = " + username));
            throw new FusionRestException(-1, "Incorrect chat privacy option specified");
        }
        if (chatPrivacyOption.intValue() == SettingsEnums.EveryoneFollowerFriend.FOLLOWER_ONLY.value()) {
            chatPrivacyOption = SettingsEnums.EveryoneFollowerFriend.FRIEND_ONLY.value();
        }
        if ((chatSetting = UserSettingData.MessageEnum.fromValue(chatPrivacyOption)) == null) {
            log.error((Object)("Invalid chat setting for user = " + username));
            throw new FusionRestException(-1, "Incorrect chat setting specified");
        }
        userEJB.updateMessageSetting(username, chatSetting);
    }

    @GET
    @Path(value="/{userid}/account/picture")
    @Produces(value={"application/json"})
    public DataHolder<SettingsAccountPictureData> getProfilePicture(@PathParam(value="userid") String useridStr) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = null;
            if (userid != -1) {
                userData = userEJB.loadUserFromID(userid);
            } else {
                String username = useridStr;
                userData = userEJB.loadUser(username, false, false);
                userid = userData.userID;
            }
            if (userData == null) {
                throw new FusionRestException(102, String.format("Unable to find user '%s'", useridStr));
            }
            return new DataHolder<SettingsAccountPictureData>(new SettingsAccountPictureData(RedisDataUtil.getUserDisplayPictureSetting(userid)));
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to retrieve user display picture setting '%s' due to EJB CreateException: %s", useridStr, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user data");
        }
    }

    @POST
    @Path(value="/{userid}/account/picture")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setProfilePicture(@PathParam(value="userid") String useridStr, DataHolder<SettingsAccountPictureData> dataHolder) throws FusionRestException {
        SettingsAccountPictureData data = (SettingsAccountPictureData)dataHolder.data;
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Failed to save user account display picture setting: no user id is specified", new Object[0]));
            throw new FusionRestException(-1, "Incorrect user id specified");
        }
        if (!RedisDataUtil.setUserDisplayPictureSetting(userid, data.retrieveDisplayPictureSetting())) {
            log.error((Object)String.format("Failed to save user account display picture setting '%s'", useridStr));
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/{userid}/email/{emailtype}/notification/")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setEmailNotification(@PathParam(value="userid") String useridStr, @PathParam(value="emailtype") String emailTypeStr, DataHolder<BooleanData> enabled) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        int emailTypeInt = StringUtil.toIntOrDefault(emailTypeStr, -1);
        UserSettingData.TypeEnum emailType = UserSettingData.TypeEnum.fromValue(emailTypeInt);
        if (userid == -1) {
            log.error((Object)String.format("Failed to save user email notification setting: no user id is specified", new Object[0]));
            throw new FusionRestException(-1, "Incorrect user id specified");
        }
        if (emailType == null) {
            log.error((Object)String.format("Failed to save user email notification setting: invalid email type", new Object[0]));
            throw new FusionRestException(-1, "Incorrect email type specified");
        }
        if (((BooleanData)enabled.data).value == null) {
            log.error((Object)String.format("Failed to save user email notification setting: boolean value is null", new Object[0]));
            throw new FusionRestException(-1, "Incorrect boolean value specified");
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userEJB.getUsernameByUserid(userid, null);
            userEJB.updateEmailNotificationSetting(username, emailType, ((BooleanData)enabled.data).value != false ? UserSettingData.EmailSettingEnum.ENABLED : UserSettingData.EmailSettingEnum.DISABLED);
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to save user email notification setting '%s': %s", useridStr, e.getMessage()));
            throw new FusionRestException(101, "Internal error while saving user email notification setting");
        }
    }

    @POST
    @Path(value="/{userid}/emailcategory/{emailtype}/notification/")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setEmailCategoryNotification(@PathParam(value="userid") String useridStr, @PathParam(value="emailtype") String emailTypeStr, DataHolder<String> emailSettingValue) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        UserSettingData.TypeEnum emailType = UserSettingData.TypeEnum.fromName(emailTypeStr);
        if (userid == -1) {
            log.error((Object)String.format("Failed to save user email notification setting: no user id is specified", new Object[0]));
            throw new FusionRestException(-1, "Incorrect user id specified");
        }
        if (emailType == null) {
            log.error((Object)String.format("Failed to save user email notification setting: invalid email type", new Object[0]));
            throw new FusionRestException(-1, "Incorrect email type specified");
        }
        UserSettingData.EmailSettingEnum emailSetting = UserSettingData.EmailSettingEnum.fromName((String)emailSettingValue.data);
        if (emailSetting == null) {
            log.error((Object)String.format("Failed to save user email notification setting: invalid email setting value", new Object[0]));
            throw new FusionRestException(-1, "Incorrect email setting value specified");
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userEJB.getUsernameByUserid(userid, null);
            userEJB.updateEmailNotificationSetting(username, emailType, emailSetting);
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to save user email notification setting '%s': %s", useridStr, e.getMessage()));
            throw new FusionRestException(101, "Internal error while saving user email notification setting");
        }
    }

    @GET
    @Path(value="/{userid}/account/alias")
    @Produces(value={"application/json"})
    public DataHolder<SettingsUserAliasData> getUserAlias(@PathParam(value="userid") String useridStr) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String alias = null;
            if (userid != -1) {
                alias = userEJB.getUserAliasByUserid(userid, null);
            } else {
                String username = useridStr;
                alias = userEJB.getUserAliasByUsername(username, null);
            }
            return new DataHolder<SettingsUserAliasData>(new SettingsUserAliasData(alias));
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to retrieve user alias '%s' due to EJB CreateException: %s", useridStr, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user alias");
        }
    }

    @GET
    @Path(value="/{userid}/account/alias/check")
    @Produces(value={"application/json"})
    public DataHolder<? extends Object> checkUserAlias(@PathParam(value="userid") String useridStr, @QueryParam(value="alias") String alias) throws FusionRestException {
        if (StringUtil.isBlank(alias)) {
            throw new FusionRestException(103, String.format("alias is not specified", new Object[0]));
        }
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = null;
            if (userid == -1) {
                username = useridStr;
                userid = userEJB.getUserID(username, null, false);
            } else {
                username = userEJB.getUsernameByUserid(userid, null);
            }
            if (userid == -1) {
                throw new FusionRestException(102, String.format("Invalid user '%s'", useridStr));
            }
            HashMap<String, Object> data = new HashMap<String, Object>();
            try {
                userEJB.checkUserAliasByUserid(userid, alias, alias.equalsIgnoreCase(username), null);
                data.put("is_valid_alias", Boolean.TRUE);
            }
            catch (EJBException e) {
                data.put("is_valid_alias", Boolean.FALSE);
                data.put("reason", e.getMessage());
            }
            return new DataHolder(data);
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to check user alias '%s' for user '%s' due to EJB CreateException: %s", alias, useridStr, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while checking user alias");
        }
    }

    @POST
    @Path(value="/{userid}/account/alias")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setUserAlias(@PathParam(value="userid") String useridStr, DataHolder<SettingsUserAliasData> dataHolder) throws FusionRestException {
        SettingsUserAliasData data = (SettingsUserAliasData)dataHolder.data;
        log.info((Object)"set user alias");
        if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
            log.info((Object)"DAO: set user alias");
            try {
                UserObject user = UserObject.createUserObject(useridStr);
                user.setAlias(data.alias);
                return Response.ok().entity(new DataHolder<String>("ok")).build();
            }
            catch (DAOException e) {
                log.error((Object)String.format("Failed to save alias:[%s] for user:[%s] due to DAOException: %s", data.alias, useridStr, e.getMessage()), (Throwable)e);
                throw new FusionRestException(103, String.format("Unable to set alias: %s", e.getMessage()));
            }
        }
        log.info((Object)"EJB: user alias");
        try {
            int userid = StringUtil.toIntOrDefault(useridStr, -1);
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            if (userid == -1) {
                String username = useridStr;
                userid = userEJB.getUserID(username, null, false);
            }
            if (userid == -1) {
                throw new FusionRestException(102, String.format("Invalid user '%s'", useridStr));
            }
            try {
                userEJB.setUserAliasByUserid(userid, data.alias);
            }
            catch (EJBException e) {
                throw new FusionRestException(103, String.format("Unable to set alias: %s", e.getMessage()));
            }
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to save user alias '%s' due to EJB CreateException: %s", useridStr, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while saving user alias");
        }
    }

    @POST
    @Path(value="/emailinquery")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response emailInquery(DataHolder<EmailInqueryData> dataHolder) throws FusionRestException {
        EmailInqueryData data = (EmailInqueryData)dataHolder.data;
        try {
            EJBIcePrxFinder.getUserNotificationServiceProxy().sendEmailFromNoReply(SystemProperty.get(SystemPropertyEntities.UserNotificationServiceSettings.INQUERY_TO_EMAIL_ADDRESS), data.subject, data.content);
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to send email inquery due toException: %s", e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while saving user alias");
        }
    }

    @Deprecated
    public DataHolder<RegistrationTokenData> setUserAlias(@PathParam(value="userid") Integer userid, @PathParam(value="token") String token) throws FusionRestException {
        return this.verifyExternalEmailAddress(userid, token);
    }

    @GET
    @Path(value="/{userid}/email/verify/{token}")
    @Produces(value={"application/json"})
    public DataHolder<RegistrationTokenData> verifyExternalEmailAddress(@PathParam(value="userid") Integer userid, @PathParam(value="token") String token) throws FusionRestException {
        try {
            UserLocal user = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            try {
                return new DataHolder<RegistrationTokenData>(user.verifyExternalEmailAddress(userid, token));
            }
            catch (EJBException e) {
                log.error((Object)("Email verification failed for [" + userid + "] : " + e.getMessage()));
                throw new FusionRestException(102, "Invalid token provided.");
            }
        }
        catch (CreateException e) {
            log.error((Object)("Email verification failed for [" + userid + "] : " + e.getMessage()));
            throw new FusionRestException(101, "Failed to verify email address - internal esrver error.");
        }
        catch (Exception e) {
            log.error((Object)("Email verification failed for [" + userid + "] : " + e.getMessage()));
            throw new FusionRestException(102, "Failed to verify email address - " + e.getMessage());
        }
    }

    @GET
    @Path(value="/{userid}/security_question")
    @Produces(value={"application/json"})
    public DataHolder<String> getUserSecurityQuestion(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        try {
            int userid = StringUtil.toIntOrDefault(userIdStr, -1);
            if (userid == -1) {
                throw new FusionRestException(101, String.format("Invalid userid [%s]", userIdStr));
            }
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            SecurityQuestion sq = userEJB.getSecurityQuestion(userid);
            if (sq == null) {
                throw new FusionRestException(101, "User has not set security question yet.");
            }
            return new DataHolder<String>(sq.question);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while retrieving user categories: " + e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user security question.");
        }
    }

    @GET
    @Path(value="/{userid}/email/activation_request")
    @Produces(value={"application/json"})
    public Response sendEmailVerification(@PathParam(value="userid") Integer userid) throws FusionRestException {
        String username = null;
        try {
            UserLocal user = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            username = user.getUsernameByUserid(userid, null);
            if (null == username) {
                throw new FusionRestException(102, "User does not exist.");
            }
            String emailAddressToVerify = null;
            UserEmailAddressData emailAddress = user.getUserEmailAddressByType(userid, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY);
            if (emailAddress != null) {
                emailAddressToVerify = emailAddress.emailAddress;
            } else {
                UserData userData = user.loadUserFromID(userid);
                emailAddressToVerify = userData.emailAddress;
                user.addUserEmailAddress(userid, emailAddressToVerify, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY);
            }
            if (StringUtil.isBlank(emailAddressToVerify)) {
                throw new FusionRestException(101, "No email address detected.");
            }
            user.sendEmailVerification(userid, emailAddressToVerify);
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to save user profile '%s' due to EJBException: %s", username, e.getMessage()));
            throw new FusionRestException(101, "Internal error while saving user data");
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to save user profile '%s' due to EJBException: %s", username, e.getMessage()));
            throw new FusionRestException(101, "Failed to send activation email - " + e.getMessage());
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/email/activation_request/username/{username}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response sendEmailVerificationWithUsername(@PathParam(value="username") String username, DataHolder<UserEmailData> dataholder) throws FusionRestException {
        UserEmailData userEmailData = (UserEmailData)dataholder.data;
        try {
            UserLocal user = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            if (!user.isEmailRegistrationNotVerifiedForUsername(username, userEmailData.emailAddress)) {
                throw new FusionRestException(101, "Username and email address do not match");
            }
            UserActivationData userActivationData = user.getVerificationDataFromUserRegistrationTable(username);
            user.sendVerificationToken(username, userActivationData.emailAddress, userActivationData.token, userActivationData.registrationType);
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to save user profile '%s' due to EJBException: %s", username, e.getMessage()));
            throw new FusionRestException(101, "Internal error while saving user data");
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to save user profile '%s' due to EJBException: %s", username, e.getMessage()));
            throw new FusionRestException(101, "Failed to send activation email - " + e.getMessage());
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/{userid}/account/thirdpartysites/{siteid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response updateThirdPartySiteCredential(@PathParam(value="userid") String userIdStr, @PathParam(value="siteid") String siteIdStr, String credentialsJson) throws FusionRestException {
        int userId = StringUtil.toIntOrDefault(userIdStr, -1);
        if (userId == -1) {
            throw new FusionRestException(101, "Invalid User ID");
        }
        byte siteId = StringUtil.toByteOrDefault(siteIdStr, (byte)-1);
        if (siteId == -1) {
            throw new FusionRestException(101, "Invalid third party site ID");
        }
        log.info((Object)("Updating credential for User ID [" + userIdStr + "] and Site ID [" + siteIdStr + "] with Credential: " + credentialsJson));
        if (ThirdPartySiteCredentialManager.updateCredential(userId, siteId, credentialsJson)) {
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        throw new FusionRestException(101, "Internal Server Error: Failed to update credential");
    }

    @POST
    @Path(value="/account/thirdpartysites/{siteid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response updateThirdPartySiteCredentialUsingEmail(@QueryParam(value="emailAddress") String emailAddress, @PathParam(value="siteid") String siteIdStr, String credentialsJson) throws FusionRestException {
        try {
            UserLocal user = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            int userId = user.getUserID(user.getUsernameByEmailAddress(emailAddress, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY), null);
            return this.updateThirdPartySiteCredential(String.valueOf(userId), siteIdStr, credentialsJson);
        }
        catch (Exception e) {
            throw new FusionRestException(101, "Unable to link email address");
        }
    }

    @DELETE
    @Path(value="/{userid}/account/thirdpartysites/{siteid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response deleteThirdPartySiteCredential(@PathParam(value="userid") String userIdStr, @PathParam(value="siteid") String siteIdStr) throws FusionRestException {
        int userId = StringUtil.toIntOrDefault(userIdStr, -1);
        if (userId == -1) {
            throw new FusionRestException(101, "Invalid User ID");
        }
        byte siteId = StringUtil.toByteOrDefault(siteIdStr, (byte)-1);
        if (siteId == -1) {
            throw new FusionRestException(101, "Invalid third party site ID");
        }
        log.info((Object)("Deleting credential for User ID [" + userIdStr + "] and Site ID [" + siteIdStr + "]"));
        if (ThirdPartySiteCredentialManager.deleteCredential(userId, siteId)) {
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        throw new FusionRestException(101, "Internal Server Error: Failed to delete credential");
    }

    @GET
    @Path(value="/{userid}/account/thirdpartysites")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public String getThirdPartySiteCredentials(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        int userId = StringUtil.toIntOrDefault(userIdStr, -1);
        if (userId == -1) {
            throw new FusionRestException(101, "Invalid User ID");
        }
        log.info((Object)("Retrieving third party site credentials for User ID [" + userIdStr + "]"));
        String credentials = ThirdPartySiteCredentialManager.getCredentialsJsonStr(userId);
        if (credentials == null) {
            throw new FusionRestException(101, "Internal Server Error: Failed to get credential");
        }
        return credentials;
    }

    @GET
    @Path(value="/forgotpassword/{token}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<Boolean> validateForgotPasswordToken(@PathParam(value="token") String tokenStr, @QueryParam(value="username") String usernameStr) throws FusionRestException {
        if (StringUtil.isBlank(usernameStr)) {
            throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Please provide your username.");
        }
        String memcacheToken = MemCachedClientWrapper.getString(MemCachedKeySpaces.TokenKeySpace.FORGOT_PASSWORD, usernameStr);
        if (StringUtil.isBlank(memcacheToken) || !memcacheToken.equals(tokenStr)) {
            log.warn((Object)("Invalid token entered: token [" + tokenStr + "] username [" + usernameStr + "]."));
            throw new FusionRestException(FusionRestException.RestException.INVALID_TOKEN);
        }
        return new DataHolder<Boolean>(true);
    }

    @GET
    @Path(value="/status/forgotpassword")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<Map<Enums.ForgotPasswordEnum, Boolean>> validateForgotPasswordToken(@QueryParam(value="username") String usernameStr) throws FusionRestException {
        if (StringUtil.isBlank(usernameStr)) {
            throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Please provide your username.");
        }
        HashMap<Enums.ForgotPasswordEnum, Boolean> result = new HashMap<Enums.ForgotPasswordEnum, Boolean>();
        for (Enums.ForgotPasswordEnum type : Enums.ForgotPasswordEnum.values()) {
            boolean check = true;
            try {
                check = MemCachedRateLimiter.checkWithoutHit(MemCachedRateLimiter.NameSpace.FORGOT_PASSWORD_REQUEST.toString(), StringUtil.join(new String[]{usernameStr, type.name()}, "/"), Enums.ForgotPasswordEnum.getRatelimitPattern(type));
            }
            catch (Exception ignored) {
                log.warn((Object)("Faield to check ratelimit for key pattern: [" + StringUtil.join(new String[]{usernameStr, type.name()}, "/") + "], due to:" + ignored + ". ignored and set status to false."), (Throwable)ignored);
                check = false;
            }
            if (check && type == Enums.ForgotPasswordEnum.VIA_SMS) {
                try {
                    UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                    check = userEJB.allowForgotPasswordViaSMS(usernameStr);
                }
                catch (Exception ignored) {
                    log.warn((Object)("Failed to check forgot password status for sms, due to:" + ignored + ", ignored and set status to false"), (Throwable)ignored);
                    check = false;
                }
            }
            result.put(type, check);
        }
        return new DataHolder<Map<Enums.ForgotPasswordEnum, Boolean>>(result);
    }

    @POST
    @Path(value="/forgotpassword")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<String> forgotPassword(DataHolder<ForgotPasswordData> postRequest) throws FusionRestException {
        if (!SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.ENABLED)) {
            throw new FusionRestException(FusionRestException.RestException.DISABLED_FEATURE);
        }
        ForgotPasswordData postRequestData = (ForgotPasswordData)postRequest.data;
        Enums.ForgotPasswordEnum type = null;
        if (postRequestData.type == null) {
            if (!StringUtil.isBlank(postRequestData.emailAddress)) {
                type = Enums.ForgotPasswordEnum.VIA_EMAIL;
            }
        } else {
            type = Enums.ForgotPasswordEnum.fromValue(postRequestData.type);
        }
        if (type == null) {
            throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "You need to specify forgot password type");
        }
        if (!Enums.ForgotPasswordEnum.isForgotPasswordTypeEnabled(type)) {
            throw new FusionRestException(FusionRestException.RestException.DISABLED_FEATURE);
        }
        this.checkRetrievePassworRatelimit(type, postRequestData);
        String token = null;
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            if (type == Enums.ForgotPasswordEnum.VIA_EMAIL) {
                token = userEJB.forgotPasswordViaEmail(postRequestData.username, postRequestData.emailAddress);
            } else if (type == Enums.ForgotPasswordEnum.VIA_SECURITY_QUESTION) {
                token = userEJB.forgotPasswordViaSecurityQuestion(postRequestData.username, postRequestData.securityAnswer, postRequestData.securityQuestion);
            } else if (type == Enums.ForgotPasswordEnum.VIA_SMS) {
                token = userEJB.forgotPasswordViaSMS(postRequestData.username, new AccountEntrySourceData(postRequestData.ipAddress, null, postRequestData.mobileDevice, postRequestData.userAgent));
            }
        }
        catch (CreateException e) {
            log.error((Object)String.format("Forgot password request failed for request data:%s", postRequestData), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Unable to process your request at the moment. Please try again later.");
        }
        catch (FusionEJBException e) {
            log.error((Object)String.format("Forgot password request failed for request data:%s", postRequestData), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
        catch (Exception e) {
            log.error((Object)String.format("Forgot password request failed for request data:%s", postRequestData), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Unable to process your request at the moment. Please try again later.");
        }
        return new DataHolder<String>(token);
    }

    private void checkRetrievePassworRatelimit(Enums.ForgotPasswordEnum type, ForgotPasswordData postRequestData) throws FusionRestException {
        String forgotPasswordRateLimit = SystemProperty.get(SystemPropertyEntities.ForgotPassword.ATTEMPT_RATE_LIMIT);
        try {
            MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.FORGOT_PASSWORD_REQUEST.toString(), postRequestData.username, forgotPasswordRateLimit);
        }
        catch (MemCachedRateLimiter.LimitExceeded e) {
            log.warn((Object)String.format("Rate limit exceeded for forgot password, request data:%s, ratelimit:%s", postRequestData, forgotPasswordRateLimit));
            throw new FusionRestException(FusionRestException.RestException.FORGOT_PASSWORD_OVERALL_RATE_LIMIT, FusionRestException.RestException.FORGOT_PASSWORD_OVERALL_RATE_LIMIT.getMessage());
        }
        catch (MemCachedRateLimiter.FormatError e) {
            log.warn((Object)String.format("Unable to trigger forgot password request due to invalid rate limit format error:, request data:%s, ratelimit:%s", postRequestData, forgotPasswordRateLimit));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Sorry, we are unable to process your request at the moment. please try again later.");
        }
        if (SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.ENABLED_RATELIMIT_PER_IP) && !StringUtils.isBlank((CharSequence)postRequestData.ipAddress)) {
            forgotPasswordRateLimit = SystemProperty.get(SystemPropertyEntities.ForgotPassword.ATTEMPT_RATE_LIMIT_PER_IP);
            try {
                MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.FORGOT_PASSWORD_REQUEST.toString(), postRequestData.ipAddress, forgotPasswordRateLimit);
            }
            catch (MemCachedRateLimiter.LimitExceeded e) {
                log.warn((Object)String.format("Rate limit exceeded for forgot password, request data:%s, ratelimit:%s", postRequestData, forgotPasswordRateLimit));
                throw new FusionRestException(FusionRestException.RestException.FORGOT_PASSWORD_OVERALL_RATE_LIMIT, FusionRestException.RestException.FORGOT_PASSWORD_OVERALL_RATE_LIMIT.getMessage());
            }
            catch (MemCachedRateLimiter.FormatError e) {
                log.warn((Object)String.format("Unable to trigger forgot password request due to invalid rate limit format error:, request data:%s, ratelimit:%s", postRequestData, forgotPasswordRateLimit));
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Sorry, we are unable to process your request at the moment. please try again later.");
            }
        }
        if (!StringUtil.isBlank(forgotPasswordRateLimit = Enums.ForgotPasswordEnum.getRatelimitPattern(type))) {
            try {
                MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.FORGOT_PASSWORD_REQUEST.toString(), StringUtil.join(new String[]{postRequestData.username, type.name()}, "/"), forgotPasswordRateLimit);
            }
            catch (MemCachedRateLimiter.LimitExceeded e) {
                log.warn((Object)String.format("Rate limit exceeded for forgot password, request data:%s, ratelimit:%s", postRequestData, forgotPasswordRateLimit));
                throw new FusionRestException(Enums.ForgotPasswordEnum.getRatelimitRestException(type), FusionRestException.RestException.FORGOT_PASSWORD_OVERALL_RATE_LIMIT.getMessage());
            }
            catch (MemCachedRateLimiter.FormatError e) {
                log.warn((Object)String.format("Unable to trigger forgot password request due to invalid rate limit format error:, request data:%s, ratelimit:%s", postRequestData, forgotPasswordRateLimit));
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Sorry, we are unable to process your request at the moment. please try again later.");
            }
        }
    }

    @POST
    @Path(value="forgotpassword/{token}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<Boolean> changePassword(@PathParam(value="token") String tokenStr, DataHolder<ChangePasswordData> postRequest) throws FusionRestException {
        if (!SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.ENABLED)) {
            throw new FusionRestException(FusionRestException.RestException.DISABLED_FEATURE);
        }
        ChangePasswordData postRequestData = (ChangePasswordData)postRequest.data;
        String responseRateLimit = SystemProperty.get(SystemPropertyEntities.ForgotPassword.RESPONSE_RATE_LIMIT);
        try {
            MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.FORGOT_PASSWORD_RESPONSE.toString(), StringUtil.join(new String[]{postRequestData.username, postRequestData.ipAddress}, "/"), responseRateLimit);
        }
        catch (MemCachedRateLimiter.LimitExceeded e) {
            log.warn((Object)("Rate limit exceeded for forgot password response :: user [" + postRequestData.username + "] token [" + tokenStr + "] ipAddress [" + postRequestData.ipAddress + "]: " + responseRateLimit));
            if (!SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.THROW_CAPTCHA_ON_CHANGE_PASSWORD_RATE_LIMIT_HIT)) {
                throw new FusionRestException(FusionRestException.RestException.FORGOT_PASSWORD_VIA_EMAIL_RATE_LIMIT);
            }
        }
        catch (MemCachedRateLimiter.FormatError e) {
            log.warn((Object)("Unable to trigger forgot password transaction due to invalid rate limit format error: user [" + postRequestData.username + "] token [" + tokenStr + "] ipAddress [" + postRequestData.ipAddress + "]: " + responseRateLimit));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Sorry, we are unable to process your request at the moment. please try again later.");
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userEJB.changePassword(tokenStr, postRequestData.username, postRequestData.newPassword, postRequestData.ipAddress);
        }
        catch (CreateException e) {
            log.error((Object)("Changing of password failed [" + postRequestData.username + "] ip [" + postRequestData.ipAddress + "]"), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Failed to change your password. Please try again later.");
        }
        catch (FusionEJBException e) {
            log.error((Object)("Changing of password failed [" + postRequestData.username + "] ip [" + postRequestData.ipAddress + "]"), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
        catch (Exception e) {
            log.error((Object)("Changing of password failed [" + postRequestData.username + "] ip [" + postRequestData.ipAddress + "]"), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Failed to change your password. Please try again later.");
        }
        return new DataHolder<Boolean>(true);
    }

    @POST
    @Path(value="/forgotusername")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<Boolean> forgotUsernameViaEmailAddress(DataHolder<ForgotUsernameData> postData) throws FusionRestException {
        ForgotUsernameData requestData = (ForgotUsernameData)postData.data;
        String forgotUsernameRateLimit = SystemProperty.get(SystemPropertyEntities.ForgotUsername.REQUEST_RATE_LIMIT);
        try {
            MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.FORGOT_USERNAME_REQUEST.toString(), StringUtil.join(new String[]{requestData.emailAddress, requestData.ipAddress}, "/"), forgotUsernameRateLimit);
        }
        catch (MemCachedRateLimiter.LimitExceeded e) {
            log.warn((Object)("Rate limit exceeded for forgot username :: email address [" + requestData.emailAddress + "]: ipAddress [" + requestData.ipAddress + "] : " + forgotUsernameRateLimit));
            throw new FusionRestException(FusionRestException.RestException.FORGOT_USERNAME_VIA_EMAIL_RATE_LIMIT);
        }
        catch (MemCachedRateLimiter.FormatError e) {
            log.warn((Object)("Unable to trigger forgot password request due to invalid rate limit format error: ipAddress [" + requestData.ipAddress + "] email address [" + requestData.emailAddress + "]: " + forgotUsernameRateLimit));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Sorry, we are unable to process your request at the moment. please try again later.");
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userEJB.forgotUsernameViaEmailAddress(requestData.emailAddress);
        }
        catch (CreateException e) {
            log.error((Object)("Unable to process forgot username request for emailaddress [" + requestData.emailAddress + "] ipAddress [" + requestData.ipAddress + "]"), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Sorry, we are unable to process you request at the moment. Please try again later");
        }
        catch (FusionEJBException e) {
            log.error((Object)("Unable to process forgot username request for emailaddress [" + requestData.emailAddress + "] ipAddress [" + requestData.ipAddress + "]"), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
        return new DataHolder<Boolean>(true);
    }

    @POST
    @Path(value="/{userid}/usersettings")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<Boolean> updateUserSettings(@PathParam(value="userid") String useridStr, DataHolder<UserSettingRestData> dataHolder) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            throw new FusionRestException(102, "Incorrect user id specified:" + useridStr);
        }
        UserSettingRestData data = (UserSettingRestData)dataHolder.data;
        UserSettingData.TypeEnum type = UserSettingData.TypeEnum.fromValue(data.type);
        if (type == null) {
            throw new FusionRestException(102, "Invalid type specified:" + data.type);
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userEJB.getUsernameByUserid(userid, null);
            userEJB.updateUserSetting(username, type, data.value);
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to update user setting data for user:%s with data:%s, due to:%s", new Object[]{useridStr, data, e}), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to update user setting data for user:%s with data:%s, due to:%s", useridStr, data, e), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
        return new DataHolder<Boolean>(true);
    }
}

