/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.PurchasedVirtualGoodsUserEventIce;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.projectgoth.fusion.userevent.domain.VirtualGoodType;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent
public class PurchasedVirtualGoodsUserEvent
extends UserEvent {
    public static final String EVENT_NAME = "PURCHASED_VIRTUAL_GOODS";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(PurchasedVirtualGoodsUserEvent.class));
    private byte itemType;
    private int itemId;
    private String itemName;

    public PurchasedVirtualGoodsUserEvent() {
    }

    public PurchasedVirtualGoodsUserEvent(UserEvent event, byte itemType, int itemId, String itemName) {
        super(event);
        this.itemType = itemType;
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public PurchasedVirtualGoodsUserEvent(PurchasedVirtualGoodsUserEventIce event) {
        super(event);
        this.itemId = event.itemId;
        this.itemName = event.itemName;
        this.itemType = event.itemType;
    }

    public byte getItemType() {
        return this.itemType;
    }

    public void setItemType(byte itemType) {
        this.itemType = itemType;
    }

    public int getItemId() {
        return this.itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public PurchasedVirtualGoodsUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        PurchasedVirtualGoodsUserEventIce iceEvent = new PurchasedVirtualGoodsUserEventIce(this.getTimestamp(), this.getGeneratingUsername(), null, this.getText(), this.itemType, this.itemId, this.itemName);
        return iceEvent;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" itemId [").append(this.itemId).append("]");
        buffer.append(" itemName [").append(this.itemName).append("]");
        buffer.append(" itemType [").append((Object)VirtualGoodType.fromValue(this.itemType)).append("]");
        return buffer.toString();
    }

    public static Map<String, String> findSubstitutionParameters(PurchasedVirtualGoodsUserEventIce event) {
        Map<String, String> map = UserEvent.findSubstitutionParameters(event);
        map.put("itemid", Integer.toString(event.itemId));
        map.put("itemname", event.itemName);
        map.put("itemtype", VirtualGoodType.fromValue(event.itemType).name());
        return map;
    }
}

