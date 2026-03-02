/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao;

import com.projectgoth.fusion.domain.CountryLogins;
import com.projectgoth.fusion.userevent.system.domain.AllowListEntry;
import java.util.Date;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface UserDAO {
    public List<CountryLogins> findRecentLoginsGroupedByCountry(Date var1, Date var2, int var3);

    public List<AllowListEntry> getAllowListForUser(String var1);

    public String getMobileNumberForUser(String var1);

    public boolean isVerfiedEmailAddress(String var1);

    public boolean isBounceEmailAddress(String var1);
}

