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
	private final ObjectMapper mapper = new ObjectMapper();
	private final Customer customer;

	public NewebPayService(OrderRepository orderRepository,Customer customer) {
		this.orderRepository = orderRepository;
		this.customer=customer;
	}

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

		// --- 1) 組 TradeInfo 明文（value 需 URL encode） ---
		Map<String, String> trade = new LinkedHashMap<>();
		trade.put("MerchantID", merchantId);
		trade.put("RespondType", "JSON");
		trade.put("TimeStamp", String.valueOf(Instant.now().getEpochSecond())); // 秒
		trade.put("Version", version);
		trade.put("MerchantOrderNo", order.getPaymentid());
		trade.put("Amt", String.valueOf(order.getTotalamount()));
		trade.put("ItemDesc", "Booking " + bookingId);
		trade.put("Email", customer.getEmail() != null ? customer.getEmail() : "test@example.com");
		trade.put("LoginType", "0");
		trade.put("CREDIT", "1");
		trade.put("ReturnURL", returnUrl);
		trade.put("NotifyURL", notifyUrl);
		trade.put("ClientBackURL", clientBackUrl);

		String plain = toQueryString(trade, true);

		// --- 2) AES 加密 → hex ---
		String tradeInfoHex = NewebPayUtil.aesEncryptToHex(plain, hashKey, hashIv);

		// --- 3) 產生 TradeSha ---
		String shaSource = "HashKey=" + hashKey + "&" + tradeInfoHex + "&HashIV=" + hashIv;
		String tradeSha = NewebPayUtil.sha256ToUpper(shaSource);

		Map<String, String> res = new HashMap<>();
		res.put("MerchantID", merchantId);
		res.put("TradeInfo", tradeInfoHex);
		res.put("TradeSha", tradeSha);
		res.put("Version", version);
		res.put("Gateway", mpgGateway);
		return res;
	}

	/** Notify：驗簽 → 解密 → 用 paymentId 對單 → 金額比對 → 更新 mentStatus / paidTime */
	@Transactional
	public void handleNotify(String status, String tradeInfoHex, String tradeSha) {
		// 1) 比對簽章
		String expected = NewebPayUtil.sha256ToUpper("HashKey=" + hashKey + "&" + tradeInfoHex + "&HashIV=" + hashIv);
		if (!expected.equals(tradeSha)) {
			throw new IllegalArgumentException("TradeSha mismatch");
		}

		// 2) 解密 TradeInfo
		String decrypted = NewebPayUtil.aesDecryptFromHex(tradeInfoHex, hashKey, hashIv);
		Map<String, Object> flat = parseTradeInfo(decrypted);

		// 3) 取出欄位
		String merchantOrderNo = String.valueOf(flat.get("Result/MerchantOrderNo"));
		Integer amt = Integer.valueOf(String.valueOf(flat.get("Result/Amt")));
		String respondCode = String.valueOf(flat.get("Result/RespondCode")); // "00" 成功

		// 4) 對單
		Order order = orderRepository.findyByPaymentId(merchantOrderNo)
				.orElseThrow(() -> new IllegalStateException("Order not found by paymentId=" + merchantOrderNo));

		if (!Objects.equals(order.getTotalamount(), amt)) {
			throw new IllegalStateException("Amount mismatch");
		}

		// 5) 更新狀態（以 Notify 為準）
		if ("SUCCESS".equalsIgnoreCase(status) && "00".equals(respondCode)) {
			order.setMentstatus("PAID");
			order.setPaidtime(LocalDateTime.now());
		} else {
			order.setMentstatus("FAILED");
		}
		orderRepository.save(order);
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
			if (decrypted.trim().startsWith("{")) {
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
					if (i > 0)
						flat.put(p.substring(0, i), p.substring(i + 1));
				}
				return flat;
			}
		} catch (Exception e) {
			throw new RuntimeException("Parse TradeInfo failed", e);
		}
	}
}
