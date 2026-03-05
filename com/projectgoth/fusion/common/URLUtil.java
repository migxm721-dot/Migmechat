/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.fdl.enums.ClientType;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class URLUtil {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(URLUtil.class));
    private static final Pattern PATTERN_URL_HOSTNAME_PREFIX = Pattern.compile("https?://[^/]+");

    public static String replaceViewTypeToken(String url, ClientType deviceType) {
        if (!StringUtil.isBlank(url)) {
            url = ClientType.ANDROID == deviceType || ClientType.WINDOWS_MOBILE == deviceType || ClientType.BLACKBERRY == deviceType ? url.replaceAll("%1", "touch") : (ClientType.MRE == deviceType ? url.replaceAll("%1", "mre") : (ClientType.IOS == deviceType ? url.replaceAll("%1", "ios") : url.replaceAll("%1", "midlet")));
        }
        return url;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public static boolean purgeVanishCache(String uri) {
        block12: {
            Socket socket = null;
            String payload = "PURGE " + uri + " HTTP/1.1\r\n\r\n";
            String serverAddress = SystemProperty.get("Varnish01HTTPServer", null);
            if (StringUtil.isBlank(serverAddress)) {
                return false;
            }
            socket = new Socket();
            InetSocketAddress endPoint = new InetSocketAddress(serverAddress, 80);
            socket.connect(endPoint, 1000);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(payload);
            bufferedWriter.flush();
            bufferedWriter.close();
            Object var7_8 = null;
            try {
                if (socket != null) {
                    socket.close();
                }
                break block12;
            }
            catch (IOException e2) {}
            break block12;
            {
                catch (Exception e) {
                    log.error((Object)e.getMessage());
                    boolean bl = false;
                    Object var7_9 = null;
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                    }
                    catch (IOException e2) {
                        // empty catch block
                    }
                    return bl;
                }
            }
            catch (Throwable throwable) {
                Object var7_10 = null;
                try {
                    if (socket != null) {
                        socket.close();
                    }
                }
                catch (IOException e2) {
                    // empty catch block
                }
                throw throwable;
            }
        }
        return true;
    }

    public static String replaceUrlHost(String url, String replaceUrl) {
        Matcher m = PATTERN_URL_HOSTNAME_PREFIX.matcher(url);
        url = m.replaceFirst(replaceUrl);
        return url;
    }
}

