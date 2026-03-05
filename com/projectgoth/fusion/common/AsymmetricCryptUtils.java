/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class AsymmetricCryptUtils {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AsymmetricCryptUtils.class));
    private static final String CRYPT_ALGORITHM = "RSA/ECB/OAEPWITHSHA1ANDMGF1PADDING";
    private static final String KEY_ALGORITHM = "RSA";

    public static PrivateKey loadPrivateKey(String location) {
        try {
            FileInputStream fis = new FileInputStream(location);
            byte[] encodedPrivateKey = new byte[fis.available()];
            fis.read(encodedPrivateKey);
            fis.close();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        }
        catch (Exception e) {
            log.error((Object)("failed to load private key from [" + location + "]"), (Throwable)e);
            return null;
        }
    }

    public static PublicKey loadPublicKey(String location) {
        try {
            FileInputStream fis = new FileInputStream(location);
            byte[] encodedPublicKey = new byte[fis.available()];
            fis.read(encodedPublicKey);
            fis.close();
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(encodedPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
            return publicKey;
        }
        catch (Exception e) {
            log.error((Object)("failed to load public key from [" + location + "]"), (Throwable)e);
            return null;
        }
    }

    public static Cipher getEncryptCiper(PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
        cipher.init(1, publicKey);
        return cipher;
    }

    public static String encryptAndBase64EncodeText(String clearText, PublicKey publicKey) throws Exception {
        return AsymmetricCryptUtils.encryptAndBase64EncodeText(AsymmetricCryptUtils.getEncryptCiper(publicKey), clearText);
    }

    public static String encryptAndBase64EncodeText(Cipher cipher, String clearText) throws Exception {
        byte[] clearTextBytes = clearText.getBytes();
        byte[] cipherText = cipher.doFinal(clearTextBytes);
        return new String(Base64.encodeBase64((byte[])cipherText));
    }

    public static String decryptBase64EncodedText(String encryptedText, PrivateKey privateKey) throws Exception {
        Cipher decryptCipher = Cipher.getInstance(CRYPT_ALGORITHM);
        decryptCipher.init(2, privateKey);
        byte[] cipherTextBytes = Base64.decodeBase64((byte[])encryptedText.getBytes());
        byte[] decryptedText = decryptCipher.doFinal(cipherTextBytes);
        return new String(decryptedText);
    }
}

