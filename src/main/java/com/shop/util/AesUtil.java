package com.shop.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.security.crypto.encrypt.BouncyCastleAesCbcBytesEncryptor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

public class AesUtil {
    private static final String TRANSFORMATIONPLCS5 = "AES/CBC/PKCS5Padding ";
    private static final String TRANSFORMATIONNOPAD = "AES/CBC/NoPadding";
    private static String stripPadding(String text) {
        int pad = text.charAt(text.length() - 1);
        if (pad < 1 || pad > 16) {
            return text.trim(); // 防爆
        }
        return text.substring(0, text.length() - pad);
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String encrypt(String plainText, String key, String iv) {
        try {
            System.out.println(plainText);
            Cipher cipher = Cipher.getInstance(TRANSFORMATIONPLCS5);
            SecretKeySpec keySpec =
                    new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec =
                    new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(
                    plainText.getBytes(StandardCharsets.UTF_8)
            );

            return Hex.encodeHexString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES encrypt error", e);
        }
    }

    public static String decrypt(String hex, String key, String iv) {
        try {
            byte[] encryptedBytes = Hex.decodeHex(hex);
            Cipher cipher = Cipher.getInstance(TRANSFORMATIONPLCS5);
            SecretKeySpec keySpec =
                    new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec =
                    new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));


            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(encryptedBytes);

            System.out.println(" STRIP : " + new String(decrypted, StandardCharsets.UTF_8));
            return stripPadding(new String(decrypted, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("AES decrypt error", e);
        }
    }



    public static String decryptLogistics(String hex, String key, String iv) {
        try {
            byte[] encryptedBytes = Hex.decodeHex(hex);
            Cipher cipher = Cipher.getInstance(TRANSFORMATIONNOPAD);
            SecretKeySpec keySpec =
                    new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec =
                    new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));


            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(encryptedBytes);

            System.out.println(" STRIP : " + new String(decrypted, StandardCharsets.UTF_8));
            return stripPadding(new String(decrypted, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("AES decrypt error", e);
        }
    }
}
