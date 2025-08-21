package com.EEITG3.Airbnb.payMent.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class HostPayoutDto {
	 private UUID payoutId;
	    private String hostId;     
	    private String payoutMonth;     
	    private BigDecimal totalEarnings;
	    private BigDecimal totalPlatformFee;
	    private BigDecimal totalNetPayout;
	    private String status;    
	    private LocalDateTime payoutDate;
	    private LocalDateTime createdAt;
	    private LocalDateTime updatedAt;

	    
	    private Integer orders;          

	    public HostPayoutDto() {}

	    public HostPayoutDto(UUID payoutId, String hostId, String payoutMonth,
	                         BigDecimal totalEarnings, BigDecimal totalPlatformFee, BigDecimal totalNetPayout,
	                         String status, LocalDateTime payoutDate, LocalDateTime createdAt, LocalDateTime updatedAt,
	                         Integer orders) {
	        this.payoutId = payoutId;
	        this.hostId = hostId;
	        this.payoutMonth = payoutMonth;
	        this.totalEarnings = totalEarnings;
	        this.totalPlatformFee = totalPlatformFee;
	        this.totalNetPayout = totalNetPayout;
	        this.status = status;
	        this.payoutDate = payoutDate;
	        this.createdAt = createdAt;
	        this.updatedAt = updatedAt;
	        this.orders = orders;
	    }

	    public UUID getPayoutId() { return payoutId; }
	    public void setPayoutId(UUID payoutId) { this.payoutId = payoutId; }
	    public String getHostId() { return hostId; }
	    public void setHostId(String hostId) { this.hostId = hostId; }
	    public String getPayoutMonth() { return payoutMonth; }
	    public void setPayoutMonth(String payoutMonth) { this.payoutMonth = payoutMonth; }
	    public BigDecimal getTotalEarnings() { return totalEarnings; }
	    public void setTotalEarnings(BigDecimal totalEarnings) { this.totalEarnings = totalEarnings; }
	    public BigDecimal getTotalPlatformFee() { return totalPlatformFee; }
	    public void setTotalPlatformFee(BigDecimal totalPlatformFee) { this.totalPlatformFee = totalPlatformFee; }
	    public BigDecimal getTotalNetPayout() { return totalNetPayout; }
	    public void setTotalNetPayout(BigDecimal totalNetPayout) { this.totalNetPayout = totalNetPayout; }
	    public String getStatus() { return status; }
	    public void setStatus(String status) { this.status = status; }
	    public LocalDateTime getPayoutDate() { return payoutDate; }
	    public void setPayoutDate(LocalDateTime payoutDate) { this.payoutDate = payoutDate; }
	    public LocalDateTime getCreatedAt() { return createdAt; }
	    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	    public LocalDateTime getUpdatedAt() { return updatedAt; }
	    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
	    public Integer getOrders() { return orders; }
	    public void setOrders(Integer orders) { this.orders = orders; }
	}