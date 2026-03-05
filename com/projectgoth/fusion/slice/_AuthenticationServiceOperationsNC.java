/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.FusionException;

public interface _AuthenticationServiceOperationsNC {
    public AuthenticationServiceResponseCodeEnum authenticate(Credential var1, String var2) throws FusionException;

    public AuthenticationServiceResponseCodeEnum exists(int var1, byte var2) throws FusionException;

    public AuthenticationServiceResponseCodeEnum createCredential(Credential var1) throws FusionException;

    public AuthenticationServiceResponseCodeEnum updateCredential(Credential var1) throws FusionException;

    public AuthenticationServiceResponseCodeEnum updateFusionCredential(Credential var1, String var2) throws FusionException;

    public AuthenticationServiceResponseCodeEnum removeCredential(Credential var1) throws FusionException;

    public AuthenticationServiceCredentialResponse getCredential(int var1, byte var2) throws FusionException;

    public byte[] availableCredentialTypes(int var1) throws FusionException;

    public Credential[] getCredentialsForTypes(int var1, byte[] var2) throws FusionException;

    public Credential[] getAllCredentials(int var1) throws FusionException;

    public Credential[] getAllCredentialsFromOldSource(int var1) throws FusionException;

    public void migrateUserCredentials(int var1) throws FusionException;

    public int userIDForFusionUsername(String var1) throws FusionException;

    public AuthenticationServiceCredentialResponse getLatestCredentialByUsernameAndPasswordType(String var1, byte var2);

    public AuthenticationServiceResponseCodeEnum checkCredential(Credential var1);

    public AuthenticationServiceResponseCodeEnum checkCredentialByUserId(int var1, String var2, byte var3);

    public AuthenticationServiceResponseCodeEnum checkCredentialByUsername(String var1, String var2, byte var3);
}

