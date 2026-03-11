package com.projectgoth.fusion.crypto;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;

import java.security.GeneralSecurityException;
import java.util.Base64;

public class DualCrypter {

    private final Aead tinkAead;

    // Legacy Keyczar tetap dipakai di sini
    // private final KeyczarReader keyczarReader; // nanti kalau Keyczar masih dipakai

    public DualCrypter() throws GeneralSecurityException {
        // Initialize Tink
        AeadConfig.register();

        // Generate or load keyset for Tink (contoh AES128_GCM)
        KeysetHandle keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);
        this.tinkAead = AeadFactory.getPrimitive(keysetHandle);

        // Keyczar init kalau mau dual support
        // this.keyczarReader = ...
    }

    // ===== Tink encrypt/decrypt =====
    public String encryptNew(String plaintext) throws GeneralSecurityException {
        byte[] ciphertext = tinkAead.encrypt(plaintext.getBytes(), null);
        return Base64.getEncoder().encodeToString(ciphertext);
    }

    public String decryptNew(String ciphertextB64) throws GeneralSecurityException {
        byte[] ciphertext = Base64.getDecoder().decode(ciphertextB64);
        byte[] plaintext = tinkAead.decrypt(ciphertext, null);
        return new String(plaintext);
    }

    // ===== Legacy Keyczar encrypt/decrypt =====
    // public String encryptLegacy(String plaintext) { ... }
    // public String decryptLegacy(String ciphertext) { ... }
}
