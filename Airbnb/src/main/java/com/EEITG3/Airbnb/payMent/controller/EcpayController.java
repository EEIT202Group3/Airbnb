package com.EEITG3.Airbnb.payMent.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.repository.OrderRepository;
import com.EEITG3.Airbnb.payMent.util.EcpayUtil;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/ecpay")
public class EcpayController {

	@Autowired
	private OrderRepository orderRepository;

	public void goPay(@RequestBody Map<String, String> req, HttpServletResponse response) throws IOException {
		String bookingId = req.get("orderId");

		Order order = orderRepository.findByBookingId(bookingId)
				.orElseThrow(() -> new RuntimeException("找不到此訂單:" + bookingId));
		// 產生對應的付款編號(API需要唯一)
		String merchantTradeNo = "ORD" + System.currentTimeMillis();

		// 存進資料庫(payment_id對應綠界的MarchantTradeNo)
		order.setPaymentid(merchantTradeNo);
		orderRepository.save(order);

		// 準備參數傳給綠界
		Map<String, String> params = new TreeMap<>();
		params.put("MerchantID", "300267"); // 特店編號碼:300267->綠界提供測試用的編號
		params.put("MerchantTradeNO", merchantTradeNo); // 唯一付款編號
		params.put("MerchantTradeData", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())); // 時間綠界固定格式
		params.put("PaymentType", "aio"); // 綠界要求固定填入aio
		params.put("TotalAmount", String.valueOf(order.getTotal().intValue()));// 待整數，不可有小數點
		params.put("ItemName", String.valueOf(order.getHousename())); // 商品名稱
		params.put("ReturnURL", "http://localhost:8080/api/ecpay/return"); // 伺服器端:回傳付款完成通知(或付款不成功)，用來處理訂單狀態更新、入帳、開立發票等後端邏輯
		params.put("OrderResultURL", "http://localhost:5173/payment-success"); // 使用者端:用來顯示付款結果、感謝頁面或導回商店前端頁面。
		params.put("ChoosePayment", "Credit"); //依設定的付款方式及付款子項目帶入訂單
		params.put("EncryptType", "1"); //固定填入1，使用SHA256加密。

		String checkMacValue = EcpayUtil.generateCheckMacValue(params);
		params.put("CheckMacValue", checkMacValue); //綠界進行資料傳遞進行的加密
		
		//送出HTML表單給綠界，一次性付清建議用HTML格式傳輸
		StringBuilder html = new StringBuilder();
		html.append("<form id='ecpayForm' method='post' action='http://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5'>");
		for (Map.Entry<String, String> entry : params.entrySet()) {
			html.append("<input type = 'hidden' name='" + entry.getKey() + " 'value=' " + entry.getValue() + " ' /> ");
			
		}
		html.append("</form><script>document.getElementById('ecpayForm').submit();</script>");
		
		response.setContentType("text/html;charset=UTF=8");
		response.getWriter().write(html.toString());
	}
	//綠界回傳付款成功通知(後端背景用)
	@PostMapping("/return")
	public ResponseEntity<String> handleReturn(@RequestParam Map<String, String> data){
		boolean valid = EcpayUtil.verifyCheckMacValue(data);
		if(!valid) return ResponseEntity.badRequest().body("CheckMacValue 錯誤");
		
		String rtnCode =data.get("RtnCode");
		if("1".equals(rtnCode)) {
			String tradeNo = data.get("MerchantTradeNo");
			
			//用paymentId找出訂單
			Order order = orderRepository.findyByPaymentId(tradeNo)
					.orElseThrow(()-> new RuntimeException("找不到對應的付款訂單:" + tradeNo));
			order.setMentstatus("已付款");
			order.setPaidtime(LocalDateTime.now());
			order.setMentstatus("已完成");
			orderRepository.save(order);
			
			System.out.println("訂單[" + order.getBookingid() + "] 已完成付款");
			
		}
		return ResponseEntity.ok("1|OK");
	}
}
