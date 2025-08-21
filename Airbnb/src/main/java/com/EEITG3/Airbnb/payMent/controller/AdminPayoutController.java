package com.EEITG3.Airbnb.payMent.controller;

import java.time.YearMonth;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.payMent.dto.HostPayoutDto;
import com.EEITG3.Airbnb.payMent.dto.OrderPreviewDto;
import com.EEITG3.Airbnb.payMent.dto.PayoutOrderDto;
import com.EEITG3.Airbnb.payMent.dto.PayoutPreviewDto;
import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.repository.OrderRepository;
import com.EEITG3.Airbnb.payMent.service.PayoutService;
import com.EEITG3.Airbnb.payMent.service.RevenueSplitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admins/payouts")
@RequiredArgsConstructor
public class AdminPayoutController {
	private final PayoutService payoutService;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private RevenueSplitService revenueSplitService;
	
	public AdminPayoutController(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

	@GetMapping("/preview")
	public ResponseEntity<OrderPreviewDto> preview(@RequestParam String bookingId) {
	    Order order = orderRepository.findByBookingId(bookingId).orElseThrow();

	    // 先套用拆帳邏輯
	    revenueSplitService.applySplitAndPersist(order);

	    OrderPreviewDto dto = new OrderPreviewDto();
	    dto.setHouseName(order.getListing().getHouseName());
	    dto.setAddress(order.getListing().getAds());
	    dto.setBed(order.getListing().getBed());
	    dto.setPrice(order.getListing().getPrice());

	    dto.setGrandTotal(order.getGrandTotal());
	    dto.setPlatformFeeRate(order.getPlatformFeeRate());
	    dto.setPlatformFeeAmount(order.getPlatformFeeAmount());
	    dto.setHostNetAmount(order.getHostNetAmount());
	    dto.setSettlementMonth(order.getSettlementMonth());

	    return ResponseEntity.ok(dto);
	}

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestParam String month) {
        payoutService.generateAndLockPayouts(YearMonth.parse(month), "admin");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/markpaid")
    public ResponseEntity<Void> markPaid(@RequestBody Map<String, String> body) {
        var payoutId = java.util.UUID.fromString(body.get("payoutId"));
        payoutService.markPayoutPaid(payoutId, java.time.LocalDateTime.now());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/monthly-preview")
    public ResponseEntity<PayoutPreviewDto> monthlyPreview(@RequestParam String month) {
        return ResponseEntity.ok(payoutService.preview(YearMonth.parse(month)));
    }
    @GetMapping("/host")
    public ResponseEntity<java.util.List<HostPayoutDto>> findHostPayouts(
            @RequestParam(required = false) String hostId,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(payoutService.findHostPayouts(hostId, month, status));
    }
    @GetMapping("/{payoutId}/orders")
    public ResponseEntity<java.util.List<PayoutOrderDto>> findPayoutOrders(
            @PathVariable("payoutId") java.util.UUID payoutId) {
        return ResponseEntity.ok(payoutService.findPayoutOrdersByPayoutId(payoutId));
    }
}