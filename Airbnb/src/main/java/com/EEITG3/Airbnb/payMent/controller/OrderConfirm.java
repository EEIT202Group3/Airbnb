package com.EEITG3.Airbnb.payMent.controller;

import java.time.LocalDate;
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
@Transactional
public class OrderConfirm {

	@Autowired
	private OrderService orderService;

	@Autowired
	private ListRepository listRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private CustomerRepository customerRepository;

	@PostMapping("/preview")
	public ResponseEntity<?> previewOrder(@RequestBody OrderRequestDto dto, @CookieValue(value = "jwt_customer", required = false) String token) {
		//String email = jwtService.extractEmail(token);
		//測試用
		String email ;
		if(token==null || token.isEmpty()) {
			email="sa@gmail.com";
		}else {
			email=jwtService.extractEmail(token);
		}
		LisBean listing;
		try {
			listing = listRepository.findById(dto.getListid())
				.orElseThrow(() -> new RuntimeException("房源不存在"));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
				.body(createErrorResponse("找不到指定的房源", "LISTING_NOT_FOUND"));
		}
		
		Customer customer;
		try {
			customer = customerRepository.findCustomerByEmail(email)
				.orElseThrow(() -> new RuntimeException("會員不存在"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(createErrorResponse("找不到會員資料，請重新登入", "CUSTOMER_NOT_FOUND"));
		}
		
		
		// 驗證必要參數
	    if (dto.getListid() == null || dto.getCheckindate() == null || 
	        dto.getCheckoutdate() == null || dto.getPeople() <= 0) {
	        return ResponseEntity.badRequest().body("缺少必要參數");
	    }
	    
	 // 驗證日期邏輯
	 		if (dto.getCheckindate().isAfter(dto.getCheckoutdate()) || 
	 		    dto.getCheckindate().isBefore(LocalDate.now())) {
	 		    return ResponseEntity.badRequest()
	 		    	.body(createErrorResponse("日期設定錯誤", "INVALID_DATE"));
	 		}

	 		long days = ChronoUnit.DAYS.between(dto.getCheckindate(), dto.getCheckoutdate());

	 		// 檢查房間是否已被預訂
	 		boolean isBooked = orderService.isRoomBooked(listing.getHouseName(), 
	 			dto.getCheckindate().atStartOfDay(), dto.getCheckoutdate().atStartOfDay());
	 		if (isBooked) {
	 			return ResponseEntity.status(HttpStatus.CONFLICT)
	 				.body(createErrorResponse("此日期已被預訂", "DATE_UNAVAILABLE"));
	 		}

		int total = listing.getPrice() * (int) days * dto.getPeople() / 2;

		Map<String, Object> result = new HashMap<>();
		result.put("listing", listing);
		result.put("customer", customer);
		result.put("days", days);
		result.put("total", total);
		result.put("checkindate", dto.getCheckindate());
		result.put("checkoutdate", dto.getCheckoutdate());
		result.put("people", dto.getPeople());

		return ResponseEntity.ok(result);
	}

	@PostMapping("/finalize")
	public ResponseEntity<?> finalizeOrder(@RequestBody OrderRequestDto dto, @CookieValue(value = "jwt_customer") String token) {
		try {
			String email = jwtService.extractEmail(token);
			Order order = orderService.createOrder(dto,email);
			return ResponseEntity.ok("訂單成功！訂單編號：" + order.getBookingId());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("建立訂單失敗：" + e.getMessage());
		}
	}
	//單筆訂單明細
	@GetMapping("/detail")
	public OrderDetailResponseDto getOrderDetail(@RequestParam("booking_id") String booking_id) {
	return orderService.getOrderByBookingId(booking_id);
	    }
	
	//客戶查詢全部訂單
    @GetMapping("/byCustomer")
    public List<OrderAllResponseDto> getOrdersByCustomerId(@CookieValue(value = "jwt_customer") String token) {
        String email = jwtService.extractEmail(token);
        
    	return orderService.getOrdersByCustomerId(email);
    }	
    // 輔助方法：創建錯誤回應
    private Map<String, Object> createErrorResponse(String message, String code) {
    	Map<String, Object> error = new HashMap<>();
    	error.put("error", message);
    	error.put("code", code);
    	error.put("timestamp", java.time.LocalDateTime.now());
    	return error;
    }


  
}
