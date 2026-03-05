/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.ChatroomCategoryData;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.LoginChatroomCategoryList;
import com.projectgoth.fusion.gateway.packet.FusionPktChatRoomCategoryOld;
import com.projectgoth.fusion.gateway.packet.FusionPktGetChatRoomCategoriesCompleteOld;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class FusionPktGetChatRoomCategoriesOld
extends FusionRequest {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetChatRoomCategoriesOld.class));

    public FusionPktGetChatRoomCategoriesOld() {
        super((short)713);
    }

    public FusionPktGetChatRoomCategoriesOld(short transactionId) {
        super((short)713, transactionId);
    }

    public FusionPktGetChatRoomCategoriesOld(FusionPacket packet) {
        super(packet);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        String username = connection.getUsername();
        ArrayList<FusionPacket> packets = new ArrayList<FusionPacket>();
        try {
            List<ChatroomCategoryData> chatroomCategories = LoginChatroomCategoryList.get(username, connection.getUserID());
            for (ChatroomCategoryData category : chatroomCategories) {
                packets.add(new FusionPktChatRoomCategoryOld(this.transactionId, category));
            }
            packets.add(new FusionPktGetChatRoomCategoriesCompleteOld(this.transactionId));
        }
        catch (Exception e) {
            log.error((Object)("Unable to get chatroom categories for user [" + username + "]: " + e.getMessage()));
        }
        return packets.toArray(new FusionPacket[packets.size()]);
    }
}

