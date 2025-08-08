package com.EEITG3.Airbnb.payMent.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class EcpayUtil {

    private static final String HASH_KEY = "pwFHCqoQZGmho4w6";
    private static final String HASH_IV = "EkRm7iFT261dpevs";

    //主方法：產生 CheckMacValue
     
    public static String generateCheckMacValue(Map<String, String> params) {
        try {
            String raw = buildRawDataString(params);
            String urlEncoded = urlEncode(raw);
            String hash = sha256(urlEncoded);
            System.out.println(" CheckMacValue 原始字串：" + raw);
            System.out.println(" URL編碼後字串：" + urlEncoded);
            System.out.println(" 最終 CheckMacValue：" + hash);
            return hash;
        } catch (Exception e) {
            throw new RuntimeException(" CheckMacValue 產生失敗", e);
        }
    }

     // 驗證回傳的 CheckMacValue 是否正確
     
    public static boolean verifyCheckMacValue(Map<String, String> params) {
        String receivedMac = params.get("CheckMacValue");
        if (receivedMac == null) return false;

        Map<String, String> filtered = new TreeMap<>(params);
        filtered.remove("CheckMacValue");
        String calculated = generateCheckMacValue(filtered);

        boolean valid = receivedMac.equalsIgnoreCase(calculated);
        if (!valid) {
            System.out.println(" CheckMacValue 驗證失敗！");
            System.out.println(" 正確應為：" + calculated);
            System.out.println(" 實際收到：" + receivedMac);
        }
        return valid;
    }

    //將所有參數組成原始字串：HashKey + params + HashIV
  
    private static String buildRawDataString(Map<String, String> params) {
        Map<String, String> sorted = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        sb.append("HashKey=").append(HASH_KEY);
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.append("&HashIV=").append(HASH_IV);
        return sb.toString();
    }

    // 綠界要求的 URL encode（小寫並保留特定符號）
    private static String urlEncode(String input) throws Exception {
        return URLEncoder.encode(input, StandardCharsets.UTF_8.name())
                .toLowerCase()
                .replace("%21", "!")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%2a", "*")
                .replace("%2d", "-")
                .replace("%2e", ".")
                .replace("%5f", "_");
    }

    // 對資料進行 SHA-256 加密並轉成大寫十六進位

    private static String sha256(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hashBytes) {
            hex.append(String.format("%02X", b));
        }
        return hex.toString();
    }
}
