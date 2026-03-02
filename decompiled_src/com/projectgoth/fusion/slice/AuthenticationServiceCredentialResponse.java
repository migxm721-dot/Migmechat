/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.InputStream
 *  Ice.MarshalException
 *  Ice.Object
 *  Ice.ObjectFactory
 *  Ice.ObjectImpl
 *  Ice.OutputStream
 *  IceInternal.BasicStream
 *  IceInternal.Ex
 *  IceInternal.Patcher
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.InputStream;
import Ice.MarshalException;
import Ice.Object;
import Ice.ObjectFactory;
import Ice.ObjectImpl;
import Ice.OutputStream;
import IceInternal.BasicStream;
import IceInternal.Ex;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.Credential;
import java.util.Arrays;

public class AuthenticationServiceCredentialResponse
extends ObjectImpl {
    private static ObjectFactory _factory = new __F();
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::AuthenticationServiceCredentialResponse"};
    public AuthenticationServiceResponseCodeEnum code;
    public Credential userCredential;

    public AuthenticationServiceCredentialResponse() {
    }

    public AuthenticationServiceCredentialResponse(AuthenticationServiceResponseCodeEnum code, Credential userCredential) {
        this.code = code;
        this.userCredential = userCredential;
    }

    public static ObjectFactory ice_factory() {
        return _factory;
    }

    public boolean ice_isA(String s) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean ice_isA(String s, Current __current) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[] ice_ids() {
        return __ids;
    }

    public String[] ice_ids(Current __current) {
        return __ids;
    }

    public String ice_id() {
        return __ids[1];
    }

    public String ice_id(Current __current) {
        return __ids[1];
    }

    public static String ice_staticId() {
        return __ids[1];
    }

    public void __write(BasicStream __os) {
        __os.writeTypeId(AuthenticationServiceCredentialResponse.ice_staticId());
        __os.startWriteSlice();
        this.code.__write(__os);
        __os.writeObject((Object)this.userCredential);
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void __read(BasicStream __is, boolean __rid) {
        if (__rid) {
            __is.readTypeId();
        }
        __is.startReadSlice();
        this.code = AuthenticationServiceResponseCodeEnum.__read(__is);
        __is.readObject((IceInternal.Patcher)new Patcher());
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void __write(OutputStream __outS) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::AuthenticationServiceCredentialResponse was not generated with stream support";
        throw ex;
    }

    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::AuthenticationServiceCredentialResponse was not generated with stream support";
        throw ex;
    }

    private class Patcher
    implements IceInternal.Patcher {
        private Patcher() {
        }

        public void patch(Object v) {
            try {
                AuthenticationServiceCredentialResponse.this.userCredential = (Credential)v;
            }
            catch (ClassCastException ex) {
                Ex.throwUOE((String)this.type(), (String)v.ice_id());
            }
        }

        public String type() {
            return "::com::projectgoth::fusion::slice::Credential";
        }
    }

    private static class __F
    implements ObjectFactory {
        private __F() {
        }

        public Object create(String type) {
            assert (type.equals(AuthenticationServiceCredentialResponse.ice_staticId()));
            return new AuthenticationServiceCredentialResponse();
        }

        public void destroy() {
        }
    }
}

