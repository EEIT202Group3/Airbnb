package com.EEITG3.Airbnb.payMent.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.payMent.dto.AdminOrderAllResponseDto;
import com.EEITG3.Airbnb.payMent.dto.AdminOrderDetailResponseDto;
import com.EEITG3.Airbnb.payMent.service.AdminOrderService;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;

@RestController
@RequestMapping("/api/admins/admingetorderdetail")
public class AdminOrderController {

	@Autowired
	private AdminOrderService adminOrderService;
	@Autowired
	private CustomerRepository customerRepository;
	
	//查詢單筆明細
	@GetMapping("/admindetail")
	public AdminOrderDetailResponseDto adminOrderDetailResponseDto(@RequestParam("bookingId") String bookingId) {
		return adminOrderService.getOrderByBookingId(bookingId);
	}
	//更新付款狀態
	@PostMapping("/updatementstatus")
	public ResponseEntity<String> updateMentStatus(@RequestParam String bookingId, @RequestParam String mentStatus) {
	    boolean success = adminOrderService.updateMentStatus(bookingId, mentStatus);
	    return success ? ResponseEntity.ok("更新成功") : ResponseEntity.badRequest().body("更新失敗");
	}
	//更新訂單狀態
	@PostMapping("/updatebookingstatus")
	public ResponseEntity<String> updateBookingStatus(@RequestParam String bookingId, @RequestParam String bookingStatus) {
	    boolean success = adminOrderService.updateBookingStatus(bookingId, bookingStatus);
	    return success ? ResponseEntity.ok("更新成功") : ResponseEntity.badRequest().body("更新失敗");
	}
	//查詢單筆明細
	@GetMapping("/customerbyemail")
	public ResponseEntity<?> getCustomerByEmail(@RequestParam("email") String email) {
	    Customer customer = customerRepository.findCustomerByEmail(email)
	        .orElseThrow(() -> new RuntimeException("找不到該會員"));
	    
	    Map<String, Object> result = new HashMap<>();
	    result.put("customerId", customer.getCustomerId());
	    result.put("username", customer.getUsername()); 

	    return ResponseEntity.ok(result);
	}
	//查詢全部
	@GetMapping("/adminbyCustomer")
	public List<AdminOrderAllResponseDto> getOrdersByCustomerId(@RequestParam("customerId") String customerId) {
	    return adminOrderService.getOrdersByCustomerId(customerId);
	}
}
