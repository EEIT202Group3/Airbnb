package com.EEITG3.Airbnb.payMent.service;

import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.repository.OrderRepository;
import com.EEITG3.Airbnb.payMent.util.NewebPayUtil;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class NewebPayService {

	@Value("${newebpay.merchant-id}")
	private String merchantId;
	@Value("${newebpay.hash-key}")
	private String hashKey;
	@Value("${newebpay.hash-iv}")
	private String hashIv;
	@Value("${newebpay.version}")
	private String version;
	@Value("${newebpay.mpg-gateway}")
	private String mpgGateway;
	@Value("${newebpay.notify-url}")
	private String notifyUrl;
	@Value("${newebpay.return-url}")
	private String returnUrl;
	@Value("${newebpay.client-back-url}")
	private String clientBackUrl;

	private final OrderRepository orderRepository;
	private final CustomerRepository customerRepository;
	private final ObjectMapper mapper = new ObjectMapper();

	public NewebPayService(OrderRepository orderRepository, CustomerRepository customerRepository) {
		this.orderRepository = orderRepository;
		this.customerRepository = customerRepository;
	}

	// Step1：回傳要 POST 到藍新的四個欄位（MerchantID / TradeInfo / TradeSha / Version） 
	public Map<String, String> buildMpgFormByBookingId(String bookingId) {
	    Order order = orderRepository.findByBookingId(bookingId)
	            .orElseThrow(() -> new IllegalArgumentException("Order not found:" + bookingId));
	    if (order.getGrandTotal() == null || order.getGrandTotal().compareTo(BigDecimal.ZERO) <= 0) {
	        throw new IllegalStateException("Order amount invalid");
	    }

	    // 產生 MerchantOrderNo（避免太長、含 -）
	    if (order.getPaymentId() == null || order.getPaymentId().isBlank()) {
	        order.setPaymentId("ORD" + System.currentTimeMillis()); // 約 16~20 字
	        order.setMentStatus("PENDING");
	        orderRepository.save(order);
	    }

	    // 取得 Email（這段依你的資料庫而定，保持簡單）
	    String email = "test@example.com";
	    try {
	        String customerId = order.getCustomerId();
	        email = customerRepository.findCustomerByEmail(customerId)
	                .map(Customer::getEmail).orElse(email);
	    } catch (Exception ignore) {}

	    // 組 TradeInfo 明文參數（使用 LinkedHashMap 保順序）
	    LinkedHashMap<String, String> trade = new LinkedHashMap<>();
	    trade.put("MerchantID", merchantId);
	    trade.put("RespondType", "JSON");
	    trade.put("TimeStamp", String.valueOf(Instant.now().getEpochSecond()));
	    trade.put("Version", version);
	    trade.put("MerchantOrderNo", order.getPaymentId());
	    int amt = order.getGrandTotal().setScale(0, RoundingMode.HALF_DOWN).intValueExact();
	    trade.put("Amt", String.valueOf(amt));
	    trade.put("ItemDesc", "Booking " + bookingId);
	    trade.put("Email", email);
	    trade.put("LoginType", "0");
	    trade.put("CREDIT", "1");
	    trade.put("ReturnURL", returnUrl);
	    trade.put("NotifyURL", notifyUrl);
	    trade.put("ClientBackURL", clientBackUrl);

	    // 【關鍵修正】：先組成明文字串，再對整個字串做 URL encode
	    String plain = NewebPayUtil.joinParams(trade);  // 不先 URL 編碼
	    String tradeInfoHex = NewebPayUtil.aesEncryptToHex(plain, hashKey, hashIv);

	    // AES 加密（使用 URL encoded 的明文）
	   // String tradeInfoHex = NewebPayUtil.aesEncryptToHex(urlEncodedPlain, hashKey, hashIv);

	    // 產生 TradeSha（使用修正版本）
	    String tradeSha = NewebPayUtil.buildTradeSha(tradeInfoHex, hashKey, hashIv);

	    // ===== Debug（測通後可移除） =====
	    System.out.println("=== TradeInfo 明文（未編碼） ===\n" + plain);
	    //System.out.println("=== TradeInfo 明文（已編碼） ===\n" + urlEncodedPlain);
	    System.out.println("=== TradeInfo HEX ===\n" + tradeInfoHex);
	    System.out.println("=== TradeSha (final) ===\n" + tradeSha);
	    // ================================

	    Map<String, String> res = new HashMap<>();
	    res.put("MerchantID", merchantId);
	    res.put("TradeInfo", tradeInfoHex);
	    res.put("TradeSha", tradeSha);
	    res.put("Version", version);
	    res.put("action", mpgGateway);
	    return res;
	}
	
	//Step2：Notify（Server->Server）
	// Notify：驗簽 → 解密 → 用 paymentId 對單 → 金額比對 → 更新 mentStatus / paidTime 
	@Transactional
	public void handleNotify(String status, String tradeInfoHex, String tradeSha) {
	    // 【關鍵修正】使用與 buildTradeSha 相同的邏輯
	    String expectedTradeSha = NewebPayUtil.buildTradeSha(tradeInfoHex, hashKey, hashIv);
	    
	    if (!expectedTradeSha.equals(tradeSha)) {
	        System.out.println("=== TradeSha 驗證失敗 ===");
	        System.out.println("Expected: " + expectedTradeSha);
	        System.out.println("Received: " + tradeSha);
	        throw new IllegalArgumentException("TradeSha mismatch");
	    }

		// 解密 TradeInfo
		String decrypted = NewebPayUtil.aesDecryptFromHex(tradeInfoHex, hashKey, hashIv);
		Map<String, Object> flat = parseTradeInfo(decrypted);

		// 取出欄位
		 String merchantOrderNo = String.valueOf(flat.get("Result/MerchantOrderNo"));
		    Integer amt = Integer.valueOf(String.valueOf(flat.get("Result/Amt")));
		    String respondCode = String.valueOf(flat.get("Result/RespondCode")); // "00" 成功


		// 對單 修正：方法名要跟 Entity 欄位名一致
		Order order = orderRepository.findByPaymentId(merchantOrderNo)
				.orElseThrow(() -> new IllegalStateException("Order not found by paymentId=" + merchantOrderNo));

		// 金額比對（整數）
        int orderAmt = order.getGrandTotal().setScale(0, RoundingMode.HALF_DOWN).intValueExact();
        if (orderAmt != amt) {
            throw new IllegalStateException("Amount mismatch");
        }

		// 更新狀態（以 Notify 為準）
        if ("SUCCESS".equalsIgnoreCase(status) && "00".equals(respondCode)) {
            if (!"PAID".equalsIgnoreCase(order.getMentStatus())) {
                order.setMentStatus("PAID");
                order.setPaidTime(LocalDateTime.now());
                order.setBookingMethod("CREDIT_NEWEBPAY"); // 設定付款方式
                order.setBookingStatus(Order.STATUS_CONFIRMED); // 更新訂單狀態
                orderRepository.save(order);
                
                // 新增：記錄日誌以便除錯
                System.out.println("訂單更新成功: " + merchantOrderNo + " -> PAID");
            }
        } else {
            order.setMentStatus("FAILED");
            order.setBookingStatus(Order.STATUS_CANCELLED);
            orderRepository.save(order);
            
            System.out.println("付款失敗: " + merchantOrderNo + " -> FAILED");
        }
	}

	// ===== Helpers =====
	private String toQueryString(Map<String, String> map, boolean urlEncodeValue) {
		StringJoiner sj = new StringJoiner("&");
		map.forEach((k, v) -> {
			String val = urlEncodeValue ? NewebPayUtil.urlEncode(v) : v;
			sj.add(k + "=" + val);
		});
		return sj.toString();
	}

	@SuppressWarnings("unchecked")
    private Map<String, Object> parseTradeInfo(String decrypted) {
        try {
            if (decrypted != null && decrypted.trim().startsWith("{")) {
                Map<String, Object> root = mapper.readValue(decrypted, Map.class);
                Map<String, Object> flat = new LinkedHashMap<>();
                flat.put("Status", root.get("Status"));
                Object result = root.get("Result");
                if (result instanceof Map<?, ?> res) {
                    res.forEach((k, v) -> flat.put("Result/" + k, v));
                }
                return flat;
            } else {
                Map<String, Object> flat = new LinkedHashMap<>();
                for (String p : decrypted.split("&")) {
                    int i = p.indexOf('=');
                    if (i > 0) flat.put(p.substring(0, i), p.substring(i + 1));
                }
                return flat;
            }
        } catch (Exception e) {
            throw new RuntimeException("Parse TradeInfo failed", e);
        }
	}
}