/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.packet.FusionPacket;

public class FusionPktError
extends FusionPacket {
    public FusionPktError() {
        super((short)0);
    }

    public FusionPktError(short transactionId) {
        super((short)0, transactionId);
    }

    public FusionPktError(short transactionId, Code code, String errorDescription) {
        super((short)0, transactionId);
        this.setErrorCode(code);
        this.setErrorDescription(errorDescription);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public FusionPktError(short transactionId, Code code, int infoId) {
        super((short)0, transactionId);
        this.setErrorCode(code);
        String infoText = null;
        try {
            MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
            infoText = misEJB.getInfoText(infoId);
            if (infoText == null) {
                this.setErrorDescription("Request failed");
            } else {
                this.setErrorDescription(infoText);
            }
        }
        catch (Exception e) {
            if (infoText == null) {
                this.setErrorDescription("Request failed");
            } else {
                this.setErrorDescription(infoText);
            }
            catch (Throwable throwable) {
                if (infoText == null) {
                    this.setErrorDescription("Request failed");
                } else {
                    this.setErrorDescription(infoText);
                }
                throw throwable;
            }
        }
    }

    public FusionPktError(FusionPacket packet) {
        super(packet);
    }

    public String getErrorDescription() {
        return this.getField((short)2).getStringVal();
    }

    public void setErrorDescription(String errorDescription) {
        this.setField((short)2, errorDescription);
    }

    public Short getErrorNumber() {
        return this.getShortField((short)1);
    }

    public void setErrorCode(Code errorCode) {
        this.setField((short)1, errorCode.getErrorNumber());
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Code {
        UNDEFINED(1),
        CHAT_SYNC_ENTITY_NOT_FOUND(2),
        INCORRECT_CREDENTIAL(3),
        INVALID_VERSION(100),
        UNSUPPORTED_PROTOCOL(101);

        private short errorNumber;

        private Code(short errorNumber) {
            this.errorNumber = errorNumber;
        }

        public short getErrorNumber() {
            return this.errorNumber;
        }
    }
}

