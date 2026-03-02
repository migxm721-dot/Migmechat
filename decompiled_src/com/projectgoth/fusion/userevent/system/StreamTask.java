/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.ObjectNotExistException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.system;

import Ice.LocalException;
import Ice.ObjectNotExistException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.userevent.system.EventSystemI;
import com.projectgoth.fusion.userevent.system.EventTask;
import com.projectgoth.fusion.userevent.system.domain.UsernameAndUserEvents;
import org.apache.log4j.Logger;

public class StreamTask
extends EventTask
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(StreamTask.class));
    private UsernameAndUserEvents userEvents;
    private ConnectionPrx connectionProxy;

    public StreamTask(UsernameAndUserEvents userEvents, ConnectionPrx connectionProxy, EventSystemI eventSystemI) {
        super(eventSystemI);
        this.userEvents = userEvents;
        this.connectionProxy = connectionProxy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void run() {
        boolean streamed = false;
        try {
            try {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("sending event for user [" + this.userEvents.getUsername() + "] to proxy [" + this.connectionProxy + "]"));
                }
                for (UserEventIce event : this.userEvents.getUserEvents()) {
                    this.assignDisplayPicture(event);
                    this.assignRuntimeValues(event);
                    if (this.connectionProxy == null) continue;
                    this.connectionProxy.putEvent(event);
                    streamed = true;
                }
                Object var7_10 = null;
                if (streamed) {
                    this.eventSystemI.getStreamedEventsCounter().add();
                }
            }
            catch (FusionException e) {
                log.error((Object)("failed to distribute event for user [" + this.userEvents.getUsername() + "]"), (Throwable)((Object)e));
                Object var7_11 = null;
                if (streamed) {
                    this.eventSystemI.getStreamedEventsCounter().add();
                }
                if (!log.isDebugEnabled()) return;
                log.debug((Object)"done distributing event");
                return;
            }
            catch (ObjectNotExistException e) {
                log.warn((Object)"failed to send event to user, the connectionProxy no longer exists, the user probably got disconnected during/after login");
                Object var7_12 = null;
                if (streamed) {
                    this.eventSystemI.getStreamedEventsCounter().add();
                }
                if (!log.isDebugEnabled()) return;
                log.debug((Object)"done distributing event");
                return;
            }
            catch (LocalException e) {
                log.error((Object)("could not send event to gateway for user [" + this.userEvents.getUsername() + "]"), (Throwable)e);
                Object var7_13 = null;
                if (streamed) {
                    this.eventSystemI.getStreamedEventsCounter().add();
                }
                if (!log.isDebugEnabled()) return;
                log.debug((Object)"done distributing event");
                return;
            }
            catch (Exception e) {
                log.error((Object)("something really bad happened trying to distribute event for user [" + this.userEvents.getUsername() + "]"), (Throwable)e);
                Object var7_14 = null;
                if (streamed) {
                    this.eventSystemI.getStreamedEventsCounter().add();
                }
                if (!log.isDebugEnabled()) return;
                log.debug((Object)"done distributing event");
                return;
            }
        }
        catch (Throwable throwable) {
            Object var7_15 = null;
            if (streamed) {
                this.eventSystemI.getStreamedEventsCounter().add();
            }
            if (!log.isDebugEnabled()) throw throwable;
            log.debug((Object)"done distributing event");
            throw throwable;
        }
        if (!log.isDebugEnabled()) return;
        log.debug((Object)"done distributing event");
    }
}

