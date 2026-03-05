/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.eliza;

import com.projectgoth.fusion.botservice.bot.migbot.eliza.EString;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.PrePost;
import java.util.Vector;

public class PrePostList
extends Vector {
    public void add(String src, String dest) {
        this.addElement(new PrePost(src, dest));
    }

    public void print(int indent) {
        for (int i = 0; i < this.size(); ++i) {
            PrePost p = (PrePost)this.elementAt(i);
            p.print(indent);
        }
    }

    String xlate(String str) {
        for (int i = 0; i < this.size(); ++i) {
            PrePost p = (PrePost)this.elementAt(i);
            if (!str.equals(p.src())) continue;
            return p.dest();
        }
        return str;
    }

    public String translate(String s) {
        String[] lines = new String[2];
        String work = EString.trim(s);
        s = "";
        while (EString.match(work, "* *", lines)) {
            s = s + this.xlate(lines[0]) + " ";
            work = EString.trim(lines[1]);
        }
        s = s + this.xlate(work);
        return s;
    }
}

