/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.java_websocket.WebSocket
 *  org.java_websocket.framing.Framedata
 *  org.java_websocket.handshake.ClientHandshake
 *  org.java_websocket.server.WebSocketServer
 */
package com.projectgoth.fusion.gateway.websocket;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.gateway.ConnectionWS;
import com.projectgoth.fusion.gateway.GatewayWS;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class FusionWebSocketServer
extends WebSocketServer {
    private GatewayWS gway;
    private ConcurrentHashMap<WebSocket, ConnectionWS> connectionMap;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionWebSocketServer.class));

    public FusionWebSocketServer(int port, GatewayWS gway) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.gway = gway;
        this.connectionMap = new ConcurrentHashMap();
        if (log.isDebugEnabled()) {
            log.debug((Object)"Starting the websocket server - OK");
        }
    }

    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("got a new open connection from " + conn.getRemoteSocketAddress().getAddress()));
        }
        ConnectionWS connection = this.gway.createAndRegisterWSConnection(conn);
        this.connectionMap.put(conn, connection);
    }

    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("closing the connection for  " + conn.getRemoteSocketAddress().getAddress() + " because " + reason));
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        ConnectionWS connectionWS = this.connectionMap.get(conn);
        if (connectionWS == null) {
            return;
        }
        connectionWS.onWebsocketClose(code, reason, remote);
        this.connectionMap.remove(conn);
    }

    public void onMessage(WebSocket conn, String message) {
        ConnectionWS connectionWS;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Got message - " + message + " ; from " + conn.getRemoteSocketAddress()));
        }
        if ((connectionWS = this.connectionMap.get(conn)) == null) {
            return;
        }
        connectionWS.onWebsocketMessage(message);
    }

    public void onFragment(WebSocket conn, Framedata fragment) {
    }

    public void onError(WebSocket conn, Exception ex) {
        try {
            log.warn((Object)("got error - " + ex.getMessage() + " ; from " + conn.getRemoteSocketAddress().getAddress()));
        }
        catch (Exception e) {
            // empty catch block
        }
        ConnectionWS connectionWS = this.connectionMap.get(conn);
        if (connectionWS == null) {
            return;
        }
        connectionWS.onWebsocketError(ex);
    }
}

