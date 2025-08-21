package com.EEITG3.Airbnb.payMent.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PayoutOrderDto {
	 private UUID payoutOrderId;
	    private UUID payoutId;
	    private String bookingId;  
	    private Integer listId;         
	    private BigDecimal grossAmount;
	    private BigDecimal platformFee;
	    private BigDecimal netAmount;
	    private LocalDateTime createdAt;

	    public PayoutOrderDto() {}

	    public PayoutOrderDto(UUID payoutOrderId, UUID payoutId, String bookingId,
	                          Integer listId, BigDecimal grossAmount, BigDecimal platformFee,
	                          BigDecimal netAmount, LocalDateTime createdAt) {
	        this.payoutOrderId = payoutOrderId;
	        this.payoutId = payoutId;
	        this.bookingId = bookingId;
	        this.listId = listId;
	        this.grossAmount = grossAmount;
	        this.platformFee = platformFee;
	        this.netAmount = netAmount;
	        this.createdAt = createdAt;
	    }

	    public UUID getPayoutOrderId() { return payoutOrderId; }
	    public void setPayoutOrderId(UUID payoutOrderId) { this.payoutOrderId = payoutOrderId; }
	    public UUID getPayoutId() { return payoutId; }
	    public void setPayoutId(UUID payoutId) { this.payoutId = payoutId; }
	    public String getBookingId() { return bookingId; }
	    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
	    public Integer getListId() { return listId; }
	    public void setListId(Integer listId) { this.listId = listId; }
	    public BigDecimal getGrossAmount() { return grossAmount; }
	    public void setGrossAmount(BigDecimal grossAmount) { this.grossAmount = grossAmount; }
	    public BigDecimal getPlatformFee() { return platformFee; }
	    public void setPlatformFee(BigDecimal platformFee) { this.platformFee = platformFee; }
	    public BigDecimal getNetAmount() { return netAmount; }
	    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
	    public LocalDateTime getCreatedAt() { return createdAt; }
	    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	}