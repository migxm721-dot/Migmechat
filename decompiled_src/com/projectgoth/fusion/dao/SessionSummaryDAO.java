/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao;

import com.projectgoth.fusion.domain.CountryLogins;
import java.util.Date;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface SessionSummaryDAO {
    public void updateTotalSession(Date var1, int var2, int var3, int var4);

    public void updateUniqueTotals(Date var1, int var2, boolean var3, List<CountryLogins> var4);

    public void createDailyRows(Date var1);

    public boolean rowsExist(Date var1);

    public boolean yesterdaysTotalsExists(Date var1);
}

