/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.Expose
 */
package com.projectgoth.fusion.chat.external.aim;

import com.google.gson.annotations.Expose;
import com.projectgoth.fusion.chat.external.ChatConnectionInterface;
import com.projectgoth.fusion.chat.external.ChatConnectionListenerInterface;
import com.projectgoth.fusion.chat.external.aim.AIMException;
import com.projectgoth.fusion.chat.external.aim.AIMStatus;
import com.projectgoth.fusion.chat.external.aim.BuddyIcon;
import com.projectgoth.fusion.chat.external.aim.Connection;
import com.projectgoth.fusion.chat.external.aim.FLAP;
import com.projectgoth.fusion.chat.external.aim.SNAC;
import com.projectgoth.fusion.chat.external.aim.TLV;
import com.projectgoth.fusion.common.ByteBufferHelper;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AIMConnection
extends Connection
implements ChatConnectionInterface {
    private static final String LOGIN_SERVER = "login.oscar.aol.com";
    private static final int LOGIN_PORT = 5190;
    private static final byte[] PASSWORD_HASH = new byte[]{-13, 38, -127, -60, 57, -122, -37, -110, 113, -93, -71, -26, 83, 122, -107, 124};
    private static final int SIGNIN_TIMEOUT = 30000;
    private static final String DEFAULT_GROUP_NAME = "migme";
    private static final String ONLINE_MESSAGE = "";
    private static final String AWAY_MESSAGE = "migme";
    private static final String BUDDY_ICON_NAME = "1";
    private static final short[] SERVICES = new short[]{34, 1, 19, 2, 3, 21, 4, 6, 9, 10, 11};
    private static final short[] SERVICES_VERSION = new short[]{1, 4, 3, 1, 1, 1, 1, 1, 1, 1, 1};
    private static final short CLIENT_ID = 272;
    private static final short CLIENT_VERSION = 5014;
    private static final byte[] CLIENT_CAPABILITIES = new byte[]{9, 70, 19, 70, 76, 127, 17, -47, -126, 34, 68, 69, 83, 84, 0, 0, 9, 70, 19, 77, 76, 127, 17, -47, -126, 34, 68, 69, 83, 84, 0, 0, 9, 70, 19, 78, 76, 127, 17, -47, -126, 34, 68, 69, 83, 84, 0, 0};
    private ChatConnectionListenerInterface listener;
    private Map<Short, Short> availableServices = new ConcurrentHashMap<Short, Short>();
    @Expose
    private String loginUser;
    private String loginPassword;
    private TLV loginCookie;
    @Expose
    private boolean isSignedIn;
    @Expose
    private String signInFailedReason;
    private short defaultGroupID;
    private List<Short> itemIDs = new ArrayList<Short>();
    private Map<String, Short> contactIDs = new ConcurrentHashMap<String, Short>();
    private Map<String, Short> contactGroupIDs = new ConcurrentHashMap<String, Short>();
    private BuddyIcon buddyIcon;
    private String buddyIconLocation;
    private Map<Integer, SNAC> ssiTransactions = new ConcurrentHashMap<Integer, SNAC>();

    public AIMConnection(ChatConnectionListenerInterface listener, String buddyIconLocation, int connectionTimeout) {
        this.listener = listener;
        this.buddyIconLocation = buddyIconLocation;
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public synchronized void signIn(String username, String password) throws AIMException {
        if (!SystemProperty.getBool("AIMEnabled", true)) {
            throw new AIMException("AIM is not supported anymore");
        }
        try {
            if (!this.isSignedIn()) {
                this.loginUser = username;
                this.loginPassword = password;
                this.loginCookie = null;
                this.availableServices.clear();
                this.itemIDs.clear();
                this.contactIDs.clear();
                this.contactGroupIDs.clear();
                this.defaultGroupID = 0;
                this.signInFailedReason = "Sign in time out";
                this.ssiTransactions.clear();
                this.connect(LOGIN_SERVER, 5190);
                this.wait(30000L);
                if (!this.isSignedIn()) {
                    throw new Exception(this.signInFailedReason);
                }
                this.listener.onSignInSuccess(this);
            }
        }
        catch (Exception e) {
            this.disconnect(e.getMessage());
            throw new AIMException(e.getMessage());
        }
    }

    @Override
    public boolean isSignedIn() {
        return this.isSignedIn && this.isConnected();
    }

    @Override
    public void signOut() {
        this.disconnect(ONLINE_MESSAGE);
    }

    public void setStatus(AIMStatus status, String personalMessage) {
        TLV statusMessageTLV;
        TLV statusTLV;
        if (status == AIMStatus.AWAY) {
            personalMessage = personalMessage == null || personalMessage.length() == 0 ? "migme" : personalMessage;
            statusTLV = new TLV(29, "migme".getBytes());
            statusMessageTLV = new TLV(4, personalMessage.getBytes());
        } else {
            personalMessage = personalMessage == null || personalMessage.length() == 0 ? ONLINE_MESSAGE : personalMessage;
            statusTLV = new TLV(6, status.getValue());
            statusMessageTLV = new TLV(4, personalMessage.getBytes());
        }
        this.sendAsyncPacket(new FLAP(new SNAC(2, 4).append(statusMessageTLV)));
        this.sendAsyncPacket(new FLAP(new SNAC(1, 30).append(statusTLV)));
    }

    @Override
    public void addContact(String username) throws AIMException {
        try {
            if (this.defaultGroupID == 0) {
                this.defaultGroupID = this.newItemID();
                this.ssiTransaction((short)8, "migme", this.defaultGroupID, (short)0, (short)1, null);
            }
            short contactID = this.newItemID();
            this.ssiTransaction((short)8, username, this.defaultGroupID, contactID, (short)0, null);
        }
        catch (UnsupportedEncodingException e) {
            throw new AIMException(e.getMessage());
        }
    }

    @Override
    public void removeContact(String username) throws AIMException {
        try {
            Short contactID = this.contactIDs.get(username);
            Short contactGroupID = this.contactGroupIDs.get(username);
            if (contactID != null || contactGroupID != null) {
                this.ssiTransaction((short)10, username, contactGroupID, contactID, (short)0, null);
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new AIMException(e.getMessage());
        }
    }

    @Override
    public void sendMessage(String username, String message) throws AIMException {
        try {
            byte[] usernameBytes = username.getBytes("UTF-8");
            TLV messageBody = new TLV(2);
            messageBody.append(new TLV(1281, new byte[]{1})).append(new TLV(257, 0).append(message, "UTF-8"));
            SNAC messageThruServer = new SNAC(4, 6);
            messageThruServer.append(System.currentTimeMillis()).append((short)1).append((byte)usernameBytes.length).append(usernameBytes).append(messageBody).append(new TLV(3)).append(new TLV(6));
            this.sendAsyncPacket(new FLAP(messageThruServer));
        }
        catch (UnsupportedEncodingException e) {
            throw new AIMException(e.getMessage());
        }
    }

    @Override
    public ImType getImType() {
        return ImType.AIM;
    }

    @Override
    public String getUsername() {
        return this.loginUser;
    }

    @Override
    public void setAvatar(String fileLocation) throws Exception {
        throw new UnsupportedOperationException("AIM chat does not support changing profile pictures");
    }

    @Override
    public void setStatus(PresenceType status, String message) throws Exception {
        this.setStatus(AIMStatus.fromFusionPresence(status), message);
    }

    @Override
    public String inviteToConference(String conferenceID, String username) throws Exception {
        throw new UnsupportedOperationException("AIM chat does not support conferences");
    }

    @Override
    public void leaveConference(String conferenceID) {
        throw new UnsupportedOperationException("AIM chat does not support conferences");
    }

    @Override
    public List<String> getConferenceParticipants(String conferenceID) {
        throw new UnsupportedOperationException("AIM chat does not support conferences");
    }

    private short newItemID() {
        short id;
        for (id = (short)(32767.0 * Math.random()); id == 0 || this.itemIDs.contains(id); id = (short)(id + 1)) {
        }
        this.itemIDs.add(id);
        return id;
    }

    private byte[] hashPassword(String password) {
        byte[] hashedPassword = new byte[password.length()];
        for (int i = 0; i < password.length(); ++i) {
            hashedPassword[i] = (byte)(this.loginPassword.charAt(i) ^ PASSWORD_HASH[i % 16]);
        }
        return hashedPassword;
    }

    private void ssiTransaction(short type, String itemName, short groupID, short itemID, short itemType, TLV[] tlvs) throws UnsupportedEncodingException {
        byte[] itemNameBytes = itemName.getBytes("UTF-8");
        short tlvsLength = 0;
        if (tlvs != null) {
            for (TLV tlv : tlvs) {
                tlvsLength = (short)(tlvsLength + (4 + tlv.getData().length));
            }
        }
        SNAC ssiItem = new SNAC(19, type);
        ssiItem.append((short)itemNameBytes.length).append(itemNameBytes).append(groupID).append(itemID).append(itemType).append(tlvsLength);
        if (tlvs != null) {
            for (TLV tlv : tlvs) {
                ssiItem.append(tlv);
            }
        }
        if (itemType == 1 || itemType == 0) {
            this.sendAsyncPacket(new FLAP(new SNAC(19, 17)));
            this.ssiTransactions.put(ssiItem.getRequestID(), ssiItem);
        }
        this.sendAsyncPacket(new FLAP(ssiItem));
        if (itemType == 1 || itemType == 0) {
            this.sendAsyncPacket(new FLAP(new SNAC(19, 18)));
        }
    }

    private void sendBuddyIconMD5Hash() {
        try {
            if (this.buddyIconLocation != null) {
                if (this.buddyIcon == null) {
                    this.buddyIcon = new BuddyIcon(this.newItemID(), BUDDY_ICON_NAME, this.buddyIconLocation);
                }
                TLV[] tlvs = new TLV[]{new TLV(305), new TLV(213, 272).append(this.buddyIcon.getMd5Hash())};
                this.ssiTransaction((short)8, this.buddyIcon.getName(), (short)0, this.buddyIcon.getID(), (short)20, tlvs);
            }
        }
        catch (Exception e) {
            this.buddyIcon = null;
            e.printStackTrace();
        }
    }

    @Override
    protected void onDisconnect(String reason) {
        if (this.isSignedIn) {
            this.isSignedIn = false;
            if (this.buddyIcon != null) {
                this.buddyIcon.disconnect(ONLINE_MESSAGE);
            }
            this.listener.onDisconnected(this, reason);
        }
    }

    @Override
    protected void onIncomingPacket(FLAP packet) {
        byte channel = packet.getChannel();
        if (channel == 1) {
            if (packet.getData().length == 4) {
                this.onServerHello();
            }
        } else if (channel == 2) {
            SNAC snac = new SNAC(ByteBuffer.wrap(packet.getData()));
            short service = snac.getService();
            short subType = snac.getSubType();
            ByteBuffer data = snac.getAdjustedData();
            if (service == 1) {
                if (subType == 3) {
                    this.onServiceList(snac, data);
                } else if (subType == 5) {
                    this.onRedirect(snac, data);
                } else if (subType == 24) {
                    this.onServiceVersions(snac, data);
                } else if (subType == 7) {
                    this.onRateLimits(snac, data);
                } else if (subType == 33) {
                    this.onExtendedStatus(snac, data);
                }
            } else if (service == 3) {
                if (subType == 11) {
                    this.onStatus(snac, data, true);
                } else if (subType == 12) {
                    this.onStatus(snac, data, false);
                }
            } else if (service == 4) {
                if (subType == 7) {
                    this.onMessage(snac, data);
                }
            } else if (service == 19) {
                if (subType == 6) {
                    this.onItemList(snac, data);
                } else if (subType == 14) {
                    this.onSSITransactionStatus(snac, data);
                }
            }
        } else if (channel == 4) {
            this.onCloseConnectionNegotiation(packet);
        }
    }

    private void onServerHello() {
        try {
            FLAP flap = new FLAP(1).append(1);
            if (this.loginCookie == null) {
                flap.append(new TLV(1, this.loginUser, "UTF-8")).append(new TLV(2, this.hashPassword(this.loginPassword)));
            } else {
                flap.append(this.loginCookie);
            }
            this.sendAsyncPacket(flap);
        }
        catch (UnsupportedEncodingException e) {
            this.disconnect(e.getMessage());
        }
    }

    private void onCloseConnectionNegotiation(FLAP flap) {
        Map<Integer, TLV> tlvs = TLV.parse(ByteBuffer.wrap(flap.getData()));
        TLV bosServer = tlvs.get(5);
        this.loginCookie = tlvs.get(6);
        if (bosServer != null && this.loginCookie != null) {
            this.disconnect("Server authentication cookie received");
            try {
                String[] serverTokens = bosServer.getDataAsString("UTF-8").split(":");
                this.connect(serverTokens[0], Integer.valueOf(serverTokens[1]));
            }
            catch (Exception e) {
                this.disconnect(e.getMessage());
            }
        } else {
            TLV reason = tlvs.get(8);
            if (reason == null) {
                this.signInFailedReason = "Unknown reason";
            } else {
                switch (reason.getDataAsShort()) {
                    case 1: {
                        this.signInFailedReason = "Multiple logins";
                        break;
                    }
                    case 4: 
                    case 5: {
                        this.signInFailedReason = "Incorrect password";
                        break;
                    }
                    case 7: 
                    case 8: {
                        this.signInFailedReason = "Incorrect screen name";
                        break;
                    }
                    case 21: 
                    case 22: {
                        this.signInFailedReason = "Too many clients from same IP";
                        break;
                    }
                    case 24: 
                    case 29: {
                        this.signInFailedReason = "Rate exceeded";
                        break;
                    }
                    case 27: {
                        this.signInFailedReason = "Unsupported version";
                        break;
                    }
                    default: {
                        this.signInFailedReason = "AIM service temporary unavailable (" + reason.getDataAsShort() + ")";
                    }
                }
            }
            this.disconnect(this.signInFailedReason);
            this.notifyAll();
        }
    }

    private void onServiceList(SNAC snac, ByteBuffer data) {
        while (data.hasRemaining()) {
            this.availableServices.put(data.getShort(), (short)1);
        }
        SNAC serviceVersionRequest = new SNAC(1, 23);
        for (int i = 0; i < SERVICES.length; ++i) {
            if (!this.availableServices.containsKey(SERVICES[i])) continue;
            serviceVersionRequest.append(SERVICES[i]).append(SERVICES_VERSION[i]);
        }
        this.sendAsyncPacket(new FLAP(serviceVersionRequest));
    }

    private void onServiceVersions(SNAC snac, ByteBuffer data) {
        while (data.hasRemaining()) {
            short snacService = data.getShort();
            short version = data.getShort();
            this.availableServices.put(snacService, version);
        }
        this.sendAsyncPacket(new FLAP(new SNAC(1, 6)));
    }

    private void onRateLimits(SNAC snac, ByteBuffer data) {
        SNAC rateLimitAck = new SNAC(1, 8);
        int classes = data.getShort();
        data.get(new byte[35 * classes]);
        for (int i = 0; i < classes; ++i) {
            short groupID = data.getShort();
            short pairs = data.getShort();
            data.get(new byte[4 * pairs]);
            rateLimitAck.append(groupID);
        }
        this.sendAsyncPacket(new FLAP(rateLimitAck));
        this.sendAsyncPacket(new FLAP(new SNAC(1, 14)));
        this.sendAsyncPacket(new FLAP(new SNAC(19, 2).append(new byte[]{0, 11, 0, 2, 0, 61})));
        this.sendAsyncPacket(new FLAP(new SNAC(19, 5).append(0).append((short)0)));
        this.sendAsyncPacket(new FLAP(new SNAC(2, 2)));
        this.sendAsyncPacket(new FLAP(new SNAC(3, 2).append(new byte[]{0, 5, 0, 2, 0, 3})));
        this.sendAsyncPacket(new FLAP(new SNAC(4, 4)));
        this.sendAsyncPacket(new FLAP(new SNAC(9, 2)));
    }

    private void onExtendedStatus(SNAC snac, ByteBuffer data) {
        byte[] md5Hash;
        String name = String.valueOf(data.getShort());
        byte flag = data.get();
        if (flag >> 6 == 1 && this.buddyIcon != null && name.equals(this.buddyIcon.getName()) && this.buddyIcon.validateMD5Hash(md5Hash = ByteBufferHelper.readBytes(data, data.get()))) {
            this.sendAsyncPacket(new FLAP(new SNAC(1, 4).append((short)16)));
        }
    }

    private void onRedirect(SNAC snac, ByteBuffer data) {
        Map<Integer, TLV> tlvs = TLV.parse(data);
        TLV service = tlvs.get(13);
        if (service != null && service.getDataAsShort() == 16) {
            TLV server = tlvs.get(5);
            TLV cookie = tlvs.get(6);
            if (server != null && cookie != null) {
                try {
                    this.buddyIcon.upload(server.getDataAsString("UTF-8"), 5190, cookie.getData());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onItemList(SNAC snac, ByteBuffer data) {
        try {
            data.get();
            int count = data.getShort();
            for (int i = 0; i < count; ++i) {
                String itemName = new String(ByteBufferHelper.readBytes(data, data.getShort()), "UTF-8");
                short groupID = data.getShort();
                short itemID = data.getShort();
                short itemType = data.getShort();
                ByteBufferHelper.readBytes(data, data.getShort());
                if (itemType == 0) {
                    if (!this.contactIDs.containsKey(itemName)) {
                        this.contactIDs.put(itemName, itemID);
                        this.contactGroupIDs.put(itemName, groupID);
                        this.listener.onContactDetail(this, itemName, itemName);
                    }
                } else if (itemType == 1) {
                    if ("migme".equals(itemName)) {
                        this.defaultGroupID = groupID;
                    }
                } else if (itemType == 20) {
                    this.ssiTransaction((short)10, itemName, groupID, itemID, itemType, null);
                }
                this.itemIDs.add(itemID);
            }
            this.sendAsyncPacket(new FLAP(new SNAC(19, 7)));
            this.sendAsyncPacket(new FLAP(new SNAC(2, 4).append(new TLV(5, CLIENT_CAPABILITIES))));
            this.sendBuddyIconMD5Hash();
            SNAC ready = new SNAC(1, 2);
            for (int i = 0; i < SERVICES.length; ++i) {
                if (!this.availableServices.containsKey(SERVICES[i])) continue;
                ready.append(SERVICES[i]).append(SERVICES_VERSION[i]).append((short)272).append((short)5014);
            }
            this.sendAsyncPacket(new FLAP(ready));
            this.isSignedIn = true;
            this.notifyAll();
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
    }

    private void onStatus(SNAC snac, ByteBuffer data, boolean online) {
        while (data.hasRemaining()) {
            try {
                String screenName = new String(ByteBufferHelper.readBytes(data, data.get()), "UTF-8");
                data.getShort();
                AIMStatus status = null;
                Map<Integer, TLV> tlvs = TLV.parse(data, data.getShort());
                if (online) {
                    TLV tlv = tlvs.get(6);
                    if (tlv == null) {
                        tlv = tlvs.get(1);
                        status = tlv == null ? AIMStatus.AVAILABLE : ((tlv.getDataAsShort() & 0x20) == 32 ? AIMStatus.AWAY : AIMStatus.AVAILABLE);
                    } else {
                        status = AIMStatus.valueOf(tlv.getDataAsInt() & 0xFFFF);
                        if (status == null) {
                            status = AIMStatus.AWAY;
                        }
                    }
                } else {
                    status = AIMStatus.OFFLINE;
                }
                this.listener.onContactStatusChanged(this, screenName, status.toFusionPresence());
            }
            catch (UnsupportedEncodingException e) {}
        }
    }

    private void onMessage(SNAC snac, ByteBuffer data) {
        try {
            TLV tlv;
            data.getLong();
            short channel = data.getShort();
            String screenName = new String(ByteBufferHelper.readBytes(data, data.get()), "UTF-8");
            data.getInt();
            if (channel == 1 && (tlv = TLV.parse(data).get(2)) != null && (tlv = TLV.parse(ByteBuffer.wrap(tlv.getData())).get(257)) != null) {
                ByteBuffer messageData = ByteBuffer.wrap(tlv.getData());
                messageData.getInt();
                String messageString = new String(ByteBufferHelper.readBytes(messageData, messageData.remaining()), "UTF-8");
                messageString = messageString.replaceAll("(<|</)[^>]*>", ONLINE_MESSAGE).replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
                this.listener.onMessageReceived(this, null, screenName, messageString);
            }
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
    }

    private void onSSITransactionStatus(SNAC snac, ByteBuffer data) {
        SNAC originalSNAC = this.ssiTransactions.remove(snac.getRequestID());
        if (originalSNAC != null) {
            try {
                boolean success = data.getShort() == 0;
                short subType = originalSNAC.getSubType();
                ByteBuffer originalData = originalSNAC.getAdjustedData();
                String itemName = new String(ByteBufferHelper.readBytes(originalData, originalData.getShort()), "UTF-8");
                short groupID = originalData.getShort();
                short itemID = originalData.getShort();
                short itemType = originalData.getShort();
                if (success && itemType == 0) {
                    if (subType == 8) {
                        this.contactIDs.put(itemName, itemID);
                        this.contactGroupIDs.put(itemName, groupID);
                        this.listener.onContactDetail(this, itemName, itemName);
                    } else if (subType == 10) {
                        this.contactIDs.remove(itemName);
                        this.contactGroupIDs.remove(itemName);
                    }
                }
            }
            catch (UnsupportedEncodingException e) {
                // empty catch block
            }
        }
    }
}

