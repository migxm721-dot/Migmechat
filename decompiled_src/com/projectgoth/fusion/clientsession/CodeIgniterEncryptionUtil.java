/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.clientsession;

import com.projectgoth.fusion.clientsession.SSOLogin;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.HashUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class CodeIgniterEncryptionUtil {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(CodeIgniterEncryptionUtil.class));
    private static final int BLOCK_SIZE = 16;

    public static String encryptToBase64(String data) {
        log.debug((Object)String.format("encoding data %s", data));
        return CodeIgniterEncryptionUtil.encryptToBase64(data, SSOLogin.getSSOEIDEncryptionKeyInBytes());
    }

    public static String decryptFromBase64(String cipher) {
        if (cipher != null && cipher.indexOf("%") >= 0) {
            try {
                cipher = URLDecoder.decode(cipher, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                log.warn((Object)String.format("Unable to url decode cipher, using the un-url-decoded version: %s", cipher));
            }
        }
        log.debug((Object)String.format("decoding cipher %s", cipher));
        return CodeIgniterEncryptionUtil.decryptFromBase64(cipher, SSOLogin.getSSOEIDEncryptionKeyInBytes());
    }

    public static String encryptToBase64(String data, byte[] keyBytes) {
        try {
            byte[] encryptedBytes = CodeIgniterEncryptionUtil.encryptRaw(data.getBytes("UTF-8"), keyBytes);
            if (encryptedBytes != null) {
                return new String(Base64.encodeBase64((byte[])CodeIgniterEncryptionUtil.addNoise(encryptedBytes, keyBytes)));
            }
            return null;
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String encryptToBase64(String data, byte[] keyBytes, byte[] iv) {
        try {
            return new String(Base64.encodeBase64((byte[])CodeIgniterEncryptionUtil.addNoise(CodeIgniterEncryptionUtil.encryptRaw(data.getBytes("UTF-8"), keyBytes, iv), keyBytes)));
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static byte[] generateIV() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        }
        catch (NoSuchAlgorithmException e) {
            log.error((Object)String.format("Unable to get KeyGenerator for AES algorithm, using 0 bytes", new Object[0]), (Throwable)e);
            return new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        }
        keyGen.init(128);
        return keyGen.generateKey().getEncoded();
    }

    private static Cipher createCipher(int mode, byte[] keyBytes, byte[] ivBytes) {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        }
        catch (NoSuchAlgorithmException e) {
            log.error((Object)String.format("Unable to get cipher instance", new Object[0]), (Throwable)e);
            return null;
        }
        catch (NoSuchPaddingException e) {
            log.error((Object)String.format("Unable to get cipher instance", new Object[0]), (Throwable)e);
            return null;
        }
        try {
            cipher.init(mode, (Key)keySpec, ivSpec);
        }
        catch (InvalidKeyException e) {
            log.error((Object)String.format("Unable to init cipher", new Object[0]), (Throwable)e);
            return null;
        }
        catch (InvalidAlgorithmParameterException e) {
            log.error((Object)String.format("Unable to init cipher", new Object[0]), (Throwable)e);
            return null;
        }
        return cipher;
    }

    private static byte[] encryptRaw(byte[] dataBytes, byte[] keyBytes) {
        return CodeIgniterEncryptionUtil.encryptRaw(dataBytes, keyBytes, CodeIgniterEncryptionUtil.generateIV());
    }

    private static byte[] encryptRaw(byte[] dataBytes, byte[] keyBytes, byte[] ivBytes) {
        Cipher cipher = CodeIgniterEncryptionUtil.createCipher(1, keyBytes, ivBytes);
        if (cipher == null) {
            return null;
        }
        int dataSize = dataBytes.length;
        byte[] paddedDataBytes = null;
        int lenToPad = 16 - dataSize % 16;
        if (lenToPad == 16 && dataSize > 0) {
            paddedDataBytes = dataBytes;
        } else {
            paddedDataBytes = new byte[dataSize + lenToPad];
            Arrays.fill(paddedDataBytes, (byte)0);
            System.arraycopy(dataBytes, 0, paddedDataBytes, 0, dataSize);
        }
        byte[] cipherBytes = null;
        try {
            cipherBytes = cipher.doFinal(paddedDataBytes);
        }
        catch (IllegalBlockSizeException e) {
            log.error((Object)String.format("Unable to encrypt using cipher", new Object[0]), (Throwable)e);
            return null;
        }
        catch (BadPaddingException e) {
            log.error((Object)String.format("Unable to encrypt using cipher", new Object[0]), (Throwable)e);
            return null;
        }
        int cipherSize = cipherBytes.length;
        int ivSize = ivBytes.length;
        byte[] cipherWithIVBytes = new byte[ivSize + cipherSize];
        System.arraycopy(ivBytes, 0, cipherWithIVBytes, 0, ivSize);
        System.arraycopy(cipherBytes, 0, cipherWithIVBytes, ivSize, cipherSize);
        return cipherWithIVBytes;
    }

    private static String getSha1InHex(byte[] keyBytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException e) {
            log.error((Object)String.format("Unable to get message digest instance for sha1", new Object[0]), (Throwable)e);
            return null;
        }
        md.reset();
        md.update(keyBytes);
        byte[] hashBinary = md.digest();
        return HashUtils.asHex(hashBinary);
    }

    private static byte[] addNoise(byte[] cipherBytes, byte[] keyBytes) {
        byte[] hashHex = CodeIgniterEncryptionUtil.getSha1InHex(keyBytes).getBytes();
        int hashSize = hashHex.length;
        int cipherSize = cipherBytes.length;
        byte[] cipherWithNoise = new byte[cipherSize];
        int i = 0;
        int j = 0;
        while (i < cipherSize) {
            if (j >= hashSize) {
                j = 0;
            }
            cipherWithNoise[i] = (byte)((cipherBytes[i] + hashHex[j]) % 256);
            ++i;
            ++j;
        }
        return cipherWithNoise;
    }

    private static byte[] removeNoise(byte[] cipherWithNoiseBytes, byte[] keyBytes) {
        byte[] hashHex = CodeIgniterEncryptionUtil.getSha1InHex(keyBytes).getBytes();
        int hashSize = hashHex.length;
        int cipherSize = cipherWithNoiseBytes.length;
        byte[] cipherWithoutNoise = new byte[cipherSize];
        int i = 0;
        int j = 0;
        while (i < cipherSize) {
            if (j >= hashSize) {
                j = 0;
            }
            cipherWithoutNoise[i] = (byte)((cipherWithNoiseBytes[i] - hashHex[j]) % 256);
            ++i;
            ++j;
        }
        return cipherWithoutNoise;
    }

    public static String decryptFromBase64(String cipher, byte[] keyBytes) {
        try {
            byte[] decryptedBytes = CodeIgniterEncryptionUtil.decryptRaw(CodeIgniterEncryptionUtil.removeNoise(Base64.decodeBase64((byte[])cipher.getBytes("UTF-8")), keyBytes), keyBytes);
            if (decryptedBytes != null) {
                return new String(decryptedBytes);
            }
            return null;
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static byte[] decryptRaw(byte[] cBytes, byte[] keyBytes) {
        int i;
        if (16 > cBytes.length) {
            log.error((Object)String.format("Incorrect cipher bytes: cipher bytes %d is shorter than expected IV size %d", cBytes.length, 16));
            return null;
        }
        int dataSize = cBytes.length - 16;
        byte[] ivBytes = new byte[16];
        System.arraycopy(cBytes, 0, ivBytes, 0, 16);
        byte[] cipherDataBytes = new byte[dataSize];
        System.arraycopy(cBytes, 16, cipherDataBytes, 0, dataSize);
        Cipher cipher = CodeIgniterEncryptionUtil.createCipher(2, keyBytes, ivBytes);
        if (cipher == null) {
            return null;
        }
        byte[] cipherBytes = null;
        try {
            cipherBytes = cipher.doFinal(cipherDataBytes);
        }
        catch (IllegalBlockSizeException e) {
            log.error((Object)String.format("Unable to decrypt using cipher", new Object[0]), (Throwable)e);
            return null;
        }
        catch (BadPaddingException e) {
            log.error((Object)String.format("Unable to decrypt using cipher", new Object[0]), (Throwable)e);
            return null;
        }
        for (i = cipherBytes.length - 1; i >= 0 && cipherBytes[i] == 0; --i) {
        }
        int cipherSize = cipherBytes.length;
        if (i < 0) {
            return new byte[0];
        }
        if (i < cipherSize) {
            byte[] unpaddedCipherBytes = new byte[i + 1];
            System.arraycopy(cipherBytes, 0, unpaddedCipherBytes, 0, i + 1);
            cipherBytes = unpaddedCipherBytes;
        }
        return cipherBytes;
    }
}

