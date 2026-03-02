/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.impl.outcome.MMv2Outcomes
 *  com.projectgoth.leto.common.outcome.Outcomes
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.rewardsystem;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RedisQueue;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.rewardsystem.CommonOutcomes;
import com.projectgoth.fusion.rewardsystem.ExcecutorMetrics;
import com.projectgoth.fusion.rewardsystem.RewardDispatcherEx;
import com.projectgoth.fusion.rewardsystem.RewardDispatcherFactory;
import com.projectgoth.fusion.rewardsystem.RewardPrograms;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeData;
import com.projectgoth.fusion.rewardsystem.outcomes.UserRewardOutcome;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.leto.common.impl.outcome.MMv2Outcomes;
import com.projectgoth.leto.common.outcome.Outcomes;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RewardDispatcher
implements Runnable {
    private static final int POLL_INTERVAL = 1000;
    private static final int POLL_ERROR_RETRY_INTERVAL = 1000;
    private static final int WAIT_OLD_ENGINE_SHUTDOWN_SECS = 60;
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RewardDispatcher.class));
    protected RewardProgramData program;
    private AtomicBoolean stop = new AtomicBoolean(false);
    private static RewardDispatcherFactory dispatcherFactory = null;
    private static RewardPrograms rewardPrograms = null;
    private static ThreadPoolExecutor dispatchExecutor = null;

    public static RewardDispatcherFactory getRewardDispatcherFactory() {
        if (null == dispatcherFactory) {
            RewardDispatcher.setRewardDispatcherFactory(new DefaultRewardDispatcherFactory());
        }
        return dispatcherFactory;
    }

    public static void setRewardDispatcherFactory(RewardDispatcherFactory dispatcherFactory) {
        RewardDispatcher.dispatcherFactory = dispatcherFactory;
    }

    private static RewardPrograms getRewardPrograms() {
        if (null == rewardPrograms) {
            rewardPrograms = RewardPrograms.Instance.get();
        }
        return rewardPrograms;
    }

    public static void setRewardPrograms(RewardPrograms rewardPrograms) {
        RewardDispatcher.rewardPrograms = rewardPrograms;
    }

    public RewardDispatcher(RewardProgramData program) {
        this.program = program;
    }

    public void stop() {
        this.stop.set(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void run() {
        block18: {
            RewardDispatcher.log.info((Object)("RewardDispatcher for program " + this.program.id + " (" + this.program.name + ") started"));
            dispatchKey = "RewardProgram:" + this.program.id + ":Dispatch";
            dispatchedRewardDataStr = null;
            userid = -1;
            queue = null;
            try {
                while (!this.stop.get()) {
                    try {
                        if (queue == null && (queue = RedisQueue.getInstance()) == null) {
                            throw new Exception("Unable to get redisQueue instance");
                        }
                        dispatchedRewardDataStr = queue.pop(dispatchKey, true);
                    }
                    catch (LinkageError e) {
                        RewardDispatcher.log.fatal((Object)"Unexpected linkage error", (Throwable)e);
                        System.exit(-1);
                    }
                    catch (Exception e) {
                        RewardDispatcher.log.error((Object)("Error while polling redis queue to give rewards for program ID " + this.program.id), (Throwable)e);
                        if (queue != null) {
                            try {
                                queue.disconnect();
                            }
                            catch (Exception e2) {
                                // empty catch block
                            }
                        }
                        queue = null;
                        try {
                            Thread.sleep(1000L);
                            continue;
                        }
                        catch (InterruptedException e1) {
                            continue;
                        }
                    }
                    if (dispatchedRewardDataStr == null) {
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException e) {}
                        continue;
                    }
                    this.doDispatch(dispatchKey, dispatchedRewardDataStr);
                }
                var8_12 = null;
                if (queue == null) break block18;
            }
            catch (Throwable var7_16) {
                var8_13 = null;
                if (queue == null) throw var7_16;
                try {
                    queue.disconnect();
                    throw var7_16;
                }
                catch (Exception e3) {
                    queue = null;
                }
                throw var7_16;
            }
            ** try [egrp 5[TRYBLOCK] [7 : 269->277)] { 
lbl53:
            // 1 sources

            queue.disconnect();
lbl55:
            // 1 sources

            catch (Exception e3) {
                queue = null;
            }
        }
        RewardDispatcher.log.info((Object)("RewardDispatcher for program " + this.program.id + " (" + this.program.name + ") stopped"));
    }

    protected void doDispatch(String redisQueueSubkey, String dispatchedRewardDataStr) {
        boolean processedAsUserIDString = false;
        if (SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.ENABLE_PROCESSING_OUTCOME_AS_USERID_STRING)) {
            try {
                Integer.parseInt(dispatchedRewardDataStr);
                this.processRewardOutcomeAsUserIDString(dispatchedRewardDataStr);
                processedAsUserIDString = true;
            }
            catch (NumberFormatException nfe) {
                processedAsUserIDString = false;
            }
            catch (Exception ex) {
                log.error((Object)("RedisQueue:[" + redisQueueSubkey + "];Unhandled exception processing data:[" + dispatchedRewardDataStr + "].Exception:" + ex), (Throwable)ex);
                processedAsUserIDString = true;
            }
        }
        if (!processedAsUserIDString) {
            this.processAsJSONString(redisQueueSubkey, dispatchedRewardDataStr);
        }
    }

    private void processAsJSONString(String redisQueueSubkey, String dispatchedRewardDataStr) {
        if (SystemProperty.getBool(SystemPropertyEntities.RewardDispatcherSettings.ENABLE_LOGGING_RECEIVED_OUTCOMES_FROM_QUEUE)) {
            log.info((Object)("RedisQueue:[" + redisQueueSubkey + "];Received:[" + dispatchedRewardDataStr + "]"));
        }
        try {
            this.processAsOutcomesJSONString(redisQueueSubkey, dispatchedRewardDataStr);
            if (log.isDebugEnabled()) {
                log.debug((Object)("RedisQueue:[" + redisQueueSubkey + "];Done processing [" + dispatchedRewardDataStr + "]"));
            }
        }
        catch (Exception ex) {
            log.error((Object)("RedisQueue:[" + redisQueueSubkey + "];Unhandled exception while processing data:[" + dispatchedRewardDataStr + "].Exception:" + ex), (Throwable)ex);
        }
    }

    private void processAsOutcomesJSONString(String redisQueueSubkey, String dispatchedRewardDataStr) throws CommonOutcomes.InvalidJSONException, CreateException, RemoteException {
        Outcomes outcomes = CommonOutcomes.deserialize(dispatchedRewardDataStr);
        switch (outcomes.getOutcomeType()) {
            case 1: {
                this.processOutcomes(redisQueueSubkey, (UserRewardOutcome)outcomes);
                break;
            }
            case 2: {
                this.processOutcomes(redisQueueSubkey, (MMv2Outcomes)outcomes);
                break;
            }
            default: {
                throw new CommonOutcomes.InvalidJSONException("Unsupported outcome type:[" + outcomes.getOutcomeType() + "].");
            }
        }
    }

    private void processRewardOutcomeAsUserIDString(String userIDStr) throws RemoteException, CreateException {
        log.info((Object)("[Deprecated]Dispatching rewards to user ID " + userIDStr + " for program ID " + this.program.id));
        int userid = StringUtil.toIntOrDefault(userIDStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Error while giving rewards to user ID %s for program ID %d: incorrect userid '%s'", userIDStr, this.program.id, userIDStr));
        } else {
            this.giveRewards(this.program, userid, null, null);
        }
    }

    private void processOutcomes(String redisQueueSubkey, UserRewardOutcome rewardOutcome) throws RemoteException, CreateException {
        log.info((Object)("RedisQueue:[" + redisQueueSubkey + "];Processing UserRewardOutcome id:[" + rewardOutcome.getId() + "] createdtime:[" + rewardOutcome.getCreateTs() + "] origin:[" + rewardOutcome.getOrigin() + "] userID:[" + rewardOutcome.getUserid() + "] programId:[" + this.program.id + "]"));
        CommonOutcomes.processOutcomes(this.program.id, rewardOutcome, new AccountEntrySourceData(RewardDispatcher.class));
    }

    private void giveRewards(RewardProgramData program, int userid, List<RewardProgramOutcomeData> outcomeList, Map<String, String> templateData) throws RemoteException, CreateException {
        Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
        contentEJB.giveRewards(program.id, userid, new AccountEntrySourceData(RewardDispatcher.class), outcomeList, templateData);
    }

    private void processOutcomes(String redisQueueSubkey, MMv2Outcomes outcomes) throws RemoteException, CreateException {
        log.info((Object)("RedisQueue:[" + redisQueueSubkey + "];Processing MMv2Outcomes id:[" + outcomes.getId() + "] createdtime:[" + outcomes.getCreateTimestamp().getTime() + "] origin:[" + outcomes.getOrigin() + "] userID:[" + outcomes.getSubjectId() + "] programId:[" + outcomes.getRuleId() + "]"));
        CommonOutcomes.processOutcomes(outcomes, new AccountEntrySourceData(RewardDispatcher.class));
    }

    public static String getQueueName(int id) {
        return "RewardProgram:" + id + ":Dispatch";
    }

    public static ExcecutorMetrics getExcecutorMetrics() {
        if (null == dispatchExecutor) {
            return new ExcecutorMetrics();
        }
        return new ExcecutorMetrics(dispatchExecutor.getActiveCount(), dispatchExecutor.getQueue().size(), dispatchExecutor.getCorePoolSize(), dispatchExecutor.getMaximumPoolSize(), dispatchExecutor.getKeepAliveTime(TimeUnit.SECONDS), dispatchExecutor.getLargestPoolSize());
    }

    private static int calculateMaxThreads(int programSize) {
        return (int)(SystemProperty.getDouble(SystemPropertyEntities.MechanicsEngineSettings.TPOOL_MAX_THREAD_MULTIPLIER) * (double)programSize);
    }

    public static boolean newMain(RewardPrograms programs) throws FusionException {
        log.info((Object)"NEW Engine started.");
        boolean terminateSignalReceived = false;
        dispatchExecutor = new ThreadPoolExecutor(SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.TPOOL_CORE_SIZE), RewardDispatcher.calculateMaxThreads(programs.getAll().size()), SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.TPOOL_KEEPALIVE_SECS), TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.MAX_WORKERS_QUEUED)));
        dispatchExecutor.prestartAllCoreThreads();
        int pollIntervalTPoolSettingsMillis = SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.POLL_INTERVAL_SETTINGS_SECS) * 1000;
        long timeLastSettingsChecked = System.currentTimeMillis();
        ProgramsStateHelper programsIterator = new ProgramsStateHelper(programs);
        while (true) {
            int pollIntervalSetting;
            int keepaliveSetting;
            int maxSizeSetting;
            RewardProgramData program = programsIterator.currentOrNext();
            while (null != program) {
                int maxPoolSize;
                int tasksQueued;
                int activeThreads = dispatchExecutor.getActiveCount();
                if (activeThreads + (tasksQueued = dispatchExecutor.getQueue().size()) >= (maxPoolSize = dispatchExecutor.getMaximumPoolSize())) {
                    log.info((Object)("Reward Thread pool busy - Current size:" + activeThreads + ", Queued:" + tasksQueued + ", Max Size:" + maxPoolSize + "."));
                    break;
                }
                String dispatchedRewardDataStr = programsIterator.getFromQueue();
                if (null != dispatchedRewardDataStr) {
                    dispatchExecutor.execute(new RewardDispatcherEx(program, dispatchedRewardDataStr));
                }
                program = programsIterator.next();
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                terminateSignalReceived = true;
                break;
            }
            catch (Exception e) {
                log.error((Object)"Unknown exception occured during 'Thread.sleep':", (Throwable)e);
            }
            if (!SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.ENABLE_MINIMISE_REDIS_THREADS_ENGINE)) {
                log.warn((Object)"Switching to old Engine.");
                break;
            }
            if ((long)pollIntervalTPoolSettingsMillis >= System.currentTimeMillis() - timeLastSettingsChecked) continue;
            int coreSizeSetting = SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.TPOOL_CORE_SIZE);
            if (coreSizeSetting != dispatchExecutor.getCorePoolSize()) {
                dispatchExecutor.setCorePoolSize(coreSizeSetting);
                log.info((Object)("New CorePoolSize set to " + coreSizeSetting));
            }
            if ((maxSizeSetting = RewardDispatcher.calculateMaxThreads(programs.getAll().size())) != dispatchExecutor.getMaximumPoolSize()) {
                dispatchExecutor.setMaximumPoolSize(maxSizeSetting);
                log.info((Object)("New Maximum Pool Size adjusted to " + maxSizeSetting));
            }
            if ((long)(keepaliveSetting = SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.TPOOL_KEEPALIVE_SECS)) != dispatchExecutor.getKeepAliveTime(TimeUnit.SECONDS)) {
                dispatchExecutor.setKeepAliveTime(keepaliveSetting, TimeUnit.SECONDS);
                log.info((Object)("New KeepAliveTime set to " + keepaliveSetting + " seconds."));
            }
            if ((pollIntervalSetting = SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.POLL_INTERVAL_SETTINGS_SECS) * 1000) != pollIntervalTPoolSettingsMillis) {
                pollIntervalTPoolSettingsMillis = pollIntervalSetting;
                log.info((Object)("Polling interval to check if settings have changed changed to " + pollIntervalSetting + " seconds."));
            }
            timeLastSettingsChecked = System.currentTimeMillis();
        }
        try {
            log.info((Object)"Shutting down new engine.");
            dispatchExecutor.shutdown();
            dispatchExecutor.awaitTermination(SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.WAIT_ENGINE_SHUTDOWN_SECS), TimeUnit.SECONDS);
            log.info((Object)"New engine shutdown gracefuly.");
        }
        catch (Exception e) {
            log.warn((Object)"New Engine DID NOT shutdown gracefuly.", (Throwable)e);
        }
        return terminateSignalReceived;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public static void main(String[] args) {
        block23: {
            dispatchers = null;
            pool = null;
            DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
            programs = RewardDispatcher.getRewardPrograms();
            block11: do {
                if (SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.ENABLE_MINIMISE_REDIS_THREADS_ENGINE)) continue;
                RewardDispatcher.log.warn((Object)"OLD Engine started. Will NOT switch back with SysProp change.");
                pool = Executors.newCachedThreadPool();
                dispatchers = new ConcurrentHashMap<Integer, RewardDispatcher>();
                while (true) lbl-1000:
                // 5 sources

                {
                    for (Integer programID : dispatchers.keySet()) {
                        if (programs.get(programID) != null) continue;
                        ((RewardDispatcher)dispatchers.remove(programID)).stop();
                    }
                    for (RewardProgramData program : programs.getAll()) {
                        if (dispatchers.containsKey(program.id)) continue;
                        dispatcher = RewardDispatcher.getRewardDispatcherFactory().getNewInstance(program);
                        dispatchers.put(program.id, dispatcher);
                        pool.execute(dispatcher);
                    }
                    try {
                        Thread.sleep(1000L);
                        ** continue;
                    }
                    catch (InterruptedException e) {
                        RewardDispatcher.log.fatal((Object)"Interrupted exception.", (Throwable)e);
                        break block11;
                    }
                    catch (Exception e) {
                        RewardDispatcher.log.fatal((Object)"Uncaught exception. continuing...", (Throwable)e);
                        continue;
                    }
                    break;
                }
            } while (!RewardDispatcher.newMain(programs));
            RewardDispatcher.log.info((Object)"RewardDispatcher terminated.");
            var8_10 = null;
            if (null == dispatchers) break block23;
            for (RewardDispatcher dispatcher : dispatchers.values()) {
                dispatcher.stop();
            }
            if (null != pool) {
                pool.shutdown();
            }
            try {
                pool.awaitTermination(60L, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {}
            break block23;
            {
                catch (Exception e) {
                    RewardDispatcher.log.fatal((Object)"Unexpected exception. Terminating RewardDispatcher", (Throwable)e);
                    System.exit(-1);
                    var8_11 = null;
                    if (null != dispatchers) {
                        for (RewardDispatcher dispatcher : dispatchers.values()) {
                            dispatcher.stop();
                        }
                        if (null != pool) {
                            pool.shutdown();
                        }
                        try {
                            pool.awaitTermination(60L, TimeUnit.SECONDS);
                        }
                        catch (InterruptedException e) {}
                    }
                }
            }
            catch (Throwable var7_19) {
                var8_12 = null;
                if (null != dispatchers) {
                    for (RewardDispatcher dispatcher : dispatchers.values()) {
                        dispatcher.stop();
                    }
                    if (null != pool) {
                        pool.shutdown();
                    }
                    try {
                        pool.awaitTermination(60L, TimeUnit.SECONDS);
                    }
                    catch (InterruptedException e) {
                        // empty catch block
                    }
                }
                throw var7_19;
            }
        }
    }

    private static class ProgramsStateHelper {
        STATE state = STATE.CLOSE;
        private final RewardPrograms programs;
        private RedisQueue queue = null;
        private Iterator<RewardProgramData> iterPrograms = null;
        private RewardProgramData curProgram = null;

        public ProgramsStateHelper(RewardPrograms programs) {
            this.programs = programs;
        }

        private void testQueue() {
            if (STATE.CLOSE == this.state) {
                this.queue = RedisQueue.getInstance();
            }
            this.state = null == this.queue ? STATE.CLOSE : STATE.OPEN;
        }

        public String getFromQueue() {
            block7: {
                if (STATE.CLOSE == this.state) {
                    return null;
                }
                String dispatchKey = RewardDispatcher.getQueueName(this.curProgram.id);
                try {
                    String dispatchedRewardDataStr;
                    if (0 < this.queue.size(dispatchKey) && !StringUtil.isBlank(dispatchedRewardDataStr = this.queue.pop(dispatchKey, true))) {
                        return dispatchedRewardDataStr;
                    }
                }
                catch (LinkageError e) {
                    log.fatal((Object)"Unexpected linkage error", (Throwable)e);
                    System.exit(-1);
                }
                catch (Exception e) {
                    log.error((Object)("Error while polling redis queue to give rewards for program ID " + this.curProgram.id), (Throwable)e);
                    if (this.queue == null) break block7;
                    try {
                        this.queue.disconnect();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    this.queue = null;
                    this.iterPrograms = null;
                    this.curProgram = null;
                    this.state = STATE.CLOSE;
                }
            }
            return null;
        }

        public RewardProgramData currentOrNext() throws FusionException {
            switch (this.state) {
                case CLOSE: {
                    this.testQueue();
                    if (STATE.CLOSE == this.state) {
                        return null;
                    }
                }
                case OPEN: {
                    this.iterPrograms = this.programs.getAll().iterator();
                    this.state = STATE.ITERATING;
                }
                case ITERATING: {
                    this.curProgram = this.iterPrograms.next();
                    this.state = STATE.HOLD;
                }
                case HOLD: {
                    return this.curProgram;
                }
            }
            throw new FusionException("Unknown State in RewardDispatcher.ProgramState");
        }

        public RewardProgramData next() throws FusionException {
            if (STATE.HOLD == this.state) {
                this.curProgram = null;
                this.state = STATE.ITERATING;
                if (!this.iterPrograms.hasNext()) {
                    this.iterPrograms = null;
                    this.state = STATE.OPEN;
                    return null;
                }
            }
            return this.currentOrNext();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum STATE {
            CLOSE,
            OPEN,
            ITERATING,
            HOLD;

        }
    }

    private static class DefaultRewardDispatcherFactory
    implements RewardDispatcherFactory {
        private DefaultRewardDispatcherFactory() {
        }

        public RewardDispatcher getNewInstance(RewardProgramData program) {
            return new RewardDispatcher(program);
        }
    }
}

