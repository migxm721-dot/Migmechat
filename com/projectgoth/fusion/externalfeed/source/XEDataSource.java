/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.externalfeed.source;

import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.externalfeed.source.ExchangeRateFeedDataSource;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class XEDataSource
implements ExchangeRateFeedDataSource {
    private static String ELEMENT_TAG = "currency";
    private static String FILE_PREFIX = "xe";

    @Override
    public Map<String, CurrencyData> getCurrencies(Document xmlDocument) {
        HashMap<String, CurrencyData> currencies = new HashMap<String, CurrencyData>();
        NodeList nodeList = xmlDocument.getElementsByTagName(ELEMENT_TAG);
        if (nodeList != null) {
            for (Node node = nodeList.item(0); node != null; node = node.getNextSibling()) {
                if (node.getNodeType() != 1) continue;
                CurrencyData currencyData = new CurrencyData();
                for (Node childNode = node.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
                    String name = childNode.getNodeName();
                    String content = childNode.getTextContent();
                    if ("csymbol".equals(name)) {
                        currencyData.code = content;
                        continue;
                    }
                    if ("cname".equals(name)) {
                        currencyData.name = content;
                        continue;
                    }
                    if (!"crate".equals(name)) continue;
                    currencyData.exchangeRate = Double.valueOf(content);
                }
                if (currencyData.code == null) continue;
                currencies.put(currencyData.code, currencyData);
            }
        }
        return currencies;
    }

    @Override
    public String getSourceURL() {
        return SystemProperty.get("XeDataFeedURL", "http://www.xe.com/dfs/sample-usd.xml");
    }

    @Override
    public String getFilePrefix() {
        return FILE_PREFIX;
    }
}

