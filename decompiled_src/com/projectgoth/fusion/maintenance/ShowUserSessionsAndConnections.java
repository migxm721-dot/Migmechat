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
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.RegistryPrxHelper;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.ArrayList;

public class ShowUserSessionsAndConnections {
    private static Communicator iceCommunicator = Util.initialize((String[])new String[0]);
    private static RegistryPrx registryPrx;

    public static RegistryPrx getRegistry(String hostname) throws Exception {
        if (registryPrx == null) {
            if (iceCommunicator == null) {
                throw new Exception("Ice communicator has not been initialized");
            }
            ObjectPrx base = iceCommunicator.stringToProxy("Registry: tcp -h " + hostname + " -p 10000 -t 5000");
            registryPrx = RegistryPrxHelper.checkedCast(base);
            if (registryPrx == null) {
                throw new Exception("Invalid Registry proxy");
            }
        }
        return registryPrx;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: ShowUserSessionsAndConnections <registry hostname> <username to inspect>");
            System.exit(1);
        }
        String hostname = args[0];
        String username = args[1];
        try {
            UserPrx userProxy = ShowUserSessionsAndConnections.getRegistry(hostname).findUserObject(username);
            SessionPrx[] sessions = userProxy.getSessions();
            if (sessions == null || sessions.length < 1) {
                System.out.println("no sessions found for user [" + username + "]");
                return;
            }
            ArrayList<ConnectionPrx> connections = new ArrayList<ConnectionPrx>();
            System.out.println("found " + sessions.length + " sessions:");
            for (SessionPrx session : sessions) {
                String sessionID = session.getSessionID();
                ConnectionPrx connection = null;
                try {
                    connection = ShowUserSessionsAndConnections.getRegistry(hostname).findConnectionObject(sessionID);
                }
                catch (Exception e) {
                    // empty catch block
                }
                if (connection != null) {
                    System.out.println("\t session ID [" + sessionID + "] connectionProxy ICE endpoint [" + connection.ice_getEndpoints()[0] + "]");
                    connections.add(connection);
                    continue;
                }
                System.out.println("\t session ID [" + sessionID + "] has no connection according to registry?");
            }
        }
        catch (ObjectNotFoundException e) {
            System.err.println(username + " was not found in the registry, no problem.");
        }
        iceCommunicator.shutdown();
        System.exit(0);
    }
}

