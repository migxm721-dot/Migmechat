/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.StringEntity
 *  org.apache.http.impl.client.DefaultHttpClient
 *  org.apache.http.util.EntityUtils
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.TrustModifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SimpleXMLParser {
    private static final String DEFAULT_CHATSET = "UTF-8";
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SimpleXMLParser.class));
    private Document document;

    public SimpleXMLParser(InputStream in) throws SAXException, IOException, ParserConfigurationException {
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
    }

    public SimpleXMLParser(String server, String request) throws SAXException, IOException, ParserConfigurationException {
        URL url = new URL(server);
        HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
        this.processRequest(httpConn, request);
    }

    public SimpleXMLParser(String server, String request, boolean strictSSLCheck) throws SAXException, IOException, ParserConfigurationException, Exception {
        if (strictSSLCheck) {
            URL url = new URL(server);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            this.processRequest(httpConn, request);
        } else {
            this.nonStrictXMLPost(server, request);
        }
    }

    private void nonStrictXMLPost(String serverName, String postData) throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        TrustModifier.relaxHostChecking(client);
        HttpPost httppost = new HttpPost(serverName);
        httppost.addHeader("Content-Type", "application/xml");
        httppost.setEntity((HttpEntity)new StringEntity(postData, DEFAULT_CHATSET));
        HttpResponse response = null;
        try {
            response = client.execute((HttpUriRequest)httppost);
        }
        catch (Exception e) {
            log.error((Object)e.getMessage());
        }
        if (null != response) {
            HttpEntity entity = response.getEntity();
            String xmlStr = EntityUtils.toString((HttpEntity)entity);
            InputSource is = new InputSource(new StringReader(xmlStr));
            this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        }
    }

    private void processRequest(HttpURLConnection httpConn, String request) throws IOException, ParserConfigurationException, SAXException {
        httpConn.setDoOutput(true);
        httpConn.setUseCaches(false);
        httpConn.getOutputStream().write(request.getBytes(DEFAULT_CHATSET));
        if (httpConn.getResponseCode() != 200) {
            throw new IOException("HTTP " + httpConn.getResponseCode() + " " + httpConn.getResponseMessage());
        }
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(httpConn.getInputStream());
    }

    public boolean containsTag(String tagName) {
        NodeList nodes = this.document.getElementsByTagName(tagName);
        return nodes != null && nodes.getLength() > 0;
    }

    public String getTagValue(String tagName) {
        NodeList nodes = this.document.getElementsByTagName(tagName);
        if (nodes == null || nodes.getLength() == 0) {
            return null;
        }
        Node node = nodes.item(0).getFirstChild();
        if (node == null) {
            return null;
        }
        return node.getNodeValue();
    }

    public String getTagValue(String parentTagName, String tagName) {
        NodeList nodes = this.document.getElementsByTagName(parentTagName);
        if (nodes == null || nodes.getLength() == 0) {
            return null;
        }
        for (Node node = nodes.item(0).getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!node.getNodeName().equalsIgnoreCase(tagName)) continue;
            return node.getFirstChild().getNodeValue();
        }
        return null;
    }

    public String toString() {
        try {
            StringWriter writer = new StringWriter();
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty("indent", "yes");
            t.transform(new DOMSource(this.document), new StreamResult(writer));
            return writer.toString();
        }
        catch (Exception e) {
            return this.document.toString();
        }
    }
}

