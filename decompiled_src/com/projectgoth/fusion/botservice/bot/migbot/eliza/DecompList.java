/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.eliza;

import com.projectgoth.fusion.botservice.bot.migbot.eliza.Decomp;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.ReasembList;
import java.util.Vector;

public class DecompList
extends Vector {
    public void add(String word, boolean mem, ReasembList reasmb) {
        this.addElement(new Decomp(word, mem, reasmb));
    }

    public void print(int indent) {
        for (int i = 0; i < this.size(); ++i) {
            Decomp d = (Decomp)this.elementAt(i);
            d.print(indent);
        }
    }
}

