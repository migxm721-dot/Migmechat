/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.s3uploader.S3Uploader
 *  com.projectgoth.s3uploader.S3UploaderConfiguration
 *  com.projectgoth.s3uploader.S3UploaderContentTypeEnum
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.HeaderParam
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 *  org.imgscalr.Scalr
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  redis.clients.jedis.Jedis
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.AlertMessageData;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.EmoticonData;
import com.projectgoth.fusion.data.ListDataWrapper;
import com.projectgoth.fusion.data.MerchantLocationData;
import com.projectgoth.fusion.data.RegistrationContextData;
import com.projectgoth.fusion.data.StoreItemData;
import com.projectgoth.fusion.data.StoreItemInventoryData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserDataAndRegistrationContextData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.data.UserRegistrationContextData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.data.VirtualGiftReceivedData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.eventqueue.EventQueue;
import com.projectgoth.fusion.eventqueue.events.ThirdPartySiteCredentialUpdatedEvent;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.interfaces.ContactLocal;
import com.projectgoth.fusion.interfaces.ContactLocalHome;
import com.projectgoth.fusion.interfaces.ContentLocal;
import com.projectgoth.fusion.interfaces.ContentLocalHome;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.interfaces.MerchantsLocal;
import com.projectgoth.fusion.interfaces.MerchantsLocalHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.interfaces.Web;
import com.projectgoth.fusion.interfaces.WebHome;
import com.projectgoth.fusion.invitation.InvitationData;
import com.projectgoth.fusion.invitation.InvitationResponseData;
import com.projectgoth.fusion.invitation.InvitationUtils;
import com.projectgoth.fusion.invitation.restapi.data.InvitationDetailsData;
import com.projectgoth.fusion.invitation.restapi.data.SendingInvitationData;
import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.restapi.data.BooleanData;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FacebookCredentialData;
import com.projectgoth.fusion.restapi.data.FileUploadData;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.RSSFeedForUser;
import com.projectgoth.fusion.restapi.data.RSSFeedForUserList;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.fusion.restapi.data.SettingsEnums;
import com.projectgoth.fusion.restapi.data.SettingsProfileDetailsData;
import com.projectgoth.fusion.restapi.data.StringData;
import com.projectgoth.fusion.restapi.data.UpdateUnfundedBalanceData;
import com.projectgoth.fusion.restapi.data.UserAlerts;
import com.projectgoth.fusion.restapi.data.UserCreationData;
import com.projectgoth.fusion.restapi.data.UserEmoticonList;
import com.projectgoth.fusion.restapi.data.UserMigboProfileData;
import com.projectgoth.fusion.restapi.data.UserUserRelationshipData;
import com.projectgoth.fusion.restapi.data.UserVerificationData;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import com.projectgoth.fusion.restapi.enums.MigboAccessMemberTypeEnum;
import com.projectgoth.fusion.restapi.enums.RegistrationType;
import com.projectgoth.fusion.restapi.resource.SettingsResource;
import com.projectgoth.fusion.restapi.util.MigboApiUtil;
import com.projectgoth.fusion.restapi.util.RedisDataUtil;
import com.projectgoth.fusion.restapi.util.ResourceUtil;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.thirdpartysites.ThirdPartySiteCredentialManager;
import com.projectgoth.fusion.uns.UserNotificationServiceI;
import com.projectgoth.s3uploader.S3Uploader;
import com.projectgoth.s3uploader.S3UploaderConfiguration;
import com.projectgoth.s3uploader.S3UploaderContentTypeEnum;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/user")
public class UserResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserResource.class));

    @GET
    @Path(value="/{userid}/profile")
    @Produces(value={"application/json"})
    public DataHolder<UserMigboProfileData> getMigboUserProfile(@PathParam(value="userid") String useridOrUsername, @QueryParam(value="useUsername") String useUsernameStr, @QueryParam(value="relatedUserid") String relatedUseridStr) throws FusionRestException {
        boolean useUsername = StringUtil.toBooleanOrDefault(useUsernameStr, false);
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            int userid = -1;
            if (!useUsername && (userid = StringUtil.toIntOrDefault(useridOrUsername, -1)) == -1) {
                log.warn((Object)String.format("useUsername is false '%s', but userid '%s' is invalid, treating it as username anyway", useUsername, useridOrUsername));
                useUsername = true;
            }
            UserMigboProfileData profile = useUsername ? UserMigboProfileData.createUserMigboProfileData(useridOrUsername, userEJB, relatedUseridStr) : UserMigboProfileData.createUserMigboProfileData(userid, userEJB, relatedUseridStr);
            return new DataHolder<UserMigboProfileData>(profile);
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to retrieve user profile '%s' due to EJB CreateException: %s", useridOrUsername, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user data");
        }
        catch (FusionRestException e) {
            throw e;
        }
        catch (Exception e) {
            log.error((Object)String.format("Unexpected error during retreival of user profile %s", useridOrUsername), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user data");
        }
    }

    @POST
    @Path(value="/profile/fetchlist")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<List<UserMigboProfileData>> getProfileList(String jsonData) throws FusionRestException {
        try {
            JSONObject containerObject = new JSONObject(jsonData);
            JSONObject dataObject = containerObject.getJSONObject("data");
            JSONArray userIdArray = dataObject.getJSONArray("userIds");
            int numberOfUserIds = userIdArray.length();
            ArrayList<UserMigboProfileData> resultList = new ArrayList<UserMigboProfileData>(numberOfUserIds);
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            for (int i = 0; i < numberOfUserIds; ++i) {
                int userId = userIdArray.getInt(i);
                try {
                    UserMigboProfileData profile = UserMigboProfileData.createUserMigboProfileData(userId, userEJB, null);
                    resultList.add(profile);
                    continue;
                }
                catch (Exception e) {
                    log.error((Object)String.format("Unexpected error during retreival of user profile %d during fetchlist", userId), (Throwable)e);
                }
            }
            if (resultList.size() == 0) {
                throw new FusionRestException(101, "Internal error while retrieving user data");
            }
            return new DataHolder<List<UserMigboProfileData>>(resultList);
        }
        catch (FusionRestException e) {
            throw e;
        }
        catch (Exception e) {
            log.error((Object)"Unexpected error during retreival of profile list", (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user data");
        }
    }

    @GET
    @Path(value="/{userid}/miglevel")
    @Produces(value={"application/json"})
    public DataHolder<String> getMiglevel(@PathParam(value="userid") String useridOrUsername, @QueryParam(value="useUsername") String useUsernameStr) throws FusionRestException {
        boolean useUsername = StringUtil.toBooleanOrDefault(useUsernameStr, false);
        try {
            int userid = -1;
            String username = null;
            if (!useUsername && (userid = StringUtil.toIntOrDefault(useridOrUsername, -1)) == -1) {
                log.warn((Object)String.format("useUsername is false '%s', but userid '%s' is invalid, treating it as username anyway", useUsername, useridOrUsername));
                username = useridOrUsername;
            }
            int migLevel = -1;
            if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
                migLevel = userid == -1 ? new UserObject(username).getReputationLevel().level.intValue() : new UserObject((int)userid).getReputationLevel().level.intValue();
            } else {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                if (userid == -1) {
                    username = useridOrUsername;
                    userid = userEJB.getUserID(username, null, false);
                    if (userid == -1) {
                        throw new FusionRestException(101, String.format("Invalid username %s", username));
                    }
                    migLevel = MemCacheOrEJB.getUserReputationLevel(username);
                } else {
                    migLevel = userEJB.getReputationLevelByUserid((int)userid).level;
                }
            }
            return new DataHolder<String>(Integer.toString(migLevel));
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to retrieve user miglevel '%s' due to EJB CreateException: %s", useridOrUsername, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user miglevel");
        }
        catch (FusionRestException e) {
            throw e;
        }
        catch (Exception e) {
            log.error((Object)String.format("Unexpected error during retreival of user miglevel %s", useridOrUsername), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user miglevel");
        }
    }

    @POST
    @Path(value="/{userid}/profile/{viewerUserid}/view")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> recordFootprints(@PathParam(value="userid") String userIdStr, @PathParam(value="viewerUserid") String viewerUseridStr) throws FusionRestException {
        if (!SystemProperty.getBool("recordFootprintsEnabled", false)) {
            return new DataHolder<BooleanData>(new BooleanData(false));
        }
        int userId = this.getAndCheckUserid(userIdStr);
        int viewerUserid = StringUtil.toIntOrDefault(viewerUseridStr, -1);
        if (viewerUserid == -1) {
            throw new FusionRestException(101, String.format("Invalid requesting user ID [%s]", viewerUseridStr));
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            UserData viewedUserData = userEJB.loadUserFromID(userId);
            if (viewedUserData == null) {
                throw new FusionRestException(101, String.format("Invalid user ID [%s] - not found", userIdStr));
            }
            CountryData viewedCountryData = misEJB.getCountry(viewedUserData.countryID);
            UserData viewerUserData = userEJB.loadUserFromID(viewerUserid);
            if (viewerUserData == null) {
                throw new FusionRestException(101, String.format("Invalid viewer user ID [%s] - not found", viewerUseridStr));
            }
            CountryData viewerCountryData = misEJB.getCountry(viewerUserData.countryID);
            BooleanData data = new BooleanData(RedisDataUtil.recordProfileView(viewedUserData, viewedCountryData, viewerUserData, viewerCountryData));
            return new DataHolder<BooleanData>(data);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while recording footprints for user: " + e.getMessage()));
            BooleanData data = new BooleanData(false);
            return new DataHolder<BooleanData>(data);
        }
    }

    @GET
    @Path(value="/{userid}/friends")
    @Produces(value={"application/json"})
    public DataHolder<Map<String, List<Integer>>> getUserFriends(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        int userID = -1;
        LinkedList<Integer> userIDList = new LinkedList<Integer>();
        try {
            userID = Integer.parseInt(userIdStr);
        }
        catch (Exception ignored) {
            // empty catch block
        }
        if (userID == -1) {
            log.warn((Object)String.format("Failed to retrieve user friends for '%d'", userIdStr));
            throw new FusionRestException(101, "Incorrect userid specified");
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userEJB.getUsernameByUserid(userID, null);
            Set bcl = userEJB.loadBroadcastList(username, null);
            Iterator iter = bcl.iterator();
            while (iter.hasNext()) {
                int friendID = userEJB.getUserID((String)iter.next(), null, false);
                if (friendID <= 0) continue;
                userIDList.add(friendID);
            }
            HashMap<String, LinkedList<Integer>> map = new HashMap<String, LinkedList<Integer>>();
            map.put("friends", userIDList);
            return new DataHolder<Map<String, List<Integer>>>(map);
        }
        catch (SQLException e) {
            log.error((Object)String.format("Failed to retrieve user friends '%s' due to SQLException: %s", userIdStr, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user friends");
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to retrieve user friends '%s' due to EJB CreateException: %s", userIdStr, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user friends");
        }
    }

    @GET
    @Path(value="/{userid}/user/{relatedUserid}")
    @Produces(value={"application/json"})
    public DataHolder<UserUserRelationshipData> getUserRelationship(@PathParam(value="userid") String useridStr, @PathParam(value="relatedUserid") String relatedUseridStr) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        int relatedUserid = StringUtil.toIntOrDefault(relatedUseridStr, -1);
        try {
            ContactLocal contactEJB = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserUserRelationshipData data = new UserUserRelationshipData();
            try {
                if (userid == -1 || relatedUserid == -1) {
                    log.error((Object)String.format("Invalid input provided to the getUserRelationship() API userid[%s] relatedUserid[%s]", useridStr, relatedUseridStr));
                    throw new FusionRestException(101, "Internal error while retrieving user relationship data");
                }
                String username = userEJB.getUsernameByUserid(userid, null);
                String relatedUsername = userEJB.getUsernameByUserid(relatedUserid, null);
                data.isFriend = contactEJB.isFriend(userid, relatedUserid);
                data.isBlockedBy = contactEJB.isBlocking(relatedUsername, username);
                data.isBlocking = contactEJB.isBlocking(username, relatedUsername);
            }
            catch (FusionEJBException e) {
                log.error((Object)String.format("Failed to retrieve user relationship '%s' '%s': %s", useridStr, relatedUseridStr, e.getMessage()));
                throw new FusionRestException(101, "Internal error while retrieving user relationship data");
            }
            return new DataHolder<UserUserRelationshipData>(data);
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to retrieve user relationship '%s' '%s' due to EJB CreateException: %s", useridStr, relatedUseridStr, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user relationship data");
        }
    }

    @POST
    @Path(value="/{userid}/user/{relatedUserid}")
    @Produces(value={"application/json"})
    public Response updateUserRelationship(@PathParam(value="userid") String useridStr, @PathParam(value="relatedUserid") String relatedUseridStr, @QueryParam(value="action") String actionStr) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        int relatedUserid = StringUtil.toIntOrDefault(relatedUseridStr, -1);
        try {
            ContactLocal contactEJB = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String requestorUsername = userEJB.getUsernameByUserid(userid, null);
            String relatedUsername = userEJB.getUsernameByUserid(relatedUserid, null);
            boolean isBlocking = contactEJB.isBlocking(requestorUsername, relatedUsername);
            if ("@block".equals(actionStr)) {
                if (!isBlocking) {
                    contactEJB.blockContact(userid, requestorUsername, relatedUsername);
                } else {
                    log.warn((Object)String.format("Ignoring block request as [%d][%s] is already blocking [%d][%s]", userid, requestorUsername, relatedUserid, relatedUsername));
                }
            } else if ("@unblock".equals(actionStr)) {
                if (isBlocking) {
                    boolean shareMobilePhone = false;
                    contactEJB.unblockContact(requestorUsername, relatedUsername, shareMobilePhone);
                } else {
                    log.warn((Object)String.format("Ignoring unblock request as [%d][%s] is currently not blocking [%d][%s]", userid, requestorUsername, relatedUserid, relatedUsername));
                }
            } else {
                log.error((Object)String.format("Invalid action [%s] current_status[%s]", actionStr, isBlocking ? "blocked" : "unblocked"));
                throw new FusionRestException(101, String.format("Invalid action [%s] current_status[%s]", actionStr, isBlocking ? "blocked" : "unblocked"));
            }
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (FusionEJBException e) {
            log.error((Object)String.format("Failed to [%s] user '%s' by '%s' due to FusionEJBException: %s", actionStr, relatedUseridStr, useridStr, e.getMessage()), (Throwable)e);
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to [%s] user '%s' by '%s' due to EJB CreateException: %s", actionStr, relatedUseridStr, useridStr, e.getMessage()), (Throwable)e);
        }
        throw new FusionRestException(101, "Internal error while retrieving user relationship data");
    }

    public static Map<String, Integer> getAllPrivacy(int userid) throws FusionRestException {
        try {
            Enum privacyNew;
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            Map<String, Integer> privacy = RedisDataUtil.getAllAccountPrivacy(userid);
            String username = null;
            HashMap<String, Integer> privacyToWrite = new HashMap<String, Integer>();
            SettingsEnums.EveryoneOrFollowerAndFriend feed = null;
            if (!privacy.containsKey("FeedPv")) {
                username = userEJB.getUsernameByUserid(userid, null);
                feed = SettingsResource.getOldFeedContentPrivacy(userid, username, userEJB);
                privacy.put("FeedPv", feed.value());
                privacyToWrite.put("FeedPv", feed.value());
            } else {
                feed = SettingsEnums.EveryoneOrFollowerAndFriend.fromValue(privacy.get("FeedPv"));
            }
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
            if (!privacy.containsKey("MobNumPrivacy")) {
                privacyNew = SettingsProfileDetailsData.PRIVACY_DEFAULT_MOBILEPHONE;
                privacy.put("MobNumPrivacy", ((SettingsEnums.EveryoneFriendHide)privacyNew).value());
                privacyToWrite.put("MobNumPrivacy", ((SettingsEnums.EveryoneFriendHide)privacyNew).value());
            }
            if (!privacy.containsKey("ChatPv")) {
                if (username == null) {
                    username = userEJB.getUsernameByUserid(userid, null);
                }
                SettingsEnums.EveryoneFollowerFriend chatNew = SettingsResource.getMigratedChatPrivacy(userid, username, userEJB, feed);
                privacy.put("ChatPv", chatNew.value());
                privacyToWrite.put("ChatPv", chatNew.value());
            }
            if (!privacy.containsKey("BuzzPv")) {
                SettingsEnums.OnOff buzzNew = SettingsResource.getMigratedBuzzPrivacy(userid, username, userEJB);
                privacy.put("BuzzPv", buzzNew.value());
                privacyToWrite.put("BuzzPv", buzzNew.value());
            }
            if (privacyToWrite.size() > 0 && !RedisDataUtil.setAccountPrivacy(userid, privacyToWrite)) {
                log.error((Object)String.format("Failed to save user user privacy '%d'", userid));
            }
            return privacy;
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to retrieve user privacy '%d' due to EJB CreateException: %s", userid, e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving user privacy");
        }
    }

    @GET
    @Path(value="/{userid}/privacy")
    @Produces(value={"application/json"})
    public DataHolder<Map<String, Integer>> getPrivacy(@PathParam(value="userid") String useridStr) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Failed to retrieve user privacy due to invalid userid '%s'", useridStr));
            throw new FusionRestException(101, "Internal error while retrieving user privacy");
        }
        return new DataHolder<Map<String, Integer>>(UserResource.getAllPrivacy(userid));
    }

    @GET
    @Path(value="/{userid}/alerts/unread/count")
    @Produces(value={"application/json"})
    public DataHolder<Map<Integer, Integer>> getAlertsUnreadCount(@PathParam(value="userid") String useridStr) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Failed to retrieve user alerts unread count due to invalid userid '%s'", useridStr));
            throw new FusionRestException(101, "Internal error while retrieving user alerts unread count");
        }
        try {
            UserNotificationServicePrx unsPrx = EJBIcePrxFinder.getUserNotificationServiceProxy();
            if (unsPrx != null) {
                try {
                    Map<Integer, Integer> data = unsPrx.getUnreadNotificationCountForUser(userid);
                    return new DataHolder<Map<Integer, Integer>>(data);
                }
                catch (FusionException e) {
                    log.error((Object)String.format("Failed to retrieve user alerts unread count: FusionException '%s'", e.message));
                    throw new FusionRestException(102, "Internal error while retrieving user alerts unread count");
                }
            }
            log.error((Object)String.format("Failed to retrieve user alerts unread count: unable to find UserNotificationService", new Object[0]));
            throw new FusionRestException(101, "Internal error while retrieving user alerts unread count");
        }
        catch (EJBException e) {
            log.error((Object)String.format("Failed to retrieve user alerts unread count: EJBException '%s'", e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving user alerts unread count");
        }
    }

    @GET
    @Path(value="/{userid}/alerts/unread")
    @Produces(value={"application/json"})
    public DataHolder<UserAlerts> getAlertsUnread(@PathParam(value="userid") String useridStr, @QueryParam(value="limit") String limitStr, @QueryParam(value="offset") String offsetStr) throws FusionRestException {
        int offset = StringUtil.toIntOrDefault(offsetStr, 0);
        int limit = StringUtil.toIntOrDefault(limitStr, 33);
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Failed to retrieve user alerts due to invalid userid '%s'", useridStr));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
        try {
            UserNotificationServicePrx unsPrx = EJBIcePrxFinder.getUserNotificationServiceProxy();
            if (unsPrx != null) {
                try {
                    Map<Integer, Integer> unreadCountMap = unsPrx.getUnreadNotificationCountForUser(userid);
                    Map<Integer, Map<String, Map<String, String>>> data = unsPrx.getUnreadPendingNotificationDataForUser(userid);
                    int unreadCount = 0;
                    UserAlerts alerts = new UserAlerts();
                    for (Integer key : data.keySet()) {
                        if (!unreadCountMap.containsKey(key)) continue;
                        unreadCount += unreadCountMap.get(key).intValue();
                    }
                    alerts.alerts = data;
                    alerts.unread = unreadCount;
                    return new DataHolder<UserAlerts>(alerts.retrievePage(offset, limit));
                }
                catch (FusionException e) {
                    log.error((Object)String.format("Failed to retrieve user alerts: FusionException '%s'", e.message));
                    throw new FusionRestException(102, "Internal error while retrieving user alerts");
                }
            }
            log.error((Object)String.format("Failed to retrieve user alerts: unable to find UserNotificationService", new Object[0]));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
        catch (EJBException e) {
            log.error((Object)String.format("Failed to retrieve user alerts: EJBException '%s'", e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @GET
    @Path(value="/{userid}/alerts")
    @Produces(value={"application/json"})
    public DataHolder<UserAlerts> getAlerts(@PathParam(value="userid") String useridStr, @QueryParam(value="limit") String limitStr, @QueryParam(value="offset") String offsetStr) throws FusionRestException {
        int offset = StringUtil.toIntOrDefault(offsetStr, 0);
        int limit = StringUtil.toIntOrDefault(limitStr, 33);
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Failed to retrieve user alerts due to invalid userid '%s'", useridStr));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
        try {
            UserNotificationServicePrx unsPrx = EJBIcePrxFinder.getUserNotificationServiceProxy();
            if (unsPrx == null) {
                log.error((Object)String.format("Failed to retrieve user alerts: unable to find UserNotificationService", new Object[0]));
                throw new FusionRestException(101, "Internal error while retrieving user alerts");
            }
            try {
                Map<Integer, Map<String, Map<String, String>>> data;
                Map<Integer, Integer> unreadCountMap;
                block15: {
                    unreadCountMap = unsPrx.getUnreadNotificationCountForUser(userid);
                    data = unsPrx.getPendingNotificationDataForUser(userid);
                    if (SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.PERSISTENT_ALERT_RECOUNT_ENABLED)) {
                        Jedis masterInstance = null;
                        try {
                            Object var16_19;
                            try {
                                masterInstance = Redis.getMasterInstanceForUserID(userid);
                                for (Enums.NotificationTypeEnum typeEnum : Enums.NotificationTypeEnum.PERSISTENT_SET) {
                                    int size;
                                    int type = typeEnum.getType();
                                    if (!unreadCountMap.containsKey(type)) continue;
                                    int n = size = data.get(type) == null ? 0 : data.get(type).size();
                                    if (unreadCountMap.get(type) == size) continue;
                                    log.debug((Object)String.format("Unread alert count was out of sync for User [%s], Type [%s], Old [%s], New [%s]. Resyncing...", new Object[]{userid, typeEnum, unreadCountMap.get(type), size}));
                                    masterInstance.set(UserNotificationServiceI.getUnreadCountUnsKey(userid, type), "" + size);
                                    unreadCountMap.put(type, size);
                                }
                                var16_19 = null;
                            }
                            catch (Exception e) {
                                log.error((Object)("Failed to set unread persistent alert count for User ID: " + userid), (Throwable)e);
                                var16_19 = null;
                                Redis.disconnect(masterInstance, log);
                                break block15;
                            }
                        }
                        catch (Throwable throwable) {
                            Object var16_20 = null;
                            Redis.disconnect(masterInstance, log);
                            throw throwable;
                        }
                        Redis.disconnect(masterInstance, log);
                    }
                }
                int unreadCount = 0;
                Iterator<Integer> i$ = data.keySet().iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        UserAlerts alerts = new UserAlerts();
                        alerts.alerts = data;
                        alerts.unread = unreadCount;
                        return new DataHolder<UserAlerts>(alerts.retrievePage(offset, limit));
                    }
                    Integer key = i$.next();
                    if (!unreadCountMap.containsKey(key)) continue;
                    unreadCount += unreadCountMap.get(key).intValue();
                }
            }
            catch (FusionException e) {
                log.error((Object)String.format("Failed to retrieve user alerts: FusionException '%s'", e.message));
                throw new FusionRestException(102, "Internal error while retrieving user alerts");
            }
        }
        catch (EJBException e) {
            log.error((Object)String.format("Failed to retrieve user alerts: EJBException '%s'", e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
    }

    @POST
    @Path(value="/{userid}/alert_response")
    @Produces(value={"application/json"})
    public Response alertResponse(@PathParam(value="userid") String useridStr, @QueryParam(value="alertType") String alertTypeStr, @QueryParam(value="alertKey") String alertKey, @QueryParam(value="responseType") String responseTypeStr) throws FusionRestException {
        int responseTypeInt;
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Failed to retrieve user alerts due to invalid userid '%s'", useridStr));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
        int alertType = StringUtil.toIntOrDefault(alertTypeStr, -1);
        if (alertType == -1) {
            try {
                Enums.NotificationTypeEnum type = Enums.NotificationTypeEnum.valueOf(alertTypeStr);
                alertType = type.getType();
            }
            catch (Exception ignored) {}
        } else if (!Enums.NotificationTypeEnum.isForPersistent(alertType)) {
            log.error((Object)String.format("Can not response to a non-important alert, alertTypeStr '%s'", alertType));
            throw new FusionRestException(101, "Internal error while retrieving alert type");
        }
        if ((responseTypeInt = StringUtil.toIntOrDefault(responseTypeStr, -1)) == -1) {
            log.error((Object)String.format("Failed to retrieve alert responseType due to invalid responseTypeStr '%s'", responseTypeStr));
            throw new FusionRestException(101, "Internal error while retrieving alert type");
        }
        InvitationResponseData.ResponseType responseType = InvitationResponseData.ResponseType.fromTypeCode(responseTypeInt);
        if (responseType == null) {
            log.error((Object)String.format("Failed to retrieve alert responseType due to invalid responseTypeStr '%s'", responseType));
            throw new FusionRestException(101, "Internal error while retrieving alert type");
        }
        if (responseType == InvitationResponseData.ResponseType.SIGN_UP_VERIFIED) {
            log.error((Object)String.format("Invalid responseTypeStr '%s', should not be handled in alertResponse", responseType));
            throw new FusionRestException(101, "Internal error while retrieving alert type");
        }
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            if (alertType == -1) {
                for (Enums.NotificationTypeEnum t : Enums.NotificationTypeEnum.ACCUMULATED_SET) {
                    userBean.responseToAlert(t.getType(), alertKey, userid, responseType);
                }
            } else {
                userBean.responseToAlert(alertType, alertKey, userid, responseType);
            }
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to response to alert, userid:%s alertType:%s, alertKey:%s: EJBException '%s'", userid, alertTypeStr, alertKey, e.getMessage()));
        }
        catch (FusionEJBException e) {
            log.error((Object)String.format("Failed to response to alert, userid:%s alertType:%s, alertKey:%s: EJBException '%s'", userid, alertTypeStr, alertKey, e.getMessage()));
        }
        catch (EJBException e) {
            log.error((Object)String.format("Failed to response to alert, userid:%s alertType:%s, alertKey:%s: EJBException '%s'", userid, alertTypeStr, alertKey, e.getMessage()));
        }
        throw new FusionRestException(101, "Internal error while retrieving user alerts");
    }

    @GET
    @Path(value="/{userid}/response")
    @Produces(value={"application/json"})
    public DataHolder<UserAlerts> responseAlert(@PathParam(value="userid") String useridStr, @QueryParam(value="limit") String limitStr, @QueryParam(value="offset") String offsetStr) throws FusionRestException {
        int offset = StringUtil.toIntOrDefault(offsetStr, 0);
        int limit = StringUtil.toIntOrDefault(limitStr, 33);
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Failed to retrieve user alerts due to invalid userid '%s'", useridStr));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
        try {
            UserNotificationServicePrx unsPrx = EJBIcePrxFinder.getUserNotificationServiceProxy();
            if (unsPrx != null) {
                try {
                    Map<Integer, Integer> unreadCountMap = unsPrx.getUnreadNotificationCountForUser(userid);
                    Map<Integer, Map<String, Map<String, String>>> data = unsPrx.getPendingNotificationDataForUser(userid);
                    int unreadCount = 0;
                    UserAlerts alerts = new UserAlerts();
                    for (Integer key : data.keySet()) {
                        if (!unreadCountMap.containsKey(key)) continue;
                        unreadCount += unreadCountMap.get(key).intValue();
                    }
                    alerts.alerts = data;
                    alerts.unread = unreadCount;
                    return new DataHolder<UserAlerts>(alerts.retrievePage(offset, limit));
                }
                catch (FusionException e) {
                    log.error((Object)String.format("Failed to retrieve user alerts: FusionException '%s'", e.message));
                    throw new FusionRestException(102, "Internal error while retrieving user alerts");
                }
            }
            log.error((Object)String.format("Failed to retrieve user alerts: unable to find UserNotificationService", new Object[0]));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
        catch (EJBException e) {
            log.error((Object)String.format("Failed to retrieve user alerts: EJBException '%s'", e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
    }

    @DELETE
    @Path(value="/{userid}/alert/{alerttype}/{alertkey}")
    @Produces(value={"application/json"})
    public Response deleteAlert(@PathParam(value="userid") String useridStr, @PathParam(value="alerttype") String alertTypeStr, @PathParam(value="alertkey") String alertKey) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Failed to delete user alert due to invalid userid '%s'", useridStr));
            throw new FusionRestException(101, "Internal error while deleting user alert");
        }
        int alertType = StringUtil.toIntOrDefault(alertTypeStr, -1);
        if (alertType == -1) {
            log.error((Object)String.format("Failed to delete user alert due to invalid alert type '%s'", alertTypeStr));
            throw new FusionRestException(101, "Internal error while deleting user alert");
        }
        if (Enums.NotificationTypeEnum.fromType(alertType) == null) {
            log.error((Object)String.format("Failed to delete user alert due to unknown alert type '%s'", alertTypeStr));
            throw new FusionRestException(101, "Internal error while deleting user alert");
        }
        try {
            UserNotificationServicePrx unsPrx = EJBIcePrxFinder.getUserNotificationServiceProxy();
            if (unsPrx != null) {
                try {
                    if (alertKey.equals("@all")) {
                        unsPrx.clearAllNotificationsByTypeForUser(userid, alertType);
                    } else {
                        unsPrx.clearNotificationsForUser(userid, alertType, new String[]{alertKey});
                    }
                    return Response.ok().entity(new DataHolder<String>("ok")).build();
                }
                catch (FusionException e) {
                    log.error((Object)String.format("Failed to retrieve user alerts: FusionException '%s'", e.message));
                    throw new FusionRestException(102, "Internal error while retrieving user alerts");
                }
            }
            log.error((Object)String.format("Failed to retrieve user alerts: unable to find UserNotificationService", new Object[0]));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
        catch (EJBException e) {
            log.error((Object)String.format("Failed to retrieve user alerts: EJBException '%s'", e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
    }

    @DELETE
    @Path(value="/{userid}/alert/unread/count")
    @Produces(value={"application/json"})
    public Response deleteAlertCount(@PathParam(value="userid") String useridStr, @QueryParam(value="resetAll") String resetAllStr) throws FusionRestException {
        if (!SystemProperty.getBool(SystemPropertyEntities.Alert.ENABLED_MANDATORY_RESET_UNREAD_COUNT)) {
            throw new FusionRestException(101, "Feature disabled");
        }
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Failed to reset user unread alert count due to invalid userid '%s'", useridStr));
            throw new FusionRestException(101, "Internal error while deleting user alert");
        }
        String rateLimit = SystemProperty.get(SystemPropertyEntities.Alert.MANDATORY_RESET_UNREAD_COUNT_RATE_LIMIT);
        if (!StringUtil.isBlank(rateLimit)) {
            try {
                MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.MANDATORY_RESET_UNREAD_ALERT_COUNT.toString(), useridStr, rateLimit);
            }
            catch (MemCachedRateLimiter.LimitExceeded e) {
                throw new FusionRestException(FusionRestException.RestException.RATE_LIMIT);
            }
            catch (MemCachedRateLimiter.FormatError e) {
                log.error((Object)"Formatting error in rate limiter expression", (Throwable)e);
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
            }
        }
        try {
            boolean resetAll = StringUtil.toBooleanOrDefault(resetAllStr, false);
            UserNotificationServicePrx unsPrx = EJBIcePrxFinder.getUserNotificationServiceProxy();
            if (unsPrx != null) {
                unsPrx.clearAllUnreadNotificationCountForUser(userid, resetAll);
                return Response.ok().entity(new DataHolder<String>("ok")).build();
            }
            log.error((Object)String.format("Failed to reset user[%s] unread alert counts: unable to find UserNotificationService", useridStr));
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to reset user[%s] unread alert counts: EJBException '%s'", useridStr, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user alerts");
        }
    }

    @GET
    @Path(value="/{userid}/emoticons")
    @Produces(value={"application/json"})
    public DataHolder<UserEmoticonList> getEmoticons(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            List emoticonPack = contentBean.getEmoticonPack(1);
            UserEmoticonList data = new UserEmoticonList(emoticonPack);
            return new DataHolder<UserEmoticonList>(data);
        }
        catch (CreateException e) {
            log.error((Object)("Unable to create content bean: " + e.getMessage()));
        }
        catch (EJBException e) {
            log.error((Object)("Unable to retrieve emoticon pack: " + e.getMessage()));
        }
        catch (FusionEJBException e) {
            log.error((Object)("Unable to retrieve emoticon pack: " + e.getMessage()));
        }
        throw new FusionRestException(101, "Internal error while retrieving emoticons");
    }

    @GET
    @Path(value="/{userid}/rss")
    @Produces(value={"application/json"})
    public DataHolder<RSSFeedForUserList> getRSSFeedForUser(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        try {
            int userid = StringUtil.toIntOrDefault(userIdStr, -1);
            if (userid == -1) {
                throw new FusionRestException(101, String.format("Invalid userid [%s]", userIdStr));
            }
            RSSFeedForUserList rssForUserList = new RSSFeedForUserList();
            MigboApiUtil api = MigboApiUtil.getInstance();
            log.info((Object)String.format("Retrieving list of RSSFeedForUsers from Migbo Dataservice for userid [%d]", userid));
            JSONObject obj = api.get(String.format("/user/%d/rss", userid));
            JSONObject data = obj.getJSONObject("data");
            JSONArray rssList = data.getJSONArray("rss");
            for (int i = 0; i < rssList.length(); ++i) {
                JSONObject rssFeedForUser = rssList.getJSONObject(i);
                String feedURL = rssFeedForUser.getString("url");
                RSSFeedForUser r = new RSSFeedForUser(userid, feedURL);
                rssForUserList.addRSSFeedForUser(r);
            }
            log.debug((Object)String.format("Received JSON Response fro migbo-datsvc : %s ", obj.toString()));
            return new DataHolder<RSSFeedForUserList>(rssForUserList);
        }
        catch (Exception e) {
            log.error((Object)String.format("Exception caught while retrieving RSS Feeds for user [%s]", userIdStr));
            throw new FusionRestException(101, "Internal error while retrieving RSS Feeds for user");
        }
    }

    @PUT
    @Path(value="/{userid}/rss")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public DataHolder<Map<String, String>> addRSSFeedForUser(@PathParam(value="userid") String userIdStr, String jsonData) throws FusionRestException {
        try {
            int userid = StringUtil.toIntOrDefault(userIdStr, -1);
            if (userid == -1) {
                throw new FusionRestException(101, String.format("Invalid userid [%s]", userIdStr));
            }
            MigboApiUtil api = MigboApiUtil.getInstance();
            JSONObject obj = api.put(String.format("/user/%d/rss", userid), jsonData);
            JSONObject data = obj.getJSONObject("data");
            log.debug((Object)String.format("Received JSON Response from migbo-datsvc : %s ", obj.toString()));
            String feedID = data.getString("id");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", feedID);
            return new DataHolder<Map<String, String>>(map);
        }
        catch (Exception e) {
            log.error((Object)String.format("Exception caught while creating RSS Feed for user [%s] %s", userIdStr, jsonData));
            throw new FusionRestException(101, "Internal error while registering RSS Feeds for user");
        }
    }

    @DELETE
    @Path(value="/{userid}/rss/{rssid}")
    @Produces(value={"application/json"})
    public Response removeRSSFeedForUser(@PathParam(value="userid") String userIdStr, @PathParam(value="rssid") String rssIdStr) throws FusionRestException {
        try {
            int userid = StringUtil.toIntOrDefault(userIdStr, -1);
            if (userid == -1) {
                throw new FusionRestException(101, String.format("Invalid userid [%s]", userIdStr));
            }
            MigboApiUtil api = MigboApiUtil.getInstance();
            JSONObject obj = api.delete(String.format("/user/%d/rss/%s", userid, rssIdStr));
            log.debug((Object)String.format("Received JSON Response from migbo-datsvc : %s ", obj.toString()));
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (Exception e) {
            log.error((Object)String.format("Exception caught while deleting RSS Feed for user [%s] feed [%s]", userIdStr, rssIdStr));
            throw new FusionRestException(101, "Internal error while removing RSS Feeds for user");
        }
    }

    @GET
    @Path(value="/{userid}/categories")
    @Produces(value={"application/json"})
    public DataHolder<Map<Integer, String[]>> getUserCategories(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        try {
            int userid = StringUtil.toIntOrDefault(userIdStr, -1);
            if (userid == -1) {
                throw new FusionRestException(101, String.format("Invalid userid [%s]", userIdStr));
            }
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            Map categories = userEJB.getUserCategoryNames(userid);
            return new DataHolder<Map<Integer, String[]>>(categories);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while retrieving user categories: " + e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving user categories.");
        }
    }

    @GET
    @Path(value="/category/{categoryid}")
    @Produces(value={"application/json"})
    public DataHolder<String[]> getUsernamesInCategory(@PathParam(value="categoryid") String strCategoryId) throws FusionRestException {
        try {
            int categoryId = StringUtil.toIntOrDefault(strCategoryId, -1);
            if (categoryId <= 0) {
                throw new FusionRestException(101, "Invalid category id: [" + categoryId + "]");
            }
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String[] usernames = userEJB.getUsersInUserCategory(categoryId);
            return new DataHolder<String[]>(usernames);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while retrieving user categories: " + e.getMessage()));
            throw new FusionRestException(101, "Internal error while retrieving users in category.");
        }
    }

    @POST
    @Path(value="/{userid}/blacklist")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> blacklistUser(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        int userId = this.getAndCheckUserid(userIdStr);
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            BooleanData data = new BooleanData(userEJB.blacklistUser(userId));
            return new DataHolder<BooleanData>(data);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while blacklisting user: " + e.getMessage()));
            BooleanData data = new BooleanData(false);
            return new DataHolder<BooleanData>(data);
        }
    }

    @DELETE
    @Path(value="/{userid}/blacklist")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> removeUserFromBlacklist(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        int userId = this.getAndCheckUserid(userIdStr);
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            BooleanData data = new BooleanData(userEJB.removeUserFromBlacklist(userId));
            return new DataHolder<BooleanData>(data);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while removing user from blacklist: " + e.getMessage()));
            BooleanData data = new BooleanData(false);
            return new DataHolder<BooleanData>(data);
        }
    }

    @POST
    @Path(value="/{userid}/ban")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> banUser(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        int userId = this.getAndCheckUserid(userIdStr);
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            BooleanData data = new BooleanData(userEJB.banUser(userId));
            return new DataHolder<BooleanData>(data);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while banning user: " + e.getMessage()));
            BooleanData data = new BooleanData(false);
            return new DataHolder<BooleanData>(data);
        }
    }

    @POST
    @Path(value="/{userid}/suspend")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> suspendUser(@PathParam(value="userid") String userIdStr, @QueryParam(value="duration") String durationStr) throws FusionRestException {
        int userId = this.getAndCheckUserid(userIdStr);
        int duration = StringUtil.toIntOrDefault(durationStr, -1);
        if (duration < 0) {
            throw new FusionRestException(101, String.format("Invalid duration parameter [%s]", durationStr));
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            BooleanData data = new BooleanData(userEJB.suspendUser(userId, duration));
            return new DataHolder<BooleanData>(data);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while suspending user: " + e.getMessage()));
            BooleanData data = new BooleanData(false);
            return new DataHolder<BooleanData>(data);
        }
    }

    private UserData getUserData(UserLocal userEJB, String useridOrUsername, boolean useUsername) throws FusionRestException {
        int userid = -1;
        String username = null;
        if (!useUsername && (userid = StringUtil.toIntOrDefault(useridOrUsername, -1)) == -1) {
            log.warn((Object)String.format("useUsername is false '%s', but userid '%s' is invalid, treating it as username anyway", useUsername, useridOrUsername));
            username = useridOrUsername;
        }
        UserData userData = null;
        if (userid == -1) {
            username = useridOrUsername;
            userData = userEJB.loadUserByUsernameOrAlias(username, false, false);
        } else {
            userData = userEJB.loadUserFromID(userid);
        }
        if (userData == null) {
            throw new FusionRestException(102, String.format("Unable to find user '%s'", useridOrUsername));
        }
        if (userid != -1) {
            if (!useridOrUsername.equalsIgnoreCase(userData.username)) {
                log.info((Object)String.format("user data loaded by alias '%s' for userid '%d'", useridOrUsername, userData.userID));
            } else {
                log.info((Object)String.format("user data loaded by username '%s' for userid '%d'", useridOrUsername, userData.userID));
            }
        } else {
            userid = userData.userID;
        }
        return userData;
    }

    @GET
    @Path(value="/{userid}/registrationcontext/campaign")
    @Produces(value={"application/json"})
    public DataHolder<String> getRegistrationContextCampaign(@PathParam(value="userid") String useridOrUsername, @QueryParam(value="useUsername") String useUsernameStr) throws FusionRestException {
        boolean useUsername = StringUtil.toBooleanOrDefault(useUsernameStr, false);
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = this.getUserData(userEJB, useridOrUsername, useUsername);
            if (userData == null) {
                throw new FusionRestException(102, String.format("Unable to find user '%s'", useridOrUsername));
            }
            RegistrationContextData regContextData = userEJB.getRegistrationContextData(userData.userID);
            return new DataHolder<String>(regContextData.campaign);
        }
        catch (CreateException e) {
            log.error((Object)String.format("Failed to retrieve registration context campaign for user '%s' due to EJB CreateException: %s", useridOrUsername, e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user registration context data");
        }
        catch (Exception e) {
            log.error((Object)String.format("Unexpected error during registration context campaign for user %s", useridOrUsername), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving user registration context data");
        }
    }

    @POST
    @Path(value="/{userid}/statusmessage")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public DataHolder<BooleanData> updateStatusMessage(@PathParam(value="userid") String userIdStr, @QueryParam(value="view") String ssoViewStr, DataHolder<StringData> dataholder) throws FusionRestException {
        int userId = this.getAndCheckUserid(userIdStr);
        if (dataholder == null || dataholder.data == null || ((StringData)dataholder.data).value == null) {
            throw new FusionRestException(101, "Invalid status message");
        }
        String statusMessage = ((StringData)dataholder.data).value;
        log.info((Object)String.format("updating status message of userid %s, msg '%s', view %s", userIdStr, statusMessage, ssoViewStr));
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userBean.getUsernameByUserid(userId, null);
            userBean.updateStatusMessage(userId, username, statusMessage, null, SSOEnums.View.fromString(ssoViewStr));
        }
        catch (CreateException e) {
            throw new FusionRestException(101, e.getMessage());
        }
        catch (EJBException e) {
            throw new FusionRestException(101, e.getMessage());
        }
        catch (Exception e) {
            throw new FusionRestException(101, e.getMessage());
        }
        return new DataHolder<BooleanData>(new BooleanData(true));
    }

    @POST
    @Path(value="")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public DataHolder<BooleanData> createUser(DataHolder<UserCreationData> dataholder) throws FusionRestException {
        String invitationToken;
        UserCreationData userCreationData = (UserCreationData)dataholder.data;
        if (log.isDebugEnabled()) {
            log.debug((Object)("createUser:UserCreationData:username=[" + userCreationData.username + "],invitationToken=[" + userCreationData.invitationToken + "]" + ",campaign=[" + userCreationData.campaign + "]" + ",userAgent=[" + userCreationData.userAgent + "]" + ",emailAddress=[" + userCreationData.emailAddress + "]" + ",fbid=[" + userCreationData.fbid + "]" + ",accessToken=[" + userCreationData.accessToken + "]" + ",countryISOCode=[" + userCreationData.countryISOCode + "]" + ",registrationIPAddress=[" + userCreationData.registrationIPAddress + "]" + ",registrationDevice=[" + userCreationData.registrationDevice + "]" + ",registrationToken=[" + userCreationData.registrationToken + "]" + ",registrationType=[" + userCreationData.registrationType + "]"));
        }
        Integer invitationID = null;
        if (InvitationUtils.isInvitationEngineEnabled(null) && !StringUtil.isBlank(invitationToken = userCreationData.invitationToken) && (invitationID = Integer.valueOf(InvitationUtils.decryptReferralInvitation(invitationToken))) < 0) {
            throw new FusionRestException(FusionRestException.RestException.INVALID_REFERRAL_TOKEN);
        }
        userCreationData.initializeToDefaultValues();
        UserData userData = userCreationData.getUserData();
        UserProfileData userProfileData = new UserProfileData();
        String registrationType = userCreationData.registrationType;
        String campaign = userCreationData.campaign;
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            RegistrationType registrationTypeEnum = RegistrationType.fromValue(registrationType);
            if (registrationTypeEnum == null) {
                throw new FusionRestException(101, "Registration failed:Please provide a registrationType(email1/email2/mobile)");
            }
            UserRegistrationContextData userRegContextData = new UserRegistrationContextData(campaign, true, registrationTypeEnum, invitationID);
            if (registrationTypeEnum == RegistrationType.EMAIL_REGISTRATION_PATH1 || registrationTypeEnum == RegistrationType.EMAIL_REGISTRATION_PATH2) {
                UserVerificationData userVerificationData = userBean.getVerificationDataFromToken(userCreationData.registrationToken);
                if (!StringUtil.isBlank(userCreationData.username) && !userCreationData.username.equals(userVerificationData.username)) {
                    log.error((Object)String.format("registration username passed in '%s' is different from that in userregistration table '%s'", userCreationData.username, userVerificationData.username));
                    throw new Exception("Token is not valid");
                }
                if (!StringUtil.isBlank(userCreationData.emailAddress) && !userCreationData.emailAddress.equals(userVerificationData.emailAddress)) {
                    log.error((Object)String.format("registration email passed in '%s' is different from that in userregistration table '%s'", userCreationData.emailAddress, userVerificationData.emailAddress));
                    throw new Exception("Token is not valid");
                }
                if (RegistrationType.fromValue(userVerificationData.registrationType) != registrationTypeEnum) {
                    log.error((Object)String.format("registration type passed in '%s' is different from that in userregistration table '%s'", registrationType, userVerificationData.registrationType));
                    throw new Exception("Token is not valid");
                }
                if (registrationTypeEnum == RegistrationType.EMAIL_REGISTRATION_PATH1 && !userBean.validateRegistrationToken(userVerificationData)) {
                    throw new Exception("Token is not valid");
                }
                if (registrationTypeEnum == RegistrationType.EMAIL_REGISTRATION_PATH2 && !userVerificationData.isVerified.booleanValue()) {
                    throw new FusionRestException(101, "Registration failed:Invalid or unverified token");
                }
                UserDataAndRegistrationContextData data = userBean.getUserDataFromUserRegistrationTable(userVerificationData.username, userData);
                if (registrationTypeEnum == RegistrationType.EMAIL_REGISTRATION_PATH1) {
                    userData = data.userData;
                }
                userRegContextData = data.regContextData.updateUserRegistrationContextData(userRegContextData);
            }
            AccountEntrySourceData accountEntrySourceData = new AccountEntrySourceData(userData.registrationIPAddress, null, userData.registrationDevice, userData.userAgent);
            userData = userBean.createUser(userData, userProfileData, true, userRegContextData, accountEntrySourceData);
            if (SystemProperty.getBool(SystemPropertyEntities.FacebookConnect.ENABLED)) {
                if (registrationTypeEnum == RegistrationType.FACEBOOK_CONNECT) {
                    int userId = userBean.getUserID(userCreationData.username, null);
                    if (userId != -1) {
                        this.updateFacebookDetails(userId, userCreationData.fbid, userCreationData.accessToken);
                    }
                } else {
                    log.error((Object)("Unable to find User ID for Username: " + userCreationData.username));
                }
            }
        }
        catch (CreateException e) {
            throw new FusionRestException(101, e.getMessage());
        }
        catch (EJBException e) {
            throw new FusionRestException(101, e.getMessage());
        }
        catch (Exception e) {
            throw new FusionRestException(101, e.getMessage());
        }
        return new DataHolder<BooleanData>(new BooleanData(true));
    }

    private void updateFacebookDetails(int userId, String facebookId, String accessToken) throws FusionRestException, JSONException {
        FacebookCredentialData credentialData = new FacebookCredentialData(facebookId, accessToken);
        if (ThirdPartySiteCredentialManager.updateCredential(userId, PasswordType.FACEBOOK_IM.value(), credentialData.toJSONString())) {
            EventQueue.enqueueSingleEvent(new ThirdPartySiteCredentialUpdatedEvent(userId));
        }
    }

    @POST
    @Path(value="/{userid}/contact/{contactUserid}")
    @Produces(value={"application/json"})
    public DataHolder<ContactData> addFusionContact(@PathParam(value="userid") String useridStr, @PathParam(value="contactUserid") String contactUseridStr, @QueryParam(value="followOnMiniblog") String followOnMiniblogStr) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid < 1) {
            throw new FusionRestException(101, String.format("Invalid userid provided: %s", useridStr));
        }
        int contactUserid = StringUtil.toIntOrDefault(contactUseridStr, -1);
        if (contactUserid < 1) {
            throw new FusionRestException(101, String.format("Invalid contactUserid provided: %s", contactUseridStr));
        }
        boolean followOnMiniblog = StringUtil.toBooleanOrDefault(followOnMiniblogStr, false);
        ContactData contactData = new ContactData();
        ContactData returnContactData = null;
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            ContactLocal contactEJB = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            String username = userEJB.getUsernameByUserid(userid, null);
            if (StringUtil.isBlank(username)) {
                throw new FusionRestException(101, String.format("Invalid userid provided: %s - user not found", useridStr));
            }
            String contactUsername = userEJB.getUsernameByUserid(contactUserid, null);
            if (StringUtil.isBlank(username)) {
                throw new FusionRestException(101, String.format("Invalid contactUserid provided: %s - user not found", contactUseridStr));
            }
            contactData.username = username;
            contactData.fusionUsername = contactUsername;
            contactData.displayOnPhone = true;
            returnContactData = SystemProperty.getBool(SystemPropertyEntities.Contacts.FOLLOW_ON_ADD_CONTACT_ENABLED) ? contactEJB.addFusionUserAsContact(userid, contactData, followOnMiniblog) : contactEJB.addPendingFusionContact(userid, contactData);
            if (returnContactData != null && returnContactData.id != null) {
                return new DataHolder<ContactData>(returnContactData);
            }
        }
        catch (Exception e) {
            log.error((Object)String.format("Exception caught while adding %s to contact list of %s - followOnMiniblog %s", contactUseridStr, useridStr, followOnMiniblog ? "true" : "false"), (Throwable)e);
        }
        throw new FusionRestException(101, "Internal system error - unable to fulfill add contact request");
    }

    @DELETE
    @Path(value="/{userid}/contact/{contactUserid}")
    @Produces(value={"application/json"})
    public Response removeFusionContact(@PathParam(value="userid") String useridStr, @PathParam(value="contactUserid") String contactUseridStr, @QueryParam(value="unfollowOnMiniblog") String unfollowOnMiniblogStr) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid < 1) {
            throw new FusionRestException(101, String.format("Invalid userid provided: %s", useridStr));
        }
        int contactUserid = StringUtil.toIntOrDefault(contactUseridStr, -1);
        if (contactUserid < 1) {
            throw new FusionRestException(101, String.format("Invalid contactUserid provided: %s", contactUseridStr));
        }
        boolean unfollowOnMiniblog = StringUtil.toBooleanOrDefault(unfollowOnMiniblogStr, false);
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            ContactLocal contactEJB = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            String username = userEJB.getUsernameByUserid(userid, null);
            if (StringUtil.isBlank(username)) {
                throw new FusionRestException(101, String.format("Invalid userid provided: %s - user not found", useridStr));
            }
            String contactUsername = userEJB.getUsernameByUserid(contactUserid, null);
            if (StringUtil.isBlank(username)) {
                throw new FusionRestException(101, String.format("Invalid contactUserid provided: %s - user not found", contactUseridStr));
            }
            ContactData contactData = contactEJB.getContact(username, contactUsername);
            if (contactData == null || contactData.id == null) {
                throw new FusionRestException(101, String.format("%s does not have %s on contact list", username, contactUsername));
            }
            if (SystemProperty.getBool(SystemPropertyEntities.Contacts.UNFOLLOW_ON_REMOVE_CONTACT_ENABLED)) {
                contactEJB.removeFusionUserFromContact(userid, username, contactData.id, unfollowOnMiniblog);
            } else {
                contactEJB.removeContact(userid, username, contactData.id);
            }
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (Exception e) {
            log.error((Object)String.format("Exception caught while remove %s to contact list of %s - unfollowOnMiniblog %s", contactUseridStr, useridStr, unfollowOnMiniblog ? "true" : "false"), (Throwable)e);
            throw new FusionRestException(101, "Internal system error - unable to fulfill remove contact request");
        }
    }

    private int getAndCheckUserid(String userIdStr) throws FusionRestException {
        int userId = StringUtil.toIntOrDefault(userIdStr, -1);
        if (userId == -1) {
            throw new FusionRestException(101, String.format("Invalid user ID [%s]", userIdStr));
        }
        return userId;
    }

    @GET
    @Path(value="/{userid}/payments")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public DataHolder<List<PaymentData>> getPendingPayments(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        int userId = this.getAndCheckUserid(userIdStr);
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userBean.getUsernameByUserid(userId, null);
            if (username == null) {
                throw new FusionRestException(FusionRestException.RestException.UNKNOWN_USER_ID, "Unknown user id [" + userIdStr + "]");
            }
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            List paymentTransactions = accountEJB.getPaymentTransactions(username, null, PaymentData.StatusEnum.PENDING.getEnumValue(), null);
            return new DataHolder<List<PaymentData>>(paymentTransactions);
        }
        catch (CreateException e) {
            log.error((Object)"Error in retrieving user transactions", (Throwable)e);
            throw new FusionRestException(101, "Unable to retrieve pending payments.");
        }
        catch (EJBException e) {
            log.error((Object)"Error in retrieving user transactions", (Throwable)e);
            throw new FusionRestException(101, e.getMessage());
        }
    }

    @POST
    @Path(value="/{userid}/balance/unfunded")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> updateUnfundedBalance(@PathParam(value="userid") String useridStr, DataHolder<UpdateUnfundedBalanceData> dataHolder) throws FusionRestException {
        block8: {
            int userid = StringUtil.toIntOrDefault(useridStr, -1);
            if (userid < 1) {
                throw new FusionRestException(101, String.format("Invalid userid provided: %s", useridStr));
            }
            try {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                String username = userEJB.getUsernameByUserid(userid, null);
                if (StringUtil.isBlank(username)) {
                    throw new FusionRestException(101, String.format("Invalid userid provided: %s - user not found", useridStr));
                }
                AccountEntryData accountEntry = new AccountEntryData();
                accountEntry.username = username;
                AccountEntryData.TypeEnum type = AccountEntryData.TypeEnum.fromValue(((UpdateUnfundedBalanceData)dataHolder.data).type);
                accountEntry.type = AccountEntryData.TypeEnum.fromValue(((UpdateUnfundedBalanceData)dataHolder.data).type);
                accountEntry.reference = ((UpdateUnfundedBalanceData)dataHolder.data).reference;
                accountEntry.description = ((UpdateUnfundedBalanceData)dataHolder.data).description;
                accountEntry.currency = ((UpdateUnfundedBalanceData)dataHolder.data).currency;
                accountEntry.amount = ((UpdateUnfundedBalanceData)dataHolder.data).amount;
                accountEntry.fundedAmount = 0.0;
                accountEntry.tax = 0.0;
                accountEntry.wholesaleCost = 0.0;
                AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                AccountEntrySourceData accountEntrySourceData = new AccountEntrySourceData(((UpdateUnfundedBalanceData)dataHolder.data).ipAddress, null, null, ((UpdateUnfundedBalanceData)dataHolder.data).userAgent);
                if (type == AccountEntryData.TypeEnum.BONUS_CREDIT) {
                    accountEJB.giveUnfundedCredits(username, ((UpdateUnfundedBalanceData)dataHolder.data).reference, ((UpdateUnfundedBalanceData)dataHolder.data).description, ((UpdateUnfundedBalanceData)dataHolder.data).amount, ((UpdateUnfundedBalanceData)dataHolder.data).currency, accountEntrySourceData);
                    break block8;
                }
                if (type == AccountEntryData.TypeEnum.DEDUCT_UNFUNDED_BALANCE) {
                    accountEJB.deductUnfundedCredits(username, ((UpdateUnfundedBalanceData)dataHolder.data).reference, ((UpdateUnfundedBalanceData)dataHolder.data).description, ((UpdateUnfundedBalanceData)dataHolder.data).amount, ((UpdateUnfundedBalanceData)dataHolder.data).currency, accountEntrySourceData);
                    break block8;
                }
                throw new FusionRestException(FusionRestException.RestException.INVALID_ACCOUNTENTRY_TYPE);
            }
            catch (CreateException e) {
                log.error((Object)"CreateException on updateUnfundedBalance while creating UserBean/AccountBean", (Throwable)e);
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
            }
            catch (EJBException e) {
                log.error((Object)"CreateException on updateUnfundedBalance", (Throwable)e);
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
            }
            catch (Exception e) {
                log.error((Object)"Unhandled exception on updateUnfundedBalance", (Throwable)e);
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
            }
        }
        return new DataHolder<BooleanData>(new BooleanData(true));
    }

    @GET
    @Path(value="/{userid}/invitation_response/{response}/{invitationToken}")
    @Produces(value={"application/json"})
    public DataHolder<InvitationDetailsData> logInvitationResponse(@PathParam(value="userid") String userIdStr, @PathParam(value="response") String responseStr, @PathParam(value="invitationToken") String invitationTokenCode, @QueryParam(value="fetchExtraParameters") String fetchExtraParametersStr) throws FusionRestException {
        if (!InvitationUtils.isInvitationEngineEnabled(null)) {
            throw new FusionRestException(FusionRestException.RestException.INVITATION_DISABLED);
        }
        int userId = this.getAndCheckUserid(userIdStr);
        try {
            InvitationData invitationData;
            Timestamp actionTime;
            UserLocal userBean;
            boolean fetchExtraParameters;
            block14: {
                InvitationResponseData.ResponseType invitationResponseType = InvitationResponseData.ResponseType.fromTypeCode(Integer.valueOf(responseStr));
                if (invitationResponseType == null || invitationResponseType == InvitationResponseData.ResponseType.SIGN_UP_UNVERIFIED || invitationResponseType == InvitationResponseData.ResponseType.SIGN_UP_VERIFIED) {
                    throw new FusionRestException(FusionRestException.RestException.INVALID_INVITATION_RESPONSE);
                }
                fetchExtraParameters = StringUtil.toBooleanOrDefault(fetchExtraParametersStr, false);
                userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                String username = userBean.getUsernameByUserid(userId, null);
                int invitationId = InvitationUtils.decryptReferralInvitation(invitationTokenCode);
                if (invitationId == -1) {
                    throw new FusionRestException(FusionRestException.RestException.INVALID_REFERRAL_TOKEN);
                }
                actionTime = new Timestamp(System.currentTimeMillis());
                invitationData = null;
                try {
                    invitationData = userBean.getInvitationData(invitationId, fetchExtraParameters, null);
                    InvitationUtils.getInvitationStatus(invitationData, actionTime);
                    if (invitationData != null) {
                        if (invitationData.status == InvitationData.StatusFieldValue.NO_RESPONSE) {
                            userBean.logInvitationResponse(null, actionTime, invitationData, invitationResponseType, username, InvitationData.StatusFieldValue.CLOSED);
                        } else if (invitationData.status == InvitationData.StatusFieldValue.EXPIRED) {
                            log.info((Object)String.format("Inivtation is already EXPIRED, invitationId:%s, expireTime:%s, responseTime:%s", invitationId, invitationData.expireTime, actionTime));
                        } else {
                            log.info((Object)String.format("Invitation is already CLOSED, Can only reponse to a invitation once, invitationId:%s, invitationStatus:%s", invitationId, invitationData.status));
                        }
                        break block14;
                    }
                    log.error((Object)String.format("Failed to retrieve invitationData for invitationID:%s", invitationId));
                    throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
                }
                catch (EJBException e) {
                    log.error((Object)String.format("Failed to response to invitation:%s, userid:%s, resonseType:%s, root cause:%s", invitationId, userId, invitationResponseType, e.getMessage()));
                }
            }
            return new DataHolder<InvitationDetailsData>(userBean.convertInvitationDataToInvitationDetailsData(invitationData, fetchExtraParameters, actionTime));
        }
        catch (CreateException e) {
            log.error((Object)"CreateException on getInvitationTokenData", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (EJBException e) {
            log.error((Object)"CreateException on getInvitationTokenData", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            log.error((Object)"Unhandled exception on getInvitationTokenData", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path(value="/{userid}/sent_invitation")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public DataHolder<Map<String, Integer>> sendInvitation(@PathParam(value="userid") String userIdStr, DataHolder<SendingInvitationData> dataHolder) throws FusionRestException {
        try {
            int userId = this.getAndCheckUserid(userIdStr);
            if (userId < 1) {
                throw new FusionRestException(FusionRestException.RestException.UNKNOWN_USER_ID, String.format("Invalid userid provided: %s", userIdStr));
            }
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userBean.loadUserFromID(userId);
            if (userData == null) {
                throw new FusionRestException(FusionRestException.RestException.UNKNOWN_USER_ID, String.format("Invalid userid provided: %s", userIdStr));
            }
            if (!InvitationUtils.isUserMobileOrEmailVerified(userData)) {
                throw new FusionRestException(FusionRestException.RestException.UNAUTHENTICATED_MOBILE_USER, String.format("You need to verify your account at %s before you can proceed.", SystemProperty.get(SystemPropertyEntities.Default.MIG33_WEB_BASE_URL)));
            }
            String username = null;
            username = userData.username;
            SendingInvitationData sendingInvitationData = (SendingInvitationData)dataHolder.data;
            InvitationData.ChannelType channelType = InvitationData.ChannelType.fromTypeCode(sendingInvitationData.channel);
            if (channelType == null) {
                throw new FusionRestException(FusionRestException.RestException.INVALID_REFERRAL_CHANNEL);
            }
            if (sendingInvitationData.destinations == null || sendingInvitationData.destinations.size() == 0) {
                throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "destinations are not empty or not specified");
            }
            InvitationData.ActivityType activityType = InvitationData.ActivityType.fromTypeCode(sendingInvitationData.type);
            if (activityType == null) {
                throw new FusionRestException(FusionRestException.RestException.INVALID_REFERRAL_ACTIVITY_TYPE);
            }
            if (channelType == InvitationData.ChannelType.EMAIL && SystemProperty.getBool(new SystemPropertyEntities.GuardsetEnabled(GuardCapabilityEnum.INVITATION_EMAIL_REFERRAL)) && !userBean.isUserInMigboAccessList(userId, MigboAccessMemberTypeEnum.WHITELIST.value(), GuardCapabilityEnum.INVITATION_EMAIL_REFERRAL.value())) {
                throw new FusionRestException(FusionRestException.RestException.INVITATION_DENIED);
            }
            if (channelType == InvitationData.ChannelType.FB && SystemProperty.getBool(new SystemPropertyEntities.GuardsetEnabled(GuardCapabilityEnum.FB_INVITATION_REFERRAL)) && !userBean.isUserInMigboAccessList(userId, MigboAccessMemberTypeEnum.WHITELIST.value(), GuardCapabilityEnum.FB_INVITATION_REFERRAL.value())) {
                throw new FusionRestException(FusionRestException.RestException.INVITATION_DENIED);
            }
            if (channelType == InvitationData.ChannelType.INTERNAL && SystemProperty.getBool(new SystemPropertyEntities.GuardsetEnabled(GuardCapabilityEnum.INTERNAL_INVITATION_REFERRAL)) && !userBean.isUserInMigboAccessList(userId, MigboAccessMemberTypeEnum.WHITELIST.value(), GuardCapabilityEnum.INTERNAL_INVITATION_REFERRAL.value())) {
                throw new FusionRestException(FusionRestException.RestException.INVITATION_DENIED);
            }
            InvitationUtils.deduplicateDestinations(sendingInvitationData);
            int needToExecuteCount = 0;
            try {
                String rateLimit = SystemProperty.get(SystemPropertyEntities.Invitation.REFERRAL_RATE_LIMIT);
                if (channelType == InvitationData.ChannelType.FB) {
                    rateLimit = SystemProperty.get(SystemPropertyEntities.Invitation.FACEBOOK_REFERRAL_RATE_LIMIT);
                } else if (channelType == InvitationData.ChannelType.INTERNAL) {
                    rateLimit = SystemProperty.get(SystemPropertyEntities.Invitation.INTERNAL_REFERRAL_RATE_LIMIT);
                }
                for (int i = 0; i < sendingInvitationData.destinations.size(); ++i) {
                    MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.USER_REFERRAL.toString(), MemCachedKeyUtils.getFullKeyFromStrings(activityType.toString(), userIdStr), rateLimit);
                    ++needToExecuteCount;
                }
            }
            catch (MemCachedRateLimiter.LimitExceeded e) {
                log.warn((Object)String.format("user:%s has reached the number of referrals he can send today", userId));
                if (needToExecuteCount == 0) {
                    throw new FusionRestException(FusionRestException.RestException.REACH_REFERRAL_RATE_LIMIT);
                }
            }
            catch (MemCachedRateLimiter.FormatError e) {
                log.error((Object)"Formatting error in rate limiter expression", (Throwable)e);
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
            }
            sendingInvitationData.destinations = sendingInvitationData.destinations.subList(0, needToExecuteCount);
            log.info((Object)String.format("Creating invitation for User:%s at Channel:%s - Type:%s", userIdStr, channelType, activityType));
            Map<String, Integer> result = userBean.createInvitation(userId, (SendingInvitationData)dataHolder.data, null).getSendInvitationStatusSummary();
            return new DataHolder<Map<String, Integer>>(result);
        }
        catch (CreateException e) {
            log.error((Object)"Error in retrieving user transactions", (Throwable)e);
            throw new FusionRestException(101, "Unable to retrieve pending payments.");
        }
        catch (EJBException e) {
            log.error((Object)("EJBException in sendInvitation: " + e.getMessage()));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (FusionRestException e) {
            throw e;
        }
        catch (Exception e) {
            log.error((Object)"Unhandled exception", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path(value="/{userid}/stickers")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public DataHolder<List<EmoticonData>> getStickersForUser(@PathParam(value="userid") String userIdStr, @QueryParam(value="limit") String limitStr, @QueryParam(value="offset") String offsetStr) throws FusionRestException {
        int userId = this.getAndCheckUserid(userIdStr);
        int offset = StringUtil.toIntOrDefault(offsetStr, 0);
        int limit = StringUtil.toIntOrDefault(limitStr, 33);
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userBean.getUsernameByUserid(userId, null);
            if (username == null) {
                throw new FusionRestException(FusionRestException.RestException.UNKNOWN_USER_ID, "Unknown user id [" + userIdStr + "]");
            }
            ContentLocal contentEJB = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            List emoticons = contentEJB.getStickerDataListForUser(username);
            if (offset > emoticons.size()) {
                return new DataHolder<List<EmoticonData>>();
            }
            if (offset + limit > emoticons.size()) {
                limit = emoticons.size() - offset;
            }
            return new DataHolder<List<EmoticonData>>(emoticons.subList(offset, offset + limit));
        }
        catch (CreateException e) {
            log.error((Object)"Error in retrieving user stickers", (Throwable)e);
            throw new FusionRestException(101, "Unable to retrieve user stickers.");
        }
        catch (EJBException e) {
            log.error((Object)"Error in retrieving user stickers", (Throwable)e);
            throw new FusionRestException(101, e.getMessage());
        }
    }

    @GET
    @Path(value="/{userid}/ip")
    @Produces(value={"application/json"})
    public DataHolder<Set<String>> getIpAddress(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        int userId = this.getAndCheckUserid(userIdStr);
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userBean.getUsernameByUserid(userId, null);
            if (username == null) {
                log.error((Object)("Error in retrieving username to get IP for user ID: " + userIdStr));
                throw new FusionRestException(101, "Unable to retrieve user IP");
            }
            UserPrx userPrx = EJBIcePrxFinder.findUserPrx(username);
            if (userPrx == null) {
                return new DataHolder<Set<String>>(new HashSet(0));
            }
            SessionPrx[] sessions = userPrx.getSessions();
            HashSet<String> ipAddresses = new HashSet<String>(sessions.length);
            for (int i = 0; i < sessions.length; ++i) {
                ipAddresses.add(sessions[i].getRemoteIPAddress());
            }
            return new DataHolder<Set<String>>(ipAddresses);
        }
        catch (Exception e) {
            log.warn((Object)"Error in retrieving user IP", (Throwable)e);
            throw new FusionRestException(101, "Unable to retrieve user IP");
        }
    }

    @POST
    @Path(value="/{userid}/disconnect")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> disconnectUser(@PathParam(value="userid") String userIdStr) throws FusionRestException {
        int userId = this.getAndCheckUserid(userIdStr);
        try {
            UserPrx userPrx;
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userBean.getUsernameByUserid(userId, null);
            if (username == null) {
                log.error((Object)("Error in retrieving username to disconnect user ID: " + userIdStr));
                throw new FusionRestException(101, "Unable to disconnect user");
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("disconnect user:%s", userId));
            }
            if (SystemProperty.getBool(SystemPropertyEntities.Default.CAPTCHA_REQUIRED_ENABLED)) {
                MemCachedClientWrapper.add(MemCachedKeySpaces.CommonKeySpace.CAPTCHA_REQUIRED, StringUtil.normalizeUsername(username), 1);
            }
            if ((userPrx = EJBIcePrxFinder.findUserPrx(username)) == null) {
                return new DataHolder<BooleanData>(new BooleanData(true));
            }
            userPrx.disconnect(SystemProperty.get(SystemPropertyEntities.Default.REASON_FOR_DISCONNECTING_SPAMMER));
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.warn((Object)String.format("Error in disconnecting user:%s", userId), (Throwable)e);
            throw new FusionRestException(101, "Unable to disconnect user");
        }
    }

    @GET
    @Path(value="/{username}/store/received/")
    @Produces(value={"application/json"})
    public DataHolder<? extends Serializable> getStoreItemsReceived(@QueryParam(value="sessionId") String sessionId, @PathParam(value="username") String username, @QueryParam(value="type") int type, @QueryParam(value="offset") int offset, @QueryParam(value="limit") int limit) throws FusionRestException {
        ConnectionPrx prx = ResourceUtil.getConnectionProxy(sessionId);
        UserData userData = new UserData(prx.getUserObject().getUserData());
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            switch (StoreItemData.TypeEnum.fromValue(type)) {
                case VIRTUAL_GIFT: {
                    return new DataHolder<ListDataWrapper>(contentBean.getVirtualGiftsReceived(userData.username, username, offset, limit));
                }
            }
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Type not supported.");
        }
        catch (FusionRestException fre) {
            throw fre;
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to retrieve store item received for: %s", userData.username), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @GET
    @Path(value="/{username}/store/received/{itemid}")
    @Produces(value={"application/json"})
    public DataHolder<? extends Serializable> getStoreItemReceived(@QueryParam(value="sessionId") String sessionId, @PathParam(value="username") String username, @QueryParam(value="type") int type, @PathParam(value="itemid") int itemid) throws FusionRestException {
        ConnectionPrx prx = ResourceUtil.getConnectionProxy(sessionId);
        UserData userData = new UserData(prx.getUserObject().getUserData());
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            switch (StoreItemData.TypeEnum.fromValue(type)) {
                case VIRTUAL_GIFT: {
                    return new DataHolder<VirtualGiftReceivedData>(contentBean.getVirtualGiftReceived(userData.username, username, itemid));
                }
            }
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Type not supported.");
        }
        catch (FusionRestException fre) {
            throw fre;
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to retrieve store item received id: %d", type), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @GET
    @Path(value="/{username}/store/inventory/")
    @Produces(value={"application/json"})
    public DataHolder<List<StoreItemInventoryData>> getStoreItemsInventoryByType(@PathParam(value="username") String username, @QueryParam(value="type") int type) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            int requestingId = userBean.getUserID(username, null);
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            return new DataHolder<List<StoreItemInventoryData>>(contentBean.getStoreItemsInventoryByType(requestingId, StoreItemData.TypeEnum.fromValue(type)));
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to retrieve store item received type: %d", type), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @GET
    @Path(value="/{username}/store/inventory/{storeitemid}")
    @Produces(value={"application/json"})
    public DataHolder<StoreItemInventoryData> getStoreItemInventory(@PathParam(value="username") String username, @PathParam(value="storeitemid") int storeitemid) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            int requestingId = userBean.getUserID(username, null);
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            return new DataHolder<StoreItemInventoryData>(contentBean.getStoreItemInventory(requestingId, storeitemid));
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to retrieve store item id: %d", storeitemid), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @POST
    @Path(value="/{username}/store/inventory/{storeitemid}/give")
    @Produces(value={"application/json"})
    public DataHolder<StoreItemData> giveInventory(@QueryParam(value="sessionId") String sessionId, @QueryParam(value="requestingUserid") int requestingUserId, @PathParam(value="storeitemid") int storeitemid, String jsonStr) throws FusionRestException {
        ConnectionPrx prx = ResourceUtil.getConnectionProxy(sessionId);
        UserData userData = new UserData(prx.getUserObject().getUserData());
        if (!userData.userID.equals(requestingUserId)) {
            log.error((Object)String.format("User session id for %s is not the same with user %s", userData.username, requestingUserId));
            throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("Invalid session id for user %s", requestingUserId));
        }
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            StoreItemData sid = contentBean.getStoreItem(userData.username, storeitemid);
            if (sid == null) {
                log.error((Object)String.format("Failed to retrieved store item id for %d", storeitemid));
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to find the store item id for " + storeitemid);
            }
            switch (sid.type) {
                case VIRTUAL_GIFT: {
                    JSONObject json = new JSONObject(jsonStr);
                    String[] recepients = !StringUtil.isBlank(json.getString("to")) ? json.getString("to").split(",") : null;
                    String message = json.getString("message");
                    boolean privateGifts = json.getBoolean("private");
                    VirtualGiftData vgd = (VirtualGiftData)sid.referenceData;
                    Map virtualGiftReceivedIdMap = contentBean.giveVirtualGiftForMultipleUsers(userData.username, Arrays.asList(recepients), sid, VirtualGiftReceivedData.PurchaseLocationEnum.STORE.value(), privateGifts, message);
                    UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                    HashMap recipientUserDataList = new HashMap();
                    ClientType deviceEnum = ClientType.fromValue(prx.getDeviceTypeAsInt());
                    String receiverStr = "";
                    for (Map.Entry entry : virtualGiftReceivedIdMap.entrySet()) {
                        String recipient = (String)entry.getKey();
                        UserData recipientUserData = userBean.loadUser(recipient, false, false);
                        if (recipientUserData == null) continue;
                        receiverStr = receiverStr + " @" + recipient;
                        recipientUserDataList.put(entry.getValue(), recipientUserData);
                    }
                    if (json.getBoolean("postToMiniblog") && virtualGiftReceivedIdMap.keySet().size() != 0) {
                        String msg = SystemProperty.getBool(SystemPropertyEntities.Temp.WW422_ENABLED) && !StringUtil.isBlank(message) ? String.format(SystemProperty.get(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_POST_MESSAGE_CUSTOM), vgd.getName(), receiverStr, vgd.getGiftUrl(VirtualGiftData.ImageFormatType.PNG, (short)SystemProperty.getInt(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_RESOLUTIONS_DEFAULT)), message) : (virtualGiftReceivedIdMap.keySet().size() == 1 ? String.format(SystemProperty.get(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_POST_MESSAGE), vgd.getName(), receiverStr, vgd.getGiftUrl(VirtualGiftData.ImageFormatType.PNG, (short)SystemProperty.getInt(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_RESOLUTIONS_DEFAULT))) : String.format(SystemProperty.get(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_POST_MESSAGES), vgd.getName(), receiverStr, vgd.getGiftUrl(VirtualGiftData.ImageFormatType.PNG, (short)SystemProperty.getInt(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_RESOLUTIONS_DEFAULT))));
                        contentBean.createMigboTextPostForUser(userData.userID, msg, null, null, "1", deviceEnum, SSOEnums.View.fromFusionDeviceEnum(deviceEnum));
                    }
                    contentBean.onPurchaseVirtualGift(userData.username, recipientUserDataList, vgd, privateGifts, message, null, true);
                    break;
                }
            }
            return new DataHolder<StoreItemData>(sid);
        }
        catch (FusionRestException fre) {
            throw fre;
        }
        catch (Exception e) {
            log.error((Object)"Failed to purchase store item.", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @GET
    @Path(value="/{username}/merchant")
    @Produces(value={"application/json"})
    public DataHolder<List<MerchantLocationData>> getMerchantById(@QueryParam(value="requestingUserid") int requestingUserid, @PathParam(value="username") String username, @QueryParam(value="offset") int offset, @QueryParam(value="limit") int limit) throws FusionRestException {
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            int userId = userEJB.getUserID(username, null);
            UserData userData = userEJB.loadUserFromID(userId);
            MerchantsLocal merchantBean = (MerchantsLocal)EJBHomeCache.getLocalObject("MerchantsLocal", MerchantsLocalHome.class);
            List results = merchantBean.getMerchantsByCountry(requestingUserid, userData.countryID, offset, limit, true);
            return new DataHolder<List<MerchantLocationData>>(results);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while getting merchant location " + e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal Server Error: Could not fetch merchant location");
        }
    }

    @POST
    @Path(value="/{userid}/file/upload")
    @Consumes(value={"image/jpeg", "image/gif", "image/png"})
    @Produces(value={"application/json"})
    public DataHolder<FileUploadData> uploadUserFile(@PathParam(value="userid") String userIdStr, @QueryParam(value="requestingUserid") int requestingUserid, @HeaderParam(value="Content-Type") MediaType contentType, @HeaderParam(value="Content-Length") int fileSize, InputStream is) throws FusionRestException {
        int userId = this.getAndCheckUserid(userIdStr);
        if (fileSize > SystemProperty.getInt(SystemPropertyEntities.S3UploaderSettings.MAX_UPLOAD_FILE_SIZE)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to upload file. Max upload file size exceeded.");
        }
        try {
            S3UploaderContentTypeEnum s3ContentType;
            BufferedImage image;
            if (log.isDebugEnabled()) {
                log.debug((Object)("user file upload content type: " + contentType + "; file size: " + fileSize));
            }
            if (null == (image = ImageIO.read(is))) {
                log.error((Object)String.format("Error in reading image from input stream for user:%s", userId));
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to upload file. Please try again later.");
            }
            String photoId = Long.toString(System.currentTimeMillis());
            String s3Key = S3Uploader.calculatePath((String)userIdStr, (String)photoId);
            String url = S3Uploader.uploadBufferedImage((S3UploaderConfiguration)new FusionS3UploaderConfiguration(), (String)s3Key, (S3UploaderContentTypeEnum)(s3ContentType = S3UploaderContentTypeEnum.fromMimeType((String)contentType.toString())), (BufferedImage)image);
            if (StringUtil.isBlank(url)) {
                log.error((Object)String.format("Error in uploading file to s3 for user:%s, empty URL!", userId));
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to upload file. Please try again later.");
            }
            FileUploadData uploadData = new FileUploadData(url);
            String thumbnailUrl = null;
            int thumbnailWidth = SystemProperty.getInt(SystemPropertyEntities.MimeDataSettings.IMAGE_THUMBNAIL_WIDTH);
            if (image.getWidth() <= thumbnailWidth) {
                thumbnailUrl = url;
            } else {
                BufferedImage thumbnail = Scalr.resize((BufferedImage)image, (int)thumbnailWidth, (BufferedImageOp[])new BufferedImageOp[0]);
                if (null != thumbnail) {
                    thumbnailUrl = S3Uploader.uploadBufferedImage((S3UploaderConfiguration)new FusionS3UploaderConfiguration(), (String)String.format("%s_%dx", s3Key, thumbnailWidth), (S3UploaderContentTypeEnum)s3ContentType, (BufferedImage)thumbnail);
                }
            }
            if (!StringUtil.isBlank(thumbnailUrl)) {
                uploadData.thumbnailUrl = thumbnailUrl;
            } else {
                log.error((Object)String.format("Error in generating or uploading image thumbnail to s3 for user:%s", userId));
            }
            return new DataHolder<FileUploadData>(uploadData);
        }
        catch (FusionRestException e) {
            throw e;
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in uploading file to s3 for user:%s", userId), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to upload file. Please try again later.");
        }
    }

    @GET
    @Path(value="/test/DAO/userdata")
    @Produces(value={"application/json"})
    public DataHolder<UserData> testDAOUserData(@QueryParam(value="username") String username, @QueryParam(value="fullUserData") String fullUserDataStr) throws FusionRestException {
        try {
            UserObject user = new UserObject(username);
            log.info((Object)("DAO -> BCL:" + user.getBroadcastList()));
            log.info((Object)("DAO -> contact list:" + user.getContactList()));
            log.info((Object)("DAO -> group list:" + user.getGroupList()));
            log.info((Object)("DAO -> usersetting:" + user.getUserSettings()));
            log.info((Object)("DAO -> user id:" + user.getUserID()));
            log.info((Object)("DAO -> ReputationScoreAndLevel:" + user.getReputationScoreAndLevel()));
            log.info((Object)("DAO -> ReputationLevel:" + user.getReputationLevel()));
            log.info((Object)("DAO -> BasicMerchantDetails:" + user.getBasicMerchantDetails()));
            log.info((Object)("DAO -> AccountBalance:" + user.getAccountBalance()));
            log.info((Object)("DAO -> GroupMember:" + user.getGroupMember(400393)));
            log.info((Object)("DAO -> Emoticon IDS:" + user.getEmoticonPacks()));
            log.info((Object)("DAO -> Emoticons:" + user.getEmoticons()));
            log.info((Object)("DAO -> All Emoticons:" + DAOFactory.getInstance().getEmoAndStickerDAO().loadEmoticons()));
            log.info((Object)("DAO -> All Emoticon packs:" + DAOFactory.getInstance().getEmoAndStickerDAO().loadEmoticonPacks()));
            log.info((Object)("DAO -> getEmoticonPack 1:" + DAOFactory.getInstance().getEmoAndStickerDAO().getEmoticonPack(1)));
            log.info((Object)("DAO -> get GroupData for groupd:400393:" + DAOFactory.getInstance().getGroupDAO().getGroup(400393)));
            log.info((Object)("DAO -> get group chat rooms for group:400393:" + DAOFactory.getInstance().getChatRoomDAO().getGroupChatRooms(400393)));
            log.info((Object)("DAO -> get isUserBlackListedInGroup for group:400393:" + user.isUserBlackListedInGroup(400393)));
            log.info((Object)("DAO -> get ModeratorUserNames for group:400393:" + DAOFactory.getInstance().getGroupDAO().getModeratorUserNames(400393, false)));
            log.info((Object)("DAO -> latest msg:" + DAOFactory.getInstance().getMessageDAO().getLatestAlertMessage(5, AlertMessageData.TypeEnum.CHAT_ROOM_WELCOME_MESSAGE, 119, new Date(), null, 7)));
            log.info((Object)("System Properties: " + SystemProperty.getAllSystemProperties()));
            log.info((Object)("DAO -> get ChatroomNamesPerCategory:" + DAOFactory.getInstance().getChatRoomDAO().getChatroomNamesPerCategory(true)));
            log.info((Object)("DAO -> get Chatrooms:" + DAOFactory.getInstance().getChatRoomDAO().getChatRooms(199, "chat")));
            log.info((Object)("DAO -> get Chatrooms:" + DAOFactory.getInstance().getChatRoomDAO().getChatRooms(199, "test")));
            log.info((Object)("DAO -> get FavouriteChatRooms:" + user.getFavouriteChatRooms()));
            log.info((Object)("DAO -> get RecentChatRooms:" + user.getRecentChatRooms()));
            log.info((Object)("DAO -> get chat room data:clickers:" + DAOFactory.getInstance().getChatRoomDAO().getChatRoom("clickers")));
            log.info((Object)("DAO -> get bot data:1:" + DAOFactory.getInstance().getBotDAO().getBot(1)));
            log.info((Object)("DAO -> gloadEmoticonHeights:" + DAOFactory.getInstance().getEmoAndStickerDAO().loadEmoticonHeights()));
            log.info((Object)("DAO -> get opt height for height: 50 :" + DAOFactory.getInstance().getEmoAndStickerDAO().getOptimalEmoticonHeight(50)));
            log.info((Object)("DAO -> load info text :" + DAOFactory.getInstance().getMessageDAO().loadInfoTexts()));
            log.info((Object)("DAO -> load help text :" + DAOFactory.getInstance().getMessageDAO().loadHelpTexts()));
            log.info((Object)("DAO -> load info text for 1 :" + DAOFactory.getInstance().getMessageDAO().getInfoText(1)));
            return new DataHolder<UserData>(user.getUserData(StringUtil.toBooleanOrDefault(fullUserDataStr, false), false));
        }
        catch (Exception e) {
            log.error((Object)"Failed to retrieve DAO data", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @GET
    @Path(value="/test/DAO/guardset")
    @Produces(value={"application/json"})
    public DataHolder<Short> testDAOGuardset(@QueryParam(value="clientType") String clientTypeStr, @QueryParam(value="guardCapability") String guardCapabilityStr) throws FusionRestException {
        try {
            int clientType = Integer.parseInt(clientTypeStr);
            int guardCapability = Integer.parseInt(guardCapabilityStr);
            return new DataHolder<Short>(DAOFactory.getInstance().getGuardsetDAO().getMinimumClientVersionForAccess(clientType, guardCapability));
        }
        catch (Exception e) {
            log.error((Object)"Failed to retrieve DAO data", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
    }

    @POST
    @Path(value="/{username}/changepassword")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> changePassword(@PathParam(value="username") String username, @QueryParam(value="oldpassword") String oldPassword, @QueryParam(value="newpassword") String newPassword) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.changePassword(username, oldPassword, newPassword);
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in changepassword username:%s", username), (Throwable)e);
            throw new FusionRestException(101, "Unable to changepassword");
        }
    }

    @POST
    @Path(value="/{username}/changemobilephone")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> changeMobilePhone(@PathParam(value="username") String username, @QueryParam(value="mobilenumber") String mobileNumber, @QueryParam(value="ipaddress") String ipAddress, @QueryParam(value="sessionid") String sessionID, @QueryParam(value="mobiledevice") String mobileDevice, @QueryParam(value="useragent") String userAgent) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.changeMobilePhone(username, mobileNumber, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in changemobilephone username:%s", username), (Throwable)e);
            throw new FusionRestException(101, "Unable to changemobilephone");
        }
    }

    @POST
    @Path(value="/{username}/updateuserdisplaypicture")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> updateUserDisplayPicture(@PathParam(value="username") String username, @QueryParam(value="displaypictureid") String displayPictureId) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.updateDisplayPicture(username, displayPictureId);
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in updateuserdisplaypicture username:%s", username), (Throwable)e);
            throw new FusionRestException(101, "Unable to updateuserdisplaypicture");
        }
    }

    @POST
    @Path(value="/{username}/updateuserdetailsice")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> updateUserDetailsIce(@PathParam(value="username") String username) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userBean.loadUser(username, false, true);
            UserPrx userPrx = EJBIcePrxFinder.findUserPrx(userData.username);
            userPrx.userDetailChanged(userData.toIceObject());
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in updateuserdetailsice username:%s", username), (Throwable)e);
            throw new FusionRestException(101, "Unable to updateuserdetailsice");
        }
    }

    @GET
    @Path(value="/{username}/getuserownedchatrooms")
    @Produces(value={"application/json"})
    public DataHolder<Hashtable> getUserOwnedChatrooms(@PathParam(value="username") String username, @QueryParam(value="page") int page, @QueryParam(value="numentries") int numEntries) throws FusionRestException {
        try {
            Web webBean = (Web)EJBHomeCache.getObject("ejb/Web", WebHome.class);
            return new DataHolder<Hashtable>(webBean.getUserOwnedChatrooms(username, page, numEntries));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in getuserownedchatrooms username:%s", username), (Throwable)e);
            throw new FusionRestException(101, "Unable to getuserownedchatrooms");
        }
    }

    private static class FusionS3UploaderConfiguration
    implements S3UploaderConfiguration {
        private FusionS3UploaderConfiguration() {
        }

        public String getAccessKey() {
            return SystemProperty.get(SystemPropertyEntities.S3UploaderSettings.ACCESS_KEY);
        }

        public String getSecretKey() {
            return SystemProperty.get(SystemPropertyEntities.S3UploaderSettings.SECRET_KEY);
        }

        public String getS3BaseDomain() {
            return SystemProperty.get(SystemPropertyEntities.S3UploaderSettings.S3_BASE_DOMAIN);
        }

        public String getPhotoBucketName() {
            return SystemProperty.get(SystemPropertyEntities.S3UploaderSettings.BUCKET_NAME);
        }

        public int getMaxAgeForCache() {
            return SystemProperty.getInt(SystemPropertyEntities.S3UploaderSettings.MAX_AGE_FOR_CACHE);
        }

        public boolean useCdnDomain() {
            return SystemProperty.getBool(SystemPropertyEntities.S3UploaderSettings.USE_CDN_DOMAIN);
        }

        public String getCdnDomain() {
            return SystemProperty.get(SystemPropertyEntities.S3UploaderSettings.CDN_DOMAIN);
        }

        public int getAWSRefresh() {
            return SystemProperty.getInt(SystemPropertyEntities.S3UploaderSettings.AWS_REFRESH);
        }

        public int getMaxBucketFailures() {
            return SystemProperty.getInt(SystemPropertyEntities.S3UploaderSettings.MAX_BUCKET_FAILURES);
        }
    }
}

