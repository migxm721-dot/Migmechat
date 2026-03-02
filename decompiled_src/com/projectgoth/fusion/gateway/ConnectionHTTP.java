/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 */
package com.projectgoth.fusion.gateway;

import Ice.Current;
import com.projectgoth.fusion.clientsession.SSOLogin;
import com.projectgoth.fusion.common.ByteBufferHelper;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.fdl.enums.DeviceModeType;
import com.projectgoth.fusion.fdl.packets.FusionPktDataCreateSession;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.gateway.GatewayContext;
import com.projectgoth.fusion.gateway.HTTPRequest;
import com.projectgoth.fusion.gateway.HTTPResponse;
import com.projectgoth.fusion.gateway.PacketProcessor;
import com.projectgoth.fusion.gateway.PacketProcessorCallback;
import com.projectgoth.fusion.gateway.packet.FusionPacketFactory;
import com.projectgoth.fusion.gateway.packet.FusionPktCreateSession;
import com.projectgoth.fusion.gateway.packet.FusionPktCreateSessionOld;
import com.projectgoth.fusion.gateway.packet.FusionPktHTTPPollOld;
import com.projectgoth.fusion.gateway.packet.FusionPktHttpPoll;
import com.projectgoth.fusion.gateway.packet.FusionPktLogin;
import com.projectgoth.fusion.gateway.packet.FusionPktLoginOld;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.apache.log4j.Logger;
import org.json.JSONException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ConnectionHTTP
extends ConnectionI
implements PacketProcessorCallback {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ConnectionHTTP.class));
    public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final int READ_BUFFER_SIZE = 5120;
    private static final int MAX_READ_BUFFER_SIZE = 358400;
    private static final String HTTP_ERROR = "HTTP/1.1 500 ERROR\r\n\r\n";
    private Format format;
    private ByteBuffer readBuffer = ByteBuffer.allocate(5120);
    private ByteBuffer writeBuffer;
    private PacketProcessor packetProcessor = new PacketProcessor(this, this);
    private Queue<FusionPacket> outgoingQueue;
    private int jsonVersion = 1;
    private boolean isPermanent;

    public ConnectionHTTP(Gateway gateway, SocketChannel channel, GatewayContext gatewayContext) {
        super(gateway, channel, gatewayContext);
    }

    public ConnectionHTTP(ConnectionHTTP connection) {
        super(connection);
        this.format = connection.format;
        this.jsonVersion = connection.jsonVersion;
    }

    @Override
    public Gateway.ServerType getServerType() {
        return Gateway.ServerType.HTTP;
    }

    @Override
    public void onChannelReadable() {
        try {
            this.processHTTPRequest(this.readHTTPRequest());
        }
        catch (BufferUnderflowException e) {
            try {
                this.readBuffer = ByteBufferHelper.adjustSize(this.readBuffer, 5120, 358400, 2.0);
                this.gateway.registerConnection(this, 1);
            }
            catch (BufferOverflowException ie) {
                log.warn((Object)("Terminating connection " + this.getDisplayName() + " - Read buffer is full"));
                this.disconnect();
            }
            catch (FusionException fe) {
                if (SystemPropertyEntities.Temp.Cache.se493WebSocketsEnabled.getValue().booleanValue()) {
                    log.warn((Object)("Terminating connection " + this.getDisplayName() + " - Trying to make invalid connection to gateway"), (Throwable)((Object)fe));
                    this.disconnect();
                }
            }
        }
        catch (EOFException eofe) {
            this.disconnect();
        }
        catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)(e.getClass().getName() + " caught while reading on port " + this.getRemotePort()), (Throwable)e);
                log.debug((Object)("Cause=" + e.getCause()));
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                log.debug((Object)sw.toString());
            }
            this.writeHTTPResponse(HTTP_ERROR, true);
            this.disconnect();
        }
    }

    @Override
    public void onChannelWritable() {
        try {
            this.channel.write(this.writeBuffer);
            if (this.writeBuffer.hasRemaining()) {
                this.gateway.registerConnection(this, 4);
            } else if (this.format == Format.BINARY) {
                this.disconnect();
            } else {
                this.gateway.registerConnection(this, 1);
            }
        }
        catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)(e.getClass().getName() + " caught while writing to port " + this.getRemotePort()), (Throwable)e);
            }
            this.disconnect();
        }
    }

    private HTTPRequest readHTTPRequest() throws IOException {
        int bytesRead = this.channel.read(this.readBuffer);
        if (bytesRead == -1) {
            throw new EOFException("Socket closed - EOF");
        }
        if (bytesRead == 0) {
            throw new BufferUnderflowException();
        }
        ByteBuffer buffer = (ByteBuffer)this.readBuffer.duplicate().flip();
        HTTPRequest httpRequest = new HTTPRequest();
        httpRequest.read(buffer);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Receiving request on port " + this.getRemotePort() + "\n" + httpRequest + "\n"));
        }
        this.readBuffer = buffer.compact();
        String contentType = httpRequest.getProperty("Content-Type");
        this.format = contentType != null && contentType.regionMatches(true, 0, "text/xml", 0, 8) ? Format.XML : (contentType != null && contentType.regionMatches(true, 0, "application/json", 0, 16) ? Format.JSON : Format.BINARY);
        String jsonVersionStr = httpRequest.getProperty("X-Mig33-JSON-Version");
        if (!StringUtil.isBlank(jsonVersionStr)) {
            try {
                this.jsonVersion = Integer.parseInt(jsonVersionStr);
            }
            catch (NumberFormatException nfe) {
                // empty catch block
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Format is " + this.format.toString()));
        }
        return httpRequest;
    }

    private void processHTTPRequest(HTTPRequest httpRequest) throws Exception {
        byte[] content = httpRequest.getContent();
        FusionRequest request = null;
        this.updateRemoteAddress(httpRequest);
        if (content == null) {
            request = SystemPropertyEntities.Temp.Cache.fi83Week2UseAutogeneratedPacketsEnabled.getValue().booleanValue() ? new FusionPktHttpPoll() : new FusionPktHTTPPollOld();
        } else if (this.format == Format.BINARY) {
            request = FusionRequest.parseRequest(content);
        } else if (this.format == Format.XML) {
            request = FusionRequest.parseXMLRequest(new String(content, "UTF-8"));
        } else if (this.format == Format.JSON) {
            request = FusionRequest.parseJSONRequest(new String(content, "UTF-8"), this.jsonVersion);
        }
        if (request == null) {
            throw new Exception("Failed to convert HTTP packet into a Fusion request");
        }
        request.setConnection(this);
        boolean requestQueued = false;
        ConnectionHTTP localConnection = null;
        ConnectionPrx remoteConnection = null;
        String sessionId = httpRequest.getSessionId();
        String encryptedSessionId = null;
        if (request.getType() == 211) {
            FusionRequest createSessionPacket;
            if (SystemPropertyEntities.Temp.Cache.fi83Week2UseAutogeneratedPacketsEnabled.getValue().booleanValue()) {
                createSessionPacket = (FusionPktCreateSession)FusionPacketFactory.getSpecificRequest(request);
                log.info((Object)String.format("Getting sessionId from packet for CREATE_SESSION '%s' instead of from cookie '%s", ((FusionPktDataCreateSession)createSessionPacket).getSessionId(), sessionId));
                sessionId = ((FusionPktDataCreateSession)createSessionPacket).getSessionId();
            } else {
                createSessionPacket = (FusionPktCreateSessionOld)FusionPacketFactory.getSpecificRequest(request);
                log.info((Object)String.format("Getting sessionId from packet for CREATE_SESSION '%s' instead of from cookie '%s", ((FusionPktCreateSessionOld)createSessionPacket).getSessionID(), sessionId));
                sessionId = ((FusionPktCreateSessionOld)createSessionPacket).getSessionID();
            }
        }
        if (!StringUtil.isBlank(sessionId)) {
            if (SSOLogin.isEncryptedSessionID(sessionId) && StringUtil.isBlank(sessionId = SSOLogin.getSessionIDFromEncryptedSessionID(encryptedSessionId = sessionId))) {
                if (SystemProperty.getBool(SystemPropertyEntities.GatewaySettings.LOG_EID_DECRYPTION_FAILED_REMOTE_ADDRESS_ENABLED)) {
                    log.error((Object)("Decryption failure of encrypted session id " + this.encryptedSessionID + " was from remote address=" + this.getRemoteAddress()));
                }
                throw new Exception("Invalid session ID in HTTP request packet");
            }
            if ((request.sessionRequired() || request.getType() == 211) && (localConnection = (ConnectionHTTP)this.gateway.findConnection(sessionId)) == null) {
                RegistryPrx registryPrx = this.findRegistry();
                if (registryPrx == null) {
                    throw new Exception("Failed to locate registry");
                }
                try {
                    remoteConnection = registryPrx.findConnectionObject(sessionId);
                }
                catch (ObjectNotFoundException e) {
                    // empty catch block
                }
            }
        }
        if (!request.sessionRequired() && localConnection == null && remoteConnection == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Processing request");
            }
            this.accessed();
            requestQueued = this.packetProcessor.queueFusionRequest(request);
        } else {
            if (sessionId == null || sessionId.length() == 0) {
                throw new Exception("No session ID in HTTP request packet");
            }
            if (localConnection != null) {
                if (SystemProperty.getBool("EnforceSessionOrigin", false)) {
                    this.enforceSessionOrigin(sessionId, localConnection.remoteAddress, this.remoteAddress, "local");
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Processing request on local server");
                }
                if (request.getType() == 13 && localConnection.packetProcessor.getQueueSize() > 0) {
                    requestQueued = false;
                } else {
                    localConnection.accessed();
                    requestQueued = localConnection.packetProcessor.queueFusionRequest(request);
                }
            } else if (remoteConnection != null) {
                if (SystemProperty.getBool("EnforceSessionOrigin", false)) {
                    this.enforceSessionOrigin(sessionId, remoteConnection.getRemoteIPAddress(), this.remoteAddress, "registry");
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Processing request on remote server");
                }
                requestQueued = remoteConnection.processPacket(this.addConnectionToIceAdaptor(false), content);
            } else {
                log.warn((Object)String.format("Unable to find ConnectionHTTP object with session id %s request-type=%d", sessionId, request.getType()));
                throw new Exception("Could not find conenction object");
            }
        }
        if (!requestQueued) {
            this.writeHTTPResponse(new HTTPResponse().toString(), true);
        }
    }

    private void updateRemoteAddress(HTTPRequest httpRequest) {
        String[] ipPortPair;
        String forwardedIp = httpRequest.getProperty(HEADER_X_FORWARDED_FOR);
        if (StringUtil.isBlank(forwardedIp)) {
            return;
        }
        String[] forwardedIps = forwardedIp.split(",");
        String xffIp = forwardedIps[forwardedIps.length - 1].trim();
        if (this.remoteAddress.equals(xffIp)) {
            return;
        }
        if (xffIp.contains(":") && (ipPortPair = xffIp.split(":")).length == 2) {
            xffIp = ipPortPair[0];
        }
        if (this.remoteAddress.equals(xffIp)) {
            return;
        }
        this.remoteAddress = xffIp;
        log.info((Object)("RemoteAddress:" + this.remoteAddress + " assigned from HTTP XFF header:" + forwardedIp + " with socket IP:" + this.channel.socket().getInetAddress().getHostAddress()));
        if (xffIp.startsWith("10.") || xffIp.startsWith("172.16.") || xffIp.startsWith("192.168.")) {
            log.info((Object)("RemoteAddress ending with local IP:" + forwardedIp));
        }
    }

    private void enforceSessionOrigin(String sessionId, String connectionRemoteAddress, String requestRemoteAddress, String connectionSource) throws Exception {
        if (!StringUtil.isBlank(requestRemoteAddress) && !StringUtil.isBlank(connectionRemoteAddress)) {
            String requestNetworkAddress;
            int lastindex_connectionRemoteAddress = connectionRemoteAddress.lastIndexOf(46);
            int lastindex_requestRemoteAddress = requestRemoteAddress.lastIndexOf(46);
            if (lastindex_connectionRemoteAddress < 0 || lastindex_requestRemoteAddress < 0) {
                log.warn((Object)("Unable to determine network address for session[" + sessionId + "] source[" + connectionSource + "] connectionRemoteAddress[" + connectionRemoteAddress + "] requestRemoteAddress[" + requestRemoteAddress + "]"));
                return;
            }
            String connectionNetworkAddress = connectionRemoteAddress.substring(0, lastindex_connectionRemoteAddress);
            if (!connectionNetworkAddress.equals(requestNetworkAddress = requestRemoteAddress.substring(0, lastindex_requestRemoteAddress))) {
                log.info((Object)("Session was created from [" + connectionRemoteAddress + "] but was accessed from [" + requestRemoteAddress + "]"));
                throw new Exception("Unable to process request right now. Please try again later");
            }
        } else {
            log.warn((Object)("Not able to enforce session origin for session[" + sessionId + "], source[" + connectionSource + "] connectionRemoteAddress[" + connectionRemoteAddress + "] requestRemoteAddress[" + requestRemoteAddress + "]"));
        }
    }

    private void writeHTTPResponse(String response, boolean executeNow) {
        block6: {
            try {
                this.writeBuffer = ByteBuffer.wrap(response.getBytes("8859_1"));
                if (executeNow) {
                    this.onChannelWritable();
                } else {
                    this.gateway.registerConnection(this, 4);
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Sending response to port " + this.getRemotePort() + "\n" + response + "\n"));
                }
            }
            catch (UnsupportedEncodingException e) {
                log.error((Object)"Unable to convert HTTP response into byte stream", (Throwable)e);
                this.disconnect();
            }
            catch (FusionException fe) {
                if (!SystemPropertyEntities.Temp.Cache.se493WebSocketsEnabled.getValue().booleanValue()) break block6;
                log.error((Object)"Trying to call method from unsuppported gateway type", (Throwable)((Object)fe));
                this.disconnect();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<FusionPacket> poll() {
        ArrayList<FusionPacket> packets = new ArrayList<FusionPacket>();
        if (this.outgoingQueue != null) {
            Queue<FusionPacket> queue = this.outgoingQueue;
            synchronized (queue) {
                packets.addAll(this.outgoingQueue);
                this.outgoingQueue.clear();
            }
        }
        return packets;
    }

    @Override
    public boolean isOnLoginCalled() {
        return this.isPermanent;
    }

    @Override
    public ConnectionI onLogin(FusionPktLogin loginPacket) throws Exception {
        if (this.format == Format.BINARY) {
            super.onLogin(loginPacket);
            ConnectionPrx connectionPrx = this.addConnectionToIceAdaptor(true);
            if (connectionPrx == null) {
                throw new Exception("Unable to add Connection to Ice adaptor");
            }
            return this;
        }
        ConnectionHTTP connection = new ConnectionHTTP(this);
        connection.isPermanent = true;
        connection.setLoginPacket(loginPacket);
        connection.loginChallenge = connection.newChallengeString();
        connection.sessionID = loginPacket.getSessionId() == null ? ConnectionI.newSessionID() : loginPacket.getSessionId();
        this.gateway.onConnectionAccessed(connection);
        ConnectionPrx connectionPrx = connection.addConnectionToIceAdaptor(true);
        if (connectionPrx == null) {
            throw new Exception("Unable to add Connection to Ice adaptor");
        }
        return connection;
    }

    @Override
    @Deprecated
    public ConnectionI onLogin(FusionPktLoginOld loginPacket) throws Exception {
        if (this.format == Format.BINARY) {
            super.onLogin(loginPacket);
            ConnectionPrx connectionPrx = this.addConnectionToIceAdaptor(true);
            if (connectionPrx == null) {
                throw new Exception("Unable to add Connection to Ice adaptor");
            }
            return this;
        }
        ConnectionHTTP connection = new ConnectionHTTP(this);
        connection.isPermanent = true;
        connection.setLoginPacketOld(loginPacket);
        connection.loginChallenge = connection.newChallengeString();
        connection.sessionID = loginPacket.getSessionId() == null ? ConnectionI.newSessionID() : loginPacket.getSessionId();
        this.gateway.onConnectionAccessed(connection);
        ConnectionPrx connectionPrx = connection.addConnectionToIceAdaptor(true);
        if (connectionPrx == null) {
            throw new Exception("Unable to add Connection to Ice adaptor");
        }
        return connection;
    }

    @Override
    public void onCreatingSession() {
        super.onCreatingSession();
        this.outgoingQueue = new LinkedList<FusionPacket>();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendFusionPacket(FusionPacket packet) throws FusionException {
        Queue<FusionPacket> queue = this.outgoingQueue;
        synchronized (queue) {
            this.outgoingQueue.add(packet);
        }
    }

    @Override
    public boolean processPacket(ConnectionPrx requestingConnection, byte[] packet, Current __current) throws FusionException {
        try {
            FusionRequest request = null;
            if (packet == null || packet.length == 0) {
                if (this.packetProcessor.getQueueSize() > 0) {
                    return false;
                }
                request = SystemPropertyEntities.Temp.Cache.fi83Week2UseAutogeneratedPacketsEnabled.getValue().booleanValue() ? new FusionPktHttpPoll() : new FusionPktHTTPPollOld();
            } else if (this.format == Format.BINARY) {
                request = FusionRequest.parseRequest(packet);
            } else if (this.format == Format.XML) {
                request = FusionRequest.parseXMLRequest(new String(packet, "UTF-8"));
            } else if (this.format == Format.JSON) {
                request = FusionRequest.parseJSONRequest(new String(packet, "UTF-8"), this.jsonVersion);
            }
            if (request == null) {
                throw new Exception("Failed to parse byte stream into a Fusion request");
            }
            request.setConnectionPrx(requestingConnection);
            this.accessed();
            return this.packetProcessor.queueFusionRequest(request);
        }
        catch (Exception e) {
            throw new FusionException(e.getMessage());
        }
    }

    @Override
    public void packetProcessed(byte[] result, Current __current) {
        HTTPResponse response;
        if (this.connectionPrx != null && !this.registeredWithRegistry) {
            this.removeConnectionFromIceAdaptor();
        }
        if (this.format == Format.BINARY) {
            response = new HTTPResponse("image/gif", result);
        } else if (this.format == Format.XML) {
            response = new HTTPResponse("text/xml", result);
        } else if (this.format == Format.JSON) {
            response = new HTTPResponse("application/json", result);
        } else {
            log.error((Object)("packetProcessed(): Unknown format: " + (Object)((Object)this.format)));
            return;
        }
        this.writeHTTPResponse(response.toString(), false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onFusionRequestProcessed(FusionRequest request, FusionPacket[] responses) {
        try {
            int chunkSize;
            List<FusionPacket> packetList = new ArrayList<FusionPacket>();
            if (responses != null) {
                packetList.addAll(Arrays.asList(responses));
            }
            packetList.addAll(this.poll());
            if (this.format == Format.JSON && (chunkSize = SystemProperty.getInt("HTTPChunkSize", 0)) > 0 && packetList.size() > chunkSize) {
                Queue<FusionPacket> queue = this.outgoingQueue;
                synchronized (queue) {
                    List<FusionPacket> tmp = this.poll();
                    this.outgoingQueue.addAll(packetList.subList(chunkSize, packetList.size()));
                    this.outgoingQueue.addAll(tmp);
                }
                packetList = packetList.subList(0, chunkSize);
            }
            FusionPacket[] packetArray = packetList.toArray(new FusionPacket[packetList.size()]);
            byte[] result = null;
            if (this.format == Format.BINARY) {
                result = FusionPacket.toByteArray(packetArray);
            } else if (this.format == Format.XML) {
                result = FusionPacket.toXML(packetArray).getBytes("UTF-8");
            } else if (this.format == Format.JSON) {
                result = FusionPacket.toJSON(packetArray, this.jsonVersion).getBytes("UTF-8");
            } else {
                log.error((Object)("onFusionRequestProcessed(): Unknown format...: " + (Object)((Object)this.format)));
                return;
            }
            if (request.getConnectionPrx() != null) {
                request.getConnectionPrx().packetProcessed(result);
            } else {
                request.getConnection().packetProcessed(result);
            }
        }
        catch (UnsupportedEncodingException e) {
            log.error((Object)"Unable to convert Fusion packet(s) into byte stream", (Throwable)e);
            this.writeHTTPResponse(HTTP_ERROR, true);
            this.disconnect();
        }
        catch (JSONException e) {
            log.error((Object)"Unable to convert Fusion packet(s) into JSON", (Throwable)e);
            this.writeHTTPResponse(HTTP_ERROR, true);
            this.disconnect();
        }
    }

    @Override
    public void onFusionRequestDiscarded(FusionRequest request) {
        if (request.getConnectionPrx() != null) {
            request.getConnectionPrx().packetProcessed(new byte[0]);
        } else {
            request.getConnection().packetProcessed(new byte[0]);
        }
    }

    @Override
    public void onFusionRequestException(FusionRequest request, Exception e) {
        if (request.getConnectionPrx() != null) {
            request.getConnectionPrx().packetProcessed(new byte[0]);
        } else {
            request.getConnection().packetProcessed(new byte[0]);
        }
    }

    @Override
    public void silentlyDropIncomingPackets(Current __current) {
        log.warn((Object)("No longer processing incoming packets for this connection for user [" + this.username + "] session id [" + this.sessionID + "] and remote address [" + this.remoteAddress + "]"));
        this.packetProcessor.stopProcessing();
    }

    @Override
    public void onDeviceModeChanged(DeviceModeType mode) {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum Format {
        BINARY,
        XML,
        JSON;

    }
}

