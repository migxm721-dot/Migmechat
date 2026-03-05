/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.data.SMSRouteData;
import java.io.Serializable;
import java.util.List;

public class SMSGatewayData
implements Serializable {
    public Integer id;
    public String name;
    public TypeEnum type;
    public String url;
    public Integer port;
    public MethodEnum method;
    public String iddPrefix;
    public String authorization;
    public String usernameParam;
    public String passwordParam;
    public String sourceParam;
    public String destinationParam;
    public String messageParam;
    public String unicodeMessageParam;
    public String unicodeParam;
    public String extraParam;
    public String unicodeCharset;
    public String successPattern;
    public String errorPattern;
    public Boolean deliveryReporting;
    public StatusEnum status;
    public List<SMSRouteData> smsRoutes;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        INACTIVE(0),
        ACTIVE(1);

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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MethodEnum {
        GET(1),
        POST(2);

        private int value;

        private MethodEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static MethodEnum fromValue(int value) {
            for (MethodEnum e : MethodEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum {
        HTTP(1),
        SMPP_TRANSMITTER(2),
        SMPP_TRANSCEIVER(3);

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
}

