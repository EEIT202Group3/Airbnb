package com.EEITG3.Airbnb.payMent.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

@Entity
@Table(name = "payout_orders", indexes = {
    @Index(name = "idx_payout_orders_payout_id", columnList = "payout_id"),
    @Index(name = "idx_payout_orders_booking_id", columnList = "booking_id")
})
public class PayoutOrder {

    @Id
    @Column(name = "payout_order_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String payoutOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payout_id", nullable = false)
    private HostPayout hostPayout;  // 對應 host_payouts

    @Column(name = "booking_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String bookingId;  // 訂單ID

    @Column(name = "list_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String listId;  // 房源ID

    @Column(name = "gross_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "platform_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal platformFee;

    @Column(name = "net_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal netAmount;
    
    @CreationTimestamp	
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

	public String getPayoutOrderId() {
		return payoutOrderId;
	}

	public void setPayoutOrderId(String payoutOrderId) {
		this.payoutOrderId = payoutOrderId;
	}

	public HostPayout getHostPayout() {
		return hostPayout;
	}

	public void setHostPayout(HostPayout hostPayout) {
		this.hostPayout = hostPayout;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}

	public BigDecimal getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(BigDecimal grossAmount) {
		this.grossAmount = grossAmount;
	}

	public BigDecimal getPlatformFee() {
		return platformFee;
	}

	public void setPlatformFee(BigDecimal platformFee) {
		this.platformFee = platformFee;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
    
}
