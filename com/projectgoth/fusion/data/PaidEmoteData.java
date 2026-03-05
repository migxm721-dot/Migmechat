/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.emote.EmoteCommandData;
import java.io.Serializable;

public class PaidEmoteData
implements Serializable {
    private static final long serialVersionUID = 8403037251643819502L;
    private long id;
    private String commandName;
    private String description;
    private double price;
    private String currency;

    public PaidEmoteData(EmoteCommandData emoteCommandData) {
        this.id = emoteCommandData.getId();
        this.commandName = emoteCommandData.getCommandName();
        this.description = emoteCommandData.getDescription();
        this.price = emoteCommandData.isFree() ? 0.0 : emoteCommandData.getPrice();
        this.currency = emoteCommandData.getCurrency();
    }

    public long getId() {
        return this.id;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public String getDescription() {
        return this.description;
    }

    public double getPrice() {
        return this.price;
    }

    public String getCurrency() {
        return this.currency;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EmotePurchaseLocationEnum {
        PRIVATE_CHAT_COMMAND(2),
        GROUP_CHAT_COMMAND(3),
        CHATROOM_COMMAND(4),
        PRIVATE_CHAT_MENU(5),
        GROUP_CHAT_MENU(6),
        CHATROOM_MENU(7);

        private int value;

        private EmotePurchaseLocationEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static EmotePurchaseLocationEnum fromValue(int value) {
            for (EmotePurchaseLocationEnum e : EmotePurchaseLocationEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

