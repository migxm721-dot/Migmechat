/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.annotation.Required
 */
package com.projectgoth.fusion.bl1;

import Ice.Application;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.bl1.BlueLabelServiceAdminI;
import com.projectgoth.fusion.bl1.BlueLabelServiceI;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.stats.IceStats;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class BlueLabelService
extends Application {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BlueLabelService.class));
    public static ObjectAdapter BlueLabelServiceAdapter = null;
    public static Properties properties = null;
    public static long startTime = System.currentTimeMillis();
    private String hostName;
    private BlueLabelServiceI blueLabelServiceServant;

    @Required
    public void setBlueLabelServiceServant(BlueLabelServiceI blueLabelServiceServant) {
        this.blueLabelServiceServant = blueLabelServiceServant;
    }

    private void configureServant(Properties properties) {
        this.blueLabelServiceServant.setCreateAccountEntries(Boolean.parseBoolean(properties.getPropertyWithDefault("createAccountEntries", "false")));
        this.blueLabelServiceServant.setMibliUsername(properties.getProperty("mibli.username"));
        this.blueLabelServiceServant.setMibliPassword(properties.getProperty("mibli.password"));
        this.blueLabelServiceServant.setMibliWSDLURL(properties.getProperty("mibli.wsdlURL"));
    }

    public int run(String[] arg0) {
        properties = BlueLabelService.communicator().getProperties();
        this.configureServant(properties);
        try {
            this.blueLabelServiceServant.configureService();
        }
        catch (Exception e) {
            log.fatal((java.lang.Object)"failed to configure bluelabel service servant", (Throwable)e);
            BlueLabelService.communicator().shutdown();
            return 1;
        }
        log.info((java.lang.Object)("Configured endpoint [" + properties.getProperty("BlueLabelServiceAdapter.Endpoints") + "]"));
        try {
            this.hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            this.hostName = "UNKNOWN";
        }
        log.debug((java.lang.Object)"Initialising BlueLabelService interface");
        BlueLabelServiceAdapter = BlueLabelService.communicator().createObjectAdapter("BlueLabelServiceAdapter");
        BlueLabelServiceAdapter.add((Object)this.blueLabelServiceServant, Util.stringToIdentity((String)"BlueLabelService"));
        IceStats.getInstance().setIceObjects(BlueLabelService.communicator(), BlueLabelServiceAdapter, null);
        log.debug((java.lang.Object)"Initialising BlueLabelServiceAdmin interface");
        ObjectAdapter BlueLabelServiceAdminAdapter = BlueLabelService.communicator().createObjectAdapter("BlueLabelServiceAdminAdapter");
        BlueLabelServiceAdminI blueLabelServiceAdmin = new BlueLabelServiceAdminI(this.blueLabelServiceServant);
        BlueLabelServiceAdminAdapter.add((Object)blueLabelServiceAdmin, Util.stringToIdentity((String)"BlueLabelServiceAdmin"));
        BlueLabelServiceAdminAdapter.activate();
        BlueLabelServiceAdapter.activate();
        log.info((java.lang.Object)"Service started");
        BlueLabelService.communicator().waitForShutdown();
        if (BlueLabelService.interrupted()) {
            log.fatal((java.lang.Object)("BlueLabelService " + this.hostName + ": terminating"));
            this.blueLabelServiceServant.shutdown();
        }
        return 0;
    }
}

