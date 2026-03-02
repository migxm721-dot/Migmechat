/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.smsengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.SMSGatewayData;
import com.projectgoth.fusion.data.SystemSMSData;
import com.projectgoth.fusion.smsengine.DispatchStatus;
import com.projectgoth.fusion.smsengine.GenericSourceNumberPool;
import com.projectgoth.fusion.smsengine.MOSMSProcessor;
import com.projectgoth.fusion.smsengine.SMPPBody;
import com.projectgoth.fusion.smsengine.SMPPID;
import com.projectgoth.fusion.smsengine.SMPPPacket;
import com.projectgoth.fusion.smsengine.SMPPStatus;
import com.projectgoth.fusion.smsengine.SMSGateway;
import com.projectgoth.fusion.smsengine.SMSMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SMPPGateway
extends SMSGateway
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SMPPGateway.class));
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int SEND_TIMEOUT = 5000;
    private static final int RECONNECTION_INTERVAL = 30000;
    private static final int HEART_BEAT_INTERVAL = 30000;
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static MOSMSProcessor moSMSProcessor = new MOSMSProcessor();
    private Socket socket;
    private int sequence;
    private volatile boolean ready;
    private volatile boolean stop;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private Map<Integer, SMPPPacket> packetLocker = new ConcurrentHashMap<Integer, SMPPPacket>();

    public SMPPGateway(SMSGatewayData gatewayData) {
        super(gatewayData);
        this.scheduler.execute(this);
        this.scheduler.scheduleWithFixedDelay(new HeartBeat(), 10000L, 30000L, TimeUnit.MILLISECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public void run() {
        while (!this.stop) {
            this.connect();
            this.bind();
            this.ready = true;
            while (!this.stop) {
                this.onSMPPPacket(this.readSMPPPacket());
            }
            Object var3_2 = null;
            if (this.ready) {
                this.ready = false;
                this.unbind();
            }
            this.disconnect();
            try {
                Thread.sleep(30000L);
            }
            catch (Exception e2) {}
            continue;
            {
                catch (Exception e) {
                    log.warn((Object)("Exception occured in SMPP gateway " + this.getID()), (Throwable)e);
                    var3_2 = null;
                    if (this.ready) {
                        this.ready = false;
                        this.unbind();
                    }
                    this.disconnect();
                    try {
                        Thread.sleep(30000L);
                    }
                    catch (Exception e2) {}
                    continue;
                }
            }
            catch (Throwable throwable) {
                var3_2 = null;
                if (this.ready) {
                    this.ready = false;
                    this.unbind();
                }
                this.disconnect();
                try {
                    Thread.sleep(30000L);
                }
                catch (Exception e2) {
                    // empty catch block
                }
                throw throwable;
            }
        }
    }

    private void connect() throws IOException {
        log.debug((Object)("Connecting to SMPP gateway " + this.getID() + " at " + this.gatewayData.url + ":" + this.gatewayData.port));
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(this.gatewayData.url, (int)this.gatewayData.port), 10000);
        this.packetLocker.clear();
    }

    private void disconnect() {
        log.debug((Object)("Disconnecting SMPP gateway " + this.getID()));
        try {
            this.socket.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void bind() throws IOException {
        SMPPPacket pktBind;
        if (this.gatewayData.type == SMSGatewayData.TypeEnum.SMPP_TRANSMITTER) {
            log.debug((Object)("Binding as transmitter on SMPP gateway " + this.getID()));
            pktBind = new SMPPPacket(SMPPID.BIND_TRANSMITTER);
        } else {
            log.debug((Object)("Binding as transceiver on SMPP gateway " + this.getID()));
            pktBind = new SMPPPacket(SMPPID.BIND_TRANSCEIVER);
        }
        pktBind.put(this.gatewayData.usernameParam).put(this.gatewayData.passwordParam).put("").put(SMPPPacket.INTERFACE_VERSION).put((byte)0).put((byte)0).put("");
        this.sendSMPPPacket(pktBind, false);
        SMPPPacket pktResponse = this.readSMPPPacket();
        if (pktBind.getId() == SMPPID.BIND_TRANSMITTER && pktResponse.getId() != SMPPID.BIND_TRANSMITTER_RESP) {
            throw new IOException("Unexpected response command ID " + (Object)((Object)pktResponse.getId()) + " while binding transmitter");
        }
        if (pktBind.getId() == SMPPID.BIND_TRANSCEIVER && pktResponse.getId() != SMPPID.BIND_TRANSCEIVER_RESP) {
            throw new IOException("Unexpected response command ID " + (Object)((Object)pktResponse.getId()) + " while binding transceiver");
        }
        if (pktResponse.getStatus() != SMPPStatus.ESME_ROK) {
            throw new IOException((Object)((Object)pktBind.getId()) + " failed. SMPP status " + (Object)((Object)pktResponse.getStatus()) + ": " + pktResponse.getStatus().description());
        }
    }

    private void unbind() {
        log.debug((Object)("Unbinding on SMPP gateway " + this.getID()));
        try {
            this.sendSMPPPacket(new SMPPPacket(SMPPID.UNBIND), false);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private SMPPPacket readSMPPPacket() throws IOException {
        SMPPPacket packet = new SMPPPacket(this.socket.getInputStream());
        log.debug((Object)("Gateway " + this.getID() + " Received\t" + packet));
        return packet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SMPPPacket sendSMPPPacket(SMPPPacket packet, boolean waitForResponse) throws IOException {
        SMPPPacket sMPPPacket = packet;
        synchronized (sMPPPacket) {
            OutputStream out;
            OutputStream outputStream = out = this.socket.getOutputStream();
            synchronized (outputStream) {
                if (packet.getSequence() == 0) {
                    packet.setSequence(++this.sequence);
                }
                this.socket.getOutputStream().write(packet.toByteArray());
            }
            log.debug((Object)("Gateway " + this.getID() + " Sent\t\t" + packet));
            if (!waitForResponse) {
                return null;
            }
            this.packetLocker.put(packet.getSequence(), packet);
            boolean interrupted = false;
            try {
                packet.wait(5000L);
            }
            catch (InterruptedException e) {
                interrupted = true;
            }
            SMPPPacket pktResponse = this.packetLocker.remove(packet.getSequence());
            if (pktResponse == packet) {
                if (interrupted) {
                    throw new IOException("Thread interrupted while waiting for response sequence number " + packet.getSequence());
                }
                throw new IOException("Timeout while while waiting for response sequence number " + packet.getSequence());
            }
            return pktResponse;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onSMPPPacket(SMPPPacket packet) {
        SMPPPacket originalPacket;
        SMPPID id = packet.getId();
        if (id.isResponse() && (originalPacket = this.packetLocker.get(packet.getSequence())) != null) {
            SMPPPacket sMPPPacket = originalPacket;
            synchronized (sMPPPacket) {
                this.packetLocker.put(packet.getSequence(), packet);
                originalPacket.notifyAll();
            }
        }
        switch (id) {
            case ENQUIRE_LINK: {
                this.onEnquireLink(packet);
                break;
            }
            case DELIVER_SM: {
                this.onDeliverSM(packet);
                break;
            }
        }
    }

    private void onEnquireLink(SMPPPacket packet) {
        SMPPPacket responsePacket = new SMPPPacket(SMPPID.ENQUIRE_LINK_RESP);
        responsePacket.setSequence(packet.getSequence());
        try {
            this.sendSMPPPacket(responsePacket, false);
        }
        catch (Exception e) {
            log.warn((Object)"Unable to acknowledge ENQUIRE_LINK", (Throwable)e);
        }
    }

    private void onDeliverSM(SMPPPacket packet) {
        try {
            SMPPBody body = packet.getBody();
            body.getCString();
            body.getByteArray(2);
            String source = body.getCString();
            body.getByteArray(2);
            String destination = body.getCString();
            body.getByteArray(9);
            int messageLen = body.getIntFromUnsignedByte();
            String message = body.getString(messageLen);
            log.info((Object)("DELIVER_SM gateway = " + this.getID() + ", source = " + source + ", destination = " + destination + ". " + message));
            moSMSProcessor.process(this, packet.getSequence(), source, destination, message);
        }
        catch (Exception e) {
            log.warn((Object)"Expception occured while prossing DELIVER_SM", (Throwable)e);
        }
    }

    public DispatchStatus dispatchMessage(SMSMessage message) {
        DispatchStatus dispatchStatus = new DispatchStatus(DispatchStatus.StatusEnum.TRY_OTHER_GATEWAYS, message);
        try {
            if (!this.ready) {
                return dispatchStatus;
            }
            String destination = message.getDestination();
            byte sourceTON = 0;
            byte sourceNPI = 0;
            byte destinationTON = 0;
            byte destinationNPI = 0;
            byte registeredDelivery = 0;
            if (this.gatewayData.name.toLowerCase().equals("smart telecom")) {
                sourceTON = 1;
                sourceNPI = 1;
                destinationTON = 49;
                destinationNPI = 2;
                registeredDelivery = 1;
                if (message.useSourceAsSourceAddress() && message.getIDDCode() != 91) {
                    return dispatchStatus;
                }
                dispatchStatus.source = GenericSourceNumberPool.getSourceNumber("smarttelcom");
                if (this.gatewayData.iddPrefix != null) {
                    destination = this.gatewayData.iddPrefix + destination;
                    log.debug((Object)("SMPP gateway " + this.gatewayData.id + " modified destination from " + message.getDestination() + " to " + destination));
                }
            }
            if (this.gatewayData.name.toLowerCase().contains("iris")) {
                if (message.useSourceAsSourceAddress()) {
                    return dispatchStatus;
                }
                dispatchStatus.source = GenericSourceNumberPool.getSourceNumber("iris");
                if (this.gatewayData.iddPrefix != null && !destination.startsWith("1")) {
                    destination = this.gatewayData.iddPrefix + destination;
                    log.debug((Object)("SMPP gateway " + this.gatewayData.id + " modified destination from " + message.getDestination() + " to " + destination));
                }
            } else if (this.gatewayData.name.toLowerCase().contains("banglalink")) {
                if (message.useSourceAsSourceAddress()) {
                    return dispatchStatus;
                }
                dispatchStatus.source = "7575";
                destination = destination.substring(2);
            }
            byte[] bytesToSend = message.getMessageText().getBytes(DEFAULT_CHARSET);
            SMPPPacket pktSubmit = new SMPPPacket(SMPPID.SUBMIT_SM);
            pktSubmit.put("").put(sourceTON).put(sourceNPI).put(dispatchStatus.source).put(destinationTON).put(destinationNPI).put(destination).put((byte)0).put((byte)0).put((byte)0).put("").put("").put(registeredDelivery).put((byte)0).put((byte)0).put((byte)0).put((byte)bytesToSend.length).put(bytesToSend);
            SMPPPacket pktResponse = this.sendSMPPPacket(pktSubmit, true);
            if (pktResponse.getId() != SMPPID.SUBMIT_SM_RESP) {
                dispatchStatus.status = DispatchStatus.StatusEnum.FAILED;
                dispatchStatus.failedReason = "Unexpected response command ID " + (Object)((Object)pktResponse.getId()) + " while sending message";
                log.warn((Object)("Gateway " + this.gatewayData.id + ". Failed to dispatch " + message.getShortDescription() + " - " + dispatchStatus.failedReason));
            } else if (pktResponse.getStatus() == SMPPStatus.ESME_RINVSRCADR) {
                log.warn((Object)("Gateway " + this.gatewayData.id + ". Failed to dispatch " + message.getShortDescription() + " - Invalid source address " + dispatchStatus.source));
            } else if (pktResponse.getStatus() != SMPPStatus.ESME_ROK) {
                dispatchStatus.status = DispatchStatus.StatusEnum.FAILED;
                dispatchStatus.failedReason = pktResponse.getStatus().description();
                log.warn((Object)("Gateway " + this.gatewayData.id + ". Failed to dispatch " + message.getShortDescription() + " - " + dispatchStatus.failedReason));
            } else {
                SMPPBody body = pktResponse.getBody();
                String transactionID = body.getCString();
                dispatchStatus.status = DispatchStatus.StatusEnum.SUCCEEDED;
                dispatchStatus.transactionID = transactionID;
                dispatchStatus.billed = this.gatewayData.deliveryReporting == null || this.gatewayData.deliveryReporting == false;
                log.info((Object)("Gateway " + this.gatewayData.id + ". Successfully dispatched " + message.getShortDescription() + ". Transaction ID = " + dispatchStatus.transactionID));
            }
        }
        catch (UnsupportedEncodingException e) {
            dispatchStatus.status = DispatchStatus.StatusEnum.FAILED;
            dispatchStatus.failedReason = "Unsupported encoding character set UTF-8";
        }
        catch (IOException e) {
            log.warn((Object)("IOException occured in SMPP gateway " + this.getID()), (Throwable)e);
        }
        catch (Exception e) {
            log.warn((Object)("Unexpected exception occured in SMPP gateway " + this.getID()), (Throwable)e);
            dispatchStatus.status = DispatchStatus.StatusEnum.FAILED;
            dispatchStatus.failedReason = e.getMessage();
        }
        return dispatchStatus;
    }

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("SMPPGateway Type Name Server Port Username Password Source Destination Message");
                return;
            }
            log.setLevel(Level.DEBUG);
            SMSGatewayData gatewayData = new SMSGatewayData();
            gatewayData.id = 1;
            gatewayData.type = "1".equals(args[0]) ? SMSGatewayData.TypeEnum.SMPP_TRANSMITTER : SMSGatewayData.TypeEnum.SMPP_TRANSCEIVER;
            gatewayData.name = args[1];
            gatewayData.url = args[2];
            gatewayData.port = Integer.valueOf(args[3]);
            gatewayData.iddPrefix = null;
            gatewayData.usernameParam = args[4];
            gatewayData.passwordParam = args[5];
            SMPPGateway gateway = new SMPPGateway(gatewayData);
            Thread.sleep(5000L);
            for (String destination : args[7].split(";")) {
                SystemSMSData systemSMSData = new SystemSMSData();
                systemSMSData.id = 1;
                systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
                systemSMSData.subType = SystemSMSData.SubTypeEnum.BUZZ;
                systemSMSData.username = "koko";
                systemSMSData.source = args[6];
                systemSMSData.destination = destination;
                systemSMSData.IDDCode = 1;
                systemSMSData.messageText = args[8];
                List<SMSMessage> messages = SMSMessage.parse(systemSMSData);
                for (SMSMessage message : messages) {
                    DispatchStatus status = ((SMSGateway)gateway).dispatchMessage(message);
                    if (status.status == DispatchStatus.StatusEnum.SUCCEEDED) {
                        log.info((Object)((Object)((Object)status.status) + ": " + status.transactionID + ", source = " + status.source));
                        continue;
                    }
                    log.info((Object)((Object)((Object)status.status) + ": " + status.failedReason + ", source = " + status.source));
                }
                Thread.sleep(2000L);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class HeartBeat
    implements Runnable {
        private HeartBeat() {
        }

        public void run() {
            try {
                if (SMPPGateway.this.ready) {
                    SMPPGateway.this.sendSMPPPacket(new SMPPPacket(SMPPID.ENQUIRE_LINK), false);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

