/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.hashtag.data.HashTagData
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
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
import java.util.List;
import java.util.Map;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface MISLocal
extends EJBLocalObject {
    public List getApplicationMenuOptions(int var1) throws EJBException;

    public int getApplicationMenuVersion(int var1, short var2, String var3) throws EJBException;

    public void resetCachedSystemProperties();

    public Map getSystemProperties() throws EJBException;

    public void updateSystemProperty(String var1, String var2) throws EJBException;

    public String getHelpText(int var1) throws EJBException;

    public String getInfoText(int var1) throws EJBException;

    public List getCountries() throws EJBException;

    public List getCountriesSupportedHashtag() throws EJBException;

    public List getCurrencies() throws EJBException;

    public List getMenus(int var1, short var2, int var3) throws EJBException;

    public MenuData getMenu(int var1) throws EJBException;

    public CountryData getCountry(int var1) throws EJBException;

    public CountryData getCountryByISOCode(String var1) throws EJBException;

    public CountryData getCountryByLocation(int var1) throws EJBException;

    public CountryData getCountryByIDDCode(int var1, String var2) throws EJBException;

    public CountryData getCountryFromIPNumber(Double var1) throws EJBException;

    public CurrencyData getCurrency(String var1) throws EJBException;

    public CashReceiptData getCashReceiptData(int var1);

    public CashReceiptData createCashReceipt(CashReceiptData var1, AccountEntrySourceData var2) throws EJBException;

    public void matchCashReceipt(CashReceiptData var1, AccountEntrySourceData var2) throws EJBException;

    public void deleteCashReceipt(String var1) throws EJBException;

    public void logMISLogin(String var1, String var2, boolean var3) throws EJBException;

    public double calculateBonusCredit(double var1) throws EJBException;

    public void allowUserToReRegister(String var1) throws EJBException;

    public AlertMessageData getAlertMessageData(String var1) throws EJBException;

    public void createAlertMessage(AlertMessageData var1) throws EJBException;

    public void updateAlertMessage(AlertMessageData var1) throws EJBException;

    public ResellerData getResellerData(String var1) throws EJBException;

    public void createReseller(ResellerData var1) throws EJBException;

    public void updateReseller(ResellerData var1) throws EJBException;

    public void updateCountry(CountryData var1) throws EJBException;

    public void changeStaffPassword(String var1, String var2, String var3) throws EJBException;

    public String newFileID();

    public FileData getFile(String var1) throws EJBException;

    public ScrapbookData saveFile(FileData var1, String var2) throws EJBException;

    public void saveFileToScrapbooks(String var1, String[] var2, String var3, String var4) throws EJBException;

    public void updateFileFromScrapbook(ScrapbookData var1) throws EJBException;

    public void publishFileFromScrapbook(String var1, int var2, String var3, boolean var4) throws EJBException;

    public void unpublishFileFromScrapbook(String var1, int var2) throws EJBException;

    public void deleteFileFromScrapbook(String var1, int var2) throws EJBException;

    public void setFileReportedFromScrapbook(String var1, int var2) throws EJBException;

    public void setFilePrivateFromScrapbook(String var1) throws EJBException;

    public void removeAllInstancesOfFile(String var1) throws EJBException;

    public ScrapbookData getFileFromScrapbook(int var1) throws EJBException;

    public List getScrapbook(String var1) throws EJBException;

    public List getScrapbook(String var1, boolean var2) throws EJBException;

    public List getGallery(String var1, String var2) throws EJBException;

    public Double getScrapbookSize(String var1) throws EJBException;

    public List getHandsetVendors() throws EJBException;

    public List getHandsetInstructions() throws EJBException;

    public HandsetInstructionsData getHandsetInstruction(int var1) throws EJBException;

    public List getHandsetVendorPrefixes() throws EJBException;

    public List getHandsetDetails(String var1) throws EJBException;

    public List getDefaultHandsetDetails() throws EJBException;

    public HandsetData getHandsetDetail(String var1, String var2) throws EJBException;

    public HandsetData getHandsetDetail(Integer var1) throws EJBException;

    public void createHandset(HandsetData var1) throws EJBException;

    public void updateHandsetDetail(HandsetData var1) throws EJBException;

    public void createHandsetInstructions(HandsetInstructionsData var1) throws EJBException;

    public void updateHandsetInstructions(HandsetInstructionsData var1) throws EJBException;

    public void removeMerchantPinAuthentication(int var1);

    public String getMerchantPinEmail(int var1);

    public void sendMerchantResetPinNotification(int var1);

    public List getEmoteCommands() throws EJBException;

    public List getAuthenticatedAccessControlData() throws EJBException;

    public HashTagData getHashTagData(String var1, int var2) throws EJBException;

    public void updateHashTagData(String var1, int var2, String var3) throws EJBException;

    public void updatePromotedPost(List var1) throws EJBException;

    public List getPromotedPost(int var1, boolean var2, int var3) throws EJBException;

    public ReputationLevelData getReputationLevelDataForLevel(int var1) throws EJBException;

    public int getMaximumSystemMigLevel() throws EJBException;

    public boolean logMig33UserAction(int var1, Enums.Mig33UserActionMisLogEnum var2, String var3) throws Exception;
}

