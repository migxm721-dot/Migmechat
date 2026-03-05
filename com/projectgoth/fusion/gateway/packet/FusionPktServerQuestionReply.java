/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.fdl.packets.FusionPktDataServerQuestionReply;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktMidletTab;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionPktServerQuestion;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FusionPktServerQuestionReply
extends FusionPktDataServerQuestionReply {
    public FusionPktServerQuestionReply(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktServerQuestionReply(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        String url;
        FusionPktServerQuestion serverQuestion = connection.getServerQuestion();
        if (serverQuestion == null) {
            FusionPktError error = new FusionPktError(this.transactionId);
            error.setErrorDescription("You have already replied the question");
            return new FusionPacket[]{error};
        }
        connection.setServerQuestionOld(null);
        Boolean answer = this.getAnswer();
        if (answer != null && answer.booleanValue() && (url = serverQuestion.getUrl()) != null) {
            FusionPktMidletTab midletTab = new FusionPktMidletTab();
            midletTab.setURL(url);
            midletTab.setFocus((byte)1);
            return new FusionPacket[]{new FusionPktOk(this.transactionId), midletTab};
        }
        return new FusionPacket[]{new FusionPktOk(this.transactionId)};
    }
}

