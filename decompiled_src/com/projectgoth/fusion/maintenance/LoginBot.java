/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.maintenance;

import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.fdl.enums.ServiceType;
import com.projectgoth.fusion.gateway.HTTPRequest;
import com.projectgoth.fusion.gateway.HTTPResponse;
import com.projectgoth.fusion.gateway.packet.FusionPktLogin;
import com.projectgoth.fusion.gateway.packet.FusionPktLoginResponse;
import com.projectgoth.fusion.gateway.packet.FusionPktLogout;
import com.projectgoth.fusion.gateway.packet.FusionPktMessage;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class LoginBot {
    private Socket socket;
    private String server;
    private int port;
    private boolean isTCP;
    private String username;
    private String password;
    private String sessionId;
    private short transactionId;

    public LoginBot(String server, int port, boolean isTCP, String username, String password) {
        this.username = username;
        this.password = password;
        this.server = server;
        this.port = port;
        this.isTCP = isTCP;
    }

    public String getUsername() {
        return this.username;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    protected synchronized FusionPacket sendFusionPkt(String sessionId, FusionPacket pkt) throws IOException {
        return this.sendFusionPkt(sessionId, pkt, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected synchronized FusionPacket sendFusionPkt(String sessionId, FusionPacket pkt, boolean getReply) throws IOException {
        block21: {
            block20: {
                block19: {
                    block18: {
                        block17: {
                            try {
                                if (this.socket == null || !this.isTCP) {
                                    this.socket = new Socket();
                                    this.socket.connect(new InetSocketAddress(this.server, this.port), 20000);
                                    this.socket.setSoTimeout(30000);
                                }
                                if ((this.transactionId = (short)(this.transactionId + 1)) == 32767) {
                                    this.transactionId = 1;
                                }
                                pkt.setTransactionId(this.transactionId);
                                if (this.isTCP) {
                                    pkt.write(this.socket.getOutputStream());
                                    if (!getReply) {
                                        var4_4 = null;
                                        var11_7 = null;
                                        if (this.isTCP != false) return var4_4;
                                        break block17;
                                    }
                                    reply = new FusionPacket();
                                    do {
                                        reply.read(this.socket.getInputStream());
                                    } while (reply.getTransactionId() != pkt.getTransactionId());
                                    var5_19 = reply;
                                    break block18;
                                }
                                r = new HTTPRequest(HTTPRequest.RequestMethod.POST, sessionId, "image/gif", pkt.toByteArray());
                                r.setProperty("Transfer-encoding", "binary");
                                r.write(this.socket.getOutputStream());
                                x = new HTTPResponse();
                                x.read(this.socket.getInputStream());
                                ba = x.getContent();
                                if (ba == null) {
                                    var7_22 = null;
                                    break block19;
                                }
                                packets = FusionPacket.parse(ba);
                                if (packets != null) {
                                    for (i = 0; i < packets.length; ++i) {
                                        if (packets[i].getTransactionId() != pkt.getTransactionId()) continue;
                                        var9_26 = packets[i];
                                        break block20;
                                    }
                                }
                                var8_25 = null;
                                break block21;
                            }
                            catch (Throwable var10_27) {
                                var11_12 = null;
                                if (this.isTCP != false) throw var10_27;
                                try {
                                    this.socket.close();
                                    throw var10_27;
                                }
                                catch (Exception e) {
                                    throw var10_27;
                                }
                            }
                        }
                        ** try [egrp 1[TRYBLOCK] [6 : 330->340)] { 
lbl55:
                        // 1 sources

                        this.socket.close();
                        return var4_4;
lbl57:
                        // 1 sources

                        catch (Exception e) {
                            // empty catch block
                        }
                        return var4_4;
                    }
                    var11_8 = null;
                    if (this.isTCP != false) return var5_19;
                    ** try [egrp 1[TRYBLOCK] [6 : 330->340)] { 
lbl64:
                    // 1 sources

                    this.socket.close();
                    return var5_19;
lbl66:
                    // 1 sources

                    catch (Exception e) {
                        // empty catch block
                    }
                    return var5_19;
                }
                var11_9 = null;
                if (this.isTCP != false) return var7_22;
                ** try [egrp 1[TRYBLOCK] [6 : 330->340)] { 
lbl73:
                // 1 sources

                this.socket.close();
                return var7_22;
lbl75:
                // 1 sources

                catch (Exception e) {
                    // empty catch block
                }
                return var7_22;
            }
            var11_10 = null;
            if (this.isTCP != false) return var9_26;
            ** try [egrp 1[TRYBLOCK] [6 : 330->340)] { 
lbl82:
            // 1 sources

            this.socket.close();
            return var9_26;
lbl84:
            // 1 sources

            catch (Exception e) {
                // empty catch block
            }
            return var9_26;
        }
        var11_11 = null;
        if (this.isTCP != false) return var8_25;
        ** try [egrp 1[TRYBLOCK] [6 : 330->340)] { 
lbl91:
        // 1 sources

        this.socket.close();
        return var8_25;
lbl93:
        // 1 sources

        catch (Exception e) {
            // empty catch block
        }
        return var8_25;
    }

    public void login() throws IOException {
        String session;
        FusionPktLogin login = new FusionPktLogin();
        login.setProtocolVersion((short)1);
        login.setClientVersion((short)300);
        login.setServiceType(ServiceType.X_TXT);
        login.setUsername(this.username);
        login.setEmailAddress("");
        login.setUserAgent("");
        login.setInitialPresence(PresenceType.AVAILABLE);
        login.setClientType(ClientType.MIDP1);
        FusionPacket reply = this.sendFusionPkt("", login);
        if (reply.getType() == 201) {
            String challenge = reply.getStringField((short)1);
            session = reply.getStringField((short)2);
            FusionPktLoginResponse loginResp = new FusionPktLoginResponse();
            loginResp.setPasswordHash((challenge + this.password).hashCode());
            reply = this.sendFusionPkt(session, loginResp);
            if (reply.getType() != 203) {
                throw new IOException(reply.getStringField((short)2));
            }
        } else {
            throw new IOException(reply.getStringField((short)2));
        }
        this.sessionId = session;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public void logout() {
        try {
            try {
                this.sendFusionPkt(this.sessionId, new FusionPktLogout());
            }
            catch (Exception exception) {
                Object var3_2 = null;
                this.sessionId = null;
                try {}
                catch (Throwable throwable) {
                    Object var6_9 = null;
                    this.socket = null;
                    throw throwable;
                }
                try {}
                catch (Exception ie) {
                    Object var6_8 = null;
                    this.socket = null;
                    return;
                }
                this.socket.close();
                Object var6_7 = null;
                this.socket = null;
                return;
            }
            Object var3_1 = null;
            this.sessionId = null;
            try {
                try {
                    this.socket.close();
                }
                catch (Exception ie) {
                    Object var6_5 = null;
                    this.socket = null;
                    return;
                }
                Object var6_4 = null;
                this.socket = null;
                return;
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                this.socket = null;
                throw throwable;
            }
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            this.sessionId = null;
            try {}
            catch (Throwable throwable2) {
                Object var6_12 = null;
                this.socket = null;
                throw throwable2;
            }
            try {}
            catch (Exception ie) {
                Object var6_11 = null;
                this.socket = null;
                throw throwable;
            }
            this.socket.close();
            Object var6_10 = null;
            this.socket = null;
            throw throwable;
        }
    }

    public boolean isOnline() {
        if (this.isTCP && (this.socket == null || !this.socket.isConnected())) {
            return false;
        }
        return this.sessionId != null && this.sessionId.length() > 0;
    }

    public void sendMessage(String destination, String message) throws IOException {
        FusionPktMessage msg = new FusionPktMessage();
        msg.setMessageType((byte)1);
        msg.setSource(this.username);
        msg.setDestinationType((byte)1);
        msg.setDestination(destination);
        msg.setContentType((short)1);
        msg.setContentAsString(message);
        FusionPacket reply = this.sendFusionPkt(this.sessionId, msg);
        if (reply.getType() != 1) {
            throw new IOException(reply.getStringField((short)2));
        }
    }

    public static void main(String[] args) {
        if (args.length < 7) {
            System.out.println("Usage: LoginBot host gateway port tcp username password interval");
            return;
        }
        String host = args[0];
        boolean isTCP = Integer.parseInt(args[3]) == 1;
        long interval = Long.parseLong(args[6]);
        int port = Integer.parseInt(args[2]);
        LoginBot bot = new LoginBot(args[1], port, isTCP, args[4], args[5]);
        while (true) {
            try {
                bot.login();
                bot.logout();
                System.out.println(host + "\tGateway " + port + "\t0\tOK");
            }
            catch (Exception e) {
                System.out.println(host + "\tGateway " + port + "\t2\tCRITICAL - " + e.getMessage());
            }
            if (interval <= 0L) break;
            try {
                Thread.sleep(interval);
            }
            catch (Exception e) {}
        }
    }
}

