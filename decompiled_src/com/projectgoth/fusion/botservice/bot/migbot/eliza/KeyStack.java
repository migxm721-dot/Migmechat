/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.eliza;

import com.projectgoth.fusion.botservice.bot.migbot.eliza.Key;

public class KeyStack {
    final int stackSize = 20;
    Key[] keyStack = new Key[20];
    int keyTop = 0;

    public void print() {
        System.out.println("Key stack " + this.keyTop);
        for (int i = 0; i < this.keyTop; ++i) {
            this.keyStack[i].printKey(0);
        }
    }

    public int keyTop() {
        return this.keyTop;
    }

    public void reset() {
        this.keyTop = 0;
    }

    public Key key(int n) {
        if (n < 0 || n >= this.keyTop) {
            return null;
        }
        return this.keyStack[n];
    }

    public void pushKey(Key key) {
        if (key == null) {
            System.out.println("push null key");
            return;
        }
        for (int i = this.keyTop; i > 0 && key.rank > this.keyStack[i - 1].rank; --i) {
            this.keyStack[i] = this.keyStack[i - 1];
        }
        this.keyStack[i] = key;
        ++this.keyTop;
    }
}

