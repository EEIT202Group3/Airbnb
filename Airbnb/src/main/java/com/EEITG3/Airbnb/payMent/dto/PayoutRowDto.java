package com.EEITG3.Airbnb.payMent.dto;

import java.math.BigDecimal;

public class PayoutRowDto {
	  private String hostId;
	    private String payoutMonth;
	    private BigDecimal totalEarnings;
	    private BigDecimal totalPlatformFee;
	    private BigDecimal totalNetPayout;
	    private int orders;

	    public PayoutRowDto(String hostId, String payoutMonth,
	                     BigDecimal totalEarnings, BigDecimal totalPlatformFee,
	                     BigDecimal totalNetPayout, int orders) {
	        this.hostId = hostId;
	        this.payoutMonth = payoutMonth;
	        this.totalEarnings = totalEarnings;
	        this.totalPlatformFee = totalPlatformFee;
	        this.totalNetPayout = totalNetPayout;
	        this.orders = orders;
	    }

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

	    public int getOrders() { return orders; }
	    public void setOrders(int orders) { this.orders = orders; }

	    @Override
	    public String toString() {
	        return "PayoutRow{" +
	                "hostId='" + hostId + '\'' +
	                ", payoutMonth='" + payoutMonth + '\'' +
	                ", totalEarnings=" + totalEarnings +
	                ", totalPlatformFee=" + totalPlatformFee +
	                ", totalNetPayout=" + totalNetPayout +
	                ", orders=" + orders +
	                '}';
	    }
	}