/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.payment.creditcard;

import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.payment.PaymentException;
import com.projectgoth.fusion.payment.PaymentIResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class CreditCardIResponse
extends PaymentIResponse {
    public String result;
    public String mac;
    public String returnMac;
    public String returnUrl;
    public String amount;
    public String currency;
    public String message;
    public String status;
    public String vendorTransactionId;

    public String toJSON(PaymentIResponse.ReturnType returnType) throws JSONException {
        if (returnType == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.ERROR, "An error occurred in your request. Please contact merchant@mig.me.");
        }
        JSONObject response = new JSONObject();
        switch (returnType) {
            case CREATE: {
                response.put("redirectUrl", (Object)this.returnUrl);
                response.put("ref", (Object)this.vendorTransactionId);
                response.put("returnMac", (Object)this.returnMac);
                break;
            }
            case GET_COMPACT_DETAILS: {
                response.put("status", (Object)this.result);
                response.put("amount", (Object)this.amount);
                response.put("currency", (Object)this.currency);
                if (this.result.equals(PaymentData.StatusEnum.REJECTED.name())) {
                    response.put("message", (Object)"Your payment has been rejected.");
                    break;
                }
                if (this.result.equals(PaymentData.StatusEnum.APPROVED.name())) {
                    response.put("message", (Object)"Your payment has been approved.");
                    break;
                }
                if (null == this.message) {
                    response.put("message", (Object)"Your payment is still being processed.");
                    break;
                }
                response.put("message", (Object)this.message);
                break;
            }
            case APPROVE: 
            case REJECT: {
                response.put("status", (Object)this.status);
                response.put("amount", (Object)this.amount);
                response.put("currency", (Object)this.currency);
                break;
            }
            default: {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.ERROR, "An error occurred in your request. Please contact merchant@mig.me.");
            }
        }
        return response.toString();
    }
}

