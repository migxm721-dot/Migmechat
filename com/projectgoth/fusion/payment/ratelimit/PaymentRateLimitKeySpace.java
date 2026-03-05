/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.payment.ratelimit;

import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.payment.ratelimit.PaymentRateLimitType;

public interface PaymentRateLimitKeySpace {
    public PaymentRateLimitType getRateLimitType();

    public PaymentData.TypeEnum getVendorType();

    public String getSubNamespace();
}

