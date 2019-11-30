package com.etuproject.p2cloud.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private static String LOCAL_KEY = "aesEncryptionKey";
    private static String INIT_VECTOR = "encryptionIntVec";

    /**
     * Aldığı stringi CBC modda AES ile encrypt edip dönen method.
     * @param value
     * @return String
     */
    public byte[] encrypt(byte[] value, byte[] remoteKey) {
        try {
            byte[] key;
            if (remoteKey == null) {
                key = LOCAL_KEY.getBytes();
            } else {
                key = remoteKey;
            }
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            return cipher.doFinal(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Parametre olarak aldığı şifrelenmiş stringi CBC modunda AES kullanarak decrypt edip dönen method.
     * @param encrypted
     * @return String
     */
    public byte[] decrypt(byte[] encrypted, byte[] remoteKey) {
        try {
            byte[] key;
            if (remoteKey == null) {
                key = LOCAL_KEY.getBytes();
            } else {
                key = remoteKey;
            }
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    public String hash(byte[] plaintext) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //return bytesToHex(digest.digest(
           //     plaintext.getBytes(StandardCharsets.ISO_8859_1)));
        return bytesToHex(digest.digest(plaintext));
    }
    public String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] generateKey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = new SecureRandom();
            int keyBitSize = 128;
            keyGen.init(keyBitSize, secureRandom);
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
