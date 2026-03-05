/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.system;

import Ice.LocalException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.EventStorePrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.userevent.domain.EventPrivacySetting;
import com.projectgoth.fusion.userevent.system.EventSystemI;
import com.projectgoth.fusion.userevent.system.EventTask;
import com.projectgoth.fusion.userevent.system.domain.UsernameAndUserEvent;
import org.apache.log4j.Logger;

public class DistributionTask
extends EventTask
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DistributionTask.class));
    private UsernameAndUserEvent userEvent;
    private EventStorePrx eventStoreProxy;

    public DistributionTask(UsernameAndUserEvent userEvent, EventSystemI eventSystemI, EventStorePrx eventStoreProxy) {
        super(eventSystemI);
        this.userEvent = userEvent;
        this.eventStoreProxy = eventStoreProxy;
    }

    public void run() {
        try {
            EventPrivacySetting mask;
            UserPrx userProxy = this.eventSystemI.getRegistryProxy().findUserObject(this.userEvent.getUsername());
            this.assignDisplayPicture(this.userEvent.getUserEvent());
            this.assignRuntimeValues(this.userEvent.getUserEvent());
            if (log.isDebugEnabled()) {
                log.debug((Object)("sending event for user [" + this.userEvent.getUsername() + "] to proxy [" + userProxy + "] with display picture [" + this.userEvent.getUserEvent().generatingUserDisplayPicture + "]"));
            }
            if ((mask = EventPrivacySetting.fromEventPrivacySettingIce(this.eventStoreProxy.getReceivingPrivacyMask(this.userEvent.getUsername()))).applyMask(this.userEvent.getUserEvent())) {
                userProxy.putEvent(this.userEvent.getUserEvent());
                this.eventSystemI.getDistributedEventsCounter().add();
            }
        }
        catch (ObjectNotFoundException e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("user [" + this.userEvent.getUsername() + "] is not online according to registry, doing nothing"));
            }
        }
        catch (FusionException e) {
            log.error((Object)("failed to distribute event for user [" + this.userEvent.getUsername() + "]"), (Throwable)((Object)e));
        }
        catch (LocalException e) {
            log.error((Object)("could not send event to gateway for user [" + this.userEvent.getUsername() + "]"), (Throwable)e);
        }
        catch (Exception e) {
            log.error((Object)("something really bad happened trying to distribute event for user [" + this.userEvent.getUsername() + "]"), (Throwable)e);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"done distributing event");
        }
    }
}

