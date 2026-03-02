/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.emailalert;

import com.projectgoth.fusion.emailalert.EmailAlert;
import com.projectgoth.fusion.emailalert.EmailNotificationProcessor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SMTPServer
implements Runnable {
    private Thread socketListenerThread;
    private ServerSocket serverSocket;

    public SMTPServer(int port) throws Exception {
        try {
            this.serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            throw new Exception("Could not listen on port " + port + ": " + e.getMessage());
        }
        this.socketListenerThread = new Thread(this);
        this.socketListenerThread.start();
    }

    public void run() {
        EmailAlert.logger.info((Object)("Ready for a connection on port " + this.serverSocket.getLocalPort()));
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            }
            catch (IOException e) {
                EmailAlert.logger.warn((Object)("Socket accept failed on port " + this.serverSocket.getLocalPort() + ": " + e.getMessage()));
                continue;
            }
            EmailAlert.logger.debug((Object)("Accepted remote connection from " + clientSocket.getInetAddress().getHostAddress()));
            EmailAlert.notificationsThreadPool.execute(new EmailNotificationProcessor(clientSocket));
        }
    }
}

