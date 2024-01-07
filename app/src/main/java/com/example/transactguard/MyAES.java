package com.example.transactguard;

import android.os.Build;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class MyAES {

    private static final String SECRET_KEY = "secretKey";
    private static final String SALT = "salt";
    private static final String INIT_VECTOR = generateRandomIV(); // Implement this function to generate a random IV


    private static String generateRandomIV() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16]; // 16 bytes for AES
        secureRandom.nextBytes(iv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(iv);
        }
        return null;
    }


    public static String encrypt(String value, String secretKey) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(INIT_VECTOR.getBytes()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                byte[] encryptedBytes = cipher.doFinal(value.getBytes("UTF-8"));
                return Base64.getEncoder().encodeToString(encryptedBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if encryption fails
    }


    public static String decrypt(String encrypted) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(INIT_VECTOR.getBytes()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
