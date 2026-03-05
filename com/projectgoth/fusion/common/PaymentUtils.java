/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.StringUtil;
import java.text.DecimalFormat;

public class PaymentUtils {
    public static String normalizeCurrency(String currencyString) {
        if (currencyString == null) {
            return null;
        }
        return StringUtil.trimmedUpperCase(currencyString);
    }

    public static String formatAmountInCurrency(double amount) {
        amount = Math.ceil(amount * 100.0);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(amount / 100.0);
    }
}

