/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.FusionException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AuthenticationServicePrx
extends ObjectPrx {
    public AuthenticationServiceResponseCodeEnum authenticate(Credential var1, String var2) throws FusionException;

    public AuthenticationServiceResponseCodeEnum authenticate(Credential var1, String var2, Map<String, String> var3) throws FusionException;

    public AuthenticationServiceResponseCodeEnum exists(int var1, byte var2) throws FusionException;

    public AuthenticationServiceResponseCodeEnum exists(int var1, byte var2, Map<String, String> var3) throws FusionException;

    public AuthenticationServiceResponseCodeEnum createCredential(Credential var1) throws FusionException;

    public AuthenticationServiceResponseCodeEnum createCredential(Credential var1, Map<String, String> var2) throws FusionException;

    public AuthenticationServiceResponseCodeEnum updateCredential(Credential var1) throws FusionException;

    public AuthenticationServiceResponseCodeEnum updateCredential(Credential var1, Map<String, String> var2) throws FusionException;

    public AuthenticationServiceResponseCodeEnum updateFusionCredential(Credential var1, String var2) throws FusionException;

    public AuthenticationServiceResponseCodeEnum updateFusionCredential(Credential var1, String var2, Map<String, String> var3) throws FusionException;

    public AuthenticationServiceResponseCodeEnum removeCredential(Credential var1) throws FusionException;

    public AuthenticationServiceResponseCodeEnum removeCredential(Credential var1, Map<String, String> var2) throws FusionException;

    public AuthenticationServiceCredentialResponse getCredential(int var1, byte var2) throws FusionException;

    public AuthenticationServiceCredentialResponse getCredential(int var1, byte var2, Map<String, String> var3) throws FusionException;

    public byte[] availableCredentialTypes(int var1) throws FusionException;

    public byte[] availableCredentialTypes(int var1, Map<String, String> var2) throws FusionException;

    public Credential[] getCredentialsForTypes(int var1, byte[] var2) throws FusionException;

    public Credential[] getCredentialsForTypes(int var1, byte[] var2, Map<String, String> var3) throws FusionException;

    public Credential[] getAllCredentials(int var1) throws FusionException;

    public Credential[] getAllCredentials(int var1, Map<String, String> var2) throws FusionException;

    public Credential[] getAllCredentialsFromOldSource(int var1) throws FusionException;

    public Credential[] getAllCredentialsFromOldSource(int var1, Map<String, String> var2) throws FusionException;

    public void migrateUserCredentials(int var1) throws FusionException;

    public void migrateUserCredentials(int var1, Map<String, String> var2) throws FusionException;

    public int userIDForFusionUsername(String var1) throws FusionException;

    public int userIDForFusionUsername(String var1, Map<String, String> var2) throws FusionException;

    public AuthenticationServiceCredentialResponse getLatestCredentialByUsernameAndPasswordType(String var1, byte var2);

    public AuthenticationServiceCredentialResponse getLatestCredentialByUsernameAndPasswordType(String var1, byte var2, Map<String, String> var3);

    public AuthenticationServiceResponseCodeEnum checkCredential(Credential var1);

    public AuthenticationServiceResponseCodeEnum checkCredential(Credential var1, Map<String, String> var2);

    public AuthenticationServiceResponseCodeEnum checkCredentialByUserId(int var1, String var2, byte var3);

    public AuthenticationServiceResponseCodeEnum checkCredentialByUserId(int var1, String var2, byte var3, Map<String, String> var4);

    public AuthenticationServiceResponseCodeEnum checkCredentialByUsername(String var1, String var2, byte var3);

    public AuthenticationServiceResponseCodeEnum checkCredentialByUsername(String var1, String var2, byte var3, Map<String, String> var4);
}

