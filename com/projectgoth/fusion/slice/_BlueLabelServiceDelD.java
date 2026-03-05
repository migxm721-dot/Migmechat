/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.DispatchStatus
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
import Ice.Object;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.SystemException;
import Ice.UserException;
import Ice._ObjectDelD;
import IceInternal.Direct;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.BlueLabelOneVoucher;
import com.projectgoth.fusion.slice.BlueLabelService;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.WebServiceResponse;
import com.projectgoth.fusion.slice.WebServiceResponseHolder;
import com.projectgoth.fusion.slice._BlueLabelServiceDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _BlueLabelServiceDelD
extends _ObjectDelD
implements _BlueLabelServiceDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public WebServiceResponse authenticate(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "authenticate", OperationMode.Normal, __ctx);
        final WebServiceResponseHolder __result = new WebServiceResponseHolder();
        Direct __direct = null;
        try {
            WebServiceResponse webServiceResponse;
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BlueLabelService __servant = null;
                    try {
                        __servant = (BlueLabelService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.authenticate(username, __current);
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
                webServiceResponse = __result.value;
                java.lang.Object var9_11 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return webServiceResponse;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public WebServiceResponse fullVoucherRedemption(final String migUsername, final String userTicket, final BlueLabelOneVoucher voucher, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "fullVoucherRedemption", OperationMode.Normal, __ctx);
        final WebServiceResponseHolder __result = new WebServiceResponseHolder();
        Direct __direct = null;
        try {
            WebServiceResponse webServiceResponse;
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BlueLabelService __servant = null;
                    try {
                        __servant = (BlueLabelService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.fullVoucherRedemption(migUsername, userTicket, voucher, __current);
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
                webServiceResponse = __result.value;
                java.lang.Object var11_13 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var11_14 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return webServiceResponse;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public WebServiceResponse getAccountStatus(final String liveid, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getAccountStatus", OperationMode.Normal, __ctx);
        final WebServiceResponseHolder __result = new WebServiceResponseHolder();
        Direct __direct = null;
        try {
            WebServiceResponse webServiceResponse;
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BlueLabelService __servant = null;
                    try {
                        __servant = (BlueLabelService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getAccountStatus(liveid, __current);
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
                webServiceResponse = __result.value;
                java.lang.Object var9_11 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return webServiceResponse;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public WebServiceResponse registerAccount(final String username, final String password, final int countryCode, final String mobileNumber, final int secretQuestionCode, final String secretQuestionAnswer, final String firstName, final String lastName, final String nickName, final String dateOfBirth, final String sex, final String emailAddress, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "registerAccount", OperationMode.Normal, __ctx);
        final WebServiceResponseHolder __result = new WebServiceResponseHolder();
        Direct __direct = null;
        try {
            WebServiceResponse webServiceResponse;
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    BlueLabelService __servant = null;
                    try {
                        __servant = (BlueLabelService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.registerAccount(username, password, countryCode, mobileNumber, secretQuestionCode, secretQuestionAnswer, firstName, lastName, nickName, dateOfBirth, sex, emailAddress, __current);
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
                webServiceResponse = __result.value;
                java.lang.Object var20_22 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var20_23 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return webServiceResponse;
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
}

