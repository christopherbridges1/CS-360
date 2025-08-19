package com.example.chrisbridgesweighttracker3;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

// Password Encryption
public class PasswordUtil {
    private static final SecureRandom RNG = new SecureRandom();

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        byte[] digest = sha256(salt, password.getBytes());
        return Base64.getEncoder().encodeToString(salt) + ":" +
                Base64.getEncoder().encodeToString(digest);
    }

    public static boolean verify(String password, String stored) {
        String[] parts = stored.split(":");
        if (parts.length != 2) return false;
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expected = Base64.getDecoder().decode(parts[1]);
        byte[] actual = sha256(salt, password.getBytes());
        if (actual.length != expected.length) return false;
        int r = 0; for (int i = 0; i < actual.length; i++) r |= actual[i] ^ expected[i];
        return r == 0;
    }

    private static byte[] sha256(byte[] salt, byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return md.digest(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
