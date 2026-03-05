/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.Object
 *  Ice.OperationMode
 *  Ice.UnknownUserException
 *  Ice.UserException
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 *  IceInternal.LocalExceptionWrapper
 *  IceInternal.Outgoing
 *  IceInternal.Patcher
 */
package com.projectgoth.fusion.slice;

import Ice.LocalException;
import Ice.Object;
import Ice.OperationMode;
import Ice.UnknownUserException;
import Ice.UserException;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import IceInternal.Outgoing;
import IceInternal.Patcher;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponseHolder;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.ByteArrayHelper;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.CredentialArrayHelper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._AuthenticationServiceDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _AuthenticationServiceDelM
extends _ObjectDelM
implements _AuthenticationServiceDel {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceResponseCodeEnum authenticate(Credential userCredential, String clientIP, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
        Outgoing __og = this.__handler.getOutgoing("authenticate", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeObject((Object)userCredential);
                __os.writeString(clientIP);
                __os.writePendingObjects();
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
                AuthenticationServiceResponseCodeEnum __ret = AuthenticationServiceResponseCodeEnum.__read(__is);
                __is.endReadEncaps();
                authenticationServiceResponseCodeEnum = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var10_14 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var10_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return authenticationServiceResponseCodeEnum;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public byte[] availableCredentialTypes(int userid, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        byte[] byArray;
        Outgoing __og = this.__handler.getOutgoing("availableCredentialTypes", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userid);
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
                byte[] __ret = ByteArrayHelper.read(__is);
                __is.endReadEncaps();
                byArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var9_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return byArray;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceResponseCodeEnum checkCredential(Credential userCredential, Map<String, String> __ctx) throws LocalExceptionWrapper {
        AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
        Outgoing __og = this.__handler.getOutgoing("checkCredential", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeObject((Object)userCredential);
                __os.writePendingObjects();
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
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                AuthenticationServiceResponseCodeEnum __ret = AuthenticationServiceResponseCodeEnum.__read(__is);
                __is.endReadEncaps();
                authenticationServiceResponseCodeEnum = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var9_12 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_13 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return authenticationServiceResponseCodeEnum;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceResponseCodeEnum checkCredentialByUserId(int userid, String password, byte passwordType, Map<String, String> __ctx) throws LocalExceptionWrapper {
        AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
        Outgoing __og = this.__handler.getOutgoing("checkCredentialByUserId", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userid);
                __os.writeString(password);
                __os.writeByte(passwordType);
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
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                AuthenticationServiceResponseCodeEnum __ret = AuthenticationServiceResponseCodeEnum.__read(__is);
                __is.endReadEncaps();
                authenticationServiceResponseCodeEnum = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var11_14 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var11_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return authenticationServiceResponseCodeEnum;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceResponseCodeEnum checkCredentialByUsername(String username, String password, byte passwordType, Map<String, String> __ctx) throws LocalExceptionWrapper {
        AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
        Outgoing __og = this.__handler.getOutgoing("checkCredentialByUsername", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeString(password);
                __os.writeByte(passwordType);
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
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                AuthenticationServiceResponseCodeEnum __ret = AuthenticationServiceResponseCodeEnum.__read(__is);
                __is.endReadEncaps();
                authenticationServiceResponseCodeEnum = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var11_14 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var11_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return authenticationServiceResponseCodeEnum;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceResponseCodeEnum createCredential(Credential userCredential, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
        Outgoing __og = this.__handler.getOutgoing("createCredential", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeObject((Object)userCredential);
                __os.writePendingObjects();
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
                AuthenticationServiceResponseCodeEnum __ret = AuthenticationServiceResponseCodeEnum.__read(__is);
                __is.endReadEncaps();
                authenticationServiceResponseCodeEnum = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var9_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return authenticationServiceResponseCodeEnum;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceResponseCodeEnum exists(int userid, byte passwordType, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
        Outgoing __og = this.__handler.getOutgoing("exists", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userid);
                __os.writeByte(passwordType);
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
                AuthenticationServiceResponseCodeEnum __ret = AuthenticationServiceResponseCodeEnum.__read(__is);
                __is.endReadEncaps();
                authenticationServiceResponseCodeEnum = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var10_14 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var10_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return authenticationServiceResponseCodeEnum;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Credential[] getAllCredentials(int userid, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Credential[] credentialArray;
        Outgoing __og = this.__handler.getOutgoing("getAllCredentials", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userid);
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
                Credential[] __ret = CredentialArrayHelper.read(__is);
                __is.readPendingObjects();
                __is.endReadEncaps();
                credentialArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var9_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return credentialArray;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Credential[] getAllCredentialsFromOldSource(int userid, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Credential[] credentialArray;
        Outgoing __og = this.__handler.getOutgoing("getAllCredentialsFromOldSource", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userid);
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
                Credential[] __ret = CredentialArrayHelper.read(__is);
                __is.readPendingObjects();
                __is.endReadEncaps();
                credentialArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var9_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return credentialArray;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceCredentialResponse getCredential(int userid, byte passwordType, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        AuthenticationServiceCredentialResponse authenticationServiceCredentialResponse;
        Outgoing __og = this.__handler.getOutgoing("getCredential", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userid);
                __os.writeByte(passwordType);
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
                AuthenticationServiceCredentialResponseHolder __ret = new AuthenticationServiceCredentialResponseHolder();
                __is.readObject((Patcher)__ret.getPatcher());
                __is.readPendingObjects();
                __is.endReadEncaps();
                authenticationServiceCredentialResponse = __ret.value;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var10_14 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var10_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return authenticationServiceCredentialResponse;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Credential[] getCredentialsForTypes(int userid, byte[] passwordTypes, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Credential[] credentialArray;
        Outgoing __og = this.__handler.getOutgoing("getCredentialsForTypes", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userid);
                ByteArrayHelper.write(__os, passwordTypes);
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
                Credential[] __ret = CredentialArrayHelper.read(__is);
                __is.readPendingObjects();
                __is.endReadEncaps();
                credentialArray = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var10_14 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var10_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return credentialArray;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceCredentialResponse getLatestCredentialByUsernameAndPasswordType(String username, byte passwordType, Map<String, String> __ctx) throws LocalExceptionWrapper {
        AuthenticationServiceCredentialResponse authenticationServiceCredentialResponse;
        Outgoing __og = this.__handler.getOutgoing("getLatestCredentialByUsernameAndPasswordType", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeByte(passwordType);
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
                    catch (UserException __ex) {
                        throw new UnknownUserException(__ex.ice_name());
                    }
                }
                BasicStream __is = __og.is();
                __is.startReadEncaps();
                AuthenticationServiceCredentialResponseHolder __ret = new AuthenticationServiceCredentialResponseHolder();
                __is.readObject((Patcher)__ret.getPatcher());
                __is.readPendingObjects();
                __is.endReadEncaps();
                authenticationServiceCredentialResponse = __ret.value;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var10_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var10_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return authenticationServiceCredentialResponse;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void migrateUserCredentials(int userid, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        Outgoing __og = this.__handler.getOutgoing("migrateUserCredentials", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeInt(userid);
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
            java.lang.Object var7_10 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var7_11 = null;
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
    public AuthenticationServiceResponseCodeEnum removeCredential(Credential userCredential, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
        Outgoing __og = this.__handler.getOutgoing("removeCredential", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeObject((Object)userCredential);
                __os.writePendingObjects();
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
                AuthenticationServiceResponseCodeEnum __ret = AuthenticationServiceResponseCodeEnum.__read(__is);
                __is.endReadEncaps();
                authenticationServiceResponseCodeEnum = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var9_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return authenticationServiceResponseCodeEnum;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceResponseCodeEnum updateCredential(Credential userCredential, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
        Outgoing __og = this.__handler.getOutgoing("updateCredential", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeObject((Object)userCredential);
                __os.writePendingObjects();
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
                AuthenticationServiceResponseCodeEnum __ret = AuthenticationServiceResponseCodeEnum.__read(__is);
                __is.endReadEncaps();
                authenticationServiceResponseCodeEnum = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var9_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return authenticationServiceResponseCodeEnum;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AuthenticationServiceResponseCodeEnum updateFusionCredential(Credential userCredential, String oldPassword, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        AuthenticationServiceResponseCodeEnum authenticationServiceResponseCodeEnum;
        Outgoing __og = this.__handler.getOutgoing("updateFusionCredential", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeObject((Object)userCredential);
                __os.writeString(oldPassword);
                __os.writePendingObjects();
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
                AuthenticationServiceResponseCodeEnum __ret = AuthenticationServiceResponseCodeEnum.__read(__is);
                __is.endReadEncaps();
                authenticationServiceResponseCodeEnum = __ret;
            }
            catch (LocalException __ex) {
                throw new LocalExceptionWrapper(__ex, false);
            }
            java.lang.Object var10_14 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var10_15 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return authenticationServiceResponseCodeEnum;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public int userIDForFusionUsername(String username, Map<String, String> __ctx) throws LocalExceptionWrapper, FusionException {
        int n;
        Outgoing __og = this.__handler.getOutgoing("userIDForFusionUsername", OperationMode.Normal, __ctx);
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
            java.lang.Object var9_13 = null;
        }
        catch (Throwable throwable) {
            java.lang.Object var9_14 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
        return n;
    }
}

