/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import Ice.LocalException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.ChatroomCategoryData;
import com.projectgoth.fusion.fdl.packets.FusionPktDataGetCategorizedChatrooms;
import com.projectgoth.fusion.gateway.ChatRoomList;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.LoginChatroomCategoryList;
import com.projectgoth.fusion.gateway.packet.FusionPktChatRoomNotificationOld;
import com.projectgoth.fusion.gateway.packet.FusionPktChatroom;
import com.projectgoth.fusion.gateway.packet.FusionPktChatroomNotification;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktGetCategorizedChatroomsComplete;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.RegistryPrx;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktGetCategorizedChatrooms
extends FusionPktDataGetCategorizedChatrooms {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetCategorizedChatrooms.class));

    public FusionPktGetCategorizedChatrooms() {
    }

    public FusionPktGetCategorizedChatrooms(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktGetCategorizedChatrooms(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            ChatRoomList.Page page;
            Short category = this.getChatroomCategoryId();
            if (category == null) {
                throw new Exception("Category not specified");
            }
            Boolean boolVal = this.getDoRefresh();
            if (boolVal == null) {
                throw new Exception("Refresh not specified");
            }
            boolean refresh = boolVal;
            RegistryPrx registryPrx = connection.findRegistry();
            if (registryPrx == null) {
                throw new Exception("Failed to locate registry");
            }
            FusionPktGetCategorizedChatroomsComplete complete = new FusionPktGetCategorizedChatroomsComplete(this.transactionId);
            ChatroomCategoryData chatroomCategory = LoginChatroomCategoryList.getChatroomCategory(category.intValue());
            if (chatroomCategory.id.intValue() == ChatroomCategoryData.SpecialCategoriesEnum.BOOKMARKED.value()) {
                page = connection.getChatRoomList().getFavouriteChatRooms(registryPrx, connection.getUsername(), refresh);
            } else if (chatroomCategory.id.intValue() == ChatroomCategoryData.SpecialCategoriesEnum.RECENT.value()) {
                page = connection.getChatRoomList().getRecentChatRooms(registryPrx, connection.getUsername(), refresh);
            } else if (chatroomCategory.id.intValue() == ChatroomCategoryData.SpecialCategoriesEnum.POPULAR.value()) {
                page = connection.getChatRoomList().getPopularChatRooms(registryPrx, connection.getCountryID(), refresh);
            } else if (chatroomCategory.id.intValue() == ChatroomCategoryData.SpecialCategoriesEnum.RECOMMENDED.value()) {
                page = connection.getChatRoomList().getRecommendedChatRooms(registryPrx, connection.getUserID(), refresh);
                if (page == null || page.getFullList().size() == 0) {
                    page = connection.getChatRoomList().getPopularChatRooms(registryPrx, connection.getCountryID(), refresh);
                }
            } else {
                page = connection.getChatRoomList().getChatrooms(registryPrx, refresh, chatroomCategory.id);
            }
            if (page.itemsLeft() > 0) {
                complete.setCategoryFooterText(chatroomCategory.refreshDisplayString);
            }
            ArrayList<FusionPacket> fusionPkts = new ArrayList<FusionPacket>();
            for (ChatRoomData room : page.getPageList()) {
                fusionPkts.add(new FusionPktChatroom(this.transactionId, room, category));
            }
            fusionPkts.add(complete);
            int notificationInterval = connection.getGateway().getChatRoomNotificationInterval();
            long lastNotificationSent = connection.getLastChatRoomNotificationSent();
            if (System.currentTimeMillis() - lastNotificationSent > (long)notificationInterval) {
                if (SystemPropertyEntities.Temp.Cache.fi83Week3UseAutogeneratedPacketsEnabled.getValue().booleanValue()) {
                    fusionPkts.add(new FusionPktChatroomNotification(this.transactionId, connection));
                } else {
                    fusionPkts.add(new FusionPktChatRoomNotificationOld(connection));
                }
                connection.chatRoomNotificationSent();
            }
            return fusionPkts.toArray(new FusionPacket[fusionPkts.size()]);
        }
        catch (CreateException e) {
            log.error((Object)"Failed to get chat rooms -", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get chat rooms - " + e.getMessage()).toArray();
        }
        catch (RemoteException e) {
            log.error((Object)"Failed to get chat rooms -", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get chat rooms - " + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (LocalException e) {
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Failed to get chat rooms").toArray();
        }
        catch (Exception e) {
            log.error((Object)"Failed to get chat rooms -", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get chat rooms - " + e.getMessage()).toArray();
        }
    }
}

