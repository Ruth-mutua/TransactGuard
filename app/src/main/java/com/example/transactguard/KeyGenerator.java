package com.example.transactguard;

import android.os.Build;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {

    public static void main(String[] args) {
        // Generate a random 128-bit key
        String secretKey = generateRandomKey(128);
        System.out.println("Generated Secret Key: " + secretKey);

        // Generate a random 128-bit salt
        String salt = generateRandomKey(128);
        System.out.println("Generated Salt: " + salt);

        // Generate a random 128-bit IV
        String iv = generateRandomKey(128);
        System.out.println("Generated Initialization Vector (IV): " + iv);
    }

    static String generateRandomKey(int keyLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[keyLength / 8];
        secureRandom.nextBytes(key);
        return bytesToBase64(key);
    }

    private static String bytesToBase64(byte[] bytes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }
}
