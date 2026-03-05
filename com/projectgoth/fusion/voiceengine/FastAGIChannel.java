/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.voiceengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.voiceengine.ChannelRequest;
import com.projectgoth.fusion.voiceengine.ChannelResponse;
import com.projectgoth.fusion.voiceengine.FastAGICommand;
import com.projectgoth.fusion.voiceengine.FastAGIServer;
import com.projectgoth.fusion.voiceengine.FastAGIWorker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.log4j.Logger;

public class FastAGIChannel {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FastAGIChannel.class));
    private static final String CHAR_ENCODING = "UTF-8";
    protected FastAGIWorker worker = null;
    protected FastAGIServer server = null;
    protected FastAGICommand command = null;
    protected Socket socket = null;

    public FastAGIChannel(FastAGIWorker worker, FastAGIServer server, FastAGICommand command, Socket socket) {
        this.worker = worker;
        this.server = server;
        this.command = command;
        this.socket = socket;
    }

    public FastAGIWorker getWorker() {
        return this.worker;
    }

    public FastAGICommand getCommand() {
        return this.command;
    }

    protected String prepareAppParam(String param) {
        if (param == null) {
            return "";
        }
        return param;
    }

    public ChannelResponse setParameter(String name, String value) {
        ChannelResponse response = null;
        if (this.command != null) {
            this.command.setParameter(name, value);
            response = new ChannelResponse(200, 0, null);
        }
        return response;
    }

    public ChannelResponse getParameter(String name) {
        ChannelResponse response = null;
        if (this.command != null) {
            response = new ChannelResponse(200, 0, this.command.getParameter(name));
        }
        return response;
    }

    public void writeRequest(ChannelRequest request) {
        if (this.socket == null || !this.socket.isConnected()) {
            log.error((Object)"Invalid socket in writeRequest");
            return;
        }
        if (request == null || request.getCommand() == null) {
            log.error((Object)"Invalid request in writeRequest");
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(request.getCommand());
        if (request.getParam() != null) {
            builder.append(" " + request.getParam() + "");
        }
        if (request.getData() != null) {
            builder.append(" \"" + request.getData() + "\"");
        }
        builder.append("\n");
        log.debug((Object)builder);
        try {
            OutputStream out = this.socket.getOutputStream();
            out.write(builder.toString().getBytes(CHAR_ENCODING));
            out.flush();
        }
        catch (IOException ex) {
            log.error((Object)"Could not write to socket in writeRequest");
        }
    }

    public ChannelResponse readResponse() {
        if (this.socket == null || !this.socket.isConnected()) {
            log.error((Object)"Invalid socket in readResponse");
            return null;
        }
        ChannelResponse response = null;
        try {
            InputStream in = this.socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            int responseCode = 0;
            int returnValue = 0;
            String returnData = null;
            String line = reader.readLine();
            while (line != null && line.length() > 0 && line != "\r" && line != "\r\n") {
                log.debug((Object)line);
                String[] parts = line.split(" ");
                if (parts != null) {
                    String part;
                    if (parts.length > 0 && parts[0] != null) {
                        part = parts[0].trim();
                        try {
                            responseCode = Integer.parseInt(part);
                        }
                        catch (Throwable t) {
                            // empty catch block
                        }
                    }
                    if (parts.length > 1 && parts[1] != null) {
                        part = parts[1].trim();
                        int pos = 0;
                        pos = part.indexOf("=");
                        if (pos >= 0) {
                            part = part.substring(pos + 1);
                            try {
                                returnValue = Integer.parseInt(part);
                            }
                            catch (Throwable t) {
                                // empty catch block
                            }
                        }
                    }
                    if (parts.length > 2 && parts[2] != null) {
                        part = line.trim();
                        int pos = 0;
                        pos = part.indexOf("(");
                        if (pos >= 0 && (pos = (part = part.substring(pos + 1)).lastIndexOf(")")) >= 0) {
                            part = part.substring(0, pos);
                        }
                        returnData = part;
                    }
                    if (responseCode > 0) break;
                }
                line = reader.readLine();
            }
            response = new ChannelResponse(responseCode, returnValue, returnData);
        }
        catch (IOException ex) {
            log.error((Object)"Could not read from to socket in readResponse");
        }
        return response;
    }

    public ChannelResponse execCommand(String command, String param, String data) {
        ChannelRequest request = new ChannelRequest(command, param, data);
        ChannelResponse response = null;
        this.writeRequest(request);
        response = this.readResponse();
        return response;
    }

    public ChannelResponse execChannelStatus() {
        ChannelResponse response = null;
        response = this.execCommand("CHANNEL STATUS", null, null);
        return response;
    }

    public ChannelResponse execApp(String app, String data) {
        ChannelResponse response = null;
        response = this.execCommand("EXEC", app, data);
        return response;
    }

    public ChannelResponse execSetVariable(String name, String value) {
        ChannelResponse response = null;
        response = this.execCommand("SET VARIABLE", name, value);
        return response;
    }

    public ChannelResponse execGetVariable(String name) {
        ChannelResponse response = null;
        response = this.execCommand("GET VARIABLE", name, null);
        return response;
    }

    public ChannelResponse execGetDigit(int maxMillis) {
        ChannelResponse response = null;
        response = this.execCommand("WAIT FOR DIGIT", "" + maxMillis, null);
        return response;
    }

    public ChannelResponse execAppAnswer() {
        ChannelResponse response = null;
        response = this.execApp("ANSWER", null);
        return response;
    }

    public ChannelResponse execAppWait(int seconds) {
        ChannelResponse response = null;
        response = this.execApp("WAIT", this.prepareAppParam("" + seconds));
        return response;
    }

    public ChannelResponse execAppHangup() {
        ChannelResponse response = null;
        response = this.execApp("HANGUP", null);
        return response;
    }

    public ChannelResponse execAppDial(String dialString, int seconds, String options) {
        ChannelResponse response = null;
        response = this.execApp("DIAL", this.prepareAppParam(dialString) + "|" + this.prepareAppParam("" + seconds) + "|" + this.prepareAppParam(options));
        return response;
    }

    public ChannelResponse execAppPlayback(String filename, String options) {
        ChannelResponse response = null;
        response = this.execApp("PLAYBACK", this.prepareAppParam(filename) + "|" + this.prepareAppParam(options));
        return response;
    }

    public ChannelResponse execAppBackground(String filename, String options) {
        ChannelResponse response = null;
        response = this.execApp("BACKGROUND", this.prepareAppParam(filename) + "|" + this.prepareAppParam(options));
        return response;
    }

    public ChannelResponse execAppRead(String variableName, String filename, int maxDigits, String options, int attempts, int maxSeconds) {
        ChannelResponse response = null;
        response = this.execApp("READ", this.prepareAppParam(variableName) + "|" + this.prepareAppParam(filename) + "|" + this.prepareAppParam("" + maxDigits) + "|" + this.prepareAppParam(options) + "|" + this.prepareAppParam("" + attempts) + "|" + this.prepareAppParam("" + maxSeconds));
        return response;
    }

    public ChannelResponse execAppRecord(String filename, String format, int maxSilenceSeconds, int maxSeconds, String options) {
        ChannelResponse response = null;
        response = this.execApp("RECORD", this.prepareAppParam(filename) + "." + this.prepareAppParam(format) + "|" + this.prepareAppParam("" + maxSilenceSeconds) + "|" + this.prepareAppParam("" + maxSeconds) + "|" + this.prepareAppParam(options));
        return response;
    }

    public ChannelResponse execAppSayDigits(String digits) {
        ChannelResponse response = null;
        response = this.execApp("SAYDIGITS", this.prepareAppParam(digits));
        return response;
    }

    public ChannelResponse execAppSayNumber(String number) {
        ChannelResponse response = null;
        response = this.execApp("SAYNUMBER", this.prepareAppParam(number));
        return response;
    }

    public ChannelResponse execAppPlayTones(String toneName) {
        ChannelResponse response = null;
        response = this.execApp("PLAYTONES", this.prepareAppParam(toneName));
        return response;
    }

    public ChannelResponse execAppStopPlayTones() {
        ChannelResponse response = null;
        response = this.execApp("STOPPLAYTONES", null);
        return response;
    }

    public ChannelResponse execAppSetCDRUserField(String value) {
        ChannelResponse response = null;
        response = this.execApp("SETCDRUSERFIELD", value);
        return response;
    }

    public ChannelResponse execAppSIPAddHeader(String value) {
        ChannelResponse response = null;
        response = this.execApp("SIPADDHEADER", value);
        return response;
    }

    public ChannelResponse execAppResetCDR() {
        ChannelResponse response = null;
        response = this.execApp("RESETCDR", null);
        return response;
    }
}

