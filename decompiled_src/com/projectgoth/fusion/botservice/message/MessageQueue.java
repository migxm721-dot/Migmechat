/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.message;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.message.Message;
import com.projectgoth.fusion.common.ConfigUtils;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;
import org.apache.log4j.Logger;

public class MessageQueue
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MessageQueue.class));
    private Bot bot;
    private Executor executor;
    private Queue<Message> messages = new LinkedList<Message>();

    public MessageQueue(Bot bot, Executor executor) {
        this.bot = bot;
        this.executor = executor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        Message message;
        Queue<Message> queue = this.messages;
        synchronized (queue) {
            message = this.messages.peek();
            if (message == null) {
                return;
            }
        }
        try {
            message.dispatch(this.bot);
        }
        catch (Exception e) {
            String botName = "";
            if (this.bot != null) {
                botName = this.bot.getBotData() != null ? this.bot.getBotData().getDisplayName() : "";
            }
            log.warn((Object)("Unexpected exception while dispatching message for " + botName), (Throwable)e);
        }
        queue = this.messages;
        synchronized (queue) {
            this.messages.poll();
            if (this.messages.size() > 0) {
                this.executor.execute(this);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void queue(Message message) {
        Queue<Message> queue = this.messages;
        synchronized (queue) {
            this.messages.add(message);
            if (this.messages.size() == 1) {
                this.executor.execute(this);
            }
        }
    }
}

