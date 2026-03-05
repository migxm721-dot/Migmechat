/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.externalfeed.source;

import com.projectgoth.fusion.data.CurrencyData;
import java.util.Map;
import org.w3c.dom.Document;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ExchangeRateFeedDataSource {
    public Map<String, CurrencyData> getCurrencies(Document var1);

    public String getSourceURL();

    public String getFilePrefix();
}

