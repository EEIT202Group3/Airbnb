package com.EEITG3.Airbnb.payMent.dto;

import java.math.BigDecimal;

public class HostOrderAggDto {
	private final String hostId;
    private final Integer listId;
    private final String bookingId;
    private final BigDecimal totalAmount;
    
    public HostOrderAggDto(String hostId, Integer listId, String bookingId, BigDecimal totalAmount) {
        this.hostId = hostId;
        this.listId = listId;
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
    }
    public String getHostId() { return hostId; }
    public Integer getListId() { return listId; }
    public String getBookingId() { return bookingId; }
    public BigDecimal getTotalAmount() { return totalAmount; }

}
