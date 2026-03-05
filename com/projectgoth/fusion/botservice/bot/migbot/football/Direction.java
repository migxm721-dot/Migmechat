/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.football;

import java.security.SecureRandom;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum Direction {
    UNKNOWN,
    LEFT,
    CENTRE,
    RIGHT;

    private static SecureRandom random;

    public static Direction random() {
        int i = random.nextInt(3);
        if (i == 0) {
            return LEFT;
        }
        if (i == 1) {
            return CENTRE;
        }
        return RIGHT;
    }

    static {
        random = new SecureRandom();
    }
}

