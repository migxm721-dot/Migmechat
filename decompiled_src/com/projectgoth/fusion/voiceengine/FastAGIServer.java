/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.voiceengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.voiceengine.CallMakerI;
import com.projectgoth.fusion.voiceengine.FastAGIWorker;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

public class FastAGIServer
extends Thread {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FastAGIServer.class));
    private ServerSocket serverSocket;
    private int port;
    private Executor pool;
    private CallMakerI callMaker;
    private int callThroughValidPeriod;

    public FastAGIServer(CallMakerI callMaker, int port, int threads) throws IOException {
        this.callMaker = callMaker;
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setReuseAddress(true);
        this.pool = Executors.newFixedThreadPool(threads);
    }

    public int getCallThroughValidPeriod() {
        return this.callThroughValidPeriod;
    }

    public void setCallThroughValidPeriod(int callThroughValidPeriod) {
        this.callThroughValidPeriod = callThroughValidPeriod;
    }

    public void run() {
        while (true) {
            try {
                while (true) {
                    this.pool.execute(new FastAGIWorker(this, this.callMaker, this.serverSocket.accept()));
                }
            }
            catch (IOException e) {
                log.warn((Object)("Failed to accept FastAGI client connections - " + e.getClass().getName() + ":" + e.getMessage()));
                try {
                    this.serverSocket.close();
                    this.serverSocket = new ServerSocket(this.port);
                    this.serverSocket.setReuseAddress(true);
                    continue;
                }
                catch (IOException ie) {
                    log.fatal((Object)("Failed to restart FastAGI server socket - " + e.getClass().getName() + ":" + e.getMessage()));
                    continue;
                }
            }
            break;
        }
    }
}

