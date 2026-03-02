/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.mogilefs;

import com.projectgoth.fusion.mogilefs.NoTrackersException;
import com.projectgoth.fusion.mogilefs.SocketWithReaderAndWriter;
import com.projectgoth.fusion.mogilefs.TrackerCommunicationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.IllegalBlockingModeException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

class Backend {
    private static Logger log;
    private List hosts;
    private Map deadHosts;
    private String lastErr;
    private String lastErrStr;
    private SocketWithReaderAndWriter cachedSocket;
    private Pattern ERROR_PATTERN = Pattern.compile("^ERR\\s+(\\w+)\\s*(\\S*)");
    private static final int ERR_PART = 1;
    private static final int ERRSTR_PART = 2;
    private Pattern OK_PATTERN = Pattern.compile("^OK\\s+\\d*\\s*(\\S*)");
    private static final int ARGS_PART = 1;

    public Backend(List trackers, boolean connectNow, Logger logger) throws NoTrackersException {
        log = logger;
        this.reload(trackers, connectNow);
    }

    public void reload(List trackers, boolean connectNow) throws NoTrackersException {
        this.hosts = trackers;
        if (this.hosts.size() == 0) {
            throw new NoTrackersException();
        }
        this.deadHosts = new HashMap();
        this.lastErr = null;
        this.lastErrStr = null;
        this.cachedSocket = null;
        if (connectNow) {
            this.cachedSocket = this.getSocket();
        }
    }

    private SocketWithReaderAndWriter getSocket() throws NoTrackersException {
        int hostSize = this.hosts.size();
        int tries = hostSize > 15 ? 15 : hostSize;
        int index = (int)Math.floor((double)this.hosts.size() * Math.random());
        long now = System.currentTimeMillis();
        while (tries-- > 0) {
            InetSocketAddress host;
            Long deadTime;
            if ((deadTime = (Long)this.deadHosts.get(host = (InetSocketAddress)this.hosts.get(index++ % hostSize))) != null && deadTime > now - 5000L) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)(" skipping connect attempt to dead host " + host));
                continue;
            }
            try {
                Socket socket = new Socket();
                socket.setSoTimeout(30000);
                socket.connect(host, 3000);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("connected to tracker " + socket.getInetAddress().getHostName()));
                }
                return new SocketWithReaderAndWriter(socket);
            }
            catch (IOException e) {
                log.warn((Object)("Unable to connect to tracker at " + host.toString()), (Throwable)e);
            }
            catch (IllegalBlockingModeException e) {
                log.warn((Object)("Unable to connect to tracker at " + host.toString()), (Throwable)e);
            }
            catch (IllegalArgumentException e) {
                log.warn((Object)("Unable to connect to tracker " + host.toString()), (Throwable)e);
            }
            log.warn((Object)("marking host " + host + " as dead"));
            this.deadHosts.put(host, new Long(now));
        }
        throw new NoTrackersException();
    }

    public synchronized Map doRequest(String command, String[] args) throws NoTrackersException, TrackerCommunicationException {
        if (command == null || args == null) {
            log.error((Object)"null command or args sent to doRequest");
            return null;
        }
        String argString = this.encodeURLString(args);
        String request = command + " " + argString + "\r\n";
        if (log.isDebugEnabled()) {
            log.debug((Object)("command: " + request));
        }
        if (this.cachedSocket != null) {
            try {
                this.cachedSocket.getWriter().write(request);
                this.cachedSocket.getWriter().flush();
            }
            catch (IOException e) {
                log.debug((Object)"cached socket went bad while sending request");
                this.cachedSocket = null;
            }
        }
        if (this.cachedSocket == null) {
            SocketWithReaderAndWriter socket = this.getSocket();
            try {
                socket.getWriter().write(request);
                socket.getWriter().flush();
            }
            catch (IOException e) {
                throw new TrackerCommunicationException("problem finding a working tracker in this list: " + this.listKnownTrackers());
            }
            this.cachedSocket = socket;
        }
        try {
            Matcher ok;
            String response = this.cachedSocket.getReader().readLine();
            if (response == null) {
                throw new TrackerCommunicationException("received null response from tracker at " + this.cachedSocket.getSocket().getInetAddress());
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("response: " + response));
            }
            if ((ok = this.OK_PATTERN.matcher(response)).matches()) {
                return this.decodeURLString(ok.group(1));
            }
            Matcher err = this.ERROR_PATTERN.matcher(response);
            if (err.matches()) {
                this.lastErr = err.group(1);
                this.lastErrStr = err.group(2);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("error message from tracker: " + this.lastErr + ", " + this.lastErrStr));
                }
                return null;
            }
            throw new TrackerCommunicationException("invalid server response from " + this.cachedSocket.getSocket().getInetAddress() + ": " + response);
        }
        catch (IOException e) {
            log.warn((Object)("problem reading response from server (" + this.cachedSocket.getSocket().getInetAddress() + ")"), (Throwable)e);
            throw new TrackerCommunicationException("problem talking to server at " + this.cachedSocket.getSocket().getInetAddress(), e);
        }
    }

    public String getLastErr() {
        return this.lastErr;
    }

    public String getLastErrStr() {
        return this.lastErrStr;
    }

    private String listKnownTrackers() {
        StringBuffer trackers = new StringBuffer();
        for (InetSocketAddress host : this.hosts) {
            if (trackers.length() > 0) {
                trackers.append(", ");
            }
            trackers.append(host.toString());
        }
        return trackers.toString();
    }

    private String encodeURLString(String[] args) {
        try {
            StringBuffer encoded = new StringBuffer();
            for (int i = 0; i < args.length; i += 2) {
                String key = args[i];
                String value = args[i + 1];
                if (encoded.length() > 0) {
                    encoded.append("&");
                }
                encoded.append(key);
                encoded.append("=");
                encoded.append(URLEncoder.encode(value, "UTF-8"));
            }
            return encoded.toString();
        }
        catch (UnsupportedEncodingException e) {
            log.error((Object)"problem encoding URL for tracker", (Throwable)e);
            return null;
        }
    }

    private Map decodeURLString(String encoded) {
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            if (encoded == null || encoded.length() == 0) {
                return map;
            }
            String[] parts = encoded.split("&");
            for (int i = 0; i < parts.length; ++i) {
                String[] pair = parts[i].split("=");
                if (pair == null || pair.length != 2) {
                    log.error((Object)("poorly encoded string: " + encoded));
                    continue;
                }
                map.put(pair[0], URLDecoder.decode(pair[1], "UTF-8"));
            }
            return map;
        }
        catch (UnsupportedEncodingException e) {
            log.error((Object)"problem decoding URL from tracker", (Throwable)e);
            return null;
        }
    }

    public String getTracker() {
        if (this.cachedSocket == null) {
            return null;
        }
        return this.cachedSocket.getTracker();
    }
}

