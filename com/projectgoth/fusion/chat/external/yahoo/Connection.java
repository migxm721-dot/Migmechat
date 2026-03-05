/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.yahoo;

import com.projectgoth.fusion.chat.external.yahoo.SelectorThread;
import com.projectgoth.fusion.chat.external.yahoo.YMSGPacket;
import com.projectgoth.fusion.chat.external.yahoo.YahooException;
import com.projectgoth.fusion.common.ByteBufferHelper;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Connection
implements Runnable {
    private static final int MIN_BUFFER_SIZE = 2048;
    private static final int MAX_BUFFER_SIZE = 65536;
    private static final int BAD_IP_ADDRESS_PERIOD = 21600000;
    protected int connectionTimeout;
    protected int maxConcurrentConnections;
    private static SecureRandom secureRandom = new SecureRandom();
    private static List<String> goodIPAddresses = new LinkedList<String>();
    private static Map<String, Long> badIPAddresses = new HashMap<String, Long>();
    private static SelectorThread selector;
    private static Object selectorLock;
    private SocketChannel channel;
    private ByteBuffer readBuffer = ByteBuffer.allocate(2048);

    public SocketChannel getSocketChannel() {
        return this.channel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void connect(String server, int port) throws YahooException {
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
            if (selector.getNoOfKeys() > this.maxConcurrentConnections) {
                throw new Exception("Exceeded maximum connection limit");
            }
            server = this.getGoodIPAddress(server, port);
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
            this.updateGoodIPAddresses(server);
            selector.registerConnection(this);
        }
        catch (Exception e) {
            System.out.println("Failed to connect to " + server + ":" + port + " - " + e.getMessage());
            this.updateBadIPAddresses(server);
            this.disconnect(e.getMessage());
            throw new YahooException(e.getMessage());
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getGoodIPAddress(String server, int port) {
        String ipAddress = new InetSocketAddress(server, port).getAddress().getHostAddress();
        List<String> list = goodIPAddresses;
        synchronized (list) {
            Long badServerExpiryTime = badIPAddresses.get(ipAddress);
            if (badServerExpiryTime != null && badServerExpiryTime > System.currentTimeMillis() && goodIPAddresses.size() > 0) {
                ipAddress = goodIPAddresses.get(secureRandom.nextInt(goodIPAddresses.size()));
            }
        }
        return ipAddress;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateGoodIPAddresses(String ipAddress) {
        List<String> list = goodIPAddresses;
        synchronized (list) {
            if (!goodIPAddresses.contains(ipAddress)) {
                goodIPAddresses.add(ipAddress);
            }
            badIPAddresses.remove(ipAddress);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateBadIPAddresses(String ipAddress) {
        List<String> list = goodIPAddresses;
        synchronized (list) {
            goodIPAddresses.remove(ipAddress);
            badIPAddresses.put(ipAddress, System.currentTimeMillis() + 21600000L);
        }
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isConnected();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendAsyncPacket(YMSGPacket packet) {
        try {
            SocketChannel socketChannel = this.channel;
            synchronized (socketChannel) {
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
                    this.disconnect("Connection closed by Yahoo host - make sure you are logged in only once");
                } else if (bytesRead == 0) {
                    this.disconnect("Connection to Yahoo host is lost - make sure you are logged in only once");
                } else if (bytesRead > 0) {
                    while (true) {
                        ByteBuffer buffer = (ByteBuffer)this.readBuffer.duplicate().flip();
                        YMSGPacket packet = new YMSGPacket(buffer);
                        this.readBuffer = buffer.compact();
                        this.onIncomingPacket(packet);
                    }
                }
            }
        }
        catch (BufferUnderflowException e) {
            this.readBuffer = ByteBufferHelper.adjustSize(this.readBuffer, 2048, 65536, 2.0);
            selector.registerConnection(this);
        }
        catch (IOException e) {
            this.disconnect("Failed to read Yahoo packet - " + e.getMessage());
        }
        catch (NotYetConnectedException e) {
            selector.registerConnection(this);
        }
        catch (Exception e) {
            this.disconnect("Connection to Yahoo host is lost - " + e.getMessage());
        }
    }

    protected abstract void onIncomingPacket(YMSGPacket var1);

    protected abstract void onDisconnect(String var1);

    static {
        selectorLock = new Object();
    }
}

