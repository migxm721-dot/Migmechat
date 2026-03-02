/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.impl.outcome.MMv2Outcomes
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.AvatarItemData;
import com.projectgoth.fusion.data.ContentData;
import com.projectgoth.fusion.data.ContentPurchasedData;
import com.projectgoth.fusion.data.EmoticonData;
import com.projectgoth.fusion.data.EmoticonPackData;
import com.projectgoth.fusion.data.ListDataWrapper;
import com.projectgoth.fusion.data.PaidEmoteData;
import com.projectgoth.fusion.data.RewardProgramCompletionData;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.StoreCategoryData;
import com.projectgoth.fusion.data.StoreItemData;
import com.projectgoth.fusion.data.StoreItemInventoryData;
import com.projectgoth.fusion.data.StoreItemInventorySummaryData;
import com.projectgoth.fusion.data.ThemeData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.data.VirtualGiftReceivedData;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.leto.common.impl.outcome.MMv2Outcomes;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface ContentLocal
extends EJBLocalObject {
    public void invalidateLocalCachedItems();

    public int getOptimalEmoticonHeight(String var1, int var2) throws FusionEJBException, EJBException;

    public EmoticonData getEmoticon(int var1) throws EJBException, FusionEJBException;

    public EmoticonData getEmoticon(String var1, int var2) throws FusionEJBException, EJBException;

    public List getAllEmoticonDataByHotKey(String var1) throws FusionEJBException, EJBException;

    public EmoticonData getByHotKey(String var1, int var2, int var3) throws FusionEJBException, EJBException;

    public List getEmoticons(String var1) throws FusionEJBException, EJBException;

    public List getSecurityQeustions() throws FusionEJBException, EJBException;

    public List getStickerDataListForUser(String var1) throws EJBException;

    public EmoticonData getStickerDataByNameForUser(String var1, String var2);

    public List getStickerPackIDListForUser(String var1) throws EJBException;

    public List getStickerPackDataListForUser(String var1) throws EJBException;

    public int getEmoticonPackCountForUser(String var1);

    public List getAllEmoticons() throws FusionEJBException, EJBException;

    public List getEmoticonPack(int var1) throws FusionEJBException, EJBException;

    public List getStickerDataListForStickerPack(int var1) throws EJBException;

    public EmoticonPackData getStickerPackData(int var1) throws EJBException;

    public List getStickerPackDataList(Collection var1) throws EJBException;

    public EmoticonData getStickerData(int var1) throws EJBException;

    public List getTopWallpaper(int var1) throws Exception;

    public List getTopRingtones(int var1) throws Exception;

    public List getEmotes() throws EJBException;

    public void updateEmoticonPackStatus(String var1, int var2, int var3) throws EJBException;

    public void buyEmoticonPack(String var1, int var2, AccountEntrySourceData var3) throws FusionEJBException, EJBException;

    public void saveMobileContentItem(ContentData var1, String var2, int var3) throws EJBException;

    public String buyMobileContentItem(String var1, int var2, AccountEntrySourceData var3) throws EJBException;

    public void processILoopAPICall(String var1, String var2, String var3, AccountEntrySourceData var4) throws EJBException;

    public void refundMobileContentItem(String var1, String var2, ContentPurchasedData.RefundReasonEnum var3, AccountEntrySourceData var4) throws EJBException;

    public void refundMobileContentItemFromMIS(String var1, int var2, String var3, AccountEntrySourceData var4) throws EJBException;

    public void sendMobileContentDownloadURLInSMS(String var1, int var2, AccountEntrySourceData var3) throws EJBException;

    public void onPurchaseVirtualGift(String var1, Map var2, VirtualGiftData var3, boolean var4, String var5, String var6, boolean var7);

    public void onPurchaseVirtualGift(String var1, Map var2, VirtualGiftData var3, boolean var4, String var5, String var6);

    public List getVirtualGifts(String var1, int var2, int var3) throws EJBException;

    public ArrayList getVirtualGiftCategoryNames() throws EJBException;

    public List getVirtualGiftForCategory(String var1, String var2, int var3, int var4) throws EJBException;

    public List getFeaturedPopularNewVirtualGifts(String var1, int var2, int var3) throws EJBException;

    public List getFeaturedVirtualGifts(String var1, int var2, int var3) throws EJBException;

    public List getPopularVirtualGifts(String var1, int var2, int var3) throws EJBException;

    public List getNewVirtualGifts(String var1, int var2, int var3) throws EJBException;

    public List getRecentVirtualGifts(String var1, int var2, int var3) throws EJBException;

    public List searchVirtualGifts(String var1, int var2, String var3, int var4, boolean var5) throws EJBException;

    public List getRecentGiftsReceivedBy(String var1, String var2, int var3, int var4) throws EJBException;

    public List getRecentGiftsSentBy(String var1, String var2, int var3, int var4) throws EJBException;

    public VirtualGiftData getVirtualGiftDetails(VirtualGiftData var1);

    public VirtualGiftData getVirtualGiftByHotKey(String var1) throws EJBException;

    public VirtualGiftData getVirtualGift(Integer var1, String var2, String var3) throws EJBException, FusionEJBException;

    public double isGiftLowPrice(VirtualGiftData var1, String var2) throws FusionEJBException;

    public void buyVirtualGift(String var1, String var2, int var3, int var4, boolean var5, String var6, String var7, AccountEntrySourceData var8) throws FusionEJBException;

    public Map buyVirtualGiftForMultipleUsers(String var1, List var2, VirtualGiftData var3, int var4, boolean var5, String var6, boolean var7, boolean var8, AccountEntrySourceData var9) throws EJBException, FusionEJBException;

    public boolean rateVirtualGift(int var1, String var2, int var3) throws EJBException, FusionEJBException;

    public void buyAvatarItem(int var1, int var2, int var3, AccountEntrySourceData var4) throws EJBException;

    public void incrementStoreItemSold(StoreItemData.TypeEnum var1, int var2, Connection var3) throws SQLException;

    public void incrementStoreItemSoldByNumber(StoreItemData.TypeEnum var1, int var2, int var3, Connection var4) throws SQLException;

    public StoreItemData getStoreItem(int var1) throws EJBException;

    public void resetCachedRewardPrograms();

    public List getRewardProgramProcessorMapping() throws EJBException;

    public void giveRewards(int var1, int var2, AccountEntrySourceData var3, List var4, Map var5) throws EJBException;

    public RewardProgramCompletionData awardUser(RewardProgramData var1, UserData var2, AccountEntrySourceData var3, List var4, Map var5) throws EJBException;

    public int getRewardProgramsCompletionCount(int var1, int var2) throws EJBException;

    public void updateUserOwnedChatRoomSizes(String var1, int var2);

    public boolean buyPaidEmote(String var1, PaidEmoteData var2, int var3, AccountEntrySourceData var4) throws EJBException;

    public Vector getPaintWarsSpecialItems();

    public String createMigboTextPostForUser(int var1, String var2, String var3, String var4, String var5, ClientType var6, SSOEnums.View var7) throws EJBException;

    public void enforceFreeGiftToReferrerRules(String var1, String var2) throws FusionEJBException, EJBException;

    public int recordVirtualGift(String var1, String var2, VirtualGiftData var3, int var4, boolean var5, String var6) throws FusionEJBException, EJBException;

    public void billVirtualGiftForMultipleUsers(String var1, VirtualGiftData var2, HashMap var3, AccountEntrySourceData var4) throws EJBException, FusionEJBException;

    public StoreItemData getStoreItem(String var1, int var2) throws EJBException;

    public VirtualGiftData getVirtualGift(Integer var1, String var2) throws EJBException, FusionEJBException;

    public AvatarItemData getAvatarItem(int var1) throws EJBException;

    public ThemeData getThemeById(int var1) throws EJBException;

    public VirtualGiftReceivedData getVirtualGiftReceived(String var1, String var2, int var3);

    public ListDataWrapper getVirtualGiftsReceived(String var1, String var2, int var3, int var4);

    public ListDataWrapper getStoreItemsByType_old(String var1, int var2, String var3, String var4, int var5, int var6, boolean var7) throws FusionEJBException;

    public ListDataWrapper searchStoreItems(String var1, String var2, StoreItemData.TypeEnum var3, Integer var4, double var5, double var7, String var9, String var10, int var11, int var12, boolean var13) throws FusionEJBException;

    public ListDataWrapper getStoreItemsByType(String var1, int var2, String var3, String var4, int var5, int var6, boolean var7) throws FusionEJBException;

    public List getStoreCategories(int var1, int var2, boolean var3) throws FusionEJBException;

    public StoreCategoryData getStoreCategory(int var1, int var2, boolean var3) throws FusionEJBException;

    public ListDataWrapper getStoreItemsByCategory(String var1, int var2, String var3, String var4, int var5, int var6, boolean var7) throws FusionEJBException;

    public List addStoreItemsInventory(Map var1, int var2, StoreItemInventoryData.StoreItemInventoryLocationEnum var3);

    public long addStoreItemInventory(int var1, int var2, StoreItemInventoryData.StoreItemInventoryLocationEnum var3);

    public void addStoreItemInventoryReceived(long var1, int var3, Integer var4, StoreItemData.TypeEnum var5);

    public StoreItemInventorySummaryData getStoreItemInventory(int var1, int var2);

    public List getInventoryVirtualGifts(int var1);

    public List getStoreItemsInventoryByType(int var1, StoreItemData.TypeEnum var2);

    public Map giveVirtualGiftForMultipleUsers(String var1, List var2, StoreItemData var3, int var4, boolean var5, String var6) throws EJBException, FusionEJBException;

    public void giveRewards(MMv2Outcomes var1, AccountEntrySourceData var2) throws EJBException;

    public RewardProgramCompletionData awardUser(RewardProgramData var1, UserData var2, AccountEntrySourceData var3, List var4) throws EJBException;
}

