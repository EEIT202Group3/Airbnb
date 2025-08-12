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
=======
	public Map<String, String> buildMpgFormByBookingId(String bookingId) {
		Order order = orderRepository.findByBookingId(bookingId)
				.orElseThrow(() -> new IllegalArgumentException("Order not found:" + bookingId));
		if (order.getTotalamount() == null || order.getTotalamount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalStateException("Order amount invalid");
		}
		// 若尚未產生 paymentId，這裡產生（**paymentId 即 MerchantOrderNo**）
		if (order.getPaymentid() == null || order.getPaymentid().isBlank()) {
			order.setPaymentid("ORD" + System.currentTimeMillis());
			order.setMentstatus("PENDING");
			orderRepository.save(order);
		}


		// 取 Email（// FIX: 別用 findCustomerByEmail(customerId)）
		String customerId = order.getCustomerId();// 確認 Order 有這欄位與 getter
		String email = customerRepository.findCustomerByEmail(customerId).map(Customer::getEmail)
				.orElse("test@example.com");

		//組裝 TradeInfo 明文（value 需 URL encode） ---
		Map<String, String> trade = new LinkedHashMap<>();
		trade.put("MerchantID", merchantId);
		trade.put("RespondType", "JSON");
		trade.put("TimeStamp", String.valueOf(Instant.now().getEpochSecond())); // 秒
		trade.put("Version", version);
		trade.put("MerchantOrderNo", order.getPaymentid());
		int amt = order.getTotalamount().setScale(0, RoundingMode.HALF_DOWN).intValueExact();
		trade.put("Amt", String.valueOf(amt));
		trade.put("ItemDesc", "Booking " + bookingId);
		trade.put("Email", email);
		trade.put("LoginType", "0");
		trade.put("CREDIT", "1");
		trade.put("ReturnURL", returnUrl);
		trade.put("NotifyURL", notifyUrl);
		trade.put("ClientBackURL", clientBackUrl);

		String plain = toQueryString(trade, true);


		//  AES 加密 → hex 
		String tradeInfoHex = NewebPayUtil.aesEncryptToHex(plain, hashKey, hashIv);

		// 產生 TradeSha 
		String shaSource = "HashKey=" + hashKey + "&" + tradeInfoHex + "&HashIV=" + hashIv;
		String tradeSha = NewebPayUtil.sha256ToUpper(shaSource);

		Map<String, String> res = new HashMap<>();
		res.put("MerchantID", merchantId);
		res.put("TradeInfo", tradeInfoHex);
		res.put("TradeSha", tradeSha);
		res.put("Version", version);
		res.put("Gateway", mpgGateway);// 前端要 POST 的 action
		return res;
	}
	//Step2：Notify（Server->Server
	// Notify：驗簽 → 解密 → 用 paymentId 對單 → 金額比對 → 更新 mentStatus / paidTime 
	@Transactional
	public void handleNotify(String status, String tradeInfoHex, String tradeSha) {
		//  比對簽章 FIX: 一樣要 URL-encode 後 SHA-256
		String expectedRaw = "HashKey=" + hashKey + "&TradeInfo=" + tradeInfoHex + "&HashIV=" + hashIv;
        String expected = NewebPayUtil.sha256ToUpper(NewebPayUtil.urlEncode(expectedRaw));
        if (!expected.equals(tradeSha)) {
            throw new IllegalArgumentException("TradeSha mismatch");
        }

		// 解密 TradeInfo
		String decrypted = NewebPayUtil.aesDecryptFromHex(tradeInfoHex, hashKey, hashIv);
		Map<String, Object> flat = parseTradeInfo(decrypted);

		// 取出欄位
		String merchantOrderNo = String.valueOf(flat.get("Result/MerchantOrderNo"));
		Integer amt = Integer.valueOf(String.valueOf(flat.get("Result/Amt")));
		String respondCode = String.valueOf(flat.get("Result/RespondCode")); // "00" 成功


		// 對單 FIX: 方法名要跟 Entity 欄位名一致
		Order order = orderRepository.findByPaymentId(merchantOrderNo)
				.orElseThrow(() -> new IllegalStateException("Order not found by paymentId=" + merchantOrderNo));

		// 金額比對（整數）
        int orderAmt = order.getTotalamount().setScale(0, RoundingMode.HALF_DOWN).intValueExact();
        if (orderAmt != amt) {
            throw new IllegalStateException("Amount mismatch");
        }

		// 更新狀態（以 Notify 為準）
        if ("SUCCESS".equalsIgnoreCase(status) && "00".equals(respondCode)) {
            if (!"PAID".equalsIgnoreCase(order.getMentstatus())) { // 防重覆更新
                order.setMentstatus("PAID");
                order.setPaidtime(LocalDateTime.now());
                orderRepository.save(order);
            }
        } else {
            order.setMentstatus("FAILED");
            orderRepository.save(order);
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
