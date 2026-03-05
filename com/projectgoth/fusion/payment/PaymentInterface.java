/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.payment;

import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.payment.PaymentException;
import com.projectgoth.fusion.payment.PaymentIResponse;
import java.util.Map;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface PaymentInterface {
    public boolean isAccessAllowed(UserData var1) throws PaymentException, Exception;

    public Map<String, Object> clientInitiatePayment(JSONObject var1) throws PaymentException, Exception;

    public Map<String, Object> updatePaymentStatus(JSONObject var1) throws Exception;

    public String getCurrencyForUser(UserData var1);

    public <T extends PaymentData> PaymentIResponse clientInitiatePayment(T var1) throws PaymentException, Exception;

    public <T extends PaymentData> PaymentIResponse updatePaymentStatus(T var1) throws PaymentException, Exception;

    public PaymentIResponse onPaymentAuthorized(String var1, JSONObject var2) throws PaymentException, Exception;

    public <T extends PaymentData> PaymentIResponse approve(T var1, String var2) throws PaymentException, Exception;

    public <T extends PaymentData> PaymentIResponse reject(T var1, String var2) throws PaymentException, Exception;
}

