/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.aim;

import com.projectgoth.fusion.chat.external.aim.AIMException;
import com.projectgoth.fusion.chat.external.aim.FLAP;
import com.projectgoth.fusion.chat.external.aim.SelectorThread;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;

public abstract class Connection
implements Runnable {
    protected static final String DEFAULT_CHARSET = "UTF-8";
    protected static final int COMMAND_TIMEOUT = 5000;
    protected static final int BUFFER_SIZE = 4096;
    private static SelectorThread selector;
    private static Object selectorLock;
    private SocketChannel channel;
    private ByteBuffer readBuffer = ByteBuffer.allocate(4096);
    private short nextSequence;
    protected int connectionTimeout;

    public SocketChannel getSocketChannel() {
        return this.channel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void connect(String server, int port) throws AIMException {
        try {
            Object object = selectorLock;
            synchronized (object) {
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
        }
        catch (Exception e) {
            System.out.println("Failed to connect to " + server + ":" + port + " - " + e.getMessage());
            this.disconnect(e.getMessage());
            throw new AIMException(e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void disconnect(String reason) {
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
                try {
                    this.channel.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Object var4_10 = null;
                    this.channel = null;
                }
                Object var4_9 = null;
                this.channel = null;
            }
            catch (Throwable throwable) {
                Object var4_11 = null;
                this.channel = null;
                throw throwable;
            }
            selector.wakeup();
            this.readBuffer.clear();
            this.onDisconnect(reason);
        }
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isConnected();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendAsyncPacket(FLAP packet) {
        try {
            SocketChannel socketChannel = this.channel;
            synchronized (socketChannel) {
                short s = this.nextSequence;
                this.nextSequence = (short)(s + 1);
                packet.setSequence(s);
                ByteBuffer buffer = ByteBuffer.wrap(packet.toByteArray());
                while (buffer.hasRemaining()) {
                    this.channel.write(buffer);
                }
            }
        }
        catch (Exception e) {
            this.disconnect(e.getMessage());
        }
    }

    public void run() {
        this.onChannelReadable();
    }

    public synchronized void onChannelReadable() {
        try {
            if (this.channel != null) {
                int bytesRead = this.channel.read(this.readBuffer);
                if (bytesRead == -1) {
                    this.disconnect("Connection closed by remote host");
                } else if (bytesRead == 0) {
                    this.disconnect("Connection to AIM server is lost - 0 byte read");
                } else if (bytesRead > 0) {
                    while (true) {
                        ByteBuffer buffer = (ByteBuffer)this.readBuffer.duplicate().flip();
                        FLAP packet = new FLAP(buffer);
                        this.readBuffer = buffer.compact();
                        this.onIncomingPacket(packet);
                    }
                }
            }
        }
        catch (BufferUnderflowException e) {
            selector.registerConnection(this);
        }
        catch (IOException e) {
            this.disconnect("Failed to read AIM packet - " + e.getMessage());
        }
        catch (NotYetConnectedException e) {
            selector.registerConnection(this);
        }
        catch (Exception e) {
            this.disconnect("Connection to AIM server is lost - " + e.getMessage());
        }
    }

    protected abstract void onIncomingPacket(FLAP var1);

    protected abstract void onDisconnect(String var1);

    static {
        selectorLock = new Object();
    }
}

