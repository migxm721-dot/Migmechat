/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import java.io.Serializable;
import java.util.Date;

public class ContentPurchasedData
implements Serializable {
    public Integer id;
    public String username;
    public Date dateCreated;
    public String mobilephone;
    public Integer contentId;
    public String providerContentId;
    public String providerTransactionId;
    public String downloadURL;
    public Integer numDownloads;
    public Boolean refunded;
    public RefundReasonEnum refundReason;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RefundReasonEnum {
        PROVIDER_ERROR(1),
        HANDSET_INCOMPATIBLE(2),
        CONTENT_NOT_DOWNLOADED(3),
        MANUAL(99);

        private int value;

        private RefundReasonEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static RefundReasonEnum fromValue(int value) {
            for (RefundReasonEnum e : RefundReasonEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }
    }
}

