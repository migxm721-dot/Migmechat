/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBHome
 *  javax.ejb.EJBLocalHome
 *  javax.ejb.EJBLocalObject
 *  javax.ejb.EJBObject
 *  javax.rmi.PortableRemoteObject
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.ejb;

import com.projectgoth.fusion.common.ConfigUtils;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import org.apache.log4j.Logger;

public class EJBHomeCache {
    private static Map<Class, EJBHome> homes = new ConcurrentHashMap<Class, EJBHome>();
    private static Map<Class, EJBLocalHome> localHomes = new ConcurrentHashMap<Class, EJBLocalHome>();
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(EJBHomeCache.class));

    public static EJBHome getHome(String jndiName, Class homeInterface) throws NamingException {
        EJBHome home = homes.get(homeInterface);
        if (home == null) {
            Object ref = new InitialContext().lookup(jndiName);
            home = (EJBHome)PortableRemoteObject.narrow((Object)ref, (Class)homeInterface);
            homes.put(homeInterface, home);
        }
        return home;
    }

    public static EJBObject getObject(String jndiName, Class homeInterface) throws CreateException {
        EJBHome home = null;
        Method method = null;
        try {
            method = homeInterface.getMethod("create", null);
            home = EJBHomeCache.getHome(jndiName, homeInterface);
            return (EJBObject)method.invoke(home, null);
        }
        catch (NamingException e) {
            log.error((Object)"failed to create bean", (Throwable)e);
            throw new CreateException(e.getMessage());
        }
        catch (NoSuchMethodException e) {
            log.error((Object)"failed to create bean", (Throwable)e);
            throw new CreateException(e.getMessage());
        }
        catch (Exception e) {
            log.error((Object)"failed to create bean", (Throwable)e);
            try {
                homes.remove(homeInterface);
                home = EJBHomeCache.getHome(jndiName, homeInterface);
                return (EJBObject)method.invoke(home, null);
            }
            catch (Exception ie) {
                log.error((Object)("failed to create EJB for " + homeInterface), (Throwable)e);
                throw new CreateException(ie.getMessage());
            }
        }
    }

    public static EJBLocalHome getLocalHome(String jndiName, Class homeInterface) throws NamingException {
        EJBLocalHome home = localHomes.get(homeInterface);
        if (home == null) {
            home = (EJBLocalHome)new InitialContext().lookup(jndiName);
            localHomes.put(homeInterface, home);
        }
        return home;
    }

    public static EJBLocalObject getLocalObject(String jndiName, Class homeInterface) throws CreateException {
        try {
            Method method = homeInterface.getMethod("create", null);
            EJBLocalHome home = EJBHomeCache.getLocalHome(jndiName, homeInterface);
            return (EJBLocalObject)method.invoke(home, null);
        }
        catch (Exception e) {
            log.error((Object)"failed to create bean", (Throwable)e);
            throw new CreateException(e.getMessage());
        }
    }
}

