/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.msn;

import com.projectgoth.fusion.chat.external.msn.Connection;
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

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SelectorThread
extends Thread {
    private static final int THREAD_POOL_SIZE = 20;
    private Selector selector = Selector.open();
    private List<Connection> pendingConnections = new ArrayList<Connection>();
    private ExecutorService pool = Executors.newFixedThreadPool(20);

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
            } else if (key.isValid()) {
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
                    SocketChannel channel = connection.getSocketChannel();
                    if (channel == null) continue;
                    channel.register(this.selector, 1, connection);
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

    @Override
    public void run() {
        try {
            while (true) {
                if (this.selector.select() > 0) {
                    this.processSelections(this.selector.selectedKeys());
                }
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
}

