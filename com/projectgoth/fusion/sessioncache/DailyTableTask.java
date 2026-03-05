/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.sessioncache;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.dao.impl.DailyVariableTableName;
import java.util.Date;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class DailyTableTask
extends TimerTask {
    private Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DailyTableTask.class));
    private final DailyVariableTableName tableName;
    private Date lastUpdated;

    public DailyTableTask(DailyVariableTableName tableName) {
        this.tableName = tableName;
        this.lastUpdated = new Date();
    }

    public void run() {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Updating table name, currently " + this.tableName.getTablename()));
        }
        this.tableName.updateTableName();
        this.lastUpdated.setTime(this.tableName.getLastUpdated().getTime());
        this.log.info((Object)("Table name updated, now " + this.tableName.getTablename()));
    }
}

