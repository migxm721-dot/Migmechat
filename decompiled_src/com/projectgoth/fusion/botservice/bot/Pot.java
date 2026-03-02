/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot;

import com.projectgoth.fusion.botservice.BotService;
import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.PotData;
import com.projectgoth.fusion.data.PotStakeData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class Pot {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Pot.class));
    private PotData potData;
    private Bot bot;
    private Map<String, PotStakeData> stakes = new HashMap<String, PotStakeData>();
    Account accountEJB;

    public Pot(Bot bot) throws Exception {
        this.bot = bot;
        this.accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
        this.potData = this.accountEJB.createPot((int)bot.getBotData().getId(), bot.getInstanceID());
        if (log.isDebugEnabled()) {
            log.debug((Object)("Creating the pot " + this.potData.getId() + " for the game " + bot.getBotData().getDisplayName()));
        }
    }

    public int getPotID() {
        return this.potData.getId();
    }

    public synchronized void enterPlayer(String username, double amount, String currency) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)(username + " enters the pot " + this.potData.getId() + " with " + amount + " " + currency));
        }
        PotStakeData stake = this.accountEJB.enterUserIntoPot(this.bot.getBotData().getDisplayName(), (int)this.bot.getBotData().getId(), this.potData.getId(), username, amount, currency, new AccountEntrySourceData(BotService.class));
        this.stakes.put(username, stake);
        if (log.isDebugEnabled()) {
            log.debug((Object)("New pot: " + this.toString()));
        }
    }

    public synchronized void removePlayer(String username) throws Exception {
        PotStakeData stake;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Removing " + username + " from the pot " + this.potData.getId()));
        }
        if ((stake = this.stakes.get(username)) == null) {
            log.warn((Object)("Request to remove " + username + " from the pot " + this.potData.getId() + " and no stake for the user exists"));
            return;
        }
        this.accountEJB.removeUserFromPot(stake.getId());
        stake.setEligible(false);
        if (log.isDebugEnabled()) {
            log.debug((Object)("New pot: " + this.toString()));
        }
    }

    public double payout(boolean cancelOnException) throws Exception {
        try {
            return this.payout();
        }
        catch (Throwable t) {
            log.error((Object)("payout for bot:[" + this.bot.getBotData().getId() + "] pot:[" + this.potData.getId() + "] failed. Exception:" + t), t);
            if (cancelOnException) {
                this.cancel();
            }
            throw t instanceof Exception ? (Exception)t : new Exception("Throwable error detected:" + t, t);
        }
    }

    public double payout(String username, boolean cancelOnException) throws Exception {
        if (!this.stakes.containsKey(username)) {
            throw new Exception("User is not eligible for the pot");
        }
        for (String u : this.stakes.keySet()) {
            if (u.equalsIgnoreCase(username)) continue;
            this.removePlayer(u);
        }
        return this.payout(cancelOnException);
    }

    public synchronized double payout() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Paying out the pot " + this.potData.getId()));
        }
        return this.accountEJB.payoutPotAndNotify(this.bot.getBotData().getDisplayName(), this.potData.getId(), new AccountEntrySourceData(BotService.class));
    }

    public synchronized double payout(String username) throws Exception {
        try {
            return this.payout(username, false);
        }
        catch (Exception e) {
            log.error((Object)("Payout to user [" + username + "] failed.Exception:" + e), (Throwable)e);
            throw e;
        }
    }

    public void cancel() throws Exception {
        this.accountEJB.cancelPot(this.potData.getId(), new AccountEntrySourceData(BotService.class));
    }

    public double getTotalAmountInBaseCurrency() {
        double totalAmount = 0.0;
        for (PotStakeData stake : this.stakes.values()) {
            totalAmount += stake.getAmountInBaseCurrency();
        }
        return totalAmount;
    }

    public double getCurrentPayout(String currency) throws Exception {
        double totalAmount = this.accountEJB.convertCurrency(this.getTotalAmountInBaseCurrency(), CurrencyData.baseCurrency, currency);
        double currentPayout = totalAmount * (1.0 - SystemProperty.getDouble("RakePercent") / 100.0);
        return currentPayout;
    }

    public double getTotalAmount(String currency) throws Exception {
        return this.accountEJB.convertCurrency(this.getTotalAmountInBaseCurrency(), CurrencyData.baseCurrency, currency);
    }

    public String toString() {
        String s = "Pot " + this.potData.getId() + " total size (base currency): " + String.format("%.2f", this.getTotalAmountInBaseCurrency()) + " " + CurrencyData.baseCurrency + "\n";
        s = s + "Stakes:\n";
        for (String username : this.stakes.keySet()) {
            PotStakeData stake = this.stakes.get(username);
            s = s + String.format("User %s: %.2f %s (%.2f %s base).", username, stake.getAmount(), stake.getCurrency(), stake.getAmountInBaseCurrency(), CurrencyData.baseCurrency) + " Eligible? " + (stake.isEligible() ? "yes" : "no") + "\n";
        }
        return s;
    }
}

