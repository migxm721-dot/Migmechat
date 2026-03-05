/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.TieBase
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.TieBase;
import com.projectgoth.fusion.slice.EmailUserNotification;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupEvent;
import com.projectgoth.fusion.slice.SMSUserNotification;
import com.projectgoth.fusion.slice._JobSchedulingServiceDisp;
import com.projectgoth.fusion.slice._JobSchedulingServiceOperations;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class _JobSchedulingServiceTie
extends _JobSchedulingServiceDisp
implements TieBase {
    private _JobSchedulingServiceOperations _ice_delegate;

    public _JobSchedulingServiceTie() {
    }

    public _JobSchedulingServiceTie(_JobSchedulingServiceOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_JobSchedulingServiceOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _JobSchedulingServiceTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_JobSchedulingServiceTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    @Override
    public void rescheduleFusionGroupEvent(GroupEvent event, Current __current) throws FusionException {
        this._ice_delegate.rescheduleFusionGroupEvent(event, __current);
    }

    @Override
    public int scheduleFusionGroupEvent(GroupEvent event, Current __current) throws FusionException {
        return this._ice_delegate.scheduleFusionGroupEvent(event, __current);
    }

    @Override
    public String scheduleFusionGroupEventNotificationViaAlert(int eventId, int groupId, long time, String message, Current __current) throws FusionException {
        return this._ice_delegate.scheduleFusionGroupEventNotificationViaAlert(eventId, groupId, time, message, __current);
    }

    @Override
    public String scheduleFusionGroupEventNotificationViaEmail(int eventId, int groupId, long time, EmailUserNotification note, Current __current) throws FusionException {
        return this._ice_delegate.scheduleFusionGroupEventNotificationViaEmail(eventId, groupId, time, note, __current);
    }

    @Override
    public String scheduleFusionGroupEventNotificationViaSMS(int eventId, int groupId, long time, SMSUserNotification note, Current __current) throws FusionException {
        return this._ice_delegate.scheduleFusionGroupEventNotificationViaSMS(eventId, groupId, time, note, __current);
    }

    @Override
    public void triggerJob(String jobName, String jobGroup, Map<String, String> jobDataMap, Current __current) throws FusionException {
        this._ice_delegate.triggerJob(jobName, jobGroup, jobDataMap, __current);
    }

    @Override
    public void unscheduleFusionGroupEvent(int groupEventID, Current __current) throws FusionException {
        this._ice_delegate.unscheduleFusionGroupEvent(groupEventID, __current);
    }
}

