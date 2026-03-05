/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.voiceengine;

import Ice.Current;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.CallData;
import com.projectgoth.fusion.data.VoiceRouteData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Voice;
import com.projectgoth.fusion.interfaces.VoiceHome;
import com.projectgoth.fusion.slice.CallDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._CallMakerDisp;
import com.projectgoth.fusion.voiceengine.AsteriskGateway;
import com.projectgoth.fusion.voiceengine.CallRequest;
import com.projectgoth.fusion.voiceengine.RoutingTable;
import com.projectgoth.fusion.voiceengine.VoiceEngine;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CallMakerI
extends _CallMakerDisp
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(CallMakerI.class));
    private Map<Integer, AsteriskGateway> gateways;
    private RoutingTable routingTable;
    private Set<String> usersInCall;
    private List<CallData> callsCompleted;
    private Map<Integer, CallParam> callParams;

    public CallMakerI(Map<Integer, AsteriskGateway> gateways, RoutingTable routingTable) {
        this.gateways = gateways;
        this.routingTable = routingTable;
        this.usersInCall = Collections.synchronizedSet(new HashSet());
        this.callsCompleted = Collections.synchronizedList(new LinkedList());
        this.callParams = new ConcurrentHashMap<Integer, CallParam>();
        for (AsteriskGateway gateway : gateways.values()) {
            for (CallData callData : gateway.getCallsInProgress()) {
                this.usersInCall.add(callData.username);
            }
        }
        new Thread(this).start();
    }

    public boolean shouldRetry(int isdnCause) {
        return isdnCause == 8 || isdnCause == 34 || isdnCause == 38 || isdnCause == 41 || isdnCause == 42 || isdnCause == 47 || isdnCause == 55 || isdnCause == 57 || isdnCause == 58 || isdnCause == 88;
    }

    @Override
    public CallDataIce requestCallback(CallDataIce call, int maxDuration, int retries, Current __current) throws FusionException {
        boolean userAdded = false;
        try {
            CallData callData = new CallData(call);
            if (callData.username == null) {
                throw new Exception("Invalid or null username for callback");
            }
            CallParam callParam = this.callParams.get(callData.id);
            if (callParam == null) {
                callParam = new CallParam(maxDuration, ++retries);
                this.callParams.put(callData.id, callParam);
                log.info((Object)(callData.username + " requesting a callback. ID: " + callData.id + ". Source: " + callData.source + ". Destination: " + callData.destination + ". Retries+1: " + retries + "."));
                userAdded = this.usersInCall.add(callData.username);
                if (!userAdded) {
                    throw new Exception(callData.username + " already in another call for callback");
                }
            } else {
                ++callParam.retryAttempt;
                if (retries > 0) {
                    log.info((Object)(callData.username + " retrying callback with same route. ID: " + callData.id + ". Source: " + callData.source + ". Destination: " + callData.destination + ". Attempt: " + callParam.retryAttempt + "."));
                } else if (retries == 0) {
                    log.info((Object)(callData.username + " retrying callback with next provider. ID: " + callData.id + ". Source: " + callData.source + ". Destination: " + callData.destination + ". Attempt: " + callParam.retryAttempt + "."));
                }
            }
            if (retries == 0 && callParam.retryAttempt > 0) {
                int isdnCause;
                int n = isdnCause = callData.failReasonCode != null ? callData.failReasonCode : 16;
                if (!this.shouldRetry(isdnCause)) {
                    throw new Exception(callData.username + " exhausted all retries - " + callData.failReason);
                }
            }
            List<VoiceRouteData> routes = this.routingTable.getSourceRoutes(callData);
            if (retries == 0 && callParam.retryAttempt > 0) {
                VoiceRouteData nextRoute = this.routingTable.getSourceNextRoute(callData, callData.gateway, callData.sourceProvider);
                if (nextRoute == null || nextRoute.providerID == Integer.MIN_VALUE) {
                    log.warn((Object)("Exhausted all callback routes for source failover. ID: " + callData.id + ". Source: " + callData.source + ". Destination: " + callData.destination + ". Attempt: " + callParam.retryAttempt + "."));
                    throw new Exception(callData.username + " exhausted all retries - " + callData.failReason);
                }
                routes.add(0, nextRoute);
            }
            for (VoiceRouteData route : routes) {
                VoiceRouteData dRoute = this.routingTable.getDestinationRoute(callData, route.gatewayID);
                VoiceRouteData dNextRoute = this.routingTable.getDestinationNextRoute(callData, dRoute.gatewayID, dRoute.providerID);
                if (dNextRoute == null || dNextRoute.providerID == Integer.MIN_VALUE) {
                    log.warn((Object)("Exhausted all callback routes for destination failover. ID: " + callData.id + ". Source: " + callData.source + ". Destination: " + callData.destination + ". Attempt: " + callParam.retryAttempt + "."));
                    dNextRoute = new VoiceRouteData();
                    dNextRoute.gatewayID = Integer.MIN_VALUE;
                    dNextRoute.providerID = Integer.MIN_VALUE;
                    dNextRoute.dialCommand = "";
                }
                callData.gateway = route.gatewayID;
                callData.sourceProvider = route.providerID;
                callData.destinationProvider = dRoute.providerID;
                callData.destinationFirstProvider = dRoute.providerID;
                callData.destinationNextProvider = dNextRoute.providerID;
                callData.sourceDialCommand = route.dialCommand;
                callData.destinationDialCommand = dRoute.dialCommand;
                callData.destinationFirstDialCommand = dRoute.dialCommand;
                callData.destinationNextDialCommand = dNextRoute.dialCommand;
                callData.maxDuration = maxDuration;
                callData = this.gateways.get(route.gatewayID).requestCallback(callData, callParam.retryAttempt > 0, false);
                if (callData.status == CallData.StatusEnum.IN_PROGRESS) {
                    log.info((Object)("Successfully initiated a callback. ID: " + callData.id + ". Gateway: " + callData.gateway + ". SP: " + callData.sourceProvider + ". DP: " + callData.destinationProvider + ". DNP: " + callData.destinationNextProvider + "."));
                    return callData.toIceObject();
                }
                log.warn((Object)("Trying next route for callback. ID: " + callData.id + ". Gateway: " + callData.gateway + ". SP: " + callData.sourceProvider + ". DP: " + callData.destinationProvider + ". DNP: " + callData.destinationNextProvider + ". Error: " + callData.failReason + "."));
            }
            throw new Exception(callData.failReason);
        }
        catch (Exception e) {
            log.warn((Object)("Callback request failed. ID: " + call.id + ". Error: " + e.getMessage() + "."));
            if (userAdded) {
                this.usersInCall.remove(call.username);
            }
            this.callParams.remove(call.id);
            FusionException fe = new FusionException();
            fe.message = e.getMessage();
            throw fe;
        }
    }

    public CallData requestCall(CallData callData, int gatewayId) throws Exception {
        boolean userAdded = false;
        try {
            if (callData == null) {
                throw new Exception("Invalid or null call data for request call");
            }
            if (callData.username == null) {
                throw new Exception("Invalid or null username for request call");
            }
            log.info((Object)(callData.username + " requesting a request call. ID: " + callData.id + ". Source: " + callData.source + ". Destination: " + callData.destination + "."));
            AsteriskGateway gateway = this.gateways.get(gatewayId);
            if (gateway == null) {
                throw new Exception("Invalid gateway ID " + gatewayId + " for request call");
            }
            callData.gateway = gateway.getData().id;
            userAdded = this.usersInCall.add(callData.username);
            if (!userAdded) {
                throw new Exception(callData.username + " already in another call for request call");
            }
            if (callData.destination != null && callData.destinationIDDCode != null) {
                VoiceRouteData dRoute = this.routingTable.getDestinationRoute(callData, gatewayId);
                VoiceRouteData dNextRoute = this.routingTable.getDestinationNextRoute(callData, dRoute.gatewayID, dRoute.providerID);
                if (dNextRoute == null || dNextRoute.providerID == Integer.MIN_VALUE) {
                    log.warn((Object)("Exhausted all request call routes for destination failover. ID: " + callData.id + ". Source: " + callData.source + ". Destination: " + callData.destination + "."));
                    dNextRoute = new VoiceRouteData();
                    dNextRoute.gatewayID = Integer.MIN_VALUE;
                    dNextRoute.providerID = Integer.MIN_VALUE;
                    dNextRoute.dialCommand = "";
                }
                callData.destinationProvider = dRoute.providerID;
                callData.destinationFirstProvider = dRoute.providerID;
                callData.destinationNextProvider = dNextRoute.providerID;
                callData.destinationDialCommand = dRoute.dialCommand;
                callData.destinationFirstDialCommand = dRoute.dialCommand;
                callData.destinationNextDialCommand = dNextRoute.dialCommand;
            }
            callData = gateway.requestCall(callData, false);
            if (callData.status == CallData.StatusEnum.IN_PROGRESS) {
                log.info((Object)("Successfully initiated a request call. ID: " + callData.id + ". Gateway: " + callData.gateway + ". SP: " + callData.sourceProvider + ". DP: " + callData.destinationProvider + ". DNP: " + callData.destinationNextProvider + "."));
                return callData;
            }
            throw new Exception(callData.failReason);
        }
        catch (Exception e) {
            log.warn((Object)("Request call request failed. ID: " + callData.id + ". Error: " + e.getMessage() + "."));
            if (userAdded) {
                this.usersInCall.remove(callData.username);
            }
            throw e;
        }
    }

    public void cancelCall(CallData callData) {
        AsteriskGateway gateway;
        if (callData.gateway != null && (gateway = this.gateways.get(callData.gateway)) != null) {
            gateway.cancelCall(callData);
            this.usersInCall.remove(callData.username);
        }
    }

    public void callCompleted(CallData callData, boolean retry) {
        CallParam callParam;
        if (retry && (callData.initialLeg == CallData.InitialLegEnum.SOURCE && (callData.sourceDuration == null || callData.sourceDuration == 0L) || callData.initialLeg == CallData.InitialLegEnum.DESTINATION && (callData.destinationDuration == null || callData.destinationDuration == 0L)) && (callParam = this.callParams.get(callData.id)) != null && callParam.retries > 0) {
            try {
                this.requestCallback(callData.toIceObject(), callParam.maxDuration, --callParam.retries);
                return;
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        this.callsCompleted.add(callData);
    }

    public CallRequest fillInCallRequest(CallData callData, int gatewayId) {
        try {
            if (callData == null) {
                log.warn((Object)"Invalid or null call data for call request.");
                return null;
            }
            if (callData.username == null) {
                log.warn((Object)("Invalid or null username for call request. ID: " + callData.id + "."));
                return null;
            }
            log.info((Object)(callData.username + " filling in call request. ID: " + callData.id + ". Source: " + callData.source + ". Destination: " + callData.destination + "."));
            AsteriskGateway gateway = this.gateways.get(gatewayId);
            if (gateway == null) {
                log.warn((Object)("Invalid gateway ID " + gatewayId + " for call request. ID: " + callData.id + "."));
                return null;
            }
            callData.gateway = gateway.getData().id;
            String dialCommand = null;
            String dialNextCommand = null;
            if (callData.destination != null && callData.destinationIDDCode != null) {
                VoiceRouteData dRoute = this.routingTable.getDestinationRoute(callData, gatewayId);
                VoiceRouteData dNextRoute = this.routingTable.getDestinationNextRoute(callData, dRoute.gatewayID, dRoute.providerID);
                if (dNextRoute == null || dNextRoute.providerID == Integer.MIN_VALUE) {
                    log.warn((Object)("Exhausted all request call routes for destination failover. ID: " + callData.id + ". Source: " + callData.source + ". Destination: " + callData.destination + "."));
                    dNextRoute = new VoiceRouteData();
                    dNextRoute.gatewayID = Integer.MIN_VALUE;
                    dNextRoute.providerID = Integer.MIN_VALUE;
                    dNextRoute.dialCommand = "";
                }
                callData.destinationProvider = dRoute.providerID;
                callData.destinationFirstProvider = dRoute.providerID;
                callData.destinationNextProvider = dNextRoute.providerID;
                callData.destinationDialCommand = dRoute.dialCommand;
                callData.destinationFirstDialCommand = dRoute.dialCommand;
                callData.destinationNextDialCommand = dNextRoute.dialCommand;
                dialCommand = dRoute.dialCommand != null ? dRoute.dialCommand.replaceAll("%n", callData.destination) : null;
                dialNextCommand = dNextRoute.dialCommand != null ? dNextRoute.dialCommand.replaceAll("%n", callData.destination) : null;
            }
            CallRequest callRequest = new CallRequest(callData, dialCommand, dialNextCommand, (long)callData.maxCallDuration * 1000L, callData.rate != null ? callData.rate : 0.0, gateway.getData().timeoutWarning != null ? (long)gateway.getData().timeoutWarning.intValue() : 0L, gateway.getData().timeoutWarningRepeat != null ? (long)gateway.getData().timeoutWarningRepeat.intValue() : 0L, callData.id != null ? callData.id : 0);
            log.debug((Object)("Successfully filled in call request. ID: " + callData.id + "."));
            return callRequest;
        }
        catch (Exception e) {
            log.warn((Object)("Fill in call request failed. ID: " + callData.id + ". Error: " + e.getMessage() + "."));
            return null;
        }
    }

    @Override
    public void run() {
        AccountEntrySourceData accountEntrySourceData = new AccountEntrySourceData(VoiceEngine.class);
        block7: while (true) {
            Voice voiceEJB;
            try {
                Thread.sleep(1000L);
            }
            catch (Exception e) {
                // empty catch block
            }
            try {
                voiceEJB = (Voice)EJBHomeCache.getObject("ejb/Voice", VoiceHome.class);
            }
            catch (Exception e) {
                log.warn((Object)("Failed to create Voice EJB for charging calls - " + e.getMessage() + "."));
                continue;
            }
            while (true) {
                if (this.callsCompleted.isEmpty()) continue block7;
                CallData callData = this.callsCompleted.get(0);
                try {
                    voiceEJB.chargeCall(callData, accountEntrySourceData);
                    this.callsCompleted.remove(callData);
                    this.usersInCall.remove(callData.username);
                    this.callParams.remove(callData.id);
                    log.info((Object)("Call completed. ID: " + callData.id + ". Source Duration: " + callData.sourceDuration + ". Destination Duration: " + callData.destinationDuration + "."));
                }
                catch (RemoteException e) {
                    log.warn((Object)("Failed to charge " + callData.username + " for call ID " + callData.id + " - " + RMIExceptionHelper.getRootMessage(e) + "."));
                    continue block7;
                }
                catch (Exception e) {
                    log.warn((Object)("Failed to charge " + callData.username + " for call ID " + callData.id + " - " + e.getMessage() + "."));
                    continue block7;
                }
            }
            break;
        }
    }

    private class CallParam {
        public int maxDuration;
        public int retries;
        public int retryAttempt;

        public CallParam(int maxDuration, int retries) {
            this.maxDuration = maxDuration;
            this.retries = retries;
        }
    }
}

