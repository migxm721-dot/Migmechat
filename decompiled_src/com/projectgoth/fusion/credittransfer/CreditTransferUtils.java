/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.credittransfer;

import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.UserData;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CreditTransferUtils {
    private final LazyLoader<Set<Integer>> exemptedUserIdsLoader = new LazyLoader<Set<Integer>>("EXEMPTED_USER_IDS", 60000L){

        @Override
        protected Set<Integer> fetchValue() throws Exception {
            int[] userIds = SystemProperty.getIntArray(SystemPropertyEntities.AccountTransaction_CreditTransfer.USERIDS_EXEMPTED_FROM_MIG_LEVEL_CHECK);
            if (userIds != null && userIds.length > 0) {
                HashSet<Integer> userIdSet = new HashSet<Integer>();
                for (int userId : userIds) {
                    userIdSet.add(userId);
                }
                return Collections.unmodifiableSet(userIdSet);
            }
            return Collections.EMPTY_SET;
        }
    };
    private final LazyLoader<Set<UserData.TypeEnum>> userTypesToCheckLoader = new LazyLoader<Set<UserData.TypeEnum>>("userTypesToCheck", 60000L){

        @Override
        protected Set<UserData.TypeEnum> fetchValue() throws Exception {
            int[] userTypeCodes = SystemProperty.getIntArray(SystemPropertyEntities.AccountTransaction_CreditTransfer.USER_TYPES_TO_APPLY_MIG_LEVEL_CHECK);
            if (userTypeCodes != null && userTypeCodes.length > 0) {
                HashSet<UserData.TypeEnum> userTypeSet = new HashSet<UserData.TypeEnum>();
                for (int userTypeCode : userTypeCodes) {
                    UserData.TypeEnum userType = UserData.TypeEnum.fromValue(userTypeCode);
                    if (userType == null) continue;
                    userTypeSet.add(userType);
                }
                return Collections.unmodifiableSet(userTypeSet);
            }
            return Collections.EMPTY_SET;
        }
    };

    private CreditTransferUtils() {
    }

    public static CreditTransferUtils getInstance() {
        return CreditTransferUtilsSingletonHolder.INSTANCE;
    }

    public boolean migLevelCheckEnabled() {
        return SystemProperty.getBool(SystemPropertyEntities.AccountTransaction_CreditTransfer.ENABLE_MIG_LEVEL_CHECK);
    }

    public boolean isUserIdExemptedFromMigLevelCheck(int userId) {
        return this.exemptedUserIdsLoader.getValue().contains(userId);
    }

    public boolean userTypeRequiresMigLevelCheck(UserData.TypeEnum userType) {
        if (userType == null) {
            return true;
        }
        return this.userTypesToCheckLoader.getValue().contains((Object)userType);
    }

    public boolean applyMigLevelCheckForVerifiedAccountStatusValue(UserData.AccountVerifiedEnum accountVerified) {
        if (accountVerified == UserData.AccountVerifiedEnum.VERIFIED_OK) {
            return SystemProperty.getBool(SystemPropertyEntities.AccountTransaction_CreditTransfer.ENABLE_MIG_LEVEL_CHECK_FOR_ACCOUNT_VERIFIED_USER);
        }
        return true;
    }

    public void resetLoader() {
        this.exemptedUserIdsLoader.invalidateCache();
        this.userTypesToCheckLoader.invalidateCache();
    }

    private static class CreditTransferUtilsSingletonHolder {
        public static final CreditTransferUtils INSTANCE = new CreditTransferUtils();

        private CreditTransferUtilsSingletonHolder() {
        }
    }
}

