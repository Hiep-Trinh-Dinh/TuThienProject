package com.example.server.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {
    public static String hmacSHA256(String secretKey, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        // MoMo expects hex lowercase (docs examples use hex). We'll return hex.
        StringBuilder sb = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
