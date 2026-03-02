/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.FusionException;

public interface _AuthenticationServiceOperations {
    public AuthenticationServiceResponseCodeEnum authenticate(Credential var1, String var2, Current var3) throws FusionException;

    public AuthenticationServiceResponseCodeEnum exists(int var1, byte var2, Current var3) throws FusionException;

    public AuthenticationServiceResponseCodeEnum createCredential(Credential var1, Current var2) throws FusionException;

    public AuthenticationServiceResponseCodeEnum updateCredential(Credential var1, Current var2) throws FusionException;

    public AuthenticationServiceResponseCodeEnum updateFusionCredential(Credential var1, String var2, Current var3) throws FusionException;

    public AuthenticationServiceResponseCodeEnum removeCredential(Credential var1, Current var2) throws FusionException;

    public AuthenticationServiceCredentialResponse getCredential(int var1, byte var2, Current var3) throws FusionException;

    public byte[] availableCredentialTypes(int var1, Current var2) throws FusionException;

    public Credential[] getCredentialsForTypes(int var1, byte[] var2, Current var3) throws FusionException;

    public Credential[] getAllCredentials(int var1, Current var2) throws FusionException;

    public Credential[] getAllCredentialsFromOldSource(int var1, Current var2) throws FusionException;

    public void migrateUserCredentials(int var1, Current var2) throws FusionException;

    public int userIDForFusionUsername(String var1, Current var2) throws FusionException;

    public AuthenticationServiceCredentialResponse getLatestCredentialByUsernameAndPasswordType(String var1, byte var2, Current var3);

    public AuthenticationServiceResponseCodeEnum checkCredential(Credential var1, Current var2);

    public AuthenticationServiceResponseCodeEnum checkCredentialByUserId(int var1, String var2, byte var3, Current var4);

    public AuthenticationServiceResponseCodeEnum checkCredentialByUsername(String var1, String var2, byte var3, Current var4);
}

