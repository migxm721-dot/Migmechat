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
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktGiftHotKeysOld;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.CreateException;

public class FusionPktGetGiftOld
extends FusionRequest {
    public FusionPktGetGiftOld() {
        super((short)935);
    }

    public FusionPktGetGiftOld(short transactionId) {
        super((short)935, transactionId);
    }

    public FusionPktGetGiftOld(FusionPacket packet) {
        super(packet);
    }

    public Integer getLimit() {
        return this.getIntField((short)1);
    }

    public void setLimit(int limit) {
        this.setField((short)1, limit);
    }

    public boolean sessionRequired() {
        return true;
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
            FusionPktGiftHotKeysOld returnPacket = new FusionPktGiftHotKeysOld(this.transactionId);
            returnPacket.setHotKeys(hotkeysReturned);
            returnPacket.setGiftNames(giftNameReturned);
            returnPacket.setGiftPrice(giftPrice);
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

