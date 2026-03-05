/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.slice.FusionException;
import org.apache.log4j.Logger;

public class ChatUserReputation {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatUserReputation.class));
    private int userID;
    private ReputationLevelData reputationData;

    public ChatUserReputation(int userID) throws FusionException {
        this.userID = userID;
        this.reloadReputationData();
    }

    public void reloadReputationData() throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Reloading reputation Data");
            }
            this.reputationData = MemCacheOrEJB.getUserReputationLevelData(null, this.userID, null);
        }
        catch (Exception e) {
            String err = "Exception reloading reputation data: e=" + e;
            log.error((Object)err, (Throwable)e);
            throw new FusionException(err);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getReputationDataLevel() {
        ReputationLevelData reputationLevelData = this.reputationData;
        synchronized (reputationLevelData) {
            Integer level = this.reputationData.level;
            return level != null ? level : 1;
        }
    }

    public void verifyIMAllowed(ImType imTypeEnum) throws FusionException {
        if (this.getReputationDataLevel() < SystemProperty.getInt(SystemPropertyEntities.IMSettings.MINMIGLEVEL.forIM(imTypeEnum))) {
            throw new FusionException(imTypeEnum.name() + " is currently disabled");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getMinLevelBeforeLoginBan() throws FusionException {
        int minMigLevelBeforeLoginBan = SystemProperty.getInt(SystemPropertyEntities.Default.MIN_MIG_LEVEL_BEFORE_LOGIN_BAN);
        ReputationLevelData reputationLevelData = this.reputationData;
        synchronized (reputationLevelData) {
            if (minMigLevelBeforeLoginBan > 0 && this.reputationData == null) {
                this.reloadReputationData();
            }
        }
        return minMigLevelBeforeLoginBan;
    }

    public boolean canGetBanned() throws FusionException {
        int minMigLevelBeforeLoginBan = this.getMinLevelBeforeLoginBan();
        return minMigLevelBeforeLoginBan == 0 || minMigLevelBeforeLoginBan > 0 && this.getReputationDataLevel() <= minMigLevelBeforeLoginBan;
    }
}

