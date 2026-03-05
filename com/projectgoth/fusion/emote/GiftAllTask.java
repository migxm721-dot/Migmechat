/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.data.VirtualGiftReceivedData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandFactory;
import com.projectgoth.fusion.emote.Gift;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.objectcache.ChatObjectManagerRoom;
import com.projectgoth.fusion.objectcache.ChatSourceRoom;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SessionPrx;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class GiftAllTask
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GiftAllTask.class));
    private static final Logger metricslog = Logger.getLogger((String)"GiftAllMetrics");
    private int giftId = -1;
    private String message = null;
    private MessageData messageData = null;
    private ChatSourceRoom chatRoom = null;
    private ChatObjectManagerRoom objectManager = null;
    private SessionPrx sessionProxy = null;

    public GiftAllTask(int giftId, String message, MessageData messageData, ChatSourceRoom chatRoom, ChatObjectManagerRoom objectManager, SessionPrx sessionPrx) {
        this.giftId = giftId;
        this.message = message;
        this.messageData = messageData;
        this.chatRoom = chatRoom;
        this.objectManager = objectManager;
        this.sessionProxy = sessionPrx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void run() {
        block19: {
            block18: {
                long executionBeginTime = System.currentTimeMillis();
                this.objectManager.getGiftAllSemaphore().acquireUninterruptibly();
                long acquisitionTime = System.currentTimeMillis();
                ChatSource chatSource = ChatSource.createChatSourceForChatRoom(this.sessionProxy, this.chatRoom);
                try {
                    try {
                        String userName = this.messageData.username;
                        ChatRoomData chatRoomData = this.chatRoom.getNewRoomData();
                        if (!this.chatRoom.isParticipant(userName)) {
                            log.warn((Object)(userName + " left the chatroom " + chatRoomData.name + ". Dropping the gift all request"));
                            Object var18_14 = null;
                            this.objectManager.getGiftAllSemaphore().release();
                            return;
                        }
                        List<String> participants = Arrays.asList(this.chatRoom.getParticipants(userName));
                        if (participants.size() == 0) {
                            this.messageData.messageText = "There are no other users in the room.";
                            chatSource.sendMessageToSender(this.messageData);
                            break block18;
                        }
                        Content contentBean = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
                        VirtualGiftData gift = contentBean.getVirtualGift(this.giftId, null, userName);
                        double lowPriceThreshold = contentBean.isGiftLowPrice(gift, userName);
                        String[] args = this.messageData.messageText.toLowerCase().split(" ");
                        EmoteCommand emoteCommand = EmoteCommandFactory.getEmoteCommand(args[0].substring(1), ChatSource.ChatType.CHATROOM_CHAT, this.objectManager.getIcePrxFinder());
                        Gift.giftAll(chatRoomData, this.message, this.chatRoom.getMaximumMessageLength(userName), lowPriceThreshold >= 0.0, VirtualGiftReceivedData.PurchaseLocationEnum.CHATROOM_COMMAND, participants, gift, this.messageData, emoteCommand.emoteCommandData, chatSource, this.objectManager.getIcePrxFinder());
                        long executionCompleteTime = System.currentTimeMillis();
                        metricslog.info((Object)String.format("chatRoom:%s, available semaphore permits: %d,  numParticipants:%d, semaphoreAcquisitionTime:%d, taskExecution:%d", chatRoomData.name, this.objectManager.getGiftAllSemaphore().availablePermits(), participants.size(), acquisitionTime - executionBeginTime, executionCompleteTime - executionBeginTime));
                        break block19;
                    }
                    catch (FusionException e) {
                        log.error((Object)e, (Throwable)((Object)e));
                        this.messageData.messageText = e.getMessage();
                        try {
                            chatSource.sendMessageToSender(this.messageData);
                        }
                        catch (Exception excep) {
                        }
                        Object var18_17 = null;
                        this.objectManager.getGiftAllSemaphore().release();
                        return;
                    }
                    catch (CreateException e) {
                        log.error((Object)e, (Throwable)e);
                        this.messageData.messageText = "Unable to gift all, at the moment. Please try again later.";
                        try {
                            chatSource.sendMessageToSender(this.messageData);
                        }
                        catch (Exception excep) {
                        }
                        Object var18_18 = null;
                        this.objectManager.getGiftAllSemaphore().release();
                        return;
                    }
                    catch (RemoteException e) {
                        log.error((Object)e, (Throwable)e);
                        this.messageData.messageText = "Unable to gift all, at the moment. Please try again later.";
                        try {
                            chatSource.sendMessageToSender(this.messageData);
                        }
                        catch (Exception excep) {
                        }
                        Object var18_19 = null;
                        this.objectManager.getGiftAllSemaphore().release();
                        return;
                    }
                    catch (FusionEJBException e) {
                        log.error((Object)e, (Throwable)e);
                        this.messageData.messageText = e.getMessage();
                        try {
                            chatSource.sendMessageToSender(this.messageData);
                        }
                        catch (Exception excep) {
                        }
                        Object var18_20 = null;
                        this.objectManager.getGiftAllSemaphore().release();
                        return;
                    }
                }
                catch (Throwable throwable) {
                    Object var18_21 = null;
                    this.objectManager.getGiftAllSemaphore().release();
                    throw throwable;
                }
            }
            Object var18_15 = null;
            this.objectManager.getGiftAllSemaphore().release();
            return;
        }
        Object var18_16 = null;
        this.objectManager.getGiftAllSemaphore().release();
    }
}

