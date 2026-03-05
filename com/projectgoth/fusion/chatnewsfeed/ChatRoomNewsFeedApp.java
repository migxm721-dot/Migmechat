/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.LocalException
 *  Ice.ObjectPrx
 *  Ice.Properties
 *  org.apache.axis.utils.StringUtils
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatnewsfeed;

import Ice.Application;
import Ice.LocalException;
import Ice.ObjectPrx;
import Ice.Properties;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.interfaces.Web;
import com.projectgoth.fusion.interfaces.WebHome;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.RegistryPrxHelper;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.List;
import org.apache.axis.utils.StringUtils;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ChatRoomNewsFeedApp
extends Application {
    protected static Logger logger;
    public static String hostName;
    public static Properties properties;
    public static RegistryPrx registryPrx;
    protected String[] chatRoomNames;

    public int run(String[] arg0) {
        properties = ChatRoomNewsFeedApp.communicator().getProperties();
        try {
            hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            hostName = "UNKNOWN";
        }
        String registryStringifiedProxy = ChatRoomNewsFeedApp.communicator().getProperties().getProperty("RegistryProxy");
        ObjectPrx basePrx = ChatRoomNewsFeedApp.communicator().stringToProxy(registryStringifiedProxy);
        logger.info((Object)("Connecting to [" + basePrx + "]"));
        try {
            registryPrx = RegistryPrxHelper.checkedCast(basePrx);
        }
        catch (LocalException e) {
            logger.fatal((Object)("Chat Room NewsFeed: " + hostName + ": Connection to [" + registryPrx + "] failed. "), (Throwable)e);
            return 1;
        }
        if (registryPrx == null) {
            logger.fatal((Object)("Chat Room NewsFeed: " + hostName + ": Connection to [" + registryPrx + "] failed"));
            return 1;
        }
        return 0;
    }

    protected void sendMessagesToChatRooms(List<MessageData> messages, String roomDescription) throws Exception {
        if (messages == null || messages.isEmpty()) {
            logger.error((Object)"No messages to send.");
            return;
        }
        ChatRoomPrx[] chatRoomProxies = this.getChatRooms();
        this.updateRoomDescription(roomDescription, chatRoomProxies);
        for (MessageData message : messages) {
            for (ChatRoomPrx chatRoomPrx : chatRoomProxies) {
                if (chatRoomPrx == null) continue;
                List<String> emoticonKeys = message.emoticonKeys;
                chatRoomPrx.putSystemMessage(message.messageText, emoticonKeys != null ? emoticonKeys.toArray(new String[0]) : null);
                if (!logger.isInfoEnabled()) continue;
                logger.info((Object)("Message sent successfully to " + chatRoomPrx));
            }
        }
    }

    private void updateRoomDescription(String roomDescription, ChatRoomPrx[] chatRoomProxies) throws FusionException {
        if (roomDescription != null) {
            try {
                Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                messageEJB.updateRoomDescriptions(this.chatRoomNames, roomDescription);
            }
            catch (Exception e) {
                logger.warn((Object)"Unable to update chat room description in the DB. ", (Throwable)e);
                throw new FusionException("Unable to obtain details of the chat room from the DB");
            }
        }
    }

    public void createGroupPost(String username, int groupModuleID, String title, String text, int status) {
        if (groupModuleID != -1 && !StringUtils.isEmpty((String)title)) {
            try {
                Web webEJB = (Web)EJBHomeCache.getObject("ejb/Web", WebHome.class);
                Hashtable result = webEJB.createGroupPost(username, groupModuleID, title, text, status);
                if (logger.isInfoEnabled()) {
                    logger.info((Object)("Post result: " + result));
                }
            }
            catch (Exception e) {
                logger.warn((Object)("Unable to create group post in the DB with title '" + title + "' and groupModuleID " + groupModuleID), (Throwable)e);
            }
        }
    }

    protected ChatRoomPrx[] getChatRooms() {
        if (this.chatRoomNames == null || this.chatRoomNames.length == 0) {
            logger.error((Object)"No subscribed chatrooms found.");
            return null;
        }
        ChatRoomPrx[] chatRoomPrxs = this.getChatRoomProxies();
        return chatRoomPrxs;
    }

    protected void updateChatRoomDescription(String text) throws Exception {
        ChatRoomPrx[] chatRoomProxies = this.getChatRooms();
        this.updateRoomDescription(text, chatRoomProxies);
    }

    private ChatRoomPrx[] getChatRoomProxies() {
        ChatRoomPrx[] chatRoomPrxs = new ChatRoomPrx[this.chatRoomNames.length];
        try {
            if (registryPrx != null) {
                chatRoomPrxs = registryPrx.findChatRoomObjects(this.chatRoomNames);
            }
        }
        catch (Exception e) {
            logger.error((Object)("One or more chat rooms in list starting'" + this.chatRoomNames[0] + "'... could not be found"), (Throwable)e);
        }
        return chatRoomPrxs;
    }

    static {
        hostName = null;
        properties = null;
    }
}

