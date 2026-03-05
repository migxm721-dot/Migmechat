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
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.gateway.packet.sticker.FusionPktStickerPackListOld;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktGetStickerPackListOld
extends FusionRequest {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetStickerPackListOld.class));
    private static final char DELIMITER = ' ';

    public FusionPktGetStickerPackListOld() {
        super((short)938);
    }

    public FusionPktGetStickerPackListOld(short transactionId) {
        super((short)938, transactionId);
    }

    public FusionPktGetStickerPackListOld(FusionPacket packet) {
        super(packet);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            return FusionPktGetStickerPackListOld.processRequest(this.transactionId, connection.getUsername());
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
        StringBuilder packIdsStrBuilder = new StringBuilder();
        StringBuilder packVersionStrBuilder = new StringBuilder();
        for (EmoticonPackData packData : stickerPackList) {
            if (packIdsStrBuilder.length() > 0) {
                packIdsStrBuilder.append(' ');
            }
            packIdsStrBuilder.append(packData.getId());
            if (packVersionStrBuilder.length() > 0) {
                packVersionStrBuilder.append(' ');
            }
            packVersionStrBuilder.append(packData.getVersion());
        }
        FusionPktStickerPackListOld pktStickerPackList = new FusionPktStickerPackListOld(transactionId);
        pktStickerPackList.setStickerPackIDs(packIdsStrBuilder.toString());
        pktStickerPackList.setStickerPackVersions(packVersionStrBuilder.toString());
        return pktStickerPackList.toArray();
    }
}

