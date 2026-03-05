/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.io.Serializable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AuthenticationServiceResponseCodeEnum
extends Enum<AuthenticationServiceResponseCodeEnum>
implements Serializable {
    public static final /* enum */ AuthenticationServiceResponseCodeEnum Failed = new AuthenticationServiceResponseCodeEnum();
    public static final /* enum */ AuthenticationServiceResponseCodeEnum Success = new AuthenticationServiceResponseCodeEnum();
    public static final /* enum */ AuthenticationServiceResponseCodeEnum UnknownUsername = new AuthenticationServiceResponseCodeEnum();
    public static final /* enum */ AuthenticationServiceResponseCodeEnum UnknownCredential = new AuthenticationServiceResponseCodeEnum();
    public static final /* enum */ AuthenticationServiceResponseCodeEnum InvalidCredential = new AuthenticationServiceResponseCodeEnum();
    public static final /* enum */ AuthenticationServiceResponseCodeEnum CredentialsExpired = new AuthenticationServiceResponseCodeEnum();
    public static final /* enum */ AuthenticationServiceResponseCodeEnum AuthenticationRateExceeded = new AuthenticationServiceResponseCodeEnum();
    public static final /* enum */ AuthenticationServiceResponseCodeEnum InvalidRequestingIP = new AuthenticationServiceResponseCodeEnum();
    public static final /* enum */ AuthenticationServiceResponseCodeEnum CredentialAlreadyExists = new AuthenticationServiceResponseCodeEnum();
    public static final /* enum */ AuthenticationServiceResponseCodeEnum InternalError = new AuthenticationServiceResponseCodeEnum();
    public static final /* enum */ AuthenticationServiceResponseCodeEnum UnknownError = new AuthenticationServiceResponseCodeEnum();
    public static final int _Failed = 0;
    public static final int _Success = 1;
    public static final int _UnknownUsername = 2;
    public static final int _UnknownCredential = 3;
    public static final int _InvalidCredential = 4;
    public static final int _CredentialsExpired = 5;
    public static final int _AuthenticationRateExceeded = 6;
    public static final int _InvalidRequestingIP = 7;
    public static final int _CredentialAlreadyExists = 8;
    public static final int _InternalError = 9;
    public static final int _UnknownError = 10;
    private static final /* synthetic */ AuthenticationServiceResponseCodeEnum[] $VALUES;

    public static AuthenticationServiceResponseCodeEnum[] values() {
        return (AuthenticationServiceResponseCodeEnum[])$VALUES.clone();
    }

    public static AuthenticationServiceResponseCodeEnum valueOf(String name) {
        return Enum.valueOf(AuthenticationServiceResponseCodeEnum.class, name);
    }

    public static AuthenticationServiceResponseCodeEnum convert(int val) {
        assert (val >= 0 && val < 11);
        return AuthenticationServiceResponseCodeEnum.values()[val];
    }

    public static AuthenticationServiceResponseCodeEnum convert(String val) {
        try {
            return AuthenticationServiceResponseCodeEnum.valueOf(val);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public int value() {
        return this.ordinal();
    }

    public void __write(BasicStream __os) {
        __os.writeByte((byte)this.value());
    }

    public static AuthenticationServiceResponseCodeEnum __read(BasicStream __is) {
        byte __v = __is.readByte(11);
        return AuthenticationServiceResponseCodeEnum.convert(__v);
    }

    static {
        $VALUES = new AuthenticationServiceResponseCodeEnum[]{Failed, Success, UnknownUsername, UnknownCredential, InvalidCredential, CredentialsExpired, AuthenticationRateExceeded, InvalidRequestingIP, CredentialAlreadyExists, InternalError, UnknownError};
    }
}

