/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.payment.paypal;

import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.JSONUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.UsernameUtils;
import com.projectgoth.fusion.payment.PaymentException;
import java.text.ParseException;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class PaypalTransactionTokenData {
    public String username;
    public Integer paymentID;
    public double amount;
    public String currency;
    public String paypalUserID;
    public Date createDate;
    public Date expireDate;
    public double usdAmount = 0.0;

    public static PaypalTransactionTokenData createFromJSONString(String jsonString) throws JSONException, ParseException {
        if (jsonString == null) {
            return null;
        }
        JSONObject jsonObj = new JSONObject(jsonString);
        PaypalTransactionTokenData tokenData = new PaypalTransactionTokenData();
        String username = JSONUtils.getString(jsonObj, "username");
        if (username == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "username");
        }
        tokenData.username = UsernameUtils.normalizeUsername(username);
        tokenData.paymentID = JSONUtils.getInteger(jsonObj, "paymentID");
        tokenData.currency = JSONUtils.getString(jsonObj, "currency");
        Double amount = JSONUtils.getDouble(jsonObj, "amount");
        if (amount == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "amount");
        }
        Double usdAmount = JSONUtils.getDouble(jsonObj, "usdAmount");
        tokenData.usdAmount = usdAmount == null || usdAmount.isNaN() || usdAmount < 0.0 ? 0.0 : usdAmount;
        tokenData.amount = amount;
        tokenData.paypalUserID = JSONUtils.getString(jsonObj, "paypalUserID");
        String createDateStr = JSONUtils.getString(jsonObj, "createDate");
        if (StringUtil.isBlank(createDateStr)) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "createDate");
        }
        tokenData.createDate = DateTimeUtils.getPaymentTransactionTimeFromString(createDateStr);
        String expireDateStr = JSONUtils.getString(jsonObj, "expireDate");
        if (StringUtil.isBlank(expireDateStr)) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "expireDate");
        }
        tokenData.expireDate = DateTimeUtils.getPaymentTransactionTimeFromString(expireDateStr);
        return tokenData;
    }

    public String toJSONString() throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currency", (Object)this.currency);
        jsonObj.put("username", (Object)UsernameUtils.normalizeUsername(this.username));
        jsonObj.put("amount", this.amount);
        jsonObj.put("paymentID", (Object)this.paymentID);
        jsonObj.put("paypalUserID", (Object)this.paypalUserID);
        jsonObj.put("usdAmount", this.usdAmount);
        if (this.createDate != null) {
            jsonObj.put("createDate", (Object)DateTimeUtils.getStringForPaymentTransactionTime(this.createDate));
        }
        if (this.expireDate != null) {
            jsonObj.put("expireDate", (Object)DateTimeUtils.getStringForPaymentTransactionTime(this.expireDate));
        }
        return jsonObj.toString();
    }
}

