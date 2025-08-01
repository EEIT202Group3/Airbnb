package com.EEITG3.Airbnb.payMent.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.listing.repository.ListRepository;
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
public class OrderService {


	    @Autowired
	    private OrderRepository orderRepository;

	    @Autowired
	    private ListRepository listRepository;

	    @Autowired
	    private CustomerRepository customerRepository;

	    public Order createOrder(OrderRequestDto dto,String email) {
	        // 根據 username 找 Customer
	        Optional<Customer> temp = customerRepository.findCustomerByEmail(email);
	        if(!temp.isPresent()) {
	        	throw new RuntimeException("找不到客戶");
	        	//throw new IllegalArgumentException("找不到使用者：" + dto.getUsername());
	        }
	    	Customer customer = temp.get();

	        // 根據 list_id 找房源
	        LisBean listing = listRepository.findById(dto.getListid())
	                .orElseThrow(() -> new IllegalArgumentException("找不到房源，ID：" + dto.getListid()));

	        Order order = new Order();
	        order.setListing(listing);   // 設定房源對象

	        order.setUsername(dto.getUsername()); // 顯示用
	        order.setHousename(dto.getHousename());
	        order.setAddress(dto.getAddress());
	        order.setTel(dto.getTel());
	        order.setBed(dto.getBed());
	        order.setPeople(dto.getPeople());

	        LocalDateTime now = LocalDateTime.now();
	        order.setCheckindate(dto.getCheckindate().atStartOfDay());
	        order.setCheckoutdate(dto.getCheckoutdate().atStartOfDay());
	        order.setCreatedtime(now);

	        String booking_method = "現金";
	        order.setBookingmethod(booking_method);
	        order.setMentstatus("現金".equals(booking_method) ? "待付款" : "已付款");
	        order.setBookingstatus("待入住");

	        order.setPaymentid(UUID.randomUUID().toString());
	        order.setPrice(dto.getPrice());
	        order.setTotal(BigDecimal.valueOf(dto.getTotal()));
	        order.setTotalamount(BigDecimal.valueOf(dto.getPrice() * dto.getDays()));
	        order.setPaidtime(now);

	        return orderRepository.save(order);
	    }

	    @PersistenceContext
	    private EntityManager entityManager;
	    //日期判斷邏輯
	    public boolean isRoomBooked(String houseName, LocalDateTime checkinDate, LocalDateTime checkoutDate) {
	        String hql = "SELECT COUNT(o) FROM Order o " +
	                     "WHERE o.houseName = :houseName AND " +
	                     "o.checkinDate < :checkoutDate AND o.checkoutDate > :checkinDate";

	        Long count = entityManager.createQuery(hql, Long.class)
	            .setParameter("houseName", houseName)
	            .setParameter("checkinDate", checkinDate)
	            .setParameter("checkoutDate", checkoutDate)
	            .getSingleResult();

	        return count > 0;
	    }
	    //客戶查詢全部訂單
	    public List<OrderAllResponseDto> getOrdersByCustomerId(String email) {
			Optional<Customer> temp = customerRepository.findCustomerByEmail(email);
			if(!temp.isPresent()) {
				throw new RuntimeException("找不到客戶");
			}
			Customer customer = temp.get();
			List<Order> orders = orderRepository.findByCustomerId(customer.getCustomerId());
				
			return orders.stream().map(order -> {
				OrderAllResponseDto dto = new OrderAllResponseDto();
				dto.setBookingId(order.getBookingid());
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
	    //單筆訂單明細
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