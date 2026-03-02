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
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.EmoticonData;
import com.projectgoth.fusion.data.EmoticonPackData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.gateway.packet.sticker.FusionPktEndStickerPackOld;
import com.projectgoth.fusion.gateway.packet.sticker.FusionPktStickerPackOld;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.packet.FusionPacket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktGetStickerPackOld
extends FusionRequest {
    private static final short FIELD_PACK_IDS = 1;
    private static final char DELIMITER = ' ';
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetStickerPackOld.class));

    public FusionPktGetStickerPackOld() {
        super((short)940);
    }

    public FusionPktGetStickerPackOld(short transactionId) {
        super((short)940, transactionId);
    }

    public FusionPktGetStickerPackOld(FusionPacket packet) {
        super(packet);
    }

    public String getPackIDs() {
        return this.getStringField((short)1);
    }

    public void setPackIDs(String packIDs) {
        this.setField((short)1, packIDs);
    }

    public boolean sessionRequired() {
        return true;
    }

    private static int coalesce(int ... vals) {
        for (int v : vals) {
            if (v <= 0) continue;
            return v;
        }
        return vals[vals.length - 1];
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            short requestedThumbnailSize = (short)FusionPktGetStickerPackOld.coalesce(connection.getVgSize(), connection.getOptimalEmoticonHeight());
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("vgsize:[%s],optimalEmoticonHeight:[%s]=>effective requestedThumbnailSize:[%s]", connection.getVgSize(), connection.getOptimalEmoticonHeight(), requestedThumbnailSize));
            }
            return FusionPktGetStickerPackOld.processRequest(this.transactionId, this.getPackIDs(), requestedThumbnailSize);
        }
        catch (CreateException e) {
            log.error((Object)"Create exception", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get sticker pack - Failed to create contentEJB").toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get sticker pack - " + e.getClass().getName() + ":" + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (Exception e) {
            log.error((Object)"Unhandled exception on FusionPktGetStickerPack", (Throwable)e);
            if (e.getMessage() == null) {
                return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get sticker pack - " + e.getClass().getName() + ":" + e.getMessage()).toArray();
            }
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get sticker pack - " + e.getMessage()).toArray();
        }
    }

    public static String generateThumbnailUrlForStickerPack(String catalogimage, String thumbnailName, int height) {
        boolean previewImage = SystemProperty.getBool(SystemPropertyEntities.ContentService.LOAD_THUMBNAIL_STICKER_DIR_AS_PREVIEW);
        if (previewImage) {
            return String.format("%s/%s/%s/%s_%s%s", SystemProperty.get(SystemPropertyEntities.Default.MIG33_WEB_BASE_URL), SystemProperty.get(SystemPropertyEntities.ContentService.STICKER_PACK_THUMBNAIL_BASE_FOLDER_URL), catalogimage, thumbnailName, height, SystemProperty.get(SystemPropertyEntities.ContentService.STICKER_PACK_THUMBNAIL_DEFAULT_EXTENSION));
        }
        return String.format("%s/%s/%s_%s%s", SystemProperty.get(SystemPropertyEntities.Default.MIG33_WEB_BASE_URL), SystemProperty.get(SystemPropertyEntities.ContentService.STICKER_PACK_THUMBNAIL_BASE_FOLDER_URL), thumbnailName, height, SystemProperty.get(SystemPropertyEntities.ContentService.STICKER_PACK_THUMBNAIL_DEFAULT_EXTENSION));
    }

    public static FusionPacket[] processRequest(short transactionId, String packIDsFieldValue, short requestedThumbnailSize) throws CreateException, RemoteException {
        if (!ContentUtils.isStickersEnabled()) {
            return new FusionPktError(transactionId, FusionPktError.Code.UNDEFINED, "Feature disabled").toArray();
        }
        if (packIDsFieldValue == null) {
            return new FusionPktError(transactionId, FusionPktError.Code.UNDEFINED, "Missing pack ids").toArray();
        }
        if (packIDsFieldValue.length() > SystemProperty.getInt(SystemPropertyEntities.GatewaySettings.PKT_GET_STICKER_PACK_MAX_PACKIDS_LENGTH)) {
            return new FusionPktError(transactionId, FusionPktError.Code.UNDEFINED, "Invalid PackIDs length").toArray();
        }
        if (StringUtil.isBlank(packIDsFieldValue)) {
            return new FusionPktEndStickerPackOld(transactionId).toArray();
        }
        List<String> packIDStrList = StringUtil.split(packIDsFieldValue, ' ');
        if (packIDStrList == null) {
            return new FusionPktError(transactionId, FusionPktError.Code.UNDEFINED, "Missing pack ids").toArray();
        }
        if (packIDStrList.size() == 0) {
            return new FusionPktEndStickerPackOld(transactionId).toArray();
        }
        short supportedThumbnailSize = ContentUtils.getValueOrRoundDown(ContentUtils.getSupportedStickerPackThumbnailResolutions(), requestedThumbnailSize);
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("Requested Thumbnail size:[%s];Supported Size:[%s]", requestedThumbnailSize, supportedThumbnailSize));
        }
        ArrayList<Integer> packIDsList = new ArrayList<Integer>(packIDStrList.size());
        for (String packIDStr : packIDStrList) {
            packIDsList.add(Integer.valueOf(packIDStr));
        }
        Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
        List stickerPackDataList = contentEJB.getStickerPackDataList(packIDsList);
        FusionPacket[] fusionPacketReply = new FusionPacket[stickerPackDataList.size() + 1];
        int replyPktNumber = 0;
        for (EmoticonPackData stickerPackData : stickerPackDataList) {
            FusionPktStickerPackOld pktStickerPack = new FusionPktStickerPackOld(transactionId);
            pktStickerPack.setStickerPackID(stickerPackData.getId());
            String thumbnailUrl = FusionPktGetStickerPackOld.generateThumbnailUrlForStickerPack(stickerPackData.getCatalogImage(), stickerPackData.getThumbnailFile(), supportedThumbnailSize);
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("PackID:[%s];Thumbnail Url:[%s];Requested Size:[%s]", stickerPackData.getId(), thumbnailUrl, requestedThumbnailSize));
            }
            pktStickerPack.setStickerPackIconURL(thumbnailUrl);
            pktStickerPack.setStickerPackName(stickerPackData.getName());
            pktStickerPack.setStickerPackVersion(stickerPackData.getVersion().toString());
            StringBuilder stickerHotKeysStrBuilder = new StringBuilder();
            List stickerDataList = contentEJB.getStickerDataListForStickerPack(stickerPackData.getId());
            HashSet<String> addedHotKeys = new HashSet<String>();
            for (int stkDataNum = 0; stkDataNum < stickerDataList.size(); ++stkDataNum) {
                String hotKey = ((EmoticonData)stickerDataList.get((int)stkDataNum)).hotKey;
                if (addedHotKeys.contains(hotKey)) continue;
                if (stkDataNum > 0) {
                    stickerHotKeysStrBuilder.append(' ');
                }
                stickerHotKeysStrBuilder.append(hotKey);
                addedHotKeys.add(hotKey);
            }
            pktStickerPack.setStickerHotkeys(stickerHotKeysStrBuilder.toString());
            fusionPacketReply[replyPktNumber] = pktStickerPack;
            ++replyPktNumber;
        }
        fusionPacketReply[replyPktNumber] = new FusionPktEndStickerPackOld(transactionId);
        return fusionPacketReply;
    }
}

