/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.emote.EmoteCommandException;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import com.projectgoth.fusion.restapi.enums.MigboAccessMemberTypeEnum;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeSet;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ContentUtils {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ContentUtils.class));

    public static Short getValueOrRoundDown(short[] sortedAvailableValue, short requestedValue) {
        if (sortedAvailableValue == null || sortedAvailableValue.length == 0) {
            return null;
        }
        int index = Arrays.binarySearch(sortedAvailableValue, requestedValue);
        if (index < 0) {
            int wouldBeLocation = -(index + 1);
            if (wouldBeLocation >= sortedAvailableValue.length) {
                return sortedAvailableValue[sortedAvailableValue.length - 1];
            }
            if (wouldBeLocation > 0) {
                return sortedAvailableValue[wouldBeLocation - 1];
            }
            return sortedAvailableValue[0];
        }
        return sortedAvailableValue[index];
    }

    public static <K extends Comparable<K>, V> V getValueOrRoundDown(SortedMap<K, V> sortedMap, K requestedKey) {
        if (sortedMap == null || sortedMap.isEmpty()) {
            return null;
        }
        Object value = sortedMap.get(requestedKey);
        if (value != null) {
            return value;
        }
        SortedMap<K, V> mapOfLesserThanRequested = sortedMap.headMap(requestedKey);
        if (mapOfLesserThanRequested == null || mapOfLesserThanRequested.size() == 0) {
            return sortedMap.entrySet().iterator().next().getValue();
        }
        return mapOfLesserThanRequested.get(mapOfLesserThanRequested.lastKey());
    }

    public static String normalizeImageFormatType(String imgFormat) {
        return StringUtil.trimmedUpperCase(imgFormat);
    }

    public static Map.Entry<String, short[]>[] getSupportedVirtualGiftResolutions() {
        return SingletonHolder.SUPPORTED_RESOLUTIONS.getValue().asEntryArray;
    }

    public static short[] getSupportedVirtualGiftResolutions(String imgFormat) {
        return SingletonHolder.SUPPORTED_RESOLUTIONS.getValue().asMap.get(ContentUtils.normalizeImageFormatType(imgFormat));
    }

    public static short[] getSupportedStickerPackThumbnailResolutions() {
        return SingletonHolder.SUPPORTED_STICKER_PACK_IMAGE_RESOLUTIONS.getValue();
    }

    public static void invalidateSupportVirtualGiftResolutionsCache() {
        SingletonHolder.SUPPORTED_RESOLUTIONS.invalidateCache();
    }

    public static boolean isStickersEnabled() {
        return SystemProperty.getBool(SystemPropertyEntities.ContentService.ENABLE_STICKERS);
    }

    public static boolean deviceCanReceiveStickersNatively(ClientType deviceEnum, short clientVersion) {
        try {
            if (SystemProperty.getBool(SystemPropertyEntities.GuardsetEnabled.RECEIVE_STICKERS_NATIVE_SUPPORT)) {
                Short minVersion = MemCacheOrEJB.getMinimumClientVersionForAccess(deviceEnum.value(), GuardCapabilityEnum.RECEIVE_STICKERS_NATIVE_SUPPORT.value());
                return minVersion != null && clientVersion >= minVersion;
            }
            return true;
        }
        catch (Exception ex) {
            throw new RuntimeException("Error while checking whether device [" + (Object)((Object)deviceEnum) + "] version [" + clientVersion + "] supports receiving of stickers natively", ex);
        }
    }

    public static boolean userCanSendStickers(int userId) throws RemoteException, CreateException {
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            ContentUtils.checkUserCanSendStickers(userEJB, userId);
            return true;
        }
        catch (EmoteCommandException ex) {
            ErrorCause errorCause = ex.getErrorCause();
            String errorCauseCodeStr = errorCause == null ? "" : errorCause.getCode();
            log.info((Object)("UserId [" + userId + "] disallowed from sending stickers. Error Cause:[" + errorCauseCodeStr + "]"));
            if (errorCause == ErrorCause.EmoteCommandError.INSUFFICIENT_REPUTATION_LEVEL) {
                return false;
            }
            if (errorCause == ErrorCause.EmoteCommandError.INSUFFICIENT_RIGHTS) {
                return false;
            }
            throw new RuntimeException("Unexpected exception:" + ex.getMessage(), ex);
        }
        catch (CreateException ce) {
            throw ce;
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new RuntimeException("Unexpected exception:" + ex.getMessage(), ex);
        }
    }

    public static void checkUserCanSendStickers(User userEJB, int userId) throws EmoteCommandException, EJBException, RemoteException, CreateException, FusionEJBException {
        boolean hasGuardCapability;
        int minMigLevel;
        int userReputationLevel = MemCacheOrEJB.getUserReputationLevel(null, userId);
        if (userReputationLevel < (minMigLevel = SystemProperty.getInt(SystemPropertyEntities.ContentService.MIN_REPUTATION_LEVEL_FOR_SENDING_STICKERS))) {
            throw new EmoteCommandException(ErrorCause.EmoteCommandError.INSUFFICIENT_REPUTATION_LEVEL, minMigLevel);
        }
        if (SystemProperty.getBool(SystemPropertyEntities.GuardsetEnabled.SEND_STICKERS_ALLOWED) && !(hasGuardCapability = userEJB.isUserInMigboAccessList(userId, MigboAccessMemberTypeEnum.WHITELIST.value(), GuardCapabilityEnum.SEND_STICKERS_ALLOWED.value()))) {
            throw new EmoteCommandException(ErrorCause.EmoteCommandError.INSUFFICIENT_RIGHTS, new Object[0]);
        }
    }

    private static class SingletonHolder {
        private static final long CACHE_TIMEOUT_MILLIES = 120000L;
        public static final LazyLoader<ImageTypeResolutionData> SUPPORTED_RESOLUTIONS = new LazyLoader<ImageTypeResolutionData>("VIRTUAL_GIFT_DATA_SUPPORTED_RESOLUTIONS", 120000L){

            @Override
            protected ImageTypeResolutionData fetchValue() {
                String[] imageFormats;
                HashMap<String, short[]> asMap = new HashMap<String, short[]>();
                for (String imgFmt : imageFormats = SystemProperty.getArray(SystemPropertyEntities.ContentService.VIRTUAL_GIFT_IMAGE_FORMATS)) {
                    imgFmt = ContentUtils.normalizeImageFormatType(imgFmt);
                    short[] resolutionArray = SystemProperty.getShortArray(SystemPropertyEntities.ContentService.getVirtualGiftResolutionSettingEnum(imgFmt));
                    TreeSet<Short> resolutionSortedSet = new TreeSet<Short>();
                    for (short resolution : resolutionArray) {
                        resolutionSortedSet.add(resolution);
                    }
                    short[] resolutionSortedArray = new short[resolutionSortedSet.size()];
                    int i = 0;
                    for (Short resolution : resolutionSortedSet) {
                        resolutionSortedArray[i] = resolution;
                        ++i;
                    }
                    asMap.put(imgFmt, resolutionSortedArray);
                }
                return new ImageTypeResolutionData(asMap);
            }
        };
        public static final LazyLoader<short[]> SUPPORTED_STICKER_PACK_IMAGE_RESOLUTIONS = new LazyLoader<short[]>("SUPPORTED_STICKER_PACK_IMAGE_RESOLUTIONS", 120000L){

            @Override
            protected short[] fetchValue() {
                short[] heights = SystemProperty.getShortArray(SystemPropertyEntities.ContentService.STICKER_PACK_IMAGE_RESOLUTION);
                TreeSet<Short> sortedHeightsSet = new TreeSet<Short>();
                for (short h : heights) {
                    sortedHeightsSet.add(h);
                }
                short[] sortedHeightsArray = new short[sortedHeightsSet.size()];
                int idx = 0;
                for (Short h : sortedHeightsSet) {
                    sortedHeightsArray[idx] = h;
                    ++idx;
                }
                return sortedHeightsArray;
            }
        };

        private SingletonHolder() {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ImageTypeResolutionData {
        public final Map.Entry<String, short[]>[] asEntryArray;
        public final Map<String, short[]> asMap;

        public ImageTypeResolutionData(Map<String, short[]> asMap) {
            this.asEntryArray = asMap.entrySet().toArray(new Map.Entry[asMap.size()]);
            this.asMap = asMap;
        }
    }
}

