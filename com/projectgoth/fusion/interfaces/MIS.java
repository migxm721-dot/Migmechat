/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.hashtag.data.HashTagData
 *  javax.ejb.EJBObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.AlertMessageData;
import com.projectgoth.fusion.data.CashReceiptData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.FileData;
import com.projectgoth.fusion.data.HandsetData;
import com.projectgoth.fusion.data.HandsetInstructionsData;
import com.projectgoth.fusion.data.MenuData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.ResellerData;
import com.projectgoth.fusion.data.ScrapbookData;
import com.projectgoth.hashtag.data.HashTagData;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import javax.ejb.EJBObject;

public interface MIS
extends EJBObject {
    public List getApplicationMenuOptions(int var1) throws RemoteException;

    public int getApplicationMenuVersion(int var1, short var2, String var3) throws RemoteException;

    public void resetCachedSystemProperties() throws RemoteException;

    public Map getSystemProperties() throws RemoteException;

    public void updateSystemProperty(String var1, String var2) throws RemoteException;

    public String getHelpText(int var1) throws RemoteException;

    public String getInfoText(int var1) throws RemoteException;

    public List getCountries() throws RemoteException;

    public List getCountriesSupportedHashtag() throws RemoteException;

    public List getCurrencies() throws RemoteException;

    public List getMenus(int var1, short var2, int var3) throws RemoteException;

    public MenuData getMenu(int var1) throws RemoteException;

    public CountryData getCountry(int var1) throws RemoteException;

    public CountryData getCountryByISOCode(String var1) throws RemoteException;

    public CountryData getCountryByLocation(int var1) throws RemoteException;

    public CountryData getCountryByIDDCode(int var1, String var2) throws RemoteException;

    public CountryData getCountryFromIPNumber(Double var1) throws RemoteException;

    public double getVoiceRate(int var1, int var2, int var3, int var4) throws Exception, RemoteException;

    public CurrencyData getCurrency(String var1) throws RemoteException;

    public void setCurrency(CurrencyData var1) throws RemoteException;

    public double getSMSRate(String var1) throws Exception, RemoteException;

    public double getPremiumCost(int var1) throws Exception, RemoteException;

    public double getPremiumFee(int var1) throws Exception, RemoteException;

    public String getCountryCurrency(int var1) throws Exception, RemoteException;

    public void addToMailList(String var1) throws RemoteException;

    public boolean isVASBuild(String var1) throws RemoteException;

    public CashReceiptData getCashReceiptData(int var1) throws RemoteException;

    public CashReceiptData createCashReceipt(CashReceiptData var1, AccountEntrySourceData var2) throws RemoteException;

    public void matchCashReceipt(CashReceiptData var1, AccountEntrySourceData var2) throws RemoteException;

    public void deleteCashReceipt(String var1) throws RemoteException;

    public void logMISLogin(String var1, String var2, boolean var3) throws RemoteException;

    public double calculateBonusCredit(double var1) throws RemoteException;

    public void allowUserToReRegister(String var1) throws RemoteException;

    public AlertMessageData getAlertMessageData(String var1) throws RemoteException;

    public void createAlertMessage(AlertMessageData var1) throws RemoteException;

    public void updateAlertMessage(AlertMessageData var1) throws RemoteException;

    public ResellerData getResellerData(String var1) throws RemoteException;

    public void createReseller(ResellerData var1) throws RemoteException;

    public void updateReseller(ResellerData var1) throws RemoteException;

    public void updateCountry(CountryData var1) throws RemoteException;

    public void changeStaffPassword(String var1, String var2, String var3) throws RemoteException;

    public String newFileID() throws RemoteException;

    public FileData getFile(String var1) throws RemoteException;

    public ScrapbookData saveFile(FileData var1, String var2) throws RemoteException;

    public void saveFileToScrapbooks(String var1, String[] var2, String var3, String var4) throws RemoteException;

    public void updateFileFromScrapbook(ScrapbookData var1) throws RemoteException;

    public void publishFileFromScrapbook(String var1, int var2, String var3, boolean var4) throws RemoteException;

    public void unpublishFileFromScrapbook(String var1, int var2) throws RemoteException;

    public void deleteFileFromScrapbook(String var1, int var2) throws RemoteException;

    public void setFileReportedFromScrapbook(String var1, int var2) throws RemoteException;

    public void setFilePrivateFromScrapbook(String var1) throws RemoteException;

    public void removeAllInstancesOfFile(String var1) throws RemoteException;

    public ScrapbookData getFileFromScrapbook(int var1) throws RemoteException;

    public List getScrapbook(String var1) throws RemoteException;

    public List getScrapbook(String var1, boolean var2) throws RemoteException;

    public List getGallery(String var1, String var2) throws RemoteException;

    public Double getScrapbookSize(String var1) throws RemoteException;

    public List getHandsetVendors() throws RemoteException;

    public List getHandsetInstructions() throws RemoteException;

    public HandsetInstructionsData getHandsetInstruction(int var1) throws RemoteException;

    public List getHandsetVendorPrefixes() throws RemoteException;

    public List getHandsetDetails(String var1) throws RemoteException;

    public List getDefaultHandsetDetails() throws RemoteException;

    public HandsetData getHandsetDetail(String var1, String var2) throws RemoteException;

    public HandsetData getHandsetDetail(Integer var1) throws RemoteException;

    public void createHandset(HandsetData var1) throws RemoteException;

    public void updateHandsetDetail(HandsetData var1) throws RemoteException;

    public void createHandsetInstructions(HandsetInstructionsData var1) throws RemoteException;

    public void updateHandsetInstructions(HandsetInstructionsData var1) throws RemoteException;

    public void removeMerchantPinAuthentication(int var1) throws RemoteException;

    public String getMerchantPinEmail(int var1) throws RemoteException;

    public void sendMerchantResetPinNotification(int var1) throws RemoteException;

    public List getEmoteCommands() throws RemoteException;

    public List getAuthenticatedAccessControlData() throws RemoteException;

    public HashTagData getHashTagData(String var1, int var2) throws RemoteException;

    public void updateHashTagData(String var1, int var2, String var3) throws RemoteException;

    public void updatePromotedPost(List var1) throws RemoteException;

    public List getPromotedPost(int var1, boolean var2, int var3) throws RemoteException;

    public ReputationLevelData getReputationLevelDataForLevel(int var1) throws RemoteException;

    public int getMaximumSystemMigLevel() throws RemoteException;

    public boolean logMig33UserAction(int var1, Enums.Mig33UserActionMisLogEnum var2, String var3) throws Exception, RemoteException;
}

