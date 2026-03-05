/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.ObjectPrx
 *  Ice.Util
 */
package com.projectgoth.fusion.maintenance;

import Ice.Communicator;
import Ice.ObjectPrx;
import Ice.Util;
import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.common.PortRegistry;
import com.projectgoth.fusion.slice.AuthenticationServicePrx;
import com.projectgoth.fusion.slice.AuthenticationServicePrxHelper;
import com.projectgoth.fusion.slice.Credential;

public class ShowCreds {
    private static Communicator iceCommunicator = Util.initialize((String[])new String[0]);
    private static AuthenticationServicePrx authenticationServicePrx;

    public static AuthenticationServicePrx getAuthenticationServiceProxy(String hostname) throws Exception {
        if (authenticationServicePrx == null) {
            if (iceCommunicator == null) {
                throw new Exception("Ice communicator has not been initialized");
            }
            ObjectPrx base = iceCommunicator.stringToProxy("AuthenticationService: tcp -h " + hostname + " -p " + PortRegistry.AUTHENTICATION_SERVICE.getPort() + " -t 5000");
            authenticationServicePrx = AuthenticationServicePrxHelper.checkedCast(base);
            if (authenticationServicePrx == null) {
                throw new Exception("Invalid AuthenticationService proxy");
            }
        }
        return authenticationServicePrx;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: ShowCreds <authentication hostname> <username>");
            System.exit(1);
        }
        try {
            String hostname = args[0];
            String username = args[1];
            int userid = ShowCreds.getAuthenticationServiceProxy(hostname).userIDForFusionUsername(username);
            Credential[] creds = ShowCreds.getAuthenticationServiceProxy(hostname).getAllCredentials(userid);
            System.out.println("credentials from new source:");
            for (Credential cred : creds) {
                System.out.println(cred.username + " -> [" + cred.password + "], " + PasswordType.fromValue(cred.passwordType).toString());
            }
            System.out.println();
            System.out.println("credentials from old source:");
            for (Credential cred : creds = ShowCreds.getAuthenticationServiceProxy(hostname).getAllCredentialsFromOldSource(userid)) {
                System.out.println(cred.username + " -> [" + cred.password + "], " + PasswordType.fromValue(cred.passwordType).toString());
            }
            System.out.println();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        iceCommunicator.shutdown();
        System.exit(1);
    }
}

