/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  javax.ejb.CreateException
 */
package com.projectgoth.fusion.gateway.packet;

import Ice.LocalException;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.packets.FusionPktDataGetChatrooms;
import com.projectgoth.fusion.gateway.ChatRoomList;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktChatroom;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktGetChatroomsComplete;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.RegistryPrx;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.CreateException;

public class FusionPktGetChatrooms
extends FusionPktDataGetChatrooms {
    public FusionPktGetChatrooms(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktGetChatrooms(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            Integer pages;
            ChatRoomList list;
            RegistryPrx registryPrx = connection.findRegistry();
            if (registryPrx == null) {
                throw new Exception("Failed to locate registry");
            }
            List<ChatRoomData> chatRooms = null;
            String infoText = null;
            String[] chatroomNameList = this.getChatroomNameList();
            if (chatroomNameList == null) {
                list = connection.getChatRoomList();
                Byte page = this.getPage();
                chatRooms = page == null ? list.getList(registryPrx, connection.getCountryID(), this.getSearchString()) : list.getPage(registryPrx, page.intValue());
                pages = list.pages();
            } else {
                list = connection.getChatRoomList();
                chatRooms = list.getInitialList(registryPrx, connection.getUsername(), chatroomNameList);
                pages = 1;
                MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
                infoText = misEJB.getInfoText(24);
            }
            ArrayList<FusionPacket> fusionPkts = new ArrayList<FusionPacket>();
            if (chatRooms != null) {
                for (ChatRoomData room : chatRooms) {
                    fusionPkts.add(new FusionPktChatroom(this.transactionId, room));
                }
            }
            FusionPktGetChatroomsComplete complete = new FusionPktGetChatroomsComplete(this.transactionId);
            complete.setNumberOfPages(pages.byteValue());
            if (infoText != null && infoText.length() > 0) {
                complete.setInfoText(infoText);
            }
            fusionPkts.add(complete);
            return fusionPkts.toArray(new FusionPacket[fusionPkts.size()]);
        }
        catch (CreateException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get chat rooms - " + e.getMessage()).toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get chat rooms - " + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (LocalException e) {
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Failed to get chat rooms").toArray();
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get chat rooms - " + e.getMessage()).toArray();
        }
    }
}

