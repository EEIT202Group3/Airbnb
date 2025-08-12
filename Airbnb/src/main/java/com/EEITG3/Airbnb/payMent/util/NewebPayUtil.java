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
	// ====== 測試環境金鑰（請妥善保護，正式上線改用環境變數）======
    private static final String HASH_KEY = "7Tlk7OhSY6kcFzNtnSSDhgeoTJZ5jj10"; // 32 chars
    private static final String HASH_IV  = "C1kMoPKw76zPjNbP";                 // 16 chars

    // 對外提供（若需要）
    public static String getHashKey() { return HASH_KEY; }
    public static String getHashIv()  { return HASH_IV;  }


    // ================= AES-256-CBC =================

    /** AES-256-CBC + PKCS5Padding 加密 → 回傳小寫 hex */
    public static String aesEncryptToHex(String plain) {
        return aesEncryptToHex(plain, HASH_KEY, HASH_IV);
    }

    /** AES-256-CBC + PKCS5Padding 加密（自訂 key/iv）→ 小寫 hex */
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

    /** AES-256-CBC + PKCS5Padding 解密（預設 key/iv）*/
    public static String aesDecryptFromHex(String hex) {
        return aesDecryptFromHex(hex, HASH_KEY, HASH_IV);
    }

    /** AES-256-CBC + PKCS5Padding 解密（自訂 key/iv）*/
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

    // ================= 藍新用的 TradeInfo / TradeSha =================

    /**
     * 將參數依傳入順序組成 query string（key=value&...），再做 AES 加密得到 TradeInfo。
     * 建議用 LinkedHashMap 以保留參數順序（符合藍新文件示例）。
     */
    public static String buildTradeInfoHex(LinkedHashMap<String, String> params) {
        String query = joinParams(params);
        return aesEncryptToHex(query);
    }

    /**
     * 產生 TradeSha（藍新規格）：
     * 1) raw = "HashKey={KEY}&{TradeInfoHex}&HashIV={IV}"
     * 2) urlEncode(raw)  -> 注意只需一般 UTF-8 編碼
     * 3) SHA-256 後轉大寫
     */
    public static String buildTradeSha(String tradeInfoHex) {
        String raw = "HashKey=" + HASH_KEY + "&" + tradeInfoHex + "&HashIV=" + HASH_IV;
        String encoded = urlEncode(raw);
        return sha256ToUpper(encoded);
    }

    // ================= 小工具 =================

    /** 組 query string（不做 URL Encode，AES 加密前不需要）*/
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

    /** URL encode（UTF-8）*/
    public static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /** byte[] → hex */
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