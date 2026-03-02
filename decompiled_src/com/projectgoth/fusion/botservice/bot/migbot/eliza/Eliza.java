/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.eliza;

import com.projectgoth.fusion.botservice.bot.migbot.eliza.Decomp;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.DecompList;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.EString;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.Key;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.KeyList;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.KeyStack;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.Mem;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.PrePostList;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.ReasembList;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.SynList;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.WordList;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Eliza {
    final boolean echoInput = false;
    final boolean printData = false;
    final boolean printKeys = false;
    final boolean printSyns = false;
    final boolean printPrePost = false;
    final boolean printInitialFinal = false;
    KeyList keys = new KeyList();
    SynList syns = new SynList();
    PrePostList pre = new PrePostList();
    PrePostList post = new PrePostList();
    String initial = "Hello.";
    String finl = "Goodbye.";
    WordList quit = new WordList();
    KeyStack keyStack = new KeyStack();
    Mem mem = new Mem();
    DecompList lastDecomp;
    ReasembList lastReasemb;
    boolean finished = false;
    static final int success = 0;
    static final int failure = 1;
    static final int gotoRule = 2;

    public boolean finished() {
        return this.finished;
    }

    public void collect(String s) {
        String[] lines = new String[4];
        if (EString.match(s, "*reasmb: *", lines)) {
            if (this.lastReasemb == null) {
                System.out.println("Error: no last reasemb");
                return;
            }
            this.lastReasemb.add(lines[1]);
        } else if (EString.match(s, "*decomp: *", lines)) {
            if (this.lastDecomp == null) {
                System.out.println("Error: no last decomp");
                return;
            }
            this.lastReasemb = new ReasembList();
            String temp = lines[1];
            if (EString.match(temp, "$ *", lines)) {
                this.lastDecomp.add(lines[0], true, this.lastReasemb);
            } else {
                this.lastDecomp.add(temp, false, this.lastReasemb);
            }
        } else if (EString.match(s, "*key: * #*", lines)) {
            this.lastDecomp = new DecompList();
            this.lastReasemb = null;
            int n = 0;
            if (lines[2].length() != 0) {
                try {
                    n = Integer.parseInt(lines[2]);
                }
                catch (NumberFormatException e) {
                    System.out.println("Number is wrong in key: " + lines[2]);
                }
            }
            this.keys.add(lines[1], n, this.lastDecomp);
        } else if (EString.match(s, "*key: *", lines)) {
            this.lastDecomp = new DecompList();
            this.lastReasemb = null;
            this.keys.add(lines[1], 0, this.lastDecomp);
        } else if (EString.match(s, "*synon: * *", lines)) {
            WordList words = new WordList();
            words.add(lines[1]);
            s = lines[2];
            while (EString.match(s, "* *", lines)) {
                words.add(lines[0]);
                s = lines[1];
            }
            words.add(s);
            this.syns.add(words);
        } else if (EString.match(s, "*pre: * *", lines)) {
            this.pre.add(lines[1], lines[2]);
        } else if (EString.match(s, "*post: * *", lines)) {
            this.post.add(lines[1], lines[2]);
        } else if (EString.match(s, "*initial: *", lines)) {
            this.initial = lines[1];
        } else if (EString.match(s, "*final: *", lines)) {
            this.finl = lines[1];
        } else if (EString.match(s, "*quit: *", lines)) {
            this.quit.add(" " + lines[1] + " ");
        } else {
            System.out.println("Unrecognized input: " + s);
        }
    }

    public void print() {
    }

    public String processInput(String s) {
        Key dummy;
        String reply;
        s = EString.translate(s, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz");
        s = EString.translate(s, "@#$%^&*()_-+=~`{[}]|:;<>\\\"", "                          ");
        s = EString.translate(s, ",?!", "...");
        s = EString.compress(s);
        String[] lines = new String[2];
        while (EString.match(s, "*.*", lines)) {
            reply = this.sentence(lines[0]);
            if (reply != null) {
                return reply;
            }
            s = EString.trim(lines[1]);
        }
        if (s.length() != 0 && (reply = this.sentence(s)) != null) {
            return reply;
        }
        String m = this.mem.get();
        if (m != null) {
            return m;
        }
        Key key = this.keys.getKey("xnone");
        if (key != null && (reply = this.decompose(key, s, dummy = null)) != null) {
            return reply;
        }
        return "I am at a loss for words.";
    }

    String sentence(String s) {
        s = this.pre.translate(s);
        if (this.quit.find(s = EString.pad(s))) {
            this.finished = true;
            return this.finl;
        }
        this.keys.buildKeyStack(this.keyStack, s);
        for (int i = 0; i < this.keyStack.keyTop(); ++i) {
            Key gotoKey = new Key();
            String reply = this.decompose(this.keyStack.key(i), s, gotoKey);
            if (reply != null) {
                return reply;
            }
            while (gotoKey.key() != null) {
                reply = this.decompose(gotoKey, s, gotoKey);
                if (reply == null) continue;
                return reply;
            }
        }
        return null;
    }

    String decompose(Key key, String s, Key gotoKey) {
        String[] reply = new String[10];
        for (int i = 0; i < key.decomp().size(); ++i) {
            Decomp d = (Decomp)key.decomp().elementAt(i);
            String pat = d.pattern();
            if (!this.syns.matchDecomp(s, pat, reply)) continue;
            String rep = this.assemble(d, reply, gotoKey);
            if (rep != null) {
                return rep;
            }
            if (gotoKey.key() == null) continue;
            return null;
        }
        return null;
    }

    String assemble(Decomp d, String[] reply, Key gotoKey) {
        String[] lines = new String[3];
        d.stepRule();
        String rule = d.nextRule();
        if (EString.match(rule, "goto *", lines)) {
            gotoKey.copy(this.keys.getKey(lines[0]));
            if (gotoKey.key() != null) {
                return null;
            }
            System.out.println("Goto rule did not match key: " + lines[0]);
            return null;
        }
        String work = "";
        while (EString.match(rule, "* (#)*", lines)) {
            rule = lines[2];
            int n = 0;
            try {
                n = Integer.parseInt(lines[1]) - 1;
            }
            catch (NumberFormatException e) {
                System.out.println("Number is wrong in reassembly rule " + lines[1]);
            }
            if (n < 0 || n >= reply.length) {
                System.out.println("Substitution number is bad " + lines[1]);
                return null;
            }
            reply[n] = this.post.translate(reply[n]);
            work = work + lines[0] + " " + reply[n];
        }
        work = work + rule;
        if (d.mem()) {
            this.mem.save(work);
            return null;
        }
        return work;
    }

    public int readScript(boolean local, String script) {
        try {
            String s;
            BufferedReader reader;
            if (local) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(script)));
            } else {
                try {
                    URL url = new URL(script);
                    URLConnection connection = url.openConnection();
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                }
                catch (MalformedURLException e) {
                    System.out.println("The URL is malformed: " + script);
                    return 1;
                }
                catch (IOException e) {
                    System.out.println("Could not read script file.");
                    return 1;
                }
            }
            while ((s = reader.readLine()) != null) {
                this.collect(s);
            }
        }
        catch (IOException e) {
            System.out.println("There was a problem reading the script file.");
            System.out.println("Tried " + script);
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) {
        try {
            Eliza eliza = new Eliza();
            eliza.readScript(true, "/Users/koko/Desktop/Eliza.txt");
            System.out.println(eliza.processInput("hello"));
            System.out.println(eliza.processInput("i am a moose"));
            System.out.println(eliza.processInput("hello"));
            System.out.println(eliza.processInput("hello"));
            System.out.println(eliza.processInput("hello"));
            System.out.println(eliza.processInput("my name is koko"));
            System.out.println(eliza.processInput("how are you today"));
            System.out.println(eliza.processInput("i don't like you"));
            System.out.println(eliza.processInput("can you be my boy friend"));
            System.out.println(eliza.processInput("yes"));
            System.out.println(eliza.processInput("of course"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

