package com.EEITG3.Airbnb.payMent.service;


import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.carRent.repository.VehicleDamageRepository;
import com.EEITG3.Airbnb.listing.repository.ListRepository;
import com.EEITG3.Airbnb.payMent.dto.HostAllOrderResponseDto;
import com.EEITG3.Airbnb.payMent.dto.OrderAllResponseDto;
import com.EEITG3.Airbnb.payMent.dto.OrderDetailResponseDto;
import com.EEITG3.Airbnb.payMent.dto.OrderRequestDto;
import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.repository.OrderRepository;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;


//商業邏輯層，負責整合 repository	
@Service
@Transactional
public class HostOrderService {

	    @Autowired
	    private OrderRepository orderRepository;
	   
	    //房東查詢全部訂單
	    public List<HostAllOrderResponseDto> getOrdersByHostId(UUID hostId) {
	        List<Order> orders = orderRepository.findAllByHostId(hostId);
	        return orders.stream().map(this::toAllDto).toList();
	    }

	    // 共用轉換
	    private HostAllOrderResponseDto toAllDto(Order order) {
	    	HostAllOrderResponseDto dto = new HostAllOrderResponseDto();
	        dto.setBookingId(order.getBookingId());
	        dto.setHouseName(order.getHouseName());  
	        dto.setBed(order.getBed());
	        dto.setAddress(order.getAddress());
	        dto.setTel(order.getTel());
	        dto.setPeople(order.getPeople());
	        dto.setBookingStatus(order.getBookingStatus());
	        dto.setUsername(order.getUsername());
	        dto.setGrandtotal(order.getGrandTotal());
	        dto.setCheckinDate(order.getCheckinDate());
	        dto.setCheckoutDate(order.getCheckoutDate());
	        return dto;
	    }
	  
	
	}