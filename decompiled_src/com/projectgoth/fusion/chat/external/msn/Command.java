/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.msn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Command {
    private Type type;
    private Integer transactionId;
    private int errorCode;
    private List<String> paramList = new ArrayList<String>();
    private byte[] payload;
    private int payloadSize;
    private Command reply;

    public Command(Type type) {
        this.type = type;
    }

    public Command(String rawCommand) throws IOException {
        StringTokenizer tokens = new StringTokenizer(rawCommand);
        if (!tokens.hasMoreTokens()) {
            throw new IOException("Empty command");
        }
        String token = tokens.nextToken();
        try {
            this.type = Type.valueOf(token);
        }
        catch (Exception e) {
            try {
                this.type = Type.ERROR;
                this.errorCode = Integer.parseInt(token);
            }
            catch (NumberFormatException ie) {
                throw new IOException("Unknown command type " + token);
            }
        }
        if (this.containTransactionId()) {
            try {
                this.transactionId = Integer.parseInt(tokens.nextToken());
            }
            catch (Exception e) {
                throw new IOException("Command contains no transaction ID");
            }
        }
        while (tokens.hasMoreTokens()) {
            this.addParam(tokens.nextToken());
        }
        if (this.isPayloadCommand() && this.paramList.size() > 0) {
            this.payloadSize = Integer.parseInt(this.paramList.remove(this.paramList.size() - 1));
        }
    }

    public Type getType() {
        return this.type;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public Integer getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public byte[] getPayload() {
        return this.payload;
    }

    public Command setPayload(byte[] payload) {
        this.payload = payload;
        this.payloadSize = payload == null ? 0 : payload.length;
        return this;
    }

    public int getPayloadSize() {
        return this.payloadSize;
    }

    public void setReply(Command reply) {
        this.reply = reply;
    }

    public Command getReply() {
        return this.reply;
    }

    public Command addParam(String param) {
        this.paramList.add(param);
        return this;
    }

    public String getParam(int index) {
        return this.paramList.get(index);
    }

    public List<String> getParamList() {
        return this.paramList;
    }

    public boolean isPayloadCommand() {
        switch (this.type) {
            case QRY: 
            case MSG: 
            case NOT: 
            case UUX: 
            case UBX: 
            case GCF: {
                return true;
            }
        }
        return false;
    }

    public boolean containTransactionId() {
        switch (this.type) {
            case QRY: 
            case UUX: 
            case GCF: 
            case ERROR: 
            case VER: 
            case CVR: 
            case USR: 
            case XFR: 
            case ILN: 
            case CHG: 
            case SYN: 
            case ADD: 
            case REM: 
            case CAL: 
            case ACK: 
            case NAK: 
            case ANS: 
            case ADC: 
            case SBP: 
            case LKP: {
                return true;
            }
        }
        return false;
    }

    public String getCommandString() {
        StringBuilder builder = new StringBuilder(this.type.toString());
        if (this.transactionId != null) {
            builder.append(" ").append(this.transactionId.toString());
        }
        for (String param : this.paramList) {
            builder.append(" ").append(param);
        }
        if (this.isPayloadCommand()) {
            builder.append(" ").append(this.payloadSize);
        }
        return builder.append("\r\n").toString();
    }

    public byte[] getBytes(String charset) throws UnsupportedEncodingException {
        if (this.isPayloadCommand() && this.payloadSize > 0) {
            byte[] ba = this.getCommandString().getBytes(charset);
            byte[] retval = new byte[ba.length + this.payload.length];
            System.arraycopy(ba, 0, retval, 0, ba.length);
            System.arraycopy(this.payload, 0, retval, ba.length, this.payload.length);
            return retval;
        }
        return this.getCommandString().getBytes(charset);
    }

    public String toString() {
        if (this.isPayloadCommand() && this.payloadSize > 0) {
            StringBuilder builder = new StringBuilder(this.getCommandString());
            builder.append(new String(this.payload));
            return builder.toString();
        }
        return this.getCommandString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Type {
        ERROR,
        ACK,
        ADD,
        ADG,
        ANS,
        BLP,
        BPR,
        BYE,
        CAL,
        CHG,
        CHL,
        FLN,
        GTC,
        ILN,
        TWN,
        IRO,
        JOI,
        LSG,
        LST,
        MSG,
        NAK,
        NLN,
        OUT,
        PRP,
        QRY,
        REA,
        REG,
        REM,
        RMG,
        RNG,
        SYN,
        USR,
        VER,
        XFR,
        CVR,
        SDC,
        NOT,
        UUX,
        UBX,
        SBS,
        ADC,
        SBP,
        GCF,
        LKP;

    }
}

