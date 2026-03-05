/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Required
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ExternalizedQueries;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Required;

public class ExternalizedQueriesProperties
implements ExternalizedQueries {
    private Properties queries;

    @Required
    public void setQueries(Properties queries) {
        this.queries = queries;
    }

    public String getQuery(String name) {
        return this.queries.getProperty(name);
    }
}

