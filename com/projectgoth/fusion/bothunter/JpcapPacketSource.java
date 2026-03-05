/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jpcap.JpcapCaptor
 *  jpcap.NetworkInterface
 *  jpcap.packet.Packet
 *  jpcap.packet.TCPPacket
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.bothunter;

import com.projectgoth.fusion.bothunter.ClientAddressToUsername;
import com.projectgoth.fusion.bothunter.InvalidPackets;
import com.projectgoth.fusion.bothunter.MonitorThread;
import com.projectgoth.fusion.bothunter.PacketDetails;
import com.projectgoth.fusion.bothunter.PacketSource;
import com.projectgoth.fusion.bothunter.Params;
import com.projectgoth.fusion.bothunter.TcpOptionsCompact;
import com.projectgoth.fusion.bothunter.ValidFusionPackets;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.gateway.packet.FusionPktLogin;
import com.projectgoth.fusion.packet.FusionField;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import org.apache.log4j.Logger;

public class JpcapPacketSource
implements PacketSource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MonitorThread.class));
    private static final int FUSION_PACKET_HEADER_SIZE = 9;
    private static final byte FUSION_PACKET_TYPE = 2;
    private static final byte LOGIN_PACKET_TYPE_MSB = 0;
    private static final byte LOGIN_PACKET_TYPE_LSB = -56;
    private static final byte MSG_PACKET_TYPE_MSB = 1;
    private static final byte MSG_PACKET_TYPE_LSB = -12;
    private static final int LOGIN_PACKET_USERNAME_FIELD_INDEX = 5;
    private boolean debugEnabled;
    private NetworkInterface nic;
    private JpcapCaptor captor;
    private TcpOptionsCompact tcpOptions;
    private ByteBuffer bbInt;
    private ByteBuffer bbShort;
    private static final int READ_BUFFER_SIZE = 1024;
    private static final int MAX_READ_BUFFER_SIZE = 358400;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);

    public JpcapPacketSource(String nicName, String pcapFilter) throws Exception {
        NetworkInterface[] devices;
        this.debugEnabled = log.isDebugEnabled();
        for (NetworkInterface d : devices = JpcapCaptor.getDeviceList()) {
            if (!d.name.equals(nicName)) continue;
            this.nic = d;
            break;
        }
        this.captor = JpcapCaptor.openDevice((NetworkInterface)this.nic, (int)256000, (boolean)false, (int)Integer.MAX_VALUE);
        if (pcapFilter == null) {
            this.captor.setFilter("tcp and dst 192.168.2.220", true);
        } else {
            this.captor.setFilter(pcapFilter, true);
        }
        this.captor.setNonBlockingMode(true);
        this.tcpOptions = new TcpOptionsCompact();
        this.bbInt = ByteBuffer.allocate(4);
        this.bbInt.order(ByteOrder.BIG_ENDIAN);
        this.bbShort = ByteBuffer.allocate(2);
        this.bbShort.order(ByteOrder.BIG_ENDIAN);
    }

    public PacketDetails nextPacket() throws Exception {
        boolean hasWinscale;
        Packet p = this.captor.getPacket();
        if (p == null) {
            return null;
        }
        if (!(p instanceof TCPPacket)) {
            return null;
        }
        TCPPacket tp = (TCPPacket)p;
        if (tp.option == null) {
            return null;
        }
        try {
            this.tcpOptions.parse(tp.syn, tp.option);
        }
        catch (Exception e) {
            log.error((Object)"Exception in JpcapPacketSource.nextPacket(): ", (Throwable)e);
            return null;
        }
        if (log.isDebugEnabled() && tp.syn) {
            log.debug((Object)("SYN packet" + (this.tcpOptions.hasWinScale() ? " with winscale=" + this.tcpOptions.getWinScale() : "")));
        }
        boolean bl = hasWinscale = tp.syn && this.tcpOptions.hasWinScale() && this.tcpOptions.getWinScale() != 1;
        if (p.data.length == 0 && !hasWinscale) {
            return null;
        }
        PacketDetails pd = new PacketDetails(System.currentTimeMillis(), tp.src_ip.toString(), tp.src_port, this.tcpOptions.getTsVal(), this.tcpOptions.getWinScale());
        if (hasWinscale) {
            return pd;
        }
        if (this.debugEnabled) {
            log.debug((Object)("Packet with src_ip=" + tp.src_ip.toString() + " src_port=" + tp.src_port + " dst_ip=" + tp.dst_ip.toString() + " dst_port=" + tp.dst_port + " timestamp=" + pd.getTcpTimestamp() + " winscale=" + pd.getTcpWinscale() + " tp.data.length=" + tp.data.length));
        }
        if (tp.data.length != 0 && tp.data[0] == 2) {
            this.onFusionPacket(tp, pd);
        }
        return pd;
    }

    private void onFusionPacket(TCPPacket tp, PacketDetails pd) throws Exception {
        FusionPacket fp;
        block9: {
            fp = null;
            try {
                fp = this.readFusionPacket(tp, true);
            }
            catch (Exception e) {
                if (!this.debugEnabled) break block9;
                log.debug((Object)"Invalid fusion packet!");
            }
        }
        if (fp != null) {
            this.sniffUsername(tp, fp, pd);
        }
        if (Params.MODE.equals((Object)Params.Mode.INVALID_FUSION_PACKET_TYPE_DETECTION)) {
            log.debug((Object)"Invalid type detection, checking...");
            this.onFusionPacketInvalidPacketTypeDetectionMode(tp, pd);
        } else if (Params.MODE.equals((Object)Params.Mode.INVALID_FUSION_PACKET_DETECTION)) {
            if (fp == null) {
                this.onInvalidPacket(tp);
            }
        } else if (!Params.MODE.equals((Object)Params.Mode.BOT_DETECTION)) {
            throw new Exception("Invalid mode parameter");
        }
    }

    private void onFusionPacketInvalidPacketTypeDetectionMode(TCPPacket tp, PacketDetails pd) throws Exception {
        Short packetType = this.getPacketType(tp);
        if (packetType == null) {
            log.info((Object)("Invalid fusion packet type=" + packetType));
            this.onInvalidPacket(tp);
        } else if (!ValidFusionPackets.getInstance().contains(packetType)) {
            log.info((Object)("Invalid fusion packet type=" + packetType));
            this.onInvalidPacket(tp);
        }
    }

    private void onInvalidPacket(TCPPacket tp) throws Exception {
        InvalidPackets.getInstance().hit(tp);
    }

    private void sniffUsername(TCPPacket tp, FusionPacket fp, PacketDetails pd) throws Exception {
        if (tp.data.length < 3) {
            return;
        }
        if (fp.getType() == 200) {
            this.extractUsername(tp, fp, (short)5);
            if (this.debugEnabled) {
                log.debug((Object)"Getting claimed client type...");
            }
            FusionPktLogin login = new FusionPktLogin(fp);
            pd.setClaimedClientType(login.getClientType().value());
            if (this.debugEnabled) {
                log.debug((Object)("Got claimed client type=" + (Object)((Object)login.getClientType())));
            }
        } else if (fp.getType() == 500) {
            this.extractUsername(tp, fp, (short)2);
        }
        if (this.debugEnabled) {
            String username = ClientAddressToUsername.getInstance().getUsername(tp);
            log.debug((Object)("Fusion packet (doesnt contain username): username=" + username + " host=" + tp.src_ip.toString() + " port=" + tp.src_port + " timestamp=" + pd.getTcpTimestamp() + "  packet as string=" + tp.toString()));
        }
    }

    private Short getPacketType(TCPPacket tp) throws Exception {
        if (tp.data.length < 3) {
            return null;
        }
        return (short)(tp.data[1] << 8 | tp.data[2] & 0xFF);
    }

    private FusionPacket readFusionPacket(TCPPacket tp, boolean tolerateEOF) throws Exception {
        FusionPacket fp = new FusionPacket();
        ByteArrayInputStream in = new ByteArrayInputStream(tp.data);
        try {
            fp.read(in);
        }
        catch (EOFException e) {
            if (tolerateEOF) {
                if (this.debugEnabled) {
                    // empty if block
                }
            }
            throw e;
        }
        if (this.debugEnabled) {
            for (short i = 0; i < fp.getFields().size(); i = (short)(i + 1)) {
                FusionField field = fp.getField(i);
                log.debug((Object)(i + " = " + field));
            }
        }
        return fp;
    }

    private void extractUsername(TCPPacket tp, FusionPacket fp, short usernameFieldIndex) throws Exception {
        String username;
        if (this.debugEnabled) {
            log.debug((Object)("Extracting username from FusionPacket field " + usernameFieldIndex));
        }
        if ((username = fp.getStringField(usernameFieldIndex)) != null && username.length() != 0) {
            if (this.debugEnabled) {
                log.debug((Object)("User " + username + " logging in " + "with host=" + tp.src_ip.toString() + " port=" + tp.src_port));
            }
            ClientAddressToUsername.getInstance().putUsername(tp, username);
        }
    }

    private void handrolledExtractUsername_NotUsed(TCPPacket tp) throws Exception {
        int fieldOffset = 9;
        for (int field = 1; field < 5; ++field) {
            log.debug((Object)("Field " + field + " is at offset " + fieldOffset));
            this.bbShort.position(0);
            this.bbShort.put(tp.data, fieldOffset, 2);
            this.bbShort.position(0);
            short fieldType = this.bbShort.getShort();
            log.debug((Object)("...is of type " + fieldType));
            this.bbInt.position(0);
            this.bbInt.put(tp.data, fieldOffset + 2, 4);
            this.bbInt.position(0);
            int dataLength = this.bbInt.getInt();
            log.debug((Object)("and has length 6+" + dataLength));
            fieldOffset += 6 + dataLength;
        }
        this.bbInt.position(0);
        this.bbInt.put(tp.data, fieldOffset + 2, 4);
        this.bbInt.position(0);
        int stringDataLength = this.bbInt.getInt();
        log.debug((Object)("Username is of length " + stringDataLength));
        byte[] utf = new byte[stringDataLength];
        System.arraycopy(tp.data, fieldOffset + 2 + 4, utf, 0, stringDataLength);
        String username = new String(utf, "UTF-8");
    }

    public boolean finished() throws Exception {
        return false;
    }

    public void close() {
    }

    public String getSourceIP() {
        return null;
    }

    public String getDestinationIP() {
        return null;
    }

    public long getTcpTimestamp() {
        return 0L;
    }

    public long getTimeReceived() {
        return 0L;
    }
}

