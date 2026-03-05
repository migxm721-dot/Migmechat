/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatRoomEmoteLogData;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.emote.EmoteCommandUtils;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

public class Follow
extends EmoteCommand {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Follow.class));
    private static final String USAGE_STR = "Usage: /follow or /f [username]";

    public Follow(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        String[] args = messageData.getArgs();
        if (args.length != 2) {
            throw new FusionException(USAGE_STR);
        }
        String rateLimitPerUser = SystemProperty.get(SystemPropertyEntities.Emote.FOLLOW_RATE_LIMIT);
        super.checkRateLimit(Follow.class, "s:" + messageData.source, rateLimitPerUser);
        if (!EmoteCommandUtils.clientMeetsMinVersion(chatSource.getSessionI().getClientVersion(), chatSource.getSessionI().getDeviceType())) {
            throw new FusionException("/follow is not supported on this client device and version");
        }
        String usernameToFollow = args[1];
        try {
            ContactData contactData = new ContactData();
            contactData.username = messageData.source;
            contactData.fusionUsername = usernameToFollow;
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            int userId = userBean.getUserID(messageData.source, null);
            Contact contactBean = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
            contactBean.addFusionUserAsContact(userId, contactData, true);
            messageData.messageText = "You are now following " + usernameToFollow;
            chatSource.sendMessageToSender(messageData);
        }
        catch (CreateException e) {
            log.error((Object)e.getMessage());
            throw new FusionException("Unable to follow user [" + usernameToFollow + "]. Please try again later.");
        }
        catch (RemoteException e) {
            log.error((Object)e.getMessage());
            throw new FusionException("Unable to follow user [" + usernameToFollow + "]. Please try again later.");
        }
        catch (EJBException e) {
            log.error((Object)e.getMessage());
            throw new FusionException("Unable to follow user [" + usernameToFollow + "]. Please try again later.");
        }
        catch (FusionEJBException e) {
            log.warn((Object)e.getMessage());
            throw new FusionException(e.getMessage());
        }
        int chatroomId = -1;
        int groupId = -1;
        if (chatSource.getChatType() == ChatSource.ChatType.CHATROOM_CHAT) {
            ChatRoomPrx chatRoomPrx = chatSource.castToLocalChatRoomChatSource().getChatRoomPrx();
            ChatRoomDataIce roomData = chatRoomPrx.getRoomData();
            chatroomId = roomData.id;
            groupId = roomData.groupID;
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Emote.LOG_ALL_EMOTES_ENABLED)) {
            ChatRoomEmoteLogData logData = new ChatRoomEmoteLogData(messageData.source, usernameToFollow, args[0], chatroomId, groupId, -1, null);
            chatSource.getSessionI().logEmoteData(logData);
        }
        return EmoteCommand.ResultType.HANDLED_AND_STOP;
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }
}

