/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  org.apache.log4j.Logger
 *  org.springframework.jdbc.core.RowMapper
 */
package com.projectgoth.fusion.dao.impl;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.cache.ContactList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.dao.ContactDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import com.projectgoth.fusion.data.ContactData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ContactDAOJDBC
extends MigJdbcDaoSupport
implements ContactDAO {
    private Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ContactDAOJDBC.class));
    private static MemCachedClient contactListMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.contactList);

    @Override
    public Set<ContactData> getContactListForUser(String username) {
        Set<ContactData> contactList = ContactList.getContactList(contactListMemcache, username);
        if (contactList == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("no contactlist found in memcached for user [" + username + "]"));
            }
            contactList = this.loadContactListFromDB(username);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("putting contactlist found in memcached for user [" + username + "]"));
            }
            ContactList.setContactList(contactListMemcache, username, contactList);
        } else if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("returning cached contactlist for user [" + username + "]"));
        }
        return contactList;
    }

    private Set<ContactData> loadContactListFromDB(String username) {
        List contactList = this.getJdbcTemplate().query(this.getExternalizedQuery("ContactDAO.getContactListForUser"), new Object[]{username}, (RowMapper)new ContactDataRowMapper());
        Set<ContactData> contacts = null;
        contacts = contactList == null ? ContactList.newContactList() : ContactList.newContactList(contactList);
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("found " + contacts.size() + "contacts in DB for user [" + username + "]"));
        }
        return contacts;
    }

    private static final class ContactDataRowMapper
    implements RowMapper {
        private ContactDataRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ContactData(rs);
        }
    }
}

