/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao.impl;

import com.projectgoth.fusion.dao.SystemDAO;
import com.projectgoth.fusion.dao.impl.MigJdbcDaoSupport;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SystemDAOJDBC
extends MigJdbcDaoSupport
implements SystemDAO {
    @Override
    public Map<String, String> getSystemProperties() {
        return this.getJdbcTemplate().queryForMap(this.getExternalizedQuery("SystemDAO.getSystemProperties"));
    }

    @Override
    public String getSystemProperty(String key) {
        return (String)this.getJdbcTemplate().queryForObject(this.getExternalizedQuery("SystemDAO.getSystemProperty"), new Object[]{key}, String.class);
    }
}

