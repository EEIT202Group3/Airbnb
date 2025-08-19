package com.EEITG3.Airbnb.payMent.controller;

import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.repository.OrderRepository;
import com.EEITG3.Airbnb.payMent.service.OrderService;
import com.EEITG3.Airbnb.payMent.service.PayPalService;
import com.paypal.orders.Capture;
import com.paypal.orders.PaymentCollection;
import com.paypal.orders.PurchaseUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
public class PayPalController {

    @Autowired
    private PayPalService paypalService;

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderService orderService;

    //建立 PayPal 訂單
    @PostMapping("/create-order")
    public ResponseEntity<?> createPayPalOrder(@RequestBody Map<String, String> request) {
        try {
            String bookingId = request.get("bookingId");
            if (bookingId == null || bookingId.isBlank()) {
                return ResponseEntity.badRequest().body("缺少訂單編號");
            }

            Order order = orderRepository.findByBookingId(bookingId)
                    .orElseThrow(() -> new RuntimeException("找不到訂單"));

            // 轉 USD（1 :31）
            BigDecimal twdAmount = order.getGrandTotal() == null ? BigDecimal.ZERO : order.getGrandTotal();
            BigDecimal usdAmount = twdAmount.divide(new BigDecimal("31"), 2, RoundingMode.HALF_UP);

            // 向 PayPal 建單
            String paypalOrderId = paypalService.createOrder(bookingId, usdAmount);

            // 更新本地訂單狀態
            order.setPaymentId("PP_" + paypalOrderId);
            order.setBookingMethod("PAYPAL");
            order.setMentStatus("處理中");
            // 建單時就先標示「待入住」
            if (order.getBookingStatus() == null || order.getBookingStatus().isBlank()) {
                order.setBookingStatus("待入住");
            }
            orderRepository.save(order);

            Map<String, Object> resp = new HashMap<>();
            resp.put("paypalOrderId", paypalOrderId);
            resp.put("bookingId", bookingId);
            resp.put("amount", usdAmount);
            resp.put("originalAmount", twdAmount);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("建立 PayPal 訂單失敗: " + e.getMessage());
        }
    }

    //捕獲付款-前端 onApprove 後呼叫 
    @Transactional
    @PostMapping("/capture-payment")
    public ResponseEntity<?> capturePayment(@RequestBody Map<String, String> request) {
        String paypalOrderId = request.get("paypalOrderId");
        String bookingId = request.get("bookingId");

        if (paypalOrderId == null || bookingId == null) {
            return ResponseEntity.badRequest().body("缺少必要參數");
        }

        try {
            // 使用全名避免與entity衝突
            com.paypal.orders.Order ppOrder = paypalService.captureOrder(paypalOrderId);

            Order order = orderRepository.findByBookingId(bookingId)
                    .orElseThrow(() -> new RuntimeException("找不到訂單"));

            boolean paymentSuccess = isCaptureCompleted(ppOrder);

            if (paymentSuccess) {

                // 1) 取第一筆 capture 當憑證
                Capture first = firstCapture(ppOrder);
                String txId = (first != null) ? "PP_" + first.id() : "PP_" + paypalOrderId;

                // 2) 交給 Service：標記已付款 + 回寫 paymentId/paidTime + 拆帳（單一交易）
                orderService.markOrderPaidAndSplit(bookingId, txId, LocalDateTime.now());

                // 3) 付款後才更新顯示用 bookingStatus（可保留在 Controller）
                Order fresh = orderRepository.findByBookingId(bookingId)
                        .orElseThrow(() -> new RuntimeException("找不到訂單"));

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime checkin  = fresh.getCheckinDate();
                LocalDateTime checkout = fresh.getCheckoutDate();
                if (checkin != null && checkout != null) {
                    if (now.isBefore(checkin)) {
                        fresh.setBookingStatus("待入住");
                    } else if (!now.isAfter(checkout)) {
                        fresh.setBookingStatus("已入住");
                    } else {
                        fresh.setBookingStatus("完成");
                    }
                } else {
                    fresh.setBookingStatus("待入住");
                }
                orderRepository.save(fresh);

            } else {
                order.setMentStatus("付款失敗");
                order.setBookingStatus("已取消");
                orderRepository.save(order);
            }

            orderRepository.save(order);

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", paymentSuccess);
            resp.put("paypalOrderId", paypalOrderId);
            resp.put("bookingId", bookingId);
            resp.put("status", order.getMentStatus());
            resp.put("bookingStatus", order.getBookingStatus());
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            e.printStackTrace();
            // 發生錯誤時也嘗試標示為取消
            try {
                Order order = orderRepository.findByBookingId(bookingId).orElse(null);
                if (order != null) {
                    order.setMentStatus("付款失敗");
                    order.setBookingStatus("已取消");
                    orderRepository.save(order);
                }
            } catch (Exception ignore) {}
            return ResponseEntity.internalServerError()
                    .body("處理付款失敗: " + e.getMessage());
        }
    }

    //供前端輪詢的狀態查詢
    @GetMapping("/order-status/{bookingId}")
    public ResponseEntity<?> getOrderStatus(@PathVariable String bookingId) {
        try {
            Order order = orderRepository.findByBookingId(bookingId)
                    .orElseThrow(() -> new RuntimeException("找不到訂單"));

            Map<String, Object> resp = new HashMap<>();
            resp.put("bookingId", bookingId);
            resp.put("username",   order.getUsername());
            resp.put("mentStatus", order.getMentStatus());
            resp.put("bookingStatus", order.getBookingStatus());
            resp.put("paidTime", order.getPaidTime());
            resp.put("paymentId", order.getPaymentId());
            resp.put("checkinDate",  order.getCheckinDate());   
            resp.put("checkoutDate", order.getCheckoutDate());
            resp.put("grandTotal",   order.getGrandTotal());
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("查詢訂單狀態失敗: " + e.getMessage());
        }
    }

    // paypal方法

    //是否有任一 capture 為 COMPLETED
    private boolean isCaptureCompleted(com.paypal.orders.Order ppOrder) {
        if (ppOrder == null || ppOrder.purchaseUnits() == null) return false;
        for (PurchaseUnit u : ppOrder.purchaseUnits()) {
            PaymentCollection p = u.payments();
            if (p == null || p.captures() == null) continue;
            for (Capture c : p.captures()) {
                if ("COMPLETED".equalsIgnoreCase(c.status())) return true;
            }
        }
        return false;
    }

    //取得第一筆 capture（若有
    private Capture firstCapture(com.paypal.orders.Order ppOrder) {
        if (ppOrder == null || ppOrder.purchaseUnits() == null) return null;
        for (PurchaseUnit u : ppOrder.purchaseUnits()) {
            PaymentCollection p = u.payments();
            if (p == null || p.captures() == null) continue;
            List<Capture> caps = p.captures();
            if (!caps.isEmpty()) return caps.get(0);
        }
        return null;
    }
}
