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
 */
package com.projectgoth.fusion.userevent;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.userevent.EventTextTranslator;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.projectgoth.fusion.userevent.store.StoreGeneratorEvent;
import com.projectgoth.fusion.userevent.store.StoreGeneratorEventDA;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import java.io.File;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class DumpTranslateGeneratorEvents {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DumpTranslateGeneratorEvents.class));
    private Environment environment;
    private EntityStore generatedStore;
    private StoreGeneratorEventDA storeGeneratorEventDA;
    private EventTextTranslator translator = new EventTextTranslator();
    private static Date TWO_WEEKS_AGO = DateTimeUtils.minusDays(new Date(), 14);

    public DumpTranslateGeneratorEvents(String path) {
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
            this.generatedStore = new EntityStore(this.environment, "GeneratedStore", storeConfig);
            log.info((Object)("environment stats " + this.environment.getStats(null).toString()));
            this.storeGeneratorEventDA = new StoreGeneratorEventDA(this.generatedStore);
            log.info((Object)"Store opened successfully");
            log.info((Object)("Maximum cache memory: " + (int)((double)environmentConfig.getCachePercent() / 100.0 * (double)Runtime.getRuntime().maxMemory() / 1000000.0) + " MB"));
        }
        catch (DatabaseException e) {
            log.fatal((Object)"Error opening environment and store, exiting... ", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public void dump() {
        block10: {
            EntityCursor cursor = null;
            log.info((Object)"getting cursor...");
            cursor = this.storeGeneratorEventDA.getPrimaryIndex().entities();
            StoreGeneratorEvent storeEvent = null;
            while ((storeEvent = (StoreGeneratorEvent)cursor.next()) != null) {
                if (storeEvent.getEvents() == null) continue;
                for (UserEvent event : storeEvent.getEvents()) {
                    String clazz = event.getClass().toString();
                    if (event.getTimestamp() <= TWO_WEEKS_AGO.getTime()) continue;
                    System.out.println(clazz.substring(46) + "," + new Date(event.getTimestamp()) + "," + event.getGeneratingUsername() + "," + this.translator.translate(event.toIceEvent(), ClientType.MIDP2, null));
                }
            }
            Object var7_7 = null;
            try {
                cursor.close();
            }
            catch (DatabaseException e2) {
                log.error((Object)"failed to close cursor", (Throwable)e2);
            }
            break block10;
            {
                catch (Exception e) {
                    log.error((Object)"failed to dump events", (Throwable)e);
                    Object var7_8 = null;
                    try {
                        cursor.close();
                    }
                    catch (DatabaseException e2) {
                        log.error((Object)"failed to close cursor", (Throwable)e2);
                    }
                }
            }
            catch (Throwable throwable) {
                Object var7_9 = null;
                try {
                    cursor.close();
                }
                catch (DatabaseException e2) {
                    log.error((Object)"failed to close cursor", (Throwable)e2);
                }
                throw throwable;
            }
        }
    }

    public void closeDb() {
        log.error((Object)"closing db");
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) {
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        if (args.length < 1) {
            System.err.println("Usage: DumpEvents <data directory>");
            System.exit(1);
        }
        DumpTranslateGeneratorEvents dumpEvents = new DumpTranslateGeneratorEvents(args[0]);
        try {
            try {
                dumpEvents.dump();
            }
            catch (Throwable t) {
                log.error((Object)"problem dumping events ", t);
                Object var4_3 = null;
                dumpEvents.closeDb();
            }
            Object var4_2 = null;
            dumpEvents.closeDb();
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            dumpEvents.closeDb();
            throw throwable;
        }
    }
}

