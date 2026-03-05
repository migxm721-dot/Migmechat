/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.MigboEnums;
import com.projectgoth.fusion.common.MiniblogDailyDigestHelper;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.metrics.MetricsEnums;
import com.projectgoth.fusion.common.metrics.MetricsLogger;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.interfaces.ContactLocal;
import com.projectgoth.fusion.interfaces.ContactLocalHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.interfaces.Web;
import com.projectgoth.fusion.interfaces.WebHome;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.EventFollowingRequestData;
import com.projectgoth.fusion.restapi.data.EventNewPostData;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.GroupActivityRewardProgramTriggerData;
import com.projectgoth.fusion.restapi.data.ThirdPartyAppActivityRewardProgramTriggerData;
import com.projectgoth.fusion.rewardsystem.RewardCentre;
import com.projectgoth.fusion.rewardsystem.triggers.GroupActivityTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.MigboCampaignTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.MigboFollowedByEventTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.MigboFollowingEventTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.MigboPostCreatedTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.MigboPostEventTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.MutuallyFollowingEventTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RelationshipEventTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.ThirdPartyAppInternalInvitationTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.ThirdPartyAppStartEventTrigger;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.CreateException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Provider
@Path(value="/event")
public class EventResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(EventResource.class));
    private UserNotificationServicePrx unsProxy = null;

    public void setUserNotificationServicePrx(UserNotificationServicePrx proxy) {
        this.unsProxy = proxy;
    }

    public UserNotificationServicePrx getUserNotificationServicePrx() {
        if (this.unsProxy == null) {
            this.unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
        } else {
            try {
                this.unsProxy.ice_ping();
            }
            catch (Exception e) {
                this.unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
            }
        }
        return this.unsProxy;
    }

    @POST
    @Path(value="/newpost/{userid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response createNewPostEvent(@PathParam(value="userid") String useridStr, @QueryParam(value="fireRewardProgramTrigger") boolean fireRewardProgramTrigger, @QueryParam(value="shareToThirdParty") String shareToThirdParty, EventNewPostData data) throws FusionRestException {
        User userBean;
        int userid;
        block22: {
            MigboEnums.MigboPostTypeEnum type;
            log.info((Object)String.format("received new migbo post event, userid=%s, fullPostId=%s, parentFullPostid=%s, originality=%s application=%s type=%s hashtags=%s links=%s mentions=%s fireRewardProgramTrigger=%b shareToThirdParty=%s", useridStr, data.fullPostid, data.parentFullPostid, data.originality, data.application, data.type, data.hashtags, data.links, data.mentions, fireRewardProgramTrigger, shareToThirdParty));
            userid = StringUtil.toIntOrDefault(useridStr, -1);
            if (userid == -1) {
                log.error((Object)String.format("Invalid userid '%s' specified", useridStr));
                throw new FusionRestException(-1, String.format("Invalid userid '%s' specified", useridStr));
            }
            MigboEnums.MigboPostOriginalityEnum originality = MigboEnums.MigboPostOriginalityEnum.fromType(StringUtil.toIntOrDefault(data.originality, -1));
            if (originality == null) {
                log.error((Object)String.format("Invalid orinality '%s' specified", data.originality));
                throw new FusionRestException(-1, String.format("Invalid originality '%s' specified", data.originality));
            }
            MigboEnums.PostApplicationEnum application = MigboEnums.PostApplicationEnum.fromValue(StringUtil.toIntOrDefault(data.application, 0));
            if (application == null) {
                application = MigboEnums.PostApplicationEnum.WEB;
            }
            if ((type = MigboEnums.MigboPostTypeEnum.fromValue(StringUtil.toIntOrDefault(data.type, 0))) == null) {
                type = MigboEnums.MigboPostTypeEnum.TEXT;
            }
            List<Object> hashtags = data.hashtags == null ? new LinkedList() : data.hashtags;
            List<Object> links = data.links == null ? new LinkedList() : data.links;
            userBean = null;
            try {
                userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            }
            catch (CreateException ce) {
                log.error((Object)String.format("Unable to initialize UserBean for the new post created event, userid %s, full post id %s", useridStr, data.fullPostid), (Throwable)ce);
                throw new FusionRestException(101, String.format("Internal error: unable to initialize UserBean", new Object[0]));
            }
            if (fireRewardProgramTrigger) {
                try {
                    if (application != MigboEnums.PostApplicationEnum.SYSTEM) {
                        UserData authorUserData = userBean.loadUserFromID(userid);
                        MigboPostCreatedTrigger trigger = new MigboPostCreatedTrigger(authorUserData);
                        trigger.amountDelta = 0.0;
                        trigger.quantityDelta = 1;
                        trigger.postOriginality = originality;
                        trigger.postType = type;
                        trigger.application = application;
                        trigger.hashtags = hashtags;
                        trigger.links = links;
                        trigger.parentPostID = data.parentFullPostid;
                        trigger.shareToThirdParty = EnumSet.noneOf(Enums.ThirdPartyEnum.class);
                        if (!StringUtil.isBlank(shareToThirdParty)) {
                            String[] shareToThirdPartyArr;
                            for (String string : shareToThirdPartyArr = shareToThirdParty.split(",")) {
                                trigger.shareToThirdParty.add(Enums.ThirdPartyEnum.valueOf(string));
                            }
                        }
                        RewardCentre.getInstance().sendTrigger(trigger);
                        if (originality == MigboEnums.MigboPostOriginalityEnum.REPLY || originality == MigboEnums.MigboPostOriginalityEnum.RESHARE) {
                            MigboPostEventTrigger.PostEventTypeEnum eventType;
                            int originalAuthorUserid = StringUtil.toIntOrDefault(data.parentFullPostid.split("-")[0], -1);
                            UserData originalAuthorUserData = userBean.loadUserFromID(originalAuthorUserid);
                            MigboPostEventTrigger.PostEventTypeEnum postEventTypeEnum = eventType = originality == MigboEnums.MigboPostOriginalityEnum.REPLY ? MigboPostEventTrigger.PostEventTypeEnum.REPLIED_TO : MigboPostEventTrigger.PostEventTypeEnum.RESHARED;
                            if (originalAuthorUserid == -1) {
                                log.warn((Object)("Unable to determine original post author userid [" + data.parentFullPostid + "]"));
                            } else {
                                MigboPostEventTrigger postEventTrigger = new MigboPostEventTrigger(originalAuthorUserData, data.parentFullPostid, eventType);
                                postEventTrigger.amountDelta = 0.0;
                                postEventTrigger.quantityDelta = 1;
                                postEventTrigger.shareToThirdParty = trigger.shareToThirdParty;
                                RewardCentre.getInstance().sendTrigger(postEventTrigger);
                            }
                        }
                    }
                }
                catch (Exception e) {
                    log.error((Object)String.format("Unable to notify RewardCentre about the new post created event, userid %s, full post id %s", useridStr, data.fullPostid), (Throwable)e);
                    throw new FusionRestException(101, String.format("Internal error: unable to notify the new post created event", new Object[0]));
                }
            }
            if (originality == MigboEnums.MigboPostOriginalityEnum.REPLY) {
                try {
                    int originalAuthorUserid = StringUtil.toIntOrDefault(data.parentFullPostid.split("-")[0], -1);
                    if (originalAuthorUserid == -1) {
                        throw new Exception(String.format("Unable to determine original author's userid from parentFullPostid [%s]", data.parentFullPostid));
                    }
                    if (userid == originalAuthorUserid) break block22;
                    String originalAuthorUsername = userBean.getUsernameByUserid(originalAuthorUserid, null);
                    UserNotificationServicePrx unsProxy = this.getUserNotificationServicePrx();
                    if (unsProxy != null) {
                        long replyTimestamp = StringUtil.toLongOrDefault(data.timestamp, System.currentTimeMillis());
                        HashMap<String, String> parameters = new HashMap<String, String>();
                        parameters.put("replierUserid", Integer.toString(userid));
                        parameters.put("parentFullPostid", data.parentFullPostid);
                        parameters.put("fullPostid", data.fullPostid);
                        String key = data.fullPostid;
                        unsProxy.notifyFusionUser(new Message(key, originalAuthorUserid, originalAuthorUsername, Enums.NotificationTypeEnum.REPLY_TO_MIGBO_POST_ALERT.getType(), replyTimestamp, parameters));
                        log.debug((Object)String.format("Successfully created notification for user [%s][%d] that post [%s] has been replied by user [%s] with post[%s]", originalAuthorUsername, originalAuthorUserid, data.parentFullPostid, useridStr, data.fullPostid));
                        break block22;
                    }
                    log.error((Object)String.format("Unable to find UserNotificationServicePrx to generate REPLY_TO_MIGBO_POST_ALERT for userid [%d] postid [%s]", userid, data.fullPostid));
                    throw new FusionRestException(101, String.format("Internal error: unable to notify the new post created event", new Object[0]));
                }
                catch (Exception e) {
                    log.error((Object)String.format("Unexpected exception while generating REPLY_TO_MIGBO_POST_ALERT for userid [%d] postid [%s] : [%s]", userid, data.fullPostid, e.getMessage()), (Throwable)e);
                    throw new FusionRestException(101, String.format("Internal error: unable to notify the new post created event", new Object[0]));
                }
            }
        }
        this.notifyMentionedUsers(data, userBean, userid);
        try {
            MetricsLogger.log(MetricsEnums.FusionRestMetrics.MIGBO_NEW_POST_APPLICATION, data.fullPostid, data.application);
            MetricsLogger.log(MetricsEnums.FusionRestMetrics.MIGBO_NEW_POST_ORIGINALITY, data.fullPostid, data.originality);
            MetricsLogger.log(MetricsEnums.FusionRestMetrics.MIGBO_NEW_POST_TYPE, data.fullPostid, data.type);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while logging to metrics logger: " + e.getMessage()));
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    private void notifyMentionedUsers(EventNewPostData data, User userBean, int userid) throws FusionRestException {
        block6: {
            if (!SystemProperty.getBool(SystemPropertyEntities.Temp.SE523_ALERT_USER_MENTIONED_IN_MIGBO_POST_ENABLED)) {
                return;
            }
            if (null != data.mentions && !data.mentions.isEmpty()) {
                try {
                    UserNotificationServicePrx unsProxy = this.getUserNotificationServicePrx();
                    if (unsProxy != null) {
                        for (Integer mentionedUserId : data.mentions) {
                            if (null == mentionedUserId) continue;
                            String userName = userBean.getUsernameByUserid(mentionedUserId, null);
                            HashMap<String, String> parameters = new HashMap<String, String>();
                            parameters.put("fullPostid", data.fullPostid);
                            String key = data.fullPostid;
                            unsProxy.notifyFusionUser(new Message(key, mentionedUserId, userName, Enums.NotificationTypeEnum.MENTIONED_IN_MIGBO_POST_ALERT.getType(), StringUtil.toLongOrDefault(data.timestamp, System.currentTimeMillis()), parameters));
                            if (!log.isDebugEnabled()) continue;
                            log.debug((Object)String.format("Successfully created notification for user [%s][%d] who has been mentioned by user [%d] in post[%s]", mentionedUserId, userName, userid, data.fullPostid));
                        }
                        break block6;
                    }
                    log.error((Object)String.format("Unable to find UserNotificationServicePrx to generate MENTIONED_IN_MIGBO_POST_ALERT for userid [%d] postid [%s]", userid, data.fullPostid));
                    throw new FusionRestException(101, String.format("Internal error: unable to notify the new post created event", new Object[0]));
                }
                catch (Exception e) {
                    log.error((Object)String.format("Unexpected exception while generating MENTIONED_IN_MIGBO_POST_ALERT for userid [%d] postid [%s] : [%s]", userid, data.fullPostid, e.getMessage()), (Throwable)e);
                    throw new FusionRestException(101, String.format("Internal error: unable to notify the new post created event", new Object[0]));
                }
            }
        }
    }

    @POST
    @Path(value="/followingrequest/{userid}/{followerUserid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response createFollowingRequestEvent(@PathParam(value="userid") String useridStr, @PathParam(value="followerUserid") String followerUseridStr, EventFollowingRequestData data) throws FusionRestException {
        log.info((Object)String.format("recieved following request event, timestamp=%s", data.timestamp));
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Invalid userid '%s' specified", useridStr));
            throw new FusionRestException(-1, String.format("Invalid userid '%s' specified", useridStr));
        }
        int followerUserid = StringUtil.toIntOrDefault(followerUseridStr, -1);
        if (followerUserid == -1) {
            log.error((Object)String.format("Invalid follower userid '%s' specified", followerUseridStr));
            throw new FusionRestException(-1, String.format("Invalid follower userid '%s' specified", followerUseridStr));
        }
        try {
            UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
            if (unsProxy != null) {
                UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                String username = userBean.getUsernameByUserid(userid, null);
                long requestedTimestamp = StringUtil.toLongOrDefault(data.timestamp, -1L);
                if (requestedTimestamp == -1L) {
                    log.warn((Object)String.format("Invalid following request timestamp '%s', using current system time.", data.timestamp));
                    requestedTimestamp = System.currentTimeMillis();
                }
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("requestedTimestamp", data.timestamp);
                String key = Integer.toString(followerUserid);
                unsProxy.notifyFusionUser(new Message(key, userid, username, Enums.NotificationTypeEnum.FOLLOWING_REQUEST.getType(), requestedTimestamp, parameters));
                return Response.ok().entity(new DataHolder<String>("ok")).build();
            }
            log.error((Object)String.format("Unable to find UserNotificationServicePrx to push following request alert notification for user [%d], followerUserId %d", userid, followerUserid));
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to notify UserNotificationService about the new following request event due to exception, userid %s, followerUserid %s", useridStr, followerUseridStr), (Throwable)e);
        }
        throw new FusionRestException(101, String.format("Internal error: unable to notify the new following request event", new Object[0]));
    }

    @POST
    @Path(value="/followingevent/{userid}/{otherUserid}")
    @Produces(value={"application/json"})
    public Response handleFollowingEvent(@PathParam(value="userid") String useridStr, @PathParam(value="otherUserid") String otherUseridStr, @QueryParam(value="eventType") String eventTypeStr, @QueryParam(value="showAlert") String showAlertStr, @QueryParam(value="isAutoFollow") String isAutoFollow) throws FusionRestException {
        log.info((Object)String.format("received following event, userid=%s otherUserid=%s eventType=%s", useridStr, otherUseridStr, eventTypeStr));
        if (!SystemProperty.getBool(SystemPropertyEntities.Contacts.HANDLE_FOLLOWING_EVENTS_FROM_MIGBO_ENABLED)) {
            throw new FusionRestException(FusionRestException.RestException.SERVICE_DISABLED);
        }
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Invalid userid '%s' specified", useridStr));
            throw new FusionRestException(-1, String.format("Invalid userid '%s' specified", useridStr));
        }
        int otherUserid = StringUtil.toIntOrDefault(otherUseridStr, -1);
        if (otherUserid == -1) {
            log.error((Object)String.format("Invalid other userid '%s' specified", otherUseridStr));
            throw new FusionRestException(-1, String.format("Invalid other userid '%s' specified", otherUseridStr));
        }
        if (userid == otherUserid) {
            log.error((Object)String.format("Invalid userid '%s' and other userid '%s' specified - must not be the same", useridStr, otherUseridStr));
            throw new FusionRestException(-1, String.format("Invalid userid '%s' and other userid '%s' specified - must not be the same", useridStr, otherUseridStr));
        }
        MigboEnums.MigboFollowingEventTypeEnum type = MigboEnums.MigboFollowingEventTypeEnum.fromType(StringUtil.toIntOrDefault(eventTypeStr, -1));
        if (type == null) {
            log.error((Object)String.format("Invalid following event type '%s' specified", eventTypeStr));
            throw new FusionRestException(-1, String.format("Invalid following event type '%s' specified", eventTypeStr));
        }
        try {
            UserNotificationServicePrx unsProxy = this.getUserNotificationServicePrx();
            if (unsProxy != null) {
                UserData followedUserData;
                UserLocal userEJB;
                ContactData userContactData;
                UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
                String username = userBean.getUsernameByUserid(userid, null);
                String otherUsername = userBean.getUsernameByUserid(otherUserid, null);
                if (StringUtil.isBlank(username)) {
                    log.error((Object)String.format("Invalid userid '%s' specified - user not found", useridStr));
                    throw new FusionRestException(-1, String.format("Invalid userid '%s' specified - user not found", useridStr));
                }
                if (StringUtil.isBlank(otherUsername)) {
                    log.error((Object)String.format("Invalid other userid '%s' specified - user not found", otherUseridStr));
                    throw new FusionRestException(-1, String.format("Invalid other userid '%s' specified - user not found", otherUseridStr));
                }
                long eventTimestamp = System.currentTimeMillis();
                if (type == MigboEnums.MigboFollowingEventTypeEnum.MUTUAL_FOLLOWING) {
                    userContactData = contactBean.getContact(username, otherUsername);
                    ContactData otherContactData = contactBean.getContact(otherUsername, username);
                    if (StringUtil.isBlank(userContactData.username)) {
                        userContactData.username = username;
                        userContactData.fusionUsername = otherUsername;
                        contactBean.addFusionUserAsContact(userid, userContactData, false);
                    }
                    if (StringUtil.isBlank(otherContactData.username)) {
                        otherContactData.username = otherUsername;
                        otherContactData.fusionUsername = username;
                        contactBean.addFusionUserAsContact(otherUserid, otherContactData, false);
                    }
                    HashMap<String, String> parametersForUserBeingFollowedAlert = new HashMap<String, String>();
                    parametersForUserBeingFollowedAlert.put("otherUsername", otherUsername);
                    String key = Integer.toString(otherUserid);
                    unsProxy.notifyFusionUser(new Message(key, userid, username, Enums.NotificationTypeEnum.MUTUAL_FOLLOWING_ALERT.getType(), eventTimestamp, parametersForUserBeingFollowedAlert));
                    HashMap<String, String> parametersForFollowerAlert = new HashMap<String, String>();
                    parametersForFollowerAlert.put("otherUsername", username);
                    String key2 = Integer.toString(userid);
                    unsProxy.notifyFusionUser(new Message(key2, otherUserid, otherUsername, Enums.NotificationTypeEnum.MUTUAL_FOLLOWING_ALERT.getType(), eventTimestamp, parametersForFollowerAlert));
                    unsProxy.clearNotificationsForUser(userid, Enums.NotificationTypeEnum.NEW_FOLLOWER_ALERT.getType(), new String[]{Integer.toString(otherUserid)});
                    unsProxy.clearNotificationsForUser(otherUserid, Enums.NotificationTypeEnum.NEW_FOLLOWER_ALERT.getType(), new String[]{Integer.toString(userid)});
                } else if (type == MigboEnums.MigboFollowingEventTypeEnum.NEW_FOLLOWING) {
                    if (SystemProperty.getBool(SystemPropertyEntities.Contacts.ADD_CONTACT_ON_NEW_FOLLOWING_EVENT_ENABLED)) {
                        userContactData = contactBean.getContact(username, otherUsername);
                        if (StringUtil.isBlank(userContactData.username)) {
                            userContactData.username = username;
                            userContactData.fusionUsername = otherUsername;
                            contactBean.addFusionUserAsContact(userid, userContactData, false);
                        }
                    }
                    if (StringUtil.toBooleanOrDefault(showAlertStr, true)) {
                        HashMap<String, String> parametersForFollowerAlert = new HashMap<String, String>();
                        parametersForFollowerAlert.put("otherUsername", username);
                        String key = Integer.toString(userid);
                        unsProxy.notifyFusionUser(new Message(key, otherUserid, otherUsername, Enums.NotificationTypeEnum.NEW_FOLLOWER_ALERT.getType(), eventTimestamp, parametersForFollowerAlert));
                    }
                    MiniblogDailyDigestHelper.addRecipientForRecentlyFollowedDailyDigest(userid, otherUserid, System.currentTimeMillis());
                } else if (type == MigboEnums.MigboFollowingEventTypeEnum.REMOVE_FOLLOWING) {
                    if (SystemProperty.getBool(SystemPropertyEntities.Contacts.REMOVE_CONTACT_ON_REMOVE_FOLLOWING_EVENT_ENABLED)) {
                        userContactData = contactBean.getContact(username, otherUsername);
                        if (userContactData.id != null) {
                            contactBean.removeFusionUserFromContact(userid, username, userContactData.id, false);
                        }
                    }
                } else {
                    log.warn((Object)("Unsupported migbo following event type: " + (Object)((Object)type)));
                }
                boolean isAutoFollowBool = StringUtil.toBooleanOrDefault(isAutoFollow, false);
                if (type == MigboEnums.MigboFollowingEventTypeEnum.NEW_FOLLOWING) {
                    userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                    UserData followerUserData = userEJB.loadUserFromID(userid);
                    followedUserData = userEJB.loadUserFromID(otherUserid);
                    EventResource.sendRewardTrigger(new MigboFollowingEventTrigger(followerUserData, followedUserData, RelationshipEventTrigger.RelationshipEventTypeEnum.NEW, isAutoFollowBool));
                    EventResource.sendRewardTrigger(new MigboFollowedByEventTrigger(followedUserData, followerUserData, RelationshipEventTrigger.RelationshipEventTypeEnum.NEW, isAutoFollowBool));
                } else if (type == MigboEnums.MigboFollowingEventTypeEnum.MUTUAL_FOLLOWING) {
                    userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                    UserData followBackerUserData = userEJB.loadUserFromID(userid);
                    followedUserData = userEJB.loadUserFromID(otherUserid);
                    EventResource.sendRewardTrigger(new MigboFollowingEventTrigger(followBackerUserData, followedUserData, RelationshipEventTrigger.RelationshipEventTypeEnum.MUTUALLY_FOLLOWING, isAutoFollowBool));
                    EventResource.sendRewardTrigger(new MigboFollowedByEventTrigger(followedUserData, followBackerUserData, RelationshipEventTrigger.RelationshipEventTypeEnum.MUTUALLY_FOLLOWING, isAutoFollowBool));
                    EventResource.sendRewardTrigger(new MutuallyFollowingEventTrigger(followBackerUserData, followedUserData, true));
                    EventResource.sendRewardTrigger(new MutuallyFollowingEventTrigger(followedUserData, followBackerUserData, false));
                }
                return Response.ok().entity(new DataHolder<String>("ok")).build();
            }
            log.error((Object)String.format("Unable to find UserNotificationServicePrx to push following request alert notification for user [%d], followerUserId %d", userid, otherUserid));
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to to handle the following event, userid %s, followerUserid %s, eventType %s", useridStr, otherUseridStr, eventTypeStr), (Throwable)e);
        }
        throw new FusionRestException(101, String.format("Internal error: unable to handle the following event", new Object[0]));
    }

    private static void sendRewardTrigger(RewardProgramTrigger trigger) {
        try {
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.error((Object)("Unexpected exception while sending event triggers " + e), (Throwable)e);
        }
    }

    /*
     * Loose catch block
     */
    @POST
    @Path(value="/groupinvite/{userid}/{action}/{groupid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response createGroupInvitationEventResponse(@PathParam(value="userid") String useridStr, @PathParam(value="groupid") String groupidStr, @PathParam(value="action") String actionStr) throws FusionRestException {
        block21: {
            int groupid;
            int userid;
            block20: {
                userid = StringUtil.toIntOrDefault(useridStr, -1);
                groupid = StringUtil.toIntOrDefault(groupidStr, -1);
                if (!actionStr.equals("accept") && !actionStr.equals("reject")) {
                    throw new FusionRestException(-1, String.format("Invalid action '%s'", actionStr));
                }
                if (userid == -1) {
                    throw new FusionRestException(-1, String.format("Invalid userid '%s'", useridStr));
                }
                if (!(groupid != -1 || groupidStr.equals("@all") && actionStr.equals("reject"))) {
                    throw new FusionRestException(-1, String.format("Invalid groupid '%s'", groupidStr));
                }
                Web webBean = (Web)EJBHomeCache.getObject("ejb/Web", WebHome.class);
                User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                String username = userBean.getUsernameByUserid(userid, null);
                if (StringUtil.isBlank(username)) {
                    throw new FusionRestException(101, String.format("User with id '%d' not found", userid));
                }
                if (actionStr.equals("accept")) {
                    boolean smsNotification = false;
                    boolean emailNotification = false;
                    boolean eventNotification = true;
                    boolean smsGroupEventNotification = false;
                    boolean emailThreadUpdateNotification = false;
                    boolean eventThreadUpdateNotification = false;
                    int locationID = 0;
                    String ipAddress = "127.0.0.1";
                    try {
                        ipAddress = InetAddress.getLocalHost().toString();
                    }
                    catch (Exception ignored) {
                        // empty catch block
                    }
                    String sessionID = "None";
                    String mobileDevice = "FusionRest";
                    String userAgent = "FusionRest";
                    webBean.joinGroup(username, groupid, locationID, ipAddress, sessionID, mobileDevice, userAgent, smsNotification, emailNotification, eventNotification, smsGroupEventNotification, emailThreadUpdateNotification, eventThreadUpdateNotification);
                    log.debug((Object)String.format("Accepted group invitation for user [%s][%d] group [%d]", username, userid, groupid));
                    break block20;
                }
                if (groupidStr.equals("@all")) {
                    webBean.declineAllGroupInvitations(username);
                    log.debug((Object)String.format("Declined all group invitations for user [%s][%d]", username, userid));
                    break block20;
                }
                webBean.declineGroupInvitation(username, groupid);
                log.debug((Object)String.format("Declined group invitation for user [%s][%d] group [%d]", username, userid, groupid));
            }
            Object var21_23 = null;
            try {
                UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
                if (unsProxy == null) break block21;
                if (groupidStr.equals("@all")) {
                    unsProxy.clearAllNotificationsByTypeForUser(userid, Enums.NotificationTypeEnum.GROUP_INVITE.getType());
                    break block21;
                }
                unsProxy.clearNotificationsForUser(userid, Enums.NotificationTypeEnum.GROUP_INVITE.getType(), new String[]{userid + "/" + groupid});
            }
            catch (Exception e) {
                log.warn((Object)("Unable to remove GROUP_INVITE notification for userid [" + userid + "] from group [" + groupidStr + "]"), (Throwable)e);
            }
            break block21;
            {
                catch (CreateException e) {
                    log.error((Object)String.format("Unable to process friend invitation response due to EJB CreateException '%s'", e.getMessage()), (Throwable)e);
                    throw new FusionRestException(101, String.format("Internal system error: unable to respond to the friend invite", new Object[0]));
                }
                catch (RemoteException e) {
                    log.error((Object)String.format("Unable to process friend invitation response due to EJB RemoteException '%s'", e.getMessage()), (Throwable)e);
                    throw new FusionRestException(101, String.format("Internal system error: unable to respond to the friend invite", new Object[0]));
                }
            }
            catch (Throwable throwable) {
                Object var21_24 = null;
                try {
                    UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
                    if (unsProxy != null) {
                        if (groupidStr.equals("@all")) {
                            unsProxy.clearAllNotificationsByTypeForUser(userid, Enums.NotificationTypeEnum.GROUP_INVITE.getType());
                        } else {
                            unsProxy.clearNotificationsForUser(userid, Enums.NotificationTypeEnum.GROUP_INVITE.getType(), new String[]{userid + "/" + groupid});
                        }
                    }
                }
                catch (Exception e) {
                    log.warn((Object)("Unable to remove GROUP_INVITE notification for userid [" + userid + "] from group [" + groupidStr + "]"), (Throwable)e);
                }
                throw throwable;
            }
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    /*
     * Loose catch block
     */
    @POST
    @Path(value="/friendinvite/{userid}/{action}/{requestorUserid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response createFriendInvitationEventResponse(@PathParam(value="userid") String useridStr, @PathParam(value="requestorUserid") String requestorUseridStr, @PathParam(value="action") String actionStr) throws FusionRestException {
        block18: {
            String requestorUsername;
            int userid;
            block17: {
                if (!SystemProperty.getBool(SystemPropertyEntities.Contacts.HANDLE_FRIENDINVITE_EVENTS_FROM_MIGBO_ENABLED)) {
                    throw new FusionRestException(FusionRestException.RestException.SERVICE_DISABLED);
                }
                log.info((Object)String.format("received friend invitation response %s %s %s", useridStr, requestorUseridStr, actionStr));
                userid = StringUtil.toIntOrDefault(useridStr, -1);
                int requestorUserid = StringUtil.toIntOrDefault(requestorUseridStr, -1);
                if (userid == -1) {
                    throw new FusionRestException(-1, String.format("Invalid userid '%s'", useridStr));
                }
                if (requestorUserid == -1) {
                    throw new FusionRestException(-1, String.format("Invalid requestorUserid '%s'", requestorUseridStr));
                }
                if (!(actionStr.equals("accept") || actionStr.equals("reject") || actionStr.equals("rejectAndBlock"))) {
                    throw new FusionRestException(-1, String.format("Invalid action '%s'", actionStr));
                }
                requestorUsername = "";
                Contact contactBean = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
                User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                String username = userBean.getUsernameByUserid(userid, null);
                requestorUsername = userBean.getUsernameByUserid(requestorUserid, null);
                if (StringUtil.isBlank(username)) {
                    throw new FusionRestException(101, String.format("User with id '%d' not found", userid));
                }
                if (StringUtil.isBlank(requestorUsername)) {
                    throw new FusionRestException(101, String.format("User with id '%d' not found", requestorUserid));
                }
                if (actionStr.equals("accept")) {
                    ContactData contactData = new ContactData();
                    contactData.username = username;
                    contactData.fusionUsername = requestorUsername;
                    contactData.contactGroupId = null;
                    contactData.displayOnPhone = true;
                    contactData.shareMobilePhone = false;
                    contactData = contactBean.acceptContactRequest(userid, contactData, false);
                    break block17;
                }
                contactBean.rejectContactRequest(userid, username, requestorUsername);
                if (!actionStr.equals("rejectAndBlock")) break block17;
                contactBean.blockContact(userid, username, requestorUsername);
            }
            Object var12_13 = null;
            try {
                UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
                if (!StringUtil.isBlank(requestorUsername) && unsProxy != null) {
                    unsProxy.clearNotificationsForUser(userid, Enums.NotificationTypeEnum.FRIEND_INVITE.getType(), new String[]{requestorUsername});
                }
                break block18;
            }
            catch (Exception e) {
                log.warn((Object)("Unable to remove FRIEND_INVITE notification for [" + userid + "] from [" + requestorUsername + "]"), (Throwable)e);
            }
            break block18;
            {
                catch (CreateException e) {
                    log.error((Object)String.format("Unable to process friend invitation response due to EJB CreateException '%s'", e.getMessage()), (Throwable)e);
                    throw new FusionRestException(101, String.format("Internal system error: unable to respond to the friend invite", new Object[0]));
                }
                catch (RemoteException e) {
                    log.error((Object)String.format("Unable to process friend invitation response due to EJB RemoteException '%s'", e.getMessage()), (Throwable)e);
                    throw new FusionRestException(101, String.format("Internal system error: unable to respond to the friend invite", new Object[0]));
                }
            }
            catch (Throwable throwable) {
                Object var12_14 = null;
                try {
                    UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
                    if (!StringUtil.isBlank(requestorUsername) && unsProxy != null) {
                        unsProxy.clearNotificationsForUser(userid, Enums.NotificationTypeEnum.FRIEND_INVITE.getType(), new String[]{requestorUsername});
                    }
                }
                catch (Exception e) {
                    log.warn((Object)("Unable to remove FRIEND_INVITE notification for [" + userid + "] from [" + requestorUsername + "]"), (Throwable)e);
                }
                throw throwable;
            }
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/alertbatch")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response processAlertBatch(String data) throws FusionRestException {
        UserNotificationServicePrx unsProxy = this.getUserNotificationServicePrx();
        if (unsProxy != null) {
            try {
                JSONArray jsonArray = new JSONArray(data);
                String rateLimit = SystemProperty.get(SystemPropertyEntities.FusionRestRateLimit.EVENT_ALERTBATCH);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    Message msg = new Message();
                    msg.key = jsonObj.getString("key");
                    msg.toUserId = jsonObj.getInt("toUserId");
                    msg.toUsername = jsonObj.getString("toUsername");
                    msg.notificationType = jsonObj.getInt("notificationType");
                    msg.dateCreated = jsonObj.getLong("dateCreated");
                    msg.parameters = new HashMap<String, String>();
                    JSONObject parametersJsonObj = jsonObj.getJSONObject("parameters");
                    JSONArray parameterKeys = parametersJsonObj.names();
                    for (int j = 0; j < parameterKeys.length(); ++j) {
                        String parameterKey = parameterKeys.getString(j);
                        String parameterValue = parametersJsonObj.getString(parameterKey);
                        msg.parameters.put(parameterKey, parameterValue);
                    }
                    try {
                        String parentFullPostid = msg.parameters.get("parentFullPostid");
                        if (!StringUtil.isBlank(parentFullPostid)) {
                            String rateLimitKey = parentFullPostid + ':' + msg.toUserId;
                            MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.FUSIONREST_ALERTBATCH, rateLimitKey, rateLimit);
                        }
                        unsProxy.notifyFusionUser(msg);
                        continue;
                    }
                    catch (MemCachedRateLimiter.LimitExceeded le) {
                        log.warn((Object)String.format("Rate limit exceeded in /event/alertbatch when sending alert of type %d to user %s key %s : %s", msg.notificationType, msg.toUsername, msg.key, le.getMessage()));
                        continue;
                    }
                    catch (MemCachedRateLimiter.FormatError fe) {
                        log.error((Object)String.format("Format Error in /event/alertbatch : %s", fe.getMessage()));
                    }
                }
            }
            catch (JSONException e) {
                log.error((Object)("Unable to parse JSON data for alert batch processing: " + (Object)((Object)e)));
            }
            catch (Exception e) {
                log.error((Object)("Unable to process alert batch: " + e));
            }
        } else {
            log.error((Object)"Unable to find UserNotificationServicePrx to for alert batch processing");
            throw new FusionRestException(101, String.format("Internal error: Unable to process alert batch", new Object[0]));
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/rewardprogram/group/topic/{userid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response submitGroupTopicCreatedRewardProgramTrigger(@PathParam(value="userid") String useridStr, GroupActivityRewardProgramTriggerData data) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            String message = String.format("Invalid userid '%s' specified", useridStr);
            log.error((Object)message);
            throw new FusionRestException(-1, message);
        }
        User userBean = null;
        try {
            userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        }
        catch (CreateException ce) {
            log.error((Object)String.format("Unable to notify RewardCentre about the group topic created reward program trigger, userid %s", useridStr), (Throwable)ce);
            throw new FusionRestException(101, String.format("Internal error: unable to initialize UserBean", new Object[0]));
        }
        try {
            UserData authorUserData = userBean.loadUserFromID(userid);
            GroupActivityTrigger trigger = new GroupActivityTrigger(GroupActivityTrigger.ActivityTypeEnum.TOPIC_CREATED, authorUserData);
            trigger.amountDelta = 0.0;
            trigger.quantityDelta = 1;
            trigger.groupID = data.groupID;
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to notify RewardCentre about the group topic created reward program trigger, userid %s", useridStr), (Throwable)e);
            throw new FusionRestException(101, String.format("Internal error: unable to notify RewardCentre about the group topic created reward program trigger", new Object[0]));
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/rewardprogram/group/topic_comment/{userid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response submitGroupTopicCommentRewardProgramTrigger(@PathParam(value="userid") String useridStr, GroupActivityRewardProgramTriggerData data) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            String message = String.format("Invalid userid '%s' specified", useridStr);
            log.error((Object)message);
            throw new FusionRestException(-1, message);
        }
        User userBean = null;
        try {
            userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        }
        catch (CreateException ce) {
            log.error((Object)String.format("Unable to notify RewardCentre about the group topic comment reward program trigger, userid %ss", useridStr), (Throwable)ce);
            throw new FusionRestException(101, String.format("Internal error: unable to initialize UserBean", new Object[0]));
        }
        try {
            UserData authorUserData = userBean.loadUserFromID(userid);
            GroupActivityTrigger trigger = new GroupActivityTrigger(GroupActivityTrigger.ActivityTypeEnum.TOPIC_COMMENTED, authorUserData);
            trigger.amountDelta = 0.0;
            trigger.quantityDelta = 1;
            trigger.groupID = data.groupID;
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to notify RewardCentre about the group topic comment program trigger, userid %s", useridStr), (Throwable)e);
            throw new FusionRestException(101, String.format("Internal error: unable to notify RewardCentre about the group topic comment reward program trigger", new Object[0]));
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/rewardprogram/group/wallpost/{userid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response submitGroupWallPostCreatedRewardProgramTrigger(@PathParam(value="userid") String useridStr, GroupActivityRewardProgramTriggerData data) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            String message = String.format("Invalid userid '%s' specified", useridStr);
            log.error((Object)message);
            throw new FusionRestException(-1, message);
        }
        User userBean = null;
        try {
            userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        }
        catch (CreateException ce) {
            log.error((Object)String.format("Unable to notify RewardCentre about the group topic comment reward program trigger, userid %ss", useridStr), (Throwable)ce);
            throw new FusionRestException(101, String.format("Internal error: unable to initialize UserBean", new Object[0]));
        }
        try {
            UserData authorUserData = userBean.loadUserFromID(userid);
            GroupActivityTrigger trigger = new GroupActivityTrigger(GroupActivityTrigger.ActivityTypeEnum.WALLPOST_CREATED, authorUserData);
            trigger.amountDelta = 0.0;
            trigger.quantityDelta = 1;
            trigger.groupID = data.groupID;
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to notify RewardCentre about the group topic comment program trigger, userid %s", useridStr), (Throwable)e);
            throw new FusionRestException(101, String.format("Internal error: unable to notify RewardCentre about the group topic comment reward program trigger", new Object[0]));
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/rewardprogram/group/wallpost_comment/{userid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response submitGroupWallPostCommentRewardProgramTrigger(@PathParam(value="userid") String useridStr, GroupActivityRewardProgramTriggerData data) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            String message = String.format("Invalid userid '%s' specified", useridStr);
            log.error((Object)message);
            throw new FusionRestException(-1, message);
        }
        User userBean = null;
        try {
            userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        }
        catch (CreateException ce) {
            log.error((Object)String.format("Unable to notify RewardCentre about the group wallpost comment reward program trigger, userid %ss", useridStr), (Throwable)ce);
            throw new FusionRestException(101, String.format("Internal error: unable to initialize UserBean", new Object[0]));
        }
        try {
            UserData authorUserData = userBean.loadUserFromID(userid);
            GroupActivityTrigger trigger = new GroupActivityTrigger(GroupActivityTrigger.ActivityTypeEnum.WALLPOST_COMMENTED, authorUserData);
            trigger.amountDelta = 0.0;
            trigger.quantityDelta = 1;
            trigger.groupID = data.groupID;
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to notify RewardCentre about the group wallpost comment program trigger, userid %s", useridStr), (Throwable)e);
            throw new FusionRestException(101, String.format("Internal error: unable to notify RewardCentre about the group wallpost comment reward program trigger", new Object[0]));
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/rewardprogram/thirdpartyapp/start_event/{userid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response submitThirdPartyAppStartEventRewardProgramTrigger(@PathParam(value="userid") String useridStr, ThirdPartyAppActivityRewardProgramTriggerData data) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            String message = String.format("Invalid userid '%s' specified", useridStr);
            log.error((Object)message);
            throw new FusionRestException(-1, message);
        }
        if (StringUtil.isBlank(data.applicationName)) {
            String message = "applicationName cannot be empty";
            log.error((Object)message);
            throw new FusionRestException(-1, message);
        }
        User userBean = null;
        try {
            userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        }
        catch (CreateException ce) {
            log.error((Object)String.format("Unable to notify RewardCentre about the third party app start event reward program trigger, userid %ss", useridStr), (Throwable)ce);
            throw new FusionRestException(101, String.format("Internal error: unable to initialize UserBean", new Object[0]));
        }
        try {
            UserData authorUserData = userBean.loadUserFromID(userid);
            ThirdPartyAppStartEventTrigger trigger = new ThirdPartyAppStartEventTrigger(authorUserData);
            trigger.amountDelta = 0.0;
            trigger.quantityDelta = 1;
            trigger.applicationName = data.applicationName;
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to notify RewardCentre about the third party app start event program trigger, userid %s", useridStr), (Throwable)e);
            throw new FusionRestException(101, String.format("Internal error: unable to notify RewardCentre about the third party app start event reward program trigger", new Object[0]));
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/rewardprogram/thirdpartyapp/internal_invite_sent/{userid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response submitThirdPartyAppInternalInviteSentEventRewardProgramTrigger(@PathParam(value="userid") String useridStr, ThirdPartyAppActivityRewardProgramTriggerData data) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            String message = String.format("Invalid userid '%s' specified", useridStr);
            log.error((Object)message);
            throw new FusionRestException(-1, message);
        }
        if (StringUtil.isBlank(data.applicationName)) {
            String message = "applicationName cannot be empty";
            log.error((Object)message);
            throw new FusionRestException(-1, message);
        }
        User userBean = null;
        try {
            userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        }
        catch (CreateException ce) {
            log.error((Object)String.format("Unable to notify RewardCentre about the third party app start event reward program trigger, userid %ss", useridStr), (Throwable)ce);
            throw new FusionRestException(101, String.format("Internal error: unable to initialize UserBean", new Object[0]));
        }
        try {
            UserData authorUserData = userBean.loadUserFromID(userid);
            ThirdPartyAppInternalInvitationTrigger trigger = new ThirdPartyAppInternalInvitationTrigger(ThirdPartyAppInternalInvitationTrigger.StateEnum.SENT, authorUserData);
            trigger.amountDelta = 0.0;
            trigger.quantityDelta = 1;
            trigger.applicationName = data.applicationName;
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to notify RewardCentre about the third party app start event program trigger, userid %s", useridStr), (Throwable)e);
            throw new FusionRestException(101, String.format("Internal error: unable to notify RewardCentre about the third party app start event reward program trigger", new Object[0]));
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/rewardprogram/thirdpartyapp/internal_invite_accepted/{userid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response submitThirdPartyAppInternalInviteAcceptedEventRewardProgramTrigger(@PathParam(value="userid") String useridStr, ThirdPartyAppActivityRewardProgramTriggerData data) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            String message = String.format("Invalid userid '%s' specified", useridStr);
            log.error((Object)message);
            throw new FusionRestException(-1, message);
        }
        if (StringUtil.isBlank(data.applicationName)) {
            String message = "applicationName cannot be empty";
            log.error((Object)message);
            throw new FusionRestException(-1, message);
        }
        User userBean = null;
        try {
            userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        }
        catch (CreateException ce) {
            log.error((Object)String.format("Unable to notify RewardCentre about the third party app start event reward program trigger, userid %ss", useridStr), (Throwable)ce);
            throw new FusionRestException(101, String.format("Internal error: unable to initialize UserBean", new Object[0]));
        }
        try {
            UserData authorUserData = userBean.loadUserFromID(userid);
            ThirdPartyAppInternalInvitationTrigger trigger = new ThirdPartyAppInternalInvitationTrigger(ThirdPartyAppInternalInvitationTrigger.StateEnum.ACCEPTED, authorUserData);
            trigger.amountDelta = 0.0;
            trigger.quantityDelta = 1;
            trigger.applicationName = data.applicationName;
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to notify RewardCentre about the third party app start event program trigger, userid %s", useridStr), (Throwable)e);
            throw new FusionRestException(101, String.format("Internal error: unable to notify RewardCentre about the third party app start event reward program trigger", new Object[0]));
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }

    @POST
    @Path(value="/migbo_campaign/{campaignid}/{userid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response submitMigboCampaignEvent(@PathParam(value="campaignid") String campaignidStr, @PathParam(value="userid") String useridStr, @QueryParam(value="type") String typeStr, String jsonBody) throws FusionRestException {
        JSONObject jsonObj;
        int campaignId = StringUtil.toIntOrDefault(campaignidStr, 0);
        if (campaignId < 1) {
            throw new FusionRestException(101, String.format("Invalid campaignid provided [%s]", campaignidStr));
        }
        int userid = StringUtil.toIntOrDefault(useridStr, 0);
        if (userid < 1) {
            throw new FusionRestException(101, String.format("Invalid userid provided [%s]", useridStr));
        }
        MigboCampaignTrigger.EventTypeEnum type = MigboCampaignTrigger.EventTypeEnum.fromString(typeStr);
        if (type == null) {
            throw new FusionRestException(101, String.format("Invalid type provided [%s]", typeStr));
        }
        try {
            jsonObj = new JSONObject(jsonBody);
        }
        catch (Exception e) {
            log.error((Object)String.format("JSONException caught while parsing jsonBody %s", e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Invalid JSON body provided : " + jsonBody);
        }
        MigboCampaignTrigger trigger = null;
        UserLocal userBean = null;
        try {
            userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
        }
        catch (CreateException ce) {
            log.error((Object)"Unable to initialize UserBean in submitMigboCampaignEvent()", (Throwable)ce);
            throw new FusionRestException(101, String.format("Internal error: unable to initialize UserBean", new Object[0]));
        }
        long eventTimestamp = StringUtil.toLongOrDefault(jsonObj.optString("timestamp"), System.currentTimeMillis());
        String entityType = jsonObj.optString("entityType");
        String entityId = jsonObj.optString("entityID");
        int tagValue = jsonObj.optInt("tagValue");
        UserData userData = userBean.loadUserFromID(userid);
        if (userData == null) {
            throw new FusionRestException(101, String.format("Invalid userid provided", useridStr));
        }
        switch (type) {
            case REGISTRATION: {
                trigger = MigboCampaignTrigger.getCampaignRegistrationTrigger(userData, campaignId, eventTimestamp);
                break;
            }
            case TAG_CREATED: {
                trigger = MigboCampaignTrigger.getTagCreatedTrigger(userData, campaignId, eventTimestamp, entityType, entityId, tagValue);
                break;
            }
            default: {
                throw new FusionRestException(101, String.format("Unsupported eventType : %s", typeStr));
            }
        }
        try {
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unknown exception while submitting trigger %s", e.getMessage(), e));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        return Response.ok().entity(new DataHolder<String>("ok")).build();
    }
}

