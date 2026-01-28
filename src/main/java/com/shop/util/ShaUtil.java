package com.shop.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class ShaUtil {
    public static String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    text.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02X", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA256 error", e);
        }
    }
}
