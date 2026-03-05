/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.eliza;

import com.projectgoth.fusion.botservice.bot.migbot.eliza.DecompList;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.EString;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.Key;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.KeyStack;
import java.util.Vector;

public class KeyList
extends Vector {
    public void add(String key, int rank, DecompList decomp) {
        this.addElement(new Key(key, rank, decomp));
    }

    public void print(int indent) {
        for (int i = 0; i < this.size(); ++i) {
            Key k = (Key)this.elementAt(i);
            k.print(indent);
        }
    }

    Key getKey(String s) {
        for (int i = 0; i < this.size(); ++i) {
            Key key = (Key)this.elementAt(i);
            if (!s.equals(key.key())) continue;
            return key;
        }
        return null;
    }

    public void buildKeyStack(KeyStack stack, String s) {
        Key k;
        stack.reset();
        s = EString.trim(s);
        String[] lines = new String[2];
        while (EString.match(s, "* *", lines)) {
            k = this.getKey(lines[0]);
            if (k != null) {
                stack.pushKey(k);
            }
            s = lines[1];
        }
        k = this.getKey(s);
        if (k != null) {
            stack.pushKey(k);
        }
    }
}

