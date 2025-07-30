package com.EEITG3.Airbnb.payMent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.payMent.dto.OrderDetailResponseDto;
import com.EEITG3.Airbnb.payMent.service.getOrdeDetailService;

@RestController
@RequestMapping("/getorderdetail")
public class getOrderDetail {
	
	@Autowired
    private getOrdeDetailService orderDetailService;
	
	@GetMapping("/detail")
	public OrderDetailResponseDto getOrderDetail(@RequestParam("booking_id") String booking_id) {
	return orderDetailService.getOrderByBookingId(booking_id);
	    }
	}
