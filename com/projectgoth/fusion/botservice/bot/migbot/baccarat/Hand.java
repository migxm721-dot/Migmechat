/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.botservice.bot.migbot.baccarat;

import com.projectgoth.fusion.botservice.bot.migbot.common.Card;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Hand
extends ArrayList<Card> {
    public int count() {
        int sum = 0;
        for (Card card : this) {
            switch (card.rank()) {
                case ACE: {
                    ++sum;
                    break;
                }
                case DEUCE: {
                    sum += 2;
                    break;
                }
                case THREE: {
                    sum += 3;
                    break;
                }
                case FOUR: {
                    sum += 4;
                    break;
                }
                case FIVE: {
                    sum += 5;
                    break;
                }
                case SIX: {
                    sum += 6;
                    break;
                }
                case SEVEN: {
                    sum += 7;
                    break;
                }
                case EIGHT: {
                    sum += 8;
                    break;
                }
                case NINE: {
                    sum += 9;
                    break;
                }
            }
        }
        return sum % 10;
    }

    public String toEmoticonHotKeys() {
        StringBuilder builder = new StringBuilder();
        for (Card card : this) {
            builder.append(card.toEmoticonHotkey());
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.toEmoticonHotKeys());
        builder.append(" ");
        int count = this.count();
        if (count >= 8 && this.size() == 2) {
            builder.append("Natural ");
        }
        return builder.append(count).toString();
    }
}

