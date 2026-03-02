/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.GuardsetDAOChain;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import com.projectgoth.fusion.restapi.enums.GuardsetCapabilityTypeEnum;
import com.projectgoth.fusion.restapi.enums.MigboAccessMemberTypeEnum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

public class FusionDbGuardsetDAOChain
extends GuardsetDAOChain {
    private static final Logger log = Logger.getLogger(FusionDbGuardsetDAOChain.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Short getMinimumClientVersionForAccess(int clientType, int guardCapability) throws DAOException {
        Short minClientVersion;
        if (ClientType.fromValue(clientType) == null) {
            return null;
        }
        if (GuardCapabilityEnum.fromValue(guardCapability) == null) {
            return null;
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            block7: {
                try {
                    conn = DBUtils.getFusionReadConnection();
                    String sql = "SELECT cv.clientversion FROM guardcapability gc, guardsetcapability gsc, guardsetmember gsm, guardset gs, clientversion cv WHERE gsc.capabilitytype= ? AND gc.id = gsc.guardcapabilityid AND gsm.guardsetid = gsc.guardsetid AND gsm.guardsetid = gs.id AND gsm.membertype = ? AND gc.id = ? AND gsm.memberid = cv.id AND cv.clienttype = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, GuardsetCapabilityTypeEnum.GUARD_BY_MIN_CLIENT_VERSION.value());
                    ps.setInt(2, MigboAccessMemberTypeEnum.MIN_VERSION.value());
                    ps.setInt(3, guardCapability);
                    ps.setInt(4, clientType);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        minClientVersion = (short)rs.getInt(1);
                        break block7;
                    }
                    minClientVersion = Short.MAX_VALUE;
                }
                catch (Exception e) {
                    log.error((Object)"Unable to retrieve getMinimumClientVersionForAccess", (Throwable)e);
                    Short s = super.getMinimumClientVersionForAccess(clientType, guardCapability);
                    Object var10_10 = null;
                    DBUtils.closeResource(rs, ps, conn, log);
                    return s;
                }
            }
            Object var10_9 = null;
        }
        catch (Throwable throwable) {
            Object var10_11 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            throw throwable;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return minClientVersion != null && minClientVersion >= Short.MAX_VALUE ? null : minClientVersion;
    }
}

