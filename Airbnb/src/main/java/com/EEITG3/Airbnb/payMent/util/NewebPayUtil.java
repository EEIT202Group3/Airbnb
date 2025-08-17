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
            // 新增：詳細的除錯資訊
            System.out.println("=== AES 解密除錯 ===");
            System.out.println("輸入 HEX: " + hex);
            System.out.println("HEX 長度: " + (hex != null ? hex.length() : "null"));
            System.out.println("Key: " + key);
            System.out.println("IV: " + iv);
            
            // 驗證輸入
            if (hex == null || hex.trim().isEmpty()) {
                throw new IllegalArgumentException("HEX 字串不能為空");
            }
            
            // 清理輸入（移除可能的空白字元）
            hex = hex.trim();
            
            // 驗證 HEX 格式
            if (!hex.matches("^[0-9a-fA-F]+$")) {
                throw new IllegalArgumentException("無效的 HEX 格式: " + hex);
            }
            
            if (hex.length() % 2 != 0) {
                throw new IllegalArgumentException("HEX 長度必須為偶數: " + hex.length());
            }
            
            // 驗證 Key/IV
            if (key.getBytes(StandardCharsets.UTF_8).length != 32) {
                throw new IllegalArgumentException("Key 長度必須為 32 bytes，當前: " + key.getBytes(StandardCharsets.UTF_8).length);
            }
            
            if (iv.getBytes(StandardCharsets.UTF_8).length != 16) {
                throw new IllegalArgumentException("IV 長度必須為 16 bytes，當前: " + iv.getBytes(StandardCharsets.UTF_8).length);
            }
            
            byte[] bytes = hexStringToByteArray(hex);
            System.out.println("轉換後 bytes 長度: " + bytes.length);
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            
            byte[] decrypted = cipher.doFinal(bytes);
            String result = new String(decrypted, StandardCharsets.UTF_8);
            System.out.println("解密成功，結果長度: " + result.length());
            return result;
            
        } catch (Exception e) {
            System.err.println("AES 解密失敗: " + e.getMessage());
            e.printStackTrace();
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
     * 2) URL encode
     * 3) 轉小寫
     * 4) SHA-256 後轉大寫
     */
    public static String buildTradeSha(String tradeInfoHex, String hashKey, String hashIv) {
        try {
            // 步驟1: 拼接原始字串
            String raw = "HashKey=" + hashKey + "&TradeInfo=" + tradeInfoHex + "&HashIV=" + hashIv;
            System.out.println("TradeSha 原始字串: " + raw);
            
            // 步驟2: URL encode
            String urlEncoded = urlEncode(raw);
            System.out.println("URL編碼後: " + urlEncoded);
            
            // 步驟3: 轉小寫
            String lowerCase = urlEncoded.toLowerCase();
            System.out.println("轉小寫後: " + lowerCase);
            
            // 步驟4: SHA-256 並轉大寫
            String sha = sha256ToUpper(lowerCase);
            System.out.println("TradeSha 計算結果: " + sha);
            
            return sha;
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
    public static String sha256ToUpper(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("SHA256 失敗", e);
        }
    }

    /** 標準 UTF-8 URL encode - 符合藍新規範 */
    public static String urlEncode(String input) {
        try {
            // 使用標準的 URLEncoder，然後針對藍新的特殊需求調整
            return URLEncoder.encode(input, StandardCharsets.UTF_8)
                    .replace("+", "%20"); // 空格應該是 %20 而不是 +
        } catch (Exception e) {
            // 如果標準方法失敗，使用手動替換
            return input.replace("%", "%25")
                        .replace(" ", "%20")
                        .replace("!", "%21")
                        .replace("#", "%23")
                        .replace("$", "%24")
                        .replace("&", "%26")
                        .replace("'", "%27")
                        .replace("(", "%28")
                        .replace(")", "%29")
                        .replace("*", "%2A")
                        .replace("+", "%2B")
                        .replace(",", "%2C")
                        .replace("/", "%2F")
                        .replace(":", "%3A")
                        .replace(";", "%3B")
                        .replace("=", "%3D")
                        .replace("?", "%3F")
                        .replace("@", "%40")
                        .replace("[", "%5B")
                        .replace("]", "%5D");
        }
    }

    /** byte[] → hex（小寫） */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    /** hex → byte[] */
    public static byte[] hexStringToByteArray(String s) {
        try {
            s = s.trim();
            int len = s.length();
            if (len % 2 != 0) {
                throw new IllegalArgumentException("HEX 字串長度必須為偶數");
            }
            
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                int high = Character.digit(s.charAt(i), 16);
                int low = Character.digit(s.charAt(i + 1), 16);
                
                if (high == -1 || low == -1) {
                    throw new IllegalArgumentException("無效的 HEX 字元: " + s.substring(i, i + 2));
                }
                
                data[i / 2] = (byte) ((high << 4) + low);
            }
            return data;
        } catch (Exception e) {
            System.err.println("HEX 轉換失敗: " + e.getMessage());
            throw new RuntimeException("HEX conversion error", e);
        }
    }
}