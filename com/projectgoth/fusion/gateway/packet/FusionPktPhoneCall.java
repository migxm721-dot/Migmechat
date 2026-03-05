/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.FloodControl;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.CallData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktDial;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.interfaces.Voice;
import com.projectgoth.fusion.interfaces.VoiceHome;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserPrx;
import java.rmi.RemoteException;
import java.util.Arrays;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktPhoneCall
extends FusionRequest {
    private static final Logger auditLog = Logger.getLogger((String)"VoiceAudit");

    public FusionPktPhoneCall() {
        super((short)800);
    }

    public FusionPktPhoneCall(short transactionId) {
        super((short)800, transactionId);
    }

    public FusionPktPhoneCall(FusionPacket packet) {
        super(packet);
    }

    public String getSource() {
        return this.getStringField((short)1);
    }

    public void setSource(String source) {
        this.setField((short)1, source);
    }

    public String getDestination() {
        return this.getStringField((short)2);
    }

    public void setDestination(String destination) {
        this.setField((short)2, destination);
    }

    public Integer getContactId() {
        return this.getIntField((short)3);
    }

    public void setContactId(int contactId) {
        this.setField((short)3, contactId);
    }

    public Byte getEvaluate() {
        return this.getByteField((short)4);
    }

    public void setEvaluate(byte evaluate) {
        this.setField((short)4, evaluate);
    }

    public Byte getMethod() {
        return this.getByteField((short)5);
    }

    public void setMethod(byte method) {
        this.setField((short)5, method);
    }

    public boolean sessionRequired() {
        return true;
    }

    private FusionPacket[] processCallThrough(ConnectionI connection) throws CreateException, RemoteException {
        User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        UserData userData = userEJB.loadUser(connection.getUsername(), false, false);
        if (!userData.mobilePhone.equals(this.getSource())) {
            return this.processCallback(connection, true);
        }
        Voice voiceEJB = (Voice)EJBHomeCache.getObject("ejb/Voice", VoiceHome.class);
        String didNumber = voiceEJB.getDIDNumber(userData.countryID);
        String fullDIDNumber = voiceEJB.getFullDIDNumber(userData.countryID);
        if (didNumber == null || fullDIDNumber == null) {
            return this.processCallback(connection, true);
        }
        CallData callData = new CallData();
        callData.username = connection.getUsername();
        callData.source = this.getSource();
        callData.sourceType = CallData.SourceDestinationTypeEnum.PSTN_PHONE;
        callData.destination = this.getDestination();
        callData.destinationType = CallData.SourceDestinationTypeEnum.PSTN_PHONE;
        callData.contactID = this.getContactId();
        callData.type = CallData.TypeEnum.MIDLET_CALL_THROUGH;
        callData.didNumber = fullDIDNumber;
        callData = voiceEJB.initiatePhoneCall(callData);
        FusionPktDial dial = new FusionPktDial(this.transactionId);
        dial.setPhoneNumber(didNumber);
        dial.setConfirmationMessage(this.constructInfoText(callData, false).replaceAll("%n", didNumber));
        return dial.toArray();
    }

    private FusionPacket[] processCallback(ConnectionI connection, boolean callThroughFailed) throws CreateException, RemoteException {
        CallData callData = new CallData();
        callData.username = connection.getUsername();
        callData.source = this.getSource();
        callData.sourceType = CallData.SourceDestinationTypeEnum.PSTN_PHONE;
        callData.destination = this.getDestination();
        callData.destinationType = CallData.SourceDestinationTypeEnum.PSTN_PHONE;
        callData.contactID = this.getContactId();
        callData.type = CallData.TypeEnum.MIDLET_CALLBACK;
        Voice voiceEJB = (Voice)EJBHomeCache.getObject("ejb/Voice", VoiceHome.class);
        Byte evaluate = this.getEvaluate();
        if (evaluate != null && evaluate == 1) {
            callData = voiceEJB.evaluatePhoneCall(callData);
            FusionPktOk pkt = new FusionPktOk(this.transactionId);
            pkt.setServerResponse(this.constructInfoText(callData, callThroughFailed));
            return pkt.toArray();
        }
        voiceEJB.initiatePhoneCall(callData);
        return new FusionPktOk(this.transactionId, 7).toArray();
    }

    private FusionPacket[] processAnonymousCall(ConnectionI connection) throws CreateException, RemoteException, FusionException {
        String destination = this.getDestination();
        boolean anonymousCalling = SystemProperty.getBool("AnonymousCalling", false);
        if (!anonymousCalling) {
            throw new FusionException("Invalid destination number " + destination);
        }
        UserPrx userPrx = null;
        try {
            userPrx = connection.findRegistry().findUserObject(destination);
        }
        catch (Exception e) {
            throw new FusionException(destination + " is currently offline");
        }
        Byte evaluate = this.getEvaluate();
        if (evaluate != null && evaluate == 1) {
            CallData callData = new CallData();
            callData.username = connection.getUsername();
            callData.source = this.getSource();
            callData.sourceType = CallData.SourceDestinationTypeEnum.PSTN_PHONE;
            callData.destination = userPrx.getUserData().mobilePhone;
            callData.destinationType = CallData.SourceDestinationTypeEnum.PSTN_PHONE;
            callData.contactID = this.getContactId();
            callData.type = CallData.TypeEnum.MIDLET_ANONYMOUS_CALLBACK;
            Voice voiceEJB = (Voice)EJBHomeCache.getObject("ejb/Voice", VoiceHome.class);
            callData = voiceEJB.evaluatePhoneCall(callData);
            callData.destination = this.getDestination();
            FusionPktOk pkt = new FusionPktOk(this.transactionId);
            pkt.setServerResponse(this.constructInfoText(callData, false));
            return pkt.toArray();
        }
        userPrx.putAnonymousCallNotification(connection.getUsername(), this.getSource());
        return new FusionPktOk(this.transactionId, 43).toArray();
    }

    private String constructInfoText(CallData callData, boolean callThroughFailed) throws CreateException, RemoteException {
        String infoText = "";
        MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
        if (callData.isCallback()) {
            infoText = callData.signallingFee == 0.0 ? misEJB.getInfoText(callThroughFailed ? 35 : 19) : misEJB.getInfoText(callThroughFailed ? 36 : 20);
            if (infoText == null) {
                infoText = "You have requested a call from " + callData.source + " to " + callData.destination + ". Continue?";
            }
        } else if (callData.isCallThrough() && (infoText = callData.signallingFee == 0.0 ? misEJB.getInfoText(29) : misEJB.getInfoText(30)) == null) {
            infoText = "You have requested a call to " + callData.destination + ". Continue?";
        }
        Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
        CurrencyData userCurrency = accountEJB.getUsersLocalCurrency(callData.username);
        infoText = infoText.replaceAll("%s", callData.source).replaceAll("%d", callData.destination).replaceAll("%r", userCurrency.formatWithCode(userCurrency.convertFromBaseCurrency(callData.rate))).replaceAll("%f", userCurrency.formatWithCode(userCurrency.convertFromBaseCurrency(callData.signallingFee))).replaceAll("%t", String.valueOf(callData.maxCallDuration / 60));
        return infoText;
    }

    private boolean validateClientBuild(ConnectionI connection) throws CreateException, RemoteException {
        String invalidBuilds = SystemProperty.get("MidletsNotSupportPhoneCall", "");
        if (invalidBuilds.length() == 0) {
            return true;
        }
        return !Arrays.asList(invalidBuilds.split("[,;]")).contains(connection.getUserAgent());
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            if (!this.validateClientBuild(connection)) {
                throw new Exception("Your version of client does not support phone call feature");
            }
            String destination = this.getDestination();
            String source = this.getSource();
            try {
                FloodControl.detectFlooding(connection.getUsername(), connection.getUserPrx(), new FloodControl.Action[]{FloodControl.Action.PHONE_CALL.setMaxHits(SystemProperty.getLong("PhoneCallUserPerSecondRateLimit", 3L))});
            }
            catch (Exception e) {
                auditLog.info((Object)(connection.getUsername() + ", user disconnected and suspended for 1 hour, exceeded 3/second rate limit. Destination[" + destination + "] from source[" + source + "]"));
                throw e;
            }
            if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.PHONECALL.toString(), MemCachedKeyUtils.getFullKeyFromStrings("PS", "DEST", destination), SystemProperty.getLong("PhoneCallDestinationPerSecondRateLimit", 1L), 1000L) || !MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.PHONECALL.toString(), MemCachedKeyUtils.getFullKeyFromStrings("PH", "DEST", destination), SystemProperty.getLong("PhoneCallDestinationPerHourRateLimit", 60L), 3600000L)) {
                if (SystemProperty.getBool("SuspendPhoneCallDestinationRateLimitOffender", false)) {
                    auditLog.info((Object)(connection.getUsername() + ", user disconnected and suspended for 1 hour. Destination Rate Limit exceeded. Destination [" + destination + "] from source[" + source + "]"));
                    connection.getUserPrx().disconnectFlooder("Flooding. Broke Phone Call Destination Rate Limit. Destination [" + destination + "] from source[" + source + "]");
                    throw new Exception("You have been disconnected.");
                }
                auditLog.info((Object)(connection.getUsername() + ", call dropped, exceeded rate limit to destination[" + destination + "] from source[" + source + "]"));
                return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "System busy. Please try again later.").toArray();
            }
            if (!(source == null || source.equals(connection.getUserPrx().getUserData().mobilePhone) || MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.PHONECALL.toString(), MemCachedKeyUtils.getFullKeyFromStrings("PS", "SRC", source), SystemProperty.getLong("PhoneCallSourcePerSecondRateLimit", 1L), 1000L) && MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.PHONECALL.toString(), MemCachedKeyUtils.getFullKeyFromStrings("PH", "SRC", source), SystemProperty.getLong("PhoneCallSourcePerHourRateLimit", 60L), 3600000L))) {
                if (SystemProperty.getBool("SuspendPhoneCallSourceRateLimitOffender", false)) {
                    auditLog.info((Object)(connection.getUsername() + ", user disconnected and suspended for 1 hour. Destination Rate Limit exceeded. Destination [" + destination + "] from source[" + source + "]"));
                    connection.getUserPrx().disconnectFlooder("Flooding. Broke Phone Call Destination Rate Limit. Destination [" + destination + "] from source[" + source + "]");
                    throw new Exception("You have been disconnected.");
                }
                auditLog.info((Object)(connection.getUsername() + ", call dropped, exceeded rate limit for source[" + source + "] from destination[" + destination + "]"));
                return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "System busy. Please try again later.").toArray();
            }
            if (destination.matches("^[a-zA-Z].*")) {
                return this.processAnonymousCall(connection);
            }
            Byte method = this.getMethod();
            if (method == null || method == 1) {
                return this.processCallback(connection, false);
            }
            return this.processCallThrough(connection);
        }
        catch (CreateException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to initiate a call - " + e.getMessage()).toArray();
        }
        catch (RemoteException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to initiate a call - " + RMIExceptionHelper.getRootMessage(e)).toArray();
        }
        catch (FusionException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to initiate a call - " + e.message).toArray();
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to initiate a call - " + e.getMessage()).toArray();
        }
    }
}

