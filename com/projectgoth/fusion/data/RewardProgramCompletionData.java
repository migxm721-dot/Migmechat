/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.RewardedBadgeData;
import com.projectgoth.fusion.data.RewardedGroupMembershipData;
import com.projectgoth.fusion.data.RewardedMigCreditData;
import com.projectgoth.fusion.data.RewardedReputationData;
import com.projectgoth.fusion.data.RewardedStoreItemData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.RewardedUnlockedStoreItemData;
import com.projectgoth.fusion.rewardsystem.outcomes.NotificationTemplateOutcomeData;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RewardProgramCompletionData
implements Serializable {
    private final int oldMigLevel;
    private final int newMigLevel;
    private final int rewardCount;
    private Timestamp rewardedTime;
    private final UserData userData;
    private final RewardProgramData rewardProgramData;
    private final long completionid;
    private final Collection<RewardedReputationData> rewardedReputations;
    private final AccountEntrySourceData accountEntrySourceData;
    private final Collection<RewardedMigCreditData> rewardedMigCredits;
    private final Collection<RewardedStoreItemData> rewardedStoreItems;
    private final Collection<RewardedBadgeData> rewardedBadges;
    private final Collection<RewardedGroupMembershipData> rewardedGroupMemberships;
    private final Collection<RewardedUnlockedStoreItemData> rewardedUnlockedStoreItems;
    private final Collection<NotificationTemplateOutcomeData> notificationTemplates;

    public RewardProgramCompletionData(UserData userData, RewardProgramData rewardProgramData, long completionid, Timestamp rewardedTime, Collection<RewardedReputationData> rewardedReputations, Collection<RewardedMigCreditData> rewardedMigCredits, Collection<RewardedStoreItemData> rewardedStoreItems, Collection<RewardedBadgeData> rewardedBadges, Collection<RewardedGroupMembershipData> rewardedGroupMemberships, Collection<RewardedUnlockedStoreItemData> rewardedUnlockedStoreItems, Collection<NotificationTemplateOutcomeData> notificationTemplateKeys, AccountEntrySourceData accountEntrySourceData) {
        this.userData = userData;
        this.rewardProgramData = rewardProgramData;
        this.completionid = completionid;
        this.rewardedTime = rewardedTime;
        this.accountEntrySourceData = accountEntrySourceData;
        this.rewardedReputations = rewardedReputations != null ? Collections.unmodifiableCollection(rewardedReputations) : Collections.EMPTY_LIST;
        this.rewardedMigCredits = rewardedMigCredits != null ? Collections.unmodifiableCollection(rewardedMigCredits) : Collections.EMPTY_LIST;
        this.rewardedStoreItems = rewardedStoreItems != null ? Collections.unmodifiableCollection(rewardedStoreItems) : Collections.EMPTY_LIST;
        this.rewardedBadges = rewardedBadges != null ? Collections.unmodifiableCollection(rewardedBadges) : Collections.EMPTY_LIST;
        this.rewardedGroupMemberships = rewardedGroupMemberships != null ? Collections.unmodifiableCollection(rewardedGroupMemberships) : Collections.EMPTY_LIST;
        this.rewardedUnlockedStoreItems = rewardedUnlockedStoreItems != null ? Collections.unmodifiableCollection(rewardedUnlockedStoreItems) : Collections.EMPTY_LIST;
        this.notificationTemplates = notificationTemplateKeys != null ? Collections.unmodifiableCollection(notificationTemplateKeys) : Collections.EMPTY_LIST;
        int minOldLevel = -1;
        int maxNewLevel = -1;
        if (rewardedReputations != null) {
            for (RewardedReputationData rewardedReputation : rewardedReputations) {
                if (rewardedReputation.getOldLevel() == rewardedReputation.getNewLevel()) continue;
                if (minOldLevel == -1 || rewardedReputation.getOldLevel() < minOldLevel) {
                    minOldLevel = rewardedReputation.getOldLevel();
                }
                if (maxNewLevel != -1 && rewardedReputation.getNewLevel() <= maxNewLevel) continue;
                maxNewLevel = rewardedReputation.getNewLevel();
            }
        }
        this.oldMigLevel = minOldLevel;
        this.newMigLevel = maxNewLevel;
        this.rewardCount = RewardProgramCompletionData.sumSizes(rewardedReputations, rewardedMigCredits, rewardedStoreItems, rewardedBadges, rewardedGroupMemberships, rewardedUnlockedStoreItems);
    }

    private static int sumSizes(Collection<?> ... colls) {
        int sum = 0;
        for (Collection<?> c : colls) {
            if (c == null) continue;
            sum += c.size();
        }
        return sum;
    }

    public int getNewMigLevel() {
        return this.newMigLevel;
    }

    public int getOldMigLevel() {
        return this.oldMigLevel;
    }

    public UserData getUserData() {
        return this.userData;
    }

    public RewardProgramData getRewardProgramData() {
        return this.rewardProgramData;
    }

    public Collection<RewardedReputationData> getRewardedReputations() {
        return this.rewardedReputations;
    }

    public long getCompletionid() {
        return this.completionid;
    }

    public AccountEntrySourceData getAccountEntrySourceData() {
        return this.accountEntrySourceData;
    }

    public Collection<RewardedMigCreditData> getRewardedMigCredits() {
        return this.rewardedMigCredits;
    }

    public Collection<RewardedStoreItemData> getRewardedStoreItems() {
        return this.rewardedStoreItems;
    }

    public Collection<RewardedBadgeData> getRewardedBadges() {
        return this.rewardedBadges;
    }

    public Collection<RewardedGroupMembershipData> getRewardedGroupMemberships() {
        return this.rewardedGroupMemberships;
    }

    public Collection<RewardedUnlockedStoreItemData> getRewardedUnlockedStoreItems() {
        return this.rewardedUnlockedStoreItems;
    }

    public Collection<NotificationTemplateOutcomeData> getNotificationTemplates() {
        return this.notificationTemplates;
    }

    public int getRewardCount() {
        return this.rewardCount;
    }

    public Timestamp getRewardedTime() {
        return this.rewardedTime;
    }

    public String toString() {
        return "RewardProgramID:" + this.rewardProgramData.id + ";UserID:" + this.userData.userID + ";RewardCount:" + this.rewardCount;
    }
}

