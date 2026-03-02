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
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.SystemException;
import Ice.UserException;
import Ice._ObjectDelD;
import IceInternal.Direct;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.AuthenticationService;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponseHolder;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnumHolder;
import com.projectgoth.fusion.slice.ByteArrayHolder;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.CredentialArrayHolder;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._AuthenticationServiceDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _AuthenticationServiceDelD
extends _ObjectDelD
implements _AuthenticationServiceDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceResponseCodeEnum authenticate(final Credential userCredential, final String clientIP, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "authenticate", OperationMode.Normal, __ctx);
        final AuthenticationServiceResponseCodeEnumHolder __result = new AuthenticationServiceResponseCodeEnumHolder();
        Direct __direct = null;
        try {
            AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.authenticate(userCredential, clientIP, __current);
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
                authenticationServiceResponseCodeEnum = __result.value;
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return authenticationServiceResponseCodeEnum;
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
    public byte[] availableCredentialTypes(final int userid, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "availableCredentialTypes", OperationMode.Normal, __ctx);
        final ByteArrayHolder __result = new ByteArrayHolder();
        Direct __direct = null;
        try {
            byte[] byArray;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.availableCredentialTypes(userid, __current);
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
                byArray = __result.value;
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return byArray;
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
    public AuthenticationServiceResponseCodeEnum checkCredential(final Credential userCredential, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "checkCredential", OperationMode.Normal, __ctx);
        final AuthenticationServiceResponseCodeEnumHolder __result = new AuthenticationServiceResponseCodeEnumHolder();
        Direct __direct = null;
        try {
            AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.checkCredential(userCredential, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                authenticationServiceResponseCodeEnum = __result.value;
                Object var9_10 = null;
            }
            catch (Throwable throwable) {
                Object var9_11 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return authenticationServiceResponseCodeEnum;
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
    public AuthenticationServiceResponseCodeEnum checkCredentialByUserId(final int userid, final String password, final byte passwordType, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "checkCredentialByUserId", OperationMode.Normal, __ctx);
        final AuthenticationServiceResponseCodeEnumHolder __result = new AuthenticationServiceResponseCodeEnumHolder();
        Direct __direct = null;
        try {
            AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.checkCredentialByUserId(userid, password, passwordType, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                authenticationServiceResponseCodeEnum = __result.value;
                Object var11_12 = null;
            }
            catch (Throwable throwable) {
                Object var11_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return authenticationServiceResponseCodeEnum;
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
    public AuthenticationServiceResponseCodeEnum checkCredentialByUsername(final String username, final String password, final byte passwordType, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "checkCredentialByUsername", OperationMode.Normal, __ctx);
        final AuthenticationServiceResponseCodeEnumHolder __result = new AuthenticationServiceResponseCodeEnumHolder();
        Direct __direct = null;
        try {
            AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.checkCredentialByUsername(username, password, passwordType, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                authenticationServiceResponseCodeEnum = __result.value;
                Object var11_12 = null;
            }
            catch (Throwable throwable) {
                Object var11_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return authenticationServiceResponseCodeEnum;
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
    public AuthenticationServiceResponseCodeEnum createCredential(final Credential userCredential, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "createCredential", OperationMode.Normal, __ctx);
        final AuthenticationServiceResponseCodeEnumHolder __result = new AuthenticationServiceResponseCodeEnumHolder();
        Direct __direct = null;
        try {
            AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.createCredential(userCredential, __current);
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
                authenticationServiceResponseCodeEnum = __result.value;
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return authenticationServiceResponseCodeEnum;
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
    public AuthenticationServiceResponseCodeEnum exists(final int userid, final byte passwordType, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "exists", OperationMode.Normal, __ctx);
        final AuthenticationServiceResponseCodeEnumHolder __result = new AuthenticationServiceResponseCodeEnumHolder();
        Direct __direct = null;
        try {
            AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.exists(userid, passwordType, __current);
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
                authenticationServiceResponseCodeEnum = __result.value;
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return authenticationServiceResponseCodeEnum;
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
    public Credential[] getAllCredentials(final int userid, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getAllCredentials", OperationMode.Normal, __ctx);
        final CredentialArrayHolder __result = new CredentialArrayHolder();
        Direct __direct = null;
        try {
            Credential[] credentialArray;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getAllCredentials(userid, __current);
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
                credentialArray = __result.value;
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return credentialArray;
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
    public Credential[] getAllCredentialsFromOldSource(final int userid, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getAllCredentialsFromOldSource", OperationMode.Normal, __ctx);
        final CredentialArrayHolder __result = new CredentialArrayHolder();
        Direct __direct = null;
        try {
            Credential[] credentialArray;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getAllCredentialsFromOldSource(userid, __current);
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
                credentialArray = __result.value;
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return credentialArray;
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
    public AuthenticationServiceCredentialResponse getCredential(final int userid, final byte passwordType, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getCredential", OperationMode.Normal, __ctx);
        final AuthenticationServiceCredentialResponseHolder __result = new AuthenticationServiceCredentialResponseHolder();
        Direct __direct = null;
        try {
            AuthenticationServiceCredentialResponse authenticationServiceCredentialResponse;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getCredential(userid, passwordType, __current);
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
                authenticationServiceCredentialResponse = __result.value;
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return authenticationServiceCredentialResponse;
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
    public Credential[] getCredentialsForTypes(final int userid, final byte[] passwordTypes, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "getCredentialsForTypes", OperationMode.Normal, __ctx);
        final CredentialArrayHolder __result = new CredentialArrayHolder();
        Direct __direct = null;
        try {
            Credential[] credentialArray;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.getCredentialsForTypes(userid, passwordTypes, __current);
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
                credentialArray = __result.value;
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return credentialArray;
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
    public AuthenticationServiceCredentialResponse getLatestCredentialByUsernameAndPasswordType(final String username, final byte passwordType, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "getLatestCredentialByUsernameAndPasswordType", OperationMode.Normal, __ctx);
        final AuthenticationServiceCredentialResponseHolder __result = new AuthenticationServiceCredentialResponseHolder();
        Direct __direct = null;
        try {
            AuthenticationServiceCredentialResponse authenticationServiceCredentialResponse;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.getLatestCredentialByUsernameAndPasswordType(username, passwordType, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                authenticationServiceCredentialResponse = __result.value;
                Object var10_11 = null;
            }
            catch (Throwable throwable) {
                Object var10_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return authenticationServiceCredentialResponse;
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
     */
    @Override
    public void migrateUserCredentials(final int userid, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "migrateUserCredentials", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __servant.migrateUserCredentials(userid, __current);
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
                Object var7_9 = null;
            }
            catch (Throwable throwable) {
                Object var7_10 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (FusionException __ex) {
            throw __ex;
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceResponseCodeEnum removeCredential(final Credential userCredential, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "removeCredential", OperationMode.Normal, __ctx);
        final AuthenticationServiceResponseCodeEnumHolder __result = new AuthenticationServiceResponseCodeEnumHolder();
        Direct __direct = null;
        try {
            AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.removeCredential(userCredential, __current);
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
                authenticationServiceResponseCodeEnum = __result.value;
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return authenticationServiceResponseCodeEnum;
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
    public AuthenticationServiceResponseCodeEnum updateCredential(final Credential userCredential, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "updateCredential", OperationMode.Normal, __ctx);
        final AuthenticationServiceResponseCodeEnumHolder __result = new AuthenticationServiceResponseCodeEnumHolder();
        Direct __direct = null;
        try {
            AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.updateCredential(userCredential, __current);
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
                authenticationServiceResponseCodeEnum = __result.value;
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return authenticationServiceResponseCodeEnum;
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
    public AuthenticationServiceResponseCodeEnum updateFusionCredential(final Credential userCredential, final String oldPassword, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "updateFusionCredential", OperationMode.Normal, __ctx);
        final AuthenticationServiceResponseCodeEnumHolder __result = new AuthenticationServiceResponseCodeEnumHolder();
        Direct __direct = null;
        try {
            AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.updateFusionCredential(userCredential, oldPassword, __current);
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
                authenticationServiceResponseCodeEnum = __result.value;
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return authenticationServiceResponseCodeEnum;
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
    public int userIDForFusionUsername(final String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        final Current __current = new Current();
        this.__initCurrent(__current, "userIDForFusionUsername", OperationMode.Normal, __ctx);
        final IntHolder __result = new IntHolder();
        Direct __direct = null;
        try {
            int n;
            __direct = new Direct(__current){

                public DispatchStatus run(Ice.Object __obj) {
                    AuthenticationService __servant = null;
                    try {
                        __servant = (AuthenticationService)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    try {
                        __result.value = __servant.userIDForFusionUsername(username, __current);
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
                n = __result.value;
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            return n;
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

