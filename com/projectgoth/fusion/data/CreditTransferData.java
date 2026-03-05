/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.data.AccountBalanceData;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.UserData;
import java.io.Serializable;

public class CreditTransferData
implements Serializable {
    private static final long serialVersionUID = 7242592585963825428L;
    private AccountEntryData accountEntryData;
    private AccountBalanceData accountBalanceData;

    public CreditTransferData(AccountEntryData accountEntryData, AccountBalanceData accountBalanceData) {
        this.accountEntryData = accountEntryData;
        this.accountBalanceData = accountBalanceData;
    }

    public AccountEntryData getAccountEntryData() {
        return this.accountEntryData;
    }

    public void setAccountEntryData(AccountEntryData accountEntryData) {
        this.accountEntryData = accountEntryData;
    }

    public AccountBalanceData getAccountBalanceData() {
        return this.accountBalanceData;
    }

    public void setAccountBalanceData(AccountBalanceData accountBalanceData) {
        this.accountBalanceData = accountBalanceData;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum CreditTransferFeeEnum {
        NON_TOP_MERCHANT_TO_NON_TOP_MERCHANT(1),
        NON_TOP_MERCHANT_TO_TOP_MERCHANT(2),
        TOP_MERCHANT_TO_NON_TOP_MERCHANT(3),
        TOP_MERCHANT_TO_TOP_MERCHANT(4);

        private int value;

        private CreditTransferFeeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static CreditTransferFeeEnum fromValue(int value) {
            for (CreditTransferFeeEnum e : CreditTransferFeeEnum.values()) {
                if (e.value() != value) continue;
                return e;
            }
            return null;
        }

        public static CreditTransferFeeEnum fromUserType(UserData.TypeEnum senderUserType, UserData.TypeEnum receipientUserType) {
            if (senderUserType != UserData.TypeEnum.MIG33_TOP_MERCHANT) {
                if (receipientUserType != UserData.TypeEnum.MIG33_TOP_MERCHANT) {
                    return NON_TOP_MERCHANT_TO_NON_TOP_MERCHANT;
                }
                return NON_TOP_MERCHANT_TO_TOP_MERCHANT;
            }
            if (receipientUserType != UserData.TypeEnum.MIG33_TOP_MERCHANT) {
                return TOP_MERCHANT_TO_NON_TOP_MERCHANT;
            }
            return TOP_MERCHANT_TO_TOP_MERCHANT;
        }
    }
}

