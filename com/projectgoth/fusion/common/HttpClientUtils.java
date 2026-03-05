/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HeaderElement
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.auth.AuthScope
 *  org.apache.http.auth.Credentials
 *  org.apache.http.auth.UsernamePasswordCredentials
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.conn.ClientConnectionManager
 *  org.apache.http.conn.ConnectionKeepAliveStrategy
 *  org.apache.http.conn.params.ConnManagerParams
 *  org.apache.http.conn.params.ConnPerRoute
 *  org.apache.http.conn.params.ConnPerRouteBean
 *  org.apache.http.conn.scheme.PlainSocketFactory
 *  org.apache.http.conn.scheme.Scheme
 *  org.apache.http.conn.scheme.SchemeRegistry
 *  org.apache.http.conn.scheme.SocketFactory
 *  org.apache.http.conn.ssl.SSLSocketFactory
 *  org.apache.http.impl.client.DefaultHttpClient
 *  org.apache.http.impl.conn.SingleClientConnManager
 *  org.apache.http.message.BasicHeaderElementIterator
 *  org.apache.http.params.BasicHttpParams
 *  org.apache.http.params.HttpConnectionParams
 *  org.apache.http.params.HttpParams
 *  org.apache.http.protocol.HttpContext
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.TrustModifier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HttpClientUtils {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(HttpClientUtils.class));
    private static final int DEFAULT_HTTP_PORT = 80;
    private static final int DEFAULT_HTTPS_PORT = 443;
    private static final ResponseProcessor<Object, Document> DOCUMENT_EXTRACTOR = new ResponseProcessor<Object, Document>(){

        @Override
        public Document processOnNullRespose(Object extraData) throws IOException {
            return null;
        }

        @Override
        public Document processStream(InputStream ins, Object extraData_unused) throws IOException {
            try {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                return docBuilder.parse(ins);
            }
            catch (Exception ex) {
                IOException ioex = new IOException("Error extracting xml data");
                ioex.initCause(ex);
                throw ioex;
            }
        }
    };
    private static final ResponseProcessor<Charset, String> STRING_EXTRACTOR = new ResponseProcessor<Charset, String>(){

        @Override
        public String processOnNullRespose(Charset extraData_encoding) throws IOException {
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String processStream(InputStream ins, Charset extraData_charSet) throws IOException {
            try {
                StringBuilder result = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(ins, extraData_charSet));
                try {
                    int charInt;
                    while ((charInt = ((Reader)in).read()) >= 0) {
                        result.append((char)charInt);
                    }
                    Object var7_8 = null;
                }
                catch (Throwable throwable) {
                    Object var7_9 = null;
                    ((Reader)in).close();
                    throw throwable;
                }
                ((Reader)in).close();
                return result.toString();
            }
            catch (Exception ex) {
                IOException ioEx = new IOException("Error extracting xml data");
                ioEx.initCause(ex);
                throw ioEx;
            }
        }
    };

    private HttpClientUtils() {
    }

    private static DefaultHttpClient createHttpClient(HttpClientConfig clientConfig) throws GeneralSecurityException {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", (SocketFactory)PlainSocketFactory.getSocketFactory(), 80));
        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout((HttpParams)params, (int)clientConfig.timeOutInMillis);
        HttpConnectionParams.setSoTimeout((HttpParams)params, (int)clientConfig.timeOutInMillis);
        ConnManagerParams.setMaxTotalConnections((HttpParams)params, (int)clientConfig.maxConnections);
        ConnManagerParams.setTimeout((HttpParams)params, (long)clientConfig.timeOutInMillis);
        ConnManagerParams.setMaxConnectionsPerRoute((HttpParams)params, (ConnPerRoute)new ConnPerRouteBean(clientConfig.maxConnections));
        SingleClientConnManager cm = new SingleClientConnManager((HttpParams)params, schemeRegistry);
        DefaultHttpClient client = new DefaultHttpClient((ClientConnectionManager)cm, (HttpParams)params);
        client.setKeepAliveStrategy(HttpClientUtils.getKeepAliveStrategy(clientConfig));
        if (clientConfig.strictHttpsCertCheck) {
            SchemeRegistry currSchemeRegistry = client.getConnectionManager().getSchemeRegistry();
            currSchemeRegistry.register(new Scheme("https", (SocketFactory)SSLSocketFactory.getSocketFactory(), 443));
        } else {
            TrustModifier.relaxHostChecking(client);
        }
        return client;
    }

    private static void setCredentials(DefaultHttpClient client, HttpClientConfig config, URI uri) {
        if (config.username != null) {
            if (config.password != null) {
                client.getCredentialsProvider().setCredentials(new AuthScope(uri.getHost(), uri.getPort()), (Credentials)new UsernamePasswordCredentials(config.username, config.password));
            } else {
                throw new IllegalArgumentException("Password not supplied for username [" + config.username + "]");
            }
        }
    }

    private static ConnectionKeepAliveStrategy getKeepAliveStrategy(HttpClientConfig hcConfig) {
        class CustomConnectionKeepAliveStrategy
        implements ConnectionKeepAliveStrategy {
            HttpClientConfig hcConfig;

            public CustomConnectionKeepAliveStrategy(HttpClientConfig hcConfig) {
                this.hcConfig = hcConfig;
            }

            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                BasicHeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value == null || !param.equalsIgnoreCase("timeout")) continue;
                    try {
                        return Long.parseLong(value) * 1000L;
                    }
                    catch (NumberFormatException ignore) {
                    }
                }
                return 1000 * this.hcConfig.keepAliveInSecs;
            }
        }
        return new CustomConnectionKeepAliveStrategy(hcConfig);
    }

    private static void cleanUp(HttpClient httpClient) {
        try {
            httpClient.getConnectionManager().shutdown();
        }
        catch (Exception ex) {
            log.warn((Object)"Error cleaning up httpClient", (Throwable)ex);
        }
    }

    public static Document getXMLResponseAsDocument(HttpClientConfig httpClientConfig, HttpUriRequest request) throws GeneralSecurityException, IOException {
        return HttpClientUtils.processResponse(httpClientConfig, request, DOCUMENT_EXTRACTOR, null);
    }

    public static String getResponseString(HttpClientConfig httpClientConfig, HttpUriRequest request, Charset charSet) throws GeneralSecurityException, IOException {
        if (charSet == null) {
            charSet = Charset.defaultCharset();
        }
        return HttpClientUtils.processResponse(httpClientConfig, request, STRING_EXTRACTOR, charSet);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <TOutput, TExtra> TOutput processResponse(HttpClientConfig httpClientConfig, HttpUriRequest request, ResponseProcessor<TExtra, TOutput> processor, TExtra extraData) throws GeneralSecurityException, IOException {
        TOutput output = null;
        DefaultHttpClient client = HttpClientUtils.createHttpClient(httpClientConfig);
        try {
            block5: {
                HttpClientUtils.setCredentials(client, httpClientConfig, request.getURI());
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream ins = entity.getContent();
                    try {
                        output = processor.processStream(ins, extraData);
                        Object var10_9 = null;
                    }
                    catch (Throwable throwable) {
                        Object var10_10 = null;
                        ins.close();
                        throw throwable;
                    }
                    ins.close();
                    {
                        break block5;
                    }
                }
                output = processor.processOnNullRespose(extraData);
            }
            Object var12_12 = null;
        }
        catch (Throwable throwable) {
            Object var12_13 = null;
            HttpClientUtils.cleanUp((HttpClient)client);
            throw throwable;
        }
        HttpClientUtils.cleanUp((HttpClient)client);
        return output;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface ResponseProcessor<TExtra, TOutput> {
        public TOutput processStream(InputStream var1, TExtra var2) throws IOException;

        public TOutput processOnNullRespose(TExtra var1) throws IOException;
    }

    public static class HttpClientConfig {
        public int timeOutInMillis = 30000;
        public int maxConnections = 100;
        public boolean strictHttpsCertCheck = true;
        public int keepAliveInSecs = 60;
        public String username = null;
        public String password = null;
    }
}

