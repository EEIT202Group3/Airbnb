package com.EEITG3.Airbnb.payMent.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	    //新增訂單
	    public Order createOrder(OrderRequestDto dto, String email) {
	        var customer = customerRepository.findCustomerByEmail(email)
	                .orElseThrow(() -> new RuntimeException("會員不存在"));

	        var listing = listRepository.findById(dto.getListid())
	                .orElseThrow(() -> new IllegalArgumentException("找不到房源，ID：" + dto.getListid()));

	        long days = ChronoUnit.DAYS.between(dto.getCheckindate(), dto.getCheckoutdate());
	        int people = Optional.ofNullable(dto.getPeople()).orElse(1);

	        // 房租（以房源售價 * 晚數 * 人數 / 2）
	        int roomPrice = listing.getPrice();
	        BigDecimal roomTotal = BigDecimal.valueOf(roomPrice)
	                .multiply(BigDecimal.valueOf(days))
	                .multiply(BigDecimal.valueOf(people))
	                .divide(BigDecimal.valueOf(2));

	        // carRent 金額：寫進 total_amount
	        BigDecimal carTotal = Optional.ofNullable(dto.getCartotal())
	                .map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);

	        // 訂單總金額（房租 + 租車）：寫進 total
	        BigDecimal grandTotal = roomTotal.add(carTotal);

	        Order order = new Order();
	        order.setListing(listing);
	        order.setHostId(
	        	    listing.getHostId() != null ? listing.getHostId().toString() : null
	        	);          
	        order.setCustomerId(
	        	    customer.getCustomerId() != null ? customer.getCustomerId().toString() : null
	        	);   
	        order.setUsername(customer.getUsername());            // 顯示用
	        order.setHouseName(listing.getHouseName());
	        order.setAddress(listing.getAds());
	        order.setTel(listing.getTel());
	        order.setBed(listing.getBed());
	        order.setPeople(people);
	        order.setCheckinDate(dto.getCheckindate().atStartOfDay());
	        order.setCheckoutDate(dto.getCheckoutdate().atStartOfDay());
	        order.setRoomPrice(roomPrice);     // price
	        order.setCarTotal(carTotal);       // total（租車金額）
	        order.setGrandTotal(grandTotal);   // total_amount（整筆總金額）
	        order.setBookingMethod(
	                Optional.ofNullable(dto.getBookingmethod()).orElse("CASH"));
	        order.setPaymentId("ORD" + System.currentTimeMillis());
	        
	        boolean paidByCard = "CREDIT_NEWEBPAY".equalsIgnoreCase(order.getBookingMethod())
                    || "CREDIT".equalsIgnoreCase(order.getBookingMethod());
	        
	        if (paidByCard) {
	            order.setMentStatus("已付款");
	            order.setPaidTime(LocalDateTime.now());
	        } else {
	            order.setMentStatus("待付款");
	            order.setPaidTime(null);
	        }
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
				dto.setBookingId(order.getBookingId());
				dto.setHousename(order.getHouseName());
				dto.setBed(order.getBed());
				dto.setAddress(order.getAddress());
				dto.setTel(order.getTel());
				dto.setPeople(order.getPeople());
				dto.setBookingstatus(order.getBookingStatus());
				dto.setUsername(order.getUsername());
				dto.setGrandtotal(order.getGrandTotal());
				dto.setCheckindate(order.getCheckinDate());
				dto.setCheckoutdate(order.getCheckoutDate());
				return dto;
			}).collect(Collectors.toList());
		}
	    //單筆訂單明細
	    public OrderDetailResponseDto getOrderByBookingId(String bookingid) {
	    	 Order order = orderRepository.findByBookingId(bookingid)
	    	            .orElseThrow(() -> new IllegalArgumentException("查無此訂單：" + bookingid));

	    	    OrderDetailResponseDto dto = new OrderDetailResponseDto();
	       	    dto.setBookingId(order.getBookingId());
	    	    dto.setUsername(order.getUsername());
	    	    dto.setHouseName(order.getHouseName());
	    	    dto.setAddress(order.getAddress());
	    	    dto.setTel(order.getTel());
	    	    dto.setBed(order.getBed());
	    	    dto.setCheckinDate(order.getCheckinDate());
	    	    dto.setCheckoutDate(order.getCheckoutDate());
	    	    dto.setPeople(order.getPeople());
	    	    dto.setLocationId(order.getLocationid());
	    	    dto.setPaymentId(order.getPaymentId());
	    	    dto.setRoomprice(order.getRoomPrice());
	    	    dto.setBookingStatus(order.getBookingStatus());
	    	    dto.setMentStatus(order.getMentStatus());
	    	    dto.setCartotal(order.getCarTotal());
	    	    dto.setGrandtotal(order.getGrandTotal());
	    	    dto.setPaidTime(order.getPaidTime());
	    	    dto.setBookingMethod(order.getBookingMethod());
	    	    dto.setBookingStatus(order.getBookingStatus());

	    	    return dto;
	    }
	
	}