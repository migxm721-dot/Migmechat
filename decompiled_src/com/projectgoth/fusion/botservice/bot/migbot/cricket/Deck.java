/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.cricket;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Deck {
    private LinkedList<Card> deck = new LinkedList();
    private HashMap<Card, Integer> combination = new HashMap(12);

    public Deck() {
        this.combination.put(Card.ONE, 45);
        this.combination.put(Card.TWO, 39);
        this.combination.put(Card.THREE, 3);
        this.combination.put(Card.FOUR, 21);
        this.combination.put(Card.SIX, 12);
        this.combination.put(Card.BOWLED, 4);
        this.combination.put(Card.STUMPED, 2);
        this.combination.put(Card.CATCH, 6);
        this.combination.put(Card.HIT_WICKET, 1);
        this.combination.put(Card.LBW, 3);
        this.combination.put(Card.RUN_OUT, 2);
        this.combination.put(Card.THIRD_UMPIRE, 3);
    }

    public void loadConfig() {
    }

    public void init() {
        for (Map.Entry<Card, Integer> e : this.combination.entrySet()) {
            Card card = e.getKey();
            int qty = e.getValue();
            for (int i = 0; i < qty; ++i) {
                this.deck.add(card);
            }
        }
        this.shuffle();
    }

    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    public Card draw() {
        if (this.deck.size() <= 0) {
            return null;
        }
        return this.deck.poll();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Card {
        ONE("1", "One", "(cr1)"),
        TWO("2", "Two", "(cr2)"),
        THREE("3", "Three", "(cr3)"),
        FOUR("4", "Four", "(cr4)"),
        SIX("6", "Six", "(cr6)"),
        BOWLED("O", "Bowled", "(crBowled)"),
        STUMPED("O", "Stumped", "(crStumped)"),
        CATCH("O", "Catch", "(crCatch)"),
        HIT_WICKET("O", "Hit Wicket", "(crHitWicket)"),
        LBW("O", "Leg Before Wicket", "(crLBW)"),
        RUN_OUT("O", "Run Out", "(crRunOut)"),
        THIRD_UMPIRE("U", "Third Umpire", "(crThirdUmpire)");

        private String type;
        private String name;
        private String emoticonKey;

        private Card(String type, String name, String emoticonKey) {
            this.type = type;
            this.name = name;
            this.emoticonKey = emoticonKey;
        }

        public String getType() {
            return this.type;
        }

        public String getName() {
            return this.name;
        }

        public String getEmoticonKey() {
            return this.emoticonKey;
        }
    }
}

