/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.rewardsystem.RewardPrograms;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class RewardProgramsImpl
implements RewardPrograms {
    private static final int CACHE_TIME = 60000;
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RewardPrograms.class));
    private Map<Integer, RewardProgramData> programs = new ConcurrentHashMap<Integer, RewardProgramData>();
    private Map<RewardProgramData.TypeEnum, RewardPrograms.RewardProgramDataList> programsMap = new ConcurrentHashMap<RewardProgramData.TypeEnum, RewardPrograms.RewardProgramDataList>();
    private static Semaphore semaphore = new Semaphore(1);
    private volatile long lastUpdated;

    RewardProgramsImpl() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void loadPrograms() {
        if (this.lastUpdated == 0L) {
            semaphore.acquireUninterruptibly();
        } else if (!semaphore.tryAcquire()) {
            return;
        }
        log.debug((Object)"Loading reward programs from data storage");
        try {
            try {
                Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
                List newPrograms = contentEJB.getRewardPrograms();
                for (Integer oldProgramID : this.programs.keySet()) {
                    boolean found = false;
                    for (RewardProgramData program : newPrograms) {
                        if (program.id.intValue() != oldProgramID.intValue()) continue;
                        found = true;
                        break;
                    }
                    if (found) continue;
                    this.programs.remove(oldProgramID);
                    this.programsMap.clear();
                }
                for (RewardProgramData program : newPrograms) {
                    this.add(program);
                }
                this.lastUpdated = System.currentTimeMillis();
            }
            catch (Exception e) {
                log.error((Object)("Unable to load reward programs.Exception:" + e), (Throwable)e);
                Object var9_10 = null;
                semaphore.release();
            }
            Object var9_9 = null;
            semaphore.release();
        }
        catch (Throwable throwable) {
            Object var9_11 = null;
            semaphore.release();
            throw throwable;
        }
    }

    @Override
    public void add(RewardProgramData program) {
        this.programs.put(program.id, program);
        this.programsMap.clear();
    }

    @Override
    public void resetCachedProperties() {
        this.lastUpdated = 0L;
    }

    private void testCacheUpdate() {
        if (System.currentTimeMillis() - this.lastUpdated > 60000L) {
            this.loadPrograms();
        }
    }

    @Override
    public Collection<RewardProgramData> getAll() {
        this.testCacheUpdate();
        return this.programs.values();
    }

    @Override
    public RewardProgramData get(int id) {
        this.testCacheUpdate();
        return this.programs.get(id);
    }

    @Override
    public List<RewardProgramData> get(RewardProgramData.TypeEnum type) {
        return this.getRewardPrograms(type).getRewardPrograms();
    }

    @Override
    public RewardPrograms.RewardProgramDataList getRewardPrograms(RewardProgramData.TypeEnum type) {
        this.testCacheUpdate();
        RewardPrograms.RewardProgramDataList rewardPrograms = this.programsMap.get(type);
        if (rewardPrograms == null) {
            LinkedList<RewardProgramData> rpdList = new LinkedList<RewardProgramData>();
            boolean needToCheckUserReputation = false;
            for (RewardProgramData program : this.programs.values()) {
                if (program.type != type) continue;
                rpdList.add(program);
                if (!program.needToCheckUserReputation()) continue;
                needToCheckUserReputation = true;
            }
            rewardPrograms = new RewardPrograms.RewardProgramDataList(rpdList, needToCheckUserReputation);
            this.programsMap.put(type, rewardPrograms);
        }
        return rewardPrograms;
    }
}

