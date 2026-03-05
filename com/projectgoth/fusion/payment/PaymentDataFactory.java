/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.payment;

import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.payment.PaymentException;
import com.projectgoth.fusion.payment.creditcard.CreditCardData;
import com.projectgoth.fusion.payment.mimopay.MIMOVoucherData;
import com.projectgoth.fusion.payment.mol.MOLPaymentData;
import com.projectgoth.fusion.payment.paypal.PaypalPaymentData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PaymentDataFactory {
    private static final Map<Integer, ObjectCreator> objectCreatorLookup = new HashMap<Integer, ObjectCreator>();

    public static <T extends PaymentData> T getPayment(ResultSet rs) throws SQLException, Exception {
        Integer type = rs.getInt("type");
        ObjectCreator objectCreator = objectCreatorLookup.get(type);
        if (type != null) {
            return (T)objectCreator.createFrom(rs);
        }
        throw new Exception("Unknown payment type: [" + type + "]");
    }

    public static <T extends PaymentData> T getPayment(PaymentData.TypeEnum type, JSONObject jsonObject) throws JSONException, ParseException, PaymentException {
        jsonObject.put("type", type.value());
        return PaymentDataFactory.getPayment(jsonObject);
    }

    public static <T extends PaymentData> T getPayment(JSONObject jsonObject) throws JSONException, ParseException, PaymentException {
        Integer type = jsonObject.getInt("type");
        ObjectCreator objectCreator = objectCreatorLookup.get(type);
        if (type != null) {
            return (T)objectCreator.createFrom(jsonObject);
        }
        throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_CREDIT_RECHARGE_TYPE, type);
    }

    static {
        objectCreatorLookup.put(PaymentData.TypeEnum.MOL.value(), MOLPaymentData.OBJECT_CREATOR);
        objectCreatorLookup.put(PaymentData.TypeEnum.MIMOPAY.value(), MIMOVoucherData.OBJECT_CREATOR);
        objectCreatorLookup.put(PaymentData.TypeEnum.PAYPAL.value(), PaypalPaymentData.OBJECT_CREATOR);
        objectCreatorLookup.put(PaymentData.TypeEnum.CREDIT_CARD.value(), CreditCardData.OBJECT_CREATOR);
    }

    public static interface ObjectCreator {
        public PaymentData createFrom(ResultSet var1) throws SQLException;

        public PaymentData createFrom(JSONObject var1) throws JSONException, ParseException;
    }
}

