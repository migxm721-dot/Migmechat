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
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserPrx;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class Whois
extends EmoteCommand {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Whois.class));

    public Whois(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] args = messageData.getArgs();
        if (args.length < 2) {
            throw new FusionException("Usage: /whois [username]");
        }
        String target = args[1];
        String source = chatSource.getParentUsername();
        String rateLimit = SystemProperty.get("WhoisRateLimitExpr", "1/5S");
        super.checkRateLimit(Whois.class, "s:" + messageData.source, rateLimit);
        messageData.messageText = String.format("** %s :", target);
        Object[] chatrooms = null;
        User userBean = null;
        MIS misBean = null;
        boolean isSourceGlobalAdmin = false;
        boolean isTargetGlobalAdmin = false;
        boolean isUserFound = false;
        try {
            userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            misBean = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
            try {
                int userId = userBean.getUserID(target, null);
                if (userId > 0) {
                    UserData targetUserData = userBean.loadUser(target, false, false);
                    UserData sourceUserData = userBean.loadUser(chatSource.getParentUsername(), false, false);
                    UserProfileData profileData = userBean.getUserProfile(chatSource.getParentUsername(), target, false);
                    isSourceGlobalAdmin = sourceUserData.chatRoomAdmin;
                    isTargetGlobalAdmin = targetUserData.chatRoomAdmin;
                    String country = misBean.getCountry((int)targetUserData.countryID.intValue()).name;
                    int migLevel = MemCacheOrEJB.getUserReputationLevel(target);
                    String gender = "Unknown";
                    if (null != profileData.gender) {
                        gender = profileData.gender == UserProfileData.GenderEnum.MALE ? "Male" : "Female";
                    }
                    String profileTemplate = " Gender: %s, migLevel: %d, Location: %s.";
                    messageData.messageText = messageData.messageText + String.format(profileTemplate, gender, migLevel, country);
                    isUserFound = true;
                }
            }
            catch (FusionEJBException e) {
                log.debug((Object)("unable to load user profile for " + target), (Throwable)e);
            }
            catch (RemoteException e) {
                log.debug((Object)("unable to load user profile for " + target), (Throwable)e);
            }
        }
        catch (CreateException e) {
            log.error((Object)"Unknown error while loading user profile", (Throwable)e);
        }
        if (isUserFound) {
            String presenceText = isSourceGlobalAdmin ? " Status: offline." : "";
            String chatRoomText = "";
            try {
                block17: {
                    try {
                        UserPrx userPrx = this.getIcePrxFinder().getRegistry(false).findUserObject(target);
                        if (null == userPrx) break block17;
                        chatrooms = userPrx.getCurrentChatrooms();
                        boolean isWhoisSelf = target.equals(source);
                        List<String> broadcastList = Arrays.asList(userPrx.getBroadcastList());
                        int targetPresence = userPrx.getOverallFusionPresence(null);
                        if (isSourceGlobalAdmin) {
                            presenceText = String.format(" Status: %s.", PresenceType.fromValue(targetPresence).toString().toLowerCase());
                        }
                        if (!isSourceGlobalAdmin && !isWhoisSelf && (!broadcastList.contains(source) || PresenceType.OFFLINE.value() == targetPresence) || chatrooms.length <= 0) break block17;
                        if (!isSourceGlobalAdmin && isTargetGlobalAdmin) {
                            chatRoomText = String.format(" Chatting in : ***.", new Object[0]);
                            break block17;
                        }
                        chatRoomText = String.format(" Chatting in : %s.", StringUtil.join(chatrooms, ","));
                    }
                    catch (Exception e) {
                        log.debug((Object)("unable to find target user " + args[1]), (Throwable)e);
                        Object var22_28 = null;
                        messageData.messageText = messageData.messageText + presenceText;
                        messageData.messageText = messageData.messageText + chatRoomText;
                    }
                }
                Object var22_27 = null;
                messageData.messageText = messageData.messageText + presenceText;
                messageData.messageText = messageData.messageText + chatRoomText;
            }
            catch (Throwable throwable) {
                Object var22_29 = null;
                messageData.messageText = messageData.messageText + presenceText;
                messageData.messageText = messageData.messageText + chatRoomText;
                throw throwable;
            }
        } else {
            messageData.messageText = messageData.messageText + " Not Found.";
        }
        messageData.messageText = messageData.messageText + " **";
        this.emoteCommandData.updateMessageData(messageData);
        chatSource.sendMessageToSender(messageData);
        return EmoteCommand.ResultType.HANDLED_AND_STOP;
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }
}

