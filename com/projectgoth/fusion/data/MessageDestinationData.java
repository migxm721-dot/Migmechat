/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.slice.MessageDestinationDataIce;
import java.io.Serializable;
import java.util.Date;

public class MessageDestinationData
implements Serializable {
    public Integer id;
    public Integer messageID;
    public Integer contactID;
    public TypeEnum type;
    public String destination;
    public Integer IDDCode;
    public Double cost;
    public Integer gateway;
    public Date dateDispatched;
    public String providerTransactionID;
    public StatusEnum status;

    public MessageDestinationData() {
    }

    public MessageDestinationData(MessageDestinationDataIce messageDestIce) {
        this.id = messageDestIce.id == Integer.MIN_VALUE ? null : Integer.valueOf(messageDestIce.id);
        this.messageID = messageDestIce.messageID == Integer.MIN_VALUE ? null : Integer.valueOf(messageDestIce.messageID);
        this.contactID = messageDestIce.contactID == Integer.MIN_VALUE ? null : Integer.valueOf(messageDestIce.contactID);
        this.type = messageDestIce.type == Integer.MIN_VALUE ? null : TypeEnum.fromValue(messageDestIce.type);
        this.destination = messageDestIce.destination.equals("\u0000") ? null : messageDestIce.destination;
        this.IDDCode = messageDestIce.IDDCode == Integer.MIN_VALUE ? null : Integer.valueOf(messageDestIce.IDDCode);
        this.cost = messageDestIce.cost == Double.MIN_VALUE ? null : Double.valueOf(messageDestIce.cost);
        this.gateway = messageDestIce.gateway == Integer.MIN_VALUE ? null : Integer.valueOf(messageDestIce.gateway);
        this.dateDispatched = messageDestIce.dateDispatched == Long.MIN_VALUE ? null : new Date(messageDestIce.dateDispatched);
        this.providerTransactionID = messageDestIce.providerTransactionID.equals("\u0000") ? null : messageDestIce.providerTransactionID;
        this.status = messageDestIce.status == Integer.MIN_VALUE ? null : StatusEnum.fromValue(messageDestIce.status);
    }

    public MessageDestinationDataIce toIceObject() {
        MessageDestinationDataIce messageDestIce = new MessageDestinationDataIce();
        messageDestIce.id = this.id == null ? Integer.MIN_VALUE : this.id;
        messageDestIce.messageID = this.messageID == null ? Integer.MIN_VALUE : this.messageID;
        messageDestIce.contactID = this.contactID == null ? Integer.MIN_VALUE : this.contactID;
        messageDestIce.type = this.type == null ? Integer.MIN_VALUE : this.type.value();
        messageDestIce.destination = this.destination == null ? "\u0000" : this.destination;
        messageDestIce.IDDCode = this.IDDCode == null ? Integer.MIN_VALUE : this.IDDCode;
        messageDestIce.cost = this.cost == null ? Double.MIN_VALUE : this.cost;
        messageDestIce.gateway = this.gateway == null ? Integer.MIN_VALUE : this.gateway;
        messageDestIce.dateDispatched = this.dateDispatched == null ? Long.MIN_VALUE : this.dateDispatched.getTime();
        messageDestIce.providerTransactionID = this.providerTransactionID == null ? "\u0000" : this.providerTransactionID;
        messageDestIce.status = this.status == null ? Integer.MIN_VALUE : this.status.value();
        return messageDestIce;
    }

    public static String toString(MessageDestinationDataIce i) {
        return new MessageDestinationData(i).toString();
    }

    public String toString() {
        return String.format("MsgDest:id=%d, messageId=%d, contactID=%d, type=%s, destination=%s, IDDCode=%d, cost=%f, gateway=%d, dateDispatched=%s, providerTransactionID=%s, status=%s", this.id, this.messageID, this.contactID, this.type == null ? "null" : this.type.toString(), this.destination, this.IDDCode, this.cost, this.gateway, this.dateDispatched, this.providerTransactionID, this.status == null ? "null" : this.status.toString());
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum {
        INDIVIDUAL(1),
        GROUP(2),
        CHAT_ROOM(3),
        DISTRIBUTION_LIST(4);

        private int value;

        private TypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static TypeEnum fromValue(int value) {
            for (TypeEnum e : TypeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        PENDING(0),
        SENT(1),
        FAILED(2);

        private int value;

        private StatusEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static StatusEnum fromValue(int value) {
            for (StatusEnum e : StatusEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

