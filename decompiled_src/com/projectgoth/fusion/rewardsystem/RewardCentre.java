/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.userreward.UserMigLevelIncreaseEvent
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 */
package com.projectgoth.fusion.rewardsystem;

import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.FulfilledFuture;
import com.projectgoth.fusion.common.HostUtils;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.RedisQueue;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserReputationScoreAndLevelData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.Rewards;
import com.projectgoth.fusion.interfaces.RewardsHome;
import com.projectgoth.fusion.rewardsystem.RewardProgramOutcomes;
import com.projectgoth.fusion.rewardsystem.RewardPrograms;
import com.projectgoth.fusion.rewardsystem.TriggerProcessingContext;
import com.projectgoth.fusion.rewardsystem.mmv2.MMv2TriggerSender;
import com.projectgoth.fusion.rewardsystem.notification.TemplateDataProvider;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeProcessorInstances;
import com.projectgoth.fusion.rewardsystem.outcomes.UserRewardOutcome;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessorFactory;
import com.projectgoth.fusion.rewardsystem.stateprocessors.UserProgramCASState;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.leto.common.event.userreward.UserMigLevelIncreaseEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RewardCentre {
    public static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RewardCentre.class));
    private static RewardCentre instance = new RewardCentre();
    private LazyLoader<HashMap<String, Integer>> rewardScoreCapCache = new LazyLoader<HashMap<String, Integer>>(SystemProperty.getLong(SystemPropertyEntities.MechanicsEngineSettings.REWARD_CENTRE_SCORECAP_REFRESH_MILLIS)){

        @Override
        protected HashMap<String, Integer> fetchValue() throws Exception {
            Rewards rewardEJB = (Rewards)EJBHomeCache.getObject("ejb/Rewards", RewardsHome.class);
            return rewardEJB.getRewardScoreCap();
        }
    };
    private RewardProgramOutcomeProcessorInstances outcomeProcessorInstances = new RewardProgramOutcomeProcessorInstances();
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    private static final FulfilledFuture<Boolean> PROCESSED_FUTURE = new FulfilledFuture<Boolean>(Boolean.TRUE);
    private static final String COMPLETED_FIELD = "Completed";
    private static final String QUANTITY_FIELD = "Quantity";
    private static final String AMOUNT_FIELD = "Amount";
    private static final long millisInDay = 86400000L;

    private RewardCentre() {
    }

    protected RewardPrograms getRewardPrograms() {
        return RewardPrograms.Instance.get();
    }

    protected RewardCentre(int dummy) {
    }

    public static RewardCentre getInstance() {
        return instance;
    }

    public static RewardCentre setInstance(RewardCentre newInstance) {
        RewardCentre oldInstance = instance;
        instance = newInstance;
        return oldInstance;
    }

    public RewardProgramData getRewardProgram(int rewardProgramID) {
        return this.getRewardPrograms().get(rewardProgramID);
    }

    public void resetCachedRewardPrograms() {
        this.getRewardPrograms().resetCachedProperties();
        RewardProgramProcessorFactory.resetProgramProcessorMap();
    }

    public final Future<Boolean> sendTrigger(RewardProgramTrigger trigger) throws Exception {
        this.sendToMMv2(trigger);
        return this.sendToMMv1(trigger);
    }

    private void sendToMMv2(RewardProgramTrigger trigger) {
        try {
            MMv2TriggerSender.getInstance().send(trigger);
        }
        catch (Exception ex) {
            log.error((Object)("Failed to send to MMv2.Exception:" + ex), (Throwable)ex);
        }
    }

    private Future<Boolean> sendToMMv1(RewardProgramTrigger trigger) throws Exception {
        if (SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.ENABLE_MM_V1)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Sending trigger [" + trigger + "] to MMv1"));
            }
            TriggerProcessingContext ctx = new TriggerProcessingContext(trigger);
            ctx.received();
            return this.processTrigger(ctx, trigger);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Not sending trigger [" + trigger + "] to MMv1"));
        }
        return PROCESSED_FUTURE;
    }

    protected Future<Boolean> processTrigger(TriggerProcessingContext context, RewardProgramTrigger trigger) throws Exception {
        int maxQueueSize;
        int queueSize = this.executor.getQueue().size();
        if (queueSize <= (maxQueueSize = SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.REWARD_CENTRE_QUEUE_MAXSIZE))) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Submitting trigger for async execution: " + trigger.toString()));
            }
            return this.executor.submit(this.createWorker(context, trigger));
        }
        context.dropped();
        log.warn((Object)("RewardCentreWorker queue too busy (" + queueSize + "/" + maxQueueSize + "). Dropping Trigger: " + trigger.toString()));
        return null;
    }

    public UserReputationScoreAndLevelData getUserReputationScoreAndLevelData(UserData userData) throws RemoteException, CreateException {
        boolean fetchUserReputationFromMaster = SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.FETCH_USER_REPUTATION_FROM_MASTER);
        UserReputationScoreAndLevelData reputation = MemCacheOrEJB.getUserReputationScoreAndLevelData(fetchUserReputationFromMaster, userData);
        return reputation;
    }

    private void checkPrograms(TriggerProcessingContext context, RewardProgramTrigger trigger) throws Throwable {
        if (SystemPropertyEntities.Temp.Cache.se218Enabled.getValue().booleanValue()) {
            this.checkProgramsMinimizeUserReputationCheck(context, trigger);
        } else {
            this.checkProgramsOld(context, trigger);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    private void checkProgramsOld(TriggerProcessingContext context, RewardProgramTrigger trigger) throws Throwable {
        RewardPrograms programs = this.getRewardPrograms();
        List<RewardProgramProcessor> processors = RewardProgramProcessorFactory.getProcessorsForProgramType(trigger.programType);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Received trigger [" + trigger.programType + "]"));
            log.debug((Object)("Found " + processors.size() + " processors tied to " + trigger.programType));
            log.debug((Object)("Found [" + programs.get(trigger.programType).size() + "] programs for type [" + trigger.programType + "]"));
        }
        Jedis redisConnection = null;
        RedisQueue queue = null;
        try {
            redisConnection = Redis.getMasterInstanceForUserID(trigger.userData.userID);
            if (redisConnection == null) {
                throw new Exception("Unable to get master instance of user " + trigger.userData.userID);
            }
            queue = RedisQueue.getInstance();
            if (queue == null) {
                throw new Exception("Unable to get redis queue instance ");
            }
            if (AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.PARTICIPATE_IN_MARKETING_MECHANICS, trigger.userData)) {
                UserReputationScoreAndLevelData reputation = this.getUserReputationScoreAndLevelData(trigger.userData);
                for (RewardProgramData program : programs.get(trigger.programType)) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("  Checking trigger against program :" + program.id + ";name=" + program.name + ";type=" + program.type));
                        log.debug((Object)("  Program.isActive [" + program.isActive(trigger.userData.countryID, reputation.level, trigger.userData.type) + "]"));
                    }
                    if (!program.isActive(trigger.userData.countryID, reputation.level, trigger.userData.type)) continue;
                    boolean triggerIsValid = true;
                    for (RewardProgramProcessor proc : processors) {
                        boolean bl = triggerIsValid = triggerIsValid && proc.checkValidity(program, trigger);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("    Processor: " + proc.toString() + " triggerIsValid:" + triggerIsValid));
                        }
                        if (triggerIsValid) continue;
                        break;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Trigger:[" + trigger + "]  isValid:[" + triggerIsValid + "]"));
                    }
                    if (!triggerIsValid) continue;
                    this.checkProgram(program, trigger, redisConnection, queue);
                }
            }
            Object var14_13 = null;
        }
        catch (Throwable throwable) {
            Object var14_14 = null;
            Redis.disconnect(redisConnection, log);
            if (queue != null) {
                try {
                    queue.disconnect();
                }
                catch (IOException e) {
                    log.warn((Object)("Failed to close RedisQueue connection, reason: " + e.getLocalizedMessage()));
                }
            }
            throw throwable;
        }
        Redis.disconnect(redisConnection, log);
        if (queue != null) {
            try {
                queue.disconnect();
            }
            catch (IOException e) {
                log.warn((Object)("Failed to close RedisQueue connection, reason: " + e.getLocalizedMessage()));
            }
        }
    }

    public boolean hasValidTrigger(RewardProgramData rewardProgramData, RewardProgramTrigger trigger) {
        UserData userData = trigger.userData;
        if (userData == null) {
            if (userData == null) {
                log.error((Object)("Trigger [" + trigger + "] has null userData"));
            }
            return false;
        }
        if (rewardProgramData.status != RewardProgramData.StatusEnum.ACTIVE) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Reward program [" + rewardProgramData.id + "]:rewardProgramData.status [" + (Object)((Object)rewardProgramData.status) + "]"));
            }
            return false;
        }
        if (rewardProgramData.userType != null && rewardProgramData.userType != userData.type) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Reward program [" + rewardProgramData.id + "]:rewardProgramData.userType [" + (Object)((Object)rewardProgramData.userType) + "] userData.type [" + (Object)((Object)userData.type) + "]"));
            }
            return false;
        }
        if (rewardProgramData.countryID != null && rewardProgramData.countryID.equals(userData.countryID)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Reward program [" + rewardProgramData.id + "]:rewardProgramData.userType [" + rewardProgramData.countryID + "] userData.countryID [" + userData.countryID + "]"));
            }
            return false;
        }
        long now = System.currentTimeMillis();
        if (rewardProgramData.startDate != null && now < rewardProgramData.startDate.getTime()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Reward program [" + rewardProgramData.id + "]:rewardProgramData.startDate [" + rewardProgramData.startDate + "] now [" + new Timestamp(now) + "]"));
            }
            return false;
        }
        if (rewardProgramData.endDate != null && now > rewardProgramData.endDate.getTime()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Reward program [" + rewardProgramData.id + "]:rewardProgramData.endDate [" + rewardProgramData.endDate + "] now [" + new Timestamp(now) + "]"));
            }
            return false;
        }
        return true;
    }

    public boolean hasValidReputation(RewardProgramData rewardProgramData, int userLevel) {
        if (rewardProgramData.minMigLevel != null) {
            if (rewardProgramData.minMigLevel > 1 && userLevel < rewardProgramData.minMigLevel) {
                return false;
            }
        }
        return rewardProgramData.maxMigLevel == null || userLevel <= rewardProgramData.maxMigLevel;
    }

    private boolean isValidTriggerAndReputation(RewardProgramData rewardProgramData, RewardProgramTrigger trigger, Integer userMigLevelToCheck) {
        if (!this.hasValidTrigger(rewardProgramData, trigger)) {
            return false;
        }
        if (userMigLevelToCheck != null) {
            if (!this.hasValidReputation(rewardProgramData, userMigLevelToCheck)) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("RewardProgram:[" + rewardProgramData.id + "] user level upon trigger entry [" + userMigLevelToCheck + "] doesn't meet required mig level"));
                }
                return false;
            }
        } else if (log.isDebugEnabled()) {
            log.debug((Object)("RewardProgram:[" + rewardProgramData.id + "] reputation level check not performed"));
        }
        return true;
    }

    private Integer getUserMigLevelUponTriggerEntry(RewardPrograms.RewardProgramDataList rewardProgramDataList, RewardProgramTrigger trigger) throws CreateException, RemoteException {
        if (rewardProgramDataList.needsToCheckUserReputation()) {
            if (trigger instanceof UserMigLevelIncreaseEvent) {
                return ((UserMigLevelIncreaseEvent)trigger).getNewMigLevel();
            }
            UserReputationScoreAndLevelData reputationLevelData = this.getUserReputationScoreAndLevelData(trigger.userData);
            if (reputationLevelData == null) {
                throw new IllegalArgumentException("null reputationLevelData for [" + trigger.userData.userID + "]");
            }
            if (reputationLevelData.level == null) {
                throw new IllegalArgumentException("null 'reputationLevelData.level' for [" + trigger.userData.userID + "]");
            }
            return reputationLevelData.level;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkProgramsMinimizeUserReputationCheck(TriggerProcessingContext context, RewardProgramTrigger trigger) throws Throwable {
        if (trigger.userData == null) {
            throw new IllegalArgumentException("Null userData in trigger [" + trigger + "]");
        }
        RewardPrograms programs = this.getRewardPrograms();
        List<RewardProgramProcessor> processors = RewardProgramProcessorFactory.getProcessorsForProgramType(trigger.programType);
        RewardPrograms.RewardProgramDataList rewardProgramDataList = programs.getRewardPrograms(trigger.programType);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Received trigger type [" + trigger.programType + "]." + "Reward programs[" + rewardProgramDataList.getRewardPrograms().size() + "],needsReputationLevelCheck:[" + rewardProgramDataList.needsToCheckUserReputation() + "],Reward Program Processors:[" + processors + "]"));
        }
        Jedis redisConnection = null;
        RedisQueue queue = null;
        try {
            redisConnection = Redis.getMasterInstanceForUserID(trigger.userData.userID);
            if (redisConnection == null) {
                throw new Exception("Unable to get master instance of user " + trigger.userData.userID);
            }
            queue = RedisQueue.getInstance();
            if (queue == null) {
                throw new Exception("Unable to get redis queue instance ");
            }
            if (AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.PARTICIPATE_IN_MARKETING_MECHANICS, trigger.userData)) {
                Integer userMigLevelUponTrigger = this.getUserMigLevelUponTriggerEntry(rewardProgramDataList, trigger);
                for (RewardProgramData program : rewardProgramDataList.getRewardPrograms()) {
                    boolean passedCommonValidation = this.isValidTriggerAndReputation(program, trigger, userMigLevelUponTrigger);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Checking trigger against program :" + program.id + ";name=" + program.name + ";type=" + program.type + ";trigger=[" + trigger + "];passedCommonValidation:[" + passedCommonValidation + "]"));
                    }
                    if (!passedCommonValidation) continue;
                    boolean triggerIsValid = true;
                    for (RewardProgramProcessor proc : processors) {
                        boolean bl = triggerIsValid = triggerIsValid && proc.checkValidity(program, trigger);
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("Processor: " + proc.toString() + " triggerIsValid:" + triggerIsValid));
                        }
                        if (triggerIsValid) continue;
                        break;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Trigger:[" + trigger + "]  passed all validation:[" + triggerIsValid + "]"));
                    }
                    if (!triggerIsValid) continue;
                    this.checkProgram(program, trigger, redisConnection, queue);
                }
            }
            Object var17_15 = null;
        }
        catch (Throwable throwable) {
            Object var17_16 = null;
            Redis.disconnect(redisConnection, log);
            if (queue != null) {
                try {
                    queue.disconnect();
                }
                catch (IOException e) {
                    log.warn((Object)("Failed to close RedisQueue connection, reason: " + e.getLocalizedMessage()));
                }
            }
            throw throwable;
        }
        Redis.disconnect(redisConnection, log);
        if (queue != null) {
            try {
                queue.disconnect();
            }
            catch (IOException e) {
                log.warn((Object)("Failed to close RedisQueue connection, reason: " + e.getLocalizedMessage()));
            }
        }
    }

    private void checkProgram(RewardProgramData program, RewardProgramTrigger trigger, Jedis r, RedisQueue queue) throws Exception {
        double amountDeltaInProgramCurrency;
        boolean checkScoreCapFirst;
        int userID = trigger.userData.userID;
        int quantityDelta = trigger.quantityDelta;
        double amountDelta = trigger.amountDelta;
        String currency = trigger.currency;
        String key = "RewardProgram:" + program.id + ":" + userID;
        String completionRateLimitKey = "" + program.id + ':' + userID;
        if (program.rewardFrequency == RewardProgramData.RewardFrequencyEnum.ONCE_OFF) {
            if (r.hexists(key, COMPLETED_FIELD).booleanValue()) {
                return;
            }
        } else if (program.completionRateLimit != null && !MemCachedRateLimiter.checkWithoutHit(MemCachedRateLimiter.NameSpace.REWARD_PRG, completionRateLimitKey, program.completionRateLimit)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("RATE LIMIT HIT: " + key + " " + program.completionRateLimit));
            }
            return;
        }
        if ((checkScoreCapFirst = SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.REWARD_CENTRE_SCORECAP_CHECKED_FIRST_ENABLED)) && program.isReputationMechanic() && !this.checkScoreCap(program, trigger, r, false)) {
            return;
        }
        boolean requirementsMet = true;
        if (program.quantityRequired > 0) {
            if (r.hincrBy(key, QUANTITY_FIELD, (long)quantityDelta) < (long)program.quantityRequired.intValue()) {
                requirementsMet = false;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)(" CheckProgram. Quantity Required [" + program.quantityRequired + "] Current Quantity [" + r.hget(key, QUANTITY_FIELD) + "]"));
            }
        }
        if (program.amountRequired > 0.0) {
            amountDeltaInProgramCurrency = this.convertCurrency(amountDelta, currency, program.amountRequiredCurrency) * 100.0;
            if ((double)r.hincrBy(key, AMOUNT_FIELD, (long)((int)amountDeltaInProgramCurrency)).longValue() < program.amountRequired * 100.0) {
                requirementsMet = false;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)(" CheckProgram. Amount Required [" + program.amountRequired * 100.0 + "] Current Amount [" + r.hget(key, AMOUNT_FIELD) + "]"));
            }
        } else {
            amountDeltaInProgramCurrency = 0.0;
        }
        if (checkScoreCapFirst && requirementsMet && program.isReputationMechanic() && !(requirementsMet = this.checkScoreCap(program, trigger, r, true))) {
            this.incrementReputationScoreAwardedCounter(r, -program.scoreReward.intValue(), userID, program.category);
            r.hincrBy(key, QUANTITY_FIELD, (long)(-quantityDelta));
            if (amountDeltaInProgramCurrency > 0.0) {
                r.hincrBy(key, AMOUNT_FIELD, (long)((int)(-amountDeltaInProgramCurrency)));
            }
        }
        boolean fullfillUserProgramState = UserProgramCASState.apply(r, program, trigger, key);
        boolean bl = requirementsMet = requirementsMet && fullfillUserProgramState;
        if (!checkScoreCapFirst && requirementsMet && program.isReputationMechanic()) {
            requirementsMet = this.checkScoreCap(program, trigger, r, true);
        }
        if (requirementsMet) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("requirements for rewardprogram[id=" + program.id + ";name=" + program.name + ";type=" + program.type + "] for userid[" + userID + "] is met."));
            }
            if (program.rewardFrequency == RewardProgramData.RewardFrequencyEnum.ONCE_OFF) {
                long completed = r.hincrBy(key, COMPLETED_FIELD, 1L);
                if (completed == 1L) {
                    r.hdel(key, new String[]{QUANTITY_FIELD});
                    r.hdel(key, new String[]{AMOUNT_FIELD});
                    this.dispatchReward(program, trigger, queue);
                }
            } else {
                r.hincrBy(key, QUANTITY_FIELD, (long)(-program.quantityRequired.intValue()));
                r.hincrBy(key, AMOUNT_FIELD, (long)((int)(-program.amountRequired.doubleValue() * 100.0)));
                boolean reachedMaxCompletion = false;
                if (program.completionRateLimit != null) {
                    try {
                        MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.REWARD_PRG, completionRateLimitKey, program.completionRateLimit);
                    }
                    catch (MemCachedRateLimiter.LimitExceeded le) {
                        reachedMaxCompletion = true;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("reachedMaxCompletion [" + reachedMaxCompletion + "]  programID [" + program.id + "] programType [" + program.type + "] rateLimit [" + program.completionRateLimit + "]"));
                    }
                }
                if (log.isDebugEnabled() && program.completionRateLimit != null) {
                    log.debug((Object)("   currentHit [" + MemCachedRateLimiter.checkWithoutHit(MemCachedRateLimiter.NameSpace.REWARD_PRG, completionRateLimitKey, program.completionRateLimit)));
                }
                if (!reachedMaxCompletion) {
                    this.dispatchReward(program, trigger, queue);
                }
            }
        }
    }

    public void resetProgramScoreForUser(RewardProgramData program, int userID) throws Exception {
        Jedis r = null;
        String key = "RewardProgram:" + program.id + ":" + userID;
        try {
            block4: {
                r = Redis.getMasterInstanceForUserID(userID);
                r.del(key);
                if (!program.isReputationMechanic()) break block4;
                r.del(RewardCentre.getRedisKeyForReputationScoreAwarded(userID));
            }
            Object var7_5 = null;
        }
        catch (Throwable throwable) {
            Object var7_6 = null;
            Redis.disconnect(r, log);
            throw throwable;
        }
        Redis.disconnect(r, log);
    }

    private boolean checkScoreCap(RewardProgramData program, RewardProgramTrigger trigger, Jedis r, boolean toIncrement) throws Exception {
        int userID = trigger.userData.userID;
        Integer categoryScoreMax = null;
        if (SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.REWARD_CENTRE_LEVEL_SCORECAP_ENABLED)) {
            UserReputationScoreAndLevelData reputation = this.getUserReputationScoreAndLevelData(trigger.userData);
            String cacheKey = RewardCentre.generateCacheKeyForRewardLevelScoreCap(reputation.level, program.category.value());
            categoryScoreMax = this.rewardScoreCapCache.getValue().get(cacheKey);
        }
        if (categoryScoreMax == null) {
            categoryScoreMax = SystemProperty.getInt(new SystemPropertyEntities.MechanicsEngineCategoryDailyMaximum(program.category));
        }
        int currentScore = 0;
        currentScore = toIncrement ? this.incrementReputationScoreAwardedCounter(r, program.scoreReward, userID, program.category) : this.readReputationScoreAwardedCounter(r, userID, program.category);
        if (log.isDebugEnabled()) {
            log.debug((Object)("  user: " + userID + " program: " + program.id + " trigger: " + program.type + " category: " + (Object)((Object)program.category) + " max:" + categoryScoreMax + " current:" + currentScore));
        }
        return currentScore <= categoryScoreMax;
    }

    private void dispatchReward(RewardProgramData program, RewardProgramTrigger trigger, RedisQueue queue) throws Exception {
        int userID = trigger.userData.userID;
        String origin = HostUtils.getHostname();
        long currentTs = System.currentTimeMillis();
        String id = UUID.randomUUID().toString();
        RewardProgramOutcomes outcomes = RewardProgramOutcomes.getOutcomes(this.outcomeProcessorInstances, program, trigger);
        UserRewardOutcome userReward = new UserRewardOutcome(id, origin, currentTs);
        userReward.setUserid(userID);
        userReward.setOutcomeDataList(outcomes.getOutcomeList());
        userReward.setTemplateData(this.getTemplateData(program, trigger, outcomes));
        String userRewardJsonStr = userReward.toJSONObject().toString();
        if (log.isDebugEnabled()) {
            log.debug((Object)("dispatchReward():queue.push=key:[RewardProgram:" + program.id + ":Dispatch];value:[" + userRewardJsonStr + "];Trigger=[" + trigger + "]"));
        }
        this.dispatchRewardData(queue, program.id, userRewardJsonStr);
    }

    public void dispatchRewardData(RedisQueue redisQueue, int programId, String userRewardJsonStr) throws Exception {
        redisQueue.push("RewardProgram:" + programId + ":Dispatch", userRewardJsonStr, true);
    }

    private Map<String, String> getTemplateData(RewardProgramData rewardProgramData, RewardProgramTrigger trigger, RewardProgramOutcomes outcomes) throws Exception {
        if (rewardProgramData.emailTemplateID != null || outcomes.isTemplateDataRequired()) {
            TemplateDataProvider templateDataProvider = rewardProgramData.emailTemplateDataProviderClassName != null ? (TemplateDataProvider)Class.forName(rewardProgramData.emailTemplateDataProviderClassName).newInstance() : TemplateDataProvider.getDefaultInstance();
            return templateDataProvider.getTemplateData(rewardProgramData, trigger, outcomes.getOutcomeList());
        }
        return null;
    }

    private double convertCurrency(double amount, String fromCurrency, String toCurrency) throws CreateException, RemoteException {
        if (toCurrency.equalsIgnoreCase(fromCurrency)) {
            return amount;
        }
        Account accountBean = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
        return accountBean.convertCurrency(amount, fromCurrency, toCurrency);
    }

    protected final Callable<Boolean> createWorker(TriggerProcessingContext context, RewardProgramTrigger trigger) {
        return new RewardCentreWorker(context, trigger);
    }

    private int incrementReputationScoreAwardedCounter(Jedis jedis, int score, int userID, RewardProgramData.CategoryEnum category) {
        try {
            String key = RewardCentre.getRedisKeyForReputationScoreAwarded(userID);
            String field = Integer.toString(category.value());
            Long resultLong = jedis.hincrBy(key, field, (long)score);
            if (resultLong == null) {
                throw new FusionException("incrementReputationScoreAwardedCounter-hincrBy returns null");
            }
            int newScore = resultLong.intValue();
            if (newScore == score) {
                jedis.expireAt(key, this.getEndOfDayInUnixTime());
            }
            return newScore;
        }
        catch (Exception e) {
            log.warn((Object)("incrementReputationScoreAwardedCounter exception handle:" + e.getMessage()));
            return Integer.MAX_VALUE;
        }
    }

    private int readReputationScoreAwardedCounter(Jedis jedis, int userID, RewardProgramData.CategoryEnum category) {
        int resultInt = 0;
        try {
            String key = RewardCentre.getRedisKeyForReputationScoreAwarded(userID);
            String field = Integer.toString(category.value());
            String resultString = jedis.hget(key, field);
            if (resultString == null) {
                return resultInt;
            }
            resultInt = Integer.parseInt(resultString);
            return resultInt;
        }
        catch (Exception e) {
            log.warn((Object)("readReputationScoreAwardedCounter exception handle:" + e.getMessage()));
            return Integer.MAX_VALUE;
        }
    }

    private long getEndOfDayInUnixTime() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return (currentTime / 86400000L * 86400000L + 86400000L) / 1000L;
    }

    public static String getRedisKeyForReputationScoreAwarded(int userID) {
        return "U:" + userID + ":RPS";
    }

    public static String generateCacheKeyForRewardLevelScoreCap(int level, int category) {
        return Integer.toString(level) + ":" + Integer.toString(category);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class RewardCentreWorker
    implements Callable<Boolean> {
        private final RewardProgramTrigger trigger;
        private final TriggerProcessingContext context;

        RewardCentreWorker(TriggerProcessingContext context, RewardProgramTrigger trigger) {
            this.trigger = trigger;
            this.context = context;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Boolean call() {
            try {
                this.context.dequeued();
                RewardCentre.this.checkPrograms(this.context, this.trigger);
                this.context.successful();
                Boolean bl = true;
                Object var3_3 = null;
                return bl;
            }
            catch (Throwable e) {
                try {
                    this.context.failed();
                    log.error((Object)("Unable to execute trigger:[" + this.trigger.programType + "] id:[" + this.trigger.userData.userID + "] Exception:[" + e.getMessage() + "]"), e);
                    Object var3_4 = null;
                }
                catch (Throwable throwable) {
                    Object var3_5 = null;
                    throw throwable;
                }
            }
            return false;
        }
    }
}

