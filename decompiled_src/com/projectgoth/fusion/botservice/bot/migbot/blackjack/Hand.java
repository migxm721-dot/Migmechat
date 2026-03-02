/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.blackjack;

import com.projectgoth.fusion.botservice.bot.migbot.common.Card;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Hand
extends ArrayList<Card> {
    public List<Integer> count() {
        int minSum = 0;
        boolean hasAce = false;
        for (Card card : this) {
            switch (card.rank()) {
                case ACE: {
                    ++minSum;
                    hasAce = true;
                    break;
                }
                case DEUCE: {
                    minSum += 2;
                    break;
                }
                case THREE: {
                    minSum += 3;
                    break;
                }
                case FOUR: {
                    minSum += 4;
                    break;
                }
                case FIVE: {
                    minSum += 5;
                    break;
                }
                case SIX: {
                    minSum += 6;
                    break;
                }
                case SEVEN: {
                    minSum += 7;
                    break;
                }
                case EIGHT: {
                    minSum += 8;
                    break;
                }
                case NINE: {
                    minSum += 9;
                    break;
                }
                case TEN: 
                case JACK: 
                case QUEEN: 
                case KING: {
                    minSum += 10;
                }
            }
        }
        ArrayList<Integer> possibleCounts = new ArrayList<Integer>();
        possibleCounts.add(minSum);
        if (hasAce && minSum + 10 <= 21) {
            possibleCounts.add(0, minSum + 10);
        }
        return possibleCounts;
    }

    public int highestCount() {
        List<Integer> possibleCounts = this.count();
        return possibleCounts.get(0);
    }

    public String toEmoticonHotKeys() {
        StringBuilder builder = new StringBuilder();
        for (Card card : this) {
            builder.append(card.toEmoticonHotkey());
        }
        return builder.toString();
    }
}

