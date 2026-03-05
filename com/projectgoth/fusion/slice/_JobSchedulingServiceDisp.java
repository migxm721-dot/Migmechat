/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.DispatchStatus
 *  Ice.InputStream
 *  Ice.MarshalException
 *  Ice.Object
 *  Ice.ObjectImpl
 *  Ice.OperationMode
 *  Ice.OperationNotExistException
 *  Ice.OutputStream
 *  Ice.UserException
 *  IceInternal.BasicStream
 *  IceInternal.Incoming
 *  IceInternal.Patcher
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.DispatchStatus;
import Ice.InputStream;
import Ice.MarshalException;
import Ice.ObjectImpl;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.OutputStream;
import Ice.UserException;
import IceInternal.BasicStream;
import IceInternal.Incoming;
import IceInternal.Patcher;
import com.projectgoth.fusion.slice.EmailUserNotification;
import com.projectgoth.fusion.slice.EmailUserNotificationHolder;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupEvent;
import com.projectgoth.fusion.slice.GroupEventHolder;
import com.projectgoth.fusion.slice.JobSchedulingService;
import com.projectgoth.fusion.slice.ParamMapHelper;
import com.projectgoth.fusion.slice.SMSUserNotification;
import com.projectgoth.fusion.slice.SMSUserNotificationHolder;
import java.util.Arrays;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class _JobSchedulingServiceDisp
extends ObjectImpl
implements JobSchedulingService {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::JobSchedulingService"};
    private static final String[] __all = new String[]{"ice_id", "ice_ids", "ice_isA", "ice_ping", "rescheduleFusionGroupEvent", "scheduleFusionGroupEvent", "scheduleFusionGroupEventNotificationViaAlert", "scheduleFusionGroupEventNotificationViaEmail", "scheduleFusionGroupEventNotificationViaSMS", "triggerJob", "unscheduleFusionGroupEvent"};

    protected void ice_copyStateFrom(Ice.Object __obj) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public boolean ice_isA(String s) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean ice_isA(String s, Current __current) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[] ice_ids() {
        return __ids;
    }

    public String[] ice_ids(Current __current) {
        return __ids;
    }

    public String ice_id() {
        return __ids[1];
    }

    public String ice_id(Current __current) {
        return __ids[1];
    }

    public static String ice_staticId() {
        return __ids[1];
    }

    @Override
    public final void rescheduleFusionGroupEvent(GroupEvent event) throws FusionException {
        this.rescheduleFusionGroupEvent(event, null);
    }

    @Override
    public final int scheduleFusionGroupEvent(GroupEvent event) throws FusionException {
        return this.scheduleFusionGroupEvent(event, null);
    }

    @Override
    public final String scheduleFusionGroupEventNotificationViaAlert(int eventId, int groupId, long time, String message) throws FusionException {
        return this.scheduleFusionGroupEventNotificationViaAlert(eventId, groupId, time, message, null);
    }

    @Override
    public final String scheduleFusionGroupEventNotificationViaEmail(int eventId, int groupId, long time, EmailUserNotification note) throws FusionException {
        return this.scheduleFusionGroupEventNotificationViaEmail(eventId, groupId, time, note, null);
    }

    @Override
    public final String scheduleFusionGroupEventNotificationViaSMS(int eventId, int groupId, long time, SMSUserNotification note) throws FusionException {
        return this.scheduleFusionGroupEventNotificationViaSMS(eventId, groupId, time, note, null);
    }

    @Override
    public final void triggerJob(String jobName, String jobGroup, Map<String, String> jobDataMap) throws FusionException {
        this.triggerJob(jobName, jobGroup, jobDataMap, null);
    }

    @Override
    public final void unscheduleFusionGroupEvent(int groupEventID) throws FusionException {
        this.unscheduleFusionGroupEvent(groupEventID, null);
    }

    public static DispatchStatus ___scheduleFusionGroupEventNotificationViaEmail(JobSchedulingService __obj, Incoming __inS, Current __current) {
        _JobSchedulingServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int eventId = __is.readInt();
        int groupId = __is.readInt();
        long time = __is.readLong();
        EmailUserNotificationHolder note = new EmailUserNotificationHolder();
        __is.readObject((Patcher)note.getPatcher());
        __is.readPendingObjects();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            String __ret = __obj.scheduleFusionGroupEventNotificationViaEmail(eventId, groupId, time, note.value, __current);
            __os.writeString(__ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___scheduleFusionGroupEventNotificationViaSMS(JobSchedulingService __obj, Incoming __inS, Current __current) {
        _JobSchedulingServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int eventId = __is.readInt();
        int groupId = __is.readInt();
        long time = __is.readLong();
        SMSUserNotificationHolder note = new SMSUserNotificationHolder();
        __is.readObject((Patcher)note.getPatcher());
        __is.readPendingObjects();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            String __ret = __obj.scheduleFusionGroupEventNotificationViaSMS(eventId, groupId, time, note.value, __current);
            __os.writeString(__ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___scheduleFusionGroupEventNotificationViaAlert(JobSchedulingService __obj, Incoming __inS, Current __current) {
        _JobSchedulingServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int eventId = __is.readInt();
        int groupId = __is.readInt();
        long time = __is.readLong();
        String message = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            String __ret = __obj.scheduleFusionGroupEventNotificationViaAlert(eventId, groupId, time, message, __current);
            __os.writeString(__ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___scheduleFusionGroupEvent(JobSchedulingService __obj, Incoming __inS, Current __current) {
        _JobSchedulingServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        GroupEventHolder event = new GroupEventHolder();
        __is.readObject((Patcher)event.getPatcher());
        __is.readPendingObjects();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            int __ret = __obj.scheduleFusionGroupEvent(event.value, __current);
            __os.writeInt(__ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___unscheduleFusionGroupEvent(JobSchedulingService __obj, Incoming __inS, Current __current) {
        _JobSchedulingServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int groupEventID = __is.readInt();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.unscheduleFusionGroupEvent(groupEventID, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___rescheduleFusionGroupEvent(JobSchedulingService __obj, Incoming __inS, Current __current) {
        _JobSchedulingServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        GroupEventHolder event = new GroupEventHolder();
        __is.readObject((Patcher)event.getPatcher());
        __is.readPendingObjects();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.rescheduleFusionGroupEvent(event.value, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___triggerJob(JobSchedulingService __obj, Incoming __inS, Current __current) {
        _JobSchedulingServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String jobName = __is.readString();
        String jobGroup = __is.readString();
        Map<String, String> jobDataMap = ParamMapHelper.read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.triggerJob(jobName, jobGroup, jobDataMap, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public DispatchStatus __dispatch(Incoming in, Current __current) {
        int pos = Arrays.binarySearch(__all, __current.operation);
        if (pos < 0) {
            throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
        }
        switch (pos) {
            case 0: {
                return _JobSchedulingServiceDisp.___ice_id((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 1: {
                return _JobSchedulingServiceDisp.___ice_ids((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 2: {
                return _JobSchedulingServiceDisp.___ice_isA((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 3: {
                return _JobSchedulingServiceDisp.___ice_ping((Ice.Object)this, (Incoming)in, (Current)__current);
            }
            case 4: {
                return _JobSchedulingServiceDisp.___rescheduleFusionGroupEvent(this, in, __current);
            }
            case 5: {
                return _JobSchedulingServiceDisp.___scheduleFusionGroupEvent(this, in, __current);
            }
            case 6: {
                return _JobSchedulingServiceDisp.___scheduleFusionGroupEventNotificationViaAlert(this, in, __current);
            }
            case 7: {
                return _JobSchedulingServiceDisp.___scheduleFusionGroupEventNotificationViaEmail(this, in, __current);
            }
            case 8: {
                return _JobSchedulingServiceDisp.___scheduleFusionGroupEventNotificationViaSMS(this, in, __current);
            }
            case 9: {
                return _JobSchedulingServiceDisp.___triggerJob(this, in, __current);
            }
            case 10: {
                return _JobSchedulingServiceDisp.___unscheduleFusionGroupEvent(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_JobSchedulingServiceDisp.ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void __read(BasicStream __is, boolean __rid) {
        if (__rid) {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void __write(OutputStream __outS) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::JobSchedulingService was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::JobSchedulingService was not generated with stream support";
        throw ex;
    }
}

