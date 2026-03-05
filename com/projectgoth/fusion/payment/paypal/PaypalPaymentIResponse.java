/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.payment.paypal;

import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.payment.PaymentException;
import com.projectgoth.fusion.payment.PaymentIResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class PaypalPaymentIResponse
extends PaymentIResponse {
    public String token;
    public Double amount;
    public String currency;
    public Double amountUSD;
    public String result;

    public String toJSON(PaymentIResponse.ReturnType returnType) throws JSONException {
        if (returnType == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.ERROR, "An error occurred in your request. Please contact merchant@mig.me.");
        }
        JSONObject response = new JSONObject();
        switch (returnType) {
            case CREATE: {
                response.put("token", (Object)this.token);
                return response.toString();
            }
            case GET_COMPACT_DETAILS: {
                response.put("amount", (Object)this.amount);
                response.put("currency", (Object)this.currency);
                response.put("result", (Object)this.result);
                response.put("amountUSD", (Object)this.amountUSD);
                return response.toString();
            }
        }
        throw new PaymentException(ErrorCause.PaymentErrorReasonType.ERROR, "An error occurred in your request. Please contact merchant@mig.me.");
    }
}

