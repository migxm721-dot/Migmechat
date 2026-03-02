/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.HashUtils;
import org.apache.commons.codec.binary.Base64;

public class CreditCardUtils {
    public static final String CRYPTER_KEY_LOCATION = ConfigUtils.getConfigDirectory() + "/aeskeys";
    public static final String PUBLIC_KEY_LOCATION = ConfigUtils.getConfigDirectory() + "/key.x509.pub";

    public static void maskCreditCardNumber(char[] creditCardNumber) throws Exception {
        if (creditCardNumber == null || creditCardNumber.length < 13) {
            throw new Exception("invalid credit card number");
        }
        for (int i = 6; i < creditCardNumber.length - 4; ++i) {
            creditCardNumber[i] = 42;
        }
    }

    public static String creditCardHash(char[] creditCardNumber) throws Exception {
        return new String(Base64.encodeBase64((byte[])HashUtils.sha256(creditCardNumber)));
    }

    public static void printCharArray(char[] charArray) {
        for (char thechar : charArray) {
            System.out.print(thechar);
        }
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        char[] creditCardNumber = new char[16];
        for (int i = 1; i <= 16; ++i) {
            creditCardNumber[i - 1] = (char)(48 + i % 10);
        }
        System.out.println(CreditCardUtils.creditCardHash(creditCardNumber));
        System.out.println(CreditCardUtils.creditCardHash(creditCardNumber));
        System.out.println(CreditCardUtils.creditCardHash(creditCardNumber));
        CreditCardUtils.printCharArray(creditCardNumber);
        CreditCardUtils.maskCreditCardNumber(creditCardNumber);
        CreditCardUtils.printCharArray(creditCardNumber);
    }
}

