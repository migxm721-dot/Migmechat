/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.projectgoth.fusion.restapi.data;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.restapi.data.Alert;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@XmlAccessorType(value=XmlAccessType.NONE)
@XmlRootElement(name="alerts")
public class UserAlerts {
    @XmlElement
    public int unread;
    @XmlElement
    public Map<Integer, Map<String, Map<String, String>>> alerts;
    @XmlElement
    public int offset;
    @XmlElement
    public int total;

    public UserAlerts() {
        this.alerts = new HashMap<Integer, Map<String, Map<String, String>>>();
    }

    public UserAlerts(UserAlerts a) {
        this.unread = a.unread;
        this.alerts = a.alerts;
        this.offset = a.offset;
        this.total = a.total;
    }

    public UserAlerts(int unread, Map<Integer, Map<String, Map<String, String>>> alerts) {
        this.unread = unread;
        this.alerts = alerts;
    }

    public UserAlerts retrievePage(int offset, int limit) {
        UserAlerts page = new UserAlerts(this);
        if (this.alerts == null) {
            return page;
        }
        if (this.alerts.size() == 0) {
            return page;
        }
        List<Alert> allAlerts = this.retrieveAlertsAsList();
        if (offset > allAlerts.size()) {
            return new UserAlerts();
        }
        if (offset + limit > allAlerts.size()) {
            limit = allAlerts.size() - offset;
        }
        List<Alert> sublist = allAlerts.subList(offset, offset + limit);
        HashMap<Integer, Map<String, Map<String, String>>> finalAlerts = new HashMap<Integer, Map<String, Map<String, String>>>();
        for (Alert a : sublist) {
            Integer alertType = a.alertType;
            if (!finalAlerts.containsKey(alertType)) {
                finalAlerts.put(alertType, new HashMap());
            }
            Map map = (Map)finalAlerts.get(alertType);
            map.put(a.alertKey, a.alertData);
        }
        page.offset = offset;
        page.total = allAlerts.size();
        page.alerts = finalAlerts;
        return page;
    }

    public List<Alert> retrieveAlertsAsList() {
        LinkedList<Alert> allAlerts = new LinkedList<Alert>();
        for (Map.Entry<Integer, Map<String, Map<String, String>>> e : this.alerts.entrySet()) {
            Integer alertType = e.getKey();
            for (Map.Entry<String, Map<String, String>> e2 : e.getValue().entrySet()) {
                String alertKey = e2.getKey();
                String timestamp = e2.getValue().get("timestamp");
                if (StringUtil.toLongOrDefault(timestamp, -1L) <= 0L) continue;
                Alert alert = new Alert(timestamp, alertType, alertKey, e2.getValue());
                allAlerts.add(alert);
            }
        }
        Collections.sort(allAlerts);
        return allAlerts;
    }
}

