/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.OperationMode
 *  Ice.UnknownUserException
 *  Ice.UserException
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 *  IceInternal.LocalExceptionWrapper
 *  IceInternal.Outgoing
 */
package com.projectgoth.fusion.slice;

import Ice.LocalException;
import Ice.OperationMode;
import Ice.UnknownUserException;
import Ice.UserException;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import IceInternal.Outgoing;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.IntArrayHelper;
import com.projectgoth.fusion.slice.ScoreAndLevel;
import com.projectgoth.fusion.slice.ScoreAndLevelSequenceHelper;
import com.projectgoth.fusion.slice._ReputationServiceDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _ReputationServiceDelM
extends _ObjectDelM
implements _ReputationServiceDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void gatherAndProcess(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("gatherAndProcess", OperationMode.Normal, __ctx);
        try {
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var6_7 = null;
        }
        catch (Throwable throwable) {
            Object var6_8 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public int getUserLevel(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        int n;
        Outgoing __og = this.__handler.getOutgoing("getUserLevel", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                int __ret = __is.readInt();
                __is.endReadEncaps();
                n = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_13 = null;
        }
        catch (Throwable throwable) {
            Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return n;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public ScoreAndLevel[] getUserScoreAndLevels(int[] userIDs, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        ScoreAndLevel[] scoreAndLevelArray;
        Outgoing __og = this.__handler.getOutgoing("getUserScoreAndLevels", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                IntArrayHelper.write(__os, userIDs);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                ScoreAndLevel[] __ret = ScoreAndLevelSequenceHelper.read(__is);
                __is.endReadEncaps();
                scoreAndLevelArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var9_13 = null;
        }
        catch (Throwable throwable) {
            Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return scoreAndLevelArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void processPreviouslyDumpedData(String runDateString, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("processPreviouslyDumpedData", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(runDateString);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var7_10 = null;
        }
        catch (Throwable throwable) {
            Object var7_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void processPreviouslySortedData(String runDateString, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("processPreviouslySortedData", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(runDateString);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var7_10 = null;
        }
        catch (Throwable throwable) {
            Object var7_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateLastRunDate(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("updateLastRunDate", OperationMode.Normal, __ctx);
        try {
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var6_7 = null;
        }
        catch (Throwable throwable) {
            Object var6_8 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateScoreFromPreviouslyProcessedData(String runDateString, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("updateScoreFromPreviouslyProcessedData", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(runDateString);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try {
                if (!__ok) {
                    try {
                        __og.throwUserException();
                    }
                    catch (FusionException __ex) {
                        throw __ex;
                    }
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                __og.is().skipEmptyEncaps();
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            Object var7_10 = null;
        }
        catch (Throwable throwable) {
            Object var7_11 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }
}

