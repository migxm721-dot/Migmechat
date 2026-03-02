/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.je.DatabaseException
 *  com.sleepycat.je.Environment
 *  com.sleepycat.je.EnvironmentConfig
 *  com.sleepycat.persist.EntityCursor
 *  com.sleepycat.persist.EntityStore
 *  com.sleepycat.persist.StoreConfig
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.userevent;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.projectgoth.fusion.userevent.store.StoreUserEvent;
import com.projectgoth.fusion.userevent.store.StoreUserEventDA;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.util.StringUtils;

public class DumpEvents {
    private Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DumpEvents.class));
    private Environment environment;
    private EntityStore feedStore;
    private StoreUserEventDA storeUserEventDA;

    public DumpEvents(String path) {
        this.setup(new File(path));
    }

    private void setup(File environmentHome) {
        try {
            EnvironmentConfig environmentConfig = new EnvironmentConfig();
            StoreConfig storeConfig = new StoreConfig();
            environmentConfig.setAllowCreate(false);
            environmentConfig.setCachePercent(20);
            environmentConfig.setReadOnly(true);
            storeConfig.setAllowCreate(false);
            storeConfig.setReadOnly(true);
            this.environment = new Environment(environmentHome, environmentConfig);
            this.feedStore = new EntityStore(this.environment, "FeedStore", storeConfig);
            this.storeUserEventDA = new StoreUserEventDA(this.feedStore);
            this.log.info((Object)"Store opened successfully");
            this.log.info((Object)("Maximum cache memory: " + (int)((double)environmentConfig.getCachePercent() / 100.0 * (double)Runtime.getRuntime().maxMemory() / 1000000.0) + " MB"));
        }
        catch (DatabaseException e) {
            this.log.fatal((Object)"Error opening environment and store, exiting... ", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public void dump(String filterUsername) {
        block16: {
            EntityCursor cursor = null;
            this.log.info((Object)"getting cursor...");
            if (!StringUtils.hasLength((String)filterUsername) || "*".equals(filterUsername)) {
                cursor = this.storeUserEventDA.getPrimaryIndex().entities();
                this.log.info((Object)"moving cursor to last event");
                cursor.last();
                StoreUserEvent storeEvent = null;
                while ((storeEvent = (StoreUserEvent)cursor.prev()) != null) {
                    for (UserEvent event : storeEvent.getEvents()) {
                        System.out.println("username [" + storeEvent.getUsername() + "] " + event);
                    }
                }
            } else {
                StoreUserEvent storeEvent = (StoreUserEvent)this.storeUserEventDA.getPrimaryIndex().get((Object)filterUsername);
                System.out.println("username [" + storeEvent.getUsername() + "] mask [" + storeEvent.getReceivingMask());
                for (UserEvent event : storeEvent.getEvents()) {
                    System.out.println("username [" + storeEvent.getUsername() + "] " + event);
                }
            }
            Object var7_10 = null;
            try {
                if (cursor != null) {
                    cursor.close();
                }
                break block16;
            }
            catch (DatabaseException e2) {
                this.log.error((Object)"failed to close cursor", (Throwable)e2);
            }
            break block16;
            {
                catch (Exception e) {
                    this.log.error((Object)"failed to dump events", (Throwable)e);
                    Object var7_11 = null;
                    try {
                        if (cursor != null) {
                            cursor.close();
                        }
                        break block16;
                    }
                    catch (DatabaseException e2) {
                        this.log.error((Object)"failed to close cursor", (Throwable)e2);
                    }
                }
            }
            catch (Throwable throwable) {
                Object var7_12 = null;
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                catch (DatabaseException e2) {
                    this.log.error((Object)"failed to close cursor", (Throwable)e2);
                }
                throw throwable;
            }
        }
    }

    public void closeDb() {
        this.log.error((Object)"closing db");
        if (this.environment != null) {
            try {
                this.environment.close();
            }
            catch (DatabaseException e) {
                this.log.fatal((Object)"Error closing Environment, exiting... ", (Throwable)e);
                return;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) {
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        if (args.length < 1) {
            System.err.println("Usage: DumpEvents <data directory> <optional filter user>");
            System.exit(1);
        }
        DumpEvents dumpEvents = new DumpEvents(args[0]);
        try {
            dumpEvents.dump(args[1]);
            Object var3_2 = null;
            dumpEvents.closeDb();
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            dumpEvents.closeDb();
            throw throwable;
        }
    }
}

