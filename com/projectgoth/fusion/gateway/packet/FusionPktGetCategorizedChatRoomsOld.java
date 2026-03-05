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
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.ChatroomCategoryData;
import com.projectgoth.fusion.gateway.ChatRoomList;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.LoginChatroomCategoryList;
import com.projectgoth.fusion.gateway.packet.FusionPktChatRoomNotificationOld;
import com.projectgoth.fusion.gateway.packet.FusionPktChatRoomOld;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktGetCategorizedChatRoomsCompleteOld;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.RegistryPrx;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktGetCategorizedChatRoomsOld
extends FusionRequest {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetCategorizedChatRoomsOld.class));

    public FusionPktGetCategorizedChatRoomsOld() {
        super((short)716);
    }

    public FusionPktGetCategorizedChatRoomsOld(short transactionId) {
        super((short)716, transactionId);
    }

    public FusionPktGetCategorizedChatRoomsOld(FusionPacket packet) {
        super(packet);
    }

    public Short getCategory() {
        return this.getShortField((short)1);
    }

    public void setCategory(short category) {
        this.setField((short)1, category);
    }

    public Byte getRefresh() {
        return this.getByteField((short)2);
    }

    public void setRefresh(byte refresh) {
        this.setField((short)2, refresh);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            ChatRoomList.Page page;
            Short category = this.getCategory();
            if (category == null) {
                throw new Exception("Category not specified");
            }
            Byte byteVal = this.getRefresh();
            if (byteVal == null) {
                throw new Exception("Refresh not specified");
            }
            boolean refresh = byteVal == 1;
            RegistryPrx registryPrx = connection.findRegistry();
            if (registryPrx == null) {
                throw new Exception("Failed to locate registry");
            }
            FusionPktGetCategorizedChatRoomsCompleteOld complete = new FusionPktGetCategorizedChatRoomsCompleteOld(this.transactionId);
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
                complete.setCategoryFooter(chatroomCategory.refreshDisplayString);
            }
            ArrayList<FusionPacket> fusionPkts = new ArrayList<FusionPacket>();
            for (ChatRoomData room : page.getPageList()) {
                fusionPkts.add(new FusionPktChatRoomOld(this.transactionId, room, category));
            }
            fusionPkts.add(complete);
            int notificationInterval = connection.getGateway().getChatRoomNotificationInterval();
            long lastNotificationSent = connection.getLastChatRoomNotificationSent();
            if (System.currentTimeMillis() - lastNotificationSent > (long)notificationInterval) {
                fusionPkts.add(new FusionPktChatRoomNotificationOld(connection));
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

