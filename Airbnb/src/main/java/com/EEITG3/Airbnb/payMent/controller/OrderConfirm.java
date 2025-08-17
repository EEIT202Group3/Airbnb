package com.EEITG3.Airbnb.payMent.controller;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.jwt.JwtService;
import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.listing.repository.ListRepository;
import com.EEITG3.Airbnb.payMent.dto.OrderAllResponseDto;
import com.EEITG3.Airbnb.payMent.dto.OrderDetailResponseDto;
import com.EEITG3.Airbnb.payMent.dto.OrderRequestDto;
import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.service.OrderService;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;

import jakarta.transaction.Transactional;

//接收 /ordersconfirm 的 POST 請求
@RestController
@RequestMapping("/api/orderconfirm")

public class OrderConfirm {

	@Autowired
	private OrderService orderService;

	@Autowired
	private JwtService jwtService;

	@PostMapping("/preview")
	public ResponseEntity<?> previewOrder(@RequestBody OrderRequestDto dto,
			@CookieValue(value = "jwt_customer", required = false) String token) {

		try {
			String email = jwtService.extractEmail(token);
			Map<String, Object> result = orderService.previewOrderSimple(dto, email);
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException | IllegalStateException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("預覽失敗：" + e.getMessage());
		}
	}

	@PostMapping("/finalize")
	public ResponseEntity<?> finalizeOrder(@RequestBody OrderRequestDto dto,
			@CookieValue(value = "jwt_customer") String token) {
		try {
			String email = jwtService.extractEmail(token);
			Order order = orderService.createOrder(dto, email);
			return ResponseEntity.ok("訂單成功！訂單編號：" + order.getBookingId());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("建立訂單失敗：" + e.getMessage());
		}
	}

	// 單筆訂單明細
	@GetMapping("/detail")
	public OrderDetailResponseDto orderDetailResponseDto(@RequestParam("bookingId") String bookingId) {
		return orderService.getOrderByBookingId(bookingId);
	}

	// 客戶查詢全部訂單
	@GetMapping("/byCustomer")
	public List<OrderAllResponseDto> getOrdersByCustomerId(@CookieValue(value = "jwt_customer") String token) {
		String email = jwtService.extractEmail(token);

		return orderService.getOrdersByCustomerId(email);
	}

}
