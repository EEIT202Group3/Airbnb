package com.EEITG3.Airbnb.payMent.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.EEITG3.Airbnb.jwt.EmailService;
import com.EEITG3.Airbnb.jwt.JwtService;
import com.EEITG3.Airbnb.payMent.dto.HostAllOrderResponseDto;
import com.EEITG3.Airbnb.payMent.dto.OrderAllResponseDto;
import com.EEITG3.Airbnb.payMent.dto.OrderDetailResponseDto;
import com.EEITG3.Airbnb.payMent.dto.OrderRequestDto;
import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.service.HostOrderService;
import com.EEITG3.Airbnb.payMent.service.OrderService;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;
import com.EEITG3.Airbnb.users.repository.HostRepository;


//接收 /ordersconfirm 的 POST 請求
@RestController
@RequestMapping("/api/orderconfirm")

public class OrderConfirm {

	@Autowired
	private OrderService orderService;

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private HostOrderService hostOrderService;
	
	@Autowired
	private HostRepository hostRepository;
	


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
	// 依房東ID查詢房東訂單
	@GetMapping("/byhost")
	public List<HostAllOrderResponseDto> byHost(
	        @CookieValue(value = "jwt_host", required = false) String token) {

	    if (token == null || token.isBlank()) {
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登入房東");
	    }

	    String email = jwtService.extractEmail(token);
	    String role  = jwtService.extractRole(token);
	    if (!"ROLE_HOST".equalsIgnoreCase(role)) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "身分不是房東");
	    }

	    // hostRepository.findByEmail(...) 取到的若是「字串型」hostId，就轉成 UUID
	    String hostId = hostRepository.findHostByEmail(email)
	            .map(h -> {
	                try {
	                    return h.getHostId(); // ← 這裡把 String 轉 UUID
	                } catch (IllegalArgumentException e) {
	                    throw new ResponseStatusException(
	                        HttpStatus.BAD_REQUEST, "hostId 不是合法的 UUID 格式");
	                }
	            })
	            .orElseThrow(() -> new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "找不到房東: " + email));

	    return hostOrderService.getOrdersByHostId(hostId);
	}

}
