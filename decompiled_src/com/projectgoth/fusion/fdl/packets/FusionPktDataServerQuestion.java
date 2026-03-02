/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.packets;

import com.projectgoth.fusion.fdl.enums.PacketType;
import com.projectgoth.fusion.packet.FusionPacket;
import java.util.HashMap;

public class FusionPktDataServerQuestion
extends FusionPacket {
    public FusionPktDataServerQuestion() {
        super(PacketType.SERVER_QUESTION);
    }

    public FusionPktDataServerQuestion(short transactionId) {
        super(PacketType.SERVER_QUESTION, transactionId);
    }

    public FusionPktDataServerQuestion(FusionPacket packet) {
        super(packet);
    }

    public final QuestionType getQuestionType() {
        return QuestionType.fromValue(this.getByteField((short)1));
    }

    public final void setQuestionType(QuestionType questionType) {
        this.setField((short)1, questionType.value());
    }

    public final String getQuestion() {
        return this.getStringField((short)2);
    }

    public final void setQuestion(String question) {
        this.setField((short)2, question);
    }

    public final String getUrl() {
        return this.getStringField((short)3);
    }

    public final void setUrl(String url) {
        this.setField((short)3, url);
    }

    public final String getTitle() {
        return this.getStringField((short)4);
    }

    public final void setTitle(String title) {
        this.setField((short)4, title);
    }

    public final Short getTimeout() {
        return this.getShortField((short)5);
    }

    public final void setTimeout(short timeout) {
        this.setField((short)5, timeout);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum QuestionType {
        YES_NO(1),
        OK_CANCEL(2);

        private byte value;
        private static final HashMap<Byte, QuestionType> LOOKUP;

        private QuestionType(byte value) {
            this.value = value;
        }

        public byte value() {
            return this.value;
        }

        public static QuestionType fromValue(int value) {
            return LOOKUP.get((byte)value);
        }

        public static QuestionType fromValue(Byte value) {
            return LOOKUP.get(value);
        }

        static {
            LOOKUP = new HashMap();
            for (QuestionType questionType : QuestionType.values()) {
                LOOKUP.put(questionType.value, questionType);
            }
        }
    }
}

