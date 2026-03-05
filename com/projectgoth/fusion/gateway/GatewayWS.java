/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.java_websocket.WebSocket
 */
package com.projectgoth.fusion.gateway;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.ConnectionWS;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.gateway.GatewayContext;
import com.projectgoth.fusion.gateway.GatewayMain;
import com.projectgoth.fusion.gateway.websocket.FusionWebSocketServer;
import com.projectgoth.fusion.slice.FusionException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GatewayWS
extends Gateway {
    private FusionWebSocketServer webSocketServer;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GatewayWS.class));

    public GatewayWS(GatewayContext context) {
        super(context);
        this.serverType = Gateway.ServerType.WS;
    }

    @Override
    public void registerConnection(ConnectionI connection, int interest) throws FusionException {
        throw new FusionException("Unexpected function called");
    }

    @Override
    protected void registerPendingConnections() throws FusionException {
        throw new FusionException("Unexpected function called");
    }

    @Override
    public void onConnectionDisconnected(ConnectionI connection) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Connection " + connection.getDisplayName() + " disconnected"));
        }
        if (connection.getConnectionPrx() == null) {
            this.applicationContext.getPurger().remove(connection);
        }
    }

    @Override
    protected void processSelections(Set<SelectionKey> selectedKeys) throws FusionException {
        throw new FusionException("Unexpected function called");
    }

    @Override
    protected void onKeyAcceptable(SelectionKey key) throws FusionException {
        throw new FusionException("Unexpected function called");
    }

    public ConnectionWS createAndRegisterWSConnection(WebSocket conn) {
        ConnectionWS connection = new ConnectionWS((Gateway)this, conn, this.applicationContext);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Accepted connection " + connection.getDisplayName()));
        }
        this.applicationContext.getPurger().add(connection);
        return connection;
    }

    @Override
    protected void doAccepts() throws FusionException {
        throw new FusionException("Unexpected function called");
    }

    @Override
    protected void onKeyReadable(SelectionKey key) throws FusionException {
        throw new FusionException("Unexpected function called");
    }

    @Override
    protected void onKeyWritable(SelectionKey key) throws FusionException {
        throw new FusionException("Unexpected function called");
    }

    public void bindServerSocketWS() throws UnknownHostException {
        if (this.webSocketServer == null) {
            this.webSocketServer = new FusionWebSocketServer(this.port, this);
        }
        this.webSocketServer.start();
    }

    @Override
    public void bindServerSocket() throws IOException {
        this.bindServerSocketWS();
    }

    @Override
    public void run() {
    }

    @Override
    public void closeServerChannel() throws IOException {
        try {
            this.webSocketServer.stop();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    @Override
    public int getLocalPort() {
        return this.webSocketServer.getPort();
    }

    @Override
    public Selector getSelector() {
        return null;
    }

    @Override
    public void logDebugPurgingCompleted() {
        log.debug((Object)("Purging task completed. " + this.connectionCount.get() + " " + (Object)((Object)this.getServerType()) + " connection(s) left on the adaptor."));
    }

    public static void main(String[] args) {
        GatewayMain.main(args);
    }
}

