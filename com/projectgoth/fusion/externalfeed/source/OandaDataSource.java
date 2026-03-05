/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.externalfeed.source;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.externalfeed.source.ExchangeRateFeedDataSource;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OandaDataSource
implements ExchangeRateFeedDataSource {
    private static String PAYLOAD_PATTERN = "?fxmlrequest=<convert><client_id>%s</client_id><expr>%s</expr><exch>%s</exch></convert>";
    private static String BASE_CURRENCY = "AUD";
    private static String SUPPORTED_CURRENCIES = "";
    private static String FILE_PREFIX = "oanda";
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(OandaDataSource.class));

    @Override
    public Map<String, CurrencyData> getCurrencies(Document xmlDocument) {
        HashMap<String, CurrencyData> currencies = new HashMap<String, CurrencyData>();
        HashMap<String, CurrencyData> oldCurrencyData = new HashMap<String, CurrencyData>();
        try {
            MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
            List oldCurrencies = misEJB.getCurrencies();
            for (CurrencyData c : oldCurrencies) {
                oldCurrencyData.put(c.code, c);
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to obtain MISEJB " + e.getMessage()), (Throwable)e);
            return currencies;
        }
        NodeList nodeList = xmlDocument.getElementsByTagName("EXPR");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        if (nodeList != null) {
            for (Node node = nodeList.item(0); node != null; node = node.getNextSibling()) {
                if (node.getNodeType() != 1) continue;
                CurrencyData currencyData = new CurrencyData();
                currencyData.code = node.getTextContent();
                log.debug((Object)("Found currency [" + currencyData.code + "]"));
                while (!"CONVERSION".equals(node.getNodeName())) {
                    if (!"EXPR".equals((node = node.getNextSibling()).getNodeName())) continue;
                    log.error((Object)("Conversion rate for [" + currencyData.code + "] not found. Possible Invalid XML Format. Terminating"));
                    return new HashMap<String, CurrencyData>();
                }
                for (Node childNode = node.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
                    if ("DATE".equals(childNode.getNodeName())) {
                        try {
                            currencyData.lastUpdated = dateFormat.parse(childNode.getTextContent());
                            log.debug((Object)("Found lastUpdated [" + currencyData.lastUpdated + "]"));
                        }
                        catch (Exception e) {
                            log.warn((Object)(" Unable to parse currency lastupated date: [" + childNode.getTextContent() + "] using today's date instead."));
                            currencyData.lastUpdated = new Date();
                        }
                        continue;
                    }
                    if (!"BID".equals(childNode.getNodeName())) continue;
                    try {
                        currencyData.exchangeRate = Double.valueOf(childNode.getTextContent());
                        log.debug((Object)("Found exchange rate [" + currencyData.exchangeRate + "]"));
                        continue;
                    }
                    catch (NumberFormatException nfe) {
                        log.error((Object)("Invalid number format [" + childNode.getTextContent() + "] for currency [" + currencyData.code + "]. Skipping this currency"));
                        currencyData.code = null;
                    }
                }
                if (currencyData.code == null) continue;
                currencyData.name = "";
                CurrencyData oldData = (CurrencyData)oldCurrencyData.get(currencyData.code);
                if (oldData != null) {
                    currencyData.name = oldData.name;
                }
                log.debug((Object)("Successfully retrieved data for [" + currencyData.code + "]"));
                currencies.put(currencyData.code, currencyData);
            }
        }
        return currencies;
    }

    @Override
    public String getSourceURL() {
        String clientId = SystemProperty.get("OandaClientId", "oandatest");
        String SOURCE_URL = SystemProperty.get("OandaFeedURL", "http://web-services.oanda.com/cgi-bin/fxml/fxml");
        return SOURCE_URL + String.format(PAYLOAD_PATTERN, clientId, SUPPORTED_CURRENCIES, BASE_CURRENCY);
    }

    @Override
    public String getFilePrefix() {
        return FILE_PREFIX;
    }
}

