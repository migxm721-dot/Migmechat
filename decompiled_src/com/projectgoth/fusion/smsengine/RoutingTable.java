/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.smsengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.SMSGatewayData;
import com.projectgoth.fusion.data.SMSRouteData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.smsengine.HTTPGateway;
import com.projectgoth.fusion.smsengine.SMPPGateway;
import com.projectgoth.fusion.smsengine.SMSGateway;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RoutingTable {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RoutingTable.class));
    private static Map<Integer, SMSGateway> gateways = null;
    private static Map<String, List<SMSRouteData>> routes = null;
    private static Map<Integer, Set<String>> areaCodes = null;

    private static String getAreaCode(Integer iddCode, String phoneNumber) {
        String areaCode = "";
        Set<String> codes = areaCodes.get(iddCode);
        if (codes != null) {
            phoneNumber = phoneNumber.substring(iddCode.toString().length());
            for (String code : codes) {
                if (!phoneNumber.startsWith(code) || code.length() <= areaCode.length()) continue;
                areaCode = code;
            }
        }
        return areaCode;
    }

    private static String constructKey(SMSRouteData.TypeEnum type, int iddCode, String areaCode) {
        return type.toString() + ";" + Integer.toString(iddCode) + ";" + areaCode;
    }

    public static synchronized void load() throws CreateException, RemoteException {
        Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
        List gatewayList = messageEJB.getSMSGateways();
        if (gatewayList == null || gatewayList.size() == 0) {
            log.warn((Object)"No SMS routing table in database");
            return;
        }
        gateways = new ConcurrentHashMap<Integer, SMSGateway>();
        routes = new ConcurrentHashMap<String, List<SMSRouteData>>();
        areaCodes = new ConcurrentHashMap<Integer, Set<String>>();
        for (SMSGatewayData sMSGatewayData : gatewayList) {
            if (sMSGatewayData.type == SMSGatewayData.TypeEnum.HTTP) {
                gateways.put(sMSGatewayData.id, new HTTPGateway(sMSGatewayData));
            } else if (sMSGatewayData.type == SMSGatewayData.TypeEnum.SMPP_TRANSMITTER || sMSGatewayData.type == SMSGatewayData.TypeEnum.SMPP_TRANSCEIVER) {
                gateways.put(sMSGatewayData.id, new SMPPGateway(sMSGatewayData));
            } else {
                log.warn((Object)("Gateway " + sMSGatewayData.id + " is using unsupported type " + (Object)((Object)sMSGatewayData.type)));
                continue;
            }
            for (SMSRouteData routeData : sMSGatewayData.smsRoutes) {
                String key = RoutingTable.constructKey(routeData.type, routeData.iddCode, routeData.areaCode);
                List<SMSRouteData> route = routes.get(key);
                if (route == null) {
                    route = new LinkedList<SMSRouteData>();
                    route.add(routeData);
                    routes.put(key, route);
                } else {
                    route.add(routeData);
                }
                if (routeData.areaCode.length() <= 0) continue;
                Set<String> codes = areaCodes.get(routeData.iddCode);
                if (codes == null) {
                    codes = new HashSet<String>();
                    areaCodes.put(routeData.iddCode, codes);
                }
                codes.add(routeData.areaCode);
            }
        }
        for (SMSGateway sMSGateway : gateways.values()) {
            log.debug((Object)sMSGateway);
        }
        for (List list : routes.values()) {
            for (SMSRouteData route : list) {
                log.debug((Object)route);
            }
        }
    }

    public static List<SMSRouteData> getRoutes(SMSRouteData.TypeEnum type, int iddCode, String phoneNumber) {
        if (routes == null) {
            return Collections.EMPTY_LIST;
        }
        List<SMSRouteData> list = routes.get(RoutingTable.constructKey(type, iddCode, RoutingTable.getAreaCode(iddCode, phoneNumber)));
        if (list == null && (list = routes.get(RoutingTable.constructKey(type, iddCode, ""))) == null) {
            return Collections.EMPTY_LIST;
        }
        list = new LinkedList<SMSRouteData>(list);
        Collections.shuffle(list);
        Collections.sort(list);
        return list;
    }

    public static SMSGateway getGateway(int id) {
        return gateways == null ? null : gateways.get(id);
    }
}

