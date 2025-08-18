package com.EEITG3.Airbnb.payMent.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

	    private int resolveCapacity(String bedText) {
	        if (bedText == null || bedText.isBlank()) return 1;
	        String s = bedText;

	        // 常見中文字
	        if (s.contains("四")) return 4;
	        if (s.contains("三")) return 3;
	        if (s.contains("雙") || s.contains("二")) return 2;
	        if (s.contains("單")) return 1;

	        // 嘗試抓第一個數字
	        for (char c : s.toCharArray()) {
	            if (Character.isDigit(c)) {
	                int v = Character.getNumericValue(c);
	                if (v > 0) return v;
	            }
	        }
	        return 1;
	    }
	    public Map<String, Object> previewOrderSimple(OrderRequestDto dto, String email) {
	        var listing = listRepository.findById(dto.getListid())
	                .orElseThrow(() -> new IllegalArgumentException("房源不存在"));
	        var customer = customerRepository.findCustomerByEmail(email)
	                .orElseThrow(() -> new IllegalArgumentException("會員不存在"));

	        // 檢核
	        if (dto.getCheckindate() == null || dto.getCheckoutdate() == null) {
	            throw new IllegalArgumentException("缺少入住/退房日期");
	        }
	        long nights = ChronoUnit.DAYS.between(dto.getCheckindate(), dto.getCheckoutdate());
	        if (nights <= 0) throw new IllegalArgumentException("退房日期必須晚於入住日期");

	        Integer peopleInput = dto.getPeople();
	        int people = (peopleInput == null || peopleInput <= 0) ? 1 : peopleInput;

	        // 可訂查核
	        if (isRoomBooked(listing.getHouseName(),
	                dto.getCheckindate().atStartOfDay(),
	                dto.getCheckoutdate().atStartOfDay())) {
	            throw new IllegalStateException("此日期已被預訂");
	        }

	        // 👉 預覽專用「獨立計算」
	        int unitPricePerNight = listing.getPrice();
	        int capacityPerRoom = resolveCapacity(listing.getBed());
	        if (capacityPerRoom <= 0) capacityPerRoom = 1;

	        int roomsNeeded = (int) Math.ceil((double) people / capacityPerRoom);
	        if (roomsNeeded <= 0) roomsNeeded = 1;

	        BigDecimal roomTotal = BigDecimal.valueOf(unitPricePerNight)
	                .multiply(BigDecimal.valueOf(nights))
	                .multiply(BigDecimal.valueOf(roomsNeeded));

	        // 預覽階段是否含租車金額：看你需求。這裡沿用 dto.cartotal（沒有就 0）
	        BigDecimal carTotal = Optional.ofNullable(dto.getCartotal())
	                .map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);

	        BigDecimal grandTotal = roomTotal.add(carTotal);

	        Map<String, Object> result = new HashMap<>();
	        result.put("listing", listing);
	        result.put("customer", customer);
	        result.put("days", nights);
	        result.put("total", grandTotal.setScale(0, RoundingMode.HALF_UP).intValue());
	        result.put("checkindate", dto.getCheckindate());
	        result.put("checkoutdate", dto.getCheckoutdate());
	        result.put("people", people);
	        // 額外（可選）
	        result.put("rooms", roomsNeeded);
	        result.put("roomTotal", roomTotal);
	        result.put("carTotal", carTotal);
	        return result;
	    }

	    
	    //新增訂單
	    public Order createOrder(OrderRequestDto dto, String email) {
	        var customer = customerRepository.findCustomerByEmail(email)
	                .orElseThrow(() -> new RuntimeException("會員不存在"));

	        var listing = listRepository.findById(dto.getListid())
	                .orElseThrow(() -> new IllegalArgumentException("找不到房源，ID：" + dto.getListid()));

	        // 1) 晚數（至少 1 晚；若 checkout <= checkin 可視情況改為拋錯）
	        long nights = ChronoUnit.DAYS.between(dto.getCheckindate(), dto.getCheckoutdate());
	        if (nights <= 0) nights = 1;

	        // 2) 人數（沒傳就 1）
	        int people = Optional.ofNullable(dto.getPeople()).orElse(1);
	        if (people <= 0) people = 1;

	        // 3) 每晚單價（每間/每晚）
	        int unitPricePerNight = listing.getPrice();

	        // 4) 單間可住人數（依 bed 文字推斷）
	        int capacityPerRoom = resolveCapacity(listing.getBed());
	        if (capacityPerRoom <= 0) capacityPerRoom = 1;

	        // 5) 需要房間數 = ceil(people / capacity)
	        int roomsNeeded = (int) Math.ceil((double) people / capacityPerRoom);
	        if (roomsNeeded <= 0) roomsNeeded = 1;

	        // 6) 房租總額 = 每晚房價 × 晚數 × 房間數
	        BigDecimal roomTotal = BigDecimal.valueOf(unitPricePerNight)
	                .multiply(BigDecimal.valueOf(nights))
	                .multiply(BigDecimal.valueOf(roomsNeeded));

	        // 7) 租車金額（若有）
	        BigDecimal carTotal = Optional.ofNullable(dto.getCartotal())
	                .map(BigDecimal::valueOf)
	                .orElse(BigDecimal.ZERO);

	        // 8) 訂單總金額
	        BigDecimal grandTotal = roomTotal.add(carTotal);

	        Order order = new Order();
	        order.setListing(listing);
	        order.setHostId(listing.getHostId() != null ? listing.getHostId().toString() : null);
	        order.setCustomerId(customer.getCustomerId() != null ? customer.getCustomerId().toString() : null);
	        order.setUsername(customer.getUsername());
	        order.setHouseName(listing.getHouseName());
	        order.setAddress(listing.getAds());
	        order.setTel(listing.getTel());
	        order.setBed(listing.getBed());
	        order.setPeople(people);
	        order.setCheckinDate(dto.getCheckindate().atStartOfDay());
	        order.setCheckoutDate(dto.getCheckoutdate().atStartOfDay());

	        // ⚠ 將「房租總額」存進 roomPrice（若你想存每晚單價，改為 unitPricePerNight）
	        int roomTotalInt = roomTotal.setScale(0, RoundingMode.HALF_UP).intValue();
	        order.setRoomPrice(roomTotalInt);

	        // 維持你原本欄位語意：carTotal -> total（租車金額），grandTotal -> total_amount（整筆總額）
	        order.setCarTotal(carTotal);
	        order.setGrandTotal(grandTotal);

	        order.setBookingMethod(Optional.ofNullable(dto.getBookingmethod()).orElse("CASH"));
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

	    // 日期判斷邏輯
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