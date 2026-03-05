/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.impl.outcome.MMv2Outcomes
 *  javax.ejb.EJBObject
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
import com.projectgoth.fusion.data.StoreCategoryData;
import com.projectgoth.fusion.data.StoreItemData;
import com.projectgoth.fusion.data.StoreItemInventoryData;
import com.projectgoth.fusion.data.StoreItemInventorySummaryData;
import com.projectgoth.fusion.data.ThemeData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.data.VirtualGiftReceivedData;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.leto.common.impl.outcome.MMv2Outcomes;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.ejb.EJBObject;

public interface Content
extends EJBObject {
    public int getOptimalEmoticonHeight(String var1, int var2) throws FusionEJBException, RemoteException;

    public EmoticonData getEmoticon(int var1) throws FusionEJBException, RemoteException;

    public EmoticonData getEmoticon(String var1, int var2) throws FusionEJBException, RemoteException;

    public List getAllEmoticonDataByHotKey(String var1) throws FusionEJBException, RemoteException;

    public EmoticonData getByHotKey(String var1, int var2, int var3) throws FusionEJBException, RemoteException;

    public List getEmoticons(String var1) throws FusionEJBException, RemoteException;

    public List getSecurityQeustions() throws FusionEJBException, RemoteException;

    public List getStickerDataListForUser(String var1) throws RemoteException;

    public EmoticonData getStickerDataByNameForUser(String var1, String var2) throws RemoteException;

    public List getStickerPackIDListForUser(String var1) throws RemoteException;

    public List getStickerPackDataListForUser(String var1) throws RemoteException;

    public int getEmoticonPackCountForUser(String var1) throws RemoteException;

    public List getAllEmoticons() throws FusionEJBException, RemoteException;

    public List getEmoticonPack(int var1) throws FusionEJBException, RemoteException;

    public List getStickerDataListForStickerPack(int var1) throws RemoteException;

    public EmoticonPackData getStickerPackData(int var1) throws RemoteException;

    public List getStickerPackDataList(Collection var1) throws RemoteException;

    public EmoticonData getStickerData(int var1) throws RemoteException;

    public List getTopWallpaper(int var1) throws Exception, RemoteException;

    public List getTopRingtones(int var1) throws Exception, RemoteException;

    public List getEmotes() throws RemoteException;

    public void updateEmoticonPackStatus(String var1, int var2, int var3) throws RemoteException;

    public void buyEmoticonPack(String var1, int var2, AccountEntrySourceData var3) throws FusionEJBException, RemoteException;

    public void saveMobileContentItem(ContentData var1, String var2, int var3) throws RemoteException;

    public String buyMobileContentItem(String var1, int var2, AccountEntrySourceData var3) throws RemoteException;

    public void processILoopAPICall(String var1, String var2, String var3, AccountEntrySourceData var4) throws RemoteException;

    public void refundMobileContentItem(String var1, String var2, ContentPurchasedData.RefundReasonEnum var3, AccountEntrySourceData var4) throws RemoteException;

    public void refundMobileContentItemFromMIS(String var1, int var2, String var3, AccountEntrySourceData var4) throws RemoteException;

    public void sendMobileContentDownloadURLInSMS(String var1, int var2, AccountEntrySourceData var3) throws RemoteException;

    public void onPurchaseVirtualGift(String var1, Map var2, VirtualGiftData var3, boolean var4, String var5, String var6, boolean var7) throws RemoteException;

    public void onPurchaseVirtualGift(String var1, Map var2, VirtualGiftData var3, boolean var4, String var5, String var6) throws RemoteException;

    public List getVirtualGifts(String var1, int var2, int var3) throws RemoteException;

    public ArrayList getVirtualGiftCategoryNames() throws RemoteException;

    public List getVirtualGiftForCategory(String var1, String var2, int var3, int var4) throws RemoteException;

    public List getFeaturedPopularNewVirtualGifts(String var1, int var2, int var3) throws RemoteException;

    public List getFeaturedVirtualGifts(String var1, int var2, int var3) throws RemoteException;

    public List getPopularVirtualGifts(String var1, int var2, int var3) throws RemoteException;

    public List getNewVirtualGifts(String var1, int var2, int var3) throws RemoteException;

    public List getRecentVirtualGifts(String var1, int var2, int var3) throws RemoteException;

    public List searchVirtualGifts(String var1, int var2, String var3, int var4, boolean var5) throws RemoteException;

    public List getRecentGiftsReceivedBy(String var1, String var2, int var3, int var4) throws RemoteException;

    public List getRecentGiftsSentBy(String var1, String var2, int var3, int var4) throws RemoteException;

    public VirtualGiftData getVirtualGiftDetails(VirtualGiftData var1) throws RemoteException;

    public VirtualGiftData getVirtualGiftByHotKey(String var1) throws RemoteException;

    public VirtualGiftData getVirtualGift(Integer var1, String var2, String var3) throws FusionEJBException, RemoteException;

    public double isGiftLowPrice(VirtualGiftData var1, String var2) throws FusionEJBException, RemoteException;

    public void buyVirtualGift(String var1, String var2, int var3, int var4, boolean var5, String var6, String var7, AccountEntrySourceData var8) throws FusionEJBException, RemoteException;

    public Map buyVirtualGiftForMultipleUsers(String var1, List var2, VirtualGiftData var3, int var4, boolean var5, String var6, boolean var7, boolean var8, AccountEntrySourceData var9) throws FusionEJBException, RemoteException;

    public boolean rateVirtualGift(int var1, String var2, int var3) throws FusionEJBException, RemoteException;

    public void buyAvatarItem(int var1, int var2, int var3, AccountEntrySourceData var4) throws RemoteException;

    public void incrementStoreItemSoldByNumber(StoreItemData.TypeEnum var1, int var2, int var3, Connection var4) throws SQLException, RemoteException;

    public StoreItemData getStoreItem(int var1) throws RemoteException;

    public void resetCachedRewardPrograms() throws RemoteException;

    public List getRewardProgramProcessorMapping() throws RemoteException;

    public List getRewardPrograms() throws RemoteException;

    public void giveRewards(int var1, int var2, AccountEntrySourceData var3) throws RemoteException;

    public void giveRewards(int var1, int var2, AccountEntrySourceData var3, List var4, Map var5) throws RemoteException;

    public int getRewardProgramsCompletionCount(int var1, int var2) throws RemoteException;

    public void updateUserOwnedChatRoomSizes(String var1, int var2) throws RemoteException;

    public boolean buyPaidEmote(String var1, PaidEmoteData var2, int var3, AccountEntrySourceData var4) throws RemoteException;

    public Vector getPaintWarsSpecialItems() throws RemoteException;

    public String createMigboTextPostForUser(int var1, String var2, String var3, String var4, String var5, ClientType var6, SSOEnums.View var7) throws RemoteException;

    public void enforceFreeGiftToReferrerRules(String var1, String var2) throws FusionEJBException, RemoteException;

    public int recordVirtualGift(String var1, String var2, VirtualGiftData var3, int var4, boolean var5, String var6) throws FusionEJBException, RemoteException;

    public void billVirtualGiftForMultipleUsers(String var1, VirtualGiftData var2, HashMap var3, AccountEntrySourceData var4) throws FusionEJBException, RemoteException;

    public StoreItemData getStoreItem(String var1, int var2) throws RemoteException;

    public VirtualGiftData getVirtualGift(Integer var1, String var2) throws FusionEJBException, RemoteException;

    public AvatarItemData getAvatarItem(int var1) throws RemoteException;

    public ThemeData getThemeById(int var1) throws RemoteException;

    public VirtualGiftReceivedData getVirtualGiftReceived(String var1, String var2, int var3) throws RemoteException;

    public ListDataWrapper getVirtualGiftsReceived(String var1, String var2, int var3, int var4) throws RemoteException;

    public ListDataWrapper getStoreItemsByType_old(String var1, int var2, String var3, String var4, int var5, int var6, boolean var7) throws FusionEJBException, RemoteException;

    public ListDataWrapper searchStoreItems(String var1, String var2, StoreItemData.TypeEnum var3, Integer var4, double var5, double var7, String var9, String var10, int var11, int var12, boolean var13) throws FusionEJBException, RemoteException;

    public ListDataWrapper getStoreItemsByType(String var1, int var2, String var3, String var4, int var5, int var6, boolean var7) throws FusionEJBException, RemoteException;

    public List getStoreCategories(int var1, int var2, boolean var3) throws FusionEJBException, RemoteException;

    public StoreCategoryData getStoreCategory(int var1, int var2, boolean var3) throws FusionEJBException, RemoteException;

    public ListDataWrapper getStoreItemsByCategory(String var1, int var2, String var3, String var4, int var5, int var6, boolean var7) throws FusionEJBException, RemoteException;

    public List addStoreItemsInventory(Map var1, int var2, StoreItemInventoryData.StoreItemInventoryLocationEnum var3) throws RemoteException;

    public long addStoreItemInventory(int var1, int var2, StoreItemInventoryData.StoreItemInventoryLocationEnum var3) throws RemoteException;

    public void addStoreItemInventoryReceived(long var1, int var3, Integer var4, StoreItemData.TypeEnum var5) throws RemoteException;

    public StoreItemInventorySummaryData getStoreItemInventory(int var1, int var2) throws RemoteException;

    public List getInventoryVirtualGifts(int var1) throws RemoteException;

    public List getStoreItemsInventoryByType(int var1, StoreItemData.TypeEnum var2) throws RemoteException;

    public Map giveVirtualGiftForMultipleUsers(String var1, List var2, StoreItemData var3, int var4, boolean var5, String var6) throws FusionEJBException, RemoteException;

    public void giveRewards(MMv2Outcomes var1, AccountEntrySourceData var2) throws RemoteException;
}

