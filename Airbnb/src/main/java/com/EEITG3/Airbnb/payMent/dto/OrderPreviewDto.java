package com.EEITG3.Airbnb.payMent.dto;

import java.math.BigDecimal;

public class OrderPreviewDto {
	 private String houseName;
	    private String address;
	    private String bed;
	    private int price;

	    // 訂單金額資訊
	    private BigDecimal grandTotal;       // 總金額
	    private BigDecimal platformFeeRate;  // 平台抽成比例
	    private BigDecimal platformFeeAmount;// 平台抽成金額
	    private BigDecimal hostNetAmount;    // 房東實拿
	    private String settlementMonth;      // 結算月份

	
	    public String getHouseName() { return houseName; }
	    public void setHouseName(String houseName) { this.houseName = houseName; }

	    public String getAddress() { return address; }
	    public void setAddress(String address) { this.address = address; }

	    public String getBed() { return bed; }
	    public void setBed(String bed) { this.bed = bed; }

	    public int getPrice() {return price;}
		public void setPrice(int price) {this.price = price;}
		
		public BigDecimal getGrandTotal() { return grandTotal; }
	    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

	    public BigDecimal getPlatformFeeRate() { return platformFeeRate; }
	    public void setPlatformFeeRate(BigDecimal platformFeeRate) { this.platformFeeRate = platformFeeRate; }

	    public BigDecimal getPlatformFeeAmount() { return platformFeeAmount; }
	    public void setPlatformFeeAmount(BigDecimal platformFeeAmount) { this.platformFeeAmount = platformFeeAmount; }

	    public BigDecimal getHostNetAmount() { return hostNetAmount; }
	    public void setHostNetAmount(BigDecimal hostNetAmount) { this.hostNetAmount = hostNetAmount; }

	    public String getSettlementMonth() { return settlementMonth; }
	    public void setSettlementMonth(String settlementMonth) { this.settlementMonth = settlementMonth; }
	}