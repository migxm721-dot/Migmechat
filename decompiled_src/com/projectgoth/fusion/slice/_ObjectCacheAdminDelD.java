/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.DispatchStatus
 *  Ice.IntHolder
 *  Ice.Object
 *  Ice.OperationMode
 *  Ice.OperationNotExistException
 *  Ice.SystemException
 *  Ice.UserException
 *  Ice._ObjectDelD
 *  IceInternal.Direct
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.DispatchStatus;
import Ice.IntHolder;
import Ice.Object;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.SystemException;
import Ice.UserException;
import Ice._ObjectDelD;
import IceInternal.Direct;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectCacheAdmin;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice.ObjectCacheStatsHolder;
import com.projectgoth.fusion.slice.StringArrayHolder;
import com.projectgoth.fusion.slice._ObjectCacheAdminDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _ObjectCacheAdminDelD
extends _ObjectDelD
implements _ObjectCacheAdminDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getLoadWeightage(Map<String, String> __ctx) throws LocalExceptionWrapper {
        int n;
        final Current __current = new Current();
        this.__initCurrent(__current, "getLoadWeightage", OperationMode.Normal, __ctx);
        final IntHolder __result = new IntHolder();
        Direct __direct = null;
        __direct = new Direct(__current){

            public DispatchStatus run(Object __obj) {
                ObjectCacheAdmin __servant = null;
                try {
                    __servant = (ObjectCacheAdmin)__obj;
                }
                catch (ClassCastException __ex) {
                    throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                }
                __result.value = __servant.getLoadWeightage(__current);
                return DispatchStatus.DispatchOK;
            }
        };
        try {
            DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
            if (__status == DispatchStatus.DispatchUserException) {
                __direct.throwUserException();
            }
            assert (__status == DispatchStatus.DispatchOK);
            n = __result.value;
        }
        catch (Throwable throwable) {
            try {
                __direct.destroy();
                throw throwable;
            }
            catch (SystemException __ex) {
                throw __ex;
            }
            catch (Throwable __ex) {
                LocalExceptionWrapper.throwWrapper((Throwable)__ex);
                return __result.value;
            }
        }
        __direct.destroy();
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ObjectCacheStats getStats(Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        ObjectCacheStats objectCacheStats;
        final Current __current = new Current();
        this.__initCurrent(__current, "getStats", OperationMode.Normal, __ctx);
        final ObjectCacheStatsHolder __result = new ObjectCacheStatsHolder();
        Direct __direct = null;
        __direct = new Direct(__current){

            public DispatchStatus run(Object __obj) {
                ObjectCacheAdmin __servant = null;
                try {
                    __servant = (ObjectCacheAdmin)__obj;
                }
                catch (ClassCastException __ex) {
                    throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                }
                try {
                    __result.value = __servant.getStats(__current);
                    return DispatchStatus.DispatchOK;
                }
                catch (UserException __ex) {
                    this.setUserException(__ex);
                    return DispatchStatus.DispatchUserException;
                }
            }
        };
        try {
            DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
            if (__status == DispatchStatus.DispatchUserException) {
                __direct.throwUserException();
            }
            assert (__status == DispatchStatus.DispatchOK);
            objectCacheStats = __result.value;
        }
        catch (Throwable throwable) {
            try {
                __direct.destroy();
                throw throwable;
            }
            catch (FusionException __ex) {
                throw __ex;
            }
            catch (SystemException __ex) {
                throw __ex;
            }
            catch (Throwable __ex) {
                LocalExceptionWrapper.throwWrapper((Throwable)__ex);
                return __result.value;
            }
        }
        __direct.destroy();
        return objectCacheStats;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getUsernames(Map<String, String> __ctx) throws LocalExceptionWrapper {
        String[] stringArray;
        final Current __current = new Current();
        this.__initCurrent(__current, "getUsernames", OperationMode.Normal, __ctx);
        final StringArrayHolder __result = new StringArrayHolder();
        Direct __direct = null;
        __direct = new Direct(__current){

            public DispatchStatus run(Object __obj) {
                ObjectCacheAdmin __servant = null;
                try {
                    __servant = (ObjectCacheAdmin)__obj;
                }
                catch (ClassCastException __ex) {
                    throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                }
                __result.value = __servant.getUsernames(__current);
                return DispatchStatus.DispatchOK;
            }
        };
        try {
            DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
            if (__status == DispatchStatus.DispatchUserException) {
                __direct.throwUserException();
            }
            assert (__status == DispatchStatus.DispatchOK);
            stringArray = __result.value;
        }
        catch (Throwable throwable) {
            try {
                __direct.destroy();
                throw throwable;
            }
            catch (SystemException __ex) {
                throw __ex;
            }
            catch (Throwable __ex) {
                LocalExceptionWrapper.throwWrapper((Throwable)__ex);
                return __result.value;
            }
        }
        __direct.destroy();
        return stringArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int ping(Map<String, String> __ctx) throws LocalExceptionWrapper {
        int n;
        final Current __current = new Current();
        this.__initCurrent(__current, "ping", OperationMode.Normal, __ctx);
        final IntHolder __result = new IntHolder();
        Direct __direct = null;
        __direct = new Direct(__current){

            public DispatchStatus run(Object __obj) {
                ObjectCacheAdmin __servant = null;
                try {
                    __servant = (ObjectCacheAdmin)__obj;
                }
                catch (ClassCastException __ex) {
                    throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                }
                __result.value = __servant.ping(__current);
                return DispatchStatus.DispatchOK;
            }
        };
        try {
            DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
            if (__status == DispatchStatus.DispatchUserException) {
                __direct.throwUserException();
            }
            assert (__status == DispatchStatus.DispatchOK);
            n = __result.value;
        }
        catch (Throwable throwable) {
            try {
                __direct.destroy();
                throw throwable;
            }
            catch (SystemException __ex) {
                throw __ex;
            }
            catch (Throwable __ex) {
                LocalExceptionWrapper.throwWrapper((Throwable)__ex);
                return __result.value;
            }
        }
        __direct.destroy();
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void reloadEmotes(Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "reloadEmotes", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    ObjectCacheAdmin __servant = null;
                    try {
                        __servant = (ObjectCacheAdmin)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.reloadEmotes(__current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
            }
            finally {
                __direct.destroy();
            }
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLoadWeightage(final int weightage, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "setLoadWeightage", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    ObjectCacheAdmin __servant = null;
                    try {
                        __servant = (ObjectCacheAdmin)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.setLoadWeightage(weightage, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
            }
            finally {
                __direct.destroy();
            }
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }
}

