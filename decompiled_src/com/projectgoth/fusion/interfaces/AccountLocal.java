/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.common.EJBExceptionWithErrorCause;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.data.AccountBalanceData;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.BankTransferIntentData;
import com.projectgoth.fusion.data.BankTransferReceivedData;
import com.projectgoth.fusion.data.BasicMerchantTagDetailsData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.CreditCardPaymentData;
import com.projectgoth.fusion.data.CreditTransferData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.DiscountTierData;
import com.projectgoth.fusion.data.MerchantTagData;
import com.projectgoth.fusion.data.MoneyTransferData;
import com.projectgoth.fusion.data.PaidEmoteData;
import com.projectgoth.fusion.data.PotData;
import com.projectgoth.fusion.data.PotStakeData;
import com.projectgoth.fusion.data.ServiceData;
import com.projectgoth.fusion.data.SubscriptionData;
import com.projectgoth.fusion.data.UserPotEligibilityData;
import com.projectgoth.fusion.data.UserReferralData;
import com.projectgoth.fusion.data.pot.PayoutData;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.payment.PaymentException;
import com.projectgoth.fusion.payment.PaymentMetaDetails;
import com.projectgoth.fusion.payment.PaymentSummaryData;
import com.projectgoth.fusion.payment.creditcard.CreditCardPaymentUserAndCountryInfo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface AccountLocal
extends EJBLocalObject {
    public AccountEntryData createAccountEntry(Connection var1, AccountEntryData var2, AccountEntrySourceData var3) throws EJBException;

    public AccountEntryData refundAccountEntry(long var1, String var3, String var4, AccountEntrySourceData var5) throws EJBException;

    public AccountBalanceData getAccountBalance(String var1) throws EJBException;

    public AccountEntryData getAccountEntry(long var1) throws EJBException;

    public AccountEntryData getAccountEntryFromSlave(long var1) throws EJBException;

    public AccountEntryData getAccountEntryFromReference(AccountEntryData.TypeEnum var1, String var2) throws EJBException;

    public List getAccountEntries(String var1, boolean var2) throws EJBException;

    public MoneyTransferData getMoneyTransferEntry(int var1) throws EJBException;

    public List getMoneyTransferEntries(String var1) throws EJBException;

    public AccountEntryData giveActivationCredit(String var1, int var2, String var3, AccountEntrySourceData var4) throws EJBException;

    public AccountEntryData creditReferrer(UserReferralData var1, AccountEntrySourceData var2) throws EJBException;

    public CreditTransferData transferPartnerCredit(int var1, String var2, double var3, String var5, AccountEntryData.TypeEnum var6, AccountEntrySourceData var7) throws EJBException;

    public AccountEntryData refundCreditTransferFee(long var1, AccountEntrySourceData var3) throws EJBException;

    public boolean isChargeableWithCreditTransferFee(int var1) throws FusionEJBException;

    public AccountEntryData chargeCreditTransferFee(String var1, String var2, AccountEntryData var3, Connection var4) throws EJBExceptionWithErrorCause, EJBException;

    public CreditTransferData transferCredit(String var1, String var2, double var3, boolean var5, String var6, AccountEntrySourceData var7) throws EJBException;

    public Map getMinTopMerchantToNonTopMerchantTagAmount(int var1) throws Exception;

    public Map getMinTopMerchantToNonTopMerchantTagAmount(int var1, String var2) throws Exception;

    public double getNETTTransfer(Connection var1, String var2, String var3, Date var4, int var5) throws SQLException;

    public BasicMerchantTagDetailsData getMerchantTagFromUsername(Connection var1, String var2, boolean var3) throws EJBException;

    public MerchantTagData getInactiveMerchantTagBetweenUsers(Connection var1, int var2, int var3, boolean var4) throws EJBException;

    public void tagMerchantPending(Connection var1, int var2, String var3, int var4, String var5) throws EJBException;

    public boolean tagMerchant(Connection var1, int var2, String var3, int var4, String var5) throws EJBException;

    public boolean tagMerchant(Connection var1, int var2, String var3, int var4, String var5, MerchantTagData var6, Date var7, Long var8, Integer var9) throws Exception;

    public void untagMerchant(Connection var1, String var2) throws EJBException;

    public void untagMerchant(Connection var1, MerchantTagData var2, String var3) throws EJBException;

    public void untagMerchant(Connection var1, List var2) throws EJBException;

    public MerchantTagData fixNullAccountentryInMerchantTag(Connection var1, MerchantTagData var2) throws EJBException;

    public void activatePendingMerchantTag(Connection var1, String var2) throws EJBException;

    public void reverseTransferCredit(long var1, String var3, AccountEntrySourceData var4) throws EJBException;

    public void creditAndNotifyUser(String var1, double var2, double var4, String var6, AccountEntrySourceData var7, boolean var8) throws Exception;

    public void reverseTTCredit(long var1, String var3, AccountEntrySourceData var4) throws EJBException;

    public AccountEntryData chargeUserForChatRoomKick(String var1, String var2, String var3, double var4, AccountEntrySourceData var6) throws EJBException;

    public AccountEntryData chargeUserForSMS(String var1, String var2, String var3, double var4, AccountEntrySourceData var6) throws EJBException;

    public AccountEntryData refundUserForSMS(int var1, String var2, String var3, AccountEntrySourceData var4) throws EJBException;

    public AccountEntryData chargeUserForSystemSMS(String var1, String var2, String var3, double var4, AccountEntrySourceData var6) throws EJBException;

    public void updateWholesaleSMSCost(String var1, double var2) throws EJBException;

    public void updateWholesaleSystemSMSCost(String var1, double var2) throws EJBException;

    public AccountEntryData chargeUserForCall(String var1, String var2, String var3, double var4, double var6, AccountEntrySourceData var8) throws EJBException;

    public void refundUserForCall(long var1, String var3, AccountEntrySourceData var4) throws EJBException;

    public void reverseExpiredCredit(long var1, String var3, AccountEntrySourceData var4) throws EJBException;

    public MoneyTransferData moneyTransferTopup(MoneyTransferData var1) throws EJBException;

    public CreditCardPaymentData creditCardPayment(CreditCardPaymentData var1, AccountEntrySourceData var2) throws FusionEJBException, EJBException;

    public CreditCardPaymentUserAndCountryInfo getUserAndCountryInfoForCreditCardPayment(Connection var1, int var2) throws FusionEJBException;

    public boolean isOnCreditCardWhiteList(Connection var1, String var2, String var3) throws SQLException;

    public CreditCardPaymentData validatePastCreditCardHMLTransactions(CreditCardPaymentData var1, CountryData var2, double var3, double var5, double var7, boolean var9) throws EJBException, SQLException, NoSuchFieldException, Exception;

    public CreditCardPaymentData getCreditCardPayment(int var1) throws EJBException;

    public CreditCardPaymentData approveCreditCardPayment(String var1, int var2, AccountEntrySourceData var3) throws EJBException;

    public CreditCardPaymentData rejectCreditCardPayment(String var1, int var2, String var3, AccountEntrySourceData var4) throws EJBException;

    public AccountEntryData creditCardRefund(int var1, AccountEntrySourceData var2) throws EJBException;

    public void creditCardChargeBack(String var1, Date var2, String var3) throws EJBException;

    public CreditCardPaymentData updateCreditCardPaymentStatus(CreditCardPaymentData var1, AccountEntrySourceData var2) throws EJBException;

    public void creditUserFromCreditCardPayment(CreditCardPaymentData var1, AccountEntrySourceData var2, Connection var3);

    public Vector getCreditCardTransactions(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, int var9) throws Exception;

    public Vector getCreditCardHMLTransactions(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, int var9) throws Exception;

    public CurrencyData getUsersLocalCurrency(String var1) throws EJBException;

    public void setUsersLocalCurrency(String var1, String var2, AccountEntrySourceData var3) throws EJBException;

    public int getPaymentType(String var1) throws EJBException;

    public List getPaymentsByMetaData(PaymentData.TypeEnum var1, PaymentMetaDetails[] var2) throws FusionEJBException;

    public int getBankTransferProductID(int var1) throws EJBException;

    public BankTransferIntentData bankTransfer(BankTransferIntentData var1) throws EJBException;

    public BankTransferReceivedData updateBankTransferStatus(BankTransferReceivedData var1, AccountEntrySourceData var2) throws EJBException;

    public DiscountTierData getApplicableDiscountTier(Enums.PaymentEnum var1, String var2, double var3, CurrencyData var5, boolean var6);

    public Vector getEligibleDiscountTiers(Enums.PaymentEnum var1, String var2, CurrencyData var3);

    public double discountTierRounding(double var1);

    public double[] getCreditCardPaymentAmounts(String var1, boolean var2, String var3) throws EJBException;

    public ServiceData getService(int var1) throws EJBException;

    public SubscriptionData getSubscription(int var1) throws EJBException;

    public List getSubscriptions(String var1, Integer var2) throws EJBException;

    public List getExpiringSubscriptions(int var1, int var2) throws EJBException;

    public List getExpiredSubscriptions(int var1, int var2) throws EJBException;

    public List getPendingSubscriptions(int var1, int var2) throws EJBException;

    public SubscriptionData subscribeService(String var1, int var2, AccountEntrySourceData var3) throws EJBException;

    public void updateSubscriptionBillingStatus(String var1, int var2, boolean var3, AccountEntrySourceData var4) throws EJBException;

    public void cancelSubscription(String var1, int var2) throws EJBException;

    public void expireSubscription(String var1, int var2) throws EJBException;

    public void sendSubscriptionExpiryReminderSMS(String var1, int var2, String var3, AccountEntrySourceData var4) throws EJBException;

    public AccountEntryData chargeUserForGame(String var1, String var2, AccountEntryData.TypeEnum var3, String var4, double var5, AccountEntrySourceData var7) throws EJBException;

    public boolean userCanAffordCost(String var1, double var2, String var4, Connection var5) throws EJBException;

    public UserPotEligibilityData userCanAffordToEnterPot(String var1, double var2, String var4, String var5) throws EJBException;

    public PotData createPot(int var1, String var2) throws EJBException;

    public PotStakeData enterUserIntoPot(String var1, int var2, int var3, String var4, double var5, String var7, AccountEntrySourceData var8) throws EJBException;

    public void removeUserFromPot(int var1) throws EJBException;

    public double payoutPot(String var1, int var2, AccountEntrySourceData var3) throws EJBException;

    public PayoutData performPayout(String var1, int var2, AccountEntrySourceData var3) throws EJBException;

    public double payoutPotAndNotify(String var1, int var2, AccountEntrySourceData var3) throws EJBException;

    public void cancelPot(int var1, AccountEntrySourceData var2) throws EJBException;

    public void cancelAllPots(AccountEntrySourceData var1) throws EJBException;

    public double convertCurrency(double var1, String var3, String var4) throws EJBException;

    public List getUnpaidUserReferrals(int var1);

    public AccountEntryData chargeUserForGameItem(String var1, String var2, String var3, double var4, String var6, AccountEntrySourceData var7) throws EJBException;

    public AccountEntryData giveGameReward(String var1, String var2, String var3, double var4, double var6, String var8, AccountEntrySourceData var9) throws EJBException;

    public AccountEntryData giveMarketingReward(String var1, String var2, String var3, double var4, String var6, AccountEntrySourceData var7) throws EJBException;

    public AccountEntryData giveUnfundedCredits(String var1, String var2, String var3, double var4, String var6, AccountEntrySourceData var7) throws EJBException;

    public AccountEntryData deductUnfundedCredits(String var1, String var2, String var3, double var4, String var6, AccountEntrySourceData var7) throws EJBException;

    public Map getMerchantRevenueTrailReport(Date var1, Date var2) throws EJBException;

    public Map getMerchantRevenueGameTrailReport(Date var1, Date var2) throws EJBException;

    public Map getMerchantRevenueThirdPartyTrailReport(Date var1, Date var2) throws EJBException;

    public List getExpiredNonTopMerchantTags(int var1, int var2) throws EJBException;

    public List getExpiredTopMerchantTags(int var1, int var2) throws EJBException;

    public Map getSuperMerchantRevenueTrailReport(Date var1, Date var2) throws EJBException;

    public AccountEntryData giveMerchantRevenueTrail(String var1, String var2, String var3, double var4, String var6, AccountEntrySourceData var7) throws EJBException;

    public AccountEntryData giveMerchantRevenueGameTrail(String var1, String var2, String var3, double var4, String var6, AccountEntrySourceData var7) throws EJBException;

    public AccountEntryData giveMerchantRevenueThirdPartyTrail(String var1, String var2, String var3, double var4, String var6, AccountEntrySourceData var7) throws EJBException;

    public AccountEntryData thirdPartyAPIDebit(String var1, String var2, String var3, double var4, String var6, AccountEntrySourceData var7) throws EJBException;

    public boolean userCanAffordPaidEmote(String var1, PaidEmoteData var2, Connection var3) throws EJBException;

    public boolean sendRechargeCreditRewardProgramTrigger(String var1, double var2, String var4, int var5) throws EJBException;

    public PaymentData createPayment(PaymentData var1) throws EJBException;

    public PaymentData getPaymentById(int var1) throws EJBException;

    public PaymentData getPaymentByVoucher(int var1, Integer var2, String var3, Date var4, int var5) throws EJBException;

    public List getPendingPaymentTransactionsForRequery(int var1, Integer var2);

    public List getPaymentTransactions(String var1, Integer var2, Integer var3, Integer var4) throws EJBException;

    public PaymentData updatePayment(PaymentData var1, AccountEntrySourceData var2) throws PaymentException;

    public boolean isPaymentAllowedToUser(int var1, int var2) throws FusionEJBException;

    public int getPendingPaymentsCount(int var1, Integer var2) throws FusionEJBException;

    public PaymentSummaryData getPaymentSummaryByUserType(int var1, int var2, int var3, Date var4, Date var5);

    public PaymentSummaryData getPaymentSummaryByCountryAndUserType(int var1, Integer var2, int var3, int var4, Date var5, Date var6);

    public PaymentSummaryData getPaymentSummaryByUserID(int var1, int var2, int var3, Date var4, Date var5);

    public PaymentSummaryData getPaymentSummaryByMetaDetails(int var1, int var2, int var3, String var4, Date var5, Date var6);

    public double getNonTransferableFundsAtSystemBaseCurrency(String var1, Date var2, int var3, Connection var4, boolean var5);
}

