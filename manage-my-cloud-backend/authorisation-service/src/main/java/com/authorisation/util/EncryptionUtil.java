package com.authorisation.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {

    private static final String SECRET_KEY = System.getenv("ENCRYPTION_SECRET_KEY") != null ? System.getenv("ENCRYPTION_SECRET_KEY") : "1234567891234567";
    private static final int IV_SIZE = 12;
    private static final int TAG_SIZE = 128;

    public static String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

            byte[] iv = new byte[IV_SIZE];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_SIZE, iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
            byte[] cipherTextWithIv = new byte[iv.length + cipherText.length];

            System.arraycopy(iv, 0, cipherTextWithIv, 0, iv.length);
            System.arraycopy(cipherText, 0, cipherTextWithIv, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(cipherTextWithIv);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: " + e);
        }
    }

    public static String decrypt(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

            byte[] cipherTextWithIv = Base64.getDecoder().decode(strToDecrypt);
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(cipherTextWithIv, 0, iv, 0, iv.length);

            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

            byte[] cipherText = new byte[cipherTextWithIv.length - IV_SIZE];
            System.arraycopy(cipherTextWithIv, IV_SIZE, cipherText, 0, cipherText.length);

            return new String(cipher.doFinal(cipherText));
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting: " + e);
        }
    }
}

