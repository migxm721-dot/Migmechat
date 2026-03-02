/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice._ObjectDel
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice._ObjectDel;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.FusionException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _AuthenticationServiceDel
extends _ObjectDel {
    public AuthenticationServiceResponseCodeEnum authenticate(Credential var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public AuthenticationServiceResponseCodeEnum exists(int var1, byte var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public AuthenticationServiceResponseCodeEnum createCredential(Credential var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public AuthenticationServiceResponseCodeEnum updateCredential(Credential var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public AuthenticationServiceResponseCodeEnum updateFusionCredential(Credential var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public AuthenticationServiceResponseCodeEnum removeCredential(Credential var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public AuthenticationServiceCredentialResponse getCredential(int var1, byte var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public byte[] availableCredentialTypes(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public Credential[] getCredentialsForTypes(int var1, byte[] var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public Credential[] getAllCredentials(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public Credential[] getAllCredentialsFromOldSource(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void migrateUserCredentials(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public int userIDForFusionUsername(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public AuthenticationServiceCredentialResponse getLatestCredentialByUsernameAndPasswordType(String var1, byte var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public AuthenticationServiceResponseCodeEnum checkCredential(Credential var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public AuthenticationServiceResponseCodeEnum checkCredentialByUserId(int var1, String var2, byte var3, Map<String, String> var4) throws LocalExceptionWrapper;

    public AuthenticationServiceResponseCodeEnum checkCredentialByUsername(String var1, String var2, byte var3, Map<String, String> var4) throws LocalExceptionWrapper;
}

