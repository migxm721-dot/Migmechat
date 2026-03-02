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
import com.projectgoth.fusion.common.EJBExceptionWithErrorCause;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.ejb.util.LookupUtil;
import com.projectgoth.fusion.rewardsystem.RewardCentre;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RewardBean
implements SessionBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RewardBean.class));
    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private DataSource userRegistrationMaster;
    private DataSource userRegistrationSlave;
    private SecureRandom randomGen;
    private SessionContext context;

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
            log.error((Object)"Unable to create RewardSystem EJB", (Throwable)e);
            throw new CreateException("Unable to create RewardSystem EJB: " + e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int getRewardScoreCap(int level, RewardProgramData.CategoryEnum ce) throws EJBException {
        try {
            Connection conn = this.dataSourceMaster.getConnection();
            try {
                PreparedStatement ps = conn.prepareStatement("select scorecap from rewardscorecap rsc where rsc.level=? and rsc.category=?");
                try {
                    ResultSet rs;
                    block10: {
                        int n;
                        ps.setInt(1, level);
                        ps.setInt(2, ce.value());
                        rs = ps.executeQuery();
                        try {
                            if (!rs.next()) break block10;
                            n = rs.getInt(1);
                            Object var8_8 = null;
                        }
                        catch (Throwable throwable) {
                            Object var8_10 = null;
                            rs.close();
                            throw throwable;
                        }
                        rs.close();
                        Object var10_11 = null;
                        ps.close();
                        Object var12_14 = null;
                        conn.close();
                        return n;
                    }
                    Object var8_9 = null;
                    rs.close();
                    Object var10_12 = null;
                    ps.close();
                }
                catch (Throwable throwable) {
                    Object var10_13 = null;
                    ps.close();
                    throw throwable;
                }
                Object var12_15 = null;
                conn.close();
                return 0;
            }
            catch (Throwable throwable) {
                Object var12_16 = null;
                conn.close();
                throw throwable;
            }
        }
        catch (Exception e) {
            throw new EJBException("failed to getRewardScoreCap :" + e, e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HashMap<String, Integer> getRewardScoreCap() throws EJBException {
        try {
            HashMap<String, Integer> result = new HashMap<String, Integer>();
            Connection conn = this.dataSourceMaster.getConnection();
            try {
                PreparedStatement ps = conn.prepareStatement("select * from rewardscorecap rsc");
                try {
                    ResultSet rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            String key = RewardCentre.generateCacheKeyForRewardLevelScoreCap(rs.getInt("level"), rs.getInt("category"));
                            result.put(key, rs.getInt("scorecap"));
                        }
                        Object var7_7 = null;
                    }
                    catch (Throwable throwable) {
                        Object var7_8 = null;
                        rs.close();
                        throw throwable;
                    }
                    rs.close();
                    Object var9_10 = null;
                }
                catch (Throwable throwable) {
                    Object var9_11 = null;
                    ps.close();
                    throw throwable;
                }
                ps.close();
                Object var11_13 = null;
            }
            catch (Throwable throwable) {
                Object var11_14 = null;
                conn.close();
                throw throwable;
            }
            conn.close();
            return result;
        }
        catch (Exception e) {
            throw new EJBException("failed to getRewardScoreCap :" + e, e);
        }
    }

    public Boolean sendTrigger(RewardProgramTrigger trigger, long waitTimeoutMillis) {
        if (!SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.ENABLE_SEND_TRIGGER_FROM_REWARDS_BEAN)) {
            throw new EJBExceptionWithErrorCause(ErrorCause.RewardErrorReasonType.FEATURE_DISABLED, new Object[0]);
        }
        try {
            Future<Boolean> futureResult = RewardCentre.getInstance().sendTrigger(trigger);
            if (futureResult == null) {
                throw new EJBException("Null futures after sending trigger");
            }
            if (waitTimeoutMillis == -1L) {
                return futureResult.get();
            }
            if (waitTimeoutMillis == -2L) {
                return null;
            }
            if (waitTimeoutMillis >= 0L) {
                return futureResult.get(waitTimeoutMillis, TimeUnit.MILLISECONDS);
            }
            throw new IllegalArgumentException("illegal waitTimeoutMillis : " + waitTimeoutMillis);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (TimeoutException ex) {
            return null;
        }
        catch (Exception ex) {
            throw new EJBException("Error sending out trigger [" + trigger + "].Exception:" + ex, ex);
        }
    }
}

