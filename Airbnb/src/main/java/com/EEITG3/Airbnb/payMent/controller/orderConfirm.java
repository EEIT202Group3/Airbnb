package com.EEITG3.Airbnb.payMent.controller;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.listing.repository.ListingRepository;
import com.EEITG3.Airbnb.payMent.dto.OrderRequestDto;
import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.service.OrderService;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;

import jakarta.servlet.http.HttpSession;


//接收 /ordersconfirm 的 POST 請求
@RestController
@RequestMapping("/orderconfirm")
public class orderConfirm {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ListingRepository listingRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	

//    @PostMapping("/confirm")
//    public ResponseEntity<?> confirmOrder(@RequestBody OrderRequestDTO dto) {
//        // 你可以在這裡用 listId 查資料（從 ListingRepository）
//        LisBean listing = listingRepository.findById(dto.getList_id())
//            .orElseThrow(() -> new RuntimeException("房源不存在"));
//
//        // 回傳房源資訊 + DTO 給前端顯示在 orderConfirm 頁面
//        Map<String, Object> response = new HashMap<>();
//        response.put("listing", listing);
//        response.put("checkin_date", dto.getCheckin_date());
//        response.put("checkout_date", dto.getCheckout_date());
//        response.put("people", dto.getPeople());
//
//        return ResponseEntity.ok(response);
//    }
	
	
	  @PostMapping("/login")
	    public ResponseEntity<String> login(@RequestParam String customerId, HttpSession session) {
	        session.setAttribute("customerId", customerId);
	        return ResponseEntity.ok("登入成功！Session 建立完成");
	    }
    
    @GetMapping("/init")
    public ResponseEntity<?> initOrderForm(@RequestParam Integer listId, HttpSession session) {
        String customerId = (String) session.getAttribute("customerId");
        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入");
        }

        LisBean listing = listingRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("找不到房源"));
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("找不到會員"));

        Map<String, Object> result = new HashMap<>();
        result.put("listing", listing);
        result.put("customer", customer);

        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/preview")
    public ResponseEntity<?> previewOrder(@RequestBody OrderRequestDto dto) {

        LisBean listing = listingRepository.findById(dto.getListid())
            .orElseThrow(() -> new RuntimeException("房源不存在"));
        Customer customer = customerRepository.findById(dto.getCustomerid())
            .orElseThrow(() -> new RuntimeException("會員不存在"));

        long days = ChronoUnit.DAYS.between(dto.getCheckindate(), dto.getCheckoutdate());

        boolean isBooked = orderService.isRoomBooked(
        	    listing.getHouseName(),
        	    dto.getCheckindate().atStartOfDay(),
        	    dto.getCheckoutdate().atStartOfDay()
        	);
        if (isBooked) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("此日期已被預訂");
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
    public ResponseEntity<?> finalizeOrder(@RequestBody OrderRequestDto dto) {
        try {
            Order order = orderService.createOrder(dto);
            return ResponseEntity.ok("訂單成功！訂單編號：" + order.getBookingid());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("建立訂單失敗：" + e.getMessage());
        }
    }
}