package com.EEITG3.Airbnb.payMent.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.payMent.dto.AdminOrderAllResponseDto;
import com.EEITG3.Airbnb.payMent.dto.AdminOrderDetailResponseDto;
import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.payMent.repository.AdminRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AdminOrderService {
    @Autowired
    private AdminRepository adminRepository;

    
    //查詢全部明細
    public List<AdminOrderAllResponseDto> getOrdersByCustomerId(String customerId) {
		List<Order> orders = adminRepository.findAll().stream()
				.filter(o -> o.getCustomerId() != null && customerId.equals(o.getCustomerId()))
				.collect(Collectors.toList());

		return orders.stream().map(order -> {
			AdminOrderAllResponseDto dto = new AdminOrderAllResponseDto();
			dto.setBookingId(order.getBookingid());
			dto.setHousename(order.getHousename());
			dto.setBed(order.getBed());
			dto.setAddress(order.getAddress());
			dto.setTel(order.getTel());
			dto.setPeople(order.getPeople());
			dto.setBookingstatus(order.getBookingstatus());
			dto.setUsername(order.getUsername());
			dto.setGrandtotal(order.getGrandTotal());
			dto.setCheckindate(order.getCheckindate());
			dto.setCheckoutdate(order.getCheckoutdate());
			return dto;
		}).collect(Collectors.toList());
	}
    
    
    //查詢單筆明細
    public AdminOrderDetailResponseDto getOrderByBookingId(String bookingId) {
    	 Order order = adminRepository.findByBookingId(bookingId)
    	            .orElseThrow(() -> new IllegalArgumentException("查無此訂單：" + bookingId));

    	 	AdminOrderDetailResponseDto dto = new AdminOrderDetailResponseDto();
    	    dto.setBookingId(order.getBookingid());
    	    dto.setUsername(order.getUsername());
    	    dto.setHouseName(order.getHousename());
    	    dto.setAddress(order.getAddress());
    	    dto.setTel(order.getTel());
    	    dto.setBed(order.getBed());
    	    dto.setCheckinDate(order.getCheckindate());
    	    dto.setCheckoutDate(order.getCheckoutdate());
    	    dto.setPeople(order.getPeople());
    	    dto.setLocationId(order.getLocationid());
    	    dto.setPaymentId(order.getPaymentid());
    	    dto.setRoomprice(order.getRoomPrice());
    	    dto.setBookingStatus(order.getBookingstatus());
    	    dto.setMentStatus(order.getMentstatus());
    	    dto.setCartotal(order.getCarTotal());
    	    dto.setGrandtotal(order.getGrandTotal());
    	    dto.setPaidTime(order.getPaidtime());
    	    dto.setBookingMethod(order.getBookingmethod());
    	    dto.setBookingStatus(order.getBookingstatus());

    	    return dto;
    }
    public boolean updateMentStatus(String bookingId, String mentStatus) {
        int updatedMent = adminRepository.updateMentStatus(bookingId, mentStatus);
        return updatedMent > 0;
    }
    public boolean updateBookingStatus(String bookingId, String bookingStatus) {
        int updatedBooking = adminRepository.updateBookingStatus(bookingId, bookingStatus);
        return updatedBooking > 0;
    }
    
}
