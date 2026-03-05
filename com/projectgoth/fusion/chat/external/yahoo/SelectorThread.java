/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.yahoo;

import com.projectgoth.fusion.chat.external.yahoo.Connection;
import com.projectgoth.fusion.chat.external.yahoo.YahooConnection;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SelectorThread
extends Thread {
    private static final int SELECT_TIMEOUT = 1000;
    private static final int PING_INTERVAL = 900000;
    private static final int THREAD_POOL_SIZE = 20;
    private Selector selector;
    private List<Connection> pendingConnections;
    private ExecutorService pool;
    private long nextPing;
    private AtomicInteger noOfKeys = new AtomicInteger();

    public SelectorThread() throws IOException {
        this.selector = Selector.open();
        this.pendingConnections = new ArrayList<Connection>();
        this.pool = Executors.newFixedThreadPool(20);
        this.nextPing = System.currentTimeMillis() + 900000L;
    }

    public int getNoOfKeys() {
        return this.noOfKeys.get();
    }

    public void wakeup() {
        this.selector.wakeup();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerConnection(Connection connection) {
        SocketChannel channel = connection.getSocketChannel();
        if (channel != null) {
            SelectionKey key = channel.keyFor(this.selector);
            if (key == null) {
                List<Connection> list = this.pendingConnections;
                synchronized (list) {
                    if (!this.pendingConnections.contains(connection)) {
                        this.pendingConnections.add(connection);
                        this.selector.wakeup();
                    }
                }
            } else {
                key.interestOps(1);
                this.selector.wakeup();
            }
        }
    }

    public void deregisterConnection(Connection connection) {
        SelectionKey key;
        SocketChannel channel = connection.getSocketChannel();
        if (channel != null && (key = channel.keyFor(this.selector)) != null) {
            key.cancel();
            this.selector.wakeup();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void registerPendingConnections() {
        List<Connection> list = this.pendingConnections;
        synchronized (list) {
            for (Connection connection : this.pendingConnections) {
                try {
                    connection.getSocketChannel().register(this.selector, 1, connection);
                }
                catch (ClosedChannelException e) {
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.pendingConnections.clear();
        }
    }

    private void processSelections(Set<SelectionKey> selectedKeys) {
        for (SelectionKey key : selectedKeys) {
            try {
                Connection conn = (Connection)key.attachment();
                if (conn == null) continue;
                if (key.isValid()) {
                    key.interestOps(0);
                }
                this.pool.execute(conn);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        selectedKeys.clear();
    }

    private void keepConnectionsAlive() {
        if (System.currentTimeMillis() > this.nextPing) {
            this.nextPing += 900000L;
            ArrayList<YahooConnection> connections = new ArrayList<YahooConnection>();
            for (SelectionKey key : this.selector.keys()) {
                YahooConnection conn;
                if (!key.isValid() || (conn = (YahooConnection)key.attachment()) == null) continue;
                connections.add(conn);
            }
            this.pool.execute(new KeepAliveTask(connections));
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (this.selector.select(1000L) > 0) {
                    this.processSelections(this.selector.selectedKeys());
                }
                this.noOfKeys.set(this.selector.keys().size());
                this.keepConnectionsAlive();
                this.registerPendingConnections();
                Thread.sleep(10L);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                this.selector.close();
            }
            catch (Exception exception) {}
            return;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class KeepAliveTask
    implements Runnable {
        private List<YahooConnection> connections;

        public KeepAliveTask(List<YahooConnection> connections) {
            this.connections = connections;
        }

        @Override
        public void run() {
            for (YahooConnection conn : this.connections) {
                conn.ping();
            }
        }
    }
}

