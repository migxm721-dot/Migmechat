/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote.gift;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.AccountBalanceData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.slice.FusionException;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GiftAllBillingMessageData
extends MessageData {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GiftAllBillingMessageData.class));
    public static final String EST_BALANCE_MSG = "and your estimated remaining balance after gifting will be";

    public GiftAllBillingMessageData(VirtualGiftData gift, MessageData giftAllCommand, List<String> allRecipients, AccountBalanceData initialBalanceData) throws FusionException {
        super(giftAllCommand);
        try {
            MIS misBean = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
            CurrencyData costCurrency = misBean.getCurrency(gift.getCurrency());
            double totalPrice = gift.getRoundedPrice() * (double)allRecipients.size();
            initialBalanceData.balance -= totalPrice;
            this.messageText = String.format("Congratulations for sending gifts! You have used %s and your estimated remaining balance after gifting will be %s.", costCurrency.formatWithCode(totalPrice), initialBalanceData.formatWithCode());
            this.clearMimeTypeAndData();
            this.guid = null;
        }
        catch (Exception e) {
            log.error((Object)("Unable to create GiftAllBillingMessageData, e=" + e), (Throwable)e);
            throw new FusionException(e.getMessage());
        }
    }
}

