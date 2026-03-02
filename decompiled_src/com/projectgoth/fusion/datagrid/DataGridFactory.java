/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.datagrid;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.datagrid.DataGrid;
import com.projectgoth.fusion.datagrid.HazelcastDataGrid;
import org.apache.log4j.Logger;

public class DataGridFactory {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DataGridFactory.class));

    public static DataGridFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public DataGrid getGrid() {
        return HazelcastDataGrid.getInstance();
    }

    private static class SingletonHolder {
        public static final DataGridFactory INSTANCE = new DataGridFactory();

        private SingletonHolder() {
        }
    }
}

