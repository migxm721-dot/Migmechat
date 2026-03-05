/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.trivia;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Score
implements Comparable<Score> {
    String player;
    int points;

    public Score(String player, int points) {
        this.player = player;
        this.points = points;
    }

    public String getPlayer() {
        return this.player;
    }

    public int getScore() {
        return this.points;
    }

    public void incrementScore(int points) {
        this.points += points;
    }

    public boolean equals(Object scoreObj) {
        if (scoreObj == null || !(scoreObj instanceof Score)) {
            return false;
        }
        Score score = (Score)scoreObj;
        return this.player.equals(score.player) && this.points == score.points;
    }

    @Override
    public int compareTo(Score o) {
        Score score = o;
        if (this.points > score.points) {
            return -1;
        }
        return this.points > score.points ? 0 : 1;
    }
}

