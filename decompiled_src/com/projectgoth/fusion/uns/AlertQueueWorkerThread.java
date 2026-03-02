/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.uns;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.uns.domain.AlertNote;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AlertQueueWorkerThread
extends Thread {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AlertQueueWorkerThread.class));
    private BlockingQueue<AlertNote> queue;
    private RegistryPrx registryProxy;
    private boolean dryRun;
    private AtomicLong alertsSent;

    public AlertQueueWorkerThread(BlockingQueue<AlertNote> queue, RegistryPrx registryProxy, boolean dryRun, AtomicLong alertsSent) {
        this.queue = queue;
        this.registryProxy = registryProxy;
        this.dryRun = dryRun;
        this.alertsSent = alertsSent;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void run() {
        while (true) {
            try {
                block3: while (true) {
                    note = this.queue.take();
                    usernames = note.getUsersArray();
                    userProxies = this.registryProxy.findUserObjects(usernames);
                    if (AlertQueueWorkerThread.log.isDebugEnabled()) {
                        AlertQueueWorkerThread.log.debug((Object)("sending alert [" + note.getText() + "] to [" + StringUtil.asString(userProxies) + "]"));
                    }
                    if (this.dryRun) continue;
                    arr$ = userProxies;
                    len$ = arr$.length;
                    i$ = 0;
                    while (true) {
                        if (i$ < len$) ** break;
                        continue block3;
                        userProxy = arr$[i$];
                        userProxy.putAlertMessage(note.getText(), null, (short)0);
                        this.alertsSent.incrementAndGet();
                        ++i$;
                    }
                    break;
                }
            }
            catch (Exception e) {
                AlertQueueWorkerThread.log.error((Object)"failed to send alert ", (Throwable)e);
                continue;
            }
            break;
        }
    }
}

