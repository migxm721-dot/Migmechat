/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.rockpaperscissors;

import java.security.SecureRandom;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum Hand {
    CLOSED("(rps_closed)"),
    ROCK("(rps_rock)"),
    PAPER("(rps_paper)"),
    SCISSORS("(rps_scissors)");

    private static SecureRandom random;
    private String emoticonKey;

    private Hand(String emoticonKey) {
        this.emoticonKey = emoticonKey;
    }

    public String getEmoticonKey() {
        return this.emoticonKey;
    }

    public static Hand random() {
        int i = random.nextInt(3);
        if (i == 0) {
            return ROCK;
        }
        if (i == 1) {
            return PAPER;
        }
        return SCISSORS;
    }

    static {
        random = new SecureRandom();
    }
}

