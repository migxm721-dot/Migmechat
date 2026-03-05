/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  com.sleepycat.je.DatabaseException
 *  com.sleepycat.je.Environment
 *  com.sleepycat.je.EnvironmentConfig
 *  com.sleepycat.je.LockMode
 *  com.sleepycat.je.Transaction
 *  com.sleepycat.persist.EntityStore
 *  com.sleepycat.persist.StoreConfig
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.InitializingBean
 */
package com.projectgoth.fusion.userevent.store;

import Ice.Current;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RequestAndRateLongCounter;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice._EventStoreDisp;
import com.projectgoth.fusion.userevent.domain.EventPrivacySetting;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.projectgoth.fusion.userevent.domain.UserEventFactory;
import com.projectgoth.fusion.userevent.store.EventStoreStatistics;
import com.projectgoth.fusion.userevent.store.StoreGeneratorEvent;
import com.projectgoth.fusion.userevent.store.StoreGeneratorEventDA;
import com.projectgoth.fusion.userevent.store.StoreUserEvent;
import com.projectgoth.fusion.userevent.store.StoreUserEventDA;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BDBEventStoreI
extends _EventStoreDisp
implements EventStoreStatistics,
InitializingBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BDBEventStoreI.class));
    private static final int MAXIMUM_LOCKS_CAPACITY = 1000;
    public static final int TWO_WEEKS_MS = 1209600000;
    private String environmentHome;
    private boolean allowStoreCreation;
    private int eventsPerUser = 10;
    private int bdbCachePercent = 25;
    private boolean useBuffer = true;
    private boolean printStats = false;
    private int bufferCapacity = 100000;
    private Environment environment;
    private EntityStore feedStore;
    private EntityStore generatedStore;
    private StoreUserEventDA storeUserEventDA;
    private StoreGeneratorEventDA storeGeneratorEventDA;
    private StatsThread statsThread;
    private PersistThread persistThread;
    private Map<String, StoreUserEvent> buffer;
    private Map<String, ReentrantLock> locks;
    private ConcurrentMap<String, StoreUserEvent> persistBuffer;
    private RequestAndRateLongCounter eventsCounter;
    private RequestAndRateLongCounter generatorEventsCounter;
    private RequestAndRateLongCounter cacheExpiredCounter;
    private RequestAndRateLongCounter persistBufferCounter;

    public void afterPropertiesSet() throws Exception {
        this.eventsCounter = new RequestAndRateLongCounter(1);
        this.generatorEventsCounter = new RequestAndRateLongCounter(1);
        this.cacheExpiredCounter = new RequestAndRateLongCounter(1);
        this.persistBufferCounter = new RequestAndRateLongCounter(1);
        this.persistBuffer = new ConcurrentHashMap<String, StoreUserEvent>();
        this.buffer = new LinkedHashMap<String, StoreUserEvent>(this.bufferCapacity + 10){

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, StoreUserEvent> eldest) {
                if (this.size() > BDBEventStoreI.this.bufferCapacity) {
                    BDBEventStoreI.this.persistBuffer.put(eldest.getKey(), eldest.getValue());
                    BDBEventStoreI.this.persistBufferCounter.add();
                    return true;
                }
                return false;
            }
        };
        this.locks = new LinkedHashMap<String, ReentrantLock>(1000){

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ReentrantLock> eldest) {
                return this.size() > 1000;
            }
        };
        this.setup(new File(this.environmentHome), this.allowStoreCreation);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UserEventIce[] getUserEventsForUser(String username, Current __current) throws FusionException {
        UserEventIce[] array = null;
        try {
            Object object;
            StoreUserEvent userEvent = null;
            userEvent = (StoreUserEvent)this.persistBuffer.remove(username);
            if (this.useBuffer && userEvent == null) {
                object = this.buffer;
                synchronized (object) {
                    userEvent = this.buffer.get(username);
                }
            }
            if (userEvent == null) {
                log.debug((Object)"falling back to disk");
                userEvent = (StoreUserEvent)this.storeUserEventDA.primaryIndex.get((Object)username);
            }
            if (userEvent == null || userEvent.getEvents() == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("nothing stored yet for user [" + username + "]"));
                }
                return null;
            }
            object = userEvent;
            synchronized (object) {
                List<UserEvent> list = userEvent.getEvents();
                array = new UserEventIce[list.size()];
                int index = 0;
                for (UserEvent event : list) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Getting event [" + event + "] from storage for user [" + username + "] to send back"));
                    }
                    UserEventIce userEventIce = event.toIceEvent();
                    array[index++] = userEventIce;
                }
            }
        }
        catch (Exception e) {
            log.error((Object)("Failed to read events for user [" + username + "]"), (Throwable)e);
            throw new FusionException("Failed to read events for user");
        }
        return array;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public UserEventIce[] getUserEventsGeneratedByUser(String username, Current __current) throws FusionException {
        UserEventIce[] array = null;
        try {
            StoreGeneratorEvent generatorEvent = null;
            generatorEvent = (StoreGeneratorEvent)this.storeGeneratorEventDA.primaryIndex.get((Object)username);
            if (generatorEvent == null || generatorEvent.getEvents() == null) {
                if (!log.isDebugEnabled()) return new UserEventIce[0];
                log.debug((Object)("nothing stored yet for user [" + username + "]"));
                return new UserEventIce[0];
            }
            ReentrantLock lock = null;
            Map<String, ReentrantLock> map = this.locks;
            synchronized (map) {
                if (!this.locks.containsKey(username)) {
                    this.locks.put(username, new ReentrantLock());
                }
                lock = this.locks.get(username);
            }
            try {
                lock.lock();
                List<UserEvent> list = generatorEvent.getEvents();
                array = new UserEventIce[list.size()];
                int index = 0;
                for (UserEvent event : list) {
                    if (event.getClass().equals(UserEvent.class)) continue;
                    UserEventIce iceEvent = event.toIceEvent();
                    array[index++] = iceEvent;
                }
                if (index < list.size()) {
                    log.debug((Object)"found dodgy entry, recreating a shorter array");
                    UserEventIce[] shorterArray = new UserEventIce[index];
                    System.arraycopy(array, 0, shorterArray, 0, index);
                    UserEventIce[] userEventIceArray = shorterArray;
                    Object var12_12 = null;
                    lock.unlock();
                    return userEventIceArray;
                }
                Object var12_13 = null;
                lock.unlock();
                return array;
            }
            catch (Throwable throwable) {
                Object var12_14 = null;
                lock.unlock();
                throw throwable;
            }
        }
        catch (Exception e) {
            log.error((Object)("Failed to read generator events for user [" + username + "]"), (Throwable)e);
            throw new FusionException("Failed to read generator events for user");
        }
    }

    private void transactionallyPersistEvent(String username, UserEvent event) throws FusionException {
        StoreUserEvent storeUserEvent = null;
        Transaction transaction = null;
        try {
            transaction = this.environment.beginTransaction(null, null);
            storeUserEvent = (StoreUserEvent)this.storeUserEventDA.primaryIndex.get(transaction, (Object)username, LockMode.RMW);
            storeUserEvent.addEvent(this.eventsPerUser, event);
            this.storeUserEventDA.primaryIndex.put(transaction, (Object)storeUserEvent);
            transaction.commit();
        }
        catch (DatabaseException e) {
            log.error((Object)("failed to store events " + storeUserEvent + "]"), (Throwable)e);
            if (transaction != null) {
                try {
                    transaction.abort();
                }
                catch (DatabaseException ex) {
                    log.error((Object)"failed to roll back transaction", (Throwable)e);
                }
            }
            throw new FusionException("failed to store events");
        }
    }

    private StoreUserEvent getStoreUserEventFromDB(String username) {
        try {
            return (StoreUserEvent)this.storeUserEventDA.primaryIndex.get((Object)username);
        }
        catch (DatabaseException e) {
            log.error((Object)("failed to read events from BDB for user [" + username + "]"), (Throwable)e);
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addEventToBuffer(String username, UserEvent event) {
        StoreUserEvent storeUserEvent = null;
        storeUserEvent = (StoreUserEvent)this.persistBuffer.remove(username);
        Object object = this.buffer;
        synchronized (object) {
            if (storeUserEvent != null) {
                this.buffer.put(username, storeUserEvent);
            } else if (!this.buffer.containsKey(username)) {
                this.buffer.put(username, new StoreUserEvent(username));
            }
            storeUserEvent = this.buffer.get(username);
        }
        object = storeUserEvent;
        synchronized (object) {
            StoreUserEvent forUser;
            if (storeUserEvent.getEvents() == null && (forUser = this.getStoreUserEventFromDB(username)) != null) {
                storeUserEvent.setEvents(forUser.getEvents());
            }
            storeUserEvent.addEvent(this.eventsPerUser, event);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void storeUserEvent(String username, UserEventIce event, Current __current) throws FusionException {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("storing user event [" + (Object)((Object)event) + "] for user [" + username + "]"));
            }
            if (!this.useBuffer) {
                this.transactionallyPersistEvent(username, UserEventFactory.getUserEvent(event));
            } else {
                this.addEventToBuffer(username, UserEventFactory.getUserEvent(event));
            }
            Object var5_4 = null;
            this.eventsCounter.add();
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            this.eventsCounter.add();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void storeGeneratorEvent(String username, UserEventIce event, Current __current) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("storing generator event [" + (Object)((Object)event) + "] for user [" + username + "]"));
        }
        ReentrantLock lock = null;
        Map<String, ReentrantLock> map = this.locks;
        synchronized (map) {
            if (!this.locks.containsKey(username)) {
                this.locks.put(username, new ReentrantLock());
            }
            lock = this.locks.get(username);
        }
        try {
            try {
                lock.lock();
                StoreGeneratorEvent storeGeneratorEvent = (StoreGeneratorEvent)this.storeGeneratorEventDA.primaryIndex.get((Object)username);
                if (storeGeneratorEvent == null) {
                    storeGeneratorEvent = new StoreGeneratorEvent(username);
                }
                storeGeneratorEvent.addEvent(this.eventsPerUser, UserEventFactory.getUserEvent(event));
                this.storeGeneratorEventDA.primaryIndex.put((Object)storeGeneratorEvent);
            }
            catch (Exception ex) {
                log.error((Object)"failed to store generated event ", (Throwable)ex);
                throw new FusionException("Failed to store generated event for user [" + username + "]");
            }
            Object var8_8 = null;
            lock.unlock();
            this.generatorEventsCounter.add();
        }
        catch (Throwable throwable) {
            Object var8_9 = null;
            lock.unlock();
            this.generatorEventsCounter.add();
            throw throwable;
        }
    }

    @Override
    public void deleteUserEvents(String username, Current __current) throws FusionException {
        try {
            this.storeUserEventDA.primaryIndex.delete((Object)username);
            this.storeGeneratorEventDA.primaryIndex.delete((Object)username);
        }
        catch (DatabaseException ex) {
            log.error((Object)("failed to delete events for user [" + username + "]"), (Throwable)ex);
            throw new FusionException(ex.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EventPrivacySettingIce getPublishingPrivacyMask(String username, Current __current) throws FusionException {
        StoreGeneratorEvent storeGeneratorEvent;
        ReentrantLock lock;
        block10: {
            if (log.isDebugEnabled()) {
                log.debug((Object)("getting publishing mask for generator [" + username + "]"));
            }
            lock = null;
            Map<String, ReentrantLock> map = this.locks;
            synchronized (map) {
                if (!this.locks.containsKey(username)) {
                    this.locks.put(username, new ReentrantLock());
                }
                lock = this.locks.get(username);
            }
            lock.lock();
            storeGeneratorEvent = (StoreGeneratorEvent)this.storeGeneratorEventDA.primaryIndex.get((Object)username);
            if (storeGeneratorEvent != null) break block10;
            EventPrivacySettingIce eventPrivacySettingIce = new EventPrivacySetting().toEventPrivacySettingIce();
            Object var7_9 = null;
            lock.unlock();
            return eventPrivacySettingIce;
        }
        try {
            EventPrivacySettingIce eventPrivacySettingIce = storeGeneratorEvent.getPublishingMask().toEventPrivacySettingIce();
            Object var7_10 = null;
            lock.unlock();
            return eventPrivacySettingIce;
        }
        catch (Exception ex) {
            try {
                log.error((Object)("failed to retrieve publishing mask for user [" + username + "]"), (Throwable)ex);
                throw new FusionException("Failed to retrieve publishing mask for user [" + username + "]");
            }
            catch (Throwable throwable) {
                Object var7_11 = null;
                lock.unlock();
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EventPrivacySettingIce getReceivingPrivacyMask(String username, Current __current) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("getting receiving mask for user [" + username + "]"));
        }
        try {
            Object object;
            StoreUserEvent userEvent = null;
            userEvent = (StoreUserEvent)this.persistBuffer.remove(username);
            if (this.useBuffer && userEvent == null) {
                object = this.buffer;
                synchronized (object) {
                    userEvent = this.buffer.get(username);
                }
            }
            if (userEvent == null) {
                log.debug((Object)"falling back to disk");
                userEvent = (StoreUserEvent)this.storeUserEventDA.primaryIndex.get((Object)username);
                if (userEvent == null) {
                    return new EventPrivacySetting().toEventPrivacySettingIce();
                }
            }
            object = userEvent;
            synchronized (object) {
                return userEvent.getReceivingMask().toEventPrivacySettingIce();
            }
        }
        catch (Exception e) {
            log.error((Object)("failed to load receiving mask for user [" + username + "]"), (Throwable)e);
            return new EventPrivacySetting().toEventPrivacySettingIce();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPublishingPrivacyMask(String username, EventPrivacySettingIce mask, Current __current) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("setting publishing mask for generator [" + username + "]"));
        }
        ReentrantLock lock = null;
        Map<String, ReentrantLock> map = this.locks;
        synchronized (map) {
            if (!this.locks.containsKey(username)) {
                this.locks.put(username, new ReentrantLock());
            }
            lock = this.locks.get(username);
        }
        try {
            try {
                lock.lock();
                StoreGeneratorEvent storeGeneratorEvent = (StoreGeneratorEvent)this.storeGeneratorEventDA.primaryIndex.get((Object)username);
                if (storeGeneratorEvent == null) {
                    storeGeneratorEvent = new StoreGeneratorEvent(username, EventPrivacySetting.fromEventPrivacySettingIce(mask));
                } else {
                    storeGeneratorEvent.setPublishingMask(EventPrivacySetting.fromEventPrivacySettingIce(mask));
                }
                this.storeGeneratorEventDA.primaryIndex.put((Object)storeGeneratorEvent);
            }
            catch (Exception ex) {
                log.error((Object)("failed to retrieve publishing mask for user [" + username + "]"), (Throwable)ex);
                throw new FusionException("Failed to retrieve publishing mask for user [" + username + "]");
            }
            Object var8_8 = null;
            lock.unlock();
        }
        catch (Throwable throwable) {
            Object var8_9 = null;
            lock.unlock();
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setReceivingPrivacyMask(String username, EventPrivacySettingIce mask, Current __current) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("setting receiving mask for user [" + username + "]"));
        }
        StoreUserEvent storeUserEvent = null;
        storeUserEvent = (StoreUserEvent)this.persistBuffer.remove(username);
        Object object = this.buffer;
        synchronized (object) {
            if (storeUserEvent != null) {
                this.buffer.put(username, storeUserEvent);
            } else if (!this.buffer.containsKey(username)) {
                this.buffer.put(username, new StoreUserEvent(username));
            }
            storeUserEvent = this.buffer.get(username);
        }
        object = storeUserEvent;
        synchronized (object) {
            StoreUserEvent forUser;
            if (storeUserEvent.getEvents() == null && (forUser = this.getStoreUserEventFromDB(username)) != null) {
                storeUserEvent.setEvents(forUser.getEvents());
            }
            storeUserEvent.setReceivingMask(EventPrivacySetting.fromEventPrivacySettingIce(mask));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void persistToBDB(String username, StoreUserEvent storeUserEvent) {
        if (log.isDebugEnabled()) {
            log.info((Object)("persisting StoreUserEvent to BDB [" + storeUserEvent + "]"));
        }
        try {
            try {
                this.storeUserEventDA.primaryIndex.put((Object)storeUserEvent);
            }
            catch (DatabaseException e) {
                log.error((Object)("Failed to store events for user [" + username + "]"), (Throwable)e);
                Object var5_4 = null;
                this.cacheExpiredCounter.add();
            }
            Object var5_3 = null;
            this.cacheExpiredCounter.add();
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            this.cacheExpiredCounter.add();
            throw throwable;
        }
    }

    @Override
    public long freeMemory() {
        return 0L;
    }

    private void setup(File environmentHome, boolean allowStoreCreation) {
        try {
            log.info((Object)("EnvironmentHome[" + environmentHome.toString() + "] allowStoreCreation[" + allowStoreCreation + "]"));
            EnvironmentConfig environmentConfig = new EnvironmentConfig();
            StoreConfig storeConfig = new StoreConfig();
            if (!this.useBuffer) {
                environmentConfig.setTransactional(true);
                environmentConfig.setTxnSerializableIsolation(true);
                storeConfig.setTransactional(true);
            }
            environmentConfig.setAllowCreate(allowStoreCreation);
            environmentConfig.setCachePercent(this.bdbCachePercent);
            storeConfig.setAllowCreate(allowStoreCreation);
            this.environment = new Environment(environmentHome, environmentConfig);
            this.feedStore = new EntityStore(this.environment, "FeedStore", storeConfig);
            this.generatedStore = new EntityStore(this.environment, "GeneratedStore", storeConfig);
            this.storeUserEventDA = new StoreUserEventDA(this.feedStore);
            this.storeGeneratorEventDA = new StoreGeneratorEventDA(this.generatedStore);
            log.info((Object)"Store opened successfully");
            log.info((Object)("Maximum cache memory: " + (int)((double)environmentConfig.getCachePercent() / 100.0 * (double)Runtime.getRuntime().maxMemory() / 1000000.0) + " MB"));
            if (this.printStats) {
                this.statsThread = new StatsThread(this.environment, this.eventsCounter);
                this.statsThread.start();
            }
            this.persistThread = new PersistThread(this.persistBuffer, this);
            this.persistThread.start();
        }
        catch (DatabaseException e) {
            log.fatal((Object)"Error opening environment and store, exiting... ", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void persistBufferToBDB() {
        log.info((Object)"Stand by... we're flushing the buffer to disk, this can take up to a few minutes...");
        Map<String, StoreUserEvent> map = this.buffer;
        synchronized (map) {
            int total = this.buffer.keySet().size();
            int count = 0;
            for (String username : this.buffer.keySet()) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("persisting " + this.buffer.get(username)));
                    }
                    this.storeUserEventDA.primaryIndex.put((Object)this.buffer.get(username));
                }
                catch (DatabaseException e) {
                    log.error((Object)("failed to persist buffer event for user [" + username + "]"));
                }
                if (count++ != total / 2) continue;
                log.info((Object)("half way done flushing buffer to disk, " + (total - count) + " remaining"));
            }
        }
        log.info((Object)"Done flushing buffer");
    }

    public void shutdownStore() {
        log.info((Object)("Shutting down " + this.getClass().getCanonicalName()));
        if (this.printStats) {
            this.statsThread.stopRunning();
        }
        this.persistThread.stopRunning();
        if (this.useBuffer) {
            this.persistBufferToBDB();
        }
        try {
            if (this.feedStore != null) {
                this.feedStore.sync();
                this.feedStore.close();
            }
            if (this.generatedStore != null) {
                this.generatedStore.sync();
                this.generatedStore.close();
            }
        }
        catch (DatabaseException e) {
            log.fatal((Object)"Error closing store, exiting... ", (Throwable)e);
            return;
        }
        if (this.environment != null) {
            try {
                this.environment.close();
            }
            catch (DatabaseException e) {
                log.fatal((Object)"Error closing Environment, exiting... ", (Throwable)e);
                return;
            }
        }
    }

    public void setEnvironmentHome(String environmentHome) {
        this.environmentHome = environmentHome;
    }

    public void setAllowStoreCreation(boolean allowStoreCreation) {
        this.allowStoreCreation = allowStoreCreation;
    }

    public void setEventsPerUser(int eventsPerUser) {
        this.eventsPerUser = eventsPerUser;
    }

    public void setBdbCachePercent(int bdbCachePercent) {
        this.bdbCachePercent = bdbCachePercent;
    }

    public void setBufferCapacity(int bufferCapacity) {
        this.bufferCapacity = bufferCapacity;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public EntityStore getFeedStore() {
        return this.feedStore;
    }

    public Map<String, StoreUserEvent> getBuffer() {
        return this.buffer;
    }

    public ConcurrentMap<String, StoreUserEvent> getPersistBuffer() {
        return this.persistBuffer;
    }

    public RequestAndRateLongCounter getEventsCounter() {
        return this.eventsCounter;
    }

    public RequestAndRateLongCounter getGeneratorEventsCounter() {
        return this.generatorEventsCounter;
    }

    public RequestAndRateLongCounter getCacheExpiredCounter() {
        return this.cacheExpiredCounter;
    }

    public RequestAndRateLongCounter getPersistBufferCounter() {
        return this.persistBufferCounter;
    }

    class StatsThread
    extends Thread {
        private Environment environment;
        private RequestAndRateLongCounter counter;
        private boolean running = true;

        public StatsThread(Environment environment, RequestAndRateLongCounter counter) {
            this.environment = environment;
            this.counter = counter;
        }

        public void stopRunning() {
            this.running = false;
        }

        public void run() {
            int count = 0;
            while (this.running) {
                try {
                    StatsThread.sleep(10000L);
                    if (count++ % 6 == 0) {
                        log.info((Object)this.environment.getStats(null).toString());
                    }
                    log.info((Object)this.counter.toString());
                }
                catch (Exception e) {
                    log.error((Object)"failed to get stats", (Throwable)e);
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class PersistThread
    extends Thread {
        private ConcurrentMap<String, StoreUserEvent> persistBuffer;
        private BDBEventStoreI store;
        private boolean running = true;

        public PersistThread(ConcurrentMap<String, StoreUserEvent> persistBuffer, BDBEventStoreI store) {
            this.persistBuffer = persistBuffer;
            this.store = store;
        }

        public void stopRunning() {
            log.info((Object)"explicitely stopping PersistThread");
            this.running = false;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                while (this.running) {
                    try {
                        while (!this.persistBuffer.isEmpty()) {
                            for (String username : this.persistBuffer.keySet()) {
                                StoreUserEvent event = (StoreUserEvent)this.persistBuffer.remove(username);
                                if (event != null) {
                                    this.store.persistToBDB(username, event);
                                    continue;
                                }
                                log.debug((Object)"event removed from persistBuffer was null");
                            }
                            Thread.sleep(500L);
                        }
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e) {
                        log.error((Object)"interrupted?", (Throwable)e);
                    }
                }
                Object var5_5 = null;
            }
            catch (Throwable throwable) {
                Object var5_6 = null;
                log.warn((Object)"Exiting PersistThread...");
                throw throwable;
            }
            log.warn((Object)"Exiting PersistThread...");
        }
    }
}

