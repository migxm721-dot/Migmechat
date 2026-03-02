/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.ejb;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.ejb.util.LookupUtil;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import com.projectgoth.fusion.restapi.enums.GuardsetCapabilityTypeEnum;
import com.projectgoth.fusion.restapi.enums.MigboAccessMemberTypeEnum;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

public class GuardsetBean
implements SessionBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GuardsetBean.class));
    private static final String MEMCACHE_KEY_SEP = ":";
    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private DataSource userRegistrationMaster;
    private DataSource userRegistrationSlave;
    private SecureRandom randomGen;
    private SessionContext context;
    public static final short VERSION_NOT_EXIST = Short.MAX_VALUE;

    public void setSessionContext(SessionContext newContext) throws EJBException {
        this.context = newContext;
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbCreate() throws CreateException {
        try {
            this.dataSourceMaster = LookupUtil.getFusionMasterDataSource();
            this.dataSourceSlave = LookupUtil.getFusionSlaveDataSource();
            this.userRegistrationMaster = LookupUtil.getRegistrationMasterDataSource();
            this.userRegistrationSlave = LookupUtil.getRegistrationSlaveDataSource();
            this.randomGen = new SecureRandom();
            SystemProperty.ejbInit(this.dataSourceSlave);
        }
        catch (Exception e) {
            log.error((Object)"Unable to create GuardSet EJB", (Throwable)e);
            throw new CreateException("Unable to create User EJB: " + e.getMessage());
        }
    }

    public static String makeMemcacheKey(int clientType, int guardCapability) {
        return Integer.toString(clientType) + MEMCACHE_KEY_SEP + Integer.toString(guardCapability);
    }

    /*
     * Loose catch block
     */
    public Short getMinimumClientVersionForAccess(int clientType, int guardCapability) throws FusionEJBException {
        Short minClientVersion;
        block27: {
            ResultSet rs;
            PreparedStatement ps;
            Connection connSlave;
            if (ClientType.fromValue(clientType) == null) {
                return null;
            }
            if (GuardCapabilityEnum.fromValue(guardCapability) == null) {
                return null;
            }
            String sCachedVer = (String)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.MINIMUM_CLIENT_VERSION, GuardsetBean.makeMemcacheKey(clientType, guardCapability));
            if (sCachedVer != null) {
                minClientVersion = Short.parseShort(sCachedVer);
            } else {
                block24: {
                    connSlave = null;
                    ps = null;
                    rs = null;
                    connSlave = this.dataSourceSlave.getConnection();
                    String sql = "SELECT cv.clientversion FROM guardcapability gc, guardsetcapability gsc, guardsetmember gsm, guardset gs, clientversion cv WHERE gsc.capabilitytype= ? AND gc.id = gsc.guardcapabilityid AND gsm.guardsetid = gsc.guardsetid AND gsm.guardsetid = gs.id AND gsm.membertype = ? AND gc.id = ? AND gsm.memberid = cv.id AND cv.clienttype = ?";
                    ps = connSlave.prepareStatement(sql);
                    ps.setInt(1, GuardsetCapabilityTypeEnum.GUARD_BY_MIN_CLIENT_VERSION.value());
                    ps.setInt(2, MigboAccessMemberTypeEnum.MIN_VERSION.value());
                    ps.setInt(3, guardCapability);
                    ps.setInt(4, clientType);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        int value = rs.getInt(1);
                        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.MINIMUM_CLIENT_VERSION, GuardsetBean.makeMemcacheKey(clientType, guardCapability), (short)value);
                        minClientVersion = (short)value;
                        break block24;
                    }
                    MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.MINIMUM_CLIENT_VERSION, GuardsetBean.makeMemcacheKey(clientType, guardCapability), (short)Short.MAX_VALUE);
                    minClientVersion = Short.MAX_VALUE;
                }
                Object var11_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                    break block27;
                }
                catch (SQLException e) {
                    connSlave = null;
                }
            }
            break block27;
            {
                catch (Exception e) {
                    log.error((Object)("Exception occurred in getMinimumClientVersionForAccess: " + e));
                    throw new FusionEJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var11_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
        return minClientVersion != null && minClientVersion >= Short.MAX_VALUE ? null : minClientVersion;
    }
}

