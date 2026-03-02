/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.accesscontrol;

import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlData;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlParameter;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class AuthenticatedAccessControl {
    private static final int LOCAL_CACHE_TIME = 600000;
    private static final int LOCAL_CACHE_RANDOMNESS_TIME = 13;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AuthenticatedAccessControl.class));
    private static Map<String, AuthenticatedAccessControlData> dataMap = new ConcurrentHashMap<String, AuthenticatedAccessControlData>();
    private static Semaphore semaphore = new Semaphore(1);
    private static long lastUpdated;
    private static Random random;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadData() {
        if (lastUpdated == 0L) {
            semaphore.acquireUninterruptibly();
        } else if (!semaphore.tryAcquire()) {
            return;
        }
        try {
            try {
                MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
                List newData = misEJB.getAuthenticatedAccessControlData();
                HashSet<String> newKeySet = new HashSet<String>();
                for (AuthenticatedAccessControlData acData : newData) {
                    try {
                        String key = acData.name.toLowerCase();
                        if (dataMap.containsKey(key)) {
                            log.info((Object)String.format("updating authenticated access control data %s", key));
                        } else {
                            log.info((Object)String.format("adding authenticated access control data %s", key));
                        }
                        newKeySet.add(key);
                        dataMap.put(key, acData);
                    }
                    catch (Exception e) {
                        log.error((Object)("Unable to load authenticated access control data [" + acData.name + "]"), (Throwable)e);
                    }
                }
                for (String key : dataMap.keySet()) {
                    if (newKeySet.contains(key)) continue;
                    log.info((Object)String.format("removing authenticated access control data %s", key));
                    dataMap.remove(key);
                    log.info((Object)String.format("removed authenticated access control data %s", key));
                }
                lastUpdated = System.currentTimeMillis();
            }
            catch (Exception e) {
                log.error((Object)"Unable to load authenticated access control data", (Throwable)e);
                Object var7_9 = null;
                semaphore.release();
            }
            Object var7_8 = null;
            semaphore.release();
        }
        catch (Throwable throwable) {
            Object var7_10 = null;
            semaphore.release();
            throw throwable;
        }
    }

    private static AuthenticatedAccessControlData getData(AuthenticatedAccessControlTypeEnum type) {
        AuthenticatedAccessControlData acd;
        long curTime = System.currentTimeMillis();
        if (curTime - lastUpdated > 600000L && curTime - lastUpdated - 600000L > (long)(random.nextInt(13) * 1000)) {
            AuthenticatedAccessControl.loadData();
        }
        if ((acd = dataMap.get(type.name().toLowerCase())) == null) {
            acd = type.toData();
        }
        return acd;
    }

    private static boolean checkAccess(AuthenticatedAccessControlParameter param, AuthenticatedAccessControlData acd, AuthenticatedAccessControlTypeEnum type) {
        boolean allowed = false;
        String rateLimit = null;
        log.debug((Object)String.format("Checking auth access for %s by %s with %s", new Object[]{type, param, acd}));
        if (acd.isMobileVerifiedAllowed && param.isMobileVerified) {
            allowed = true;
            rateLimit = acd.mobileVerifiedRateLimit;
        } else if (acd.isEmailVerifiedAllowed && param.isEmailVerified) {
            allowed = true;
            rateLimit = acd.emailVerifiedRateLimit;
        }
        if (allowed && !StringUtil.isBlank(rateLimit)) {
            try {
                MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.AUTH_AC.toString(), MemCachedKeyUtils.getFullKeyFromStrings(type.name(), param.username), rateLimit);
            }
            catch (MemCachedRateLimiter.LimitExceeded e) {
                allowed = false;
                log.debug((Object)e.getPrettyMessage());
            }
            catch (MemCachedRateLimiter.FormatError e) {
                allowed = false;
                log.error((Object)"Formatting error in rate limiter expression", (Throwable)e);
            }
        }
        log.debug((Object)String.format(" Result %s", allowed));
        return allowed;
    }

    public static boolean hasAccess(AuthenticatedAccessControlTypeEnum type, AuthenticatedAccessControlParameter param) {
        if (type == null || param == null) {
            return false;
        }
        AuthenticatedAccessControlData acd = AuthenticatedAccessControl.getData(type);
        if (acd == null) {
            return false;
        }
        return AuthenticatedAccessControl.checkAccess(param, acd, type);
    }

    public static boolean hasAccess(AuthenticatedAccessControlTypeEnum type, UserData userData) throws IllegalArgumentException {
        if (userData == null) {
            log.error((Object)String.format("Unable to check authenticated access for user for type %s due to null userdata, returning false", new Object[]{type}));
            throw new IllegalArgumentException("Invalid user");
        }
        return AuthenticatedAccessControl.hasAccess(type, new AuthenticatedAccessControlParameter(userData));
    }

    public static boolean hasAccessByUsernameLocal(AuthenticatedAccessControlTypeEnum type, String username) {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            return AuthenticatedAccessControl.hasAccessByUsername(type, username, userBean);
        }
        catch (CreateException e) {
            return false;
        }
    }

    public static boolean hasAccessByUsername(AuthenticatedAccessControlTypeEnum type, String username, Object ejbProxy) throws IllegalArgumentException, IllegalStateException {
        if (type == null || StringUtil.isBlank(username)) {
            return false;
        }
        AuthenticatedAccessControlData acd = AuthenticatedAccessControl.getData(type);
        if (acd == null) {
            return false;
        }
        if (!acd.isMobileVerifiedAllowed && !acd.isEmailVerifiedAllowed) {
            return false;
        }
        try {
            AuthenticatedAccessControlParameter acp;
            if (ejbProxy instanceof User) {
                acp = ((User)ejbProxy).getUserAuthenticatedAccessControlParameter(username);
            } else if (ejbProxy instanceof UserLocal) {
                acp = ((UserLocal)ejbProxy).getUserAuthenticatedAccessControlParameter(username);
            } else {
                throw new IllegalArgumentException("user bean proxy is neither remote nor local");
            }
            if (null == acp) {
                throw new IllegalArgumentException("Unknown user " + username);
            }
            return AuthenticatedAccessControl.checkAccess(acp, acd, type);
        }
        catch (RemoteException e) {
            log.error((Object)String.format("Unable to check authenticated access control for user '%s' for %s", new Object[]{username, type}), (Throwable)e);
            throw new IllegalStateException("Internal Error");
        }
    }

    public static boolean hasAccessByUseridLocal(AuthenticatedAccessControlTypeEnum type, int userID) {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            return AuthenticatedAccessControl.hasAccessByUserid(type, userID, userBean);
        }
        catch (CreateException e) {
            return false;
        }
    }

    public static boolean hasAccessByUserid(AuthenticatedAccessControlTypeEnum type, int userID, Object ejbProxy) throws IllegalArgumentException, IllegalStateException {
        if (type == null || userID < 0) {
            return false;
        }
        AuthenticatedAccessControlData acd = AuthenticatedAccessControl.getData(type);
        if (acd == null) {
            return false;
        }
        if (!acd.isMobileVerifiedAllowed && !acd.isEmailVerifiedAllowed) {
            return false;
        }
        try {
            UserData userData;
            if (ejbProxy instanceof User) {
                userData = ((User)ejbProxy).loadUserFromID(userID);
            } else if (ejbProxy instanceof UserLocal) {
                userData = ((UserLocal)ejbProxy).loadUserFromID(userID);
            } else {
                throw new IllegalArgumentException("user bean proxy is neither remote nor local");
            }
            if (userData == null) {
                throw new IllegalArgumentException("Unknown user " + userID);
            }
            return AuthenticatedAccessControl.checkAccess(new AuthenticatedAccessControlParameter(userData), acd, type);
        }
        catch (RemoteException e) {
            log.error((Object)String.format("Unable to check authenticated access control for user id %d for %s", new Object[]{userID, type}), (Throwable)e);
            throw new IllegalStateException("Internal Error");
        }
    }

    static {
        random = new Random();
        AuthenticatedAccessControl.loadData();
    }
}

