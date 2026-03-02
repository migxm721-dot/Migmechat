/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.annotation.Required
 *  org.springframework.jdbc.core.RowMapper
 */
package com.projectgoth.fusion.dao.impl;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.cache.BroadcastList;
import com.projectgoth.fusion.cache.ContactList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.dao.BroadcastListDAO;
import com.projectgoth.fusion.dao.ContactDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import com.projectgoth.fusion.data.ContactData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.RowMapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BroadcastListDAOJDBC
extends MigJdbcDaoSupport
implements BroadcastListDAO {
    private Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BroadcastListDAOJDBC.class));
    private static MemCachedClient broadcastListMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.broadcastList);
    private ContactDAO contactDAO;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Set<String> getBroadcastListForUser(String username) {
        block12: {
            block11: {
                Set<String> set;
                block10: {
                    Set<String> set2;
                    block9: {
                        Set<String> set3;
                        block8: {
                            try {
                                try {
                                    if (MemCachedUtils.getLock(broadcastListMemcache, "BLDL", username, 15000)) {
                                        Set<String> broadcastList = BroadcastList.getBroadcastList(broadcastListMemcache, username);
                                        if (broadcastList != null) {
                                            this.log.debug((Object)"found a broadcastlist in memcached");
                                            set3 = broadcastList;
                                            Object var5_7 = null;
                                            break block8;
                                        }
                                        broadcastList = this.loadBroadcastListFromDB(username);
                                        if (broadcastList != null && !broadcastList.isEmpty()) {
                                            this.log.debug((Object)(username + " already has a BCL, not generating, using the existing one from DB"));
                                            BroadcastList.setBroadcastList(broadcastListMemcache, username, broadcastList);
                                            set2 = broadcastList;
                                            break block9;
                                        }
                                        broadcastList = this.generateBCL(username);
                                        BroadcastList.setBroadcastList(broadcastListMemcache, username, broadcastList);
                                        set = broadcastList;
                                        break block10;
                                    }
                                    this.log.error((Object)("Failed to get a lock to update user [" + username + "]'s BCL in 10 seconds"));
                                    break block11;
                                }
                                catch (Exception ex) {
                                    this.log.error((Object)"failed to load the BCL, rolling back", (Throwable)ex);
                                    Object var5_11 = null;
                                    MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
                                    break block12;
                                }
                            }
                            catch (Throwable throwable) {
                                Object var5_12 = null;
                                MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
                                throw throwable;
                            }
                        }
                        MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
                        return set3;
                    }
                    Object var5_8 = null;
                    MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
                    return set2;
                }
                Object var5_9 = null;
                MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
                return set;
            }
            Object var5_10 = null;
            MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("broadcast list for user [" + username + "] not persisted as it's empty"));
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getBroadcastListForGroup(int groupId) {
        Set<String> broadcastList = this.loadGroupBroadcastListFromDB(groupId);
        if (broadcastList != null && !broadcastList.isEmpty()) {
            return broadcastList;
        }
        return Collections.emptySet();
    }

    private Set<String> loadBroadcastListFromDB(String username) {
        List broadcastList = this.getJdbcTemplate().query(this.getExternalizedQuery("BroadcastDAO.getBroadcastListForUser"), new Object[]{username}, (RowMapper)new BroadcastListRowMapper());
        Set<String> broadcastListSet = BroadcastList.newBroadcastList(broadcastList);
        return broadcastListSet;
    }

    private Set<String> loadGroupBroadcastListFromDB(Integer groupId) {
        List broadcastList = this.getJdbcTemplate().query(this.getExternalizedQuery("BroadcastDAO.getBroadcastListForGroup"), new Object[]{groupId}, (RowMapper)new BroadcastListRowMapper());
        Set<String> broadcastListSet = BroadcastList.newBroadcastList(broadcastList);
        return broadcastListSet;
    }

    private Set<String> generateBCL(String username) throws SQLException {
        Set<ContactData> contacts;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("generating BCL for user [" + username + "]"));
        }
        if ((contacts = this.contactDAO.getContactListForUser(username)) == null || contacts.isEmpty()) {
            return BroadcastList.newBroadcastList();
        }
        Set<String> broadcastList = BroadcastList.newBroadcastList(ContactList.fusionContactUsernames(contacts));
        List contactsWhoHaveMe = this.getJdbcTemplate().query("select username from contact where fusionUsername = ?", new Object[]{username}, (RowMapper)new GenericStringMapper());
        broadcastList.retainAll(contactsWhoHaveMe);
        List contactsUserBlocked = this.getJdbcTemplate().query("select blockusername from blocklist where username = ?", new Object[]{username}, (RowMapper)new GenericStringMapper());
        broadcastList.removeAll(contactsUserBlocked);
        List contactsBlockedUser = this.getJdbcTemplate().query("select username from blocklist where blockusername = ?", new Object[]{username}, (RowMapper)new GenericStringMapper());
        broadcastList.removeAll(contactsBlockedUser);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("generated " + broadcastList.size() + " BCL entries for user [" + username + "]"));
        }
        return broadcastList;
    }

    @Required
    public void setContactDAO(ContactDAO contactDAO) {
        this.contactDAO = contactDAO;
    }

    private static final class GenericStringMapper
    implements RowMapper {
        private GenericStringMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        }
    }

    private static final class BroadcastListRowMapper
    implements RowMapper {
        private BroadcastListRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        }
    }
}

