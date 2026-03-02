/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.smsengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.SMSRouteData;
import com.projectgoth.fusion.data.SystemSMSData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.smsengine.DispatchStatus;
import com.projectgoth.fusion.smsengine.MessageHistory;
import com.projectgoth.fusion.smsengine.RoutingTable;
import com.projectgoth.fusion.smsengine.SMSEngine;
import com.projectgoth.fusion.smsengine.SMSGateway;
import com.projectgoth.fusion.smsengine.SMSMessage;
import java.util.Iterator;
import java.util.List;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class DispatchThread
implements Runnable {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DispatchThread.class));
    private SMSEngine engine;
    private List<SMSMessage> messages;
    private Message messageEJB;

    public DispatchThread(SMSEngine engine, MessageData messageData) {
        this.engine = engine;
        this.messages = SMSMessage.parse(messageData);
    }

    public DispatchThread(SMSEngine engine, SystemSMSData systemSMSData) {
        this.engine = engine;
        this.messages = SMSMessage.parse(systemSMSData);
    }

    public void run() {
        try {
            this.messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            for (SMSMessage message : this.messages) {
                Integer iddCode = message.getIDDCode();
                if (iddCode == null) {
                    log.warn((Object)("Invalid " + message.getShortDescription() + " - No IDD code"));
                    continue;
                }
                SMSRouteData.TypeEnum type = message.getType();
                if (type == null) {
                    log.warn((Object)("Invalid " + message.getShortDescription() + " - SMS type not specified"));
                    continue;
                }
                List<SMSRouteData> routes = RoutingTable.getRoutes(type, iddCode, message.getDestination());
                if (routes == null || routes.size() == 0) {
                    log.warn((Object)("No route(s) available for phone number " + message.getDestination()));
                    this.updateDispatchingStatus(message, null, new DispatchStatus(DispatchStatus.StatusEnum.FAILED, message));
                    continue;
                }
                if (!MessageHistory.add(message.getKey())) {
                    log.warn((Object)("Discarding duplicated " + message.getShortDescription()));
                    continue;
                }
                Iterator<SMSRouteData> iterator = routes.iterator();
                DispatchStatus result = new DispatchStatus(DispatchStatus.StatusEnum.TRY_OTHER_GATEWAYS, message);
                while (iterator.hasNext()) {
                    SMSRouteData route = iterator.next();
                    SMSGateway gateway = RoutingTable.getGateway(route.gatewayID);
                    if (gateway == null) {
                        log.warn((Object)("Unable to locate SMS gateway " + route.gatewayID));
                        continue;
                    }
                    try {
                        result = gateway.dispatchMessage(message);
                        if (result.status == DispatchStatus.StatusEnum.TRY_OTHER_GATEWAYS) continue;
                        this.updateDispatchingStatus(message, gateway, result);
                        break;
                    }
                    catch (Exception e) {
                        log.error((Object)"exception while dispatching message", (Throwable)e);
                    }
                }
                if (result.status != DispatchStatus.StatusEnum.TRY_OTHER_GATEWAYS) continue;
                MessageHistory.remove(message.getKey());
            }
        }
        catch (CreateException e) {
            log.warn((Object)("Unable to create Message EJB - " + e.getMessage()), (Throwable)e);
        }
    }

    private void updateDispatchingStatus(SMSMessage message, SMSGateway gateway, DispatchStatus result) {
        try {
            Integer gatewayID;
            SMSRouteData.TypeEnum type = message.getType();
            Integer n = gatewayID = gateway == null ? null : gateway.getID();
            if (result.status == DispatchStatus.StatusEnum.SUCCEEDED) {
                this.engine.onMessageSent();
                if (type == SMSRouteData.TypeEnum.USER_SMS) {
                    this.messageEJB.changePendingMessageToSent(MessageType.SMS, message.getParentID(), message.getID(), gatewayID, message.getIDDCode(), message.getDestination(), result.transactionID);
                } else {
                    this.messageEJB.changePendingSystemSMSToSent(message.getType().value(), message.getID(), gatewayID, result.source, message.getIDDCode(), message.getDestination(), result.transactionID, result.billed);
                }
            } else if (result.status == DispatchStatus.StatusEnum.FAILED) {
                if (type == SMSRouteData.TypeEnum.USER_SMS) {
                    this.messageEJB.smsFailed(message.getID(), gatewayID, message.getUsername(), new AccountEntrySourceData(SMSEngine.class));
                } else {
                    this.messageEJB.systemSMSFailed(message.getID(), gatewayID, result.source);
                }
            }
        }
        catch (Exception e) {
            log.error((Object)("Failed to update dispatching status of " + message.getShortDescription() + " to " + result + " - " + e.getMessage()));
        }
    }
}

