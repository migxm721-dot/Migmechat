/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jivesoftware.smack.SASLAuthentication
 *  org.jivesoftware.smack.XMPPException
 *  org.jivesoftware.smack.sasl.SASLMechanism
 *  org.jivesoftware.smack.util.Base64
 */
package com.projectgoth.fusion.chat.external.facebook;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.Sasl;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.Base64;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FacebookConnectSASLMechanism
extends SASLMechanism {
    private String accessToken = "";
    private String apiKey = "";

    public FacebookConnectSASLMechanism(SASLAuthentication saslAuthentication) {
        super(saslAuthentication);
    }

    protected void authenticate() throws IOException, XMPPException {
        StringBuilder stanza = new StringBuilder();
        stanza.append("<auth mechanism=\"").append(this.getName());
        stanza.append("\" xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">");
        stanza.append("</auth>");
        this.getSASLAuthentication().send(stanza.toString());
    }

    public void authenticate(String apiKey, String host, String accessToken) throws IOException, XMPPException {
        if (accessToken == null || apiKey == null) {
            throw new IllegalStateException("Invalid parameters!");
        }
        this.accessToken = accessToken;
        this.apiKey = apiKey;
        this.hostname = host;
        String[] mechanisms = new String[]{"DIGEST-MD5"};
        HashMap props = new HashMap();
        this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props, (CallbackHandler)((Object)this));
        this.authenticate();
    }

    protected String getName() {
        return "X-FACEBOOK-PLATFORM";
    }

    public void challengeReceived(String challenge) throws IOException {
        StringBuilder stanza = new StringBuilder();
        byte[] response = null;
        if (challenge != null) {
            String decodedResponse = new String(Base64.decode((String)challenge));
            Map<String, String> parameters = this.getQueryMap(decodedResponse);
            String version = parameters.get("version");
            String nonce = parameters.get("nonce");
            String method = parameters.get("method");
            Long callId = new GregorianCalendar().getTimeInMillis() / 1000L;
            String composedResponse = "api_key=" + URLEncoder.encode(this.apiKey, "utf-8") + "&" + "&call_id=" + callId + "&method=" + URLEncoder.encode(method, "utf-8") + "&nonce=" + URLEncoder.encode(nonce, "utf-8") + "&access_token=" + URLEncoder.encode(this.accessToken, "utf-8") + "&v=" + URLEncoder.encode(version, "utf-8");
            response = composedResponse.getBytes("utf-8");
        }
        String authenticationText = "";
        if (response != null) {
            authenticationText = Base64.encodeBytes(response, (int)8);
        }
        stanza.append("<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">");
        stanza.append(authenticationText);
        stanza.append("</response>");
        this.getSASLAuthentication().send(stanza.toString());
    }

    private Map<String, String> getQueryMap(String query) {
        String[] params;
        HashMap<String, String> map = new HashMap<String, String>();
        for (String param : params = query.split("\\&")) {
            String[] fields = param.split("=", 2);
            map.put(fields[0], fields.length > 1 ? fields[1] : null);
        }
        return map;
    }
}

