/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.voiceengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.voiceengine.AsteriskCommand;
import com.projectgoth.fusion.voiceengine.AsteriskListener;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.naming.AuthenticationException;
import org.apache.log4j.Logger;

public class AsteriskConnection {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AsteriskConnection.class));
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String LINE_TERMINATOR = "\r\n";
    private static final int SOCKET_TIMEOUT = 10000;
    private AsteriskListener listener;
    private Socket socket;
    private BufferedReader reader;
    private Thread readingThread;
    private Thread writingThread;
    private BlockingQueue<AsteriskCommand> commandQueue = new LinkedBlockingQueue<AsteriskCommand>();
    private Set<AsteriskCommand> commandSent = Collections.synchronizedSet(new HashSet());
    private AtomicBoolean isLoggedIn = new AtomicBoolean();

    public AsteriskConnection(AsteriskListener listener) {
        this.listener = listener;
        this.writingThread = new Thread(){

            public void run() {
                AsteriskConnection.this.sendQueuedCommands();
            }
        };
        this.writingThread.start();
        this.readingThread = new Thread(){

            public void run() {
                AsteriskConnection.this.readCommands();
            }
        };
        this.readingThread.start();
    }

    public void connect(String server, int port) throws IOException {
        this.isLoggedIn.set(false);
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(server, port), 10000);
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), DEFAULT_CHARSET));
        this.reader.readLine();
    }

    public void disconnect() {
        this.isLoggedIn.set(false);
        try {
            this.socket.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public boolean isConnected() {
        return this.socket != null && this.readingThread.isAlive() && this.writingThread.isAlive() && this.isLoggedIn.get();
    }

    public void login(String username, String password) throws AuthenticationException, IOException {
        AsteriskCommand command = new AsteriskCommand(AsteriskCommand.Type.ACTION, "Login");
        command.setProperty("Username", username);
        command.setProperty("Secret", password);
        this.sendCommand(command);
        try {
            AsteriskCommand response = this.readCommand();
            if (!"success".equalsIgnoreCase(response.getName())) {
                throw new AuthenticationException(response.getProperty("Message"));
            }
        }
        catch (ParseException e) {
            throw new AuthenticationException(e.getMessage());
        }
        this.isLoggedIn.set(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendCommand(AsteriskCommand command) throws IOException {
        AsteriskCommand asteriskCommand = command;
        synchronized (asteriskCommand) {
            this.commandQueue.add(command);
            try {
                command.wait(10000L);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (!this.commandSent.remove(command)) {
            log.warn((Object)"Failed to send command. Closing connection");
            this.disconnect();
            throw new IOException("Failed to send command");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void sendQueuedCommands() {
        while (true) {
            AsteriskCommand asteriskCommand;
            Object var4_4;
            AsteriskCommand command = null;
            try {
                try {
                    command = this.commandQueue.take();
                    log.debug((Object)("\n" + command.toString()));
                    OutputStream out = this.socket.getOutputStream();
                    out.write(command.toString().getBytes(DEFAULT_CHARSET));
                    out.write(LINE_TERMINATOR.getBytes(DEFAULT_CHARSET));
                    this.commandSent.add(command);
                }
                catch (Exception e) {
                    log.warn((Object)(e.getClass().getName() + " occured in AsteriskConnection.sendQueuedCommands()"), (Throwable)e);
                    var4_4 = null;
                    asteriskCommand = command;
                    synchronized (asteriskCommand) {
                        command.notifyAll();
                        continue;
                    }
                }
                var4_4 = null;
                asteriskCommand = command;
            }
            catch (Throwable throwable) {
                var4_4 = null;
                asteriskCommand = command;
                synchronized (asteriskCommand) {
                    command.notifyAll();
                    throw throwable;
                }
            }
            synchronized (asteriskCommand) {
                command.notifyAll();
            }
        }
    }

    private void readCommands() {
        while (true) {
            try {
                while (true) {
                    if (this.isLoggedIn.get()) {
                        AsteriskCommand command = this.readCommand();
                        switch (command.getType()) {
                            case EVENT: {
                                this.listener.asteriskEventReceived(command);
                                break;
                            }
                        }
                        continue;
                    }
                    Thread.sleep(100L);
                }
            }
            catch (IOException e) {
                this.disconnect();
                this.listener.asteriskDisconnected(e.getMessage());
                continue;
            }
            catch (Exception e) {
                log.error((Object)(e.getClass().getName() + " occured in AsteriskConnection.run()"), (Throwable)e);
                continue;
            }
            break;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private AsteriskCommand readCommand() throws EOFException, ParseException, IOException {
        AsteriskCommand asteriskCommand;
        StringBuilder builder = new StringBuilder();
        try {
            while (true) {
                String line;
                if ((line = this.reader.readLine()) == null) {
                    throw new EOFException();
                }
                if ((line = line.trim()).length() == 0) {
                    if (builder.length() <= 0) continue;
                    asteriskCommand = new AsteriskCommand(builder.toString());
                    Object var5_4 = null;
                    break;
                }
                builder.append(line).append(LINE_TERMINATOR);
            }
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            log.debug((Object)("\n" + builder.toString()));
            throw throwable;
        }
        log.debug((Object)("\n" + builder.toString()));
        return asteriskCommand;
    }
}

