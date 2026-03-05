/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;

public class MigboCampaignTrigger
extends RewardProgramTrigger {
    public static final int TAG_VALUE_EMPTY = -1;
    public int campaignID;
    public long eventTimestamp;
    public String entityType = "";
    public String entityId = "";
    public int tagValue = -1;
    public EventTypeEnum eventType;

    public MigboCampaignTrigger(EventTypeEnum eventType, UserData userData, int campaignID, long eventTimestamp, String entityType, String entityId, int tagValue) {
        super(RewardProgramData.TypeEnum.MIGBO_CAMPAIGN_EVENT, userData);
        this.eventType = eventType;
        this.eventTimestamp = eventTimestamp;
        this.entityType = entityType;
        this.tagValue = tagValue;
        this.entityId = entityId;
        this.campaignID = campaignID;
        this.quantityDelta = 1;
    }

    public static MigboCampaignTrigger getCampaignRegistrationTrigger(UserData userData, int campaignID, long eventTimestamp) {
        return new MigboCampaignTrigger(EventTypeEnum.REGISTRATION, userData, campaignID, eventTimestamp, "", "", -1);
    }

    public static MigboCampaignTrigger getTagCreatedTrigger(UserData userData, int campaignID, long eventTimestamp, String entityType, String entityId, int tagValue) {
        return new MigboCampaignTrigger(EventTypeEnum.TAG_CREATED, userData, campaignID, eventTimestamp, entityType, entityId, tagValue);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EventTypeEnum {
        REGISTRATION(1),
        TAG_CREATED(2);

        private int type;

        private EventTypeEnum(int type) {
            this.type = type;
        }

        public int intValue() {
            return this.type;
        }

        public static boolean isValid(int type) {
            return EventTypeEnum.fromIntValue(type) != null;
        }

        public static EventTypeEnum fromIntValue(int type) {
            for (EventTypeEnum e : EventTypeEnum.values()) {
                if (e.type != type) continue;
                return e;
            }
            return null;
        }

        public static EventTypeEnum fromString(String typeStr) {
            if (StringUtil.isBlank(typeStr)) {
                return null;
            }
            for (EventTypeEnum e : EventTypeEnum.values()) {
                if (!e.toString().toLowerCase().equals(typeStr.toLowerCase())) continue;
                return e;
            }
            return null;
        }
    }
}

