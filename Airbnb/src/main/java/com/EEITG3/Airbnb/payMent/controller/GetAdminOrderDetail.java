package com.EEITG3.Airbnb.payMent.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.payMent.dto.AdminOrderDetailResponseDto;
import com.EEITG3.Airbnb.payMent.service.GetAdminOrderDetailService;

@RestController
@RequestMapping("/admingetorderdetail")
public class GetAdminOrderDetail {

	@Autowired
	private GetAdminOrderDetailService getAdminOrderDetailService;

	@GetMapping("/admindetail")
	public AdminOrderDetailResponseDto adminOrderDetailResponseDto(@RequestParam("booking_id") String booking_id) {
		return getAdminOrderDetailService.getOrderByBookingId(booking_id);
	}

	@PostMapping("/updatementstatus")
	public ResponseEntity<String> updateMentStatus(@RequestParam String bookingId, @RequestParam String mentStatus) {
	    boolean success = getAdminOrderDetailService.updateMentStatus(bookingId, mentStatus);
	    return success ? ResponseEntity.ok("更新成功") : ResponseEntity.badRequest().body("更新失敗");
	}
	
	@PostMapping("/updatebookingstatus")
	public ResponseEntity<String> updateBookingStatus(@RequestParam String bookingId, @RequestParam String bookingStatus) {
	    boolean success = getAdminOrderDetailService.updateBookingStatus(bookingId, bookingStatus);
	    return success ? ResponseEntity.ok("更新成功") : ResponseEntity.badRequest().body("更新失敗");
	}
}
