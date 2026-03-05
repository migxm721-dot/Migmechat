/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.eliza;

import java.util.Vector;

public class WordList
extends Vector {
    public void add(String word) {
        this.addElement(word);
    }

    public void print(int indent) {
        for (int i = 0; i < this.size(); ++i) {
            String s = (String)this.elementAt(i);
            System.out.print(s + "  ");
        }
        System.out.println();
    }

    boolean find(String s) {
        for (int i = 0; i < this.size(); ++i) {
            if (!s.equals((String)this.elementAt(i))) continue;
            return true;
        }
        return false;
    }
}

