/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.msn;

import com.projectgoth.fusion.chat.external.msn.Command;
import com.projectgoth.fusion.chat.external.msn.DWord;
import com.projectgoth.fusion.chat.external.msn.MSNSLPMessage;
import com.projectgoth.fusion.chat.external.msn.P2PMessage;
import com.projectgoth.fusion.chat.external.msn.SwitchBoard;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class P2PSession {
    private SwitchBoard switchBoard;
    private String counterParty;
    private DWord sessionId;
    private String callId;
    private DWord baseIdentifier;
    private DWord lastIdentifier;
    private Map<String, P2PMessage> messagesSent = new ConcurrentHashMap<String, P2PMessage>();

    private void sendP2PMessage(DWord identifier, P2PMessage message) {
        try {
            message.setDestination(this.counterParty);
            message.setIdentifier(identifier);
            byte[] payload = message.toString().getBytes("ISO8859_1");
            this.messagesSent.put(identifier.toString(), message);
            this.switchBoard.sendAsyncCommand(new Command(Command.Type.MSG).addParam("D").setPayload(payload));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private DWord nextIdentifier() {
        long next = this.lastIdentifier.longValue() + 1L;
        if (next == this.baseIdentifier.longValue()) {
            ++next;
        }
        this.lastIdentifier = new DWord(next);
        return this.lastIdentifier;
    }

    public P2PSession(SwitchBoard switchBoard, String source, P2PMessage p2pInvitation) {
        MSNSLPMessage msnslp = p2pInvitation.getMSNSLP();
        MSNSLPMessage.Content msnslpContent = msnslp.getContent();
        this.switchBoard = switchBoard;
        this.counterParty = source;
        this.sessionId = new DWord(Long.parseLong(msnslpContent.getString("SessionID")));
        this.callId = msnslp.getHeader("Call-ID");
        this.baseIdentifier = new DWord(Math.abs(new SecureRandom().nextInt()));
        this.lastIdentifier = new DWord(this.baseIdentifier.longValue() - 4L);
        this.sendP2PMessage(this.baseIdentifier, P2PMessage.createAcknowledgement(2, p2pInvitation));
        this.sendP2PMessage(this.nextIdentifier(), P2PMessage.createOkStatus(this.sessionId, p2pInvitation));
    }

    public boolean belongsToMe(P2PMessage message) {
        DWord ackIdentifier = message.getAckIdentifier();
        if (ackIdentifier.longValue() != 0L && this.messagesSent.containsKey(ackIdentifier.toString())) {
            return true;
        }
        MSNSLPMessage msnslp = message.getMSNSLP();
        return msnslp != null && this.callId.equals(msnslp.getHeader("Call-ID"));
    }

    public void onP2PMessage(String source, P2PMessage message) {
        Object content = message.getContent();
        if (content == null) {
            Object acknowledgedContent;
            P2PMessage acknowledgedP2P = this.messagesSent.get(message.getAckIdentifier().toString());
            if (acknowledgedP2P != null && (acknowledgedContent = acknowledgedP2P.getContent()) != null) {
                byte[] acknowledgedBinaryData;
                if (acknowledgedContent instanceof MSNSLPMessage) {
                    MSNSLPMessage acknowledgedSLP = (MSNSLPMessage)acknowledgedContent;
                    if (acknowledgedSLP.getType() == MSNSLPMessage.Type.STATUS) {
                        this.sendP2PMessage(this.nextIdentifier(), P2PMessage.createDataPreparation(this.sessionId));
                    }
                } else if (acknowledgedContent instanceof byte[] && Arrays.equals(acknowledgedBinaryData = (byte[])acknowledgedContent, new byte[4])) {
                    DWord identifier = this.nextIdentifier();
                    for (P2PMessage fragment : P2PMessage.createDataMessages(this.sessionId, this.switchBoard.displayPicture.getContent())) {
                        this.sendP2PMessage(identifier, fragment);
                    }
                }
            }
        } else if (content instanceof MSNSLPMessage) {
            MSNSLPMessage msnslp = (MSNSLPMessage)content;
            if (msnslp.getType() == MSNSLPMessage.Type.BYE) {
                this.sendP2PMessage(this.nextIdentifier(), P2PMessage.createAcknowledgement(64, message));
            }
        } else if (content instanceof byte[]) {
            // empty if block
        }
    }
}

