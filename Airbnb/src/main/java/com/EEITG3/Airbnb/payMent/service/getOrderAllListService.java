package com.EEITG3.Airbnb.payMent.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.listing.repository.ListingRepository;
import com.EEITG3.Airbnb.payMent.dto.OrderAllResponseDto;
import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.repository.OrderRepository;
import com.EEITG3.Airbnb.users.repository.CustomerRepository;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class getOrderAllListService {
	
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private CustomerRepository customerRepository;
	
	 public List<OrderAllResponseDto> getOrdersByCustomerId(String customer_id) {
		 List<Order> orders = orderRepository.findAll().stream()
			        .filter(o -> o.getCustomer() != null && customer_id.equals(o.getCustomer().getCustomerId()))
			        .collect(Collectors.toList());

			    return orders.stream().map(order -> {
			        OrderAllResponseDto dto = new OrderAllResponseDto();
			        dto.setHousename(order.getHousename());
			        dto.setBed(order.getBed());
			        dto.setAddress(order.getAddress());
			        dto.setTel(order.getTel());
			        dto.setPeople(order.getPeople());
			        dto.setBookingstatus(order.getBookingstatus());
			        dto.setUsername(order.getUsername());
			        dto.setTotalamount(order.getTotalamount());
			        dto.setCheckindate(order.getCheckindate());
			        dto.setCheckoutdate(order.getCheckoutdate());
			        return dto;
			    }).collect(Collectors.toList());
	    }

}
