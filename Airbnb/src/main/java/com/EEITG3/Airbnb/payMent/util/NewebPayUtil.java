package com.EEITG3.Airbnb.payMent.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class NewebPayUtil {
	  // ================= AES-256-CBC =================

    /** AES-256-CBC + PKCS5Padding 加密（自訂 key/iv）→ 回傳小寫 hex */
    public static String aesEncryptToHex(String plain, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encrypted).toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("AES encrypt error", e);
        }
    }

    /** AES-256-CBC + PKCS5Padding 解密（自訂 key/iv）→ 回傳 UTF-8 字串 */
    public static String aesDecryptFromHex(String hex, String key, String iv) {
        try {
            byte[] bytes = hexStringToByteArray(hex);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            return new String(cipher.doFinal(bytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES decrypt error", e);
        }
    }

    // ================= TradeInfo / TradeSha =================

    /**
     * 依參數順序組成明文（key=value&...），再做 AES 加密得到 TradeInfo hex
     */
    public static String buildTradeInfoHex(LinkedHashMap<String, String> params, String key, String iv) {
        String query = joinParams(params);
        return aesEncryptToHex(query, key, iv);
    }

    /**
     * 產生 TradeSha - 嚴格按照藍新規範：
     * 1) 拼接：HashKey={KEY}&TradeInfo={TradeInfoHex}&HashIV={IV}
     * 2) 先轉小寫
     * 3) URL encode
     * 4) SHA-256 後轉大寫
     */
    public static String buildTradeSha(String tradeInfoHex, String hashKey, String hashIv) {
        try {
            // Step 1: 拼接原始字串
            String raw = String.format("HashKey=%s&TradeInfo=%s&HashIV=%s", hashKey, tradeInfoHex, hashIv);

            // Step 2: 轉小寫（很重要）
            String lowerRaw = raw.toLowerCase();

            // Step 3: URL encode（一次就好）
            String urlEncoded = URLEncoder.encode(lowerRaw, StandardCharsets.UTF_8.name());

            // Step 4: SHA-256 → upper
            return sha256ToUpper(urlEncoded);
        } catch (Exception e) {
            throw new RuntimeException("buildTradeSha error", e);
        }
    }

    // ================= 小工具 =================

    /** 組 query string（不做 URL Encode；由呼叫端決定 value 是否 encode） */
    public static String joinParams(Map<String, String> params) {
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> e : params.entrySet()) {
            sj.add(e.getKey() + "=" + e.getValue());
        }
        return sj.toString();
    }

    /** 產生 SHA-256 大寫 HEX */
    public static String sha256ToUpper(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digested = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digested).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("SHA256 error", e);
        }
    }

    /** 一般 UTF-8 URL encode */
    public static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /** byte[] → hex（小寫） */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    /** hex → byte[] */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}