package com.EEITG3.Airbnb.payMent.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.listing.repository.ListRepository;
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
	    private ListRepository listingRepository;

	    @Autowired
	    private CustomerRepository customerRepository;

	    public Order createOrder(OrderRequestDto dto) {
	        // 根據 username 找 Customer
	        Customer customer = customerRepository.findByUsername(dto.getUsername());
	        if (customer == null) {
	            throw new IllegalArgumentException("找不到使用者：" + dto.getUsername());
	        }


	        // 根據 list_id 找房源
	        LisBean listing = listingRepository.findById(dto.getListid())
	                .orElseThrow(() -> new IllegalArgumentException("找不到房源，ID：" + dto.getListid()));

	        Order order = new Order();
	        order.setCustomer(customer); // 設定關聯對象
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


	}