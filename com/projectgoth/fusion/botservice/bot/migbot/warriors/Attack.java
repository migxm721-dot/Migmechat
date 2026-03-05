/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.warriors;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class Attack
implements Comparable<Attack> {
    String name;
    int min;
    int max;
    double probability;
    String description;
    String attackEmote;

    Attack(String name, String description, int min, int max, double probability, String attackEmote) {
        this.description = description;
        this.name = name;
        this.min = min;
        this.max = max;
        this.probability = probability;
        this.attackEmote = attackEmote;
    }

    public int getRandomizedAttackDamage() {
        return (int)((double)this.min + Math.random() * (double)(this.max - this.min));
    }

    @Override
    public int compareTo(Attack att) {
        return (int)(100.0 * (this.probability - att.probability));
    }

    public String getAttackEmote() {
        return this.attackEmote;
    }
}

