/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.externalfeed.source;

import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.externalfeed.source.ExchangeRateFeedDataSource;
import com.projectgoth.fusion.externalfeed.source.OandaDataSource;
import com.projectgoth.fusion.externalfeed.source.XEDataSource;

public class ExchangeRateFeedDataSourceFactory {
    public static ExchangeRateFeedDataSource getDataSource() throws Exception {
        String dataSource = SystemProperty.get("ExchangeRateDataSource", "xe");
        ExchangeRateFeedDataSource source = ExchangeRateFeedDataSourceFactory.getDataSource(DataSourceType.fromName(dataSource));
        if (source == null) {
            throw new Exception("Invalid datasource specified in system property [" + dataSource + "]");
        }
        return source;
    }

    public static ExchangeRateFeedDataSource getDataSource(DataSourceType type) throws Exception {
        if (DataSourceType.XE.equals((Object)type)) {
            return new XEDataSource();
        }
        if (DataSourceType.OANDA.equals((Object)type)) {
            return new OandaDataSource();
        }
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum DataSourceType {
        XE("xe"),
        OANDA("oanda");

        private String name;

        private DataSourceType(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        public static DataSourceType fromName(String s) {
            for (DataSourceType t : DataSourceType.values()) {
                if (!t.name.equals(s)) continue;
                return t;
            }
            return null;
        }
    }
}

