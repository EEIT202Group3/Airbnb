package com.EEITG3.Airbnb.payMent.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.listing.repository.ListRepository;
import com.EEITG3.Airbnb.payMent.dto.OrderDetailResponseDto;
import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.repository.OrderRepository;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class getOrdeDetailService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ListRepository listRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public OrderDetailResponseDto getOrderByBookingId(String bookingid) {
    	 Order order = orderRepository.findByBookingId(bookingid)
    	            .orElseThrow(() -> new IllegalArgumentException("查無此訂單：" + bookingid));

    	    OrderDetailResponseDto dto = new OrderDetailResponseDto();
    	    dto.setBookingid(order.getBookingid());
    	    dto.setUsername(order.getUsername());
    	    dto.setHousename(order.getHousename());
    	    dto.setAddress(order.getAddress());
    	    dto.setTel(order.getTel());
    	    dto.setBed(order.getBed());
    	    dto.setCheckindate(order.getCheckindate());
    	    dto.setCheckoutdate(order.getCheckoutdate());
    	    dto.setPeople(order.getPeople());
    	    dto.setLocationid(order.getLocationid());
    	    dto.setPaymentid(order.getPaymentid());
    	    dto.setPrice(order.getPrice());
    	    dto.setBookingstatus(order.getBookingstatus());
    	    dto.setMentstatus(order.getMentstatus());
    	    dto.setTotal(order.getTotal());
    	    dto.setPaidtime(order.getPaidtime());
    	    dto.setBookingmethod(order.getBookingmethod());
    	    dto.setBookingstatus(order.getBookingstatus());

    	    return dto;
    }
}