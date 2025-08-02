package com.EEITG3.Airbnb.payMent.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.payMent.dto.AdminOrderAllResponseDto;
import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.repository.AdminRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class GetAdminOrderAllService {

	@Autowired
	private AdminRepository adminRepository;


	public List<AdminOrderAllResponseDto> getOrdersByCustomerId(String customer_id) {
		List<Order> orders = adminRepository.findAll().stream()
				.filter(o -> o.getCustomerId() != null && customer_id.equals(o.getCustomerId()))
				.collect(Collectors.toList());

		return orders.stream().map(order -> {
			AdminOrderAllResponseDto dto = new AdminOrderAllResponseDto();
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
