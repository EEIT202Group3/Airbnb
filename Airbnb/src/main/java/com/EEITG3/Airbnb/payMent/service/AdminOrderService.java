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
    
    
    //查詢單筆明細
    public AdminOrderDetailResponseDto getOrderByBookingId(String bookingid) {
    	 Order order = adminRepository.findByBookingId(bookingid)
    	            .orElseThrow(() -> new IllegalArgumentException("查無此訂單：" + bookingid));

    	 	AdminOrderDetailResponseDto dto = new AdminOrderDetailResponseDto();
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
    public boolean updateMentStatus(String bookingId, String mentStatus) {
        int updatedMent = adminRepository.updateMentStatus(bookingId, mentStatus);
        return updatedMent > 0;
    }
    public boolean updateBookingStatus(String bookingId, String bookingStatus) {
        int updatedBooking = adminRepository.updateBookingStatus(bookingId, bookingStatus);
        return updatedBooking > 0;
    }
    
}
