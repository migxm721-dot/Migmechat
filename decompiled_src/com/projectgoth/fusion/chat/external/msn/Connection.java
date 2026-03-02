/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chat.external.msn;

import com.projectgoth.fusion.chat.external.msn.Command;
import com.projectgoth.fusion.chat.external.msn.MSNException;
import com.projectgoth.fusion.chat.external.msn.SelectorThread;
import com.projectgoth.fusion.common.ByteBufferHelper;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

public abstract class Connection
implements Runnable {
    protected static final String DEFAULT_CHARSET = "UTF-8";
    protected static final int COMMAND_TIMEOUT = 20000;
    protected static final int MIN_BUFFER_SIZE = 1024;
    protected static final int MAX_BUFFER_SIZE = 10240;
    private static Logger log = Logger.getLogger(Connection.class);
    private static SelectorThread selector;
    private static Object selectorLock;
    private SocketChannel channel;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private int transactionId;
    private Map<Integer, Command> commandsSent = new ConcurrentHashMap<Integer, Command>();
    private Object connectionLock = new Object();
    protected int connectionTimeout;

    public static Logger getLogger() {
        return log;
    }

    public SocketChannel getSocketChannel() {
        return this.channel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void connect(String server, int port) throws MSNException {
        Object object = this.connectionLock;
        synchronized (object) {
            try {
                Object object2 = selectorLock;
                synchronized (object2) {
                    if (selector == null || !selector.isAlive()) {
                        selector = new SelectorThread();
                        selector.start();
                    }
                }
                if (this.channel != null) {
                    this.disconnect("Reconnecting");
                }
                this.channel = SocketChannel.open();
                this.channel.configureBlocking(false);
                this.channel.connect(new InetSocketAddress(server, port));
                int hundrethsWaited = 0;
                while (!this.channel.finishConnect()) {
                    try {
                        Thread.sleep(100L);
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                    if (++hundrethsWaited < this.connectionTimeout * 10) continue;
                    throw new Exception("Forced timeout");
                }
                selector.registerConnection(this);
                this.transactionId = 0;
                this.commandsSent.clear();
                this.readBuffer.clear();
            }
            catch (Exception e) {
                log.warn((Object)("Failed to connect to " + server + ":" + port + " - " + e.getMessage()));
                this.disconnect(e.getMessage());
                throw new MSNException(e.getMessage());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void disconnect(String reason) {
        Object object = this.connectionLock;
        synchronized (object) {
            if (this.channel != null) {
                try {
                    selector.deregisterConnection(this);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(100L);
                }
                catch (Exception e) {
                    // empty catch block
                }
                try {
                    this.channel.socket().shutdownOutput();
                }
                catch (Exception e) {
                    // empty catch block
                }
                try {
                    this.channel.socket().shutdownInput();
                }
                catch (Exception e) {
                    // empty catch block
                }
                try {
                    this.channel.socket().close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    this.channel.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    this.channel = null;
                }
                selector.wakeup();
                this.onDisconnect(reason);
            }
        }
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isConnected();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Command sendCommand(Command command) throws MSNException {
        if (!this.isConnected()) {
            throw new MSNException("Not connected to MSN server");
        }
        Command command2 = command;
        synchronized (command2) {
            Command reply;
            this.sendAsyncCommand(command);
            if (this.isConnected()) {
                try {
                    command.wait(20000L);
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            if ((reply = command.getReply()) == null) {
                if (this.isConnected()) {
                    throw new MSNException("Time out while waiting for reply from MSN server");
                }
                throw new MSNException("Connection to MSN server is lost");
            }
            if (reply.getType() == Command.Type.ERROR) {
                throw new MSNException(reply.getErrorCode());
            }
            return reply;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void sendAsyncCommand(Command command) {
        try {
            SocketChannel socketChannel = this.channel;
            synchronized (socketChannel) {
                command.setTransactionId(++this.transactionId);
                this.commandsSent.put(this.transactionId, command);
                ByteBuffer buffer = ByteBuffer.wrap(command.getBytes(DEFAULT_CHARSET));
                while (buffer.hasRemaining()) {
                    this.channel.write(buffer);
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)(">>> " + command.toString()));
                }
            }
        }
        catch (Exception e) {
            log.warn((Object)"Error in sendAsyncCommand()", (Throwable)e);
            this.disconnect(e.getMessage());
        }
    }

    public void run() {
        this.onChannelReadable();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void onChannelReadable() {
        block12: {
            try {
                if (this.channel == null) break block12;
                int bytesRead = this.channel.read(this.readBuffer);
                if (bytesRead == -1) {
                    this.disconnect("Connection closed by remote host");
                    break block12;
                }
                if (bytesRead == 0) {
                    log.warn((Object)("0 byte read on port " + this.channel.socket().getLocalPort()));
                    selector.registerConnection(this);
                    break block12;
                }
                if (bytesRead <= 0) break block12;
                while (true) {
                    Command command = this.readCommand();
                    Integer transactionId = command.getTransactionId();
                    Command originalCommand = null;
                    if (transactionId != null && (originalCommand = this.commandsSent.remove(transactionId)) != null) {
                        Command command2 = originalCommand;
                        synchronized (command2) {
                            originalCommand.setReply(command);
                            originalCommand.notifyAll();
                        }
                    }
                    this.onIncomingCommand(command, originalCommand);
                }
            }
            catch (BufferUnderflowException e) {
                this.readBuffer = ByteBufferHelper.adjustSize(this.readBuffer, 1024, 10240, 2.0);
                selector.registerConnection(this);
            }
            catch (IOException e) {
                this.disconnect("Failed to read MSN command - " + e.getMessage());
            }
            catch (NotYetConnectedException e) {
                selector.registerConnection(this);
            }
            catch (Exception e) {
                this.disconnect("Connection to MSN server is lost - " + e.getMessage());
            }
        }
    }

    protected Command readCommand() throws IOException {
        Command command;
        int payloadSize;
        ByteBuffer buffer = (ByteBuffer)this.readBuffer.duplicate().flip();
        String line = ByteBufferHelper.readLine(buffer, DEFAULT_CHARSET).trim();
        if (line.length() == 0) {
            this.readBuffer = buffer.compact();
            throw new BufferUnderflowException();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("<<< " + line));
        }
        if ((payloadSize = (command = new Command(line)).getPayloadSize()) > 0) {
            byte[] payload = ByteBufferHelper.readBytes(buffer, payloadSize);
            command.setPayload(payload);
        }
        this.readBuffer = buffer.compact();
        return command;
    }

    protected abstract void onIncomingCommand(Command var1, Command var2);

    protected abstract void onDisconnect(String var1);

    static {
        selectorLock = new Object();
    }
}

