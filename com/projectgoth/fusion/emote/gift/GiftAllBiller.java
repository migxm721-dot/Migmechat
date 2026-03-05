/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote.gift;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emote.gift.GiftAsync;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GiftAllBiller
implements Callable<Boolean>,
Serializable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GiftAllBiller.class));
    private final String buyerUsername;
    private final VirtualGiftData gift;
    private final int recipientCount;
    private final String recipientWithVGReceivedIDMapGUID;
    private final AccountEntrySourceData accountEntrySourceData;

    public GiftAllBiller(String buyerUsername, VirtualGiftData gift, int recipientCount, String recipientWithVGReceivedIDMapGUID, AccountEntrySourceData accountEntrySourceData) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Constructing GiftAllBiller: buyer=" + buyerUsername + " gift=" + gift));
        }
        this.buyerUsername = buyerUsername;
        this.gift = gift;
        this.recipientCount = recipientCount;
        this.recipientWithVGReceivedIDMapGUID = recipientWithVGReceivedIDMapGUID;
        this.accountEntrySourceData = accountEntrySourceData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Boolean call() {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("GiftAllBiller.call: buyer=" + this.buyerUsername + " gift=" + this.gift + " no of recipients=" + this.recipientCount));
            }
            int timeoutSeconds = SystemProperty.getInt(SystemPropertyEntities.GiftSettings.FUTURE_TIMEOUT_SECONDS);
            Map<String, Integer> results = GiftAsync.getGiftGrid().getStringIntMap(this.recipientWithVGReceivedIDMapGUID);
            long endTime = System.currentTimeMillis() + (long)timeoutSeconds * 1000L;
            do {
                try {
                    Thread.sleep(500L);
                }
                catch (Exception e) {
                    // empty catch block
                }
            } while (results.size() < this.recipientCount && System.currentTimeMillis() < endTime);
            if (results.size() < this.recipientCount) {
                log.warn((Object)("GiftAllBiller timed out waiting for all recipients to receive their gifts: discrepancy=" + (this.recipientCount - results.size())));
            }
            HashMap<String, Integer> resultsClone = new HashMap<String, Integer>();
            resultsClone.putAll(results);
            Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
            contentEJB.billVirtualGiftForMultipleUsers(this.buyerUsername, this.gift, resultsClone, this.accountEntrySourceData);
            if (log.isDebugEnabled()) {
                log.debug((Object)"GiftAllBiller.call: billed virtual gift");
            }
            Boolean bl = true;
            Object var9_10 = null;
            GiftAsync.getGiftGrid().destroyMap(this.recipientWithVGReceivedIDMapGUID);
            return bl;
        }
        catch (Exception e) {
            try {
                log.error((Object)("Exception in GiftAllBiller: e=" + e), (Throwable)e);
                Boolean bl = false;
                Object var9_11 = null;
                GiftAsync.getGiftGrid().destroyMap(this.recipientWithVGReceivedIDMapGUID);
                return bl;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                GiftAsync.getGiftGrid().destroyMap(this.recipientWithVGReceivedIDMapGUID);
                throw throwable;
            }
        }
    }
}

