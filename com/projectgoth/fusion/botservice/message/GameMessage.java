/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.message;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.message.Message;

public class GameMessage
extends Message {
    private String username;
    private String message;
    private long receivedTimestamp;

    public GameMessage(String username, String message, long receivedTimestamp) {
        this.username = username;
        this.message = message;
        this.receivedTimestamp = receivedTimestamp;
    }

    public void dispatch(Bot bot) {
        bot.onMessage(this.username, this.message, this.receivedTimestamp);
    }
}

