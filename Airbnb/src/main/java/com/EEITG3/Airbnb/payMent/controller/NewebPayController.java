package com.EEITG3.Airbnb.payMent.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.repository.OrderRepository;
import com.EEITG3.Airbnb.payMent.service.NewebPayService;
import com.EEITG3.Airbnb.payMent.util.NewebPayUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/newebPay")
public class NewebPayController {

    private final NewebPayService newebPayService;
    private final OrderRepository orderRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Value("${newebpay.hash-key}")
    private String hashKey;

    @Value("${newebpay.hash-iv}")
    private String hashIv;

    @Value("${newebpay.client-back-url}")
    private String clientBackUrl;

    @Autowired
    public NewebPayController(NewebPayService newebPayService, OrderRepository orderRepository) {
        this.newebPayService = newebPayService;
        this.orderRepository = orderRepository;
    }

    /** Step1:前端帶bookingId取得要post到藍新的欄位 */
    @PostMapping(value = "/checkout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> checkout(@RequestBody Map<String, String> body) {
        String bookingId = body.get("bookingId");
        if (bookingId == null || bookingId.isBlank()) {
            throw new IllegalArgumentException("bookingId is required");
        }
        return newebPayService.buildMpgFormByBookingId(bookingId);
    }

    /** Step2：Notify（Server->Server），以這個為準更新訂單狀態 */
    @PostMapping(value = "/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String notifyCallback(
            @RequestParam("Status") String status,
            @RequestParam("TradeInfo") String tradeInfo,
            @RequestParam("TradeSha") String tradeSha
    ) {
        try {
            System.out.println("收到 Notify 回調");
            System.out.println("Status: " + status);
            System.out.println("TradeInfo: " + tradeInfo);
            System.out.println("TradeSha: " + tradeSha);
            
            newebPayService.handleNotify(status, tradeInfo, tradeSha);
            
            System.out.println("Notify 處理成功");
            return "SUCCESS";
            
        } catch (Exception e) {
            System.err.println("Notify 處理失敗: " + e.getMessage());
            e.printStackTrace();
            return "FAILED";
        }
    }

    /** Step3：Return（Browser->Server），僅顯示用途，狀態仍以 Notify 為準 */
    @PostMapping(value = "/return", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void returnCallback(
            @RequestParam("Status") String status,
            @RequestParam("TradeInfo") String tradeInfo,
            @RequestParam("TradeSha") String tradeSha,
            HttpServletResponse response
    ) {
        String bookingId = null;
        boolean paymentSuccess = false;
        
        try {
            System.out.println("=== Return 回調開始 ===");
            System.out.println("Status: " + status);
            System.out.println("TradeInfo 原始: " + tradeInfo);
            System.out.println("TradeSha: " + tradeSha);
            
            // 【關鍵修正】使用與 Service 完全相同的 TradeSha 驗證邏輯
            String expectedTradeSha = NewebPayUtil.buildTradeSha(tradeInfo, hashKey, hashIv);
            if (!expectedTradeSha.equals(tradeSha)) {
                System.err.println("TradeSha 驗證失敗！");
                System.err.println("Expected: " + expectedTradeSha);
                System.err.println("Received: " + tradeSha);
                throw new SecurityException("TradeSha validation failed");
            }
            
            System.out.println("TradeSha 驗證成功");
            
            // 解密 TradeInfo
            String decrypted = NewebPayUtil.aesDecryptFromHex(tradeInfo, hashKey, hashIv);
            System.out.println("解密後內容: " + decrypted);
            
            // 解析交易資訊
            Map<String, Object> flat = parseTradeInfo(decrypted);
            String merchantOrderNo = extractMerchantOrderNo(flat);
            String respondCode = extractRespondCode(flat);
            
            System.out.println("MerchantOrderNo: " + merchantOrderNo);
            System.out.println("RespondCode: " + respondCode);
            
            if (merchantOrderNo != null && !merchantOrderNo.isEmpty()) {
                Order order = orderRepository.findByPaymentId(merchantOrderNo).orElse(null);
                
                if (order != null) {
                    bookingId = order.getBookingId();
                    paymentSuccess = "SUCCESS".equalsIgnoreCase(status) && "00".equals(respondCode);
                    System.out.println("找到訂單 BookingId: " + bookingId);
                } else {
                    System.err.println("找不到對應訂單: " + merchantOrderNo);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Return 處理異常: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 導向前端頁面
        redirectToFrontend(response, bookingId, paymentSuccess);
    }
    
    /**
     * 從解析結果中提取 MerchantOrderNo
     */
    private String extractMerchantOrderNo(Map<String, Object> flat) {
        // 嘗試多種可能的 key
        String[] possibleKeys = {
            "Result/MerchantOrderNo",
            "MerchantOrderNo", 
            "Result/merchantOrderNo",
            "merchantOrderNo"
        };
        
        for (String key : possibleKeys) {
            Object value = flat.get(key);
            if (value != null && !value.toString().isEmpty()) {
                return value.toString();
            }
        }
        return null;
    }
    
    /**
     * 從解析結果中提取 RespondCode
     */
    private String extractRespondCode(Map<String, Object> flat) {
        String[] possibleKeys = {
            "Result/RespondCode",
            "RespondCode",
            "Result/respondCode", 
            "respondCode"
        };
        
        for (String key : possibleKeys) {
            Object value = flat.get(key);
            if (value != null && !value.toString().isEmpty()) {
                return value.toString();
            }
        }
        return null;
    }
    
    /**
     * 導向前端頁面
     */
    private void redirectToFrontend(HttpServletResponse response, String bookingId, boolean paymentSuccess) {
        try {
            String redirectUrl;
            
            if (bookingId != null) {
                if (paymentSuccess) {
                    redirectUrl = clientBackUrl + "/" + bookingId + "?status=success";
                    System.out.println("導向成功頁面: " + redirectUrl);
                } else {
                    redirectUrl = clientBackUrl + "/" + bookingId + "?status=failed";
                    System.out.println("導向失敗頁面: " + redirectUrl);
                }
            } else {
                redirectUrl = clientBackUrl.replace("/booking/done", "/error?reason=booking_not_found");
                System.out.println("導向錯誤頁面: " + redirectUrl);
            }
            
            // 設置跳過 ngrok 警告的 headers
            response.setHeader("ngrok-skip-browser-warning", "true");
            response.setHeader("User-Agent", "MyApp-PaymentReturn/1.0");
            
            // 創建 HTML 頁面自動跳轉（避免 ngrok 警告）
            String htmlContent = createRedirectHtml(redirectUrl);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(htmlContent);
            
        } catch (IOException e) {
            System.err.println("重定向失敗: " + e.getMessage());
            e.printStackTrace();
            
            // 嘗試傳統重定向作為備用方案
            try {
                String fallbackUrl = clientBackUrl.replace("/booking/done", "/");
                response.sendRedirect(fallbackUrl);
            } catch (IOException fallbackException) {
                System.err.println("連備用重定向也失敗");
            }
        }
    }
    
    /**
     * 創建 HTML 自動跳轉頁面（繞過 ngrok 警告）
     */
    private String createRedirectHtml(String redirectUrl) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<title>付款結果處理中...</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; text-align: center; padding: 50px; background-color: #f5f5f5; }");
        html.append(".container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); display: inline-block; }");
        html.append(".spinner { border: 4px solid #f3f3f3; border-top: 4px solid #3498db; border-radius: 50%; width: 40px; height: 40px; animation: spin 2s linear infinite; margin: 0 auto 20px; }");
        html.append("@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<div class=\"spinner\"></div>");
        html.append("<h2>付款結果處理中...</h2>");
        html.append("<p>正在為您跳轉到結果頁面，請稍候...</p>");
        html.append("<p><a href=\"").append(redirectUrl).append("\">如果沒有自動跳轉，請點擊這裡</a></p>");
        html.append("</div>");
        html.append("<script>");
        html.append("if (typeof window !== 'undefined') {");
        html.append("window.onload = function() {");
        html.append("setTimeout(function() {");
        html.append("window.location.href = '").append(redirectUrl).append("';");
        html.append("}, 2000);");
        html.append("};");
        html.append("}");
        html.append("</script>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
    
    /**
     * 解析 TradeInfo 內容
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseTradeInfo(String decrypted) {
        try {
            if (decrypted != null && decrypted.trim().startsWith("{")) {
                // JSON 格式
                Map<String, Object> root = mapper.readValue(decrypted, Map.class);
                Map<String, Object> flat = new java.util.LinkedHashMap<>();
                flat.put("Status", root.get("Status"));
                
                Object result = root.get("Result");
                if (result instanceof Map<?, ?> res) {
                    res.forEach((k, v) -> flat.put("Result/" + k, v));
                }
                return flat;
            } else {
                // URL 編碼格式 (key=value&key=value)
                Map<String, Object> flat = new java.util.LinkedHashMap<>();
                String[] params = decrypted.split("&");
                
                for (String param : params) {
                    int equalIndex = param.indexOf('=');
                    if (equalIndex > 0) {
                        String key = param.substring(0, equalIndex);
                        String value = param.substring(equalIndex + 1);
                        
                        // 處理巢狀結構，例如 Result.MerchantOrderNo
                        if (key.contains(".")) {
                            key = key.replace(".", "/");
                        }
                        flat.put(key, value);
                    }
                }
                return flat;
            }
        } catch (Exception e) {
            System.err.println("解析 TradeInfo 失敗: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Parse TradeInfo failed", e);
        }
    }

    /**
     * 配置驗證
     */
    @PostConstruct
    public void validateConfiguration() {
        System.out.println("=== 藍新金流設定驗證 ===");
        System.out.println("HashKey: " + hashKey);
        System.out.println("HashIV: " + hashIv);
        System.out.println("HashKey 長度: " + (hashKey != null ? hashKey.length() : "null"));
        System.out.println("HashIV 長度: " + (hashIv != null ? hashIv.length() : "null"));
        
        // 驗證 Key/IV 格式
        if (hashKey == null || hashKey.length() != 32) {
            System.err.println("HashKey 長度 32 字元！");
        }
        if (hashIv == null || hashIv.length() != 16) {
            System.err.println("HashIV 長度 16 字元！");
        }
        
        // 測試加解密
        try {
            String testData = "test";
            String encrypted = NewebPayUtil.aesEncryptToHex(testData, hashKey, hashIv);
            String decrypted = NewebPayUtil.aesDecryptFromHex(encrypted, hashKey, hashIv);
            
            if (testData.equals(decrypted)) {
                System.out.println("加解密測試成功");
            } else {
                System.err.println("加解密測試失敗");
            }
        } catch (Exception e) {
            System.err.println("加解密測試異常: " + e.getMessage());
        }
    }
}