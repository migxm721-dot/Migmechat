/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.InitializationData
 *  Ice.Properties
 *  Ice.Util
 *  javax.ejb.EJBException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.ejb;

import Ice.InitializationData;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.slice.AuthenticationServicePrx;
import com.projectgoth.fusion.slice.BlueLabelServicePrx;
import com.projectgoth.fusion.slice.CallMakerPrx;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.EventSystemPrx;
import com.projectgoth.fusion.slice.MessageLoggerPrx;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServicePrx;
import com.projectgoth.fusion.slice.RegistryAdminPrx;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.ReputationServicePrx;
import com.projectgoth.fusion.slice.SMSSenderPrx;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import com.projectgoth.fusion.slice.UserPrx;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

public class EJBIcePrxFinder {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(EJBIcePrxFinder.class));
    private static IcePrxFinder icePrxFinder;
    private static boolean logMessagesToFile;

    public static RegistryPrx getRegistry() throws EJBException {
        try {
            return icePrxFinder.getRegistry(false);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static RegistryAdminPrx getRegistryAdmin() throws EJBException {
        try {
            return icePrxFinder.getRegistryAdmin(false);
        }
        catch (Exception e) {
            log.error((Object)"failed to find registry admin proxy", (Throwable)e);
            return null;
        }
    }

    public static UserPrx findUserPrx(String username) throws EJBException {
        try {
            return icePrxFinder.findUserPrx(username);
        }
        catch (Exception e) {
            log.error((Object)"failed to find user proxy", (Throwable)e);
            throw new EJBException(e.getMessage());
        }
    }

    public static UserPrx findOnewayUserPrx(String username) throws EJBException {
        try {
            return icePrxFinder.findOnewayUserPrx(username);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static ConnectionPrx findConnectionPrx(String sessionId) throws EJBException {
        try {
            return icePrxFinder.findConnectionPrx(sessionId);
        }
        catch (Exception e) {
            log.error((Object)"failed to find connection proxy", (Throwable)e);
            throw new EJBException(e.getMessage());
        }
    }

    public static ChatRoomPrx findChatRoomPrx(String name) throws EJBException {
        try {
            return icePrxFinder.findChatRoomPrx(name);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static ChatRoomPrx[] findChatRoomProxies(String[] names) throws EJBException {
        try {
            return icePrxFinder.findChatRoomProxies(names);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static CallMakerPrx getCallMaker() throws EJBException {
        try {
            return icePrxFinder.getCallMaker();
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static SMSSenderPrx getSMSSender() throws EJBException {
        try {
            return icePrxFinder.getSMSSender();
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static BlueLabelServicePrx getBlueLabelService() throws EJBException {
        try {
            return icePrxFinder.getBlueLabelServiceProxy();
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static ReputationServicePrx getReputationService() throws EJBException {
        try {
            return icePrxFinder.getReputationServiceProxy();
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static MessageLoggerPrx getOnewayMessageLoggerPrx() throws EJBException {
        try {
            return icePrxFinder.getOnewayMessageLoggerPrx();
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static boolean logMessagesToFile() {
        return logMessagesToFile;
    }

    public static EventSystemPrx getEventSystemProxy() throws EJBException {
        try {
            return icePrxFinder.getEventSystemProxy();
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static EventSystemPrx getOnewayEventSystemProxy() throws EJBException {
        try {
            return icePrxFinder.getOnewayEventSystemProxy();
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static UserNotificationServicePrx getUserNotificationServiceProxy() throws EJBException {
        try {
            return icePrxFinder.getUserNotificationServiceProxy();
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage(), e);
        }
    }

    public static AuthenticationServicePrx getAuthenticationServiceProxy() throws EJBException {
        try {
            return icePrxFinder.getAuthenticationServiceProxy();
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static ConnectionPrx getConnectionProxy(String sessionId) throws EJBException {
        try {
            return icePrxFinder.findConnectionPrx(sessionId);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public static RecommendationDataCollectionServicePrx getRecommendationDataCollectionServicePrx() {
        try {
            return icePrxFinder.getRecommendationDataCollectionServiceProxy();
        }
        catch (Exception e) {
            log.error((Object)("Unable to get RDCS proxy instance.Exception:" + e), (Throwable)e);
            throw new EJBException(e.getMessage());
        }
    }

    static {
        logMessagesToFile = false;
        try {
            String configFile = System.getProperty("ice.config");
            Properties properties = Util.createProperties();
            properties.load(configFile == null ? "Ice.cfg" : configFile);
            InitializationData initializationData = new InitializationData();
            initializationData.properties = properties;
            icePrxFinder = new IcePrxFinder(Util.initialize((String[])new String[0], (InitializationData)initializationData), properties);
            logMessagesToFile = properties.getPropertyAsIntWithDefault("LogMessagesToFile", 0) == 1;
        }
        catch (Exception e) {
            log.error((Object)"failed to initialize ice proxy finder", (Throwable)e);
        }
    }
}

