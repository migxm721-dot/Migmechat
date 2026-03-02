/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.VGReceivedTrigger;
import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class VGReceivedRewardProgramProcessor
extends RewardProgramProcessor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(VGReceivedRewardProgramProcessor.class));
    @RewardProgramParamName
    public static final String UNIQUE_GIFT_PARAM_KEY = "virtualGiftID";
    @RewardProgramParamName
    public static final String GIFT_PARAM_LIST_KEY = "virtualGiftIDList";
    @RewardProgramParamName
    public static final String IS_FROM_INVENTORY = "isFromInventory";
    @RewardProgramParamName
    public static final String MAX_TRIGGER_AMOUNT_PARAM_KEY = "maxTriggerAmount";
    @RewardProgramParamName
    public static final String MIN_TRIGGER_AMOUNT_PARAM_KEY = "minTriggerAmount";
    @RewardProgramParamName
    public static final String TRIGGER_RULE_AMOUNT_CCY_PARAM_KEY = "triggerRuleAmountCcy";
    @RewardProgramParamName
    public static final String IS_INCLUSIVE_MAX_AMOUNT_PARAM_KEY = "isInclusiveMaxAmount";
    @RewardProgramParamName
    public static final String IS_INCLUSIVE_MIN_AMOUNT_PARAM_KEY = "isInclusiveMinAmount";

    private boolean matchesGiftParamKey(RewardProgramData programData, VGReceivedTrigger vgTrigger) {
        int giftid = programData.getIntParam(UNIQUE_GIFT_PARAM_KEY, -1);
        if (giftid > 0) {
            return giftid == vgTrigger.virtualGiftID;
        }
        return true;
    }

    private boolean belowMaxTriggerAmountCcy(double triggerAmountInRequiredCurrency, RewardProgramData programData, VGReceivedTrigger vgTrigger) {
        if (programData.hasParameter(MAX_TRIGGER_AMOUNT_PARAM_KEY)) {
            boolean valid;
            boolean isInclusiveMaxAmt = programData.getBoolParam(IS_INCLUSIVE_MAX_AMOUNT_PARAM_KEY, true);
            double maxTriggerAmt = programData.getDoubleParam(MAX_TRIGGER_AMOUNT_PARAM_KEY, Double.MAX_VALUE);
            boolean bl = isInclusiveMaxAmt ? triggerAmountInRequiredCurrency <= maxTriggerAmt : (valid = triggerAmountInRequiredCurrency < maxTriggerAmt);
            if (log.isDebugEnabled()) {
                log.debug((Object)("belowMaxTriggerAmountCcy():Trigger amount:" + vgTrigger.amountDelta + " triggerCcy:[" + vgTrigger.currency + "] triggerAmountInRequiredCurrency:[" + triggerAmountInRequiredCurrency + " maxTriggerAmt:[" + maxTriggerAmt + "]  maxAmtInclusive:[" + isInclusiveMaxAmt + "]. Valid:[" + valid + "]"));
            }
            return valid;
        }
        return true;
    }

    private boolean aboveMinTriggerAmountCcy(double triggerAmountInRequiredCurrency, RewardProgramData programData, VGReceivedTrigger vgTrigger) {
        if (programData.hasParameter(MIN_TRIGGER_AMOUNT_PARAM_KEY)) {
            boolean valid;
            boolean isInclusiveMinAmt = programData.getBoolParam(IS_INCLUSIVE_MIN_AMOUNT_PARAM_KEY, true);
            double minTriggerAmt = programData.getDoubleParam(MIN_TRIGGER_AMOUNT_PARAM_KEY, 0.0);
            boolean bl = isInclusiveMinAmt ? triggerAmountInRequiredCurrency >= minTriggerAmt : (valid = triggerAmountInRequiredCurrency > minTriggerAmt);
            if (log.isDebugEnabled()) {
                log.debug((Object)("aboveMinTriggerAmountCcy():Trigger amount:" + vgTrigger.amountDelta + " triggerCcy:[" + vgTrigger.currency + "] triggerAmountInRequiredCurrency:[" + triggerAmountInRequiredCurrency + "] minTriggerAmt:[" + minTriggerAmt + "] isInclusiveMinAmt:[" + isInclusiveMinAmt + "]. Valid:[" + valid + "]"));
            }
            return valid;
        }
        return true;
    }

    private boolean withinBounds(RewardProgramData programData, VGReceivedTrigger vgTrigger) {
        try {
            if (programData.hasParameter(MAX_TRIGGER_AMOUNT_PARAM_KEY) || programData.hasParameter(MIN_TRIGGER_AMOUNT_PARAM_KEY)) {
                String triggerRuleAmtCcy = programData.getStringParam(TRIGGER_RULE_AMOUNT_CCY_PARAM_KEY, null);
                if (null != triggerRuleAmtCcy) {
                    double triggerAmountInRequiredCurrency = StringUtil.equalsIgnoreCase(triggerRuleAmtCcy, vgTrigger.currency) ? vgTrigger.amountDelta : this.convertCurrency(vgTrigger.amountDelta, vgTrigger.currency, triggerRuleAmtCcy);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("withinBounds():Trigger amount:[" + vgTrigger.amountDelta + "] triggerCcy:[" + vgTrigger.currency + "] equates to triggerAmountInRequiredCurrency:[" + triggerAmountInRequiredCurrency + "] triggerRuleAmtCcy:[" + triggerRuleAmtCcy + "]"));
                    }
                    return this.aboveMinTriggerAmountCcy(triggerAmountInRequiredCurrency, programData, vgTrigger) && this.belowMaxTriggerAmountCcy(triggerAmountInRequiredCurrency, programData, vgTrigger);
                }
                log.error((Object)"withinBounds():Unable to process trigger. Undefined parameter[triggerRuleAmountCcy]");
                return false;
            }
            return true;
        }
        catch (Exception ex) {
            log.error((Object)("withinBound(): Failed to process trigger. Exception:" + ex), (Throwable)ex);
            return false;
        }
    }

    private boolean matchesGiftListParamKey(RewardProgramData programData, VGReceivedTrigger vgTrigger) {
        if (programData.hasParameter(GIFT_PARAM_LIST_KEY)) {
            List<String> giftIds = programData.getStringListParam(GIFT_PARAM_LIST_KEY);
            if (null != giftIds && !giftIds.isEmpty()) {
                for (String giftId : giftIds) {
                    try {
                        int id = Integer.parseInt(giftId);
                        if (id <= 0 || id != vgTrigger.virtualGiftID) continue;
                        return true;
                    }
                    catch (NumberFormatException e) {
                        log.error((Object)("Item [" + giftId + "] found in program [" + programData.id + "] parameter [" + GIFT_PARAM_LIST_KEY + "] is not numeric."), (Throwable)e);
                    }
                }
            }
            return false;
        }
        return true;
    }

    private boolean matchesFromSenderInventory(RewardProgramData programData, VGReceivedTrigger vgTrigger) {
        if (programData.hasParameter(IS_FROM_INVENTORY)) {
            return vgTrigger.fromSenderInventory == programData.getBoolParam(IS_FROM_INVENTORY, false);
        }
        return true;
    }

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        if (!(trigger instanceof VGReceivedTrigger)) {
            return false;
        }
        VGReceivedTrigger vgTrigger = (VGReceivedTrigger)trigger;
        if (SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.REWARD_PROCESSOR_MATCH_FROM_INVENTORY_ENABLED)) {
            return this.matchesGiftParamKey(programData, vgTrigger) && this.withinBounds(programData, vgTrigger) && this.matchesGiftListParamKey(programData, vgTrigger) && this.matchesFromSenderInventory(programData, vgTrigger);
        }
        return this.matchesGiftParamKey(programData, vgTrigger) && this.withinBounds(programData, vgTrigger) && this.matchesGiftListParamKey(programData, vgTrigger);
    }

    private double convertCurrency(double amount, String fromCurrency, String toCurrency) throws CreateException, RemoteException {
        if (toCurrency.equalsIgnoreCase(fromCurrency)) {
            return amount;
        }
        Account accountBean = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
        return accountBean.convertCurrency(amount, fromCurrency, toCurrency);
    }
}

