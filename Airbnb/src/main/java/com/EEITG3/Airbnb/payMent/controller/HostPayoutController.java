package com.EEITG3.Airbnb.payMent.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.EEITG3.Airbnb.payMent.entity.HostPayout;
import com.EEITG3.Airbnb.payMent.entity.PayoutOrder;
import com.EEITG3.Airbnb.payMent.repository.HostPayoutRepository;
import com.EEITG3.Airbnb.payMent.repository.PayoutOrderRepository;
import com.EEITG3.Airbnb.payMent.repository.OrderRepository;
import com.EEITG3.Airbnb.payMent.dto.HostOrderAggDto;
import com.EEITG3.Airbnb.payMent.service.PayoutService;


@RestController
@RequestMapping("/api/payouts")
public class HostPayoutController {
		private final PayoutService payoutService;
	    private final HostPayoutRepository hostPayoutRepository;
	    private final PayoutOrderRepository payoutOrderRepository;
	    private final OrderRepository orderRepository;

	    public HostPayoutController(PayoutService payoutService,
	                            HostPayoutRepository hostPayoutRepository,
	                            PayoutOrderRepository payoutOrderRepository,
	                            OrderRepository orderRepository) {
	        this.payoutService = payoutService;
	        this.hostPayoutRepository = hostPayoutRepository;
	        this.payoutOrderRepository = payoutOrderRepository;
	        this.orderRepository = orderRepository;
	    }

	    // 手動跑某月份（例如 2025-07）
	    @PostMapping("/run")
	    public ResponseEntity<String> run(@RequestParam String month) {
	      payoutService.generateMonthlyPayouts(month);
	      return ResponseEntity.ok("payout generated for " + month);
	    }

	    // 先預覽來源資料（不落地）
	    @GetMapping("/preview")
	    public ResponseEntity<List<HostOrderAggDto>> preview(@RequestParam String month) {
	      LocalDate first = LocalDate.parse(month + "-01");
	      return ResponseEntity.ok(
	        orderRepository.findPaidOrdersForPayout(first.atStartOfDay(), first.plusMonths(1).atStartOfDay()));
	    }

	    // 查撥款單
	    @GetMapping
	    public ResponseEntity<List<HostPayout>> list(@RequestParam String month,
	                                                 @RequestParam(required=false) String hostId) {
	      return ResponseEntity.ok(
	        hostId == null || hostId.isBlank()
	          ? hostPayoutRepository.findByPayoutMonth(month)
	          : hostPayoutRepository.findByHostIdAndPayoutMonth(hostId, month));
	    }

	    // 查撥款明細
	    @GetMapping("/{payoutId}/orders")
	    public ResponseEntity<List<PayoutOrder>> payoutOrders(@PathVariable String payoutId) {
	      return ResponseEntity.ok(payoutOrderRepository.findByHostPayout_PayoutId(payoutId));
	    }
	}