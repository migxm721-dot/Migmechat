/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Util
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.voiceengine;

import Ice.Application;
import Ice.ObjectAdapter;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RMIExceptionHelper;
import com.projectgoth.fusion.data.CallData;
import com.projectgoth.fusion.data.VoiceGatewayData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Voice;
import com.projectgoth.fusion.interfaces.VoiceHome;
import com.projectgoth.fusion.voiceengine.AsteriskGateway;
import com.projectgoth.fusion.voiceengine.CallMakerI;
import com.projectgoth.fusion.voiceengine.FastAGIServer;
import com.projectgoth.fusion.voiceengine.RoutingTable;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import javax.ejb.CreateException;
import javax.naming.AuthenticationException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class VoiceEngine
extends Application {
    private static final String APP_NAME = "VoiceEngine";
    private static final String CONFIG_FILE = "VoiceEngine.cfg";
    private Map<Integer, AsteriskGateway> gateways;
    private RoutingTable routingTable;
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(VoiceEngine.class));

    private Map<Integer, AsteriskGateway> loadGatewaysFromDB(int variableFormat) throws CreateException, RemoteException {
        List callsInProgress;
        ConcurrentHashMap<Integer, AsteriskGateway> gateways = new ConcurrentHashMap<Integer, AsteriskGateway>();
        Voice voiceEJB = (Voice)EJBHomeCache.getObject("ejb/Voice", VoiceHome.class);
        List gatewayData = voiceEJB.getVoiceGateways();
        if (gatewayData != null) {
            for (VoiceGatewayData data : gatewayData) {
                gateways.put(data.id, new AsteriskGateway(data, variableFormat));
            }
        }
        if ((callsInProgress = voiceEJB.getCallEntries(null, CallData.StatusEnum.IN_PROGRESS.value())) != null) {
            for (CallData callData : callsInProgress) {
                if (callData.gateway == null) {
                    log.warn((Object)("Unable to add call in progress. ID: " + callData.id + ". Gateway: " + callData.gateway));
                    continue;
                }
                AsteriskGateway gateway = (AsteriskGateway)gateways.get(callData.gateway);
                if (gateway == null) {
                    log.warn((Object)("Unable to add call in progress. ID: " + callData.id + ". Gateway: " + callData.gateway));
                    continue;
                }
                gateway.addCallInProgress(callData);
            }
        }
        return gateways;
    }

    private RoutingTable buildRoutingTable(Collection<AsteriskGateway> gateways) {
        RoutingTable routingTable = new RoutingTable();
        if (gateways != null) {
            for (AsteriskGateway gateway : gateways) {
                routingTable.add(gateway.getData().voiceRoutes);
            }
        }
        routingTable.print();
        return routingTable;
    }

    private void pingAsteriskGateways() {
        for (AsteriskGateway gateway : this.gateways.values()) {
            try {
                log.debug((Object)("Pinging gateway " + gateway.getData().id));
                gateway.ping();
            }
            catch (Exception e) {
                log.warn((Object)(e.getClass().getName() + " occured while ping gateway " + gateway.getData().id + " - " + e.getMessage()));
            }
        }
    }

    public int run(String[] arg0) {
        try {
            int variableFormat = VoiceEngine.communicator().getProperties().getPropertyAsIntWithDefault("VariableFormat", 1);
            this.gateways = this.loadGatewaysFromDB(variableFormat);
            this.routingTable = this.buildRoutingTable(this.gateways.values());
            CallMakerI callMaker = new CallMakerI(this.gateways, this.routingTable);
            for (AsteriskGateway gateway : this.gateways.values()) {
                try {
                    gateway.setCallMaker(callMaker);
                    gateway.connect();
                }
                catch (AuthenticationException e) {
                    log.warn((Object)("Unable to start gateway " + gateway.getData().id + " - " + e.getMessage()));
                }
                catch (IOException e) {
                    log.warn((Object)("Unable to start gateway " + gateway.getData().id + " - " + e.getMessage()));
                }
            }
            long keepAliveInterval = VoiceEngine.communicator().getProperties().getPropertyAsIntWithDefault("KeepAliveInterval", 60) * 1000;
            if (keepAliveInterval > 0L) {
                new Timer().schedule(new TimerTask(){

                    public void run() {
                        VoiceEngine.this.pingAsteriskGateways();
                    }
                }, keepAliveInterval, keepAliveInterval);
            }
            ObjectAdapter callMakerAdaptor = VoiceEngine.communicator().createObjectAdapter("CallMakerAdapter");
            callMakerAdaptor.add((Ice.Object)callMaker, Util.stringToIdentity((String)"CallMaker"));
            callMakerAdaptor.activate();
            int fastAGIPort = VoiceEngine.communicator().getProperties().getPropertyAsIntWithDefault("FastAGIPort", 4573);
            int fastAGIThreads = VoiceEngine.communicator().getProperties().getPropertyAsIntWithDefault("FastAGIThreads", 20);
            int callThroughValidPeriod = VoiceEngine.communicator().getProperties().getPropertyAsIntWithDefault("CallThroughValidPeriod", 300) * 1000;
            FastAGIServer fastAGIServer = new FastAGIServer(callMaker, fastAGIPort, fastAGIThreads);
            fastAGIServer.setCallThroughValidPeriod(callThroughValidPeriod);
            fastAGIServer.start();
            log.info((Object)"Ready to receive call requests");
            VoiceEngine.communicator().waitForShutdown();
        }
        catch (CreateException e) {
            log.fatal((Object)"Failed to load voice gateways and calls in progress from database", (Throwable)e);
        }
        catch (RemoteException e) {
            log.fatal((Object)("EJB excepion - " + RMIExceptionHelper.getRootMessage(e)), (Throwable)e);
        }
        catch (IOException e) {
            log.fatal((Object)"IO exception", (Throwable)e);
        }
        return 0;
    }

    public static void main(String[] args) {
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        log.info((Object)"VoiceEngine version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        VoiceEngine voiceEngine = new VoiceEngine();
        String configFile = args.length > 0 ? args[0] : CONFIG_FILE;
        int status = voiceEngine.main(((Object)((Object)voiceEngine)).getClass().getName(), args, configFile);
        System.exit(status);
    }
}

