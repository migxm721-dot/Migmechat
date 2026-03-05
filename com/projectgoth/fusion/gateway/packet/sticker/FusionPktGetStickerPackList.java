/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet.sticker;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ContentUtils;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.data.EmoticonPackData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.packets.FusionPktDataGetStickerPackList;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.sticker.FusionPktStickerPackList;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktGetStickerPackList
extends FusionPktDataGetStickerPackList {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetStickerPackList.class));

    public FusionPktGetStickerPackList(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktGetStickerPackList(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            return FusionPktGetStickerPackList.processRequest(this.transactionId, connection.getUsername());
        }
        catch (CreateException e) {
            log.error((Object)"Create exception", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get sticker pack list - Failed to create contentEJB").toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get sticker pack list - " + e.getClass().getName() + ":" + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (Exception e) {
            log.error((Object)"Unhandled exception on FusionPktGetStickerPackList", (Throwable)e);
            if (e.getMessage() == null) {
                return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get sticker pack list - " + e.getClass().getName() + ":" + e.getMessage()).toArray();
            }
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get sticker pack list - " + e.getMessage()).toArray();
        }
    }

    public static FusionPacket[] processRequest(short transactionId, String username) throws RemoteException, CreateException {
        if (!ContentUtils.isStickersEnabled()) {
            return new FusionPktError(transactionId, FusionPktError.Code.UNDEFINED, "Feature disabled").toArray();
        }
        Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
        List stickerPackList = contentEJB.getStickerPackDataListForUser(username);
        String[] packIdList = new String[stickerPackList.size()];
        String[] versionList = new String[stickerPackList.size()];
        for (int i = 0; i < stickerPackList.size(); ++i) {
            EmoticonPackData epd = (EmoticonPackData)stickerPackList.get(i);
            packIdList[i] = epd.getId().toString();
            versionList[i] = epd.getVersion().toString();
        }
        FusionPktStickerPackList pktStickerPackList = new FusionPktStickerPackList(transactionId);
        pktStickerPackList.setStickerPackIdList(packIdList);
        pktStickerPackList.setVersionList(versionList);
        return pktStickerPackList.toArray();
    }
}

