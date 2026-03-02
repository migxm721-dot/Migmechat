/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.packets.FusionPktDataGetGift;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktGiftHotkeys;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.CreateException;

public class FusionPktGetGift
extends FusionPktDataGetGift {
    public FusionPktGetGift(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktGetGift(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
            List list = contentEJB.getPopularVirtualGifts(connection.getUsername(), 0, this.getLimit());
            if (list == null) {
                return new FusionPacket[]{new FusionPktOk(this.transactionId)};
            }
            String[] hotkeysReturned = new String[list.size()];
            String[] giftNameReturned = new String[list.size()];
            String[] giftPrice = new String[list.size()];
            int index = 0;
            for (VirtualGiftData giftData : list) {
                hotkeysReturned[index] = giftData.getHotKey();
                giftNameReturned[index] = giftData.getName();
                giftPrice[index] = giftData.getRoundedPrice() + " " + giftData.getCurrency();
                ++index;
            }
            FusionPktGiftHotkeys returnPacket = new FusionPktGiftHotkeys(this.transactionId);
            returnPacket.setHotkeyList(hotkeysReturned);
            returnPacket.setNameList(giftNameReturned);
            returnPacket.setPriceList(giftPrice);
            return new FusionPacket[]{returnPacket};
        }
        catch (CreateException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get gift - Failed to create ContentEJB").toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get gift - " + e.getClass().getName() + ":" + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (Exception e) {
            if (e.getMessage() == null) {
                return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get gift - " + e.getClass().getName() + ":" + e.getMessage()).toArray();
            }
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get gift - " + e.getMessage()).toArray();
        }
    }
}

