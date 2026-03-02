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
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.IntArrayHelper;
import com.projectgoth.fusion.slice.ReputationService;
import com.projectgoth.fusion.slice.ScoreAndLevel;
import com.projectgoth.fusion.slice.ScoreAndLevelSequenceHelper;
import java.util.Arrays;

public abstract class _ReputationServiceDisp
extends ObjectImpl
implements ReputationService {
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::ReputationService"};
    private static final String[] __all = new String[]{"gatherAndProcess", "getUserLevel", "getUserScoreAndLevels", "ice_id", "ice_ids", "ice_isA", "ice_ping", "processPreviouslyDumpedData", "processPreviouslySortedData", "updateLastRunDate", "updateScoreFromPreviouslyProcessedData"};

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

    public final void gatherAndProcess() throws FusionException {
        this.gatherAndProcess(null);
    }

    public final int getUserLevel(String username) throws FusionException {
        return this.getUserLevel(username, null);
    }

    public final ScoreAndLevel[] getUserScoreAndLevels(int[] userIDs) throws FusionException {
        return this.getUserScoreAndLevels(userIDs, null);
    }

    public final void processPreviouslyDumpedData(String runDateString) throws FusionException {
        this.processPreviouslyDumpedData(runDateString, null);
    }

    public final void processPreviouslySortedData(String runDateString) throws FusionException {
        this.processPreviouslySortedData(runDateString, null);
    }

    public final void updateLastRunDate() throws FusionException {
        this.updateLastRunDate(null);
    }

    public final void updateScoreFromPreviouslyProcessedData(String runDateString) throws FusionException {
        this.updateScoreFromPreviouslyProcessedData(runDateString, null);
    }

    public static DispatchStatus ___gatherAndProcess(ReputationService __obj, Incoming __inS, Current __current) {
        _ReputationServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.gatherAndProcess(__current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___processPreviouslyDumpedData(ReputationService __obj, Incoming __inS, Current __current) {
        _ReputationServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String runDateString = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.processPreviouslyDumpedData(runDateString, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___processPreviouslySortedData(ReputationService __obj, Incoming __inS, Current __current) {
        _ReputationServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String runDateString = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.processPreviouslySortedData(runDateString, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___updateScoreFromPreviouslyProcessedData(ReputationService __obj, Incoming __inS, Current __current) {
        _ReputationServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String runDateString = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.updateScoreFromPreviouslyProcessedData(runDateString, __current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___updateLastRunDate(ReputationService __obj, Incoming __inS, Current __current) {
        _ReputationServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        __inS.is().skipEmptyEncaps();
        BasicStream __os = __inS.os();
        try {
            __obj.updateLastRunDate(__current);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getUserLevel(ReputationService __obj, Incoming __inS, Current __current) {
        _ReputationServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String username = __is.readString();
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            int __ret = __obj.getUserLevel(username, __current);
            __os.writeInt(__ret);
            return DispatchStatus.DispatchOK;
        }
        catch (FusionException ex) {
            __os.writeUserException((UserException)ex);
            return DispatchStatus.DispatchUserException;
        }
    }

    public static DispatchStatus ___getUserScoreAndLevels(ReputationService __obj, Incoming __inS, Current __current) {
        _ReputationServiceDisp.__checkMode((OperationMode)OperationMode.Normal, (OperationMode)__current.mode);
        BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int[] userIDs = IntArrayHelper.read(__is);
        __is.endReadEncaps();
        BasicStream __os = __inS.os();
        try {
            ScoreAndLevel[] __ret = __obj.getUserScoreAndLevels(userIDs, __current);
            ScoreAndLevelSequenceHelper.write(__os, __ret);
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
                return _ReputationServiceDisp.___gatherAndProcess(this, in, __current);
            }
            case 1: {
                return _ReputationServiceDisp.___getUserLevel(this, in, __current);
            }
            case 2: {
                return _ReputationServiceDisp.___getUserScoreAndLevels(this, in, __current);
            }
            case 3: {
                return _ReputationServiceDisp.___ice_id((Object)this, (Incoming)in, (Current)__current);
            }
            case 4: {
                return _ReputationServiceDisp.___ice_ids((Object)this, (Incoming)in, (Current)__current);
            }
            case 5: {
                return _ReputationServiceDisp.___ice_isA((Object)this, (Incoming)in, (Current)__current);
            }
            case 6: {
                return _ReputationServiceDisp.___ice_ping((Object)this, (Incoming)in, (Current)__current);
            }
            case 7: {
                return _ReputationServiceDisp.___processPreviouslyDumpedData(this, in, __current);
            }
            case 8: {
                return _ReputationServiceDisp.___processPreviouslySortedData(this, in, __current);
            }
            case 9: {
                return _ReputationServiceDisp.___updateLastRunDate(this, in, __current);
            }
            case 10: {
                return _ReputationServiceDisp.___updateScoreFromPreviouslyProcessedData(this, in, __current);
            }
        }
        assert (false);
        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(_ReputationServiceDisp.ice_staticId());
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
        ex.reason = "type com::projectgoth::fusion::slice::ReputationService was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::ReputationService was not generated with stream support";
        throw ex;
    }
}

