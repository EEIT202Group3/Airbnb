package com.EEITG3.Airbnb.payMent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.payMent.dto.OrderAllResponseDto;
import com.EEITG3.Airbnb.payMent.service.GetOrderAllListService;

@RestController
@RequestMapping("/admingetorderall")
public class GetAdminOrderAll {

	    @Autowired
	    private GetOrderAllListService orderService;

	    @GetMapping("/adminbyCustomer")
	    public List<OrderAllResponseDto> getOrdersByCustomerId(@RequestParam("customer_id") String customer_id) {
	        return orderService.getOrdersByCustomerId(customer_id);
	    }	
	}
