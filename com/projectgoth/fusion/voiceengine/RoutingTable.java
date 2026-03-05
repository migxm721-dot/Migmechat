/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.voiceengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.CallData;
import com.projectgoth.fusion.data.VoiceRouteData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RoutingTable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RoutingTable.class));
    private Map<Integer, List<VoiceRouteData>> countryRoutes = new ConcurrentHashMap<Integer, List<VoiceRouteData>>();
    private Map<Integer, Set<String>> areaCodes = new ConcurrentHashMap<Integer, Set<String>>();

    private String getAreaCode(Integer iddCode, String phoneNumber) {
        String areaCode = "";
        Set<String> codes = this.areaCodes.get(iddCode);
        if (codes != null) {
            phoneNumber = phoneNumber.substring(iddCode.toString().length());
            for (String code : codes) {
                if (!phoneNumber.startsWith(code) || code.length() <= areaCode.length()) continue;
                areaCode = code;
            }
        }
        return areaCode;
    }

    private List<VoiceRouteData> getRoutes(Integer iddCode, String phoneNumber) throws Exception {
        String areaCode = this.getAreaCode(iddCode, phoneNumber);
        ArrayList<VoiceRouteData> result = new ArrayList<VoiceRouteData>();
        List<VoiceRouteData> routes = this.countryRoutes.get(iddCode);
        if (routes != null) {
            for (VoiceRouteData route : routes) {
                if (!areaCode.equals(route.areaCode)) continue;
                result.add(route);
            }
        }
        if (result.size() == 0) {
            if (areaCode == null) {
                throw new Exception("No route(s) available for IDD code " + iddCode);
            }
            throw new Exception("No route(s) available for IDD code " + iddCode + ", area code " + areaCode);
        }
        return result;
    }

    public void add(VoiceRouteData route) {
        List<VoiceRouteData> routes;
        if (route.areaCode.length() > 0) {
            Set<String> codes = this.areaCodes.get(route.iddCode);
            if (codes == null) {
                codes = new HashSet<String>();
                this.areaCodes.put(route.iddCode, codes);
            }
            codes.add(route.areaCode);
        }
        if ((routes = this.countryRoutes.get(route.iddCode)) == null) {
            routes = new ArrayList<VoiceRouteData>();
            this.countryRoutes.put(route.iddCode, routes);
        }
        routes.add(route);
    }

    public void add(Collection<VoiceRouteData> routes) {
        if (routes != null) {
            for (VoiceRouteData route : routes) {
                this.add(route);
            }
        }
    }

    public List<VoiceRouteData> getSourceRoutes(CallData callData) throws Exception {
        if (callData.sourceType == CallData.SourceDestinationTypeEnum.PSTN_PHONE) {
            if (callData.sourceIDDCode == null) {
                throw new Exception("Source IDD Code must be specified");
            }
            if (callData.source == null) {
                throw new Exception("Source must be specified");
            }
            List<VoiceRouteData> routes = this.getRoutes(callData.sourceIDDCode, callData.source);
            routes = new ArrayList<VoiceRouteData>(routes);
            Collections.shuffle(routes);
            Collections.sort(routes);
            if (routes != null && routes.size() > 0) {
                return routes;
            }
            Exception e = new Exception("No source route(s) available to source " + callData.source);
            log.error((Object)e.getMessage());
            throw e;
        }
        if (callData.sourceType == CallData.SourceDestinationTypeEnum.MIG33_USER) {
            VoiceRouteData route = new VoiceRouteData();
            route.gatewayID = callData.gateway;
            route.dialCommand = (Object)((Object)callData.sourceProtocol) + "/%n";
            route.priority = 1;
            ArrayList<VoiceRouteData> routes = new ArrayList<VoiceRouteData>();
            routes.add(route);
            return routes;
        }
        throw new Exception("Unsupported source type " + (Object)((Object)callData.sourceType));
    }

    public VoiceRouteData getSourceNextRoute(CallData callData, int gatewayId, int lastProviderId) throws Exception {
        if (callData.sourceType == CallData.SourceDestinationTypeEnum.PSTN_PHONE) {
            if (callData.sourceIDDCode == null) {
                throw new Exception("Source IDD Code must be specified");
            }
            if (callData.source == null) {
                throw new Exception("Source must be specified");
            }
            List<VoiceRouteData> routes = this.getRoutes(callData.sourceIDDCode, callData.source);
            routes = new ArrayList<VoiceRouteData>(routes);
            Collections.shuffle(routes);
            Collections.sort(routes);
            boolean found = false;
            for (VoiceRouteData route : routes) {
                if (route.gatewayID != gatewayId) continue;
                if (route.providerID == lastProviderId) {
                    found = true;
                    continue;
                }
                if (!found) continue;
                return route;
            }
            if (found) {
                return null;
            }
            Exception e = new Exception("No more source route(s) available on voice gateway " + gatewayId + " to source " + callData.source);
            log.error((Object)e.getMessage());
            throw e;
        }
        if (callData.sourceType == CallData.SourceDestinationTypeEnum.MIG33_USER) {
            return null;
        }
        throw new Exception("Unsupported source type " + (Object)((Object)callData.sourceType));
    }

    public VoiceRouteData getDestinationRoute(CallData callData, int gatewayId) throws Exception {
        if (callData.destinationType == CallData.SourceDestinationTypeEnum.PSTN_PHONE) {
            if (callData.destinationIDDCode == null) {
                throw new Exception("Destination IDD Code must be specified");
            }
            if (callData.destination == null) {
                throw new Exception("Destination must be specified");
            }
            List<VoiceRouteData> routes = this.getRoutes(callData.destinationIDDCode, callData.destination);
            routes = new ArrayList<VoiceRouteData>(routes);
            Collections.shuffle(routes);
            Collections.sort(routes);
            for (VoiceRouteData route : routes) {
                if (route.gatewayID != gatewayId) continue;
                return route;
            }
            Exception e = new Exception("No destination route(s) available on voice gateway " + gatewayId + " to destination " + callData.destination);
            log.error((Object)e.getMessage());
            throw e;
        }
        if (callData.destinationType == CallData.SourceDestinationTypeEnum.MIG33_USER) {
            VoiceRouteData route = new VoiceRouteData();
            route.gatewayID = callData.gateway;
            route.dialCommand = (Object)((Object)callData.destinationProtocol) + "/%n";
            route.priority = 1;
            return route;
        }
        throw new Exception("Unsupported destination type " + (Object)((Object)callData.destinationType));
    }

    public VoiceRouteData getDestinationNextRoute(CallData callData, int gatewayId, int lastProviderId) throws Exception {
        if (callData.destinationType == CallData.SourceDestinationTypeEnum.PSTN_PHONE) {
            if (callData.destinationIDDCode == null) {
                throw new Exception("Destination IDD Code must be specified");
            }
            if (callData.destination == null) {
                throw new Exception("Destination must be specified");
            }
            List<VoiceRouteData> routes = this.getRoutes(callData.destinationIDDCode, callData.destination);
            routes = new ArrayList<VoiceRouteData>(routes);
            Collections.shuffle(routes);
            Collections.sort(routes);
            boolean found = false;
            for (VoiceRouteData route : routes) {
                if (route.gatewayID != gatewayId) continue;
                if (route.providerID == lastProviderId) {
                    found = true;
                    continue;
                }
                if (!found) continue;
                return route;
            }
            if (found) {
                return null;
            }
            Exception e = new Exception("No more destination route(s) available on voice gateway " + gatewayId + " to destination " + callData.destination);
            log.error((Object)e.getMessage());
            throw e;
        }
        if (callData.destinationType == CallData.SourceDestinationTypeEnum.MIG33_USER) {
            return null;
        }
        throw new Exception("Unsupported destination type " + (Object)((Object)callData.destinationType));
    }

    public void print() {
        int count = 0;
        for (List<VoiceRouteData> l : this.countryRoutes.values()) {
            for (VoiceRouteData r : l) {
                if (r.areaCode.length() == 0) {
                    log.debug((Object)("IDD = " + r.iddCode + ", gateway = " + r.gatewayID + ", provider = " + r.providerID + ", priority = " + r.priority));
                } else {
                    log.debug((Object)("IDD = " + r.iddCode + ", area code = " + r.areaCode + ", gateway = " + r.gatewayID + ", provider = " + r.providerID + ", priority = " + r.priority));
                }
                ++count;
            }
        }
        log.debug((Object)(count + " routes"));
    }
}

