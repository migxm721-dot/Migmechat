/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.ChatroomCategoryData;
import com.projectgoth.fusion.fdl.packets.FusionPktDataGetChatroomCategories;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.LoginChatroomCategoryList;
import com.projectgoth.fusion.gateway.packet.FusionPktChatroomCategory;
import com.projectgoth.fusion.gateway.packet.FusionPktGetChatroomCategoriesComplete;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class FusionPktGetChatroomCategories
extends FusionPktDataGetChatroomCategories {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetChatroomCategories.class));

    public FusionPktGetChatroomCategories(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktGetChatroomCategories(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        String username = connection.getUsername();
        ArrayList<FusionPacket> packets = new ArrayList<FusionPacket>();
        try {
            List<ChatroomCategoryData> chatroomCategories = LoginChatroomCategoryList.get(username, connection.getUserID());
            for (ChatroomCategoryData category : chatroomCategories) {
                packets.add(new FusionPktChatroomCategory(this.transactionId, category));
            }
            packets.add(new FusionPktGetChatroomCategoriesComplete(this.transactionId));
        }
        catch (Exception e) {
            log.error((Object)("Unable to get chatroom categories for user [" + username + "]: " + e.getMessage()));
        }
        return packets.toArray(new FusionPacket[packets.size()]);
    }
}

