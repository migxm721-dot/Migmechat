/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 *  org.keyczar.Crypter
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.maintenance;

import com.projectgoth.fusion.common.AsymmetricCryptUtils;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.CreditCardUtils;
import com.projectgoth.fusion.data.CreditCardPaymentData;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.PublicKey;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.Cipher;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.keyczar.Crypter;
import org.springframework.util.StringUtils;

public class EncryptCreditCardDump {
    protected static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(EncryptCreditCardDump.class));
    public static final SimpleDateFormat SQL_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void assignField(String[] fields, StringBuffer field, int fieldIndex) {
        fields[fieldIndex] = field.toString().equals("\\N") ? null : field.toString();
        field.delete(0, field.length());
    }

    public String encloseInQuotes(String input) {
        if (input != null) {
            return "\"" + input + "\"";
        }
        return "\\N";
    }

    public String encloseInQuotes(Date input) {
        if (input != null) {
            return "\"" + SQL_DATE_FORMATTER.format(input) + "\"";
        }
        return "\\N";
    }

    public String encloseInQuotes(Integer input) {
        if (input != null) {
            return "\"" + input + "\"";
        }
        return "\\N";
    }

    public String encloseInQuotes(Double input) {
        if (input != null) {
            return "\"" + input + "\"";
        }
        return "\\N";
    }

    public String nullSafeTrim(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String[] fromCSVLine(String line) {
        String[] fields = new String[18];
        int i = 0;
        int fieldIndex = 0;
        StringBuffer field = new StringBuffer();
        boolean inQuotes = false;
        char c = '\u0000';
        int pc = 0;
        while (true) {
            Object var10_9;
            block14: {
                block13: {
                    block12: {
                        block11: {
                            block10: {
                                block9: {
                                    if (i >= line.length() - 1) {
                                        this.assignField(fields, field, fieldIndex);
                                        return fields;
                                    }
                                    try {
                                        c = line.charAt(i);
                                        if (pc == 92) {
                                            if (c == '\"' || c == 'N') {
                                                field.append(c);
                                                var10_9 = null;
                                                ++i;
                                                break block9;
                                            }
                                            field.delete(field.length() - 1, field.length());
                                            c = '\u0000';
                                            break block10;
                                        }
                                        if (c == '\"' && pc != 92) {
                                            if (!inQuotes) {
                                                inQuotes = true;
                                                break block11;
                                            }
                                            inQuotes = false;
                                            break block12;
                                        }
                                        if (!inQuotes && c == ',' || i == line.length() - 1) {
                                            this.assignField(fields, field, fieldIndex);
                                            ++fieldIndex;
                                            break block13;
                                        }
                                        field.append(c);
                                        break block14;
                                    }
                                    catch (Throwable throwable) {
                                        var10_9 = null;
                                        ++i;
                                        pc = c;
                                        throw throwable;
                                    }
                                }
                                pc = c;
                                continue;
                            }
                            var10_9 = null;
                            ++i;
                            pc = c;
                            continue;
                        }
                        var10_9 = null;
                        ++i;
                        pc = c;
                        continue;
                    }
                    var10_9 = null;
                    ++i;
                    pc = c;
                    continue;
                }
                var10_9 = null;
                ++i;
                pc = c;
                continue;
            }
            var10_9 = null;
            ++i;
            pc = c;
        }
    }

    public CreditCardPaymentData paymentFromCSVLine(String line) throws ParseException {
        String[] fields = this.fromCSVLine(line);
        CreditCardPaymentData payment = new CreditCardPaymentData();
        payment.id = Integer.parseInt(fields[0]);
        payment.username = fields[1];
        payment.dateCreated = fields[2] == null ? null : SQL_DATE_FORMATTER.parse(fields[2]);
        payment.source = !StringUtils.hasLength((String)fields[3]) ? null : CreditCardPaymentData.SourceEnum.fromValue(Integer.parseInt(fields[3]));
        payment.ipAddress = fields[4];
        payment.cardType = !StringUtils.hasLength((String)fields[5]) ? null : CreditCardPaymentData.CardTypeEnum.fromValue(Integer.parseInt(fields[5]));
        payment.cardNumber = fields[6];
        payment.cardHolder = this.nullSafeTrim(fields[7]);
        payment.cardExpiryDate = fields[8];
        payment.cardVerificationNumber = fields[9];
        payment.amount = !StringUtils.hasLength((String)fields[10]) ? null : Double.valueOf(Double.parseDouble(fields[10]));
        payment.currency = fields[11];
        payment.exchangeRate = !StringUtils.hasLength((String)fields[12]) ? null : Double.valueOf(Double.parseDouble(fields[12]));
        payment.providerTransactionId = fields[13];
        payment.responseCode = fields[14];
        payment.chargeBackDate = !StringUtils.hasLength((String)fields[15]) ? null : SQL_DATE_FORMATTER.parse(fields[15]);
        payment.chargeBackReasonCode = fields[16];
        payment.status = !StringUtils.hasLength((String)fields[17]) ? null : CreditCardPaymentData.StatusEnum.fromValue(Integer.parseInt(fields[17]));
        return payment;
    }

    public String toCSVLine(CreditCardPaymentData payment) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.encloseInQuotes(payment.id)).append(",");
        buffer.append(this.encloseInQuotes(payment.username)).append(",");
        buffer.append(this.encloseInQuotes(payment.dateCreated)).append(",");
        buffer.append(payment.source == null ? "\\N" : this.encloseInQuotes(payment.source.value())).append(",");
        buffer.append(this.encloseInQuotes(payment.ipAddress)).append(",");
        buffer.append(payment.cardType == null ? "\\N" : this.encloseInQuotes(payment.cardType.value())).append(",");
        buffer.append(this.encloseInQuotes(payment.cardNumber)).append(",");
        buffer.append(this.encloseInQuotes(payment.encryptedCardNumber)).append(",");
        buffer.append(this.encloseInQuotes(payment.checkNumber)).append(",");
        buffer.append(this.encloseInQuotes(payment.cardHolder)).append(",");
        buffer.append(this.encloseInQuotes(payment.cardExpiryDate)).append(",");
        buffer.append(this.encloseInQuotes(payment.cardVerificationNumber)).append(",");
        buffer.append(this.encloseInQuotes(payment.amount)).append(",");
        buffer.append(this.encloseInQuotes(payment.currency)).append(",");
        buffer.append(this.encloseInQuotes(payment.exchangeRate)).append(",");
        buffer.append(this.encloseInQuotes(payment.providerTransactionId)).append(",");
        buffer.append(this.encloseInQuotes(payment.responseCode)).append(",");
        buffer.append(this.encloseInQuotes(payment.chargeBackDate)).append(",");
        buffer.append(this.encloseInQuotes(payment.chargeBackReasonCode)).append(",");
        buffer.append(payment.status == null ? "\\N" : this.encloseInQuotes(payment.status.value()));
        return buffer.toString();
    }

    public void start() throws SQLException, Exception {
        Crypter crypter = new Crypter(CreditCardUtils.CRYPTER_KEY_LOCATION);
        PublicKey publicKey = AsymmetricCryptUtils.loadPublicKey(CreditCardUtils.PUBLIC_KEY_LOCATION);
        if (publicKey == null) {
            log.fatal((Object)("loaded public key from [" + CreditCardUtils.PUBLIC_KEY_LOCATION + "] is null!"));
            System.exit(1);
        }
        log.info((Object)("loaded public key of algo " + publicKey.getAlgorithm() + " and format " + publicKey.getFormat() + " length " + publicKey.getEncoded().length));
        Cipher encryptingCipher = AsymmetricCryptUtils.getEncryptCiper(publicKey);
        FileInputStream fis = new FileInputStream("creditcardpayment.txt");
        InputStreamReader inputReader = new InputStreamReader(fis);
        BufferedReader reader = new BufferedReader(inputReader);
        FileOutputStream fos = new FileOutputStream("creditcardpayment.encrypted.txt");
        OutputStreamWriter outputWriter = new OutputStreamWriter(fos);
        BufferedWriter writer = new BufferedWriter(outputWriter);
        CreditCardPaymentData payment = null;
        String line = reader.readLine();
        while (line != null) {
            try {
                payment = this.paymentFromCSVLine(line);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("looking at payment id [" + payment.id + "]"));
                }
                if (StringUtils.hasLength((String)payment.cardNumber)) {
                    payment.checkNumber = CreditCardUtils.creditCardHash(payment.cardNumber.toCharArray());
                    if (payment.cardNumber.length() >= 13) {
                        payment.encryptedCardNumber = AsymmetricCryptUtils.encryptAndBase64EncodeText(encryptingCipher, payment.cardNumber);
                        char[] ccnArray = payment.cardNumber.toCharArray();
                        CreditCardUtils.maskCreditCardNumber(ccnArray);
                        payment.cardNumber = new String(ccnArray);
                    }
                }
                if (StringUtils.hasLength((String)payment.cardHolder)) {
                    payment.cardHolder = crypter.encrypt(payment.cardHolder);
                }
                if (StringUtils.hasLength((String)payment.cardExpiryDate)) {
                    payment.cardExpiryDate = crypter.encrypt(payment.cardExpiryDate);
                }
                writer.write(this.toCSVLine(payment));
                writer.newLine();
            }
            catch (Exception e) {
                log.error((Object)"error", (Throwable)e);
            }
            line = reader.readLine();
        }
        inputReader.close();
        fis.close();
        writer.flush();
        outputWriter.close();
        fos.close();
    }

    public static void main(String[] args) throws Exception {
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        EncryptCreditCardDump main = new EncryptCreditCardDump();
        main.start();
    }
}

