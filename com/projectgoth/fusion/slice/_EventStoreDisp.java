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
import Ice.Object;
import Ice.ObjectImpl;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.OutputStream;
import Ice.UserException;
import IceInternal.BasicStream;
import IceInternal.Incoming;
import IceInternal.Patcher;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.EventStore;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice.UserEventIceArrayHelper;
import com.projectgoth.fusion.slice.UserEventIceHolder;
import java.util.Arrays;

public abstract class _EventStoreDisp
extends ObjectImpl
implements EventStore {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::EventStore"};
    private static final String[] __all = new String[]{"deleteUserEvents", "getPublishingPrivacyMask", "getReceivingPrivacyMask", "getUserEventsForUser", "getUserEventsGeneratedByUser", "ice_id", "ice_ids", "ice_isA", "ice_ping", "setPublishingPrivacyMask", "setReceivingPrivacyMask", "storeGeneratorEvent", "storeUserEvent"};

    protected void ice_copyStateFrom(Object __obj) throws CloneNotSupportedException {
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

    public final void deleteUserEvents(String username) throws FusionException {
        this.deleteUserEvents(username, null);
    }

    public final EventPrivacySettingIce getPublishingPrivacyMask(String username) throws FusionException {
        return this.getPublishingPrivacyMask(username, null);
    }

    public final EventPrivacySettingIce getReceivingPrivacyMask(String username) throws FusionException {
        return this.getReceivingPrivacyMask(username, null);
    }

    public final UserEventIce[] getUserEventsForUser(String username) throws FusionException {
        return this.getUserEventsForUser(username, null);
    }

    public final UserEventIce[] getUserEventsGeneratedByUser(String username) throws FusionException {
        return this.getUserEventsGeneratedByUser(username, null);
    }

    public final void setPublishingPrivacyMask(String username, EventPrivacySettingIce mask) throws FusionException {
        this.setPublishingPrivacyMask(username, mask, null);
    }

    public final void setReceivingPrivacyMask(String username, EventPrivacySettingIce mask) throws FusionException {
        this.setReceivingPrivacyMask(username, mask, null);
    }

    public final void storeGeneratorEvent(String username, UserEventIce event) throws FusionException {
        this.storeGeneratorEvent(username, event, null);
    }

    public final void storeUserEvent(String username, UserEventIce event) throws FusionException {
        this.storeUserEvent(username, event, null);
    }

    public static DispatchStatus ___storeUserEvent(EventStore __obj, Incoming __inS, Current __current) {
        _EventStoreDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        UserEventIceHolder event = new UserEventIceHolder();
        __is.readObject((Patcher)event.getPatcher());
        __is.readPendingObjects();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.storeUserEvent(username, event.value, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___storeGeneratorEvent(EventStore __obj, Incoming __inS, Current __current) {
        _EventStoreDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        UserEventIceHolder event = new UserEventIceHolder();
        __is.readObject((Patcher)event.getPatcher());
        __is.readPendingObjects();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.storeGeneratorEvent(username, event.value, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getUserEventsForUser(EventStore __obj, Incoming __inS, Current __current) {
        _EventStoreDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            UserEventIce[] __ret = __obj.getUserEventsForUser(username, __current);
            UserEventIceArrayHelper.write(__os, __ret);
            __os.writePendingObjects();
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getUserEventsGeneratedByUser(EventStore __obj, Incoming __inS, Current __current) {
        _EventStoreDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            UserEventIce[] __ret = __obj.getUserEventsGeneratedByUser(username, __current);
            UserEventIceArrayHelper.write(__os, __ret);
            __os.writePendingObjects();
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___deleteUserEvents(EventStore __obj, Incoming __inS, Current __current) {
        _EventStoreDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.deleteUserEvents(username, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getPublishingPrivacyMask(EventStore __obj, Incoming __inS, Current __current) {
        _EventStoreDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            EventPrivacySettingIce __ret = __obj.getPublishingPrivacyMask(username, __current);
            __ret.__write(__os);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___setPublishingPrivacyMask(EventStore __obj, Incoming __inS, Current __current) {
        _EventStoreDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        EventPrivacySettingIce mask = new EventPrivacySettingIce();
        mask.__read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.setPublishingPrivacyMask(username, mask, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getReceivingPrivacyMask(EventStore __obj, Incoming __inS, Current __current) {
        _EventStoreDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            EventPrivacySettingIce __ret = __obj.getReceivingPrivacyMask(username, __current);
            __ret.__write(__os);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___setReceivingPrivacyMask(EventStore __obj, Incoming __inS, Current __current) {
        _EventStoreDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        EventPrivacySettingIce mask = new EventPrivacySettingIce();
        mask.__read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.setReceivingPrivacyMask(username, mask, __current);
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
                return _EventStoreDisp.___deleteUserEvents(this, in, __current);
            }
            case 1: {
                return _EventStoreDisp.___getPublishingPrivacyMask(this, in, __current);
            }
            case 2: {
                return _EventStoreDisp.___getReceivingPrivacyMask(this, in, __current);
            }
            case 3: {
                return _EventStoreDisp.___getUserEventsForUser(this, in, __current);
            }
            case 4: {
                return _EventStoreDisp.___getUserEventsGeneratedByUser(this, in, __current);
            }
            case 5: {
                return _EventStoreDisp.___ice_id((Object)this, (Incoming)in, (Current)__current);
            }
            case 6: {
                return _EventStoreDisp.___ice_ids((Object)this, (Incoming)in, (Current)__current);
            }
            case 7: {
                return _EventStoreDisp.___ice_isA((Object)this, (Incoming)in, (Current)__current);
            }
            case 8: {
                return _EventStoreDisp.___ice_ping((Object)this, (Incoming)in, (Current)__current);
            }
            case 9: {
                return _EventStoreDisp.___setPublishingPrivacyMask(this, in, __current);
            }
            case 10: {
                return _EventStoreDisp.___setReceivingPrivacyMask(this, in, __current);
            }
            case 11: {
                return _EventStoreDisp.___storeGeneratorEvent(this, in, __current);
            }
            case 12: {
                return _EventStoreDisp.___storeUserEvent(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_EventStoreDisp.ice_staticId());
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
        ex.reason = "type com::projectgoth::fusion::slice::EventStore was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::EventStore was not generated with stream support";
        throw ex;
    }
}

