package com.EEITG3.Airbnb.payMent.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class NewebPayUtil {

    /** AES-256-CBC + PKCS5Padding 加密
     * 輸入明碼參數 -> 使用key與iv進行AES-256-CBC模式加密 ->輸出小寫 16進位字串回傳 hex */
    public static String aesEncryptToHex(String plain, String key, String iv) {
        try {
        	//建立AES加密物件，模式為CBC，填充為PKCS5Padding
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
            //加密用的金鑰(AES-256-CBC必須為32bytes)
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
           
            //出駛向量化IV(必須為16bytes)
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
           
            //初始化加密模式
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            
            //執行加密，得到byte陣列
            byte[] encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            
            //轉成16進為字串並轉小寫
            return bytesToHex(encrypted).toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("AES encrypt error", e);
        }
    }
    /**
     * AES-256-CBC + PKCS5Padding解密
     * 輸入小寫16進為字串->還原成byte[]->使用key與iv解密->輸出明碼
     */
    public static String aesDecryptFromHex(String hex, String key, String iv) {
        try {
        	//將16進為字串轉成byte陣列
            byte[] bytes = hexStringToByteArray(hex);
            
            //建立AES解密物件
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
            //設定金鑰與IV(跟加密時相同)
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            
            //初始化解密模式
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            
            //執行解密並轉成字串
            return new String(cipher.doFinal(bytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES decrypt error", e);
        }
    }

    /** 
     * 產生 SHA-256 大寫簽章
     * NewebPay 會要求格式:
     * 	"Hashkey=你的金鑰 & { TradeInfoHex } & HashIV = 你的IV"
     * 	再進行SHA-256 -> 轉成大寫 HEX
     */
    public static String sha256ToUpper(String data) {
        try {
        	//建立 SHA-256 訊息摘要物件
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            //將輸入文字串轉成byte[]並計算雜湊值
            byte[] digested = md.digest(data.getBytes(StandardCharsets.UTF_8));
            
            //轉成 HEX 字串並轉大寫
            return bytesToHex(digested).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("SHA256 error", e);
        }
    }
    /**
     * URL 編碼 (只對 value 做)
     * 確保特數字元在 HTTP 傳輸時不出錯
     */
    public static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
    /**
     * byte[] ->16 進為字串
     * 每個 byte 轉成2位 HEX
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b: bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
    /**
     * 16 進為字串 -> byte[]
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];
        for (int i=0; i<len; i+=2) {
            data[i/2] = (byte)((Character.digit(s.charAt(i),16)<<4) + Character.digit(s.charAt(i+1),16));
        }
        return data;
    }
}