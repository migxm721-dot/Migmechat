/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.ByteBufferHelper;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ContentUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.EmoticonData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktEmoticon;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktGetEmoticon;
import com.projectgoth.fusion.gateway.packet.FusionPktGetEmoticonsComplete;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.gateway.packet.PacketUtils;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.recommendation.collector.DataCollectorUtils;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.CreateException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

public class FusionPktGetEmoticonPreSE378
extends FusionRequest {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetEmoticon.class));
    private static final Short SMALLEST_VERSION_NUMBER = Short.MIN_VALUE;
    private static final String HOTKEY_DELIMITER = " ";
    public static final String ERROR_MSG_HOT_KEYS_NOT_SPECIFIED = "Unable to get emoticon - Hot keys not specified";
    public static final String ERROR_MSG_OPTIMAL_EMOTICON_HEIGHT_NOT_SET = "Unable to get emoticon - Optimal emoticon height not set";
    public static final String ERROR_MSG_ERROR_LOADING_HOTKEY = "Error loading hotkey";

    public FusionPktGetEmoticonPreSE378() {
        super((short)913);
    }

    public FusionPktGetEmoticonPreSE378(short transactionId) {
        super((short)913, transactionId);
    }

    public FusionPktGetEmoticonPreSE378(FusionPacket packet) {
        super(packet);
    }

    public String getHotKeys() {
        return this.getStringField((short)1);
    }

    public void setHotKeys(String hotKeys) {
        this.setField((short)1, hotKeys);
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

    private static short getClosestSupportedVgSize(short requestedVgSize, boolean usePNG) {
        String imgFormatType = usePNG ? VirtualGiftData.ImageFormatType.PNG.getCode() : VirtualGiftData.ImageFormatType.GIF.getCode();
        short[] availableResolutions = ContentUtils.getSupportedVirtualGiftResolutions(imgFormatType);
        if (availableResolutions == null) {
            return 0;
        }
        Short result = ContentUtils.getValueOrRoundDown(availableResolutions, requestedVgSize);
        if (result == null) {
            return 0;
        }
        return result;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            return FusionPktGetEmoticonPreSE378.processRequest(this.transactionId, this.getHotKeys(), connection.getStickerSize(), connection.getOptimalEmoticonHeight(), connection.getVgSize(), connection.getFontHeight(), connection.getDeviceType(), connection.getClientVersion());
        }
        catch (CreateException e) {
            PacketUtils.ErrorLogData errorLogData = new PacketUtils.ErrorLogData(913, log, Level.ERROR, DataCollectorUtils.newErrorID(), this.transactionId, "Unable to get emoticon - Server Error");
            errorLogData.setExceptionCause(e);
            return PacketUtils.logAndPrepareFusionPktError(errorLogData).toArray();
        }
        catch (RemoteException e) {
            PacketUtils.ErrorLogData errorLogData = new PacketUtils.ErrorLogData(913, log, Level.WARN, DataCollectorUtils.newErrorID(), this.transactionId, "Unable to get emoticon - Internal Error");
            errorLogData.setExceptionCause(e);
            return PacketUtils.logAndPrepareFusionPktError(errorLogData).toArray();
        }
        catch (Exception e) {
            PacketUtils.ErrorLogData errorLogData = new PacketUtils.ErrorLogData(913, log, Level.WARN, DataCollectorUtils.newErrorID(), this.transactionId, "Unable to get emoticon - Unhandled exception");
            errorLogData.setExceptionCause(e);
            return PacketUtils.logAndPrepareFusionPktError(errorLogData).toArray();
        }
    }

    public static FusionPacket[] processRequest(short transactionId, String hotKeys, int stickerSize, int emoticonHeight, int vgSize, int fontHeight, ClientType deviceType, short clientVersion) throws Exception {
        boolean hiResEnabled;
        if (emoticonHeight <= 0) {
            String errorMessage = ERROR_MSG_OPTIMAL_EMOTICON_HEIGHT_NOT_SET;
            String detailedMessage = "ConnectionI has an incorrect state.Cause:[Unable to get emoticon - Optimal emoticon height not set].DeviceType:[" + (Object)((Object)deviceType) + "].Client Version:[" + clientVersion + "].EmoticonHeight:[" + emoticonHeight + "]";
            PacketUtils.ErrorLogData errorLogData = new PacketUtils.ErrorLogData(913, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionId, ERROR_MSG_OPTIMAL_EMOTICON_HEIGHT_NOT_SET);
            errorLogData.setDetailedErrorMessage(detailedMessage);
            return PacketUtils.logAndPrepareFusionPktError(errorLogData).toArray();
        }
        if (StringUtil.isBlank(hotKeys)) {
            String errorMessage = ERROR_MSG_HOT_KEYS_NOT_SPECIFIED;
            String detailedMessage = "Invalid field value sent by client.Cause:[Unable to get emoticon - Hot keys not specified].DeviceType:[" + (Object)((Object)deviceType) + "].Client Version:[" + clientVersion + "]." + "Blank HotKeys Length:[" + (hotKeys == null ? "NA" : Integer.valueOf(hotKeys.length())) + "]";
            PacketUtils.ErrorLogData errorLogData = new PacketUtils.ErrorLogData(913, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionId, ERROR_MSG_HOT_KEYS_NOT_SPECIFIED);
            errorLogData.setDetailedErrorMessage(detailedMessage);
            return PacketUtils.logAndPrepareFusionPktError(errorLogData).toArray();
        }
        boolean usePNG = ConnectionI.isMidletVersionAndAbove(deviceType, clientVersion, 400);
        boolean createBackwardCompatiblePayload = !ConnectionI.isMidletVersionAndAbove(deviceType, clientVersion, 410);
        boolean deviceCanReceiveStickersNatively = ContentUtils.deviceCanReceiveStickersNatively(deviceType, clientVersion);
        if (SystemProperty.getBool(SystemPropertyEntities.ContentService.ENABLE_FUSION_PKT_GET_EMOTICON_HIGH_RES)) {
            boolean minClientVerGuardSetEnabled = SystemProperty.getBool(SystemPropertyEntities.GuardsetEnabled.FUSION_PKT_GET_EMOTICON_HI_RES_SUPPORT);
            Short minimumVersion = minClientVerGuardSetEnabled ? MemCacheOrEJB.getMinimumClientVersionForAccess(deviceType.value(), GuardCapabilityEnum.FUSION_PKT_GET_EMOTICON_HI_RES_SUPPORT.value()) : SMALLEST_VERSION_NUMBER;
            hiResEnabled = minimumVersion != null && clientVersion >= minimumVersion;
        } else {
            hiResEnabled = false;
        }
        int requestedVgSize = vgSize > 0 ? vgSize : fontHeight;
        short optimumVirtualGiftSize = FusionPktGetEmoticonPreSE378.getClosestSupportedVgSize((short)requestedVgSize, usePNG);
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("connection.getVgSize():%s,connection.getFontHeight():%s,connection.getStickerSize():%s,requestedVgSize:%s,optimumVirtualGiftSize:%s", vgSize, fontHeight, stickerSize, requestedVgSize, (int)optimumVirtualGiftSize));
        }
        RequestedDimensionSpec requestedDimensionSpec = new RequestedDimensionSpec();
        requestedDimensionSpec.stickerSize = FusionPktGetEmoticonPreSE378.coalesce(stickerSize, optimumVirtualGiftSize, emoticonHeight);
        requestedDimensionSpec.closestSupportedVgSize = (short)FusionPktGetEmoticonPreSE378.coalesce(optimumVirtualGiftSize, emoticonHeight);
        requestedDimensionSpec.emoticonHeight = emoticonHeight;
        String[] hotKeyArray = hotKeys.split(HOTKEY_DELIMITER);
        ArrayList<FusionPacket> responseList = new ArrayList<FusionPacket>(hotKeyArray.length);
        for (String hotKey : hotKeyArray) {
            try {
                AssetData assetData = FusionPktGetEmoticonPreSE378.getEmoticonAssetData(hotKey, requestedDimensionSpec, usePNG, deviceCanReceiveStickersNatively, createBackwardCompatiblePayload);
                if (assetData == null) {
                    assetData = FusionPktGetEmoticonPreSE378.getVirtualGiftAssetData(hotKey, requestedDimensionSpec, usePNG, hiResEnabled, createBackwardCompatiblePayload);
                }
                if (assetData == null) continue;
                responseList.add(FusionPktGetEmoticonPreSE378.createFusionPktEmoticon(transactionId, assetData, deviceCanReceiveStickersNatively));
            }
            catch (Exception ex) {
                PacketUtils.ErrorLogData errorLogData = new PacketUtils.ErrorLogData(913, log, Level.WARN, DataCollectorUtils.newErrorID(), transactionId, FusionPktGetEmoticonPreSE378.getErrorMessagePrefixForErrorLoadingHotKeys(hotKey));
                errorLogData.setExceptionCause(ex);
                FusionPktError pktError = PacketUtils.logAndPrepareFusionPktError(errorLogData);
                responseList.add(pktError);
            }
        }
        if (SystemProperty.getBool(SystemPropertyEntities.GatewaySettings.ALWAYS_SEND_PKT_GET_EMOTICONS_COMPLETE)) {
            responseList.add(new FusionPktGetEmoticonsComplete(transactionId));
        } else if (hotKeyArray.length > 1) {
            responseList.add(new FusionPktGetEmoticonsComplete(transactionId));
        }
        return responseList.toArray(new FusionPacket[responseList.size()]);
    }

    public static String getErrorMessagePrefixForErrorLoadingHotKeys(String hotKey) {
        return "Error loading hotkey(" + hotKey + ")";
    }

    private static ByteBuffer getContentData(EmoticonData.TypeEnum type, String filePath, boolean createBackwardCompatiblePayload) throws Exception {
        if (SystemProperty.getBool(SystemPropertyEntities.ContentService.LOAD_EMOTICONS_FROM_REDIS)) {
            return FusionPktGetEmoticonPreSE378.getContentDataFromRedis(type, filePath, createBackwardCompatiblePayload);
        }
        return FusionPktGetEmoticonPreSE378.getContentDataFromFile(type, filePath, createBackwardCompatiblePayload);
    }

    private static ByteBuffer getContentDataFromFile(EmoticonData.TypeEnum type, String filePath, boolean createBackwardCompatiblePayload) throws IOException {
        ByteBuffer buffer = ByteBufferHelper.readFile(new File(filePath));
        if (createBackwardCompatiblePayload) {
            if (type == EmoticonData.TypeEnum.ANIMATION) {
                buffer.getInt();
                buffer = ByteBuffer.wrap(ByteBufferHelper.readBytes(buffer, buffer.getInt()));
            } else if (type == EmoticonData.TypeEnum.AUDIO) {
                buffer = ByteBuffer.wrap(ByteBufferHelper.readBytes(buffer, buffer.getInt()));
            }
        }
        return buffer;
    }

    private static ByteBuffer getContentDataFromRedis(EmoticonData.TypeEnum type, String filePath, boolean createBackwardCompatiblePayload) throws Exception {
        ByteBuffer byteBuffer;
        Jedis slaveInst = null;
        String key = null;
        try {
            slaveInst = Redis.getWebResourcesSlaveInstance();
            key = (Object)((Object)Redis.KeySpace.WEB_RESOURCE_ENTITY) + filePath;
            byte[] keyBytes = Redis.safeEncoderEncode(key);
            byte[] content = slaveInst.get(keyBytes);
            ByteBuffer buffer = ByteBuffer.wrap(content);
            if (createBackwardCompatiblePayload) {
                if (type == EmoticonData.TypeEnum.ANIMATION) {
                    buffer.getInt();
                    buffer = ByteBuffer.wrap(ByteBufferHelper.readBytes(buffer, buffer.getInt()));
                } else if (type == EmoticonData.TypeEnum.AUDIO) {
                    buffer = ByteBuffer.wrap(ByteBufferHelper.readBytes(buffer, buffer.getInt()));
                }
            }
            byteBuffer = buffer;
            Object var10_10 = null;
        }
        catch (Exception e) {
            try {
                log.error((Object)("Exception loading emoticon content from Redis: key=" + key + " e=" + e), (Throwable)e);
                throw e;
            }
            catch (Throwable throwable) {
                Object var10_11 = null;
                Redis.disconnect(slaveInst, log);
                throw throwable;
            }
        }
        Redis.disconnect(slaveInst, log);
        return byteBuffer;
    }

    private static AssetData getVirtualGiftAssetData(String hotKey, RequestedDimensionSpec requestedDimensionSpec, boolean usePNG, boolean enableHiRes, boolean backwardCompatiblePayload) throws Exception {
        Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
        VirtualGiftData virtualGift = contentEJB.getVirtualGiftByHotKey(hotKey);
        if (virtualGift != null) {
            AssetData assetData = new AssetData();
            assetData.type = EmoticonData.TypeEnum.IMAGE;
            assetData.alias = virtualGift.getName();
            assetData.hotkey = hotKey;
            if (enableHiRes) {
                VirtualGiftData.ImageFormatType imageFormatType = usePNG ? VirtualGiftData.ImageFormatType.PNG : VirtualGiftData.ImageFormatType.GIF;
                assetData.imagelocation = ContentUtils.getValueOrRoundDown(virtualGift.getAvailableImageLocations(imageFormatType), Short.valueOf(requestedDimensionSpec.closestSupportedVgSize));
            } else {
                if (requestedDimensionSpec.emoticonHeight > 16) {
                    requestedDimensionSpec.emoticonHeight = 16;
                }
                String location = "";
                switch (requestedDimensionSpec.emoticonHeight) {
                    case 12: {
                        location = usePNG ? virtualGift.getLocation12x12PNG() : virtualGift.getLocation12x12GIF();
                        break;
                    }
                    case 14: {
                        location = usePNG ? virtualGift.getLocation14x14PNG() : virtualGift.getLocation14x14GIF();
                        break;
                    }
                    case 16: {
                        location = usePNG ? virtualGift.getLocation16x16PNG() : virtualGift.getLocation16x16GIF();
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("No emoticon found for hotkey " + hotKey + " with height " + requestedDimensionSpec.emoticonHeight);
                    }
                }
                assetData.imagelocation = location;
            }
            assetData.packID = null;
            assetData.alternateHotKeys = Collections.emptyList();
            assetData.contentData = FusionPktGetEmoticonPreSE378.getContentData(assetData.type, assetData.imagelocation, backwardCompatiblePayload);
            return assetData;
        }
        return null;
    }

    private static AssetData getEmoticonAssetData(String hotKey, RequestedDimensionSpec requestedDimensionSpec, boolean usePNG, boolean deviceCanNativelyReceiveStickers, boolean backwardCompatiblePayload) throws Exception {
        Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
        int stickerSize = deviceCanNativelyReceiveStickers ? requestedDimensionSpec.stickerSize : requestedDimensionSpec.emoticonHeight;
        EmoticonData emoticonData = contentEJB.getByHotKey(hotKey, requestedDimensionSpec.emoticonHeight, stickerSize);
        if (log.isDebugEnabled()) {
            log.debug((Object)String.format("requestedDimensionSpec:[%s];deviceCanNativelyReceiveStickers:%s", requestedDimensionSpec, deviceCanNativelyReceiveStickers));
            log.debug((Object)String.format("getByHotKey(%s,%s,%s)", hotKey, requestedDimensionSpec.emoticonHeight, stickerSize));
        }
        if (emoticonData != null) {
            String imagelocation;
            AssetData assetData = new AssetData();
            assetData.alternateHotKeys = emoticonData.alternateHotKeys;
            assetData.type = emoticonData.type;
            assetData.alias = emoticonData.alias;
            assetData.hotkey = hotKey;
            String string = imagelocation = usePNG ? emoticonData.locationPNG : emoticonData.location;
            if (deviceCanNativelyReceiveStickers) {
                if (assetData.type == EmoticonData.TypeEnum.STICKER) {
                    assetData.contentData = null;
                    assetData.imagelocation = FusionPktGetEmoticonPreSE378.getStickerImageURLLocation(emoticonData.locationPNG);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Asset type:" + (Object)((Object)assetData.type) + ";StickerSupportedMode:" + deviceCanNativelyReceiveStickers + ";imagelocation:[" + assetData.imagelocation + "]"));
                    }
                } else {
                    assetData.contentData = FusionPktGetEmoticonPreSE378.getContentData(assetData.type, imagelocation, backwardCompatiblePayload);
                    assetData.imagelocation = null;
                }
            } else if (assetData.type == EmoticonData.TypeEnum.STICKER) {
                String imageFullPath = FusionPktGetEmoticonPreSE378.getStickerImageFullPath(emoticonData.locationPNG);
                assetData.imagelocation = null;
                assetData.contentData = FusionPktGetEmoticonPreSE378.getContentData(assetData.type, imageFullPath, backwardCompatiblePayload);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Asset type:" + (Object)((Object)assetData.type) + ";StickerSupportedMode:" + deviceCanNativelyReceiveStickers + ";imagelocation:[" + imageFullPath + "]"));
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Asset type:" + (Object)((Object)assetData.type) + ";StickerSupportedMode:" + deviceCanNativelyReceiveStickers + ";imagelocation:[" + imagelocation + "]"));
                }
                assetData.imagelocation = null;
                assetData.contentData = FusionPktGetEmoticonPreSE378.getContentData(assetData.type, imagelocation, backwardCompatiblePayload);
            }
            assetData.packID = emoticonData.emoticonPackID;
            return assetData;
        }
        return null;
    }

    private static FusionPktEmoticon createFusionPktEmoticon(short transactionId, AssetData assetData, boolean deviceCanNativelyReceiveStickers) {
        FusionPktEmoticon pktEmoticon = new FusionPktEmoticon(transactionId);
        pktEmoticon.setAlias(assetData.alias);
        pktEmoticon.setAlternateHotKeys(StringUtil.join(assetData.alternateHotKeys, HOTKEY_DELIMITER));
        if (deviceCanNativelyReceiveStickers) {
            pktEmoticon.setContentType((byte)assetData.type.value());
            if (assetData.type == EmoticonData.TypeEnum.STICKER) {
                String url = StringUtil.isBlank(assetData.imagelocation) ? "" : assetData.imagelocation;
                pktEmoticon.setURL(url);
                if (assetData.packID != null) {
                    pktEmoticon.setPackID(assetData.packID);
                }
            } else {
                pktEmoticon.setContent(assetData.contentData.array());
            }
        } else {
            if (assetData.type == EmoticonData.TypeEnum.STICKER) {
                pktEmoticon.setContentType((byte)EmoticonData.TypeEnum.IMAGE.value());
            } else {
                pktEmoticon.setContentType((byte)assetData.type.value());
            }
            pktEmoticon.setContent(assetData.contentData.array());
        }
        pktEmoticon.setHotKey(assetData.hotkey);
        return pktEmoticon;
    }

    public static String getStickerImageURLLocation(String location) {
        return String.format("%s/%s/%s", SystemProperty.get(SystemPropertyEntities.Default.MIG33_WEB_BASE_URL), SystemProperty.get(SystemPropertyEntities.ContentService.STICKER_IMAGE_BASE_FOLDER_URL), location);
    }

    public static String getStickerImageFullPath(String location) {
        return String.format("%s/%s", SystemProperty.get(SystemPropertyEntities.ContentService.STICKER_IMAGE_PHYSICAL_FOLDER), location);
    }

    private static class RequestedDimensionSpec {
        public short closestSupportedVgSize;
        public int emoticonHeight;
        public int stickerSize;

        private RequestedDimensionSpec() {
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[closestSupportedVgSize=");
            builder.append(this.closestSupportedVgSize);
            builder.append(", emoticonHeight=");
            builder.append(this.emoticonHeight);
            builder.append(", stickerSize=");
            builder.append(this.stickerSize);
            builder.append("]");
            return builder.toString();
        }
    }

    private static class AssetData {
        public String hotkey;
        public String alias;
        public String imagelocation;
        public ByteBuffer contentData;
        public List<String> alternateHotKeys;
        public Integer packID;
        public EmoticonData.TypeEnum type;

        private AssetData() {
        }
    }
}

