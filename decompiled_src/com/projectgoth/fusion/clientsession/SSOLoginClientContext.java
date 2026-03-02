/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 */
package com.projectgoth.fusion.clientsession;

import com.projectgoth.fusion.clientsession.LoginException;
import com.projectgoth.fusion.clientsession.SSOLoginSessionInfo;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.slice.AuthenticationServicePrx;
import com.projectgoth.fusion.slice.FusionException;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface SSOLoginClientContext {
    public AuthenticationServicePrx findAuthenticationServiceProxy(SSOLoginSessionInfo var1);

    public void captchaRequired(SSOLoginSessionInfo var1);

    public void ssoSessionCreated(SSOLoginSessionInfo var1, UserData var2) throws RemoteException, CreateException, FusionException, LoginException;

    public void errorOccurred(String var1);

    public void errorOccurred(FusionPktError.Code var1, String var2);

    public void errorOccurred(String var1, Exception var2);
}

