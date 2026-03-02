/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.one;

import com.projectgoth.fusion.botservice.bot.migbot.one.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private String name = "";
    private List<Card> cards = new ArrayList<Card>();
    boolean calledUno = false;

    public Player(String name) {
        this.name = name;
    }

    public void addCard(Card card) {
        if (null == card) {
            return;
        }
        this.cards.add(card);
    }

    public List getCards() {
        return this.cards;
    }

    public void setCalledUno(boolean called) {
        this.calledUno = called;
    }

    public boolean hasCalledUno() {
        return this.calledUno;
    }

    public String toString() {
        StringBuffer playersCards = new StringBuffer(this.name + ": (" + this.cards.size() + " cards) ");
        for (Card card : this.cards) {
            playersCards.append(" ");
            playersCards.append(card.toString());
        }
        return playersCards.toString();
    }

    public List getHand() {
        ArrayList<Card> blue = new ArrayList<Card>();
        ArrayList<Card> green = new ArrayList<Card>();
        ArrayList<Card> red = new ArrayList<Card>();
        ArrayList<Card> yellow = new ArrayList<Card>();
        ArrayList<Card> wild = new ArrayList<Card>();
        for (Card card : this.cards) {
            switch (card.getColour()) {
                case 1: {
                    blue.add(card);
                    break;
                }
                case 2: {
                    green.add(card);
                    break;
                }
                case 3: {
                    red.add(card);
                    break;
                }
                case 4: {
                    yellow.add(card);
                    break;
                }
                case 10: {
                    wild.add(card);
                }
            }
        }
        Collections.sort(blue);
        Collections.sort(green);
        Collections.sort(red);
        Collections.sort(yellow);
        Collections.sort(wild);
        blue.addAll(green);
        blue.addAll(red);
        blue.addAll(yellow);
        blue.addAll(wild);
        return blue;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public Card getCard(int cardValue, int cardColour) {
        for (Card card : this.cards) {
            if (!(card.getColour() == cardColour && card.getValue() == cardValue || card.getValue() == 10 && cardValue == 10) && (card.getValue() != 23 || cardValue != 23)) continue;
            return card;
        }
        return null;
    }

    public boolean hasCardWithValue(int cardValue) {
        for (Card card : this.cards) {
            if (card.getValue() != cardValue && card.getValue() != 10 && card.getValue() != 23) continue;
            return true;
        }
        return false;
    }

    public boolean hasCardWithColour(int cardColour) {
        for (Card card : this.cards) {
            if (card.getColour() != cardColour) continue;
            return true;
        }
        return false;
    }

    public int getPoints() {
        int val = -1;
        int scr = 0;
        for (Card card : this.cards) {
            val = card.getValue();
            if (val == 21 || val == 22 || val == 20) {
                scr += 20;
                continue;
            }
            if (val == 10 || val == 23) {
                scr += 50;
                continue;
            }
            scr += val;
        }
        return scr;
    }

    public void removeCard(Card card) {
        if (this.cards.contains(card)) {
            this.cards.remove(card);
        }
    }

    public boolean isLastCard() {
        return this.cards.size() == 1;
    }

    public boolean hasUno() {
        return this.cards.size() == 1;
    }

    public boolean hasWon() {
        return this.cards.size() == 0;
    }

    public int cardCount() {
        return this.cards.size();
    }
}

